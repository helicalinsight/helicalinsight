package com.helicalinsight.adhoc.metadata.genericdb;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * Handles the update of views in the metadata.
 * Created by author on 10-09-2015.
 * @author Rajasekhar
 */
@Component
public class ViewsUpdateHandler {

    private final static Logger logger = LoggerFactory.getLogger(ViewsUpdateHandler.class);

    /**
     * Updates views in the metadata.
     * 
     * @param metadata 				 metadata object provides database object.
     * @param viewsArray 			 array of views
     * @param tableAlias 			 map containing table aliases
     * @param columnAlias 			 map containing column aliases
     * @param removeViewColumn 		 list of columns to remove from views
     */
    public void updateViews(Metadata metadata, JsonArray viewsArray, Map<String, String> tableAlias, Map<String, String> columnAlias, List<String> removeViewColumn) {
        Database database = metadata.getDatabase();
        Views views = database.getViews();
        List<View> existingViewsList;

        if (views != null) {
            existingViewsList = views.getViewList();
            if (existingViewsList == null) {
                existingViewsList = new ArrayList<>();
            }
        } else {
            views = ApplicationContextAccessor.getBean(Views.class);
            existingViewsList = new ArrayList<>();
        }

        String tempPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();

        List<Table> tableList = getTables(database);

        List<String> allVertices = AdhocUtils.allVertices(database);
        Iterator<JsonElement> listIterator = viewsArray.iterator();

        List<View> replacedViews = new ArrayList<>();

        while (listIterator.hasNext()) {
            JsonElement element = listIterator.next();
            String viewId = element.getAsString();
            ListIterator<View> existingViewListIterator = existingViewsList.listIterator();
            while (existingViewListIterator.hasNext()) {
                View view = existingViewListIterator.next();
                if (viewId.equals(view.getId())) {
                    //Found a replaceable view. Replace
                    listIterator.remove();
                    View replacedView;
                    //To fix a bug that was causing exception when a metadata is saved without any changes that has
                    // some views
                    //If file is not found in temp location may be it is part of the existing metadata. Use the same
                    replacedView = getView(tempPath, tableList, viewId, false, allVertices, tableAlias, columnAlias, removeViewColumn);
                    if (replacedView == null) {
                        replacedView = view;
                    }
                    replacedViews.add(replacedView);
                    existingViewListIterator.remove();
                }
            }
        }

        //Remove old entries of tables
        existingViewsList.addAll(replacedViews);

        for (JsonElement element : viewsArray) {
            String viewId = element.getAsString();
            //All existing views replaced. New ones
            View view = getView(tempPath, tableList, viewId, true, allVertices, tableAlias, columnAlias, removeViewColumn);
            existingViewsList.add(view);
        }

        views.setViewList(existingViewsList);
        database.setViews(views);
    }

    /**
     * Retrieves the list of tables from the database.
     * @param database 			 database object provides tables
     * @return The list of tables
     */
    private List<Table> getTables(Database database) {
        Tables tables = database.getTables();
        if (tables == null) {
            tables = ApplicationContextAccessor.getBean(Tables.class);
        }
        List<Table> tableList = tables.getTableList();

        if (tableList == null) {
            tableList = new ArrayList<>();
        }
        return tableList;
    }
    /**
     * Retrieves the view from a temporary file.
     * 
     * @param tempPath 					 temporary file path
     * @param tableList 				 list of tables
     * @param viewId 					 ID of the view
     * @param check 					 Indicates whether to check for existing views
     * @param allVertices 				 list of all vertices
     * @param tableAlias 				 map containing table aliases
     * @param columnAlias 				 map containing column aliases
     * @param remove 					 list of columns to remove from views
     * @return The view object
     */
    private View getView(String tempPath, List<Table> tableList, String viewId, boolean check,
                         List<String> allVertices, Map<String, String> tableAlias, Map<String, String> columnAlias, List<String> remove) {
        File file = new File(tempPath + File.separator + viewId + ".xml");
        if (!file.exists()) {
            return null;
        }
        View temporaryView = JaxbUtils.unMarshal(View.class, file);
        String name = temporaryView.getName();
        if (check) {
            if (allVertices.contains(name)) {
                throw new ViewNameExistsException("The view name " + name + " is already part of the metadata");
            }
        }
        Table table = temporaryView.getTable();
        temporaryView.setTable(null);

        if (!check) {//Table of view is previously saved in Tables. Remove it.
            ListIterator<Table> tableListIterator = tableList.listIterator();
            while (tableListIterator.hasNext()) {
                Table next = tableListIterator.next();
                if (next.getId().equals(table.getId())) {
                    tableListIterator.remove();
                }
            }
        }

        if (tableAlias != null) {
            String tableId = table.getId();
            String aliasName = tableAlias.get(tableId);
            if (aliasName != null) {
                table.setAliasName(aliasName);
                temporaryView.setAlias(aliasName);
            }
        }
        if (columnAlias != null) {
            Columns columns = table.getColumns();
            List<Column> columnList = columns.getColumn();


         if(columnList!=null){
             Iterator<Column> iterator = columnList.iterator();
            while (iterator.hasNext()) {
                Column column = iterator.next();
                String columnId = column.getId();
                if (remove != null && remove.contains(columnId)) {
                    iterator.remove();
                } else {
                    String aliasColumn = columnAlias.get(columnId);
                    if (aliasColumn != null) {
                        column.setAliasName(aliasColumn);
                    }
                }
            }
        }
        }

        tableList.add(table);
        if (logger.isDebugEnabled()) {
            logger.debug("Temporary file created for the view " + name + " is " +
                    "successfully deleted.");
        }
        return temporaryView;
    }
}
