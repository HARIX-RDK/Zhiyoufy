package com.example.zhiyoufy.server.service.impl;

import java.io.File;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.server.service.EmailService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private MailProperties mailProperties;

	@Override
	public void sendMail(String to, String subject,
			String text, boolean html) {
		sendMailWithAttachment(to, subject, text, html, null);
	}

	@Override
	public void sendMailWithAttachment(String to, String subject,
			String text, boolean html, List<String> pathsToAttachment) {
		String tag = RandomUtils.generateShortHexId();
		String logMsg = String.format("tag %s send mail to %s about %s",
				tag, to, subject);

		log.info("Enter to {}", logMsg);

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(
					message, true, "UTF-8");
			helper.setFrom(mailProperties.getUsername());
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, html);

			if (!CollectionUtils.isEmpty(pathsToAttachment)) {
				for (var path : pathsToAttachment) {
					File attachFile = new File(path);
					FileSystemResource fileResource = new FileSystemResource(attachFile);
					helper.addAttachment(attachFile.getName(), fileResource);
				}
			}

			emailSender.send(message);

			log.info("Done to {}", logMsg);
		} catch (MessagingException exception) {
			log.error("Failed to " + logMsg, exception);

			throw new ErrorCodeException(CommonErrorCode.RES_SEND_MAIL_FAILED, exception);
		}
	}
}
