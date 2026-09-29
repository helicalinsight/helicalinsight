

function setQueryOffsetLimit(query, offset, limit,context) {
		var contextObj = JSON.parse(context);
	var databaseName=contextObj.databaseName
	var cat=contextObj.formData.metadataFileJson.database.catalog;
	var sch=contextObj.formData.metadataFileJson.database.schema;
	//databaseName=databaseName.split(".").join("\".\"");
databaseName="\""+databaseName+"\".";
	query=query.split(databaseName).join("");
    query=query.split('"null"').join('null');
    query=query.split("'null'").join('null');
    query=query.split('"NULL"').join('NULL');
    query=query.split("'NULL'").join('NULL');
    query=query.split('"'+cat+'"."'+sch+'".').join('');
	
	
 obj = JSON.parse(context);

var orderByArr = obj.formData.functions.orderBy;


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
								if(arrayElement!="_all_")
                                otherReplaceFilterValuesArray.pushUnique( "'" + arrayElement + "'"+"::"+type);
                            }

                        }
                    }
                }
            }
        }
    }


//return query;
var fromIndex = query.indexOf("\nfrom\n");
    var whereIndex = query.indexOf("\nwhere\n");
    var groupByIndex = query.indexOf("\ngroup by\n");
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


 var fromToOrderBy = "";

 if(orderByArr){
   fromToOrderBy = "\n order by ";
   var orderAliasArry =[];
   for(var index=0;index<orderByArr.length;index++){
		var eachOrderAliasObj = orderByArr[index];
        orderAliasArry.push('"'+eachOrderAliasObj.alias+'"'+" "+eachOrderAliasObj.order);
   }
   fromToOrderBy+=orderAliasArry.join();
 }
 
 
	whereClause = whereClause.split("\= \'\"null\"\'").join(' IS NULL');
	whereClause = whereClause.split("\= \'\"NULL\"\'").join(' IS NULL');
	whereClause = whereClause.split('\= \"NULL\"').join(' IS NULL');
	whereClause = whereClause.split('\= \"null\"').join(' IS NULL');
	
	



    query = selectClause + fromClause + whereClause + groupByClause + havingClause + fromToOrderBy;

	var OriginalQuery = query;


	if (actualFilterValuesArray && otherReplaceFilterValuesArray.length > 0) {
        if (OriginalQuery.includes(actualFilterValuesArray[0])) {

            OriginalQuery = replaceOriginal(actualFilterValuesArray, otherReplaceFilterValuesArray, OriginalQuery)
        }
        else {
            OriginalQuery = replaceOriginal(actualFilterValuesArrayWithOutQuotes, otherReplaceFilterValuesArray, OriginalQuery)
        }
    }

	//return query;
	OriginalQuery = OriginalQuery.split(" distinct ").join("");  
	
    if (limit == 0) {
        return OriginalQuery;
    }

    if (offset == 0) {
        return OriginalQuery + " \nlimit " + limit; 
    }
	
	//The 2nd page will fetch all data
if(offset>0){
	return OriginalQuery + " \nlimit " + limit * offset;
}
    
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
   