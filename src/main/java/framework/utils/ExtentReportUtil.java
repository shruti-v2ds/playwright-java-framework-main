package framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.microsoft.playwright.Page;
import framework.config.ConfigManager;
import framework.config.FrameworkConfig;

import java.io.File;
import java.util.Base64;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportUtil {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> EXTENT_TEST = new ThreadLocal<>();

    private ExtentReportUtil() {
        throw new IllegalStateException("ExtentReport utility class");
    }

    public static void initExtentReports(String baseReportPath) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = baseReportPath + File.separator + timestamp + "/TestReport.html";
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        extent = new ExtentReports();
        extent.attachReporter(spark);

        FrameworkConfig config = ConfigManager.getConfig();
        extent.setSystemInfo("Base URL", config.baseUrl());
        extent.setSystemInfo("Browser", config.browser());
    }

    public static void createTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        EXTENT_TEST.set(test);
    }

    public static ExtentTest getTest() {
        return EXTENT_TEST.get();
    }

    public static void logInfo(String message) {
        getTest().info(message);
    }

    public static void logPass(String message) {
        getTest().pass(message);
    }

    public static void logFail(String message) {
        getTest().fail(message);
    }

    public static void attachScreenshot(Page page, String message) {
        String base64 = ScreenshotUtil.takeScreenshot(page);
        getTest().info(message, MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void removeTest() {
        EXTENT_TEST.remove();
    }
}