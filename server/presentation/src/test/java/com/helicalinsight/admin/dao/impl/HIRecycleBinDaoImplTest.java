package com.helicalinsight.admin.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.dao.HIResourceDBDAO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHUsers;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;

@RunWith(MockitoJUnitRunner.Silent.class)
public class HIRecycleBinDaoImplTest {

	@InjectMocks
	private HIRecycleBinDaoImpl dao;

	@Mock
	private SessionFactory sessionFactory;

	@Mock
	private Session session;

	@Mock
	private MutationQuery mutationQuery;

	@Mock
	private SelectionQuery<HIRecycleBin> hiRecycleBinSelectionQuery;

	@Mock
	private SelectionQuery<Long> countSelectionQuery;

	@Mock
	private Query query;

	@Mock
	private HIResourceDBDAO hiResourceDao;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void saveReturnsTrueWhenPersistSucceeds() {
		HIRecycleBin bin = new HIRecycleBin();

		assertTrue(dao.save(bin));

		verify(session).persist(bin);
	}

	@Test
	public void saveReturnsFalseWhenPersistFails() {
		HIRecycleBin bin = new HIRecycleBin();
		doThrow(new RuntimeException("persist failed")).when(session).persist(bin);

		assertFalse(dao.save(bin));
	}

	@Test
	public void deleteByIdRemovesLinksAndHeader() {
		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(1);

		assertTrue(dao.delete(1L));

		verify(session, times(6)).createMutationQuery(anyString());
		verify(mutationQuery, times(6)).executeUpdate();
	}

	@Test
	public void deleteByIdReturnsFalseWhenMutationFails() {
		when(session.createMutationQuery(anyString())).thenThrow(new RuntimeException("delete failed"));

		assertFalse(dao.delete(2L));
	}

	@Test
	public void deleteBinRemovesAssociationsAndRecycleBinEntry() {
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(3L);
		bin.setHiRecycleBinHUsers(new HIRecycleBinHUsers());

		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(1);

		assertTrue(dao.delete(bin));

		// 5 link tables + recycle-bin header
		verify(session, times(6)).createMutationQuery(anyString());
		verify(mutationQuery, times(6)).executeUpdate();
	}

	@Test
	public void deleteBinReturnsFalseWhenMutationFails() {
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(4L);
		when(session.createMutationQuery(anyString())).thenThrow(new RuntimeException("delete failed"));

		assertFalse(dao.delete(bin));
	}

	@Test
	public void findHIRecycleBinsByResourceIdsReturnsEmptyMapForNullOrEmptyInput() {
		assertTrue(dao.findHIRecycleBinsByResourceIds(null).isEmpty());
		assertTrue(dao.findHIRecycleBinsByResourceIds(Collections.emptyList()).isEmpty());
		verify(session, never()).createQuery(anyString());
	}

	@Test
	public void deleteHIRecycleBinByResourceIdReturnsTrueWhenBinMissing() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		doReturn(null).when(daoSpy).findHIRecycleBinByResourceId(10);

		assertTrue(daoSpy.deleteHIRecycleBinByResourceId(10));

		verify(daoSpy, never()).delete(anyLong());
	}

	@Test
	public void deleteHIRecycleBinByResourceIdDeletesWhenBinExists() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(11L);
		doReturn(bin).when(daoSpy).findHIRecycleBinByResourceId(10);
		doReturn(true).when(daoSpy).delete(11L);

		assertTrue(daoSpy.deleteHIRecycleBinByResourceId(10));

		verify(daoSpy).delete(11L);
	}

	@Test
	public void findHIRecycleBinByIdThrowsWhenBinMissing() {
		when(session.createSelectionQuery(anyString(), eq(HIRecycleBin.class))).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.setParameter(anyString(), any())).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.uniqueResult()).thenReturn(null);

		try {
			dao.findHIRecycleBinById(20L);
		} catch (ResourceNotFoundException ex) {
			assertNotNull(ex.getMessage());
			return;
		}
		throw new AssertionError("Expected ResourceNotFoundException");
	}

	@Test
	public void findHIRecycleBinByIdPlainReturnsBin() {
		HIRecycleBin bin = new HIRecycleBin();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), any())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(bin);

		assertSameBin(bin, dao.findHIRecycleBinByIdPlain(21L));
	}

	@Test
	public void isRecycleBinPresentReturnsTrueWhenCountIsPositive() {
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(countSelectionQuery);
		when(countSelectionQuery.setParameter(anyString(), any())).thenReturn(countSelectionQuery);
		when(countSelectionQuery.uniqueResult()).thenReturn(2L);

		assertTrue(dao.isRecycleBinPresent(30L));
	}

	@Test
	public void isRecycleBinPresentReturnsFalseWhenCountIsZero() {
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(countSelectionQuery);
		when(countSelectionQuery.setParameter(anyString(), any())).thenReturn(countSelectionQuery);
		when(countSelectionQuery.uniqueResult()).thenReturn(0L);

		assertFalse(dao.isRecycleBinPresent(31L));
	}

	@Test
	public void findHIRecycleBinByGlobalIdReturnsEmptyOptionalWhenNotFound() {
		when(session.createSelectionQuery(anyString(), eq(HIRecycleBin.class))).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.setParameter(anyString(), any())).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.uniqueResult()).thenReturn(null);

		Optional<HIRecycleBin> result = dao.findHIRecycleBinByGlobalId(40);

		assertFalse(result.isPresent());
	}

	@Test
	public void findHIRecycleBinByEFWDIdReturnsPresentOptionalWhenFound() {
		HIRecycleBin bin = new HIRecycleBin();
		when(session.createSelectionQuery(anyString(), eq(HIRecycleBin.class))).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.setParameter(anyString(), any())).thenReturn(hiRecycleBinSelectionQuery);
		when(hiRecycleBinSelectionQuery.uniqueResult()).thenReturn(bin);

		Optional<HIRecycleBin> result = dao.findHIRecycleBinByEFWDId(41);

		assertTrue(result.isPresent());
		assertEquals(bin, result.get());
	}

	@Test
	public void deleteHIRecycleByEfwdIdDeletesWhenBinExists() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Long> binIdQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(binIdQuery);
		when(binIdQuery.setParameter(anyString(), any())).thenReturn(binIdQuery);
		when(binIdQuery.getResultList()).thenReturn(List.of(50L));
		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(1);

		dao.deleteHIRecycleByEfwdId(42);

		verify(session, times(6)).createMutationQuery(anyString());
		verify(mutationQuery, times(6)).executeUpdate();
	}

	@Test
	public void deleteHIRecycleByEfwdIdSkipsWhenNoBinLinked() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Long> binIdQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(binIdQuery);
		when(binIdQuery.setParameter(anyString(), any())).thenReturn(binIdQuery);
		when(binIdQuery.getResultList()).thenReturn(Collections.emptyList());

		dao.deleteHIRecycleByEfwdId(42);

		verify(session, never()).createMutationQuery(anyString());
	}

	@Test
	public void deleteRecycleBinByGlobalIdSkipsWhenBinMissing() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		doReturn(Optional.empty()).when(daoSpy).findHIRecycleBinByGlobalId(43);

		daoSpy.deleteRecycleBinByGlobalId(43);

		verify(daoSpy, never()).delete(any(HIRecycleBin.class));
	}

	@Test
	public void deleteRecycleBinsByIdsSkipsWhenListIsEmpty() {
		dao.deleteRecycleBinsByIds(null);
		dao.deleteRecycleBinsByIds(Collections.emptyList());

		verify(session, never()).createMutationQuery(anyString());
	}

	@Test
	public void deleteRecycleBinsByIdsExecutesBulkDeleteQueries() {
		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(2);

		dao.deleteRecycleBinsByIds(List.of(60L, 61L));

		verify(session, times(6)).createMutationQuery(anyString());
		verify(mutationQuery, times(6)).executeUpdate();
	}

	@Test
	public void deleteRecycleBinsByResourceIdsSkipsWhenNullOrEmpty() {
		dao.deleteRecycleBinsByResourceIds(null);
		dao.deleteRecycleBinsByResourceIds(Collections.emptyList());

		verify(session, never()).createSelectionQuery(anyString(), eq(Long.class));
		verify(session, never()).createMutationQuery(anyString());
	}

	@Test
	public void deleteRecycleBinsByResourceIdsSkipsMutationsWhenNoBinsFound() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Long> binIdQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(binIdQuery);
		when(binIdQuery.setParameterList(anyString(), anyCollection())).thenReturn(binIdQuery);
		when(binIdQuery.getResultList()).thenReturn(Collections.emptyList());

		dao.deleteRecycleBinsByResourceIds(List.of(10, 11));

		verify(session, never()).createMutationQuery(anyString());
	}

	@Test
	public void deleteRecycleBinsByResourceIdsDeletesLinksThenHeaders() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Long> binIdQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(binIdQuery);
		when(binIdQuery.setParameterList(anyString(), anyCollection())).thenReturn(binIdQuery);
		when(binIdQuery.getResultList()).thenReturn(List.of(70L, 71L));
		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(2);

		dao.deleteRecycleBinsByResourceIds(List.of(10, 11));

		verify(session, times(2)).createMutationQuery(anyString());
		verify(mutationQuery, times(2)).executeUpdate();
		verify(session).flush();
	}

	@Test
	public void deleteRecycleBinsByUserIdsSkipsWhenNullOrEmpty() {
		dao.deleteRecycleBinsByUserIds(null);
		dao.deleteRecycleBinsByUserIds(Collections.emptyList());

		verify(session, never()).createSelectionQuery(anyString(), eq(Long.class));
		verify(session, never()).createMutationQuery(anyString());
	}

	@Test
	public void deleteRecycleBinsByUserIdsDeletesViaLinksAndHeader() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Long> binIdQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		when(session.createSelectionQuery(anyString(), eq(Long.class))).thenReturn(binIdQuery);
		when(binIdQuery.setParameterList(anyString(), anyCollection())).thenReturn(binIdQuery);
		when(binIdQuery.getResultList()).thenReturn(List.of(80L));
		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameterList(anyString(), anyCollection())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(1);

		dao.deleteRecycleBinsByUserIds(List.of(5));

		verify(session, times(6)).createMutationQuery(anyString());
		verify(mutationQuery, times(6)).executeUpdate();
	}

	@Test
	public void findResourceBinsBlockedByLiveDependentsReturnsEmptyForNullOrEmpty() {
		assertTrue(dao.findResourceBinsBlockedByLiveDependents(null).isEmpty());
		assertTrue(dao.findResourceBinsBlockedByLiveDependents(Collections.emptySet()).isEmpty());
		verify(session, never()).createSelectionQuery(anyString(), eq(Object[].class));
	}

	@Test
	public void findResourceBinsBlockedByLiveDependentsBlocksWhenLiveInstantReportReferencesModel() {
		@SuppressWarnings("unchecked")
		SelectionQuery<Object[]> objectArrayQuery = org.mockito.Mockito.mock(SelectionQuery.class);
		@SuppressWarnings("unchecked")
		SelectionQuery<Integer> integerQuery = org.mockito.Mockito.mock(SelectionQuery.class);

		when(session.createSelectionQuery(nullable(String.class), eq(Object[].class))).thenReturn(objectArrayQuery);
		when(session.createSelectionQuery(nullable(String.class), eq(Integer.class))).thenReturn(integerQuery);

		when(objectArrayQuery.setParameterList(anyString(), anyCollection())).thenReturn(objectArrayQuery);
		when(integerQuery.setParameterList(anyString(), anyCollection())).thenReturn(integerQuery);

		when(objectArrayQuery.getResultList()).thenReturn(Collections.singletonList(new Object[] { 1L, 100 }),
				Collections.emptyList());
		when(integerQuery.getResultList()).thenReturn(Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), List.of(100), Collections.emptyList());

		when(hiResourceDao.getChildrenResourceByParentIds(anyList())).thenReturn(Collections.emptyList());
		when(hiResourceDao.findParentIdsByResourceIds(anyCollection())).thenReturn(Collections.emptyMap());

		Set<Long> blocked = dao.findResourceBinsBlockedByLiveDependents(Set.of(1L));

		assertEquals(Set.of(1L), blocked);
	}

	@Test
	public void findGlobalBinsBlockedByLiveDependentsReturnsEmptyForNullOrEmpty() {
		assertTrue(dao.findGlobalBinsBlockedByLiveDependents(null).isEmpty());
		assertTrue(dao.findGlobalBinsBlockedByLiveDependents(Set.of()).isEmpty());
	}

	@Test
	public void findEfwdBinsBlockedByLiveDependentsReturnsEmptyForNullOrEmpty() {
		assertTrue(dao.findEfwdBinsBlockedByLiveDependents(null).isEmpty());
		assertTrue(dao.findEfwdBinsBlockedByLiveDependents(Set.of()).isEmpty());
	}

	@Test
	public void findUserBinsBlockedByLiveDependentsReturnsEmptyForNullOrEmpty() {
		assertTrue(dao.findUserBinsBlockedByLiveDependents(null).isEmpty());
		assertTrue(dao.findUserBinsBlockedByLiveDependents(Set.of()).isEmpty());
	}

	@Test
	public void findOrgBinsBlockedByLiveDependentsReturnsEmptyForNullOrEmpty() {
		assertTrue(dao.findOrgBinsBlockedByLiveDependents(null).isEmpty());
		assertTrue(dao.findOrgBinsBlockedByLiveDependents(Set.of()).isEmpty());
	}

	@Test
	public void getAllRecycleBinDTOsReturnsEmptyListWhenNoItemsExist() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		doReturn(Collections.emptyList()).when(daoSpy).getAllRecycleBinItems();

		assertTrue(daoSpy.getAllRecycleBinDTOs().isEmpty());
	}

	@Test
	public void findHIRecycleBinByResourceIdReturnsNullWhenQueryFails() {
		when(session.createSelectionQuery(anyString(), eq(HIRecycleBin.class))).thenThrow(new RuntimeException("query failed"));

		assertNull(dao.findHIRecycleBinByResourceId(70));
	}

	private static void assertSameBin(HIRecycleBin expected, HIRecycleBin actual) {
		assertNotNull(actual);
		assertEquals(expected, actual);
	}
}
