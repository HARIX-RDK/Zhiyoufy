package com.example.zhiyoufy.server.config;

import com.example.zhiyoufy.server.web.socket.SessionPubWebSocketHandlerDecoratorFactory;
import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.CompositeMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private TaskScheduler messageBrokerTaskScheduler;

	@Autowired
	EventBus eventBus;

	@Bean
	public SimpMessagingTemplate clientMessagingTemplate(MessageChannel clientOutboundChannel,
			CompositeMessageConverter brokerMessageConverter) {
		SimpMessagingTemplate clientMessagingTemplate =
				new SimpMessagingTemplate(clientOutboundChannel);
		clientMessagingTemplate.setMessageConverter(brokerMessageConverter);

		return clientMessagingTemplate;
	}

	@Autowired
	public void setMessageBrokerTaskScheduler(@Lazy TaskScheduler taskScheduler) {
		messageBrokerTaskScheduler = taskScheduler;
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
		registry.addDecoratorFactory(new SessionPubWebSocketHandlerDecoratorFactory(eventBus));
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "queue")
			.setHeartbeatValue(new long[]{10_000, 20_000})
			.setTaskScheduler(messageBrokerTaskScheduler);
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/zhiyoufy-api/v1/websocket-stomp");
	}
}
