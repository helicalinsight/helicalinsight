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
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIRecycleBinHIEfwdConnection;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.service.EFWDConnectionService;

public class HIRecycleBinHiEfwdConnectionRestoreHandlerTest {

	@InjectMocks
	private HIRecycleBinHiEfwdConnectionRestoreHandler handler;

	@Mock
	private EFWDConnectionService connectionService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinRestoresConnectionAndDeletesBinEntry() {
		HIEfwdConnection connection = new HIEfwdConnection();
		connection.setDeleted(true);

		HIRecycleBinHIEfwdConnection binConnection = new HIRecycleBinHIEfwdConnection();
		binConnection.setEfwdConnection(connection);

		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(10L);
		bin.setHiRecycleBinHIEfwdConnection(binConnection);

		Map<Long, Boolean> completed = new HashMap<>();
		when(recycleBinService.delete(10L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(connectionService).edit(connection);
		assertEquals(Boolean.FALSE, connection.isDeleted());
		assertEquals(Boolean.TRUE, completed.get(10L));
		verify(recycleBinService).delete(10L);
	}

	@Test
	public void handleRecycleBinDtoRestoresConnectionAndDeletesBinEntry() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(20L);
		bin.setResourceId(5);

		Map<Long, Boolean> completed = new HashMap<>();
		when(recycleBinService.delete(20L)).thenReturn(true);

		assertTrue(handler.handle(bin, completed));

		verify(connectionService).restoreConnection(5);
		assertEquals(Boolean.TRUE, completed.get(20L));
		verify(recycleBinService).delete(20L);
	}
}
