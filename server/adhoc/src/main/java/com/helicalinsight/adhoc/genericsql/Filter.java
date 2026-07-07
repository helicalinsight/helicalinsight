package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.efw.ApplicationProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The Filter class represents a filter condition used in generic SQL queries.
 * It provides methods to construct filter conditions based on various criteria.
 * 
 * Created by author on 08-04-2015.
 * @author Rajasekhar
 * @author Somen
 */
@SuppressWarnings("ALL")
final class Filter {

    private static final String NULL_VALUE = ApplicationProperties.getInstance().getNullValue();
    private static final String ALL_VALUES = ApplicationProperties.getInstance().getAllValues();
    private static final String NULL = "null";
    private static final String JSON_NULL = "\"" + NULL + "\"";

    private String column;
    private SqlQueryContext context;
    private String condition;
    private boolean isCustomCondition;
    private boolean isCustomValue;
    private String dataType;
    private String custom;
    private List values;
    private boolean ignore;

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public Filter(String column, String condition, boolean isCustomCondition, String dataType, String custom,
                  List values, boolean isCustomValue, SqlQueryContext context, boolean ignore) {
        this.column = column;
        this.context = context;
        this.condition = condition;
        this.isCustomCondition = isCustomCondition;
        this.dataType = dataType;
        this.custom = custom;
        this.values = SqlQueryUtilities.getSanitizedValueForList(values);
        this.isCustomValue = isCustomValue;
        this.ignore =ignore;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Filter filter = (Filter) object;

        if (column != null ? !column.equals(filter.column) : filter.column != null) return false;
        if (condition != null ? !condition.equals(filter.condition) : filter.condition != null) return false;
        if (dataType != null ? !dataType.equals(filter.dataType) : filter.dataType != null) return false;
        //noinspection SimplifiableIfStatement
        if (custom != null ? !custom.equals(filter.custom) : filter.custom != null) return false;
        return !(values != null ? !values.equals(filter.values) : filter.values != null);
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (custom != null ? custom.hashCode() : 0);
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public List getValues() {

        return values;
    }

    public void setValues(List values) {
        this.values = values;
    }

    public String getQuotedValues() {
        return addValue();
    }
    /**
     * Returns a string representation of the filter condition.
     * If the filter condition contains the keyword "all", it replaces it with appropriate SQL syntax.
     * and checks different condition .
     * @return The string representation of the filter condition.
     */
    @NotNull
    @Override
    public String toString() {
        String filter;
        if (this.column.contains("(") && this.column.contains(")")) {
            //This filter is part of having clause. Having clause related function already has quotes applied.
            filter = this.column;
        } else {
            filter = this.context.quotes(this.column);
        }

        if (this.addValue().contains(ALL_VALUES)) {
            return replace_all_(this.condition.toLowerCase().contains("not"));
        }

        if ("in".equals(this.condition) && !this.isCustomCondition) {
            if (this.values != null) {

                if (this.values.contains(NULL_VALUE)) {
                    this.values.remove(NULL_VALUE);
                    filter = filter + " is null or " + this.context.quotes(this.column) + " in (" + addValue() + ")";
                } else {
                    filter = filter + " in (" + addValue() + ")";
                }
            } else {//In case of no values just add the condition whatever is provided
                return filter + " " + this.condition;
            }
        } else if ("=".equals(this.condition) && !this.isCustomCondition) {
            if (this.values != null) {
                if (this.values.size() == 1) {
                    if (this.values.contains(NULL_VALUE)) {
                        filter = filter + " is null";
                    } else {
                        filter = filter + " = " + addValue();
                    }
                } else {
                    throw new QueryBuilderException("SQL where clause equal operator has more than one value. " +
                            "Invalid condition.");
                }
            } else {//In case of no values just add the condition whatever is provided
                return filter + " " + this.condition;
            }
        } else if ("inRange".equals(this.condition)) {
            return getRangeFilter(filter, ">=", " and ", "<=");
        } else if ("notInRange".equals(this.condition)) {
            return getRangeFilter(filter, "<", " or ", ">");

        } else {
            if (this.values != null) {
                return filter + " " + this.condition + " " + filterValue();
            } else {//In case of no values just add the condition whatever is provided
                return filter + " " + this.condition;
            }
        }


        return filter;
    }
    /**
     * Replaces the keyword "all" in the filter condition with appropriate SQL syntax based on whether it's negated.
     * @param isNegate 			Indicates if the condition is negated.
     * @return The updated filter condition.
     */
    @NotNull
    private String replace_all_(Boolean isNegate) {
        if(this.condition.contains("<>")){
            isNegate=true;
        }
        return "'" + ALL_VALUES + "' " + (isNegate ? "<>" : "=") + " '" + ALL_VALUES + "'";
    }
    /**
     * Constructs a range filter condition based on the specified filter, comparison operators, and values.
     * @param filter 			     filter condition.
     * @param firstCondition 		 first comparison operator (e.g., ">=").
     * @param andOr 				 logical operator ("and" or "or").
     * @param secondCondition 		 second comparison operator (e.g., "<=").
     * @return The range filter condition.
     * @throws QueryBuilderException if there are less than two values for range selection.
     */
    private String getRangeFilter(String filter, String firstCondition, String andOr, String secondConditon) {
        if (this.values != null && this.values.size() >= 2) {
            String firstValue, secondValue;
            Boolean quotesRequired = areQuotesRequired();
            if (quotesRequired) {
                firstValue = AdhocUtils.singleQuotes(getSanitizedValue(this.values.get(0)));
                secondValue = AdhocUtils.singleQuotes(getSanitizedValue(this.values.get(1)));
            } else {
                firstValue = this.values.get(0).toString();
                secondValue = this.values.get(1).toString();
            }
            return "( " + filter + firstCondition + firstValue + andOr + filter + secondConditon + secondValue + " )";
        } else {
            throw new QueryBuilderException("There should be atleast two values for range selection");
        }
    }
    /**
     * Constructs a string representing the values of the filter, appropriately formatted for SQL.
     * @return The formatted string of filter values.
     */
    @NotNull
    private String addValue() {
        boolean quotesRequired;
        if (this.isCustomValue) {
            quotesRequired = false;
        } else {
            quotesRequired = areQuotesRequired();
        }
        return addValues(quotesRequired);
    }
    /**
     * Constructs a string representing all values of the filter, appropriately formatted for SQL.
     * @param quotesRequired 		Indicates if quotes are required for values.
     * @return The formatted string of filter values.
     */
    private String addValues(boolean quotesRequired) {
        if (this.values == null) {
            return "";
        }
        if (this.values.size() == 1) {
            if (quotesRequired) {
                return AdhocUtils.singleQuotes(getSanitizedValue(this.values.get(0)));
            }
            return this.values.get(0) + "";
        }

        String withQuotes = "";
        for (Object object : this.values) {
            if (quotesRequired) {
                withQuotes = withQuotes + AdhocUtils.singleQuotes(getSanitizedValue(object)) + ", ";
            } else {
                withQuotes = withQuotes + object + ", ";
            }
        }
        //Remove space and comma at the end
        if (withQuotes.length() >= 2) {
            return withQuotes.substring(0, withQuotes.length() - 2);
        } else {
            return withQuotes;
        }
    }
    /**
     * Checks if quotes are required for the filter values based on their data type.
     * @return True if quotes are required, otherwise false.
     */
    private boolean areQuotesRequired() {
        JavaTypes enumType = EnumTypeFinder.findEnumType(this.dataType);
        boolean isDateTime = JavaTypes.DATETIME.equals(enumType) || JavaTypes.DATE.equals(enumType) || JavaTypes.TIME
                .equals(enumType);
        boolean isString = false;
        if (!isDateTime) {
            isString = JavaTypes.STRING.equals(enumType);
        }
        return isDateTime || isString;
    }
    /**
     * Constructs the filter value, considering custom values and data type requirements.
     * @return The constructed filter value.
     */
    private Object filterValue() {
        //Only single value of custom value is possible as the value is taken in one text box
        String string = this.values.get(0).toString();
        if (JSON_NULL.equals(string)) {
            return NULL;
        }
        if (this.isCustomValue || (string.startsWith("'") && string.endsWith("'")) || (string.startsWith("\"") &&
                string.endsWith("\""))) {
            return string;
        }
        if (areQuotesRequired()) {
            return addValues(true);
        }
        return string;
    }
    /**
     * Sanitizes the specified object value by escaping single quotes if present.
     * @param object     value to be sanitized.
     * @return The sanitized string value.
     */
    //Escape all single quotes present in the values
    private String getSanitizedValue(Object object) {
        String str = String.valueOf(object);
        if (str.contains("'")) {
            return str.replaceAll("'", "\\'");
        }
        return str;
    }


}