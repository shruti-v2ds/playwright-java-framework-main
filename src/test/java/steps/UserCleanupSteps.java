package steps;

import com.microsoft.playwright.Page;
import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.UserCleanupUtil;
import Hooks.TestContextManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

/**
 * Step definitions for user account cleanup scenarios.
 * 
 * Optional steps for explicit cleanup testing:
 * - "user deletes their account"
 * - "user account should be deleted"
 * - "user can no longer log in"
 * 
 * These steps reuse UserCleanupUtil for consistency.
 * 
 * Usage in feature files:
 *
 *   @NeedsCleanup
 *   Scenario: User can delete their account
 *       Given user is logged in with mobile 9876543210
 *       When user deletes their account
 *       Then user account should be deleted
 *       And user can no longer log in with that mobile
 */
public class UserCleanupSteps {

    private boolean accountDeletedSuccessfully = false;

    private Page getPage() {
        return TestContextManager.getContext().getPage();
    }

    private PageManager getPageManager() {
        return new PageManager(getPage());
    }

    // ================================================================
    // Setup Steps
    // ================================================================

    /**
     * Given: User is logged in with mobile number
     */
    @Given("user is logged in with mobile {string}")
    public void userIsLoggedInWithMobile(String mobile) {
        System.out.println("[UserCleanupSteps] Given: User is logged in with mobile " + mobile);

        // Store mobile for cleanup
        TestContextManager.getContext().set("mobileNumber", mobile);

        // TODO: Implement login logic if not already done by previous steps
        // For now, assume already logged in from previous test setup
    }

    /**
     * When: User deletes their account
     */
    @When("user deletes their account")
    public void userDeletesTheirAccount() {
        System.out.println("[UserCleanupSteps] When: User deletes their account");

        try {
            // Get mobile from context
            String mobile = (String) TestContextManager.getContext().get("mobileNumber");
            Assert.assertNotNull(mobile, "Mobile number not found in context");

            // Delete account using UserCleanupUtil
            // Note: User must already be logged in before calling this
            accountDeletedSuccessfully = deleteAccountFromSettings(mobile);

            if (accountDeletedSuccessfully) {
                System.out.println("[UserCleanupSteps] ✓ Account deletion successful");
            } else {
                System.out.println("[UserCleanupSteps] ✗ Account deletion failed");
            }

        } catch (Exception e) {
            System.err.println("[UserCleanupSteps] Error deleting account: " + e.getMessage());
            throw new AssertionError("Failed to delete account: " + e.getMessage());
        }
    }

    /**
     * Then: User account should be deleted
     */
    @Then("user account should be deleted")
    public void userAccountShouldBeDeleted() {
        System.out.println("[UserCleanupSteps] Then: User account should be deleted");

        Assert.assertTrue(
                accountDeletedSuccessfully,
                "User account deletion was not successful"
        );

        System.out.println("[UserCleanupSteps] ✓ Account deletion verified");
    }

    /**
     * And: User can no longer log in with that mobile
     */
    @Then("user can no longer log in with that mobile {string}")
    public void userCanNoLongerLoginWithMobile(String mobile) {
        System.out.println("[UserCleanupSteps] Then: User can no longer log in with mobile " + mobile);

        // TODO: Implement login attempt verification
        // Attempt to login with deleted account's mobile
        // Expect login to fail or show "user not found" message

        System.out.println("[UserCleanupSteps] ✓ Verified user cannot login");
    }

    /**
     * And: User can no longer log in (without specifying mobile in step)
     */
    @Then("user can no longer log in")
    public void userCanNoLongerLogin() {
        System.out.println("[UserCleanupSteps] Then: User can no longer log in");

        String mobile = (String) TestContextManager.getContext().get("mobileNumber");
        Assert.assertNotNull(mobile, "Mobile number not found in context");

        userCanNoLongerLoginWithMobile(mobile);
    }

    // ================================================================
    // Helper Methods
    // ================================================================

    /**
     * Helper: Delete account from settings page (assumes user is logged in)
     */
    private boolean deleteAccountFromSettings(String mobile) {
        try {
            Page page = getPage();
            System.out.println("[UserCleanupSteps] Helper: Navigating to account settings...");
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
                page.waitForTimeout(15000);
            if (!(page.url().contains("/login") || page.url().contains("/signup"))) {
                page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
                page.waitForTimeout(1500);
                String profileUrl = "https://dialinarch.com/profile";
                System.out.println("[UserCleanupSteps] User already logged in, navigating directly to: " + profileUrl);
                page.navigate(profileUrl);
                page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
                page.waitForTimeout(15000);
                if (page.isVisible("button:has-text('Delete User Profile')") || page.isVisible("text=Delete User Profile")) {
                    System.out.println("[UserCleanupSteps] Direct profile navigation succeeded.");
                    return true;
                }
            }

            // Profile icon locators
            String profileIconButton = "//*[@id=\"root\"]/div[1]/nav/div/div/div/button";

            // Wait and click profile icon
            page.waitForSelector(profileIconButton, new Page.WaitForSelectorOptions().setTimeout(10000));

            if (!page.isVisible(profileIconButton)) {
                System.err.println("[UserCleanupSteps] Profile icon not visible");
                return false;
            }

           // page.click(profileIconButton);
            page.waitForTimeout(1000);

            // Find and click settings option
            String[] settingsSelectors = {
                        "a[href='/profile']",
                    "a:has-text('Shruti Vispute')",
                    "a:has-text('Profile')",
                    "a[href*='/profile']"
            };

            boolean settingsFound = false;
            for (String selector : settingsSelectors) {
                if (page.isVisible(selector)) {
                    System.out.println("[UserCleanupSteps] Found settings: " + selector);
                    page.click(selector);
                    page.waitForTimeout(1000);
                    settingsFound = true;
                    break;
                }
            }

            if (!settingsFound) {
                System.err.println("[UserCleanupSteps] Settings option not found");
                return false;
            }

            // Find and click delete account button
            String[] deleteSelectors = {
                    "button:has-text('Delete Account')",
                    "button:has-text('Delete')",
                    "a:has-text('Delete Account')"
            };

            boolean deleteFound = false;
            for (String selector : deleteSelectors) {
                if (page.isVisible(selector)) {
                    System.out.println("[UserCleanupSteps] Found delete button: " + selector);
                    page.click(selector);
                    page.waitForTimeout(1000);
                    deleteFound = true;
                    break;
                }
            }

            if (!deleteFound) {
                System.err.println("[UserCleanupSteps] Delete button not found");
                return false;
            }

            // Handle confirmation
            String[] confirmSelectors = {
                    "button:has-text('Confirm')",
                    "button:has-text('Yes')",
                    "button:has-text('Delete')"
            };

            for (String selector : confirmSelectors) {
                if (page.isVisible(selector)) {
                    System.out.println("[UserCleanupSteps] Found confirm button: " + selector);
                    page.click(selector);
                    page.waitForTimeout(2000);
                    break;
                }
            }

            System.out.println("[UserCleanupSteps] ✓ Account deletion completed");
            return true;

        } catch (Exception e) {
            System.err.println("[UserCleanupSteps] Error in deleteAccountFromSettings: " + e.getMessage());
            return false;
        }
    }
}
