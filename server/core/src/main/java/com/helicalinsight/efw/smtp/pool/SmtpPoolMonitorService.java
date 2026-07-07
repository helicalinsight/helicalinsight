package com.helicalinsight.efw.smtp.pool;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmtpPoolMonitorService implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(SmtpPoolMonitorService.class);
	
	private  long monitoringPeriod = 60;
	private String errorMessage;
	
	private SmtpConnectionPool pool;
	
	public void run() {
		try {
			while (true ) {
				if ( pool != null) {
					monitorMailConnectionPool();
				}
				if (StringUtils.isNotBlank(errorMessage)) {
					logger.info(errorMessage);
				}
				Thread.sleep(monitoringPeriod * 1000);
			}
		} catch (Exception e) {
			logger.error("Exception occurred", e);
		}
	}

	public void monitorMailConnectionPool() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("ActiveConnectionCount : ").append(pool.getNumActive());
		strBuff.append(" - Borrowed Count : ").append(pool.getBorrowedCount());
		strBuff.append(" - Created Count : ").append(pool.getCreatedCount());
		strBuff.append(" - Max Idle Count : ").append(pool.getMaxIdle());
		strBuff.append(" - Max Total Count : ").append(pool.getMaxTotal());
		strBuff.append(" - Idle count : ").append(pool.getNumIdle());
		logger.debug(strBuff.toString());
	}


	public void setConnectionPool(SmtpConnectionPool pool) {
		this.pool = pool;
	}

	public void setMonitoringPeriod(long monitoringPeriod) {
		this.monitoringPeriod = monitoringPeriod;
	}
	
	public void setErrorMessage(String error) {
		this.errorMessage = error;
	}

}
