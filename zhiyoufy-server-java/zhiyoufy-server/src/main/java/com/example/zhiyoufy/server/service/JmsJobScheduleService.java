package com.example.zhiyoufy.server.service;


import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.mbg.model.JmsJobSchedule;
import com.example.zhiyoufy.server.domain.dto.common.DeleteInfo;
import com.example.zhiyoufy.server.domain.dto.common.UpdateInfo;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobScheduleUpdateParam;

public interface JmsJobScheduleService {
    JmsJobSchedule addJobSchedule(JmsJobScheduleParam jobScheduleParam);

    DeleteInfo delJobScheduleById(Long id);

    CommonPage<JmsJobSchedule> getJobScheduleList(Long projectId,
                                                  JmsJobScheduleQueryParam queryParam,
                                                  Integer pageSize, Integer pageNum);

    UpdateInfo updateJobSchedule(Long id, JmsJobScheduleUpdateParam updateParam);

    JmsJobSchedule getJobScheduleById(Long id);

    void loadScheduledJobs();
}
