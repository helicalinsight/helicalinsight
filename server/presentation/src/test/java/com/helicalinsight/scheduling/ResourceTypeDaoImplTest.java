package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNull;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.scheduling.dao.impl.ResourceTypeDaoImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ResourceTypeDaoImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;

	@Mock
	private ResourceType resourceType;

	@InjectMocks
	private ResourceTypeDaoImpl resourceTypeDaoImpl;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void testAddResourceType_a1() {
		resourceTypeDaoImpl.addResourceType(resourceType);
		verify(session, times(1)).save(resourceType);
	}

	@Test
	public void testAddResourceType_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).save(resourceType);
		resourceTypeDaoImpl.addResourceType(resourceType);
	}

	@Test
	public void testEditResourceType_a1() {
		resourceTypeDaoImpl.editResourceType(resourceType);
		verify(session, times(1)).update(resourceType);
	}

	@Test
	public void testEditResourceType_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).update(resourceType);
		resourceTypeDaoImpl.editResourceType(resourceType);
	}

	@Test
	public void testGetResourceType_a1() {
		when(session.get(eq(ResourceType.class), anyLong())).thenReturn(resourceType);
		resourceTypeDaoImpl.getResourceType(11l);
    	
	}

	@Test
	public void testGetResourceType_a2() {
		when(session.get(eq(ResourceType.class), anyLong())).thenThrow(new RuntimeException("Mocked exception"));
		resourceTypeDaoImpl.getResourceType(11l);
    	
	}

	@Test
	public void testDeleteResourceType_a1() {
		when(session.get(eq(ResourceType.class), anyLong())).thenReturn(resourceType);
		resourceTypeDaoImpl.deleteResourceType(11l);
    	
	}

	@Test
	public void testDeleteResourceType_a2() {
		when(session.get(eq(ResourceType.class), anyLong())).thenReturn(resourceType);
		doThrow(new RuntimeException("Mocked exception")).when(session).delete(resourceType);
		resourceTypeDaoImpl.deleteResourceType(11l);
	}

	@Test
	public void testFindUniqueResourceType() {
		ResourceType findUniqueResourceType = resourceTypeDaoImpl.findUniqueResourceType(resourceType);
		assertNull(findUniqueResourceType);
	}

	@Test
	public void testDeleteAllResourceType_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		resourceTypeDaoImpl.deleteAllResourceType();
	}

	@Test
	public void testDeleteAllResourceType_a2() {
		resourceTypeDaoImpl.deleteAllResourceType();
	}

	@Test
	public void testGetResourceTypeByTypeAndExtension_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		
		resourceTypeDaoImpl.getResourceTypeByTypeAndExtension("type", "extension");
	}
	
	@Test
	public void testGetResourceTypeByTypeAndExtension_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		TypedQuery<ResourceType> typed = mock(TypedQuery.class);
		
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		when(em.createQuery(criteriaQueryMock)).thenReturn(typed);
		resourceTypeDaoImpl.getResourceTypeByTypeAndExtension("type", "extension");
	}
	@Test
	public void testGetResourceTypeByTypeAndExtension_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		TypedQuery<ResourceType> typed = mock(TypedQuery.class);
		
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		when(em.createQuery(criteriaQueryMock)).thenThrow(new NoResultException());
		resourceTypeDaoImpl.getResourceTypeByTypeAndExtension("type", "extension");
	}
	@Test
	public void testGetAllResourceTypes_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
	
		resourceTypeDaoImpl.getAllResourceTypes();
	}
	@Test
	public void testGetAllResourceTypes_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		when(em.createQuery(criteriaQueryMock)).thenThrow(new NoResultException());
		resourceTypeDaoImpl.getAllResourceTypes();
	}
	@Test
	public void testGetAllResourceTypes_a3() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EntityManager em = mock(EntityManager.class);

		Field field = ResourceTypeDaoImpl.class.getDeclaredField("em");
		field.setAccessible(true);
		field.set(resourceTypeDaoImpl, em);
		CriteriaBuilder builder = mock(CriteriaBuilder.class);
		CriteriaQuery<ResourceType> criteriaQueryMock = mock(CriteriaQuery.class);
		Root<ResourceType> resource = mock(Root.class);
		TypedQuery<ResourceType> typed = mock(TypedQuery.class);
		 List<ResourceType> listOfResourceTypes = new ArrayList<>();
		when(em.getCriteriaBuilder()).thenReturn(builder);
		when(criteriaQueryMock.from(eq(ResourceType.class))).thenReturn(resource);
		when(builder.createQuery(eq(ResourceType.class))).thenReturn(criteriaQueryMock);
		when(criteriaQueryMock.select(resource)).thenReturn(criteriaQueryMock);
		when(em.createQuery(criteriaQueryMock)).thenReturn(typed);
		when(typed.getResultList()).thenReturn(listOfResourceTypes);
		resourceTypeDaoImpl.getAllResourceTypes();
	}
	
}
