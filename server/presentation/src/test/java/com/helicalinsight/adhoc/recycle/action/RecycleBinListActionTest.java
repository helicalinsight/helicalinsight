package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.core.request.RecycleBinItem;

public class RecycleBinListActionTest {

	@InjectMocks
	private RecycleBinListAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void performActionReturnsRecycleBinItems() {
		RecycleBinItem item = new RecycleBinItem();
		item.setRecycleBinId(1L);
		when(recycleBinService.list()).thenReturn(List.of(item));

		String response = action.performAction();

		assertTrue(response.contains("\"recycleBinId\":1"));
		assertTrue(response.contains("\"data\""));
	}
}
