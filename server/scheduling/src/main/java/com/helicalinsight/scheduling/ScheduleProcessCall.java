package com.helicalinsight.scheduling;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.controller.EFWController;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * ScheduleProcessCall
 * This class is responsible for doing scheduling related operation
 * This class no longer used use instead this {@link EnhancedScheduleProcessCall} class
 * @author Prashansa
 * @version 1.1
 */
@Deprecated
public class ScheduleProcessCall {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleProcessCall.class);

    /**
     * scheduleOperation(String path, String baseUrl, UserService userService)
     * <p>
     * scheduleOperation() is responsible for reading the scheduling.xml from
     * given path and schedule all the job given in XML
     * </p>
     *
     * @param path    a <code>String</code> specify path of scheduling.xml
     * @param baseUrl a <code>String</code> specify base URL like:
     *                http://localhost:9090/Example/test.html
     * @see EFWController
     */
    public void scheduleOperation(String path, String baseUrl, UserService userService) {
        JsonArray jsonArray1;
        String cronExpression;

        XmlOperation xmlOperation = new XmlOperation();

        jsonArray1 = xmlOperation.convertXmlStringIntoJSONArray(path);


		/*
         * This className is static value after proper integration this value
		 * will come from some other place
		 */
        String jobName;
        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        for (int count = 0; count < jsonArray1.size(); count++) {
            JsonObject node = jsonArray1.get(count).getAsJsonObject();
            String scheduleId = node.get("@id").getAsString();
            String id = scheduleId;
            if (id.equals("0")) {
                logger.debug("discarding 0 id");
                node.remove("@id");
                node.remove("isActive");
                logger.debug("jsonArray1" + node.entrySet().isEmpty());
                logger.debug("" + node.has("EndAfterExecutions"));
            }
            if (!node.entrySet().isEmpty() && node.get("isActive").getAsString().equals("true")) {
                JsonObject newJsonObject;
                newJsonObject = node;
                logger.debug("count:  " + count);
                logger.debug("newJSoJsonObject:  " + newJsonObject);
                logger.debug("contains end after execution" + newJsonObject.getAsJsonObject("ScheduleOptions")
                        .has("EndAfterExecutions"));
                if (newJsonObject.getAsJsonObject("ScheduleOptions").has("EndAfterExecutions")) {
                    String endAfterExecution = newJsonObject.getAsJsonObject("ScheduleOptions").get
                            ("EndAfterExecutions").getAsString();
                    logger.debug("endAfterExecution: " + endAfterExecution);
                    logger.debug("newJSoJsonObject.getString: " + newJsonObject.get("NoOfExecutions").getAsString());
                    logger.debug("condition: " + Integer.parseInt(newJsonObject.get("NoOfExecutions").getAsString()));
                    logger.debug("" + Integer.parseInt(endAfterExecution));
                    if (newJsonObject.get("NoOfExecutions").getAsString().equalsIgnoreCase(endAfterExecution) || Integer
                            .parseInt(newJsonObject.get("NoOfExecutions").getAsString()) >= Integer.parseInt
                            (endAfterExecution)) {
                        logger.debug("Deleting job fom schedule");
                        schedulerProcess.delete(id);
                        continue;
                    }
                }
                jobName = String.valueOf(id);
                JsonObject scheduleOption = node.getAsJsonObject("ScheduleOptions");
                logger.debug("ScheduleOption:  " + scheduleOption.get("Frequency").getAsString());
                String jobType = node.getAsJsonObject("SchedulingJob").get("@type").getAsString();
                logger.info("Scheduling type: " + jobType);
                String className = ScheduleJobFactory.getScheduleClass(jobType);


                cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(scheduleOption);

                if (!scheduleId.equals("0")) {
                    int userId = Integer.valueOf(AuthenticationUtils.getUserId(node));
                    User currentUser = userService.findUser(userId);
                    if (currentUser != null && !currentUser.getUsername().equals("")) {
                        ISchedule scheduleClass = ScheduleJobFactory.getIScheduleJobInstance(className);
                        schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, "DEFAULT", path,
                                newJsonObject, baseUrl);
                    }
                }
            }
        }
    }

    /**
     * scheduleSpecificJob(String path, String theId, String baseUrl)
     * this method is responsible to schedule specific job on the basis of id.
     *
     * @param path     path of scheduling.xml
     * @param theId    specify id.
     * @param baseUrl  base URL like:
     *                http://localhost:9090/Example/test.html
     */
    public String scheduleSpecificJob(String path, String theId, String baseUrl) {
        ISchedule scheduleClass;
        String className = "com.helicalinsight.scheduling.ScheduleJob";
        String jobName;

        XmlOperation xmlOperation = new XmlOperation();
        //String password = AuthenticationUtils.getPassword();

        JsonObject jsonObject = xmlOperation.getParticularObject(path, theId);
        //jsonObject.accumulate("password", password);

        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        JsonObject jsonObjectScheduleOption = jsonObject.getAsJsonObject("ScheduleOptions");
        //xmlOperation.getParticularObject(path, theId);
        String jobType = "DEFAULT";
        String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(jsonObjectScheduleOption);

        jobName = jsonObject.get("@id").getAsString();

        JsonObject reportParameter;
        try {
            reportParameter = jsonObject.getAsJsonObject("SchedulingJob").getAsJsonObject("reportParameters");
            logger.debug("reportParameter:  " + reportParameter);
        } catch (Exception ignore) {
            logger.error("Exception stack trace is ", ignore);
        }

        logger.debug("jsonObject before sending to schedule: " + jsonObject);
        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, jobType, path, jsonObject, baseUrl);
        return "scheduled";
    }


  /*  public String scheduleSpecificJobFromDb(Schedules schedule, String baseUrl) {
        ISchedule scheduleClass;
        String className = "com.helicalinsight.scheduling.ScheduleJob";
        String jobName;
        SchedulesService schedulesService = ApplicationContextAccessor.getBean(SchedulesService.class);
        //todo get the exact job from db
        ScheduleProcess schedulerProcess = new ScheduleProcess();
        ConvertIntoCronExpression convertIntoCronExpression = new ConvertIntoCronExpression();
        //todo extract ScheduleOptions from the specific job
        // JSONObject jsonObjectScheduleOption = jsonObject.getJSONObject("ScheduleOptions");
        String jobType = "DEFAULT";
        //todo provide the specific job to convertIntoCronExpression.convertDateIntoCronExpression(job) to get the cronExpression
        //String cronExpression = convertIntoCronExpression.convertDateIntoCronExpression(jsonObjectScheduleOption);
        //todo get the job id
        //jobName = jsonObject.getString("@id");

        JSONObject reportParameter;
        try {
            //todo get report parameters from the specific job
            //reportParameter = jsonObject.getJSONObject("SchedulingJob").getJSONObject("reportParameters");
            //logger.debug("reportParameter:  " + reportParameter);
        } catch (Exception ignore) {
            logger.error("Exception stack trace is ", ignore);
        }
        //logger.debug("jsonObject before sending to schedule: " + jsonObject);
        scheduleClass = (ISchedule) FactoryMethodWrapper.getUntypedInstance(className);
        //todo schedulerProcess the job
        //schedulerProcess.scheduleJob(cronExpression, scheduleClass, jobName, jobType, path, jsonObject, baseUrl);
        return "scheduled";
    }*/

    /**
     * getSchedulePath() is responsible to read scheduling.xml path from
     * project.properties file
     *
     * @return scheduling.xml file path
     * @see EFWController
     */
    public String getSchedulePath() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> map = propertiesFileReader.read("project.properties");
        return map.get("schedulerPath");
    }
}
