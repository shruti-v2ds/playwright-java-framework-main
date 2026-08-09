package framework.core;

import com.microsoft.playwright.*;
import framework.config.ConfigManager;
import framework.config.FrameworkConfig;
import framework.utils.SessionManager;

/**
 * DriverFactory manages browser instances and session contexts.
 * 
 * Features:
 * - Single-user mode: Traditional ThreadLocal pattern (backward compatible)
 * - Multi-user mode: SessionManager for user isolation and session reuse
 * 
 * Usage:
 *   // Single user (backward compatible)
 *   DriverFactory.initBrowser("chromium");
 *   Page page = DriverFactory.getPage();
 *   
 *   // Multi-user (new)
 *   DriverFactory.initSessionManager();
 *   DriverFactory.createNewUserSession("USER001");
 *   DriverFactory.switchUser("USER002", "9876543210");
 *   DriverFactory.switchUser("USER001", "9876543210");  // Reuse existing session
 */
public final class DriverFactory {

    private DriverFactory() {
        throw new IllegalStateException("DriverFactory utility class");
    }

    // Single-user mode (backward compatible)
    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
    
    // Multi-user mode
    private static final ThreadLocal<SessionManager> SESSION_MANAGER = new ThreadLocal<>();

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
    
    // ===== MULTI-USER SESSION MANAGEMENT =====
    
    /**
     * Initializes the SessionManager for multi-user testing.
     * Call this at the start of a multi-user test run.
     */
    public static void initSessionManager() {
        if (SESSION_MANAGER.get() == null) {
            SESSION_MANAGER.set(new SessionManager());
            System.out.println("[DriverFactory] SessionManager initialized");
        }
    }
    
    /**
     * Gets the current SessionManager instance.
     * Returns null if SessionManager not initialized.
     * 
     * @return SessionManager or null
     */
    public static SessionManager getSessionManager() {
        return SESSION_MANAGER.get();
    }
    
    /**
     * Creates a new browser context and page for a user.
     * Registers the session with SessionManager.
     * Call after user login is complete.
     * 
     * @param userId The UserID to create session for
     * @return The Page object for this user
     */
    public static Page createNewUserSession(String userId) {
        SessionManager manager = getSessionManager();
        if (manager == null) {
            throw new IllegalStateException("SessionManager not initialized. Call initSessionManager() first.");
        }
        
        // Get the browser from single-user context
        Browser browser = BROWSER.get();
        if (browser == null) {
            throw new IllegalStateException("Browser not initialized. Call initBrowser() first.");
        }
        
        // Create new context for this user
        BrowserContext context = browser.newContext(
            new Browser.NewContextOptions()
                .setViewportSize(null)
        );
        
        Page page = context.newPage();
        
        // Register with SessionManager
        manager.registerUserSession(userId, context, page);
        
        System.out.println("[DriverFactory] Created new session for user: " + userId);
        
        return page;
    }
    
    /**
     * Switches to a user's session.
     * If user already has a session, reuses it.
     * If user is new, caller should create session after login.
     * 
     * @param userId The UserID to switch to
     * @param mobile The mobile number (for reference)
     */
    public static void switchUser(String userId, String mobile) {
        SessionManager manager = getSessionManager();
        if (manager == null) {
            throw new IllegalStateException("SessionManager not initialized. Call initSessionManager() first.");
        }
        
        BrowserContext context = manager.switchToUser(userId, mobile);
        
        if (context != null) {
            // Update thread-local references to this user's resources
            Page page = manager.getPage(userId);
            CONTEXT.set(context);
            PAGE.set(page);
            System.out.println("[DriverFactory] Switched to user: " + userId);
        } else {
            // User session doesn't exist yet
            System.out.println("[DriverFactory] User session doesn't exist yet: " + userId + 
                             ". Create session after login.");
        }
    }
    
    /**
     * Gets the current user's SessionManager-managed Page.
     * Prefers SessionManager page if available, falls back to single-user ThreadLocal.
     * 
     * @return Page object
     */
    public static Page getCurrentUserPage() {
        SessionManager manager = SESSION_MANAGER.get();
        if (manager != null) {
            Page page = manager.getCurrentPage();
            if (page != null) {
                return page;
            }
        }
        return PAGE.get();
    }
    
    /**
     * Gets a specific user's Page from SessionManager.
     * 
     * @param userId The UserID
     * @return Page for that user, or null if not logged in
     */
    public static Page getUserPage(String userId) {
        SessionManager manager = SESSION_MANAGER.get();
        if (manager != null) {
            return manager.getPage(userId);
        }
        return null;
    }
    
    /**
     * Logs out a user and closes their session.
     * 
     * @param userId The UserID to logout
     */
    public static void logoutUser(String userId) {
        SessionManager manager = SESSION_MANAGER.get();
        if (manager != null) {
            manager.logoutUser(userId);
            System.out.println("[DriverFactory] Logged out user: " + userId);
        }
    }
    
    /**
     * Clears all user sessions.
     */
    public static void clearAllUserSessions() {
        SessionManager manager = SESSION_MANAGER.get();
        if (manager != null) {
            manager.clearAllSessions();
        }
    }
    
    /**
     * Closes everything: all user sessions and single-user browser.
     */
    public static void closeAll() {
        // Close all user sessions first
        SessionManager manager = SESSION_MANAGER.get();
        if (manager != null) {
            manager.clearAllSessions();
            SESSION_MANAGER.remove();
        }
        
        // Then close single-user browser
        closeBrowser();
    }
}