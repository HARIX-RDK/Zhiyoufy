package com.example.zhiyoufy.server.manager.scheduledjobrun;

import com.example.zhiyoufy.server.config.ZhiyoufyServerProperties;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.AddScheduleJobEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.DeleteScheduleJobEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.ScheduledJobRunEvent;
import com.example.zhiyoufy.server.manager.scheduledjobrun.event.UpdateScheduleJobEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
@Getter
@Setter
@Slf4j
public class ScheduledJobRunManager {

    @Autowired
    ScheduledExecutorService zhiyoufyScheduledExecutorService;

    @Autowired
    ActiveJobRunManager activeJobRunManager;

    ScheduledFuture<?> processTimer = null;

    EventBus eventBus;

    private long processTimerTargetTime = 0L;

    private final Object scheduledJobEventLock = new Object();

    private List<ScheduledJobRunEvent> scheduledJobEventList = new ArrayList<>();
    private List<Long> scheduledJobFireTimeList = new ArrayList<>();
    private Map<Long, ScheduledJobRunEvent> scheduledJobEventMap = new ConcurrentHashMap<>();

    private ZoneId timeZone;
    private ZoneOffset zoneOffset;


    public ScheduledJobRunManager(EventBus eventBus, ZhiyoufyServerProperties zhiyoufyServerProperties) {
        this.eventBus = eventBus;
        eventBus.register(this);

        timeZone = TimeZone.getTimeZone(zhiyoufyServerProperties.getTimeZone()).toZoneId();
        LocalDateTime curTime = LocalDateTime.now(timeZone);
        zoneOffset = timeZone.getRules().getOffset(curTime);

    }

    @Subscribe
    public void onAddScheduleEvent(AddScheduleJobEvent event){
        log.info("onAddScheduleEvent entered");

        ScheduledJobRunEvent scheduledJobRunEvent = new ScheduledJobRunEvent(event.getJmsJobSchedule());
        addScheduledJob(scheduledJobRunEvent);
    }

    @Subscribe
    public void onDeleteScheduleEvent(DeleteScheduleJobEvent event) {
        log.info("DeleteScheduleJobEvent entered, id={}, name={}", event.getId(), event.getName());
        delScheduledJob(event.getId());
    }

    @Subscribe
    public void onUpdateScheuduleEvent(UpdateScheduleJobEvent event) {
        log.info("UpdateScheduleJobEvent entered");

        delScheduledJob(event.getJmsJobSchedule().getId());

        ScheduledJobRunEvent scheduledJobRunEvent = new ScheduledJobRunEvent(event.getJmsJobSchedule());
        addScheduledJob(scheduledJobRunEvent);
    }

    public void addScheduledJob(ScheduledJobRunEvent scheduledJobEvent) {
        synchronized (scheduledJobEventLock) {
            pushToScheduledJobQueue(scheduledJobEvent, null);
        }
    }

    public void delScheduledJob(long id) {
        synchronized (scheduledJobEventLock) {
            ScheduledJobRunEvent scheduledJobRunEvent = scheduledJobEventMap.get(id);
            if (scheduledJobRunEvent == null) {
                return;
            }
            int index = scheduledJobEventList.indexOf(scheduledJobRunEvent);
            scheduledJobFireTimeList.remove(index);
            scheduledJobEventList.remove(index);
            scheduledJobEventMap.remove(id);
        }
    }

    private void pushToScheduledJobQueue(ScheduledJobRunEvent scheduledJobEvent, Long curFireTimeSeconds) {

        LocalDateTime curTime = LocalDateTime.now(timeZone);
        long curTimeSeconds = curTime.toEpochSecond(zoneOffset);

        LocalDateTime fireTime = scheduledJobEvent.getNextFireTime(curTime);
        long fireTimeSeconds = fireTime.toEpochSecond(zoneOffset);

        if (curFireTimeSeconds != null && curFireTimeSeconds == fireTimeSeconds) {
            fireTimeSeconds = scheduledJobEvent.getNextFireTime(curTime).toEpochSecond(zoneOffset);
        }

        int insertIndex = Arrays.binarySearch(scheduledJobFireTimeList.toArray(), fireTimeSeconds);

        insertIndex = Math.abs(insertIndex + 1);

        scheduledJobFireTimeList.add(insertIndex, fireTimeSeconds);
        scheduledJobEventList.add(insertIndex, scheduledJobEvent);
        scheduledJobEventMap.put(scheduledJobEvent.getScheduleId(), scheduledJobEvent);

        if(processTimer != null && processTimerTargetTime > fireTimeSeconds) {
            processTimer.cancel(false);
            processTimer = null;
        }

        if(processTimer == null) {
            processTimerTargetTime = fireTimeSeconds;
            long timeout = fireTimeSeconds - curTimeSeconds;

            processTimer = zhiyoufyScheduledExecutorService.schedule(
                    this::processScheduledJobs,
                    timeout,
                    TimeUnit.SECONDS);
        }
    }

    private void processScheduledJobs() {
        synchronized (scheduledJobEventLock) {
            processTimer.cancel(false);
            processTimer = null;

            while (!scheduledJobFireTimeList.isEmpty()) {
                Instant curTime = Instant.now();
                long curTimeSeconds = curTime.getEpochSecond();
                ScheduledJobRunEvent firedEvent = null;

                long firstFireTimeSeconds = scheduledJobFireTimeList.get(0);
                if (curTimeSeconds >= firstFireTimeSeconds) {
                    firedEvent = scheduledJobEventList.get(0);
                    scheduledJobFireTimeList.remove(0);
                    scheduledJobEventList.remove(0);
                }

                if(firedEvent != null) {
                    scheduledJobEventMap.remove(firedEvent.getScheduleId());
                    pushToScheduledJobQueue(firedEvent, firstFireTimeSeconds);
                    firedEvent.onFire(activeJobRunManager);
                    continue;
                }

                processTimerTargetTime = firstFireTimeSeconds;
                long timeout = processTimerTargetTime - curTimeSeconds;
                processTimer = zhiyoufyScheduledExecutorService.schedule(
                        this::processScheduledJobs,
                        timeout,
                        TimeUnit.SECONDS);

                return;
            }
        }
    }
}
