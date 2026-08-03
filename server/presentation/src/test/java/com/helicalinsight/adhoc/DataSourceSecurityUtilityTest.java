// TODO: Configuration issue

package com.helicalinsight.adhoc;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.EfwdReaderUtility;
import com.helicalinsight.efw.components.GlobalDSReaderUtility;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.sf.json.JSONObject;

@RunWith(MockitoJUnitRunner.class)
public class DataSourceSecurityUtilityTest {

	@Mock
	private IProcessor processor;

	@Test(expected = Exception.class)
	public void testThrowException() {
		DataSourceSecurityUtility.throwException();
	}

	@Test(expected = Exception.class)
	public void theThrowResourceNotFoundException() {
		DataSourceSecurityUtility.throwResourceNotFoundException();
	}

	@Test
	public void testisGlobalAccessible() {

		DataSourceSecurityUtility.isGlobalAccessible("", "READ");
	}

//	@Test
	public void testgetPermissionLevel() {
		String str = "READ_WRITE";
		DataSourceSecurityUtility.getPermissionLevel(str);
	}

	@Test
	public void testValidateGlobalDataSourceAccessForWriteOperation() {

		DataSourceSecurityUtility.validateGlobalDataSourceAccessForWriteOperation("", "edit");
	}

//	@Test(expected =EfwException.class)
	public void testValidateGlobalDataSourceAccessForDeleteOperation() {

		DataSourceSecurityUtility.validateGlobalDataSourceAccessForDeleteOperation("1", "delete");
	}

	@Test(expected = Exception.class)
	public void testvalidateGlobalDS() {
		JSONObject obj = new JSONObject();
		obj.put("@id", "");
		obj.put("access", "edit");
		boolean validateDataSource = DataSourceSecurityUtility.validateGlobalDS("", obj, "edit");
		System.out.println(validateDataSource);
	}

	@Test(expected = Exception.class)
	public void testcheckEfwdPermission() {
		File file = new File("EFWD.txt");
		DataSourceSecurityUtility.checkEfwdPermission("", file, "edit");
	}

	@Test
	public void testGetRequiredConnectionDetails_Global() {
		JsonObject json = createConnectionJson("global.jdbc", "100", null);
		JsonObject result = DataSourceSecurityUtility.getRequiredConnectionDetails(json);
		assertEquals("global.jdbc", result.get("type").getAsString());
		assertEquals("100", result.get("connectionId").getAsString());
	}

	@Test
	public void testGetRequiredConnectionDetails_Efwd() {
		JsonObject json = createConnectionJson("sql.jdbc", "", "55");
		JsonObject result = DataSourceSecurityUtility.getRequiredConnectionDetails(json);
		assertEquals("sql.jdbc", result.get("type").getAsString());
		assertEquals("55", result.get("connectionId").getAsString());
	}
	
	@Test
	public void testAuthenticateTempReport_WithTempFile() throws Exception {
		File tempDir = new File("temp");
		File efwdFile = new File("temp/report.efwd");
		JsonObject fileJson = new JsonObject();
		fileJson.addProperty("id", "10");
		IProcessor processor = mock(IProcessor.class);

		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class);
				MockedStatic<ApplicationUtilities> appMock = Mockito.mockStatic(ApplicationUtilities.class);
				MockedStatic<ResourceProcessorFactory> processorMock = Mockito.mockStatic(ResourceProcessorFactory.class)) {
			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(tempDir);
			appMock.when(() -> ApplicationUtilities.getEfwdFileFromTemp(tempDir.getAbsolutePath(), "report.efwd")).thenReturn(efwdFile);
			processorMock.when(ResourceProcessorFactory::getIProcessor).thenReturn(processor);
			when(processor.getJsonObject(efwdFile.toString(), true)).thenReturn(fileJson);
			Method method = DataSourceSecurityUtility.class.getDeclaredMethod("authenticateTempReport", String.class,String.class);
			method.setAccessible(true);
			method.invoke(null, "report.efwd", DataSourceSecurityUtility.EXECUTE);
			verify(processor).getJsonObject(efwdFile.toString(), true);
	    }
	}
	
	@Test
	public void testAuthenticateTempReport_WithoutTempFile() throws Exception {

		File tempDir = new File("temp");
		File efwdFile = new File("temp/default.efwd");

		JsonObject fileJson = new JsonObject();
		IProcessor processor = mock(IProcessor.class);
		try (MockedStatic<TempDirectoryCleaner> tempMock = Mockito.mockStatic(TempDirectoryCleaner.class);
				MockedStatic<ApplicationUtilities> appMock = Mockito.mockStatic(ApplicationUtilities.class);
				MockedStatic<ResourceProcessorFactory> processorMock = Mockito.mockStatic(ResourceProcessorFactory.class)) {

			tempMock.when(TempDirectoryCleaner::getTempDirectory).thenReturn(tempDir);
			appMock.when(() -> ApplicationUtilities.getTempEfwdFile(tempDir.getAbsolutePath())).thenReturn(efwdFile);
			processorMock.when(ResourceProcessorFactory::getIProcessor).thenReturn(processor);
			when(processor.getJsonObject(efwdFile.toString(), true)).thenReturn(fileJson);
			Method method = DataSourceSecurityUtility.class.getDeclaredMethod("authenticateTempReport", String.class,String.class);
			method.setAccessible(true);
			method.invoke(null, null, DataSourceSecurityUtility.EXECUTE);
			verify(processor).getJsonObject(efwdFile.toString(), true);
		}
	}
	@Test
	public void testAuthenticateSavedReport_BlankFileName() throws Exception {
		try (MockedStatic<HCRUtils> hcrMock = Mockito.mockStatic(HCRUtils.class)) {
			Method method = DataSourceSecurityUtility.class.getDeclaredMethod("authenticateSavedReport", String.class,String.class);
			method.setAccessible(true);
			method.invoke(null, "", DataSourceSecurityUtility.EXECUTE);
			hcrMock.verifyNoInteractions();
		}
	}
	
	@Test
	public void testAuthenticateSavedReport_NonNumericPrefix() throws Exception {
	    try (MockedStatic<HCRUtils> hcrMock = Mockito.mockStatic(HCRUtils.class)) {
	        Method method = DataSourceSecurityUtility.class.getDeclaredMethod("authenticateSavedReport",String.class,String.class);
	        method.setAccessible(true);
	        method.invoke(null, "abc_hi_hcr_db.efwd", DataSourceSecurityUtility.EXECUTE);
	        hcrMock.verifyNoInteractions();
	    }
	}
	
	@Test
	public void testAuthenticateSavedReport_NumericPrefix() throws Exception {
	    JsonObject connectionJson = new JsonObject();
	    connectionJson.addProperty("id", "10");
	    try (MockedStatic<HCRUtils> hcrMock = Mockito.mockStatic(HCRUtils.class)) {
	        hcrMock.when(() -> HCRUtils.prepareConnectionJson("123")).thenReturn(connectionJson);
	        Method method = DataSourceSecurityUtility.class.getDeclaredMethod("authenticateSavedReport", String.class, String.class);
	        method.setAccessible(true);
	        method.invoke(null, "123hi_hcr_db.efwd", DataSourceSecurityUtility.EXECUTE);
	        hcrMock.verify(() -> HCRUtils.prepareConnectionJson("123"));
	    }
	}

	@Test
	public void testIsDataSourceAuthenticated_GlobalAccessible() {
	    JsonObject form = new JsonObject();
	    form.addProperty("id", "10");
	    form.addProperty("dir", "");
	    form.addProperty("access", DataSourceSecurityUtility.EXECUTE);

	    try (MockedStatic<ApplicationContextAccessor> contextMock = Mockito
				.mockStatic(ApplicationContextAccessor.class);
				MockedStatic<DataSourceSecurityUtility> securityMock = Mockito
						.mockStatic(DataSourceSecurityUtility.class, Mockito.CALLS_REAL_METHODS)) {
			GlobalDSReaderUtility globalReader = mock(GlobalDSReaderUtility.class);
			contextMock.when(() -> ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class)).thenReturn(globalReader);
			securityMock.when(() -> DataSourceSecurityUtility.hasId("10")).thenReturn(true);
			Map<String, Object> data = new HashMap<>();
			Map<String, Object> inner = new HashMap<>();
			inner.put("id", "10");
			data.put("data", inner);
			when(globalReader.addDataSourcesId(DataSourceSecurityUtility.EXECUTE, 10)).thenReturn(data);
			DataSourceSecurityUtility.isDataSourceAuthenticated(form);
			verify(globalReader).addDataSourcesId(DataSourceSecurityUtility.EXECUTE, 10);
		}
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testIsDataSourceAuthenticated_EFWDAccessible() {

	    JsonObject form = new JsonObject();
	    form.addProperty("id", "5");
	    form.addProperty("dir", "/tmp");
	    form.addProperty("access", DataSourceSecurityUtility.EXECUTE);

	    EFWDConnectionService service = mock(EFWDConnectionService.class);
	    HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
	    
	    try (MockedStatic<ApplicationContextAccessor> contextMock = Mockito.mockStatic(ApplicationContextAccessor.class);
	    	 MockedStatic<AuthenticationUtils> authMock = Mockito.mockStatic(AuthenticationUtils.class)
	    		) {
	        contextMock.when(() -> ApplicationContextAccessor.getBean(EFWDConnectionService.class)).thenReturn(service);
	        contextMock.when(() -> ApplicationContextAccessor.getBean(HIResourceServiceDB.class)).thenReturn(serviceDb);
	        when(service.isDeleted("5")).thenReturn(false);
	        when(serviceDb.getSecurityMap()).thenReturn(Map.of(5,5));
	        authMock.when(() -> AuthenticationUtils.getUserId()).thenReturn("1");
	        DataSourceSecurityUtility.isDataSourceAuthenticated(form);
	    }
	}
	
	@Test
	public void testIsEFWDAccessible_PermissionExists() throws Exception {
		EFWDConnectionService service = mock(EFWDConnectionService.class);
		try (MockedStatic<ApplicationContextAccessor> contextMock = Mockito
				.mockStatic(ApplicationContextAccessor.class);
				MockedConstruction<EfwdReaderUtility> mockedConstruction = Mockito
						.mockConstruction(EfwdReaderUtility.class, (mock, context) -> {
							doAnswer(invocation -> {
								List<ObjectNode> list = invocation.getArgument(0);

								ObjectMapper mapper = new ObjectMapper();

								ObjectNode data = mapper.createObjectNode();
								data.put("id", 5);

								ObjectNode node = mapper.createObjectNode();
								node.set("data", new POJONode(data));
								node.put("permissionLevel", DataSourceSecurityUtility.EXECUTE);

								list.add(node);

								return null;

							}).when(mock).addDataSources(anyList(), eq("all"), eq("READ"));
						})) {

			contextMock.when(() -> ApplicationContextAccessor.getBean(EFWDConnectionService.class)).thenReturn(service);

			when(service.isDeleted("5")).thenReturn(false);

			Method method = DataSourceSecurityUtility.class.getDeclaredMethod("isEFWDAccessible", String.class,
					String.class);

			method.setAccessible(true);

			method.invoke(null, "5", "READ");
		}
	}
	
	

	private JsonObject createConnectionJson(String type, String globalId, String efwdId) {
		JsonObject connection = new JsonObject();
		connection.addProperty("type", type);

		if (globalId != null) {
			connection.addProperty("globalId", globalId);
		}

		if (efwdId != null) {
			connection.addProperty("efwdId", efwdId);
		}

		JsonObject ds = new JsonObject();
		ds.add("Connection", connection);

		JsonObject root = new JsonObject();
		root.add("DataSources", ds);

		return root;
	}

}
