package runners;

import io.cucumber.testng.CucumberOptions;
import io.cucumber.testng.AbstractTestNGCucumberTests;

/**
 * Main test runner for Cucumber/Playwright automation.
 * 
 * Configuration:
 * - Features: src/test/resources/features
 * - Step Definitions: steps, Hooks
 * - Default tag: @skillservice (existing tests)
 * 
 * To run different test profiles, modify the 'tags' property:
 * 
 * 1. Run existing skill service tests (DEFAULT):
 *    tags = "@skillservice"
 * 
 * 2. Run existing business hosting tests:
 *    tags = "@Completeflow"
 * 
 * 3. Run new data-driven multi-user tests:
 *    tags = "@DataDriven"
 * 
 * 4. Run data-driven multi-user multi-business:
 *    tags = "@DataDriven and @MultiUser"
 * 
 * 5. Run data-driven single-user multi-business:
 *    tags = "@DataDriven and @SingleUserMultiBusinessInSession"
 * 
 * 6. Run data-driven Excel validation:
 *    tags = "@DataDriven and @ExcelValidation"
 * 
 * 7. Run all tests:
 *    tags = ""  (empty string)
 * 
 * 8. Run backward compatibility check (exclude new data-driven):
 *    tags = "not @DataDriven"
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"steps", "Hooks"},

        // ==============================================================
        // TAG CONFIGURATION - Change this to run different test profiles
        // ==============================================================
        // Option 1: Run existing skill service tests (DEFAULT)
        tags = "@firstscenario1",
        
        // Option 2: Run existing business hosting tests
        // tags = "@Completeflow",
        
        // Option 3: Run new data-driven multi-user tests
        // tags = "@DataDriven",
        
        // Option 4: Run data-driven multi-user multi-business
        // tags = "@DataDriven and @MultiUser",
        
        // Option 5: Run data-driven single-user multi-business in session
        // tags = "@DataDriven and @SingleUserMultiBusinessInSession",
        
        // Option 6: Run data-driven Excel validation
        // tags = "@DataDriven and @ExcelValidation",
        
        // Option 7: Run all tests (comment out tags property)
        // tags = "",
        
        // Option 8: Backward compatibility (exclude new data-driven tests)
        // tags = "not @DataDriven",
        
        // ==============================================================

        plugin = {
                "pretty",

                // Console output
                "summary",

                // JSON report
                "json:target/cucumber-reports/cucumber.json",

                // HTML report
             //   "html:target/cucumber-reports/cucumber.html",

                // JUnit XML report
             //   "junit:target/cucumber-reports/cucumber.xml",

                // Extent Report Adapter
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        },

        monochrome = true,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {

}
