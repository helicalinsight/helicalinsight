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

package com.helicalinsight.datasource.managed;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;

/**
 * Created by author on 28-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
class JdbcServiceImpl implements IJdbcService {

    @Autowired
    private IJdbcDao jdbcDao;

    @Autowired
    private IGlobalXmlReader globalXmlReader;

    @Autowired
    private IJdbcConnectionService jdbcConnectionService;

    @Override
    public JsonObject execute(String connectionsFile, Integer connectionId, String sql) {
        File file = new File(connectionsFile);
        validate(file);
        String json = this.globalXmlReader.getDataSourceJson(connectionId, file);
        Connection connection = this.jdbcConnectionService.getDatabaseConnection(json);
        return this.jdbcDao.query(connection, sql);
    }

    private void validate(File file) {
        if (!file.exists()) {
            throw new ConfigurationException("The connections xml file does not exist");
        }
    }
}