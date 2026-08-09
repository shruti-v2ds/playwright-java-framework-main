package framework.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;

import framework.utils.ExtentReportUtil;

import java.util.Map;

public class HostYourBusinessPage extends BasePage {

    // ===========================
    // Buttons
    // ===========================

    private final String hostYourBusinessButton = "//*[@id=\"root\"]/div[1]/nav/div/div/div/div/div[2]/button/div/div[2]/p";
    private final String nextButton = "//button[text()='Next']";
    private final String NextButtonCat="//button[text()='Next' and @class='px-4 py-2 rounded-lg font-medium text-sm bg-blue-500 text-white hover:bg-blue-600']";
    private final String reviewButton = "//button[text()='Review']";
    private final String createVendorProfileButton = "//button[text()='Create Vendor Profile →']";
    private final String profileIconButton = "//*[@id=\"root\"]/div[1]/nav/div/div/div/button";
    private final String ABCIconButton = "//button/div/div/p[contains(text(),'ABC')]";
    private final String hostedBusinessprofileiCon = "//*[@id=\"root\"]/div[1]/div/aside/div/div/div[1]/div/div";
    private final String professionInterior = "(//button/div/div/div[contains(text(),'Interior')])[1]";
    private final String nextButtonUpdate = "//div[@class='flex justify-between gap-3 mt-8']/button[text()='Next']";
    // ===========================
    // Business & Basic Details
    // ===========================

    private final String businessName = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div/div[1]/input";
    private final String aboutBusiness = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div/div[2]/textarea";
    private final String professionDropdown = "select[name='profession']";
    private final String businessLogo = "input[type='file']";

    // ===========================
    // Address Details
    // ===========================

    private final String address = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div/div[2]/textarea";
    private final String pinCode = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div/div[1]/div[1]/input";
    private final String qualification = "//*[@id=\"root\"]/div[2]/div/div/form/div[4]/div/div/div[1]/input";
    private final String experience = "//*[@id=\"root\"]/div[2]/div/div/form/div[5]/div/div/div[1]/input";
    private final String projectDone = "//*[@id=\"root\"]/div[2]/div/div/form/div[5]/div/div/div[2]/input";
    private final String successStory = "//*[@id=\"root\"]/div[2]/div/div/form/div[5]/div/div/div[3]/input";

    // ===========================
    // Category
    // ===========================

    private final String category = "//input[@data-group='business_category' and @value=\"Turnkey / End-to-End Services\"]";
   private final String businessCategory = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div/div[2]/div/div[1]/div/div/input";
    private final String businessCategoryEnd = "//*[@id=\"root\"]/div[2]/div/div/form/div[3]/div[1]/div[2]/div/div[1]/div/div/input";
    

    // ===========================
    // Success Message
    // ===========================

    
    private final String successMessage = "text=Business hosted successfully";

    public HostYourBusinessPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(LoadState.NETWORKIDLE);
    }

    // =====================================================
    // Navigation
    // =====================================================

    public void clickHostBusiness() {
        waitForSelectorVisible(hostYourBusinessButton);
           if (isVisible(hostYourBusinessButton)) {
            click(hostYourBusinessButton);
        }
          ExtentReportUtil.attachScreenshot(page, "Host Your Business Button Clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);

    }

    public void clickNext() {
         Locator profile = page.locator(nextButton);

    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));

    profile.scrollIntoViewIfNeeded();   

    profile.click(new Locator.ClickOptions().setTimeout(10000));

          ExtentReportUtil.attachScreenshot(page, "Next Button Clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

   public void clickonNextButton() {

    page.waitForTimeout(30000);

    Locator nextButton = page.locator(nextButtonUpdate).first();

    nextButton.click();

    ExtentReportUtil.attachScreenshot(
        page,
        "Next Button Clicked"
    );

    waitForLoadState(
        com.microsoft.playwright.options.LoadState.NETWORKIDLE
    );
}
    public void clickNextCat() {
         Locator profile = page.locator(NextButtonCat);

    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));

    profile.scrollIntoViewIfNeeded();   

    profile.click(new Locator.ClickOptions().setTimeout(10000));

          ExtentReportUtil.attachScreenshot(page, "Next Button Clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void clickReview() {


        Locator profile = page.locator(reviewButton);

    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));

    profile.scrollIntoViewIfNeeded();   

    profile.click(new Locator.ClickOptions().setTimeout(10000));
          ExtentReportUtil.attachScreenshot(page, "Review Button Clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void clickCreateVendorProfile()  {
        waitForSelectorVisible(createVendorProfileButton);
        if (isVisible(createVendorProfileButton)) {
            click(createVendorProfileButton);
        }
          ExtentReportUtil.attachScreenshot(page, " Create Vendor Profile Clicked");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    
    public void clickProfileIcon() {

    Locator profile = page.locator(profileIconButton);

    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));

    profile.scrollIntoViewIfNeeded();

    profile.click(new Locator.ClickOptions().setTimeout(10000));

    ExtentReportUtil.attachScreenshot(page, "Profile Icon Clicked");

    waitForLoadState(LoadState.NETWORKIDLE);
}

public void clickProfession(String profession) {
    Locator professionButton = page.locator(
    "(//button/div/div/div[contains(text(),'" + profession + "')])[1]");
    professionButton.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));
    professionButton.click(new Locator.ClickOptions().setTimeout(10000));
    ExtentReportUtil.attachScreenshot(page, profession + " Profession Clicked");
    waitForLoadState(LoadState.NETWORKIDLE);
}

public void clickABCIcon() {
  Locator profile = page.locator(ABCIconButton);
    profile.waitFor(new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE));
    profile.click(new Locator.ClickOptions().setTimeout(10000));
    ExtentReportUtil.attachScreenshot(page, "ABC Icon Clicked");

}

    // =====================================================
    // Business Details
    // =====================================================

    public void fillBusinessDetails(Map<String, String> data) {

        type(businessName, data.get("BusinessName"));

        type(aboutBusiness, data.get("AboutBusiness"));

        // Uncomment after implementing dropdown selection
        // selectByVisibleText(professionDropdown, data.get("Profession"));

        // Uncomment if logo upload is required
        // page.locator(businessLogo).setInputFiles(data.get("BusinessLogo"));

        ExtentReportUtil.attachScreenshot(page,
                "Business Details Filled");
    }

    // =====================================================
    // Address Details
    // =====================================================

    public void fillAddressDetails(Map<String, String> data) {

        type(pinCode, data.get("Pincode"));

        type(address, data.get("Address"));

        type(qualification, data.get("Qualification"));

        type(experience, data.get("Experience"));
        
        type(projectDone, data.get("ProjectDone"));
        
        type(successStory, data.get("SuccessStory"));

        ExtentReportUtil.attachScreenshot(page,
                "Address Details Filled");
    }

    // =====================================================
    // Categories
    // =====================================================

    public void selectCategoriesAndTerms() {

       if (isVisible(category)) {
            click(category);
        }
        ExtentReportUtil.attachScreenshot(page, "Categories Selected");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        page.waitForTimeout(45000);
    }

   public void selectCatRunTime(String catName) {

    Locator checkbox = page.locator(
        "//input[@type='checkbox' and @data-group='business_category' and @value=\"" 
        + catName + "\"]"
    ).first();

    checkbox.waitFor(
        new Locator.WaitForOptions()
            .setState(WaitForSelectorState.VISIBLE)
            .setTimeout(30000)
    );

    if (!checkbox.isChecked()) {
        checkbox.check();
    }

    System.out.println("Category selected: " + catName);
}

     public void selectbusinessCategories() {

       if (isVisible(businessCategory)) {
            click(businessCategory);
            isChecked(businessCategory);
        }
        ExtentReportUtil.attachScreenshot(page, "Business Categories Selected");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

     public void selectbusinessCategoriesend() {

       if (isVisible(businessCategoryEnd)) {
            click(businessCategoryEnd);
            isChecked(businessCategoryEnd);
        }
        ExtentReportUtil.attachScreenshot(page, "End Business Categories Selected");
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }
    // =====================================================
    // Validation
    // =====================================================

    public void verifyBusinessCreated() {

        page.waitForLoadState(LoadState.NETWORKIDLE);

       isVisible(hostedBusinessprofileiCon);

        ExtentReportUtil.attachScreenshot(page,
                "Business Created Successfully");
    }

}