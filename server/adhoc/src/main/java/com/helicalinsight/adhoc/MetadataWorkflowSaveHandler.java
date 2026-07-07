package com.helicalinsight.adhoc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadata;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataServiceException;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class, `MetadataWorkflowSaveHandler`,  It implements the `{@link IComponent} interface,
 * 
 * The primary purpose of this class is to manage the requests related to the saving and updating of
 * metadata, includes the addition or removal of columns,joins in tables.
 *
 * @author Rajasekhar
 * @since 26-06-2015
 */
@SuppressWarnings("unused")
public class MetadataWorkflowSaveHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(MetadataWorkflowSaveHandler.class);
    /**
     * Executes the component logic for saving or updating metadata workflows based on the provided JSON form data.
     * Handles scenarios involving the addition or removal of columns in tables using `AddRemoveTableColumnsHandler`.
     * Verifies and extracts metadata information from the form data for further processing.
     * Utilizes the configured metadata implementation to fetch metadata information.
     * Merges new joins with existing joins and updates the metadata accordingly.
     *
     * @param jsonFormData 				string containing the form data for saving or updating metadata.
     * @return A JSON-formatted string representing the updated metadata after processing the form data.
     * @throws EfwServiceException 		If there is an issue in processing the metadata workflow.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        JsonObject updatedMetadata = null;
        JsonObject updatedMdMd = null;
        try {

            if (formData.has("uniqueId") || (formData.has("metadata") && formData.getAsJsonObject("metadata").has("uniqueId")) || (formData.has("location") && formData.has("metadataFileName"))) {
                //Handle the case for add remove columns
                JsonObject formDataMetadata=formData.getAsJsonObject("metadata");
                String response = AddRemoveTableColumnsHandler.handleAddRemove(formData);
                if (response != null) {
                    updatedMetadata = JsonParser.parseString(response).getAsJsonObject();
                    updatedMdMd = updatedMetadata.getAsJsonObject("metadata");
                    if(formDataMetadata==null || formDataMetadata.entrySet().isEmpty()){
                        return response;
                    }
                    JsonArray catalogs = formDataMetadata.getAsJsonArray("catalogs");

                    if (catalogs.size() < 1) {
                        return response;
                    }
                    if (catalogs.size() == 1) {
                        if (catalogs.get(0).getAsJsonObject().getAsJsonArray("schemas").size() < 1) {
                            return response;
                        }
                    }

                }

            }


            JsonObject metadata = GsonUtility.optJsonObject(formData,"metadata");
            if (metadata == null || metadata.entrySet().isEmpty()) {
                throw new MetadataServiceException("The metadata parameter is null or empty");
            }

            JsonArray catalogs = metadata.getAsJsonArray("catalogs");

            if (catalogs.size() < 1) {
                throw new EfwServiceException("There is no selected schema information. Please select a catalog " +
                        "and/or schema.");
            }
        } catch (MetadataServiceException ex) {
            throw new MalformedJsonException("The metadata json is malformed and some of the required parameters" + "" +
                    " are missing or null");
        }

        String metadataImplementation = formData.get("metadataImplementation").getAsString();
        IMetadata iMetadata = FactoryMethodWrapper.getTypedInstance(metadataImplementation, IMetadata.class);
        if (iMetadata == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is incorrect.");
        }

        String metadata = iMetadata.getMetadata(jsonFormData);
        if (logger.isDebugEnabled()) {
            logger.debug("The metadata implementation has completed processing.");
        }

        if (updatedMdMd == null) {
            return metadata;
        }
        //merge the new metadata and relationship
        JsonObject metadataJSON = JsonParser.parseString(metadata).getAsJsonObject().get("metadata").getAsJsonObject();
        JsonArray newJoins = null;
        JsonArray oldJoins = null;
        if (updatedMdMd.has("joins")) {
            oldJoins = updatedMdMd.getAsJsonArray("joins");
        }
        if (metadataJSON.has("joins")) {
            newJoins = metadataJSON.getAsJsonArray("joins");
        }

        if (newJoins != null && !newJoins.isEmpty()) {

            if (oldJoins == null || oldJoins.isEmpty()) {
                oldJoins = new JsonArray();
                whenNewJoinsFound(formData, newJoins, oldJoins);
                updatedMdMd.add("joins", oldJoins);
                //create new Joins with the new Joins
            } else {

                for (JsonElement item : newJoins) {
                    JsonObject itemRef = item.getAsJsonObject();
                    itemRef.remove("id");
                }

                for (JsonElement item : oldJoins) {
                    JsonObject itemRef = item.getAsJsonObject();
                    itemRef.remove("id");
                    if (newJoins.contains(itemRef)) {
                        newJoins.remove(itemRef);
                    }
                }

                whenNewJoinsFound(formData, newJoins, oldJoins);

                //merge the joins
            }

        }
        if (updatedMetadata != null) {
            return updatedMetadata.toString();
        } else {
            return metadata;
        }
    }

    public void whenNewJoinsFound(JsonObject formData, JsonArray newJoins, JsonArray oldJoins) {
        if (!newJoins.isEmpty()) {
            String absolutePath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            String uniqueId = "";
            String metadataExtension = "." + com.helicalinsight.efw.utility.JsonUtils.getMetadataExtension();
            if (formData.has("location")) {
                String location = formData.get("location").getAsString();
                String metadataFileName = formData.get("metadataFileName").getAsString();
                uniqueId = location + "_temp_" + metadataFileName;
                metadataExtension = uniqueId.endsWith(metadataExtension) ? "" : metadataExtension;
                File tempFile = new File(absolutePath + File.separator + location + "_temp_" + metadataFileName + metadataExtension);
                tempFile.getParentFile().mkdirs();
                if (!tempFile.exists()) {
                    absolutePath = ApplicationProperties.getInstance().getSolutionDirectory();
                    uniqueId = location + File.separator + metadataFileName;
                }
            } else if (formData.has("uniqueId")) {

                uniqueId = formData.get("uniqueId").getAsString();
            } else if (formData.has("metadata") && formData.getAsJsonObject("metadata").has("uniqueId")) {
                uniqueId = formData.getAsJsonObject("metadata").get("uniqueId").getAsString();
            }
            File metadataFile = new File(absolutePath + File.separator +
                    uniqueId + metadataExtension);
            Metadata metadataJaxb = JaxbUtils.unMarshal(Metadata.class, metadataFile);

            Database database = metadataJaxb.getDatabase();
            Relationships relationships = database.getRelationships();
            if (relationships == null) {
                relationships = ApplicationContextAccessor.getBean(Relationships.class);
            }
            List<Relationship> listOfRelations = relationships.getListOfRelations();
            if (listOfRelations == null) {
                listOfRelations = new ArrayList<>();
            }
            Map<String, Relationship> tableRefMap = new HashMap<String, Relationship>();

            for (JsonElement item : newJoins) {
                Relationship relationship = null;
                JsonObject itemRef = item.getAsJsonObject();
                Join newJoin = ApplicationContextAccessor.getBean(Join.class);
               // String uuid = AdhocUtils.getUuid();
               // itemRef.put("id", uuid);


                LeftTable leftTable = ApplicationContextAccessor.getBean(LeftTable.class);
                JsonObject left = itemRef.getAsJsonObject("left");
                leftTable.setColumn(left.get("column").getAsString());
                String table = left.get("table").getAsString();
                leftTable.setTable(table);

                RightTable rightTable = ApplicationContextAccessor.getBean(RightTable.class);
                JsonObject right = itemRef.getAsJsonObject("right");
                rightTable.setColumn(right.get("column").getAsString());
                String refTable = right.get("table").getAsString();
                rightTable.setTable(refTable);

                newJoin.setType(itemRef.get("type").getAsString());
                newJoin.setLeftTable(leftTable);
                newJoin.setRightTable(rightTable);
                newJoin.setOperator(itemRef.get("operator").getAsString());
                newJoin.setId(MetadataUtils.getId(newJoin.getLeftTable().toString(), newJoin.getRightTable().toString(), newJoin.getOperator()));

                String tableRef = table + refTable;
                if (tableRefMap.containsKey(tableRef)) {
                    relationship = tableRefMap.get(tableRef);
                } else {
                    relationship = ApplicationContextAccessor.getBean(Relationship.class);
                }
                List<Join> joinList = relationship.getJoin();
                if (joinList == null) {
                    joinList = new ArrayList<>();
                }

                joinList.add(newJoin);

                relationship.setTable(table);
                relationship.setReferenceTable(refTable);
                relationship.setJoin(joinList);
                tableRefMap.put(tableRef, relationship);

            }

            for (Relationship relationship : tableRefMap.values()) {
                listOfRelations.add(relationship);

            }
            relationships.setListOfRelations(listOfRelations);
            database.setRelationships(relationships);

            oldJoins.addAll(newJoins);
            if(metadataFile.getAbsolutePath().contains("_temp_")){
                metadataFile.getParentFile().mkdirs();
            }
            JaxbUtils.marshal(metadataJaxb, metadataFile);
        }
    }
    /**
     * Checks if the class is thread-safe to be cached. Always returns true for this class.
     * @return Always returns {@code true} as this class is designed to be thread-safe for caching.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
