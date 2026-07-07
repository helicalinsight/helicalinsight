import com.helicalinsight.adhoc.genericsql.AdhocUtils
import com.helicalinsight.adhoc.genericsql.SqlQueryUtilities
import com.helicalinsight.adhoc.genericsql.EnumTypeFinder;
import com.helicalinsight.adhoc.genericsql.JavaTypes;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import groovy.json.JsonOutput
import com.google.gson.JsonElement;

import groovy.json.JsonSlurper

_INSERT_IMPORTS_HERE_



filters = formData.filters?formData.filters:[];
requestedTables=[];
schema="";
catalog="";
dbName="";
database="";
metadata="";
if(sqlContext){
    metadata=sqlContext?.metadata;
    database=metadata?.database;
    dbName=database?.name
    catalog=database?.catalog;
    schema=database?.schema;
    requestedTables=sqlContext?.requestedTables
}

if (formData.having) {
    if (formData.having.size() > 0)
        filters.addAll(formData.having)
}
derivedFilters = []
filter=[:]
columnString=[]
onlyColumnString=[]
conditionString=[]
isCustomConditionString=[]
dataTypeString=[]
customString=[]
valuesString=[]
isCustomValueString=[]
idString=[]
labelString=[]
modeString=[]
if(filters)
    for (int i = 0; i < filters.size(); i++) {
        item = filters[i];
        String column = item.column;
        String onlyColumn="";
        for(String tblName:requestedTables){
            onlyColumn=column.replace(tblName+".","");
        }

        column = AdhocUtils.sanitizeStringIfStartsWithDot(column);

        String custom = item?.custom
        if (sqlContext.derivedTableColumns.contains(column)) {
            column = AdhocUtils.stripDatabaseName(column);
        }

        if (item?.databaseFunction) {
            column = sqlContext.databaseFunction(item);
        }

        //Only having clause can have aggregate functions
        if (item?.function) {
            String function = item.function;
            column = applyFunction(column, function);
        }

        //Default data type
        String javaLangString = "java.lang.String";
        String dataType = javaLangString;
        if (item?.dataType) {
            dataType = item.dataType
            if ("java.lang.Object".equals(dataType)) {
                dataType = javaLangString;
            }
        }
        if (item?.databaseFunction) {
            databaseFunction = item.databaseFunction

            if (databaseFunction?.dataType) {
                String unknownDataType = databaseFunction.dataType;
                if (unknownDataType.equalsIgnoreCase("numeric")) {
                    dataType = "java.lang.Integer";
                }
                if (unknownDataType.equalsIgnoreCase("text") || unknownDataType.equalsIgnoreCase("date") || unknownDataType.equalsIgnoreCase("time") || unknownDataType.equalsIgnoreCase("dateTime") || unknownDataType.equalsIgnoreCase("other")) {
                    dataType = javaLangString;
                }


            }
        }

        String condition = item.condition;
        boolean isCustomCondition = false;
        if ("CUSTOM".equalsIgnoreCase(condition)) {
            isCustomCondition = true;
        }

        String actualCondition =null;
        switch (condition.toUpperCase()) {
            case "EQUALS":         actualCondition = "=";         break
            case "IS_ONE_OF":         actualCondition = "in" ;        break
            case "IN_RANGE":         actualCondition = "inRange";         break
            case "NOT_IN_RANGE":         actualCondition = "notInRange";         break
            case "CUSTOM":         actualCondition = item.customCondition;

        }

        boolean isCustomValue = item?.isCustomValue;
        def filterValues = [];
        if (item?.values) {
            filterValues = item.values;
        }
        List values = [];
        List<String> list = new ArrayList<>();
        if ((filterValues != null && !filterValues.isEmpty()) && !(filterValues.contains(null))) {
            for( element : filterValues) {
                list.add(element);
            }
        }
        if(list.isEmpty()) {
            values = [];
        }else {
            values = list;
        }

        valuesQuoted= getQuotedValues(isCustomValue,dataType, values)
        column=sqlContext.quotes(column);
        onlyColumn=sqlContext.quotes(onlyColumn);
        def tempObj = [:]
        tempObj.fullyQualifiedColumn = column
        tempObj.column = onlyColumn
        tempObj.condition = actualCondition
        tempObj.isCustomCondition = isCustomCondition
        tempObj.dataType = dataType
        tempObj.custom = custom

        tempObj.value = valuesQuoted
        tempObj.values=values
        tempObj.isCustomValue = isCustomValue
        tempObj.id = item.id
        tempObj.label = item.label
        tempObj.mode = item.mode
        columnString.add(column)
        conditionString.add(actualCondition)
        isCustomConditionString.add(isCustomCondition)
        onlyColumnString.add(onlyColumn)
        dataTypeString.add(dataType)
        customString.add(""+custom)
        valuesString.add(""+valuesQuoted)
        isCustomValueString.add(""+isCustomValue)
        idString.add(""+item.id)
        labelString.add(item.label)
        modeString.add(item.mode)
        derivedFilters.add(tempObj)
    }

filter.fullyQualifiedColumn=columnString.join(",")
filter.condition=conditionString.join(",")
filter.isCustomCondition=isCustomConditionString.join(",")
filter.dataType=dataTypeString.join(",")
filter.custom=customString.join(",")
def joinedString = valuesString.join(",").trim();
if(joinedString.endsWith(")")&&joinedString.length()>2){
    joinedString= joinedString.substring(0,joinedString.length()-1);
}
filter.values="["+ joinedString +"]"
filter.value= joinedString
filter.isCustomValue=isCustomValueString.join(",")
filter.id=idString.join(",")
filter.label=labelString.join(",")
filter.mode=modeString.join(",")
filter.column=onlyColumnString.join(",");

def findFilter(String jsonCriteria) {
    def jsonSlurper = new JsonSlurper()
    fiterCriteria = jsonSlurper.parseText(jsonCriteria)
    if (hasFilters()) {
        for (int i = 0; i < derivedFilters.size(); i++) {
            it = derivedFilters[i];
            boolean search = false
            for (entry in fiterCriteria) {
                search = (it[entry.key]?.toString() == entry.value?.toString())
                if (!search) {
                    break;
                }
            }
            if (search) {
                return it;
            }

        }
    }
    return null;
}


private boolean hasFilters() {
    return !(filters?.isEmpty());
}

def findFilter(Map fiterCriteria) {
    if (hasFilters()) {
        for (int i = 0; i < derivedFilters.size(); i++) {
            it = derivedFilters[i];
            boolean search = false
            for (entry in fiterCriteria) {
                search = (it[entry.key] == entry.value)
                if (!search) {
                    break;
                }
            }
            if (search) {
                return it;
            }

        }
    }
    return null;

}

def boolean check(String multiValue, String argument) {
    if (!(argument && multiValue)) {
        return false
    }

    multiValue = replaceQuoteBlank(multiValue)
    multiValue = replaceOpenSquareBlank(multiValue)
    multiValue = replaceCloseSquareBlank(multiValue)
    multiValue = replaceSpaceBlank(multiValue)

    def multiArrayList = splitComa(multiValue);

    argument = replaceQuoteBlank(argument)
    argument = replaceSpaceBlank(argument)
    List<String> argumentList = splitComa(argument)

    for (String argumentValue : argumentList) {
        if (multiArrayList.contains(argumentValue)) {
            return true;
        }
    }

    return false;
}

private String replaceSpaceBlank(multiValue) {
    multiValue.replaceAll("\\s", "")
}

private String replaceCloseSquareBlank(multiValue) {
    multiValue.replaceAll("\\]", "")
}

private String replaceOpenSquareBlank(multiValue) {
    multiValue.replaceAll("\\[", "")
}

def String addQuotes(item) {
    return "'" + item + "'"
}

private String replaceQuoteBlank(String argument) {
    argument.replaceAll("'", "")
}

private String[] splitComa(String multiValue) {
    multiValue.split(",")
}


def findFilterByLabel(String label) {
    if (hasFilters()) {
        for (int i = 0; i < derivedFilters.size(); i++) {
            it = derivedFilters[i];
            if (it.label && it.label == label) {
                if(it.value&&it.value=="_all_")
                    it.value="'_all_'"
                if(it.value&&(!it.value.startsWith("("))&&it.value.endsWith(")"))
                    it.value = it.value.substring(0, it.value.length() - 1);
                if(it.value&&it.value.startsWith("''")&&it.value.endsWith("''"))
                    it.value = it.value.replace("''","'");
                return it;
            }
        }
    }
    return null;
}

def getFilterArrayByLabels(labelArray) {
    def toSend = []
    if (hasFilters()) {
        for (int i = 0; i < derivedFilters.size(); i++) {
            it = derivedFilters[i];
            if (it.label && labelArray.contains(it.label)) {
                toSend.add(it)
            }
        }
    }
    return toSend
}


def String applyFunction(String column, String function) {
    if(!sqlContext){
        return column;
    }
    String temp = "";
    if (function.contains("_")) {
        List<String> functions = Arrays.asList(function.split("_"));
        int last = functions.size() - 1;
        for (int index = last; index >= 0; index--) {
            String name = functions.get(index);
            if (index == last) {
                temp = name + "(" + sqlContext.quotes(column) + ")";
                continue;
            }
            temp = name + "(" + temp + ")";
        }
        column = temp;
    } else {
        column = function + "(" + sqlContext.quotes(column) + ")";
    }
    return column;
}


def String getQuotedValues(Boolean isCustomValue,String dataType,List<String> values) {
    boolean quotesRequired=isCustomValue?false:areQuotesRequired(dataType);

    return addValues(quotesRequired,values);
}
def String getSanitizedValue(Object object) {
    String str = String.valueOf(object);
    if (str.contains("'")) {
        return str.replaceAll("'", "\\'");
    }
    return str;
}
def String addValues(boolean quotesRequired,List<String> values) {
    if (values == null) {
        return "";
    }
    if (values.size() == 1) {
        if (quotesRequired) {
            return AdhocUtils.singleQuotes(getSanitizedValue(values.get(0)));
        }
        return values.get(0) + "";
    }

    String withQuotes = "";
    for (Object object : values) {
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

def boolean areQuotesRequired(String dataType) {
    if (dataType.startsWith("\"") && dataType.endsWith("\"")) {
        dataType = dataType.substring(1, dataType.length() - 1);
    }

    JavaTypes enumType = EnumTypeFinder.findEnumType(dataType);
    boolean isDateTime = JavaTypes.DATETIME.equals(enumType) || JavaTypes.DATE.equals(enumType) || JavaTypes.TIME
            .equals(enumType);
    boolean isString = false;
    if (!isDateTime) {
        isString = JavaTypes.STRING.equals(enumType);
    }
    return isDateTime || isString;
}


def select(String... args){
    String concat="";
    for(int i=0; i<args.length; i++){
        concat+=args[i]
    }
    return "select "+args.join(",");
}
filters=derivedFilters;

__INSERT_CODE_HERE__
		
		
