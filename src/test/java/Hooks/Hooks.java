package Hooks;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import framework.config.ConfigManager;
import framework.core.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Hooks {

    @Before(order = 0)
    public void setup(Scenario scenario) {

        DriverFactory.initBrowser(ConfigManager.getConfig().browser());

        DriverFactory.getContext()
                .tracing()
                .start(
                        new Tracing.StartOptions()
                                .setScreenshots(true)
                                .setSnapshots(true)
                                .setSources(true)
                );

        System.out.println("\n====================================");
        System.out.println("STARTING SCENARIO : " + scenario.getName());
        System.out.println("====================================");
    }

    @After(order = 2)
    public void captureFailureScreenshot(Scenario scenario) {

        if (!scenario.isFailed()) {
            return;
        }

        try {

            Page page = DriverFactory.getPage();

            byte[] screenshot =
                    page.screenshot(
                            new Page.ScreenshotOptions()
                                    .setFullPage(true)
                    );

            scenario.attach(
                    screenshot,
                    "image/png",
                    "Failure Screenshot"
            );

        } catch (Exception e) {

            System.err.println(
                    "Failed to capture screenshot: "
                            + e.getMessage()
            );
        }
    }

    @After(order = 1)
    public void saveTrace(Scenario scenario) {

        try {

            Files.createDirectories(
                    Paths.get("target/traces")
            );

            String traceName =
                    scenario.getName()
                            .replaceAll("[^a-zA-Z0-9-_]", "_");

            Path tracePath =
                    Paths.get(
                            "target/traces/"
                                    + traceName
                                    + ".zip"
                    );

            DriverFactory.getContext()
                    .tracing()
                    .stop(
                            new Tracing.StopOptions()
                                    .setPath(tracePath)
                    );

            System.out.println(
                    "Trace saved: "
                            + tracePath.toAbsolutePath()
            );

        } catch (Exception e) {

            System.err.println(
                    "Unable to save trace: "
                            + e.getMessage()
            );
        }
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {

        System.out.println(
                "SCENARIO STATUS : "
                        + scenario.getStatus()
        );

        DriverFactory.closeBrowser();

        System.out.println("====================================");
        System.out.println("FINISHED SCENARIO : " + scenario.getName());
        System.out.println("====================================\n");
    }
}