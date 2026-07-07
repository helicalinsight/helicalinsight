package com.helicalinsight.datasource.calcite;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.datasource.managed.JdbcDaoImpl;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Created by user on 11/24/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class CalciteAdapter implements IDriver {

    private static final Logger logger = LoggerFactory.getLogger(CalciteAdapter.class);
    	/**
	 * getQuery using gson
	 * @param JsonObject dataMapTagContent
	 * @param JsonObject requestParameterJson
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
		if (!connectionDetails.has("model")) {
            throw new EfwServiceException("Required parameter model is missing.");
        }
        String model = connectionDetails.get("model").getAsString();

        CalciteConnectionProvider calciteConnectionProvider = ApplicationContextAccessor.getBean
                (CalciteConnectionProvider.class);
        try (Connection connection = calciteConnectionProvider.getConnection(model)) {
            String query = getQuery(dataMapTagContent, requestParameterJson);
            if (logger.isDebugEnabled()) {
                logger.debug("Connection from calcite and the query from the efwd file are obtained.");
            }

            IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
            if (requestParameterJson.has("maxRows") && (jdbcDao instanceof JdbcDaoImpl)) {
                ((JdbcDaoImpl) jdbcDao).setLimit(requestParameterJson.get("maxRows").getAsInt());
                query = GsonUtility.optStringValue(requestParameterJson,"processedLimitQuery", query);
            }
            return jdbcDao.query(connection, query);
        } catch (Exception ex) {
            throw new EfwServiceException(ex);
        }
    }

    

	
}
