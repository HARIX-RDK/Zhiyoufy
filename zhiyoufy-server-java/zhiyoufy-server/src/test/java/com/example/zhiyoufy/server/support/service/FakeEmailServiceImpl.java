package com.example.zhiyoufy.server.support.service;

import java.util.List;

import com.example.zhiyoufy.server.service.EmailService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeEmailServiceImpl implements EmailService {
	private int sendCnt = 0;
	private String lastTo;
	private String lastSubject;
	private String lastText;

	@Override
	public void sendMail(String to, String subject, String text, boolean html) {
		sendCnt++;
		lastTo = to;
		lastSubject = subject;
		lastText = text;
	}

	@Override
	public void sendMailWithAttachment(String to, String subject, String text, boolean html, List<String> pathsToAttachment) {
		sendCnt++;
		lastTo = to;
		lastSubject = subject;
		lastText = text;
	}

	public void reset() {
		sendCnt = 0;
		lastTo = null;
		lastSubject = null;
		lastText = null;
	}
}
