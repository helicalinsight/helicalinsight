package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.genericsql.QueryGeneratorUtility;
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.DialectHelper;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.dialect.Dialect;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Service for generating SQL queries based on user input.
 * Created by author on 05-03-2015.
 * @author Rajasekhar
 */
public class QueryGeneratorService implements IService {

    private static final Logger logger = LoggerFactory.getLogger(QueryGeneratorService.class);
    private Map<String, String> fullyQualifiedNameMap;
    private Dialect dialect = null;

    @Nullable
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JsonObject formDataJson = newFindConfigurationSettings(formData);
        if (formDataJson.isJsonNull()) {
            return null;
        }
        return ServiceUtils.executeService(type, serviceType, service, formDataJson.toString());
    }

    /**
     * Finds the configuration settings for query generation.
     *
     * @param formData The form data for the query generation.
     * @return The JSON object containing the configuration settings.
     */
    @Nullable
    public JsonObject newFindConfigurationSettings(String formData) {

        JsonObject formDataJson = JsonParser.parseString(formData).getAsJsonObject();


        String uniqueId = null;
        if (formDataJson.has("uniqueId")) {
            uniqueId = formDataJson.get("uniqueId").getAsString();
        }

        String metadataExtension = JsonUtils.getMetadataExtension();
        String metadataFile;
        String directory = null;

        try {
            File resource = null;
            Metadata metadata;
            try {
                metadataFile = formDataJson.get("metadataFileName").getAsString();
                directory = formDataJson.get("location").getAsString();

                metadata = MetadataDBUtility.getMetadata(directory, metadataFile);
            } catch (Exception ex) {
                if (uniqueId == null) {
                    throw new RequiredParameterIsNullException("Parameter uniqueId is null and the parameters " +
                            "metadataFileName and location are not provided.");
                }
                File tempDirectory = TempDirectoryCleaner.getTempDirectory();
                metadataFile = tempDirectory.getAbsolutePath() + File.separator + uniqueId + "." + metadataExtension;
                resource = new File(metadataFile);
                metadata = JaxbUtils.unMarshal(Metadata.class, resource);
            }


           

            /*Accumulate the json with file content as you don't have to read the file
             * time and again*/
            boolean isCached  = false;
            if (!formDataJson.has("metadataFileJson")) {
                Metadata mdJsonCandidate;
                if (resource == null) {
                    mdJsonCandidate = MetadataDBUtility.getMetadata(directory, metadataFile);
                } else {
                    mdJsonCandidate = JaxbUtils.unMarshal(Metadata.class, resource);
                }

                MultiConnectionMergeAdhocTable mc = new MultiConnectionMergeAdhocTable(mdJsonCandidate);
                mc.merge();
                JsonObject asJsonObject = JsonParser.parseString(new Gson().toJson(mdJsonCandidate)).getAsJsonObject();
                GsonUtility.accumulate(formDataJson, "metadataFileJson", asJsonObject);
                GsonUtility.accumulate(formDataJson,"dbIdDbName", mc.getDbIdDbNameMap()); 
                fullyQualifiedNameMap =  mc.getFullyQualifedColumnMap();
                isCached = mdJsonCandidate.getCached() != null ? mdJsonCandidate.getCached() : false;
                formDataJson.addProperty("multiConnection", isCached);
                metadata = mdJsonCandidate;
            }
            
            ConnectionDetails connectionDetails = metadata.getConnectionDetails();
            String connectionType = connectionDetails.getConnectionType();
            String dialectOfDatabase = connectionDetails.getDialect();
            Properties properties = new Properties();
            properties.put("hibernate.dialect", dialectOfDatabase);
            dialect = DialectHelper.getDialect(properties);
            
            convertToMultiConnection(formDataJson,isCached);
            modifyFormData(formDataJson);

            QueryGeneratorUtility queryGeneratorUtility = SqlQueryUtilities.getQueryGeneratorUtility(connectionType);

            if (logger.isDebugEnabled()) {
                logger.debug("Debug: " + queryGeneratorUtility);
            }

            /*Validate the configuration of the xml*/
            if (queryGeneratorUtility == null) {
                throw new ConfigurationException(String.format("There is no " +
                        "query generator implementations configuration for the type %s. Configure" +
                        "the application properly. Check for typos.", connectionType));
            }

            String queryGeneratorImplementation = queryGeneratorUtility.getImplementationClazz();
            String classifier = queryGeneratorUtility.getClassifier();

            Map<String, String> parameters = new HashMap<>();
            parameters.put("classifier", classifier);
            parameters.put("queryGeneratorImplementation", queryGeneratorImplementation);

            try {
                ControllerUtils.checkForNullsAndEmptyParameters(parameters);
            } catch (RequiredParameterIsNullException exception) {
                throw new ConfigurationException("The application settings are wrongly " + "configured.", exception);
            }
            if (!formDataJson.has("classifier")) {
                GsonUtility.accumulate(formDataJson,"classifier", classifier);
            }
            GsonUtility.accumulate(formDataJson,"queryGeneratorImplementation", queryGeneratorImplementation);
        } catch (Exception ex) {
            throw new EfwServiceException(ExceptionUtils.getRootCauseMessage(ex));
        }
        return formDataJson;
    }
    /**
     * Converts the form data into multi-connection format if needed.
     *
     * @param formDataJson 		 JSON object containing the form data.
     * @param isDumped     		 Boolean indicating whether the data is dumped.
     */
	private void convertToMultiConnection(JsonObject formDataJson, Boolean isDumped) {

//      boolean multiConnection = formDataJson.optBoolean("multiConnection", false);
//      if (!multiConnection) return;
		String[] lookupArray = { "columns", "filters", "having" };
		for (String lookup : lookupArray) {
			modifyColumn(GsonUtility.optJsonArray(formDataJson, lookup), isDumped);
		}
		JsonObject functionJson = GsonUtility.optJsonObject(formDataJson, "functions");
		if (functionJson != null) {
			JsonArray aggregate = GsonUtility.optJsonArray(functionJson, "aggregate");
			modifyColumn(aggregate, isDumped);
		}

	}
   
	/**
     * Modifies the columns in the JSON array based on whether the data is dumped or not.
     *
     * @param columnArray    JSON array containing the columns.
     * @param isDumped       Boolean indicating whether the data is dumped.
     */
	private void modifyColumn(JsonArray columnArray,boolean isDumped) {
		if (columnArray != null) {
			for (Object i : columnArray) {
				JsonObject item = (JsonObject) i;
				String colName = "";
				String colId = "";
				
				String alias = GsonUtility.optString(item,"alias");
				if(!StringUtils.isBlank(alias)  && (alias.contains(String.valueOf(dialect.openQuote()))
						|| alias.contains(String.valueOf(dialect.closeQuote())))) {
					throw new EfwServiceException("Special characters "+dialect.openQuote()+" or " + dialect.closeQuote()+" not  supported with the selected database.");
				}
				
				try {
					
					JsonObject column = item.get("column").getAsJsonObject();
					colId = column.get("id").getAsString();
					colName = column.get("name").getAsString();
					
				}
				catch (Exception e) {
					colName = item.get("column").getAsString();
				}
				if (isDumped) {
					if(StringUtils.isBlank(colId)) {
						throw new EfwServiceException("Please provide column id.");
					}
					item.addProperty("column", fullyQualifiedNameMap.get(colId));
				}
				else {
					item.addProperty("column", colName);
				}
			}
		}
    }
	/**
     * Modifies the form data by replacing column names with their original names.
     *
     * @param formDataJson 			 JSON object containing containing tables, database, name etc
     */
	public void modifyFormData(JsonObject formDataJson) {
        Map<String, String> duplicateOriginalMap = new HashMap<>();
        Map<String, String> duplicateOriginalMapTable = new HashMap<>();
        JsonObject metadataFileJson = formDataJson.get("metadataFileJson").getAsJsonObject();
        JsonObject database = metadataFileJson.get("database").getAsJsonObject();
        String databaseName = database.get("name").getAsString();
        if (!databaseName.equals("")) {
            databaseName += ".";
        }
        JsonObject tables = database.getAsJsonObject("tables");
        JsonArray tableList = tables.getAsJsonArray("tableList");
        for (int index = 0; index < tableList.size(); index++) {
            JsonObject table = tableList.get(index).getAsJsonObject();
            JsonObject columnsHolder = table.getAsJsonObject("columns");
            if (columnsHolder.size() == 0) {
                continue;
            }
            JsonArray columns = columnsHolder.getAsJsonArray("column");
            String tableName = table.get("name").getAsString();
            String tableOriginalName;
            if (table.has("originalName")) {
                tableOriginalName = table.get("originalName").getAsString();
                duplicateOriginalMapTable.put(databaseName + tableName, databaseName + tableOriginalName);

            }
            for (int jndex = 0; jndex < columns.size(); jndex++) {
                JsonObject column = columns.get(jndex).getAsJsonObject();
                String name = column.get("name").getAsString();
                if (column.has("originalName")) {
                    String originalName = column.get("originalName").getAsString();
                    String columnsDuplicateName = databaseName + tableName + "." + name;
                    String columnsOriginalName = databaseName + tableName + "." + originalName;
                    duplicateOriginalMap.put(columnsDuplicateName, columnsOriginalName);
                }
            }
        }


        JsonArray columns = formDataJson.getAsJsonArray("columns");
        replaceColumnWithOriginal(duplicateOriginalMap, columns, databaseName);


        if (formDataJson.has("filters")) {
            JsonArray filtersArray = formDataJson.getAsJsonArray("filters");
            replaceColumnWithOriginal(duplicateOriginalMap, filtersArray, databaseName);
        }

        if (formDataJson.has("functions")) {
            JsonObject functions = formDataJson.getAsJsonObject("functions");
            if (functions.has("aggregate")) {
                JsonArray aggregate = functions.getAsJsonArray("aggregate");
                replaceColumnWithOriginal(duplicateOriginalMap, aggregate, databaseName);
            }
        }


        if (formDataJson.has("having")) {
            JsonArray filtersArray = formDataJson.getAsJsonArray("having");
            replaceColumnWithOriginal(duplicateOriginalMap, filtersArray, databaseName);
        }
        Gson gson =new Gson();//here map is converted to JsonObject
        formDataJson.add("duplicateTableMap", gson.toJsonTree(duplicateOriginalMapTable).getAsJsonObject());
        formDataJson.add("duplicateOriginalMap", gson.toJsonTree(duplicateOriginalMap).getAsJsonObject());


    }

	
	/**
     * Replaces column names with their original names based on the duplicate-original map.
     *
     * @param duplicateOriginalMap 		 map containing duplicate and original column names.
     * @param filtersArray         		 JSON array containing the filters.
     * @param databaseName         		 name of the database.
     */
	public void replaceColumnWithOriginal(Map<String, String> duplicateOriginalMap, JsonArray filtersArray, String databaseName) {
        for (int index = 0; index < filtersArray.size(); index++) {
            JsonObject filterColumn = filtersArray.get(index).getAsJsonObject();
            String column = filterColumn.get("column").getAsString();
            String checkOriginal = duplicateOriginalMap.get(column);
            if (checkOriginal != null) {
                filterColumn.addProperty("column", checkOriginal);
                String originalTableWithoutDatabaseName = checkOriginal.substring(databaseName.length());
                String aliasedTableWithoutDatabaseName = column.substring(databaseName.length());
                if (filterColumn.has("databaseFunction")) {
                    String databaseFunction = filterColumn.get("databaseFunction").getAsString();
                    String newDatabaseFunction = databaseFunction.replaceAll(aliasedTableWithoutDatabaseName, originalTableWithoutDatabaseName);
                    filterColumn.remove("databaseFunction");
                    filterColumn.add("databaseFunction", JsonParser.parseString(newDatabaseFunction).getAsJsonObject());
                }
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}