package com.connect.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.connect.entities.Notifications;
import com.connect.entities.User;
import com.connect.entities.UserPosts;

public interface NotificationRepository extends JpaRepository<Notifications, Integer> {
	// fetch notifications for a recipient user
	public List<Notifications> findAllByReciepientUserId(User userId);

	// fetch notifications for a sender user
	public List<Notifications> findAllBySenderUserId(User userId);

	// fetch notifications by post
	@Query("from Notifications as n where n.postId = :pid")
	public List<Notifications> findAllByPostId(@Param("pid") UserPosts pid);
	
	// fetch notifications by type (message or notification)
	@Query("from Notifications as n where n.type = :type")
	public List<Notifications> getAllMessages(@Param("type") String type);
}
