package com.example.zhiyoufy.server.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class WebSocketConnectionEstablishedEvent {
	private String sessionId;
	private String remoteAddress;
	@JsonIgnore
	private WebSocketSession wsSession;
}
