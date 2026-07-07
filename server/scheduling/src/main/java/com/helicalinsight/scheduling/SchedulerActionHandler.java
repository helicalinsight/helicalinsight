package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;


import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.helicalinsight.efw.utility.JsonUtils.safeGetJsonObject;

/**
 * SchedulerActionHandler class implements {@link IComponent}
 * class is responsible for handling scheduling-related actions
 * This class is no longer used use instead ({@link EnhancedSchedulerActionHandler} class
 * Created by Author on 10/07/2015
 * @author Somen
 */
@Deprecated
@SuppressWarnings("unused")
public class SchedulerActionHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerActionHandler.class);

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * executeComponent(String jsonFormData)
     * Executes a scheduling-related action based on the provided JSON form data.
     *
     * @param jsonFormData       JSON data containing the action to trigger schedule.
     * @return result of the executed action.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
        String action = GsonUtility.optString(formJson,"action");
        JsonArray scheduleXmlAsJson = schedulingXmlAsJson();
        JsonObject responseJson;
        responseJson = new JsonObject();
        if ("list".equals(action)) {
            responseJson.addProperty("scheduledList", filter(scheduleXmlAsJson).toString());
            return responseJson.toString();
        } else {
            return handleScheduleAction(formJson).toString();
        }
    }

    
    /**
     * schedulingXmlAsJson()
     * Returns the scheduling.XML file content as a JSON array.
     * @return JSON array scheduling XML content.
     * @throws ResourceNotFoundException if the scheduling XML file is not found.
     */
	private JsonArray schedulingXmlAsJson() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        String schedulerPath = hashMap.get("schedulerPath");
        File file = new File(schedulerPath);
        if (file.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            return processor.getJsonArray(schedulerPath, false);
        } else {
            throw new ResourceNotFoundException("The scheduling.xml file is not found. There may " +
                    "" + "not be any schedule");
        }
    }
	/**
	 * filter(JsonArray scheduleXmlAsJson)
	 * Method is responsible for getting active/logged in users .
	 * @param scheduleXmlAsJson      containing schedule.xml file information.   
	 * @return JsonArray containing job id , serial no.
	 */
    public JsonArray filter(JsonArray scheduleXmlAsJson) {
        Principal activeUser = AuthenticationUtils.getUserDetails();
        User currentUser = activeUser.getLoggedInUser();
        return addExtraInformation(listJobKey(currentUser, scheduleXmlAsJson));
    }
    /**
     * handleScheduleAction(JsonObject instructions)
     * Performs various scheduling actions based on the provided instructions.
     * @param instructions  			object containing action instructions, like pause,start,stop.
     * @return A JSON object indicating the result of the action.
     */
    public JsonObject handleScheduleAction(JsonObject instructions) {
        JsonObject result = new JsonObject();
        try {
            String command = GsonUtility.optString(instructions,"action");
            Scheduler scheduler = SchedulerUtility.getInstance();
            boolean adminCommandsExecuted = false;
            if (AuthenticationUtils.isSuperOrganizationUser()) {
                adminCommandsExecuted = true;
                if ("pauseAll".equalsIgnoreCase(command)) {
                    scheduler.pauseAll();
                    result.addProperty("message", "Paused all successfully");
                } else if ("resumeAll".equalsIgnoreCase(command)) {
                    scheduler.resumeAll();
                    result.addProperty("message", "Resumed all successfully");
                } else if ("summary".equalsIgnoreCase(command)) {
                    result.addProperty("summary", scheduler.getMetaData().getSummary());
                } else if ("start".equalsIgnoreCase(command)) {
                    scheduler.start();
                    result.addProperty("message", "Started successfully");
                } else if ("shutdown".equalsIgnoreCase(command)) {
                    String isForceShutdown = GsonUtility.optString(instructions,"force");
                    if ("true".equalsIgnoreCase(isForceShutdown)) {
                        scheduler.shutdown(false);
                        result.addProperty("message", "Scheduler is Shutdown completely");
                    } else {
                        scheduler.standby();
                        result.addProperty("message", "Scheduler is currently in standby mode. Set force " + "option true to " +
                                "shutdown completely");
                    }
                } else if ("pauseList".equalsIgnoreCase(command)) {
                    result.add("pauseList", new Gson().fromJson(new Gson().toJson(scheduler.getPausedTriggerGroups()),JsonArray.class));
                } else {
                    adminCommandsExecuted = false;
                }

            }

            if (adminCommandsExecuted) {

                return result;
            }

            String jobKey = GsonUtility.optString(instructions,"jobId");
            JobKey requestedJobKey = JobKey.jobKey(jobKey);
            if (!command.equalsIgnoreCase("delete") && !scheduler.checkExists(requestedJobKey)) {
                throw new EfwServiceException("The scheduled action may not exists, " +
                        "" + "or you may not have sufficient privilege to access this service");
            }
            logger.info("Requested Job key is" + requestedJobKey);
            if ("pause".equalsIgnoreCase(command)) {
                scheduler.pauseJob(requestedJobKey);
                result.addProperty("message", "The job  paused successfully");
            } else if ("resume".equalsIgnoreCase(command)) {
                scheduler.resumeJob(requestedJobKey);
                result.addProperty("message", "The job resumed successfully");
            } else if ("delete".equals(command)) {
                SchedulesService scheduleService  = ApplicationContextAccessor.getBean(SchedulesService.class);
                Long scheduleId =  Long.valueOf(instructions.get("jobId").getAsString());
                Schedules schedule =  scheduleService.getSchedule(scheduleId);
                if(schedule==null){
                    throw  new EfwServiceException("The given scheduleId "+scheduleId+" does not exists");
                }
                @SuppressWarnings("unchecked") List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob
                        (requestedJobKey);

				if (!schedule.getIsActive() || triggers ==null || triggers.size() == 0) {
					scheduleService.deleteSchedule(scheduleId);
					result.addProperty("message", "The schedule is deleted successfully");
				} else {
					scheduler.deleteJob(requestedJobKey);
                schedule.setIsActive(false);
                scheduleService.editSchedule(schedule);
					result.addProperty("message", "The job deleted from memory successfully");
				}
            } else if ("execute".equalsIgnoreCase(command)) {
                scheduler.triggerJob(requestedJobKey);
                result.addProperty("message", "The job triggered successfully");
            } else if ("jobDetails".equalsIgnoreCase(command)) {
                JobDetail jobDetail = scheduler.getJobDetail(requestedJobKey);
                JsonObject jobDetailsJson = new Gson().fromJson(new Gson().toJson(jobDetail),JsonObject.class);
                jobDetailsJson.remove("jobDataMap");
                jobDetailsJson.remove("ScheduleOptions");
                result.add("jobDetails", jobDetailsJson);
            } else {
                throw new EfwServiceException("This requested action was not found");
            }
        } catch (Exception exception) {
            logger.error("Exception", exception);
            throw new EfwServiceException(exception);
        }
        return result;
    }
    /**
     * addExtraInformation(List<JsonObject> jsonList)
     * @param jsonList    list contains schedule object, id to get job key
     * @return JsonArray containing job id , serial no
     * @throws EfwServiceException If an exception occurs during the process.
     */
    public JsonArray addExtraInformation(List<JsonObject> jsonList) {
        try {
            JsonArray result = new JsonArray();
            Scheduler scheduler = SchedulerUtility.getInstance();
            int slno = 1;
            for (JsonObject schedule : jsonList) {
                JsonObject entries = new JsonObject();
                String id = GsonUtility.optString(schedule,"@id");
                JobKey jobKey = JobKey.jobKey(id);
                //Could not resolve compiler warning. So casting to a safe type.
                @SuppressWarnings("unchecked") List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob
                        (jobKey);
                entries.addProperty("slno", slno++);
                entries.addProperty("jobId", id);
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
    /**
     * Returns list of job keys based on user and scheduling information.
     *
     * @param user             		user object to get organizationId and userId .
     * @param scheduleXmlAsJson 	containing schedule.xml file information.
     * @return list of JSON objects representing job keys based on user and organization.
     */
    public List<JsonObject> listJobKey(User user, JsonArray scheduleXmlAsJson) {
        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor.getBean
                (ApplicationDefaultUserAndRoleNamesConfigurer.class);
        List<String> roleList = AuthenticationUtils.getUserRoles();
        boolean isAdmin = roleList.contains(namesConfigurer.getRoleAdmin());
        if (user.getOrganization() == null && isAdmin) {
            return findAllScheduleJobKeys();
        } else if (isAdmin) {
            return findJobKeysSpecificToOrganization(user.getOrganization().getId(), scheduleXmlAsJson);
        } else {
            return findJobKeysSpecificUser(user.getId());
        }
    }

  /*  private void getScheduleFireInfo() {
        String id = null;
        JobKey jobKey = JobKey.jobKey(id);
        //Could not resolve compiler warning. So casting to a safe type.
        Scheduler scheduler = SchedulerUtility.getInstance();
        Map<String,Date> mapOfFireTimes = new HashMap<>();
        try {
            @SuppressWarnings("unchecked") List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob
                    (jobKey);

            if (triggers != null && triggers.size() > 0) {
                Trigger trigger = triggers.get(0);
                TriggerKey triggerKey = trigger.getKey();
                Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                Date previousFireTime = trigger.getPreviousFireTime();
                Date nextFireTime = trigger.getNextFireTime();//It gives the first
                mapOfFireTimes.put("LastExecutedOn",)

            }

        } catch (SchedulerException exception) {
            logger.error("Exception occurred", exception);
            throw new SchedulingException(exception.getMessage());
        }

    }
*/
    /**
     * addTriggerInfo(Scheduler scheduler, JsonObject entries, List<Trigger> triggers)
     * Adds trigger information to the provided JSON object based on the given triggers.
     *
     * @param scheduler           	 scheduler objecttoget Trigger state.
     * @param entries                object to which trigger information is added.
     * @param triggers    			 list of triggers associated with a job.
     * @throws SchedulerException if there's an error accessing scheduler information.
     */
    private void addTriggerInfo(Scheduler scheduler, JsonObject entries, List<Trigger> triggers) throws
            SchedulerException {
        boolean neverFlag;
        SchedulerMetaData metaData = scheduler.getMetaData();
        String jobStoreClass = metaData.getJobStoreClass().getName();
        Boolean inMemory = jobStoreClass.contains("RAMJobStore");
        if (triggers != null && triggers.size() > 0) {
            entries.addProperty("inMemoryStatus", true);
            Trigger trigger = triggers.get(0);
            TriggerKey triggerKey = trigger.getKey();
            Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
            entries.addProperty("triggerState", triggerState.toString());
            Date nextFireTime = trigger.getNextFireTime();//It gives the first
            Date endTime = trigger.getEndTime();
            Date finalFireTime = trigger.getFinalFireTime();
            JobDataMap jobDataMap = trigger.getJobDataMap();
            String calendarName = trigger.getCalendarName();
            String description = trigger.getDescription();
            Date startDate = trigger.getStartTime();
            if (nextFireTime != null) {
                entries.addProperty("nextFireTime", nextFireTime.getTime());
            } else {
                entries.addProperty("nextFireTime", "");
            }

            if (startDate != null) {
                entries.addProperty("startDate", startDate.getTime());
            } else {
                entries.addProperty("startDate", "");
            }

            if (endTime != null) {
                entries.addProperty("endTime", endTime.getTime());


            } else {
                entries.addProperty("endTime", "never");
            }

            if (finalFireTime != null) {
                entries.addProperty("finalFireTime", finalFireTime.getTime());
            } else {
                entries.addProperty("finalFireTime", "");
            }
            entries.addProperty("calendarName", calendarName==null?"":calendarName);
            entries.addProperty("description", description==null?"":description);
            if(jobDataMap.get("jsonobject")!=null) {
                JsonObject json = safeGetJsonObject(jobDataMap.get("jsonobject"));
                entries.add("dataMap", json);
            }
        } else {
            entries.addProperty("inMemoryStatus", false);
        }
        entries.addProperty("jobClassName",jobStoreClass);
        entries.addProperty("scheduleStorageMemory",inMemory);
    }
    /**
     * addScheduleInfo(JsonObject schedule, JsonObject entries)
     * Adds schedule information to the provided JSON object.
     * @param schedule 					object containing scheduling information.
     * @param entries  					object to which schedule information is added.
     */
    private void addScheduleInfo(JsonObject schedule, JsonObject entries) {
        JsonObject schedulingJob = GsonUtility.optJsonObject(schedule,"SchedulingJob");
        JsonObject scheduleOptions = GsonUtility.optJsonObject(schedule,"ScheduleOptions");
        String type = GsonUtility.optString(schedulingJob,"@type");
        if ("hwf".equalsIgnoreCase(type)) {

            String hwfDirectory = GsonUtility.optString(schedulingJob,"reportDirectory");
            String hwfFile = GsonUtility.optString(schedulingJob,"reportFile");
            entries.addProperty("hwfPath", hwfDirectory + File.separator + hwfFile);

            entries.addProperty("reportDirectory", hwfDirectory);
            entries.addProperty("reportFile", hwfFile);
        }

        if (schedulingJob.has("EmailSettings")) {
            String reportDirectory = GsonUtility.optString(schedulingJob,"reportDirectory");
            String reportFile = GsonUtility.optString(schedulingJob,"reportFile");

            JsonObject emailSetting = GsonUtility.optJsonObject(schedulingJob,"EmailSettings");
            entries.addProperty("scheduledSaveReportName", GsonUtility.optString(schedule,"JobName"));
            entries.addProperty("reportPath", reportDirectory + File.separator + reportFile);
String re=GsonUtility.optString(emailSetting,"Recipients");
if(!re.startsWith("[")){
    re="["+re+"]";
}
            entries.add("emailRecipients", JsonParser.parseString(re).getAsJsonArray());
            entries.addProperty("emailSubject", GsonUtility.optString(emailSetting,"Subject"));
            entries.addProperty("emailBody", GsonUtility.optString(emailSetting,"Body"));
            entries.addProperty("reportDirectory", reportDirectory);
            entries.addProperty("reportFile", reportFile);
        }

        entries.addProperty("type", type);
        entries.addProperty("frequency", GsonUtility.optString(scheduleOptions,"Frequency"));
        entries.add("daysofWeek", JsonParser.parseString(GsonUtility.optString(scheduleOptions,"DaysofWeek")).getAsJsonArray());
        entries.addProperty("startDate", GsonUtility.optString(scheduleOptions,"StartDate"));

        if (!"never".equalsIgnoreCase(GsonUtility.optString(scheduleOptions,"endsRadio"))) {
            entries.addProperty("endDate", GsonUtility.optString(scheduleOptions,"EndDate"));
        }
        entries.addProperty("scheduledTime", GsonUtility.optString(scheduleOptions,"ScheduledTime"));

        JsonObject lastExecutionTime = GsonUtility.optJsonObject(schedule,"LastExecutedOn");
        entries.addProperty("lastExecutedOn", lastExecutionTime == null ? "" : GsonUtility.optString(lastExecutionTime,"time"));

        if (schedulingJob.has("reportParameters") && (schedulingJob.get("reportParameters") instanceof JsonObject)) {
            JsonObject reportParameters = schedulingJob.getAsJsonObject("reportParameters");
            entries.add("reportParameters", reportParameters);
        }
    }

   
    /**
     * findAllScheduleJobKeys()
     * list of all schedule job keys with associated information.
     * @return A list of schedule .
     */
	protected List<JsonObject> findAllScheduleJobKeys() {
        List<JsonObject> jobKeyList = new ArrayList<>();
        JsonArray scheduleXmlAsJson = schedulingXmlAsJson();
        for (int count = 0; count < scheduleXmlAsJson.size(); count++) {
            JsonObject schedules = scheduleXmlAsJson.get(count).getAsJsonObject();
            String id = schedules.get("@id").getAsString();
            if (!id.equals("0") && !schedules.entrySet().isEmpty()) {
                jobKeyList.add(schedules);
            }
        }
        return jobKeyList;
    }
	/**
	 * findJobKeysSpecificToOrganization(Integer organization, JsonArray scheduleXmlAsJson)
	 * @param organization				organization  to get job keys
	 * @param scheduleXmlAsJson			containing scheduling information.
	 * @return list of organization-specific job keys.
	 */
    public List<JsonObject> findJobKeysSpecificToOrganization(Integer organization, JsonArray scheduleXmlAsJson) {
        List<JsonObject> jobKeyList = new ArrayList<>();
        for (int count = 0; count < scheduleXmlAsJson.size(); count++) {
            JsonObject schedules = scheduleXmlAsJson.get(count).getAsJsonObject();
            String id = schedules.get("@id").getAsString();
            if (!id.equals("0") && !schedules.entrySet().isEmpty()) {
                JsonObject security = GsonUtility.optJsonObject(schedules,"security");
                String organizationArray = GsonUtility.optString(security,"organization");
                if (!"[]".equals(organizationArray) && organizationArray.equals("" + organization)) {
                    jobKeyList.add(schedules);
                }
            }
        }
        return jobKeyList;
    }
    /**
     * findJobKeysSpecificUser(int userId)
     * Returns a list of schedule job keys specific to the given user.
     *
     * @param userId 		user ID to get job keys.
     * @return A list of JSON objects representing user-specific job keys.
     */
    public List<JsonObject> findJobKeysSpecificUser(int userId) {
        List<JsonObject> jobKeyList = new ArrayList<>();
        JsonArray scheduleXmlAsJson = schedulingXmlAsJson();
        for (int count = 0; count < scheduleXmlAsJson.size(); count++) {
            //JSONObject schedules = scheduleXmlAsJson.optJSONObject(count);
        	JsonElement jsonElement = scheduleXmlAsJson.get(count);
            if (jsonElement.isJsonObject()) {
                JsonObject schedules = jsonElement.getAsJsonObject();
                String id = GsonUtility.optString(schedules,"@id");
                if (!id.equals("0") && !schedules.entrySet().isEmpty()) {
                	JsonObject security = GsonUtility.optJsonObject(schedules,"security");
                	if (optInt(security,"createdBy") == userId) {
                		jobKeyList.add(schedules);
                	}
                }
            }
        }
        return jobKeyList;
    }
    /**
     * optInt(JsonObject jsonObject, String key)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns key value ,otherwise null.
     * @param jsonObject 		
     * @param key 				key to check in jsonObject
     * @return key's value otherwise {@code null}	
     */
    private static Integer optInt(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					return jsonPrimitive.getAsInt();
				}
			}
		}
		return null;
	}
    /**
     * listAllJobsInSchedule()
     * Provides lists all jobs in the scheduler along with their trigger information.
     * @throws SchedulerException if there's an error accessing scheduler information.
     */
	public void listAllJobsInSchedule() throws SchedulerException {
        Scheduler scheduler = SchedulerUtility.getInstance();
        for (String groupName : scheduler.getJobGroupNames()) {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();
                //System.out.println("[jobName] : " + jobName + " [groupName] : "
                       // + jobGroup + " - " + nextFireTime);

            }

        }

    }
}