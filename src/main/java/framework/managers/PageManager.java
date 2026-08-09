package framework.managers;

import com.microsoft.playwright.Page;
import framework.pages.AddProjectPage;
import framework.pages.HomePage;
import framework.pages.HostYourBusinessPage;
import framework.pages.LoginPage;
import framework.pages.SignUpPage;

import java.util.Objects;

public class PageManager {
    private final Page page;
    private LoginPage loginPage;
    private HomePage homePage;
    private SignUpPage signUpPage;
    private HostYourBusinessPage hostYourBusinessPage;
    private AddProjectPage addProjectPage;

    public PageManager(Page page) {
        this.page = page;
    }

    public LoginPage loginPage() {
        return Objects.requireNonNullElseGet(loginPage, () -> loginPage = new LoginPage(page));
    }

    public HomePage homePage() {
        return Objects.requireNonNullElseGet(homePage, () -> homePage = new HomePage(page));
    }

    public SignUpPage signUpPage() {
        return Objects.requireNonNullElseGet(signUpPage, () -> signUpPage = new SignUpPage(page));
    }

    public HostYourBusinessPage hostYourBusinessPage() {
        return Objects.requireNonNullElseGet(hostYourBusinessPage,
                () -> hostYourBusinessPage = new HostYourBusinessPage(page));
    }

    public AddProjectPage addProjectPage() {
        return Objects.requireNonNullElseGet(addProjectPage,
                () -> addProjectPage = new AddProjectPage(page));
    }
}

