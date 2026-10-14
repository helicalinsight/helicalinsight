package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.helicalinsight.stream.ProgressEvent;

public class Notification implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, List<String>> errors;
	private Map<String, List<String>> warnings;
	private Map<String, List<String>> info;
	private List<ProgressEvent> logs;

	public Notification() {
		this.errors = new LinkedHashMap<>();
		this.warnings = new LinkedHashMap<>();
		this.info = new LinkedHashMap<>();
		this.logs = new ArrayList<>();
	}

	public Map<String, List<String>> getErrors() {
		return errors;
	}

	public Map<String, List<String>> getWarnings() {
		return warnings;
	}

	public Map<String, List<String>> getInfo() {
		return info;
	}

	public List<ProgressEvent> getLogs() {
		return logs;
	}

	public void setErrors(Map<String, List<String>> errors) {
		this.errors = errors;
	}

	public void setWarnings(Map<String, List<String>> warnings) {
		this.warnings = warnings;
	}

	public void setInfo(Map<String, List<String>> info) {
		this.info = info;
	}

	public void setLogs(List<ProgressEvent> logs) {
		this.logs = logs;
	}

	public void addError(String resourcePath, String message) {
		add(errors, resourcePath, message);
	}

	public void addWarning(String resourcePath, String message) {
		add(warnings, resourcePath, message);
	}

	public void addInfo(String resourcePath, String message) {
		add(info, resourcePath, message);
	}

	public void addLog(ProgressEvent event) {
		if (event == null) {
			return;
		}
		if (logs == null) {
			logs = new ArrayList<>();
		}
		logs.add(event);
	}

	private void add(Map<String, List<String>> bucket, String resourcePath, String message) {
		if (resourcePath == null || message == null) {
			return;
		}
		List<String> messages = bucket.computeIfAbsent(resourcePath, _ -> new ArrayList<>());
		if (!messages.contains(message)) {
			messages.add(message);
		}
	}

	@Override
	public String toString() {
		try {
			return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			return "{\"errors\":{},\"warnings\":{},\"info\":{}}";
		}
	}
}
