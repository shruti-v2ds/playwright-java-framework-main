package framework.utils;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import framework.config.ConfigManager;
import framework.config.FrameworkConfig;

import java.util.function.Supplier;

public final class WaitUtil {

    private static final FrameworkConfig config = ConfigManager.getConfig();

    private static final int DEFAULT_TIMEOUT =
            config.defaultTimeout();

    private static final int POLLING_INTERVAL = 250;

    private WaitUtil() {
        throw new IllegalStateException("Utility class");
    }

    // =====================================================
    // Selector Waits
    // =====================================================

    public static void waitForSelector(
            Page page,
            String selector,
            WaitForSelectorState state) {

        page.waitForSelector(
                selector,
                new Page.WaitForSelectorOptions()
                        .setState(state)
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    public static void waitForVisible(
            Page page,
            String selector) {

        waitForSelector(
                page,
                selector,
                WaitForSelectorState.VISIBLE);
    }

    public static void waitForHidden(
            Page page,
            String selector) {

        waitForSelector(
                page,
                selector,
                WaitForSelectorState.HIDDEN);
    }

    public static void waitForAttached(
            Page page,
            String selector) {

        waitForSelector(
                page,
                selector,
                WaitForSelectorState.ATTACHED);
    }

    public static void waitForDetached(
            Page page,
            String selector) {

        waitForSelector(
                page,
                selector,
                WaitForSelectorState.DETACHED);
    }

    // =====================================================
    // Locator Waits
    // =====================================================

    public static void waitForLocatorVisible(
            Locator locator) {

        locator.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    public static void waitForLocatorHidden(
            Locator locator) {

        locator.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN)
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    public static void waitForLocatorAttached(
            Locator locator) {

        locator.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.ATTACHED)
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    // =====================================================
    // URL Waits
    // =====================================================

    public static void waitForURL(
            Page page,
            String urlPattern) {

        page.waitForURL(
                urlPattern,
                new Page.WaitForURLOptions()
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    public static void waitForUrlContains(
            Page page,
            String partialUrl) {

        waitUntil(
                () -> page.url().contains(partialUrl),
                "URL does not contain: " + partialUrl);
    }

    // =====================================================
    // Page Waits
    // =====================================================

    public static void waitForLoad(
            Page page,
            LoadState state) {

        page.waitForLoadState(
                state,
                new Page.WaitForLoadStateOptions()
                        .setTimeout(DEFAULT_TIMEOUT));
    }

    public static void waitForDomContentLoaded(
            Page page) {

        waitForLoad(page, LoadState.DOMCONTENTLOADED);
    }

    public static void waitForPageLoaded(
            Page page) {

        waitForLoad(page, LoadState.LOAD);
    }

    public static void waitForNetworkIdle(
            Page page) {

        waitForLoad(page, LoadState.NETWORKIDLE);
    }

    // =====================================================
    // Text Waits
    // =====================================================

    public static void waitForTextInLocator(
            Locator locator,
            String expectedText) {

        waitUntil(() -> {
            try {
                String actual = locator.textContent();

                return actual != null &&
                        actual.trim().contains(expectedText);

            } catch (Exception e) {
                return false;
            }
        }, "Text not found: " + expectedText);
    }

    public static void waitForExactText(
            Locator locator,
            String expectedText) {

        waitUntil(() -> {
            try {
                String actual = locator.textContent();

                return actual != null &&
                        actual.trim().equals(expectedText);

            } catch (Exception e) {
                return false;
            }
        }, "Expected text not found: " + expectedText);
    }

    // =====================================================
    // Attribute Waits
    // =====================================================

    public static void waitForAttribute(
            Locator locator,
            String attribute,
            String expectedValue) {

        waitUntil(() -> {
            try {
                String value =
                        locator.getAttribute(attribute);

                return expectedValue.equals(value);

            } catch (Exception e) {
                return false;
            }
        }, "Attribute mismatch");
    }

    // =====================================================
    // Element State Waits
    // =====================================================

    public static void waitForEnabled(
            Locator locator) {

        waitUntil(locator::isEnabled,
                "Element not enabled");
    }

    public static void waitForDisabled(
            Locator locator) {

        waitUntil(() -> !locator.isEnabled(),
                "Element not disabled");
    }

    public static void waitForChecked(
            Locator locator) {

        waitUntil(locator::isChecked,
                "Element not checked");
    }
    

    // =====================================================
    // Collection Waits
    // =====================================================

    public static void waitForElementsCount(
            Locator locator,
            int expectedCount) {

        waitUntil(() ->
                        locator.count() == expectedCount,
                "Expected count: " + expectedCount);
    }

    public static void waitForMinimumElements(
            Locator locator,
            int minimumCount) {

        waitUntil(() ->
                        locator.count() >= minimumCount,
                "Minimum count not reached");
    }

    // =====================================================
    // Network Waits
    // =====================================================

    public static Response waitForResponse(
            Page page,
            String urlPattern,
            Runnable action) {

        return page.waitForResponse(
                response ->
                        response.url().contains(urlPattern),
                action::run);
    }

    public static Request waitForRequest(
            Page page,
            String urlPattern,
            Runnable action) {

        return page.waitForRequest(
                request ->
                        request.url().contains(urlPattern),
                action::run);
    }

    public static Response waitForApiSuccess(
            Page page,
            String endpoint,
            Runnable action) {

        return page.waitForResponse(
                response ->
                        response.url().contains(endpoint)
                                && response.status() == 200,
                action::run);
    }

    // =====================================================
    // Download Waits
    // =====================================================

    public static Download waitForDownload(
            Page page,
            Runnable action) {

        return page.waitForDownload(action::run);
    }


    // =====================================================
    // Spinner / Loader Waits
    // =====================================================

    public static void waitForLoaderToDisappear(
            Page page,
            String loaderSelector) {

        waitForHidden(page, loaderSelector);
    }

    public static void waitForAjaxComplete(
            Page page) {

        page.waitForFunction(
                "() => window.jQuery ? jQuery.active === 0 : true");
    }

    // =====================================================
    // Generic Wait
    // =====================================================

    public static void waitUntil(
            Supplier<Boolean> condition,
            String failureMessage) {

        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start
                < DEFAULT_TIMEOUT) {

            try {
                if (condition.get()) {
                    return;
                }
            } catch (Exception ignored) {
            }

            sleep(POLLING_INTERVAL);
        }

        throw new RuntimeException(
                "Timeout waiting for condition: "
                        + failureMessage);
    }

    // =====================================================
    // Retry Utility
    // =====================================================

    public static void retry(
            Runnable action,
            int retries) {

        RuntimeException exception = null;

        for (int i = 0; i < retries; i++) {

            try {
                action.run();
                return;
            } catch (RuntimeException e) {
                exception = e;
                sleep(1000);
            }
        }

        throw exception;
    }

    // =====================================================
    // Sleep Wrapper
    // =====================================================

    public static void sleep(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}