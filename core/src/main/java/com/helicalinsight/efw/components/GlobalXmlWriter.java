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

package com.helicalinsight.efw.components;

import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalXmlWriter implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(GlobalXmlWriter.class);

    public static boolean checkPoolSize(String maximumPoolSize, int minimumIdle) {
        boolean flag = true;
        int maxPoolSizeIntegerValue = Integer.parseInt(maximumPoolSize);
        if (maxPoolSizeIntegerValue > minimumIdle) {
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJsonObject = JSONObject.fromObject(formData);
        JSONObject globalConnectionJsonObject = JsonUtils.getGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalConnectionJsonObject);

        String dataSourceProvider = formDataJsonObject.getString("dataSourceProvider");
        if (dataSourceProvider == null) {
            throw new IllegalArgumentException("The dataSourceProvider is null.");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The dataSourceProvider from the http request is " + dataSourceProvider);
        }

        int maxId = maxId(keys, globalConnectionJsonObject);
        try {
            String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            JAXBContext jaxbContext = jaxbContexts.getContextForClass(Connections.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File xml = new File(connectionsXmlFile);
            Connections connections = (Connections) unmarshaller.unmarshal(xml);
            String mode = "create";
            String message = "";
            if ("none".equalsIgnoreCase(dataSourceProvider)) {
                List<JdbcConnection> jdbcConnectionList = connections.getJdbcConnection();
                JdbcConnection jdbcConnection = ApplicationContextAccessor.getBean(JdbcConnection.class);
                message = JdbcConnectionProperties.writeNonPooledType(jdbcConnection, maxId, formDataJsonObject, mode);
                if (jdbcConnectionList == null) {
                    jdbcConnectionList = new ArrayList<>();
                    connections.setJdbcConnection(jdbcConnectionList);
                }
                jdbcConnectionList.add(jdbcConnection);

            } else if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
                List<JndiDataSource> jndiDataSourceList = connections.getJndiDataSource();
                JndiDataSource jndiDataSource = ApplicationContextAccessor.getBean(JndiDataSource.class);
                message = JndiDataSourceProperties.writeStaticDataSourceType(jndiDataSource, maxId,
                        formDataJsonObject, mode);
                if (jndiDataSourceList == null) {
                    jndiDataSourceList = new ArrayList<>();
                    connections.setJndiDataSource(jndiDataSourceList);

                }
                jndiDataSourceList.add(jndiDataSource);
            } else if ("hikari".equalsIgnoreCase(dataSourceProvider)) {
                List<HikariProperties> hikariPropertiesList = connections.getHikariProperties();
                HikariProperties hikariProperties = ApplicationContextAccessor.getBean(HikariProperties.class);

                message = HikariCpDataSourceProperties.writeHikariDataSourceType(hikariProperties, maxId,
                        formDataJsonObject, mode);
                if (hikariPropertiesList == null) {
                    hikariPropertiesList = new ArrayList<>();
                    connections.setHikariProperties(hikariPropertiesList);
                }
                hikariPropertiesList.add(hikariProperties);

            } else if ("tomcat".equalsIgnoreCase(dataSourceProvider)) {
                List<TomcatPoolProperties> tomcatPoolPropertiesList = connections.getTomcatPoolProperties();
                TomcatPoolProperties tomcatPoolProperties = ApplicationContextAccessor.getBean(TomcatPoolProperties
                        .class);

                message = TomcatJdbcDataSourceProperties.writeTomcatJdbcDataSourceType(tomcatPoolProperties, maxId,
                        formDataJsonObject, mode);
                if (tomcatPoolPropertiesList == null) {
                    tomcatPoolPropertiesList = new ArrayList<>();
                    connections.setTomcatPoolProperties(tomcatPoolPropertiesList);
                }
                tomcatPoolPropertiesList.add(tomcatPoolProperties);

            }
            if (message.isEmpty()) {
                throw new IllegalArgumentException("The dataSourceProvider is unknown. Can not " + "perform write " +
                        "operation.");
            } else {
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(connections, xml);
                return message;
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }

    private int maxId(List<String> keys, JSONObject globalConnectionJsonObject) {
        JSONArray jsonArray;
        ArrayList<Integer> arrayList = new ArrayList<>();
        String stringId;
        int maxValue;
        for (String key : keys) {
            Object theKey = globalConnectionJsonObject.get(key);
            if (theKey instanceof JSONArray) {
                jsonArray = globalConnectionJsonObject.getJSONArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    stringId = jsonArray.getJSONObject(counter).getString("@id");
                    arrayList.add(Integer.parseInt(stringId));
                }
            } else if (theKey instanceof JSONObject) {
                stringId = globalConnectionJsonObject.getJSONObject(key).getString("@id");
                arrayList.add(Integer.parseInt(stringId));
            }
        }

        if (!arrayList.isEmpty()) {
            maxValue = Collections.max(arrayList);
        } else {
            maxValue = 0;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Max id in globalConnectionJsonObject is:  " + maxValue);
        }
        return (maxValue + 1);
    }
}
