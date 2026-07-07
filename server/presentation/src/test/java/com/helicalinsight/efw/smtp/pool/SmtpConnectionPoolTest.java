package com.helicalinsight.efw.smtp.pool;

import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Test;

import com.helicalinsight.efw.smtp.transport.factory.SmtpConnectionFactory;

public class SmtpConnectionPoolTest {

	@Test
	public void test() {
		SmtpConnectionFactory factory =new  SmtpConnectionFactory(null, null, null, false);
		GenericObjectPoolConfig config = new GenericObjectPoolConfig<>();
		AbandonedConfig abandonedConfig = new AbandonedConfig();
		SmtpConnectionPool connectionPool = new SmtpConnectionPool(factory,config,abandonedConfig);
	}
	
	@Test
	public void testfac()  {
		SmtpConnectionFactory factory =new  SmtpConnectionFactory(null, null, null, false);
		SmtpConnectionPool connectionPool = new SmtpConnectionPool(factory);
		connectionPool.getSession();
	}
	
	
}
