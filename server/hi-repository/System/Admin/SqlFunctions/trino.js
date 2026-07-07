function setQueryOffsetLimit(query, offset, limit, context) {

	var contextObj= JSON.parse(context);
	var replaceKey={};
	var tableAlias={}
	if(contextObj.metadata&& contextObj.metadata.database && contextObj.metadata.database.tables && contextObj.metadata.database.tables.tableList){
		var tableList=contextObj.metadata.database.tables.tableList;

		for(i=0; i<tableList.length; i++){
			tableAlias[contextObj.metadata.database.name+"."+tableList[i].name]=tableList[i].name
		}
	}

	var duplicateTables=contextObj.formData.duplicateTableMap;
	if(duplicateTables){
		for(i=0; i<contextObj.requestedTables.length;i++){
			if(duplicateTables[contextObj.requestedTables[i]]){
				replaceKey[contextObj.requestedTables[i]]=tableAlias[contextObj.requestedTables[i]]
			}
		}
	}


	var reformedQuery=formQueryWithPagination(query, offset, limit, context);

	for (x in replaceKey) {
		var findText="\""+x.split(".").join("\".\"")+"\""
		var replaceText="\""+replaceKey[x]+"\""
		reformedQuery = reformedQuery.replaceAll(findText,replaceText);
	}
	return reformedQuery;

}

function formQueryWithPagination(query, offset, limit, context) {
	//return query;
	var obj = JSON.parse(context);
	var filtersArray = obj.formData.filters;
	var actualFilterValuesArray = [];
	var actualFilterValuesArrayWithOutQuotes = [];
	var replaceFilterValuesArray = [];
	var functionName;
	var requiredToCast;

	if (filtersArray !== undefined) {
		for (var ind = 0; ind < filtersArray.length; ind++) {
			var outerFunctionReturnType = undefined;
			var singleFilter = filtersArray[ind];
			var valuesArray = singleFilter.values;
			var filterDataType = singleFilter.dataType;
			var databseFunction = singleFilter.databaseFunction;

			if (databseFunction !== undefined) {
				functionName = databseFunction.functionName;
				outerFunctionReturnType = databseFunction.dataType;
				requiredToCast = (outerFunctionReturnType === 'dateTime' || outerFunctionReturnType === 'date' || outerFunctionReturnType === 'time');
			} else {

				if (filterDataType === 'java.sql.Timestamp') {
					outerFunctionReturnType = 'dateTime';
					requiredToCast = true;
				} else if (filterDataType === 'java.sql.Date') {
					outerFunctionReturnType = 'date';
					requiredToCast = true;
				}
			}



			if (valuesArray !== undefined) {
				var type = undefined;

				if (compareEqual(outerFunctionReturnType, "dateTime")) {
					type = "TIMESTAMP ";

				}

				if (compareEqual(outerFunctionReturnType, "date")) {
					type = "DATE ";

				}
				if (compareEqual(outerFunctionReturnType, "time")) {
					type = "TIME ";

				}


				if (type != undefined && requiredToCast) {
					for (var index = 0; index < valuesArray.length; index++) {
						var arrayElement = valuesArray[index];

						if (arrayElement != null && arrayElement.includes(",")) {
							var separateValueArray = arrayElement.split(',');
							prepareArraysForReplace(separateValueArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, replaceFilterValuesArray, type, functionName)
						} else if (arrayElement != null && arrayElement.includes("AND")) {
							var inRangeValuesArray = arrayElement.split('AND');
							prepareArraysForReplace(inRangeValuesArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, replaceFilterValuesArray, type, functionName)
						} else {

							if (arrayElement != null /*&& !contains.call(checkFunction, functionName)*/) {
								arrayElement = arrayElement.replaceAll(")", "");
								arrayElement = arrayElement.replaceAll("'", "").trim();
								actualFilterValuesArrayWithOutQuotes.push(arrayElement);
								actualFilterValuesArray.push("'" + arrayElement + "'");
								replaceFilterValuesArray.push(type + "'" + arrayElement + "'");
							}

						}
					}
				}
			}
		}
	}

	//return outerFunctionReturnType;
	var formatedQuery;

	formatedQuery = query;
	if (replaceFilterValuesArray && replaceFilterValuesArray.length > 0) {
		if (formatedQuery.includes(actualFilterValuesArray[0])) {
			formatedQuery = replaceOriginal(actualFilterValuesArray, replaceFilterValuesArray, formatedQuery)
		} else {
			formatedQuery = replaceOriginal(actualFilterValuesArrayWithOutQuotes, replaceFilterValuesArray, formatedQuery)
		}
	}
	query = formatedQuery;

	//return query;

	//--------------------------------------
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

	var selectClause = query.substring(0, fromIndex).split("select").join("");
	var fromClause = query.substring(fromIndex, fromEndIndex);
	var whereClause = whereIndex > -1 ? query.substring(whereIndex, whereEndIndex) : "";
	var groupByClause = groupByIndex > -1 ? query.substring(groupByIndex, groupByEndIndex) : "";
	var havingClause = havingIndex > -1 ? query.substring(havingIndex, havingEndIndex) : "";
	var orderByClause = orderByIndex > -1 ? query.substring(orderByIndex, query.length) : "";
	//return orderByClause;
	whereClause = whereClause.replaceAll("'true'","true");
	whereClause = whereClause.replaceAll("'TRUE'","TRUE");
	whereClause = whereClause.replaceAll("'false'","false");
	whereClause = whereClause.replaceAll("'FALSE'","FALSE");

	var asValuesArray = [];
	var asColumnNamesArray = [];
	var aliases = selectClause.split(",");

	for (index = 0; index < aliases.length; index++) {

		var asValue = aliases[index].substring(aliases[index].lastIndexOf(" as "), aliases[index].length)

		asValue = asValue.replace(" as ", "").trim();
		asValuesArray.push(asValue);

	}

	var aliasesText = [];
	for (aliasIndex = 0; aliasIndex < obj.formData.columns.length; aliasIndex++) {
		var aliasText = obj.formData.columns[aliasIndex].alias.replace(".", "_");
		var columnName = obj.formData.columns[aliasIndex].column.split(".").join('"."');
		if (isInArray('"' + aliasText + '"', asValuesArray)) {
			aliasesText.push('"' + aliasText + '"');
			asColumnNamesArray.push('"' + columnName + '"');
		}
	}
	var aliasOrderBy = "";
	var aliasesJoined = aliasesText.join(",");
	var rankType = " row_number() ";
	if (selectClause.indexOf("distinct") > -1) {
		rankType = "dense_rank() ";
		// aliasOrderBy = "order by " + aliasesJoined + " asc";
		//return orderByClause.trim.length<=0 +" || "+ orderByClause;
		if (orderByClause.isEmpty()) {
			orderByClause = prepareOrderByClause(selectClause)
		}
	} else if ((groupByClause) && (orderByClause.isEmpty())) {
		orderByClause = prepareOrderByClauseForGroupBy(selectClause);
	}

	var q=" select " + selectClause  +  fromClause + whereClause + groupByClause + havingClause + " "+orderByClause;
	if(limit==0){
		return q
	}


	if (offset == 0) {
		return q + " \nlimit " + limit;
	}

	return q + " \noffset " + offset+" limit " + limit  ;


}

function compareEqual(str1, str2) {

	return ((str1 && str2) && (str1.trim() === str2.trim()));

}
function prepareArraysForReplace(extractArray, actualFilterValuesArrayWithOutQuotes, actualFilterValuesArray, replaceFilterValuesArray, type, functionName) {

	for (var indxx = 0; indxx < extractArray.length; indxx++) {

		singleSeperatedValue = extractArray[indxx];
		//if (!contains.call(checkFunction, functionName)) {
		singleSeperatedValue = singleSeperatedValue.replaceAll(")", "");
		singleSeperatedValue = singleSeperatedValue.replaceAll("'", "").trim();
		actualFilterValuesArrayWithOutQuotes.push(singleSeperatedValue);
		actualFilterValuesArray.push("'" + singleSeperatedValue + "'");
		replaceFilterValuesArray.push(type + "'" + singleSeperatedValue + "'");
		// }
	}
}
String.prototype.replaceAll = function (search, replacement) {
	var target = this;
	return target.split(search).join(replacement);
};
function replaceOriginal(expected, replace, query) {
	for (var indx = 0; indx < expected.length; indx++) {
		var actual = expected[indx];
		var replaceValue = replace[indx];
		if(replaceValue == undefined){
			replaceValue = actual
		}
		if (query.includes(actual) && actual!="'_all_'") {
			if(query.includes('DATE '+actual) ||
				query.includes('TIME '+actual) ||
				query.includes('TIMESTAMP '+actual)){
				//do nothing
			}else{
				query = query.split(actual).join(replaceValue);
			}
		}
	}
	return query;
}
function isInArray(value, array) {
	return array.indexOf(value) > -1;
}
String.prototype.isEmpty = function () {
	return (this.length === 0 || !this.trim());
};
/*function prepareOrderByClause(selectClause){
var startInd = selectClause.indexOf("(") + 1;
var endInd = selectClause.indexOf(")");
var orderBy = selectClause.substring(startInd, endInd);
if(orderBy.isEmpty){
startInd =selectClause.indexOf('"');
endInd = selectClause.indexOf(" as ");
orderBy = selectClause.substring(startInd, endInd).replaceAll("(","").replaceAll(")","");
}
return "order by " + orderBy + " asc";
}*/
function prepareOrderByClause(selectClause) {
	var startInd = selectClause.indexOf("select");
	var endInd = selectClause.indexOf(" as ");
	var orderBy = selectClause.substring(startInd, endInd).replace("distinct", "").trim();
	if (orderBy.isEmpty()) {
		startInd = selectClause.indexOf('"');
		endInd = selectClause.indexOf(" as ");
		orderBy = selectClause.substring(startInd, endInd).replaceAll("(", "").replaceAll(")", "");
	}
	return "order by " + orderBy + " asc";
}
function prepareOrderByClauseForGroupBy(selectClause) {
	var newSelectClause = selectClause.replace("select ", "").trim();
	var asSplitValues = newSelectClause.split(" as ");
	var requiredOrder = asSplitValues[0] + ' asc, ';
	for (index = 1; index < asSplitValues.length - 1; index++) {
		var splitValue = asSplitValues[index];
		var indexFirst = splitValue.indexOf(",");
		var requiredValue = splitValue.substring(indexFirst + 1);
		requiredOrder += requiredValue + " asc,"
	}
	requiredOrder = requiredOrder.substring(0, requiredOrder.lastIndexOf(","))

	return "order by " + requiredOrder;
}

