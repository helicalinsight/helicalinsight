package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class RecycleBinDeleteActionTest {

	@InjectMocks
	private RecycleBinDeleteAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private Deletable deletable;

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
	public void performActionDeletesDeletableRecycleBinItem() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(200);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(200L, RecycleBinType.H_USERS);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		doReturn(true).when(deletable).check(eq(200L), anyMap());
		when(recycleBinService.isRecycleBinPresent(200L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(200L)).thenReturn(bin);

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("H_USERS", "delete")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(bin), anyMap());
			assertTrue(response.contains("\"completed\":[200]"));
			assertTrue(response.contains("The selected resource have been deleted and any related content(s)."));
		}
	}

	@Test
	public void performActionMarksIncompleteWhenLinkedItemsAreNotDeleted() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(201);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		doReturn(false).when(deletable).check(eq(201L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			String response = action.performAction();

			factory.verifyNoInteractions();
			verify(recycleBinService, never()).getHIRecycleBinById(201L);
			assertTrue(response.contains("\"incomplete\":[201]"));
			assertTrue(response.contains("The resource could not be deleted, because some of the files linked to it are not in deleted state."));
		}
	}

	@Test
	public void performActionUsesPluralMessageForMultipleIncompleteItems() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(202);
		recycleBinIds.add(203);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		doReturn(false).when(deletable).check(eq(202L), anyMap());
		doReturn(true).when(deletable).check(eq(203L), anyMap());
		when(recycleBinService.isRecycleBinPresent(203L)).thenReturn(false);

		String response = action.performAction();

		assertTrue(response.contains("The delete operation was not completed successfully. Some of the items were deleted, but some of them were not"));
	}

	@Test
	public void performActionDeletesWhenForceFlagIsPresent() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(204);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		formData.addProperty("force", true);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(204L, RecycleBinType.HI_RESOURCE_DB);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		when(recycleBinService.isRecycleBinPresent(204L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(204L)).thenReturn(bin);
		doReturn(false).when(deletable).check(eq(204L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("HI_RESOURCE_DB", "delete")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(bin), anyMap());
			assertTrue(response.contains("\"completed\":[204]"));
		}
	}

	@Test
	public void performActionSkipsMissingRecycleBinItem() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(205);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		doReturn(true).when(deletable).check(eq(205L), anyMap());
		when(recycleBinService.isRecycleBinPresent(205L)).thenReturn(false);

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			String response = action.performAction();

			factory.verifyNoInteractions();
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
