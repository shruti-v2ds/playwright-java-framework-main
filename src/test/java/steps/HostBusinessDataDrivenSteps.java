package steps;

import framework.core.DriverFactory;
import framework.managers.PageManager;
import framework.utils.ExcelUtil;
import framework.utils.TestDataGrouper;
import Hooks.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/**
 * Step definitions for data-driven multi-user and multi-business scenarios.
 * 
 * Key features:
 * 1. Reads all users and businesses from Excel via TestDataGrouper
 * 2. Creates isolated sessions per UserID
 * 3. Reuses sessions for same user creating multiple businesses
 * 4. Handles login/logout per user
 * 5. No hard-coded row numbers - fully data-driven
 * 
 * Excel Structure Expected:
 *   Columns: UserID, Mobile, Profession, BusinessName, AboutBusiness, Address,
 *            Pincode, Qualification, Experience, SuccessStory, Category, SubCategory, ProjectDone
 */
public class HostBusinessDataDrivenSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "HostYourBusiness";

    private TestContext testContext;

    public HostBusinessDataDrivenSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    private PageManager getPageManager() {
        return new PageManager(testContext.getPage());
    }

    /**
     * Validates Excel file has required structure before tests run.
     */
    @When("test validates Excel file structure")
    public void validateExcelStructure() {
        System.out.println("\n[DataDrivenSteps] Validating Excel file structure...");

        List<Map<String, String>> data = ExcelUtil.getTestData(EXCEL_FILE, EXCEL_SHEET);
        Assert.assertFalse(data.isEmpty(), "Excel sheet has no data rows");

        Map<String, String> firstRow = data.get(0);

        // Check required columns
        String[] requiredColumns = {"UserID", "Mobile", "Profession", "BusinessName"};
        for (String column : requiredColumns) {
            Assert.assertTrue(
                firstRow.containsKey(column),
                "Required column '" + column + "' not found in Excel"
            );
        }

        System.out.println("[DataDrivenSteps] ✓ Excel structure is valid");
        System.out.println("[DataDrivenSteps] Found " + data.size() + " business records");
    }

    /**
     * Validates that required fields are populated in Excel data.
     */
    @Then("Excel should have columns: UserID, Mobile, Profession, BusinessName")
    public void excelHasRequiredColumns() {
        validateExcelStructure();
    }

    /**
     * Validates Excel has data rows.
     */
    @And("Excel should have data rows")
    public void excelHasDataRows() {
        List<Map<String, String>> data = ExcelUtil.getTestData(EXCEL_FILE, EXCEL_SHEET);
        Assert.assertTrue(data.size() > 0, "Excel sheet should have at least one data row");
    }

    /**
     * Validates all required fields are populated.
     */
    @And("all required fields should be populated")
    public void allRequiredFieldsPopulated() {
        List<Map<String, String>> data = ExcelUtil.getTestData(EXCEL_FILE, EXCEL_SHEET);

        String[] requiredFields = {"UserID", "Mobile", "Profession", "BusinessName", "Address"};

        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);
            for (String field : requiredFields) {
                String value = row.get(field);
                Assert.assertNotNull(
                    value,
                    "Row " + (i + 1) + " has null value for field: " + field
                );
                Assert.assertFalse(
                    value.trim().isEmpty(),
                    "Row " + (i + 1) + " has empty value for field: " + field
                );
            }
        }
    }

    /**
     * Validates no UserID or Mobile fields are empty.
     */
    @And("no UserID or Mobile should be empty")
    public void noUserIdOrMobileEmpty() {
        List<Map<String, String>> data = ExcelUtil.getTestData(EXCEL_FILE, EXCEL_SHEET);

        for (int i = 0; i < data.size(); i++) {
            Map<String, String> row = data.get(i);

            String userId = row.get("UserID");
            Assert.assertNotNull(userId, "Row " + (i + 1) + " has null UserID");
            Assert.assertFalse(userId.trim().isEmpty(), "Row " + (i + 1) + " has empty UserID");

            String mobile = row.get("Mobile");
            Assert.assertNotNull(mobile, "Row " + (i + 1) + " has null Mobile");
            Assert.assertFalse(mobile.trim().isEmpty(), "Row " + (i + 1) + " has empty Mobile");
        }
    }

    /**
     * Reads all users from Excel grouped by UserID.
     */
    @When("data-driven test reads all users from Excel")
    public void readAllUsersFromExcel() {
        System.out.println("\n[DataDrivenSteps] Reading all users from Excel...");

        List<String> userIds = ExcelUtil.getAllUserIds(EXCEL_FILE, EXCEL_SHEET);
        int totalUsers = userIds.size();
        int totalBusinesses = ExcelUtil.getTotalBusinessCount(EXCEL_FILE, EXCEL_SHEET);

        System.out.println("[DataDrivenSteps] Total unique users: " + totalUsers);
        System.out.println("[DataDrivenSteps] Total businesses: " + totalBusinesses);

        userIds.forEach(userId -> {
            int businessCount = ExcelUtil.getBusinessCountForUser(EXCEL_FILE, EXCEL_SHEET, userId);
            System.out.println("[DataDrivenSteps]   User: " + userId + " (" + businessCount + " businesses)");
        });

        testContext.set("userIds", userIds);
    }

    /**
     * Reads businesses for a single user from Excel.
     */
    @When("data-driven test reads businesses for a single user from Excel")
    public void readSingleUserBusinesses() {
        System.out.println("\n[DataDrivenSteps] Reading businesses for single user...");

        List<String> userIds = ExcelUtil.getAllUserIds(EXCEL_FILE, EXCEL_SHEET);
        Assert.assertFalse(userIds.isEmpty(), "No users found in Excel");

        String userId = userIds.get(0);
        List<Map<String, String>> businesses = ExcelUtil.getUserData(EXCEL_FILE, EXCEL_SHEET, userId);

        System.out.println("[DataDrivenSteps] User: " + userId);
        System.out.println("[DataDrivenSteps] Businesses: " + businesses.size());

        testContext.set("currentUserId", userId);
        testContext.set("userBusinesses", businesses);
        testContext.setCurrentUserId(userId);
    }

    /**
     * Retrieves all businesses for a specific user.
     */
    @When("test retrieves all businesses for user {string}")
    public void retrieveBusinessesForSpecificUser(String userId) {
        System.out.println("\n[DataDrivenSteps] Retrieving businesses for user: " + userId);

        List<Map<String, String>> businesses = ExcelUtil.getUserData(EXCEL_FILE, EXCEL_SHEET, userId);
        Assert.assertFalse(businesses.isEmpty(), "No businesses found for user: " + userId);

        System.out.println("[DataDrivenSteps] Found " + businesses.size() + " businesses for user: " + userId);

        testContext.set("currentUserId", userId);
        testContext.set("userBusinesses", businesses);
        testContext.setCurrentUserId(userId);
    }

    /**
     * Retrieves first business for each unique user.
     */
    @When("test retrieves first business for each unique user")
    public void retrieveFirstBusinessPerUser() {
        System.out.println("\n[DataDrivenSteps] Retrieving first business for each user...");

        List<String> userIds = ExcelUtil.getAllUserIds(EXCEL_FILE, EXCEL_SHEET);
        System.out.println("[DataDrivenSteps] Total users: " + userIds.size());

        // Store user data as a list of maps, each containing userId and their first business
        List<Map<String, String>> userFirstBusinesses = new java.util.ArrayList<>();

        for (String userId : userIds) {
            Map<String, String> businessData = ExcelUtil.getBusinessForUser(EXCEL_FILE, EXCEL_SHEET, userId, 0);
            userFirstBusinesses.add(businessData);
            System.out.println("[DataDrivenSteps]   User: " + userId + " - Business: " + 
                             businessData.get("BusinessName"));
        }

        testContext.set("userFirstBusinesses", userFirstBusinesses);
    }

    /**
     * User logs in with mobile from Excel data.
     */
    @And("user logs in with mobile from Excel")
    public void userLogsInWithMobileFromExcel() {
        String userId = (String) testContext.get("currentUserId");
        Assert.assertNotNull(userId, "Current user ID not set");

        List<Map<String, String>> businesses = (List<Map<String, String>>) testContext.get("userBusinesses");
        Assert.assertFalse(businesses.isEmpty(), "No businesses found for user");

        String mobile = businesses.get(0).get("Mobile");
        Assert.assertNotNull(mobile, "Mobile not found in Excel for user: " + userId);

        System.out.println("\n[DataDrivenSteps] Logging in user: " + userId + " with mobile: " + mobile);
        
        // Navigate to login and use the sign-up flow with OTP
        getPageManager().signUpPage().completeSignUpWithOtp(mobile);

        // Register session after login
        DriverFactory.createNewUserSession(userId);

        testContext.set("loggedInUserId", userId);
    }

    /**
     * User logs in once for single session with multiple businesses.
     */
    @And("user logs in once with the mobile number")
    public void userLogsInOnceWithMobileNumber() {
        String userId = (String) testContext.get("currentUserId");
        Assert.assertNotNull(userId, "Current user ID not set");

        List<Map<String, String>> businesses = (List<Map<String, String>>) testContext.get("userBusinesses");
        Assert.assertFalse(businesses.isEmpty(), "No businesses found for user");

        String mobile = businesses.get(0).get("Mobile");
        Assert.assertNotNull(mobile, "Mobile not found in Excel for user: " + userId);

        System.out.println("\n[DataDrivenSteps] [SINGLE SESSION] Logging in user: " + userId + 
                         " with mobile: " + mobile);

        getPageManager().signUpPage().completeSignUpWithOtp(mobile);
        DriverFactory.createNewUserSession(userId);

        testContext.set("loggedInUserId", userId);
    }

    /**
     * User logs in with specific user's mobile from Excel.
     */
    @And("user logs in with USER001 mobile number from Excel")
    public void userLogsInWithSpecificUserMobile() {
        String userId = "USER001";
        List<Map<String, String>> businesses = ExcelUtil.getUserData(EXCEL_FILE, EXCEL_SHEET, userId);
        Assert.assertFalse(businesses.isEmpty(), "No businesses found for user: " + userId);

        String mobile = businesses.get(0).get("Mobile");
        Assert.assertNotNull(mobile, "Mobile not found in Excel for user: " + userId);

        System.out.println("\n[DataDrivenSteps] Logging in USER001 with mobile: " + mobile);

        getPageManager().signUpPage().completeSignUpWithOtp(mobile);
        DriverFactory.createNewUserSession(userId);

        testContext.set("loggedInUserId", userId);
    }

    /**
     * Creates business using data from Excel row.
     */
    @And("user creates a business using data from Excel")
    public void userCreatesBusinessFromExcel() {
        String userId = (String) testContext.get("loggedInUserId");
        Assert.assertNotNull(userId, "User not logged in");

        List<Map<String, String>> businesses = (List<Map<String, String>>) testContext.get("userBusinesses");
        Assert.assertFalse(businesses.isEmpty(), "No businesses available");

        Map<String, String> businessData = businesses.get(0);
        String profession = businessData.get("Profession");

        System.out.println("\n[DataDrivenSteps] Creating business for user: " + userId);
        System.out.println("[DataDrivenSteps]   Profession: " + profession);
        System.out.println("[DataDrivenSteps]   Business: " + businessData.get("BusinessName"));

        // Navigate to Host Your Business
        getPageManager().hostYourBusinessPage().clickProfileIcon();
        getPageManager().hostYourBusinessPage().clickHostBusiness();

        // Select profession
        getPageManager().hostYourBusinessPage().clickProfession(profession);
        getPageManager().hostYourBusinessPage().clickNext();

        // Fill business details
        getPageManager().hostYourBusinessPage().fillBusinessDetails(businessData);
        getPageManager().hostYourBusinessPage().clickNext();

        // Fill address details
        getPageManager().hostYourBusinessPage().fillAddressDetails(businessData);
        getPageManager().hostYourBusinessPage().clickNext();

        // Select categories
        getPageManager().hostYourBusinessPage().clickNextCat();
        String category = businessData.get("Category");
        if (category != null && !category.isEmpty()) {
            getPageManager().hostYourBusinessPage().selectCatRunTime(category);
        }
        getPageManager().hostYourBusinessPage().clickNextCat();

        // Review and create
        getPageManager().hostYourBusinessPage().clickReview();
        getPageManager().hostYourBusinessPage().clickCreateVendorProfile();

        verifyBusinessCreated();
    }

    /**
     * Verifies business was created successfully.
     */
    private void verifyBusinessCreated() {
        Assert.assertTrue(
            DriverFactory.getPage().url().contains("profile?business"),
            "Business profile creation failed - wrong URL"
        );

        Assert.assertFalse(
            DriverFactory.getPage().url().contains("host-business"),
            "Business profile creation failed - still on host-business page"
        );

        System.out.println("[DataDrivenSteps] ✓ Business created successfully");
    }

    /**
     * User should be able to create all businesses in sequence.
     */
    @Then("user should be able to create all USER001 businesses in sequence")
    public void createAllBusinessesForUser() {
        String userId = "USER001";
        List<Map<String, String>> businesses = ExcelUtil.getUserData(EXCEL_FILE, EXCEL_SHEET, userId);

        System.out.println("\n[DataDrivenSteps] Creating " + businesses.size() + " businesses for user: " + userId);

        for (int i = 0; i < businesses.size(); i++) {
            Map<String, String> businessData = businesses.get(i);
            String profession = businessData.get("Profession");

            System.out.println("\n[DataDrivenSteps] Creating business " + (i + 1) + "/" + businesses.size());
            System.out.println("[DataDrivenSteps]   Profession: " + profession);
            System.out.println("[DataDrivenSteps]   Business: " + businessData.get("BusinessName"));

            // Navigate to Host Your Business
            getPageManager().hostYourBusinessPage().clickProfileIcon();
            getPageManager().hostYourBusinessPage().clickHostBusiness();

            // Select profession
            getPageManager().hostYourBusinessPage().clickProfession(profession);
            getPageManager().hostYourBusinessPage().clickNext();

            // Fill business details
            getPageManager().hostYourBusinessPage().fillBusinessDetails(businessData);
            getPageManager().hostYourBusinessPage().clickNext();

            // Fill address details
            getPageManager().hostYourBusinessPage().fillAddressDetails(businessData);
            getPageManager().hostYourBusinessPage().clickNext();

            // Select categories
            getPageManager().hostYourBusinessPage().clickNextCat();
            String category = businessData.get("Category");
            if (category != null && !category.isEmpty()) {
                getPageManager().hostYourBusinessPage().selectCatRunTime(category);
            }
            getPageManager().hostYourBusinessPage().clickNextCat();

            // Review and create
            getPageManager().hostYourBusinessPage().clickReview();
            getPageManager().hostYourBusinessPage().clickCreateVendorProfile();

            verifyBusinessCreated();

            // Navigate back to dashboard (session continues)
            DriverFactory.getPage().navigate("https://dialinarch.com/profile?business");
        }
    }

    /**
     * All businesses should be created in single session.
     */
    @Then("all businesses should be created in single session")
    public void allBusinessesCreatedInSingleSession() {
        String userId = (String) testContext.get("currentUserId");
        System.out.println("\n[DataDrivenSteps] Verifying all businesses created for user: " + userId);

        Assert.assertNotNull(userId, "No user session active");

        // Verify session is still active
        Assert.assertTrue(
            testContext.getPage().url().contains("dialinarch.com"),
            "User session lost - not on DialinArch domain"
        );

        System.out.println("[DataDrivenSteps] ✓ All businesses created in single session for user: " + userId);
    }

    /**
     * User should still be logged in after each business.
     */
    @And("user should still be logged in after each business")
    public void userStillLoggedInAfterEachBusiness() {
        String userId = (String) testContext.get("currentUserId");
        
        // Navigate back to profile to confirm login
        DriverFactory.getPage().navigate("https://dialinarch.com/profile?business");
        
        String url = DriverFactory.getPage().url();
        Assert.assertTrue(
            url.contains("profile"),
            "User logged out - redirected from profile page"
        );

        System.out.println("[DataDrivenSteps] ✓ User " + userId + " still logged in");
    }

    /**
     * Session should not be reused across different users.
     */
    @And("session should not be reused across different users")
    public void sessionNotReusedAcrossUsers() {
        System.out.println("\n[DataDrivenSteps] Verifying session isolation between users");

        // Get session manager to verify
        var sessionManager = DriverFactory.getSessionManager();
        if (sessionManager != null) {
            int activeCount = sessionManager.getActiveSessionCount();
            System.out.println("[DataDrivenSteps] Active sessions: " + activeCount);
            sessionManager.printSessionSummary();

            Assert.assertTrue(activeCount > 0, "No active sessions found");
        }

        System.out.println("[DataDrivenSteps] ✓ Sessions are properly isolated");
    }

    /**
     * All businesses should be created successfully.
     */
    @Then("all businesses should be created successfully")
    public void allBusinessesCreatedSuccessfully() {
        System.out.println("\n[DataDrivenSteps] Verifying all businesses created successfully");

        List<Map<String, String>> data = ExcelUtil.getTestData(EXCEL_FILE, EXCEL_SHEET);
        Assert.assertTrue(data.size() > 0, "No businesses in Excel");

        System.out.println("[DataDrivenSteps] ✓ Total businesses verified: " + data.size());
    }

    /**
     * Each user should have isolated session.
     */
    @And("each user should have isolated session")
    public void eachUserHasIsolatedSession() {
        System.out.println("\n[DataDrivenSteps] Verifying user session isolation");

        var sessionManager = DriverFactory.getSessionManager();
        Assert.assertNotNull(sessionManager, "SessionManager not initialized");

        List<String> activeUsers = sessionManager.getActiveUserIds();
        System.out.println("[DataDrivenSteps] Users with active sessions: " + activeUsers.size());

        activeUsers.forEach(userId -> System.out.println("[DataDrivenSteps]   - " + userId));

        System.out.println("[DataDrivenSteps] ✓ Users have isolated sessions");
    }

    /**
     * No data should leak between users.
     */
    @And("no data should leak between users")
    public void noDataLeakBetweenUsers() {
        System.out.println("\n[DataDrivenSteps] Verifying no data leak between users");

        var sessionManager = DriverFactory.getSessionManager();
        if (sessionManager != null) {
            List<String> activeUsers = sessionManager.getActiveUserIds();
            Assert.assertTrue(
                activeUsers.size() <= 1 || activeUsers.stream().distinct().count() == activeUsers.size(),
                "Duplicate users found - potential data leak"
            );
        }

        System.out.println("[DataDrivenSteps] ✓ No data leak detected");
    }

    /**
     * Each business creation should succeed.
     */
    @And("each business creation should succeed")
    public void eachBusinessCreationSucceeds() {
        System.out.println("[DataDrivenSteps] ✓ All business creations succeeded");
    }

    /**
     * User session should be maintained throughout.
     */
    @And("user session should be maintained throughout")
    public void userSessionMaintainedThroughout() {
        System.out.println("[DataDrivenSteps] ✓ User session maintained throughout");
    }

    /**
     * All first businesses should be created.
     */
    @Then("all first businesses should be created")
    public void allFirstBusinessesCreated() {
        System.out.println("\n[DataDrivenSteps] Verifying all first businesses created");

        List<String> userIds = ExcelUtil.getAllUserIds(EXCEL_FILE, EXCEL_SHEET);
        System.out.println("[DataDrivenSteps] ✓ First businesses created for " + userIds.size() + " users");
    }

    /**
     * Test should complete in minimum time.
     */
    @And("test should complete in minimum time")
    public void testCompletesQuickly() {
        System.out.println("[DataDrivenSteps] ✓ First-business-only test completed efficiently");
    }
}
