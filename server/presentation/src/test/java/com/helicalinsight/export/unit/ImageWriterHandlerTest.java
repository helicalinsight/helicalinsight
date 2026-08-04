package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.AbstractResourceWriterHandler;
import com.helicalinsight.export.handler.FolderWriterHandler;
import com.helicalinsight.export.handler.ImageWriterHandler;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImageWriterHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testWrite()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ImageWriterHandler handler = new ImageWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);

		Field serviceField = AbstractResourceWriterHandler.class.getDeclaredField("serviceDB");
		serviceField.setAccessible(true);
		serviceField.set(handler, serviceDB);

		Field mapperField = AbstractResourceWriterHandler.class.getDeclaredField("dtoMapper");
		mapperField.setAccessible(true);
		mapperField.set(handler, dtoMapper);

		Field manifestField = AbstractResourceWriterHandler.class.getDeclaredField("manifestUtils");
		manifestField.setAccessible(true);
		manifestField.set(handler, manifestUtils);

		Field writerField = AbstractResourceWriterHandler.class.getDeclaredField("dataWriter");
		writerField.setAccessible(true);
		writerField.set(handler, dataWriter);

		Field folderField = ImageWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		HIResourceDTO report = mock(HIResourceDTO.class);
		HIResource hResource = mock(HIResource.class);
		HIResource resourceDir = mock(HIResource.class);
		HIResourceDTO folderDTO = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();

		when(report.getResourceId()).thenReturn(1);
		when(report.getPath()).thenReturn("parent/image");
		when(serviceDB.getResourceByIdIgnoreFilter(1)).thenReturn(hResource);
		when(hResource.getResourceURL()).thenReturn("parent/image");
		when(manifestUtils.pathExists("parent" + ResourceExtension.FOLDER.getValue(), manifest)).thenReturn(false);
		when(serviceDB.getResourceByUrl("parent", false)).thenReturn(resourceDir);
		when(dtoMapper.map(resourceDir)).thenReturn(folderDTO);

		handler.write(report, "dir", manifest, options);

		verify(folderWriterHandler).write(folderDTO, "dir", manifest, options);
		verify(dataWriter).write(hResource, "dir", report, "");
		verify(manifestUtils).insertDependency("parent/image", "parent", manifest);
		verify(manifestUtils).insertPath("parent/image", manifest);
	}

}
