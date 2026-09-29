function guid() {
    function s4() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    return s4() + s4() + '-' + s4() + '-' + s4() + '-' + s4() + '-' + s4() + s4() + s4();
}

function setQueryOffsetLimit(query, offset, limit, context) {
	query = query.split('#coma#').join(",");
//return query

    var obj = JSON.parse(context);

    var viewsObject = obj.metadata.database.views;
    var originalViewQueryArray = [];
    var replacedViewQueryArray = [];
    if (viewsObject) {
        var viewList = viewsObject.viewList;
        for (var listIndex = 0; listIndex < viewList.length; listIndex++) {
            var singleViewObj = viewList[listIndex];
            originalViewQueryArray.push(singleViewObj.query.query);
            replacedViewQueryArray.push(guid())

        }
    }

    var schemaName = obj.metadata.database.schema;

    var quotedSchema = schemaName.split(".").join('"."');
    quotedSchema = '"' + quotedSchema + '"';

    var replaceSchemaName = "_H!@N@T_";


    var table = [];

    for (var indx = 0; indx < obj.requestedTables.length; indx++) {
        var requestedTable = obj.requestedTables[indx];
        table.push(requestedTable.replace(obj.databaseName + ".", ""));
    }

    var requiredToCast;
    var filtersArray = obj.formData.filters;
    var actualFilterValuesArray = [];
    var actualFilterValuesArrayWithOutQuotes = [];


    var filterValuesArray = [];
    var otherReplaceFilterValuesArray = [];
    var replaceFilterValuesArray = [];
    if (filtersArray) {
        var isRequired = true;
        for (var ind = 0; ind < filtersArray.length; ind++) {
            var outerFunctionReturnType = undefined;
            var singleFilter = filtersArray[ind];
            var valuesArray = singleFilter.values;
            var filterDataType = singleFilter.dataType;
			var isCustomValue = (singleFilter.isCustomValue) ? singleFilter.isCustomValue : false;
            var databseFunction = singleFilter.databaseFunction;
            var isEnclosedInQuotes = singleFilter.encloseInQuotes;
            if (databseFunction) {
                outerFunctionReturnType = databseFunction.dataType;
                requiredToCast = (outerFunctionReturnType === 'dateTime' || outerFunctionReturnType === 'date');
                isRequired = outerFunctionReturnType === "text" ? true : false;
            }
            else {
                if (filterDataType === 'java.sql.Timestamp') {
                    outerFunctionReturnType = 'dateTime';
                    requiredToCast = true;
                }
                else if (filterDataType === 'java.sql.Date') {
                    outerFunctionReturnType = 'date';
                    requiredToCast = true;
                }

            }
				
            if (valuesArray ) {
                if (isRequired) {

                    for (var filterIndex = 0; filterIndex < valuesArray.length; filterIndex++) {
                        var arrayElement = valuesArray[filterIndex];
                        if (typeof arrayElement === 'string' && arrayElement.indexOf("AND") === -1 && arrayElement.indexOf("_all_") === -1) {
                            if (arrayElement.indexOf(",") > -1) {
                                arrayElement = arrayElement.replaceAll("(", "").replaceAll(")", "").trim();
                                var separateValueArray = arrayElement.split(",");
                                for (var arrayIndex = 0; arrayIndex < separateValueArray.length; arrayIndex++) {
                                    var eachValue = separateValueArray[arrayIndex];
                                    filterValuesArray.push(eachValue);
                                    if (eachValue.startsWith("'") && eachValue.endsWith("'")) {
                                        replaceArrayElement = "'" + eachValue.substring(eachValue.indexOf("'") + 1, eachValue.lastIndexOf("'")).replace(/'/g, "''") + "'"
                                    }
                                    else {

                                        replaceArrayElement = eachValue.replace(/'/g, "''")
                                    }
                                    replaceFilterValuesArray.push(replaceArrayElement);

                                }

                            }
                            else {
                                var replaceArrayElement;
                                filterValuesArray.push(arrayElement);


                                if (arrayElement.startsWith("'") && (arrayElement.endsWith("'"))) {
                                    replaceArrayElement = "'" + arrayElement.substring(arrayElement.indexOf("'") + 1, arrayElement.lastIndexOf("'")).replace(/'/g, "''") + "'"
                                }
                                else if (arrayElement.startsWith("'") && (arrayElement.endsWith("')"))) {
                                    replaceArrayElement = "'" + arrayElement.substring(arrayElement.indexOf("'") + 1, arrayElement.lastIndexOf("'")).replace(/'/g, "''") + "')"
                                }
                                else {
                                    replaceArrayElement = arrayElement.replace(/'/g, "''")
                                }
                                replaceFilterValuesArray.push(replaceArrayElement);
                            }
                        }
                    }
                }

                var type = undefined;

                if (compareEqual(outerFunctionReturnType, "dateTime")) {
                    type = "TIMESTAMP ";
                }

                if (compareEqual(outerFunctionReturnType, "date")) {
                    type = "DATE ";
                }

                if (type && requiredToCast && isCustomValue===false) {
                    for (var index = 0; index < valuesArray.length; index++) {
                        var arrayElement = valuesArray[index];
                        if (arrayElement != null && arrayElement.includes(",")) {
                            var separateValueArray = arrayElement.split(',');
                            prepareArraysForReplace(separateValueArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, otherReplaceFilterValuesArray, type)
                        }
                        else if (arrayElement != null && arrayElement.includes("AND")) {
                            var inRangeValuesArray = arrayElement.split('AND');
                            prepareArraysForReplace(inRangeValuesArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, otherReplaceFilterValuesArray, type)
                        }
                        else {
                            if (arrayElement != null) {
                                type = arrayElement.length > 14 ? "TIMESTAMP " : type;
                                arrayElement = arrayElement.replaceAll(")", "");
                                arrayElement = arrayElement.replaceAll("'", "").trim();
                                actualFilterValuesArrayWithOutQuotes.pushUnique(arrayElement);
                                actualFilterValuesArray.pushUnique("'" + arrayElement + "'");
                                otherReplaceFilterValuesArray.pushUnique(type + "'" + arrayElement + "'");
                            }

                        }
                    }
                }
            }
        }
    }

    var requestedColumnsArray = obj.formData.columns;

    var requestedColumnsWithoutDbName = [];

    var quotedAliases = [];
    var unquotedAlias = [];
    for (var ind = 0; ind < requestedColumnsArray.length; ind++) {
        var singleColumn = requestedColumnsArray[ind];
        var requestedColumnsWithFullyQualified = singleColumn.column;
        var originalAlias = singleColumn.alias;
        unquotedAlias.push(" " + originalAlias);
        quotedAliases.push(' "' + originalAlias + '"');
        requestedColumnsWithoutDbName.push(requestedColumnsWithFullyQualified.replace(obj.databaseName + ".", ""));
        requestedColumnsWithFullyQualified = "";
    }


    var fromIndex = query.indexOf("\nfrom\n");
    var whereIndex = query.indexOf("\nwhere\n");
    var groupByIndex = query.indexOf("\ngroup by\n");

    var havingIndex = query.indexOf("\nhaving\n");
    var orderByIndex = query.indexOf("\norder by\n");

    var fromEndIndex;
    if (whereIndex > -1) {
        fromEndIndex = whereIndex
    }
    else if (groupByIndex > -1) {
        fromEndIndex = groupByIndex;
    }
    else if (havingIndex > -1) {
        fromEndIndex = havingIndex;
    }
    else if (orderByIndex > -1) {
        fromEndIndex = orderByIndex
    }
    else {
        fromEndIndex = query.length;
    }

    var whereEndIndex;
    if (groupByIndex > -1) {
        whereEndIndex = groupByIndex;
    }
    else if (havingIndex > -1) {
        whereEndIndex = havingIndex;
    }
    else if (orderByIndex > -1) {
        whereEndIndex = orderByIndex
    }
    else {
        whereEndIndex = query.length;
    }


    var groupByEndIndex;
    if (havingIndex > -1) {
        groupByEndIndex = havingIndex;
    }
    else if (orderByIndex > -1) {
        groupByEndIndex = orderByIndex
    }
    else {
        groupByEndIndex = query.length;
    }

    var havingEndIndex;
    if (orderByIndex > -1) {
        havingEndIndex = orderByIndex
    }
    else {
        havingEndIndex = query.length;
    }


    var selectClause = query.substring(0, fromIndex);
    var fromClause = query.substring(fromIndex, fromEndIndex);
    var whereClause = whereIndex > -1 ? query.substring(whereIndex, whereEndIndex) : "";
    var groupByClause = groupByIndex > -1 ? query.substring(groupByIndex, groupByEndIndex) : "";
    var havingClause = havingIndex > -1 ? query.substring(havingIndex, havingEndIndex) : "";
    var orderByClause = orderByIndex > -1 ? query.substring(orderByIndex, query.length) : "";


    fromClause = replaceOriginal(originalViewQueryArray, replacedViewQueryArray, fromClause);

    var databaseName = obj.databaseName;


    databaseName = databaseName.split(".").join('"."');
    databaseName = '"' + databaseName + '".';


    var selectWithoutDbName = selectClause.split(databaseName).join("");
    var whereWithoutDbName = whereClause.split(databaseName).join("");
    var groupByWithoutDbName = groupByClause.split(databaseName).join("");
    var havingWithoutDbName = havingClause.split(databaseName).join("");
    var orderByWithoutDbName = orderByClause.split(databaseName).join("");


    fromClause = fromClause.replaceAll('"DREMIO".', "");

  
    var startBracArray = [];
    var endBracArray = [];
    if (fromClause.includes("join")) {
        var clonedFromClause;
        clonedFromClause = fromClause.replaceAll(quotedSchema, replaceSchemaName);
        for (var bracIndex = 0; bracIndex < clonedFromClause.length; bracIndex++) {

            if (clonedFromClause[bracIndex] === "(") {
                startBracArray.push(bracIndex);
            }
            if (clonedFromClause[bracIndex] === ")") {
                endBracArray.push(bracIndex);
            }
        }

        var fromClauseExpected = [];
        var fromClauseReplaced = [];

        for (var bracCheckIndex = 0; bracCheckIndex < startBracArray.length; bracCheckIndex++) {

            var bracString = "";
            bracString = clonedFromClause.substring(startBracArray[bracCheckIndex], endBracArray[bracCheckIndex]);
            if (bracString.match(/select/gi) != null) {
                continue;
            }

            bracString = bracString.replaceAll(replaceSchemaName, quotedSchema);

            fromClauseExpected.push(bracString);

            var replaceBracString = "";
            replaceBracString = bracString.replaceAll(quotedSchema + ".", "");
            fromClauseReplaced.push(replaceBracString);


        }

        fromClause = replaceOriginal(fromClauseExpected, fromClauseReplaced, fromClause);


    }
	whereWithoutDbName = whereWithoutDbName.replaceAll("\'\"null\"\'",'null');
	whereWithoutDbName = whereWithoutDbName.replaceAll("\'\"NULL\"\'",'NULL');
 
    var requiredQueryForSpark = selectWithoutDbName;


    requiredQueryForSpark += fromClause + whereWithoutDbName + groupByWithoutDbName + havingWithoutDbName + orderByWithoutDbName;


    var actlQry = requiredQueryForSpark;

    if (actualFilterValuesArray && otherReplaceFilterValuesArray.length > 0) {
        if (actlQry.includes(actualFilterValuesArray[0])) {

            actlQry = replaceOriginal(actualFilterValuesArray, otherReplaceFilterValuesArray, actlQry)
        }
        else {
            actlQry = replaceOriginal(actualFilterValuesArrayWithOutQuotes, otherReplaceFilterValuesArray, actlQry)
        }
    }


    actlQry = replaceOriginal(replacedViewQueryArray, originalViewQueryArray, actlQry);


    actlQry = actlQry.split(quotedSchema).join('"' + schemaName + '"');

    if (limit == 0) {
        return actlQry;
    }

    if (offset == 0) {
        return actlQry + " \nlimit " + limit;
    }

    return actlQry + " \nlimit " + limit + " offset " + offset;

}

function isInArray(value, array) {
    return array.indexOf(value) > -1;
}


Array.prototype.pushUnique = function (item) {
    if (this.indexOf(item) == -1) {
        this.push(item);
    }
};

function replaceOriginal(expected, replace, query) {

    for (var indx = 0; indx < expected.length; indx++) {
        var actual = expected[indx];
        var replaceValue = replace[indx];

        if (query.includes(actual)) {
            query = query.split(actual).join(replaceValue);
        }
    }
    return query;
}

function strictReplaceOriginal(expected, replace, query) {

    for (var indx = 0; indx < expected.length; indx++) {
        var actual = expected[indx];
        var replaceValue = replace[indx];

        if (query.includes(actual)) {
            query = query.replace(new RegExp("\\b" + actual + "\\b"), replaceValue);
        }
    }
    return query;
}

function prepareArraysForReplace(extractArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, otherReplaceFilterValuesArray, type) {

    for (var indxx = 0; indxx < extractArray.length; indxx++) {

        singleSeperatedValue = extractArray[indxx];
        type = singleSeperatedValue.length > 14 ? "TIMESTAMP " : type;
        singleSeperatedValue = singleSeperatedValue.replaceAll(")", "");
        singleSeperatedValue = singleSeperatedValue.replaceAll("'", "").trim();
        actualFilterValuesArrayWithOutQuotes.pushUnique(singleSeperatedValue);
        actualFilterValuesArray.pushUnique("'" + singleSeperatedValue + "'");
        otherReplaceFilterValuesArray.pushUnique(type + "'" + singleSeperatedValue + "'");

    }
}

function compareEqual(str1, str2) {
    return ((str1 && str2) && (str1.trim() === str2.trim()));
}
String.prototype.replaceAll = function (search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};