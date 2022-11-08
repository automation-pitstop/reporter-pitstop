package org.automationpitstop;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.automationpitstiop.model.TestCase;
import org.automationpitstiop.model.TestSuite;
import org.automationpitstop.common.ReporterException;
import org.automationpitstop.utils.CommonUtils;
import org.automationpitstop.utils.ExtentUtils;
import org.automationpitstop.utils.XmlUtils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;

public class Main {

	private static Logger logger = Logger.getLogger(Main.class.getName());
	
	private static String REPORT_NAME;
	private static String OUTPUT_FOLDER_PATH;
	private static String JUNIT_XML_FILE_PATH;
	private static String JUNIT_XML_FOLDER_PATH;
	
//	private static final String WORKING_DIR_FULL_PATH = System.getProperty("user.dir") + System.getProperty("file.separator");
//	private static String junitXmlRelativePath = "target" + System.getProperty("file.separator")
//			+ "surefire-reports" + System.getProperty("file.separator")
//			+ "TEST-com.automation.pitstop.selenium_demo.HelloWorldChromeJupiter2Test.xml";
//	private static String testResultFileAbsolutePath = WORKING_DIR_FULL_PATH + junitXmlRelativePath;

	public static void main(String[] args) throws URISyntaxException, IOException, ClassNotFoundException, ReporterException {
		
		logger.info("Execution started");
		Map<String, String> contextMap = CommonUtils.createContextFromPropeties("application.properties");
		
		JUNIT_XML_FILE_PATH = CommonUtils.getOverriddenProperty("junit.xml.file.path", contextMap);
		JUNIT_XML_FOLDER_PATH = CommonUtils.getOverriddenProperty("junit.xml.folder.path", contextMap);
		OUTPUT_FOLDER_PATH = CommonUtils.getOverriddenProperty("output.folder.path", contextMap);
		REPORT_NAME = StringUtils.defaultIfBlank(CommonUtils.getOverriddenProperty("report.name", contextMap), "extent.html");

		// Validate VMs and initialize required variables
		validateVmArgs(args);

		checkForMandatoryParams();
		
		// Check and create if target folder does not exist
		createOutputFolder(OUTPUT_FOLDER_PATH);

		TestSuite testSuite = XmlUtils.parseJunitXml(new File(JUNIT_XML_FILE_PATH));
		List<TestCase> testCaseList = testSuite.getTestCaseList();

		if (testCaseList.size() == 0) {
			logger.info("No test cases found in result XML file. Skipping Extent reporting.");
			return;
		}

		// Create Extent Report
		ExtentReports extent = ExtentUtils.getExtentReports(OUTPUT_FOLDER_PATH + System.getProperty("file.separator") + REPORT_NAME);
		
		ExtentTest test = null;

		// Traverse test case list and add results to Extent report
		for (TestCase testCase : testCaseList) {

			test = extent.createTest(testCase.getName());

			if (!StringUtils.isBlank(testCase.getError())) {
				test.log(Status.FAIL, MarkupHelper.createCodeBlock(testCase.getError()));

        		if (StringUtils.isNotBlank(testCase.getSystem_err())) {
					ExtentTest failNode = test.createNode("Cick here to view system error");
					failNode.log(Status.INFO, MarkupHelper.createCodeBlock(testCase.getSystem_err()));
				}
				if (StringUtils.isNotBlank(testCase.getSystem_out())) {
					ExtentTest failNode = test.createNode("Cick here to view system logs");
					failNode.log(Status.INFO, MarkupHelper.createCodeBlock(testCase.getSystem_out()));
				}
				if (StringUtils.isNotBlank(testCase.getSnapshot_path())) {
					try {
						test.addScreenCaptureFromPath(
								StringUtils.replace(testCase.getSnapshot_path(), "pathToReplace", "pathToReplaceWith"));
					} catch (Exception e) {
						logger.info("Unable to add screenshots in Extent reports.\n" + e.getMessage());
					}
				}
			} else {
				test.log(Status.PASS, MarkupHelper.createLabel("Test Case Status is passed", ExtentColor.GREEN));
			}
			
		}

		extent.flush();
		logger.info("Extent report created at : " + OUTPUT_FOLDER_PATH + System.getProperty("file.separator")
				+ REPORT_NAME);

	}

	private static void createOutputFolder(String resultOutputPath) {
//		File directory = new File(WORKING_DIR_FULL_PATH + resultOutputPath);
		File directory = new File(resultOutputPath);
		if (!directory.exists()) {
			directory.mkdir();
		}

	}

	private static void validateVmArgs(String[] args) {
		if (args.length == 0) {
			logger.info("No VMs args are provided");
		}
//		System.exit(1);
	}

	private static void checkForMandatoryParams() {
		logger.info("Executing service with the following parameters" + "\n");
		System.out.println("junit.xml.folder.path : "+ JUNIT_XML_FILE_PATH);
		System.out.println("junit.xml.file.path : "+ JUNIT_XML_FOLDER_PATH);
		System.out.println("output.folder.path : "+ OUTPUT_FOLDER_PATH);
		System.out.println("report.name : "+ REPORT_NAME);
	}
}