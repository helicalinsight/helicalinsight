package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import groovy.json.JsonSlurper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * System service for listing and managing SystemSchedule jobs.
 * Merges systemschedule.json definitions with in-memory Quartz state and persists
 * configuration / script changes back to the SystemSchedule folder.
 */
@SuppressWarnings("unused")
public class SystemScheduleManager implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleManager.class);

    private static final Set<String> KNOWN_KEYS = new LinkedHashSet<>(Arrays.asList(
            "id", "layout", "enabled", "paused", "expireDate", "cron", "scheduledTime", "timeZone", "task", "email"
    ));

    private static final String DEFAULT_LAYOUT_ID = "Static/layout/system-schedule.default.ui.layout";

    private final Gson gson = new Gson();
    private final SystemScheduleLoader loader = new SystemScheduleLoader();

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = gson.fromJson(jsonFormData, JsonObject.class);
        String action = optString(formJson, "action");
        if (StringUtils.isBlank(action)) {
            throw new EfwServiceException("action is required");
        }
        try {
            return switch (action.toLowerCase()) {
                case "list" -> listSchedules().toString();
                case "trigger", "execute" -> triggerNow(formJson).toString();
                case "pause" -> pause(formJson).toString();
                case "resume" -> resume(formJson).toString();
                case "disable" -> disable(formJson).toString();
                case "enable" -> enable(formJson).toString();
                case "delete" -> delete(formJson).toString();
                case "save", "update" -> saveSchedule(formJson).toString();
                case "getscript" -> getScript(formJson).toString();
                case "savescript" -> saveScript(formJson).toString();
                case "getjson" -> getJson().toString();
                case "savejson" -> saveJson(formJson).toString();
                case "reload" -> reload().toString();
                default -> throw new EfwServiceException("Unsupported system schedule action: " + action);
            };
        } catch (EfwServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("System schedule action {} failed", action, ex);
            throw new EfwServiceException(ex);
        }
    }

    private JsonObject listSchedules() throws Exception {
        List<Map<String, Object>> schedules = SystemScheduleLoader.loadSchedules();
        Scheduler scheduler = SchedulerUtility.getInstance();
        JsonArray scheduledList = new JsonArray();
        int slno = 1;
        for (Map<String, Object> schedule : schedules) {
            JsonObject row = toScheduleRow(schedule, scheduler);
            row.addProperty("slno", slno++);
            scheduledList.add(row);
        }
        JsonObject response = new JsonObject();
        response.add("scheduledList", scheduledList);
        return response;
    }

    private JsonObject toScheduleRow(Map<String, Object> schedule, Scheduler scheduler) throws Exception {
        String scheduleId = SystemScheduleLoader.stringValue(schedule, "id", "");
        boolean enabled = booleanValue(schedule.get("enabled"), true);
        boolean paused = booleanValue(schedule.get("paused"), false);
        boolean expired = SystemScheduleLoader.isExpired(schedule);
        Date expireDate = SystemScheduleLoader.resolveExpireDate(schedule);
        String taskType = resolveTaskType(schedule);
        String cron = SystemScheduleLoader.resolveCronExpression(schedule);
        String scheduledTime = SystemScheduleLoader.stringValue(schedule, "scheduledTime", "");

        JsonObject row = new JsonObject();
        row.addProperty("slno", 0);
        row.addProperty("jobId", scheduleId);
        row.addProperty("id", scheduleId);
        row.addProperty("systemSchedule", true);
        row.addProperty("type", "system");
        row.addProperty("enabled", enabled);
        row.addProperty("paused", paused);
        row.addProperty("expired", expired);
        row.addProperty("expireDate", SystemScheduleLoader.stringValue(schedule, "expireDate", "never"));
        if (expireDate != null) {
            row.addProperty("expireDateMillis", expireDate.getTime());
        }
        row.addProperty("cron", cron);
        row.addProperty("scheduledTime", scheduledTime);
        row.addProperty("timeZone", SystemScheduleLoader.stringValue(schedule, "timeZone", ""));
        row.addProperty("taskType", taskType);
        row.addProperty("frequency", StringUtils.isNotBlank(SystemScheduleLoader.stringValue(schedule, "cron", ""))
                ? "Cron"
                : "Daily");
        row.add("daysofWeek", new JsonArray());
        row.add("emailRecipients", resolveEmailRecipientsJson(schedule));
        row.addProperty("emailSubject", "");
        row.addProperty("emailBody", "");
        row.addProperty("scheduledSaveReportName", scheduleId);
        row.addProperty("reportPath", "");
        row.addProperty("reportDirectory", "");
        row.addProperty("reportFile", "");
        row.addProperty("lastExecutedOn", "");
        row.addProperty("description", "");
        row.addProperty("calendarName", "");
        row.addProperty("finalFireTime", "");
        row.addProperty("startDate", "");

        row.addProperty("layout", SystemScheduleLoader.stringValue(schedule, "layout", DEFAULT_LAYOUT_ID));
        if (schedule.get("email") != null) {
            row.add("email", gson.toJsonTree(schedule.get("email")));
        }

        JsonObject config = new JsonObject();
        for (Map.Entry<String, Object> entry : schedule.entrySet()) {
            if (!KNOWN_KEYS.contains(entry.getKey())) {
                config.add(entry.getKey(), gson.toJsonTree(entry.getValue()));
            }
        }
        row.add("config", config);
        if (schedule.get("task") != null) {
            row.add("task", gson.toJsonTree(schedule.get("task")));
        }
        row.add("reportParameters", config);

        JobKey jobKey = JobKey.jobKey(scheduleId, SystemScheduleLoader.SYSTEM_JOB_GROUP);
        boolean inMemory = scheduler.checkExists(jobKey);
        row.addProperty("inMemoryStatus", inMemory);
        row.addProperty("inMemory", inMemory);

        SchedulerMetaData metaData = scheduler.getMetaData();
        String jobStoreClass = metaData.getJobStoreClass().getName();
        row.addProperty("jobClassName", jobStoreClass);
        row.addProperty("scheduleStorageMemory", jobStoreClass.contains("RAMJobStore"));

        if (inMemory) {
            @SuppressWarnings("unchecked")
            List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
            if (triggers != null && !triggers.isEmpty()) {
                Trigger trigger = triggers.get(0);
                TriggerState state = scheduler.getTriggerState(trigger.getKey());
                row.addProperty("triggerState", state == null ? "" : state.name());
                Date next = trigger.getNextFireTime();
                Date previous = trigger.getPreviousFireTime();
                Date start = trigger.getStartTime();
                Date end = trigger.getEndTime();
                Date finalFire = trigger.getFinalFireTime();
                if (next == null) {
                    row.addProperty("nextFireTime", "");
                } else {
                    row.addProperty("nextFireTime", next.getTime());
                }
                if (previous == null) {
                    row.addProperty("previousFireTime", "");
                    row.addProperty("lastExecutedOn", "");
                } else {
                    row.addProperty("previousFireTime", previous.getTime());
                    row.addProperty("lastExecutedOn", String.valueOf(previous.getTime()));
                }
                if (start == null) {
                    row.addProperty("startDate", "");
                } else {
                    row.addProperty("startDate", start.getTime());
                }
                if (end == null) {
                    row.addProperty("endTime", "never");
                } else {
                    row.addProperty("endTime", end.getTime());
                }
                if (finalFire == null) {
                    row.addProperty("finalFireTime", "");
                } else {
                    row.addProperty("finalFireTime", finalFire.getTime());
                }
            } else {
                row.addProperty("triggerState", "");
                row.addProperty("nextFireTime", "");
                row.addProperty("previousFireTime", "");
                row.addProperty("endTime", "never");
            }
        } else {
            row.addProperty("triggerState", enabled ? (expired ? "EXPIRED" : "NOT_SCHEDULED") : "DISABLED");
            row.addProperty("nextFireTime", "");
            row.addProperty("previousFireTime", "");
            if (expireDate == null) {
                row.addProperty("endTime", "never");
            } else {
                row.addProperty("endTime", expireDate.getTime());
            }
        }
        row.add("availableActions", availableActions(enabled, paused, expired, inMemory, taskType));
        return row;
    }

    private JsonArray availableActions(boolean enabled, boolean paused, boolean expired, boolean inMemory,
                                      String taskType) {
        List<String> actions = new ArrayList<>();
        actions.add("edit");
        actions.add("editJson");
        if ("script".equals(taskType)) {
            actions.add("editScript");
        }
        if (inMemory) {
            actions.add("trigger");
            if (paused) {
                actions.add("resume");
            } else {
                actions.add("pause");
            }
        }
        if (enabled) {
            actions.add("disable");
        } else {
            actions.add("enable");
        }
        actions.add("delete");
        if (!expired) {
            actions.add("reload");
        }
        return toJsonArray(actions);
    }

    private JsonObject triggerNow(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Scheduler scheduler = SchedulerUtility.getInstance();
        JobKey jobKey = JobKey.jobKey(scheduleId, SystemScheduleLoader.SYSTEM_JOB_GROUP);
        if (!scheduler.checkExists(jobKey)) {
            throw new EfwServiceException("Schedule " + scheduleId + " is not active in memory");
        }
        scheduler.triggerJob(jobKey);
        return message("Triggered schedule " + scheduleId);
    }

    private JsonObject pause(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Map<String, Object> schedule = requireSchedule(scheduleId);
        schedule.put("paused", true);
        SystemScheduleLoader.upsertSchedule(schedule);
        Scheduler scheduler = SchedulerUtility.getInstance();
        JobKey jobKey = JobKey.jobKey(scheduleId, SystemScheduleLoader.SYSTEM_JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.pauseJob(jobKey);
        }
        return message("Paused schedule " + scheduleId);
    }

    private JsonObject resume(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Map<String, Object> schedule = requireSchedule(scheduleId);
        schedule.put("paused", false);
        SystemScheduleLoader.upsertSchedule(schedule);
        Scheduler scheduler = SchedulerUtility.getInstance();
        JobKey jobKey = JobKey.jobKey(scheduleId, SystemScheduleLoader.SYSTEM_JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.resumeJob(jobKey);
        } else if (booleanValue(schedule.get("enabled"), true) && !SystemScheduleLoader.isExpired(schedule)) {
            loader.registerOrUpdate(schedule);
        }
        return message("Resumed schedule " + scheduleId);
    }

    private JsonObject disable(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Map<String, Object> schedule = requireSchedule(scheduleId);
        schedule.put("enabled", false);
        schedule.put("paused", false);
        SystemScheduleLoader.upsertSchedule(schedule);
        loader.unregister(scheduleId);
        return message("Disabled schedule " + scheduleId);
    }

    private JsonObject enable(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Map<String, Object> schedule = requireSchedule(scheduleId);
        schedule.put("enabled", true);
        SystemScheduleLoader.upsertSchedule(schedule);
        if (SystemScheduleLoader.isExpired(schedule)) {
            return message("Enabled schedule " + scheduleId + " in JSON, but it is expired and was not registered");
        }
        loader.registerOrUpdate(schedule);
        return message("Enabled schedule " + scheduleId);
    }

    private JsonObject delete(JsonObject formJson) throws Exception {
        String scheduleId = requireScheduleId(formJson);
        Map<String, Object> schedule = SystemScheduleLoader.findScheduleById(scheduleId);
        loader.unregister(scheduleId);
        boolean removed = SystemScheduleLoader.deleteSchedule(scheduleId);
        if (!removed && schedule == null) {
            throw new EfwServiceException("Schedule " + scheduleId + " was not found");
        }
        return message("Deleted schedule " + scheduleId);
    }

    @SuppressWarnings("unchecked")
    private JsonObject saveSchedule(JsonObject formJson) throws Exception {
        JsonObject scheduleJson = formJson.has("schedule")
                ? formJson.getAsJsonObject("schedule")
                : formJson;
        Map<String, Object> schedule = gson.fromJson(scheduleJson, Map.class);
        String scheduleId = SystemScheduleLoader.stringValue(schedule, "id", "");
        if (StringUtils.isBlank(scheduleId)) {
            throw new EfwServiceException("schedule.id is required");
        }
        if (!schedule.containsKey("expireDate") || schedule.get("expireDate") == null
                || StringUtils.isBlank(String.valueOf(schedule.get("expireDate")))) {
            schedule.put("expireDate", "never");
        }
        if (StringUtils.isBlank(SystemScheduleLoader.stringValue(schedule, "layout", ""))) {
            Map<String, Object> existing = SystemScheduleLoader.findScheduleById(scheduleId);
            if (existing != null && StringUtils.isNotBlank(SystemScheduleLoader.stringValue(existing, "layout", ""))) {
                schedule.put("layout", existing.get("layout"));
            } else {
                schedule.put("layout", DEFAULT_LAYOUT_ID);
            }
        }
        SystemScheduleLoader.upsertSchedule(schedule);
        loader.unregister(scheduleId);
        if (booleanValue(schedule.get("enabled"), true) && !SystemScheduleLoader.isExpired(schedule)) {
            loader.registerOrUpdate(schedule);
        }
        JsonObject response = message("Saved schedule " + scheduleId);
        response.add("schedule", toScheduleRow(schedule, SchedulerUtility.getInstance()));
        return response;
    }

    private JsonObject getScript(JsonObject formJson) throws Exception {
        String scheduleId = optString(formJson, "id");
        String scriptName = optString(formJson, "script");
        if (StringUtils.isBlank(scriptName) && StringUtils.isNotBlank(scheduleId)) {
            Map<String, Object> schedule = requireSchedule(scheduleId);
            scriptName = scriptNameFromSchedule(schedule);
        }
        if (StringUtils.isBlank(scriptName)) {
            throw new EfwServiceException("script or id with task.script is required");
        }
        File scriptFile = SystemScheduleLoader.resolveScriptFile(scriptName);
        if (!scriptFile.exists()) {
            throw new EfwServiceException("Script not found: " + scriptFile.getName());
        }
        JsonObject response = new JsonObject();
        response.addProperty("script", scriptFile.getName());
        response.addProperty("content", FileUtils.readFileToString(scriptFile, ControllerUtils.defaultCharSet()));
        return response;
    }

    private JsonObject saveScript(JsonObject formJson) throws Exception {
        String scheduleId = optString(formJson, "id");
        String scriptName = optString(formJson, "script");
        if (StringUtils.isBlank(scriptName) && StringUtils.isNotBlank(scheduleId)) {
            Map<String, Object> schedule = requireSchedule(scheduleId);
            scriptName = scriptNameFromSchedule(schedule);
        }
        if (StringUtils.isBlank(scriptName)) {
            throw new EfwServiceException("script or id with task.script is required");
        }
        if (!formJson.has("content")) {
            throw new EfwServiceException("content is required");
        }
        String content = formJson.get("content").getAsString();
        File scriptFile = SystemScheduleLoader.resolveScriptFile(scriptName);
        FileUtils.write(scriptFile, content, ControllerUtils.defaultCharSet());
        if (StringUtils.isNotBlank(scheduleId)) {
            Map<String, Object> schedule = SystemScheduleLoader.findScheduleById(scheduleId);
            if (schedule != null && booleanValue(schedule.get("enabled"), true)
                    && !SystemScheduleLoader.isExpired(schedule)) {
                loader.registerOrUpdate(schedule);
            }
        }
        return message("Saved script " + scriptFile.getName());
    }

    private JsonObject getJson() throws Exception {
        File scheduleFile = SystemScheduleLoader.getSystemScheduleFile();
        JsonObject response = new JsonObject();
        response.addProperty("file", scheduleFile.getName());
        if (scheduleFile.exists()) {
            response.addProperty("content", FileUtils.readFileToString(scheduleFile, ControllerUtils.defaultCharSet()));
        } else {
            response.addProperty("content", "[]");
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    private JsonObject saveJson(JsonObject formJson) throws Exception {
        if (!formJson.has("content")) {
            throw new EfwServiceException("content is required");
        }
        String content = formJson.get("content").getAsString();
        Object parsed = new JsonSlurper().parseText(content);
        if (!(parsed instanceof List<?>)) {
            throw new EfwServiceException("systemschedule.json content must be a JSON array");
        }
        List<Map<String, Object>> schedules = SystemScheduleLoader.extractSchedules((List<?>) parsed);
        SystemScheduleLoader.saveSchedules(schedules);
        loader.reloadAll();
        return message("Saved systemschedule.json and reloaded system schedules");
    }

    private JsonObject reload() {
        loader.reloadAll();
        return message("Reloaded system schedules from JSON");
    }

    private JsonArray resolveEmailRecipientsJson(Map<String, Object> schedule) {
        JsonArray recipients = new JsonArray();
        for (String recipient : SystemScheduleMailHelper.resolveRecipients(schedule)) {
            recipients.add(recipient);
        }
        return recipients;
    }

    private Map<String, Object> requireSchedule(String scheduleId) {
        Map<String, Object> schedule = SystemScheduleLoader.findScheduleById(scheduleId);
        if (schedule == null) {
            throw new EfwServiceException("Schedule not found: " + scheduleId);
        }
        return new LinkedHashMap<>(schedule);
    }

    private String requireScheduleId(JsonObject formJson) {
        String scheduleId = optString(formJson, "id");
        if (StringUtils.isBlank(scheduleId)) {
            scheduleId = optString(formJson, "scheduleId");
        }
        if (StringUtils.isBlank(scheduleId)) {
            throw new EfwServiceException("id is required");
        }
        return scheduleId;
    }

    @SuppressWarnings("unchecked")
    private String scriptNameFromSchedule(Map<String, Object> schedule) {
        Object task = schedule.get("task");
        if (!(task instanceof Map<?, ?> taskMap)) {
            return null;
        }
        return SystemScheduleLoader.stringValue((Map<String, Object>) taskMap, "script", null);
    }

    @SuppressWarnings("unchecked")
    private String resolveTaskType(Map<String, Object> schedule) {
        Object task = schedule.get("task");
        if (!(task instanceof Map<?, ?> taskMapRaw)) {
            return "";
        }
        Map<String, Object> taskMap = (Map<String, Object>) taskMapRaw;
        if (StringUtils.isNotBlank(SystemScheduleLoader.stringValue(taskMap, "class", null))) {
            return "class";
        }
        if (StringUtils.isNotBlank(SystemScheduleLoader.stringValue(taskMap, "script", null))) {
            return "script";
        }
        return "";
    }

    private JsonObject message(String text) {
        JsonObject response = new JsonObject();
        response.addProperty("message", text);
        return response;
    }

    private JsonArray toJsonArray(List<String> values) {
        JsonArray array = new JsonArray();
        for (String value : values) {
            array.add(value);
        }
        return array;
    }

    private static boolean booleanValue(Object value, boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return "true".equalsIgnoreCase(String.valueOf(value));
    }

    private static String optString(JsonObject json, String key) {
        if (json == null || !json.has(key) || json.get(key).isJsonNull()) {
            return "";
        }
        return json.get(key).getAsString();
    }
}
