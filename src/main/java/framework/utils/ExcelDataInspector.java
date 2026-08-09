package framework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility to inspect and display Excel file structure
 */
public class ExcelDataInspector {

    public static void main(String[] args) throws IOException {
        String filePath = "src/test/resources/testdata/registration_testdata.xlsx";
        
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            System.out.println("╔════════════════════════════════════════════════════════════════╗");
            System.out.println("║               EXCEL FILE STRUCTURE ANALYSIS                     ║");
            System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
            
            System.out.println("File: " + filePath);
            System.out.println("Total Sheets: " + workbook.getNumberOfSheets() + "\n");
            
            // List all sheets
            System.out.println("Available Sheets:");
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                Sheet sheet = workbook.getSheetAt(i);
                int rowCount = sheet.getPhysicalNumberOfRows();
                int colCount = sheet.getRow(0) != null ? sheet.getRow(0).getPhysicalNumberOfCells() : 0;
                System.out.println("  [" + (i+1) + "] " + sheetName + " - " + (rowCount-1) + " data rows × " + colCount + " columns");
            }
            
            System.out.println("\n───────────────────────────────────────────────────────────────\n");
            
            // Detailed analysis of HostYourBusiness sheet
            Sheet sheet = workbook.getSheet("HostYourBusiness");
            if (sheet != null) {
                System.out.println("SHEET: HostYourBusiness");
                System.out.println("────────────────────────────────────────────────────────────────\n");
                
                // Get headers
                Row headerRow = sheet.getRow(0);
                List<String> headers = new ArrayList<>();
                System.out.println("Column Headers:");
                if (headerRow != null) {
                    for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                        Cell cell = headerRow.getCell(i);
                        String header = getCellValueAsString(cell);
                        headers.add(header);
                        System.out.println("  [" + (i+1) + "] " + header);
                    }
                }
                
                System.out.println("\nData Rows:");
                int dataRowCount = sheet.getPhysicalNumberOfRows() - 1;
                System.out.println("Total data rows: " + dataRowCount);
                
                System.out.println("\n--- PREVIEW (First 3 rows) ---\n");
                
                for (int rowIdx = 1; rowIdx <= Math.min(3, sheet.getPhysicalNumberOfRows() - 1); rowIdx++) {
                    Row row = sheet.getRow(rowIdx);
                    System.out.println("Row " + rowIdx + ":");
                    if (row != null) {
                        for (int colIdx = 0; colIdx < headers.size(); colIdx++) {
                            Cell cell = row.getCell(colIdx);
                            String value = getCellValueAsString(cell);
                            if (value.length() > 50) {
                                value = value.substring(0, 50) + "...";
                            }
                            System.out.println("  " + headers.get(colIdx) + ": " + value);
                        }
                    }
                    System.out.println();
                }
                
                System.out.println("───────────────────────────────────────────────────────────────\n");
                System.out.println("ANALYSIS & RECOMMENDATIONS:");
                System.out.println("────────────────────────────────────────────────────────────────\n");
                
                // Check for UserID and Mobile columns
                boolean hasUserID = headers.stream().anyMatch(h -> h.equalsIgnoreCase("UserID"));
                boolean hasMobile = headers.stream().anyMatch(h -> h.equalsIgnoreCase("Mobile"));
                
                System.out.println("Current Status:");
                System.out.println("  • Has UserID column: " + (hasUserID ? "✓ YES" : "✗ NO - NEEDS TO BE ADDED"));
                System.out.println("  • Has Mobile column: " + (hasMobile ? "✓ YES" : "✗ NO - NEEDS TO BE ADDED"));
                System.out.println("  • Total columns: " + headers.size());
                System.out.println("  • Total data rows: " + dataRowCount);
                
                System.out.println("\nRecommendations for Multi-User Support:");
                if (!hasUserID) {
                    System.out.println("  1. ADD column 'UserID' (e.g., USER001, USER002)");
                }
                if (!hasMobile) {
                    System.out.println("  1. ADD column 'Mobile' (e.g., 9876543210)");
                }
                System.out.println("  2. Group all rows by UserID");
                System.out.println("  3. Each UserID can have multiple businesses");
                System.out.println("  4. Same user session reused for multiple businesses");
                
                System.out.println("\nData Structure for Implementation:");
                System.out.println("  Current columns: " + headers);
                System.out.println("\nExpected for data-driven:");
                System.out.println("  Map<UserID, List<BusinessData>>");
                
            } else {
                System.out.println("⚠ Sheet 'HostYourBusiness' not found!");
            }
            
        } catch (Exception e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield String.valueOf((long) cell.getNumericCellValue());
                } catch (Exception e) {
                    yield cell.getCellFormula();
                }
            }
            case BLANK -> "";
            default -> "";
        };
    }
}
