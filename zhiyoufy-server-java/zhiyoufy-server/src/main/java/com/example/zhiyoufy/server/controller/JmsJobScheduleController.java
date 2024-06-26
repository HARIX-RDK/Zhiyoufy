package com.example.zhiyoufy.server.controller;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.api.CommonResult;
import com.example.zhiyoufy.common.elk.ElkRecordable;
import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleUpdateParam;
import com.example.zhiyoufy.server.service.JmsJobScheduleService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_SCHEDULE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_SCHEDULE_READ;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Tag.ZHIYOUFY_JMS_JOB_SCHEDULE_WRITE;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_SCHEDULE_ADD;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_SCHEDULE_DEL;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_SCHEDULE_GET_LIST;
import static com.example.zhiyoufy.server.elk.ZhiyoufyElkRecordConsts.Type.ZHIYOUFY_JMS_JOB_SCHEDULE_UPDATE;

@RestController
@RequestMapping("/zhiyoufy-api/v1/job-schedule")
@Slf4j
public class JmsJobScheduleController {
    @Autowired
    JmsJobScheduleService jobScheduleService;

    @ElkRecordable(type = ZHIYOUFY_JMS_JOB_SCHEDULE_ADD,
            tags = {ZHIYOUFY_JMS_JOB_SCHEDULE, ZHIYOUFY_JMS_JOB_SCHEDULE_WRITE})
    @RequestMapping(value = "/add-job-schedule", method = RequestMethod.POST)
    public CommonResult<JmsJobSchedule> addJobSchedule(
            @Validated @RequestBody JmsJobScheduleParam jobScheduleParam) {
        JmsJobSchedule jmsJobSchedule = jobScheduleService.addJobSchedule(jobScheduleParam);

        return CommonResult.success(jmsJobSchedule);
    }

    @ElkRecordable(type = ZHIYOUFY_JMS_JOB_SCHEDULE_DEL,
            tags = {ZHIYOUFY_JMS_JOB_SCHEDULE, ZHIYOUFY_JMS_JOB_SCHEDULE_WRITE})
    @RequestMapping(value = "/del-job-schedule/{id}", method = RequestMethod.DELETE)
    public CommonResult<DeleteInfo> delJobSchedule(@PathVariable Long id) {
        DeleteInfo deleteInfo = jobScheduleService.delJobScheduleById(id);

        return CommonResult.success(deleteInfo);
    }

    @ElkRecordable(type = ZHIYOUFY_JMS_JOB_SCHEDULE_GET_LIST,
            tags = {ZHIYOUFY_JMS_JOB_SCHEDULE, ZHIYOUFY_JMS_JOB_SCHEDULE_READ})
    @RequestMapping(value = "/job-schedule-list", method = RequestMethod.GET)
    public CommonResult<CommonPage<JmsJobSchedule>> getJobScheduleList(
            JmsJobScheduleQueryParam queryParam,
            @RequestParam(value = "projectId") Long projectId,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        CommonPage<JmsJobSchedule> jobSchedulePage =
                jobScheduleService.getJobScheduleList(projectId, queryParam, pageSize, pageNum);

        return CommonResult.success(jobSchedulePage);
    }

    @ElkRecordable(type = ZHIYOUFY_JMS_JOB_SCHEDULE_UPDATE,
            tags = {ZHIYOUFY_JMS_JOB_SCHEDULE, ZHIYOUFY_JMS_JOB_SCHEDULE_WRITE})
    @RequestMapping(value = "/update-job-schedule/{id}", method = RequestMethod.POST)
    public CommonResult<UpdateInfo> updateJobSchedule(@PathVariable Long id,
                                                      @RequestBody JmsJobScheduleUpdateParam updateParam) {
        UpdateInfo updateInfo = jobScheduleService.updateJobSchedule(id, updateParam);

        return CommonResult.success(updateInfo);
    }
}
