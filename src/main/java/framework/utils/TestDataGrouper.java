package framework.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility to group test data by UserID for multi-user scenarios.
 * 
 * Enables:
 * 1. Multiple businesses per user (session reuse)
 * 2. Multiple users with isolated sessions
 * 3. Data-driven execution without hard-coded row numbers
 * 
 * Example:
 *   Map<String, List<Map<String, String>>> grouped = 
 *     TestDataGrouper.groupByUser("registration_testdata.xlsx", "HostYourBusiness");
 *   
 *   // USER001 with 3 businesses
 *   List<Map<String, String>> user001Data = grouped.get("USER001");
 */
public class TestDataGrouper {
    
    private TestDataGrouper() {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * Groups all test data by UserID column.
     * 
     * @param fileName Excel file name (relative to src/test/resources/testdata/)
     * @param sheetName Name of the sheet to read
     * @return Map where key=UserID, value=List of business rows for that user
     *         Maintains insertion order (preserves UserID sequence from Excel)
     * @throws IllegalArgumentException if UserID column not found or Excel structure invalid
     */
    public static Map<String, List<Map<String, String>>> groupByUser(String fileName, String sheetName) {
        List<Map<String, String>> allData = ExcelUtil.getTestData(fileName, sheetName);
        
        if (allData.isEmpty()) {
            return new LinkedHashMap<>();
        }
        
        // Verify UserID column exists
        Map<String, String> firstRow = allData.get(0);
        if (!firstRow.containsKey("UserID")) {
            throw new IllegalArgumentException(
                "UserID column not found in sheet '" + sheetName + "'. " +
                "Available columns: " + firstRow.keySet()
            );
        }
        
        // Group by UserID, maintaining insertion order
        return allData.stream()
            .collect(Collectors.groupingBy(
                row -> row.get("UserID"),
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
    
    /**
     * Gets all UserIDs from the test data in order of appearance.
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return List of unique UserIDs in order of first appearance
     */
    public static List<String> getAllUserIds(String fileName, String sheetName) {
        List<Map<String, String>> allData = ExcelUtil.getTestData(fileName, sheetName);
        
        if (allData.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Verify UserID column exists
        if (!allData.get(0).containsKey("UserID")) {
            throw new IllegalArgumentException("UserID column not found in sheet: " + sheetName);
        }
        
        // Get unique UserIDs preserving order
        return allData.stream()
            .map(row -> row.get("UserID"))
            .distinct()
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all business data for a specific UserID.
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID to filter
     * @return List of business rows for this user, or empty list if user not found
     */
    public static List<Map<String, String>> getUserData(String fileName, String sheetName, String userId) {
        Map<String, List<Map<String, String>>> grouped = groupByUser(fileName, sheetName);
        return grouped.getOrDefault(userId, new ArrayList<>());
    }
    
    /**
     * Gets the count of businesses for a specific UserID.
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID to check
     * @return Number of businesses (rows) for this user
     */
    public static int getBusinessCountForUser(String fileName, String sheetName, String userId) {
        return getUserData(fileName, sheetName, userId).size();
    }
    
    /**
     * Gets a specific business for a user by business index (0-based within user's businesses).
     * 
     * Example: User USER001 has 3 businesses; getBusinessForUser(..., "USER001", 0) 
     * returns the 1st business, getBusinessForUser(..., "USER001", 1) returns the 2nd, etc.
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @param userId The UserID
     * @param businessIndex 0-based index within user's businesses
     * @return Business data for that user at that index
     * @throws IndexOutOfBoundsException if business index is out of range
     */
    public static Map<String, String> getBusinessForUser(
        String fileName, String sheetName, String userId, int businessIndex) {
        
        List<Map<String, String>> userData = getUserData(fileName, sheetName, userId);
        
        if (businessIndex < 0 || businessIndex >= userData.size()) {
            throw new IndexOutOfBoundsException(
                "Business index " + businessIndex + " out of range for user " + userId + 
                ". User has " + userData.size() + " business(es)."
            );
        }
        
        return userData.get(businessIndex);
    }
    
    /**
     * Gets total number of unique users in the test data.
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Number of unique UserIDs
     */
    public static int getTotalUserCount(String fileName, String sheetName) {
        return getAllUserIds(fileName, sheetName).size();
    }
    
    /**
     * Gets total number of business records (all users combined).
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Total business records
     */
    public static int getTotalBusinessCount(String fileName, String sheetName) {
        return ExcelUtil.getTestData(fileName, sheetName).size();
    }
    
    /**
     * Prints a summary of the grouped data structure (for debugging/logging).
     * 
     * @param fileName Excel file name
     * @param sheetName Sheet name
     */
    public static void printGroupingSummary(String fileName, String sheetName) {
        Map<String, List<Map<String, String>>> grouped = groupByUser(fileName, sheetName);
        
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                  TEST DATA GROUPING SUMMARY                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("File: " + fileName);
        System.out.println("Sheet: " + sheetName);
        System.out.println("\nTotal Users: " + grouped.size());
        System.out.println("Total Businesses: " + getTotalBusinessCount(fileName, sheetName));
        System.out.println("\nGrouping Breakdown:\n");
        
        grouped.forEach((userId, businesses) -> {
            System.out.println("  User: " + userId);
            System.out.println("    Businesses: " + businesses.size());
            for (int i = 0; i < businesses.size(); i++) {
                Map<String, String> business = businesses.get(i);
                String profession = business.get("Profession");
                String businessName = business.get("BusinessName");
                System.out.println("      [" + (i + 1) + "] " + profession + " - " + businessName);
            }
            System.out.println();
        });
        
        System.out.println("────────────────────────────────────────────────────────────────\n");
    }
}
