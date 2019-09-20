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

package com.helicalinsight.hwf.component;

import com.helicalinsight.datasource.JDBCDriver;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.hwf.core.IJobProcess;
import com.helicalinsight.hwf.core.api.ExecutionStatus;
import com.helicalinsight.hwf.util.ComponentUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen
 *         Created  on 5/7/2016.
 */

public class ExecuteDbQuery implements IJobProcess {
    private static final Logger logger = LoggerFactory.getLogger(ExecuteDbQuery.class);
    private Boolean hasNext = ExecutionStatus.HAS_NEXT_NOT_SET;
    private Integer executionState = ExecutionStatus.NOT_SET;

    @Override
    public JSONObject jobProcess(JSONObject input, JSONObject jobProcess) {


        JDBCDriver jdbcDriver = new JDBCDriver();

        JSONObject connectionDetails = new JSONObject();
        connectionDetails.put("Url", input.getString("Url"));
        connectionDetails.put("User", input.getString("User"));
        connectionDetails.put("Pass", input.getString("Pass"));
        connectionDetails.put("Driver", input.getString("Driver"));
        JSONObject request = new JSONObject();
        JSONObject dataMap = new JSONObject();
        dataMap.put("Query", input.getString("Query"));
        executionState = ExecutionStatus.SUCCESS;

        return JSONObject.fromObject(jdbcDriver.getJSONData(request, connectionDetails, dataMap,
                ApplicationProperties.getInstance()).toString());


    }

    @Override
    public JSONObject executionStatus() {

        return ComponentUtils.setExecutionStatus(executionState);
    }
}
