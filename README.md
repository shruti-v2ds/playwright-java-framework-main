# Playwright Java Test Automation Framework

A robust, production-ready **Playwright + Java + Cucumber + TestNG** test automation framework for the **DialinArch** platform. It follows the Page Object Model (POM), supports Excel-driven test data, OTP-based authentication, Extent Reports, video/trace capture, and parallel execution.

---

## ✅ Features

| Feature | Description |
|---------|-------------|
| 🧩 **Page Object Model** | Modular page classes extending `BasePage` with reusable actions |
| 🥒 **Cucumber BDD** | Feature files + step definitions (`given/when/then`) |
| 🧪 **TestNG** | Test runner, listeners, and parallel-execution support |
| 🌐 **Multi-browser** | Chromium, Firefox, WebKit (switch via config) |
| 📊 **Extent Reports** | Rich HTML reports with inline screenshots |
| 📸 **Screenshots** | Automatic capture on failure + manual attachment |
| 🧾 **Traces** | Playwright trace files saved under `target/traces` |
| 📥 **Excel Data** | Apache POI for reading test data from `.xlsx` files |
| 🔐 **OTP Handling** | Real OTP fetch via API + mock OTP for demo environments |
| ⚙️ **Config Management** | Owner library — browser, URL, timeouts, credentials |
| 🧠 **Test Context** | Thread-safe scenario data sharing (`TestContext`) |
| 🔍 **Playwright MCP** | Drive the browser interactively via MCP for debugging |

---

## 🏗️ Current Implemented Flows

- **Home / Search** — Landing page verification, architect search
- **Login** — OTP-based login (real OTP via API, or mock)
- **Signup** — OTP-based registration
- **Registration** — Excel-driven registration form (Create Profile) **+ Skill Service Provider Registration** ⭐ NEW
- **Skill Service Registration** — Register service providers (Plumbers, Electricians, Carpenters, etc.) ⭐ NEW
- **Host Your Business** — Excel-driven multi-step business hosting flow
- **Add Project** *(planned / to be implemented)* — defined in `addproject.feature`

---

## 📁 Folder Structure

```
project-root/
├── pom.xml                         # Maven build & dependencies
├── testng.xml                      # TestNG suite configuration
├── README.md
├── .mcp.json                       # Playwright MCP server config
├── docs/
│   ├── ARCHITECTURE.md
│   ├── SETUP.md
│   ├── CONTRIBUTING.md
│   ├── REPORTING.md
│   ├── ADDING_FEATURES.md
│   ├── MCP_SETUP.md
│   └── FEATURES_ROADMAP.md
└── src/
    ├── main/java/framework/
    │   ├── config/                 # ConfigManager, FrameworkConfig
    │   ├── core/                   # DriverFactory (browser lifecycle)
    │   ├── pages/                  # Page Object Model classes
    │   ├── managers/               # PageManager factory
    │   └── utils/                  # WaitUtil, ExcelUtil, OTP, Reports, etc.
    └── test/
        ├── java/
        │   ├── Hooks/              # Cucumber setup/teardown, TestContext
        │   ├── runners/            # TestNG Cucumber runner
        │   └── steps/              # Step definitions
        └── resources/
            ├── config.properties   # Runtime configuration
            ├── features/           # .feature BDD scenarios
            └── testdata/           # .xlsx test data files
```

---

## 🧰 Technologies

- **Java 21**
- **Playwright Java 1.49.0**
- **Cucumber 7.20.1**
- **TestNG 7.10.2**
- **Owner 1.0.12** (config)
- **Apache POI 5.2.5** (Excel)
- **Extent Reports 5.1.2** + Cucumber 7 adapter
- **Maven** build

---

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Node.js 18+ (only for Playwright MCP / CLI tools)

### Install Playwright Browsers
```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

### Run Tests
```bash
# Run all scenarios tagged @Completeflow (default in TestRunner)
mvn clean test

# Specify browser
mvn clean test -Dbrowser=chromium
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=webkit

# Headless mode
mvn clean test -Dheadless=true
```

> The active `tags` are set inside `src/test/java/runners/TestRunner.java`. Change the tag to run a specific flow (e.g. `@login`, `@registration`, `@signup`, `@home`).

---

## 🌍 Configuration

Edit `src/test/resources/config.properties`:

```properties
browser=chromium
baseUrl=https://dialinarch.com/
headless=false
defaultTimeout=30000
otpApiBaseUrl=https://app-backend-a8h9.onrender.com
testMobile=1234567890
```

Override any value at runtime by passing `-Dkey=value`, e.g.:
```bash
mvn test -DbaseUrl="https://staging.dialinarch.com/" -Dbrowser=firefox
```

---

## 📊 Reports

After a run, reports are generated at:

- **Extent Report**: `reports/YYYYMMDD_HHMMSS/TestReport.html`
- **Cucumber HTML**: `target/cucumber-reports/cucumber.html`
- **Screenshots**: attached inline to Extent Report (base64)
- **Traces**: `target/traces/*.zip`

See `docs/REPORTING.md` for details.

---

## 🛠️ Adding a New Feature

Follow the documented guide in [`docs/ADDING_FEATURES.md`](docs/ADDING_FEATURES.md).
TL;DR — add a Page class, a `.feature` file, step definitions, register the page in `PageManager`, add Excel data, then update `TestRunner` tags.

---

## 📅 Roadmap

See [`docs/FEATURES_ROADMAP.md`](docs/FEATURES_ROADMAP.md) for planned & unimplemented features (e.g. **Add Project**).

---

## 🤝 Contribution

See [`docs/CONTRIBUTING.md`](docs/CONTRIBUTING.md).

---

## 📚 Documentation Index

| Doc | Purpose |
|-----|---------|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Design, packages, flow |
| [`docs/SETUP.md`](docs/SETUP.md) | Environment & IDE setup |
| [`docs/CONTRIBUTING.md`](docs/CONTRIBUTING.md) | Contribution guidelines |
| [`docs/REPORTING.md`](docs/REPORTING.md) | Reporting details |
| [`docs/ADDING_FEATURES.md`](docs/ADDING_FEATURES.md) | How to add new features |
| [`docs/MCP_SETUP.md`](docs/MCP_SETUP.md) | Playwright MCP usage |
| [`docs/FEATURES_ROADMAP.md`](docs/FEATURES_ROADMAP.md) | Roadmap & gaps |

Happy Testing 🚀

