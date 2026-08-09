package steps;

import framework.config.ConfigManager;
import framework.core.DriverFactory;
import framework.managers.PageManager;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class LoginSteps {

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    @When("user logs in with OTP using mobile {string}")
    public void userLogsInWithOtp(String mobile) {
        getPageManager().loginPage().loginWithOtp(mobile);
    }

    @When("user logs in with OTP")
    public void userLogsInWithOtpFromConfig() {
        String mobile = ConfigManager.getConfig().testMobile();
        getPageManager().loginPage().loginWithOtp(mobile);
    }

    @Then("user should be logged in successfully")
    public void verifyLogin() {
        Assert.assertTrue(getPageManager().loginPage().isLoggedIn());
    }

}
