package framework.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Utility class to generate test data Excel files for Skill Service Registration tests
 */
public class TestDataGenerator {

    /**
     * Generate skill service test data Excel file
     */
    public static void generateSkillServiceTestData() {
        String filePath = Paths.get("src/test/resources/testdata/skillservice_testdata.xlsx")
                .toAbsolutePath().toString();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SkillServiceData");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "FullName", "Email", "Phone", "SkillCategory", "SkillType",
                    "Description", "Experience", "Address", "City", "State", "ZipCode"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            // Sample test data
            Object[][] testData = {
                    {
                            "John Doe",
                            "john.doe@example.com",
                            "9876543210",
                            "Plumbing",
                            "Plumber",
                            "Experienced plumber with 10+ years in residential and commercial plumbing",
                            "10",
                            "123 Main Street",
                            "New York",
                            "NY",
                            "10001"
                    },
                    {
                            "Alice Johnson",
                            "alice.johnson@example.com",
                            "9876543211",
                            "Electrical",
                            "Electrician",
                            "Licensed electrician with expertise in commercial electrical systems",
                            "12",
                            "456 Oak Avenue",
                            "Los Angeles",
                            "CA",
                            "90001"
                    },
                    {
                            "Bob Smith",
                            "bob.smith@example.com",
                            "9876543212",
                            "Carpentry",
                            "Carpenter",
                            "Skilled carpenter specializing in custom furniture and woodwork",
                            "8",
                            "789 Pine Road",
                            "Austin",
                            "TX",
                            "78701"
                    },
                    {
                            "Carol White",
                            "carol.white@example.com",
                            "9876543213",
                            "Painting",
                            "Painter",
                            "Professional painter with 6+ years in interior and exterior painting",
                            "6",
                            "321 Elm Street",
                            "Denver",
                            "CO",
                            "80202"
                    },
                    {
                            "David Brown",
                            "david.brown@example.com",
                            "9876543214",
                            "HVAC",
                            "HVAC Technician",
                            "Certified HVAC technician experienced in maintenance and installation",
                            "7",
                            "654 Maple Drive",
                            "Miami",
                            "FL",
                            "33101"
                    },
                    {
                            "Emma Davis",
                            "emma.davis@example.com",
                            "9876543215",
                            "Plumbing",
                            "Plumber",
                            "Expert plumber with 15+ years specializing in water systems",
                            "15",
                            "987 Birch Lane",
                            "Chicago",
                            "IL",
                            "60601"
                    }
            };

            // Write data to Excel
            for (int i = 0; i < testData.length; i++) {
                Row row = sheet.createRow(i + 1);
                Object[] rowData = testData[i];

                for (int j = 0; j < rowData.length; j++) {
                    row.createCell(j).setCellValue(rowData[j].toString());
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
                System.out.println("✅ Skill service test data generated successfully at: " + filePath);
            }

        } catch (IOException e) {
            System.err.println("❌ Error generating test data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main method to run test data generation
     */
    public static void main(String[] args) {
        System.out.println("Generating Skill Service Test Data...");
        generateSkillServiceTestData();
        System.out.println("Done!");
    }
}
