# 🧩 Adding a New Feature

This step-by-step guide shows how to add a **new feature** (e.g., **Add Project**) to the framework.

---

## 🎯 Prerequisites

Understand the patterns used:
- See [ARCHITECTURE.md](ARCHITECTURE.md)
- See [CONTRIBUTING.md](CONTRIBUTING.md)

---

## 📝 Step-by-Step Workflow

### 1. Write the Feature File (BDD First)
Create `src/test/resources/features/xxx.feature`:

```gherkin
Feature: Add project for registered user and hosted business

  @addproject
  Scenario: User adds a project using Excel data
    Given the user is registered successfully
    And the user has hosted a business successfully
    And the user is logged in
    When the user navigates to the "Projects" section
    And the user clicks the "Add Project" button
    And the user enters project details using Excel row 1
    And the user uploads project images using Excel row 1
    And the user clicks the "Save Project" button
    Then the project should be added successfully
```

> Write the Gherkin first so you know exactly what steps to implement.

---

### 2. Create the Page Object
Create `src/main/java/framework/pages/XxxPage.java`:

```java
package framework.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import framework.utils.ExtentReportUtil;
import java.util.Map;

public class AddProjectPage extends BasePage {

    private final String addProjectButton = "button:has-text('Add Project')";
    private final String projectTitle = "input[name='title'], input[placeholder*='Title']";
    private final String projectDescription = "textarea[name='description']";
    private final String directAllocatorCheckbox = "input[name='direct_allocator']";
    private final String saveProjectButton = "button:has-text('Save Project')";

    public AddProjectPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void navigateToProjects(String section) {
        Locator loc = page.locator("nav:has-text('" + section + "')");
        // click logic...
        ExtentReportUtil.attachScreenshot(page, "Opened Projects section");
    }

    public void clickAddProject() {
        if (isVisible(addProjectButton)) {
            click(addProjectButton);
        }
    }

    public void fillProjectDetails(Map<String, String> data) {
        if (isVisible(projectTitle)) {
            type(projectTitle, data.get("ProjectTitle"));
        }
        if (isVisible(projectDescription)) {
            type(projectDescription, data.get("ProjectDescription"));
        }
        ExtentReportUtil.attachScreenshot(page, "Project details filled");
    }

    public void clickSaveProject() {
        if (isVisible(saveProjectButton)) {
            click(saveProjectButton);
        }
        waitForLoadState(LoadState.NETWORKIDLE);
    }

    public boolean isProjectAdded() {
        return isVisible("text=Project added successfully");
    }
}
```

---

### 3. Register the Page in `PageManager`
Add a private field + lazy accessor:

```java
private AddProjectPage addProjectPage;

public AddProjectPage addProjectPage() {
    return Objects.requireNonNullElseGet(addProjectPage, () -> addProjectPage = new AddProjectPage(page));
}
```

---

### 4. Write Step Definitions
Create `src/test/java/steps/AddProjectSteps.java`:

```java
package steps;

import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.ExcelUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import java.util.Map;

public class AddProjectSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "AddProject";

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    @And("the user has registered successfully")
    public void userRegistered() {
        // reuse existing registration steps/logic
    }

    @When("the user navigates to the {string} section")
    public void navigateToSection(String section) {
        getPageManager().addProjectPage().navigateToProjects(section);
    }

    @When("the user clicks the {string} button")
    public void clickButton(String button) {
        if (button.equals("Add Project")) {
            getPageManager().addProjectPage().clickAddProject();
        }
    }

    @When("the user enters project details using Excel row {int}")
    public void enterProjectDetails(int rowIndex) {
        Map<String, String> data = ExcelUtil.getTestDataRow(EXCEL_FILE, EXCEL_SHEET, rowIndex - 1);
        getPageManager().addProjectPage().fillProjectDetails(data);
    }

    @When("the user uploads project images using Excel row {int}")
    public void uploadImages(int rowIndex) {
        // upload logic via ExcelUtil
    }

    @And("the user clicks the {string} button")
    public void clickSave(String button) {
        getPageManager().addProjectPage().clickSaveProject();
    }

    @Then("the project should be added successfully")
    public void verifyProjectAdded() {
        Assert.assertTrue(getPageManager().addProjectPage().isProjectAdded());
    }
}
```

> ⚠️ Watch for **duplicate step patterns**. `"user clicks the {string} button"` already exists in `HostBusinessSteps`. Use a **distinct** phrase (e.g. `"the user clicks the {string} button"` vs existing `"user clicks the {string} button"`) or handle both in one step class.

---

### 5. Add Excel Test Data
- Add a new sheet (e.g. `AddProject`) to `src/test/resources/testdata/registration_testdata.xlsx`.
- Columns must match the keys used in `fillProjectDetails` (e.g. `ProjectTitle`, `ProjectDescription`).

You can extend `TestDataGenerator` to add the new sheet via a new method.

---

### 6. Update the Runner Tags
In `src/test/java/runners/TestRunner.java`, add your tag:

```java
tags = "@addproject or @Completeflow",
```

Or run only your feature:
```bash
mvn clean test -Dcucumber.filter.tags="@addproject"
```

---

### 7. Compile & Run
```bash
mvn clean compile
mvn clean test -Dcucumber.filter.tags="@addproject"
```

---

## ✅ Checklist

- [ ] Feature file written (BDD first)
- [ ] Page object created + registered in `PageManager`
- [ ] Step definitions (no ambiguous/duplicate steps)
- [ ] Excel sheet added (if data-driven)
- [ ] Runner tags updated
- [ ] `mvn clean compile` passes
- [ ] Scenario passes

---

## 📚 Related Docs
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [FEATURES_ROADMAP.md](FEATURES_ROADMAP.md)

