package com.example.zhiyoufy.server.aop;

import com.example.zhiyoufy.common.aop.MvcWebLogAspectBase;
import com.example.zhiyoufy.common.elk.AsyncElkRecordable;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.common.elk.IElkRecordFactory;
import com.example.zhiyoufy.common.elk.IElkSwitchManager;
import com.example.zhiyoufy.server.security.UmsAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 用来处理web请求对应elk数据写入到log的Aspect
 */
@Aspect
@Component
@Slf4j
public class ZhiyoufyWebLogAspect extends MvcWebLogAspectBase {
	public ZhiyoufyWebLogAspect(IElkSwitchManager elkSwitchManager,
			IElkRecordFactory elkRecordFactory) {
		super(elkSwitchManager, elkRecordFactory);
	}

	@Pointcut("within(com.example.zhiyoufy.server.controller..*)")
	public void inZhiyoufyController() {
	}

	@Around("inZhiyoufyController() && @annotation(elkRecordable)")
	public Object doAroundAsync(ProceedingJoinPoint joinPoint,
			AsyncElkRecordable elkRecordable) {
		return super.doAroundAsync(joinPoint, elkRecordable);
	}

	@Around("inZhiyoufyController() && @annotation(elkRecordable)")
	public Object doAroundSync(ProceedingJoinPoint joinPoint,
			ElkRecordable elkRecordable) throws Throwable {
		return super.doAroundSync(joinPoint, elkRecordable);
	}

	public String getPrincipalName() {
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (authentication instanceof UmsAuthenticationToken) {
			UmsAuthenticationToken umsAuthenticationToken = (UmsAuthenticationToken)authentication;
			return umsAuthenticationToken.getUserDetails().getUsername();
		}

		return null;
	}
}
