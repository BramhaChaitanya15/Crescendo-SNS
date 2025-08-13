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
public class CommentReplies {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 250)
	private String reply;
	@Column(length = 100)
	private String date_str;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "user_id")
	private User user;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "comment_id")
	private PostComments postComments;

	public void setUser(User user) {
		this.user = user;
	}

	// constructors
	public CommentReplies() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CommentReplies(int id, String reply, PostComments postComments) {
		super();
		this.id = id;
		this.reply = reply;
		this.postComments = postComments;
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
	 * @return the reply
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * @param reply the reply to set
	 */
	public void setReply(String reply) {
		this.reply = reply;
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
	 * @return the postComments
	 */
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public PostComments getPostComments() {
		return postComments;
	}

	/**
	 * @param postComments the postComments to set
	 */
	public void setPostComments(PostComments postComments) {
		this.postComments = postComments;
	}
	// end getters and setters

	// toString method
	@Override
	public String toString() {
		return "CommentReplies [id=" + id + ", reply=" + reply + ", postComments=" + postComments + "]";
	}

}
