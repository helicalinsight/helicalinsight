package com.helicalinsight.adhoc.recycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.enums.RecycleBinType;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class RecycleBinPurgePlannerTest {

	private RecycleBinPurgePlanner planner;

	@Mock
	private HIResourceServiceDB resourceServiceDb;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(transactionManager.getTransaction(any(TransactionDefinition.class)))
				.thenAnswer(_ -> new SimpleTransactionStatus());
		planner = new RecycleBinPurgePlanner(transactionManager);
		// inject mocks (constructor only wires TX)
		setField(planner, "resourceServiceDb", resourceServiceDb);
		setField(planner, "recycleBinService", recycleBinService);
	}

	@Test
	public void orderResourceIdsLeafFirst_ordersChildBeforeParent() {
		Set<Integer> ids = new LinkedHashSet<>(List.of(1, 2, 3));
		Map<Integer, Integer> parents = Map.of(2, 1, 3, 2);
		when(resourceServiceDb.findParentIdsByResourceIds(ids)).thenReturn(parents);

		List<Integer> ordered = new ArrayList<>(planner.orderResourceIdsLeafFirst(ids));

		assertEquals(List.of(3, 2, 1), ordered);
	}

	@Test
	public void orderResourceIdsLeafFirst_disconnectedNodes_preserveAll() {
		Set<Integer> ids = new LinkedHashSet<>(List.of(10, 20));
		Map<Integer, Integer> parents = new HashMap<>();
		parents.put(10, null);
		parents.put(20, null);
		when(resourceServiceDb.findParentIdsByResourceIds(ids)).thenReturn(parents);

		Set<Integer> ordered = planner.orderResourceIdsLeafFirst(ids);

		assertEquals(2, ordered.size());
		assertTrue(ordered.contains(10));
		assertTrue(ordered.contains(20));
	}

	@Test
	public void orderResourceIdsLeafFirst_ignoresParentOutsideSet() {
		Set<Integer> ids = new LinkedHashSet<>(List.of(5, 6));
		when(resourceServiceDb.findParentIdsByResourceIds(ids)).thenReturn(Map.of(5, 99, 6, 99));

		List<Integer> ordered = new ArrayList<>(planner.orderResourceIdsLeafFirst(ids));

		assertEquals(2, ordered.size());
		assertTrue(ordered.contains(5));
		assertTrue(ordered.contains(6));
	}

	@Test
	public void purge_hardDeletesEachResourceRootIndependently_leafFirst() {
		RecycleBinDTO parent = bin(1L, 1);
		RecycleBinDTO child = bin(2L, 2);
		PurgeEligibility eligibility = new PurgeEligibility(Set.of(1L, 2L), Set.of());
		Map<Long, Boolean> status = new HashMap<>();
		status.put(1L, false);
		status.put(2L, false);

		Map<Integer, Integer> parentMap = new HashMap<>();
		parentMap.put(1, null);
		parentMap.put(2, 1);
		when(resourceServiceDb.findParentIdsByResourceIds(argThat(c -> c != null && c.contains(1) && c.contains(2))))
				.thenReturn(parentMap);
		when(resourceServiceDb.hardDeleteResourcesByIds(any(), anyBoolean())).thenReturn(true);
		when(recycleBinService.isRecycleBinPresent(1L)).thenReturn(true);
		when(recycleBinService.isRecycleBinPresent(2L)).thenReturn(true);

		Set<Long> completed = planner.purge(List.of(parent, child), eligibility, status);

		verify(resourceServiceDb, times(1)).hardDeleteResourcesByIds(eq(List.of(2)), anyBoolean());
		verify(resourceServiceDb, times(1)).hardDeleteResourcesByIds(eq(List.of(1)), anyBoolean());
		verify(transactionManager, times(2)).commit(any(TransactionStatus.class));
		assertEquals(Set.of(1L, 2L), completed);
	}

	@Test
	public void purge_keepsSuccessfulRootsWhenLaterRootFails() {
		RecycleBinDTO first = bin(1L, 1);
		RecycleBinDTO second = bin(2L, 2);
		PurgeEligibility eligibility = new PurgeEligibility(Set.of(1L, 2L), Set.of());
		Map<Long, Boolean> status = new HashMap<>();
		status.put(1L, false);
		status.put(2L, false);

		Map<Integer, Integer> parentMap = new HashMap<>();
		parentMap.put(1, null);
		parentMap.put(2, null);
		when(resourceServiceDb.findParentIdsByResourceIds(any())).thenReturn(parentMap);
		when(recycleBinService.isRecycleBinPresent(any())).thenReturn(true);
		when(resourceServiceDb.hardDeleteResourcesByIds(eq(List.of(1)), anyBoolean())).thenReturn(true);
		when(resourceServiceDb.hardDeleteResourcesByIds(eq(List.of(2)), anyBoolean()))
				.thenThrow(new EfwServiceException("exception"));

		Set<Long> completed = planner.purge(List.of(first, second), eligibility, status);

		assertEquals(Set.of(1L), completed);
		assertEquals(Boolean.TRUE, status.get(1L));
		assertEquals(Boolean.FALSE, status.get(2L));
		verify(transactionManager, times(1)).commit(any(TransactionStatus.class));
		verify(transactionManager, times(1)).rollback(any(TransactionStatus.class));
	}

	@Test
	public void purge_skipsBlockedResources() {
		RecycleBinDTO blocked = bin(1L, 1);
		PurgeEligibility eligibility = new PurgeEligibility(Set.of(), Set.of(1L));
		Map<Long, Boolean> status = new HashMap<>();

		Set<Long> completed = planner.purge(List.of(blocked), eligibility, status);

		verify(resourceServiceDb, never()).hardDeleteResourcesByIds(any(), anyBoolean());
		verify(resourceServiceDb, never()).findParentIdsByResourceIds(any());
		assertTrue(completed.isEmpty());
	}

	@Test
	public void orderResourceIdsLeafFirst_cycleThrowsEfwServiceException() {
		Set<Integer> ids = new LinkedHashSet<>(List.of(1, 2));
		Map<Integer, Integer> parents = new HashMap<>();
		parents.put(1, 2);
		parents.put(2, 1);
		when(resourceServiceDb.findParentIdsByResourceIds(ids)).thenReturn(parents);

		try {
			planner.orderResourceIdsLeafFirst(ids);
			fail("expected EfwServiceException");
		} catch (EfwServiceException e) {
			assertTrue(e.getMessage().contains("cycle"));
		}
	}

	private static RecycleBinDTO bin(Long binId, Integer resourceId) {
		RecycleBinDTO dto = new RecycleBinDTO();
		dto.setRecycleBinId(binId);
		dto.setResourceId(resourceId);
		dto.setType(RecycleBinType.HI_RESOURCE_DB);
		return dto;
	}

	private static void setField(Object target, String name, Object value) {
		try {
			var field = RecycleBinPurgePlanner.class.getDeclaredField(name);
			field.setAccessible(true);
			field.set(target, value);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
