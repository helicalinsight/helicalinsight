package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.impl.HiResourceDaoImpl;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@RunWith(MockitoJUnitRunner.class)
public class HiResourceDaoImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;

	@Mock
	private HiResource hiResource;

	@InjectMocks
	private HiResourceDaoImpl hiResourceDaoImpl;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void testAddHiResource_a1() {
		hiResourceDaoImpl.addHiResource(hiResource);
		verify(session, times(1)).save(hiResource);
	}

	@Test
	public void testAddHiResource_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).save(hiResource);
		hiResourceDaoImpl.addHiResource(hiResource);
	}

	@Test
	public void testEditHiResource_a1() {
		hiResourceDaoImpl.editHiResource(hiResource);
		verify(session, times(1)).delete(hiResource);
	}

	@Test
	public void testEditHiResource_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).delete(hiResource);
		hiResourceDaoImpl.editHiResource(hiResource);
	}

	@Test
	public void testDeleteHiResource_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyLong())).thenReturn(query);
		hiResourceDaoImpl.deleteHiResource(11l);
		verify(session,times(1)).createQuery(anyString());
	}

	@Test
	public void testDeleteHiResource_a2() {
		hiResourceDaoImpl.deleteHiResource(11l);
		verify(session, times(1)).createQuery(anyString());
	}

	@Test
	public void testGetHiResource_a1() {
		hiResourceDaoImpl.getHiResource(11l);

	}

	@Test
	public void testGetHiResource_a2() {
		when(session.get(eq(HiResource.class), anyLong())).thenThrow(new RuntimeException("Mocked exception"));
		hiResourceDaoImpl.getHiResource(11l);
    	
	}

	@Test
	public void testFindUniqueHiResource_a1() {
		HiResource findUniqueHiResource = hiResourceDaoImpl.findUniqueHiResource(hiResource);
		assertNull(findUniqueHiResource);

	}

	@Test
	public void testDeleteAllHiResource_a1() {
		hiResourceDaoImpl.deleteAllHiResource();
	}

	@Test
	public void testDeleteAllHiResource_a2() {
		when(session.createQuery(anyString())).thenReturn(query);
		hiResourceDaoImpl.deleteAllHiResource();
	}

	@Test
	public void testGetResourceTypeById_a1() {
		ResourceType resourceType = mock(ResourceType.class);
		when(session.get(eq(HiResource.class), anyLong())).thenReturn(hiResource);
		when(hiResource.getResourceType()).thenReturn(resourceType);
		hiResourceDaoImpl.getResourceTypeById(11l);
	}

	@Test
	public void testGetResourceTypeById_a2() {
		hiResourceDaoImpl.getResourceTypeById(11l);
	}

	@Test
	public void testGetUserById_a1() {
		User user = mock(User.class);
		when(session.get(eq(HiResource.class), anyLong())).thenReturn(hiResource);
		when(hiResource.getCreatedBy()).thenReturn(user);
		hiResourceDaoImpl.getUserById(11l);
	}

	@Test
	public void testGetUserById_a2() {
		hiResourceDaoImpl.getUserById(11l);
	}

	@Test
	public void testGetAllSchedulesById_a1() {
		List<Schedules> listOfSchedules = new ArrayList<>();
		when(session.get(eq(HiResource.class), anyLong())).thenReturn(hiResource);
		when(hiResource.getResourceIdSchedules()).thenReturn(listOfSchedules);
		hiResourceDaoImpl.getAllSchedulesById(11l);
	}
	
	@Test
	public void testGetAllSchedulesById_a2() {
		List<Schedules> listOfSchedules = new ArrayList<>();
		hiResourceDaoImpl.getAllSchedulesById(11l);
	}
	@Test
	public void testGetHiResourceByPath_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", 11l);
	}
	
	@Test
	public void testGetHiResourceByPath_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		TypedQuery<HiResource> typed = mock(TypedQuery.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(em.createQuery(criteriaQueryMock)).thenReturn(typed);
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", 11l);
	}

	@Test
	public void testGetHiResourceByPath_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		TypedQuery<HiResource> typed = mock(TypedQuery.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(em.createQuery(criteriaQueryMock)).thenThrow(new NoResultException());
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", 11l);
	}

	@Test
	public void testGetHiResourceByPath_a4() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		TypedQuery<HiResource> typed = mock(TypedQuery.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(em.createQuery(criteriaQueryMock)).thenReturn(typed);
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", null);
	}
	@Test
	public void testGetHiResourceByPath_a5() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		TypedQuery<HiResource> typed = mock(TypedQuery.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(em.createQuery(criteriaQueryMock)).thenThrow(new NoResultException());
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", null);
	}
	@Test
	public void testGetHiResourceByPath_a6() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);
		
		Field field = HiResourceDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(hiResourceDaoImpl, em);
		
		Predicate p1 = mock(Predicate.class);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<HiResource> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<HiResource> resource = mock(Root.class);
		Path<Object> path =  mock(Path.class);
		TypedQuery<HiResource> typed = mock(TypedQuery.class);
		
		when(builder.equal(any(), anyString())).thenReturn(p1);
		when(resource.get(anyString())).thenReturn(path);
		when(criteriaQueryMock.from(eq(HiResource.class))).thenReturn(resource);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		//when(em.createQuery(criteriaQueryMock)).thenReturn(typed);
		when(builder.createQuery(eq(HiResource.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		hiResourceDaoImpl.getHiResourceByPath("path", null);
	}
	
	@Test
	public void testDeleteAllMigratedEntries_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyBoolean())).thenReturn(query);
		hiResourceDaoImpl.deleteAllMigratedEntries();
	}
	@Test
	public void testDeleteAllMigratedEntries_a2() {
		hiResourceDaoImpl.deleteAllMigratedEntries();
	}
	
	@Test
	public void testGetHiResourceByPath_b1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyString())).thenReturn(query);
		hiResourceDaoImpl.getHiResourceByPath("path");
	}
	
	@Test
	public void testGetHiResourceByPath_b2() {
		hiResourceDaoImpl.getHiResourceByPath("path");
	}
}
