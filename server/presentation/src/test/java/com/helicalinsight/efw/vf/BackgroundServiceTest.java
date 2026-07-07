package com.helicalinsight.efw.vf;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.BackgroundService;
import com.helicalinsight.efw.DatasourceOperations;
import com.helicalinsight.efw.HIManagedThread;

import net.sf.json.JSONObject;

public class BackgroundServiceTest {

	@Test
	public void teststopAllService() {
		BackgroundService.stopAllService();
	}
	//AppStatistics 
	@Test
	public void testsetRESOURCE_MEMORY() {
		AppStatistics.getPORT_MASTER();
		AppStatistics.setRESOURCE_MEMORY("temp");
		Assert.assertEquals("temp",AppStatistics.getRESOURCE_MEMORY());
		AppStatistics.setPORT_WORKER("temp");
		Assert.assertEquals("temp",AppStatistics.getPORT_WORKER());
		AppStatistics.setPORT_SPARK("temp");
		Assert.assertEquals("temp",AppStatistics.getPORT_SPARK());
		AppStatistics.setWEB_UI_PORT_MASTER("temp");
		Assert.assertEquals("temp",AppStatistics.getWEB_UI_PORT_MASTER());
		AppStatistics.setWEB_UI_PORT_WORKER("temp");
		Assert.assertEquals("temp",AppStatistics.getWEB_UI_PORT_WORKER());
		AppStatistics.setWEB_UI_PORT_SPARK("temp");
		Assert.assertEquals("temp",AppStatistics.getWEB_UI_PORT_SPARK());
		
	}
	@Test
	public void testisWORKER_STARTED() {
		AppStatistics.setWORKER_STARTED(true);
		Assert.assertTrue(AppStatistics.isWORKER_STARTED());
		
		AppStatistics.setSPARK_STARTED(true);
		Assert.assertTrue(AppStatistics.isSPARK_STARTED());
		
		AppStatistics.setLOCK_WORKER(true);
		Assert.assertTrue(AppStatistics.isLOCK_WORKER());
		
		AppStatistics.setLOCK_SPARK(true);
		Assert.assertTrue(AppStatistics.isLOCK_SPARK());
		
	}
	
	@Test
	public void testgetAllStatus() {
		AppStatistics.getAllStatus();
	}
	@Test
	public void testaddConfiguration() {
		AppStatistics.addConfiguration("key", "value", true);
	}
	@Test
	public void testreadFile() {
		AppStatistics.readFile();
	}
	
	//DatasourceOperations
	
	@Test(expected = NullPointerException.class)
	public void testshareUsers() {
		DatasourceOperations operations = new DatasourceOperations();
		JsonObject usersObject = new JsonObject();
		usersObject.addProperty("id", 101);
		usersObject.addProperty("text", 102);
	     int globalId  = 10;
	     Date d = new Date();
	     String createdBy = d.toString();
		operations.shareUsers(usersObject, globalId, createdBy);
	}
	
	@Test(expected = NullPointerException.class)
	public void testshareOrg() {
		DatasourceOperations operations = new DatasourceOperations();
		JsonObject usersObject = new JsonObject();
		usersObject.addProperty("id", 101);
		usersObject.addProperty("text", 102);
	     int globalId  = 10;
	     Date d = new Date();
	     String createdBy = d.toString();
		operations.shareOrg(usersObject, globalId, createdBy);
	}
	@Test
	public void testHIManagedThread() {
		HIManagedThread thread = new HIManagedThread();
	}
}
