package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.ISecureMetadata;
import com.helicalinsight.adhoc.metadata.MetadataSecurityObjectFactory;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.Connections;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.security.MetadataSecurity;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Created by author on 15-09-2015.
 * <p>
 * Consists of the set of where clause strings from the access node of the metadata to be added to
 * the SQL
 *
 * @author Somen
 * @author Rajasekhar
 */
public class PreConstructedFilters {
    //private static final Logger logger = LoggerFactory.getLogger(PreConstructedFilters.class);
    private static final Pattern pattern = Pattern.compile("\\$\\{([^\\}]*)\\}");
    private final Map<String, List<String>> tableFilters;
    private final List<String> globalFilters;
    private final Set<String> globalFilterTables;
    private final Metadata metadata;
    private final String openQuotes;
    private final String closeQuotes;
    private final String databaseName;
    private Map<String,String> tableNameMap;
    private Map<String,String> columnTableNameMap;

    /**
     * Constructs a new PreConstructedFilters object.
     * @param metadata 				 metadata representing the database schema.
     * @param openQuotes 			 opening quotation mark for database objects.
     * @param closeQuotes 			 closing quotation mark for database objects.
     */
    public PreConstructedFilters(Metadata metadata, String openQuotes, String closeQuotes) {
        this.metadata = metadata;
        this.tableFilters = new HashMap<>();
        this.globalFilterTables = new HashSet<>();
        this.globalFilters = new ArrayList<>();
        this.openQuotes = openQuotes;
        this.closeQuotes = closeQuotes;
        String name = metadata.getDatabase().getName();
        if (name != null && name.length() > 0) {
            this.databaseName = name + ".";
        } else {
            this.databaseName = "";
        }
        prepareTableColumnMap();
        setFilters();
        
    }
    /**
     * Prepares the mapping of table IDs to their respective names and column IDs to their respective table names.
     */
    private void prepareTableColumnMap() {
    	tableNameMap = new HashMap<>();
    	columnTableNameMap = new HashMap<>();
    	List<Table> tables =  this.metadata.getDatabase().getTables().getTableList();
    	tables.forEach(table -> { 
    		tableNameMap.put(table.getId(), table.getName());
    		table.getColumns().getColumn().forEach(col -> {
    			columnTableNameMap.put(col.getId(), table.getName());
    		});
    	});
    	Connections connections = this.metadata.getConnections();
    	if ( connections != null) {
    		List<ConnectionDatabase> cdbList =  connections.getConnectionDatabase();
    		for(ConnectionDatabase eachDb : cdbList) {
    			eachDb.getDatabase().getTables().getTableList().forEach(eachTable -> {
    				tableNameMap.put(eachTable.getId(),eachTable.getName());
    				eachTable.getColumns().getColumn().forEach(col -> {
    	    			columnTableNameMap.put(col.getId(), eachTable.getName());
    	    		});
    			});
    		}
    	}
	}
    /**
     * Sets the filters based on the metadata security settings. 
     * This method checks the security settings for tables, columns, etc.
     */
	private void setFilters() {
    	HIMetadataResourceServiceDB mdServiceDb = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
        MetadataSecurity access = this.metadata.getMetadataSecurity();
        if (access != null) {
            List<SecurityExpression> expressions = access.getExpressions();
            for (SecurityExpression securityExpression : expressions) {
                String expression = securityExpression.getCondition();
                String filter = securityExpression.getFilter();
                String type = securityExpression.getType();
                if (filter != null && expression != null) {
                    boolean result;

                    ISecureMetadata securityClass = MetadataSecurityObjectFactory.getSecurityClass(type);
                    if (securityClass == null) {
                        return;
                    }
                    result = securityClass.evaluateCondition(expression);
                    filter = securityClass.getFilters(filter);

                    filter = addQuotes(filter).trim();
                    String expressionType = securityExpression.getExpressionType();
                    String accessType = securityExpression.getAccessType();

                    if (("grant".equalsIgnoreCase(accessType) && result)) {
                        String itemName = securityExpression.getOn();
                        if (expressionType != null) {
                            if ("global".equalsIgnoreCase(expressionType)) {
                                if (itemName != null) {
                                    String[] tables = itemName.split("\\s*,\\s*");
                                    for (String item : tables) {
                                    	if (StringUtils.isNumeric(item)) {
                                    		item = tableNameMap.get(item);
                                    	}
                                        String derivedTableName = this.databaseName + item;
                                        this.globalFilterTables.remove(derivedTableName);
                                        this.globalFilterTables.add(derivedTableName);
                                    }
                                }
                                removeDuplicateFromList(filter, this.globalFilters);
                            } else if ("column".equalsIgnoreCase(expressionType)) {
                                String itemArrayFromItem[] = itemName.split(",");
                                for (String item : itemArrayFromItem) {
                                	
                                	if(StringUtils.isNumeric(item)) {
                                		item = columnTableNameMap.get(item);
                                	}
                                	
                                    String derivedTableName = this.databaseName + item;
                                    List<String> tableFilter = getTableFilters(derivedTableName);
                                    if (tableFilter == null) {
                                        tableFilter = new  ArrayList<>();
                                    }
                                    removeDuplicateFromList(filter, tableFilter);
                                    this.tableFilters.put(derivedTableName, tableFilter);
                                }
                            } else if ("view".equalsIgnoreCase(expressionType) || "table".equalsIgnoreCase
                                    (expressionType)) {
                                String itemArrayFromItem[] = itemName.split(",");
                                for (String item : itemArrayFromItem) {
                                	if( StringUtils.isNumeric(item)) {
                                		item = tableNameMap.get(item);
                                	}
                                    List<String> tableFilter = getTableFilters(item);
                                    if (tableFilter == null) {
                                        tableFilter = new ArrayList<>();
                                    }
                                    removeDuplicateFromList(filter, tableFilter);
                                    this.tableFilters.put(databaseName + item, tableFilter);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeDuplicateFromList(String filter, List<String> tableFilter) {
        tableFilter.remove(filter);
        tableFilter.add(filter);
    }
    /**
     * filters modified and  adds quotes to filter expressions.
     * @param filterExpression 			 filter expression to be modified.
     * @return The modified filter expression.
     */
    private String addQuotes(String filterExpression) {
        if (filterExpression == null) {
            throw new IllegalArgumentException("Filter expression is null");
        }
        if ("".equals(filterExpression.trim())) {
            return "";
        }
        Matcher matcher = pattern.matcher(filterExpression);
        StringBuffer stringBuffer = new StringBuffer(filterExpression.length());
        while (matcher.find()) {
            String text = matcher.group(1);
            String[] array = text.split("\\.");
            String arrayJoin = this.openQuotes;
            for (String token : array) {
                arrayJoin += token + this.closeQuotes + "." + this.openQuotes;
            }
            int endIndex = arrayJoin.lastIndexOf("." + this.openQuotes);
            if (endIndex != -1) {
                arrayJoin = arrayJoin.substring(0, endIndex);
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(arrayJoin));
            }
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }
    /**
     * Retrieves the table-wise filters for the specified table.
     * @param tableName 		 name of the table.
     * @return The list of table-wise filters.
     */
    private List<String> getTableFilters(String tableName) {
        return tableFilters.get(tableName);
    }
    /**
     * Constructs the pre-defined WHERE clause based on the requested tables.
     * @param requestedTables 		 list of tables for which the WHERE clause is to be constructed.
     * @return constructed WHERE clause.
     */
    public String preConstructedWhereClause(List<String> requestedTables) {
        for (String table : requestedTables) {
            List<String> tableFilters = getTableFilters(table);
            if (tableFilters != null) {
                globalFilters.addAll(tableFilters);
            }
        }

        StringBuilder partialQuery = new StringBuilder();
        for (String query : globalFilters) {
            partialQuery.append(query).append(" and ");
        }
        String queryString = partialQuery.toString();
        if ("".equals(queryString)) {
            return "";
        }
        return queryString.substring(0, queryString.lastIndexOf(" and "));
    }
    /**
     * Retrieves the set of tables affected by global filters.
     * @return  set of tables.
     */
    public Set<String> getGlobalFilterTables() {
        return globalFilterTables;
    }
}