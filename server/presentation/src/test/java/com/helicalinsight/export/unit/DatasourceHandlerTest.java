package com.helicalinsight.export.unit;

import java.util.List;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatasourceHandlerTest extends ExportUnitTestBase {

	private static class TestDatasourceHandler extends DatasourceHandler {

		@Override
		public void write(HIResourceDTO resource, String dir, Manifest manifest) {
			// no-op for default-method test
		}

		@Override
		public void importResource(HIResource resource, String fileName, String onConflict) {
			// no-op for default-method test
		}
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
