package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * EnhancedScheduleProcessCall class extends {@link ScheduleProcessCall}
 * 
 * Created by author on 4/1/2020.
 * @author Rajesh
 */
public class EnhancedScheduleProcessCall extends ScheduleProcessCall {
    private static final Logger logger = LoggerFactory.getLogger(EnhancedScheduleProcessCall.class);
    private ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
    /**
     * scheduleSpecificJob(Schedules schedules, String baseUrl)
     * Schedules a specific job using the provided schedule object and base URL.
     * @param schedules       schedule object consists all properties like, execution date, id
     * @param baseUrl		  URL used for scheduling
     * @return "scheduled" message indicating the result of the scheduling process.
     */
    public String scheduleSpecificJob(Schedules schedules, String baseUrl) {
        ISchedule scheduleClass;
        String className = "com.helicalinsight.scheduling.ScheduleJob";
        String jobName;

        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        String jobType = "DEFAULT";
        JsonObject scheduleEntityJson = JsonParser.parseString(scheduleOperation.prepareSchedulesEntityToJSON(schedules).toString()).getAsJsonObject();
        JsonObject scheduleOptionsJson = scheduleEntityJson.getAsJsonObject("ScheduleOptions");
        jobName = scheduleEntityJson.get("@id").getAsString();

        String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(scheduleOptionsJson);

        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, jobType, null, scheduleEntityJson, baseUrl);
        return "scheduled";
    }
    /**
     * scheduleOperation(List<Schedules> listOfScheduleJobs, String baseUrl, UserService userService)
     * Performs the scheduling operation for a list of scheduled jobs using the provided parameters.
     * @param listOfScheduleJobs               list of scheduled jobs for process
     * @param baseUrl						   URL used for scheduling
     * @param userService					   provides user-related information like userId
     */
    public void scheduleOperation(List<Schedules> listOfScheduleJobs, String baseUrl, UserService userService) {
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        for (int count = 0; count < listOfScheduleJobs.size(); count++) {
            Schedules eachSchedule = listOfScheduleJobs.get(count);
            String id;
            if (eachSchedule != null && eachSchedule.getIsActive()) {
                Long scheduleId = eachSchedule.getScheduleId();
                id = String.valueOf(scheduleId);
                JsonObject eachScheduleJson = JsonParser.parseString(scheduleOperation.prepareSchedulesEntityToJSON(eachSchedule).toString()).getAsJsonObject();
                logger.debug("count:  " + count);
                logger.debug("newJSoJsonObject:  " + eachScheduleJson);
                logger.debug("contains end after execution" + eachScheduleJson.getAsJsonObject("ScheduleOptions")
                        .has("EndAfterExecutions"));
                if (eachScheduleJson.getAsJsonObject("ScheduleOptions").has("EndAfterExecutions")) {
                    JsonObject scheduleObject = eachScheduleJson.getAsJsonObject("ScheduleOptions");
                    String endAfterExecution =optStringValue(scheduleObject,"EndAfterExecutions", null);
                    logger.debug("endAfterExecution: " + endAfterExecution);
                    String noOfExecutions = optStringValue(eachScheduleJson,"NoOfExecutions", null);
                    if (noOfExecutions != null && endAfterExecution != null) {
                        if (noOfExecutions.equalsIgnoreCase(endAfterExecution) || Integer
                                .parseInt(noOfExecutions) >= Integer.parseInt
                                (endAfterExecution)) {
                            logger.debug("Deleting job fom schedule");
                            schedulerProcess.delete(id);
                            continue;
                        }
                    }
                }
                String jobName = String.valueOf(id);
                JsonObject scheduleOption = eachScheduleJson.getAsJsonObject("ScheduleOptions");
                logger.debug("ScheduleOption:  " + scheduleOption.get("Frequency").getAsString());
                String jobType = eachScheduleJson.getAsJsonObject("SchedulingJob").get("@type").getAsString();
                logger.info("Scheduling type: " + jobType);
                String className = ScheduleJobFactory.getScheduleClass(jobType);
                String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(scheduleOption);
                int userId = Integer.valueOf(AuthenticationUtils.getUserId(eachScheduleJson));
                User currentUser = userService.findUser(userId);
                if (currentUser != null && !currentUser.getUsername().equals("")) {
                    ISchedule scheduleClass = ScheduleJobFactory.getIScheduleJobInstance(className);
                    schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, "DEFAULT", null,
                            eachScheduleJson, baseUrl);
                }

            }
        }
    }
    /**
     * optStringValue(JsonObject jsonObject, String key, String defaultValue)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns key specific value ,otherwise it returns defaultValue.
     * @param jsonObject 		
     * @param key 				key for checking
     * @return key's value otherwise default value		     
     */
	private String optStringValue(JsonObject jsonObject, String key, String defaultValue) {
		if (jsonObject != null) {
			if (jsonObject.has(key) && !jsonObject.get(key).isJsonNull()) {
				return jsonObject.get(key).getAsString();
			}
		}
		return defaultValue;
	}

}
