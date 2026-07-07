package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.RoleService;
import com.helicalinsight.admin.service.UserService;

public class HIRecycleBinOrganizationDeleteHandlerTest {

	@InjectMocks
	private HIRecycleBinOrganizationDeleteHandler handler;

	@Mock
	private OrganizationService organizationService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private UserService userService;

	@Mock
	private RoleService roleService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinDtoDeletesOrganizationUsersAndRoles() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(60L);
		bin.setResourceId(401);

		User user = new User();
		user.setId(501);

		HIRecycleBin userBin = new HIRecycleBin();
		userBin.setId(61L);

		when(userService.getAllUsersOfOrganization(401)).thenReturn(List.of(user));
		when(recycleBinService.findHIRecycleBinByUserId(501)).thenReturn(userBin);

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(60L);
		verify(recycleBinService).delete(61L);
		verify(userService).deleteUser(501);
		verify(roleService).deleteOrganization(401);
		verify(organizationService).delete(401);
	}

	@Test
	public void handleRecycleBinDtoContinuesWhenUserBinNotFound() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(62L);
		bin.setResourceId(402);

		User user = new User();
		user.setId(502);

		when(userService.getAllUsersOfOrganization(402)).thenReturn(List.of(user));
		when(recycleBinService.findHIRecycleBinByUserId(502)).thenThrow(new RuntimeException("not found"));

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(62L);
		verify(userService).deleteUser(502);
		verify(roleService).deleteOrganization(402);
		verify(organizationService).delete(402);
	}

	@Test
	public void handleRecycleBinDtoWithEmptyUserListDeletesOrganizationOnly() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(63L);
		bin.setResourceId(403);

		when(userService.getAllUsersOfOrganization(403)).thenReturn(Collections.emptyList());

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(63L);
		verify(roleService).deleteOrganization(403);
		verify(organizationService).delete(403);
	}

	@Test
	public void handleRecycleBinDtoWithMapDelegatesToSingleArgHandle() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(64L);
		bin.setResourceId(404);

		when(userService.getAllUsersOfOrganization(404)).thenReturn(Collections.emptyList());

		Map<Long, Boolean> map = new HashMap<>();

		assertTrue(handler.handle(bin, map));

		verify(recycleBinService).delete(64L);
		verify(organizationService).delete(404);
	}
}
