package com.miniproject.hotwords.task;

import com.miniproject.hotwords.controller.AdminController;
import com.miniproject.hotwords.service.IHotWordsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

@Configuration
@EnableScheduling
public class HotWordsScheduleConfig implements SchedulingConfigurer {
    private static final Logger LOG = LoggerFactory.getLogger(HotWordsScheduleConfig.class);
    @Autowired
    private IHotWordsService hotWordsService;
    /**
     * 执行定时任务.
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 注册计时任务到Scheduling接口
        taskRegistrar.addTriggerTask(new Runnable() {
            public void run() {
                LOG.info("task job is running...");
                hotWordsService.createHotWord(true);
            }
        }, new Trigger() {
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron = hotWordsService.getCron(true);
                CronTrigger cronTrigger = new CronTrigger(cron);
                Date nextExec = cronTrigger.nextExecutionTime(triggerContext);
                return nextExec;
            }
        });
    }
}
