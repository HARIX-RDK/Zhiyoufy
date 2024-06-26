package com.example.zhiyoufy.server.manager.finishedjobrun;

import com.example.zhiyoufy.server.config.SpringContext;
import com.example.zhiyoufy.server.config.ZhiyoufyServerProperties;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
@Slf4j
public class FinishedJobRunResultStore {

    private List<JmsJobRunResultFull> jmsJobRunResultList = new ArrayList<>();

    public JmsJobRunResultFull getFinishedJobRunResult(String runGuid) {
        for (var jobRunResult : jmsJobRunResultList) {
            if (jobRunResult.getRunGuid().equals(runGuid)) {
                return jobRunResult;
            }
        }
        return null;
    }

    public void addJobRunResult(JmsJobRunResultFull jobRunResult) {
        jmsJobRunResultList.add(0, jobRunResult);
        int maxKeepSize = getZhiyoufyProperties().getFinishedJobRunResultKeepSize();
        if(jmsJobRunResultList.size() > maxKeepSize) {
            jmsJobRunResultList.remove((jmsJobRunResultList.size()-1));
        }
    }

    private ZhiyoufyServerProperties getZhiyoufyProperties() {
        return SpringContext.getInstance().getZhiyoufyServerProperties();
    }
}
