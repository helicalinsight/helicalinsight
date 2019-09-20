/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.scheduling;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create instance of SchedulerFactory
 *
 * @author Prashansa
 * @see ScheduleProcess
 */
public class SchedulerUtility {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerUtility.class);
    private static Scheduler scheduler = null;
    private volatile static SchedulerFactory schedulerFactory = new StdSchedulerFactory();

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
