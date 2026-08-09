package framework.core;

import com.microsoft.playwright.*;
import framework.config.ConfigManager;
import framework.config.FrameworkConfig;

public final class DriverFactory {

    private DriverFactory() {
        throw new IllegalStateException("DriverFactory utility class");
    }

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    public static void initBrowser(String browserName) {

        FrameworkConfig config = ConfigManager.getConfig();

        Playwright playwright = Playwright.create();
        PLAYWRIGHT.set(playwright);

        BrowserType browserType = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };

        Browser browser = browserType.launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(config.headless())
        );

        BROWSER.set(browser);

        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(null)
        );

        CONTEXT.set(context);

        Page page = context.newPage();
        PAGE.set(page);
    }

    public static Playwright getPlaywright() {
        return PLAYWRIGHT.get();
    }

    public static Browser getBrowser() {
        return BROWSER.get();
    }

    public static BrowserContext getContext() {
        return CONTEXT.get();
    }

    public static Page getPage() {
        return PAGE.get();
    }

    public static void closeBrowser() {

        try {
            if (PAGE.get() != null) {
                PAGE.get().close();
                PAGE.remove();
            }

            if (CONTEXT.get() != null) {
                CONTEXT.get().close();
                CONTEXT.remove();
            }

            if (BROWSER.get() != null) {
                BROWSER.get().close();
                BROWSER.remove();
            }

            if (PLAYWRIGHT.get() != null) {
                PLAYWRIGHT.get().close();
                PLAYWRIGHT.remove();
            }

        } catch (Exception e) {
            System.err.println("Error while closing browser: " + e.getMessage());
        }
    }
}