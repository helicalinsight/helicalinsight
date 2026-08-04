package com.helicalinsight.export.unit;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.ImportHandlerFactory;
import com.helicalinsight.export.handler.importres.ImportResourceManager;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.resourcedb.Deleted;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImportResourceManagerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testDoImportWithDestination()
			throws Exception {
		ImportResourceManager manager = new ImportResourceManager();
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		setField(manager, "serviceDb", serviceDb);

		ImportManagerContext context = new ImportManagerContext();
		ImportRequest request = new ImportRequest();
		request.setDestination("dest/folder");
		context.setRequest(request);

		HIResource destResource = mock(HIResource.class);
		when(serviceDb.getResourceByUrl("dest/folder", Deleted.FALSE)).thenReturn(destResource);

		AbstractResourceImportHandler handler = mock(AbstractResourceImportHandler.class);
		HIResource imported = mock(HIResource.class);
		when(handler.setContext(context)).thenReturn(handler);
		when(handler.importResource("dest/folder/path/file.efw")).thenReturn(imported);

		try (MockedStatic<ImportHandlerFactory> factoryMock = mockStatic(ImportHandlerFactory.class)) {
			factoryMock.when(() -> ImportHandlerFactory.getHandler("efw")).thenReturn(handler);

			Method doImport = ImportResourceManager.class.getDeclaredMethod("doImport", List.class,
					ImportManagerContext.class);
			doImport.setAccessible(true);
			doImport.invoke(manager, Arrays.asList("path/file.efw"), context);

			Assert.assertTrue(context.getResourceUrlMap().containsKey("dest/folder/path/file.efw"));
			Assert.assertEquals(destResource, context.getResourceUrlMap().get("dest/folder.efwfolder"));
		}
	}

	@Test
	public void ut_a2_testDoImportWithoutDestination()
			throws Exception {
		ImportResourceManager manager = new ImportResourceManager();
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		setField(manager, "serviceDb", serviceDb);

		ImportManagerContext context = new ImportManagerContext();
		ImportRequest request = new ImportRequest();
		context.setRequest(request);

		AbstractResourceImportHandler handler = mock(AbstractResourceImportHandler.class);
		HIResource imported = mock(HIResource.class);
		when(handler.setContext(context)).thenReturn(handler);
		when(handler.importResource("path/file.efw")).thenReturn(imported);

		try (MockedStatic<ImportHandlerFactory> factoryMock = mockStatic(ImportHandlerFactory.class)) {
			factoryMock.when(() -> ImportHandlerFactory.getHandler("efw")).thenReturn(handler);

			Method doImport = ImportResourceManager.class.getDeclaredMethod("doImport", List.class,
					ImportManagerContext.class);
			doImport.setAccessible(true);
			doImport.invoke(manager, Arrays.asList("path/file.efw"), context);

			Assert.assertEquals(imported, context.getResourceUrlMap().get("path/file.efw"));
		}
	}

	@Test
	public void ut_a3_testDoImportDestinationNotFound() throws Exception {
	    ImportResourceManager manager = new ImportResourceManager();
	    HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
	    setField(manager, "serviceDb", serviceDb);

	    ImportManagerContext context = new ImportManagerContext();
	    ImportRequest request = new ImportRequest();
	    request.setDestination("missing/folder");
	    context.setRequest(request);

	    when(serviceDb.getResourceByUrl("missing/folder", Deleted.FALSE))
	            .thenReturn(null);

	    Method doImport = ImportResourceManager.class
	            .getDeclaredMethod("doImport", List.class, ImportManagerContext.class);
	    doImport.setAccessible(true);

	    try {
	        doImport.invoke(manager, Collections.emptyList(), context);
	        fail("Expected ResourceImportException");
	    } catch (InvocationTargetException e) {
	        assertTrue(e.getCause() instanceof ResourceImportException);
	        assertEquals("Destination folder not found.",
	                e.getCause().getMessage());
	    }
	}

	@Test
	public void ut_a4_testDoImportNullHandlerSkipped()
			throws Exception {
		ImportResourceManager manager = new ImportResourceManager();
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		setField(manager, "serviceDb", serviceDb);

		ImportManagerContext context = new ImportManagerContext();
		context.setRequest(new ImportRequest());

		try (MockedStatic<ImportHandlerFactory> factoryMock = mockStatic(ImportHandlerFactory.class)) {
			factoryMock.when(() -> ImportHandlerFactory.getHandler("unknown")).thenReturn(null);

			Method doImport = ImportResourceManager.class.getDeclaredMethod("doImport", List.class,
					ImportManagerContext.class);
			doImport.setAccessible(true);
			doImport.invoke(manager, Arrays.asList("path/file.unknown"), context);

			Assert.assertTrue(context.getResourceUrlMap().isEmpty());
		}
	}

	@Test
	public void ut_a5_testImportFileUploadFalseReturnsJson()
			throws Exception {
		ImportResourceManager manager = new ImportResourceManager();
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
		setField(manager, "manifestUtils", manifestUtils);
		setField(manager, "fileUtils", fileUtils);

		String key = "importTestKey" + System.currentTimeMillis();
		File tempDir = new File(TempDirectoryCleaner.getTempDirectory(), key);
		File extractedDir = new File(tempDir, "extracted");
		extractedDir.mkdirs();

		try {
			ImportRequest request = new ImportRequest();
			request.setKey(key);
			request.setUpload(false);

			Manifest manifest = new Manifest();
			ResourceOptions options = new ResourceOptions();
			manifest.setOptions(options);
			manifest.setResourcePaths(Collections.emptyList());

			when(manifestUtils.readManifest(extractedDir.getAbsolutePath())).thenReturn(manifest);

			ImportResponse response = new ImportResponse();
			String result = manager.importFile(null, request, response);

			Assert.assertTrue(result.contains("\"key\""));
			Assert.assertTrue(result.contains(key));
		} finally {
			deleteRecursively(tempDir);
		}
	}

	@Test(expected = ZipResourceException.class)
	public void ut_a6_testImportFileInvalidZip()
			throws Exception {
		ImportResourceManager manager = new ImportResourceManager();

		String key = "importInvalidKey" + System.currentTimeMillis();
		File tempDir = new File(TempDirectoryCleaner.getTempDirectory(), key);
		tempDir.mkdirs();
		new File(tempDir, "only.zip").createNewFile();

		try {
			ImportRequest request = new ImportRequest();
			request.setKey(key);
			request.setUpload(false);
			manager.importFile(null, request, new ImportResponse());
		} finally {
			deleteRecursively(tempDir);
		}
	}

	private void deleteRecursively(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursively(child);
				}
			}
		}
		file.delete();
	}

}
