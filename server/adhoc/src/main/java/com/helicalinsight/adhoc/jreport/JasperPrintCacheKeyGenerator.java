package com.helicalinsight.adhoc.jreport;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.SplitterUtils;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.design.JasperDesign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JasperPrintCacheKeyGenerator {
	
	private static final Logger logger = LoggerFactory.getLogger(JasperPrintCacheKeyGenerator.class);

	private JasperPrintCacheKeyGenerator() {
		
	}

	public static String generate(JasperDesign jasperDesign, JsonObject formData) {

		List<String> datasetSignatures = new ArrayList<>();

		datasetSignatures.add(datasetSignature("MAIN", jasperDesign.getMainDataset(), formData));

		for (JRDataset ds : jasperDesign.getDatasets()) {
			datasetSignatures.add(datasetSignature(ds.getName(), ds, formData));
		}

		Collections.sort(datasetSignatures);

		StringBuilder canonical = new StringBuilder("JASPER_PRINT|");
		canonical.append(String.join("||", datasetSignatures));
		canonical.append("||" ).append( designerPropertySignature(formData));
		
		boolean isExport = formData.has("isExport") && formData.get("isExport").getAsBoolean();

		canonical.append("|mode=").append(isExport ? "EXPORT" : "PREVIEW");
		
		logger.info("The final Canonical for Jasper print cache : {}", canonical);
		
		return SplitterUtils.prepareServiceId(canonical.toString());
	}
	
	
	private static String designerPropertySignature(JsonObject formData) {
		JsonObject designer = formData.getAsJsonObject("designerProperties");
		if (designer == null) {
			return "";
		}
		return SplitterUtils.prepareServiceId(designer.toString());
	}

	private static String datasetSignature(String name, JRDataset dataset, JsonObject formData) {
		String query = "";
		if (dataset.isMainDataset()) {
			query = GsonUtility.optStringValue(formData, "mainDataKey", "");
		}
		else {
			query = extractQuery(dataset);
		}
		String tempUuid = extractTempUuid(name, formData);
		String raw = "ds=" + name + "|q=" + normalize(query) + "|temp=" + tempUuid;
		
		logger.info("Dataset Raw Query : {}" ,  raw);
		
		return SplitterUtils.prepareServiceId(raw);
	}

	private static String extractQuery(JRDataset dataset) {
		JRQuery query = dataset.getQuery();
		return query != null ? query.getText() : "";
	}

	private static String extractTempUuid(String datasetName, JsonObject formData) {

		JsonObject designer = formData.getAsJsonObject("designerProperties");
		
		if (designer == null) {
			return "NO_TEMP_UUID";
		}

		JsonArray dataSets = designer.getAsJsonArray("dataSets");
		if (dataSets != null) {
			for (JsonElement el : dataSets) {
				JsonObject ds = el.getAsJsonObject();
				if (datasetName.equals(ds.get("name").getAsString())) {
					return getTempUuid(ds.getAsJsonObject("connectionDetails"));
				}
			}
		}

		JsonArray parameters = designer.getAsJsonArray("parameters");
		if (parameters != null) {
			for (JsonElement el : parameters) {
				JsonObject p = el.getAsJsonObject();
				if (datasetName.equals(p.get("name").getAsString())) {
					return getTempUuid(p.getAsJsonObject("connectionDetails"));
				}
			}
		}
		
		

		return "NO_TEMP_UUID";
	}

	private static String getTempUuid(JsonObject conn) {
		if (conn == null) {
			return "NO_CONN";
		}
		return conn.has("temp_uuid") ? conn.get("temp_uuid").getAsString() : "NO_TEMP_UUID";
	}

	private static String normalize(String query) {  //TODO :  sanitize
		if (query == null) {
			return "";
		}
		return query.replaceAll("\\s+", " ").trim().toLowerCase();
	}
}
