package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;

public class PhantomServicePoolManagerTest {

	@Test
	public void ut_a1_testHandlePhantomjs() {
		PhantomServicePoolManager manager = new PhantomServicePoolManager();
		JsonObject object = new JsonObject();
		object.addProperty("phantomLocation", "phantomLocation");
		object.addProperty("scriptLocation", "scriptLocation");
		object.addProperty("reportSourceUri", "reportSourceUri");
		object.addProperty("destinationFile", "destinationFile");

		try (MockedConstruction<PhantomJS> constructor = mockConstruction(PhantomJS.class, (mock, context) -> {

		})) {
			List<String> handlePhantomjs = manager.handlePhantomjs(object);
			Assert.assertEquals(2, handlePhantomjs.size());
		}
	}

	@Test
	public void ut_a2_testHandlePhantomjs() {
		PhantomServicePoolManager manager = new PhantomServicePoolManager();
		JsonObject object = new JsonObject();
		object.addProperty("phantomLocation", "phantomLocation");
		object.addProperty("scriptLocation", "scriptLocation");
		object.addProperty("reportSourceUri", "reportSourceUri");
		object.addProperty("destinationFile", "destinationFile");

		PhantomJS js = mock(PhantomJS.class);
		try (MockedConstruction<PhantomJS> constructor = mockConstruction(PhantomJS.class, (mock, context) -> {
		})) {

			try (MockedStatic<PhantomJS> mockedStatic = mockStatic(PhantomJS.class)) {
				mockedStatic.when(() -> PhantomJS.getCounter()).thenReturn(4).thenReturn(2);

				List<String> handlePhantomjs = manager.handlePhantomjs(object);
				Assert.assertEquals(2, handlePhantomjs.size());
			}

		}
	}
	@Test
	public void ut_a3_testGetPoolSize() {
		PhantomServicePoolManager manager = new PhantomServicePoolManager();
		JsonObject object = new JsonObject();
		JsonObject json = new JsonObject();
		json.addProperty("phanmtomPoolSize", 0);
		object.add("export", json);
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(object);
			Integer poolSize = PhantomServicePoolManager.getPoolSize();
			Integer size = 3;
			Assert.assertEquals(size, poolSize);
		}
	}
	@Test
	public void ut_a4_testGetPoolSize() {
		PhantomServicePoolManager manager = new PhantomServicePoolManager();
		JsonObject object = new JsonObject();
		JsonObject json = new JsonObject();
		object.add("export", json);
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(object);
			Integer poolSize = PhantomServicePoolManager.getPoolSize();
			Integer size = null;
			Assert.assertEquals(size, poolSize);
		}
	}
	@Test
	public void ut_a5_testGetPoolSize() {
		PhantomServicePoolManager manager = new PhantomServicePoolManager();
		JsonObject object = new JsonObject();
		object.add("export", null);
		try(MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)){
			mockedStatic.when(()-> JsonUtils.newGetSettingsJson()).thenReturn(object);
			Integer poolSize = PhantomServicePoolManager.getPoolSize();
			Integer size = null;
			Assert.assertEquals(size, poolSize);
		}
	}
}
