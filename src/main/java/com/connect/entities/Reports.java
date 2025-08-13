package com.connect.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Reports {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reportId;
	@ElementCollection
	private List<String> report = new ArrayList<String>();
	@ManyToOne(cascade = CascadeType.REMOVE)
	private UserPosts postId;
	
	public Reports() {
		super();
	}
	//getters and setters
	public int getReportId() {
		return reportId;
	}
	public void setReportId(int reportId) {
		this.reportId = reportId;
	}
	public List<String> getReport() {
		return report;
	}
	public void setReport(List<String> report) {
		this.report = report;
	}
	public UserPosts getPostId() {
		return postId;
	}
	public void setPostId(UserPosts postId) {
		this.postId = postId;
	}
	//end getters and setters
	//toString method
	@Override
	public String toString() {
		return "Reports [reportId=" + reportId + ", report=" + report + ", postId=" + postId + "]";
	}
	
}
