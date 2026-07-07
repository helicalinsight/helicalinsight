//TODO: configuration issue revisit it again
//package com.helicalinsight.export;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//
//import java.lang.reflect.Field;
//
//import org.junit.Assert;
//import org.junit.Test;
//import org.mockito.MockedStatic;
//
//import com.google.gson.JsonObject;
//import com.helicalinsight.efw.exceptions.ExportException;
//import com.helicalinsight.efw.utility.ApplicationUtilities;
//import com.helicalinsight.efw.utility.JsonUtils;
//
//public class PhantomThreadDemonManagerTest {
//
//	@Test
//	public void ut_a1_testStart() {
//		PhantomThreadDemonManager demonManager = new PhantomThreadDemonManager();
//		demonManager.start();
//	}
//
//	@Test
//	public void ut_a2_testStart()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		PhantomThreadDemonManager demonManager = new PhantomThreadDemonManager();
//		Field field = PhantomThreadDemonManager.class.getDeclaredField("daemonPhantom");
//		field.setAccessible(true);
//		field.set(demonManager, null);
//		demonManager.start();
//	}
//
//	@Test
//	public void ut_a3_testStop() {
//		PhantomThreadDemonManager demonManager = new PhantomThreadDemonManager();
//		demonManager.stop();
//	}
//
//	@Test
//	public void ut_a4_testStop()
//			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
//		PhantomThreadDemonManager demonManager = new PhantomThreadDemonManager();
//		Thread thread = mock(Thread.class);
//		Field field = PhantomThreadDemonManager.class.getDeclaredField("phantomThread");
//		field.setAccessible(true);
//		field.set(demonManager, thread);
//		demonManager.stop();
//	}
//
//	@Test(expected = ExportException.class)
//	public void ut_a5_testHasValidConfiguration() {
//		PhantomThreadDemonManager demonManager = new PhantomThreadDemonManager();
//		JsonObject object = new JsonObject();
//		JsonObject json = new JsonObject();
//		object.add("export", json);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			demonManager.stop();
//		}
//
//	}
//
//	@Test
//	public void ut_a6_testGetMachineAddress() {
//		String machineAddress = PhantomThreadDemonManager.getMachineAddress();
//		Assert.assertNotNull(machineAddress);
//
//	}
//
//	@Test
//	public void ut_a7_testGetPhantomPort() {
//
//		JsonObject object = new JsonObject();
//		JsonObject json = new JsonObject();
//		json.addProperty("phantomPort", 0);
//		object.add("export", json);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			Integer poolSize = PhantomThreadDemonManager.getPhantomPort();
//			Integer size = 3000;
//			Assert.assertEquals(size, poolSize);
//		}
//	}
//
//	@Test
//	public void ut_a8_testGetPhantomPort() {
//
//		JsonObject object = new JsonObject();
//		JsonObject json = new JsonObject();
//		json.addProperty("phantomPort", 0);
//		object.add("export", json);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			Integer poolSize = PhantomThreadDemonManager.getPhantomPort();
//			Integer size = 3000;
//			Assert.assertEquals(size, poolSize);
//		}
//	}
//
//	@Test
//	public void ut_a9_testGetPhantomPort() {
//
//		JsonObject object = new JsonObject();
//		JsonObject json = new JsonObject();
//		object.add("export", json);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			Integer poolSize = PhantomThreadDemonManager.getPhantomPort();
//			Integer size = null;
//			Assert.assertEquals(size, poolSize);
//		}
//	}
//
//	@Test
//	public void ut_b1_testGetPhantomPort() {
//
//		JsonObject object = new JsonObject();
//		object.add("export", null);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			Integer poolSize = PhantomThreadDemonManager.getPhantomPort();
//			Integer size = null;
//			Assert.assertEquals(size, poolSize);
//		}
//	}
//
//	@Test
//	public void ut_b2_testGetMessage() {
//		String message = PhantomThreadDemonManager.getMessage();
//		Assert.assertNull(message);
//	}
//	
//	@Test
//	public void ut_b3_testSetUp() {
//		PhantomThreadDemonManager manager = new PhantomThreadDemonManager();
//		JsonObject object = new JsonObject();
//		JsonObject json = new JsonObject();
//		object.add("export", json);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			boolean setUp = manager.setUp();
//			Assert.assertFalse(setUp);
//		}	
//		
//	}
//	
//	@Test
//	public void ut_b4_testSetUp() {
//		PhantomThreadDemonManager manager = new PhantomThreadDemonManager();
//		JsonObject object = new JsonObject();
//		object.add("export", null);
//		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
//			mockedStatic.when(() -> JsonUtils.newGetSettingsJson()).thenReturn(object);
//			boolean setUp = manager.setUp();
//			Assert.assertFalse(setUp);
//		}	
//		
//	}
//	
//	@Test
//	public void ut_b5_testCleanUp() {
//		PhantomThreadDemonManager manager = new PhantomThreadDemonManager();
//		boolean cleanUp = manager.cleanUp();
//		Assert.assertFalse(cleanUp);
//	}
//	
//	@Test
//	public void ut_b6_testCheckPortActive() {
//		
//		try(MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)){
//			mockedStatic.when(()-> ApplicationUtilities.isPortAvailable(any(Integer.class), any(String.class))).thenReturn(false);
//			boolean checkPortActive = PhantomThreadDemonManager.checkPortActive("machineAddress");
//			Assert.assertTrue(checkPortActive);
//		}
//	}
//}
