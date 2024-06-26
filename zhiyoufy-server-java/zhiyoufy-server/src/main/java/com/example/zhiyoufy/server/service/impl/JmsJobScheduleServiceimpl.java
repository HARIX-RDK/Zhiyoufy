package com.example.zhiyoufy.server.service.impl;

import com.example.zhiyoufy.common.api.CommonErrorCode;
import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.exception.Asserts;
import com.example.zhiyoufy.common.util.CheckUtils;
import com.example.zhiyoufy.mbg.mapper.JmsJobScheduleMapper;
import com.example.zhiyoufy.mbg.model.*;
import com.example.zhiyoufy.server.domain.bo.ums.UmsUserDetails;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleUpdateParam;
import com.example.zhiyoufy.server.manager.scheduledjobrun.ScheduledJobRunManager;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.AddScheduleJobEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.DeleteScheduleJobEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.ScheduledJobRunEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.UpdateScheduleJobEvent;
import com.example.zhiyoufy.server.mapstruct.JmsJobScheduleStructMapper;
import com.example.zhiyoufy.server.service.*;
import com.github.pagehelper.PageHelper;
import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Getter
@Setter
@Slf4j
public class JmsJobScheduleServiceimpl implements JmsJobScheduleService {
    @Autowired
    PmsProjectService projectService;

    @Autowired
    UmsUserService userService;

    @Autowired
    JmsJobScheduleMapper jobScheduleMapper;

    @Autowired
    ScheduledJobRunManager scheduledJobRunManager;

    @Autowired
    EventBus eventBus;

    @Override
    public JmsJobSchedule addJobSchedule(JmsJobScheduleParam jobScheduleParam) {

        Assert.notNull(jobScheduleParam, "jobTemplateParam is null");

        String crontabConfig = jobScheduleParam.getCrontabConfig();
        boolean isValid = CronExpression.isValidExpression(crontabConfig);
        Assert.isTrue(isValid, "invalid crontabConfig");

        Long projectId = jobScheduleParam.getProjectId();

        PmsProject pmsProject = projectService.getProjectById(projectId);

        if (pmsProject == null ||
                !pmsProject.getName().equals(jobScheduleParam.getProjectName())) {
            Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
        }

        UmsUserDetails userDetails = userService.getUserDetails();

        if (!userDetails.isAdmin()) {
            PmsProjectUserRelation relation = projectService
                    .getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

            if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
                    CheckUtils.isTrue(relation.getIsEditor()))) {
                Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
            }
        }

        // add JmsJobSchedule
        JmsJobSchedule jobSchedule = JmsJobScheduleStructMapper.INSTANCE
                .jmsJobScheduleParamToJmsJobSchedule(jobScheduleParam);
        jobSchedule.setCreatedBy(userDetails.getUsername());
        jobSchedule.setModifiedBy(userDetails.getUsername());
        jobSchedule.setCreatedTime(new Date());


        jobScheduleMapper.insertSelective(jobSchedule);

        JmsJobSchedule addJmsJobSchedule = JmsJobScheduleStructMapper.INSTANCE
                .jmsJobScheduleToJmsJobSchedule(jobSchedule);

        AddScheduleJobEvent addScheduleJobEvent = AddScheduleJobEvent.builder()
                .jmsJobSchedule(addJmsJobSchedule)
                .build();

        eventBus.post(addScheduleJobEvent);

        return jobSchedule;
    }

    @Override
    public DeleteInfo delJobScheduleById(Long id) {

        JmsJobSchedule jobSchedule = getJobScheduleById(id);

        if (jobSchedule == null) {
            Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
        }

        Long projectId = jobSchedule.getProjectId();

        UmsUserDetails userDetails = userService.getUserDetails();

        if (!userDetails.isAdmin()) {
            PmsProjectUserRelation relation = projectService
                    .getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

            if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
                    CheckUtils.isTrue(relation.getIsEditor()))) {
                Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
            }
        }

        int deleted = jobScheduleMapper.deleteByPrimaryKey(id);

        DeleteInfo deleteInfo = DeleteInfo.builder()
                .deleted(deleted)
                .name(jobSchedule.getName())
                .projectId(projectId)
                .build();

        DeleteScheduleJobEvent deleteScheduleJobEvent = DeleteScheduleJobEvent.builder()
                .id(jobSchedule.getId())
                .name(jobSchedule.getName())
                .build();

        eventBus.post(deleteScheduleJobEvent);

        return deleteInfo;
    }

    @Override
    @PreAuthorize("hasAuthority('roles/project.owner') "
            + "|| hasAuthority('roles/project.editor') "
            + "|| hasAuthority('roles/project.viewer')")
    public CommonPage<JmsJobSchedule> getJobScheduleList(Long projectId, JmsJobScheduleQueryParam queryParam, Integer pageSize, Integer pageNum) {

        UmsUserDetails userDetails = userService.getUserDetails();

        if (!userDetails.isAdmin()) {
            PmsProjectUserRelation relation = projectService
                    .getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

            if (relation == null) {
                Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
            }
        }

        JmsJobScheduleExample example = new JmsJobScheduleExample();
        JmsJobScheduleExample.Criteria criteria = example.createCriteria();

        criteria.andProjectIdEqualTo(projectId);

        if (queryParam.getEnvironmentId() != null) {
            criteria.andEnvironmentIdEqualTo(queryParam.getEnvironmentId());
        }

        if(StringUtils.hasText(queryParam.getTemplateName())) {
            criteria.andTemplateNameEqualTo(queryParam.getTemplateName());
        }

        if(StringUtils.hasText(queryParam.getRunTag())) {
            criteria.andRunTagEqualTo(queryParam.getRunTag());
        }

        PageHelper.startPage(pageNum, pageSize);

        List<JmsJobSchedule> jobScheduleList = jobScheduleMapper.selectByExample(example);

        return CommonPage.restPage(jobScheduleList);

    }

    @Override
    public UpdateInfo updateJobSchedule(Long id, JmsJobScheduleUpdateParam updateParam)
    {

        Assert.notNull(updateParam, "updateParam is null");

        String crontabConfig = updateParam.getCrontabConfig();
        if(StringUtils.hasText(crontabConfig)) {
            boolean isValid = CronExpression.isValidExpression(crontabConfig);
            Assert.isTrue(isValid, "invalid crontabConfig");
        }

        JmsJobSchedule targetJobSchedule = getJobScheduleById(id);

        if (targetJobSchedule == null) {
            Asserts.fail(CommonErrorCode.RES_ILLEGAL_ARGUMENT);
        }

        Long projectId = targetJobSchedule.getProjectId();

        UmsUserDetails userDetails = userService.getUserDetails();

        if (!userDetails.isAdmin()) {
            PmsProjectUserRelation relation = projectService
                    .getProjectUserRelationByProjectIdAndUserId(projectId, userDetails.getUserId());

            if (relation == null || !(CheckUtils.isTrue(relation.getIsOwner()) ||
                    CheckUtils.isTrue(relation.getIsEditor()))) {
                Asserts.fail(CommonErrorCode.RES_FORBIDDEN);
            }
        }

        JmsJobSchedule jobSchedule = JmsJobScheduleStructMapper.INSTANCE
                .jmsJobScheduleUpdateParamToJmsJobSchedule(updateParam);
        jobSchedule.setId(id);
        jobSchedule.setModifiedBy(userDetails.getUsername());
        jobSchedule.setModifiedTime(new Date());

        int updated = jobScheduleMapper.updateByPrimaryKeySelective(jobSchedule);

        UpdateInfo updateInfo = UpdateInfo.builder()
                .updated(updated)
                .name(jobSchedule.getName())
                .projectId(projectId)
                .build();

        JmsJobSchedule JobScheduleAfterUpdated = getJobScheduleById(id);

        UpdateScheduleJobEvent updateScheduleJobEvent = UpdateScheduleJobEvent.builder()
               .jmsJobSchedule(JobScheduleAfterUpdated)
                .build();

        eventBus.post(updateScheduleJobEvent);

        return updateInfo;
    }

    @Override
    public JmsJobSchedule getJobScheduleById(Long id) {
        JmsJobSchedule jobSchedule = jobScheduleMapper.selectByPrimaryKey(id);

        return jobSchedule;
    }


    @Override
    public void loadScheduledJobs(){
        List<JmsJobSchedule> jobScheduleList = jobScheduleMapper.selectByExample(null);
        if(jobScheduleList != null) {
            log.info("scheduled jobs count:{}", jobScheduleList.size());
            for(JmsJobSchedule jobSchedule : jobScheduleList) {
                try {
                    ScheduledJobRunEvent scheduledJobRunEvent = new ScheduledJobRunEvent(jobSchedule);
                    scheduledJobRunManager.addScheduledJob(scheduledJobRunEvent);
                }
                catch(Exception e) {
                    log.error("fail to load schedule event: {}, exception: {}", jobSchedule.getName(), e);
                }
            }
        }
    }
}
