package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class RecycleBinRestoreActionTest {

	@InjectMocks
	private RecycleBinRestoreAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test(expected = EfwServiceException.class)
	public void performActionThrowsWhenRecycleBinIdsEmpty() {
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", new JsonArray());
		action.setFormData(formData);
		action.performAction();
	}

	@Test
	public void performActionRestoresRecycleBinItemSuccessfully() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(300);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(300L, RecycleBinType.HI_RESOURCE_DB);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		when(recycleBinService.isRecycleBinPresent(300L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(300L)).thenReturn(bin);
		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			Map<Long, Boolean> deleteMap = invocation.getArgument(1);
			deleteMap.put(300L, true);
			return true;
		}).when(handler).handle(eq(bin), any());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("HI_RESOURCE_DB", "restore")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(bin), any());
			assertTrue(response.contains("\"completed\":[300]"));
			assertTrue(response.contains("Resource(s) restored successfully."));
		}
	}

	@Test
	public void performActionReportsIncompleteWhenRestoreFails() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(301);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(301L, RecycleBinType.HI_RESOURCE_DB);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		when(recycleBinService.isRecycleBinPresent(301L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(301L)).thenReturn(bin);
		doAnswer(invocation -> {
			@SuppressWarnings("unchecked")
			Map<Long, Boolean> deleteMap = invocation.getArgument(1);
			deleteMap.put(301L, false);
			return false;
		}).when(handler).handle(eq(bin), any());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("HI_RESOURCE_DB", "restore")).thenReturn(handler);

			String response = action.performAction();

			assertTrue(response.contains("\"incomplete\":[301]"));
			assertTrue(response.contains("You can't restore this resource because its parent is in the recycle bin. To restore it, you need to restore the parent first."));
		}
	}

	@Test
	public void performActionSkipsMissingRecycleBinItem() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(302);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		when(recycleBinService.isRecycleBinPresent(302L)).thenReturn(false);

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			String response = action.performAction();

			factory.verifyNoInteractions();
			assertTrue(response.contains("\"completed\":[]"));
			assertTrue(response.contains("Resource(s) restored successfully."));
		}
	}

	@Test
	public void performActionSkipsWhenRecycleBinDtoIsNull() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(303);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		when(recycleBinService.isRecycleBinPresent(303L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(303L)).thenReturn(null);

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			String response = action.performAction();

			factory.verifyNoInteractions();
			verify(recycleBinService).getHIRecycleBinById(303L);
			assertTrue(response.contains("\"completed\":[]"));
		}
	}

	private static RecycleBinDTO recycleBinDto(Long id, RecycleBinType type) {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(id);
		bin.setType(type);
		return bin;
	}
}
