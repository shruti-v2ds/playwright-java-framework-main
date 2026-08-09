# 🤝 Contributing Guidelines

Thank you for contributing to the **Playwright Java Test Automation Framework**! This guide explains how to add code, tests, and features in a consistent way.

---

## 📌 Coding Standards

- **Language**: Java 21
- **Formatting**: 4-space indentation, braces on new lines.
- **Naming**:
  - Pages: `XxxPage` (e.g. `LoginPage`)
  - Steps: `XxxSteps` (e.g. `LoginSteps`)
  - Utils: `XxxUtil` (e.g. `WaitUtil`)
  - Test data fields: camelCase or as header keys in Excel.
- **Visibility**: locators are `private final String`; business methods are `public`.

---

## 🗺️ How a Feature Is Structured

Every feature touches these files:

1. **Page Object** — `src/main/java/framework/pages/XxxPage.java`
2. **Step Definitions** — `src/test/java/steps/XxxSteps.java`
3. **Feature File** — `src/test/resources/features/xxx.feature`
4. **PageManager registration** — `src/main/java/framework/managers/PageManager.java`
5. **(Optional) Excel data** — `src/test/resources/testdata/*.xlsx`

---

## ✍️ Writing a Page Object

1. `extends BasePage` and add a `Page` constructor.
2. Override `isPageLoaded()`.
3. Define `private final String` locators.
4. Expose **business-meaningful** public methods that call `BasePage` actions.

```java
public class LoginPage extends BasePage {

    private final String loginLinkSelector = "a:has-text('Login'), a:has-text('Sign in')";

    public LoginPage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void navigateToLogin() {
        if (isVisible(loginLinkSelector)) {
            click(loginLinkSelector);
        }
        isPageLoaded();
    }
}
```

---

## ✍️ Writing Step Definitions

- Accept injected state via the framework (`DriverFactory.getPage()`).
- Use **TestNG `Assert`** for verification.
- Keep steps thin — delegate to page objects.

```java
public class LoginSteps {

    private PageManager getPageManager() {
        return new PageManager(DriverFactory.getPage());
    }

    @When("user navigates to login page")
    public void userNavigatesToLoginPage() {
        getPageManager().loginPage().navigateToLogin();
    }
}
```

---

## ✍️ Writing a Feature File

Keep Gherkin scenarios user-focused and tag them:

```gherkin
Feature: Login functionality

  @login
  Scenario: Successful login with OTP
    Given user is on dialinarch landing page
    When user logs in with OTP
    Then user should be logged in successfully
```

---

## 🔗 Register Pages in PageManager

Add a lazy accessor so steps can use the new page:

```java
private SignUpPage signUpPage;

public SignUpPage signUpPage() {
    return Objects.requireNonNullElseGet(signUpPage, () -> signUpPage = new SignUpPage(page));
}
```

---

## 🧪 Add Data (if needed)

- Columns must match the header keys used in step definitions.
- Use `ExcelUtil.getTestDataRow(file, sheet, index)`.
- (Re)generate or edit `src/test/resources/testdata/*.xlsx`.

---

## 🏷️ Tagging & Running

- Tag scenarios (`@login`, `@Completeflow`, etc.).
- Update `TestRunner` `tags` to run your new flow.
- Verify a single scenario before the full suite.

---

## ✅ Definition of Done

- [ ] Page object created & registered in `PageManager`
- [ ] Step definitions written and mapped to Gherkin steps
- [ ] Feature file added under `features/`
- [ ] Compiles cleanly: `mvn clean compile`
- [ ] Scenario passes when run with its tag
- [ ] No hard-coded URLs/values (use config / Excel data)
- [ ] Screenshots / reporting hooks already handle coverage

---

## 🚦 Pull Request Process

1. Create a branch (`blackboxai/feature-xxx`).
2. Make focused commits with clear messages.
3. Ensure `mvn clean compile` passes.
4. Open a PR referencing the feature.

---

## 📚 Related Docs
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [ADDING_FEATURES.md](ADDING_FEATURES.md)
- [SETUP.md](SETUP.md)

