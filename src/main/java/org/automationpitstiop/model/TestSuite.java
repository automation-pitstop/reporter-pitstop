package org.automationpitstiop.model;

import java.util.LinkedList;
import java.util.List;

public class TestSuite {
	
	private List<TestCase> testCaseList = new LinkedList<TestCase>();
	private String time;
	
	public List<TestCase> getTestCaseList() {
		return testCaseList;
	}
	public void setTestCaseList(List<TestCase> testCaseList) {
		this.testCaseList = testCaseList;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getTestCaseCount() {
		return this.testCaseList.size();
	}
	public void addTestCase(TestCase testCase) {
		this.testCaseList.add(testCase);
	}
	

}
