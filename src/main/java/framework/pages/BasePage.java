package framework.pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import framework.utils.WaitUtil;

import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public abstract void isPageLoaded();

    // =====================================================
    // Navigation
    // =====================================================

    public void navigateTo(String url) {
        page.navigate(url);
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public String getPageTitle() {
        return page.title();
    }

    public void refreshPage() {
        page.reload();
    }

    public void goBack() {
        page.goBack();
    }

    public void goForward() {
        page.goForward();
    }

    // =====================================================
    // Click Actions
    // =====================================================

    public void click(String selector) {
        waitForSelectorVisible(selector);
        page.click(selector);
    }

    public void doubleClick(String selector) {
        page.dblclick(selector);
    }

    public void rightClick(String selector) {
        page.click(selector,
                new Page.ClickOptions().setButton(MouseButton.RIGHT));
    }

    public void forceClick(String selector) {
        page.click(selector,
                new Page.ClickOptions().setForce(true));
    }

    // =====================================================
    // Input Actions
    // =====================================================

    public void type(String selector, String text) {
        waitForSelectorVisible(selector);
        page.fill(selector, text);
    }

    public void appendText(String selector, String text) {
        page.locator(selector).fill(text);
    }

    public void clearText(String selector) {
        page.fill(selector, "");
    }

    public void pressKey(String selector, String key) {
        page.locator(selector).press(key);
    }

    public void pressKeyboard(String key) {
        page.keyboard().press(key);
    }

    // =====================================================
    // Text & Attributes
    // =====================================================

    public String getText(String selector) {
        return page.textContent(selector);
    }

    public String getInnerText(String selector) {
        return page.locator(selector).innerText();
    }

    public String getAttribute(String selector, String attribute) {
        return page.getAttribute(selector, attribute);
    }

    public String getInputValue(String selector) {
        return page.inputValue(selector);
    }

    // =====================================================
    // State Checks
    // =====================================================

    public boolean isVisible(String selector) {
        return page.isVisible(selector);
    }

    public boolean isHidden(String selector) {
        return page.isHidden(selector);
    }

    public boolean isEnabled(String selector) {
        return page.isEnabled(selector);
    }

    public boolean isDisabled(String selector) {
        return page.isDisabled(selector);
    }

    public boolean isChecked(String selector) {
        return page.isChecked(selector);
    }

    // =====================================================
    // Checkbox / Radio
    // =====================================================

    public void check(String selector) {
        page.check(selector);
    }

    public void uncheck(String selector) {
        page.uncheck(selector);
    }

    // =====================================================
    // Dropdowns
    // =====================================================

    public void selectByValue(String selector, String value) {
        page.selectOption(selector, value);
    }

    public void selectByLabel(String selector, String label) {
        page.selectOption(selector,
                new SelectOption().setLabel(label));
    }

    public void selectByIndex(String selector, int index) {
        page.selectOption(selector,
                new SelectOption().setIndex(index));
    }

    // =====================================================
    // Hover & Mouse
    // =====================================================

    public void hover(String selector) {
        page.hover(selector);
    }

    public void dragAndDrop(String source, String target) {
        page.dragAndDrop(source, target);
    }

    // =====================================================
    // Scroll
    // =====================================================

    public void scrollIntoView(String selector) {
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    public void scrollToTop() {
        page.evaluate("window.scrollTo(0,0)");
    }

    public void scrollToBottom() {
        page.evaluate("window.scrollTo(0,document.body.scrollHeight)");
    }

    // =====================================================
    // Waits
    // =====================================================

    public void waitForSelectorVisible(String selector) {
        WaitUtil.waitForSelector(
                page,
                selector,
                WaitForSelectorState.VISIBLE
        );
    }

    public void waitForSelectorHidden(String selector) {
        WaitUtil.waitForSelector(
                page,
                selector,
                WaitForSelectorState.HIDDEN
        );
    }

    public void waitForURL(String urlPattern) {
        WaitUtil.waitForURL(page, urlPattern);
    }

    public void waitForLoadState(LoadState state) {
        WaitUtil.waitForLoad(page, state);
    }

    public void waitForTextInLocator(
            Locator locator,
            String expectedText) {

        WaitUtil.waitForTextInLocator(locator, expectedText);
    }

    public void waitUntil(
            java.util.function.Supplier<Boolean> condition,
            String failureMessage) {

        WaitUtil.waitUntil(condition, failureMessage);
    }

    // =====================================================
    // Frames
    // =====================================================

    public Frame getFrame(String frameName) {
        return page.frame(frameName);
    }

    // =====================================================
    // Tabs / Windows
    // =====================================================

    public Page waitForNewTab() {
        return page.context()
                .waitForPage(() -> {
                });
    }

    // =====================================================
    // Screenshot
    // =====================================================

    public void takeScreenshot(String path) {
        page.screenshot(
                new Page.ScreenshotOptions()
                        .setPath(Path.of(path))
                        .setFullPage(true));
    }

    public String takeScreenshot() {
        byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        return Base64.getEncoder().encodeToString(screenshot);
    }

    // =====================================================
    // File Upload
    // =====================================================

    public void uploadFile(String selector, String filePath) {
        page.setInputFiles(selector, Path.of(filePath));
    }

    // =====================================================
    // Download
    // =====================================================

    public Download waitForDownload(Runnable action) {

        return page.waitForDownload(action::run);
    }

    // =====================================================
    // Alerts
    // =====================================================

    public void acceptAlert() {
        page.onceDialog(Dialog::accept);
    }

    public void dismissAlert() {
        page.onceDialog(Dialog::dismiss);
    }

    // =====================================================
    // JavaScript Executor
    // =====================================================

    public Object executeJS(String script) {
        return page.evaluate(script);
    }

    public Object executeJS(String script, Object arg) {
        return page.evaluate(script, arg);
    }

    // =====================================================
    // Locator Helpers
    // =====================================================

    public Locator getLocator(String selector) {
        return page.locator(selector);
    }

    public List<String> getAllTexts(String selector) {
        return page.locator(selector).allTextContents();
    }

    public int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    // =====================================================
    // Browser Storage
    // =====================================================

    public void clearLocalStorage() {
        page.evaluate("localStorage.clear()");
    }

    public void clearSessionStorage() {
        page.evaluate("sessionStorage.clear()");
    }

    // =====================================================
    // Cookies
    // =====================================================

    public void clearCookies() {
        page.context().clearCookies();
    }

    // =====================================================
    // Network
    // =====================================================

    public Response waitForResponse(
            String urlPattern,
            Runnable action) {

        return page.waitForResponse(
                response -> response.url().contains(urlPattern),
                action::run);
    }

    public Request waitForRequest(
            String urlPattern,
            Runnable action) {

        return page.waitForRequest(
                request -> request.url().contains(urlPattern),
                action::run);
    }
}

