# Quick Start: Skill Service Registration Tests

## 🎯 What Was Added

Skill Service registration test scenarios have been integrated into the existing registration test flow. These test the registration process for service providers (Plumbers, Electricians, Carpenters, etc.) on DialinArch.

---

## ⚡ Quick Start (5 minutes)

### Step 1: Build the Project
```bash
mvn clean compile
```

### Step 2: Generate Test Data (Optional)
```bash
java -cp target/classes framework.utils.TestDataGenerator
```

### Step 3: Run Skill Service Tests
```bash
# Run only skill service tests
mvn clean test -Dtags="@skillservice"

# Run with visible browser
mvn clean test -Dtags="@skillservice" -Dheadless=false
```

### Step 4: View Results
Reports are generated at: `reports/YYYYMMDD_HHMMSS/TestReport.html`

---

## 📝 What's New

### New Scenarios in registration.feature

```gherkin
@registration @skillservice
Scenario: User registers as Skill Service Provider - Plumber
  - John Doe | 9876543210 | Plumber with 10 years experience

@registration @skillservice  
Scenario: User registers as Skill Service Provider - Electrician
  - Alice Johnson | 9876543211 | Electrician with 12 years experience

@registration @skillservice
Scenario: User registers as Skill Service Provider - Carpenter
  - Bob Smith | 9876543212 | Carpenter with 8 years experience

@registration @skillservice
Scenario Outline: Register multiple skill service providers
  - Tests: Painter, HVAC Tech, Plumber (15 years)

@registration @skillservice
Scenario: Full registration with address details
  - Frank Miller | General Contractor | 20 years
  - Includes complete address: San Francisco, CA
```

### New Step Definitions in SignUpSteps.java

```java
// New methods added to handle skill service data
user fills registration form with skill service data from Excel row {int}
user fills basic registration information with {string}, {string}, and {string}
user selects service type as {string}
user fills service description as {string}
user fills years of experience as {string}
user fills address details with {string}, {string}, {string}, {string}
```

### New Utility: TestDataGenerator.java

Generates sample Excel test data file automatically:
```bash
java -cp target/classes framework.utils.TestDataGenerator
```

---

## 🚀 Common Commands

| Command | Purpose |
|---------|---------|
| `mvn clean test -Dtags="@skillservice"` | Run all skill service tests |
| `mvn clean test -Dtags="@skillservice" -Dheadless=false` | Run with visible browser |
| `mvn clean test -Dtags="@skillservice" -Dbrowser=firefox` | Run with Firefox |
| `mvn test -Dtags="@registration"` | Run all registration tests (includes skill service) |
| `java -cp target/classes framework.utils.TestDataGenerator` | Generate test data |

---

## 📊 Test Data Format

The tests use data like this (from DataTable in scenarios):

```
FullName      | Email            | Phone      | ServiceType | Description      | Experience
John Doe      | john@example.com | 9876543210 | Plumber     | 10+ years exp    | 10
Alice Johnson | alice@example.com| 9876543211 | Electrician | 12+ years exp    | 12
Bob Smith     | bob@example.com  | 9876543212 | Carpenter   | 8+ years exp     | 8
```

---

## ✅ Verification Checklist

Before running tests, verify:

- [ ] `registration.feature` contains skill service scenarios (check ✓ above)
- [ ] `SignUpSteps.java` has new step definitions
- [ ] `TestDataGenerator.java` exists in `framework/utils/`
- [ ] Project compiles: `mvn clean compile`
- [ ] Playwright MCP is enabled in `.mcp.json`

---

## 🔍 Using Playwright MCP

Your Playwright MCP is configured and ready to use:

### Interactive Element Inspection
```bash
npx playwright codegen https://dialinarch.com
```

### Debug a Test
1. Add `@debug` tag to scenario
2. Run with: `mvn clean test -Dtags="@debug" -Dheadless=false`
3. Use browser DevTools to inspect elements

### Generate Test Code
```bash
npx @playwright/mcp@latest
```

---

## 📁 Files Changed/Created

### Modified Files
- ✏️ `src/test/resources/features/registration.feature` - Added 5 skill service scenarios
- ✏️ `src/test/java/steps/SignUpSteps.java` - Added skill service step definitions

### New Files
- ✨ `src/main/java/framework/utils/TestDataGenerator.java` - Test data generator
- ✨ `docs/SKILL_SERVICE_TEST_GUIDE.md` - Detailed guide
- ✨ `SKILL_SERVICE_INTEGRATION.md` - Integration summary
- ✨ `QUICK_START_SKILL_SERVICE.md` - This file

---

## 🎓 Test Scenarios Overview

### Scenario 1: Basic Plumber Registration
```gherkin
Registers: John Doe as a Plumber
- Verifies form can be filled
- Verifies OTP process works
- Verifies successful redirect
```

### Scenario 2: Electrician Registration  
```gherkin
Registers: Alice Johnson as Electrician
- Tests with different service type
- Validates OTP verification
```

### Scenario 3: Carpenter Registration
```gherkin
Registers: Bob Smith as Carpenter
- Another service type variation
- Full flow validation
```

### Scenario 4: Multiple Providers (Parametrized)
```gherkin
Tests 3 different service types in one scenario:
- Painter (6 years)
- HVAC Tech (7 years)  
- Plumber (15 years)
```

### Scenario 5: Full Details Registration
```gherkin
Complete registration with:
- All basic information
- Service type selection
- Experience details
- Address information
```

---

## 💡 Tips

1. **First Time Run**:
   ```bash
   mvn clean test -Dtags="@skillservice" -Dheadless=false
   ```
   This shows you exactly what the tests are doing.

2. **Quick Test**:
   ```bash
   mvn test -Dtags="@skillservice and Plumber"
   ```
   Test only the Plumber scenario.

3. **Debug Mode**:
   - Add `@debug` tag to any scenario
   - Run with `headless=false`
   - Step through in VS Code debugger

4. **View Reports**:
   - Open `reports/*/TestReport.html` in browser
   - See screenshots, logs, and execution details

---

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| "Step undefined" | Ensure `SignUpSteps.java` is compiled |
| "Excel not found" | Run `TestDataGenerator` first |
| "Page not loading" | Check `config.properties` baseUrl |
| "OTP fails" | Use mock OTP "0000" for tests |
| "Timeout" | Increase `defaultTimeout=45000` in config |

---

## 📞 Getting Help

1. **Check Documentation**:
   - `docs/SKILL_SERVICE_TEST_GUIDE.md` - Detailed methods
   - `SKILL_SERVICE_INTEGRATION.md` - Full integration details
   - `docs/ARCHITECTURE.md` - Framework architecture

2. **Playwright Resources**:
   - https://playwright.dev/java/ - Official docs
   - https://github.com/microsoft/playwright-java - Source code

3. **Cucumber Resources**:
   - https://cucumber.io/ - BDD framework
   - https://cucumber.io/docs/cucumber/ - Documentation

---

## 🎉 You're All Set!

Your skill service registration tests are ready to run. Try this command to verify everything works:

```bash
mvn clean test -Dtags="@skillservice" -Dheadless=false
```

This will:
1. Compile the project
2. Download browser drivers
3. Run all skill service scenarios
4. Generate HTML reports
5. Show visual feedback in your browser

**Happy Testing! 🚀**
