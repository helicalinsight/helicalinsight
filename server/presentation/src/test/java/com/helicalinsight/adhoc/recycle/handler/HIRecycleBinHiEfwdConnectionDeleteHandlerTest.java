package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertEquals;
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
import com.helicalinsight.datasource.service.EFWDConnectionService;

public class HIRecycleBinHiEfwdConnectionDeleteHandlerTest {

	@InjectMocks
	private HIRecycleBinHiEfwdConnectionDeleteHandler handler;

	@Mock
	private EFWDConnectionService connectionService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinDtoDeletesBinAndConnection() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(11L);
		bin.setResourceId(7);

		assertTrue(handler.handle(bin));

		verify(recycleBinService).delete(11L);
		verify(connectionService).deleteEFConnectionById(7);
	}

	@Test
	public void handleRecycleBinDtoWithMapUpdatesStatusWhenEntryPresent() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(12L);
		bin.setResourceId(8);

		Map<Long, Boolean> deleteStatusMap = new HashMap<>();
		deleteStatusMap.put(12L, false);

		assertTrue(handler.handle(bin, deleteStatusMap));

		assertEquals(Boolean.TRUE, deleteStatusMap.get(12L));
		verify(recycleBinService).delete(12L);
		verify(connectionService).deleteEFConnectionById(8);
	}

	@Test
	public void handleRecycleBinDtoWithEmptyMapSkipsStatusUpdate() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(13L);
		bin.setResourceId(9);

		Map<Long, Boolean> deleteStatusMap = new HashMap<>();

		assertTrue(handler.handle(bin, deleteStatusMap));

		assertTrue(deleteStatusMap.isEmpty());
		verify(recycleBinService).delete(13L);
		verify(connectionService).deleteEFConnectionById(9);
	}
}
