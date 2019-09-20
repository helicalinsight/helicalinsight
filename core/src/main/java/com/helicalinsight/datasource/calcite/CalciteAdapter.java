/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource.calcite;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EfwdQueryProcessor;
import com.helicalinsight.datasource.IDriver;
import com.helicalinsight.datasource.managed.IJdbcDao;
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

    @Override
    public JsonObject getJSONData(JSONObject requestParameterJson, JSONObject connectionDetails,
                                  JSONObject dataMapTagContent, ApplicationProperties applicationProperties) {
        if (!connectionDetails.has("model")) {
            throw new EfwServiceException("Required parameter model is missing.");
        }
        String model = connectionDetails.getString("model");

        CalciteConnectionProvider calciteConnectionProvider = ApplicationContextAccessor.getBean
                (CalciteConnectionProvider.class);
        try (Connection connection = calciteConnectionProvider.getConnection(model)) {
            String query = getQuery(dataMapTagContent, requestParameterJson);
            if (logger.isDebugEnabled()) {
                logger.debug("Connection from calcite and the query from the efwd file are obtained.");
            }

            IJdbcDao jdbcDao = ApplicationContextAccessor.getBean(IJdbcDao.class);
            return jdbcDao.query(connection, query);
        } catch (Exception ex) {
            throw new EfwServiceException(ex);
        }
    }

    @Override
    public String getQuery(JSONObject dataMapTagContent, JSONObject requestParameterJson) {
        EfwdQueryProcessor queryProcessor = new EfwdQueryProcessor();
        return queryProcessor.getQuery(dataMapTagContent, requestParameterJson);
    }
}
