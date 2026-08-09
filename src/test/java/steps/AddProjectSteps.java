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

/**
 * Step definitions for the Add Project flow.
 * Columns expected in the "AddProject" Excel sheet:
 * ProjectTitle, ProjectDescription, ProjectCategory, Budget, Location,
 * ProjectStatus, ProjectImagePath
 */
public class AddProjectSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "AddProject";

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    // =====================================================
    // Preconditions (reused across flows)
    // =====================================================

  @When("the user navigates to the {string} section")
    public void navigateToSection(String section) {
        getPageManager().addProjectPage().navigateToProjects(section);
    } 

    @When("the user clicks the {string} button")
    public void theUserClicksButton(String button) {
        if ("Add Project".equals(button)) {
            getPageManager().addProjectPage().clickAddProject();
        } else if ("Save Project".equals(button)) {
            getPageManager().addProjectPage().clickSaveProject();
        } else {
            throw new IllegalArgumentException("Unknown button: " + button);
        }
    }

    @When("the user enters project details using Excel row {int}")
    public void userEntersProjectDetails(int rowIndex) {
        Map<String, String> data = ExcelTestData("AddProject", rowIndex);
        getPageManager().addProjectPage().fillProjectDetails(data);
    }

    @And("the user uploads project images using Excel row {int}")
    public void userUploadsProjectImages(int rowIndex) {
        Map<String, String> data = ExcelTestData("AddProject", rowIndex);
        getPageManager().addProjectPage().uploadProjectImages(data);
    }

    @Then("the project should be added successfully")
    public void projectAddedSuccessfully() {
        Assert.assertTrue(
                getPageManager().addProjectPage().isProjectAdded(),
                "Expected project to be added successfully but success message was not visible"
        );
    }

    @And("the project should be visible in the business profile")
    public void projectVisibleInBusinessProfile() {
        Assert.assertTrue(
                getPageManager().addProjectPage().isProjectVisibleInBusinessProfile(),
                "Expected project to be visible in the business profile"
        );
    }

    // =====================================================
    // Helpers
    // =====================================================

    private Map<String, String> ExcelTestData(String sheet, int rowIndex) {
        return ExcelUtil.getTestDataRow(EXCEL_FILE, sheet, rowIndex - 1);
    }
}

