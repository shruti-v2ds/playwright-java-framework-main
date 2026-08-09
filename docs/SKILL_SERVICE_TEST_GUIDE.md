# Skill Service Registration Test Guide

## Overview

This guide explains how to use the new Skill Service Registration test suite for the DialinArch platform. The test suite includes comprehensive BDD scenarios, Page Object Model implementation, and step definitions for testing skill service provider registration.

---

## 📁 Files Added

### 1. Page Object Model
- **File**: `src/main/java/framework/pages/SkillServicePage.java`
- **Purpose**: Encapsulates all UI interactions for skill service registration
- **Key Methods**:
  - Navigation: `navigateToSkillService()`, `navigateToSkillServiceFromHome()`
  - Form Filling: `fillBasicInfo()`, `selectSkillCategory()`, `fillCompleteForm()`
  - OTP Handling: `sendOtp()`, `enterOtpFromApi()`, `enterMockOtp()`
  - Submission: `submitRegistration()`, `registerSkillService()`
  - Verification: `isRegistrationSuccessful()`, `getSuccessMessage()`

### 2. Feature File (BDD Scenarios)
- **File**: `src/test/resources/features/skillservice.feature`
- **Contains**: 7 comprehensive test scenarios covering:
  - Basic registration
  - Different skill categories
  - Complete registration with OTP
  - Bulk registration from Excel
  - Parametrized scenarios
  - Error handling
  - OTP resend functionality

### 3. Step Definitions
- **File**: `src/test/java/steps/SkillServiceSteps.java`
- **Purpose**: Implements all step definitions for the feature file
- **Includes**:
  - Navigation steps
  - Form filling steps
  - OTP verification steps
  - Assertion/Verification steps
  - Error handling steps

### 4. PageManager Update
- **File**: `src/main/java/framework/managers/PageManager.java`
- **Change**: Added `skillServicePage()` getter method for lazy initialization

---

## 🧪 Running the Tests

### Run All Skill Service Tests
```bash
mvn clean test -Dtags="@skillservice"
```

### Run Specific Scenario
```bash
mvn clean test -Dtags="@skillservice and basic"
```

### Run with Different Browser
```bash
mvn clean test -Dtags="@skillservice" -Dbrowser=firefox
```

### Run in Headless Mode
```bash
mvn clean test -Dtags="@skillservice" -Dheadless=true
```

---

## 📊 Test Data Setup

### Excel File Creation

Create `src/test/resources/testdata/skillservice_testdata.xlsx` with the following sheet structure:

#### Sheet Name: `SkillServiceData`

| FullName      | Email                | Phone      | SkillCategory | SkillType   | Description                          | Experience | Address              | City    | State  | ZipCode |
|---------------|----------------------|------------|---------------|-------------|--------------------------------------|------------|----------------------|---------|--------|---------|
| John Doe      | john@example.com     | 9876543210 | Plumbing      | Plumber     | Experienced plumber with 10+ years   | 10         | 123 Main Street      | New York| NY     | 10001   |
| Alice Johnson | alice@example.com    | 9876543211 | Electrical    | Electrician | Licensed electrician, commercial exp | 12         | 456 Oak Avenue       | LA      | CA     | 90001   |
| Bob Smith     | bob@example.com      | 9876543212 | Carpentry     | Carpenter   | Skilled carpenter, custom furniture  | 8          | 789 Pine Road        | Austin  | TX     | 78701   |
| Carol White   | carol@example.com    | 9876543213 | Painting      | Painter     | Professional painter, interior/ext   | 6          | 321 Elm Street       | Denver  | CO     | 80202   |
| David Brown   | david@example.com    | 9876543214 | HVAC          | HVAC Tech   | HVAC maintenance and installation    | 7          | 654 Maple Drive      | Miami   | FL     | 33101   |

**Column Descriptions**:
- **FullName**: Full name of skill service provider (required)
- **Email**: Valid email address (required)
- **Phone**: Phone number for OTP verification (required)
- **SkillCategory**: Category like Plumbing, Electrical, Carpentry, Painting, HVAC (required)
- **SkillType**: Specific skill type matching the category (required)
- **Description**: Detailed description of skills and experience (required)
- **Experience**: Years of experience as a number (required)
- **Address**: Street address (required)
- **City**: City name (required)
- **State**: State/Province abbreviation (required)
- **ZipCode**: Postal code (required)

### Steps to Create Excel File

1. **Using Microsoft Excel**:
   - Open Excel and create a new workbook
   - Rename the first sheet to "SkillServiceData"
   - Add column headers in row 1
   - Fill in test data rows starting from row 2
   - Save as `skillservice_testdata.xlsx` in `src/test/resources/testdata/`

2. **Using LibreOffice Calc** (Free Alternative):
   - Open LibreOffice Calc
   - Create sheet with same structure
   - Save as `.xlsx` format

3. **Using Python with openpyxl** (Automated):
```python
from openpyxl import Workbook

wb = Workbook()
ws = wb.active
ws.title = "SkillServiceData"

headers = ["FullName", "Email", "Phone", "SkillCategory", "SkillType", 
           "Description", "Experience", "Address", "City", "State", "ZipCode"]
ws.append(headers)

# Add test data
data = [
    ("John Doe", "john@example.com", "9876543210", "Plumbing", "Plumber", 
     "Experienced plumber with 10+ years", "10", "123 Main Street", "New York", "NY", "10001"),
    # Add more rows...
]

for row in data:
    ws.append(row)

wb.save("skillservice_testdata.xlsx")
```

---

## 🎯 Test Scenarios Explained

### Scenario 1: Basic Registration
Tests the fundamental registration flow with basic details only.

```gherkin
Scenario: User registers as a skill service provider with basic details
```

**What it tests**:
- Navigation to skill service page
- Form is displayed
- Basic information can be filled

### Scenario 2: Skill Category Selection
Tests selecting specific skill categories and types.

```gherkin
Scenario: Register as plumber skill service provider
```

**What it tests**:
- Skill category selection dropdown works
- Skill type dropdown works
- Skill description can be filled

### Scenario 3: Complete Registration with OTP
Tests the full registration flow including OTP verification.

```gherkin
Scenario: Register as electrician skill service provider with complete details
```

**What it tests**:
- All form fields can be filled
- OTP can be sent
- Mock OTP verification works
- Registration completes successfully

### Scenario 4: Bulk Registration
Tests multiple registrations in sequence.

```gherkin
Scenario: Register multiple skill service providers from Excel data
```

**What it tests**:
- Multiple registrations don't interfere
- Excel data is read correctly
- Each registration completes independently

### Scenario 5: Parametrized Testing
Uses scenario outline to test multiple skill categories.

```gherkin
Scenario Outline: Register skill service provider with different skill categories
```

**What it tests**:
- Different skill categories work
- Different skill types work
- Parameters are substituted correctly

### Scenario 6: Error Handling
Tests form validation for missing required fields.

```gherkin
Scenario: Verify error handling for incomplete skill service registration
```

**What it tests**:
- Validation errors for missing phone
- Form remains on registration page
- Error messages are displayed

### Scenario 7: OTP Resend
Tests the OTP resend functionality.

```gherkin
Scenario: Verify OTP resend functionality
```

**What it tests**:
- OTP can be resent
- Multiple OTP entries work
- Registration succeeds with resent OTP

---

## 🔧 Key Methods in SkillServicePage

### Navigation Methods
```java
// Navigate directly to skill service page
navigateToSkillService()

// Navigate via home page link
navigateToSkillServiceFromHome()

// Click register button
clickRegisterAsServiceProvider()
```

### Form Filling Methods
```java
// Fill individual fields
fillBasicInfo(String fullName, String email, String phone)
selectSkillCategory(String category)
selectSkillType(String skillType)
fillSkillDescription(String description)
fillExperience(String years)

// Fill complete form from map
fillCompleteForm(Map<String, String> data)
```

### OTP Methods
```java
// Send OTP
sendOtp()

// Enter OTP from API
enterOtpFromApi(String phoneNumber)

// Enter mock OTP (testing)
enterMockOtp()
```

### Verification Methods
```java
// Check if registration was successful
boolean isRegistrationSuccessful()

// Get success message
String getSuccessMessage()

// Check if form is visible
boolean isRegistrationFormVisible()
```

### Complete Flows
```java
// Register with mock OTP
registerSkillServiceWithMockOtp(Map<String, String> data)

// Register with real OTP from API
registerSkillServiceWithRealOtp(Map<String, String> data)
```

---

## 📝 Using Playwright MCP for Interactive Testing

The framework is configured to work with Playwright MCP for interactive browser control and debugging.

### Test Interactively
```bash
# Start Playwright MCP server
# Then run tests with IDE support for step-by-step execution
mvn clean test -Dtags="@skillservice" -Dheadless=false
```

### Debug a Specific Scenario
1. Add `@debug` tag to a scenario
2. Run with headless=false
3. Step through the test in VS Code
4. Use MCP tools to inspect elements, take screenshots, etc.

---

## 🐛 Troubleshooting

### Issue: Excel file not found
**Solution**: Ensure the file is at `src/test/resources/testdata/skillservice_testdata.xlsx`

### Issue: OTP verification fails
**Solution**: 
- Check if mock OTP is enabled (enter "0000")
- Check if real OTP API is accessible
- Verify phone number format

### Issue: Selectors not found
**Solution**:
- The selectors are flexible with multiple options (CSS selectors + XPath combinations)
- If UI changes, update selectors in SkillServicePage.java
- Use Playwright Inspector: `npx playwright codegen https://dialinarch.com`

### Issue: Tests timeout
**Solution**:
- Increase timeout in config.properties: `defaultTimeout=30000`
- Check network connectivity
- Verify website is accessible

---

## 🚀 Running Tests via CLI

### Using Maven
```bash
# Run all skill service tests
mvn clean test -Dtags="@skillservice"

# Run specific scenario
mvn clean test -Dtags="@skillservice and basic"

# Run with custom timeout
mvn clean test -Dtags="@skillservice" -DdefaultTimeout=45000

# Run in different environment
mvn clean test -Dtags="@skillservice" -DbaseUrl="https://staging.dialinarch.com/"
```

### View Reports
After test execution, reports are available at:
- **Extent Report**: `reports/YYYYMMDD_HHMMSS/TestReport.html`
- **Cucumber Report**: `target/cucumber-reports/cucumber.html`

---

## 📚 Integration with CI/CD

Add to your GitHub Actions or Jenkins pipeline:

### GitHub Actions Example
```yaml
- name: Run Skill Service Tests
  run: mvn clean test -Dtags="@skillservice" -Dheadless=true
  
- name: Publish Test Report
  if: always()
  uses: actions/upload-artifact@v2
  with:
    name: test-reports
    path: reports/
```

---

## ✅ Verification Checklist

Before running tests, ensure:

- [ ] SkillServicePage.java is created in `src/main/java/framework/pages/`
- [ ] skillservice.feature is created in `src/test/resources/features/`
- [ ] SkillServiceSteps.java is created in `src/test/java/steps/`
- [ ] PageManager.java is updated with `skillServicePage()` method
- [ ] Excel test data file is created in `src/test/resources/testdata/`
- [ ] Playwright MCP is configured in `.mcp.json`
- [ ] Maven dependencies are updated (if needed)
- [ ] Java 21 is installed
- [ ] Playwright browsers are installed: `mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"`

---

## 📞 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review existing test scenarios as examples
3. Check Playwright documentation: https://playwright.dev/java/
4. Review framework documentation in `/docs/`

---

## 🎓 Learning Resources

- **Playwright Java**: https://playwright.dev/java/
- **Cucumber BDD**: https://cucumber.io/
- **TestNG**: https://testng.org/
- **Page Object Model**: https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/

---

## 📋 Future Enhancements

Potential additions to this test suite:
- [ ] Test data validation (email format, phone format)
- [ ] Multiple language support in registration form
- [ ] Payment/billing information tests
- [ ] Profile image upload tests
- [ ] Skill certification upload tests
- [ ] Review/rating system tests
- [ ] Service availability calendar tests
- [ ] API-based backend verification

