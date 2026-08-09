import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility to generate registration test data Excel file
 * This creates: src/test/resources/testdata/registration_testdata.xlsx
 * 
 * Run as: mvn test -Dtest=ExcelTestDataGenerator
 */
public class ExcelTestDataGenerator {

    @Test
    public void generateTestData() {
        String filePath = "src/test/resources/testdata/registration_testdata.xlsx";
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create RegistrationData sheet
            Sheet registrationSheet = workbook.createSheet("RegistrationData");
            
            // Create header row
            Row headerRow = registrationSheet.createRow(0);
            String[] headers = {
                "FirstName",
                "LastName",
                "PhoneNumber",
                "Email"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }
            
            // Add test data rows
            // Row 1 - New user for registration
            addRegistrationRow(registrationSheet, 1, "John", "Doe", "9876543210", "john.doe@example.com");
            
            // Row 2 - New user for registration
            addRegistrationRow(registrationSheet, 2, "Jane", "Smith", "9876543211", "jane.smith@example.com");
            
            // Row 3 - Existing user for login flow (use one of the above after registration)
            // This will be used for login scenario after user registers
            addRegistrationRow(registrationSheet, 3, "Alice", "Johnson", "9876543212", "alice.johnson@example.com");
            
            // Create HostYourBusiness sheet
            Sheet businessSheet = workbook.createSheet("HostYourBusiness");
            
            Row businessHeaderRow = businessSheet.createRow(0);
            String[] businessHeaders = {
                "UserID",
                "PhoneNumber",
                "Profession",
                "BusinessName",
                "AboutBusiness",
                "Address",
                "Pincode",
                "City",
                "State",
                "Qualification",
                "Experience",
                "SuccessStory",
                "Category",
                "SubCategory"
            };
            
            for (int i = 0; i < businessHeaders.length; i++) {
                Cell cell = businessHeaderRow.createCell(i);
                cell.setCellValue(businessHeaders[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }
            
            // Add business data rows
            // User 1 - John Doe's business
            addBusinessRow(businessSheet, 1, "User1", "9876543210", "Interior", 
                "John's Interior Design", "Professional interior design services",
                "123 Main St", "560001", "Bangalore", "Karnataka",
                "B.Des, 5 years experience", "5 years", 
                "Successfully completed 50+ projects",
                "Interior Design", "Residential");
            
            // User 2 - Jane Smith's business
            addBusinessRow(businessSheet, 2, "User2", "9876543211", "Plumbing",
                "Jane's Plumbing Services", "Expert plumbing solutions",
                "456 Oak Ave", "560002", "Bangalore", "Karnataka",
                "Certified Plumber, 8 years", "8 years",
                "Served 200+ households",
                "Plumbing", "Residential");
            
            // User 3 - Alice Johnson's business
            addBusinessRow(businessSheet, 3, "User3", "9876543212", "Electrical",
                "Alice's Electrical Works", "Professional electrical services",
                "789 Pine Rd", "560003", "Bangalore", "Karnataka",
                "Licensed Electrician, 10 years", "10 years",
                "Handled commercial and residential projects",
                "Electrical", "Commercial");
            
            // Adjust column widths
            for (int i = 0; i < headers.length; i++) {
                registrationSheet.autoSizeColumn(i);
            }
            for (int i = 0; i < businessHeaders.length; i++) {
                businessSheet.autoSizeColumn(i);
            }
            
            // Write file
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                System.out.println("✅ Test data Excel file created successfully: " + filePath);
            }
            
        } catch (IOException e) {
            System.err.println("❌ Error creating Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void addRegistrationRow(Sheet sheet, int rowNum, String firstName, String lastName, 
                                          String phone, String email) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(firstName);
        row.createCell(1).setCellValue(lastName);
        row.createCell(2).setCellValue(phone);
        row.createCell(3).setCellValue(email);
    }
    
    private static void addBusinessRow(Sheet sheet, int rowNum, String userId, String phone, String profession,
                                       String businessName, String about, String address, String pincode,
                                       String city, String state, String qualification, String experience,
                                       String successStory, String category, String subCategory) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(userId);
        row.createCell(1).setCellValue(phone);
        row.createCell(2).setCellValue(profession);
        row.createCell(3).setCellValue(businessName);
        row.createCell(4).setCellValue(about);
        row.createCell(5).setCellValue(address);
        row.createCell(6).setCellValue(pincode);
        row.createCell(7).setCellValue(city);
        row.createCell(8).setCellValue(state);
        row.createCell(9).setCellValue(qualification);
        row.createCell(10).setCellValue(experience);
        row.createCell(11).setCellValue(successStory);
        row.createCell(12).setCellValue(category);
        row.createCell(13).setCellValue(subCategory);
    }
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
