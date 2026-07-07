package com.helicalinsight.efw.components;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;
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
import java.util.UUID;

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
    public synchronized String executeComponent(String formData) {
        JsonObject formDataJsonObject = new Gson().fromJson(formData,JsonObject.class);
        if(formDataJsonObject.has("driverName")) {
            String driverName = formDataJsonObject.get("driverName").getAsString();
            formDataJsonObject = JsonUtils.prepareJdbcUrlForMiddleWare(formDataJsonObject, driverName);
            if (formDataJsonObject.has("autoSave")) {
                formDataJsonObject.addProperty("name", UUID.randomUUID().toString().replace("-", ""));
            }
        }

        JsonObject globalConnectionJsonObject = JsonUtils.newGetGlobalConnectionsJson();
        List<String> keys = JsonUtils.getKeys(globalConnectionJsonObject);

        String dataSourceProvider = formDataJsonObject.get("dataSourceProvider").getAsString();
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
                    jdbcConnectionList =  new ArrayList<>();
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
                TomcatPoolProperties tomcatPoolProperties = (TomcatPoolProperties) ApplicationContextAccessor.getBean("tomcatPoolProperties"
                );

                message = TomcatJdbcDataSourceProperties.writeTomcatJdbcDataSourceType(tomcatPoolProperties, maxId,
                        formDataJsonObject, mode);
                if (tomcatPoolPropertiesList == null) {
                    tomcatPoolPropertiesList = new ArrayList<>();
                    connections.setTomcatPoolProperties(tomcatPoolPropertiesList);
                }
                tomcatPoolPropertiesList.add(tomcatPoolProperties);

            } else if ("noSql".equalsIgnoreCase(dataSourceProvider)) {
                List<NoSqlProperties> tomcatPoolPropertiesList = connections.getNoSqlProperties();
                NoSqlProperties tomcatPoolProperties = (NoSqlProperties) ApplicationContextAccessor.getBean("noSqlProperties"
                );

                message = NoSqlDataSourceProperties.writeNoSqlDataSource(tomcatPoolProperties, maxId,
                        formDataJsonObject, mode);
                if (tomcatPoolPropertiesList == null) {
                    tomcatPoolPropertiesList = new ArrayList<>();
                    connections.setNoSqlProperties(tomcatPoolPropertiesList);
                }
                tomcatPoolPropertiesList.add(tomcatPoolProperties);

            }

            if (message.isEmpty()) {
                throw new IllegalArgumentException("The dataSourceProvider is unknown. Can not " + "perform write " +
                        "operation.");
            } else {
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                String charsetName = ControllerUtils.defaultCharSet();
                marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
                marshaller.marshal(connections, xml);
                JsonObject messageObject = new Gson().fromJson(message,JsonObject.class);
                messageObject.add("data", DataSourceUtils.provideData(formDataJsonObject, "" + maxId));
                if (GsonUtility.optBooleanValue(formDataJsonObject, "prepareCache",true)) {
                AutoTriggerCatalogSchema runnable = ApplicationContextAccessor.getBean(AutoTriggerCatalogSchema.class);
                JsonObject data = messageObject.getAsJsonObject("data");
                data.addProperty("database", GsonUtility.optString(formDataJsonObject, "database"));
                data.addProperty("requestId", GsonUtility.optString(formDataJsonObject,"requestId"));
                data.addProperty("autoTriggerMode", "create-edit");
                runnable.setData(data);
                ExecutorUtils.addTask(runnable);
                data.remove("autoTriggerMode");
            }
                //Trigger the  caching process in the background.
                return messageObject.toString();
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

    }


    private int maxId(@NotNull List<String> keys, @NotNull JsonObject globalConnectionJsonObject) {
        JsonArray jsonArray;
        ArrayList<Integer> arrayList = new ArrayList<>();
        String stringId;
        int maxValue;
        for (String key : keys) {
            Object theKey = globalConnectionJsonObject.get(key);
            if (theKey instanceof JsonArray) {
                jsonArray = globalConnectionJsonObject.getAsJsonArray(key);
                for (int counter = 0; counter < jsonArray.size(); counter++) {
                    stringId = jsonArray.get(counter).getAsJsonObject().get("@id").getAsString();
                    arrayList.add(Integer.parseInt(stringId));
                }
            } else if (theKey instanceof JsonObject) {
                stringId = globalConnectionJsonObject.getAsJsonObject(key).get("@id").getAsString();
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
