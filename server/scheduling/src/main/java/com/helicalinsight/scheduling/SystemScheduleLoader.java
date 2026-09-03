package com.helicalinsight.scheduling;

import com.helicalinsight.efw.ApplicationProperties;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Loads system scheduler definitions from System/Admin/SystemSchedule/systemschedule.json
 * and registers Quartz jobs. Task nodes may use either a Java {@code class} or a groovy
 * {@code script} file name located in the SystemSchedule folder.
 */
public class SystemScheduleLoader {

    public static final String SYSTEM_JOB_GROUP = "SYSTEM";

    static final String SYSTEM_SCHEDULE_FOLDER = "SystemSchedule";

    static final String SYSTEM_SCHEDULE_JSON = "systemschedule.json";

    private static final Logger logger = LoggerFactory.getLogger(SystemScheduleLoader.class);

    private static final ReentrantLock FILE_LOCK = new ReentrantLock();

    private static final String EXPIRE_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public void loadAll() {
        try {
            List<Map<String, Object>> schedules = loadSchedules();
            logger.info("Loading {} system schedule(s)", schedules.size());
            Scheduler scheduler = SchedulerUtility.getInstance();
            removeExistingSystemJobs(scheduler);
            for (Map<String, Object> schedule : schedules) {
                registerSchedule(scheduler, schedule);
            }
            registerJobListener(scheduler);
            if (!scheduler.isStarted()) {
                scheduler.start();
            }
            logger.info("System schedules loaded");
        } catch (Exception ex) {
            logger.error("Failed to load system schedules", ex);
        }
    }

    public void reloadAll() {
        loadAll();
    }

    public void registerOrUpdate(Map<String, Object> schedule) throws SchedulerException {
        Scheduler scheduler = SchedulerUtility.getInstance();
        String scheduleId = stringValue(schedule, "id", "");
        if (StringUtils.isBlank(scheduleId)) {
            throw new IllegalArgumentException("Schedule id is required");
        }
        JobKey jobKey = JobKey.jobKey(scheduleId, SYSTEM_JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
        registerSchedule(scheduler, schedule);
    }

    public void unregister(String scheduleId) throws SchedulerException {
        Scheduler scheduler = SchedulerUtility.getInstance();
        JobKey jobKey = JobKey.jobKey(scheduleId, SYSTEM_JOB_GROUP);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }

    private static void removeExistingSystemJobs(Scheduler scheduler) throws SchedulerException {
        for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(SYSTEM_JOB_GROUP))) {
            scheduler.deleteJob(jobKey);
        }
    }

    private static void registerJobListener(Scheduler scheduler) throws SchedulerException {
        ListenerManager listenerManager = scheduler.getListenerManager();
        if (listenerManager.getJobListener(SystemScheduleJobListener.NAME) != null) {
            listenerManager.removeJobListener(SystemScheduleJobListener.NAME);
        }
        listenerManager.addJobListener(new SystemScheduleJobListener(),
                GroupMatcher.jobGroupEquals(SYSTEM_JOB_GROUP));
    }

    private void registerSchedule(Scheduler scheduler, Map<String, Object> schedule) {
        if (!isEnabled(schedule)) {
            return;
        }
        String scheduleId = stringValue(schedule, "id", "");
        if (StringUtils.isBlank(scheduleId)) {
            logger.warn("Skipping system schedule with missing id");
            return;
        }

        Date expireDate = resolveExpireDate(schedule);
        if (expireDate != null && expireDate.before(new Date())) {
            logger.info("Skipping system schedule {} because expireDate {} has passed", scheduleId, expireDate);
            return;
        }

        TaskConfig taskConfig = resolveTaskConfig(schedule);
        if (taskConfig == null) {
            logger.warn("Skipping system schedule {} because task class or script is not configured", scheduleId);
            return;
        }

        String timeZone = stringValue(schedule, "timeZone", "");
        if (StringUtils.isBlank(timeZone)) {
            timeZone = TimeZone.getDefault().getID();
        }
        try {
            Class<? extends Job> jobClass;
            String scriptPath = null;
            String functionName = null;
            String taskClassName = null;
            if (StringUtils.isNotBlank(taskConfig.className())) {
                jobClass = SystemClassScheduleJob.class;
                taskClassName = taskConfig.className();
            } else {
                jobClass = SystemGroovyScheduleJob.class;
                scriptPath = resolveScriptFile(taskConfig.script()).getAbsolutePath();
                functionName = taskConfig.functionName();
            }

            String cronExpression = resolveCronExpression(schedule);
            JobDataMap jobDataMap = buildJobDataMap(schedule, scheduleId, timeZone, scriptPath, functionName, taskClassName);

            JobDetail jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(scheduleId, SYSTEM_JOB_GROUP)
                    .usingJobData(jobDataMap)
                    .build();

            TriggerBuilder<CronTrigger> triggerBuilder = TriggerBuilder.newTrigger()
                    .withIdentity(scheduleId, SYSTEM_JOB_GROUP)
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)
                            .inTimeZone(TimeZone.getTimeZone(timeZone))
                            .withMisfireHandlingInstructionDoNothing());
            if (expireDate != null) {
                triggerBuilder.endAt(expireDate);
            }
            Trigger trigger = triggerBuilder.build();

            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }
            scheduler.scheduleJob(jobDetail, trigger);
            if (isPaused(schedule)) {
                scheduler.pauseJob(jobDetail.getKey());
            }
            logger.info("Registered system schedule {} with cron {} in timezone {}", scheduleId, cronExpression, timeZone);
        } catch (Exception ex) {
            logger.error("Failed to register system schedule {}", scheduleId, ex);
        }
    }

    private static JobDataMap buildJobDataMap(Map<String, Object> schedule, String scheduleId, String timeZone,
                                              String scriptPath, String functionName, String taskClassName) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("scheduleId", scheduleId);
        jobDataMap.put("retentionDays", stringValue(schedule, "retentionDays", "30"));
        jobDataMap.put("exportPath", stringValue(schedule, "exportPath", "Audit/LLM"));
        jobDataMap.put("timeZone", timeZone);
        // JDBC JobStore requires serializable values; store JSON string only (not Groovy LazyMap)
        jobDataMap.put(SystemScheduleJob.KEY_SCHEDULE_JSON, JsonOutput.toJson(schedule));
        if (scriptPath != null) {
            jobDataMap.put(SystemGroovyScheduleJob.KEY_SCRIPT_PATH, scriptPath);
            jobDataMap.put(SystemGroovyScheduleJob.KEY_FUNCTION_NAME, functionName);
        }
        if (taskClassName != null) {
            jobDataMap.put(SystemClassScheduleJob.KEY_TASK_CLASS, taskClassName);
        }
        return jobDataMap;
    }

    /**
     * Prefers an explicit {@code cron} expression; otherwise builds a daily cron from {@code scheduledTime}.
     */
    static String resolveCronExpression(Map<String, Object> schedule) {
        String cron = stringValue(schedule, "cron", "");
        if (StringUtils.isNotBlank(cron)) {
            return cron.trim();
        }
        return buildDailyCron(stringValue(schedule, "scheduledTime", "00:00:00"));
    }

    static String buildDailyCron(String scheduledTime) {
        String[] tokens = scheduledTime.split(":");
        int hours = tokens.length > 0 ? Integer.parseInt(tokens[0].trim()) : 0;
        int minutes = tokens.length > 1 ? Integer.parseInt(tokens[1].trim()) : 0;
        int seconds = tokens.length > 2 ? Integer.parseInt(tokens[2].trim()) : 0;
        return seconds + " " + minutes + " " + hours + " * * ?";
    }

    /**
     * {@code expireDate} may be {@code never}, blank, epoch millis, or
     * {@code yyyy-MM-dd'T'HH:mm:ss}. Returns null when there is no expiry.
     */
    static Date resolveExpireDate(Map<String, Object> schedule) {
        if (schedule == null || !schedule.containsKey("expireDate") || schedule.get("expireDate") == null) {
            return null;
        }
        Object raw = schedule.get("expireDate");
        if (raw instanceof Number number) {
            long millis = number.longValue();
            return millis <= 0 ? null : new Date(millis);
        }
        String value = String.valueOf(raw).trim();
        if (value.isEmpty() || "never".equalsIgnoreCase(value)) {
            return null;
        }
        if (StringUtils.isNumeric(value)) {
            long millis = Long.parseLong(value);
            return millis <= 0 ? null : new Date(millis);
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat(EXPIRE_DATE_PATTERN);
            format.setLenient(false);
            return format.parse(value);
        } catch (ParseException ex) {
            logger.warn("Unable to parse expireDate value '{}'. Expected pattern {}", value, EXPIRE_DATE_PATTERN);
            return null;
        }
    }

    static boolean isExpired(Map<String, Object> schedule) {
        Date expireDate = resolveExpireDate(schedule);
        return expireDate != null && expireDate.before(new Date());
    }

    private static boolean isEnabled(Map<String, Object> schedule) {
        if (!schedule.containsKey("enabled") || schedule.get("enabled") == null) {
            return true;
        }
        Object enabled = schedule.get("enabled");
        if (enabled instanceof Boolean bool) {
            return bool;
        }
        return "true".equalsIgnoreCase(String.valueOf(enabled));
    }

    private static boolean isPaused(Map<String, Object> schedule) {
        if (!schedule.containsKey("paused") || schedule.get("paused") == null) {
            return false;
        }
        Object paused = schedule.get("paused");
        if (paused instanceof Boolean bool) {
            return bool;
        }
        return "true".equalsIgnoreCase(String.valueOf(paused));
    }

    @SuppressWarnings("unchecked")
    private static TaskConfig resolveTaskConfig(Map<String, Object> schedule) {
        Object taskNode = schedule.get("task");
        if (!(taskNode instanceof Map<?, ?> taskObjectRaw)) {
            return null;
        }
        Map<String, Object> taskObject = (Map<String, Object>) taskObjectRaw;

        String className = stringValue(taskObject, "class", null);
        if (StringUtils.isNotBlank(className)) {
            return new TaskConfig(className, null, null);
        }

        String script = stringValue(taskObject, "script", null);
        if (StringUtils.isBlank(script)) {
            return null;
        }
        String functionName = firstNonBlank(stringValue(taskObject, "function", null), "execute");
        return new TaskConfig(null, script, functionName);
    }

    /**
     * Resolves groovy scripts only from the SystemSchedule folder.
     */
    static File resolveScriptFile(String script) {
        String scriptName = new File(script).getName();
        return new File(getSystemScheduleDirectory(), scriptName);
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> loadSchedules() {
        File scheduleFile = getSystemScheduleFile();
        if (!scheduleFile.exists()) {
            logger.warn("System schedule file not found at {}", scheduleFile.getAbsolutePath());
            return Collections.emptyList();
        }
        FILE_LOCK.lock();
        try {
            String content = FileUtils.readFileToString(scheduleFile, Charset.defaultCharset());
            Object parsed = new JsonSlurper().parseText(content);
            if (!(parsed instanceof List<?> scheduleArray)) {
                logger.error("systemschedule.json root must be a JSON array");
                return Collections.emptyList();
            }
            return extractSchedules(scheduleArray);
        } catch (Exception ex) {
            logger.error("Failed to read system schedule file {}", scheduleFile.getAbsolutePath(), ex);
            return Collections.emptyList();
        } finally {
            FILE_LOCK.unlock();
        }
    }

    static void saveSchedules(List<Map<String, Object>> schedules) {
        File scheduleFile = getSystemScheduleFile();
        FILE_LOCK.lock();
        try {
            File parent = scheduleFile.getParentFile();
            if (parent != null && !parent.exists()) {
                FileUtils.forceMkdir(parent);
            }
            String content = JsonOutput.prettyPrint(JsonOutput.toJson(schedules));
            FileUtils.writeStringToFile(scheduleFile, content + System.lineSeparator(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to write system schedule file " + scheduleFile.getAbsolutePath(), ex);
        } finally {
            FILE_LOCK.unlock();
        }
    }

    static Map<String, Object> findScheduleById(String scheduleId) {
        for (Map<String, Object> schedule : loadSchedules()) {
            if (scheduleId.equals(stringValue(schedule, "id", ""))) {
                return schedule;
            }
        }
        return null;
    }

    static List<Map<String, Object>> upsertSchedule(Map<String, Object> schedule) {
        String scheduleId = stringValue(schedule, "id", "");
        if (StringUtils.isBlank(scheduleId)) {
            throw new IllegalArgumentException("Schedule id is required");
        }
        List<Map<String, Object>> schedules = new ArrayList<>(loadSchedules());
        boolean replaced = false;
        for (int i = 0; i < schedules.size(); i++) {
            if (scheduleId.equals(stringValue(schedules.get(i), "id", ""))) {
                schedules.set(i, new LinkedHashMap<>(schedule));
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            schedules.add(new LinkedHashMap<>(schedule));
        }
        saveSchedules(schedules);
        return schedules;
    }

    static boolean deleteSchedule(String scheduleId) {
        List<Map<String, Object>> schedules = new ArrayList<>(loadSchedules());
        boolean removed = schedules.removeIf(schedule -> scheduleId.equals(stringValue(schedule, "id", "")));
        if (removed) {
            saveSchedules(schedules);
        }
        return removed;
    }

    /**
     * Converts the root schedule array from systemschedule.json into schedule entries.
     */
    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> extractSchedules(List<?> scheduleArray) {
        List<Map<String, Object>> schedules = new ArrayList<>();
        if (scheduleArray == null) {
            return schedules;
        }
        for (Object entry : scheduleArray) {
            if (entry instanceof Map<?, ?> map) {
                schedules.add((Map<String, Object>) map);
            }
        }
        return schedules;
    }

    static File getSystemScheduleDirectory() {
        return new File(ApplicationProperties.getInstance().getSystemDirectory()
                + File.separator + "Admin" + File.separator + SYSTEM_SCHEDULE_FOLDER);
    }

    static File getSystemScheduleFile() {
        return new File(getSystemScheduleDirectory(), SYSTEM_SCHEDULE_JSON);
    }

    static String stringValue(Map<String, Object> map, String key, String defaultValue) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return defaultValue;
        }
        String value = String.valueOf(map.get(key));
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private static String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private record TaskConfig(String className, String script, String functionName) {
    }
}
