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

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.JdbcConnectionException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Created by author on 21-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
class JndiConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(JndiConfigurer.class);


    public DataSource getJndiDataSource(String json) {
        if (json == null) {
            throw new IllegalArgumentException("The parameter json is null");
        }

        String lookUpName = JsonUtils.getKeyFromJson(json, "lookUpName");

        if (lookUpName == null) {
            throw new MalformedJsonException("Json does not have lookUpName");
        }

        Context initContext;
        try {
            initContext = new InitialContext();
            DataSource dataSource = (DataSource) initContext.lookup(lookUpName);
            if (dataSource == null) {
                throwException(lookUpName);
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("Jndi lookUp for the name " + lookUpName + " is successful and " +
                            "the DataSource class is " + dataSource.getClass());
                }
            }
            return dataSource;
        } catch (NamingException exception) {
            throw new JdbcConnectionException("Could not find the JNDI resource " + lookUpName, exception);
        }
    }

    private void throwException(String lookUpName) {
        throw new ConfigurationException(String.format("Could not find the JNDI resource %s. " +
                "Configure the JNDI DataSource in your application server and query with proper " +
                "lookUpName. " +
                "For example java:comp/env/jdbc/TestDB", lookUpName));
    }
}
