package com.helicalinsight.export;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.handler.importres.AbstractResourceImportHandler;
import com.helicalinsight.export.handler.importres.ImportHandlerFactory;

public class ImportHandlerFactoryTest {

	@Test
	public void ut_a1_testGetHandler() {
		AbstractResourceImportHandler importHandler = mock(AbstractResourceImportHandler.class);
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).
			thenReturn(importHandler);
			AbstractResourceImportHandler handler = ImportHandlerFactory.getHandler("extension");
			Assert.assertEquals(importHandler,handler);
		}	
	}
	@Test
	public void ut_a2_testGetHandler() {
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(() -> ApplicationContextAccessor.getBean(anyString())).
			thenThrow(new NoSuchBeanDefinitionException("no bean found"));
			AbstractResourceImportHandler handler = ImportHandlerFactory.getHandler("extension");
			Assert.assertNull(handler);
			
		}
	}
	
}
