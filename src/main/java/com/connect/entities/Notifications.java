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
public class Notifications {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int notificationId;
	@Column(length = 50000)
	private String message;
	@Temporal(TemporalType.DATE)
	private Date timeStamp;
	@Column(length = 100)
	private String timeStampStr;
	private String type;
	private boolean readStatus;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "sender_user_id")
	private User senderUserId;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "reciepient_user_id")
	private User reciepientUserId;
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "post_id")
	private UserPosts postId;

	public Notifications(int notificationId, String message, Date timeStamp, String timeStampStr, String type,
			boolean readStatus, User reciepientUserId, User senderUserId, UserPosts postId) {
		super();
		this.notificationId = notificationId;
		this.message = message;
		this.timeStamp = timeStamp;
		this.timeStampStr = timeStampStr;
		this.type = type;
		this.readStatus = readStatus;
		this.reciepientUserId = reciepientUserId;
		this.senderUserId = senderUserId;
		this.postId = postId;
	}

	//constructor without id
	public Notifications(String message, Date timeStamp, String timeStampStr, String type, boolean readStatus,
			User reciepientUserId, User senderUserId, UserPosts postId) {
		super();
		this.message = message;
		this.timeStamp = timeStamp;
		this.timeStampStr = timeStampStr;
		this.type = type;
		this.readStatus = readStatus;
		this.reciepientUserId = reciepientUserId;
		this.senderUserId = senderUserId;
		this.postId = postId;
	}

	public Notifications() {
		super();
		// TODO Auto-generated constructor stub
	}

	// getters and setters
	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getTimeStampStr() {
		return timeStampStr;
	}

	public void setTimeStampStr(String timeStampStr) {
		this.timeStampStr = timeStampStr;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isReadStatus() {
		return readStatus;
	}

	public void setReadStatus(boolean readStatus) {
		this.readStatus = readStatus;
	}

	public User getReciepientUserId() {
		return reciepientUserId;
	}

	public void setReciepientUserId(User reciepientUserId) {
		this.reciepientUserId = reciepientUserId;
	}

	public User getSenderUserId() {
		return senderUserId;
	}

	public void setSenderUserId(User senderUserId) {
		this.senderUserId = senderUserId;
	}

	public UserPosts getPostId() {
		return postId;
	}

	public void setPostId(UserPosts postId) {
		this.postId = postId;
	}
	// end getters and setters

	// toString method
	@Override
	public String toString() {
		return "Notifications [notificationId=" + notificationId + ", message=" + message + ", timeStamp=" + timeStamp
				+ ", timeStampStr=" + timeStampStr + ", type=" + type + ", readStatus=" + readStatus + ", senderUserId="
				+ senderUserId + ", reciepientUserId=" + reciepientUserId + ", postId=" + postId + "]";
	}

}
