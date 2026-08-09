package framework.utils;

import org.testng.*;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportUtil.initExtentReports("reports");
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentReportUtil.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportUtil.getTest().pass("Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportUtil.getTest().fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportUtil.getTest().skip(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportUtil.flushReports();
    }
}
