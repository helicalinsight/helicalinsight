package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.factory.RecycleBinHandlerFactory;
import com.helicalinsight.adhoc.recycle.handler.RecycleBinHandler;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIRecycleBinService;

public class RecycleBinClearActionTest {

	@InjectMocks
	private RecycleBinClearAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private Deletable deletable;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		action.setFormData(new JsonObject());
	}

	@Test
	public void performActionReturnsEmptyMessageWhenRecycleBinHasNoItems() {
		when(recycleBinService.getAll()).thenReturn(Collections.emptyList());

		String response = action.performAction();

		assertTrue(response.contains("RecycleBin is Emtpy!"));
	}

	@Test
	public void performActionClearsDeletableItems() {
		RecycleBinDTO item = recycleBinDto(400L, RecycleBinType.H_USERS);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		doReturn(true).when(deletable).check(eq(400L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("H_USERS", "delete")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(item), any());
			assertTrue(response.contains("\"completed\":[400]"));
			assertTrue(response.contains("Resource(s) deleted successfully."));
		}
	}

	@Test
	public void performActionMarksIncompleteItemsWhenLinkedResourcesAreActive() {
		RecycleBinDTO item = recycleBinDto(401L, RecycleBinType.HI_RESOURCE_DB);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		doReturn(false).when(deletable).check(eq(401L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			String response = action.performAction();

			factory.verifyNoInteractions();
			assertTrue(response.contains("\"incomplete\":[401]"));
			assertTrue(response.contains("The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually."));
		}
	}

	@Test
	public void performActionClearsItemsWhenForceFlagIsPresent() {
		RecycleBinDTO item = recycleBinDto(402L, RecycleBinType.ORGANIZATION);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);
		JsonObject formData = new JsonObject();
		formData.addProperty("force", true);
		action.setFormData(formData);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		doReturn(false).when(deletable).check(eq(402L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("ORGANIZATION", "delete")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(item), any());
			assertTrue(response.contains("\"completed\":[402]"));
		}
	}

	@Test
	public void performActionClearsMultipleItemsWithMixedResults() {
		RecycleBinDTO deletableItem = recycleBinDto(403L, RecycleBinType.H_USERS);
		RecycleBinDTO blockedItem = recycleBinDto(404L, RecycleBinType.HI_EFWD_CONNECTION);
		RecycleBinHandler handler = mock(RecycleBinHandler.class);

		when(recycleBinService.getAll()).thenReturn(List.of(deletableItem, blockedItem));
		doReturn(true).when(deletable).check(eq(403L), anyMap());
		doReturn(false).when(deletable).check(eq(404L), anyMap());

		try (MockedStatic<RecycleBinHandlerFactory> factory = mockStatic(RecycleBinHandlerFactory.class)) {
			factory.when(() -> RecycleBinHandlerFactory.getHandler("H_USERS", "delete")).thenReturn(handler);

			String response = action.performAction();

			verify(handler).handle(eq(deletableItem), any());
			assertTrue(response.contains("\"completed\":[403]"));
			assertTrue(response.contains("\"incomplete\":[404]"));
		}
	}

	private static RecycleBinDTO recycleBinDto(Long id, RecycleBinType type) {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(id);
		bin.setType(type);
		return bin;
	}
}
