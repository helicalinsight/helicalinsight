package com.helicalinsight.admin.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import com.helicalinsight.admin.dto.RecycleBinDTO;
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
	public void deleteByIdDelegatesToEntityDelete() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(1L);
		doReturn(bin).when(daoSpy).findHIRecycleBinById(1L);
		doReturn(true).when(daoSpy).delete(bin);

		assertTrue(daoSpy.delete(1L));

		verify(daoSpy).findHIRecycleBinById(1L);
		verify(daoSpy).delete(bin);
	}

	@Test
	public void deleteByIdReturnsFalseWhenLookupFails() {
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		doThrow(new RuntimeException("lookup failed")).when(daoSpy).findHIRecycleBinById(2L);

		assertFalse(daoSpy.delete(2L));
	}

	@Test
	public void deleteBinRemovesAssociationsAndRecycleBinEntry() {
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(3L);
		bin.setHiRecycleBinHUsers(new HIRecycleBinHUsers());

		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
		when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(1);

		assertTrue(dao.delete(bin));

		verify(session, times(2)).createMutationQuery(anyString());
		verify(mutationQuery, times(2)).executeUpdate();
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
		HIRecycleBinDaoImpl daoSpy = spy(dao);
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(50L);
		doReturn(Optional.of(bin)).when(daoSpy).findHIRecycleBinByEFWDId(42);
		doReturn(true).when(daoSpy).delete(bin);

		daoSpy.deleteHIRecycleByEfwdId(42);

		verify(daoSpy).delete(bin);
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
		when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);
		when(mutationQuery.executeUpdate()).thenReturn(2);

		dao.deleteRecycleBinsByIds(List.of(60L, 61L));

		verify(session, times(2)).createMutationQuery(anyString());
		verify(mutationQuery, times(2)).executeUpdate();
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
