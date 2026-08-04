//package com.helicalinsight.export.unit;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import java.lang.reflect.Field;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import com.helicalinsight.export.dto.ImportRequest;
//import com.helicalinsight.export.dto.Manifest;
//import com.helicalinsight.export.dto.ResourceOptions;
//import com.helicalinsight.export.handler.ImportManagerContext;
//import com.helicalinsight.export.handler.ResourceDataReader;
//import com.helicalinsight.export.utils.ResourceFileUtils;
//
//public class ResourceDataReaderTest {
//
//	@Test
//	public void ut_a1_testReadWithVersionZero() throws Exception {
//		ResourceDataReader reader = new ResourceDataReader();
//		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
//		Field field = ResourceDataReader.class.getDeclaredField("fileUtils");
//		field.setAccessible(true);
//		field.set(reader, fileUtils);
//
//		ImportManagerContext context = new ImportManagerContext();
//		Manifest manifest = new Manifest();
//		manifest.setVersion("0");
//		context.setManifest(manifest);
//		context.setCurrentDirectory("/tmp");
//
//		Manifest result = new Manifest();
//		when(fileUtils.readFile("/tmp/resources/file.json", Manifest.class)).thenReturn(result);
//		Assert.assertEquals(result, reader.read(context, "folder/file.json", Manifest.class));
//	}
//
//	@Test
//	public void ut_a2_testReadWithVersionGreaterThanZero() throws Exception {
//		ResourceDataReader reader = new ResourceDataReader();
//		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);
//		Field field = ResourceDataReader.class.getDeclaredField("fileUtils");
//		field.setAccessible(true);
//		field.set(reader, fileUtils);
//
//		ImportManagerContext context = new ImportManagerContext();
//		Manifest manifest = new Manifest();
//		manifest.setVersion("1");
//		context.setManifest(manifest);
//		context.setCurrentDirectory("/tmp");
//		
//		ResourceOptions options = mock(ResourceOptions.class);
//		ImportRequest request = mock(ImportRequest.class);
//		when(context.getRequest()).thenReturn(request);
////		when(context.getRequest().getOptions()).thenReturn(options);
//		when(context.getRequest().getDestination()).thenReturn("Destination");
//
//		Manifest result = new Manifest();
//		when(fileUtils.readFile("/tmp/resources/folder/file.json", Manifest.class)).thenReturn(result);
//		Assert.assertEquals(result, reader.read(context, "folder/file.json", Manifest.class));
//	}
//
//}