package com.helicalinsight.scheduling;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SchedulerUtility
 * class Create instance of SchedulerFactory
 *
 * @author Prashansa
 * @version 1.1
 * @see ScheduleProcess
 */
public class SchedulerUtility {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerUtility.class);
    private static Scheduler scheduler = null;
    private volatile static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    /**
     * getInstance()
     * @return instance of SchedulerFactory.
     */
    public static Scheduler getInstance() {
        if (scheduler == null) {
            try {
                synchronized (SchedulerUtility.class) {
                    if (scheduler == null) {
                        scheduler = schedulerFactory.getScheduler();
                    }
                }
            } catch (SchedulerException ex) {
                logger.error("Error: ", ex);
            }
        }
        return scheduler;
    }
}
