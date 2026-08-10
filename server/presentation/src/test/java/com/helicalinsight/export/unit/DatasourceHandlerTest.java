package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceHandlerTest extends ExportUnitTestBase {

	private static class TestDatasourceHandler extends DatasourceHandler {
		
		@Override
		public void write(HIResourceDTO resource, String dir, Manifest manifest) {
		
		}
		
		@Override
		public void importResource(HIResource resource, String fileName, String onConflict) {
		
		}
		
		User callResolveUser(Object createdByObj) {
			return resolveUser(createdByObj);
		}
	}
	
	private TestDatasourceHandler createHandler(UserService userService, ResourceShareUtils shareUtils,
			ResourceDTOMapper dtoMapper) throws Exception {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		setField(handler, "userService", userService);
		setField(handler, "shareUtils", shareUtils);
		setField(handler, "dtoMapper", dtoMapper);
		return handler;
	}


	@Test
	public void ut_a1_testImportResourceHCRDefaultReturnsNull() {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		List<String> mappings = handler.importResourceHCR("datasource.json", "update");
		Assert.assertNull(mappings);
	}

	@Test
	public void ut_a2_testImportResourceHCRDefaultWithSkipConflict() {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		List<String> mappings = handler.importResourceHCR("efwd_datasource.json", "skip");
		Assert.assertNull(mappings);
	}
	
	@Test
	public void ut_b1_testResolveUserWithUsernameUsesShareUtils() throws Exception {
		UserService userService = mock(UserService.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		TestDatasourceHandler handler = createHandler(userService, shareUtils, dtoMapper);
		User input = new User();
		input.setUsername("exporter");
		UserDTO dto = mock(UserDTO.class);
		User inserted = new User();
		inserted.setId(10);
		inserted.setUsername("exporter");
		when(dtoMapper.map(input)).thenReturn(dto);
		when(shareUtils.getOrInsertUser(dto)).thenReturn(inserted);
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			User result = handler.callResolveUser(input);
			Assert.assertSame(inserted, result);
			verify(shareUtils).getOrInsertUser(dto);
			verify(userService, never()).findUser(anyInt());
			jsonMock.verifyNoInteractions();
		}
	}
	
	@Test
	public void ut_b2_testResolveUserWithNullUsernameFallsBackToDefaultOwner() throws Exception {
		UserService userService = mock(UserService.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		TestDatasourceHandler handler = createHandler(userService, shareUtils, dtoMapper);
		User input = new User(); 
		User defaultOwner = new User();
		defaultOwner.setId(1);
		defaultOwner.setUsername("hiadmin");
		JsonObject settings = new JsonObject();
		settings.addProperty("defaultOwnerId", "1");
		when(userService.findUser(1)).thenReturn(defaultOwner);
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			jsonMock.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
			User result = handler.callResolveUser(input);
			Assert.assertSame(defaultOwner, result);
			verify(shareUtils, never()).getOrInsertUser(any());
		}
	}
	
	@Test
	public void ut_b3_testResolveUserWithNonUserFallsBackToDefaultOwner() throws Exception {
		UserService userService = mock(UserService.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		TestDatasourceHandler handler = createHandler(userService, shareUtils, dtoMapper);
		User defaultOwner = new User();
		defaultOwner.setId(5);
		JsonObject settings = new JsonObject();
		settings.addProperty("defaultOwnerId", "5");
		when(userService.findUser(5)).thenReturn(defaultOwner);
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			jsonMock.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
			Assert.assertSame(defaultOwner, handler.callResolveUser(null));
			Assert.assertSame(defaultOwner, handler.callResolveUser("not-a-user"));
		}
	}
	
	@Test
	public void ut_b4_testResolveUserReturnsNullWhenDefaultOwnerIdBlank() throws Exception {
		UserService userService = mock(UserService.class);
		TestDatasourceHandler handler = createHandler(userService, mock(ResourceShareUtils.class),
				mock(ResourceDTOMapper.class));
		JsonObject settings = new JsonObject();
		settings.addProperty("defaultOwnerId", "");
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			jsonMock.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
			Assert.assertNull(handler.callResolveUser(null));
			verify(userService, never()).findUser(anyInt());
		}
	}
	
	@Test
	public void ut_b5_testResolveUserReturnsNullWhenDefaultOwnerIdIsLiteralNull() throws Exception {
		UserService userService = mock(UserService.class);
		TestDatasourceHandler handler = createHandler(userService, mock(ResourceShareUtils.class),
				mock(ResourceDTOMapper.class));
		JsonObject settings = new JsonObject();
		settings.addProperty("defaultOwnerId", "null");
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			jsonMock.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
			Assert.assertNull(handler.callResolveUser(null));
			verify(userService, never()).findUser(anyInt());
		}
	}
	
	@Test
	public void ut_b6_testResolveUserReturnsNullWhenDefaultOwnerNotFound() throws Exception {
		UserService userService = mock(UserService.class);
		TestDatasourceHandler handler = createHandler(userService, mock(ResourceShareUtils.class),
				mock(ResourceDTOMapper.class));
		JsonObject settings = new JsonObject();
		settings.addProperty("defaultOwnerId", "99");
		when(userService.findUser(99)).thenReturn(null);
		try (MockedStatic<JsonUtils> jsonMock = mockStatic(JsonUtils.class)) {
			jsonMock.when(JsonUtils::newGetSettingsJson).thenReturn(settings);
			Assert.assertNull(handler.callResolveUser(null));
		}
	}
}
