//package com.helicalinsight.datasource.dao;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.never;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.MutationQuery;
//import org.hibernate.query.SelectionQuery;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.springframework.context.ApplicationContext;
//
//import com.helicalinsight.admin.dao.UserDao;
//import com.helicalinsight.admin.enums.RecycleBinType;
//import com.helicalinsight.admin.model.HIEFWD;
//import com.helicalinsight.admin.model.HIEfwdConnection;
//import com.helicalinsight.admin.model.HIRecycleBin;
//import com.helicalinsight.admin.model.Organization;
//import com.helicalinsight.admin.model.User;
//import com.helicalinsight.admin.service.HIRecycleBinService;
//import com.helicalinsight.admin.utils.ResourceDTOMapper;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
//import com.helicalinsight.resourcesecurity.SecurityUtils;
//import com.helicalinsight.resourcesecurity.jaxb.Security;
//
//@RunWith(MockitoJUnitRunner.Silent.class)
//public class EFWDConnectionDAOImplTest {
//
//	@InjectMocks
//	private EFWDConnectionDAOImpl dao;
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
//	private SelectionQuery<HIEfwdConnection> selectionQuery;
//
//	@Mock
//	private UserDao userDao;
//
//	@Mock
//	private HIRecycleBinService recycleBinService;
//
//	@Mock
//	private ResourceDTOMapper mapper;
//
//	@BeforeClass
//	public static void initApplicationContextAccessor() throws Exception {
//		ApplicationContext applicationContext = mock(ApplicationContext.class);
//		when(applicationContext.getBean(ResourcePermissionLevelsHolder.class))
//				.thenReturn(mock(ResourcePermissionLevelsHolder.class));
//		when(applicationContext.getBean(Security.class)).thenReturn(new Security());
//
//		ApplicationContextAccessor accessor = new ApplicationContextAccessor();
//		Field contextField = ApplicationContextAccessor.class.getDeclaredField("applicationContext");
//		contextField.setAccessible(true);
//		contextField.set(accessor, applicationContext);
//
//		Field instanceField = ApplicationContextAccessor.class.getDeclaredField("instance");
//		instanceField.setAccessible(true);
//		instanceField.set(null, accessor);
//	}
//
//	@AfterClass
//	public static void clearApplicationContextAccessor() throws Exception {
//		Field instanceField = ApplicationContextAccessor.class.getDeclaredField("instance");
//		instanceField.setAccessible(true);
//		instanceField.set(null, null);
//	}
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
//	public void deleteEFConnectionByIdSoftDeletesActiveConnectionAndPersistsDeletedFlag() {
//		HIEfwdConnection connection = activeConnection(42);
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
//			Integer result = dao.deleteEFConnectionById(42);
//
//			assertEquals(Integer.valueOf(42), result);
//			assertTrue(connection.isDeleted());
//		}
//
//		ArgumentCaptor<HIRecycleBin> binCaptor = ArgumentCaptor.forClass(HIRecycleBin.class);
//		verify(recycleBinService).save(binCaptor.capture());
//		HIRecycleBin bin = binCaptor.getValue();
//		assertEquals(RecycleBinType.HI_EFWD_CONNECTION, bin.getRecycleBinType());
//		assertEquals(connection, bin.getHiRecycleBinHIEfwdConnection().getEfwdConnection());
//		assertEquals(deletedBy, bin.getDeletedBy());
//		assertEquals(connection.getHiResourceEFWD().getCreatedBy(), bin.getCreatedBy());
//
//		verify(session).createMutationQuery(anyString());
//		verify(mutationQuery).setParameter(eq("id"), eq(42));
//		verify(mutationQuery).setParameter(eq("delete"), eq(true));
//		verify(mutationQuery).executeUpdate();
//		verify(session, never()).merge(any());
//	}
//
//	@Test
//	public void restoreConnectionPersistsDeletedFlagAsFalse() {
//		dao.restoreConnection(42);
//
//		verify(session).createMutationQuery(anyString());
//		verify(mutationQuery).setParameter(eq("id"), eq(42));
//		verify(mutationQuery).setParameter(eq("delete"), eq(false));
//		verify(mutationQuery).executeUpdate();
//	}
//
//	@Test
//	public void updateDeleteStatusPersistsDeletedFlagViaMutationQuery() throws Exception {
//		Method updateDeleteStatus = EFWDConnectionDAOImpl.class.getDeclaredMethod("updateDeleteStatus", Integer.class,
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
//	private void stubFindConnectionById(HIEfwdConnection connection) {
//		when(session.createSelectionQuery(anyString(), eq(HIEfwdConnection.class))).thenReturn(selectionQuery);
//		when(selectionQuery.setParameter(anyString(), any())).thenReturn(selectionQuery);
//		when(selectionQuery.uniqueResult()).thenReturn(connection);
//	}
//
//	private static HIEfwdConnection activeConnection(int id) {
//		Organization org = new Organization();
//		org.setId(7);
//
//		User owner = new User();
//		owner.setId(9);
//		owner.setOrganization(org);
//
//		HIEFWD efwd = new HIEFWD();
//		efwd.setCreatedBy(owner);
//
//		HIEfwdConnection connection = new HIEfwdConnection();
//		connection.setId(id);
//		connection.setDeleted(false);
//		connection.setHiResourceEFWD(efwd);
//		return connection;
//	}
//}
