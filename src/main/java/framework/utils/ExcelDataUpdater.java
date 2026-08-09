package framework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to update Excel test data with UserID and Mobile columns.
 * 
 * This utility:
 * 1. Reads existing test data
 * 2. Adds UserID and Mobile columns at the beginning
 * 3. Populates with sample data or specified values
 * 4. Preserves all existing data
 * 5. Saves back to Excel file
 * 
 * Usage:
 *   ExcelDataUpdater.addUserIdAndMobileColumns(
 *     "registration_testdata.xlsx", 
 *     "HostYourBusiness",
 *     new String[]{"USER001", "USER001", "USER002", ...},
 *     new String[]{"9876543210", "9876543211", "9876543212", ...}
 *   );
 */
public class ExcelDataUpdater {

    /**
     * Adds UserID and Mobile columns to Excel file at the beginning.
     * Creates new columns and shifts existing columns to the right.
     *
     * @param fileName Excel file name (relative to src/test/resources/testdata/)
     * @param sheetName Sheet name to update
     * @param userIds Array of UserID values (length must match number of data rows)
     * @param mobiles Array of Mobile values (length must match number of data rows)
     * @throws Exception if file operations fail
     */
    public static void addUserIdAndMobileColumns(
        String fileName, 
        String sheetName, 
        String[] userIds, 
        String[] mobiles) throws Exception {

        System.out.println("\n[ExcelDataUpdater] Starting Excel update...");
        System.out.println("[ExcelDataUpdater] File: " + fileName);
        System.out.println("[ExcelDataUpdater] Sheet: " + sheetName);

        String filePath = "src/test/resources/testdata/" + fileName;

        // Read existing workbook
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        if (sheet == null) {
            throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in workbook");
        }

        // Count actual non-empty data rows (ExcelUtil behavior)
        List<Map<String, String>> allData = ExcelUtil.getTestData(fileName, sheetName);
        int dataRowCount = allData.size();
        System.out.println("[ExcelDataUpdater] Actual data rows (non-empty): " + dataRowCount);

        if (userIds.length != dataRowCount || mobiles.length != dataRowCount) {
            throw new IllegalArgumentException(
                "UserID and Mobile array lengths must match data rows (" + dataRowCount + ")"
            );
        }

        // Get header row
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row not found in sheet: " + sheetName);
        }

        // Shift all columns 2 positions to the right to make room for UserID and Mobile
        int lastColumn = headerRow.getLastCellNum();
        System.out.println("[ExcelDataUpdater] Current columns: " + lastColumn);

        // Shift data columns
        for (int rowIdx = 0; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
            Row row = sheet.getRow(rowIdx);
            if (row != null) {
                // Shift from right to left to avoid overwriting
                for (int colIdx = lastColumn - 1; colIdx >= 0; colIdx--) {
                    Cell oldCell = row.getCell(colIdx);
                    if (oldCell != null) {
                        Cell newCell = row.createCell(colIdx + 2);
                        copyCellValue(oldCell, newCell);
                    }
                }
            }
        }

        // Add UserID and Mobile headers
        Cell userIdHeader = headerRow.createCell(0);
        userIdHeader.setCellValue("UserID");

        Cell mobileHeader = headerRow.createCell(1);
        mobileHeader.setCellValue("Mobile");

        // Apply header formatting (bold)
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        userIdHeader.setCellStyle(headerStyle);
        mobileHeader.setCellStyle(headerStyle);

        // Fill UserID and Mobile data for rows that contain data
        int dataRowIndex = 0;
        for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum() && dataRowIndex < userIds.length; rowIdx++) {
            Row dataRow = sheet.getRow(rowIdx);
            
            // Skip completely empty rows
            if (dataRow == null || dataRow.getLastCellNum() <= 0) {
                continue;
            }
            
            // Check if row has actual content in existing columns
            boolean hasContent = false;
            for (int colIdx = 2; colIdx < dataRow.getLastCellNum(); colIdx++) {
                Cell cell = dataRow.getCell(colIdx);
                if (cell != null && !getCellValueAsString(cell).isEmpty()) {
                    hasContent = true;
                    break;
                }
            }
            
            if (!hasContent) {
                continue; // Skip empty rows
            }
            
            // Add UserID and Mobile to this data row
            dataRow.createCell(0).setCellValue(userIds[dataRowIndex]);
            dataRow.createCell(1).setCellValue(mobiles[dataRowIndex]);
            dataRowIndex++;
        }

        // Auto-size new columns
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        // Write back to file
        fis.close();
        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
        workbook.close();

        System.out.println("[ExcelDataUpdater] ✓ Excel updated successfully");
        System.out.println("[ExcelDataUpdater] Added columns: UserID, Mobile");
        System.out.println("[ExcelDataUpdater] Total rows updated: " + userIds.length);
    }

    /**
     * Copies cell value and basic formatting from source to destination cell.
     */
    private static void copyCellValue(Cell source, Cell destination) {
        if (source == null) {
            return;
        }

        switch (source.getCellType()) {
            case STRING:
                destination.setCellValue(source.getStringCellValue());
                break;
            case NUMERIC:
                destination.setCellValue(source.getNumericCellValue());
                break;
            case BOOLEAN:
                destination.setCellValue(source.getBooleanCellValue());
                break;
            case FORMULA:
                destination.setCellFormula(source.getCellFormula());
                break;
            default:
                break;
        }

        // Copy formatting if available
        if (source.getCellStyle() != null) {
            destination.setCellStyle(source.getCellStyle());
        }
    }

    /**
     * Helper to safely convert a Cell value to String.
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                double numericValue = cell.getNumericCellValue();
                if (numericValue == Math.floor(numericValue) && !Double.isInfinite(numericValue)) {
                    yield String.valueOf((long) numericValue);
                }
                yield String.valueOf(numericValue);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield String.valueOf(cell.getNumericCellValue());
                } catch (Exception e) {
                    try {
                        yield cell.getStringCellValue();
                    } catch (Exception e2) {
                        yield cell.getCellFormula();
                    }
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }

    /**
     * Generates sample test data with UserID and Mobile columns.
     * Returns map with UserID -> Mobile mapping for test data population.
     *
     * Example output:
     *   USER001 -> 9876543210
     *   USER001 -> 9876543211 (2nd business for USER001)
     *   USER002 -> 9234567890
     *   USER002 -> 9234567891 (2nd business for USER002)
     */
    public static Map<String, String> generateSampleData(int userCount, int businessesPerUser) {
        Map<String, String> data = new LinkedHashMap<>();

        for (int u = 1; u <= userCount; u++) {
            String userId = String.format("USER%03d", u);
            String baseMobile = "98" + String.format("%08d", u * 10000000);

            for (int b = 0; b < businessesPerUser; b++) {
                String mobile = String.valueOf(Long.parseLong(baseMobile) + b);
                data.put(userId + "_B" + (b + 1), mobile);
            }
        }

        return data;
    }

    /**
     * Main method for standalone execution (for debugging).
     * 
     * Usage: mvn compile exec:java -Dexec.mainClass="framework.utils.ExcelDataUpdater"
     */
    public static void main(String[] args) {
        try {
            System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║                 EXCEL DATA UPDATER UTILITY                        ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

            // Sample data update
            // Current Excel has 2 rows: Architect (Row 1) and Interior (Row 2)
            // We'll assign:
            //   Row 1 (Architect): USER001, Mobile: 9876543210
            //   Row 2 (Interior): USER001, Mobile: 9876543211 (same user, different business)

            String[] userIds = {"USER001", "USER001"};
            String[] mobiles = {"9876543210", "9876543211"};

            System.out.println("Sample data to add:");
            for (int i = 0; i < userIds.length; i++) {
                System.out.println("  Row " + (i + 1) + ": " + userIds[i] + " | " + mobiles[i]);
            }
            System.out.println();

            addUserIdAndMobileColumns(
                "registration_testdata.xlsx",
                "HostYourBusiness",
                userIds,
                mobiles
            );

            System.out.println("\n[ExcelDataUpdater] Done!");

        } catch (Exception e) {
            System.err.println("[ExcelDataUpdater] Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
