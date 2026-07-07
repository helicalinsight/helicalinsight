package com.helicalinsight.adhoc.metadata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.genericsql.IMetadataStore;
import com.helicalinsight.adhoc.genericsql.MetadataStoreBuilder;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * MetadataSecurityAccessValidator
 * This class validates security access to metadata. It implements the IComponent interface
 * and provides methods to validate security access expressions and their types.
 * @author Somen
 *  Created on 8/4/2016.
 */
public class MetadataSecurityAccessValidator implements IComponent {

    private String tempLocation = TempDirectoryCleaner.getTempDirectory() + File.separator;
    /**
     * Executes the security access validation component.
     *
     * @param jsonFormData 		 JSON data containing security access information.
     * @return A JSON string containing the response message.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject request = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String uuid = request.get("uuid").getAsString();
        String metadataExtension = JsonUtils.getMetadataExtension();

        String metadataPath;
        Metadata metadata;
        boolean isCreate = false;
        if (uuid.endsWith(metadataExtension)) {
            String location = request.has("location") ? request.get("location").getAsString() + File.separator : "";
            metadataPath = ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + location +
                    uuid;
            metadata= MetadataDBUtility.getMetadata(location, uuid);
        } else {
            metadataPath = this.tempLocation + uuid + "." + metadataExtension;
            isCreate = true;
            this.isFilePresent(metadataPath, "metadata");
            metadata = JaxbUtils.unMarshal(Metadata.class, new File(metadataPath));
        }




        JsonArray expressionArray = request.getAsJsonArray("expression");

        for (JsonElement expression : expressionArray) {
            JsonObject expressionJson = expression.getAsJsonObject();

            this.validateExpressionIdCaseEdit(expressionJson, isCreate);
            String expressionType = expressionJson.get("expressionType").getAsString();
            JsonArray onJSONArray = expressionJson.getAsJsonArray("on");
            String[] viewsArray = null;
            if (expressionJson.has("views")) {
                List<String> viewsInMetadata = ifViewIsPartOfMetadata(metadata);

                JsonArray viewsJSONArray = expressionJson.getAsJsonArray("views");
                viewsArray = handleViewsForMetadata(viewsJSONArray, viewsInMetadata);
            }

            String[] onArray = convertToStringArray(onJSONArray);

            this.handleExpressionType(expressionType, onArray, metadata, viewsArray);
        }

        return null;
    }
    /**
     * Checks if a file is present.
     *
     * @param fileName 			 name of the file.
     * @param type     			 type of file.
     */
    private void isFilePresent(String fileName, String type) {
        File file = new File(fileName);
        if (!(file.exists() && file.isFile())) {
            throw new ResourceNotFoundException("The " + type + " file is  not found");
        }
    }
    /**
     * Validates the expression ID in case of edit operation.
     *
     * @param expressionJson 		 JSON object containing the expression.
     * @param isCreate       		 Indicates if the operation is create or edit.
     */
    private void validateExpressionIdCaseEdit(JsonObject expressionJson, boolean isCreate) {
        if (isCreate && expressionJson.has("expressionId") && "edit".equalsIgnoreCase(expressionJson.get
                ("action").getAsString())) {
            String expressionId = this.tempLocation + expressionJson.get("expressionId").getAsString() + ".xml";
            this.isFilePresent(expressionId, "expression");
        }
    }
    /**
     * Checks if a view is part of metadata.
     *
     * @param metadata 			 metadata object.
     * @return A list of views present in metadata.
     */
    private List<String> ifViewIsPartOfMetadata(Metadata metadata) {
        Views views = metadata.getDatabase().getViews();
        List<String> viewsInMetadata = new ArrayList<>();
        if (views != null) {
            List<View> viewList = views.getViewList();
            for (View view : viewList) {
                viewsInMetadata.add(view.getId());
            }
        }
        return viewsInMetadata;
    }
    /**
     * Handles views for metadata.
     *
     * @param viewsJSONArray    		 JSON array containing views.
     * @param viewsInMetadata   		 list of views present in metadata.
     * @return An array of valid views.
     */
    public String[] handleViewsForMetadata(JsonArray viewsJSONArray, List<String> viewsInMetadata) {
        int size = viewsJSONArray.size();
        ArrayList<String> validViews = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String viewId = viewsJSONArray.get(index).getAsString();
            if (!viewsInMetadata.contains(viewId)) {
                validViews.add(viewId);
            }
        }
        if (validViews.size() == 0) {
            return null;
        }
        return validViews.toArray(new String[0]);
    }
    /**
     * Converts a JSON array to a string array.
     *
     * @param onJSONArray 		 JSON array to convert.
     * @return A string array.
     */
    public String[] convertToStringArray(JsonArray onJSONArray) {
        int size = onJSONArray.size();
        String onArray[] = new String[size];
        for (int index = 0; index < size; index++) {
            onArray[index] = onJSONArray.get(index).getAsString();
        }
        return onArray;
    }
    /**
     * Handles the expression type.
     *
     * @param expressionType 		 type of expression.
     * @param onArray        		 array of entities.
     * @param metadata       		 metadata object.
     * @param viewsArray     		 array of views.
     */
    private void handleExpressionType(String expressionType, String[] onArray, Metadata metadata, String[] viewsArray) {
        Database database = metadata.getDatabase();
        String databaseName = database.getName();
        if (databaseName != null && !"".equalsIgnoreCase(databaseName)) {
            databaseName += ".";
        } else {
            databaseName = "";
        }

        List<String> allVertices = AdhocUtils.allVertices(database);
        IMetadataStore metadataStore = new MetadataStoreBuilder().setMetadata(metadata).createMetadataStore();
        List<String> databaseColumns = metadataStore.getFullyQualifiedColumnsList();
        List<String> derivedColumns = metadataStore.getDerivedTablesColumnsList();

        if ("table".equalsIgnoreCase(expressionType) || "global".equalsIgnoreCase(expressionType)) {
            processTableAndGlobal(onArray, allVertices, viewsArray);
        } else if ("column".equalsIgnoreCase(expressionType)) {
            processColumns(onArray, databaseName, databaseColumns, derivedColumns, viewsArray);
        } else {
            throw new FormValidationException("The expressionType " + expressionType + " is unknown");
        }
    }
    /**
     * Processes tables and global expressions.
     *
     * @param onArray			 array of entities.
     * @param allVertices The list of all vertices.
     * @param viewsArray  The array of views.
     */
    private void processTableAndGlobal(String[] onArray, List<String> allVertices, String[] viewsArray) {
        boolean isPresent;
        List<String> viewTableList = new ArrayList<>();
        prepareTableListFromViews(viewsArray, viewTableList);
        for (String entity : onArray) {
            isPresent = allVertices.contains(entity) || viewTableList.contains(entity);
            whenEntityNotFound(isPresent, entity);
        }
    }
    /**
     * Processes columns expressions.
     *
     * @param onArray         		 array of entities.
     * @param databaseName    		 name of the database.
     * @param databaseColumns 		 list of database columns.
     * @param derivedColumns  		 list of derived columns.
     * @param viewsArray      		 array of views.
     */
    private void processColumns(String[] onArray, String databaseName, List<String> databaseColumns,
                                List<String> derivedColumns, String[] viewsArray) {
        boolean isPresent;
        List<String> viewColumnList = new ArrayList<>();
        prepareColumnListFromViews(databaseName, viewsArray, viewColumnList);

        for (String entity : onArray) {
            String columnName = databaseName + entity;
            isPresent = databaseColumns.contains(columnName) || derivedColumns.contains(columnName) || viewColumnList
                    .contains(columnName);
            whenEntityNotFound(isPresent, entity);
        }
    }
    /**
     * Prepares table list from views.
     *
     * @param viewsArray     		 array of views.
     * @param viewTableList  		 list of view tables.
     */
    private void prepareTableListFromViews(String[] viewsArray, List<String> viewTableList) {
        if (viewsArray != null) {
            for (String viewId : viewsArray) {
                View view = getView(viewId);
                viewTableList.add(view.getName());
            }
        }
    }
    /**
     * Handles the case when an entity is not found.
     *
     * @param isPresent 		Indicates if the entity is present.
     * @param entity    		The name of the entity.
     */
    private void whenEntityNotFound(boolean isPresent, String entity) {
        if (!isPresent) {
            throw new EfwServiceException("The entityName " + entity + " is not present in the metadata");
        }
    }
    /**
     * Prepares column list from views.
     *
     * @param databaseName    The name of the database.
     * @param viewsArray      The array of views.
     * @param viewColumnList  The list of view columns.
     */
    private void prepareColumnListFromViews(String databaseName, String[] viewsArray, List<String> viewColumnList) {
        if (viewsArray != null) {
            for (String viewId : viewsArray) {
                View view = getView(viewId);
                String tableName = view.getName();
                for (Column next : view.getTable().getColumns().getColumn()) {
                    viewColumnList.add(databaseName + tableName + "." + next.getName());
                }
            }
        }
    }
    /**
     * Retrieves a view object by its ID.
     *
     * @param viewId 		 ID of the view.
     * @return The view object.
     */
    private View getView(String viewId) {
        return JaxbUtils.unMarshal(View.class, new File(tempLocation + viewId + ".xml"));
    }
    /**
     * Indicates whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, otherwise {@code false}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
