//TODO:revisit it again , each testcase individually runing fine , at the time whole class is running showing errors.
package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.JsonUtils;

public class DaemonPhantomServiceTest {
	
	@Test(expected = ExportException.class)
	public void ut_a1_testRun() {
		DaemonPhantomService daemonPhantomService = new DaemonPhantomService();
		daemonPhantomService.run();
	}
	
	@Test
	public void ut_a2_testGetDaemonTime() {
		Integer daemonTime = DaemonPhantomService.getDaemonTime();
		Integer expected = 8000;
		Assert.assertEquals(expected, daemonTime);
	}
	
	@Test
	public void ut_a3_testGetDaemonTime() {
		JsonObject export = new JsonObject();
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("daemonTimeCheck", 0);
		export.add("export", jsonObject);
		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(export);
			Integer daemonTime = DaemonPhantomService.getDaemonTime();
			Integer expected = 8000;
			Assert.assertEquals(expected, daemonTime);
		}
	}
	@Test
	public void ut_a4_testGetDaemonTime() {
		JsonObject export = new JsonObject();
		JsonObject jsonObject = new JsonObject();
		export.add("export", jsonObject);
		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(export);
			Integer daemonTime = DaemonPhantomService.getDaemonTime();
			Assert.assertNull(daemonTime);
		}
	}
	@Test
	public void ut_a5_testGetDaemonTime() {
		
		try(MockedStatic<JsonUtils> mock = mockStatic(JsonUtils.class)){
			mock.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(null);
			Integer daemonTime = DaemonPhantomService.getDaemonTime();
			Assert.assertNull(daemonTime);
		}
	}
	//@Test
	public void ut_a6_testStopPhantomService() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DaemonPhantomService daemonPhantomService = new DaemonPhantomService();
		Field field = DaemonPhantomService.class.getDeclaredField("isRunnable");
        field.setAccessible(true); // Allow access to private field
        field.set(null, true); 
		
        Field field2 = DaemonPhantomService.class.getDeclaredField("process");
        field2.setAccessible(true); // Allow access to private field
        Process mockProcess = mock(Process.class);
        field2.set(null, mockProcess);
		daemonPhantomService.stopPhantomService();
	}
	//@Test
	public void ut_a7_testStopPhantomService() {
		DaemonPhantomService daemonPhantomService = new DaemonPhantomService();
		daemonPhantomService.stopPhantomService();
	}
	@Test
	public void ut_a8_testGetProcess() {
		Process process = DaemonPhantomService.getProcess();
		Assert.assertNull(process);
	}
	
	//@Test
	public void ut_a9_testUrlDecode() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> clazz = DaemonPhantomService.class;
		 Method privateMethod = clazz.getDeclaredMethod("urlDecode",String.class);
		 privateMethod.setAccessible(true);
		 Object invoke = privateMethod.invoke(new DaemonPhantomService(), "World");
		 Assert.assertEquals("World", invoke);
	}
	//@Test
	public void ut_b1_testIsStringURLEncoded() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, UnsupportedEncodingException {
		Class<?> clazz = DaemonPhantomService.class;
		 Method privateMethod = clazz.getDeclaredMethod("isStringURLEncoded",String.class);
		 privateMethod.setAccessible(true);
		 Object invoke = privateMethod.invoke(new DaemonPhantomService(), "World");
		 Assert.assertEquals(false, invoke);
	}
}
