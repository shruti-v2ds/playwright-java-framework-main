package Hooks;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import framework.core.DriverFactory;
import framework.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * TestContext provides scenario-level data storage and browser resource access.
 * 
 * Features:
 * - Single-user mode: Traditional page/context access via DriverFactory
 * - Multi-user mode: Per-user page/context via SessionManager
 * - Generic data storage for scenario-specific values
 * 
 * Usage:
 *   // Single user (backward compatible)
 *   Page page = testContext.getPage();
 *   
 *   // Multi-user
 *   testContext.setCurrentUserId("USER001");
 *   Page page = testContext.getPage();  // Gets USER001's page
 */
public class TestContext {

    private final Map<String, Object> scenarioData = new HashMap<>();
    private String currentUserId;

    /**
     * Gets the current page.
     * In multi-user mode, returns the current user's page.
     * In single-user mode, returns the shared page.
     * 
     * @return Current Page object
     */
    public Page getPage() {
        // Try multi-user mode first
        SessionManager manager = DriverFactory.getSessionManager();
        if (manager != null && currentUserId != null) {
            Page userPage = manager.getPage(currentUserId);
            if (userPage != null) {
                return userPage;
            }
        }
        
        // Fall back to single-user mode
        return DriverFactory.getPage();
    }

    /**
     * Gets the current browser context.
     * In multi-user mode, returns the current user's context.
     * In single-user mode, returns the shared context.
     * 
     * @return Current BrowserContext object
     */
    public BrowserContext getBrowserContext() {
        // Try multi-user mode first
        SessionManager manager = DriverFactory.getSessionManager();
        if (manager != null && currentUserId != null) {
            BrowserContext userContext = manager.getContext(currentUserId);
            if (userContext != null) {
                return userContext;
            }
        }
        
        // Fall back to single-user mode
        return DriverFactory.getContext();
    }

    /**
     * Gets the current UserID (for multi-user scenarios).
     * 
     * @return Current UserID, or null if not in multi-user mode
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Sets the current UserID for multi-user scenario tracking.
     * 
     * @param userId The UserID to set as current
     */
    public void setCurrentUserId(String userId) {
        this.currentUserId = userId;
        System.out.println("[TestContext] Current user set to: " + userId);
    }

    /**
     * Stores a key-value pair in scenario data.
     * 
     * @param key The key
     * @param value The value
     */
    public void set(String key, Object value) {
        scenarioData.put(key, value);
    }

    /**
     * Retrieves a value from scenario data.
     * 
     * @param key The key
     * @return The value, or null if not found
     */
    public Object get(String key) {
        return scenarioData.get(key);
    }

    /**
     * Retrieves a value from scenario data with type casting.
     * 
     * @param key The key
     * @param clazz The target class for casting
     * @param <T> The type parameter
     * @return The value cast to the specified type
     */
    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(scenarioData.get(key));
    }

    /**
     * Checks if a key exists in scenario data.
     * 
     * @param key The key to check
     * @return true if key exists, false otherwise
     */
    public boolean contains(String key) {
        return scenarioData.containsKey(key);
    }

    /**
     * Removes a key from scenario data.
     * 
     * @param key The key to remove
     */
    public void remove(String key) {
        scenarioData.remove(key);
    }

    /**
     * Clears all scenario data.
     */
    public void clear() {
        scenarioData.clear();
        currentUserId = null;
    }
}