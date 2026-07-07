package com.helicalinsight.externalauth;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Role;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.externalauth.cas.CasUserDetailService;
import com.helicalinsight.externalauth.cas.ExternalAdminUsers;

@RunWith(MockitoJUnitRunner.class)
public class CasUserDetailServiceTest {

	@Test(expected = NullPointerException.class)
	public void testLoadUserByUsername_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CasUserDetailService casUserDetailService = new CasUserDetailService();
		ExternalAdminUsers externalAdminUsers = mock(ExternalAdminUsers.class);
		Field field = CasUserDetailService.class.getDeclaredField("externalAdminUsers");
		field.setAccessible(true);
		field.set(casUserDetailService, externalAdminUsers);
		casUserDetailService.loadUserByUsername("username");	
	}
	
	@Test(expected = NullPointerException.class)
	public void testLoadUserByUsername_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
			
		CasUserDetailService casUserDetailService = new CasUserDetailService();
		ExternalAdminUsers externalAdminUsers = mock(ExternalAdminUsers.class);
		String str = "username";
		List<String> list = new ArrayList<>();
		list.add(str);
		when(externalAdminUsers.getAdminUsers()).thenReturn(list);
		Field field = CasUserDetailService.class.getDeclaredField("externalAdminUsers");
		field.setAccessible(true);
		field.set(casUserDetailService, externalAdminUsers);
		
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(ApplicationDefaultUserAndRoleNamesConfigurer.class);
		when(namesConfigurer.getRoleAdmin()).thenReturn("roleAdmin");
		Field field1 = CasUserDetailService.class.getDeclaredField("namesConfigurer");
		field1.setAccessible(true);
		field1.set(casUserDetailService, namesConfigurer);
		
		RoleService roleService = mock(RoleService.class);
		Role userRole = new Role();
		userRole.setRole_name("admin");
		Field field2 = CasUserDetailService.class.getDeclaredField("roleService");
		field2.setAccessible(true);
		field2.set(casUserDetailService, roleService);
		
		UserService userService = mock(UserService.class);
		Field field3 = CasUserDetailService.class.getDeclaredField("userService");
		field3.setAccessible(true);
		field3.set(casUserDetailService, userService);
		
		when(roleService.findByName(anyString())).thenReturn(userRole);
		casUserDetailService.loadUserByUsername("username");
	}
	
	@Test
	public void testSynchronize_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CasUserDetailService casUserDetailService = new CasUserDetailService();
		ExternalAdminUsers externalAdminUsers = mock(ExternalAdminUsers.class);
		when(externalAdminUsers.getExternalOrganization()).thenReturn("userorg");
		Field field = CasUserDetailService.class.getDeclaredField("externalAdminUsers");
		field.setAccessible(true);
		field.set(casUserDetailService, externalAdminUsers);
		
		
		OrganizationService organizationService = mock(OrganizationService.class);
		Organization org = mock(Organization.class);
		when(organizationService.getOrganization(anyString())).thenReturn(org);
		Field field1 = CasUserDetailService.class.getDeclaredField("organizationService");
		field1.setAccessible(true);
		field1.set(casUserDetailService, organizationService);
		
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(ApplicationDefaultUserAndRoleNamesConfigurer.class);
		when(namesConfigurer.getRoleUser()).thenReturn("roleAdmin");
		Field field2 = CasUserDetailService.class.getDeclaredField("namesConfigurer");
		field2.setAccessible(true);
		field2.set(casUserDetailService, namesConfigurer);
		
		RoleService roleService = mock(RoleService.class);
		Role userRole = new Role();
		when(roleService.findRoleByNameNOrgId(anyString(), anyInt())).thenReturn(userRole);
		Field field3 = CasUserDetailService.class.getDeclaredField("roleService");
		field3.setAccessible(true);
		field3.set(casUserDetailService, roleService);
		
		UserService userService = mock(UserService.class);
		Field field4 = CasUserDetailService.class.getDeclaredField("userService");
		field4.setAccessible(true);
		field4.set(casUserDetailService, userService);
		
		casUserDetailService.synchronize("username");
		
		
	}
	@Test
	public void testSynchronize_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CasUserDetailService casUserDetailService = new CasUserDetailService();
		ExternalAdminUsers externalAdminUsers = mock(ExternalAdminUsers.class);
		when(externalAdminUsers.getExternalOrganization()).thenReturn("userorg");
		Field field = CasUserDetailService.class.getDeclaredField("externalAdminUsers");
		field.setAccessible(true);
		field.set(casUserDetailService, externalAdminUsers);
		
		
		OrganizationService organizationService = mock(OrganizationService.class);
		when(organizationService.getOrganization(anyString())).thenReturn(null);
		Field field1 = CasUserDetailService.class.getDeclaredField("organizationService");
		field1.setAccessible(true);
		field1.set(casUserDetailService, organizationService);
		
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = mock(ApplicationDefaultUserAndRoleNamesConfigurer.class);
		when(namesConfigurer.getRoleUser()).thenReturn("roleAdmin");
		Field field2 = CasUserDetailService.class.getDeclaredField("namesConfigurer");
		field2.setAccessible(true);
		field2.set(casUserDetailService, namesConfigurer);
		
		RoleService roleService = mock(RoleService.class);
		Role userRole = new Role();
		when(roleService.findRoleByNameNOrgId(anyString(), anyInt())).thenReturn(userRole);
		Field field3 = CasUserDetailService.class.getDeclaredField("roleService");
		field3.setAccessible(true);
		field3.set(casUserDetailService, roleService);
		
		UserService userService = mock(UserService.class);
		Field field4 = CasUserDetailService.class.getDeclaredField("userService");
		field4.setAccessible(true);
		field4.set(casUserDetailService, userService);
		
		casUserDetailService.synchronize("username");
		
		
	}
	
}
