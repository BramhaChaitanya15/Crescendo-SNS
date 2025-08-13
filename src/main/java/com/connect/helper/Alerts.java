package com.connect.helper;

public class Alerts {
	
	private String content;
	private String type;
	// constructor with fields for alert and type
	public Alerts(String content, String type) {
		super();
		this.content = content;
		this.type = type;
	}
	
	//getters and setters
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	//end getters and setters
}
