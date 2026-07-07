package com.helicalinsight.unit.export;
import org.junit.Assert;
import org.junit.Test;
import com.helicalinsight.efw.utility.ReportsUtility;


public class ReportNameSpecialCharacterTest {

	@Test
	public void a1_checkReportName() {
		String name ="3/name";
		String returned = ReportsUtility.getReportName(name);
		Assert.assertTrue(returned.contains("3_name"));
	}
}
