package framework.pages;

import com.microsoft.playwright.Page;
import framework.config.ConfigManager;
import framework.utils.ExtentReportUtil;
import framework.utils.OtpApiClient;
import framework.utils.WaitUtil;

public class LoginPage extends BasePage {

    private final String mobileInput = "input[type='tel'], input[name='mobile'], input[placeholder*='mobile'], input[placeholder*='phone']";
    private final String getOtpButton = "button:has-text('OTP'), button:has-text('Send'), button:has-text('Get')";
    private final String otpInput = "input[inputmode='numeric'], input[name='otp'], input[placeholder*='OTP'], input[placeholder*='otp']";
    private final String loginButtonSelector = "button:has-text('Login'), button[type='submit']";
    private final String loginLinkSelector = "a:has-text('Login'), a:has-text('Sign in')";
    private final String dashboardSelector = "text=Dashboard, text=My Account, text=Profile";
    private final String otp1 = "//*[@id=\"otp-0\"]";
    private final String otp2 = "//*[@id=\"otp-1\"]";
    private final String otp3 = "//*[@id=\"otp-2\"]";
    private final String otp4 = "//*[@id=\"otp-3\"]";
    private final String VerifyButton = "//*[@id=\"root\"]/div[1]/div/div/form/button";

    public LoginPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void navigateToLogin() {
        if (isVisible(loginLinkSelector)) {
            click(loginLinkSelector);
        }
        isPageLoaded();
    }

    public void navigateToLoginPage() {
        navigateTo(ConfigManager.getConfig().baseUrl() + "login");
        isPageLoaded();
    }

    public void enterMobile(String mobile) {
        if (isVisible(mobileInput)) {
            type(mobileInput, mobile);
        }
        ExtentReportUtil.attachScreenshot(page, "mobile entered");
    }

    public void requestOtp() {
        if (isVisible(getOtpButton)) {
            click(getOtpButton);
        }
        WaitUtil.sleep(2000);
    }

    public void enterOtp(String mobile) {
        String otp = OtpApiClient.fetchOtp(mobile, "login");
        if (isVisible(otpInput)) {
            type(otpInput, otp);
        }
        ExtentReportUtil.attachScreenshot(page, "otp entered");
    }

     public void enterMockOtp() {
          page.waitForSelector(otp1);
        if (!isVisible(otp1)) {
        throw new AssertionError("Otp input fields are not visible. Cannot enter OTP.");
        }

        if (isVisible(otp1)) {
            type(otp1, "0");
            type(otp2, "0");
            type(otp3, "0");
            type(otp4, "0");
        }
        ExtentReportUtil.attachScreenshot(page, "otp entered");
    }

    public void clickLogin() {
        if (isVisible(loginButtonSelector)) {
            click(loginButtonSelector);
        }
        ExtentReportUtil.attachScreenshot(page, "Login button clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void clickVerifyButton() {
        if (isVisible(VerifyButton)) {
            click(VerifyButton);
        }
        ExtentReportUtil.attachScreenshot(page, "Verify button clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }
    
    public void loginWithOtp(String mobile) {
        navigateToLoginPage();
        enterMobile(mobile);
        requestOtp();
        enterOtp(mobile);
        clickLogin();
    }

    
    public void loginWithMockOtp(String mobile) {
        navigateToLoginPage();
        enterMobile(mobile);
        requestOtp();
        enterMockOtp();
        clickVerifyButton();
    }

    public boolean isLoggedIn() {
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        boolean loggedIn = isVisible(dashboardSelector);
        ExtentReportUtil.attachScreenshot(page, "logged in page");
        return loggedIn;
    }

}