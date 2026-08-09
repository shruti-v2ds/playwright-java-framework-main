# 📊 Reporting Guide

This framework produces multiple reports and artifacts after each test run.

---

## 📁 Report Output Locations

| Artifact | Path |
|----------|------|
| **Extent HTML Report** | `reports/YYYYMMDD_HHMMSS/TestReport.html` |
| **Cucumber HTML** | `target/cucumber-reports/cucumber.html` |
| **Cucumber JSON** | `target/cucumber-reports/cucumber.json` |
| **Cucumber JUnit XML** | `target/cucumber-reports/cucumber.xml` |
| **Playwright Traces** | `target/traces/*.zip` |
| **Failure Screenshots** | Attached inline to Extent & Cucumber reports |

> The `reports/` and `target/` folders are git-ignored.

---

## 🌟 Extent Reports

Extent Reports is configured in two places:

### 1. `framework/utils/ExtentReportUtil`
- `initExtentReports(baseReportPath)` — creates a timestamped `ExtentSparkReporter`.
- `createTest(testName)` — starts a test node (thread-local).
- `attachScreenshot(page, message)` — embeds a base64 screenshot with a log message.
- `flushReports()` — writes the report to disk.

### 2. `framework/utils/TestListener`
Drives report lifecycle from TestNG events:
- `onStart` → init reports
- `onTestStart` → create test
- `onTestSuccess` / `onTestFailure` / `onTestSkipped` → log status
- `onFinish` → flush

---

## 📸 Screenshots

- `ScreenshotUtil.takeScreenshot(page)` returns base64.
- `ExtentReportUtil.attachScreenshot(page, msg)` embeds it in the report.
- Page classes call these after key actions (e.g., after login, after filling forms).

### Failure screenshots
In `Hooks.captureFailureScreenshot`, if a scenario fails, a full-page screenshot is attached to the Cucumber scenario.

---

## 🧾 Playwright Traces

In `Hooks.saveTrace`, a trace is saved for every scenario under `target/traces/<scenario>.zip`.

To view a trace:
```bash
npx playwright show-trace target/traces/<scenario>.zip
```

> Useful for debugging interactions step-by-step (screenshots, snapshots, network).

---

## 🥒 Cucumber Plugins (in `TestRunner`)

```java
plugin = {
    "pretty",
    "summary",
    "json:target/cucumber-reports/cucumber.json",
    "html:target/cucumber-reports/cucumber.html",
    "junit:target/cucumber-reports/cucumber.xml",
    "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
}
```

---

## 🌐 CI / GitHub Pages

Passing scenarios can be published. A sample GitHub Actions workflow is included in the original `README.md`. It:
1. Sets up Java + Maven.
2. Installs Playwright browsers.
3. Runs `mvn clean test`.
4. Copies `reports/*/*.html` and screenshots to `gh-pages/`.
5. Deploys via `actions/deploy-pages`.

---

## 📚 Related Docs
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [SETUP.md](SETUP.md)
