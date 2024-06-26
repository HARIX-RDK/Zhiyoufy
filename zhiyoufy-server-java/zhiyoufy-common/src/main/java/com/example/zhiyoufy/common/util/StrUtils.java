package com.example.zhiyoufy.common.util;

import java.io.IOException;

import com.example.zhiyoufy.common.util.jsonviews.PrivateView;
import com.example.zhiyoufy.common.util.jsonviews.PublicView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 字符串工具类
 */
public class StrUtils {
	public final static ObjectMapper objectMapper = initMapper();

	public static ObjectMapper initMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

		return mapper;
	}

	public static String jsonDump(Object obj) {
		String json = null;
		try {
			json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String jsonDumpPrivateView(Object obj) {
		String json = null;
		try {
			json = objectMapper.writerWithView(PrivateView.class)
					.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String jsonDumpPublicView(Object obj) {
		String json = null;
		try {
			json = objectMapper.writerWithView(PublicView.class)
					.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

	public static String toPrettyFormat(String json) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			Object jsonObject = mapper.readValue(json, Object.class);
			String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
			return prettyJson;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
