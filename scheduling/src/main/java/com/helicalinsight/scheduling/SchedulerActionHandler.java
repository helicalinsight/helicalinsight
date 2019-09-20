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

import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Author on 10/07/2015
 *
 * @author Somen
 */
@SuppressWarnings("unused")
public class SchedulerActionHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerActionHandler.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formJson = JSONObject.fromObject(jsonFormData);
        String action = formJson.optString("action");
        JSONArray scheduleXmlAsJson = schedulingXmlAsJson();
        JSONObject responseJson;
        responseJson = new JSONObject();
        if ("list".equals(action)) {
            responseJson.accumulate("scheduledList", filter(scheduleXmlAsJson).toString());
            return responseJson.toString();
        } else {
            return handleScheduleAction(formJson).toString();
        }
    }

    private JSONArray schedulingXmlAsJson() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        String schedulerPath = hashMap.get("schedulerPath");
        File file = new File(schedulerPath);
        if (file.exists()) {
            return ResourceProcessorFactory.getIProcessor().getJSONArray(schedulerPath, false);
        } else {
            throw new ResourceNotFoundException("The scheduling.xml file is not found. There may " +
                    "" + "not be any schedule");
        }
    }

    public JSONArray filter(JSONArray scheduleXmlAsJson) {
        Principal activeUser = AuthenticationUtils.getUserDetails();
        User currentUser = activeUser.getLoggedInUser();
        return addExtraInformation(listJobKey(currentUser, scheduleXmlAsJson));
    }

    public JSONObject handleScheduleAction(JSONObject instructions) {
        JSONObject result = new JSONObject();
        try {
            String command = instructions.getString("action");
            Scheduler scheduler = SchedulerUtility.getInstance();
            boolean adminCommandsExecuted = false;
            adminCommandsExecuted = true;
            if ("pauseAll".equalsIgnoreCase(command)) {
                scheduler.pauseAll();
                result.put("message", "Paused all successfully");
            } else if ("resumeAll".equalsIgnoreCase(command)) {
                scheduler.resumeAll();
                result.put("message", "Resumed all successfully");
            } else if ("summary".equalsIgnoreCase(command)) {
                result.put("summary", scheduler.getMetaData().getSummary());
            } else if ("start".equalsIgnoreCase(command)) {
                scheduler.start();
                result.put("message", "Started successfully");
            } else if ("shutdown".equalsIgnoreCase(command)) {
                String isForceShutdown = instructions.optString("force");
                if ("true".equalsIgnoreCase(isForceShutdown)) {
                    scheduler.shutdown(false);
                    result.put("message", "Scheduler is Shutdown completely");
                } else {
                    scheduler.standby();
                    result.put("message", "Scheduler is currently in standby mode. Set force " + "option true to " +
                            "shutdown completely");
                }
            } else if ("pauseList".equalsIgnoreCase(command)) {
                result.put("pauseList", JSONArray.fromObject(scheduler.getPausedTriggerGroups()));
            } else {
                adminCommandsExecuted = false;
            }


            if (adminCommandsExecuted) {

                return result;
            }

            String jobKey = instructions.optString("jobId");
            JobKey requestedJobKey = JobKey.jobKey(jobKey);
            if (!scheduler.checkExists(requestedJobKey)) {
                throw new EfwServiceException("The scheduled action may not exists, " +
                        "" + "or you may not have sufficient privilege to access this service");
            }
            logger.info("Requested Job key is" + requestedJobKey);
            if ("pause".equalsIgnoreCase(command)) {
                scheduler.pauseJob(requestedJobKey);
                result.put("message", "The job  paused successfully");
            } else if ("resume".equalsIgnoreCase(command)) {
                scheduler.resumeJob(requestedJobKey);
                result.put("message", "The job resumed successfully");
            } else if ("delete".equals(command)) {
                scheduler.deleteJob(requestedJobKey);
                result.put("message", "The job deleted from memory successfully");
            } else if ("execute".equalsIgnoreCase(command)) {
                scheduler.triggerJob(requestedJobKey);
                result.put("message", "The job triggered successfully");
            } else if ("jobDetails".equalsIgnoreCase(command)) {
                JobDetail jobDetail = scheduler.getJobDetail(requestedJobKey);
                JSONObject jobDetailsJson = JSONObject.fromObject(jobDetail);
                jobDetailsJson.remove("jobDataMap");
                jobDetailsJson.remove("ScheduleOptions");
                result.put("jobDetails", jobDetailsJson);
            } else {
                throw new EfwServiceException("This requested action was not found");
            }
        } catch (Exception exception) {
            logger.error("Exception", exception);
            throw new EfwServiceException(exception);
        }
        return result;
    }

    public JSONArray addExtraInformation(List<JSONObject> jsonList) {
        try {
            JSONArray result = new JSONArray();
            Scheduler scheduler = SchedulerUtility.getInstance();
            int slno = 1;
            for (JSONObject schedule : jsonList) {
                JSONObject entries = new JSONObject();
                String id = schedule.getString("@id");
                JobKey jobKey = JobKey.jobKey(id);
                //Could not resolve compiler warning. So casting to a safe type.
                @SuppressWarnings("unchecked") List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob
                        (jobKey);
                entries.put("slno", slno++);
                entries.put("jobId", id);
                addTriggerInfo(scheduler, entries, triggers);
                addScheduleInfo(schedule, entries);
                result.add(entries);
            }
            return result;
        } catch (Exception exception) {
            logger.error("Exception occurred", exception);
            throw new EfwServiceException(exception.getMessage());
        }

    }

    public List<JSONObject> listJobKey(User user, JSONArray scheduleXmlAsJson) {
        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor.getBean
                (ApplicationDefaultUserAndRoleNamesConfigurer.class);
        List<String> roleList = AuthenticationUtils.getUserRoles();
        boolean isAdmin = roleList.contains(namesConfigurer.getRoleAdmin());
        if (isAdmin) {
            return findAllScheduleJobKeys();
        } else {
            return findJobKeysSpecificUser(user.getId());
        }
    }

    private void addTriggerInfo(Scheduler scheduler, JSONObject entries, List<Trigger> triggers) throws
            SchedulerException {
        boolean neverFlag;
        if (triggers != null && triggers.size() > 0) {
            entries.put("inMemoryStatus", true);
            Trigger trigger = triggers.get(0);
            TriggerKey triggerKey = trigger.getKey();
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            entries.put("triggerState", triggerState);
            Date nextFireTime = trigger.getNextFireTime();//It gives the first
            Date endTime = trigger.getEndTime();
            Date finalFireTime = trigger.getFinalFireTime();
            JobDataMap jobDataMap = trigger.getJobDataMap();
            String calendarName = trigger.getCalendarName();
            String description = trigger.getDescription();
            Date startDate = trigger.getStartTime();
            if (nextFireTime != null) {
                entries.put("nextFireTime", nextFireTime.getTime());
            } else {
                entries.put("nextFireTime", "");
            }

            if (startDate != null) {
                entries.put("startDate", startDate.getTime());
            } else {
                entries.put("startDate", "");
            }

            if (endTime != null) {
                entries.put("endTime", endTime.getTime());


            } else {
                entries.put("endTime", "never");
            }

            if (finalFireTime != null) {
                entries.put("finalFireTime", finalFireTime.getTime());
            } else {
                entries.put("finalFireTime", "");
            }
            entries.put("calendarName", calendarName);
            entries.put("description", description);
            entries.put("dataMap", jobDataMap.get("jsonobject"));
        } else {
            entries.put("inMemoryStatus", false);
        }
    }

    private void addScheduleInfo(JSONObject schedule, JSONObject entries) {
        JSONObject schedulingJob = schedule.getJSONObject("SchedulingJob");
        JSONObject scheduleOptions = schedule.getJSONObject("ScheduleOptions");
        String type = schedulingJob.getString("@type");
        if ("hwf".equalsIgnoreCase(type)) {

            String hwfDirectory = schedulingJob.getString("hwfDirectory");
            String hwfFile = schedulingJob.getString("hwfFile");
            entries.put("hwfPath", hwfDirectory + File.separator + hwfFile);

            entries.put("hwfDirectory", hwfDirectory);
            entries.put("hwfFile", hwfFile);
        }

        if (schedulingJob.has("EmailSettings")) {
            String reportDirectory = schedulingJob.getString("reportDirectory");
            String reportFile = schedulingJob.getString("reportFile");

            JSONObject emailSetting = schedulingJob.getJSONObject("EmailSettings");
            entries.put("scheduledSaveReportName", schedule.getString("JobName"));
            entries.put("reportPath", reportDirectory + File.separator + reportFile);

            entries.put("emailRecipients", emailSetting.getString("Recipients"));
            entries.put("emailSubject", emailSetting.optString("Subject"));
            entries.put("emailBody", emailSetting.getString("Body"));
            entries.put("reportDirectory", reportDirectory);
            entries.put("reportFile", reportFile);
        }

        entries.put("type", type);
        entries.put("frequency", scheduleOptions.optString("Frequency"));
        entries.put("daysofWeek", scheduleOptions.optString("DaysofWeek"));
        entries.put("startDate", scheduleOptions.optString("StartDate"));

        if (!"never".equalsIgnoreCase(scheduleOptions.getString("endsRadio"))) {
            entries.put("endDate", scheduleOptions.getString("EndDate"));
        }
        entries.put("scheduledTime", scheduleOptions.optString("ScheduledTime"));

        JSONObject lastExecutionTime = schedule.optJSONObject("LastExecutedOn");
        entries.put("lastExecutedOn", lastExecutionTime == null ? "" : lastExecutionTime.optString("time"));

        if (schedulingJob.has("reportParameters") && (schedulingJob.get("reportParameters") instanceof JSONObject)) {
            JSONObject reportParameters = schedulingJob.getJSONObject("reportParameters");
            entries.put("reportParameters", reportParameters);
        }
    }

    public List<JSONObject> findAllScheduleJobKeys() {
        List<JSONObject> jobKeyList = new ArrayList<>();
        JSONArray scheduleXmlAsJson = schedulingXmlAsJson();
        for (int count = 0; count < scheduleXmlAsJson.size(); count++) {
            JSONObject schedules = scheduleXmlAsJson.getJSONObject(count);
            String id = schedules.getString("@id");
            if (!id.equals("0") && !schedules.isEmpty()) {
                jobKeyList.add(schedules);
            }
        }
        return jobKeyList;
    }


    public List<JSONObject> findJobKeysSpecificUser(int userId) {
        List<JSONObject> jobKeyList = new ArrayList<>();
        JSONArray scheduleXmlAsJson = schedulingXmlAsJson();
        for (int count = 0; count < scheduleXmlAsJson.size(); count++) {
            JSONObject schedules = scheduleXmlAsJson.getJSONObject(count);
            String id = schedules.getString("@id");
            if (!id.equals("0") && !schedules.isEmpty()) {

                String userIdString = AuthenticationUtils.getUserId(schedules);
                if (Integer.valueOf(userIdString) == userId) {
                    jobKeyList.add(schedules);
                }


            }
        }
        return jobKeyList;
    }
}