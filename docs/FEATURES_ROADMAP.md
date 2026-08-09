# 📅 Features Roadmap & Gaps

This document tracks implemented flows, known gaps, and planned enhancements for the framework.

---

## ✅ Implemented Features

| Feature | Feature File | Page Class | Steps Class | Status |
|---------|--------------|------------|-------------|--------|
| Home / Landing page | `home.feature` | `HomePage` | `HomeSteps` | ✅ Implemented |
| Architect search | `home.feature` | `HomePage` | `HomeSteps` | ✅ Implemented |
| Login (OTP) | `login.feature` | `LoginPage` | `LoginSteps` | ✅ Implemented |
| Signup (OTP) | `signup.feature` | `SignUpPage` | `SignUpSteps` | ✅ Implemented |
| Registration (Excel) | `registration.feature` | `SignUpPage` | `SignUpSteps` | ✅ Implemented |
| Host Your Business | `hostyourBusiness.feature` | `HostYourBusinessPage` | `HostBusinessSteps` | ✅ Implemented |

---

## ⚠️ Known Gaps

### 1. Add Project — **NOT fully implemented**
- Feature file exists: `src/test/resources/features/addproject.feature`
- **Missing**:
  - `AddProjectPage.java` (page object)
  - `AddProjectSteps.java` (step definitions)
  - Registration in `PageManager`
  - `AddProject` sheet in Excel test data
- The `@Completeflow` tag in `TestRunner` currently runs the **Host Your Business** flow, not the Add Project flow.

### 2. `config.properties` used `browser=chrome`
- DriverFactory only handles `chromium | firefox | webkit` (default → chromium).
- ✅ **Fixed** to `browser=chromium`.

---

## 🚧 Planned / Recommended Enhancements

| # | Enhancement | Benefit | Priority |
|---|-------------|---------|----------|
| 1 | **Implement Add Project flow** | Completes the `@Completeflow` end-to-end scenario | High |
| 2 | **Add API testing support** (Playwright `APIRequestContext`) | Reuse frameworks' API for OTP & data setup | Medium |
| 3 | **Allure reporter integration** | Alternative rich reporting | Low |
| 4 | **Cross-environment config** (dev/staging/prod profiles) | Reusability | Medium |
| 5 | **Docker / CI execution** | Parity with existing GitHub Actions sample | Medium |
| 6 | **Retry on failure** | Flakiness reduction | Low |
| 7 | **Parallel execution tuning** | Faster suite (increase `thread-count`) | Low |
| 8 | **Test data factory** (randomized data) | Independent test data | Medium |

---

## 🎯 Next Recommended Step

**Implement the Add Project feature.** Use the guide in [`ADDING_FEATURES.md`](ADDING_FEATURES.md):
1. Create `AddProjectPage`, `AddProjectSteps`
2. Register page in `PageManager`
3. Add `AddProject` Excel sheet
4. Update `TestRunner` tags to include `@addproject`
5. Use **Playwright MCP** (see `MCP_SETUP.md`) to inspect the Projects UI and capture stable selectors.

---

## 📚 Related Docs
- [ADDING_FEATURES.md](ADDING_FEATURES.md)
- [MCP_SETUP.md](MCP_SETUP.md)
- [ARCHITECTURE.md](ARCHITECTURE.md)
