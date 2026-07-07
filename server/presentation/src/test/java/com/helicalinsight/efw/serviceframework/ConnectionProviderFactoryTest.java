package com.helicalinsight.efw.serviceframework;

import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.CustomWatcherUtils;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.SFTPTransferHandler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.ImplementationNotFound;
import com.helicalinsight.efw.utility.NoSqlUtils;
import com.helicalinsight.efw.utility.SendPoolMail;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import net.sf.json.JSONObject;

public class ConnectionProviderFactoryTest {

//	@Test(expected = EfwServiceException.class)
//	public void testGetConnectionFromTemp() {
//		JSONObject formJson = new JSONObject();
//		formJson.put("access", "DataSourceSecurityUtility");
//		formJson.put("id", "1");
//		formJson.put("cache", "cache");
//		JSONObject obj = new JSONObject();
//		obj.put("file", "file.efwd");
//		formJson.put("efwd", obj);
//		ConnectionProviderFactory.getConnectionFromTemp(formJson, "dataSource");
//	}

//	@Test(expected = JSchException.class)
//	public void testSFTPTransferHandler () throws JSchException, SftpException {
//		SFTPTransferHandler handler = new SFTPTransferHandler();
//		JSch jsch = new JSch();
//		Session session = jsch.getSession("user", "host", 22);
//		JSONObject parameters = new JSONObject();
//		parameters.put("openChannelType","");
//		handler.getChannel(session, parameters);
//	}

	@Test
	public void testcloseEntryPointClass() {
		CustomWatcherUtils util  = new CustomWatcherUtils ();
		CustomWatcherUtils.closeEntryPointClass(new File("test.txt"));
	}
	
	@Test
	public void test() {
		SendPoolMail mail  = mock(SendPoolMail.class);
		mail.destroy();
		mail.getDurationStat();
		mail.setDurationStat(null);
		mail.getPropertiesMap();
		mail.getDurationStat();
		mail.getMailFailed();
		mail.getMailSend();
	}
	
	@Test(expected = ImplementationNotFound.class)
	public void testNoSqlUtil () {
		NoSqlUtils noSqlUtils= new NoSqlUtils();
		NoSqlUtils.getNoSqlImplementation(null);
	
	}
}
