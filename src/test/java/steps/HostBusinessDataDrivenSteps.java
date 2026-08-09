package steps;

import framework.utils.ExcelUtil;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;
import java.util.Map;

/**
 * Step definitions for data-driven Excel validation.
 * Validates Excel file structure before running data-driven tests.
 */
public class HostBusinessDataDrivenSteps {

    private static final String EXCEL_FILE = "registration_testdata.xlsx";
    private static final String EXCEL_SHEET = "HostYourBusiness";

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
     * Validates that Excel has required columns.
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
        System.out.println("[DataDrivenSteps] ✓ Excel has " + data.size() + " data rows");
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
        System.out.println("[DataDrivenSteps] ✓ All required fields populated");
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
        System.out.println("[DataDrivenSteps] ✓ No UserID or Mobile empty");
    }
}
