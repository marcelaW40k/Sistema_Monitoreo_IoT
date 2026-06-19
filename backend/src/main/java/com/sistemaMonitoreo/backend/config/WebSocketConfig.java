package com.sistemaMonitoreo.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Habilita el manejo de mensajes en tiempo real
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker de memoria simple para enviar datos al cliente en canales que inicien con /topic
        config.enableSimpleBroker("/topic");
        // Prefijo para los mensajes que van desde el cliente hacia el backend
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint de conexión inicial que usará React. Permitimos CORS completo.
        registry.addEndpoint("/ws-fleet")
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS(); // Soporte de caída si el navegador no soporta Websockets nativos
    }
}
