function nth_ocurrence(str, needle, nth) {
    for (i = 0; i < str.length; i++) {
        if (str.charAt(i) == needle) {
            if (!--nth) {
                return i;
            }
        }
    }
    return false;
}

function setQueryOffsetLimit(query, offset, limit, context) {
	
	if (query.indexOf("FLATTEN(") > -1 || query.indexOf("flatten(") > -1) {
		query = query.replaceAll("distinct", "").replaceAll("DISTINCT","");
	}

   // return query;
    query = query.replace(/``/g, "`").replace(/\]`/g, "]");
    obj = JSON.parse(context);
    var catalogName = obj.metadata.database.catalog;
    if (catalogName !== undefined) {
        query = query.split("`" + catalogName + "`.").join("");
    }


    var table = [];
    var expectedArray = [];
    var replaceArray = [];
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
            var databseFunction = singleFilter.databaseFunction;
            var isEnclosedInQuotes = singleFilter.encloseInQuotes;
            if (databseFunction) {
                outerFunctionReturnType = databseFunction.dataType
                requiredToCast = (outerFunctionReturnType === 'dateTime' || outerFunctionReturnType === 'date');
                isRequired = outerFunctionReturnType === "text" ? true : false;
            } else {
                if (filterDataType === 'java.sql.Timestamp') {
                    outerFunctionReturnType = 'dateTime';
                    requiredToCast = true;
                } else if (filterDataType === 'java.sql.Date') {
                    outerFunctionReturnType = 'date';
                    requiredToCast = true;
                }

            }

            if (valuesArray) {
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
                                    } else {

                                        replaceArrayElement = eachValue.replace(/'/g, "''")
                                    }
                                    replaceFilterValuesArray.push(replaceArrayElement);

                                }

                            } else{
                                var replaceArrayElement;
                                filterValuesArray.push(arrayElement);
								 
								
                                if (arrayElement.startsWith("'") && (arrayElement.endsWith("'"))) {
                                    replaceArrayElement = "'" + arrayElement.substring(arrayElement.indexOf("'") + 1, arrayElement.lastIndexOf("'")).replace(/'/g, "''") + "'"
                                }else if(arrayElement.startsWith("'") && (arrayElement.endsWith("')"))){
									replaceArrayElement = "'" + arrayElement.substring(arrayElement.indexOf("'") + 1, arrayElement.lastIndexOf("'")).replace(/'/g, "''") + "')"
								} else {
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

                if (type && requiredToCast) {
                    for (var index = 0; index < valuesArray.length; index++) {
                        var arrayElement = valuesArray[index];
                        if (arrayElement != null && arrayElement.includes(",")) {
                            var separateValueArray = arrayElement.split(',');
                            prepareArraysForReplace(separateValueArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, otherReplaceFilterValuesArray, type)
                        } else if (arrayElement != null && arrayElement.includes("AND")) {
                            var inRangeValuesArray = arrayElement.split('AND');
                            prepareArraysForReplace(inRangeValuesArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, otherReplaceFilterValuesArray, type)
                        } else {
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

    var requestedColumnsWithFullyQualified;
    var requestedColumnsArray = obj.formData.columns;
	
    var requestedColumnsWithoutDbName = [];
    var expectedColumnNames = [];
    var replaceColumnNames = [];
    for (var ind = 0; ind < requestedColumnsArray.length; ind++) {
        var singleColumn = requestedColumnsArray[ind];
        var requestedColumnsWithFullyQualified = singleColumn.column;
        requestedColumnsWithoutDbName.push(requestedColumnsWithFullyQualified.replace(obj.databaseName + ".", ""));
        requestedColumnsWithFullyQualified = "";
    }
    var columnsWithOutTableName = [];
    for (var ind = 0; ind < requestedColumnsWithoutDbName.length; ind++) {
        var singleColumn = requestedColumnsWithoutDbName[ind];
        for (var indx = 0; indx < table.length; indx++) {
            var tableValv = table[indx] + ".";
            columnsWithOutTableName.push(singleColumn.replace(tableValv, ""));
        }
    }


    var singleColumnWithOutDot = [];
    var columnSplit = [];
    for (var indx = 0; indx < columnsWithOutTableName.length; indx++) {
        var singleColumn = columnsWithOutTableName[indx];
        var lengthK = singleColumn.split(".");
        if (lengthK.length >= 2) {
            var singleColumnWithQuote = "`" + columnsWithOutTableName[indx] + "`";
            replaceColumnNames.push(singleColumnWithQuote);
            var eachColumn = singleColumn.split(".");
            var expectedColumn = "";
            for (var indx2 = 0; indx2 < eachColumn.length; indx2++) {
                expectedColumn += "`" + eachColumn[indx2] + "`";
                if (indx2 == 0) {
                    expectedColumn = expectedColumn + ".";
                }
            }
            expectedColumnNames.push(expectedColumn);
        }

    }
    var tableSplit = [];

    for (var indx = 0; indx < table.length; indx++) {
        var singleTable = table[indx];
        var lengthL = singleTable.split(".");

        if (lengthL.length >= 2) {

            var singleTableWithQuote = "`" + table[indx] + "`";
            replaceArray.push(singleTableWithQuote);
            var eachTable = singleTable.split(".");
            var expectedTable = "";
            for (var indx2 = 0; indx2 < eachTable.length; indx2++) {
                expectedTable += "`" + eachTable[indx2] + "`";
                if (indx2 == 0) {
                    expectedTable = expectedTable + ".";
                }
            }
            expectedArray.push(expectedTable);
        }

    }


    var expectedCaseArray = [];
    var replacedCaseArray = [];

    for (var indx = 0; indx < table.length; indx++) {
        var singleTable = table[indx];
        var lengthL = singleTable.split(".");
        if (lengthL.length >= 2) {
            var singleTableWithQuote = "`" + table[indx] + "`.`";
            replacedCaseArray.push(singleTableWithQuote);
            var eachTable = singleTable.split(".");
            var expectedTable = "";
            for (var indx2 = 0; indx2 < eachTable.length; indx2++) {
                if (indx2 == 0) {
                    expectedTable += "`" + eachTable[indx2] + "``";
                }
                if (indx2 == 1) {
                    expectedTable += "``" + eachTable[indx2] + "``.``"
                }
                if (indx2 == 0) {
                    expectedTable = expectedTable + ".";
                }

            }
            expectedCaseArray.push(expectedTable);
        }

    }


    var groupByExpectedArray = []
    for (indx = 0; indx < expectedArray.length; indx++) {
        var singleGroupByExpected = "`" + expectedArray[indx] + "`";
        groupByExpectedArray.push(singleGroupByExpected);
    }


    var fromIndex = query.indexOf("\nfrom\n");
    var whereIndex = query.indexOf("\nwhere\n");
    var groupByIndex = query.indexOf("\ngroup by\n");;
    var havingIndex = query.indexOf("\nhaving\n");
    var orderByIndex = query.indexOf("\norder by\n");

    var fromEndIndex;
    if (whereIndex > -1) {
        fromEndIndex = whereIndex
    } else if (groupByIndex > -1) {
        fromEndIndex = groupByIndex;
    } else if (havingIndex > -1) {
        fromEndIndex = havingIndex;
    } else if (orderByIndex > -1) {
        fromEndIndex = orderByIndex
    } else {
        fromEndIndex = query.length;
    }

    var whereEndIndex;
    if (groupByIndex > -1) {
        whereEndIndex = groupByIndex;
    } else if (havingIndex > -1) {
        whereEndIndex = havingIndex;
    } else if (orderByIndex > -1) {
        whereEndIndex = orderByIndex
    } else {
        whereEndIndex = query.length;
    }


    var groupByEndIndex;
    if (havingIndex > -1) {
        groupByEndIndex = havingIndex;
    } else if (orderByIndex > -1) {
        groupByEndIndex = orderByIndex
    } else {
        groupByEndIndex = query.length;
    }

    var havingEndIndex;
    if (orderByIndex > -1) {
        havingEndIndex = orderByIndex
    } else {
        havingEndIndex = query.length;
    }


    var selectClause = query.substring(0, fromIndex);
    var fromClause = query.substring(fromIndex, fromEndIndex);
    var whereClause = whereIndex > -1 ? query.substring(whereIndex, whereEndIndex) : "";
    var groupByClause = groupByIndex > -1 ? query.substring(groupByIndex, groupByEndIndex) : "";
    var havingClause = havingIndex > -1 ? query.substring(havingIndex, havingEndIndex) : "";
    var orderByClause = orderByIndex > -1 ? query.substring(orderByIndex, query.length) : "";
    var joinByClause;
   // return fromClause;



    var databaseNameValue = obj.databaseName;

    if (catalogName != undefined && "" !== catalogName) {
        databaseNameValue = databaseNameValue.split(catalogName + ".").join("");
    }

    databaseSplitArray = databaseNameValue.split(".");
    var databaseName = "";
    for (var indx2 = 0; indx2 < databaseSplitArray.length; indx2++) {
        databaseName += "`" + databaseSplitArray[indx2] + "`";
        databaseName = databaseName + ".";

    }
    var selectWithoutDbName = selectClause.split(databaseName).join("");
    var whereWithoutDbName = whereClause.split(databaseName).join("");
    var groupByWithoutDbName = groupByClause.split(databaseName).join("");
    var havingWithoutDbName = havingClause.split(databaseName).join("");
    var orderByWithoutDbName = orderByClause.split(databaseName).join("");

	
	groupByWithoutDbName =groupByWithoutDbName.replaceAll('distinct(','(');
    if (whereWithoutDbName.includes("[nr]")) {
        whereWithoutDbName = whereWithoutDbName.split("[nr]").join("[\\n\\r]");
    }
 
   if((fromClause.match(/from/g) || []).length ==1 && fromClause.split(".").length >= 3){
	    fromClause = fromClause.replace("`.`",".");
   }
    var startBracArray = [];
    var endBracArray = [];
    if (fromClause.includes("join")) {
        for (var bracIndex = 0; bracIndex < fromClause.length; bracIndex++) {

            if (fromClause[bracIndex] === "(") {
                startBracArray.push(bracIndex);
            }
            if (fromClause[bracIndex] === ")") {
                endBracArray.push(bracIndex);
            }
        }
        var fromClauseExpected = [];
        var fromClauseReplaced = [];

        for (var bracCheckIndex = 0; bracCheckIndex < startBracArray.length; bracCheckIndex++) {
			
            var bracString = "";
            bracString = fromClause.substring(startBracArray[bracCheckIndex], endBracArray[bracCheckIndex]);
			if(bracString.match(/select/gi)!= null){
				continue;
			}
            fromClauseExpected.push(bracString);

            var replaceBracString = "";
            replaceBracString = bracString.split(databaseName).join("");
            fromClauseReplaced.push(replaceBracString);


        }
        fromClause = replaceOriginal(fromClauseExpected, fromClauseReplaced, fromClause);


    }

    var asValuesArray = [];
    var aliases = selectWithoutDbName.split(",");
	

    for (index = 0; index < aliases.length; index++) {
        var asValue = aliases[index].substring(aliases[index].lastIndexOf(" as "), aliases[index].length)
        asValue = asValue.replace(" as ", "").trim()
        asValuesArray.push(asValue);
    }
 
    var aliasesText = [];

    for (aliasIndex = 0; aliasIndex < obj.formData.columns.length; aliasIndex++) {

        var aliasText = obj.formData.columns[aliasIndex].alias.replaceAll(".", "_");
        if (isInArray("`" + aliasText + "`", asValuesArray)) {
            aliasesText.push("`" + aliasText + "`");
        }
    }
//return aliasesText;


    var fromToOrderBy;
    if (orderByWithoutDbName) {
        var splitOrder = orderByWithoutDbName.split(",");
        for (var indx = 0; indx < splitOrder.length; indx++) {
            var itmO = splitOrder[indx].replace(" asc", "").replace(" desc", "").trim();
            if (indx == 0) {
                itmO = splitOrder[indx].substring(splitOrder[indx].indexOf("\n\t"), splitOrder[indx].length).replace(" asc", "").replace(" desc", "").trim();
            }
            var indexOfFound = selectWithoutDbName.indexOf(itmO);
            if (indexOfFound > -1) {
                indexOfFound = indexOfFound + itmO.length;
                var commaFound = selectWithoutDbName.indexOf(",", indexOfFound);
                if (commaFound == -1) {
                    commaFound = selectWithoutDbName.length;
                }
                var ascDesc;
                if (splitOrder[indx].indexOf("asc") != -1) {
                    ascDesc = " asc";

                } else {
                    ascDesc = " desc"
                }

                itmO = selectWithoutDbName.substring(indexOfFound, commaFound).replace("  as ", "").replace(" as ", "") + ascDesc;

                splitOrder[indx] = itmO;
            }

        }

        fromToOrderBy = splitOrder.join(",");

    }
	
    var requiredQueryForSpark;
    var aliasesJoined = aliasesText.join(",");

    var selectKeyword = 'select ';
    var caseQuery;

    if (selectWithoutDbName.includes("`CASE")) {
        var indexOFCase = selectWithoutDbName.indexOf("`CASE");
        var indexOFEnd = selectWithoutDbName.indexOf("END`") + 4;
        caseQuery = selectWithoutDbName.substring(indexOFCase, indexOFEnd);
        var replaceCaseQuery = replaceOriginal(expectedCaseArray, replacedCaseArray, caseQuery)
        selectWithoutDbName = selectWithoutDbName.replace(caseQuery, replaceCaseQuery)
        selectWithoutDbName = selectWithoutDbName.replace("`CASE", "CASE")
        selectWithoutDbName = selectWithoutDbName.replace("END`", "END")
    }


    var appliedRank = "row_number()";
    if (selectWithoutDbName.includes("distinct")) {
        appliedRank = "dense_rank()"
    }
    if (orderByWithoutDbName)


        requiredQueryForSpark = selectKeyword + '' + aliasesJoined + ' from (' + selectWithoutDbName + ',' + appliedRank + ' over ( ' + orderByWithoutDbName + ') as `rank`';
    else
        requiredQueryForSpark = selectKeyword + '' + aliasesJoined + ' from (' + selectWithoutDbName + ',' + appliedRank + ' over (ORDER BY 1) as `rank`';
	if(limit==0){
		requiredQueryForSpark += fromClause + whereWithoutDbName + groupByWithoutDbName + havingWithoutDbName + ' ) outerQuery ';
	}else{
    requiredQueryForSpark += fromClause + whereWithoutDbName + groupByWithoutDbName + havingWithoutDbName + ' ) outerQuery where `rank` between ' + (parseInt(offset) + 1) + ' and ' + (parseInt(offset) + parseInt(limit) + '');
	}

    var actlQry = requiredQueryForSpark;



    if (replaceFilterValuesArray) {

        actlQry = replaceOriginal(filterValuesArray, replaceFilterValuesArray, actlQry);
    }

    actlQry = replaceOriginal(groupByExpectedArray, expectedArray, actlQry);

    actlQry = replaceOriginal(expectedArray, replaceArray, actlQry);

    if (!fromClause.includes("join")) {
        actlQry = replaceOriginal(expectedColumnNames, replaceColumnNames, actlQry);
    }

    if (actualFilterValuesArray && otherReplaceFilterValuesArray.length > 0) {
        if (actlQry.includes(actualFilterValuesArray[0])) {

            actlQry = replaceOriginal(actualFilterValuesArray, otherReplaceFilterValuesArray, actlQry)
        } else {
            actlQry = replaceOriginal(actualFilterValuesArrayWithOutQuotes, otherReplaceFilterValuesArray, actlQry)
        }
    }
    return actlQry;


}

function isInArray(value, array) {
    return array.indexOf(value) > -1;
}


Array.prototype.pushUnique = function (item){
    if(this.indexOf(item) == -1) {
        this.push(item);
    } 
}

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
String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};