package com.connect.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.connect.entities.User;

public class CustomUserDetails implements UserDetails {

	// User entity containing user information from the database
	private User user;
	
	// Constructor that initializes the CustomUserDetails object with the user entity
	public CustomUserDetails(User user) {
		super();
		this.user = user;
	}

	// Returns the authorities (roles/permissions) assigned to the user
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Every user has a ROLE_USER authority in this case
		SimpleGrantedAuthority sgt = new SimpleGrantedAuthority("ROLE_USER");
		// Return the user's granted authority
		return List.of(sgt);
	}

	//overriding necessary functions
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
