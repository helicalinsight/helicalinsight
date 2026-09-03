package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.recycle.PurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgeEligibility;
import com.helicalinsight.adhoc.recycle.RecycleBinPurgePlanner;
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
	private RecycleBinPurgeEligibility purgeEligibility;

	@Mock
	private RecycleBinPurgePlanner purgePlanner;

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
	public void performActionDeletesEligibleRecycleBinItem() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(200);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(200L, RecycleBinType.H_USERS);
		when(recycleBinService.isRecycleBinPresent(200L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(200L)).thenReturn(bin);
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(200L), Set.of()));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(new LinkedHashSet<>(List.of(200L)));

		String response = action.performAction();

		verify(purgePlanner).purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false));
		assertTrue(response.contains("\"completed\":[200]"));
		assertTrue(response.contains("The selected resource have been deleted and any related content(s)."));
	}

	@Test
	public void performActionMarksIncompleteWhenBlockedByEligibility() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(201);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(201L, RecycleBinType.HI_RESOURCE_DB);
		when(recycleBinService.isRecycleBinPresent(201L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(201L)).thenReturn(bin);
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(), Set.of(201L)));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(Set.of());

		String response = action.performAction();

		verify(purgePlanner).purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false));
		assertTrue(response.contains("\"incomplete\":[201]"));
		assertTrue(response.contains(
				"The resource could not be deleted, because some of the files linked to it are not in deleted state."));
	}

	@Test
	public void performActionUsesPluralMessageForMultipleIncompleteItems() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(202);
		recycleBinIds.add(203);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO blocked = recycleBinDto(202L, RecycleBinType.HI_RESOURCE_DB);
		RecycleBinDTO eligible = recycleBinDto(203L, RecycleBinType.HI_RESOURCE_DB);
		when(recycleBinService.isRecycleBinPresent(202L)).thenReturn(true);
		when(recycleBinService.isRecycleBinPresent(203L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(202L)).thenReturn(blocked);
		when(recycleBinService.getHIRecycleBinById(203L)).thenReturn(eligible);
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(203L), Set.of(202L)));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(new LinkedHashSet<>(List.of(203L)));

		String response = action.performAction();

		assertTrue(response.contains(
				"The delete operation was not completed successfully. Some of the items were deleted, but some of them were not"));
		assertTrue(response.contains("\"completed\":[203]"));
		assertTrue(response.contains("\"incomplete\":[202]"));
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
		when(recycleBinService.isRecycleBinPresent(204L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(204L)).thenReturn(bin);
		when(purgeEligibility.evaluate(anyList(), eq(true)))
				.thenReturn(new PurgeEligibility(Set.of(204L), Set.of()));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(true)))
				.thenReturn(new LinkedHashSet<>(List.of(204L)));

		String response = action.performAction();

		verify(purgeEligibility).evaluate(anyList(), eq(true));
		verify(purgePlanner).purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(true));
		assertTrue(response.contains("\"completed\":[204]"));
	}

	@Test(expected = EfwServiceException.class)
	public void performActionThrowsWhenAllRequestedItemsAreMissing() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(205);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		when(recycleBinService.isRecycleBinPresent(205L)).thenReturn(false);

		action.performAction();
	}

	@Test
	public void performActionMarksIncompleteWhenEligiblePurgeFails() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(206);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinDTO bin = recycleBinDto(206L, RecycleBinType.HI_RESOURCE_DB);
		when(recycleBinService.isRecycleBinPresent(206L)).thenReturn(true);
		when(recycleBinService.getHIRecycleBinById(206L)).thenReturn(bin);
		when(purgeEligibility.evaluate(anyList(), eq(false)))
				.thenReturn(new PurgeEligibility(Set.of(206L), Set.of()));
		when(purgePlanner.purge(anyList(), any(PurgeEligibility.class), anyMap(), eq(false)))
				.thenReturn(Set.of());

		String response = action.performAction();

		assertTrue(response.contains("\"incomplete\":[206]"));
		assertTrue(response.contains(
				"The resource could not be deleted, because some of the files linked to it are not in deleted state."));
	}

	private static RecycleBinDTO recycleBinDto(Long id, RecycleBinType type) {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(id);
		bin.setType(type);
		return bin;
	}
}
