package com.helicalinsight.adhoc.genericsql;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a utility class for constructing WHERE clauses in SQL queries based on filter criteria.
 * Created by author on 08-04-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
class WhereClause {

	private static final String prefix = "\\$\\{\\s*";
	private static final String suffix = "\\s*\\}";
	private static final String NULL = "null";

	List<Filter> filterList;
	String customExpression;
	SqlQueryContext context;
	JsonArray filters;
	JsonArray ignoreFilters;
	List<String> derivedTableColumns;
	/**
     * Constructs a new WhereClause object with an empty list of ignored filters.
     */
	WhereClause() {
		this.ignoreFilters = new JsonArray();
	}
	/**
     * Constructs a new WhereClause object with the provided SQL query context.
     * @param context 		 SQL query context.
     */
	WhereClause(@NotNull SqlQueryContext context) {
		this.context = context;
		this.derivedTableColumns = this.context.getDerivedTableColumns();
		JsonObject formData = this.context.getFormData();
		try {
			this.filters = formData.getAsJsonArray("filters");
			this.ignoreFilters = new JsonArray();
		} catch (JsonIOException ex) {
			throw new QueryBuilderException("The parameter filters is not an array.", ex);
		}

		if (this.filters.isEmpty()) {
			throw new QueryBuilderException("The filters json is empty.");
		}

		this.customExpression = GsonUtility.optString(formData, "customFilterExpression");

		this.filterList = new ArrayList<>();
		constructFilters(false);
	}
	/**
     * Constructs filter objects based on JSON filter criteria.
     * @param having 			Indicates whether the filters are for the HAVING clause.
     */
	@SuppressWarnings("rawtypes")
	void constructFilters(boolean having) {
		try {
			for (JsonElement object : this.filters) {
				JsonObject filterJsonItem = object.getAsJsonObject();
				boolean ignore = GsonUtility.optBooleanValue(filterJsonItem, "ignore", false);
				if (ignore) {
					ignoreFilters.add(filterJsonItem);
				}
				String column = filterJsonItem.get("column").getAsString();
				column = AdhocUtils.sanitizeStringIfStartsWithDot(column);

				String custom = null;
				if (filterJsonItem.has("custom")) {
					custom = filterJsonItem.get("custom").getAsString();
				} else {
					if (this.derivedTableColumns.contains(column)) {
						column = AdhocUtils.stripDatabaseName(column);
					}
				}

				if (filterJsonItem.has("databaseFunction")) {
					column = this.context.databaseFunction(filterJsonItem);
				}

				// Only having clause can have aggregate functions
				if (having && filterJsonItem.has("function")) {
					String function = filterJsonItem.get("function").getAsString();
					column = applyFunction(column, function);
				}

				// Default data type
				String javaLangString = "java.lang.String";
				String dataType = javaLangString;
				if (filterJsonItem.has("dataType")) {
					dataType = filterJsonItem.get("dataType").getAsString();
					if ("java.lang.Object".equals(dataType)) {
						dataType = javaLangString;
					}
				}
				if (filterJsonItem.has("databaseFunction")) {
					JsonObject databaseFunction = filterJsonItem.getAsJsonObject("databaseFunction");

					if (databaseFunction.has("dataType")) {
						String unknownDataType = databaseFunction.get("dataType").getAsString();
						if (unknownDataType.equalsIgnoreCase("numeric")) {
							dataType = "java.lang.Integer";
						}
						if (unknownDataType.equalsIgnoreCase("text") || unknownDataType.equalsIgnoreCase("date")
								|| unknownDataType.equalsIgnoreCase("time")
								|| unknownDataType.equalsIgnoreCase("dateTime")
								|| unknownDataType.equalsIgnoreCase("other")) {
							dataType = javaLangString;
						}

					}
				}

				String condition = filterJsonItem.get("condition").getAsString();
				boolean isCustomCondition = false;
				if ("CUSTOM".equalsIgnoreCase(condition)) {
					isCustomCondition = true;
				}
				String actualCondition = SqlQueryUtilities.condition(filterJsonItem, condition);

				boolean isCustomValue = filterJsonItem.has("isCustomValue");
				JsonArray filterValues = null;
				if (filterJsonItem.has("values")) {
					filterValues = filterJsonItem.getAsJsonArray("values");
				}
				List values = null;
				List<String> list = new ArrayList<>();
				if ((filterValues != null && !filterValues.isEmpty()) && !(filterValues.contains(null))) {
					for(JsonElement element : filterValues) {
						list.add(element.getAsString());
					}
				}
				if(list.isEmpty()) {
					values = null;
				}else {
					values = list;
				}
				this.filterList.add(new Filter(column, actualCondition, isCustomCondition, dataType, custom, values,
						isCustomValue, this.context, ignore));
			}
		} catch (Exception ex) {
			throw new QueryBuilderException("Malformed Json format", ex);
		}
	}
	 /**
     * Applies SQL functions to columns based on filter's function property.
     *
     * @param column   			 column name.
     * @param function 			 SQL function to apply.
     * @return modified column with the applied function.
     */
	@NotNull
	private String applyFunction(@NotNull String column, @NotNull String function) {
		String temp = "";
		if (function.contains("_")) {
			List<String> functions = Arrays.asList(function.split("_"));
			int last = functions.size() - 1;
			for (int index = last; index >= 0; index--) {
				String name = functions.get(index);
				if (index == last) {
					temp = name + "(" + this.context.quotes(column) + ")";
					continue;
				}
				temp = name + "(" + temp + ")";
			}
			column = temp;
		} else {
			column = function + "(" + this.context.quotes(column) + ")";
		}
		return column;
	}
	/**
     * Generates the WHERE clause based on constructed filters.
     * @return The WHERE clause string.
     */
	@NotNull
	public String where() {
		String normalWhereClause = whereClause();
		for (int index = 0; index < ignoreFilters.size(); index++) {
			JsonObject ignorableFilter = ignoreFilters.get(index).getAsJsonObject();
			int id = ignorableFilter.get("id").getAsInt();
			String dollarId = "${" + id + "}";
			String replaceStringAnd = "AND " + dollarId;
			String replaceStringOr = "OR ${" + id + "}";
			String replaceColumn = dollarId + ".column";
			String replaceCondition = dollarId + ".condition";
			String replaceValue = dollarId + ".value";
			normalWhereClause = normalWhereClause.replace(replaceStringAnd, "");
			normalWhereClause = normalWhereClause.replace(replaceStringOr, "");
			normalWhereClause = normalWhereClause.replace(replaceColumn, "");
			normalWhereClause = normalWhereClause.replace(replaceCondition, "");
			normalWhereClause = normalWhereClause.replace(replaceValue, "");
			boolean matches = normalWhereClause.matches("(?i)^\\nwhere\\n\\t\\(\\s*" + Pattern.quote(dollarId) + ".*");

			if (matches) {
				normalWhereClause = normalWhereClause.replace(dollarId + " AND ", "");
				normalWhereClause = normalWhereClause.replace(dollarId + " OR ", "");
			}
			normalWhereClause = normalWhereClause.replace(dollarId, "");

		}
		normalWhereClause = normalWhereClause.replace("\nwhere\n\t()", "");
		normalWhereClause = normalWhereClause.replace("\nwhere\n\t(  )", "");
		String regex = "(?i)\\nwhere\\n\\t\\(\\s*\\)";
		normalWhereClause = normalWhereClause.replaceAll(regex, "");
		return normalWhereClause;
	}
	/**
     * Constructs the WHERE clause based on filter list and custom expression.
     * @return constructed WHERE clause string.
     */
	@NotNull
	private String whereClause() {
		if (filterList.isEmpty()) {
			return "";
		}
		String query = "";
		query = query + "\nwhere\n\t(";
		int index = 0;
		String regEx;
		for (Filter filter : this.filterList) {
			if (filter.isIgnore()) {
				index++;
				continue;
			}
			for (int counter = 0; counter <= 3; counter++) {
				if (counter == 0) {
					regEx = prefix + index + suffix + "\\.column";
					this.customExpression = this.customExpression.replaceAll(regEx,
							this.context.quotes(Matcher.quoteReplacement(filter.getColumn())));
					// this.context.quotes(filter.getColumn()));
				}
				if (counter == 1) {
					regEx = prefix + index + suffix + "\\.condition";
					this.customExpression = this.customExpression.replaceAll(regEx, filter.getCondition());
				}

				if (counter == 2) {
					regEx = prefix + index + suffix + "\\.value";
					String values = filter.getQuotedValues();
					String replace;
					if (values != null) {
						replace = values;
					} else {
						replace = NULL;
					}
					this.customExpression = this.customExpression.replaceAll(regEx, replace);
				}
				if (counter == 3) {
					regEx = prefix + index + suffix;
					String filterInString = filter.toString();
					String hiBug = filterInString.replaceAll(prefix, "_hiBug_");
					hiBug = Matcher.quoteReplacement(hiBug);
					hiBug = hiBug.replaceAll("_hiBug_", "\\$\\{");
					this.customExpression = this.customExpression.replaceAll(regEx, hiBug);
					this.customExpression = this.customExpression.replaceAll("\\\\\\$", "\\$");
					// this.customExpression = this.customExpression.replaceAll(regEx,
					// filter.toString());
				}
			}
			index++;
		}
		if (StringUtils.isBlank(this.customExpression)) {
			return "";
		}
		return query + this.customExpression + ")";
	}
}
