package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
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

		when(userService.findUserIdsByOrganizationId(401)).thenReturn(List.of(501));

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(60L);
		verify(userService, org.mockito.Mockito.times(2)).deleteUser(501);
		verify(recycleBinService).deleteRecycleBinsByUserIds(List.of(501));
		verify(roleService).deleteOrganization(401);
		verify(organizationService).delete(401);
	}

	@Test
	public void handleRecycleBinDtoContinuesWhenUserListEmpty() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(62L);
		bin.setResourceId(402);

		when(userService.findUserIdsByOrganizationId(402)).thenReturn(Collections.emptyList());

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(62L);
		verify(recycleBinService, never()).deleteRecycleBinsByUserIds(org.mockito.ArgumentMatchers.any());
		verify(userService, never()).deleteUser(org.mockito.ArgumentMatchers.anyInt());
		verify(roleService).deleteOrganization(402);
		verify(organizationService).delete(402);
	}

	@Test
	public void handleRecycleBinDtoWithEmptyUserListDeletesOrganizationOnly() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(63L);
		bin.setResourceId(403);

		when(userService.findUserIdsByOrganizationId(403)).thenReturn(Collections.emptyList());

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

		when(userService.findUserIdsByOrganizationId(404)).thenReturn(Collections.emptyList());

		Map<Long, Boolean> map = new HashMap<>();

		assertTrue(handler.handle(bin, map));

		verify(recycleBinService).delete(64L);
		verify(organizationService).delete(404);
	}
}
