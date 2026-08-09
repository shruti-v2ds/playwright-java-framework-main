package steps;

import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.ExcelUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.Map;

public class HostBusinessSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "HostYourBusiness";

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    @When("user clicks the {string} button")
    public void userClicksButton(String button) {

        switch (button) {

            case "Host Your Business":
                getPageManager().hostYourBusinessPage().clickHostBusiness();
                break;

            case "Create Vendor Profile":
                getPageManager().hostYourBusinessPage().clickCreateVendorProfile();
                break;

                case "Profile Icon":
                getPageManager().hostYourBusinessPage().clickProfileIcon();
                break;

                 case "ABC Architect":
                getPageManager().hostYourBusinessPage().clickABCIcon();
                break;

                

            default:
                throw new IllegalArgumentException("Unknown button : " + button);
        }
    }

     @And("user clicks on the interior next button")
    public void userClicksOnTheInteriorNextButton() {
        getPageManager().hostYourBusinessPage().clickonNextButton();
    }

    @When("user proceeds to the Business Details page")
    public void proceedToBusinessDetails() {
        getPageManager().hostYourBusinessPage().clickNext();
    }

    @When("user enters Business and Basic Details using Excel row {int}")
    public void enterBusinessDetails(int rowIndex) {

        Map<String, String> data =
                ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);

        getPageManager().hostYourBusinessPage().fillBusinessDetails(data);
    }

    @When("user proceeds to the Address Details page")
    public void proceedToAddressPage() {
        getPageManager().hostYourBusinessPage().clickNext();
    }

    @When("user enters Address, Qualification, and Success Story using Excel row {int}")
    public void enterAddressDetails(int rowIndex) {

        Map<String, String> data =
                ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);

        getPageManager().hostYourBusinessPage().fillAddressDetails(data);
    }

    @When("user proceeds to the Category Selection page")
    public void proceedToCategoryPage() {
        getPageManager().hostYourBusinessPage().clickNextCat();
    }

    @When("user selects Business Categories and accepts Terms & Conditions")
    public void selectCategory() {
        getPageManager().hostYourBusinessPage().selectCategoriesAndTerms();
    }

    @When("user proceeds to the Review page")
    public void proceedToReviewPage() {
        getPageManager().hostYourBusinessPage().clickNext();
    }

    @When("user reviews the entered business details")
    public void reviewDetails() {
        getPageManager().hostYourBusinessPage().clickReview();
    }

    @Then("business profile should be created successfully")
    public void verifyBusinessCreated() {

        getPageManager().hostYourBusinessPage().verifyBusinessCreated();

        
        Assert.assertTrue(
                DriverFactory.getPage().url().contains("profile?business"),
                "Business profile creation Successfully done."
        );

        Assert.assertFalse(
                DriverFactory.getPage().url().contains("host-business"),
                "Business profile creation failed."
        );
    }


    @When("user clicks the {string} Profession")
public void user_clicks_the_profession(String string) {
    getPageManager().hostYourBusinessPage().clickProfession(string);
}

   @And("user clicks the {string} Business Category")
public void user_clicks_the_business_category(String string) {
    getPageManager().hostYourBusinessPage().selectCatRunTime(string);
}

    @Given("userregister hosted the business")
    public void userregisterhostedthebusiness() {
        getPageManager().signUpPage().navigateToSignUp();
            
        getPageManager().signUpPage().completeSignUpWithOtp("1234567890");
        getPageManager().hostYourBusinessPage().clickHostBusiness();
    }
}