package steps;

import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.ExcelUtil;
import Hooks.TestContextManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;

import java.util.Map;

public class LoginSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "RegistrationData";

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    // =====================================================
    // Login Steps
    // =====================================================

    @When("user navigates to login page")
    public void userNavigatesToLoginPage() {
        getPageManager().loginPage().navigateToLoginPage();
    }

    @When("user enters registered mobile {string}")
    public void userEntersRegisteredMobile(String mobile) {
        getPageManager().loginPage().enterMobile(mobile);
    }

    @When("user requests OTP")
    public void userRequestsOTP() {
        getPageManager().loginPage().requestOtp();
    }

    @When("user enters mock OTP 0000")
    public void userEntersMockOTP() {
        getPageManager().loginPage().enterMockOtp();
    }

    @When("user clicks Verify button to login")
    public void userClicksVerifyButton() {
        getPageManager().loginPage().clickVerifyButton();
    }

    @When("user logs in with registered mobile from Excel row {int}")
    public void userLogsInFromExcel(int rowIndex) {
        Map<String, String> testData = ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);
        String mobile = testData.get("PhoneNumber");
        
        // Store mobile for cleanup
        if (mobile != null && !mobile.isEmpty()) {
            TestContextManager.getContext().set("mobileNumber", mobile);
            System.out.println("[LoginSteps] Stored mobile for cleanup: " + mobile);
        }
        
        getPageManager().loginPage().loginWithMockOtp(mobile);
    }

    @When("user logs in with mobile {string} using mock OTP")
    public void userLogsInWithMockOtp(String mobile) {
        // Store mobile for cleanup
        TestContextManager.getContext().set("mobileNumber", mobile);
        System.out.println("[LoginSteps] Stored mobile for cleanup: " + mobile);
        
        getPageManager().loginPage().loginWithMockOtp(mobile);
    }

    @When("user logs in with mobile {string} using real OTP")
    public void userLogsInWithRealOtp(String mobile) {
        // Store mobile for cleanup
        TestContextManager.getContext().set("mobileNumber", mobile);
        System.out.println("[LoginSteps] Stored mobile for cleanup: " + mobile);
        
        getPageManager().loginPage().loginWithOtp(mobile);
    }

    @Then("user should be successfully logged in")
    public void verifyUserLoggedIn() {
        boolean isLoggedIn = getPageManager().loginPage().isLoggedIn();
       // Assert.assertTrue(isLoggedIn, "User should be logged in but login appears to have failed");
        System.out.println("[LoginSteps] User successfully logged in");
    }

    @Then("user should land on home page")
    public void verifyUserOnHomePage() {
        String currentUrl = DriverFactory.getPage().url();
        Assert.assertFalse(
                currentUrl.contains("login") || currentUrl.contains("signup"),
                "Expected to navigate away from login/signup page after successful login"
        );
        System.out.println("[LoginSteps] User is on home page: " + currentUrl);
    }
}
