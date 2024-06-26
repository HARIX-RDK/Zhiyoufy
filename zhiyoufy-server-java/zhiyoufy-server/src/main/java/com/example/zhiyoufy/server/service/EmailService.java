package com.example.zhiyoufy.server.service;

import java.util.List;

public interface EmailService {
	void sendMail(String to, String subject,
			String text, boolean html);
	void sendMailWithAttachment(String to, String subject,
			String text, boolean html, List<String> pathsToAttachment);
}
