package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceEFW;
import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.ResourceDataReader;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.EfwImportHandler;
import com.helicalinsight.export.service.ShareHandler;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EfwImportHandlerTest {

	@Test
	public void ut_a1_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(true);
		when(request.getOnConflict()).thenReturn("update");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(content);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		HIResource importResource = efwImportHandler.importResource("efw/url");
		Assert.assertEquals(resource, importResource);
	}

	@Test
	public void ut_a2_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		
		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(true);
		when(request.getOnConflict()).thenReturn("update");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(content);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		when(efwResource.getCreatedBy()).thenReturn(null);

		HIResource importResource = efwImportHandler.importResource("efw/url");
		Assert.assertEquals(resource, importResource);

	}
	
	@Test
	public void ut_a3_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(false);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(true);
		when(request.getOnConflict()).thenReturn("update");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(content);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		when(efwResource.getCreatedBy()).thenReturn(null);

		HIResource importResource = efwImportHandler.importResource("efw/url");
		Assert.assertEquals(resource, importResource);

	}

	
	@Test
	public void ut_a4_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		
		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(true);
		when(request.getOnConflict()).thenReturn("notupdate");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(resource);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(content);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		when(efwResource.getCreatedBy()).thenReturn(null);

		HIResource importResource = efwImportHandler.importResource("efw/url");
		Assert.assertEquals(resource, importResource);

	}

	@Test
	public void ut_a5_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		
		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		Map<String,HIResource> map = new HashMap<>();
		map.put("efw.efwfolder", resource);
		
		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(context.getResourceUrlMap()).thenReturn(map);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(true);
		when(request.getOnConflict()).thenReturn("notupdate");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(null);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		when(efwResource.getCreatedBy()).thenReturn(null);

		
		
		try(MockedStatic<ResourceUtils> mockedStatic = mockStatic(ResourceUtils.class)){
			mockedStatic.when(() -> ResourceUtils.newHIResource("efw",null,null,"efw/url","url",null,0,true)).thenReturn(resource);
			HIResource importResource = efwImportHandler.importResource("efw/url");
			Assert.assertEquals(resource, importResource);
		}

	}

	@Test
	public void ut_a6_testImportResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		EfwImportHandler efwImportHandler = new EfwImportHandler();

		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		HIResourceServiceDB serviceDb = mock(HIResourceServiceDB.class);
		HIResource resource = mock(HIResource.class);
		HIResourceEFW efwResource = mock(HIResourceEFW.class);
		ResourceOptions resourceOptions = mock(ResourceOptions.class);
		ResourceEfwContents content = mock(ResourceEfwContents.class);
		ShareHandler shareHandler = mock(ShareHandler.class);
		ResourceDataReader fileReader = mock(ResourceDataReader.class);

		Field field = AbstractResourceImportHandler.class.getDeclaredField("context");
		field.setAccessible(true);
		field.set(efwImportHandler, context);

		Field field1 = AbstractResourceImportHandler.class.getDeclaredField("serviceDb");
		field1.setAccessible(true);
		field1.set(efwImportHandler, serviceDb);

		Field field2 = AbstractResourceImportHandler.class.getDeclaredField("fileReader");
		field2.setAccessible(true);
		field2.set(efwImportHandler, fileReader);

		Field field3 = AbstractResourceImportHandler.class.getDeclaredField("shareHandler");
		field3.setAccessible(true);
		field3.set(efwImportHandler, shareHandler);

		Map<String,HIResource> map = new HashMap<>();
		map.put("efw.efwfolder", resource);
		
		when(context.getResourcesDirectory()).thenReturn("dir");
		when(context.getRequest()).thenReturn(request);
		when(context.recover(any(HIResource.class))).thenReturn(true);
		when(context.getResourceUrlMap()).thenReturn(map);
		when(request.getOptions()).thenReturn(resourceOptions);
		when(resourceOptions.getShare()).thenReturn(false);
		when(request.getOnConflict()).thenReturn("notupdate");

		when(serviceDb.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		when(fileReader.read(context,"efw/url", HIResourceEFW.class)).thenReturn(efwResource);
		when(fileReader.read(context,"efw/url_content", ResourceEfwContents.class)).thenReturn(content);
		when(content.getFileName()).thenReturn("fileContent");
		when(serviceDb.getHIResourceEfwContents(anyString())).thenReturn(null);
		when(resource.getHiResourceEFW()).thenReturn(efwResource);
		when(efwResource.getCreatedBy()).thenReturn(null);

		
		
		try(MockedStatic<ResourceUtils> mockedStatic = mockStatic(ResourceUtils.class)){
			mockedStatic.when(() -> ResourceUtils.newHIResource("efw",null,null,"efw/url","url",null,0,true)).thenReturn(resource);
			HIResource importResource = efwImportHandler.importResource("efw/url");
			Assert.assertEquals(resource, importResource);
		}

	}


}
