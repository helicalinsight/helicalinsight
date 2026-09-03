package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.PurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgePlanner;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIRecycleBinService;

public class RecycleBinClearActionTest {

	@InjectMocks
	private RecycleBinClearAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private RecycleBinPurgeEligibility purgeEligibility;

	@Mock
	private RecycleBinPurgePlanner purgePlanner;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		action.setFormData(new JsonObject());
	}

//	@Test
	public void performActionReturnsEmptyMessageWhenRecycleBinHasNoItems() {
		when(recycleBinService.getAll()).thenReturn(Collections.emptyList());
		String response = action.performAction();
		System.out.println(response);
		assertTrue(response.contains("RecycleBin is Empty!"));
	}

	@Test
	public void performActionClearsDeletableItems() {
		RecycleBinDTO item = recycleBinDto(400L, RecycleBinType.H_USERS);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(400L), Set.of()));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(new LinkedHashSet<>(List.of(400L)));

		String response = action.performAction();

		verify(purgePlanner).purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false));
		assertTrue(response.contains("\"completed\":[400]"));
		assertTrue(response.contains("Resource(s) deleted successfully."));
	}

	@Test
	public void performActionMarksIncompleteItemsWhenLinkedResourcesAreActive() {
		RecycleBinDTO item = recycleBinDto(401L, RecycleBinType.HI_RESOURCE_DB);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(), Set.of(401L)));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(Set.of());

		String response = action.performAction();

		assertTrue(response.contains("\"incomplete\":[401]"));
		assertTrue(response.contains(
				"The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually."));
	}

	@Test
	public void performActionClearsItemsWhenForceFlagIsPresent() {
		RecycleBinDTO item = recycleBinDto(402L, RecycleBinType.ORGANIZATION);
		JsonObject formData = new JsonObject();
		formData.addProperty("force", true);
		action.setFormData(formData);

		when(recycleBinService.getAll()).thenReturn(List.of(item));
		when(purgeEligibility.evaluate(anyList(), eq(true)))
				.thenReturn(new PurgeEligibility(Set.of(402L), Set.of()));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(true)))
				.thenReturn(new LinkedHashSet<>(List.of(402L)));

		String response = action.performAction();

		verify(purgeEligibility).evaluate(anyList(), eq(true));
		verify(purgePlanner).purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(true));
		assertTrue(response.contains("\"completed\":[402]"));
	}

	@Test
	public void performActionClearsMultipleItemsWithMixedResults() {
		RecycleBinDTO deletableItem = recycleBinDto(403L, RecycleBinType.H_USERS);
		RecycleBinDTO blockedItem = recycleBinDto(404L, RecycleBinType.HI_EFWD_CONNECTION);

		when(recycleBinService.getAll()).thenReturn(List.of(deletableItem, blockedItem));
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(403L), Set.of(404L)));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(new LinkedHashSet<>(List.of(403L)));

		String response = action.performAction();

		assertTrue(response.contains("\"completed\":[403]"));
		assertTrue(response.contains("\"incomplete\":[404]"));
		assertTrue(response.contains(
				"The clear operation was not completed, because some of the files linked to it are not in deleted state, Please delete them manually."));
	}

	private static RecycleBinDTO recycleBinDto(Long id, RecycleBinType type) {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(id);
		bin.setType(type);
		return bin;
	}
}
