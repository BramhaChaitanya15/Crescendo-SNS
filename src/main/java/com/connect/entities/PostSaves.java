package com.connect.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class PostSaves {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_id")
	private UserPosts postId;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User userId;

	// constructors
	public PostSaves() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PostSaves(int id, UserPosts postId, User userId) {
		super();
		this.id = id;
		this.postId = postId;
		this.userId = userId;
	}

	// end getters and setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UserPosts getPostId() {
		return postId;
	}

	public void setPostId(UserPosts postId) {
		this.postId = postId;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}
	// end getters and setters

	// toString method
	@Override
	public String toString() {
		return "PostSaves [id=" + id + ", postId=" + postId + ", userId=" + userId + "]";
	}

}
