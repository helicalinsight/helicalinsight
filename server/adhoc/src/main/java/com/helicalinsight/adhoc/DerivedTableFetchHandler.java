package com.helicalinsight.adhoc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


/**
 * DerivedTableFetchHandler is a component responsible for fetching and processing metadata for a derived table
 * system. It implements the {@link IComponent} interface.
 *
 * @author Rajasekhar
 * Created by author on 10-09-2015.
 */
@SuppressWarnings("unused")
public class DerivedTableFetchHandler implements IComponent {
    private Map<String, String> dataTypesMapping = new PropertiesFileReader().read("Admin", "dataTypesMapping.properties");
    /**
     * Executes the component logic to fetch metadata for a derived table and provides a response containing
     * the query, query type, and labels.
     *
     * @param jsonFormData 			JSON string containing form data.
     * @return A JSON string containing the query, query type, and labels.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String location = null;
        String metadataFileName = null;

        String viewId;
        if (!formData.has("viewId")) {
            throw new RequiredParameterIsNullException("Parameter viewId is missing.");
        } else {
            viewId = formData.get("viewId").getAsString();
        }

        if (formData.has("location")) {
            location = formData.get("location").getAsString();
        }
        if (formData.has("metadataFileName")) {
            metadataFileName = formData.get("metadataFileName").getAsString();
        }

        Map<String, String> parameters = new HashMap<>();

        parameters.put("viewId", viewId);
        if (location != null) {
            parameters.put("location", location);
        }

        if (metadataFileName != null) {
            parameters.put("metadataFileName", metadataFileName);
        }

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
        String connectionId=GsonUtility.optString(formData,"connectionId");

        View view = getView(location, metadataFileName, viewId,connectionId);

        return response(formData, view);
    }
    /**
     * Returns the view using provided parameters .
     *
     * @param location         		 location of the metadata.
     * @param metadataFileName 		 metadata file name.
     * @param viewId           		 view ID.
     * @param connectionId     		 connection ID.
     * @return The retrieved view.
     */
    private View getView(String location, String metadataFileName, String viewId,String connectionId) {
        View view;
        File file = new File(TempDirectoryCleaner.getTempDirectory().getAbsolutePath() + File.separator + viewId + "" +
                ".xml");
        if (metadataFileName == null && location == null) {
            check(viewId, file);
            view = JaxbUtils.unMarshal(View.class, file);
        } else {
            //Always give priority to the view in the temp location. Else search for the same in metadata file
            if (file.exists()) {
                view = JaxbUtils.unMarshal(View.class, file);
            } else {
                view = fetchViewFromMetadata(location, metadataFileName, viewId,connectionId);
            }
        }
        return view;
    }
    /**
     * Processes the view and generates a response containing the query, query type, and labels.
     *
     * @param formData 		 	form data.
     * @param view     			view.
     * @return A JSON string containing the query, query type, and labels.
     */
    private String response(JsonObject formData, View view) {
        ViewLabelsRetrievalComponent component = new ViewLabelsRetrievalComponent();

        String query = view.getQuery().getUnprocessedQuery();
        GsonUtility.accumulate(formData,"query", query);
        JsonObject model = new JsonObject();
        GsonUtility.accumulate(model,"query", query);
        String type = view.getQuery().getType();
        formData.addProperty("queryType", type);
        GsonUtility.accumulate(model,"queryType", type);

        String result = component.executeComponent(formData.toString());
        JsonObject json = JsonParser.parseString(result).getAsJsonObject();
        JsonArray labels = json.getAsJsonArray("labels");
        filterLabels(view, labels);
        model.add("labels", labels);
        if (labels.size() == 0) {
            populateExistingTable(view, labels);
            model.add("labels", labels);
        }
        return model.toString();
    }
    /**
     * Filters labels based on columns in the view.
     *
     * @param view   		 retrieved view.
     * @param labels 		 JSON array of labels to be filtered.
     */
	private void filterLabels(View view, JsonArray labels) {
		Columns columns = view.getTable().getColumns();
		if (null != columns) {
			Map<String, Column> columnMap = columns.getColumn().stream()
					.collect(Collectors.toMap(Column::getName, Function.identity()));
			for (JsonElement object : labels) {
				String labelName =  object.getAsJsonObject().get("name").getAsString();
				boolean checked = columnMap.containsKey(labelName);
				((JsonObject) object).addProperty("checked", checked);
			}
		}
		else { // columns null means no label selected , so marking every label checked value to  false.
			for (JsonElement object : labels) {
				((JsonObject) object).addProperty("checked", false);
			}
		}
	}
	 /**
     * Populates labels from existing columns in the view's table.
     *
     * @param view   			 retrieved view.
     * @param labels 			 JSON array of labels to be populated.
     */
    private void populateExistingTable(View view, JsonArray labels) {
        Table table = view.getTable();
        Columns columns = table.getColumns();
        if (columns != null) {
            List<Column> columnList = columns.getColumn();
            if (columnList != null) {
                for (Column column : columnList) {
                    String aliasName = column.getAliasName();
                    String columnType = column.getType();
                    JsonObject object = new JsonObject();
                    object.addProperty("name", aliasName);
                    object.addProperty("type", dataTypesMapping.get(columnType));
                    labels.add(object);
                }
            }
        }
    }
    /**
     * Checks if the specified view file exists in the temporary directory and throws an exception if not found.
     *
     * @param viewId 				ID of the view to check for existence.
     * @param file   				File object representing the view file in the temporary directory.
     * @throws EfwServiceException  If the view file is not found in the temporary directory.
     */
    private void check(String viewId, File file) {
        if (!file.exists()) {
            throw new EfwServiceException("The viewId requested " + viewId + " is not found.");
        }
    }
    /**
     * Fetches the view from the metadata file or database based on the provided location, metadata file name,
     * and view ID. If the view is not found in the metadata file, an attempt is made to retrieve it from the database.
     *
     * @param location         		 location of the metadata file.
     * @param metadataFileName 		 name of the metadata file.
     * @param viewId           		 ID of the view to fetch.
     * @param connectionId     		 ID of the connection (optional).
     * @return The fetched View object.
     * @throws RequiredParameterIsNullException 	If one of the required parameters (metadataFileName, location) is missing.
     * @throws ResourceNotFoundException        	If the specified metadata resource does not exist.
     */
    private View fetchViewFromMetadata(String location, String metadataFileName, String viewId,String connectionId) {

        if (metadataFileName == null || location == null) {
            throw new RequiredParameterIsNullException("One of the required parameters " + "metadataFileName and/or " +
                    "location are/is missing.");
        }
        File file = new File(ApplicationProperties.getInstance().getSolutionDirectory() +
                File.separator + location + File.separator + metadataFileName);

        HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
        HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
        if(metadataResource==null){
            throw new ResourceNotFoundException("The Resource does not exists");
        }
        Metadata metadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());

        Database database = metadata.getDatabase();
        if(StringUtils.isNoneBlank(connectionId)) {
           database= MetadataUtils.getNewMetadata(connectionId, metadata).getDatabase();
        }
        return getViewFromDatabase(viewId, database);
    }
    /**
     * Retrieves the specified view from the provided database object. If the view is found in the list of views associated
     * with the provided database, it is returned. If the view is not found in the database, an attempt is made to retrieve
     * it from the temp directory.
     *
     * @param viewId   			 ID of the view to retrieve.
     * @param database 			 Database object containing information about views.
     * @return  {@code View} object.
     * @throws IllegalArgumentException 	If the requested viewId is not found in either the database or the temp directory.
     */
    private View getViewFromDatabase(String viewId, Database database) {
        View view = null;
        Views views = database.getViews();
        List<View> viewList;
        if (views == null) {
            view = fromTempDirectory(viewId);
            if (view == null) {
                throw new IllegalArgumentException("Requested viewId is not found in the file");
            } else {
                return view;
            }
        } else {
            viewList = views.getViewList();
        }

        if (viewList == null) {//An empty views tag exists. Error condition.
            throw new IllegalArgumentException("Requested viewId is not found in the file ");
        }

        for (View aView : viewList) {
            if (aView.getId().equals(viewId)) {
                view = aView;
                break;
            }
        }

        if (view == null) {
            //Oops not found in metadata file also. Probably newly created one
            view = fromTempDirectory(viewId);
        }

        if (view == null) {
            throw new IllegalArgumentException("Requested viewId is not found in the file");
        }

        //Populate the table information on existing view.
        if (view.getTable() == null) {
            List<Table> tableList = database.getTables().getTableList();
            for (Table table : tableList) {
                if (table.getId().equalsIgnoreCase(viewId)) {
                    view.setTable(table);
                }
            }
        }
        return view;
    }
    /**
     * Checks whether the execution of this component is thread-safe for caching purposes.
     * @return True if the component is thread-safe, false otherwise.
     */
    private View fromTempDirectory(String viewId) {
        View view;
        File viewFile = new File(TempDirectoryCleaner.getTempDirectory() + File.separator + viewId + ".xml");
        if (!viewFile.exists()) {
            throw new IllegalArgumentException("Requested viewId is not found in the file");
        }
        view = JaxbUtils.unMarshal(View.class, viewFile);
        return view;
    }
    /**
     * Determines whether the component is thread-safe to cache.
     * @return {@code true} if the component is thread-safe to cache, {@code false} otherwise.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
