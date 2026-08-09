package framework.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import framework.config.ConfigManager;
import framework.utils.ExtentReportUtil;
import framework.utils.OtpApiClient;
import com.microsoft.playwright.Locator;

import java.util.Map;

public class SignUpPage extends BasePage {

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
    private final String verifyOtpButton = "//*[@id=\"root\"]/div[1]/div/button";
    private final String otpField1 = "//*[@id=\"otp-0\"]";
    private final String otpField2 = "//*[@id=\"otp-1\"]";
    private final String otpField3 = "//*[@id=\"otp-2\"]";
    private final String otpField4 = "//*[@id=\"otp-3\"]";

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
        if (isVisible(createProfileButton)) {
            click(createProfileButton);
        }
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "create profile clicked");
    }

    public void registerUser(String firstName, String lastName, String phone, String email) {
        clickLoginSignupButton();
        fillRegistrationForm(firstName, lastName, phone, email);
        clickCreateProfile();
    }

    public void registerUser(Map<String, String> data) {
        registerUser(
                data.get("FirstName"),
                data.get("LastName"),
                data.get("PhoneNumber"),
                data.get("Email")
        );
    }

   public void enterMockOTP() {
        page.waitForSelector(verifyOtpButton);
        if (!isVisible(verifyOtpButton)) {
        throw new AssertionError("Verify OTP button is not visible.");
        }

    type(otpField1, "0");
    type(otpField2, "0");
    type(otpField3, "0");
    type(otpField4, "0");

    click(verifyOtpButton);
    System.out.println("Mock OTP entered and verified.");
}


 public void clickVerifyButton() {
        page.waitForSelector(verifyOtpButton);
        if (!isVisible(verifyOtpButton)) {
        throw new AssertionError("Verify OTP button is not visible.");
        }
    click(verifyOtpButton);
    System.out.println("verify Otp Button clicked.");
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

}
