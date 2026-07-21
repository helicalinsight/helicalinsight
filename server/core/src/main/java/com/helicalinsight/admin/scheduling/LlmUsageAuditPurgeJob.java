package com.helicalinsight.admin.scheduling;

import com.helicalinsight.admin.service.LlmUsageAuditService;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class LlmUsageAuditPurgeJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(LlmUsageAuditPurgeJob.class);

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String scheduleId = dataMap.getString("scheduleId");
        int retentionDays = Integer.parseInt(dataMap.getString("retentionDays"));
        String exportPath = dataMap.getString("exportPath");
        Date cutoffDate = calculateCutoffDate(retentionDays);

        try {
            LlmUsageAuditService auditService = ApplicationContextAccessor.getBean(LlmUsageAuditService.class);
            int purgedCount = auditService.purgeOlderThan(cutoffDate, exportPath);
            logger.info("System schedule {} purged {} LLM audit records older than {} days",
                    scheduleId, purgedCount, retentionDays);
        } catch (Exception ex) {
            logger.error("System schedule {} failed while purging LLM audit records", scheduleId, ex);
        }
    }

    static Date calculateCutoffDate(int retentionDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -Math.max(retentionDays, 1));
        return calendar.getTime();
    }
}
