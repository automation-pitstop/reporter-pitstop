package org.automationpitstop.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentUtils {

	public static ExtentReports getExtentReports(String reportFileName) {
		ExtentReports extent = new ExtentReports();
		extent.attachReporter(configureExtentSparkReporter(reportFileName));
//		extent.createTest("MyFirstTest")
//		  .log(Status.PASS, "This is a logging event for MyFirstTest, and it passed!");
//		extent.flush();
		return extent;
	}
	
	private static ExtentSparkReporter configureExtentSparkReporter(String reportFileName) {
		ExtentSparkReporter spark = new ExtentSparkReporter(reportFileName);
		spark.config().setDocumentTitle(reportFileName);
		spark.config().setReportName(reportFileName);
		spark.config().setEncoding("utf-8");
		spark.config().setCss("img { border: 0;width: 100%;} textarea.code-block{min-height: 15 rem;}");
		return spark;
	}
}
