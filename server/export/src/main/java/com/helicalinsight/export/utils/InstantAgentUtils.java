package com.helicalinsight.export.utils;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Utility methods for resolving and updating agent references embedded in instant report state JSON.
 */
public final class InstantAgentUtils {

	private InstantAgentUtils() {
	}

	/**
	 * Extracts the agent resource URL from instant report state JSON.
	 *
	 * @param state instant report state JSON string
	 * @return agent resource URL in the form {@code dir/file.agent}, or {@code null} if not present
	 */
	public static String getAgentUrl(String state) {
		if (StringUtils.isBlank(state)) {
			return null;
		}
		JsonObject stateJson = JsonParser.parseString(state).getAsJsonObject();
		if (!stateJson.has("subject")) {
			return null;
		}
		JsonObject subject = stateJson.getAsJsonObject("subject");
		if (!subject.has("agent")) {
			return null;
		}
		JsonObject agent = subject.getAsJsonObject("agent");
		if (!agent.has("dir") || !agent.has("file")) {
			return null;
		}
		String dir = agent.get("dir").getAsString();
		String file = agent.get("file").getAsString();
		return StringUtils.isBlank(dir) ? file : dir + "/" + file;
	}

	/**
	 * Updates the agent reference inside instant report state JSON.
	 *
	 * @param state    existing instant report state JSON string
	 * @param agentUrl resolved agent resource URL in the form {@code dir/file.agent}
	 * @return updated state JSON string
	 */
	public static String updateAgentPath(String state, String agentUrl) {
		if (StringUtils.isBlank(state) || StringUtils.isBlank(agentUrl)) {
			return state;
		}
		JsonObject stateJson = JsonParser.parseString(state).getAsJsonObject();
		if (!stateJson.has("subject")) {
			return state;
		}
		JsonObject subject = stateJson.getAsJsonObject("subject");
		if (!subject.has("agent")) {
			return state;
		}
		JsonObject agent = subject.getAsJsonObject("agent");
		String dir;
		String file;
		int lastSlash = agentUrl.lastIndexOf('/');
		if (lastSlash < 0) {
			dir = "";
			file = agentUrl;
		} else {
			dir = agentUrl.substring(0, lastSlash);
			file = agentUrl.substring(lastSlash + 1);
		}
		agent.addProperty("dir", dir);
		agent.addProperty("file", file);

		return stateJson.toString();
	}
}
