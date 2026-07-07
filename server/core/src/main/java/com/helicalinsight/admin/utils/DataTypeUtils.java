package com.helicalinsight.admin.utils;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

public class DataTypeUtils {

	private DataTypeUtils() {
		// NOOP
	}

	public static String updateColumnDataType(String typeName, Map<String, String> dataTypesMapping) {
		return dataTypesMapping.getOrDefault(typeName, "");
	}

	public static String updateColumnDataType(ResultSetMetaData metadata, int position,
			Map<String, String> dataTypesMapping) {
		try {
			String type =  metadata.getColumnTypeName(position);
			return dataTypesMapping.getOrDefault(type, "");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}

}
