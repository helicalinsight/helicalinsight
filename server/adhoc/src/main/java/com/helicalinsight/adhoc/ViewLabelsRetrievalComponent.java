package com.helicalinsight.adhoc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities;
import com.helicalinsight.adhoc.metadata.ISecureMetadata;
import com.helicalinsight.adhoc.metadata.MetadataSecurityObjectFactory;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.adhoc.utils.AdhocServiceUtils;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.ConnectionProviderFactory;
import com.helicalinsight.datasource.DriverConnection;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.IJdbcDao;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwdServiceException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.DialectHelper;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.query.spi.Limit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;


/**
 * Component for retrieving view labels based on the provided metadata and form data.
 * This component handles the execution of queries, evaluates expressions, and processes metadata to obtain view labels.
 * 
 * Created by author on 08-09-2015.
 * @author Rajasekhar
 */
public class ViewLabelsRetrievalComponent implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(ViewLabelsRetrievalComponent.class);
    /**
     * Retrieves a query using Gson for processing data map tag content and request parameters.
     *
     * @param dataMapTagContent     		 JsonObject containing data map tag content.
     * @param requestParameterJson  		 JsonObject containing request parameters.
     * @return String                		 resulting query obtained from EfwdQueryProcessor.
     */
    public static String query(JsonObject formData, String dialectOfDatabase, String limit) {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, dialectOfDatabase);

        Dialect hibernateDialect = DialectHelper.getDialect(properties);
        String query = MetadataUtils.parameter(formData, "query");
        query = query.trim();


        String queryType = GsonUtility.optString(formData, "queryType");
        if (StringUtils.isNotEmpty(queryType)) {
            if (queryType.equalsIgnoreCase("groovy")) {
                query = SqlQueryUtilities.evalAllExpression(null, query);
            } else {
                ISecureMetadata securityClass = MetadataSecurityObjectFactory.getSecurityClass(queryType);
                if (securityClass != null) {
                    query = securityClass.getFilters(query);
                }
            }
        }
        String subQuery = query;
        if (GsonUtility.optBoolean(formData, "hasStoredProcedure")) {
            query = subQuery;
        } else {
            query = "select * from (" + subQuery + ") foo ";
            LimitHandler limitHandler = hibernateDialect.getLimitHandler();
            Limit hibLimit = new Limit();
            hibLimit.setMaxRows(Integer.parseInt(limit));
            if(limitHandler.supportsLimit()) {
            		query =  limitHandler.processSql(query, hibLimit);
            		if(query.contains("?")) {
            			query = query.replace("?", limit);
            		}
             }
        }


        if (logger.isDebugEnabled()) {
            logger.debug("The query for the view is {}", query);
        }
        return query;
    }

    /**
     * Executes the ViewLabelsRetrievalComponent based on the provided JSON form data.
     *
     * @param jsonFormData 		 form data containing metadata details and query parameters.
     * @return String       	 A JSON string containing the result of the view labels retrieval.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);

        if (formData.has("location") && formData.has("metadataFileName")) {
            String location;
            String metadataFileName;
            try {
                location = formData.get("location").getAsString();
                metadataFileName = formData.get("metadataFileName").getAsString();
            } catch (Exception ex) {
                throw new MalformedJsonException("Required parameters location and/or " + "metadataFileName are " +
                        "missing.", ex);
            }

            String path = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + location +
                    File.separator + metadataFileName;

            HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
            HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
            HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
            if(metadataResource==null){
                throw new ResourceNotFoundException("The Resource does not exists");
            }
            Metadata metadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());

            ConnectionDetails connectionDetails = metadata.getConnectionDetails();
            String directory = connectionDetails.getDirectory();
            if (directory == null) {
                String subType = connectionDetails.getSubType();
                formData.addProperty("type", subType);
                formData.addProperty("id", connectionDetails.getConnectionId());
            } else {
                String connectionType = connectionDetails.getConnectionType();
                formData.addProperty("type", connectionType);
                formData.addProperty("id", connectionDetails.getConnectionId());
                formData.addProperty("dir", directory);
            }
        }

        String type = MetadataUtils.parameter(formData, "type");
        AdhocServiceUtils.addExtraDataForNormalProcess(formData, type);
        DriverConnection driverConnection = (DriverConnection) ConnectionProviderFactory.getConnection(formData, type);

        Connection connection = null;

        if (driverConnection != null) {
            connection = driverConnection.getConnection();
        }

        if (connection == null) {
            throw new EfwdServiceException("The connection object is null.");
        }

        String driverClass = driverConnection.getDriverClass();
        String dialectOfDatabase = MetadataUtils.dialectOfDatabase(driverClass);

        String query = null;

        JsonObject model;
        model = new JsonObject();
        try {
            query = query(formData, dialectOfDatabase, "10");
            JsonObject response = metadataArray(connection, query);
            JsonArray metadata = response.getAsJsonArray("metadata");
            JsonObject jsonObject = metadata.get(0).getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            Collection values = new ArrayList<>();
            for(Entry<String, JsonElement> entry : entrySet) {
                values.add(entry.getValue());
            }



            model.add("labels", new Gson().fromJson(new Gson().toJson(values),JsonArray.class));
            model.add("data",response.get("data").getAsJsonArray());
        } catch (Exception ex) {
            List<String> messageArray = ControllerUtils.getMessageChain(ex);

            model.add("labels", new JsonArray());
            model.addProperty("error", messageArray.size() > 0 ? messageArray.toString() : ex.getMessage());
        } catch (Error error) {
            model.add("labels", new JsonArray());
            String cause = error.getMessage();
            model.addProperty("error", cause != null ? error.getMessage() : "Exception while executing the query");
        }
        model.addProperty("processedQuery", query);

        return model.toString();
    }

    /**
     * Retrieves metadata array based on the provided connection and query.
     *
     * @param connection 		 database connection.
     * @param query      		 SQL query to retrieve metadata.
     * @return JsonArray  		 resulting metadata as a JSON array.
     */
    private JsonObject metadataArray(Connection connection, String query) {
        IJdbcDao dao = ApplicationContextAccessor.getBean(IJdbcDao.class);
       
        JsonObject result = new Gson().fromJson(dao.query(connection, query),JsonObject.class);
        return  result;
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} the component is thread-safe to cache.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
