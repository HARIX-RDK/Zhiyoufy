package com.example.zhiyoufy.server;

import java.util.TimeZone;

import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.server.service.JmsJobScheduleService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ZhiyoufyServerApplication implements CommandLineRunner {

	@Autowired
	private JmsJobScheduleService jobScheduleService;

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(ZhiyoufyServerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("run Enter");

		jobScheduleService.loadScheduledJobs();
	}
}
