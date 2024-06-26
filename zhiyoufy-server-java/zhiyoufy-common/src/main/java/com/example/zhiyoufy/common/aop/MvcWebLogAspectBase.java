package com.example.zhiyoufy.common.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.api.Consts;
import com.example.zhiyoufy.common.api.IApiTimeout;
import com.example.zhiyoufy.common.api.IErrorCode;
import com.example.zhiyoufy.common.elk.AsyncElkRecordable;
import com.example.zhiyoufy.common.elk.ElkConsts;
import com.example.zhiyoufy.common.elk.ElkData;
import com.example.zhiyoufy.common.elk.ElkRecordBase;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.common.elk.IElkRecordFactory;
import com.example.zhiyoufy.common.elk.IElkSwitchManager;
import com.example.zhiyoufy.common.elk.StandardElkData;
import com.example.zhiyoufy.common.elk.StandardReqData;
import com.example.zhiyoufy.common.exception.ErrorCodeException;
import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.common.util.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import reactor.core.publisher.Mono;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class MvcWebLogAspectBase {
	private IElkSwitchManager elkSwitchManager;
	private IElkRecordFactory elkRecordFactory;

	public MvcWebLogAspectBase(IElkSwitchManager elkSwitchManager,
			IElkRecordFactory elkRecordFactory) {
		this.elkSwitchManager = elkSwitchManager;
		this.elkRecordFactory = elkRecordFactory;
	}

	private final Map<Method, Integer> requestBodyIndexCache = new ConcurrentHashMap<>(256);

	public Object doAroundAsync(ProceedingJoinPoint joinPoint,
			AsyncElkRecordable elkRecordable) {
		OffsetDateTime recvTimestamp = OffsetDateTime.now();

		//获取当前请求对象
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		String reqId = request.getHeader("x-b3-spanid");
		String rootReqId = request.getHeader("x-b3-traceid");

		if (reqId == null) {
			reqId = RandomUtils.generateShortHexId();
		}

		if (rootReqId == null) {
			rootReqId = reqId;
		}

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		Integer requestBodyIndex = requestBodyIndexCache.computeIfAbsent(
				method, this::getReqBodyIndex);

		Object[] args = joinPoint.getArgs();
		Object reqBody = requestBodyIndex >= 0 ? args[requestBodyIndex] : null;

		long apiTimeoutMs = 60_000L;

		String timeoutMs = request.getHeader(Consts.HttpHeader.TimeoutMs);

		if (timeoutMs != null) {
			apiTimeoutMs = Long.parseLong(timeoutMs);
		} else if (reqBody instanceof IApiTimeout &&
				((IApiTimeout) reqBody).getApiTimeoutMs() != null) {
			apiTimeoutMs = ((IApiTimeout) reqBody).getApiTimeoutMs();
		}

		boolean elkOn = true;

		if (!elkRecordable.defaultOn() && !elkSwitchManager.isElkSwitchAllOn()
				&& !elkSwitchManager.isElkSwitchOn(elkRecordable.type(), elkRecordable.tags())) {
			String anyValue = request.getHeader("x-elk-switch-on");

			if (anyValue == null) {
				elkOn = false;
			}
		}

		StandardReqData standardReqData = null;
		ElkData elkData = null;
		StandardElkData standardElkData = null;
		ElkRecordBase elkRecordBase = null;

		if (elkOn) {
			standardReqData = StandardReqData.builder()
					.recvTimestamp(recvTimestamp)
					.reqSrc(Consts.ReqSrc.RestController)
					.userIp(request.getRemoteAddr())
					.uri(request.getRequestURI())
					.queryString(request.getQueryString())
					.timeoutMs(apiTimeoutMs)
					.build();
			standardElkData = StandardElkData.builder()
					.req(standardReqData)
					.build();
			elkData = ElkData.builder()
					.req(reqBody)
					.build();
			elkRecordBase = elkRecordFactory.createElkRecord();
			elkRecordBase.setRootReqId(rootReqId);
			elkRecordBase.setReqId(reqId);
			elkRecordBase.setType(elkRecordable.type());
			elkRecordBase.setTags(elkRecordable.tags());
			elkRecordBase.setPrincipalName(getPrincipalName());
			elkRecordBase.setStdData(standardElkData);
			elkRecordBase.setElkData(elkData);
		}

		Mono<CommonResult> result = null;

		try {
			result = (Mono<CommonResult>)joinPoint.proceed();

			Assert.notNull(result, "null in function return");
		} catch (Throwable throwable) {
			OffsetDateTime endTimestamp = OffsetDateTime.now();
			long durationMs = Duration.between(recvTimestamp, endTimestamp).toMillis();

			CommonResult commonResult;

			if (throwable instanceof ErrorCodeException) {
				ErrorCodeException errorCodeException = (ErrorCodeException) throwable;
				commonResult = CommonResult.failed(errorCodeException.getErrorCode());
			} else {
				commonResult = buildCommonResultOnThrowable(throwable);
			}

			commonResult.setReqId(reqId);
			commonResult.setCostTimeMs(durationMs);

			if (elkOn) {
				elkRecordBase.setTimestamp(endTimestamp);
				elkRecordBase.setCostTimeMs(durationMs);

				elkData.setRsp(commonResult);

				elkRecordBase.setError(commonResult.getError());

				log.error("{} {}", ElkConsts.JSON_EXTRACT_SUFFIX,
						StrUtils.jsonDumpPublicView(elkRecordBase));
			}

			return Mono.just(commonResult);
		}

		String finalReqId = reqId;
		ElkData finalElkData = elkData;
		boolean finalElkOn = elkOn;
		ElkRecordBase finalElkRecordBase = elkRecordBase;
		return result
				.timeout(Duration.ofMillis(apiTimeoutMs))
				.doOnSuccess(response -> {
					OffsetDateTime endTimestamp = OffsetDateTime.now();
					long durationMs = Duration.between(recvTimestamp, endTimestamp).toMillis();

					response.setReqId(finalReqId);
					response.setCostTimeMs(durationMs);

					if (finalElkOn) {
						finalElkRecordBase.setTimestamp(endTimestamp);
						finalElkRecordBase.setCostTimeMs(durationMs);

						finalElkData.setRsp(response);

						log.info("{} {}", ElkConsts.JSON_EXTRACT_SUFFIX, StrUtils.jsonDumpPublicView(finalElkRecordBase));
					}
				})
				.onErrorResume(throwable -> {
					OffsetDateTime endTimestamp = OffsetDateTime.now();
					long durationMs = Duration.between(recvTimestamp, endTimestamp).toMillis();

					CommonResult commonResult;

					if (throwable instanceof ErrorCodeException) {
						ErrorCodeException errorCodeException = (ErrorCodeException) throwable;
						commonResult = CommonResult.failed(errorCodeException.getErrorCode());
					} else {
						commonResult = buildCommonResultOnThrowable(throwable);
					}

					commonResult.setReqId(finalReqId);
					commonResult.setCostTimeMs(durationMs);

					if (finalElkOn) {
						finalElkRecordBase.setTimestamp(endTimestamp);
						finalElkRecordBase.setCostTimeMs(durationMs);

						finalElkData.setRsp(commonResult);

						finalElkRecordBase.setError(commonResult.getError());

						log.error("{} {}", ElkConsts.JSON_EXTRACT_SUFFIX, StrUtils.jsonDumpPublicView(finalElkRecordBase));
					}

					return Mono.just(commonResult);
				});
	}

	public Object doAroundSync(ProceedingJoinPoint joinPoint,
			ElkRecordable elkRecordable) throws Throwable {
		OffsetDateTime recvTimestamp = OffsetDateTime.now();

		//获取当前请求对象
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();

		String reqId = request.getHeader("x-b3-spanid");
		String rootReqId = request.getHeader("x-b3-traceid");

		if (reqId == null) {
			reqId = RandomUtils.generateShortHexId();
		}

		if (rootReqId == null) {
			rootReqId = reqId;
		}

		Signature signature = joinPoint.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();

		Integer requestBodyIndex = requestBodyIndexCache.computeIfAbsent(
				method, this::getReqBodyIndex);

		Object[] args = joinPoint.getArgs();
		Object reqBody = requestBodyIndex >= 0 ? args[requestBodyIndex] : null;

		boolean elkOn = true;

		if (!elkRecordable.defaultOn() && !elkSwitchManager.isElkSwitchAllOn()
				&& !elkSwitchManager.isElkSwitchOn(elkRecordable.type(), elkRecordable.tags())) {
			String anyValue = request.getHeader("x-elk-switch-on");

			if (anyValue == null) {
				elkOn = false;
			}
		}

		StandardReqData standardReqData = null;
		ElkData elkData = null;
		StandardElkData standardElkData = null;
		ElkRecordBase elkRecordBase = null;

		if (elkOn) {
			standardReqData = StandardReqData.builder()
					.recvTimestamp(recvTimestamp)
					.reqSrc(Consts.ReqSrc.RestController)
					.userIp(request.getRemoteAddr())
					.uri(request.getRequestURI())
					.queryString(request.getQueryString())
					.build();
			standardElkData = StandardElkData.builder()
					.req(standardReqData)
					.build();
			elkData = ElkData.builder()
					.req(reqBody)
					.build();
			elkRecordBase = elkRecordFactory.createElkRecord();
			elkRecordBase.setRootReqId(rootReqId);
			elkRecordBase.setReqId(reqId);
			elkRecordBase.setType(elkRecordable.type());
			elkRecordBase.setTags(elkRecordable.tags());
			elkRecordBase.setPrincipalName(getPrincipalName());
			elkRecordBase.setStdData(standardElkData);
			elkRecordBase.setElkData(elkData);
		}

		Object result = null;
		Throwable metEx = null;
		OffsetDateTime endTimestamp;
		long durationMs = -1;

		try {
			result = joinPoint.proceed();

			endTimestamp = OffsetDateTime.now();
			durationMs = Duration.between(recvTimestamp, endTimestamp).toMillis();

			if (result != null) {
				if (result instanceof CommonResult) {
					CommonResult commonResult = (CommonResult)result;
					commonResult.setReqId(reqId);
					commonResult.setCostTimeMs(durationMs);

					if (elkRecordBase != null) {
						elkRecordBase.setError(commonResult.getError());
					}
				}

				if (elkOn) {
					elkData.setRsp(result);

					elkRecordBase.setTimestamp(endTimestamp);
					elkRecordBase.setCostTimeMs(durationMs);

					log.info("{} {}", ElkConsts.JSON_EXTRACT_SUFFIX,
							StrUtils.jsonDumpPublicView(elkRecordBase));
				}
			}
		} catch (Throwable throwable) {
			log.error("met exception", throwable);

			CommonResult commonResult = buildCommonResultOnThrowable(throwable);
			result = commonResult;

			if (elkOn) {
				endTimestamp = OffsetDateTime.now();
				durationMs = Duration.between(recvTimestamp, endTimestamp).toMillis();

				elkData.setRsp(commonResult);
				elkRecordBase.setError(commonResult.getError());

				elkRecordBase.setTimestamp(endTimestamp);
				elkRecordBase.setCostTimeMs(durationMs);

				log.error("{} {}", ElkConsts.JSON_EXTRACT_SUFFIX,
						StrUtils.jsonDumpPublicView(elkRecordBase));
			}
		}

		return result;
	}

	public CommonResult buildCommonResultOnThrowable(Throwable throwable) {
		IErrorCode errorCode;
		CommonResult commonResult;

		if (throwable instanceof ErrorCodeException) {
			errorCode = ((ErrorCodeException) throwable).getErrorCode();
		} else if (throwable instanceof TimeoutException) {
			errorCode = CommonErrorCode.RES_TIMEOUT;
		} else if (throwable instanceof IllegalArgumentException) {
			errorCode = CommonErrorCode.RES_ILLEGAL_ARGUMENT;
		} else if (throwable instanceof IllegalStateException) {
			errorCode = CommonErrorCode.RES_ILLEGAL_STATE;
		} else {
			errorCode = CommonErrorCode.RES_FAILED;
		}

		commonResult = CommonResult.failed(errorCode);

		return commonResult;
	}

	public String getPrincipalName() {
		return null;
	}

	private Integer getReqBodyIndex(Method method) {
		Parameter[] parameters = method.getParameters();

		for (int i = 0; i < parameters.length; i++) {
			RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
			if (requestBody != null) {
				return i;
			}
		}

		return -1;
	}
}
