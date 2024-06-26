package com.example.zhiyoufy.server.controller;

import java.util.List;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunUpdatePerfParallelNumReq;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.service.JmsJobRunService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_RUN;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_RUN_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_RUN_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_GET_ACTIVE_BASE_SINGLE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_GET_SINGLE_RESULT;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_GET_ACTIVE_BASE_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_GET_CHILD_RESULT_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_GET_RESULT_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_START;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_STOP;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_RUN_UPDATE_PERF_PARALLEL_NUM;

@RestController
@RequestMapping("/zhiyoufy-api/v1/job-run")
@Slf4j
public class JmsJobRunController {

	@Autowired
	JmsJobRunService jobRunService;

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_START,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_WRITE})
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public CommonResult startJobRun(
			@Validated @RequestBody JmsStartJobRunParam startJobRunParam) {
		jobRunService.startJobRun(startJobRunParam);

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_STOP,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_WRITE})
	@RequestMapping(value = "/stop/{runGuid}", method = RequestMethod.POST)
	public CommonResult stopJobRun(@PathVariable String runGuid) {
		jobRunService.stopJobRun(runGuid);

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_UPDATE_PERF_PARALLEL_NUM,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_WRITE})
	@RequestMapping(value = "/updatePerfParallelNum", method = RequestMethod.POST)
	public CommonResult updatePerfParallelNum(
			@Validated @RequestBody JmsJobRunUpdatePerfParallelNumReq updateParallelNumReq) {
		jobRunService.updatePerfParallelNum(updateParallelNumReq);

		return CommonResult.success();
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_GET_ACTIVE_BASE_LIST,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_READ})
	@RequestMapping(value = "/active-base-list", method = RequestMethod.GET)
	public CommonResult<List<JmsActiveJobRunBase>> getActiveJobRunBaseList(
			@RequestParam(value = "allUsers", required = false) Boolean allUsers) {
		List<JmsActiveJobRunBase> activeJobRunBaseList =
				jobRunService.getActiveJobRunBaseList(allUsers);

		return CommonResult.success(activeJobRunBaseList);
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_GET_ACTIVE_BASE_SINGLE,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_READ})
	@RequestMapping(value = "/active-base/{runGuid}", method = RequestMethod.GET)
	public CommonResult<JmsActiveJobRunBase> getActiveJobRunBaseSingle(
			@PathVariable String runGuid) {
		JmsActiveJobRunBase activeJobRunBase =
				jobRunService.getActiveJobRunBaseSingle(runGuid);

		return CommonResult.success(activeJobRunBase);
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_GET_SINGLE_RESULT,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_READ})
	@RequestMapping(value = "/result/{runGuid}", method = RequestMethod.GET)
	public CommonResult<JmsJobRunResultFull> getJobRunResult(@PathVariable String runGuid) {
		JmsJobRunResultFull jobRunResult = jobRunService.getJobRunResult(runGuid);

		return CommonResult.success(jobRunResult);
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_GET_RESULT_LIST,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_READ})
	@RequestMapping(value = "/result-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<JmsJobRunResultFull>> getJobRunResultList(
			JmsJobRunResultQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<JmsJobRunResultFull> page = jobRunService.getJobRunResultList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(page);
	}

	@ElkRecordable(type = ZHIYOUFY_JMS_JOB_RUN_GET_CHILD_RESULT_LIST,
			tags = {ZHIYOUFY_JMS_JOB_RUN, ZHIYOUFY_JMS_JOB_RUN_READ})
	@RequestMapping(value = "/child-result-list", method = RequestMethod.GET)
	public CommonResult<CommonPage<JmsJobChildRunResultFull>> getJobChildRunResultList(
			JmsJobChildRunResultQueryParam queryParam,
			@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
			@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
		CommonPage<JmsJobChildRunResultFull> page = jobRunService.getJobChildRunResultList(
				queryParam, pageSize, pageNum);

		return CommonResult.success(page);
	}
}
