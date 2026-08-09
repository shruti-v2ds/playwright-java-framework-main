# 🏛️ Framework Architecture

This document describes the design, package structure, and execution flow of the **Playwright Java Test Automation Framework** for DialinArch.

---

## 🎯 Design Principles

- **Page Object Model (POM)** — UI pages are represented as classes; locators and actions are encapsulated.
- **Thread Safety** — `ThreadLocal`-based browser instances enable parallel execution.
- **BDD (Cucumber)** — Scenarios are written in Gherkin `.feature` files, mapped to Java step definitions.
- **Config-Driven** — All runtime settings come from `config.properties` via the **Owner** library.
- **Data-Driven** — Test data is externalized into Excel (`.xlsx`) files.

---

## 🗂️ Package Layout

```
src/
├── main/java/framework/
│   ├── config/          # ConfigManager, FrameworkConfig (Owner interface)
│   ├── core/            # DriverFactory (Playwright lifecycle)
│   ├── pages/           # BasePage + concrete page objects
│   ├── managers/        # PageManager (page object factory)
│   └── utils/           # Waits, Excel, OTP, Reports, Screenshots, Listener
└── test/
    ├── java/
    │   ├── Hooks/       # Cucumber hooks + TestContext managers
    │   ├── runners/     # TestRunner (TestNG-Cucumber)
    │   └── steps/       # Step definitions
    └── resources/
        ├── config.properties
        ├── features/    # .feature files
        └── testdata/    # .xlsx data files
```

---

## 🧩 Key Components

### 1. Config Layer (`framework.config`)
- **`FrameworkConfig`** — an **Owner** interface defining typed config keys with defaults.
- **`ConfigManager`** — a singleton that loads the config from `config.properties`, system properties, and environment variables (merge strategy).

### 2. Core Layer (`framework.core`)
- **`DriverFactory`** — manages the Playwright lifecycle:
  - `initBrowser(browserName)` — creates `Playwright`, `Browser`, `BrowserContext`, `Page`.
  - `getPage()` / `getContext()` / `getBrowser()` — accessors.
  - `closeBrowser()` — cleanly tears everything down.
  - Uses `ThreadLocal` so each parallel thread gets its own browser instance.

### 3. Page Layer (`framework.pages`)
- **`BasePage`** — abstract class with reusable, generic actions:
  - Navigation, clicking, typing, selects, waits, frames, tabs, screenshots, uploads, downloads, alerts, JS execution, cookies, network waits.
- **Concrete pages** — `HomePage`, `LoginPage`, `SignUpPage`, `HostYourBusinessPage`. Each defines its own locators and high-level business methods, and implements `isPageLoaded()`.

### 4. Manager Layer (`framework.managers`)
- **`PageManager`** — lazy factory that instantiates and caches page objects, so step definitions always get the correct page instance bound to the current `Page`.

### 5. Utils Layer (`framework.utils`)
- **`WaitUtil`** — rich waiting utilities (selectors, locators, URLs, text, attributes, network, loaders, retries).
- **`ExcelUtil`** — reads Excel rows into `List<Map<String, String>>`.
- **`OtpApiClient`** — fetches OTP from the backend API.
- **`ExtentReportUtil`** — manages Extent Reports lifecycle and inline screenshots.
- **`ScreenshotUtil`** — captures and base64-encodes screenshots.
- **`TestListener`** — TestNG listener that drives report creation on test start/success/failure/finish.
- **`TestDataGenerator`** — generates the sample `.xlsx` data file.

### 6. Test Layer (`src/test`)
- **`Hooks`** — `@Before`/`@After` Cucumber hooks: browser init, tracing start/stop, failure screenshot, teardown.
- **`TestContext` / `TestContextManager`** — thread-local storage for sharing scenario data.
- **`TestRunner`** — TestNG-Cucumber runner; configures features, glue, tags, and plugins (reports).
- **`steps/*`** — step definitions that compose page-object methods (e.g., `LoginSteps`, `SignUpSteps`, `HomeSteps`, `HostBusinessSteps`).

---

## 🔁 Execution Flow

```
testng.xml
 └─> TestRunner (AbstractTestNGCucumberTests)
      └─> Hooks @Before
            ├─ DriverFactory.initBrowser(config.browser())
            └─ start tracing
      └─> Cucumber .feature scenarios
            └─> Step Definitions (steps/*)
                  └─> PageManager -> Page Objects
                        └─> BasePage actions on Playwright Page
      └─> Hooks @After
            ├─ capture failure screenshot (if failed)
            ├─ stop & save trace
            └─ DriverFactory.closeBrowser()
      └─> TestListener -> ExtentReportUtil (report generation)
```

---

## 🔐 OTP Flow (Real vs Mock)

- **Real OTP**: `OtpApiClient.fetchOtp(mobile, purpose)` calls the backend `get-test-otp` API and parses the OTP from JSON.
- **Mock OTP**: In demo environments, `enterMockOtp()` / `enterMockOTP()` type `0000` into the OTP fields directly.

---

## 🧪 Parallel Execution

- `testng.xml` sets `parallel="methods"` and `thread-count="1"`.
- `DriverFactory` uses `ThreadLocal` so each method/thread gets an isolated browser context — safe to increase `thread-count` when needed.

---

## 📎 Related Docs
- [SETUP.md](SETUP.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [ADDING_FEATURES.md](ADDING_FEATURES.md)
- [REPORTING.md](REPORTING.md)
- [MCP_SETUP.md](MCP_SETUP.md)
