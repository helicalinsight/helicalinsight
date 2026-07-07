package com.helicalinsight.adhoc;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.IncompleteFormDataException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.CollectionUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;


/**
 * The {@code DerivedTableSaveHandler} class is an implementation of the {@link IComponent} interface,
 * designed to handle the saving of derived tables. It provides functionality to save a new view with specified
 * metadata, labels, and other related information.
 *
 * @author Rajasekhar
 * Created by author on 09-09-2015.
 */
@SuppressWarnings("unused")
public class DerivedTableSaveHandler implements IComponent {
	/**
     * it save the new derived table based on the provided form data.
     * The saved table information is then returned as a JSON-formatted string.
     *
     * @param jsonFormData 		 				form data containing parameters for saving the derived table.
     * @return string containing 				information about the saved derived table.
     * @throws IncompleteFormDataException 		If required parameters are missing in the form data.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();

        String viewName;
        JsonArray labels;
        String query;
        try {
            viewName = formData.get("viewName").getAsString();
            labels = formData.getAsJsonArray("labels");
            query = formData.get("query").getAsString();
        } catch (Exception ex) {
            throw new IncompleteFormDataException(ex);
        }

        String uuid = null;
        if (formData.has("viewId")) {
            uuid = formData.get("viewId").getAsString();
        }

		Map<String, String> parameters = new HashMap<>();

		parameters.put("viewName", viewName);
		parameters.put("labels", labels.toString());
		parameters.put("query", query);

        if (uuid != null) {
            parameters.put("viewId", uuid);
        } else {
            uuid = UUID.randomUUID().toString();
        }
        Boolean hasStored = GsonUtility.optBoolean(formData,"hasStoredProcedure");
        Map<String, String> typeMap = new HashMap<>();
        JsonArray typeArray = formData.getAsJsonArray("typeArray");
        if (typeArray != null && !typeArray.isEmpty()) {
            for (int index = 0; index < typeArray.size(); index++) {
                JsonObject item = typeArray.get(index).getAsJsonObject();
                typeMap.put(item.get("paramName").getAsString(), item.get("paramValue").getAsString());
            }
        }

/*
        if (labels.size() == 0 || labels.isEmpty()) {
            throw new MetadataServiceException("No view labels are selected. Please select at least one column");
        }
*/

        ControllerUtils.checkForNullsAndEmptyParameters(parameters);

        Map<String, String> map = AdhocUtils.getDataTypeMapping();
        String queryType = GsonUtility.optStringValue(formData,"queryType","conditionIf");
        Table table = save(viewName, query, labels, uuid, map, hasStored, typeMap, queryType);

        JsonObject model;
        model = new JsonObject();

        GsonUtility.accumulate(model,"viewId", uuid);
        GsonUtility.accumulate(model,"tables", getJsonObject(table, map));
        return model.toString();
    }
    /**
     * Saves a new derived table with the specified metadata, labels, and related information.
     *
     * @param viewName   		 name of the derived view.
     * @param query      		 query associated with the derived view.
     * @param labels     		 labels or columns selected for the derived view.
     * @param uuid       		 UUID of the derived view.
     * @param map        		 mapping of data types.
     * @param hasStored  		 boolean indicating whether the derived view has stored procedures.
     * @param typeMap    		 mapping of parameter names to parameter values.
     * @param queryType  		 type of the query associated with the derived view.
     * @return saved Table object representing the derived table.
     */
    private Table save(String viewName, String query, JsonArray labels, String uuid, Map<String, String> map, Boolean hasStored, Map<String, String> typeMap, String queryType) {
    	View view = getView(uuid, query);

		View.Query viewQuery = ApplicationContextAccessor.getBean(View.Query.class);
		viewQuery.setQuery(query);
		viewQuery.setType(queryType);

		view.setQuery(viewQuery);
		view.setId(uuid);
		view.setName(viewName);
		view.setAlias(viewName);
		view.setHasStoredProcedure(hasStored);

		Table dbView = getDBView(uuid);
		Table table = Optional.ofNullable(view.getTable()).orElse(ApplicationContextAccessor.getBean(Table.class));
		table.setId(uuid);
		table.setName(viewName);
		table.setAliasName(viewName);
		table.setType("view");
		Map<String, String> columnNameMap = new HashMap<>();
		if (dbView != null) {
			Columns dbColumns = dbView.getColumns();
			List<Column> columnList = dbColumns.getColumn();
			for (Column col : columnList) {
				columnNameMap.put(col.getName(), col.getId());
			}
		}
		Columns columns = Optional.ofNullable(table.getColumns()).orElse(ApplicationContextAccessor.getBean(Columns.class));
		Map<String,Column> columnMap = makeColumnMap(columns);
		
		List<Column> columnList = new ArrayList<>();
		
		
		for (JsonElement object : labels) {
			JsonObject selected = object.getAsJsonObject();

			String columnName = selected.get("name").getAsString();
			String dbType = selected.get("type").getAsString();
			String type = CollectionUtils.getKeyByValue(map, dbType);
			Column column = Optional.ofNullable(columnMap.get(columnName)).orElse(ApplicationContextAccessor.getBean(Column.class));
			column.setType(type);
			column.setName(JsonUtils.replaceSpecialCharacter(columnName));
			column.setId(StringUtils.isBlank(column.getId()) ? columnNameMap.getOrDefault(columnName, AdhocUtils.getUuid()) :column.getId());
			column.setAliasName(JsonUtils.replaceSpecialCharacter(columnName));
			columnList.add(column);
		}

		columns.setColumn(columnList);
		table.setColumns(columns);

		view.setTable(table);

		File tempDirectory = TempDirectoryCleaner.getTempDirectory();

		synchronized (DerivedTableSaveHandler.class) {
			JaxbUtils.marshal(view, new File(tempDirectory.getAbsolutePath() + File.separator + uuid + ".xml"));
		}

		return table;
		
    }

	
    /**
     * Creates a JSON object containing information about the saved derived table.
     *
     * @param table 		Table object representing the derived table.
     * @param map   		mapping of data types.
     * @return A JSON object containing information about the saved derived table.
     */
    private JsonObject getJsonObject(Table table, Map<String, String> map) {
        Columns columns = table.getColumns();
        JsonObject tables = new JsonObject();
        JsonObject tableJson = new JsonObject();

        JsonObject columnsJson = new JsonObject();

        List<Column> columnList = null;
        if (columns != null) {
            columnList = columns.getColumn();
        }
        if (columnList != null) {
            for (Column aColumn : columnList) {
                JsonObject columnJson = new JsonObject();
                GsonUtility.accumulate(columnJson,"alias", aColumn.getAliasName());
                GsonUtility.accumulate(columnJson,"id", aColumn.getId());
                GsonUtility.accumulate(columnJson,"columnId", aColumn.getId());

                JsonObject type = new JsonObject();
                String javaType = aColumn.getType();

                GsonUtility.accumulate(type,javaType, map.get(javaType));
                GsonUtility.accumulate(columnJson,"type", type);
                GsonUtility.accumulate(columnsJson,aColumn.getName(), columnJson);
            }
        }

        GsonUtility.accumulate(tableJson,"id", table.getId());
        GsonUtility.accumulate(tableJson,"type", table.getType());
        GsonUtility.accumulate(tableJson,"alias", table.getAliasName());
        GsonUtility.accumulate(tableJson,"columns", columnsJson);
        GsonUtility.accumulate(tables,table.getName(), tableJson);
        return tables;
    }
    
    
    /**
	 * Finds if xml file exists with provided uuid
	 *  and returns the same xml data if query matches,  returns new otherwise.
	 * @param uuid  - xml file name or viewId
	 * @param query - new query provided
	 */
	private final View getView(String uuid, String query) {
	    return Optional.ofNullable(Path.of(TempDirectoryCleaner.getTempDirectory().toString(), uuid + ".xml").toFile())
	                   .filter(File::exists)
	                   .map(file -> JaxbUtils.unMarshal(View.class, file))
	                   .filter(existingView -> existingView.getQuery().getQuery().equalsIgnoreCase(query))
	                   .orElse(ApplicationContextAccessor.getBean(View.class));
	}

	

	private final Table getDBView(String uuid) {
		return StringUtils.isNumeric(uuid) ?  Optional.of(ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class)
				.findTableById(Integer.valueOf(uuid)))
				.orElse(null): null;
	}
	
	private final Map<String, Column> makeColumnMap(@NotNull Columns columns) {
		final Map<String, Column> columnMap = new HashMap<>();
		Optional.ofNullable(columns)
				.map(Columns::getColumn)
				.ifPresent(colList -> colList.forEach(col -> columnMap.put(col.getName(), col)));
		return columnMap;
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
