package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.dto.OrganizationDTO;
import com.helicalinsight.admin.dto.RoleDTO;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.export.service.DatasourceShareHandler;
import com.helicalinsight.export.utils.ResourceShareUtils;

public class DatasourceShareHandlerTest {

	@Test
	public void ut_a1_testImportEFWDConnectionPermissions()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		HIEfwdConnSecurity security = mock(HIEfwdConnSecurity.class);
		HIEfwdConnSecurityDTO securityDTO = mock(HIEfwdConnSecurityDTO.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		EFWDConnectionService efwdConnectionService = mock(EFWDConnectionService.class);
		User user = mock(User.class);
		Role role = mock(Role.class);
		Organization org = mock(Organization.class);

		List<HIEfwdConnSecurityDTO> securitiesDtos = Arrays.asList(securityDTO);

		Field field2 = DatasourceShareHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(datasourceShareHandler, shareUtils);

		Field field1 = DatasourceShareHandler.class.getDeclaredField("efwdConnectionService");
		field1.setAccessible(true);
		field1.set(datasourceShareHandler, efwdConnectionService);
		when(security.getUserId()).thenReturn(user);
		when(security.getRoleId()).thenReturn(role);
		when(security.getOrgId()).thenReturn(org);
		when(user.getId()).thenReturn(1);
		when(role.getId()).thenReturn(1);
		when(org.getId()).thenReturn(1);
		when(mapper.map(any(HIEfwdConnSecurity.class))).thenReturn(securityDTO);
		when(shareUtils.getOrInsertUser(any(UserDTO.class))).thenReturn(user);
		when(shareUtils.getOrInsertOrganiation(any(OrganizationDTO.class))).thenReturn(org);
		when(shareUtils.getOrInsertRole(any(RoleDTO.class))).thenReturn(role);
		datasourceShareHandler.importEFWDConnectionPermissions(0, securitiesDtos);
	}

	@Test
	public void ut_a2_testImportEFWDConnectionPermissions()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		HIEfwdConnSecurity security = mock(HIEfwdConnSecurity.class);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		EFWDConnectionService efwdConnectionService = mock(EFWDConnectionService.class);
		HIEfwdConnSecurityDTO securityDTO = mock(HIEfwdConnSecurityDTO.class);
		List<HIEfwdConnSecurity> securities = Arrays.asList(security);
		List<HIEfwdConnSecurityDTO> securitiesDtos = Arrays.asList(securityDTO);
		Field field2 = DatasourceShareHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(datasourceShareHandler, shareUtils);

		Field field1 = DatasourceShareHandler.class.getDeclaredField("efwdConnectionService");
		field1.setAccessible(true);
		field1.set(datasourceShareHandler, efwdConnectionService);
		datasourceShareHandler.importEFWDConnectionPermissions(0, securitiesDtos);
	}

	@Test
	public void ut_a3_testCreateNewSecurity() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("createNewSecurity",
				GlobalConnectionSecurity.class);
		method.setAccessible(true);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		GlobalConnectionService globalConnectionService = mock(GlobalConnectionService.class);
		User user = mock(User.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		
		Field field2 = DatasourceShareHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(datasourceShareHandler, shareUtils);

		Field field1 = DatasourceShareHandler.class.getDeclaredField("globalConnectionService");
		field1.setAccessible(true);
		field1.set(datasourceShareHandler, globalConnectionService);
		
		Field field3 = DatasourceShareHandler.class.getDeclaredField("mapper");
	    field3.setAccessible(true);
	    field3.set(datasourceShareHandler, mapper);
		
		when(security.getUserId()).thenReturn(user);
		
		method.invoke(datasourceShareHandler, security);
	}

	@Test
	public void ut_a4_testCreateNewSecurity() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("createNewSecurity",
				GlobalConnectionSecurity.class);
		method.setAccessible(true);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		GlobalConnectionService globalConnectionService = mock(GlobalConnectionService.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		Role role = mock(Role.class);
		RoleDTO roleDto = mock(RoleDTO.class);

		Field field2 = DatasourceShareHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(datasourceShareHandler, shareUtils);

		Field field1 = DatasourceShareHandler.class.getDeclaredField("globalConnectionService");
		field1.setAccessible(true);
		field1.set(datasourceShareHandler, globalConnectionService);
		
		Field field3 = DatasourceShareHandler.class.getDeclaredField("mapper");
	    field3.setAccessible(true);
	    field3.set(datasourceShareHandler, mapper);
		
		when(security.getRoleId()).thenReturn(role);
		when(mapper.map(any(Role.class))).thenReturn(roleDto);
		when(shareUtils.getOrInsertRole(roleDto)).thenReturn(role);
		method.invoke(datasourceShareHandler, security);
	}

	@Test
	public void ut_a5_testCreateNewSecurity() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("createNewSecurity",
				GlobalConnectionSecurity.class);
		method.setAccessible(true);
		ResourceShareUtils shareUtils = mock(ResourceShareUtils.class);
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		GlobalConnectionService globalConnectionService = mock(GlobalConnectionService.class);
		User user = mock(User.class);
		Organization org = mock(Organization.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);

		Field field2 = DatasourceShareHandler.class.getDeclaredField("shareUtils");
		field2.setAccessible(true);
		field2.set(datasourceShareHandler, shareUtils);

		Field field1 = DatasourceShareHandler.class.getDeclaredField("globalConnectionService");
		field1.setAccessible(true);
		field1.set(datasourceShareHandler, globalConnectionService);
		
		Field field3 = DatasourceShareHandler.class.getDeclaredField("mapper");
	    field3.setAccessible(true);
	    field3.set(datasourceShareHandler, mapper);

		when(security.getUserId()).thenReturn(user);
		when(security.getOrgId()).thenReturn(org);

		method.invoke(datasourceShareHandler, security);
	}
	@Test
	public void ut_a6_testCompare() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("compare",
				GlobalConnectionSecurity.class,GlobalConnectionSecurity.class);
		method.setAccessible(true);
		
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		User user = mock(User.class);
		
		when(security.getUserId()).thenReturn(user);
		when(user.getUsername()).thenReturn("helical");
		when(security.getPermission()).thenReturn(5);
		
		Object invoke = method.invoke(datasourceShareHandler, security,security);
		Integer num = 1;
		Assert.assertEquals(num,invoke);
	}
	@Test
	public void ut_a7_testCompare() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("compare",
				GlobalConnectionSecurity.class,GlobalConnectionSecurity.class);
		method.setAccessible(true);
		
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		
		Role role = mock(Role.class);
		when(security.getRoleId()).thenReturn(role);
		when(role.getRole_name()).thenReturn("helical");
		when(security.getPermission()).thenReturn(5);
		
		Object invoke = method.invoke(datasourceShareHandler, security,security);
		Integer num = 1;
		Assert.assertEquals(num,invoke);
	}
	@Test
	public void ut_a8_testCompare() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("compare",
				GlobalConnectionSecurity.class,GlobalConnectionSecurity.class);
		method.setAccessible(true);
		
		GlobalConnectionSecurity security = mock(GlobalConnectionSecurity.class);
		Organization org = mock(Organization.class);
		when(security.getOrgId()).thenReturn(org);
		when(org.getOrg_name()).thenReturn("helical");
		when(security.getPermission()).thenReturn(5);
		
		Object invoke = method.invoke(datasourceShareHandler, security,security);
		Integer num = 1;
		Assert.assertEquals(num,invoke);
	}
	
	@Test
	public void ut_a9_testCompare() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatasourceShareHandler datasourceShareHandler = new DatasourceShareHandler();
		Method method = DatasourceShareHandler.class.getDeclaredMethod("compare",
				GlobalConnectionSecurity.class,GlobalConnectionSecurity.class);
		method.setAccessible(true);
		
		GlobalConnectionSecurity security1 = mock(GlobalConnectionSecurity.class);
		GlobalConnectionSecurity security2 = mock(GlobalConnectionSecurity.class);
		
		Object invoke = method.invoke(datasourceShareHandler, security1,security2);
		Integer num = -1;
		Assert.assertEquals(num,invoke);
	}
	
}
