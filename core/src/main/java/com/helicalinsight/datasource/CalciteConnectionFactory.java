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

import com.helicalinsight.datasource.calcite.CalciteConnectionProvider;
import com.helicalinsight.efw.components.EfwdReader;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import net.sf.json.JSONObject;

import java.sql.Connection;

/**
 * Created by user on 11/26/2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class CalciteConnectionFactory implements IConnectionFactory {

    private static final String CALCITE_DRIVER = "org.apache.calcite.jdbc.Driver";

    @Override
    public DriverConnection getConnection(String type, String jsonInfo) {
        EfwdReader efwdReader = new EfwdReader();
        String result = efwdReader.executeComponent(jsonInfo);
        String model = JSONObject.fromObject(result).getString("model");
        CalciteConnectionProvider provider = ApplicationContextAccessor.getBean(CalciteConnectionProvider.class);
        Connection connection = provider.getConnection(model);
        DriverConnection driverConnection = new DriverConnection();
        driverConnection.setConnection(connection);
        driverConnection.setDriverClass(CALCITE_DRIVER);
        return driverConnection;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}

