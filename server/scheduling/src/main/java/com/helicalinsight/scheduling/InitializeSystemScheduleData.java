package com.helicalinsight.scheduling;

import com.helicalinsight.efw.components.ApplicationValuesInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Initializes system-level Quartz schedules from systemschedule.xml at application startup.
 */
@Component("initializeSystemScheduleData")
public class InitializeSystemScheduleData implements ApplicationValuesInitializer {

    private static final Logger logger = LoggerFactory.getLogger(InitializeSystemScheduleData.class);

    @Override
    public void initializeData(ApplicationContext applicationContext) {
        try {
            new SystemScheduleLoader().loadAll();
        } catch (Exception ex) {
            logger.error("Failed to initialize system schedules", ex);
        }
    }
}
