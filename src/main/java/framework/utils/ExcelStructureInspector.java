package framework.utils;

import java.util.*;

/**
 * Quick inspection utility for Excel data structure
 * Usage: mvn compile exec:java -Dexec.mainClass="framework.utils.ExcelStructureInspector"
 */
public class ExcelStructureInspector {
    
    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║               EXCEL FILE STRUCTURE ANALYSIS                     ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        try {
            List<Map<String, String>> data = ExcelUtil.getTestData("registration_testdata.xlsx", "HostYourBusiness");
            
            if (data.isEmpty()) {
                System.out.println("No data found in HostYourBusiness sheet");
                return;
            }
            
            Map<String, String> firstRow = data.get(0);
            List<String> headers = new ArrayList<>(firstRow.keySet());
            
            System.out.println("File: src/test/resources/testdata/registration_testdata.xlsx");
            System.out.println("Sheet: HostYourBusiness\n");
            
            System.out.println("Column Headers (" + headers.size() + " columns):");
            for (int i = 0; i < headers.size(); i++) {
                System.out.println("  [" + (i + 1) + "] " + headers.get(i));
            }
            
            System.out.println("\nTotal Data Rows: " + data.size());
            
            System.out.println("\n--- DATA PREVIEW (First 3 rows) ---\n");
            for (int rowIdx = 0; rowIdx < Math.min(3, data.size()); rowIdx++) {
                Map<String, String> row = data.get(rowIdx);
                System.out.println("Row " + (rowIdx + 1) + ":");
                for (String header : headers) {
                    String value = row.get(header);
                    if (value != null && value.length() > 60) {
                        value = value.substring(0, 60) + "...";
                    }
                    System.out.println("  " + header + ": " + value);
                }
                System.out.println();
            }
            
            System.out.println("────────────────────────────────────────────────────────────────\n");
            System.out.println("ANALYSIS:\n");
            
            boolean hasUserID = headers.stream().anyMatch(h -> h.equalsIgnoreCase("UserID"));
            boolean hasMobile = headers.stream().anyMatch(h -> h.equalsIgnoreCase("Mobile"));
            boolean hasProfession = headers.stream().anyMatch(h -> h.equalsIgnoreCase("Profession"));
            
            System.out.println("✓ Has UserID: " + (hasUserID ? "YES" : "NO - NEEDS ADDING"));
            System.out.println("✓ Has Mobile: " + (hasMobile ? "YES" : "NO - NEEDS ADDING"));
            System.out.println("✓ Has Profession: " + (hasProfession ? "YES" : "NO - NEEDS ADDING"));
            
            System.out.println("\nCurrent columns: " + headers);
            System.out.println("\nFor multi-user data-driven support, we need:");
            System.out.println("  1. UserID column (to group businesses by user)");
            System.out.println("  2. Mobile column (to login each user)");
            System.out.println("  3. Profession column (for data-driven profession selection)");
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
}
