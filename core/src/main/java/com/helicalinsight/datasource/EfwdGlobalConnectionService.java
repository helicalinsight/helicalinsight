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

package com.helicalinsight.datasource;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.IJdbcService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by author on 17-Jan-15.
 *
 * @author Rajasekhar
 */
@Component
class EfwdGlobalConnectionService implements IEfwdService {

    private static final Logger logger = LoggerFactory.getLogger(EfwdGlobalConnectionService.class);

    @Autowired
    private EfwdQueryProcessor efwdQueryProcessor;

    @Autowired
    private EfwdServiceUtility efwdServiceUtility;

    @Autowired
    private IJdbcService jdbcService;


    @Override
    public JsonObject execute(String httpRequestJson, String connectionDetails,
                              String dataMapTagContent, ApplicationProperties applicationProperties) {

        Efwd efwd = this.efwdServiceUtility.getEfwd(connectionDetails);
        if (!GlobalJdbcType.TYPE.equalsIgnoreCase(efwd.getServiceType())) {
            throw new EfwdServiceException("The expected type of connection in the efwd file is " + GlobalJdbcType
                    .TYPE + ". Please configure the efwd properly.");
        }
        int connectionId = efwd.getGlobalId();
        String sqlQuery = this.efwdQueryProcessor.getQuery(JSONObject.fromObject(dataMapTagContent),
                JSONObject.fromObject(httpRequestJson));

        String connectionsFilePath = JsonUtils.getGlobalConnectionsPath();

        JsonObject result = this.jdbcService.execute(connectionsFilePath, connectionId, sqlQuery);
        if (logger.isDebugEnabled()) {
            logger.debug("The query has been successfully executed on the data source with the " + "given data map id");
        }
        return result;
    }
}
