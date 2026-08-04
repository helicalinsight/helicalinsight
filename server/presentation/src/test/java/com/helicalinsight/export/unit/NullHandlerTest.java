package com.helicalinsight.export.unit;


import static org.mockito.Mockito.mock;

import org.junit.Test;

public class NullHandlerTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testWriteDoesNothing() {
		com.helicalinsight.export.handler.NullHandler handler = new com.helicalinsight.export.handler.NullHandler();
		handler.write(mock(com.helicalinsight.resourcedb.HIResourceDTO.class), "dir",
				new com.helicalinsight.export.dto.Manifest(),
				new com.helicalinsight.export.dto.ResourceOptions());
	}

}