# Phase 6: Backward Compatibility & New Scenario Test Plan

**Status:** Execution Plan | **Date:** August 9, 2026

## Objective

Validate that:
1. Existing tests continue to work (backward compatibility)
2. New data-driven multi-user/multi-business tests execute correctly
3. No regressions introduced
4. Framework is production-ready

## Test Coverage

### Test Profile 1: Backward Compatibility (Existing Tests)

**Tags:** `not @DataDriven`

**What to Test:**
- Existing @skillservice tests ✓
- Existing @Completeflow tests ✓
- All existing step definitions ✓
- Single-user mode unchanged ✓

**Expected Results:**
- All existing tests PASS
- No modifications to test code needed
- Session management transparent to existing tests

**How to Run:**
```bash
# Edit src/test/java/runners/TestRunner.java
tags = "not @DataDriven",

# Run tests
mvn test
```

### Test Profile 2: Data-Driven Excel Validation

**Tags:** `@DataDriven and @ExcelValidation`

**What to Test:**
- Excel file has required columns (UserID, Mobile, Profession, BusinessName) ✓
- Excel has data rows ✓
- All required fields populated ✓
- No UserID or Mobile empty ✓
- TestDataGrouper correctly reads data ✓
- ExcelUtil grouping methods work ✓

**Expected Results:**
- Excel structure validated
- UserID column recognized
- Mobile column recognized
- Profession column recognized (already existed)
- 2 data rows confirmed
- USER001 grouping correct
- 2 businesses for USER001

**How to Run:**
```bash
# Edit src/test/java/runners/TestRunner.java
tags = "@DataDriven and @ExcelValidation",

# Run tests
mvn test
```

### Test Profile 3: Single-User Multi-Business (Session Reuse)

**Tags:** `@DataDriven and @SingleUserMultiBusinessInSession`

**What to Test:**
- USER001 logs in once ✓
- USER001 creates first business (Architect) ✓
- Session remains active ✓
- USER001 creates second business (Interior) ✓
- Both businesses created in SAME session ✓
- Session not reset between businesses ✓
- No logout/login cycle ✓

**Expected Results:**
- Both businesses created successfully
- URL transitions show dashboard between creations
- Browser cookies persist (same session)
- Performance: 2 businesses in ~X seconds

**How to Run:**
```bash
# Edit src/test/java/runners/TestRunner.java
tags = "@DataDriven and @SingleUserMultiBusinessInSession",

# Run tests
mvn test
```

**Success Criteria:**
```
[DataDrivenSteps] Logging in user: USER001 with mobile: 9876543210
[DataDrivenSteps] Creating business 1/2
[DataDrivenSteps] ✓ Business created successfully
[DataDrivenSteps] Creating business 2/2
[DataDrivenSteps] ✓ Business created successfully
[DataDrivenSteps] ✓ All businesses created in single session for user: USER001
```

### Test Profile 4: Multi-User with Isolation

**Tags:** `@DataDriven and @MultiUser`

**What to Test:**
- SessionManager initializes ✓
- USER001 gets isolated session (Session A) ✓
- USER001 creates business ✓
- USER002 gets NEW isolated session (Session B) ✓
- USER002 creates business ✓
- Session A and Session B completely separate ✓
- No cookies leaked between users ✓
- No data shared between users ✓

**Expected Results:**
- Both users successfully create businesses
- Each user's session is isolated
- No cross-contamination between sessions
- Cleanup removes all sessions
- SessionManager.printSessionSummary() shows isolation

**How to Run:**
```bash
# For this test, we need to add more users to Excel
# Update registration_testdata.xlsx to add USER002:
#   Row 3: USER002 | 9234567890 | Architect | Tech Solutions | ...

# Edit src/test/java/runners/TestRunner.java
tags = "@DataDriven and @MultiUser",

# Run tests
mvn test
```

**Success Criteria:**
```
[DataDrivenSteps] Total users: 2
[DataDrivenSteps]   User: USER001 (1 business)
[DataDrivenSteps]   User: USER002 (1 business)
[DriverFactory] Created new session for user: USER001
[DriverFactory] Created new session for user: USER002
[DataDrivenSteps] ✓ Users have isolated sessions
[DataDrivenSteps] ✓ No data leak detected
```

### Test Profile 5: All Data-Driven Tests

**Tags:** `@DataDriven`

**What to Test:**
- All data-driven scenarios combined
- Excel validation ✓
- Single-user multi-business ✓
- Multi-user isolation ✓
- Specific user scenario ✓
- First business only scenario ✓

**How to Run:**
```bash
# Edit src/test/java/runners/TestRunner.java
tags = "@DataDriven",

# Run tests
mvn test
```

### Test Profile 6: All Tests (Full Suite)

**Tags:** None (empty string)

**What to Test:**
- Everything: existing + new data-driven tests
- Full regression suite
- All features combined

**How to Run:**
```bash
# Edit src/test/java/runners/TestRunner.java
tags = "",

# Run tests
mvn test
```

## Test Execution Checklist

### Pre-Test Setup

- [ ] Clone feature branch: `feature/data-driven-multi-business-hosting`
- [ ] Verify no uncommitted changes: `git status`
- [ ] Compile project: `mvn compile -q`
- [ ] Verify Excel structure: `mvn exec:java -Dexec.mainClass="framework.utils.ExcelStructureInspector"`
- [ ] Backup report directory: `mv reports reports.backup`
- [ ] Clear browser cache/cookies

### Phase 6.1: Run Backward Compatibility Tests

- [ ] Edit TestRunner: `tags = "not @DataDriven",`
- [ ] Run: `mvn clean test`
- [ ] Check results:
  - [ ] No test failures
  - [ ] No compilation errors
  - [ ] All existing scenarios pass
  - [ ] Report generated: `target/cucumber-reports/cucumber.html`
- [ ] Document results

### Phase 6.2: Run Data-Driven Tests

- [ ] Edit TestRunner: `tags = "@DataDriven and @ExcelValidation",`
- [ ] Run: `mvn clean test`
- [ ] Verify:
  - [ ] Excel validation passes
  - [ ] UserID/Mobile columns recognized
  - [ ] Report generated

- [ ] Edit TestRunner: `tags = "@DataDriven and @SingleUserMultiBusinessInSession",`
- [ ] Run: `mvn clean test`
- [ ] Verify:
  - [ ] USER001 logs in once
  - [ ] Creates 2 businesses
  - [ ] Session reused
  - [ ] Both succeed

- [ ] Edit TestRunner: `tags = "@DataDriven and @MultiUser",`
- [ ] Note: Requires USER002 in Excel (see below)
- [ ] Run: `mvn clean test`
- [ ] Verify:
  - [ ] Multiple users handled
  - [ ] Sessions isolated
  - [ ] No data leak

### Prepare Excel for Multi-User Test

Before running `@MultiUser` tests, update Excel to add USER002:

```
Row 3: USER002 | 9234567890 | Architect | Tech Solutions | Advanced Tech Architecture | Tech Park | 400001 | B.Tech | 15 | 1000 | IT Services | Web Development | 50
```

Or use ExcelDataUpdater to add programmatically.

## Expected Results Summary

### Backward Compatibility: ✅ PASS
```
✓ All existing tests execute
✓ No code modifications needed
✓ Single-user mode works as before
✓ Session management transparent
```

### Excel Validation: ✅ PASS
```
✓ UserID column recognized
✓ Mobile column recognized
✓ Profession column recognized
✓ 2 data rows confirmed
✓ All fields populated
```

### Session Reuse (Single User): ✅ PASS
```
✓ USER001 logged in once with 9876543210
✓ Created Architect business (success)
✓ Session remained active
✓ Created Interior business (success)
✓ Both businesses in SAME session
✓ Performance acceptable
```

### Session Isolation (Multi User): ✅ PASS (after adding USER002 to Excel)
```
✓ USER001 session A created
✓ USER002 session B created (separate)
✓ USER001 business created (session A)
✓ USER002 business created (session B)
✓ No cookie sharing
✓ No data leak
✓ Sessions properly isolated
```

## Troubleshooting Guide

### Issue: "UserID column not found in Excel"
**Root Cause:** Excel was not updated with UserID/Mobile columns
**Solution:** Run `mvn exec:java -Dexec.mainClass="framework.utils.ExcelDataUpdater"`

### Issue: Test hangs on login
**Root Cause:** OTP input timing issue or network problem
**Solution:** 
1. Check internet connection
2. Verify OTP locators in LoginPage.java
3. Increase timeout values

### Issue: "SessionManager not initialized"
**Root Cause:** Scenario missing `@DataDriven` tag or hooks not loaded
**Solution:**
1. Verify scenario has `@DataDriven` tag
2. Verify `src/test/java/hooks/DataDrivenHooks.java` exists
3. Check glue path includes "Hooks": `glue = {"steps", "Hooks"}`

### Issue: Session A interfering with Session B
**Root Cause:** SessionManager not properly isolating contexts
**Solution:**
1. Verify each user gets unique BrowserContext
2. Check that page/context references use SessionManager
3. Run with logging enabled for debug output

### Issue: Tests pass locally but fail in CI/CD
**Root Cause:** Environment differences, timing issues
**Solution:**
1. Increase timeouts for CI environment
2. Add more explicit waits before navigation
3. Use headless mode if needed

## Performance Benchmarks (Expected)

| Scenario | Expected Duration | Notes |
|----------|------------------|-------|
| Single business creation | 2-3 min | Includes all steps (login, details, review, create) |
| USER001 + 2 businesses | 4-5 min | Session reused, no login repetition |
| USER001 + USER002 (1 each) | 6-7 min | Parallel possible, but sequential in current impl. |
| Excel validation only | 10-15 sec | Quick data structure check |
| All backward compat tests | 10-15 min | Existing full suite |
| All data-driven tests | 15-20 min | New full suite |

## Deliverables

### Phase 6.1: Backward Compatibility Results
- [ ] Test execution summary
- [ ] HTML report: `target/cucumber-reports/cucumber.html`
- [ ] JSON report: `target/cucumber-reports/cucumber.json`
- [ ] No regressions document

### Phase 6.2: New Scenario Validation
- [ ] Excel validation report
- [ ] Session reuse verification
- [ ] Session isolation verification (after adding USER002)
- [ ] Performance metrics
- [ ] Defect log (if any)

### Summary Report
- [ ] PHASE_6_TEST_RESULTS.md documenting:
  - All test execution results
  - Success/failure status per profile
  - Metrics and benchmarks
  - Recommendations
  - Sign-off for production readiness

## Sign-Off Criteria

Framework is **production-ready** when:
1. ✅ All backward compatibility tests PASS
2. ✅ All data-driven tests PASS
3. ✅ Excel validation PASS
4. ✅ Session reuse verified
5. ✅ Session isolation verified
6. ✅ No regressions introduced
7. ✅ Documentation complete and accurate
8. ✅ Performance acceptable

## Notes

- Current Excel has only USER001 (2 businesses)
- To test multi-user isolation, add USER002 to Excel
- Tests use mock OTP "0000" for faster execution
- Headless mode can be enabled in ConfigManager
- Parallel test execution not yet implemented

## Next Steps After Phase 6

1. **Merge to main/develop branch**
   - Create pull request
   - Code review
   - Merge after approval

2. **Production Deployment**
   - Deploy to CI/CD pipeline
   - Add to automated regression suite
   - Monitor for issues

3. **Future Enhancements**
   - Add performance benchmarking
   - Implement parallel user execution
   - Add load testing (N users, M businesses)
   - Performance profiling

---

**Test Plan Version:** 1.0  
**Last Updated:** August 9, 2026  
**Status:** Ready for Execution
