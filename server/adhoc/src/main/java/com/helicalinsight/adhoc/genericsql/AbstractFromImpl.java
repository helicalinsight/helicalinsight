package com.helicalinsight.adhoc.genericsql;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * AbstractFromImpl class is designed for generation of SQL query .
 * Created by author on 10/21/2015.
 * @author Rajasekhar
 */
abstract class AbstractFromImpl {

	/**
	 * This method fetches the tables info from SqlQueryContext object and creates an sql query.
	 * and returns in the form of StringBuilder.
	 * @param node            table name
	 * @param context		  provides list of derived tables .
	 * @return A StringBuilder consisting sql query.
	 */
    @NotNull
    StringBuilder subQuery(@NotNull String node, SqlQueryContext context) {
        List<DerivedTable> derivedTables = context.getDerivedTables();
        StringBuilder sql = new StringBuilder();
        for (DerivedTable derivedTable : derivedTables) {
            if (node.equals(derivedTable.getName())) {

                //"as" seems to be optional for table aliases. So removing this
                // return sql.append("(").append(derivedTable.getQuery()).append(") as ").append(context.quotes(node));
                String type = derivedTable.getType();
                String query = derivedTable.getQuery();
                if (StringUtils.isEmpty(type) || type.equalsIgnoreCase("conditionIf")) {
                    query = SecurityExpressionEvaluator.replaceExpression(query);

                } else if (type.equalsIgnoreCase("groovy")) {
                    query = SqlQueryUtilities.evalAllExpression(context, query);
                }
                if (type != null && type.equalsIgnoreCase("hasStoredProcedure")) {
                    query = SecurityExpressionEvaluator.replaceExpression(query);
                }
                if (context.getUnWrapSelect()) {
                    return sql.append(query);
                }
                return sql.append("(").append(query).append(")  ").append(context.quotes(node));
            }
        }
        throw new QueryBuilderException("The view name " + node + " is not found in the list " +
                "of views in the metadata.");
    }
    /**
     * It returns full name of join as per the required String type.
     * @param type      it represents SQL joins type .
     * @return
     */
    String sqlJoinType(String type) {
        if ("inner".equalsIgnoreCase(type)) {
            return "inner join";
        } else if ("left".equalsIgnoreCase(type)) {
            return "left outer join";
        } else if ("right".equalsIgnoreCase(type)) {
            return "right outer join";
        } else if ("full".equalsIgnoreCase(type)) {
            return "full outer join";
        } else {
            throw new QueryBuilderException("Unknown join type " + type);
        }
    }
}
