package com.helicalinsight.datasource.csv;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.JDBCDriver;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.JdbcDaoImpl;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.PropertiesFileReader;

//TODO  : Marked for removal
@Deprecated
@SuppressWarnings("unused")
public class CsvDataSourceDriver implements IDriver {
	
    private Connection getConnection(String databaseFilePath) {
        Map<String, String> map = new PropertiesFileReader().read("Admin", "csvDataSource.properties");

        String csvDriverUrl = map.get("csvDriverUrl");
        String driver = map.get("Driver");
        String username = map.get("username");
        String password = map.get("password");

        return JDBCDriver.getConnection(csvDriverUrl + databaseFilePath, username, password, driver);
    }

	/**
	 * getQuery using gson
	 * @param JsonObject dataMapTagContent
	 * @param  JsonObject requestParameterJson
	 * @return String 
	 */
	@Override
	public String getQuery(JsonObject dataMapTagContent, JsonObject requestParameterJson) {
		EfwdQueryProcessor queryProcessor = new EfwdQueryProcessor();
        return queryProcessor.getQuery(dataMapTagContent, requestParameterJson);
	}
	/**
	 * getJSONData using gson
	 * @param JsonObject requestParameterJson
	 * @param JsonObject connectionDetails
	 * @param JsonObject dataMapTagContent
	 * @param  JsonObject requestParameterJson
	 * @return JsonObject 
	 */
	@Override
	public JsonObject getJSONData(JsonObject requestParameterJson, JsonObject connectionDetails,
			JsonObject dataMapTagContent, ApplicationProperties applicationProperties) {
		if (!connectionDetails.has("dir") || !connectionDetails.has("file")) {
            throw new EfwServiceException("One or both of the required parameters dir, file are missing.");
        }

        String dir = connectionDetails.get("dir").getAsString();
        String file = connectionDetails.get("file").getAsString();

        // Create the complete path of csv file
        String solutionDirectory = applicationProperties.getSolutionDirectory();

        String csvFilePath = solutionDirectory + File.separator + dir + File.separator + file;

        // Create table name and database name having the same name as the csv
        // file
        int periodIndex = file.lastIndexOf(".");

        String tableName;
        if (periodIndex == -1) {
            tableName = file;
        } else {
            tableName = file.substring(0, periodIndex);
        }

        String databaseName = tableName + ".db";

        String databaseFilePath = solutionDirectory + File.separator + dir + File.separator + databaseName;

        Connection jdbcConnection = getConnection(databaseFilePath);//Creates an empty database file if one doesn't
        // exist

        File fileToCheck = new File(databaseFilePath);
        if (fileToCheck.length() == 0) {
            CsvToDatabaseDumpHandler.dump(csvFilePath, tableName, jdbcConnection);
        }

        String query = getQuery(dataMapTagContent, requestParameterJson);

        IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
        if (requestParameterJson.has("maxRows") && (jdbcDao instanceof JdbcDaoImpl)) {
            ((JdbcDaoImpl) jdbcDao).setLimit(requestParameterJson.get("maxRows").getAsInt());
            query = GsonUtility.optStringValue(requestParameterJson,"processedLimitQuery", query);
        }
        return jdbcDao.query(jdbcConnection, query);

	}
}
