package com.helicalinsight.export.unit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.utils.ResourceFileUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AbstractResourceImportHandlerTest extends ExportUnitTestBase {

	private static class TestImportHandler extends AbstractResourceImportHandler {
		@Override
		public HIResource importResource(String fileUrl) {
			return null;
		}
	}

	@Test
	public void ut_a1_testSetContext()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestImportHandler handler = new TestImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);

		Field fileUtilsField = AbstractResourceImportHandler.class.getDeclaredField("fileUtils");
		fileUtilsField.setAccessible(true);
		fileUtilsField.set(handler, fileUtils);

		when(context.getRequest()).thenReturn(request);

		AbstractResourceImportHandler result = handler.setContext(context);

		Assert.assertSame(handler, result);

		Field contextField = AbstractResourceImportHandler.class.getDeclaredField("context");
		contextField.setAccessible(true);
		Assert.assertEquals(context, contextField.get(handler));
		verify(fileUtils).setRequest(request);
	}

	@Test
	public void ut_a2_testSetContextReturnsSameInstance()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		TestImportHandler handler = new TestImportHandler();
		ImportManagerContext context = mock(ImportManagerContext.class);
		ImportRequest request = mock(ImportRequest.class);
		ResourceFileUtils fileUtils = mock(ResourceFileUtils.class);

		Field fileUtilsField = AbstractResourceImportHandler.class.getDeclaredField("fileUtils");
		fileUtilsField.setAccessible(true);
		fileUtilsField.set(handler, fileUtils);
		when(context.getRequest()).thenReturn(request);

		Assert.assertSame(handler, handler.setContext(context));
	}

}
