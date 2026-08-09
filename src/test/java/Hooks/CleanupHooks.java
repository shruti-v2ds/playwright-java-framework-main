package Hooks;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import framework.config.ConfigManager;
import framework.core.DriverFactory;
import framework.utils.UserCleanupUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

/**
 * Cucumber hooks for automatic user cleanup after tests.
 * 
 * This hook runs as a POST-CONDITION after every test scenario:
 * - If test has @NeedsCleanup tag: deletes user from DialinArch
 * - If test has no @NeedsCleanup tag: skips cleanup
 * - Cleanup errors are logged but DON'T fail the test
 */
public class CleanupHooks {

    /**
     * Automatic cleanup hook - runs after every @NeedsCleanup test
     * 
     * @param scenario Cucumber scenario object
     */
    @After(order = 1, value = "@NeedsCleanup")
    public void cleanupUserAfterTest(Scenario scenario) {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         STARTING POST-CONDITION USER CLEANUP                 ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("[Cleanup Hook] Scenario: " + scenario.getName());
        System.out.println("[Cleanup Hook] Status: " + scenario.getStatus());

        BrowserContext cleanupContext = null;
        Page cleanupPage = null;

        try {
            String mobile = getMobileFromContext();

            if (mobile == null || mobile.isEmpty()) {
                System.out.println("[Cleanup Hook] ⚠ No mobile number found in context - skipping cleanup");
                return;
            }

            System.out.println("[Cleanup Hook] Mobile to cleanup: " + mobile);

            Browser browser = DriverFactory.getBrowser();
            if (browser == null) {
                System.out.println("[Cleanup Hook] ⚠ No browser available for cleanup - creating a fresh one");
                DriverFactory.initBrowser(ConfigManager.getConfig().browser());
                browser = DriverFactory.getBrowser();
            }

            cleanupContext = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
            cleanupPage = cleanupContext.newPage();

            System.out.println("[Cleanup Hook] Created fresh cleanup page in a new browser context");
            boolean cleanupSuccess = UserCleanupUtil.deleteUserAccount(cleanupPage, mobile, "0000");

            if (cleanupSuccess) {
                System.out.println("[Cleanup Hook] ✅ User cleanup SUCCESSFUL");
                TestContextManager.getContext().set("cleanupStatus", "SUCCESS");
            } else {
                System.out.println("[Cleanup Hook] ⚠ User cleanup FAILED - continuing test execution");
                TestContextManager.getContext().set("cleanupStatus", "FAILED");
            }

        } catch (Exception e) {
            System.out.println("[Cleanup Hook] ❌ Cleanup exception: " + e.getMessage());
            e.printStackTrace();
            TestContextManager.getContext().set("cleanupStatus", "EXCEPTION");
        } finally {
            if (cleanupPage != null && !cleanupPage.isClosed()) {
                cleanupPage.close();
            }
            if (cleanupContext != null) {
                cleanupContext.close();
            }
        }

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         POST-CONDITION CLEANUP COMPLETED                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Optional: Before hook for cleanup setup (if needed)
     */
    @Before(value = "@NeedsCleanup")
    public void setupForCleanup(Scenario scenario) {
        System.out.println("[Cleanup Setup] Preparing cleanup for: " + scenario.getName());
        System.out.println("[Cleanup Setup] This scenario will trigger user cleanup after test");
        TestContextManager.getContext().set("cleanupRequired", true);
    }

    /**
     * Helper: Get mobile number from TestContext
     * Searches multiple possible keys where mobile might be stored
     * 
     * @return mobile number or null if not found
     */
    private String getMobileFromContext() {
        TestContext testContext = TestContextManager.getContext();
        
        // Try different possible keys
        String[] possibleKeys = {
            "mobileNumber",
            "mobile",
            "phoneNumber",
            "phone",
            "testMobile",
            "userMobile",
            "registrationMobile",
            "loginMobile"
        };

        for (String key : possibleKeys) {
            Object value = testContext.get(key);
            if (value != null && !value.toString().isEmpty()) {
                System.out.println("[Cleanup Hook] Found mobile in context: " + key + " = " + value);
                return value.toString();
            }
        }

        System.out.println("[Cleanup Hook] Mobile number not found in any expected context key");
        return null;
    }

    /**
     * Optional: Additional cleanup for multiple users (if needed)
     * For tests that create multiple users
     */
    @SuppressWarnings("unchecked")
    public void cleanupMultipleUsers(Scenario scenario) {
        System.out.println("[Cleanup Hook] Cleaning up multiple users...");

        try {
            TestContext testContext = TestContextManager.getContext();
            
            // Get list of mobile numbers from context
            Object usersObj = testContext.get("createdUsers");
            if (usersObj == null) {
                return;
            }

            java.util.List<String> mobileNumbers = (java.util.List<String>) usersObj;
            Page page = DriverFactory.getPage();

            if (page == null || page.isClosed() || mobileNumbers.isEmpty()) {
                return;
            }

            System.out.println("[Cleanup Hook] Found " + mobileNumbers.size() + " users to cleanup");

            for (String mobile : mobileNumbers) {
                System.out.println("[Cleanup Hook] Cleaning up: " + mobile);
                UserCleanupUtil.deleteUserAccount(page, mobile, "0000");
            }

            System.out.println("[Cleanup Hook] ✅ All users cleaned up");

        } catch (Exception e) {
            System.out.println("[Cleanup Hook] ⚠ Error cleaning multiple users: " + e.getMessage());
            // Don't fail test
        }
    }
}
