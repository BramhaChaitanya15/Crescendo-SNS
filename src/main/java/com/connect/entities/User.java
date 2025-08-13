package com.connect.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int user_id;
	// validations
	@NotNull
	@NotBlank(message = "Please enter your first name")
	@Size(min = 2, max = 20, message = "Minimum 2 and Maximum 20 characters are allowed")
	private String first_name;
	@Size(min = 0, max = 20, message = "Minimum 2 and Maximum 20 characters are allowed")
	private String middle_name;
	@NotNull
	@NotBlank(message = "Please enter your last name")
	@Size(min = 2, max = 20, message = "Minimum 2 and Maximum 20 characters are allowed")
	private String last_name;
	@NotNull
	@NotBlank(message = "Please enter your Email")
	@Email(message = "The Email should be in this format example@domain.com")
	private String user_email;
	@NotNull
	@NotBlank(message = "Please enter a password")
	@Size(min = 8, message = "Minimum 8 characters are required")
	private String password;
	@NotNull
	@NotBlank(message = "Please enter a username")
	@Column(unique = true)
	private String username;
	@Size(min = 10, max = 10, message = "invalid mobile number, it should be exact 10 digits")
	private String user_phone;
	@Column(length = 1000)
	private String profile_image_name;
	private boolean isOnline;

	// default constructor
	public User() {
		super();
	}

	// constructor with fields
	public User(String first_name, String middle_name, String last_name, String user_email,
			String password, String username, String user_phone, String profile_image_name, boolean isOnline) {
		super();
		this.first_name = first_name;
		this.middle_name = middle_name;
		this.last_name = last_name;
		this.user_email = user_email;
		this.password = password;
		this.username = username;
		this.user_phone = user_phone;
		this.profile_image_name = profile_image_name;
		this.isOnline = isOnline;
	}

	// getters and setters
	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getMiddle_name() {
		return middle_name;
	}

	public void setMiddle_name(String middle_name) {
		this.middle_name = middle_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUser_email() {
		return user_email;
	}

	public void setUser_email(String user_email) {
		this.user_email = user_email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUser_phone() {
		return user_phone;
	}

	public void setUser_phone(String user_phone) {
		this.user_phone = user_phone;
	}
	
	public String getProfile_image_name() {
		return profile_image_name;
	}
	
	public void setProfile_image_name(String profile_image_name) {
		this.profile_image_name = profile_image_name;
	}
	
	public boolean getIsOnline() {
		return isOnline;
	}
	
	public void setIsOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	// end getters and setters

	// toString method
	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", first_name=" + first_name + ", middle_name=" + middle_name
				+ ", last_name=" + last_name + ", user_email=" + user_email + ", password=" + password + ", username="
				+ username + ", user_phone=" + user_phone + ", profile_image_name=" + profile_image_name + "]";
	}

}
