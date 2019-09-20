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

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.TimeZone;

/**
 * <p>
 * This class is responsible to create instance for Trigger
 * </p>
 *
 * @author Prashansa
 * @see ScheduleJob
 */
public class TriggerUtility {
    private static final Logger logger = LoggerFactory.getLogger(TriggerUtility.class);
    private Trigger trigger = null;

    /**
     * <p>
     * This method is responsible for creating Trigger instance on the basis of
     * cronExpression Start date and EndDate.
     * </p>
     *
     * @param cronExpression contains <code>String</code> of cron expression
     * @param jobName        contains <code>String</code> use as a trigger group
     * @param startDate      contains <code>String</code> of start date
     * @param endDate        contains <code>String</code> of end date expression
     *                       <p>
     *                       if endDate contains null or "" then trigger will not consider
     *                       endDate it will end according to cron expression
     *                       </p>
     * @return Trigger
     */
    public Trigger getInstance(String cronExpression, String jobName, Date startDate, Date endDate, String timeZone) {
        if (trigger == null) {
            if (endDate != null) {
                if (endDate.compareTo(startDate) >= 0) {
                    logger.info("End date is greater than start date");
                    logger.info("End time is " + endDate + " start date is " + startDate);
                    logger.info("corn expression " + cronExpression);
                    try {
                        trigger = TriggerBuilder.newTrigger().withIdentity(jobName,
                                "DEFAULT").startAt(startDate).withSchedule(CronScheduleBuilder.cronSchedule
                                (cronExpression).inTimeZone(TimeZone.getTimeZone(timeZone))
                                .withMisfireHandlingInstructionDoNothing()).endAt(endDate).build();

                    } catch (Exception exception) {
                        logger.error("Exception ", exception);
                    }
                } else {
                    logger.info("Sorry this entry was not triggered");
                    //delete entry
                }
            } else {
                logger.debug("End date does not exist");
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName,
                        "DEFAULT").startAt(startDate).withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                        .inTimeZone(TimeZone.getTimeZone(timeZone)).withMisfireHandlingInstructionDoNothing()).build();
            }
            return trigger;
        }
        return trigger;
    }
}
