package com.example.zhiyoufy.server.config;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.example.zhiyoufy.common.api.CommonPage;
import com.example.zhiyoufy.common.config.ZhiyoufyCommonAutoConfiguration;
import com.example.zhiyoufy.common.config.ZhiyoufyCommonProperties;
import com.example.zhiyoufy.common.util.StrUtils;
import com.example.zhiyoufy.server.domain.dto.jms.JmsActiveJobRunBase;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobChildRunResultInd;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultFull;
import com.example.zhiyoufy.server.domain.dto.jms.JmsJobRunResultQueryParam;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobChildRunRsp;
import com.example.zhiyoufy.server.domain.dto.jms.JmsStartJobRunParam;
import com.example.zhiyoufy.server.manager.activejobrun.ActiveJobRunManager;
import com.example.zhiyoufy.server.manager.ActiveWorkerManager;
import com.example.zhiyoufy.server.manager.activejobrun.JmsActiveJobRun;
import com.example.zhiyoufy.server.manager.finishedjobrun.FinishedJobRunResultStore;
import com.example.zhiyoufy.server.service.EmsConfigCollectionCacheService;
import com.example.zhiyoufy.server.service.EmsConfigCollectionService;
import com.example.zhiyoufy.server.service.EmsConfigItemService;
import com.example.zhiyoufy.server.service.EmsConfigSingleCacheService;
import com.example.zhiyoufy.server.service.EmsConfigSingleService;
import com.example.zhiyoufy.server.service.EmsEnvironmentCacheService;
import com.example.zhiyoufy.server.service.EmsEnvironmentLockService;
import com.example.zhiyoufy.server.service.EmsEnvironmentService;
import com.example.zhiyoufy.server.service.JmsJobRunService;
import com.example.zhiyoufy.server.service.PmsJobFolderService;
import com.example.zhiyoufy.server.service.PmsJobTemplateService;
import com.example.zhiyoufy.server.service.PmsProjectCacheService;
import com.example.zhiyoufy.server.service.PmsProjectService;
import com.example.zhiyoufy.server.service.UmsIdentificationCodeService;
import com.example.zhiyoufy.server.service.UmsUserCacheService;
import com.example.zhiyoufy.server.service.UmsUserService;
import com.example.zhiyoufy.server.service.WmsGroupTokenService;
import com.example.zhiyoufy.server.service.WmsWorkerAppCacheService;
import com.example.zhiyoufy.server.service.WmsWorkerAppService;
import com.example.zhiyoufy.server.service.WmsWorkerGroupCacheService;
import com.example.zhiyoufy.server.service.WmsWorkerGroupService;
import com.example.zhiyoufy.server.service.impl.EmsConfigCollectionCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsConfigCollectionServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsConfigItemServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsConfigSingleCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsConfigSingleServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsEnvironmentCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsEnvironmentLockServiceImpl;
import com.example.zhiyoufy.server.service.impl.EmsEnvironmentServiceImpl;
import com.example.zhiyoufy.server.service.impl.JmsJobRunServiceImpl;
import com.example.zhiyoufy.server.service.impl.PmsJobFolderServiceImpl;
import com.example.zhiyoufy.server.service.impl.PmsJobTemplateServiceImpl;
import com.example.zhiyoufy.server.service.impl.PmsProjectCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.PmsProjectServiceImpl;
import com.example.zhiyoufy.server.service.impl.UmsIdentificationCodeServiceImpl;
import com.example.zhiyoufy.server.service.impl.UmsUserCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.UmsUserServiceImpl;
import com.example.zhiyoufy.server.service.impl.WmsGroupTokenServiceImpl;
import com.example.zhiyoufy.server.service.impl.WmsWorkerAppCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.WmsWorkerAppServiceImpl;
import com.example.zhiyoufy.server.service.impl.WmsWorkerGroupCacheServiceImpl;
import com.example.zhiyoufy.server.service.impl.WmsWorkerGroupServiceImpl;
import com.example.zhiyoufy.server.support.service.ServiceTestHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@TestConfiguration
@EnableConfigurationProperties({ZhiyoufyCommonProperties.class, ZhiyoufyServerProperties.class})
@Import({
        WebClientAutoConfiguration.class,
        MetricsAutoConfiguration.class,
        CompositeMeterRegistryAutoConfiguration.class,
        ZhiyoufyCommonAutoConfiguration.class,
        MapperTestConfig.class,
        ElasticsearchConfig.class,
        PageHelperAutoConfiguration.class,
        MailTestConfig.class,
        BaseTestConfig.class,
        WebSocketConfig.class
})
public class ServiceTestConfig {
    @Bean
    ObjectMapper objectMapper() {
        return StrUtils.objectMapper;
    }

    @Bean
    ServiceTestHelper serviceTestHelper() {
        return new ServiceTestHelper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UmsUserService umsUserService() {
        return new UmsUserServiceImpl();
    }

    @Bean
    UmsUserCacheService umsUserCacheService() {
        return new UmsUserCacheServiceImpl();
    }

    @Bean
    UmsIdentificationCodeService umsIdentificationCodeService() {
        return new UmsIdentificationCodeServiceImpl();
    }

    @Bean
    EmsEnvironmentService emsEnvironmentService() {
        return new EmsEnvironmentServiceImpl();
    }

    @Bean
    EmsEnvironmentCacheService emsEnvironmentCacheService() {
        return new EmsEnvironmentCacheServiceImpl();
    }

    @Bean
    EmsConfigSingleService emsConfigSingleService() {
        return new EmsConfigSingleServiceImpl();
    }

    @Bean
    EmsConfigSingleCacheService emsConfigSingleCacheService() {
        return new EmsConfigSingleCacheServiceImpl();
    }

    @Bean
    EmsConfigCollectionService emsConfigCollectionService() {
        return new EmsConfigCollectionServiceImpl();
    }

    @Bean
    EmsConfigCollectionCacheService emsConfigCollectionCacheService() {
        return new EmsConfigCollectionCacheServiceImpl();
    }

    @Bean
    EmsConfigItemService emsConfigItemService() {
        return new EmsConfigItemServiceImpl();
    }

    @Bean
    PmsProjectService pmsProjectService() {
        return new PmsProjectServiceImpl();
    }

    @Bean
    PmsProjectCacheService pmsProjectCacheService() {
        return new PmsProjectCacheServiceImpl();
    }

    @Bean
    PmsJobFolderService pmsJobFolderService() {
        return new PmsJobFolderServiceImpl();
    }

    @Bean
    PmsJobTemplateService pmsJobTemplateService() {
        return new PmsJobTemplateServiceImpl();
    }

    @Bean
    WmsWorkerAppService wmsWorkerAppService() {
        return new WmsWorkerAppServiceImpl();
    }

    @Bean
    WmsWorkerAppCacheService wmsWorkerAppCacheService() {
        return new WmsWorkerAppCacheServiceImpl();
    }

    @Bean
    WmsWorkerGroupService wmsWorkerGroupService() {
        return new WmsWorkerGroupServiceImpl();
    }

    @Bean
    WmsWorkerGroupCacheService wmsWorkerGroupCacheService() {
        return new WmsWorkerGroupCacheServiceImpl();
    }

    @Bean
    WmsGroupTokenService wmsGroupTokenService() {
        return new WmsGroupTokenServiceImpl();
    }

    @Bean
    EmsEnvironmentLockService emsEnvironmentLockService() {
        return new EmsEnvironmentLockServiceImpl();
    }

    @Bean
    EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    @Qualifier("zhiyoufy-executor")
    ExecutorService zhiyoufyExecutorService(ZhiyoufyServerProperties properties) {
        ExecutorService executorService =
                Executors.newFixedThreadPool(
                        properties.getThreadPoolSize(),
                        new CustomizableThreadFactory("zhiyoufy-executor-"));
        return executorService;
    }

    @Bean
    @Qualifier("zhiyoufy-timer")
    ScheduledExecutorService zhiyoufyScheduledExecutorService(ZhiyoufyServerProperties properties) {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(
                        properties.getTimerThreadPoolSize(),
                        new CustomizableThreadFactory("zhiyoufy-timer-"));
        return scheduledExecutorService;
    }

    @Bean
    ActiveWorkerManager activeWorkerManager(EventBus eventBus) {
        return new ActiveWorkerManager(eventBus);
    }

    @Bean
    ActiveJobRunManager activeJobRunManager() {
        return new ActiveJobRunManager();
    }

    @Bean
    JmsJobRunService jobRunService() {
        return new JmsJobRunServiceImpl();
    }

    @Bean
    FinishedJobRunResultStore finishedJobRunResultStore() {
        return new FinishedJobRunResultStore();
    }
}

