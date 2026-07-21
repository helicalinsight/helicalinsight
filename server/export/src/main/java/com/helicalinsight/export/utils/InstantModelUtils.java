package com.helicalinsight.export.utils;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility methods for resolving and updating model references embedded in instant report state JSON.
 */
public final class InstantModelUtils {

	private InstantModelUtils() {
	}

	/**
	 * Extracts the model resource URL from instant report state JSON.
	 *
	 * @param state instant report state JSON string
	 * @return model resource URL in the form {@code dir/file.model}, or {@code null} if not present
	 */
	public static String getModelUrl(String state) {
		if (StringUtils.isBlank(state)) {
			return null;
		}
		JsonObject stateJson = JsonParser.parseString(state).getAsJsonObject();
		if (!stateJson.has("subject")) {
			return null;
		}
		JsonObject subject = stateJson.getAsJsonObject("subject");
		if (!subject.has("model")) {
			return null;
		}
		JsonObject model = subject.getAsJsonObject("model");
		if (!model.has("dir") || !model.has("file")) {
			return null;
		}
		String dir = model.get("dir").getAsString();
		String file = model.get("file").getAsString();
		return StringUtils.isBlank(dir) ? file : dir + "/" + file;
	}

	/**
	 * Updates the model reference inside instant report state JSON.
	 *
	 * @param state    existing instant report state JSON string
	 * @param modelUrl resolved model resource URL in the form {@code dir/file.model}
	 * @return updated state JSON string
	 */
	public static String updateModelPath(String state, String modelUrl) {
		if (StringUtils.isBlank(state) || StringUtils.isBlank(modelUrl)) {
			return state;
		}
		JsonObject stateJson = JsonParser.parseString(state).getAsJsonObject();
		if (!stateJson.has("subject")) {
			return state;
		}
		JsonObject subject = stateJson.getAsJsonObject("subject");
		if (!subject.has("model")) {
			return state;
		}
		JsonObject model = subject.getAsJsonObject("model");
		String dir;
		String file;
		int lastSlash = modelUrl.lastIndexOf('/');
		if (lastSlash < 0) {
			dir = "";
			file = modelUrl;
		} else {
			dir = modelUrl.substring(0, lastSlash);
			file = modelUrl.substring(lastSlash + 1);
		}
		model.addProperty("dir", dir);
		model.addProperty("file", file);

		return stateJson.toString();
	}
}
