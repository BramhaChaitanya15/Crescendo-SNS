package com.connect.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Marks this class as a source of configuration for Spring
@EnableWebSecurity // Enables Spring Security for the application
public class MyConfig {

	// Custom handler for handling actions on successful logout
	@Autowired
	private CustomLogoutSuccessHandler customLogoutSuccessHandler; 

	// Bean to provide BCryptPasswordEncoder to encrypt user passwords
	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Bean to provide UserDetailsService implementation for fetching user details
	@Bean
	UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl(); 
	}

	// Bean to configure the authentication provider, using DaoAuthenticationProvider
	@Bean
	DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		// Setting custom UserDetailsService
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		// Setting BCryptPasswordEncoder for password encryption
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder()); 
		return daoAuthenticationProvider;
	}

	// Bean for configuring the AuthenticationManager, used for authentication
	@Bean
	AuthenticationManager authenticationManagerBean(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager(); 
	}

	// Configures HTTP security settings for the application
	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		try {
			// Configuring HTTP security settings
			httpSecurity
				.csrf(csrf -> csrf.disable()) // Disable CSRF protection (useful for REST APIs)
				// Configure URL access permissions
				.authorizeHttpRequests(authorizeHttpRequests -> 
					authorizeHttpRequests
						// URLs starting with /user/ need USER role
						.requestMatchers("/user/**").hasRole("USER") 
						// URLs starting with /setting/ need USER role
						.requestMatchers("/setting/**").hasRole("USER")
						// All other URLs are accessible by anyone
						.requestMatchers("/**").permitAll() 
						.anyRequest().authenticated()) 
				// Custom login configuration
				.formLogin(formLogin -> 
					formLogin
						// Custom login page URL
						.loginPage("/user_login") 
						// Redirect to profile page on successful login
						.defaultSuccessUrl("/user/profile", true)) 
				// Configuring "Remember Me" functionality
				.rememberMe(rememberMe -> 
					rememberMe
						// Secret key for token
						.key("uniqueAndSecret") 
						// Validity of the remember-me token
						.tokenValiditySeconds(Integer.MAX_VALUE)
						// Request parameter for remember me option
						.rememberMeParameter("remember-me")
						// Remember-me is not enabled by default
						.alwaysRemember(false)) 
				// Custom logout success handling
				.logout(logout -> 
					logout
						.logoutSuccessHandler(customLogoutSuccessHandler)); 

		} catch (Exception e) {
			e.printStackTrace(); 
		}

		// Use the custom DaoAuthenticationProvider
		httpSecurity.authenticationProvider(daoAuthenticationProvider());
		// Build the security filter chain
		DefaultSecurityFilterChain defaultSecurityFilterChain = httpSecurity.build();
		return defaultSecurityFilterChain; 
	}
}
