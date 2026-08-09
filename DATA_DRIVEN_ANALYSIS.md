# Data-Driven Multi-Business & Multi-User Hosting - Framework Analysis

## ✅ FRAMEWORK INSPECTION COMPLETE

---

## 📊 Current Architecture Overview

### Technology Stack
- **Language**: Java 21
- **Test Automation**: Playwright Java 1.49.0
- **BDD Framework**: Cucumber 7.20.1
- **Test Runner**: TestNG 7.10.2
- **Build**: Maven
- **Config Management**: Owner library 1.0.12
- **Excel Handling**: Apache POI 5.2.5
- **Reporting**: Extent Reports 5.1.2

### Project Structure
```
src/
├── main/java/framework/
│   ├── config/
│   │   ├── ConfigManager.java       ← Configuration management
│   │   └── FrameworkConfig.java     ← Config interface
│   ├── core/
│   │   └── DriverFactory.java       ← Browser/Context/Page lifecycle (ThreadLocal)
│   ├── managers/
│   │   └── PageManager.java         ← Lazy-loading page objects
│   ├── pages/                       ← Page Object Model
│   │   ├── BasePage.java            ← Base with common methods
│   │   ├── LoginPage.java           ← Authentication
│   │   ├── HomePage.java            ← Landing page
│   │   ├── SignUpPage.java          ← Registration
│   │   ├── HostYourBusinessPage.java ← Business hosting (CRITICAL)
│   │   └── AddProjectPage.java      ← Project management
│   └── utils/
│       ├── ExcelUtil.java           ← Excel data loading (CRITICAL)
│       ├── OtpApiClient.java        ← OTP fetching
│       ├── WaitUtil.java            ← Waits
│       └── ExtentReportUtil.java    ← Reporting
│
└── test/
    ├── java/
    │   ├── Hooks/
    │   │   ├── Hooks.java           ← Browser setup/teardown (order: 0,1,2)
    │   │   └── TestContext.java     ← Scenario data storage
    │   ├── runners/
    │   │   └── TestRunner.java      ← Cucumber runner
    │   └── steps/                   ← Step definitions
    │       ├── LoginSteps.java      ← Authentication steps
    │       ├── HomeSteps.java       ← Navigation steps
    │       ├── SignUpSteps.java     ← Registration steps
    │       ├── HostBusinessSteps.java ← Business hosting steps (CRITICAL)
    │       └── AddProjectSteps.java ← Project steps
    │
    └── resources/
        ├── features/                ← Feature files (Cucumber)
        │   ├── home.feature
        │   ├── login.feature
        │   ├── signup.feature
        │   ├── registration.feature
        │   ├── hostyourBusiness.feature (CRITICAL)
        │   └── addproject.feature
        ├── testdata/               ← Excel test data
        │   └── registration_testdata.xlsx (CRITICAL)
        └── config.properties       ← Runtime configuration
```

---

## 🔍 CRITICAL COMPONENTS ANALYSIS

### 1. BROWSER LIFECYCLE (DriverFactory.java)

**Current Implementation:**
```java
private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
```

**Issue with Multi-User Requirement:**
- ✅ Uses ThreadLocal (good for parallel tests)
- ✅ ONE browser/context/page per thread
- ❌ Problem: ONE context = ONE user session
- ❌ Cannot have multiple users with different credentials in same test

**For Multi-User Support:**
- Need to manage multiple contexts/pages per thread
- Suggested: Store context/page stack or map by userId

---

### 2. EXCEL DATA LOADING (ExcelUtil.java)

**Current Implementation:**
```java
public static Map<String, String> getTestDataRow(String fileName, String sheetName, int rowIndex)
```

**Current Usage (Hard-Coded Row Numbers):**
```gherkin
When user enters Business and Basic Details using Excel row 1
When user enters Business and Basic Details using Excel row 2
```

**Issue:**
- ✅ Row-based access works
- ❌ Hard-coded row numbers in feature file
- ❌ No concept of user grouping
- ❌ No data-driven row iteration

**For Multi-User Support:**
- Need: `getTestDataByUser(userId)` → List of businesses
- Need: `groupTestDataByUser()` → Map<userId, List<rows>>
- Keep existing `getTestDataRow()` for backward compatibility

---

### 3. HOOKS (Hooks.java)

**Current Implementation:**
```java
@Before(order = 0) → setup()           // Browser init
@After(order = 1)  → saveTrace()       // Trace save
@After(order = 2)  → captureScreenshot()
@After(order = 0)  → tearDown()        // Browser close
```

**Current Behavior:**
- ✅ Browser created before EACH scenario
- ✅ Browser closed after EACH scenario
- ❌ No session reuse between scenarios

**For Multi-User Support:**
- Need: Hooks that manage browser lifecycle per USER
- Need: Reuse context/page for same user's multiple businesses
- Need: Close/cleanup when user changes
- Suggested: Convert to data-driven iteration (NOT scenario-based)

---

### 4. TEST CONTEXT (TestContext.java)

**Current Implementation:**
```java
private final Map<String, Object> scenarioData = new HashMap<>();
```

**Current Usage:**
- Scenario-level data storage
- Cleared between scenarios

**For Multi-User Support:**
- Need: Preserve user session/context between businesses
- Need: Store current userId, mobile, context
- Keep TestContext but extend usage

---

### 5. HOST YOUR BUSINESS FEATURE (hostyourBusiness.feature)

**Current Scenarios:**

Scenario 1: Generic business (Architect implied)
```gherkin
Scenario: Successfully host a new business using Excel data
  When user clicks the "Profile Icon" button
  And user clicks the "Host Your Business" button
  And user proceeds to the Business Details page
  And user enters Business and Basic Details using Excel row 1
  ...
```

Scenario 2: Interior (Hardcoded profession)
```gherkin
@Completeflow
Scenario: Successfully host Your business for Interior using Excel data
  When user clicks the "Profile Icon" button
  And user clicks the "Host Your Business" button
  And user clicks the "Interior" Profession
  ...
  And user enters Business and Basic Details using Excel row 2
  ...
```

**Issue:**
- ✅ Two scenarios show profession selection pattern
- ✅ Reusable profession selection method exists
- ❌ Hard-coded row numbers
- ❌ Hard-coded professions
- ❌ Can't scale to multiple professions easily
- ❌ No session reuse between scenarios

**Pattern Observed:**
- Interior flow includes: `user clicks the "Interior" Profession` + profession-specific next button
- Generic flow skips profession selection (assumes default)

---

### 6. HOST YOUR BUSINESS PAGE OBJECT (HostYourBusinessPage.java)

**Key Methods:**
```java
clickHostBusiness()           // Click "Host Your Business" button
clickProfession(profession)   // Click profession by name
selectCatRunTime(catName)     // Select category at runtime
fillBusinessDetails(data)     // Fill business form
fillAddressDetails(data)      // Fill address form
selectCategoriesAndTerms()    // Select categories & accept terms
verifyBusinessCreated()       // Verify success
```

**Supported Professions:**
- Interior (explicitly tested)
- Generic/Architect (implied from Scenario 1)
- Evidence: `clickProfession(String profession)` accepts any profession

**Form Fields (from fillBusinessDetails):**
- Mapped to Excel columns via `data.get(key)`
- Supports dynamic data-driven filling

**Category Selection:**
- Hardcoded: `selectCategoriesAndTerms()`
- Runtime selection: `selectCatRunTime(catName)`

---

### 7. LOGIN & OTP HANDLING (LoginPage.java, OtpApiClient.java)

**Current Implementation:**
```java
public void loginWithMockOtp(String mobile) {
  navigateToLoginPage();
  enterMobile(mobile);
  requestOtp();
  enterMockOtp();  // ← "0000" hardcoded
  clickVerifyButton();
}
```

**Mock OTP:**
- Hardcoded as "0000" via four separate inputs (otp-0, otp-1, otp-2, otp-3)
- Config-based: ✅ Can read from config.properties if needed

**For Multi-User Support:**
- ✅ Method `loginWithMockOtp(String mobile)` already exists
- ✅ Can call multiple times with different mobiles
- Need: Ensure session isolation between logins

---

### 8. EXISTING TEST DATA (registration_testdata.xlsx)

**Known Sheets:**
- HostYourBusiness (used by current scenarios)
- Other sheets may exist for registration, signup, etc.

**Data Structure:**
- Row 1, Row 2 currently tested
- Columns: Inferred from `data.get(key)` calls
  - BusinessName
  - About
  - GSTNumber
  - Address
  - Pincode
  - Qualification
  - Experience
  - SuccessStory
  - Category (inferred)

**⚠️ Excel structure NOT fully inspected yet**
- Need to read actual Excel file to see all columns
- Need to check if UserID/Mobile columns exist

---

## 🎯 KEY FINDINGS

### ✅ What's Already in Place

| Component | Status | Reusable For Multi-User? |
|-----------|--------|--------------------------|
| Excel reading | ✅ Exists | ✅ Yes, add grouping logic |
| Page Object Model | ✅ Complete | ✅ Yes, reuse as-is |
| Profession selection | ✅ Implemented | ✅ Yes, works dynamically |
| Category selection | ✅ Implemented | ✅ Yes, runtime selection exists |
| Business details form | ✅ Data-driven | ✅ Yes, data-driven filling |
| OTP & Login | ✅ Flexible | ✅ Yes, accepts mobile parameter |
| Browser lifecycle | ✅ ThreadLocal | ⚠️ Needs enhancement |
| Test reporting | ✅ Hooks + Extent | ✅ Yes, works per test |
| Configuration | ✅ Owner library | ✅ Yes, central config |

### ❌ What Needs Enhancement

| Area | Current | Required |
|------|---------|----------|
| Row-based Excel access | Hard-coded rows | User → Business grouping |
| Browser context reuse | 1 context per scenario | N contexts for N users |
| Session management | Scenario-based | User-based persistence |
| Feature file | Row numbers visible | No row numbers visible |
| Test structure | Scenario-per-test | Data-driven test loop |
| User tracking | None | Store current userId |
| Logout handling | Not implemented | Add logout method |
| Multi-user iteration | Not supported | Auto-group by userId |

---

## 📋 RECOMMENDED ARCHITECTURE

### Phase 1: Data Layer Enhancement

**New Utility: TestDataGrouper.java**
```java
public class TestDataGrouper {
    
    public static Map<String, List<Map<String, String>>> groupByUser(
        String fileName, String sheetName
    ) {
        // Read all rows from Excel
        // Group by "UserID" column
        // Return Map<userId, List<businessRows>>
    }
    
    public static List<String> getAllUserIds(
        String fileName, String sheetName
    ) {
        // Return unique UserIds in order of first appearance
    }
}
```

**Modified ExcelUtil.java**
```java
// Add method (keep existing for backward compatibility)
public static Map<String, List<Map<String, String>>> groupByUser(
    String fileName, String sheetName
) {
    // Delegates to TestDataGrouper
}
```

### Phase 2: Session Management Enhancement

**New Utility: SessionManager.java**
```java
public class SessionManager {
    
    private Map<String, BrowserContext> userContexts;
    private Map<String, Page> userPages;
    private String currentUserId;
    
    public void switchToUser(String userId, String mobile);
    public void logoutUser(String userId);
    public void reloginUser(String userId);
    public void clearAllSessions();
}
```

**Modified DriverFactory.java**
```java
// Keep existing ThreadLocal for backward compatibility
// Add SessionManager support
public static SessionManager getSessionManager();
public static void switchUser(String userId, String mobile);
```

### Phase 3: Feature File Redesign

**OLD (Row-based, hard-coded):**
```gherkin
Scenario: Successfully host Your business for Interior using Excel data
  And user enters Business and Basic Details using Excel row 2
```

**NEW (Data-driven, no row numbers):**
```gherkin
@MultiBusiness
Scenario: Multiple users host their businesses
  When users host businesses using test data
  Then all profiles should be created successfully
```

### Phase 4: Step Definition Refactoring

**OLD Step:**
```java
@When("user enters Business and Basic Details using Excel row {int}")
public void enterBusinessDetails(int rowIndex) {
    Map<String, String> data = ExcelUtil.getTestDataRow(..., rowIndex - 1);
    // ...
}
```

**NEW Step:**
```java
@When("users host businesses using test data")
public void usersHostBusinessesFromData() {
    Map<String, List<Map<String, String>>> userBusinesses = 
        TestDataGrouper.groupByUser(...);
    
    for (String userId : userBusinesses.keySet()) {
        switchToUser(userId);
        for (Map<String, String> business : userBusinesses.get(userId)) {
            hostBusiness(business);
        }
    }
}
```

### Phase 5: Hooks Modification

**New Approach:**
- Remove scenario-based browser setup
- Add test data-driven initialization
- Browser setup happens when first user is processed
- Browser cleanup happens when all users are done
- Context switch on user change

---

## ✅ BACKWARD COMPATIBILITY STRATEGY

**Keep Existing Tests Working:**

1. Don't delete/rename existing steps
2. Don't change existing page objects
3. Don't modify existing Excel loading
4. Old feature files can continue using row-based approach

**New functionality runs in parallel:**
```
Existing Tests
├── ExcelUtil.getTestDataRow() → Still works
├── Row 1, Row 2 scenarios → Still work
└── HostBusinessSteps with row numbers → Still work

New Tests
├── TestDataGrouper.groupByUser() → New functionality
├── SessionManager → New user switching
└── Feature file without rows → New approach
```

---

## 🎯 IMPLEMENTATION PLAN

### Step 1: Excel Data Inspection
- Read actual `registration_testdata.xlsx` HostYourBusiness sheet
- Identify all columns
- Check if UserID/Mobile columns exist
- Add columns if missing

### Step 2: Create TestDataGrouper
- Group rows by UserID
- Support single-user multiple-business scenario
- Support multi-user single/multiple business scenario

### Step 3: Extend SessionManager
- Manage browser contexts per user
- Handle user switching
- Handle logout

### Step 4: Create New Feature File
- Define @MultiBusiness and @MultiUserBusiness tags
- Use data-driven step without row numbers
- Reference Excel data by structure, not row index

### Step 5: Create New Step Definitions
- Implement multi-business loop logic
- Implement user switching logic
- Implement session persistence

### Step 6: Test Existing Tests
- Verify old feature files still work
- Verify old step definitions still work
- Verify old Excel rows still work

### Step 7: Test New Tests
- Test single user, multiple businesses
- Test multiple users, multiple businesses
- Test session isolation
- Test failure handling per business

---

## 📊 PROPOSED EXCEL STRUCTURE

**Current Unknown - Will confirm after reading file**

**Proposed for Multi-User Support:**

| TestCaseID | UserID  | Mobile     | Profession       | BusinessName   | About        | GSTNumber    | Address                | Pincode | Qualification | Experience | SuccessStory | Category             | SubCategory | Services | TermsAccepted |
|-----------|---------|-----------|------------------|----------------|--------------|-------------|----------------------|---------|---------------|-----------|--------------|---------------------|-------------|----------|-------------|
| TC001     | USER001 | 9876543210| Architect        | ABC Architects | Architecture | 18AABCT1234C| 123 Main Street      | 110001  | B.Arch        | 10        | Designed 50+ | Commercial Architecture | Modern     | Design   | Yes         |
| TC002     | USER001 | 9876543210| Interior         | ABC Interiors  | Interior     | 18AABCT1235C| 123 Main Street      | 110001  | B.Interior    | 8         | Decorated 30 | Residential Interiors  | Minimalist  | Design   | Yes         |
| TC003     | USER001 | 9876543210| Skilled Services | ABC Services   | Carpentry    | 18AABCT1236C| 123 Main Street      | 110001  | Diploma       | 15        | Built 100+   | Carpentry             | Custom     | Work     | Yes         |
| TC004     | USER002 | 9234567890| Interior         | XYZ Interiors  | Luxury Int.  | 18XYZCT1234C| 456 Oak Road        | 110002  | M.Interior    | 12        | Designed 60+ | Luxury Interiors       | Contemporary| Design   | Yes         |
| TC005     | USER003 | 9345678901| Architect        | PQR Architects | Sustainable | 18PQRCT1234C| 789 Pine Lane       | 110003  | B.Arch        | 7         | Green 20 bldg| Sustainable Architecture| Eco-Friendly| Design   | Yes         |

**Execution Order:**
```
USER001
├── TC001 Architect
├── TC002 Interior
└── TC003 Skilled Services

USER002
└── TC004 Interior

USER003
└── TC005 Architect
```

---

## 🔐 SESSION MANAGEMENT DETAILED DESIGN

### Same User, Multiple Businesses Flow

```
1. USER001 logs in
   → Creates BrowserContext[USER001]
   → Creates Page[USER001]
   → Stored in SessionManager.userContexts

2. USER001 creates Architect business
   → Uses Page[USER001]
   → Clicks "Host Your Business" again
   → Page state preserved in same context

3. USER001 creates Interior business
   → Same context, same page
   → Previous business context cleared from DOM
   → New business form loaded

4. USER001 creates Skilled Services business
   → Same reuse pattern

5. USER001 session ends
   → Close Page[USER001]
   → Close BrowserContext[USER001]
   → Remove from SessionManager
```

### Different Users Flow

```
1. USER001 creates all assigned businesses
   → BrowserContext[USER001] active
   
2. USER001 session end detected
   → Close Page[USER001]
   → Close BrowserContext[USER001]
   → Clear SessionManager
   
3. USER002 logs in
   → Creates NEW BrowserContext[USER002]
   → Creates NEW Page[USER002]
   → Stored in SessionManager.userContexts
   → Auth cookies/storage NOT shared with USER001
   
4. USER002 creates assigned businesses
   → Uses Page[USER002]
   
5. Repeat for USER003, etc.
```

---

## ⚠️ KNOWN UNKNOWNS (To Be Confirmed)

- [ ] Exact columns in registration_testdata.xlsx HostYourBusiness sheet
- [ ] Whether UserID/Mobile columns already exist in Excel
- [ ] What happens when user refreshes page after creating a business
- [ ] Whether "Host Your Business" can be clicked again or requires navigation
- [ ] Session persistence across business creations (same page vs new nav)
- [ ] Application's business creation response/redirect
- [ ] Whether logout is required between users or just new login
- [ ] Maximum number of businesses per user session
- [ ] Error recovery if one business fails

---

## ✅ NEXT STEPS

1. **User Confirmation**: Approve this architecture
2. **Excel Inspection**: Read actual test data file
3. **Application Testing**: Verify same-user session reuse works
4. **Implementation**: Build TestDataGrouper, SessionManager, new steps
5. **Testing**: Validate multi-user, multi-business scenarios
6. **Backward Compatibility**: Confirm old tests still pass

---

## 📝 SUMMARY

**Current State:**
- ✅ Framework is well-structured (POM, Data-driven, modular)
- ✅ Existing tests work with row-based Excel access
- ✅ All required page object methods exist
- ❌ Row numbers hard-coded in feature files
- ❌ No user grouping or session reuse
- ❌ No multi-user capability

**Proposed Solution:**
- ✅ Keep existing code 100% backward compatible
- ✅ Add TestDataGrouper for user/business grouping
- ✅ Add SessionManager for multi-user context management
- ✅ Add new feature with @MultiBusiness/@MultiUserBusiness tags
- ✅ Add new data-driven steps (no row numbers visible)
- ✅ Reuse all existing page objects and utilities

**Acceptance Criteria:**
- ✅ Existing tests still pass
- ✅ Excel is source of truth for user→business mapping
- ✅ Same user's session reused for multiple businesses
- ✅ Different users get isolated sessions
- ✅ No code changes needed to add new Excel rows
- ✅ Each business creation independently verified
- ✅ Failures include useful context (userId, businessName, etc.)
