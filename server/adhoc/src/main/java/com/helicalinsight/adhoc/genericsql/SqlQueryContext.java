package com.helicalinsight.adhoc.genericsql;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.helicalinsight.adhoc.metadata.jaxb.*;
import com.helicalinsight.efw.utility.JsonUtils;


import org.apache.commons.lang3.StringUtils;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.View;
import com.helicalinsight.adhoc.metadata.jaxb.Views;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.DialectHelper;
import com.helicalinsight.efw.utility.JsonUtils;



/**
 * Represents the context for building SQL queries.
 * 
 * Created by author on 09-03-2015.
 * @author Rajasekhar
 */
public final class SqlQueryContext {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryContext.class);
    //List of relationships of the database
    private final List<Relationship> listOfRelationships;
    //Map of table and fully qualified table names from the metadata file
    @NotNull
    private final Map<String, String> nameAndFullNameMap;
    //All database columns. All columns
    @NotNull
    private final Map<String, String> columnsMap;
    //Fully qualified tables that are selected
    @NotNull
    private final List<String> requestedTables;
    //Just table names which are to be included in the from clause
    @NotNull
    private final List<String> graphNodes;

    public Map<String, String> getTableAliasMap() {
        return tableAliasMap;
    }

    //List of all database tables
    @NotNull
    private final List<String> tables;
    //List of derived tables
    @NotNull
    private final List<DerivedTable> derivedTables;
    //Derived table names without fully qualified names
    @NotNull
    private final List<String> derivedTableNames;
    //Fully qualified columns of the views
    @NotNull
    private final List<String> derivedTableColumns;
    //Name of the database (catalog.schema)
    private final String databaseName;

    private final Map<String, String> aliasColumnMap;

    private final Map<String,String> tableAliasMap;

    //The request
    private final JsonObject formData;
    //True if the distinct results are requested for filters(where clause)
    private final boolean distinctResults;
    //True if where clause needs to be applied
    private final boolean applyWhere;
    //Hibernate dialect
    private final Dialect hibernateDialect;
    //Metadata security
    private final PreConstructedFilters preConstructedFilters;
    //The object consists of the functions from the xml file
    private final SqlFunction standardSqlFunction;
    //Query offset for pagination
    private final String queryOffset;
    //The metadata file
    private final Metadata metadata;

    //False if limit requested is FULL
    private boolean isLimitRequested = true;
    //True if aggregation needs to be applied
    private boolean applyAggregation = false;
    //True if group-by needs to be applied
    private boolean applyGroupBy = false;
    //True if order-by needs to be applied
    private boolean applyOrderBy = false;
    //True if having needs to be applied
    private boolean applyHaving = false;
    //Actual query limit
    private String queryLimit;
    //Open quote for the sql identifier
    private String openQuote = null;
    //Close quote for the sql identifier
    private String closeQuote = null;
    //Driver class name of the current metadata data source
    private String driverClassName;
    //This stores the name of the javascript file that is corresponding to the dialect specific paginated query
    //generator. In the same location (where the javascript file is) an xml file with the same name will be present
    //that will have all the functions that are related to the database in use.
    private String referenceFile;
    private Boolean unWrapSelect;
    /**
     * Constructs a new SqlQueryContext.
     *
     * @param container        	metadata store container provides full name of column.
     * @param metadata        	metadata provides database
     * @param requestedColumns  list of requested columns
     * @param queryLimit        query limit
     * @param formData          form data provides filters, sql related details.
     */
    public SqlQueryContext(@NotNull IMetadataStore container, @NotNull Metadata metadata,
                           @NotNull List<String> requestedColumns, @NotNull String queryLimit, JsonObject formData) {
        this.metadata = metadata;
        Database database = this.metadata.getDatabase();
        this.databaseName = database.getName();
        this.formData = formData;

        this.columnsMap = container.getFullyQualifiedColumnsMap();

        this.applyWhere = this.formData.has("filters");

        if (this.formData.has("functions")) {
            JsonObject functions = this.formData.getAsJsonObject("functions");
            this.applyAggregation = functions.has("aggregate");
            this.applyGroupBy = functions.has("groupBy");
            this.applyOrderBy = functions.has("orderBy");
        }
        this.unWrapSelect = GsonUtility.optBoolean(this.formData,"unWrapSelect");

        this.applyHaving = this.formData.has("having");

        ConnectionDetails connectionDetails = this.metadata.getConnectionDetails();

        String dialect = connectionDetails.getDialect();

        Properties properties = new Properties();
        properties.put(Environment.DIALECT, dialect);

        this.hibernateDialect = DialectHelper.getDialect(properties);

        char quote = this.hibernateDialect.openQuote();
        setOpenQuote(quote);

        quote = this.hibernateDialect.closeQuote();
        setCloseQuote(quote);

        this.distinctResults = this.formData.has("distinctResults") && "true".equalsIgnoreCase(this.formData
                .get("distinctResults").getAsString());

        Relationships relationships = database.getRelationships();

        if (relationships != null) {
            this.listOfRelationships = relationships.getListOfRelations();
        } else {
            this.listOfRelationships = new ArrayList<>();
        }

        this.tables = AdhocUtils.allVertices(database);
        this.tableAliasMap = AdhocUtils.allTableAliasMap(database);

        this.requestedTables = extractTableNames(requestedColumns);
        //Here the tables used from filters and the databaseFunction objects are accumulated
        //so that they are part of the from clause.
        updateRequestedTables();
        this.preConstructedFilters = new PreConstructedFilters(this.metadata, this.openQuote, this.closeQuote);
        this.requestedTables.removeAll(this.preConstructedFilters.getGlobalFilterTables());
        this.requestedTables.addAll(this.preConstructedFilters.getGlobalFilterTables());

        this.graphNodes = graphNodeNames();
        this.nameAndFullNameMap = tablesMap();

        Views views = database.getViews();
        this.derivedTableNames = new ArrayList<>();
        this.derivedTableColumns = container.getDerivedTablesColumnsList();
        if (views == null || views.getViewList() == null) {
            this.derivedTables = new ArrayList<>();
        } else {
            this.derivedTables = new ArrayList<>();
            for (View view : views.getViewList()) {
                String name = view.getName();
                View.Query query = view.getQuery();
                String type = query.getType();
                this.derivedTables.add(new DerivedTable(name, view.getAlias(), query.getQuery(), type));
                this.derivedTableNames.add(name);
                tableAliasMap.put(name,view.getAlias());
            }
        }

        setLimitFromRequest(queryLimit);
        String offset = GsonUtility.optString(this.formData,"offset");
        if (offset.length() > 0) {
            this.queryOffset = offset;
        } else {
            this.queryOffset = "0";
        }
        DriverClass driverClass = SqlQueryUtilities.driverClass(this.metadata);
        if (driverClass != null) {
            this.driverClassName = driverClass.getDriverClass();
            this.referenceFile = driverClass.getReference();
            String databaseFunctionsFile = JsonUtils.sqlFunctionsLocation() + File.separator + driverClass
                    .getReference() + ".xml";
            this.standardSqlFunction = new StandardSqlFunction(databaseFunctionsFile, this);
        } else {
            //No database functions
            this.standardSqlFunction = new StandardSqlFunction(null, this);
        }
        this.aliasColumnMap = new HashMap<>();

        populateAliasColumnMap();


    }

    public Map<String, String> getAliasColumnMap() {
        return aliasColumnMap;
    }


    private void populateAliasColumnMap() {
        JsonArray columnsArray = getFormData().getAsJsonArray("columns");
        for (JsonElement column : columnsArray) {
            JsonObject item =  column.getAsJsonObject();
            aliasColumnMap.put(item.get("alias").getAsString(), item.get("column").getAsString());
        }

    }

    public Boolean getUnWrapSelect() {
        return unWrapSelect;
    }

    public void setUnWrapSelect(Boolean unWrapSelect) {
        this.unWrapSelect = unWrapSelect;
    }

    private void setOpenQuote(char quote) {
        if (0 == quote) {
            this.openQuote = "";
        } else {
            this.openQuote = quote + "";
        }
    }

    private void setCloseQuote(char quote) {
        if (0 == quote) {
            this.closeQuote = "";
        } else {
            this.closeQuote = quote + "";
        }
    }
    /**
     * Extracts table names from a list of requested columns.
     * 
     * @param requestedColumns 		 list of requested columns.
     * @return A list of table names extracted from the requested columns.
     */
    @SuppressWarnings("serial")
    @NotNull
    private List<String> extractTableNames(final List<String> requestedColumns) {
        return new ArrayList<String>() {
            {
                for (String column : requestedColumns) {
                    String table = extractTable(column);
                    if (!this.contains(table)) {
                        add(table);
                    }
                }
            }
        };
    }
    /**
     * Updates the list of requested tables based on the tables used in filters and database functions.
     */
    private void updateRequestedTables() {
        Set<String> tables = new HashSet<>();
        Set<String> usedTables = new HashSet<>();//Auxiliary
        if (this.formData.has("filters")) {
            JsonArray filters;
            try {
                filters = this.formData.getAsJsonArray("filters");
            } catch (Exception ex) {
                throw new QueryBuilderException(ex);
            }

            addTablesColumns(tables, usedTables, filters);
        }

        if (this.formData.has("having")) {
            JsonArray having;
            try {
                having = this.formData.getAsJsonArray("having");
            } catch (Exception ex) {
                throw new QueryBuilderException(ex);
            }

            addTablesColumns(tables, usedTables, having);
        }

        for (String table : tables) {
            if (!this.requestedTables.contains(table)) {
                this.requestedTables.add(table);
            }
        }
    }
    /**
     * Retrieves the list of graph node names, which represent tables involved in the query.
     * @return  list of graph node names.
     */
    @NotNull
    private List<String> graphNodeNames() {
        //In case of calcite the metadata contains the tables with fully qualified names.
        //But in case of the normal database, metadata tables have only simple names

        //For the query generator to work, the method AdhocUtils.allVertices() and the
        //current method should give list of table names which are in the same format.

        //If AdhocUtils.allVertices() gives list of strings that are in format databaseName.tableName,
        //and the graphNodeNames() method gives just list of tableNames, then the query generation
        //will fail as the GraphAlgorithmImpl does not get the input for processing(List<Node> selectedNodes will be
        // empty)
        //and as a result the selectedNodes.iterator().next() method call produces NoSuchElementException

        //So always the graphNodeNames() and the AdhocUtils.allVertices() should give consistent names

        //Check if all the requested tables are part of allVertices or not
        //as requestedTables always will be in fully qualified format, if all names are present in the
        //metadata then the metadata is different type(calcite). So no need to strip the databaseName in this method.
        List<String> allVertices = AdhocUtils.allVertices(this.metadata.getDatabase());
        if (allVertices.containsAll(this.requestedTables)) {
            return this.requestedTables;
        }

        List<String> originalTables = new ArrayList<>();
        for (String fullName : this.requestedTables) {
           /* int endIndex = fullName.lastIndexOf(".");
            originalTables.add(fullName.substring(endIndex + 1));*/
            String tableName = getTableName(fullName);
            originalTables.add(tableName);
        }
        return originalTables;
    }
    /**
     * Retrieves the table name from a fully qualified column name.
     *
     * @param fullName 			 fully qualified column name.
     * @return The table name extracted from the column name.
     */
    public String getTableName(String fullName) {
        int endIndex;
        if (databaseName != null && !"".equals(databaseName)) {
            endIndex = databaseName.length();

        } else {
            endIndex = fullName.lastIndexOf(".");

        }
        String sub = StringUtils.substring(fullName, endIndex+1);
        return StringUtils.isNotBlank(sub)?sub : fullName;
    }
    /**
     * Retrieves the alias name corresponding to a table name.
     * @param fullName 		 fully qualified table name.
     * @return The alias name associated with the table name, or the full name if no alias is found.
     */
    public String getAliasName(String fullName) {
    	return this.tableAliasMap.getOrDefault(fullName,fullName);
    }
    /**
     * Constructs a map of table names to their fully qualified names.
     * @return A map containing table names as keys and their fully qualified names as values.
     */
    @NotNull
    private Map<String, String> tablesMap() {
        Map<String, String> tables = new HashMap<>();
        boolean emptyName = false;
        if ("".equals(this.databaseName)) {
            emptyName = true;
        }
        for (String original : this.tables) {
            if (!emptyName) {
                tables.put(original, this.databaseName + "." + original);
            } else {
                tables.put(original, original);
            }
        }
        return tables;
    }
    /**
     * Sets the query limit based on the provided query limit string.
     *
     * @param queryLimit 		 query limit string.
     */
    private void setLimitFromRequest(@NotNull String queryLimit) {
        try {
            Integer limit = 0;
            if (!"FULL".equalsIgnoreCase(queryLimit)) {

                limit = Integer.valueOf(queryLimit);

            }
            this.queryLimit = String.valueOf(limit);

//            if (!this.hibernateDialect.getLimitHandler().supportsLimit()) {
//                this.isLimitRequested = false;
//                logger.warn("Requested query data limit is " + limit + ". Database does " +
//                        "not supports limit feature. Disabling limit.");
//            }
        } catch (NumberFormatException ex) {
            throw new QueryBuilderException("SQL query limit is not a number", ex);
        }
    }
    /**
     * Extracts the table name from a column name.
     *
     * @param column 		 column name.
     * @return The extracted table name.
     */
    @NotNull
    private String extractTable(@NotNull String column) {
        // The table name in this string consists of catalog.schema.table
        // In case if the catalog or schema is null it will have only only dot
        Database database = this.metadata.getDatabase();
        List<Table> tableList = database.getTables().getTableList();
        List<View> viewList = database.getViews().getViewList();
        List<String> preparedAllTablesList = new ArrayList<>();
        if (tableList != null)
            tableList.forEach(eachTable -> preparedAllTablesList.add(prepareTableName(eachTable.getName())));
        if (viewList != null)
            viewList.forEach(eachView -> preparedAllTablesList.add(prepareTableName(eachView.getName())));
        for (String eachValue : preparedAllTablesList) {
            if (column.contains(eachValue + "."))
                return eachValue;
        }
        return null;
    }
    /**
     * Prepares the table name by appending the database name if available.
     *
     * @param name         original table name.
     * @return The prepared table name.
     */
    private String prepareTableName(String name) {
        Database database = this.metadata.getDatabase();
        String databaseName = database.getName();
        if (databaseName == null || databaseName.isEmpty())
            return name;
        return databaseName + "." + name;
    }
    /**
     * Adds tables and columns used in filters and database functions to the provided sets.
     *
     * @param tables    		 set of tables to add to.
     * @param usedTables 		 set of used tables to add to.
     * @param array     		 JSON array containing filter or database function information.
     */
    private void addTablesColumns(Set<String> tables, Set<String> usedTables, JsonArray array) {
        if (!array.isEmpty()) {
            for (JsonElement object : array) {
                JsonObject jsonObject = object.getAsJsonObject();
                if(GsonUtility.optBooleanValue(jsonObject,"ignore",false)){
                    continue;
                }
                if (!jsonObject.has("custom")) {
                    tables.add(extractTable(jsonObject.get("column").getAsString()));
                }
                addColumnUsedInDatabaseFunctions(tables, usedTables, jsonObject);
            }
        }
    }
    /**
     * Adds columns used in a database function to the provided sets.
     *
     * @param tables      		 set of tables to add to.
     * @param usedTables  		 set of used tables to add to.
     * @param jsonObject  		 JSON object containing database function information.
     */
    private void addColumnUsedInDatabaseFunctions(Set<String> tables, Set<String> usedTables, JsonObject jsonObject) {
        //Update the used columns from the objects' databaseFunction. Each database function
        //may use one or more columns.
        if (jsonObject.has("usedColumns")) {
            JsonArray usedColumns = jsonObject.getAsJsonArray("usedColumns");
            for (JsonElement column : usedColumns) {
                usedTables.add(extractTable(column.getAsString()));
            }
            if (!usedTables.isEmpty()) {
                tables.addAll(usedTables);
            }
        }
    }
    /**
     * Applies quotes to a column name if necessary.
     *
     * @param column 		 column name.
     * @return The column name with quotes applied.
     */
    @Nullable
    String as(String column) {
        //Just in case if a column is the result of the database function expression and
        //if a column is used, then the column has the open, close quotes applied.

        //Then the open, close quotes will be removed.
        if (column.contains(this.openQuote) && column.contains(this.closeQuote)) {
            column = column.replaceAll(this.openQuote, "").replaceAll(this.closeQuote, "");
        }

        //Apply the same open and close quotes at the beginning and end
        return (this.openQuote + column + this.closeQuote).replace(".", "_");
    }
    /**
     * Applies quotes to a string, ensuring that each component is quoted individually.
     *
     * @param name 			 string to apply quotes to.
     * @return The string with quotes applied.
     */
    @NotNull
    String quotes(@NotNull String name) {
        //Keep a check - quotes may have already been applied.
        //Do not apply quotes to database functions
        if (hasParenthesis(name)) {
            return name;
        }
        return doApplyQuotes(name);
    }
    /**
     * Checks if a string contains parentheses.
     *
     * @param alias 		 string to check.
     * @return {@code true} if the string contains parentheses, {@code false} otherwise.
     */
    boolean hasParenthesis(String alias) {
        return alias.contains("(") && alias.contains(")");
    }
    /**
     * It applies quotes to a string, ensuring that each component is quoted individually.
     *
     * @param name 		 string to apply quotes to.
     * @return The string with quotes applied.
     */
    public String doApplyQuotes(String name) {
        //If the column does not consists of period in it, no need to split.
        if (name.startsWith(this.openQuote) && name.endsWith(this.closeQuote)) {
            return name;
        }
        if (!name.contains(".")) {
            return this.openQuote + name + this.closeQuote;
        }

        String safeName = "";
        String[] strings = name.split("\\.");
        for (String string : strings) {
            safeName = safeName + this.openQuote + string + this.closeQuote + ".";
        }
        // Remove the last .
        if (!safeName.endsWith(".")) {
            return safeName;
        }
        return safeName.substring(0, safeName.length() - 1);
    }

    public JsonObject getFormData() {
        return this.formData;
    }

    @NotNull
    public Map<String, String> getColumnsMap() {
        return this.columnsMap;
    }

    @NotNull
    public List<String> getDerivedTableColumns() {
        return derivedTableColumns;
    }

    public boolean isApplyAggregation() {
        return applyAggregation;
    }

    public boolean isDistinctResults() {
        return distinctResults;
    }

    @NotNull
    public List<String> getDerivedTableNames() {
        return derivedTableNames;
    }

    @NotNull
    public List<String> getRequestedTables() {
        return requestedTables;
    }

    @NotNull
    public List<String> getGraphNodes() {
        return graphNodes;
    }

    public List<String> getTables() {
        return tables;
    }

    public SqlFunction getStandardSqlFunction() {
        return standardSqlFunction;
    }

    public Dialect getHibernateDialect() {
        return hibernateDialect;
    }

    public String getOpenQuote() {
        return openQuote;
    }

    public void setOpenQuote(String openQuote) {
        this.openQuote = openQuote;
    }

    public String getCloseQuote() {
        return closeQuote;
    }

    public void setCloseQuote(String closeQuote) {
        this.closeQuote = closeQuote;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @NotNull
    public Map<String, String> getNameAndFullNameMap() {
        return nameAndFullNameMap;
    }

    public List<Relationship> getListOfRelationships() {
        return listOfRelationships;
    }

    public String databaseFunction(JsonObject json) {
        //If a database function is applied over the json(in columns array or in filters array or in having array)
        //received, then the result of the database function expression should be used as the column.

        JsonObject databaseFunction = json.getAsJsonObject("databaseFunction");
        return this.standardSqlFunction.sqlFunction(databaseFunction);
    }

    public boolean isApplyWhere() {
        return applyWhere;
    }

    public boolean isApplyGroupBy() {
        return applyGroupBy;
    }

    public boolean isApplyOrderBy() {
        return applyOrderBy;
    }

    public boolean isApplyHaving() {
        return applyHaving;
    }

    public PreConstructedFilters getPreConstructedFilters() {
        return preConstructedFilters;
    }

    public boolean isLimitRequested() {
        return isLimitRequested;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getQueryOffset() {
        return queryOffset;
    }

    public String getQueryLimit() {
        return queryLimit;
    }

    public String getReferenceFile() {
        return referenceFile;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @NotNull
    public List<DerivedTable> getDerivedTables() {
        return derivedTables;
    }
}