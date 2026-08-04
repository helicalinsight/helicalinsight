package com.helicalinsight.export.unit;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

public class ResourceWriterHandlerFactoryTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testGetHandlerFound() {
		com.helicalinsight.export.handler.AbstractResourceWriterHandler handler = mock(
				com.helicalinsight.export.handler.AbstractResourceWriterHandler.class);
		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean("folderWriterHandler")).thenReturn(handler);
			Assert.assertEquals(handler,
					com.helicalinsight.export.handler.ResourceWriterHandlerFactory.getHandler("folder"));
		}
	}

	@Test
	public void ut_a2_testGetHandlerFallbackToNullHandler() {
		com.helicalinsight.export.handler.NullHandler nullHandler = new com.helicalinsight.export.handler.NullHandler();
		try (MockedStatic<ApplicationContextAccessor> mocked = mockStatic(ApplicationContextAccessor.class)) {
			mocked.when(() -> ApplicationContextAccessor.getBean("unknownWriterHandler"))
					.thenThrow(new NoSuchBeanDefinitionException("not found"));
			mocked.when(() -> ApplicationContextAccessor.getBean(com.helicalinsight.export.handler.NullHandler.class))
					.thenReturn(nullHandler);
			Assert.assertEquals(nullHandler,
					com.helicalinsight.export.handler.ResourceWriterHandlerFactory.getHandler("unknown"));
		}
	}

}
