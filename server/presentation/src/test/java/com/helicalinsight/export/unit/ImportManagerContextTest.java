package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.handler.ImportManagerContext;

public class ImportManagerContextTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testAppendMethods() {
		ImportManagerContext context = new ImportManagerContext();
		ImportResponse response = new ImportResponse();
		context.setResponse(response);

		context.appendInsert("insert1");
		context.appendUpdate("update1");
		context.appendSkip("skip1");

		Assert.assertEquals(1, response.getInsertCount());
		Assert.assertEquals(1, response.getUpdateCount());
		Assert.assertEquals(1, response.getSkipCount());
		Assert.assertTrue(response.getInserts().contains("insert1"));
		Assert.assertTrue(response.getUpdates().contains("update1"));
		Assert.assertTrue(response.getSkips().contains("skip1"));
	}

	@Test
	public void ut_a2_testGettersSetters() {
		ImportManagerContext context = new ImportManagerContext();
		Date date = new Date();
		Manifest manifest = new Manifest();
		ImportRequest request = new ImportRequest();
		ImportResponse response = new ImportResponse();
		Map<Integer, Integer> idMap = new HashMap<>();
		idMap.put(1, 10);
		HIResource resource = mock(HIResource.class);

		context.setDate(date);
		context.setManifest(manifest);
		context.setRequest(request);
		context.setResponse(response);
		context.setCurrentDirectory("/tmp");
		context.setTableIdMap("key", idMap);
		context.setColumnIdMap("key", idMap);
		context.setResourceUrlIdMap("url", resource);

		Assert.assertEquals(date, context.getDate());
		Assert.assertEquals(manifest, context.getManifest());
		Assert.assertEquals(request, context.getRequest());
		Assert.assertEquals(response, context.getResponse());
		Assert.assertEquals("/tmp", context.getCurrentDirectory());
		Assert.assertEquals("/tmp/resources/", context.getResourcesDirectory());
		Assert.assertEquals(idMap, context.getTableIdMap("key"));
		Assert.assertEquals(idMap, context.getColumnIdMap("key"));
		Assert.assertEquals(resource, context.getResourceUrlMap().get("url"));
		Assert.assertNotNull(context.getNewOldImageIds());
	}

	@Test
	public void ut_a3_testGetDependency() {
		ImportManagerContext context = new ImportManagerContext();
		Manifest manifest = new Manifest();
		manifest.getDependencies().put("key", Arrays.asList("dep"));
		context.setManifest(manifest);
		Assert.assertEquals(Arrays.asList("dep"), context.getDependency("key"));
	}

	@Test
	public void ut_a4_testDestinationMethods() {
		ImportManagerContext context = new ImportManagerContext();
		ImportRequest request = new ImportRequest();
		request.setDestination("dest");
		context.setRequest(request);

		Assert.assertTrue(context.destinationExists());
		Assert.assertEquals("dest/folder", context.addDestination("folder"));
		Assert.assertEquals("folder", context.removeDestination("dest/folder"));
	}

	@Test
	public void ut_a5_testDestinationNotExists() {
		ImportManagerContext context = new ImportManagerContext();
		ImportRequest request = new ImportRequest();
		context.setRequest(request);

		Assert.assertFalse(context.destinationExists());
		Assert.assertEquals("folder", context.addDestination("folder"));
		Assert.assertEquals("folder", context.removeDestination("folder"));
	}

	@Test
	public void ut_a6_testProcessedAndConnections() {
		ImportManagerContext context = new ImportManagerContext();
		GlobalConnections gConn = mock(GlobalConnections.class);
		HIEfwdConnection efwdConn = mock(HIEfwdConnection.class);

		context.putGlobalConnection(1, gConn);
		context.putEfwdConnection(2, efwdConn);
		context.setProcessed("type", 1);

		Assert.assertEquals(gConn, context.getGlobalConnection(1));
		Assert.assertEquals(efwdConn, context.getEfwdConnection(2));
		Assert.assertTrue(context.getProcessed("type", 1));
		Assert.assertFalse(context.getProcessed("type", 2));
	}

	@Test
	public void ut_a7_testRecoverHIResource() {
		ImportManagerContext context = new ImportManagerContext();
		HIResource resource = mock(HIResource.class);
		HIRecycleBinService binService = mock(HIRecycleBinService.class);

		when(resource.isDeleted()).thenReturn(true);
		when(resource.getResourceId()).thenReturn(5);

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(HIRecycleBinService.class)).thenReturn(binService);
			Assert.assertTrue(context.recover(resource));
		}
	}

	@Test
	public void ut_a8_testRecoverGlobalConnection() {
		ImportManagerContext context = new ImportManagerContext();
		GlobalConnections gConnection = mock(GlobalConnections.class);
		HIRecycleBinService binService = mock(HIRecycleBinService.class);
		HIRecycleBin bin = mock(HIRecycleBin.class);

		when(gConnection.isDeleted()).thenReturn(true);
		when(gConnection.getGlobalId()).thenReturn(10);
		when(binService.findHIRecycleBinByGlobalId(10)).thenReturn(bin);
		when(bin.getId()).thenReturn(1L);

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(HIRecycleBinService.class)).thenReturn(binService);
			Assert.assertTrue(context.recover(gConnection));
		}
	}

	@Test
	public void ut_a9_testRecoverEfwdConnection() {
		ImportManagerContext context = new ImportManagerContext();
		HIEfwdConnection connection = mock(HIEfwdConnection.class);
		HIRecycleBinService binService = mock(HIRecycleBinService.class);
		HIRecycleBin bin = mock(HIRecycleBin.class);

		when(connection.isDeleted()).thenReturn(true);
		when(connection.getId()).thenReturn(20);
		when(binService.findHIRecycleBinByEFWDId(20)).thenReturn(bin);
		when(bin.getId()).thenReturn(2L);

		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean(HIRecycleBinService.class)).thenReturn(binService);
			Assert.assertTrue(context.recover(connection));
		}
	}

	@Test
	public void ut_b1_testRecoverNotDeleted() {
		ImportManagerContext context = new ImportManagerContext();
		HIResource resource = mock(HIResource.class);
		when(resource.isDeleted()).thenReturn(false);
		Assert.assertTrue(context.recover(resource));
	}

	@Test
	public void ut_b2_testEmptyIdMaps() {
		ImportManagerContext context = new ImportManagerContext();
		context.setTableIdMap("init", new HashMap<>());
		context.setColumnIdMap("init", new HashMap<>());
		Assert.assertTrue(context.getTableIdMap("missing").isEmpty());
		Assert.assertTrue(context.getColumnIdMap("missing").isEmpty());
	}

}
