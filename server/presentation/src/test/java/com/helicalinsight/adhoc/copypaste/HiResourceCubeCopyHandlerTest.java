package com.helicalinsight.adhoc.copypaste;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HiResourceCubeCopyHandlerTest {

	@Test
	public void ut_a1_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceCubeCopyHandler hiResourceCubeCopyHandler = spy(new HiResourceCubeCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDimension hiCubeDimension = mock(HICubeDimension.class);
		HIDimensionHierarchy hiDimensionHierarchy = mock(HIDimensionHierarchy.class);
		HICubeHierarchyLevel hiCubeHierarchyLevel = mock(HICubeHierarchyLevel.class);
		HICubeMeasure hiCubeMeasure = mock(HICubeMeasure.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSourcePath();

		Field field = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hiResourceCubeCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hiResourceCubeCopyHandler, hiResourceServiceDB);

		Field field3 = HiResourceCubeCopyHandler.class.getDeclaredField("hiCubeDAOService");
		field3.setAccessible(true);
		field3.set(hiResourceCubeCopyHandler, hiCubeDAOService);

		List<HIMetadataCube> existingCubes = new ArrayList<>();
		existingCubes.add(hiMetadataCube);
		List<HICubeDimension> existingCubeDimensions = new ArrayList<>();
		existingCubeDimensions.add(hiCubeDimension);
		List<HIDimensionHierarchy> existingCubeDimHierarchy = new ArrayList<>();
		existingCubeDimHierarchy.add(hiDimensionHierarchy);
		List<HICubeHierarchyLevel> existinCubHirLevels = new ArrayList<>();
		existinCubHirLevels.add(hiCubeHierarchyLevel);
		List<HICubeMeasure> existingCubeMeasures = new ArrayList<>();
		existingCubeMeasures.add(hiCubeMeasure);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(1);
		when(hiResource.getDeleted()).thenReturn(true);
		when(hiCubeDAOService.findAllCubeWithResourceId(anyInt())).thenReturn(existingCubes);
		when(hiCubeDAOService.findAllCubeDimentions(anyInt())).thenReturn(existingCubeDimensions);
		when(hiCubeDAOService.findAllDimHierarchy(anyInt())).thenReturn(existingCubeDimHierarchy);
		when(hiCubeDAOService.findAllHierarchyLevels(anyInt())).thenReturn(existinCubHirLevels);
		when(hiCubeDAOService.findAllCubeMeasuresByCubeId(anyInt())).thenReturn(existingCubeMeasures);

		when(hiMetadataCube.getId()).thenReturn(2);
		when(hiCubeDimension.getId()).thenReturn(3);
		when(hiDimensionHierarchy.getId()).thenReturn(4);
		when(hiCubeHierarchyLevel.getId()).thenReturn(5);

		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiMetadataCube)
						.thenReturn(hiCubeDimension).thenReturn(hiDimensionHierarchy).thenReturn(hiCubeHierarchyLevel)
						.thenReturn(hiCubeMeasure);
				hiResourceCubeCopyHandler.copyResource();
			}
		}

	}

	@Test
	public void ut_a2_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceCubeCopyHandler hiResourceCubeCopyHandler = spy(new HiResourceCubeCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDimension hiCubeDimension = mock(HICubeDimension.class);
		HIDimensionHierarchy hiDimensionHierarchy = mock(HIDimensionHierarchy.class);
		HICubeHierarchyLevel hiCubeHierarchyLevel = mock(HICubeHierarchyLevel.class);
		HICubeMeasure hiCubeMeasure = mock(HICubeMeasure.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSourcePath();

		Field field = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hiResourceCubeCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hiResourceCubeCopyHandler, hiResourceServiceDB);

		Field field3 = HiResourceCubeCopyHandler.class.getDeclaredField("hiCubeDAOService");
		field3.setAccessible(true);
		field3.set(hiResourceCubeCopyHandler, hiCubeDAOService);

		List<HIMetadataCube> existingCubes = new ArrayList<>();
		existingCubes.add(hiMetadataCube);
		List<HICubeDimension> existingCubeDimensions = new ArrayList<>();
		existingCubeDimensions.add(hiCubeDimension);
		List<HIDimensionHierarchy> existingCubeDimHierarchy = new ArrayList<>();
		existingCubeDimHierarchy.add(hiDimensionHierarchy);
		List<HICubeHierarchyLevel> existinCubHirLevels = new ArrayList<>();
		existinCubHirLevels.add(hiCubeHierarchyLevel);
		List<HICubeMeasure> existingCubeMeasures = new ArrayList<>();
		existingCubeMeasures.add(hiCubeMeasure);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(false);
		when(hiCubeDAOService.findAllCubeWithResourceId(anyInt())).thenReturn(existingCubes);
		when(hiCubeDAOService.findAllCubeDimentions(anyInt())).thenReturn(existingCubeDimensions);
		when(hiCubeDAOService.findAllDimHierarchy(anyInt())).thenReturn(existingCubeDimHierarchy);
		when(hiCubeDAOService.findAllHierarchyLevels(anyInt())).thenReturn(existinCubHirLevels);
		when(hiCubeDAOService.findAllCubeMeasuresByCubeId(anyInt())).thenReturn(existingCubeMeasures);

		when(hiMetadataCube.getId()).thenReturn(2);
		when(hiCubeDimension.getId()).thenReturn(3);
		when(hiDimensionHierarchy.getId()).thenReturn(4);
		when(hiCubeHierarchyLevel.getId()).thenReturn(5);

		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiMetadataCube)
						.thenReturn(hiCubeDimension).thenReturn(hiDimensionHierarchy).thenReturn(hiCubeHierarchyLevel)
						.thenReturn(hiCubeMeasure);
				hiResourceCubeCopyHandler.copyResource();
			}
		}

	}

	@Test
	public void ut_a3_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceCubeCopyHandler hiResourceCubeCopyHandler = spy(new HiResourceCubeCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDimension hiCubeDimension = mock(HICubeDimension.class);
		HIDimensionHierarchy hiDimensionHierarchy = mock(HIDimensionHierarchy.class);
		HICubeHierarchyLevel hiCubeHierarchyLevel = mock(HICubeHierarchyLevel.class);
		HICubeMeasure hiCubeMeasure = mock(HICubeMeasure.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSourcePath();

		Field field = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hiResourceCubeCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hiResourceCubeCopyHandler, hiResourceServiceDB);

		Field field3 = HiResourceCubeCopyHandler.class.getDeclaredField("hiCubeDAOService");
		field3.setAccessible(true);
		field3.set(hiResourceCubeCopyHandler, hiCubeDAOService);

		List<HIMetadataCube> existingCubes = new ArrayList<>();
		existingCubes.add(hiMetadataCube);
		List<HICubeDimension> existingCubeDimensions = new ArrayList<>();
		existingCubeDimensions.add(hiCubeDimension);
		List<HIDimensionHierarchy> existingCubeDimHierarchy = new ArrayList<>();
		existingCubeDimHierarchy.add(hiDimensionHierarchy);
		List<HICubeHierarchyLevel> existinCubHirLevels = new ArrayList<>();
		existinCubHirLevels.add(hiCubeHierarchyLevel);
		List<HICubeMeasure> existingCubeMeasures = new ArrayList<>();
		existingCubeMeasures.add(hiCubeMeasure);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(true);
		when(hiCubeDAOService.findAllCubeWithResourceId(anyInt())).thenReturn(existingCubes);
		when(hiCubeDAOService.findAllCubeDimentions(anyInt())).thenReturn(existingCubeDimensions);
		when(hiCubeDAOService.findAllDimHierarchy(anyInt())).thenReturn(existingCubeDimHierarchy);
		when(hiCubeDAOService.findAllHierarchyLevels(anyInt())).thenReturn(existinCubHirLevels);
		when(hiCubeDAOService.findAllCubeMeasuresByCubeId(anyInt())).thenReturn(existingCubeMeasures);
		when(hiResourceServiceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(hiResource);

		when(hiMetadataCube.getId()).thenReturn(2);
		when(hiCubeDimension.getId()).thenReturn(3);
		when(hiDimensionHierarchy.getId()).thenReturn(4);
		when(hiCubeHierarchyLevel.getId()).thenReturn(5);

		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiMetadataCube)
						.thenReturn(hiCubeDimension).thenReturn(hiDimensionHierarchy).thenReturn(hiCubeHierarchyLevel)
						.thenReturn(hiCubeMeasure);
				hiResourceCubeCopyHandler.copyResource();
			}
		}

	}

	@Test
	public void ut_a4_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceCubeCopyHandler hiResourceCubeCopyHandler = spy(new HiResourceCubeCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);
		HICubeDAOService hiCubeDAOService = mock(HICubeDAOService.class);
		HIMetadataCube hiMetadataCube = mock(HIMetadataCube.class);
		HICubeDimension hiCubeDimension = mock(HICubeDimension.class);
		HIDimensionHierarchy hiDimensionHierarchy = mock(HIDimensionHierarchy.class);
		HICubeHierarchyLevel hiCubeHierarchyLevel = mock(HICubeHierarchyLevel.class);
		HICubeMeasure hiCubeMeasure = mock(HICubeMeasure.class);
		ResourceType resourceType = mock(ResourceType.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getSourcePath();
		doReturn(false).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getOnConflictSkip();
		doReturn(hiResource).when((HiResourceCopyHandler) hiResourceCubeCopyHandler).getDestinationResourceId();

		Field field = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hiResourceCubeCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceCubeCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hiResourceCubeCopyHandler, hiResourceServiceDB);

		Field field3 = HiResourceCubeCopyHandler.class.getDeclaredField("hiCubeDAOService");
		field3.setAccessible(true);
		field3.set(hiResourceCubeCopyHandler, hiCubeDAOService);

		List<HIMetadataCube> existingCubes = new ArrayList<>();
		existingCubes.add(hiMetadataCube);
		List<HICubeDimension> existingCubeDimensions = new ArrayList<>();
		existingCubeDimensions.add(hiCubeDimension);
		List<HIDimensionHierarchy> existingCubeDimHierarchy = new ArrayList<>();
		existingCubeDimHierarchy.add(hiDimensionHierarchy);
		List<HICubeHierarchyLevel> existinCubHirLevels = new ArrayList<>();
		existinCubHirLevels.add(hiCubeHierarchyLevel);
		List<HICubeMeasure> existingCubeMeasures = new ArrayList<>();
		existingCubeMeasures.add(hiCubeMeasure);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(false);

		when(hiResource.getResourceURL()).thenReturn("url");
		when(hiResource.getResourceType()).thenReturn(resourceType);
		when(resourceType.getExtension()).thenReturn("cube");

		when(hiCubeDAOService.findAllCubeWithResourceId(anyInt())).thenReturn(existingCubes);
		when(hiCubeDAOService.findAllCubeDimentions(anyInt())).thenReturn(existingCubeDimensions);
		when(hiCubeDAOService.findAllDimHierarchy(anyInt())).thenReturn(existingCubeDimHierarchy);
		when(hiCubeDAOService.findAllHierarchyLevels(anyInt())).thenReturn(existinCubHirLevels);
		when(hiCubeDAOService.findAllCubeMeasuresByCubeId(anyInt())).thenReturn(existingCubeMeasures);
		when(hiResourceServiceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(hiResource);

		when(hiMetadataCube.getId()).thenReturn(2);
		when(hiCubeDimension.getId()).thenReturn(3);
		when(hiDimensionHierarchy.getId()).thenReturn(4);
		when(hiCubeHierarchyLevel.getId()).thenReturn(5);

		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiMetadataCube)
						.thenReturn(hiCubeDimension).thenReturn(hiDimensionHierarchy).thenReturn(hiCubeHierarchyLevel)
						.thenReturn(hiCubeMeasure);
				hiResourceCubeCopyHandler.copyResource();
			}
		}

	}

}
