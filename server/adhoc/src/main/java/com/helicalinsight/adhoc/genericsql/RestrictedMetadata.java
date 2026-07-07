package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.adhoc.metadata.ISecureMetadata;
import com.helicalinsight.adhoc.metadata.MetadataSecurityObjectFactory;
import com.helicalinsight.adhoc.metadata.security.SecurityExpression;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * Represents restricted metadata based on security expressions.
 * This class evaluates security expressions and extracts restricted tables and columns accordingly.
 */
public class RestrictedMetadata {
    private static final Logger logger = LoggerFactory.getLogger(RestrictedMetadata.class);

    private final List<String> restrictedColumns;
    private final List<String> restrictedTables;
    private  String strategy = "";
    /**
     * Constructs a new RestrictedMetadata object using the provided security expressions.
     * @param expressions 		 list of security expressions to evaluate
     */
    public RestrictedMetadata(List<SecurityExpression> expressions) {
        this.restrictedTables = new ArrayList<>();
        this.restrictedColumns = new ArrayList<>();
        for (SecurityExpression securityExpression : expressions) {
            String type = securityExpression.getType();
            logger.info("Type is " + type);
            boolean result=false;
            String expression = securityExpression.getCondition();
            ISecureMetadata securityClass = MetadataSecurityObjectFactory.getSecurityClass(type);
            if(securityClass!=null) {
                result = securityClass.evaluateCondition(expression);
            }
            String expressionType = securityExpression.getExpressionType();
            String accessType = securityExpression.getAccessType();
            String on = securityExpression.getOn();
            String splitted = on.split(",")[0];
            strategy = StringUtils.isNumeric(splitted) ? "new" : "old";
            if ("deny".equalsIgnoreCase(accessType) && result) {
                String itemNameOn = securityExpression.getOn();
                String itemNameArray[] = itemNameOn.split(",");
                for (String itemName : itemNameArray) {
                    if (itemName != null) {
                        if ("table".equalsIgnoreCase(expressionType) || "global".equalsIgnoreCase(expressionType)) {
                            String tableName = AdhocUtils.getTableNameFromTable(itemName);
                            this.restrictedTables.add(tableName);
                        } else if ("column".equalsIgnoreCase(expressionType)) {
                            this.restrictedColumns.add(itemName);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the list of restricted columns from the specified table.
     *
     * @param tableName 		 name of the table
     * @param aliasName 		 alias name of the table
     * @return a list of restricted columns from the table
     */
    public List<String> restrictedColumnsFromTable(String tableName, String aliasName) {
        List<String> columnList = new ArrayList<>();

        List<String> columns = getRestrictedColumns();

        for (String fullyQualifiedColumn : columns) {
            String strings[] = fullyQualifiedColumn.split("\\.");

            int arrayLength = strings.length;
            String table = strings[arrayLength - 2];

            if (table.equalsIgnoreCase(tableName) || table.equalsIgnoreCase(aliasName)) {
                columnList.add(strings[arrayLength - 1]);
            }
        }
        return columnList;
    }
    /**
     * Retrieves the list of restricted columns.
     * @return a list of restricted columns
     */
    public List<String> getRestrictedColumns() {
        return restrictedColumns;
    }
    /**
     * Retrieves the list of restricted tables.
     * @return a list of restricted tables
     */
    public List<String> getRestrictedTables() {
        return restrictedTables;
    }
    /**
     * Retrieves the strategy used for evaluation of security expressions.
     * @return the strategy used for evaluation
     */
    public String getStrategy() {
    	return strategy;
    }
}