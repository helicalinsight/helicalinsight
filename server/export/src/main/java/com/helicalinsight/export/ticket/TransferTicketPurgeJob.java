package com.helicalinsight.export.ticket;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * Quartz job: purge expired resumable download/upload tickets (shared registry).
 */
public class TransferTicketPurgeJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(TransferTicketPurgeJob.class);

	@Override
	public void execute(JobExecutionContext context) {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String scheduleId = dataMap.getString("scheduleId");
		try {
			TransferTicketService ticketService = ApplicationContextAccessor.getBean(TransferTicketService.class);
			ticketService.purgeExpired();
			String result = "System schedule " + scheduleId + " purged expired transfer tickets";
			context.setResult(result);
			logger.debug(result);
		} catch (Exception ex) {
			String result = "System schedule " + scheduleId + " failed: " + ex.getMessage();
			context.setResult(result);
			logger.error("System schedule {} failed while purging transfer tickets", scheduleId, ex);
			throw new RuntimeException(result, ex);
		}
	}
}
