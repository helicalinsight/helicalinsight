//package com.helicalinsight.datasource.dao;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.lang.reflect.Method;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.MutationQuery;
//import org.hibernate.query.Query;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import com.helicalinsight.admin.dao.UserDao;
//import com.helicalinsight.admin.enums.RecycleBinType;
//import com.helicalinsight.admin.model.HIRecycleBin;
//import com.helicalinsight.admin.model.Organization;
//import com.helicalinsight.admin.model.User;
//import com.helicalinsight.admin.service.HIRecycleBinService;
//import com.helicalinsight.datasource.model.GlobalConnections;
//import com.helicalinsight.resourcesecurity.SecurityUtils;
//import com.helicalinsight.resourcesecurity.jaxb.Security;
//
//@RunWith(MockitoJUnitRunner.Silent.class)
//public class GlobalConnectionDAOImplTest {
//
//	@InjectMocks
//	private GlobalConnectionDAOImpl dao;
//
//	@Mock
//	private SessionFactory sessionFactory;
//
//	@Mock
//	private Session session;
//
//	@Mock
//	private MutationQuery mutationQuery;
//
//	@Mock
//	private Query findQuery;
//
//	@Mock
//	private UserDao userDao;
//
//	@Mock
//	private HIRecycleBinService recycleBinService;
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//		when(sessionFactory.getCurrentSession()).thenReturn(session);
//		when(session.createMutationQuery(anyString())).thenReturn(mutationQuery);
//		when(mutationQuery.setParameter(anyString(), any())).thenReturn(mutationQuery);
//		when(mutationQuery.setParameter(anyString(), anyBoolean())).thenReturn(mutationQuery);
//		when(mutationQuery.setParameter(anyString(), anyInt())).thenReturn(mutationQuery);
//		when(mutationQuery.executeUpdate()).thenReturn(1);
//	}
//
//	@Test
//	public void deleteGlobalConnectionsSoftDeletesActiveConnectionAndPersistsDeletedFlagViaMutationQuery() {
//		GlobalConnections connection = activeConnection(42);
//		stubFindConnectionById(connection);
//
//		User deletedBy = new User();
//		deletedBy.setId(1);
//		when(userDao.findUser(1)).thenReturn(deletedBy);
//		when(recycleBinService.save(any(HIRecycleBin.class))).thenReturn(true);
//
//		Security security = new Security();
//		security.setCreatedBy("1");
//
//		try (MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
//			securityMock.when(SecurityUtils::securityObject).thenReturn(security);
//
//			assertTrue(dao.deleteGlobalConnections(42));
//			assertTrue(Boolean.TRUE.equals(connection.isDeleted()));
//		}
//
//		ArgumentCaptor<HIRecycleBin> binCaptor = ArgumentCaptor.forClass(HIRecycleBin.class);
//		verify(recycleBinService).save(binCaptor.capture());
//		HIRecycleBin bin = binCaptor.getValue();
//		assertEquals(RecycleBinType.DS_GLOBAL_CONNECTIONS, bin.getRecycleBinType());
//		assertEquals(connection, bin.getHiRecycleBinDsGlobalConnections().getGlobalConnection());
//		assertEquals(deletedBy, bin.getDeletedBy());
//		assertEquals(connection.getCreatedBy(), bin.getCreatedBy());
//
//		verify(session).createMutationQuery(anyString());
//		verify(mutationQuery).setParameter(eq("id"), eq(42));
//		verify(mutationQuery).setParameter(eq("delete"), eq(true));
//		verify(mutationQuery).executeUpdate();
//		verify(session, never()).update(any());
//	}
//
//	@Test
//	public void updateDeleteStatusPersistsDeletedFlagViaMutationQuery() throws Exception {
//		Method updateDeleteStatus = GlobalConnectionDAOImpl.class.getDeclaredMethod("updateDeleteStatus", Integer.class,
//				boolean.class);
//		updateDeleteStatus.setAccessible(true);
//
//		updateDeleteStatus.invoke(dao, 99, true);
//
//		verify(session).createMutationQuery(anyString());
//		verify(mutationQuery).setParameter(eq("id"), eq(99));
//		verify(mutationQuery).setParameter(eq("delete"), eq(true));
//		verify(mutationQuery).executeUpdate();
//	}
//
//	@SuppressWarnings("unchecked")
//	private void stubFindConnectionById(GlobalConnections connection) {
//		when(session.createQuery(anyString())).thenReturn(findQuery);
//		when(findQuery.setParameter(anyString(), any())).thenReturn(findQuery);
//		when(findQuery.uniqueResult()).thenReturn(connection);
//	}
//
//	private static GlobalConnections activeConnection(int id) {
//		Organization org = new Organization();
//		org.setId(7);
//
//		User owner = new User();
//		owner.setId(9);
//		owner.setOrganization(org);
//
//		GlobalConnections connection = new GlobalConnections();
//		connection.setGlobalId(id);
//		connection.setDeleted(false);
//		connection.setCreatedBy(owner);
//		return connection;
//	}
//}
