package framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import framework.utils.ExtentReportUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Page Object for the "Add Project" flow on DialinArch.
 * A registered user who has hosted a business can add projects
 * that are visible on their business profile.
 */
public class AddProjectPage extends BasePage {

    // ===========================
    // Navigation / Sections
    // ===========================

    private final String addYourProjectButton = "(//div[@class=\\\"space-y-4\\\"]/div/div/div)[1]";
    private final String addProjectButton = "(//div[@class=\"space-y-4\"]/div/div/div)[1]";

    // ===========================
    // Project Form Fields
    // ===========================

    private final String projectTitleInput = "//label[contains(normalize-space(.),'Project Name')]/following-sibling::input";
    private final String projectDescription = "//label[contains(normalize-space(.),'Description')]/following-sibling::textarea";
    

    // ===========================
    // File Upload
    // ===========================

    private final String projectImagesUpload = "//div/div[@class='flex gap-2']/button[contains(text(),'Add Multiple Images')]";
    private final String projectImageInput = "#add-project-image-input";
    private final String projectImagePreview = "//div/div/div/img[@alt=\"Project 1\"]";

    // ===========================
    // Buttons
    // ===========================

    private final String saveProjectButton = "//button[text()='Save']";

    // ===========================
    // Success / Validation
    // ===========================

    private final String successMessage = "text=Project added successfully, text=Project created successfully";
    private final String projectCard = ".project-card, [class*='project'], [class*='Project']";
    private final String editButton = "//button[@title=\"Edit Project\"]"; 

    public AddProjectPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(LoadState.NETWORKIDLE);
    }

    // =====================================================
    // Navigation
    // =====================================================

    public void navigateToProjects(String section) {
        if (isVisible(addYourProjectButton)) {
            click(addYourProjectButton);
        }
        waitForLoadState(LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "Opened " + section + " section");
    }

    // =====================================================
    // Add Project Actions
    // =====================================================

    public void clickAddProject() {
        Locator profile = page.locator(addProjectButton);

    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));

    profile.scrollIntoViewIfNeeded();

    profile.click(new Locator.ClickOptions().setTimeout(10000));
        waitForLoadState(LoadState.NETWORKIDLE);
        ExtentReportUtil.attachScreenshot(page, "Add Project button clicked");
    }



    // =====================================================
    // Form Filling
    // =====================================================

    public void fillProjectDetails(Map<String, String> data) {

        if (isVisible(projectTitleInput)) {
            type(projectTitleInput, data.get("ProjectTitle"));
        }

        if (isVisible(projectDescription)) {
            type(projectDescription, data.get("ProjectDescription"));
        }

        ExtentReportUtil.attachScreenshot(page, "Project details filled");
    }

    // =====================================================
    // Image Upload
    // =====================================================

    public void uploadProjectImages(Map<String, String> data) {

    String imagePath = data.get("ProjectImagePath");

    if (imagePath == null || imagePath.isBlank()) {
        throw new IllegalArgumentException(
            "ProjectImagePath is empty"
        );
    }

    System.out.println("Image path from Excel: " + imagePath);

    Path filePath = Paths.get(imagePath)
                         .toAbsolutePath()
                         .normalize();

    System.out.println("Final file path: " + filePath);
    System.out.println("File exists: " + Files.exists(filePath));

    if (!Files.exists(filePath)) {
        throw new IllegalArgumentException(
            "Image file not found: " + filePath
        );
    }

    // DO NOT click Add Multiple Images
    // DO NOT wait for hidden input to become visible

    Locator fileInput = page.locator(projectImageInput);

    // Directly upload file to hidden input
    fileInput.setInputFiles(filePath);

    System.out.println("File selected successfully: " + filePath);

    // Wait for image preview
    Locator preview = page.locator(projectImagePreview).first();

    preview.waitFor(
        new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(30000)
    );

    System.out.println("Project image preview is visible.");

    ExtentReportUtil.attachScreenshot(
        page,
        "Project image uploaded successfully"
    );
}
    // =====================================================
    // Save & Validation
    // =====================================================

    public void clickSaveProject() {

    waitForSelectorVisible(saveProjectButton);

    // Verify project image has been uploaded
    Locator preview = page.locator(projectImagePreview).first();

    preview.waitFor(
        new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(30000)
    );

    System.out.println("Image upload completed. Clicking Save.");

    click(saveProjectButton);

    ExtentReportUtil.attachScreenshot(
        page,
        "Save Project button clicked"
    );
}

    public boolean isProjectAdded() {
        boolean added = isVisible(editButton);
        ExtentReportUtil.attachScreenshot(page, "Project save status checked");
        return added;
    }

    public boolean isProjectVisibleInBusinessProfile() {
        boolean visible = isVisible(projectCard);
        ExtentReportUtil.attachScreenshot(page, "Project visibility in business profile checked");
        return visible;
    }
}
