package com.tradepulse.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // ערוץ השידור: כל מי שירשם ל-/topic יקבל הודעות
        config.enableSimpleBroker("/topic");
        // קידומת להודעות שנשלחות מהלקוח לשרת (לא נשתמש בזה כרגע, אבל טוב שיהיה)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // נקודת החיבור הראשונית (Handshake)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // מאפשר חיבור מכל דפדפן (חשוב לפיתוח)
                .withSockJS(); // מאפשר חיבור גם לדפדפנים ישנים שלא תומכים ב-WebSocket מלא
    }
}