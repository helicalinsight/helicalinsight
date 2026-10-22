package com.helicalinsight.export.download;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

/**
 * Shared routing for sync and resumable CSV/XLS/HCR downloads so behaviors cannot drift.
 */
public final class DownloadRouting {

	private DownloadRouting() {
	}

	/**
	 * Whether the payload should go through {@code DownloadCacheManager}
	 * (adhoc CSV/XLS, HCR) rather than plain {@code CSVUtility}.
	 */
	public static boolean shouldUseCacheManager(JsonObject form) {
		if (form == null) {
			return false;
		}
		if (GsonUtility.optBooleanValue(form, "isAdhoc", false)) {
			return true;
		}
		if (GsonUtility.optBooleanValue(form, "isHCR", false)) {
			return true;
		}
		String type = GsonUtility.optString(form, "type");
		return type != null && type.toLowerCase().contains("xls");
	}
}
