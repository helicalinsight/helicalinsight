package com.helicalinsight.export.unit;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.adhoc.metadata.genericdb.EnhancedJoinsHandler;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.dto.HIMetadataColumnsDTO;
import com.helicalinsight.admin.dto.HIMetadataConnectionDTO;
import com.helicalinsight.admin.dto.HIMetadataRelationshipsDTO;
import com.helicalinsight.admin.dto.HIMetadataSecurityDTO;
import com.helicalinsight.admin.dto.HIMetadataTableDTO;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourceMetadataDTO;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.MetadataImportHandler;
import com.helicalinsight.export.handler.importres.MetadataUpdateHandler;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataImportHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testGetRelationshipMap()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			java.lang.reflect.InvocationTargetException {
		MetadataImportHandler handler = new MetadataImportHandler();
		HIMetadataColumns leftCol = mock(HIMetadataColumns.class);
		HIMetadataColumns rightCol = mock(HIMetadataColumns.class);
		HIMetadataRelationships rel = mock(HIMetadataRelationships.class);

		when(leftCol.getColumnAliasName()).thenReturn("leftCol");
		when(rightCol.getColumnAliasName()).thenReturn("rightCol");
		when(rel.getLeftMetadataColumns()).thenReturn(leftCol);
		when(rel.getRightMetadataColumns()).thenReturn(rightCol);
		when(rel.getOperator()).thenReturn("=");

		Method method = MetadataImportHandler.class.getDeclaredMethod("getRelationshipMap", List.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String, List<HIMetadataRelationships>> result = (Map<String, List<HIMetadataRelationships>>) method
				.invoke(handler, Arrays.asList(rel, rel));

		Assert.assertEquals(1, result.size());
		Assert.assertEquals(2, result.get("leftCol=rightCol").size());
	}

	@Test
	public void ut_a2_testGetRelationshipMapEmpty()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			java.lang.reflect.InvocationTargetException {
		MetadataImportHandler handler = new MetadataImportHandler();
		Method method = MetadataImportHandler.class.getDeclaredMethod("getRelationshipMap", List.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String, List<HIMetadataRelationships>> result = (Map<String, List<HIMetadataRelationships>>) method
				.invoke(handler, new ArrayList<>());

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void ut_a3_testImportResourceSkip()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataImportHandler handler = new MetadataImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResourceMetadataDTO metadataDto = mock(HIResourceMetadataDTO.class);
		HIResourceDTO hiResourceDto = mock(HIResourceDTO.class);
		HIResource mappedResource = mock(HIResource.class);
		HIResource existingResource = mock(HIResource.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, mapper, manifestUtils, shareHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getResourceUrlMap()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("skip");
		when(request.getOptions()).thenReturn(mock(ResourceOptions.class));
		when(manifestUtils.compareOptions(any(), any(), anyString())).thenReturn(false);
		when(fileReader.read(context, "parent/meta.metadata", HIResourceMetadataDTO.class)).thenReturn(metadataDto);
		when(metadataDto.getHiResource()).thenReturn(hiResourceDto);
		when(mapper.toEntity(hiResourceDto)).thenReturn(mappedResource);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(existingResource);
		when(existingResource.getResourceURL()).thenReturn("parent/meta.metadata");

		HIResource result = handler.importResource("parent/meta.metadata");
		Assert.assertEquals(existingResource, result);
	}

	@Test
	public void ut_a4_testImportResourceUpdate()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataImportHandler handler = new MetadataImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadataDTO metadataDto = mock(HIResourceMetadataDTO.class);
		HIResourceDTO hiResourceDto = mock(HIResourceDTO.class);
		HIResource mappedResource = mock(HIResource.class);
		HIResource existingResource = mock(HIResource.class);
		HIResourceMetadata dbMetadata = mock(HIResourceMetadata.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		MetadataUpdateHandler metadataUpdateHandler = mock(MetadataUpdateHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, mapper, manifestUtils, shareHandler);
		setField(handler, "mdServiceDb", mdServiceDb);
		setField(handler, "metadataUpdateHandler", metadataUpdateHandler);

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.recover(existingResource)).thenReturn(true);
		when(context.getResourceUrlMap()).thenReturn(new HashMap<>());
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(false);
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(false);
		when(fileReader.read(context, "parent/meta.metadata", HIResourceMetadataDTO.class)).thenReturn(metadataDto);
		when(metadataDto.getHiResource()).thenReturn(hiResourceDto);
		when(metadataDto.getFileName()).thenReturn("metaFile");
		when(metadataDto.getCreatedBy()).thenReturn(5);
		when(mapper.toEntity(hiResourceDto)).thenReturn(mappedResource);
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(existingResource);
		when(existingResource.getResourceId()).thenReturn(1);
		when(existingResource.getResourceURL()).thenReturn("parent/meta.metadata");
		when(existingResource.getTitle()).thenReturn("metaTitle");
		when(mdServiceDb.giveHIResourceMetadataByResourceId(anyInt())).thenReturn(dbMetadata);
		when(metadataUpdateHandler.setContext(context)).thenReturn(metadataUpdateHandler);

		try (MockedStatic<ApplicationContextAccessor> appMock = ExportTestSecuritySupport.mockApplicationContextForSecurity();
				MockedStatic<SecurityUtils> securityMock = mockStatic(SecurityUtils.class)) {
			Security security = ExportTestSecuritySupport.mockSecurityWithCreatedBy("1");
			securityMock.when(SecurityUtils::securityObject).thenReturn(security);

			HIResource result = handler.importResource("parent/meta.metadata");
			Assert.assertEquals(existingResource, result);
		}
	}

	@Test
	public void ut_a5_testImportResourceCreateNew()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		MetadataImportHandler handler = new MetadataImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceOptions options = mock(ResourceOptions.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIMetadataResourceServiceDB mdServiceDb = mock(HIMetadataResourceServiceDB.class);
		HIResourceMetadataDTO metadataDto = mock(HIResourceMetadataDTO.class);
		HIResourceDTO hiResourceDto = mock(HIResourceDTO.class);
		HIResource mappedResource = mock(HIResource.class);
		HIResource createdResource = mock(HIResource.class);
		HIMetadataConnectionDTO connectionDto = mock(HIMetadataConnectionDTO.class);
		HIMetadataTableDTO tableDto = mock(HIMetadataTableDTO.class);
		HIMetadataColumnsDTO columnDto = mock(HIMetadataColumnsDTO.class);
		HIMetadataSecurityDTO securityDto = mock(HIMetadataSecurityDTO.class);
		HIMetadataRelationshipsDTO relationshipDto = mock(HIMetadataRelationshipsDTO.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);
		ResourceDTOMapper mapper = mock(ResourceDTOMapper.class);
		ManifestUtils manifestUtils = mock(ManifestUtils.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		EnhancedJoinsHandler enhancedJoinsHandler = mock(EnhancedJoinsHandler.class);
		Manifest manifest = mock(Manifest.class);

		inject(handler, context, serviceDb, fileReader, mapper, manifestUtils, shareHandler);
		setField(handler, "mdServiceDb", mdServiceDb);
		setField(handler, "enhancedJoinsHandler", enhancedJoinsHandler);

		Map<String, HIResource> urlMap = new HashMap<>();

		when(context.getRequest()).thenReturn(request);
		when(context.getManifest()).thenReturn(manifest);
		when(context.getDate()).thenReturn(new Date());
		when(context.getResourceUrlMap()).thenReturn(urlMap);
		when(request.getOnConflict()).thenReturn("update");
		when(request.getOptions()).thenReturn(options);
		when(options.getShare()).thenReturn(true);
		when(manifestUtils.compareOptions(options, manifest, "datasource")).thenReturn(false);
		when(fileReader.read(context, "parent/meta.metadata", HIResourceMetadataDTO.class)).thenReturn(metadataDto);
		when(metadataDto.getHiResource()).thenReturn(hiResourceDto);
		when(metadataDto.getConnectionType()).thenReturn("jdbc");
		when(metadataDto.getDatabaseType()).thenReturn("mysql");
		when(metadataDto.getType()).thenReturn("type");
		when(metadataDto.getFileName()).thenReturn("metaFile");
		when(metadataDto.getCreatedBy()).thenReturn(3);
		when(metadataDto.getHiMetadataConnections()).thenReturn(Arrays.asList(connectionDto));
		when(metadataDto.getMetadataSecurityList()).thenReturn(Arrays.asList(securityDto));
		when(connectionDto.getConnectionType()).thenReturn("jdbc");
		when(connectionDto.getMetadataGlobalConnList()).thenReturn(Collections.emptyList());
		when(connectionDto.getMetadataConnectionEfwd()).thenReturn(Collections.emptyList());
		when(connectionDto.getMetadataDatabases()).thenReturn(Collections.emptyList());
		when(mapper.toEntity(hiResourceDto)).thenReturn(mappedResource);
		when(mappedResource.getParentId()).thenReturn(null);
		when(hiResourceDto.getPath()).thenReturn("parent/meta.metadata");
		when(hiResourceDto.getName()).thenReturn("meta");
		when(hiResourceDto.getTitle()).thenReturn("metaTitle");
		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(createdResource.getCreatedBy()).thenReturn(3);
		when(createdResource.getResourceURL()).thenReturn("parent/meta.metadata");
		when(mdServiceDb.getMetadataTablesList(anyInt(), anyInt())).thenReturn(Collections.emptyList());
		when(mdServiceDb.getMetadataColumnsList(anyInt(), anyInt())).thenReturn(Collections.emptyList());
		when(mdServiceDb.getRelationshipListByMetadataIdAndDbId(anyInt(), anyInt()))
				.thenReturn(Collections.emptyList());
		when(mdServiceDb.getMetadataViewList(anyInt(), anyInt())).thenReturn(Collections.emptyList());
		when(securityDto.getExpressionType()).thenReturn("table");
		when(securityDto.getExpressionOn()).thenReturn("1");

		try (MockedStatic<ResourceUtils> resourceUtilsMock = mockStatic(ResourceUtils.class);
				MockedStatic<JsonUtils> jsonUtilsMock = mockStatic(JsonUtils.class)) {
			jsonUtilsMock.when(JsonUtils::getMetadataExtension).thenReturn("metadata");
			jsonUtilsMock.when(JsonUtils::getFolderFileExtension).thenReturn("efwfolder");
			resourceUtilsMock.when(() -> ResourceUtils.newHIResource(anyString(), any(), any(), anyString(),
					anyString(), anyString(), any(), anyBoolean())).thenReturn(createdResource);

			HIResource result = handler.importResource("parent/meta.metadata");
			Assert.assertEquals(createdResource, result);
		}
	}

	@Test
	public void ut_a6_testGetTableMapViaReflection()
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			java.lang.reflect.InvocationTargetException {
		MetadataImportHandler handler = new MetadataImportHandler();
		HIMetadataTables table = mock(HIMetadataTables.class);
		when(table.getTableName()).thenReturn("table1");

		Method method = MetadataImportHandler.class.getDeclaredMethod("getTableMap", List.class);
		method.setAccessible(true);

		@SuppressWarnings("unchecked")
		Map<String, HIMetadataTables> result = (Map<String, HIMetadataTables>) method.invoke(handler,
				Arrays.asList(table));

		Assert.assertEquals(table, result.get("table1"));
	}

	private void inject(MetadataImportHandler handler, ImportManagerContext context, HIResourceServiceDB serviceDb,
			ResourceDataReader fileReader, ResourceDTOMapper mapper, ManifestUtils manifestUtils,
			ShareHandler shareHandler)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		setField(handler, "context", context);
		setField(handler, "serviceDb", serviceDb);
		setField(handler, "fileReader", fileReader);
		setField(handler, "mapper", mapper);
		setField(handler, "manifestUtils", manifestUtils);
		setField(handler, "shareHandler", shareHandler);
	}

	protected void setField(Object target, String name, Object value)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> clazz = target.getClass();
		Field field = null;
		while (clazz != null) {
			try {
				field = clazz.getDeclaredField(name);
				break;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		if (field == null) {
			field = AbstractResourceImportHandler.class.getDeclaredField(name);
		}
		field.setAccessible(true);
		field.set(target, value);
	}

}
