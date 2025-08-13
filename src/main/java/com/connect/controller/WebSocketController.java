package com.connect.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.connect.dao.NotificationRepository;
import com.connect.dao.UserRepository;
import com.connect.entities.Notifications;
import com.connect.entities.User;
import com.connect.helper.Base64Util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
public class WebSocketController {

	//autowire required repository
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NotificationRepository notificationRepository;

	//create EntityManager object
	@PersistenceContext
	private EntityManager entityManager;

	private final SimpMessagingTemplate messagingTemplate;

	public WebSocketController(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}

	@MessageMapping("/message")
	@Transactional
	public void getContent(@RequestBody Notifications notification, Principal p) {

		String targetUser = notification.getReciepientUserId().getUsername();

		try {
			// Initializing the Date Object
			Date date = new Date();
			// saving date string to show on posts
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a	E, dd MMM");
			// Processing store data if the notification is of the type message
			// fetching user logged in (sender)
			String userName = p.getName();
			User sender = this.userRepository.getUserByUserName(userName);
			// fetching user that will receive the message
			User receiver = this.userRepository.getUserByUserId(notification.getReciepientUserId().getUser_id());
			// Reattach the User entities to the current persistence context
			sender = entityManager.merge(sender);
			receiver = entityManager.merge(receiver);
			// setting date and time of notification
			java.sql.Date d = new java.sql.Date(date.getTime());
			// encode the message to be saved
			String encodedMessage = Base64Util.encode(notification.getMessage());
			if (notification.getType().equalsIgnoreCase("message")) {
				Notifications n = new Notifications(encodedMessage, d, simpleDateFormat.format(date),
						notification.getType(), false, receiver, sender, null);
				this.notificationRepository.save(n);
			}

			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		messagingTemplate.convertAndSendToUser(targetUser, "/topic/return-to", notification);
	}
}