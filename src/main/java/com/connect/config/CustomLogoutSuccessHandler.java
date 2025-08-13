package com.connect.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.connect.dao.UserRepository;
import com.connect.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private UserRepository userRepository;

	// This method is triggered when a logout is successful
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		// Check if the user is authenticated and their username is available
		if (authentication != null && authentication.getName() != null) {
			String username = authentication.getName();
			User user = userRepository.getUserByUserName(username);
			// If the user is found, set their online status to false (offline)
			if (user != null) {
				user.setIsOnline(false);
				userRepository.save(user);
			}
		}
		// Set the response status to OK (HTTP 200) indicating successful logout
		response.setStatus(HttpServletResponse.SC_OK);
		// Redirect the user to the login page with a logout success message
		response.sendRedirect("/user_login?logout");
	}
}
