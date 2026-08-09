package framework.utils;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import java.util.*;

/**
 * Manages browser sessions for multi-user testing scenarios.
 * 
 * Supports:
 * 1. Session isolation per user (different users = different BrowserContexts)
 * 2. Session reuse for same user (one user can perform multiple actions in same session)
 * 3. Session cleanup and logout
 * 4. User switching within same test run
 * 
 * Usage:
 *   SessionManager manager = new SessionManager();
 *   manager.switchToUser("USER001", "9876543210");  // Login USER001
 *   // ... perform actions ...
 *   manager.switchToUser("USER002", "9234567890");  // New session for USER002
 *   // ... perform actions ...
 *   manager.logoutUser("USER001");                  // Cleanup USER001 session
 */
public class SessionManager {
    
    private final Map<String, BrowserContext> userContexts = new HashMap<>();
    private final Map<String, Page> userPages = new HashMap<>();
    private String currentUserId;
    
    /**
     * Switches to a specific user's session.
     * If the user already has an active session, returns that session.
     * If not, a new session must be created by the caller (e.g., via DriverFactory).
     * 
     * @param userId The UserID to switch to
     * @param mobile The mobile number (for reference/logging)
     * @return The BrowserContext for this user (may be null if not yet created)
     */
    public BrowserContext switchToUser(String userId, String mobile) {
        this.currentUserId = userId;
        BrowserContext context = userContexts.get(userId);
        
        if (context != null) {
            System.out.println("[SessionManager] Switched to existing session: " + userId);
        } else {
            System.out.println("[SessionManager] Switch requested for new user: " + userId + " (Mobile: " + mobile + ")");
        }
        
        return context;
    }
    
    /**
     * Registers a new user session (called after login is complete).
     * 
     * @param userId The UserID
     * @param context The BrowserContext for this user
     * @param page The Page object for this user
     */
    public void registerUserSession(String userId, BrowserContext context, Page page) {
        userContexts.put(userId, context);
        userPages.put(userId, page);
        this.currentUserId = userId;
        System.out.println("[SessionManager] Registered session for: " + userId);
    }
    
    /**
     * Gets the current user's BrowserContext.
     * 
     * @return BrowserContext for current user, or null if not set
     */
    public BrowserContext getCurrentContext() {
        if (currentUserId == null) {
            return null;
        }
        return userContexts.get(currentUserId);
    }
    
    /**
     * Gets the current user's Page.
     * 
     * @return Page for current user, or null if not set
     */
    public Page getCurrentPage() {
        if (currentUserId == null) {
            return null;
        }
        return userPages.get(currentUserId);
    }
    
    /**
     * Gets a specific user's BrowserContext.
     * 
     * @param userId The UserID
     * @return BrowserContext, or null if user not logged in
     */
    public BrowserContext getContext(String userId) {
        return userContexts.get(userId);
    }
    
    /**
     * Gets a specific user's Page.
     * 
     * @param userId The UserID
     * @return Page, or null if user not logged in
     */
    public Page getPage(String userId) {
        return userPages.get(userId);
    }
    
    /**
     * Gets the current UserID.
     * 
     * @return Current UserID, or null if no user is active
     */
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    /**
     * Checks if a specific user has an active session.
     * 
     * @param userId The UserID to check
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn(String userId) {
        BrowserContext context = userContexts.get(userId);
        return context != null && !context.browser().isConnected();
    }
    
    /**
     * Checks if a specific user has an active session (less strict check).
     * 
     * @param userId The UserID to check
     * @return true if context exists for user
     */
    public boolean hasUserSession(String userId) {
        return userContexts.containsKey(userId);
    }
    
    /**
     * Closes and removes a user's session.
     * 
     * @param userId The UserID to logout
     */
    public void logoutUser(String userId) {
        BrowserContext context = userContexts.remove(userId);
        Page page = userPages.remove(userId);
        
        if (context != null) {
            try {
                context.close();
                System.out.println("[SessionManager] Logged out and closed session: " + userId);
            } catch (Exception e) {
                System.err.println("[SessionManager] Error closing context for " + userId + ": " + e.getMessage());
            }
        }
        
        if (page != null) {
            try {
                page.close();
            } catch (Exception e) {
                System.err.println("[SessionManager] Error closing page for " + userId + ": " + e.getMessage());
            }
        }
        
        // If we logged out the current user, clear current user
        if (userId.equals(currentUserId)) {
            currentUserId = null;
        }
    }
    
    /**
     * Closes and clears all user sessions.
     */
    public void clearAllSessions() {
        List<String> userIds = new ArrayList<>(userContexts.keySet());
        for (String userId : userIds) {
            logoutUser(userId);
        }
        currentUserId = null;
        System.out.println("[SessionManager] Cleared all sessions");
    }
    
    /**
     * Gets the count of active user sessions.
     * 
     * @return Number of logged-in users
     */
    public int getActiveSessionCount() {
        return userContexts.size();
    }
    
    /**
     * Gets all active UserIDs.
     * 
     * @return List of UserIDs with active sessions
     */
    public List<String> getActiveUserIds() {
        return new ArrayList<>(userContexts.keySet());
    }
    
    /**
     * Prints a summary of all active sessions (for debugging).
     */
    public void printSessionSummary() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SESSION SUMMARY                               ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        System.out.println("Current User: " + (currentUserId != null ? currentUserId : "NONE"));
        System.out.println("Total Active Sessions: " + userContexts.size());
        System.out.println("\nActive Sessions:");
        
        if (userContexts.isEmpty()) {
            System.out.println("  (No active sessions)");
        } else {
            userContexts.keySet().forEach(userId -> 
                System.out.println("  • " + userId + (userId.equals(currentUserId) ? " [CURRENT]" : ""))
            );
        }
        
        System.out.println("\n────────────────────────────────────────────────────────────────\n");
    }
}
