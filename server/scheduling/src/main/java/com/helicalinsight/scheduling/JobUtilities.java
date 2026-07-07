package com.helicalinsight.scheduling;

import org.quartz.JobExecutionContext;
/**
 * JobUtilities
 * utility class for providing information  about job detail , execution and to trigger.
 * This class stores and manages the {@link org.quartz.JobExecutionContext} to provide access
 * to context information during job execution.
 */
public class JobUtilities {

    private static JobExecutionContext jobcontext;
    /**
     * getJobcontext()
     * Gives the stored Context.
     * @returns stored JobExecutionContext.
     */
    public static JobExecutionContext getJobcontext() {
        return jobcontext;
    }
    /**
     * setJobcontext(JobExecutionContext jobcontext) 
     * Setter of job context .
     * @param jobcontext    Context to be stored
     */
    public static void setJobcontext(JobExecutionContext jobcontext) {
        JobUtilities.jobcontext = jobcontext;
    }

}
