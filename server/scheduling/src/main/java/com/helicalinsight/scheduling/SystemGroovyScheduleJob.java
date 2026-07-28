package com.helicalinsight.scheduling;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Executes a groovy script from the SystemSchedule folder,
 * passing the complete schedule JSON node as a Groovy {@link Map}.
 */
public class SystemGroovyScheduleJob extends SystemScheduleJob {

    public static final String KEY_SCRIPT_PATH = "scriptPath";
    public static final String KEY_FUNCTION_NAME = "functionName";

    private static final String DEFAULT_FUNCTION = "execute";

    private static final Logger logger = LoggerFactory.getLogger(SystemGroovyScheduleJob.class);

    @Override
    protected Object executeSchedule(JobExecutionContext context, Map<String, Object> schedule) throws Exception {
        String scheduleId = context.getJobDetail().getJobDataMap().getString("scheduleId");
        String scriptPath = context.getJobDetail().getJobDataMap().getString(KEY_SCRIPT_PATH);
        String functionName = context.getJobDetail().getJobDataMap().getString(KEY_FUNCTION_NAME);
        if (StringUtils.isBlank(functionName)) {
            functionName = DEFAULT_FUNCTION;
        }

        File scriptFile = new File(scriptPath);
        if (!scriptFile.exists()) {
            throw new IllegalStateException("Groovy script not found at " + scriptPath);
        }
        String groovyCode = FileUtils.readFileToString(scriptFile, Charset.defaultCharset());
        Object result = invokeGroovy(groovyCode, functionName, schedule);
        logger.info("System schedule {} groovy script {} completed", scheduleId, scriptFile.getName());
        return result;
    }

    static Object invokeGroovy(String groovyCode, String functionName, Map<?, ?> schedule) throws Exception {
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        Class<?> groovyClass = classLoader.parseClass(groovyCode);
        GroovyObject groovyObj = (GroovyObject) groovyClass.getDeclaredConstructor().newInstance();
        return groovyObj.invokeMethod(functionName, new Object[]{schedule});
    }
}
