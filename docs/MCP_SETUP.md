# 🎭 Playwright MCP Setup

**Playwright MCP (Model Context Protocol)** lets you and AI assistants **drive the browser interactively** — navigate, click, inspect elements, capture screenshots, and debug — which is extremely useful when **building new features** in this framework.

---

## ✅ What This Gives You

- Open a real browser and navigate to the app.
- Inspect page elements / selectors in real time.
- Capture screenshots to identify stable locators.
- Verify flows manually before writing automation.
- Help AI assistants (like this one) drive the browser to inspect the DialinArch UI.

---

## 🔧 How It Works

The project includes a `mcp.json` at the root that registers the official **`@playwright/mcp`** server:

```json
{
    "servers": {
        "playwright": {
      "command": "C:\\Program Files\\nodejs\\npx.cmd",
      "args": ["@playwright/mcp@latest"],
      "env": { "BROWSER": "chromium" }
    }
    }
}
```

> Requires **Node.js 18+** and `npx` on your `PATH`.

---

## 🚀 Enabling in VS Code

1. Install the **Claude Desktop / MCP client** or use **VS Code Copilot** (that supports MCP).
2. Ensure the `.mcp.json` is present at the project root.
3. In VS Code, the MCP server is auto-discovered from `.mcp.json` (supported by recent Copilot versions). If not, add it manually:
   - **VS Code**: `Settings → MCP` or via the Copilot MCP manager → add the server with the command above.

---

## 🛠️ Manual Launch (any client)

```bash
npx @playwright/mcp@latest
```

You can also launch with a specific browser or headed mode:
```bash
npx @playwright/mcp@latest --browser chromium --headless false
```

---

## 🧪 Using It to Inspect the DialinArch UI

Once connected, you can ask the MCP-connected assistant to:

1. `browser_navigate` → `https://dialinarch.com/`
2. `browser_snapshot` / `browser_screenshot` → see the current UI
3. Locate elements and capture **stable selectors** to use in your Page Objects
4. Test OTP flows, registration, hosting, etc., manually before scripting

---

## 🧩 Using It With This Java Framework

- Use MCP to **explore** the UI and capture locators.
- Copy stable selectors into your `XxxPage` locator strings.
- Verify the flow end-to-end in the browser, then implement step definitions.
- Debug selectors that are flaky in your Java tests.

---

## ⚠️ Notes

- MCP drives its **own browser** instance — it is independent of the Java framework's `DriverFactory`.
- Do **not** run Java tests and MCP browser interactions on the same session simultaneously if they conflict.
- The `.mcp.json` is client-agnostic; some clients require the server to be started manually.

---

## 📚 Related Docs
- [ADDING_FEATURES.md](ADDING_FEATURES.md) — use MCP to inspect before writing page objects
- [SETUP.md](SETUP.md)
- [FEATURES_ROADMAP.md](FEATURES_ROADMAP.md)
