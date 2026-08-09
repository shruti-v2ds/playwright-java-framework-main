import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import java.nio.file.Paths;

public class LocatorCapture {
    public static void main(String[] args) {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(false)
            .setSlowMo(500)); // Slow down for visibility
        Page page = browser.newPage();
        page.setDefaultTimeout(45000); // Increase timeout
        
        try {
            System.out.println("=== DIALINARCH LOCATOR CAPTURE ===\n");
            
            // Step 1: Navigate to login
            System.out.println("Step 1: Navigating to https://dialinarch.com/login");
            page.navigate("https://dialinarch.com/login");
            page.waitForLoadState(LoadState.NETWORKIDLE);
            System.out.println("✓ Login page loaded\n");
            page.waitForTimeout(3000);
            
            // Step 2: Enter mobile
            System.out.println("Step 2: Entering mobile number 8765432112");
            try {
                page.fill("input[type='tel']", "8765432112");
                System.out.println("✓ Mobile entered");
                System.out.println("  Locator: input[type='tel']\n");
            } catch (Exception e) {
                System.out.println("✗ Could not find mobile input: " + e.getMessage());
            }
            
            page.waitForTimeout(1000);
            
            // Step 3: Click Get OTP
            System.out.println("Step 3: Clicking Get OTP button");
            try {
                page.click("button:has-text('Get OTP')");
                page.waitForTimeout(2000);
                System.out.println("✓ OTP requested\n");
            } catch (Exception e) {
                System.out.println("✗ Could not click OTP button: " + e.getMessage());
            }
            
            // Step 4: Enter OTP
            System.out.println("Step 4: Entering OTP 0000");
            try {
                // Wait for OTP input fields to be visible
                page.waitForSelector("input[maxlength='1']", new Page.WaitForSelectorOptions().setTimeout(10000));
                
                // Clear any existing values and enter OTP
                page.locator("input[maxlength='1']").first().fill("0");
                page.locator("input[maxlength='1']").nth(1).fill("0");
                page.locator("input[maxlength='1']").nth(2).fill("0");
                page.locator("input[maxlength='1']").nth(3).fill("0");
                
                page.waitForTimeout(1000);
                System.out.println("✓ OTP entered\n");
            } catch (Exception e) {
                System.out.println("✗ Could not enter OTP: " + e.getMessage());
                System.out.println("  Trying alternative OTP entry method...");
                try {
                    page.fill("//*[@id='otp-0']", "0");
                    page.fill("//*[@id='otp-1']", "0");
                    page.fill("//*[@id='otp-2']", "0");
                    page.fill("//*[@id='otp-3']", "0");
                    System.out.println("✓ OTP entered using XPath\n");
                } catch (Exception e2) {
                    System.out.println("✗ Alternative method also failed: " + e2.getMessage() + "\n");
                }
            }
            
            // Step 5: Verify/Login
            System.out.println("Step 5: Clicking Verify/Login button");
            try {
                // Try multiple possible verify button selectors
                if (page.isVisible("button[type='submit']")) {
                    page.click("button[type='submit']");
                } else if (page.isVisible("button:has-text('Verify')")) {
                    page.click("button:has-text('Verify')");
                } else {
                    page.click("//*[@id=\"root\"]/div[1]/div/button");
                }
                
                page.waitForLoadState(LoadState.NETWORKIDLE);
                page.waitForTimeout(5000);
                System.out.println("✓ Login successful\n");
                System.out.println("Current URL: " + page.url() + "\n");
            } catch (Exception e) {
                System.out.println("✗ Could not verify: " + e.getMessage());
            }
            
            // Step 6: Screenshot dashboard
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("01_dashboard.png")));
            System.out.println("Screenshot: 01_dashboard.png\n");
            
            // Step 7: Find and click profile icon
            System.out.println("Step 7: Looking for Profile Icon Button in nav");
            try {
                // Get all buttons in nav and find the profile one
                int buttonCount = page.locator("nav button").count();
                System.out.println("  Total nav buttons: " + buttonCount);
                
                // The profile icon is usually the rightmost button in nav
                if (buttonCount > 0) {
                    page.click("nav button:last-of-type");
                    page.waitForTimeout(2000);
                    System.out.println("✓ Profile icon clicked (using last button in nav)\n");
                } else {
                    // Try the XPath directly
                    System.out.println("  No nav buttons found, trying XPath...");
                    page.click("//*[@id=\"root\"]/div[1]/nav//button:last-of-type");
                    page.waitForTimeout(2000);
                    System.out.println("✓ Profile icon clicked (using XPath)\n");
                }
                
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("02_profile_menu.png")));
                System.out.println("Screenshot: 02_profile_menu.png\n");
            } catch (Exception e) {
                System.out.println("✗ Could not click profile icon: " + e.getMessage() + "\n");
                System.out.println("  Trying alternative approach...");
                try {
                    // Try clicking by role
                    page.click("button[aria-label*='Profile'], button[aria-label*='profile'], button[aria-label*='Avatar']");
                    page.waitForTimeout(2000);
                    System.out.println("✓ Profile clicked by aria-label\n");
                } catch (Exception e2) {
                    System.out.println("✗ Alternative also failed: " + e2.getMessage() + "\n");
                }
            }
            
            // Step 8: Find Jane Smith link
            System.out.println("Step 8: Looking for Jane Smith / Username link");
            try {
                if (page.isVisible("text=Jane Smith")) {
                    System.out.println("✓ Found 'Jane Smith' text");
                    System.out.println("  Locator: text=Jane Smith");
                    
                    // Get element info
                    Locator element = page.locator("text=Jane Smith");
                    String tagName = element.evaluate("el => el.tagName").toString();
                    System.out.println("  Tag: " + tagName);
                    System.out.println("  Classes: " + element.evaluate("el => el.className").toString() + "\n");
                    
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("03_jane_smith_visible.png")));
                    System.out.println("Screenshot: 03_jane_smith_visible.png\n");
                } else {
                    System.out.println("✗ 'Jane Smith' text not visible\n");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage() + "\n");
            }
            
            // Step 9: Click on Jane Smith
            System.out.println("Step 9: Clicking on Jane Smith");
            try {
                page.click("text=Jane Smith");
                page.waitForLoadState();
                page.waitForTimeout(2000);
                System.out.println("✓ Navigated to profile page\n");
                
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("04_profile_page.png")));
                System.out.println("Screenshot: 04_profile_page.png\n");
            } catch (Exception e) {
                System.out.println("✗ Could not click: " + e.getMessage() + "\n");
            }
            
            // Step 10: Find Delete User Profile link
            System.out.println("Step 10: Looking for 'Delete User Profile' link");
            try {
                if (page.isVisible("text=Delete User Profile")) {
                    System.out.println("✓ Found 'Delete User Profile'");
                    
                    Locator element = page.locator("text=Delete User Profile");
                    String tagName = element.evaluate("el => el.tagName").toString();
                    String className = element.evaluate("el => el.className").toString();
                    System.out.println("  Locator: text=Delete User Profile");
                    System.out.println("  Tag: " + tagName);
                    System.out.println("  Classes: " + className + "\n");
                    
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("05_delete_user_profile_visible.png")));
                    System.out.println("Screenshot: 05_delete_user_profile_visible.png\n");
                } else {
                    System.out.println("✗ 'Delete User Profile' not found\n");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage() + "\n");
            }
            
            // Step 11: Click Delete User Profile
            System.out.println("Step 11: Clicking 'Delete User Profile'");
            try {
                page.click("text=Delete User Profile");
                page.waitForTimeout(1500);
                System.out.println("✓ Clicked\n");
                
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("06_delete_confirmation.png")));
                System.out.println("Screenshot: 06_delete_confirmation.png\n");
            } catch (Exception e) {
                System.out.println("✗ Could not click: " + e.getMessage() + "\n");
            }
            
            // Step 12: Find Delete Profile button
            System.out.println("Step 12: Looking for 'Delete Profile' button in dialog");
            try {
                if (page.isVisible("button:has-text('Delete Profile')")) {
                    System.out.println("✓ Found 'Delete Profile' button");
                    
                    Locator element = page.locator("button:has-text('Delete Profile')");
                    String className = element.evaluate("el => el.className").toString();
                    System.out.println("  Locator: button:has-text('Delete Profile')");
                    System.out.println("  Classes: " + className + "\n");
                    
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("07_delete_profile_button.png")));
                    System.out.println("Screenshot: 07_delete_profile_button.png\n");
                } else {
                    System.out.println("✗ 'Delete Profile' button not found\n");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage() + "\n");
            }
            
            // Step 13: Click Delete Profile
            System.out.println("Step 13: Clicking 'Delete Profile' button");
            try {
                page.click("button:has-text('Delete Profile')");
                page.waitForTimeout(1500);
                System.out.println("✓ Clicked\n");
                
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("08_cannot_delete_dialog.png")));
                System.out.println("Screenshot: 08_cannot_delete_dialog.png (Cannot Delete Profile)\n");
            } catch (Exception e) {
                System.out.println("✗ Could not click: " + e.getMessage() + "\n");
            }
            
            // Step 14: Find Delete All button
            System.out.println("Step 14: Looking for 'Delete All User & Business Profiles' button");
            try {
                if (page.isVisible("button:has-text('Delete All User & Business Profiles')")) {
                    System.out.println("✓ Found 'Delete All User & Business Profiles' button");
                    
                    Locator element = page.locator("button:has-text('Delete All User & Business Profiles')");
                    String className = element.evaluate("el => el.className").toString();
                    String text = element.textContent();
                    System.out.println("  Locator: button:has-text('Delete All User & Business Profiles')");
                    System.out.println("  Classes: " + className);
                    System.out.println("  Text: " + text + "\n");
                    
                    page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get("09_delete_all_button.png")));
                    System.out.println("Screenshot: 09_delete_all_button.png\n");
                } else {
                    System.out.println("✗ Button not found\n");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage() + "\n");
            }
            
            System.out.println("=== LOCATOR CAPTURE COMPLETE ===");
            System.out.println("\nAll screenshots saved to workspace directory");
            System.out.println("Check them in the file explorer to verify locators");
            
            page.waitForTimeout(5000);
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        } finally {
            browser.close();
            playwright.close();
        }
    }
}
