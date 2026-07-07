package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHUsers;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;

public class HIRecycleBinHUserRestoreHandlerTest {

	@InjectMocks
	private HIRecycleBinHUserRestoreHandler handler;

	@Mock
	private UserService userService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinRestoresUserAndDeletesBinEntry() {
		User user = new User();
		user.setDeleted(true);

		HIRecycleBinHUsers binUsers = new HIRecycleBinHUsers();
		binUsers.setUser(user);

		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(40L);
		bin.setHiRecycleBinHUsers(binUsers);

		Map<Long, Boolean> completed = new HashMap<>();
		when(recycleBinService.delete(40L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(userService).editUser(user);
		assertEquals(Boolean.FALSE, user.isDeleted());
		assertEquals(Boolean.TRUE, completed.get(40L));
		verify(recycleBinService).delete(40L);
	}

	@Test
	public void handleRecycleBinDtoRestoresUserAndDeletesBinEntry() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(41L);
		bin.setResourceId(201);

		Map<Long, Boolean> completed = new HashMap<>();
		when(recycleBinService.delete(41L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(userService).restoreUser(201);
		assertEquals(Boolean.TRUE, completed.get(41L));
		verify(recycleBinService).delete(41L);
	}
}
