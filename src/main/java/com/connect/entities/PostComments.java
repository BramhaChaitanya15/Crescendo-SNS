package com.connect.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostComments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int comment_id;
	@Column(length = 250)
	private String comment;
	@Column(length = 100)
	private String date_str;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_id")
	private UserPosts userPost;
	
	
	//constructors
	public PostComments() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PostComments(int comment_id, String comment, UserPosts userPost) {
		super();
		this.comment_id = comment_id;
		this.comment = comment;
		this.userPost = userPost;
	}

	//getters and setters	
	/**
	 * @return the comment_id
	 */
	public int getComment_id() {
		return comment_id;
	}

	/**
	 * @param comment_id the comment_id to set
	 */
	public void setComment_id(int comment_id) {
		this.comment_id = comment_id;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the userPost
	 */
	public UserPosts getUserPost() {
		return userPost;
	}

	/**
	 * @param userPost the userPost to set
	 */
	public void setUserPost(UserPosts userPost) {
		this.userPost = userPost;
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
	//end getters and setters

	//toString method 
	@Override
	public String toString() {
		return "PostComments [comment_id=" + comment_id + ", comment=" + comment + ", user=" + user + ", userPost="
				+ userPost + "]";
	}
}
