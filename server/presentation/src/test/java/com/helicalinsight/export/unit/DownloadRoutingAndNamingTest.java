package com.helicalinsight.export.unit;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.export.download.DownloadRouting;

public class DownloadRoutingAndNamingTest {

	@Test
	public void shouldUseCacheManager_trueForAdhocBoolean() {
		JsonObject form = new JsonObject();
		form.addProperty("isAdhoc", true);
		form.addProperty("type", "csv");
		Assert.assertTrue(DownloadRouting.shouldUseCacheManager(form));
	}

	@Test
	public void shouldUseCacheManager_falseWhenIsAdhocFalse() {
		JsonObject form = new JsonObject();
		form.addProperty("isAdhoc", false);
		form.addProperty("type", "csv");
		Assert.assertFalse(DownloadRouting.shouldUseCacheManager(form));
	}

	@Test
	public void shouldUseCacheManager_trueForXlsAndHcr() {
		JsonObject xls = new JsonObject();
		xls.addProperty("type", "xlsx");
		Assert.assertTrue(DownloadRouting.shouldUseCacheManager(xls));

		JsonObject hcr = new JsonObject();
		hcr.addProperty("isHCR", true);
		Assert.assertTrue(DownloadRouting.shouldUseCacheManager(hcr));
	}

	@Test
	public void isResumableDownloadPreparePath_onlyDownloadAndExport() {
		Assert.assertTrue(ControllerUtils.isResumableDownloadPreparePath("/downloadReport"));
		Assert.assertTrue(ControllerUtils.isResumableDownloadPreparePath("/exportResource"));
		Assert.assertFalse(ControllerUtils.isResumableDownloadPreparePath("/executeDatasource"));
		Assert.assertFalse(ControllerUtils.isResumableDownloadPreparePath("/services"));
	}

	@Test
	public void getReportName_blankUsesEpochNotLeadingUnderscore() {
		String name = ReportsUtility.getReportName("");
		Assert.assertNotNull(name);
		Assert.assertFalse(name.startsWith("_"));
		Assert.assertTrue(name.matches("[0-9]+"));
	}

	@Test
	public void resolveCacheReportKey_prefersRequestedThenReportName() {
		JsonObject form = new JsonObject();
		form.addProperty("hi_requestedReport", "Req1");
		form.addProperty("reportName", "Other");
		Assert.assertEquals("Req1", ReportsUtility.resolveCacheReportKey(form));

		JsonObject onlyName = new JsonObject();
		onlyName.addProperty("reportName", "ReportA");
		Assert.assertEquals("ReportA", ReportsUtility.resolveCacheReportKey(onlyName));
	}
}
