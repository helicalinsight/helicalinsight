package com.helicalinsight.export.download.producer;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.export.components.DownloadCacheManager;
import com.helicalinsight.export.download.CsvTempMaterializer;
import com.helicalinsight.export.download.DownloadArtifact;
import com.helicalinsight.export.download.DownloadRouting;
import com.helicalinsight.efw.utility.ReportsUtility;

/**
 * Materialize HCR / adhoc CSV-XLS via {@link DownloadCacheManager#materializeToFile()},
 * which goes through CacheHelper (cache hit/miss — same as CacheFilter / scheduler prepare).
 */
public final class DownloadCacheProducer implements DownloadArtifactProducer {

	private final String dataJson;
	private final String reportParameters;
	private final DownloadCacheManager cacheManager;
	private final boolean refresh;

	public DownloadCacheProducer(String dataJson, String reportParameters, DownloadCacheManager cacheManager) {
		this(dataJson, reportParameters, cacheManager, false);
	}

	public DownloadCacheProducer(String dataJson, String reportParameters, DownloadCacheManager cacheManager,
			boolean refresh) {
		this.dataJson = dataJson;
		this.reportParameters = reportParameters;
		this.cacheManager = cacheManager;
		this.refresh = refresh;
	}

	@Override
	public DownloadArtifact produce() throws Exception {
		JsonObject form = JsonParser.parseString(dataJson).getAsJsonObject();
		JsonElement params = parseReportParameters(reportParameters);
		if (params != null) {
			form.add("reportParameters", params);
		}
		String data = form.toString();

		if (DownloadRouting.shouldUseCacheManager(form)) {
			cacheManager.setRequestData(data);
			boolean hiRefresh = refresh || GsonUtility.optBooleanValue(form, "hi_refresh", false);
			String reportName = ReportsUtility.resolveCacheReportKey(form);
			File file = cacheManager.materializeToFile(reportName, hiRefresh);
			Set<Path> tracked = new LinkedHashSet<>();
			tracked.add(file.toPath());
			return new DownloadArtifact(
					file,
					cacheManager.resolveAttachmentName(),
					cacheManager.resolveContentType(),
					tracked);
		}

		String reportName = GsonUtility.optString(form, "reportName");
		return CsvTempMaterializer.writeArtifact(
				data,
				reportName == null || reportName.isEmpty() ? null : reportName);
	}

	private static JsonElement parseReportParameters(String reportParameters) {
		if (reportParameters == null || reportParameters.isEmpty()) {
			return null;
		}
		try {
			return JsonParser.parseString(reportParameters);
		} catch (Exception ignored) {
			try {
				String decoded = new String(Base64.getDecoder().decode(reportParameters), StandardCharsets.UTF_8);
				return JsonParser.parseString(decoded);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
