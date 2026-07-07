package com.helicalinsight.adhoc.copypaste;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HiResourceHreportCopyHandlerTest {

	@Test
	public void ut_a1_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceHreportCopyHandler hreportCopyHandler = spy(new HiResourceHreportCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hreportCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hreportCopyHandler).getSourcePath();

		Field field = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hreportCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hreportCopyHandler, hiResourceServiceDB);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(1);
		when(hiResource.getDeleted()).thenReturn(true);

		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiResourceHReport);
				hreportCopyHandler.copyResource();
			}
		}

	}
	
	
	@Test
	public void ut_a2_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceHreportCopyHandler hreportCopyHandler = spy(new HiResourceHreportCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hreportCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hreportCopyHandler).getSourcePath();

		Field field = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hreportCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hreportCopyHandler, hiResourceServiceDB);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(true);
		when(hiResourceServiceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(hiResource);
		
		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiResourceHReport);
				hreportCopyHandler.copyResource();
			}
		}

	}

	
	@Test
	public void ut_a3_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceHreportCopyHandler hreportCopyHandler = spy(new HiResourceHreportCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);

		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hreportCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hreportCopyHandler).getSourcePath();

		Field field = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hreportCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hreportCopyHandler, hiResourceServiceDB);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(true);
		when(hiResourceServiceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(null);
		
		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiResourceHReport);
				hreportCopyHandler.copyResource();
			}
		}

	}

	@Test
	public void ut_a4_test_copyResource()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		HiResourceHreportCopyHandler hreportCopyHandler = spy(new HiResourceHreportCopyHandler());
		HiResourceCCPUtility hiResourceCCPUtility = mock(HiResourceCCPUtility.class);
		HIResource hiResource = mock(HIResource.class);
		HIResourceHReport hiResourceHReport = mock(HIResourceHReport.class);
		HIResourceServiceDB hiResourceServiceDB = mock(HIResourceServiceDB.class);
		ResourceType resourceType = mock(ResourceType.class);
		
		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getSource();
		doReturn(hiResource).when((HiResourceCopyHandler) hreportCopyHandler).getDestinationResourceId();
		doReturn("prefix").when((HiResourceCopyHandler) hreportCopyHandler).getPrefix();
		doReturn("source").when((HiResourceCopyHandler) hreportCopyHandler).getSourcePath();
		doReturn(false).when((HiResourceCopyHandler) hreportCopyHandler).getOnConflictSkip();

		Field field = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceCCPUtility");
		field.setAccessible(true);
		field.set(hreportCopyHandler, hiResourceCCPUtility);

		Field field2 = HiResourceHreportCopyHandler.class.getDeclaredField("hiResourceServiceDB");
		field2.setAccessible(true);
		field2.set(hreportCopyHandler, hiResourceServiceDB);

		when(hiResourceCCPUtility.prepareNewReplica(any(), any(), any(), any())).thenReturn(hiResource);

		when(hiResource.getParentId()).thenReturn(1);
		when(hiResource.getResourceId()).thenReturn(2);
		when(hiResource.getDeleted()).thenReturn(false);
		when(hiResource.getResourceURL()).thenReturn("url");
		when(hiResource.getResourceType()).thenReturn(resourceType);
		when(resourceType.getExtension()).thenReturn("hr");
		when(hiResourceServiceDB.getResourceByUrl(anyString(), anyBoolean())).thenReturn(hiResource);
		
		try (MockedStatic<HiResourceCCPUtility> mockedStatic = mockStatic(HiResourceCCPUtility.class)) {
			try (MockedStatic<AuthenticationUtils> mockedStatic2 = mockStatic(AuthenticationUtils.class)) {

				mockedStatic2.when(() -> AuthenticationUtils.getUserId()).thenReturn("11");
				mockedStatic.when(() -> HiResourceCCPUtility.prepareEntity(any(), any())).thenReturn(hiResourceHReport);
				hreportCopyHandler.copyResource();
			}
		}

	}

}
