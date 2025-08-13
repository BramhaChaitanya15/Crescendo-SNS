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
public class Followers {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "follower_id")
	private int follower_id;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user_id;

	// constructors
	public Followers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Followers(int id, int follower_id, User user_id) {
		super();
		this.id = id;
		this.follower_id = follower_id;
		this.user_id = user_id;
	}

	public Followers(int follower_id, User user_id) {
		super();
		this.follower_id = follower_id;
		this.user_id = user_id;
	}

	// getters and setters
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
	 * @return the follower_id
	 */
	public int getFollower_id() {
		return follower_id;
	}

	/**
	 * @param follower_id the follower_id to set
	 */
	public void setFollower_id(int follower_id) {
		this.follower_id = follower_id;
	}

	/**
	 * @return the user_id
	 */
	public User getUser_id() {
		return user_id;
	}

	/**
	 * @param user_id the user_id to set
	 */
	public void setUser_id(User user_id) {
		this.user_id = user_id;
	}
	// end getters and setters

	// toString method
	@Override
	public String toString() {
		return "Followers [id=" + id + ", follower_id=" + follower_id + ", user_id=" + user_id + "]";
	}

}
