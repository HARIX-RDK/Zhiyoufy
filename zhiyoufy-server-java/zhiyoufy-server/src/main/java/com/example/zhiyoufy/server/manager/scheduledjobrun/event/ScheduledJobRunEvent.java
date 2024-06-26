package com.example.zhiyoufy.server.manager.scheduledjobrun.event;

import com.example.zhiyoufy.common.util.RandomUtils;
import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;


@Setter
@Getter
@Slf4j
public class ScheduledJobRunEvent {
    public final static String EVENT_TYPE = "ScheduledJobRunEvent";

    private String eventType;
    private JmsJobSchedule jmsJobSchedule;
    CronExpression cronTrigger;

    public ScheduledJobRunEvent(JmsJobSchedule jmsJobSchedule) {

        this.setEventType(EVENT_TYPE);
        this.setJmsJobSchedule(jmsJobSchedule);

        String crontabConfig = jmsJobSchedule.getCrontabConfig();
        cronTrigger = CronExpression.parse(crontabConfig);
    }

    public LocalDateTime getNextFireTime(LocalDateTime temporalTime) {

        return cronTrigger.next(temporalTime);
    }

    public Long getScheduleId() {
        return jmsJobSchedule.getId();
    }

    public void onFire(ActiveJobRunManager activeJobRunManager) {

        log.info("onFire entered, schedule name: {}", jmsJobSchedule.getName());
        String scheduleName = jmsJobSchedule.getName();
        String runGuid = String.format("%s_%s", scheduleName, RandomUtils.generateHexId());

        try {
            String userName;
            if (StringUtils.hasText(jmsJobSchedule.getModifiedBy())) {
                userName = jmsJobSchedule.getModifiedBy();
            } else {
                userName = jmsJobSchedule.getCreatedBy();
            }
            JmsStartJobRunParam startJobRunParam = JmsStartJobRunParam.builder()
                    .projectId(jmsJobSchedule.getProjectId())
                    .projectName(jmsJobSchedule.getProjectName())
                    .runTag(jmsJobSchedule.getRunTag())
                    .runNum(jmsJobSchedule.getRunNum())
                    .parallelNum(jmsJobSchedule.getParallelNum())
                    .environmentId(jmsJobSchedule.getEnvironmentId())
                    .environmentName(jmsJobSchedule.getEnvironmentName())
                    .templateId(jmsJobSchedule.getTemplateId())
                    .templateName(jmsJobSchedule.getTemplateName())
                    .addTags(jmsJobSchedule.getAddTags())
                    .includeTags(jmsJobSchedule.getIncludeTags())
                    .excludeTags(jmsJobSchedule.getExcludeTags())
                    .removeTags(jmsJobSchedule.getRemoveTags())
                    .workerAppId(jmsJobSchedule.getWorkerAppId())
                    .workerAppName(jmsJobSchedule.getWorkerAppName())
                    .workerGroupId(jmsJobSchedule.getWorkerGroupId())
                    .workerGroupName(jmsJobSchedule.getWorkerGroupName())
                    .runGuid(runGuid)
                    .username(userName)
                    .build();

            activeJobRunManager.startJobRun(startJobRunParam);
        } catch(Exception e) {
            log.error("fail to start schedule job: {}, exception is {}", scheduleName, e);
        }
    }
}
