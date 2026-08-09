package framework.pages;

import com.microsoft.playwright.Page;

public class HomePage extends BasePage {

    private final String searchInput = "input[placeholder*='Search'], input[type='search']";
    private final String findArchitectsButton = "button:has-text('Find'), button:has-text('Search')";
    private final String headerText = "text=Verified Architects";

    public HomePage(Page page) {
        super(page);
    }

    @Override
    public void isPageLoaded() {
        waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    public void navigate(String url) {
        navigateTo(url);
        isPageLoaded();
    }

    public void searchForArchitects(String location) {
        if (isVisible(searchInput)) {
            type(searchInput, location);
            if (isVisible(findArchitectsButton)) {
                click(findArchitectsButton);
            } else {
                pressKeyboard("Enter");
            }
        }
    }

    public String getPageHeader() {
        return getPageTitle();
    }

}
