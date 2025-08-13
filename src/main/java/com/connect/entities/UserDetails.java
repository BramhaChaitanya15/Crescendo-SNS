package com.connect.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 5000)
	private String about;
	@Column(length = 1000)
	private String user_address;
	private String religion;
	@Column(length = 1000)
	private String education1;
	@Column(length = 1000)
	private String education2;
	@Column(length = 1000)
	private String education3;
	@Column(length = 1000)
	private String work;
	@OneToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;
	
	//default constructor
	public UserDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	//constructor with fields
	public UserDetails(int id, String about, String user_address, String religion, String education1, String education2,
			String education3, String work, User user) {
		super();
		this.id = id;
		this.about = about;
		this.user_address = user_address;
		this.religion = religion;
		this.education1 = education1;
		this.education2 = education2;
		this.education3 = education3;
		this.work = work;
		this.user = user;
	}

	//getters and setters
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getUser_address() {
		return user_address;
	}

	public void setUser_address(String user_address) {
		this.user_address = user_address;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getEducation1() {
		return education1;
	}

	public void setEducation1(String education1) {
		this.education1 = education1;
	}

	public String getEducation2() {
		return education2;
	}

	public void setEducation2(String education2) {
		this.education2 = education2;
	}

	public String getEducation3() {
		return education3;
	}

	public void setEducation3(String education3) {
		this.education3 = education3;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	//end getters and setters
	
	@Override
	public String toString() {
		return "UserDetails [id=" + id + ", about=" + about + ", user_address=" + user_address + ", religion="
				+ religion + ", education1=" + education1 + ", education2=" + education2 + ", education3=" + education3
				+ ", work=" + work + ", user=" + user + "]";
	}
	
	
	
}
