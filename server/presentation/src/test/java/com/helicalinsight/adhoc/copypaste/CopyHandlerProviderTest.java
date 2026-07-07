package com.helicalinsight.adhoc.copypaste;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CopyHandlerProviderTest {
	
	@Test
	public void ut_a1_test_getInstance() {
		CopyHandlerProvider copyHandlerProvider = new CopyHandlerProvider();
		HiResourceCopyHandler hiResourceCopyHandler = mock(HiResourceCopyHandler.class);
		
		try(MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)){
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceHreportCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceEfddCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceEfwsrCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceMetadataCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceCubeCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(HiResourceFolderCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			mockedStatic.when(()-> ApplicationContextAccessor.getBean(EfwdPlainConCopyHandler.class)).thenReturn(hiResourceCopyHandler);
			
			HiResourceCopyHandler hr = CopyHandlerProvider.getInstance("hr");
			HiResourceCopyHandler efwdd = CopyHandlerProvider.getInstance("efwdd");
			HiResourceCopyHandler efwsr = CopyHandlerProvider.getInstance("efwsr");
			HiResourceCopyHandler metadata = CopyHandlerProvider.getInstance("metadata");
			HiResourceCopyHandler cube = CopyHandlerProvider.getInstance("cube");
			HiResourceCopyHandler folder = CopyHandlerProvider.getInstance("folder");
			HiResourceCopyHandler efwd = CopyHandlerProvider.getInstance("efwd");
			assertEquals(hiResourceCopyHandler, hr);
			assertEquals(hiResourceCopyHandler, efwdd);
			assertEquals(hiResourceCopyHandler, efwsr);
			assertEquals(hiResourceCopyHandler, metadata);
			assertEquals(hiResourceCopyHandler, cube);
			assertEquals(hiResourceCopyHandler, folder);
			assertEquals(hiResourceCopyHandler, efwd);
			
			
		}
	}
}
