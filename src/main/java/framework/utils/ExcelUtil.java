package framework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Utility class to read test data from Excel (.xlsx) files.
 */
public final class ExcelUtil {

    private ExcelUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Reads all rows from the given sheet as a list of Maps.
     * The first row is treated as header (column keys).
     *
     * @param fullPath  Absolute or relative path to the .xlsx file
     * @param sheetName Name of the sheet to read
     * @return List of Maps where each Map represents a row (key=column header, value=cell value)
     */
    public static List<Map<String, String>> getTestDataFromFullPath(String fullPath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(fullPath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in file: " + fullPath);
            }

            int rowCount = sheet.getPhysicalNumberOfRows();
            if (rowCount < 2) {
                return data;
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return data;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValueAsString(cell).trim());
            }

            for (int i = 1; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                boolean rowEmpty = true;
                Map<String, String> rowMap = new LinkedHashMap<>();

                for (int j = 0; j < headers.size(); j++) {
                    String cellValue = "";
                    Cell cell = row.getCell(j);
                    if (cell != null) {
                        cellValue = getCellValueAsString(cell).trim();
                    }
                    rowMap.put(headers.get(j), cellValue);
                    if (!cellValue.isEmpty()) {
                        rowEmpty = false;
                    }
                }

                if (!rowEmpty) {
                    data.add(rowMap);
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Excel file not found: " + fullPath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + fullPath, e);
        }

        return data;
    }

    /**
     * Reads test data from: src/test/resources/testdata/{fileName}
     */
    public static List<Map<String, String>> getTestData(String fileName, String sheetName) {
        String filePath = "src/test/resources/testdata/" + fileName;
        return getTestDataFromFullPath(filePath, sheetName);
    }

    /**
     * Gets a single row of test data by index (0-based).
     */
    public static Map<String, String> getTestDataRow(String fileName, String sheetName, int rowIndex) {
        List<Map<String, String>> allData = getTestData(fileName, sheetName);
        if (rowIndex < 0 || rowIndex >= allData.size()) {
            throw new RuntimeException("Row index " + rowIndex + " out of bounds. Total rows: " + allData.size());
        }
        return allData.get(rowIndex);
    }

    /**
     * Groups test data by a specified column (typically UserID for multi-user scenarios).
     * Delegates to TestDataGrouper for the actual grouping logic.
     *
     * @param fileName Excel file name (relative to src/test/resources/testdata/)
     * @param sheetName Name of the sheet to read
     * @return Map where key=UserID, value=List of business rows for that user
     */
    public static java.util.Map<String, java.util.List<java.util.Map<String, String>>> groupByUser(
        String fileName, String sheetName) {
        return TestDataGrouper.groupByUser(fileName, sheetName);
    }

    /**
     * Gets all unique UserIDs from the test data in order of appearance.
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return List of unique UserIDs in order of first appearance
     */
    public static java.util.List<String> getAllUserIds(String fileName, String sheetName) {
        return TestDataGrouper.getAllUserIds(fileName, sheetName);
    }

    /**
     * Gets all business data for a specific UserID.
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID to filter
     * @return List of business rows for this user
     */
    public static java.util.List<java.util.Map<String, String>> getUserData(
        String fileName, String sheetName, String userId) {
        return TestDataGrouper.getUserData(fileName, sheetName, userId);
    }

    /**
     * Gets a specific business for a user by business index (0-based).
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID
     * @param businessIndex 0-based index within user's businesses
     * @return Business data for that user at that index
     */
    public static java.util.Map<String, String> getBusinessForUser(
        String fileName, String sheetName, String userId, int businessIndex) {
        return TestDataGrouper.getBusinessForUser(fileName, sheetName, userId, businessIndex);
    }

    /**
     * Gets the count of businesses for a specific UserID.
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID to check
     * @return Number of businesses (rows) for this user
     */
    public static int getBusinessCountForUser(String fileName, String sheetName, String userId) {
        return TestDataGrouper.getBusinessCountForUser(fileName, sheetName, userId);
    }

    /**
     * Gets total number of unique users in the test data.
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Number of unique UserIDs
     */
    public static int getTotalUserCount(String fileName, String sheetName) {
        return TestDataGrouper.getTotalUserCount(fileName, sheetName);
    }

    /**
     * Gets total number of business records (all users combined).
     * Delegates to TestDataGrouper.
     *
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Total business records
     */
    public static int getTotalBusinessCount(String fileName, String sheetName) {
        return TestDataGrouper.getTotalBusinessCount(fileName, sheetName);
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
}
