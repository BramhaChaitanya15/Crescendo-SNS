package com.connect.entities;

import java.sql.Date;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class UserPosts {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int post_id;
	@Column(length = 100)
	private String post_title;
	@Column(length = 50000)
	private String post_content;
	@Column(length = 5000)
	private String post_image_name;
	@Column(length = 5000)
	private String post_video_name;
	@Temporal(TemporalType.DATE)
	private Date post_date;
	@Column(length = 100)
	private String date_str;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;

	// constructors
	public UserPosts(int post_id, String post_title, String post_content, String post_image_name,
			String post_video_name, Date post_date, String date_str, User user) {
		super();
		this.post_id = post_id;
		this.post_title = post_title;
		this.post_content = post_content;
		this.post_image_name = post_image_name;
		this.post_video_name = post_video_name;
		this.post_date = post_date;
		this.date_str = date_str;
		this.user = user;
	}

	public UserPosts() {
		super();
	}

	/**
	 * @return the post_id
	 */
	public int getPost_id() {
		return post_id;
	}

	/**
	 * @param post_id the post_id to set
	 */
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}

	/**
	 * @return the post_title
	 */
	public String getPost_title() {
		return post_title;
	}

	/**
	 * @param post_title the post_title to set
	 */
	public void setPost_title(String post_title) {
		this.post_title = post_title;
	}

	/**
	 * @return the post_content
	 */
	public String getPost_content() {
		return post_content;
	}

	/**
	 * @param post_content the post_content to set
	 */
	public void setPost_content(String post_content) {
		this.post_content = post_content;
	}

	/**
	 * @return the post_media_name
	 */
	public String getPost_image_name() {
		return post_image_name;
	}

	/**
	 * @param post_media_name the post_image_name to set
	 */
	public void setPost_image_name(String post_image_name) {
		this.post_image_name = post_image_name;
	}

	/**
	 * @return the post_video_name
	 */
	public String getPost_video_name() {
		return post_video_name;
	}

	/**
	 * @param post_video_name the post_video_name to set
	 */
	public void setPost_video_name(String post_video_name) {
		this.post_video_name = post_video_name;
	}

	/**
	 * @return the post_date
	 */
	public Date getPost_date() {
		return post_date;
	}

	/**
	 * @param post_date the post_date to set
	 */
	public void setPost_date(Date post_date) {
		this.post_date = post_date;
	}
	
	/**
	 * @return the date_str
	 */
	public String getDate_str() {
		return date_str;
	}

	/**
	 * @param date_str the date_str to set
	 */
	public void setDate_str(String date_str) {
		this.date_str = date_str;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "UserPosts [post_id=" + post_id + ", post_title=" + post_title + ", post_content=" + post_content
				+ ", post_image_name=" + post_image_name + ", post_video_name=" + post_video_name + ", user=" + user
				+ "]";
	}

}
