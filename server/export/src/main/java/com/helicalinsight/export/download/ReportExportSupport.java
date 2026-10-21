package com.helicalinsight.export.download;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.export.ExportUtils;
import com.helicalinsight.export.ReportsProcessor;
import com.helicalinsight.export.ServerSideExport;

/**
 * Shared report file generation for sync ExportController and ReportDownloadProducer.
 */
public final class ReportExportSupport {

	private ReportExportSupport() {
	}

	public static List<String> generateLocations(
			String html,
			String format,
			String reportName,
			String reportParameters,
			String reportType,
			String dir,
			String reportFile,
			JsonObject printOptionsJson) {

		String updatedParams = ExportUtils.setPrintOptionsAndDiscardFromReportParameters(
				reportParameters, printOptionsJson);

		if (html == null || html.isEmpty()) {
			return new ServerSideExport(
					format, reportName, updatedParams, reportType, dir, reportFile, printOptionsJson)
					.listOfLocations();
		}
		return new ReportsProcessor().generateReportUsingHTMLSource(html, format, reportName);
	}

	public static JsonObject buildPrintOptionsJson(String format, JsonArray cookies) {
		JsonObject printOptionsJson = new JsonObject();
		printOptionsJson.add("format", JsonParser.parseString("['" + format + "']").getAsJsonArray());
		if (cookies != null) {
			printOptionsJson.add("cookie", cookies);
		}
		return printOptionsJson;
	}
}
