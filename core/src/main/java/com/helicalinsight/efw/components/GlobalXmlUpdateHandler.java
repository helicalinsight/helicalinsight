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
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by author on 06-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class GlobalXmlUpdateHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(GlobalXmlUpdateHandler.class);

    public static synchronized String marshal(String dataSourceProvider, String id, JSONObject formData,
                                              String mode) {


        DataSourceSecurityUtility.validateGlobalDataSourceAccessForWriteOperation(id, mode);

        String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();
        int theId = Integer.valueOf(id);
        String edit = "";
        try {
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            JAXBContext jaxbContext = jaxbContexts.getContextForClass(Connections.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File xml = new File(connectionsXmlFile);
            Connections connections = (Connections) unmarshaller.unmarshal(xml);
            if ("none".equalsIgnoreCase(dataSourceProvider)) {
                List<JdbcConnection> list = connections.getJdbcConnection();
                validateConnection(list, dataSourceProvider);
                for (JdbcConnection connection : list) {
                    if (connection.getId() == theId) {
                        edit = JdbcConnectionProperties.writeNonPooledType(connection, theId, formData, mode);
                        break;
                    }
                }

            } else if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
                List<JndiDataSource> list = connections.getJndiDataSource();
                validateConnection(list, dataSourceProvider);
                for (JndiDataSource dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = JndiDataSourceProperties.writeStaticDataSourceType(dataSource, theId, formData, mode);
                        break;
                    }
                }
            } else if ("hikari".equalsIgnoreCase(dataSourceProvider)) {
                List<HikariProperties> list = connections.getHikariProperties();
                validateConnection(list, dataSourceProvider);
                for (HikariProperties dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = HikariCpDataSourceProperties.writeHikariDataSourceType(dataSource, theId, formData,
                                mode);
                        break;
                    }
                }
            } else if ("tomcat".equalsIgnoreCase(dataSourceProvider)) {
                List<TomcatPoolProperties> list = connections.getTomcatPoolProperties();
                validateConnection(list, dataSourceProvider);
                for (TomcatPoolProperties dataSource : list) {
                    if (dataSource.getId() == theId) {
                        edit = TomcatJdbcDataSourceProperties.writeTomcatJdbcDataSourceType(dataSource, theId,
                                formData, mode);
                        break;
                    }
                }
            }

            if (!edit.isEmpty()) {
                saveChanges(jaxbContext, xml, connections);
            } else {
                throwException(dataSourceProvider);
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return edit;
    }


    private static void validateConnection(Object connection, String dataSourceProvider) {
        if (connection == null) {
            throwException(dataSourceProvider + ". The given dataSource does not exists");
        }
    }


    private static void throwException(String dataSourceProvider) {
        throw new EfwServiceException(String.format("Could not update the given data " + "source details of type %s" +
                ".", dataSourceProvider));
    }

    private static void saveChanges(JAXBContext jaxbContext, File xml, Connections connections) throws
            JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(connections, xml);
    }

    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        Map<String, String> parameters = new HashMap<>();

        String dataSourceProvider = formDataJson.getString("dataSourceProvider");
        String id = formDataJson.getString("id");
        String type = formDataJson.getString("type");

        parameters.put("id", id);
        parameters.put("type", type);
        parameters.put("dataSourceProvider", dataSourceProvider);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        if (!"jndi".equalsIgnoreCase(dataSourceProvider)) {
            ControllerUtils.validate(formDataJson);
        }

        try {
            String message = marshal(dataSourceProvider, id, formDataJson, "edit");
            if (logger.isDebugEnabled()) {
                logger.debug("The update status of the global xml is " + message);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new EfwServiceException("The data source could not be updated with the new details. Cause " +
                    ExceptionUtils.getRootCauseMessage(ex));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("The data source is updated with the new details successfully.");
        }

        JSONObject model;
        model = new JSONObject();
        model.accumulate("message", "The data source is updated with the new details successfully.");
        return model.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}