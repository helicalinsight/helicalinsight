package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardDesignerUpdateHandlerTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		boolean threadSafeToCache = dashboardDesignerUpdateHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test
	public void ut_a2_test_executeComponent() {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		JsonObject formJson = new JsonObject();
		formJson.add("uuid", null);
		String executeComponent = dashboardDesignerUpdateHandler.executeComponent(formJson.toString());
		assertNull(executeComponent);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a3_test_executeComponent() {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "uuid.designer");
		formJson.addProperty("state", "state");
		formJson.addProperty("dir", "dir");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("style", "style");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("description", "description");

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			mockedStatic.when(() -> JsonUtils.getDesignerExtension()).thenReturn("designer");
			dashboardDesignerUpdateHandler.executeComponent(formJson.toString());

		}
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a4_test_executeComponent() throws IOException {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "uuid.designer");
		formJson.addProperty("state", "state");
		formJson.addProperty("dir", "System");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("style", "style");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("description", "description");

		String path = ApplicationProperties.INSTANCE.getSolutionDirectory() + File.separator+"uuid.designer";
		File file = new File(path);
		file.createNewFile();

		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			mockedStatic.when(() -> JsonUtils.getDesignerExtension()).thenReturn("designer");
			mockedStatic.when(() -> JsonUtils.getEfwExtension()).thenReturn("efw");

			dashboardDesignerUpdateHandler.executeComponent(formJson.toString());

		} finally {
			file.delete();
		}
	}

	@Test(expected = NullPointerException.class)
	public void ut_a5_test_executeComponent() throws IOException {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "uuid.designer");
		formJson.addProperty("state", "state");
		formJson.addProperty("dir", "System");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("style", "style");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("description", "description");

		String path = ApplicationProperties.INSTANCE.getSolutionDirectory() + File.separator+"uuid.designer";
		File file = new File(path);
		file.createNewFile();
		String path2 = ApplicationProperties.INSTANCE.getSettingPath() + File.separator+"uuid.efw";
		File file2 = new File(path2);
		file2.createNewFile();

		
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {

				mockedStatic2.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(null);
				mockedStatic.when(() -> JsonUtils.getDesignerExtension()).thenReturn("designer");
				mockedStatic.when(() -> JsonUtils.getEfwExtension()).thenReturn("efw");

				dashboardDesignerUpdateHandler.executeComponent(formJson.toString());

			}
		} finally {
			file.delete();
			file2.delete();
		}
	}

	@Test
	public void ut_a6_test_executeComponent() throws IOException {
		DashboardDesignerUpdateHandler dashboardDesignerUpdateHandler = new DashboardDesignerUpdateHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "uuid.designer");
		formJson.addProperty("state", "state");
		formJson.addProperty("dir", "System");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("style", "style");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("description", "description");

		String path = ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"uuid.designer";
		File file = new File(path);
		file.createNewFile();
		String path2 = ApplicationProperties.INSTANCE.getSystemDirectory() + File.separator+"uuid.efw";
		File file2 = new File(path2);
		file2.createNewFile();
		Efw existingEfw = mock(Efw.class);
		EfwDashboardDesigner dashboardDesigner = mock(EfwDashboardDesigner.class);
		try (MockedStatic<JsonUtils> mockedStatic = mockStatic(JsonUtils.class)) {
			try (MockedStatic<JaxbUtils> mockedStatic2 = mockStatic(JaxbUtils.class)) {
				try (MockedStatic<DashboardDesignerIOHandler> mockedStatic3 = mockStatic(
						DashboardDesignerIOHandler.class)) {

					mockedStatic2.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(dashboardDesigner)
							.thenReturn(existingEfw);
					mockedStatic.when(() -> JsonUtils.getDesignerExtension()).thenReturn("designer");
					mockedStatic.when(() -> JsonUtils.getEfwExtension()).thenReturn("efw");

					String executeComponent = dashboardDesignerUpdateHandler.executeComponent(formJson.toString());
					String result = JsonParser.parseString(executeComponent).getAsJsonObject().get("message").getAsString();
					assertEquals("Design is edited successfully", result);

				}
			}
		} finally {
			file.delete();
			file2.delete();
		}
	}

}
