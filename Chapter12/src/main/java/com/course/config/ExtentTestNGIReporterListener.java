package com.course.config;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.ResourceCDN;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.jetbrains.annotations.NotNull;
import org.testng.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExtentTestNGIReporterListener implements IReporter {

    //测试报告导出到test-output文件夹下的Extent.html
    // 获取当前时间并格式化为 yyyyMMdd_HHmmss（如 20231115_143045）
    static String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HH"));
    private static final String OUTPUT_FOLDER = "test-output/";
    private static final String FILE_NAME = "report_"+timestamp+".html";

    private ExtentReports extent;


    public void generateReport(List xmlSuites, @NotNull List suites, String outputDirectory) {
        init();
        for (Object suite : suites) {
            Map result = ((ISuite) suite).getResults();
            for (Object r : result.values()) {
                ITestContext context = ((ISuiteResult) r).getTestContext();
                buildTestNodes(context.getFailedTests(), Status.FAIL);
                buildTestNodes(context.getSkippedTests(), Status.SKIP);
                buildTestNodes(context.getPassedTests(), Status.PASS);

            }
        }

        for (String s : Reporter.getOutput()) {
            extent.setTestRunnerOutput(s);
        }

        extent.flush();
    }

    private void init() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(OUTPUT_FOLDER + FILE_NAME);
        htmlReporter.config().setDocumentTitle("api自动化测试报告");
        htmlReporter.config().setReportName("api自动化测试报告");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setTheme(Theme.STANDARD);
        //解决测试报告中文乱码问题
        htmlReporter.config().setEncoding("utf-8");
        //设置静态文件的DNS
        //解决国内www.rawgit.com访问不了的情况，无法加载css文件导致网页加载一直转圈
        htmlReporter.config().setResourceCDN(ResourceCDN.EXTENTREPORTS);

        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setReportUsesManualConfiguration(true);
    }

    private void buildTestNodes(@NotNull IResultMap tests, Status status) {
        ExtentTest test;

        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
                test = extent.createTest(result.getMethod().getMethodName());

                for (String group : result.getMethod().getGroups())
                    test.assignCategory(group);

                if (result.getThrowable() != null) {
                    test.log(status, result.getThrowable());
                }
                else {
                    test.log(status, "Test " + status.toString().toLowerCase() + "ed");
                }

                test.getModel().setStartTime(getTime(result.getStartMillis()));
                test.getModel().setEndTime(getTime(result.getEndMillis()));
            }
        }
    }

    private @NotNull Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
}
