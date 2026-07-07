package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.UserService;

public class HIRecycleBinHUserDeleteHandlerTest {

	@InjectMocks
	private HIRecycleBinHUserDeleteHandler handler;

	@Mock
	private UserService userService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinDtoDeletesBinAndUser() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(30L);
		bin.setResourceId(101);

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(30L);
		verify(userService).deleteUser(101);
	}

	@Test
	public void handleRecycleBinDtoWithMapDelegatesToSingleArgHandle() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(31L);
		bin.setResourceId(102);

		Map<Long, Boolean> map = new HashMap<>();

		assertTrue(handler.handle(bin, map));

		verify(recycleBinService).delete(31L);
		verify(userService).deleteUser(102);
	}
}
