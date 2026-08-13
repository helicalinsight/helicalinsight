package com.helicalinsight.export.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dto.UserDTO;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.export.utils.ResourceShareUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceHandlerTest extends ExportUnitTestBase {

	private static class TestDatasourceHandler extends DatasourceHandler {
		
		@Override
		public void write(HIResourceDTO resource, String dir, Manifest manifest) {
		
		}
		
		@Override
		public void importResource(HIResource resource, String fileName, String onConflict) {
		
		}
	}
	
	private TestDatasourceHandler createHandler(UserService userService, ResourceShareUtils shareUtils,
			ResourceDTOMapper dtoMapper) throws Exception {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		setField(handler, "userService", userService);
		setField(handler, "shareUtils", shareUtils);
		setField(handler, "dtoMapper", dtoMapper);
		return handler;
	}


	@Test
	public void ut_a1_testImportResourceHCRDefaultReturnsNull() {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		List<String> mappings = handler.importResourceHCR("datasource.json", "update");
		Assert.assertNull(mappings);
	}

	@Test
	public void ut_a2_testImportResourceHCRDefaultWithSkipConflict() {
		TestDatasourceHandler handler = new TestDatasourceHandler();
		List<String> mappings = handler.importResourceHCR("efwd_datasource.json", "skip");
		Assert.assertNull(mappings);
	}
	
}
