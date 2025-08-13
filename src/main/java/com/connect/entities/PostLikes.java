package com.connect.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostLikes {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User userId;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_id")
	private UserPosts postId;
	
	//constructor without parameters 
	public PostLikes() {
		super();
	}

	//constructor with fields
	public PostLikes(int id, User userId, UserPosts postId) {
		super();
		this.id = id;
		this.userId = userId;
		this.postId = postId;
	}
	
	
	//getters and setters
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the userId
	 */
	public User getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(User userId) {
		this.userId = userId;
	}
	/**
	 * @return the postId
	 */
	public UserPosts getPostId() {
		return postId;
	}
	/**
	 * @param postId the postId to set
	 */
	public void setPostId(UserPosts postId) {
		this.postId = postId;
	}
	//end getters and setters
	
	
	//toString method
	@Override
	public String toString() {
		return "PostLikes [id=" + id + ", userId=" + userId + ", postId=" + postId + "]";
	}
	
}
