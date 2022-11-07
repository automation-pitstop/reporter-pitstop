package org.automationpitstiop.model;

public class TestCase {

	private String name;
	private String time;
	private String error;
	private String snapshot_path;
	private String system_out;
	private String system_err;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getSnapshot_path() {
		return snapshot_path;
	}
	public void setSnapshot_path(String snapshot_path) {
		this.snapshot_path = snapshot_path;
	}
	public String getSystem_out() {
		return system_out;
	}
	public void setSystem_out(String system_out) {
		this.system_out = system_out;
	}
	public String getSystem_err() {
		return system_err;
	}
	public void setSystem_err(String system_err) {
		this.system_err = system_err;
	}
	
}
