package steps;

import framework.config.ConfigManager;
import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.pages.HomePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class HomeSteps {

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    @Given("user is on dialinarch landing page")
    public void userIsOnDialinarchLandingPage() {
        HomePage homePage = getPageManager().homePage();
        homePage.navigate(ConfigManager.getConfig().baseUrl());
    }

    // @When("user navigates to login page")
    // public void userNavigatesToLoginPage() {
    //     getPageManager().loginPage().navigateToLogin();
    // }

    @Then("the landing page should be displayed correctly")
    public void verifyLandingPage() {
        String currentUrl = DriverFactory.getPage().url();
        Assert.assertTrue(
                currentUrl.contains("dialinarch.com"),
                "Expected URL to contain dialinarch.com but was: " + currentUrl
        );
    }

    @When("user searches for architects in {string}")
    public void userSearchesForArchitects(String location) {
        getPageManager().homePage().searchForArchitects(location);
    }

    @Then("search results should be displayed")
    public void verifySearchResults() {
        String currentUrl = DriverFactory.getPage().url();
        Assert.assertFalse(
                currentUrl.equals(ConfigManager.getConfig().baseUrl()),
                "URL should have changed after search"
        );
    }

}
