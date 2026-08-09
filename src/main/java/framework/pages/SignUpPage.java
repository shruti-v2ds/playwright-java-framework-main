package framework.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import framework.config.ConfigManager;
import framework.core.DriverFactory;
import framework.utils.ExtentReportUtil;
import framework.utils.OtpApiClient;
import framework.utils.WaitUtil;

import com.aventstack.extentreports.util.Assert;
import com.microsoft.playwright.Locator;

import java.util.Map;

public class SignUpPage extends BasePage {

    private String lastRegisteredFirstName;

    // OTP based signup selectors
    private final String mobileInput = "input[type='tel'], input[name='mobile'], input[placeholder*='mobile'], input[placeholder*='phone']";
    private final String getOtpButton = "button:has-text('OTP'), button:has-text('Send'), button:has-text('Get')";
    private final String otpInput = "input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";
    private final String registerButton = "button:has-text('Register'), button:has-text('Sign Up'), button[type='submit']";

    // Registration form selectors
    private final String loginSignupButton = "//*[@id=\"root\"]/div[1]/nav/div/div/a[3]";
    private final String firstNameInput = "input[name='first_name'], input[name='firstName'], input[placeholder*='First'], input[id*='first']";
    private final String lastNameInput = "input[name='last_name'], input[name='lastName'], input[placeholder*='Last'], input[id*='last']";
    private final String phoneInput = "input[name='phone'], input[name='mobile'], input[type='tel'], input[placeholder*='phone'], input[placeholder*='Phone'], input[placeholder*='mobile']";
    private final String emailInput = "input[name='email'], input[type='email'], input[placeholder*='email'], input[placeholder*='Email']";
    private final String createProfileButton = "button:has-text('Create Profile'), button:has-text('Create'), button[type='submit']";
    private final String verifyOtpButton = "button[data-testid='btn-verify-login'], button:has-text('Verify & Login'), button:has-text('Verify'), button[type='submit']";
    private final String otpField1 = "input[type='text'], input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";
    private final String otpField2 = "input[type='text'], input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";
    private final String otpField3 = "input[type='text'], input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";
    private final String otpField4 = "input[type='text'], input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";

    public SignUpPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    // =====================================================
    // Navigation
    // =====================================================

    public void navigateToSignUp() {
        navigateTo(ConfigManager.getConfig().baseUrl() + "signup");
        isPageLoaded();
    }

    public void clickLoginSignupButton() {
        page.waitForTimeout(2000);
                          isPageLoaded();
        if (isVisible(loginSignupButton)) {
            click(loginSignupButton);
        }
        isPageLoaded();
    }

    // =====================================================
    // Registration Form Methods
    // =====================================================

    public void fillRegistrationForm(String firstName, String lastName, String phone, String email) {
        if (isVisible(firstNameInput)) {
            type(firstNameInput, firstName);
        }
        if (isVisible(lastNameInput)) {
            type(lastNameInput, lastName);
        }
        if (isVisible(phoneInput)) {
            type(phoneInput, phone);
        }
        if (isVisible(emailInput)) {
            type(emailInput, email);
        }
           page.waitForTimeout(2000);
           page.waitForLoadState(LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "registration form filled");
    }

    public void fillRegistrationForm(Map<String, String> data) {
        fillRegistrationForm(
                data.get("FirstName"),
                data.get("LastName"),
                data.get("PhoneNumber"),
                data.get("Email")
        );
    }

    public void clickCreateProfile() {
       WaitUtil.waitForLoad(page, com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        if (isVisible(createProfileButton)) {
            click(createProfileButton);
        }
          page.waitForTimeout(20000);
           page.waitForLoadState(LoadState.NETWORKIDLE);
            ExtentReportUtil.attachScreenshot(page, "create profile clicked");
    }

    public void registerUser(String firstName, String lastName, String phone, String email) {
        this.lastRegisteredFirstName = firstName;
        clickLoginSignupButton();
        fillRegistrationForm(firstName, lastName, phone, email);
        clickCreateProfile();
    }

    public void     registerUser(Map<String, String> data) {
        String firstName = data.get("FirstName");
        this.lastRegisteredFirstName = firstName;
        registerUser(
                firstName,
                data.get("LastName"),
                data.get("PhoneNumber"),
                data.get("Email")
        );
            waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "Entered registration details");
    }

   public void enterMockOTP() {
       page.waitForSelector(verifyOtpButton, new Page.WaitForSelectorOptions().setTimeout(300000));
        if (!isVisible(verifyOtpButton)) {
           throw new AssertionError("Verify OTP button is not visible.");
        }

       Locator otpFields = page.locator("input[type='text'], input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']");
       int otpFieldCount = otpFields.count();
       if (otpFieldCount == 0) {
           throw new AssertionError("OTP input fields are not visible on the live page.");
       }

       int fillCount = Math.min(4, otpFieldCount);
       for (int i = 0; i < fillCount; i++) {
           otpFields.nth(i).fill("0");
       }
         page.waitForTimeout(2000);
       click(verifyOtpButton);
       System.out.println("Mock OTP entered and verified.");
       waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
       ExtentReportUtil.attachScreenshot(page, "Mock OTP entered and verified");
   }


 public void clickVerifyButton() {
        page.waitForSelector(verifyOtpButton);
        if (!isVisible(verifyOtpButton)) {
        throw new AssertionError("Verify OTP button is not visible.");
        }
    click(verifyOtpButton);
    System.out.println("verify Otp Button clicked.");
     ExtentReportUtil.attachScreenshot(page, "verify Otp Button clicked");
}
    // =====================================================
    // OTP based signup methods (existing)
    // =====================================================

    public void enterMobileAndGetOtp(String mobile) {
        if (isVisible(mobileInput)) {
            type(mobileInput, mobile);
        }
        ExtentReportUtil.attachScreenshot(page, "mobile entered for signup");
        if (isVisible(getOtpButton)) {
            click(getOtpButton);
        }
    }

    public void completeSignUpWithOtp(String mobile) {
        String otp = OtpApiClient.fetchOtp(mobile, "register");
        if (isVisible(otpInput)) {
            type(otpInput, otp);
        }
        ExtentReportUtil.attachScreenshot(page, "otp entered for signup");
        if (isVisible(registerButton)) {
            click(registerButton);
        }
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void verifyUrl(String urls){
         String currentUrl = DriverFactory.getPage().url();
        org.testng.Assert.assertFalse(
                currentUrl.contains(urls) && currentUrl.contains(urls),
                "Expected to navigate away from registration page after successful profile creation"    
        );
         waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "Mock OTP entered and verified");
    }
//verify register url after registration done
    public void verifyRegisterUrl(String urls){
        waitForLoadState(LoadState.NETWORKIDLE);
         String currentUrl = DriverFactory.getPage().url();
        org.testng.Assert.assertFalse(
                currentUrl.contains(urls),
                "Expected to navigate away from registration page after successful profile creation"    
        );
        
        ExtentReportUtil.attachScreenshot(page, "user registered successfully and navigated to dashboard");
    }

    //verfy after registration user can see his profile name on dashboard
    public void verifyProfileNameOnDashboards() {
        verifyProfileNameOnDashboard(lastRegisteredFirstName);
    }

    public void verifyProfileNameOnDashboard(String profileName){
        String expectedName = profileName != null && !profileName.trim().isEmpty() ? profileName : lastRegisteredFirstName;
        if (expectedName == null || expectedName.trim().isEmpty()) {
           throw new AssertionError("First name is missing. Pass it from registration data or call verifyProfileNameOnDashboard() after registerUser(...). ");
        }

        page.waitForTimeout(2000);
        String profileNameOnDashboard = page.locator("a[href='/profile']").filter(new Locator.FilterOptions().setHasText(expectedName)).first().textContent();
        org.testng.Assert.assertTrue(profileNameOnDashboard != null && profileNameOnDashboard.contains(expectedName),
               "Profile name on dashboard does not match expected name. Expected: " + expectedName + ", Actual: " + profileNameOnDashboard);
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "Profile name verified on dashboard");
    }
    
}
