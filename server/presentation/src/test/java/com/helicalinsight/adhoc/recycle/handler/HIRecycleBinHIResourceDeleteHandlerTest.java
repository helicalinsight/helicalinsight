package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.HIRecyclebinHelperService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.core.request.RecycleBinDatasource;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.datasource.service.EFWDConnectionService;

public class HIRecycleBinHIResourceDeleteHandlerTest {

	@InjectMocks
	private HIRecycleBinHIResourceDeleteHandler handler;

	@Mock
	private HIResourceServiceDB serviceDb;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Mock
	private EFWDConnectionService efwdConnectionService;

	@Mock
	private HIRecyclebinHelperService hiRecyclebinHelperService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void getLinkedDataReturnsEmptyObjectWhenRecycleBinMissing() {
		when(recycleBinService.isRecycleBinPresent(100L)).thenReturn(false);

		JsonObject linkedData = handler.getLinkedData(100L);

		assertFalse(linkedData.has("resources"));
		assertFalse(linkedData.has("dataSources"));
	}

	@Test
	public void getLinkedDataReturnsResourcesAndDataSources() {
		when(recycleBinService.isRecycleBinPresent(101L)).thenReturn(true);

		Map<String, List<Object>> associated = new HashMap<>();
		associated.put("resources", List.of(new RecycleBinResourceItem("report", "reort", true, 11)));
		associated.put("dataSources", List.of(new RecycleBinDatasource("ds", "jdbc", true, 22, "/tmp")));
		when(recycleBinService.findAllResourceOfRecycleBinItem(101L)).thenReturn(associated);

		JsonObject linkedData = handler.getLinkedData(101L);

		assertEquals(1, linkedData.get("resources").getAsJsonArray().size());
		assertEquals(11, linkedData.get("resources").getAsJsonArray().get(0).getAsInt());
		assertEquals(1, linkedData.get("dataSources").getAsJsonArray().size());
		assertEquals(22, linkedData.get("dataSources").getAsJsonArray().get(0).getAsInt());
	}

	@Test
	public void handleRecycleBinDtoHardDeletesViaHelper() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(200L);
		bin.setResourceId(31);

		Map<Long, Boolean> deleteStatusMap = new HashMap<>();

		assertTrue(handler.handle(bin, deleteStatusMap));

		verify(hiRecyclebinHelperService).deleteHIResourceAndRecyclebin(bin);
		assertEquals(Boolean.TRUE, deleteStatusMap.get(200L));
	}

	@Test
	public void handleRecycleBinDtoWithNullStatusMapStillDeletes() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(210L);

		assertTrue(handler.handle(bin, null));

		verify(hiRecyclebinHelperService).deleteHIResourceAndRecyclebin(bin);
	}

	@Test
	public void handleRecycleBinReturnsTrue() {
		HIRecycleBin bin = new HIRecycleBin();
		bin.setId(220L);

		assertTrue(handler.handle(bin));
	}
}
