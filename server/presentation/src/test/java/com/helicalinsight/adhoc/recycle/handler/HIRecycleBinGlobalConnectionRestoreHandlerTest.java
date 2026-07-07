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
import com.helicalinsight.admin.model.HIRecycleBinDSGlobalConnections;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;

public class HIRecycleBinGlobalConnectionRestoreHandlerTest {

	@InjectMocks
	private HIRecycleBinGlobalConnectionRestoreHandler handler;

	@Mock
	private GlobalConnectionService gConnectionService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinRestoresGlobalConnectionAndDeletesBinEntry() {
		GlobalConnections connection = new GlobalConnections();
		connection.setDeleted(true);

		HIRecycleBinDSGlobalConnections binConnection = new HIRecycleBinDSGlobalConnections();
		binConnection.setGlobalConnection(connection);

		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(70L);
		bin.setHiRecycleBinDsGlobalConnections(binConnection);

		Map<Long, Boolean> completed = new HashMap<>();
		when(recycleBinService.delete(70L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(gConnectionService).editGlobalConnections(connection);
		assertEquals(Boolean.FALSE, connection.isDeleted());
		assertEquals(Boolean.TRUE, completed.get(70L));
		verify(recycleBinService).delete(70L);
	}

	@Test
	public void handleRecycleBinDtoRestoresGlobalConnectionAndDeletesBinEntry() {
		GlobalConnections connection = new GlobalConnections();
		connection.setDeleted(true);

		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(71L);
		bin.setResourceId(601);

		Map<Long, Boolean> completed = new HashMap<>();
		when(gConnectionService.findGlobalConnectionById(601, false)).thenReturn(connection);
		when(recycleBinService.delete(71L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(gConnectionService).editGlobalConnections(connection);
		assertEquals(Boolean.FALSE, connection.isDeleted());
		assertEquals(Boolean.TRUE, completed.get(71L));
		verify(recycleBinService).delete(71L);
	}
}
