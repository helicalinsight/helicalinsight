package com.helicalinsight.export.download.producer;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.export.download.DownloadArtifact;
import com.helicalinsight.export.download.ReportExportSupport;

import jakarta.servlet.http.HttpServletRequest;

public class ReportDownloadProducer implements DownloadArtifactProducer {

	private final String html;
	private final String format;
	private final String reportName;
	private final String dir;
	private final String reportFile;
	private final String reportType;
	private final String reportParameters;
	private final JsonArray cookieArray;
	private final Map<String, String> contentTypes;

	public ReportDownloadProducer(String html, String format, String reportName, String dir, String reportFile,
			String reportType, String reportParameters, String printOptions, Map<String, String> contentTypes,
			HttpServletRequest request) {
		this.html = html;
		this.format = format;
		this.reportName = reportName;
		this.dir = dir;
		this.reportFile = reportFile;
		this.reportType = reportType;
		this.reportParameters = reportParameters;
		this.contentTypes = contentTypes;
		this.cookieArray = ControllerUtils.newGetCookieArray(request);
	}

	@Override
	public DownloadArtifact produce() throws Exception {
		JsonObject printOptionsJson = ReportExportSupport.buildPrintOptionsJson(format, cookieArray);
		List<String> locations = ReportExportSupport.generateLocations(
				StringUtils.isBlank(html) ? null : html,
				format,
				reportName,
				reportParameters,
				reportType,
				dir,
				reportFile,
				printOptionsJson);

		File file = new File(locations.get(0));
		String contentType = contentTypes.getOrDefault(format, "application/octet-stream");
		return new DownloadArtifact(file, reportName + "." + format, contentType, Collections.singleton(file.toPath()));
	}
}
