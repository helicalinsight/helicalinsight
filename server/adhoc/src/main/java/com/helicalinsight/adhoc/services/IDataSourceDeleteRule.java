package com.helicalinsight.adhoc.services;

import com.google.gson.JsonObject;

public interface IDataSourceDeleteRule {
String deleteDataSource(JsonObject formDataJson, String dataSourceProvider, String id);
}
