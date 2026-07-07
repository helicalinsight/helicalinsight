package com.helicalinsight.efw.smtp.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.smtp.transport.connection.ClosableSmtpConnection;
import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactory;

public class SmtpConnectionValidator {

	private static final Logger logger = LoggerFactory.getLogger(SmtpConnectionValidator.class);

	public static boolean validate(SmtpConnectionFactory factory, GenericObjectPoolConfig<?> config) {
		config.setMaxIdle(0);
		try (SmtpConnectionPool smtpConnectionPool = new SmtpConnectionPool(factory, config);
				ClosableSmtpConnection transport = smtpConnectionPool.borrowObject()) {
			logger.debug("SMTP Connection is successful.");
			return true;
		}
		catch (Exception e) {
			logger.error("SMTP Connection failed, due to {}",e);
			return false;
		}
	}
}
