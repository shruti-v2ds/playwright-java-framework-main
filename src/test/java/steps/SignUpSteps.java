package steps;

import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.ExcelUtil;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "RegistrationData";

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    // =====================================================
    // Existing OTP-based signup steps
    // =====================================================
      @Given("registered user is on the DialinArch landing page {int}")
    public void registerUserIsOnDialinarchLandingPage(int rowIndex) {
        Map<String, String> testData =
          ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);
    String mobileNumber = testData.get("PhoneNumber");
    getPageManager().loginPage().loginWithMockOtp(mobileNumber); 
    }
    
    @When("user navigates to signup page")
    public void userNavigatesToSignUp() {
        getPageManager().signUpPage().navigateToSignUp();
    }

    @When("user enters mobile {string} for signup")
    public void userEntersMobileForSignup(String mobile) {
        getPageManager().signUpPage().enterMobileAndGetOtp(mobile);
    }

    @When("user completes signup with OTP for mobile {string}")
    public void userCompletesSignup(String mobile) {
        getPageManager().signUpPage().completeSignUpWithOtp(mobile);
    }

    @When("user signs up with mobile {string}")
    public void userSignsUp(String mobile) {
        getPageManager().signUpPage().navigateToSignUp();
        getPageManager().signUpPage().enterMobileAndGetOtp(mobile);
        getPageManager().signUpPage().completeSignUpWithOtp(mobile);
    }

    @Then("signup should be successful")
    public void verifySignupSuccess() {
        String currentUrl = DriverFactory.getPage().url();
        Assert.assertFalse(
                currentUrl.contains("signup"),
                "Expected to navigate away from signup page after successful registration"
        );
    }

    // =====================================================
    // Registration form steps (Excel-driven)
    // =====================================================

    @When("user clicks on Login Signup button")
    public void userClicksOnLoginSignupButton() {
        getPageManager().signUpPage().clickLoginSignupButton();
    }

    @When("user fills registration form with data from Excel row {int}")
    public void userFillsRegistrationFormFromExcel(int rowIndex) {
        Map<String, String> testData = ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);
        getPageManager().signUpPage().fillRegistrationForm(testData);
    }

    @When("user clicks Create Profile button")
    public void userClicksCreateProfileButton() {
        getPageManager().signUpPage().clickCreateProfile();
    }

    @When("user registers from Excel row {int}")
    public void userRegistersFromExcel(int rowIndex) {
        Map<String, String> testData = ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);
        getPageManager().signUpPage().registerUser(testData);
    }

     @When("user should enter the OTP")
    public void usershouldentertheOTP() {
        getPageManager().signUpPage().enterMockOTP();
        getPageManager().signUpPage().clickVerifyButton();;
       
    }

    @Then("registration should be successful")
    public void verifyRegistrationSuccess() {
        String currentUrl = DriverFactory.getPage().url();
        Assert.assertFalse(
                currentUrl.contains("signup") && currentUrl.contains("register"),
                "Expected to navigate away from registration page after successful profile creation"
        );
    }

    // =====================================================
    // Skill Service Registration Steps
    // =====================================================

    @When("user fills registration form with skill service data from Excel row {int}")
    public void userFillsSkillServiceDataFromExcel(int rowIndex, DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        if (!rows.isEmpty()) {
            Map<String, String> data = rows.get(0);
            // Fill standard registration fields
            getPageManager().signUpPage().fillRegistrationForm(
                    data.get("FullName"),
                    "",  // Last name not in skill service data
                    data.get("Phone"),
                    data.get("Email")
            );
            // Store skill service specific data for later use if needed
            System.out.println("Skill Service Type: " + data.get("ServiceType"));
            System.out.println("Description: " + data.get("Description"));
            System.out.println("Experience: " + data.get("Experience"));
        }
    }

    @When("user fills basic registration information with {string}, {string}, and {string}")
    public void userFillsBasicRegistrationInfo(String fullName, String email, String phone) {
        getPageManager().signUpPage().fillRegistrationForm(fullName, "", phone, email);
    }

    @When("user selects service type as {string}")
    public void userSelectsServiceType(String serviceType) {
        // This step can be enhanced based on actual UI implementation
        // For now, it's a placeholder that can be extended
        System.out.println("Selected service type: " + serviceType);
    }

    @When("user fills service description as {string}")
    public void userFillsServiceDescription(String description) {
        // This step can be enhanced based on actual UI implementation
        System.out.println("Service description: " + description);
    }

    @When("user fills years of experience as {string}")
    public void userFillsExperience(String years) {
        // This step can be enhanced based on actual UI implementation
        System.out.println("Years of experience: " + years);
    }

    @When("user fills address details with {string}, {string}, {string}, {string}")
    public void userFillsAddressDetails(String address, String city, String state, String zipCode) {
        // This step can be enhanced based on actual UI implementation
        System.out.println("Address: " + address + ", " + city + ", " + state + " " + zipCode);
    }
}
