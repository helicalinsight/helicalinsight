package com.helicalinsight.adhoc.genericsql;


import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;

import java.util.ArrayList;

/**
 * Represents a utility class for constructing HAVING clauses in SQL queries based on filter criteria.
 * 
 * Created by author on 08-04-2015.
 * @author Rajasekhar
 * @author Somen
 */
final class HavingClause extends WhereClause {
	/**
     * Constructs a new HavingClause object with the provided SQL query context.
     * @param context 		 SQL query context.
     */
    HavingClause(@NotNull SqlQueryContext context) {
        super();
        this.context = context;
        this.derivedTableColumns = this.context.getDerivedTableColumns();
        JsonObject formData = this.context.getFormData();
        try {
            this.filters = formData.getAsJsonArray("having");
        } catch (JsonIOException ex) {
            throw new QueryBuilderException("The parameter having is not an array.", ex);
        }

        if (this.filters.isEmpty()) {
            throw new QueryBuilderException("The having json is empty.");
        }

        this.customExpression = GsonUtility.optString(formData,"customHavingExpression");

        this.filterList = new ArrayList<>();
        constructFilters(true);
    }
    /**
     * Generates the HAVING clause based on constructed filters.
     * @return HAVING clause string.
     */
    @NotNull
    public String having() {
        String expression = where();
        //Remove the where keyword and prepend having
        if(StringUtils.isBlank(expression)){
            return "";
        }
        return "\nhaving" + expression.substring(6, expression.length());
    }
}