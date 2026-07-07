package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.genericsql.ClusteringAlgorithm;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Provides JSON representation of ad-hoc views based on metadata and form data.
 * This component is responsible for generating JSON representation of ad-hoc views
 * using metadata information and form data provided by the user.
 * 
 * Created by author on 29-06-2015.
 * @author Rajasekhar
 */
@Component
public class AdhocViewJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(AdhocViewJsonProvider.class);
    /**
     * Retrieves the file location based on the form data JSON object.
     * This method extracts the location and metadata file name from the form data
     * and constructs the file location accordingly.
     *
     * @param formDataJson 		 JSON object containing location and metadataFileName.
     * @return The file location based on the form data.
     */
    private File getFileLocation(JsonObject formDataJson) {
        if (formDataJson.has("location") && formDataJson.has("metadataFileName")) {
            String metadataFileName = formDataJson.get("metadataFileName").getAsString();
            String location = formDataJson.get("location").getAsString();
            return new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator
                    + location + File.separator + metadataFileName);
        }
        return null;
    }
    /**
     * Generates JSON representation of ad-hoc views.
     * This method takes metadata and form data as input and generates JSON representation
     * of ad-hoc views including tables, columns, joins, and other related information.
     *
     * @param metadata 			 metadata object containing database and table information.
     * @param formData 			 JSON object containing form data provided by the user.
     * @return The JSON representation of ad-hoc views.
     */
    public String adhocViewJson(@NotNull Metadata metadata, @NotNull JsonObject formData) {
        String absolutePath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File.separator;
        Database database = metadata.getDatabase();
        String databaseName = database.getName();
        String catalog = database.getCatalog();
        String schema = database.getSchema();
        JsonObject result;
        Boolean olderVersion = false;
        Map<String, String> dataTypesMapping = AdhocUtils.getDataTypeMapping();
        JsonObject jsonOfAllTables = new JsonObject();
        boolean provideColumns = true;
        if (formData.has("provideColumns")) {
            provideColumns = formData.get("provideColumns").getAsBoolean();
        }
        boolean enabled = areDefaultFunctionsEnabled();

        Tables tables = database.getTables();
        if (tables != null) {
            List<Table> tableList = tables.getTableList();
            if (tableList != null) {

                File file = JsonUtils.defaultFunctionsFile();
                Properties properties = ConfigurationFileReader.getPropertiesFromFile(file);

                for (Table table : tableList) {
                    JsonObject tableJson = new JsonObject();

                    JsonObject columnJson = null;
                    String tableName = table.getName();

                    if (provideColumns) {
                        columnJson = new JsonObject();
                        Columns columns = table.getColumns();

                        //FIX: Fixed null pointer. Condition to check whether the database has table's
                        // without columns
                        if (columns != null) {
                            List<Column> actualColumns = columns.getColumn();
                            JsonObject column;
                            if (actualColumns != null) {
                                for (Column aColumn : actualColumns) {
                                    column = new JsonObject();
                                    GsonUtility.accumulate(column,"alias", aColumn.getAliasName());
                                    String aColumnName = aColumn.getName();
                                    String aColumnId = aColumn.getId();
                                    if (aColumn.getOriginalName() != null) {
                                        GsonUtility.accumulateBoolean(column,"duplicate", true);
                                        GsonUtility.accumulate(column,"originalName", aColumn.getOriginalName());
                                    }
                                    if (aColumnId == null) {
                                        String uuid = AdhocUtils.getUuid();
                                        aColumn.setId(uuid);
                                        aColumnId = uuid;
                                        olderVersion = true;
                                    }
                                    GsonUtility.accumulate(column,"fullyQualifiedColumn", tableName + "." + aColumnName);
                                    GsonUtility.accumulate(column,"id", aColumnId);
                                    String type = aColumn.getType();

                                    if (enabled) {
                                        String defaultFunction = aColumn.getDefaultFunction();

                                        //For old metadata files there are no default functions
                                        //So by checking the type provide a default function for such
                                        if (defaultFunction == null) {
                                            String functionFromFile = properties.getProperty(type);
                                            if (functionFromFile != null) {
                                                GsonUtility.accumulate(column,"defaultFunction", functionFromFile);
                                            }
                                        } else {
                                            GsonUtility.accumulate(column,"defaultFunction", defaultFunction);
                                        }
                                    }
                                    JsonObject typeObj = JsonParser.parseString(AdhocUtils.getType(type, dataTypesMapping)).getAsJsonObject();
                                    GsonUtility.accumulate(column,"type", typeObj);
                                    GsonUtility.accumulate(columnJson,aColumnName, column);
                                }
                            }
                        }
                    }

                    String type = table.getType();
                    if (type != null) {
                        GsonUtility.accumulate(tableJson,"type", type);
                    }


                    String id = table.getId();
                    if ("view".equalsIgnoreCase(type)) {
                        String requiredPath = absolutePath + id + ".xml";
                        File fileTemp = new File(requiredPath);
                        if (fileTemp.exists()) {
                            fileTemp.delete();
                        }
                    }
                    String originalName = table.getOriginalName();
                    if(id.contains("-") && table.getType()==null && table.getOriginalName()==null){
                        id=MetadataUtils.getId(database.getCatalog(), database.getSchema(), table.getName());
                        olderVersion=true;
                        table.setId(id);
                    }
                    GsonUtility.accumulate(tableJson,"id", id);
                    GsonUtility.accumulate(tableJson,"alias", table.getAliasName());
                    tableJson.add("columns", columnJson);
                    if (originalName != null && !originalName.isEmpty()) {
                    	GsonUtility.accumulate(tableJson,"duplicate", "true");
                        GsonUtility.accumulate(tableJson,"originalName", originalName);
                    }
                    tableJson.addProperty("name",tableName);
                    tableJson.addProperty("cacheId", MetadataUtils.getId(catalog, schema, tableName));
                    GsonUtility.accumulate(jsonOfAllTables,tableName, tableJson);
                }
            }
        }

        result = new JsonObject();
        GsonUtility.accumulate(result,"classifier", formData.get("classifier").getAsString());
        GsonUtility.accumulate(result,"name", databaseName);

        JsonObject dataSource = MetadataUtils.dataSourceJson(metadata);
        dataSource.addProperty("dbId", database.getId());
        GsonUtility.accumulate(result,"dataSource", dataSource);


        /*Added inMemory need to move to a separate class*/
        ConnectionDetails connectionDetails = metadata.getConnectionDetails();
        String fetchMode = connectionDetails.getFetchMode();
        String persist = connectionDetails.getPersist();
        String cache = connectionDetails.getCache();

        if (cache != null || persist != null || fetchMode != null) {

            JsonObject inMemory = new JsonObject();
            inMemory.addProperty("middleware", fetchMode != null);
            inMemory.addProperty("persist", persist != null);
            inMemory.addProperty("cache", cache != null);

            GsonUtility.accumulate(result,"inmemory", inMemory);
        }


        if (formData.has("uniqueId")) {
            GsonUtility.accumulate(result,"uniqueId", formData.get("uniqueId").getAsString());
        }

        GsonUtility.accumulate(result,"tables", jsonOfAllTables);
        ClusteringAlgorithm clusteringAlgorithm = new ClusteringAlgorithm(metadata);
        GsonUtility.accumulate(result,"sets", clusteringAlgorithm.connectedComponents());
        if (formData.has("provideJoins") && formData.get("provideJoins").getAsString().equalsIgnoreCase("true")) {
        	String metadataJoins = new MetadataJoinsProvider(metadata).getMetadataJoins();
        	JsonArray asJsonArray = JsonParser.parseString(metadataJoins).getAsJsonArray();
            GsonUtility.accumulate(result,"joins", asJsonArray);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Successfully completed one http request for fetching the metadata of " +
                    "the database " + databaseName);

        }
        if (olderVersion) {
            Relationships relationships = metadata.getDatabase().getRelationships();
            if(relationships!=null){
                List<Relationship> listOfRelations = relationships.getListOfRelations();
                if(listOfRelations!=null){
                    for(Relationship relationship: listOfRelations){
                        List<Join> joins = relationship.getJoin();
                        if(joins!=null){
                            for(Join join:joins){
                                join.setId(MetadataUtils.getId(join.getLeftTable().toString(),join.getRightTable().toString(),join.getOperator()));
                            }
                        }
                    }
                }
            }
            //JaxbUtils.marshal(metadata, getFileLocation(formData));
        }
        return result.toString();
    }
    /**
     * Checks if default functions are enabled.
     * This method retrieves the settings JSON object and checks if ad-hoc default functions
     * are enabled based on the configuration.
     *
     * @return {@code true} if default functions are enabled, {@code false} otherwise.
     */
    private boolean areDefaultFunctionsEnabled() {
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        boolean enabled = false;
        if (settingsJson.has("enableAdhocDefaultFunctions")) {
            if ("true".equalsIgnoreCase(settingsJson.get("enableAdhocDefaultFunctions").getAsString())) {
                enabled = true;
            }
        }
        return enabled;
    }


}
