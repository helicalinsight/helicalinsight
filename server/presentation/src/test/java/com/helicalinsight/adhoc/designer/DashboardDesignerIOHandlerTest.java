package com.helicalinsight.adhoc.designer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Efw;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DashboardDesignerIOHandlerTest {

	@Test
	public void ut_a1_test_saveHtml() {
		
		try (MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)) {
			mockedStatic.when(() -> ApplicationUtilities.createAFile(any(), anyString())).thenReturn(true);
			File saveHtml = DashboardDesignerIOHandler.saveHtml("dir", "htmlString", "uuid");
			assertNotNull(saveHtml);
		}
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a2_test_saveHtml() {

		try (MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)) {
			mockedStatic.when(() -> ApplicationUtilities.createAFile(any(), anyString())).thenReturn(false);
			DashboardDesignerIOHandler.saveHtml("dir", "htmlString", "uuid");

		}
	}

	@Test
	public void ut_a3_test_isThreadSafeToCache() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		boolean threadSafeToCache = dashboardDesignerIOHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a4_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "");
		dashboardDesignerIOHandler.executeComponent(formJson.toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a5_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.add("uuid", null);
		dashboardDesignerIOHandler.executeComponent(formJson.toString());
	}

	@Test
	public void ut_a6_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("uuid", "uuid");
		try (MockedConstruction<DashboardDesignerUpdateHandler> construction = mockConstruction(
				DashboardDesignerUpdateHandler.class, (mock, context) -> {
					when(mock.executeComponent(anyString())).thenReturn("result");
				})) {
			String executeComponent = dashboardDesignerIOHandler.executeComponent(formJson.toString());
			assertEquals("result", executeComponent);
		}
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a7_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("state", "state");
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("dir", "dir");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("description", "description");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("style", "style");
		EfwDashboardDesigner designer = mock(EfwDashboardDesigner.class);

		try (MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)) {
			mockedStatic.when(() -> ApplicationUtilities.createAFile(any(), anyString())).thenReturn(true);
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<SecurityUtils> mockedStatic3 = mockStatic(SecurityUtils.class)) {
					mockedStatic2.when(() -> ApplicationContextAccessor.getBean(EfwDashboardDesigner.class))
							.thenReturn(designer);

					dashboardDesignerIOHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test
	public void ut_a8_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("state", "state");
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("dir", "dir");
		formJson.addProperty("htmlString", "htmlString");
		formJson.addProperty("description", "description");
		formJson.addProperty("icon", "icon");
		formJson.addProperty("style", "style");
		EfwDashboardDesigner designer = mock(EfwDashboardDesigner.class);
		Efw efw = mock(Efw.class);

		try (MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<SecurityUtils> mockedStatic3 = mockStatic(SecurityUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic4 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<AuthenticationUtils> mockedStatic5 = mockStatic(AuthenticationUtils.class)) {

							mockedStatic5.when(() -> AuthenticationUtils.getUserName()).thenReturn("helical");

							mockedStatic.when(() -> ApplicationUtilities.createAFile(any(), anyString()))
									.thenReturn(true);
							mockedStatic2.when(() -> ApplicationContextAccessor.getBean(EfwDashboardDesigner.class))
									.thenReturn(designer);
							mockedStatic2.when(() -> ApplicationContextAccessor.getBean(Efw.class)).thenReturn(efw);

							dashboardDesignerIOHandler.executeComponent(formJson.toString());

						}
					}
				}
			}
		}
	}

	@Test
	public void ut_a9_test_executeComponent() {
		DashboardDesignerIOHandler dashboardDesignerIOHandler = new DashboardDesignerIOHandler();
		JsonObject formJson = new JsonObject();
		formJson.addProperty("state", "state");
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("dir", "dir");
		formJson.addProperty("htmlString", "htmlString");

		formJson.addProperty("icon", "icon");
		formJson.addProperty("style", "style");
		EfwDashboardDesigner designer = mock(EfwDashboardDesigner.class);
		Efw efw = mock(Efw.class);

		try (MockedStatic<ApplicationUtilities> mockedStatic = mockStatic(ApplicationUtilities.class)) {
			try (MockedStatic<ApplicationContextAccessor> mockedStatic2 = mockStatic(
					ApplicationContextAccessor.class)) {
				try (MockedStatic<SecurityUtils> mockedStatic3 = mockStatic(SecurityUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic4 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<AuthenticationUtils> mockedStatic5 = mockStatic(AuthenticationUtils.class)) {
							mockedStatic5.when(() -> AuthenticationUtils.getUserName()).thenReturn("helical");

							mockedStatic.when(() -> ApplicationUtilities.createAFile(any(), anyString()))
									.thenReturn(true);
							mockedStatic2.when(() -> ApplicationContextAccessor.getBean(EfwDashboardDesigner.class))
									.thenReturn(designer);
							mockedStatic2.when(() -> ApplicationContextAccessor.getBean(Efw.class)).thenReturn(efw);

							dashboardDesignerIOHandler.executeComponent(formJson.toString());

						}
					}
				}
			}
		}
	}

}
