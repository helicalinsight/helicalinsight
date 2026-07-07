package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities;
import com.helicalinsight.adhoc.metadata.jaxb.DatabaseFunctions;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.AppStatistics;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**
 * The DatabaseAggregateFunctionListProvider class Provides functionality to retrieve database-specific aggregate functions,
 * Created by author on 20-03-13
 * @author Somen
 */
@SuppressWarnings("unused")
public class DatabaseAggregateFunctionListProvider implements IComponent {
	/**
	 * The class is thread safe to the cache.
	 * @return true.
	 */
    public boolean isThreadSafeToCache() {
        return true;
    }
    /**
     * Executes the component to retrieve and present database-specific aggregate functions.
     * 
     * @param formData 		form data containing location, metadata file name, and other parameters.
     * @return A JSON string representing the response containing the aggregate functions.
     * @throws RequiredParameterIsNullException If required parameters are missing in the form data.     
     */
    public String executeComponent(String formData) {
        JsonObject formJson = JsonParser.parseString(formData).getAsJsonObject();

        validate(formJson);

        String location = null;
        String uniqueId = null;
        String metadataFileName = null;

        if (formJson.has("location") && formJson.has("metadataFileName")) {
            location = formJson.get("location").getAsString();
            metadataFileName = formJson.get("metadataFileName").getAsString();
        }

        if (formJson.has("uniqueId")) {
            uniqueId = formJson.get("uniqueId").getAsString();
        }

        JsonArray groups = null;
        if (formJson.has("groups")) {
            groups = formJson.getAsJsonArray("groups");
        }


        DriverClass driverClass = getDriverClass(location, uniqueId, metadataFileName);

        JsonObject response;
        response = new JsonObject();
        Metadata metadata = MetadataDBUtility.getMetadata(location, metadataFileName);
        String finalReference="";



        if (driverClass != null) {
            String reference = driverClass.getReference();
            String fetchMode = metadata.getConnectionDetails().getFetchMode();
            String sqlFunctionsLocation = JsonUtils.sqlFunctionsLocation();
             finalReference = "cache".equalsIgnoreCase(fetchMode) ? "spark" : reference;
            File functionsFile = new File(sqlFunctionsLocation + File.separator + finalReference + ".xml");
            Boolean cached = metadata.getCached();
            if (cached != null && cached && AppStatistics.isMASTER_STARTED() && AppStatistics.isSPARK_STARTED()) {
                functionsFile = new File(sqlFunctionsLocation + File.separator + "default.xml");
            }
            if (functionsFile.exists()) {
                DatabaseFunctionFileReader functionFileReader = new DatabaseFunctionFileReader();
                DatabaseFunctions databaseFunctions = JaxbUtils.unMarshal(DatabaseFunctions.class, functionsFile);
                functionFileReader.addDatabaseSpecificFunctions(response, databaseFunctions, groups);
            }
        }

        PropertiesFileReader functionKeywordMapper = new PropertiesFileReader();
        Map<String, String> functionKeywordMap = functionKeywordMapper.read("Admin", "sqlQuery" + ".properties");
        if (functionKeywordMap != null) {
            GsonUtility.accumulate(response,"functions", new TreeMap<>(functionKeywordMap));
        }
        response.addProperty("reference",finalReference);
        return response.toString();
    }
    /**
     * Validates the form data to ensure required parameters are present.
     * 
     * @param formJson 		 object containing location, metadata file name parameters.
     * @throws RequiredParameterIsNullException If required parameters are missing.
     */
    private void validate(JsonObject formJson) {
        if (formJson.has("location")) {
            if (!formJson.has("metadataFileName")) {
                throw new RequiredParameterIsNullException("Parameter metadataFileName is missing.");
            }
        }

        if (formJson.has("metadataFileName")) {
            if (!formJson.has("location")) {
                throw new RequiredParameterIsNullException("Parameter location is missing.");
            }
        }
    }
    /**
     * Returns the driver class based on location, unique ID, and metadata file name.
     * 
     * @param location         		 location of the metadata.
     * @param uniqueId         		 unique ID associated with the metadata.
     * @param metadataFileName 		 name of the metadata file.
     * @return The DriverClass object corresponding to the metadata.
     */
    private DriverClass getDriverClass(String location, String uniqueId, String metadataFileName) {
        Metadata metadata;
        if (uniqueId != null) {
            File tempDirectory = TempDirectoryCleaner.getTempDirectory();
            File file = new File(tempDirectory.getAbsolutePath() + File.separator + uniqueId + "." + JsonUtils
                    .getMetadataExtension());

            if (!file.exists()) {
                throw new IllegalArgumentException("The metadata file requested doesn't exist.");
            }
            metadata = JaxbUtils.unMarshal(Metadata.class, file);
        } else {
            metadata = MetadataDBUtility.getMetadata(location, metadataFileName);
        }

        return SqlQueryUtilities.driverClass(metadata);
    }


}
