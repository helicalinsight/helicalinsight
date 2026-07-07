package com.helicalinsight.adhoc.recycle.action;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.core.request.RecycleBinResourceItem;
import com.helicalinsight.efw.exceptions.EfwdServiceException;

public class RecycleBinFetchDetailsActionTest {

	@InjectMocks
	private RecycleBinFetchDetailsAction action;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test(expected = EfwdServiceException.class)
	public void performActionThrowsWhenRecycleBinIdsMissing() {
		action.setFormData(new JsonObject());
		action.performAction();
	}

	@Test(expected = EfwdServiceException.class)
	public void performActionThrowsWhenRecycleBinIdsEmpty() {
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", new JsonArray());
		action.setFormData(formData);
		action.performAction();
	}

	@Test
	public void performActionReturnsDetailsForEachRecycleBinId() {
		JsonArray recycleBinIds = new JsonArray();
		recycleBinIds.add(100);
		recycleBinIds.add(101);
		JsonObject formData = new JsonObject();
		formData.add("recycleBinIds", recycleBinIds);
		action.setFormData(formData);

		RecycleBinResourceItem resource = new RecycleBinResourceItem("report", true);
		resource.setResourceId(1);
		when(recycleBinService.findAllResourceOfRecycleBinItem(100L)).thenReturn(
				Map.of("resources", List.of(resource)));
		when(recycleBinService.findAllResourceOfRecycleBinItem(101L)).thenReturn(Map.of());

		String response = action.performAction();

		assertTrue(response.contains("\"100\""));
		assertTrue(response.contains("\"101\""));
		assertTrue(response.contains("\"report\""));
	}
}
