# 🔧 Environment & Setup Guide

Follow these steps to set up the **Playwright Java Test Automation Framework** locally.

---

## 1. Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| **Java** | 21+ | Language runtime |
| **Maven** | 3.8+ | Build & dependency management |
| **Node.js** | 18+ (recommended) | Required only for Playwright MCP / CLI tooling |
| **Git** | optional | Version control |

### Verify installation
```bash
# PowerShell
java -version
mvn -version
node --version
npm --version
```

---

## 2. Clone / Open the Project

```bash
git clone <your-repo-url>
cd playwright-java-framework-main
```

Open the folder in **IntelliJ IDEA** or **VS Code**:
- **IntelliJ**: `File → Open` → select `pom.xml` → *Import as Maven project*.
- **VS Code**: Install the **Java Extension Pack** and **Maven for Java** extensions, then open the folder.

---

## 3. Install Playwright Browsers

Playwright needs browser binaries downloaded before tests can run:

```bash
mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
```

> On some systems you may need admin privileges. See [Playwright docs](https://playwright.dev/java/docs/intro).

---

## 4. Configure Runtime Settings

Edit `src/test/resources/config.properties`:

```properties
browser=chromium        # chromium | firefox | webkit
baseUrl=https://dialinarch.com/
headless=false          # true = run without UI
defaultTimeout=30000
otpApiBaseUrl=https://app-backend-a8h9.onrender.com
testMobile=1234567890
```

You can override any key at runtime:
```bash
mvn test -Dbrowser=firefox -Dheadless=true -DbaseUrl=https://staging.dialinarch.com/
```

---

## 5. Prepare Test Data

The framework reads data from Excel. A generator is provided:

```bash
mvn compile exec:java -Dexec.mainClass=framework.utils.TestDataGenerator
```

This creates/updates `src/test/resources/testdata/registration_testdata.xlsx`.
Otherwise, keep the sheets (`RegistrationData`, `HostYourBusiness`) consistent with the columns used in the step definitions.

---

## 6. Run the Tests

```bash
# Full default run (tags set in TestRunner -> @Completeflow)
mvn clean test

# Specific browser
mvn clean test -Dbrowser=firefox

# Headless
mvn clean test -Dheadless=true
```

To change which scenarios run, edit the `tags` in `src/test/java/runners/TestRunner.java`:

```java
tags = "@login or @home or @search or @signup or @registration or @hostbusiness or @Completeflow",
```

---

## 7. (Optional) Set Up Playwright MCP

To drive the browser interactively via MCP (e.g. for debugging/new-feature development), follow [`docs/MCP_SETUP.md`](MCP_SETUP.md).

---

## 8. Running in a Specific IDE

### IntelliJ IDEA
1. Ensure the Maven tool window is loaded.
2. Run `TestRunner` (testng.xml) via the Maven `surefire` plugin or a `TestNG` run configuration pointing at `testng.xml`.
3. To run Cucumber with tags, edit the runner and run it.

### VS Code
1. Install **Test Runner for Java**.
2. Run from the Maven panel (`mvn test`) or via a launch configuration.

---

## 🛠️ Troubleshooting

| Problem | Suggestion |
|---------|-----------|
| `Executable doesn't exist` | Run the Playwright browser install command (step 3). |
| Browser won't launch | Set `headless=true` or check browser binaries. |
| Port / certificate issues | Ensure network access; check `baseUrl`. |
| OTP not fetched | Confirm `otpApiBaseUrl` is reachable & correct `purpose`. |
| `symbol not found` on compile | Run `mvn clean compile` to refresh dependencies. |

---

## 📚 Related Docs
- [ARCHITECTURE.md](ARCHITECTURE.md)
- [CONTRIBUTING.md](CONTRIBUTING.md)
- [MCP_SETUP.md](MCP_SETUP.md)

