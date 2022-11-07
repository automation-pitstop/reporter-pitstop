package org.automationpitstop.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.automationpitstiop.model.TestCase;
import org.automationpitstiop.model.TestSuite;
import org.automationpitstop.common.ReporterException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlUtils {

	static Logger logger = Logger.getLogger(XmlUtils.class.getName());

	public static TestSuite parseJunitXml(File xmlFile) {
		TestSuite testSuite = new TestSuite();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = null;

			try {
				doc = dBuilder.parse(xmlFile);
			} catch (IOException e) {
				throw new RuntimeException("Unable to locate the xml file at given location. Make sure you provide relative path.\nFile path : " + xmlFile.getAbsolutePath());
			}

			doc.getDocumentElement().normalize();

			// Set the time for the suite
			testSuite.setTime(doc.getElementsByTagName("testsuite").item(0).getAttributes().getNamedItem("time").toString());

			// Set the test case date
			NodeList testCasesNodeList = doc.getElementsByTagName("testcase");

			for (int i = 0; i < testCasesNodeList.getLength(); i++) {
				TestCase testCase = new TestCase();

				if (testCasesNodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {

					NamedNodeMap testCaseNodeAttrMap = testCasesNodeList.item(i).getAttributes();

					// Fetch Test case details i.e. Name and Time
					testCase.setName(testCaseNodeAttrMap.getNamedItem("classname").getTextContent());
					testCase.setTime(testCaseNodeAttrMap.getNamedItem("time").getTextContent());

					if (testCasesNodeList.item(i).hasChildNodes()) {

						Element testCaseElement = (Element) testCasesNodeList.item(i);

						Node errorNode = testCaseElement.getElementsByTagName("error").item(0);
						String errorMsg = "";

						try {
							errorMsg = errorNode.getTextContent();
							if (!StringUtils.isBlank(errorMsg)) {
								testCase.setError(errorMsg);
							}
						} catch (NullPointerException e) {
							testSuite.addTestCase(testCase);
							continue;
						}

						String snapshotPath = StringUtils.substringBetween(errorMsg, "Snapshot path::",	"TestCase URL::");
						if (!StringUtils.isBlank(snapshotPath)) {
							testCase.setSnapshot_path(snapshotPath.trim());
						}

						try {
							if (!StringUtils.isBlank(testCaseElement.getElementsByTagName("system-err").item(0).getTextContent())) {
								testCase.setSystem_err(testCaseElement.getElementsByTagName("system-err").item(0).getTextContent());
							}
						} catch (NullPointerException e) {}

						try {
							if (!StringUtils.isBlank(testCaseElement.getElementsByTagName("system-out").item(0).getTextContent())) {
								testCase.setSystem_out(testCaseElement.getElementsByTagName("system-out").item(0).getTextContent());
							}
						} catch (NullPointerException e) {}

					}
				}

				testSuite.addTestCase(testCase);
			}

		} catch (NullPointerException e) {
			logger.error(
					"Unable to create extent report, due to missing elements in XML file. Dumping the XML file for debugging purpose.");
			logger.error("----------------------------------------------");
			try {
				logger.error(FileUtils.readFileToString(xmlFile, StandardCharsets.UTF_8));
			} catch (IOException e2) {
				logger.error("Unable to dump the XML file.");
			}
			logger.error("----------------------------------------------");
			throw new RuntimeException(e.getMessage());
		} catch (ParserConfigurationException | SAXException e) {
			throw new RuntimeException("Unable to create extent report.\n" + e.getMessage());
		}

		return testSuite;

	}
}
