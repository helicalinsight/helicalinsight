package com.helicalinsight.datasource;

import java.sql.Connection;

import com.google.gson.JsonObject;

public interface IDataMap {
	
	JsonObject getResultSet(JsonObject dataMapTagContent,JsonObject queryParameters,Connection con );

}
