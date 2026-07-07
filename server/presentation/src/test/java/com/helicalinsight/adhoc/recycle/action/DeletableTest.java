package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.core.request.RecycleBinDatasource;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.core.request.RecycleBinUser;

public class DeletableTest {

	private static final String COMPLETED = "completed";

	@InjectMocks
	private Deletable deletable;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private HIResourceServiceDB resourceServiceDb;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void checkReturnsTrueWhenRecycleBinIsMissing() {
		when(recycleBinService.isRecycleBinPresent(1L)).thenReturn(false);

		assertTrue(deletable.check(1L, completedMap()));
	}

	@Test
	public void checkReturnsTrueWhenAssociatedItemsAreEmpty() {
		when(recycleBinService.isRecycleBinPresent(2L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(2L)).thenReturn(Map.of());

		assertTrue(deletable.check(2L, completedMap()));
	}

	@Test
	public void checkReturnsFalseWhenUserIsNotDeleted() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();
		when(recycleBinService.isRecycleBinPresent(3L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(3L)).thenReturn(
				Map.of("users", List.of(new RecycleBinUser("user", 10, false))));

		assertFalse(deletable.check(3L, recycleBinIdMap));
	}

	@Test
	public void checkMarksCompletedWhenDeletedUserHasRecycleBinEntry() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();
		HIRecycleBin userBin = new HIRecycleBin();
		userBin.setId(31L);

		when(recycleBinService.isRecycleBinPresent(4L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(4L)).thenReturn(
				Map.of("users", List.of(new RecycleBinUser("user", 11, true))));
		when(recycleBinService.findHIRecycleBinByUserId(11)).thenReturn(userBin);

		assertTrue(deletable.check(4L, recycleBinIdMap));

		assertEquals(List.of(31L), recycleBinIdMap.get(COMPLETED));
	}

	@Test
	public void checkReturnsFalseWhenUnfilteredResourceIsNotDeleted() {
		HIResource activeResource = new HIResource(20, null, false);

		when(recycleBinService.isRecycleBinPresent(5L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(5L)).thenReturn(Map.of("resources",
				List.of(resourceItem("file", true, 20)), "unfiltered", List.of(activeResource)));

		assertFalse(deletable.check(5L, completedMap()));
	}

	@Test
	public void checkReturnsFalseWhenResourceItemIsNotDeleted() {
		when(recycleBinService.isRecycleBinPresent(6L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(6L)).thenReturn(Map.of("resources",
				List.of(resourceItem("file", false, 21))));

		assertFalse(deletable.check(6L, completedMap()));
	}

	@Test
	public void checkQueuesLinkedResourceRecycleBinEntries() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();

		when(recycleBinService.isRecycleBinPresent(7L)).thenReturn(true);
		when(recycleBinService.isRecycleBinPresent(71L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(7L)).thenReturn(Map.of("resources",
				List.of(resourceItem("file", true, 22))));
		when(recycleBinService.findAllResourceOfRecycleBinItem(71L)).thenReturn(Map.of());
		when(resourceServiceDb.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(22)).thenReturn(71L);

		assertTrue(deletable.check(7L, recycleBinIdMap));

		assertEquals(List.of(71L), recycleBinIdMap.get(COMPLETED));
	}

	@Test
	public void checkSkipsAlreadyVisitedRecycleBinIds() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();

		when(recycleBinService.isRecycleBinPresent(8L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(8L)).thenReturn(Map.of("resources",
				List.of(resourceItem("first", true, 23), resourceItem("second", true, 24))));
		when(resourceServiceDb.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(23)).thenReturn(8L);
		when(resourceServiceDb.getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(24)).thenReturn(null);

		assertTrue(deletable.check(8L, recycleBinIdMap));
	}

	@Test
	public void checkMarksCompletedForEfwdDatasource() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();
		HIRecycleBin efwdBin = new HIRecycleBin();
		efwdBin.setId(91L);

		when(recycleBinService.isRecycleBinPresent(9L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(9L)).thenReturn(Map.of("dataSources",
				List.of(new RecycleBinDatasource("efwd", "jdbc", true, 30, "/efwd/path"))));
		when(recycleBinService.findHIRecycleBinByEFWDId(30)).thenReturn(efwdBin);

		assertTrue(deletable.check(9L, recycleBinIdMap));

		assertEquals(List.of(91L), recycleBinIdMap.get(COMPLETED));
	}

	@Test
	public void checkMarksCompletedForGlobalDatasource() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();
		HIRecycleBin globalBin = new HIRecycleBin();
		globalBin.setId(92L);

		when(recycleBinService.isRecycleBinPresent(10L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(10L)).thenReturn(Map.of("dataSources",
				List.of(new RecycleBinDatasource("global", "flatfile", true, 40, ""))));
		when(recycleBinService.findHIRecycleBinByGlobalId(40)).thenReturn(globalBin);

		assertTrue(deletable.check(10L, recycleBinIdMap));

		assertEquals(List.of(92L), recycleBinIdMap.get(COMPLETED));
	}

	@Test
	public void checkReturnsFalseWhenDatasourceIsNotDeleted() {
		when(recycleBinService.isRecycleBinPresent(11L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(11L)).thenReturn(Map.of("dataSources",
				List.of(new RecycleBinDatasource("global", "flatfile", false, 41, ""))));

		assertFalse(deletable.check(11L, completedMap()));
	}

	@Test
	public void checkDoesNotDuplicateCompletedEntries() {
		Map<String, List<Long>> recycleBinIdMap = completedMap();
		recycleBinIdMap.get(COMPLETED).add(101L);
		HIRecycleBin userBin = new HIRecycleBin();
		userBin.setId(101L);

		when(recycleBinService.isRecycleBinPresent(12L)).thenReturn(true);
		when(recycleBinService.findAllResourceOfRecycleBinItem(12L)).thenReturn(
				Map.of("users", List.of(new RecycleBinUser("user", 12, true))));
		when(recycleBinService.findHIRecycleBinByUserId(12)).thenReturn(userBin);

		assertTrue(deletable.check(12L, recycleBinIdMap));

		assertEquals(1, recycleBinIdMap.get(COMPLETED).size());
	}

	private static Map<String, List<Long>> completedMap() {
		Map<String, List<Long>> recycleBinIdMap = new HashMap<>();
		recycleBinIdMap.put(COMPLETED, new ArrayList<>());
		return recycleBinIdMap;
	}

	private static RecycleBinResourceItem resourceItem(String name, boolean deleted, int resourceId) {
		RecycleBinResourceItem item = new RecycleBinResourceItem(name, deleted);
		item.setResourceId(resourceId);
		return item;
	}
}
