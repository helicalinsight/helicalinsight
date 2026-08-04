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

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.AbstractResourceWriterHandler;
import com.helicalinsight.export.handler.CubeWriterHandler;
import com.helicalinsight.export.handler.FolderWriterHandler;
import com.helicalinsight.export.handler.MetadataWriterHandler;
import com.helicalinsight.export.handler.ResourceDataWriter;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CubeWriterHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testWrite()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CubeWriterHandler handler = new CubeWriterHandler();
		HIResourceServiceDB serviceDB = mock(HIResourceServiceDB.class);
		ResourceDTOMapper dtoMapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ResourceDataWriter dataWriter = mock(ResourceDataWriter.class);
		FolderWriterHandler folderWriterHandler = mock(FolderWriterHandler.class);
		MetadataWriterHandler mdWriterHandler = mock(MetadataWriterHandler.class);
		HICubeDAOService cubeService = mock(HICubeDAOService.class);

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

		Field folderField = CubeWriterHandler.class.getDeclaredField("folderWriterHandlerHandler");
		folderField.setAccessible(true);
		folderField.set(handler, folderWriterHandler);

		Field mdField = CubeWriterHandler.class.getDeclaredField("mdWriterHandler");
		mdField.setAccessible(true);
		mdField.set(handler, mdWriterHandler);

		Field cubeField = CubeWriterHandler.class.getDeclaredField("cubeService");
		cubeField.setAccessible(true);
		cubeField.set(handler, cubeService);

		HIResourceDTO resource = mock(HIResourceDTO.class);
		HIResource folderResource = mock(HIResource.class);
		HIResourceDTO folderDTO = mock(HIResourceDTO.class);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		HIResource mdResource = mock(HIResource.class);
		HIResourceDTO mdDTO = mock(HIResourceDTO.class);
		Manifest manifest = new Manifest();
		ResourceOptions options = new ResourceOptions();

		when(resource.getPath()).thenReturn("parent/cube");
		when(resource.getName()).thenReturn("cube");
		when(resource.getResourceId()).thenReturn(1);
		when(manifestUtils.pathExists("parent" + ResourceExtension.FOLDER.getValue(), manifest)).thenReturn(false);
		when(manifestUtils.pathExists("parent/cube", manifest)).thenReturn(false);
		when(serviceDB.getResourceByUrl("parent")).thenReturn(folderResource);
		when(dtoMapper.map(folderResource)).thenReturn(folderDTO);
		when(cubeService.findCubeByResourceId(1)).thenReturn(hiMetadataCube);
		when(hiMetadataCube.getHiResourceMetadata()).thenReturn(hiResourceMetadata);
		when(hiResourceMetadata.getHiResource()).thenReturn(mdResource);
		when(mdResource.getResourceURL()).thenReturn("parent/metadata");
		when(dtoMapper.map(mdResource)).thenReturn(mdDTO);

		handler.write(resource, "dir", manifest, options);

		verify(folderWriterHandler).write(folderDTO, "dir", manifest, options);
		verify(mdWriterHandler).write(mdDTO, "dir", manifest, options);
		verify(dataWriter).write(any(), eq("dir"), eq(resource), eq(""));
		verify(manifestUtils).insertPath("parent/cube", manifest);
		verify(manifestUtils).insertDependency(eq("parent/cube"), anyString(), eq(manifest));
	}

}
