package com.helicalinsight.export.unit;


import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.utils.ResourceSuffix;

public class ResourceSuffixTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testConstants() {
		Assert.assertEquals(".efwfolder", ResourceSuffix.FOLDER);
		Assert.assertEquals("_datasource", ResourceSuffix.DATASOURCE);
		Assert.assertEquals("_efwd_datasource", ResourceSuffix.DATASOURCE_EFWD);
		Assert.assertEquals("_share", ResourceSuffix.SHARE);
		Assert.assertEquals("_schedule", ResourceSuffix.SCHEDULE);
		Assert.assertEquals(".efwsr", ResourceSuffix.SCHEDULE_RESULT);
		Assert.assertEquals("_content", ResourceSuffix.EFW_CONTENT);
	}

}