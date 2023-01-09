package it.cleverad.engine.config;

import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
//        // Registers the endpoint where the connection will take place
//        registry.addEndpoint("/ws")
//                // Allow the origin http://XXXX to send messages to us. (Base URL of the client)
//                // .setAllowedOrigins("cleverad.it")
//                // .setAllowedOrigins("http://localhost:4200")
//                .setAllowedOriginPatterns("*")
//                // Enable SockJS fallback options
//                .withSockJS();

        registry.addEndpoint("/ws") .setAllowedOriginPatterns("*");
        registry.addEndpoint("/ws") .setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Set prefix for the endpoint that the client listens for our messages from
        config.enableSimpleBroker("/topic");
        // Set prefix for endpoints the client will send messages to
        config.setApplicationDestinationPrefixes("/app");
    }
}
