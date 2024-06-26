package com.example.zhiyoufy.server.web.socket;

import com.example.zhiyoufy.server.domain.event.WebSocketConnectionClosedEvent;
import com.example.zhiyoufy.server.domain.event.WebSocketConnectionEstablishedEvent;
import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.Assert;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

/**
 * 发送Session事件
 * StompSubProtocolHandler里也有通过ApplicationEventPublisher发送SessionConnectEvent,
 * SessionSubscribeEvent, SessionUnsubscribeEvent, SessionDisconnectEvent
 */
@Slf4j
public class SessionPubWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {
	private final EventBus eventBus;

	public SessionPubWebSocketHandlerDecoratorFactory(EventBus eventBus) {
		Assert.notNull(eventBus, "eventBus cannot be null");
		this.eventBus = eventBus;
	}

	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {
		return new SessionPubWebSocketHandler(handler);
	}

	private final class SessionPubWebSocketHandler extends WebSocketHandlerDecorator {
		SessionPubWebSocketHandler(WebSocketHandler delegate) {
			super(delegate);
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			log.debug("Connection established for session " + session.getId()
					+ ", remote address " + session.getRemoteAddress());

			WebSocketConnectionEstablishedEvent event = new WebSocketConnectionEstablishedEvent();
			event.setSessionId(session.getId());
			if (session.getRemoteAddress() != null) {
				event.setRemoteAddress(session.getRemoteAddress().toString());
			}
			event.setWsSession(session);

			eventBus.post(event);

			super.afterConnectionEstablished(session);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session,
				CloseStatus closeStatus) throws Exception {
			log.debug("Connection closed for session " + session.getId()
					+ ", status " + closeStatus);

			WebSocketConnectionClosedEvent event = new WebSocketConnectionClosedEvent();
			event.setSessionId(session.getId());
			if (session.getRemoteAddress() != null) {
				event.setRemoteAddress(session.getRemoteAddress().toString());
			}
			event.setWsSession(session);

			eventBus.post(event);

			super.afterConnectionClosed(session, closeStatus);
		}
	}
}
