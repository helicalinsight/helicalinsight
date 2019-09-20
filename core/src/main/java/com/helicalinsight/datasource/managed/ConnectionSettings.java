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

import com.google.gson.*;
import com.helicalinsight.datasource.calcite.CalciteConnection;
import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.utility.JaxbContexts;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
class ConnectionSettings implements IConnectionSettings {

    private final Map<Integer, Object> connectionIdsMap = new HashMap<>();

    private final Set<String> poolIdsSet = new HashSet<>();

    public String getJson(File connectionsXmlFile, Integer connectionId) {
        String json;
        JAXBContext jaxbContext;
        try {
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            jaxbContext = jaxbContexts.getContextForClass(Connections.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Connections connections = (Connections) unmarshaller.unmarshal(connectionsXmlFile);

            Gson gson = new Gson();
            json = gson.toJson(connections);
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

            populate(gson, jsonObject);

            Object object = this.connectionIdsMap.get(connectionId);
            if ((object == null) || (connectionIdsMap.size() == 0)) {
                throw new ConfigurationException("Could not obtain the connection details from " + "the specified " +
                        "file.");
            }
            json = gson.toJson(object);
        } catch (JAXBException ex) {
            throw new ConfigurationException("Connection configuration xml file is malformed.", ex);
        }
        return json;
    }

    private void populate(Gson gson, JsonObject jsonObject) {
        if (jsonObject.has("jdbcConnection")) {
            add(gson, jsonObject, "jdbcConnection");
        }
        if (jsonObject.has("calciteConnection")) {
            add(gson, jsonObject, "calciteConnection");
        }
        if (jsonObject.has("jndiDataSource")) {
            add(gson, jsonObject, "jndiDataSource");
        }
        if (jsonObject.has("hikariProperties")) {
            add(gson, jsonObject, "hikariProperties");
        }
        if (jsonObject.has("tomcatPoolProperties")) {
            add(gson, jsonObject, "tomcatPoolProperties");
        }
    }

    private void add(Gson gson, JsonObject jsonObject, String arrayName) {
        JsonArray array = jsonObject.getAsJsonArray(arrayName);

        for (JsonElement element : array) {
            if (arrayName.equalsIgnoreCase("jdbcConnection")) {
                JdbcConnection jdbcConnection = gson.fromJson(element, JdbcConnection.class);
                Integer id = jdbcConnection.getId();
                saveConnectionId(jdbcConnection, id);
            }
            if (arrayName.equalsIgnoreCase("calciteConnection")) {
                CalciteConnection calciteConnection = gson.fromJson(element, CalciteConnection.class);
                Integer id = calciteConnection.getId();
                saveConnectionId(calciteConnection, id);
            }
            if (arrayName.equalsIgnoreCase("jndiDataSource")) {
                JndiDataSource jndiDataSource = gson.fromJson(element, JndiDataSource.class);
                Integer id = jndiDataSource.getId();
                saveConnectionId(jndiDataSource, id);
            }
            if (arrayName.equalsIgnoreCase("hikariProperties")) {
                HikariProperties hikariProperties = gson.fromJson(element, HikariProperties.class);
                savePoolId(hikariProperties.getDataSourcePoolId());
                Integer id = hikariProperties.getId();
                saveConnectionId(hikariProperties, id);
            }
            if (arrayName.equalsIgnoreCase("tomcatPoolProperties")) {
                TomcatPoolProperties tomcatPoolProperties = gson.fromJson(element, TomcatPoolProperties.class);
                savePoolId(tomcatPoolProperties.getDataSourcePoolId());
                Integer id = tomcatPoolProperties.getId();
                saveConnectionId(tomcatPoolProperties, id);
            }
        }
    }

    private void saveConnectionId(Object object, Integer id) {
        if (object == null) {
            throw new ConfigurationException("The connections file has incorrect configuration.");
        }
        if (this.connectionIdsMap.containsKey(id)) {
            throw new MalformedJsonException("The connections file has duplicate connection id(s)");
        } else {
            this.connectionIdsMap.put(id, object);
        }
    }

    private void savePoolId(String dataSourcePoolId) {
        if ((("".equals(dataSourcePoolId)) || (dataSourcePoolId == null) || "".equals(dataSourcePoolId.trim()))) {
            throw new MalformedJsonException("The connections file has one(or more) null pool id(s)");
        }
        if (this.poolIdsSet.contains(dataSourcePoolId)) {
            throw new MalformedJsonException("The connections file has duplicate pool id(s)");
        } else {
            this.poolIdsSet.add(dataSourcePoolId);
        }
    }
}
