function getRowNumber(query) {
    var rownumber = " rownumber() over(";
    var orderByIndex = query.toLowerCase().indexOf("order by");
    if (orderByIndex > 0 && !hasDistinct(query)) {
        rownumber = rownumber + (query.substring(orderByIndex));
    }

    rownumber += ") as rownumber";
    return rownumber.toString();
}


function removeOrderByFromQuery(query,orderByClause,startOfSelect,hasDistinct){
	var formatedQuery;
	if(orderByClause!=""){
		formatedQuery=query.replace(orderByClause,"")
	}else{
		formatedQuery =query;
	}
	
	return hasDistinct?formatedQuery.substring(startOfSelect):formatedQuery.substring(startOfSelect + 6);
	
}

function setQueryOffsetLimit(query, offset, limit, context) {
	//return query;
	
	
    query =query.replace("<> 'Null'",'IS NOT NULL');
    query =query.replace("<> Null",'IS NOT NULL');
    query=query.split('"null"').join('null');
    query=query.split("'null'").join('null');
    query=query.split('"NULL"').join('NULL');
    query=query.split("'NULL'").join('NULL');

    obj = JSON.parse(context);
    var databaseName = '"' + obj.databaseName + '"';
	var filtersArray = obj.formData.filters;
	var viewTablesArray = obj.derivedTableNames;
    var requestedColumnsWithFullyQualified;
    var requestedColumnWithoutDbName;
    var requestedColumnsArray = obj.formData.columns;
    var requestedColumnsWithDbName = [];
    var requestedColumnsWithoutDbName = [];
	var parameterNameWithoutDbName =[];
	var parameterNameWithDbName = [];
    var expectedColumnNames = [];
    var replaceColumnNames = [];
	var databseFunction;
	/*if (filtersArray !== undefined) {
        for (var ind = 0; ind < filtersArray.length; ind++) {
			
            var singleFilter = filtersArray[ind];
            
            databseFunction = singleFilter.databaseFunction;
           
	}}
	java.lang.System.out.println("databseFunction :"+JSON.stringify(obj));
	java.lang.System.out.println("requestedColumnsArray :"+JSON.stringify(requestedColumnsArray));
    for (var ind = 0; ind < requestedColumnsArray.length; ind++) {
        var singleColumn = requestedColumnsArray[ind];
        if (singleColumn.hasOwnProperty('databaseFunction')||databseFunction) {
            var requestedColumnsWithFullyQualified = '"' + singleColumn.column + '"';
			var columnSplitArray =singleColumn.column.split(".");

			if(!isInArray(columnSplitArray[1], viewTablesArray)){
			if(singleColumn.hasOwnProperty('databaseFunction') && singleColumn.databaseFunction.hasOwnProperty('parameters')){
				var functionParameters =singleColumn.databaseFunction.parameters;
				
				for (var key in functionParameters) {
					if (functionParameters.hasOwnProperty(key)) {
					var value = functionParameters[key];
					
						value =value.split(".").join('"."').trim();
						value ='"'+value+'"'
						parameterNameWithoutDbName.push(value);
						parameterNameWithDbName.push(databaseName+"."+value);
					}
				}
			}
            requestedColumnsWithFullyQualified = requestedColumnsWithFullyQualified.split(".").join('"."');
            requestedColumnsWithDbName.push(requestedColumnsWithFullyQualified);
			
            requestedColumnWithoutDbName = requestedColumnsWithFullyQualified.replace(databaseName + '.', "");
            requestedColumnsWithoutDbName.push(requestedColumnWithoutDbName);
            requestedColumnsWithFullyQualified = "";
            requestedColumnWithoutDbName = "";
			}
        }
    }
	//return requestedColumnsWithDbName;
	//return parameterNameWithDbName +"  "+ requestedColumnsWithDbName +" || "+ parameterNameWithoutDbName+" "+requestedColumnsWithoutDbName;
	requestedColumnsWithDbName =isInBothArray(parameterNameWithDbName,requestedColumnsWithDbName);
	//return requestedColumnsWithDbName;
	requestedColumnsWithoutDbName =isInBothArray(parameterNameWithoutDbName,requestedColumnsWithoutDbName);
	//return requestedColumnsWithDbName + "  -- " +  requestedColumnsWithoutDbName;
	*/
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

    var aliasesJoined = aliasesText.join(",");
    var startOfSelect = query.toLowerCase().indexOf("select");
   
    var aliasesJoined = aliasesText.join(",");
    var pagingSelect = query.substring(0, startOfSelect) + "select " + aliasesJoined + ' from ( select ';
	
    if (hasDistinct(query)) {
		// pagingSelect += "row_.*," + getRowNumber123(query) + " from ( " + query.substring(startOfSelect) + " ) as row_";
        pagingSelect += "row_.*," + getRowNumber(query) + " from ( " +removeOrderByFromQuery(query,orderByClause,startOfSelect,true) + " ) as row_";
    } else {
		
        pagingSelect += getRowNumber(query) + ", " + removeOrderByFromQuery(query,orderByClause,startOfSelect);
			
    }
	
	if(limit==0){
		return pagingSelect += " ) as temp_ ";
	}
    pagingSelect += " ) as temp_ where rownumber ";
    if (offset) {
        pagingSelect += "between " + (parseInt(offset) + parseInt(1)) + " and " + (parseInt(offset) + parseInt(limit));
    } else {
        pagingSelect += "<= " + parseInt(limit);
    }
return pagingSelect;
	//pagingSelect =replaceOriginal(requestedColumnsWithoutDbName, requestedColumnsWithDbName, pagingSelect);
    //return pagingSelect;
}

function hasDistinct(query) {
    return query.toLowerCase().indexOf("distinct") >= 0;
}

function replaceOriginal(expected, replace, query) {

    for (var indx = 0; indx < expected.length; indx++) {
        var actual = expected[indx];
        var replaceValue = replace[indx];
		
		if (query.includes("."+actual)) {
            query = query.split("."+actual).join("#@rep#");
        }
		if (query.includes(actual)) {
            query = query.split(actual).join(replaceValue);
        }
		 query = query.split("#@rep#").join("."+actual);
		
    }
    return query;
}

function isInArray(value, array) {
    return array.indexOf(value) > -1;
}
function isInBothArray(checkArray,insertArray){
for (var i = 0; i < checkArray.length; i++) {
				if (insertArray.indexOf(checkArray[i]) === -1) {
				insertArray.push(checkArray[i]);
					
				}
			}
			return insertArray;
}