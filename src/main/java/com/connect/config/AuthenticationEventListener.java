package com.connect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.connect.dao.UserRepository;
import com.connect.entities.User;

@Component
public class AuthenticationEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

	@Autowired
	private UserRepository userRepository;

	// This method is called when an authentication success event is fired
	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent event) {
		// Get the authentication object from the event
		Authentication authentication = event.getAuthentication();
		// Retrieve the username from the authenticated user's details
		String username = authentication.getName();
		// Fetch the corresponding user from the database using the username
		User user = userRepository.getUserByUserName(username);
		// If user exists, set the user's online status to true
		if (user != null) {
			user.setIsOnline(true);
			userRepository.save(user);
		}
	}
}
