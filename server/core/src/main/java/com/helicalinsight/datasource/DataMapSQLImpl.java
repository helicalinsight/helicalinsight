package com.helicalinsight.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.JdbcQueryExecutor;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * DataMapSQLImpl implements {@link IDataMap} interface
 * This class is responsible to fetch results from SQL-based data mappings.
 */
@Component
public class DataMapSQLImpl implements IDataMap {
	private static final Logger logger = LoggerFactory.getLogger(DataMapSQLImpl.class);
	/**
	 * getResultSet(JSONObject dataMapTagContent, JSONObject queryParameters, Connection con)
	 * @param dataMapTagContent                     content of the data map tag of efwd
	 * @param queryParameters						request parameter json
	 * @param con               					Database connection to execute the query.
	 * @return ResultSet of query in jsonObject format, otherwise {@code null} if SQLException occurs.
	 * @throws EfwdServiceException if {@code java.sql.Connection} object is null.
	 */
	@Override
	public JsonObject getResultSet(JsonObject dataMapTagContent, JsonObject queryParameters, Connection con) {

		EfwdQueryProcessor queryProcessor = ApplicationContextAccessor.getBean(EfwdQueryProcessor.class);
		String processedQuery = queryProcessor.getQuery(dataMapTagContent, queryParameters);

		try {
			if(con == null){
				throw new EfwdServiceException("Expected a Connection found null Connection");
			}
			Statement statement = con.createStatement();
			JdbcQueryExecutor jdbcQueryExecutor = new JdbcQueryExecutor(statement, processedQuery);
			JsonObject result = new Gson().fromJson(jdbcQueryExecutor.executeSql().toString(),JsonObject.class);
			return result;

		} catch (	SQLException ignore) {
			logger.error("Error while executing the query");
		}
		return null;

	}

}
