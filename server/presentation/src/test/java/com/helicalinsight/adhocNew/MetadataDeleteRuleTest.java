package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.adhoc.MetadataDeleteRule;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataDeleteRuleTest {

	@Test
	public void ut_a1_test_isDeletable() {
		MetadataDeleteRule deleteRule = (MetadataDeleteRule) MetadataDeleteRule.getInstance();
		boolean deletable = deleteRule.isDeletable(new File("path"));
		assertTrue(deletable);
	}

	@Test
	public void ut_a2_test_delete() {
		MetadataDeleteRule deleteRule = (MetadataDeleteRule) MetadataDeleteRule.getInstance();
		File file = mock(File.class);

		when(file.getAbsolutePath()).thenReturn("absolutePath");
		IComponent deleteHandler = mock(IComponent.class);
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<FilenameUtils> mockedStatic2 = mockStatic(FilenameUtils.class)) {
				mockedStatic2.when(() -> FilenameUtils.getName(anyString())).thenReturn("metadataFileName");

				mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(
						"com.helicalinsight.adhoc.metadata.genericdb" + ".MetadataDeleteHandler", IComponent.class))
						.thenReturn(deleteHandler);

				deleteRule.delete(file);
			}
		}
	}
	
	@Test(expected = EfwException.class)
	public void ut_a3_test_delete() {
		MetadataDeleteRule deleteRule = (MetadataDeleteRule) MetadataDeleteRule.getInstance();
		File file = mock(File.class);

		when(file.getAbsolutePath()).thenReturn("absolutePath");
		
		try (MockedStatic<FactoryMethodWrapper> mockedStatic = mockStatic(FactoryMethodWrapper.class)) {
			try (MockedStatic<FilenameUtils> mockedStatic2 = mockStatic(FilenameUtils.class)) {
				mockedStatic2.when(() -> FilenameUtils.getName(anyString())).thenReturn("metadataFileName");

				mockedStatic.when(() -> FactoryMethodWrapper.getTypedInstance(
						"com.helicalinsight.adhoc.metadata.genericdb" + ".MetadataDeleteHandler", IComponent.class))
						.thenReturn(null);

				deleteRule.delete(file);
			}
		}
	}
	@Test
	public void ut_a4_test_isThreadSafeToCache() {
		MetadataDeleteRule deleteRule = (MetadataDeleteRule) MetadataDeleteRule.getInstance();
		boolean threadSafeToCache = deleteRule.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}
}
