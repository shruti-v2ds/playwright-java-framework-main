package Hooks;

import framework.core.DriverFactory;
import framework.utils.ExcelUtil;
import framework.utils.SessionManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.List;
import java.util.Map;

/**
 * Hooks for data-driven multi-user and multi-business scenarios.
 * 
 * Handles:
 * 1. Data-driven scenario looping (same scenario repeated for each data set)
 * 2. User session management (isolation between users)
 * 3. Session reuse (same user, multiple businesses)
 * 
 * Tags:
 * @DataDriven - Enables data-driven execution
 * @MultiUser - Enables user session management
 * @MultiBusinessPerUser - Enables business loop within same user session
 * 
 * Example:
 *   @DataDriven @MultiUser
 *   Scenario: Host a new business
 *     Given user logs in with mobile <mobile>
 *     When user hosts a new <profession> business
 *     Then business is created successfully
 */
public class DataDrivenHooks {
    
    private static final String DATA_DRIVEN_SCENARIO_TAG = "@DataDriven";
    private static final String MULTI_USER_TAG = "@MultiUser";
    private static final String MULTI_BUSINESS_TAG = "@MultiBusinessPerUser";
    
    /**
     * Initializes SessionManager for multi-user scenarios.
     * Called before each scenario tagged with @DataDriven and @MultiUser.
     */
    @Before(order = 1, value = DATA_DRIVEN_SCENARIO_TAG + " and " + MULTI_USER_TAG)
    public void initializeMultiUserMode(Scenario scenario) {
        System.out.println("\n[DataDrivenHooks] Initializing multi-user mode for: " + scenario.getName());
        DriverFactory.initSessionManager();
    }
    
    /**
     * Logs information about data-driven scenario execution.
     * Called before scenarios tagged with @DataDriven.
     */
    @Before(order = 2, value = DATA_DRIVEN_SCENARIO_TAG)
    public void logDataDrivenScenario(Scenario scenario) {
        System.out.println("\n[DataDrivenHooks] Executing data-driven scenario: " + scenario.getName());
        System.out.println("[DataDrivenHooks] Scenario tags: " + scenario.getSourceTagNames());
        System.out.println("[DataDrivenHooks] Scenario line: " + scenario.getLine());
    }
    
    /**
     * Cleans up multi-user sessions after scenario execution.
     * Called after scenarios tagged with @DataDriven and @MultiUser.
     */
    @After(order = 3, value = DATA_DRIVEN_SCENARIO_TAG + " and " + MULTI_USER_TAG)
    public void cleanupMultiUserSessions(Scenario scenario) {
        try {
            SessionManager manager = DriverFactory.getSessionManager();
            if (manager != null) {
                System.out.println("\n[DataDrivenHooks] Cleaning up multi-user sessions");
                manager.printSessionSummary();
                manager.clearAllSessions();
            }
        } catch (Exception e) {
            System.err.println("[DataDrivenHooks] Error cleaning up sessions: " + e.getMessage());
        }
    }
    
    /**
     * Handles data-driven scenario execution.
     * For scenarios tagged with @DataDriven, this hook manages the loop.
     * Note: Cucumber doesn't natively support looping scenarios, so this provides logging/tracking.
     * The actual loop logic is implemented in step definitions.
     */
    @After(order = 4, value = DATA_DRIVEN_SCENARIO_TAG)
    public void logDataDrivenCompletion(Scenario scenario) {
        System.out.println("\n[DataDrivenHooks] Completed data-driven scenario: " + scenario.getName());
        System.out.println("[DataDrivenHooks] Scenario status: " + scenario.getStatus());
    }
}
