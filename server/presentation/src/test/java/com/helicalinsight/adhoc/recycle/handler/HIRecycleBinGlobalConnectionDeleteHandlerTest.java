package com.helicalinsight.adhoc.recycle.handler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.adhoc.utils.FlatFileDeleteUtils;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;

public class HIRecycleBinGlobalConnectionDeleteHandlerTest {

	@InjectMocks
	private HIRecycleBinGlobalConnectionDeleteHandler handler;

	@Mock
	private GlobalConnectionService gConnectionService;

	@Mock
	private HIRecycleBinService recycleBinService;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void handleRecycleBinDtoDeletesConnectionBinAndFlatFileFolder() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(80L);
		bin.setResourceId(701);

		when(gConnectionService.deleteGlobalConnections(701)).thenReturn(true);

		try (MockedStatic<FlatFileDeleteUtils> flatFileUtils = mockStatic(FlatFileDeleteUtils.class)) {
			assertTrue(handler.handle(bin));

			verify(recycleBinService).delete(80L);
			flatFileUtils.verify(() -> FlatFileDeleteUtils.deleteRequestedFolders("701"));
		}
	}

	@Test(expected = EfwServiceException.class)
	public void handleRecycleBinDtoThrowsWhenConnectionCannotBeDeleted() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(81L);
		bin.setResourceId(702);

		when(gConnectionService.deleteGlobalConnections(702)).thenReturn(false);

		handler.handle(bin);
	}

	@Test
	public void handleRecycleBinDtoWithMapDelegatesToSingleArgHandle() {
		RecycleBinDTO bin = new RecycleBinDTO();
		bin.setRecycleBinId(82L);
		bin.setResourceId(703);

		when(gConnectionService.deleteGlobalConnections(703)).thenReturn(true);

		Map<Long, Boolean> map = new HashMap<>();

		try (MockedStatic<FlatFileDeleteUtils> flatFileUtils = mockStatic(FlatFileDeleteUtils.class)) {
			assertTrue(handler.handle(bin, map));

			verify(recycleBinService).delete(82L);
			flatFileUtils.verify(() -> FlatFileDeleteUtils.deleteRequestedFolders("703"));
		}
	}
}
