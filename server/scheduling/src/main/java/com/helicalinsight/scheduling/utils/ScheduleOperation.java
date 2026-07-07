package com.helicalinsight.scheduling.utils;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.helicalinsight.adhoc.RenameOperationHandler;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import com.helicalinsight.scheduling.ScheduleProcess;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.ResourceTypeService;
import com.helicalinsight.scheduling.service.SchedulesService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ScheduleOperation class deals with schedule operation such as preparing schedule data, data in json format
 * geting data from xml file, validating data etc.
 * <p>
 * Created by author on 3/30/2020.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
public class ScheduleOperation {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleOperation.class);
    private ResourceTypeService resourceTypeService = ApplicationContextAccessor.getBean(ResourceTypeService.class);
    private SchedulesService scheduleService = ApplicationContextAccessor.getBean(SchedulesService.class);
    private JobParametersService jobParametersService = ApplicationContextAccessor.getBean(JobParametersService.class);
    private HiResourceService hiResourceService = ApplicationContextAccessor.getBean(HiResourceService.class);

    @Autowired
    private HIResourceServiceDB serviceDb;

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    /**
     * validateTime(String startDate, String endDate, String timeZone, String startTime, String endTime)
     * Validates the time range based on the given start and end dates, time zone, and time values.
     *
     * @param startDate start date in string format.
     * @param endDate   end date in string format (can be null if not specified).
     * @param timeZone  time zone of the location validation should take place.
     * @param startTime start time in string format.
     * @param endTime   end time in string format.
     * @return {@code true} if the time range is valid, otherwise throws exceptions for invalid cases.
     * @throws FormValidationException If the start date is in the past, the end date is in the past,
     *                                 or the end date is before the start date.
     */
    public boolean validateTime(String startDate, String endDate, String timeZone, String startTime, String endTime) {
        Date actualStartDate = stringToDate(startDate, timeZone, startTime);
        Date currentDateInTimeZone = findDateAtTimeZone(timeZone, new Date());
        Date requestedStartDate = findDateAtTimeZone(timeZone, actualStartDate);

        logger.info("Current time in " + timeZone + " IS " + currentDateInTimeZone);
        logger.info("Requested startDate is " + requestedStartDate);

        if (requestedStartDate.getTime() < currentDateInTimeZone.getTime()) {
            throw new FormValidationException("The start date is a past date");
        }

        if (endDate != null) {
            Date actualEndDate = stringToDate(endDate, timeZone, endTime);
            Date requestedEndDate = findDateAtTimeZone(timeZone, actualEndDate);
            logger.info("Requested EndDate is " + requestedEndDate);

            if (requestedEndDate.getTime() < currentDateInTimeZone.getTime()) {
                throw new FormValidationException("The end date is a past date");
            }
            if (requestedEndDate.getTime() < requestedStartDate.getTime()) {
                throw new FormValidationException("The end date is a less than start date");
            }
        }
        return true;
    }

    /**
     * stringToDate(String dateString, String timezone, String time)
     * Converts a string representation of a date and time into a Date object based on the given time zone.
     *
     * @param dateString date in string format (format: "yyyy-MM-dd").
     * @param timezone   time zone in which the date should be interpreted.
     * @param time       time in string format (format: "HH:mm:ss").
     * @return A Date object representing the combined date and time in the specified time zone.
     * or {@code null} if the parsing fails.
     */
    public Date stringToDate(String dateString, String timezone, String time) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            return formatter.parse(dateString + " " + time);
        } catch (ParseException e) {
            logger.error("Parse exception", e);
        }
        return null;
    }

    /**
     * epochToDate(Object lastExecutedOn)
     * Converts an epoch timestamp representation to a Date object.
     *
     * @param lastExecutedOn epoch timestamp as an object.
     * @return A Date object representing the converted epoch timestamp,
     * or {@code null} if the input is not in the JsonObject format.
     */
    public Date epochToDate(Object lastExecutedOn) {
        if (lastExecutedOn instanceof JsonObject) {
            JsonObject json = new Gson().fromJson((JsonObject) lastExecutedOn, JsonObject.class);
            if (json == null) {
                return null;
            }
            long milliSeconds = json.get("time").getAsLong();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(milliSeconds);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * findDateAtTimeZone(String timeZone, Date requestDate)
     * Converts a given date to the specified time zone.
     *
     * @param timeZone    target time zone identifier.
     * @param requestDate original date to be converted.
     * @return A Date object representing specified time zone.
     */
    public Date findDateAtTimeZone(String timeZone, Date requestDate) {
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        Calendar calTZ = new GregorianCalendar(tz);
        calTZ.setTimeInMillis(requestDate.getTime());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, calTZ.get(Calendar.YEAR));
        cal.set(Calendar.MONTH, calTZ.get(Calendar.MONTH));
        cal.set(Calendar.DAY_OF_MONTH, calTZ.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, calTZ.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, calTZ.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, calTZ.get(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, calTZ.get(Calendar.MILLISECOND));
        return cal.getTime();
    }

    /**
     * prepareSchedulesEntity(JsonObject jsonObject, HiResource hiResource)
     * preparing schedule entity for saveReportController/updateScheduleController
     *
     * @param jsonObject object containing schedule-related data.
     * @param hiResource HiResource associated with the schedule.
     * @return Schedules entity prepared with the provided JSON data and HiResource.
     */
    public Schedules prepareSchedulesEntity(JsonObject jsonObject, HIResource hiResource) {
        User loggedInUser;
        //preparing JobParameters entity

        loggedInUser = AuthenticationUtils.getUserDetails().getLoggedInUser();
        //preparing Schedules entity
        Schedules schedules = null;

        if (jsonObject.has("id")) {
            Long id = Long.valueOf(jsonObject.get("id").getAsInt());
            schedules = scheduleService.getSchedule(id);

        } else {
            schedules = new Schedules();
        }
        schedules.setHIResource(hiResource);
        schedules.setCreatedBy(loggedInUser);
        if (jsonObject.has("scheduleType"))
            schedules.setScheduleType(resourceTypeService.getResourceTypeByTypeAndExtension(jsonObject.get("scheduleType").getAsString(), "." + FileUtils.getExtension(jsonObject.get("reportFile").getAsString())));
        schedules.setScheduleName(jsonObject.get("JobName").getAsString());
        schedules.setIsActive(jsonObject.get("isActive").getAsBoolean());
        JsonObject scheduleOptions = jsonObject.get("ScheduleOptions").getAsJsonObject();
        if (!scheduleOptions.entrySet().isEmpty()) {
            schedules.setDateFormat(GsonUtility.optString(scheduleOptions, "dateFormat"));
            schedules.setDaysOfWeek(GsonUtility.optString(scheduleOptions, "DaysofWeek"));
            schedules.setEndAfterExecutions(GsonUtility.optInt(scheduleOptions, "EndAfterExecutions"));
            schedules.setIsMigrated(false);
            String endDate = GsonUtility.optString(scheduleOptions, "EndDate");
            if (!StringUtils.isEmpty(endDate))
                schedules.setEndDate(SchedulerUtils.convertStringIntoDate(endDate));
            String startDate = GsonUtility.optString(scheduleOptions, "StartDate");
            if (!StringUtils.isEmpty(startDate))
                schedules.setStartDate(SchedulerUtils.convertStringIntoDate(startDate));
            schedules.setEndsOn(GsonUtility.optString(scheduleOptions, "endsRadio"));
            schedules.setFrequency(GsonUtility.optString(scheduleOptions, "Frequency"));
            //no need to add the default values for date columns
            schedules.setLastExecutionStatus(0);
            schedules.setNoOfExecution(0);
            schedules.setRepeatBy(GsonUtility.optString(scheduleOptions, "RepeatBy"));
            schedules.setRepeatsEvery(GsonUtility.optInt(scheduleOptions, "RepeatsEvery"));
            schedules.setTimeZone(GsonUtility.optString(scheduleOptions, "timeZone"));
            schedules.setScheduledTime(GsonUtility.optString(scheduleOptions, "ScheduledTime"));
            schedules.setScheduledEndTime(GsonUtility.optString(scheduleOptions, "ScheduledEndTime"));
        }
        JsonObject emailSettings = GsonUtility.optJsonObject(jsonObject, "EmailSettings");
        if (emailSettings != null && !emailSettings.entrySet().isEmpty()) {
            schedules.setIsZip(GsonUtility.optBoolean(emailSettings, "Zip"));
            schedules.setExportFormats(emailSettings.get("Formats").toString());
            schedules.setEmailRecipients(emailSettings.get("Recipients").toString());
            String body = GsonUtility.optString(emailSettings, "Body");
            if (!StringUtils.isEmpty(body) && !"[]".equals(body))
                schedules.setEmailBody(body);
            schedules.setEmailSubject(emailSettings.get("Subject").getAsString());
        }
        return schedules;
    }


    /**
     * prepareSchedulesEntity(JsonObject scheduleJsonItem, HiResource hiResource, String createdBy)
     * preparing schedule entity for migration
     *
     * @param scheduleJsonItem contains schedule related data
     * @param hiResource       provides hiResourceId.
     * @param createdBy        user who created
     * @return schedule object with all properties.
     */
    public Schedules prepareSchedulesEntity(JsonObject scheduleJsonItem, HIResource hiResource, String createdBy) {
        User loggedInUser = null;
        //preparing JobParameters entity
        if (createdBy != null && !createdBy.isEmpty()) {
            loggedInUser = userService.findUser(Integer.parseInt(createdBy));
        }

        JsonObject scheduleOptions = scheduleJsonItem.get("ScheduleOptions").getAsJsonObject();
        JsonObject schedulingJob = scheduleJsonItem.get("SchedulingJob").getAsJsonObject();
        JsonObject emailSettings = schedulingJob.get("EmailSettings").getAsJsonObject();


        Schedules schedules;
        if (scheduleJsonItem.has("@id")) {
            Long id = scheduleJsonItem.get("@id").getAsLong();
            schedules = new Schedules(id);
        } else {
            schedules = new Schedules();
        }
        schedules.setHIResource(hiResource);
        schedules.setCreatedBy(loggedInUser);

        if (scheduleJsonItem.has("scheduleType"))
            schedules.setScheduleType(resourceTypeService.getResourceTypeByTypeAndExtension(scheduleJsonItem.get("scheduleType").getAsString(), "." + FileUtils.getExtension(schedulingJob.get("reportFile").getAsString())));
        schedules.setScheduleName(GsonUtility.optString(scheduleJsonItem, "JobName"));
        schedules.setIsActive(GsonUtility.optBoolean(scheduleJsonItem, "isActive"));
        schedules.setDateFormat(GsonUtility.optString(scheduleOptions, "dateFormat"));
        schedules.setDaysOfWeek(GsonUtility.optString(scheduleOptions, "DaysofWeek"));
        schedules.setEndAfterExecutions(GsonUtility.optInt(scheduleOptions, "EndAfterExecutions"));
        String endDate = GsonUtility.optString(scheduleOptions, "EndDate");
        if (!StringUtils.isEmpty(endDate))
            schedules.setEndDate(SchedulerUtils.convertStringIntoDate(endDate));
        String startDate = GsonUtility.optString(scheduleOptions, "StartDate");
        if (!StringUtils.isEmpty(startDate))
            schedules.setStartDate(SchedulerUtils.convertStringIntoDate(startDate));
        schedules.setEndsOn(GsonUtility.optString(scheduleOptions, "endsRadio"));
        schedules.setFrequency(GsonUtility.optString(scheduleOptions, "Frequency"));
        //no need to add the default values for these columns
        schedules.setLastExecutionDate(epochToDate(GsonUtility.optJsonObject(scheduleJsonItem, "LastExecutedOn")));
        schedules.setNextExecutionDate(epochToDate(GsonUtility.optJsonObject(scheduleJsonItem, "NextExecutionOn")));
        schedules.setNoOfExecution(GsonUtility.optIntValue(scheduleOptions, "NoOfExecutions", 0));
        schedules.setLastExecutionStatus(GsonUtility.optIntValue(scheduleOptions, "LastExecutionStatus", 0));
        schedules.setRepeatBy(GsonUtility.optString(scheduleOptions, "RepeatBy"));
        schedules.setRepeatsEvery(GsonUtility.optInt(scheduleOptions, "RepeatsEvery"));
        schedules.setTimeZone(GsonUtility.optString(scheduleOptions, "timeZone"));
        schedules.setScheduledTime(GsonUtility.optString(scheduleOptions, "ScheduledTime"));
        schedules.setScheduledEndTime(GsonUtility.optString(scheduleOptions, "ScheduledEndTime"));
        schedules.setIsZip(GsonUtility.optBoolean(emailSettings, "Zip"));
        String formats = GsonUtility.optString(emailSettings, "Formats");
        if (!StringUtils.isEmpty(formats) && !"[]".equals(formats))
            schedules.setExportFormats(formats);
        schedules.setEmailRecipients(emailSettings.get("Recipients").getAsString());

        String body = GsonUtility.optString(emailSettings, "Body");
        if (!StringUtils.isEmpty(body) && !"[]".equals(body))
            schedules.setEmailBody(body);
        schedules.setEmailSubject(GsonUtility.optString(emailSettings, "Subject"));
        return schedules;
    }

    /**
     * prepareJsonToDate(Object lastExecutedOn)
     * Converts a JSON representation of a date to a Java Date object.
     *
     * @param lastExecutedOn object with the date and time.
     * @return Date object with the date and time from the JSON input, or {@code null} if the input is not valid.
     */
    private Date prepareJsonToDate(Object lastExecutedOn) {
        if (lastExecutedOn instanceof JsonObject) {
            JsonObject json = new Gson().fromJson((JsonObject) lastExecutedOn, JsonObject.class);
            int year = json.get("year").getAsInt();
            int month = json.get("month").getAsInt();
            int day = json.get("date").getAsInt();
            int hours = json.get("hours").getAsInt();
            int minutes = json.get("minutes").getAsInt();
            int seconds = json.get("seconds").getAsInt();
            Calendar calendar = new GregorianCalendar(year, month - 1, day, hours, minutes, seconds);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * deleteAllAssociatedHiResource(Long resourceId)
     * Deletes all HiResource from database.
     *
     * @param resourceId id to delete HiResource entity
     */
    public void deleteAllAssociatedHiResource(Long resourceId) {
        //todo need discuss with somen regarding deleting parent directories also.
        hiResourceService.deleteHiResource(resourceId);
    }

    /**
     * deleteScheduleEntity(String schedulingReference)
     * Deletes scheduleEntity from database using  schedule id
     *
     * @param schedulingReference consists of schedule id
     */
    public void deleteScheduleEntity(String schedulingReference) {
        Long scheduleId = Long.valueOf(schedulingReference);
        Schedules schedule = scheduleService.getSchedule(scheduleId);
        if (schedule != null) {
            //first delete jobparameters
            jobParametersService.deleteAllJobParametersRelatedToScheduleId(scheduleId);
            //second delete schedules
            HIResource resource = schedule.getHIResource();
            if (resource != null) {
                Long resourceId = Long.valueOf(resource.getResourceId());
                scheduleService.deleteSchedule(scheduleId);
                deleteAllAssociatedHiResource(resourceId);
            } else {
                scheduleService.deleteSchedule(scheduleId);
            }
        }
    }

    /**
     * prepareHiResource(Efwsr efwsr)
     * Prepares a HiResource object based on the provided Efwsr.
     *
     * @param efwsr provides report file and directory of file
     * @return HiResource object.
     */
    public HIResource prepareHiResource(Efwsr efwsr) {
        HIResource fileResource = null;
        String reportFile = efwsr.getReportFile();
        String directory = efwsr.getReportDirectory();
        String url = String.join("/", directory, reportFile);
        return serviceDb.getResourceByUrl(url);
        /**
         List<String> listOfDirectories = Arrays.asList(efwsr.getReportDirectory().split("/"));
         Long parentId = null;
         //check all directories
         for (String eachDirectory : listOfDirectories) {
         HiResource eachDirResource = hiResourceService.getHiResourceByPath(eachDirectory, parentId);
         if (eachDirResource == null) {
         parentId = prepareHiResource(efwsr, reportFile, parentId, eachDirectory, true, null);
         } else {
         parentId = eachDirResource.getResourceId();
         }
         }
         //check file
         fileResource = hiResourceService.getHiResourceByPath(reportFile, parentId);
         if (fileResource == null) {
         Long fileResourceId = prepareHiResource(efwsr, reportFile, parentId, reportFile, false, null);
         fileResource = hiResourceService.getHiResource(fileResourceId);
         }
         **/
//        return fileResource;
    }

    /**
     * prepareHiResource(JsonObject json)
     * Prepares a HiResource object using  json data.
     *
     * @param json Object containing the scheduling job details
     * @return HiResource object representing the prepared resource, or {@code null} if object not has schedule details..
     */
    public HIResource prepareHiResource(JsonObject json) {
        HIResource fileResource = null;
        if (json.has("SchedulingJob")) {
            JsonObject schedulingJob = json.get("SchedulingJob").getAsJsonObject();
            String reportDirectory = GsonUtility.optStringValue(schedulingJob, "reportDirectory", "");
            String reportFile = GsonUtility.optStringValue(schedulingJob, "reportFile", null);
            if (StringUtils.isEmpty(reportFile)) {
                return fileResource;
            }
            return serviceDb.getResourceByUrl(String.join("/", reportDirectory, reportFile));
            /**
             String createdBy = AuthenticationUtils.getUserId(json);
             List<String> listOfDirectories = Arrays.asList(reportDirectory.split("/"));
             Long parentId = null;
             //check all directories
             for (String eachDirectory : listOfDirectories) {
             HiResource eachDirResource = hiResourceService.getHiResourceByPath(eachDirectory, parentId);
             if (eachDirResource == null) {
             parentId = prepareHiResource(null, reportFile, parentId, eachDirectory, true, createdBy);
             } else {
             parentId = eachDirResource.getResourceId();
             }
             }
             //check file
             fileResource = hiResourceService.getHiResourceByPath(reportFile, parentId);
             if (fileResource == null) {
             Long fileResourceId = prepareHiResource(null, reportFile, parentId, reportFile, false, createdBy);
             fileResource = hiResourceService.getHiResource(fileResourceId);
             }
             }

             return fileResource;
             **/
        }
        return fileResource;
    }

    @Deprecated(forRemoval = true, since = "5.1.0GA")
    /**
     * prepareHiResource(Efwsr efwsr, String reportFile, Long parentId, String eachDirectory, Boolean isFolder, String createdBy)
     * Prepares a HiResource object based on the provided parameters.
     * @param efwsr                        instance of efwsr
     * @param reportFile                   to get extension of file
     * @param parentId                     id 
     * @param eachDirectory                   path directory of hi resource
     * @param isFolder                     true or false   
     * @param createdBy                    user who created
     * @return hiResource id in long format.
     */
    private Long prepareHiResource(Efwsr efwsr, String reportFile, Long parentId, String eachDirectory, Boolean isFolder, String createdBy) {
        HiResource hiResource = new HiResource();
        if (createdBy != null && !createdBy.isEmpty()) {
            hiResource.setCreatedBy(userService.findUser(Integer.parseInt(createdBy)));
            hiResource.setIsMigrated(true);
        } else {
            hiResource.setCreatedBy(AuthenticationUtils.getUserDetails().getLoggedInUser());
            hiResource.setIsMigrated(false);
        }
        Date currentDate = new Date();
        hiResource.setCreatedDate(currentDate);
        hiResource.setIsFolder(isFolder);
        hiResource.setLastModifiedDate(currentDate);
        //todo need to set resource name
        //hiResource.setName(efwsr.getReportName());
        hiResource.setResourcePath(eachDirectory);
        hiResource.setParentId(parentId);
        if (isFolder)
            hiResource.setResourceType(resourceTypeService.getResourceTypeByTypeAndExtension("folder", "." + "efwFolder"));
        else {
            String extension = FileUtils.getExtension(reportFile);
            hiResource.setResourceType(resourceTypeService.getResourceTypeByTypeAndExtension(extension, "." + extension));
        }
        return hiResourceService.addHiResource(hiResource);
    }

    /**
     * saveJobParameters(Schedules schedule, JsonObject reportParametersJson, Boolean isMigrated)
     * Saves job parameters associated with a schedule.
     *
     * @param schedule             Schedules object for which the job parameters are being saved.
     * @param reportParametersJson JsonObject containing the report parameters.
     * @param isMigrated           Specifies whether the job parameters are migrated (true) or not (false).
     */
    public void saveJobParameters(Schedules schedule, JsonObject reportParametersJson, Boolean isMigrated) {
        reportParametersJson.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            JobParameters jobParameters = new JobParameters();
            jobParameters = SchedulerUtils.prepareJobValue(jobParameters, value);
            jobParameters.setKey(key);
            jobParameters.setScheduleIdOfJobParameter(schedule);
            jobParameters.setIsMigrated(isMigrated);
            jobParametersService.addJobParameter(jobParameters);
        });
    }

    /**
     * prepareSchedulesEntityToJSON(Schedules schedules)
     *
     * @param schedules schedule details
     * @return JsonObject with all schedule information in json format.
     */
    public JsonObject prepareSchedulesEntityToJSON(Schedules schedules) {
        JsonObject scheduleJson = new JsonObject();
        scheduleJson.addProperty("@id", schedules.getScheduleId());
        scheduleJson.add("ScheduleOptions", prepareScheduleOptions(schedules));
        scheduleJson.addProperty("isActive", schedules.getIsActive());
        scheduleJson.add("SchedulingJob", prepareSchedulingJob(schedules));
        scheduleJson.addProperty("JobName", schedules.getScheduleName());
        ResourceType scheduleType = schedules.getScheduleType();
        scheduleJson.addProperty("scheduleType", scheduleType != null ? scheduleType.getName() : "");
        scheduleJson.addProperty("LastExecutedOn", (schedules.getLastExecutionDate() != null) ? schedules.getLastExecutionDate().toString() : null);
        scheduleJson.addProperty("LastExecutionStatus", schedules.getLastExecutionStatus());
        scheduleJson.addProperty("NextExecutionOn", (schedules.getNextExecutionDate() != null) ? schedules.getNextExecutionDate().toString() : null);
        scheduleJson.addProperty("NoOfExecutions", schedules.getNoOfExecution());
        scheduleJson.add("security", prepareSecurityJson(schedules));
        return scheduleJson;
    }

    /**
     * prepareSecurityJson(Schedules schedules)
     *
     * @param schedules object provides user
     * @return jsonObject with user and organization id..
     */
    private JsonObject prepareSecurityJson(Schedules schedules) {
        JsonObject security = new JsonObject();
        User user = schedules.getCreatedBy();
        if (user != null) {
            security.addProperty("createdBy", user.getId() + "");
            security.addProperty("organization", user.getOrganization() != null ? String.valueOf(user.getOrganization().getId()) : "[]");
        }
        return security;
    }

    /**
     * prepareSchedulingJob(Schedules schedules)
     *
     * @param schedules schedule data
     * @return jsonObject with schedule data.
     */
    public JsonObject prepareSchedulingJob(Schedules schedules) {
    /*    "@type": "efw",
                "EmailSettings": {
            "Formats": [],
            "Recipients": [
            "test@helicaltech.com"
            ],
            "Subject": "test",
                    "Body": []
        },
        "reportDirectory": "1463377978248\\Sample EFW Dashboard",
                "reportFile": "sample_dashboard.efw"*/


        JsonObject schedulingJob = new JsonObject();
        ResourceType scheduleType = schedules.getScheduleType();
        schedulingJob.addProperty("@type", scheduleType != null ? scheduleType.getName() : "");
        schedulingJob.add("reportParameters", prepareReportParameters(schedules));
        schedulingJob.add("EmailSettings", prepareEmailSettings(schedules));
        HIResource hiResource = schedules.getHIResource();
        String url = hiResource != null ? hiResource.getResourceURL() : "";
        schedulingJob.addProperty("reportDirectory", FilenameUtils.getPathNoEndSeparator(url));
        schedulingJob.addProperty("reportFile", FilenameUtils.getName(url));
        return schedulingJob;
    }

    @Deprecated(forRemoval = true)
    /**
     * prepareResourceDir(HiResource fileResource)
     * Prepares the resource directory path for a given file resource by constructing
     * the complete path using the parent hierarchy of the resource.
     *
     * @param fileResource                file resource for which to prepare the resource directory path.
     * @return complete resource directory path for the given file resource.
     */
    private String prepareResourceDir(HiResource fileResource) {
        List<String> parentPathList = new ArrayList<>();
        prepareParentList(fileResource, parentPathList);
        List<String> reverseList = Lists.reverse(parentPathList);
        return String.join("/", reverseList);
    }

    @Deprecated(forRemoval = true)
    /**
     * Recursively prepares a list of parent resource paths for file resource.
     * @param fileResource            file resource for which to prepare the parent path list.
     * @param parentPathList            list that will store the parent resource paths.
     */
    private void prepareParentList(HiResource fileResource, List<String> parentPathList) {
        Long parentId = fileResource.getParentId();
        if (parentId != null) {
            HiResource parentResource = hiResourceService.getHiResource(parentId);
            if (parentResource != null)
                parentPathList.add(parentResource.getResourcePath());
            prepareParentList(parentResource, parentPathList);

        }
    }

    /**
     * prepareReportParameters(Schedules schedules)
     * Prepares a JSON object containing report parameters for a given schedule.
     *
     * @param schedules provides schedule id
     * @return json object consists of report parameters.
     */
    public JsonObject prepareReportParameters(Schedules schedules) {
        JsonObject reportParameters = new JsonObject();
        SchedulesService schedulesService = ApplicationContextAccessor.getBean(SchedulesService.class);
        List<JobParameters> listOfAllJobParameters = schedulesService.getAllJobParametersById(schedules.getScheduleId());
        for (JobParameters job : listOfAllJobParameters) {
            String type = job.getType();
            JsonElement element = JsonParser.parseString(job.getValue() != null ? job.getValue() : "{}");
            if (type != null && "Integer".equals(type)) {
                reportParameters.addProperty(job.getKey(), Integer.valueOf(job.getValue()));
            } else if (element.isJsonPrimitive()) {
                reportParameters.addProperty(job.getKey(), element.getAsString());
            } else if (element.isJsonObject()) {
                reportParameters.add(job.getKey(), element.getAsJsonObject());
            } else if (element.isJsonArray()) {
                reportParameters.add(job.getKey(), element.getAsJsonArray());
            }
        }
        return reportParameters;
    }

    /**
     * prepareEmailSettings(Schedules schedules)
     *
     * @param schedules email related data
     * @return jsonObject contains email information.
     */
    public JsonObject prepareEmailSettings(Schedules schedules) {
      /*  "EmailSettings": {
            "Formats": [],
            "Recipients": [
            "test@helicaltech.com"
            ],
            "Subject": "test",
                    "Body": []
        },*/

        JsonObject emailSettingsJson = new JsonObject();
        emailSettingsJson.addProperty("Zip", schedules.getIsZip());
        emailSettingsJson.addProperty("Formats", schedules.getExportFormats());
        emailSettingsJson.addProperty("Recipients", schedules.getEmailRecipients());
        emailSettingsJson.addProperty("Subject", schedules.getEmailSubject());
        emailSettingsJson.addProperty("Body", schedules.getEmailBody());
        return emailSettingsJson;
    }

    /**
     * prepareScheduleOptions(Schedules schedules)
     *
     * @param schedules provides all schedule related data
     * @return jsonObject contain schedule data.
     */
    public JsonObject prepareScheduleOptions(Schedules schedules) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JsonObject scheduleOptions = new JsonObject();
        Date startDate = schedules.getStartDate();
        if (startDate != null)
            scheduleOptions.addProperty("StartDate", dateFormat.format(startDate));
        Date endDate = schedules.getEndDate();
        if (endDate != null)
            scheduleOptions.addProperty("EndDate", dateFormat.format(endDate));
        scheduleOptions.addProperty("timeZone", schedules.getTimeZone());
        scheduleOptions.addProperty("ScheduledTime", schedules.getScheduledTime());
        scheduleOptions.addProperty("endsRadio", schedules.getEndsOn());
        scheduleOptions.addProperty("ScheduledEndTime", schedules.getScheduledEndTime());
        scheduleOptions.addProperty("Frequency", schedules.getFrequency());
        scheduleOptions.addProperty("RepeatBy", schedules.getRepeatBy());
        scheduleOptions.addProperty("RepeatsEvery", schedules.getRepeatsEvery());
        scheduleOptions.addProperty("DaysofWeek", schedules.getDaysOfWeek());
        scheduleOptions.addProperty("EndAfterExecutions", schedules.getEndAfterExecutions());
        return scheduleOptions;
    }

    /**
     * updateScheduleStatus(Long id, Map<String, Object> updatedScheduleStatusJson, JsonObject json)
     * this simply adds new schedule data.
     *
     * @param id                        id to get schedule object
     * @param updatedScheduleStatusJson map containing no of execution, new date
     * @param json                      provides schedule data .
     */
    public void updateScheduleStatus(Long id, Map<String, Object> updatedScheduleStatusJson, JsonObject json) {
        logger.debug("updatedScheduleStatusJson :" + updatedScheduleStatusJson);
        Schedules schedule = scheduleService.getSchedule(id);
        if (updatedScheduleStatusJson.containsKey("NoOfExecutions"))
            schedule.setNoOfExecution((Integer) updatedScheduleStatusJson.get("NoOfExecutions"));
        if (updatedScheduleStatusJson.containsKey("NextExecutionOn"))
            schedule.setNextExecutionDate((Date) updatedScheduleStatusJson.get("NextExecutionOn"));
        if (updatedScheduleStatusJson.containsKey("LastExecutedOn"))
            schedule.setLastExecutionDate((Date) updatedScheduleStatusJson.get("LastExecutedOn"));
        if (updatedScheduleStatusJson.containsKey("LastExecutionStatus")) {
            schedule.setLastExecutionStatus(Integer.valueOf((String) updatedScheduleStatusJson.get("LastExecutionStatus")));
        }


        scheduleService.editSchedule(schedule);
        prepareEndAfterValidation(json, id.toString());

    }

    /**
     * prepareScheduleEntityJson(int jobId)
     * Prepares a JSON representation of a schedule entity based on the given job ID.
     *
     * @param jobId schedule id to get schedule (job)
     * @return A JSON object containing the details of the schedule entity, or null if the schedule doesn't exist.
     */
    public JsonObject prepareScheduleEntityJson(int jobId) {
        Schedules schedule = scheduleService.getSchedule(Long.valueOf(jobId));
        JsonObject json = null;
        if (schedule != null) {
            json = prepareSchedulesEntityToJSON(schedule);
        }
        return json;
    }

    /**
     * insertDefaultDataForSchedule()
     * This method adds default resource types for scheduling data based on setting.xml and extensions.
     */
    public void insertDefaultDataForSchedule() {
        List<ResourceType> allResourceTypes = resourceTypeService.getAllResourceTypes();
        RenameOperationHandler handler = new RenameOperationHandler();
        List<String> listOfExtensionsFromSettings = handler.getListOfExtensionsFromSettings();
        Set<String> existingExtensions = allResourceTypes.stream().map(ResourceType::getName).collect(Collectors.toSet());

        if (allResourceTypes.isEmpty() || allResourceTypes.size()<listOfExtensionsFromSettings.size()) {

            String folderFileExtension = JsonUtils.getFolderFileExtension();

            for (String extension : listOfExtensionsFromSettings) {
                if(!existingExtensions.contains(extension)) {
                    ResourceType hcr = new ResourceType();
                    hcr.setExtension("." + ("folder".equals(extension)?folderFileExtension:extension));
                    hcr.setName(extension);
                    resourceTypeService.addResourceType(hcr);
                }
            }

        }
    }

    /**
     * triggerMigrationProcessForScheduling(Boolean isMigarateEnabled)
     * Triggers the migration process for scheduling data from XML to the database.
     *
     * @param isMigarateEnabled whether migration is enabled or not (true or false)
     * @return {@code True} if the migration process is successful, {@code false} if migration is disabled. or exception during migration.
     */
    public Boolean triggerMigrationProcessForScheduling(Boolean isMigarateEnabled) {
        //todo step 1 check whether migration is enabled and scheduling-storage-type or not in setting.xml //MigrateFromXMLSchedulingToDatabase
        try {
            if (com.helicalinsight.efw.utility.JsonUtils.isSchedulingXMLExists() && isMigarateEnabled) {
                logger.debug("Schedule migration process is enabled");
               /* //todo step 2 delete all existing migrated schedule records from database
                jobParametersService.deleteAllMigratedEntries();
                scheduleService.deleteAllMigratedEntries();
                hiResourceService.deleteAllMigratedEntries();*/
                //todo step 3 create a utility to convert all xml (scheduling) to list of schedule entity
                //todo step 4 add all the list of entities in database.
                migrateSchedulingXmlToDatabase();
            } else {
                logger.debug("scheduling migration is disabled");
                return false;
            }
        } catch (Exception e) {
            logger.error("There is a exception during migration :", e);
            return false;
        }
        return true;
    }

    /**
     * migrateSchedulingXmlToDatabase()
     * Migrates scheduling data from XML to the
     * database. Parses the scheduling XML as JSON, processes each schedule, and
     * prepares and saves the corresponding Schedules entities in the database.
     */
    private void migrateSchedulingXmlToDatabase() {
        JsonArray schedulesJsonArray = schedulingXmlAsJson();
        if (schedulesJsonArray != null) {
            logger.debug("schedulesJsonArray :" + schedulesJsonArray);
            for (int index = 0; index < schedulesJsonArray.size(); index++) {
                JsonObject eachJson = schedulesJsonArray.get(index).getAsJsonObject();
                String id = eachJson.get("@id").getAsString();
                if (!id.equals("0") && !eachJson.entrySet().isEmpty()) {
                    Boolean isMigrated = false;
                    Schedules existingSchedule = scheduleService.getSchedule(Long.valueOf(id));
                    if (existingSchedule != null) {
                        isMigrated = existingSchedule.getIsMigrated();
                    }
                    if (!isMigrated)
                        prepareScheduleEntityFromJson(eachJson);
                }
            }
        }

    }

    /**
     * schedulingXmlAsJson()
     *
     * @return JsonArray consisting data from scheduling.xml,  otherwise {@code null} scheduler path is already exists.
     */
    private JsonArray schedulingXmlAsJson() {
        PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
        Map<String, String> hashMap = propertiesFileReader.read("project.properties");
        String schedulerPath = hashMap.get("schedulerPath");
        File file = new File(schedulerPath);
        if (file.exists()) {
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            return processor.getJsonArray(schedulerPath, false);
        }
        return null;
    }

    /**
     * prepareScheduleEntityFromJson(JsonObject json)
     * Prepares a Schedules entity from a JsonObject containing scheduling data.
     *
     * @param json object containing scheduling data.
     */
    public void prepareScheduleEntityFromJson(JsonObject json) {
        logger.debug("eachSchedulingJson :" + json);
        //todo prepare hiResource
        HIResource hiResource = prepareHiResource(json);
        String createdBy = AuthenticationUtils.getUserId(json);
        Schedules preparedSchedule = prepareSchedulesEntity(json, hiResource, createdBy);
        preparedSchedule.setIsMigrated(true);
        Schedules schedule = scheduleService.addSchedule(preparedSchedule);
        if (json.has("SchedulingJob")) {
            JsonObject schedulingJob = json.get("SchedulingJob").getAsJsonObject();
            if (schedulingJob.has("reportParameters")) {
                JsonObject reportParameters = schedulingJob.getAsJsonObject("reportParameters");
                saveJobParameters(schedule, reportParameters, true);
            }
        }

    }

    /**
     * prepareEndAfterValidation(JsonObject jsonObject1, String id)
     * Performs actions based on the "EndAfterExecutions" parameter in the schedule data.
     *
     * @param jsonObject1 provides schedule data
     * @param id          id required to delete job from scheduler
     */
    public void prepareEndAfterValidation(JsonObject jsonObject1, String id) {
        if (jsonObject1.getAsJsonObject("ScheduleOptions").has("EndAfterExecutions")) {
            String EndAfterExecutions = jsonObject1.getAsJsonObject("ScheduleOptions").get
                    ("EndAfterExecutions").getAsString();
            String NoOfExecutionsNoOfExecutions = GsonUtility.optString(jsonObject1, "NoOfExecutions");
            if (NoOfExecutionsNoOfExecutions != null && NoOfExecutionsNoOfExecutions.equalsIgnoreCase(EndAfterExecutions) && Integer.parseInt
                    (NoOfExecutionsNoOfExecutions) >= Integer.parseInt(EndAfterExecutions)) {
                ScheduleProcess schedulerProcess = new ScheduleProcess();
                schedulerProcess.delete(id);
            }
        }
    }


}
