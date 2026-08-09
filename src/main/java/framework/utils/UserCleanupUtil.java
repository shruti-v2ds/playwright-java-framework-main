package framework.utils;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import framework.config.ConfigManager;

/**
 * Utility for cleaning up user accounts after tests.
 * 
 * Automatic post-condition cleanup that:
 * 1. Logs in with mobile number + mock OTP "0000"
 * 2. Navigates to account/profile settings
 * 3. Deletes user account
 * 4. Gracefully handles errors (cleanup failure doesn't fail tests)
 * 
 * Usage:
 *   boolean success = UserCleanupUtil.deleteUserAccount(page, "9876543210", "0000");
 *   if (!success) {
 *       System.out.println("[Cleanup] User deletion failed but test continues");
 *   }
 * 
 * Locators (from DialinArch):
 * - Mobile input: input[type='tel'], input[name='mobile'], input[placeholder*='mobile']
 * - OTP fields: //*[@id="otp-0"], //*[@id="otp-1"], //*[@id="otp-2"], //*[@id="otp-3"]
 * - Profile icon: //*[@id="root"]/div[1]/nav/div/div/div/button
 * - Verify button: //*[@id="root"]/div[1]/div/button
 */
public class UserCleanupUtil {

    private static final String MOCK_OTP = "0000";
    private static final int CLEANUP_TIMEOUT_MS = 30000; // 30 seconds

    // Locators (verified from DialinArch website inspection)
    // Login Flow
    private static final String MOBILE_INPUT = "input[type='tel']";
    private static final String GET_OTP_BUTTON = "button:has-text('Get OTP'), button:has-text('Send OTP'), button:has-text('Send')";
    
    // OTP Entry Fields (4 individual digit inputs)
    private static final String OTP_INPUT_FIELD = "input[type='text'][maxlength='1']"; // Generic selector for any OTP field
    private static final String OTP_1 = "input[type='text'][maxlength='1']:nth-of-type(1)"; // First OTP digit
    private static final String OTP_2 = "input[type='text'][maxlength='1']:nth-of-type(2)"; // Second OTP digit
    private static final String OTP_3 = "input[type='text'][maxlength='1']:nth-of-type(3)"; // Third OTP digit
    private static final String OTP_4 = "input[type='text'][maxlength='1']:nth-of-type(4)"; // Fourth OTP digit
    
    // Verify & Login Button
    private static final String VERIFY_BUTTON = "button:has-text('Verify'), button:has-text('Verify & Login'), button:has-text('Login')";
    
    // Profile Navigation (after successful login)
    private static final String PROFILE_ICON_BUTTON = "nav button:last-of-type"; // Profile icon is the last button in nav
    
    // Profile Menu and Account Deletion
    private static final String USERNAME_LINK = "text=Jane Smith"; // User's name in profile menu
    private static final String DELETE_USER_PROFILE_LINK = "text=Delete User Profile"; // Delete option link/button
    private static final String DELETE_PROFILE_CONFIRM_BUTTON = "button:has-text('Delete Profile')"; // Confirmation button
    private static final String DELETE_ALL_BUTTON = "button:has-text('Delete All User & Business Profiles')"; // Delete all profiles button (when business exists)

    private UserCleanupUtil() {
        throw new IllegalStateException("Utility class - cannot instantiate");
    }

    /**
     * Main cleanup method: Delete user account
     * 
     * @param page Playwright Page object
     * @param mobile User's mobile number (login credential)
     * @param otpCode Mock OTP code (typically "0000")
     * @return true if deletion successful, false if failed
     */
    public static boolean deleteUserAccount(Page page, String mobile, String otpCode) {
        System.out.println("\n════════════════════════════════════════════════════════════");
        System.out.println("[UserCleanup] Starting user account deletion...");
        System.out.println("[UserCleanup] Mobile: " + mobile);
        System.out.println("════════════════════════════════════════════════════════════");

        try {
            boolean alreadyLoggedIn = isLikelyLoggedIn(page);

            if (!alreadyLoggedIn) {
                if (!navigateToLoginPage(page)) {
                    logError("Failed to navigate to login page");
                    return false;
                }
                logSuccess("Navigated to login page");

                if (!enterMobileNumber(page, mobile)) {
                    logError("Failed to enter mobile number");
                    return false;
                }
                logSuccess("Mobile number entered: " + mobile);

                if (!requestOtp(page)) {
                    logError("Failed to request OTP");
                    return false;
                }
                logSuccess("OTP requested");

                if (!enterMockOtp(page, otpCode)) {
                    logError("Failed to enter OTP");
                    return false;
                }
                logSuccess("Mock OTP entered: " + otpCode);

                if (!verifyLogin(page)) {
                    logError("Failed to verify login");
                    return false;
                }
                logSuccess("Login verified");
            } else {
                logSuccess("User appears to be already logged in; skipping login flow");
            }

            if (!navigateToAccountSettings(page)) {
                logError("Failed to navigate to account settings");
                return false;
            }
            logSuccess("Navigated to account settings");

            if (!deleteAccount(page)) {
                logError("Failed to delete account");
                return false;
            }
            logSuccess("Account deleted successfully");

            System.out.println("════════════════════════════════════════════════════════════");
            System.out.println("[UserCleanup] ✅ CLEANUP COMPLETED SUCCESSFULLY");
            System.out.println("════════════════════════════════════════════════════════════\n");
            return true;

        } catch (Exception e) {
            logError("Unexpected error during cleanup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    // Step 1: Navigate to Login Page
    // ================================================================
    private static boolean navigateToLoginPage(Page page) {
        try {
            System.out.println("[UserCleanup] Action: Navigating to login page...");
            String loginUrl = ConfigManager.getConfig().baseUrl() + "login";
            page.navigate(loginUrl);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            return true;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Navigation failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean isLikelyLoggedIn(Page page) {
        try {
            String[] loggedInIndicators = {
                    "button:has-text('Profile')",
                    "text=Profile",
                    "svg[data-testid*='avatar']",
                    "nav button:last-of-type",
                    "[aria-label*='profile']",
                    "button:has-text('Logout')",
                    "text=Delete User Profile"
            };

            for (String selector : loggedInIndicators) {
                if (page.isVisible(selector)) {
                    return true;
                }
            }

            return page.url().contains("profile") || page.url().contains("dashboard") || page.url().contains("home");
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean clickAnyVisible(Page page, String... selectors) {
        for (String selector : selectors) {
            try {
                if (page.isVisible(selector)) {
                    page.click(selector);
                    page.waitForTimeout(1000);
                    return true;
                }
            } catch (Exception ignored) {
                // Try next selector.
            }
        }
        return false;
    }

    // ================================================================
    // Step 2: Enter Mobile Number
    // ================================================================
    private static boolean enterMobileNumber(Page page, String mobile) {
        try {
            System.out.println("[UserCleanup] Action: Entering mobile number...");
   page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
            String[] selectors = {
                    MOBILE_INPUT,
                    "input[name='mobile']",
                    "input[placeholder*='mobile']",
                    "input[placeholder*='phone']",
                    "input[type='tel']"
            };

            for (String selector : selectors) {
                try {
                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                    if (page.isVisible(selector)) {
                        page.fill(selector, "");
                        page.type(selector, mobile);
                        page.waitForTimeout(500);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            System.err.println("[UserCleanup] Mobile input not visible");
            return false;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error entering mobile: " + e.getMessage());
            return false;
        }
    }

    // ================================================================
    // Step 3: Request OTP
    // ================================================================
    private static boolean requestOtp(Page page) {
        try {
            System.out.println("[UserCleanup] Action: Requesting OTP...");
   page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
            page.onDialog(dialog -> {
                String message = dialog.message();
                System.err.println("[UserCleanup] OTP dialog: " + message);
                if (message != null && message.toLowerCase().contains("wait 60 seconds")
                        || message != null && message.toLowerCase().contains("please wait")) {
                    System.err.println("[UserCleanup] OTP request is rate-limited. Cleanup should retry with a fresh page or after cooldown.");
                }
                dialog.accept();
            });

            String[] selectors = {
                    GET_OTP_BUTTON,
                    "button:has-text('OTP')",
                    "button:has-text('Send')",
                    "button:has-text('Get OTP')",
                    "button:has-text('Verify')"
            };

            for (String selector : selectors) {
                try {
                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                    if (page.isVisible(selector)) {
                        page.click(selector);
                        page.waitForTimeout(3000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);

                        if (page.locator("input[type='text'][maxlength='1']").count() > 0
                                || page.locator("input[placeholder*='OTP']").count() > 0
                                || page.locator("input[name='otp']").count() > 0) {
                            System.out.println("[UserCleanup] OTP field appeared after send request");
                            return true;
                        }

                        if (page.locator("text=Please wait").count() > 0 || page.locator("text=wait 60 seconds").count() > 0) {
                            System.err.println("[UserCleanup] OTP cooldown alert detected; request was blocked by app.");
                            return false;
                        }

                        return true;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            System.err.println("[UserCleanup] Get OTP button not visible");
            return false;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error requesting OTP: " + e.getMessage());
            return false;
        }
    }

    // ================================================================
    // Step 4: Enter Mock OTP "0000"
    // ================================================================
    private static boolean enterMockOtp(Page page, String otpCode) {
        try {
            System.out.println("[UserCleanup] Action: Entering mock OTP: " + otpCode);
                    page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
            String[] otpSelectors = {
                    OTP_INPUT_FIELD,
                    "input[placeholder*='OTP']",
                    "input[placeholder*='otp']",
                    "input[name='otp']",
                    "input[inputmode='numeric']"
            };

            boolean otpVisible = false;
            for (String selector : otpSelectors) {
                try {
                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                    if (page.isVisible(selector)) {
                        otpVisible = true;
                        int otpFieldCount = page.locator(selector).count();
                        if (otpFieldCount == 0) {
                            continue;
                        }

                        String[] digits = otpCode.split("");
                        for (int i = 0; i < Math.min(4, otpFieldCount); i++) {
                            String digit = (i + 1 < digits.length) ? digits[i + 1] : "0";
                            System.out.println("[UserCleanup]   Filling OTP field " + (i + 1) + " with: " + digit);
                            page.locator(selector).nth(i).fill(digit);
                            page.waitForTimeout(200);
                        }
                        System.out.println("[UserCleanup] Mock OTP entered successfully");
                        page.waitForTimeout(500);
                        return true;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            if (!otpVisible) {
                System.err.println("[UserCleanup] OTP input not visible");
            }
            return false;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error entering OTP: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    // Step 5: Verify Login (Click Verify Button)
    // ================================================================
    private static boolean verifyLogin(Page page) {
        try {
            System.out.println("[UserCleanup] Action: Verifying login...");
   page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
            String[] verifySelectors = {
                    VERIFY_BUTTON,
                    "button:has-text('Verify')",
                    "button:has-text('Login')",
                    "button:has-text('Continue')",
                    "button[type='submit']"
            };

            for (String selector : verifySelectors) {
                try {
                    page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(5000));
                    if (page.isVisible(selector)) {
                        page.click(selector);
                        page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
                        System.out.println("[UserCleanup] Login verified");
                        return true;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            System.err.println("[UserCleanup] Verify button not visible");
            return false;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error verifying login: " + e.getMessage());
            return false;
        }
    }

    // ================================================================
    // Step 6: Navigate to Account Settings (Click profile →sss Jane Smith → Delete User Profile)
    // ================================================================
    private static boolean navigateToAccountSettings(Page page) {
        try {
            System.out.println("[UserCleanup] Action: Navigating to profile settings...");

             page.waitForLoadState(LoadState.NETWORKIDLE);
                    page.waitForTimeout(15000);
            if (!(page.url().contains("/login") || page.url().contains("/signup"))) {
                System.out.println("[UserCleanup]   → User is already logged in; navigating directly to /profile before any menu click...");
                try {
                    String profileUrl = "https://dialinarch.com/profile";
                     page.navigate(profileUrl);;
                    page.waitForLoadState(LoadState.NETWORKIDLE);
                    page.waitForTimeout(15000);
                    if (page.isVisible("button:has-text('Delete User Profile')") || page.isVisible("text=Delete User Profile")) {
                        System.out.println("[UserCleanup]   ✓ Direct profile navigation succeeded and delete option is visible");
                        return true;
                    }
                } catch (Exception ignored) {
                    // Fall back to menu flow below.
                }
            }

            System.out.println("[UserCleanup]   → Opening profile icon first...");
            String[] profileIconSelectors = {
                    "//*[@id=\"root\"]/div[1]/nav/div/div/div/button",
                    "button:has(img)",
                    "button[aria-label*='profile']",
                    "button[title*='profile']",
                    "nav button:last-of-type",
                    "button:has-text('Profile')",
                    "button[aria-label='Profile Menu']",
                    "button[title='Profile Menu']"
            };
            page.waitForTimeout(5000);
            boolean profileIconClicked = false;
            for (String selector : profileIconSelectors) {
                try {
                    if (page.locator(selector).count() > 0) {
                         page.waitForTimeout(5000);
                        page.locator(selector).last().click();
                        page.waitForTimeout(1500);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
                        profileIconClicked = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            if (!profileIconClicked) {
                System.err.println("[UserCleanup]   ✗ Profile icon button not visible on the live app");
                return false;
            }

            System.out.println("[UserCleanup]   ✓ Profile menu opened");

            String[] profileNameSelectors = {
                    "a[href='/profile']",
                    "a[href*='/profile']",
                    "button:has-text('sameer lahori')",
                    "button:has-text('John Doe')",
                    "button:has-text('Profile')"
            };

            boolean profileLinkOpened = false;
            for (String selector : profileNameSelectors) {
                try {
                    if (page.isVisible(selector)) {
                        System.out.println("[UserCleanup]   ✓ Clicking user profile name from profile menu: " + selector);
                        page.click(selector);
                        page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
                        profileLinkOpened = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            if (!profileLinkOpened) {
                System.err.println("[UserCleanup]   ✗ Could not click the user profile option from the profile menu");
                return false;
            }

            if (page.isVisible("button:has-text('Delete User Profile')") || page.isVisible("text=Delete User Profile")) {
                System.out.println("[UserCleanup]   ✓ Delete User Profile button is now visible after clicking the user-name option");
                return true;
            }

            System.err.println("[UserCleanup]   ✗ Delete User Profile button not visible after opening the user profile");
            return false;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error navigating to settings: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ================================================================
    // Step 7: Delete Account
    // ================================================================
    private static boolean deleteAccount(Page page) {
        try {
            System.out.println("[UserCleanup] Action: Finding and clicking delete user button...");
            page.evaluate("window.scrollBy(0, 300)");
            page.waitForTimeout(500);

            String[] deleteSelectors = {
                "button:has-text('Delete User Profiles')",        
                "button:text('Delete User Profile')" 
            };

            boolean deleteButtonFound = false;
            for (String selector : deleteSelectors) {
                try {
                    if (page.isVisible(selector)) {
                        System.out.println("[UserCleanup]   ✓ Found delete button: " + selector);
                        page.click(selector);
                        page.waitForTimeout(1000);
                        deleteButtonFound = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            if (!deleteButtonFound) {
                System.err.println("[UserCleanup]   ✗ Delete account button not found");
                return false;
            }

            System.out.println("[UserCleanup]   → Handling confirmation dialog...");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForTimeout(1000);

            String[] confirmSelectors = {
           "button:has-text('Delete All User & Business Profiles')",        
                "button:text('Delete All User & Business Profiles')" , 
                 "button:text('Delete Profile')",
                    "button:has-text('Delete Profile')"
            };

            boolean confirmButtonFound = false;
            for (String selector : confirmSelectors) {
                try {
                    if (page.isVisible(selector)) {
                        System.out.println("[UserCleanup]   ✓ Found confirm button: " + selector);
                        page.click(selector);
                        page.waitForTimeout(2000);
                        page.waitForLoadState(LoadState.NETWORKIDLE);
                        confirmButtonFound = true;
                        break;
                    }
                } catch (Exception ignored) {
                    // Try next selector.
                }
            }

            if (!confirmButtonFound) {
                System.out.println("[UserCleanup]   ⚠ No confirmation dialog found, assuming single-click delete worked");
            }

            System.out.println("[UserCleanup]   ✓ Account deletion completed");
            return true;
        } catch (Exception e) {
            System.err.println("[UserCleanup] Error deleting account: " + e.getMessage());
            return false;
        }
    }

    // ================================================================
    // Logging Helpers
    // ================================================================
    private static void logSuccess(String message) {
        System.out.println("[UserCleanup] ✓ " + message);
    }

    private static void logError(String message) {
        System.err.println("[UserCleanup] ✗ " + message);
    }
}
