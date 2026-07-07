package com.helicalinsight.export;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;

public class DatasourceFactoryTest {

	@Test
	public void ut_a1_testGetHandler() {

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean("efwdDSHandler"))
					.thenThrow(new NoSuchBeanDefinitionException("exception check"));
			DatasourceHandler handler = DatasourceFactory.getHandler("folder");
			Assert.assertNull(handler);
		}
	}
	@Test
	public void ut_a2_testGetHandler() {
		DatasourceHandler datasourceHandler = mock(DatasourceHandler.class);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean("efwdDSHandler"))
					.thenReturn(datasourceHandler);
			DatasourceHandler handler = DatasourceFactory.getHandler("sql.jdbc");
			Assert.assertEquals(datasourceHandler,handler);
		}
	}
	@Test
	public void ut_a3_testGetHandler() {
		DatasourceHandler datasourceHandler = mock(DatasourceHandler.class);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean("efwdDSHandler"))
					.thenReturn(datasourceHandler);
			DatasourceHandler handler = DatasourceFactory.getHandler("sql.jdbc.groovy");
			Assert.assertEquals(datasourceHandler,handler);
		}
	}
	@Test
	public void ut_a4_testGetHandler() {
		DatasourceHandler datasourceHandler = mock(DatasourceHandler.class);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean("efwdDSHandler"))
						.thenReturn(datasourceHandler);
			DatasourceHandler handler = DatasourceFactory.getHandler("sql.jdbc.groovy.managed");
			Assert.assertEquals(datasourceHandler,handler);
		}
	}
	@Test
	public void ut_a5_testGetHandler() {
		DatasourceHandler datasourceHandler = mock(DatasourceHandler.class);
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			mockedStatic.when(() -> ApplicationContextAccessor.getBean("globalDSHandler"))
						.thenReturn(datasourceHandler);
			DatasourceHandler handler = DatasourceFactory.getHandler("global.jdbc");
			Assert.assertEquals(datasourceHandler,handler);
		}
	}
}
