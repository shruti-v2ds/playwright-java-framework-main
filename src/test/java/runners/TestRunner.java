package runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "Hooks"},
       // tags = "@login or @home or @search or @signup",
        tags = "@skillservice",

        plugin = {
                "pretty",

                // Console output
                "summary",

                // JSON report
                "json:target/cucumber-reports/cucumber.json",

                // HTML report
                "html:target/cucumber-reports/cucumber.html",

                // JUnit XML report
                "junit:target/cucumber-reports/cucumber.xml",
//
                // Extent Report Adapter
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },

        monochrome = true,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

}
