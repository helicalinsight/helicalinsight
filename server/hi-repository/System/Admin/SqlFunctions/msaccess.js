function setQueryOffsetLimit(query, offset, limit, context) {

	var obj = JSON.parse(context);
	var filtersArray = obj.formData.filters;
	var actualFilterValuesArray = [];
	var actualFilterValuesArrayWithOutQuotes = [];
	var replaceFilterValuesArray = [];
	var functionName;
	var requiredToCast;
	var orederByClauseFrmContext = "";
	if (obj.formData.functions.orderBy) {
		var orderByArray = obj.formData.functions.orderBy;
		var preparedArr = [];
		for (var ind = 0; ind < orderByArray.length; ind++) {
			var eachVal = orderByArray[ind];
			//java.lang.System.out.println("eachVal :" + JSON.stringify(eachVal));
			preparedArr.push("[" + eachVal.alias + "] " + eachVal.order);
		}
		orederByClauseFrmContext = "\norder by\n" + preparedArr.toString();
	}

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
				requiredToCast = (outerFunctionReturnType === 'dateTime' || outerFunctionReturnType === 'date');
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

	if (replaceFilterValuesArray && replaceFilterValuesArray.length > 0) {
		if (query.includes(actualFilterValuesArray[0])) {
			query = replaceOriginal(actualFilterValuesArray, replaceFilterValuesArray, query)
		} else {
			query = replaceOriginal(actualFilterValuesArrayWithOutQuotes, replaceFilterValuesArray, query)
		}
	}

	var orederByClauseFrmContext = "";
	if (obj.formData.functions.orderBy) {
		var orderByArray = obj.formData.functions.orderBy;
		var preparedArr = [];
		for (var ind = 0; ind < orderByArray.length; ind++) {
			var eachVal = orderByArray[ind];
			//java.lang.System.out.println("eachVal :" + JSON.stringify(eachVal));
			preparedArr.push("[" + eachVal.alias + "] " + eachVal.order);
		}
		orederByClauseFrmContext = "\norder by\n" + preparedArr.toString();
	}

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

	if (orderByClause) {
		query = query.replaceBetween(orderByIndex, query.length, orederByClauseFrmContext);
		orderByClause = orederByClauseFrmContext;
	}

	var asValuesArray = [];
	var aliases = selectClause.split(",");

	for (index = 0; index < aliases.length; index++) {
		var asValue = aliases[index].substring(aliases[index].lastIndexOf(" as "), aliases[index].length)
			asValue = asValue.replace(" as ", "").trim()
			asValuesArray.push(asValue);
	}

	var aliasesText = [];
	var aliasesSubText = [];
	var aliasesSubTextWithOutBrac = [];
	var aliasesSubSubText = [];

	for (aliasIndex = 0; aliasIndex < obj.formData.columns.length; aliasIndex++) {

		var aliasText = obj.formData.columns[aliasIndex].alias.replaceAll(".", "_");
		if (isInArray("[" + aliasText + "]", asValuesArray)) {
			aliasesText.push("[" + aliasText + "]");
			aliasesSubText.push("sub." + "[" + aliasText + "]");
			aliasesSubTextWithOutBrac.push(aliasText); //subOrdered
			aliasesSubSubText.push("subOrdered." + "[" + aliasText + "]");
		}
	}
	var splitByAs = selectClause.split("as [");

	var hasDistinct = query.indexOf("distinct") > -1;
	for (var indexx = 0; indexx < splitByAs.length; indexx++) {
		var selectPart = splitByAs[indexx];
		if ((selectPart.indexOf("distinct(") > -1)) {
			selectPart = selectPart.replace("distinct(", "");
			var lastIndexOfRightParanthesis = selectPart.lastIndexOf(")");
			if (lastIndexOfRightParanthesis > -1) {
				selectPart = selectPart.substring(0, lastIndexOfRightParanthesis);
			}
			if (indexx == 0) {
				selectPart = selectPart.replace("select distinct", "select ");
				selectPart = selectPart.replace("select", "select distinct ");
			}

		}
		splitByAs[indexx] = selectPart;
	}
	selectClause = splitByAs.join(" as [");

	//Handle groupBy
	{
		var splitGroupBy = groupByClause.split(",");
		for (var indexg = 0; indexg < splitGroupBy.length; indexg++) {
			var itemsg = splitGroupBy[indexg];
			if (itemsg.indexOf("distinct(") > -1) {
				itemsg = itemsg.replace("distinct(", "");
				itemsg = itemsg.substring(0, itemsg.lastIndexOf(")"));

			}

			splitGroupBy[indexg] = itemsg;
		}
		groupByClause = splitGroupBy.join(",");

	}
	//Handle groupBy


	//Handle orderby
	{
		var splitOrderBy = orderByClause.split(",");
		for (var index = 0; index < splitOrderBy.length; index++) {
			var items = splitOrderBy[index];
			if (items.indexOf("distinct(") > -1) {
				items = items.replace("distinct(", "");
				items = items.replace(") asc", " asc");
				items = items.replace(") desc", " desc");
			}

			splitOrderBy[index] = items;
		}
		orderByClause = splitOrderBy.join(",");

	}
	//Handle orderby

	query = selectClause + fromClause + whereClause + groupByClause + havingClause + orderByClause;

	if (limit == 0) {
		return query;
	}

	if (offset == 0) {
		if (hasDistinct) {
			// query = query.replace("select distinct", "select distinct top " + limit);
			query = query.replace("select distinct", "select top " + limit + " distinct");
		} else {
			query = query.replace("select", " select top " + limit);

		}
		return query;
	}
	java.lang.System.out.println("orderByClause :" + orderByClause)
	var subOrderBy = orderByClause;
	var subOrderedOrderBy = "orderByClause";
	var fromToOrderBy;
	//return query+"\nabcdefgh";
	if (query.indexOf("order by") > -1) {
		for (var index = 0; index < aliasesSubTextWithOutBrac.length; index++) {
			if (orderByClause.indexOf(aliasesText[index]) > 1) {
				subOrderBy = orderByClause.replace(aliasesText[index], aliasesSubText[index]);
				subOrderBy = replaceOrder(subOrderBy);
				subOrderedOrderBy = orderByClause.replace(aliasesText[index], aliasesSubSubText[index]);
			}

		}
		fromToOrderBy = orderByClause;
		

	} else {
		java.lang.System.out.println("default order by.");
		fromToOrderBy = "order by " + aliasesText[0];
		subOrderBy = "order by sub." + aliasesText[0] + " desc";
		subOrderedOrderBy = "order by subOrdered." + aliasesText[0];

	}
	var innerTopValue = (parseInt(limit, 10) + parseInt(offset, 10));
	var asAliases = aliasesText.join(",");
	selectClause = selectClause.replace("select", "select top " + innerTopValue + " ");
	var outerQueryStart = "\tSELECT * FROM ( \n\tSELECT  top " + limit + " " + asAliases + " \n\t FROM (\n\t";
	var outerQueryEnd = "\n\t) sub \n\t " + subOrderBy + "\n\t) subOrdered " + subOrderedOrderBy;
	/*SELECT * FROM ( SELECT Top 5 sub.dimdate_fiscal_year,sub.id
	FROM (
	SELECT Top 15  [dimdate].[fiscal_year]  as [dimdate_fiscal_year] ,[dimdate].[dim_id] as [id]
	from
	[dimdate]

	--where
	--	([dimdate].[fiscal_year]  NOT IN ( TIMESTAMP '2013-01-05 00:00:00.0',TIMESTAMP '2013-01-06 00:00:00.0'))
	--group by
	--	[dimdate].[fiscal_year],[dimdate].[day_number]
	order by  id
	) sub
	ORDER BY sub.id desc
	) subOrdered ORDER BY subOrdered.id*/
	return outerQueryStart + selectClause + fromClause + whereClause + groupByClause + havingClause + fromToOrderBy + outerQueryEnd;

	/*
	return "with query as (    select inner_query.*         , ROW_NUMBER() over (" + fromToOrderBy + ") as _hi_row_nr_      from (" + (selectClause + fromClause + whereClause + groupByClause + havingClause) +
	") inner_query  )  select * from query where _hi_row_nr_ >= " +( parseInt(offset)+1) + " and _hi_row_nr_ <=" + (parseInt(offset) + parseInt(limit));*/

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

		if (query.includes(actual)) {
			query = query.split(actual).join(replaceValue);
		}
	}
	return query;
}
function isInArray(value, array) {
	return array.indexOf(value) > -1;
}
String.prototype.replaceBetween = function (start, end, what) {
	return this.substring(0, start) + what + this.substring(end);
};
function replaceOrder(subOrderBy) {
	var replacedOrder = subOrderBy;
	if (subOrderBy.indexOf('desc') > 1) {
		replacedOrder = subOrderBy.replaceAll('desc', 'asc');
	} else if (subOrderBy.indexOf('asc') > 1) {
		replacedOrder = subOrderBy.replaceAll('asc', 'desc');
	}
	return replacedOrder;
}

