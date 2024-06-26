package com.example.zhiyoufy.server.service;

public interface UmsIdentificationCodeService {
	String generateIdentificationCode(String idKey);
	String getIdentificationCode(String idKey);
	void removeIdentificationCode(String idKey);
}
