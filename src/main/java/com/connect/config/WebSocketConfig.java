package com.connect.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//Marks this class as a configuration class for WebSocket communication
@Configuration
//Enables WebSocket message handling using a message broker
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// Registers a STOMP 
	// (Simple Text Oriented Messaging Protocol) 
	// endpoint for WebSocket connections
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
		 // Adds a WebSocket endpoint at '/websocket' 
		// for clients to connect to, using SockJS as a fallback option
        registry.addEndpoint("/websocket").withSockJS();
    }
	
	// Configures the message broker used for routing messages
	@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
		// Enables a simple in-memory message broker 
		// with destination prefixes "/user" and "/topic"
        // These prefixes will be used for broadcasting 
		// messages to topics or specific users
        registry.enableSimpleBroker("/user", "/topic");
        // Sets "/app" as the prefix for application-level
        // destinations where messages will be sent to
        // Controllers will handle messages prefixed with "/app"
        registry.setApplicationDestinationPrefixes("/app");
        // Sets "/user" as the prefix for point-to-point 
        // messaging (i.e., sending messages to specific users)
        registry.setUserDestinationPrefix("/user");
    }
}
