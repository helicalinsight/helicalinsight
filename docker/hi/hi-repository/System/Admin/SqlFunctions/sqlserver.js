function setQueryOffsetLimit(query, offset, limit, context) {
	//return query;
	var contextObj = JSON.parse(context);
	var databaseName = contextObj.databaseName;
	/*databaseName=databaseName.split(".").join("].[");

	databaseName="["+databaseName+"].";

	query=query.split(databaseName).join(""); */
	var orederByClauseFrmContext = "";
	if (contextObj.formData.functions.orderBy) {
		var orderByArray = contextObj.formData.functions.orderBy;
		var preparedArr = [];
		for (var ind = 0; ind < orderByArray.length; ind++) {
			var eachVal = orderByArray[ind];
			//java.lang.System.out.println("eachVal :" + JSON.stringify(eachVal));
			preparedArr.push("[" + eachVal.alias + "] " + eachVal.order);
		}
		orederByClauseFrmContext = "\norder by\n" + preparedArr.toString();
	}
	//java.lang.System.out.println("orederByClauseFrmContext :" + orederByClauseFrmContext);
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

	whereClause = whereClause.replaceAll("''false''", "false");
	whereClause = whereClause.replaceAll("''true''", "true");
	whereClause = whereClause.replaceAll('"null"', "null");
	whereClause = whereClause.replace(
		/'[^']*'|"(?:[^"]*)"|\bfalse\b|\btrue\b|\bnull\b/gi,
		function (match) {

			// If it's a quoted string → return unchanged
			if (match.charAt(0) === "'" || match.charAt(0) === '"') {
				return match;
			}

			// Normalize standalone words
			var lower = match.toLowerCase();

			if (lower === "false") return "'false'";
			if (lower === "true")  return "'true'";
			if (lower === "null")  return "null";

			return match;
		}
	);

	if (orderByClause) {
		query = query.replaceBetween(orderByIndex, query.length, orederByClauseFrmContext);
		orderByClause = orederByClauseFrmContext;
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
	java.lang.System.out.println("orderByClasuse :" + orderByClause); {
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
			query = query.replace("select distinct", "select distinct top " + limit);
		} else {
			query = query.replace("select", " select top " + limit);
		}
		return query;
	}

	/*order by
	patindex('%s%',[ProspectiveBuyer].[AddressLine1]) asc*/
	var fromToOrderBy;
	if (query.indexOf("order by") > -1) {
		/*var splitOrder = orderByClause.split(",");
		//return splitOrder;
		for (var indx = 0; indx < splitOrder.length; indx++) {
		var itmO = splitOrder[indx].replace(" asc", "").replace(" desc", "").trim();
		if (indx == 0) {
		itmO = splitOrder[indx].substring(splitOrder[indx].indexOf("\n\t"), splitOrder[indx].length).replace(" asc", "").replace(" desc", "").trim();
		}
		if ((itmO.indexOf('(') == 0) && (itmO.indexOf(')') == itmO.length - 1)) {
		itmO = itmO.replace("(", "").replace(")", "");
		}
		var indexOfFound = selectClause.indexOf(itmO);
		if (indexOfFound > -1) {
		indexOfFound = indexOfFound + itmO.length;
		var commaFound = selectClause.indexOf(",", indexOfFound);
		if (commaFound == -1) {
		commaFound = selectClause.length;
		}
		var ascDesc;
		if (splitOrder[indx].indexOf("asc") != -1) {
		ascDesc = " asc";

		} else {
		ascDesc = " desc"
		}

		itmO = "inner_query." + selectClause.substring(indexOfFound, commaFound).replace("  as [", "[").replace(" as [", "[") + ascDesc;
		if (indx == 0) {
		itmO = "order by\n\t" + itmO;
		}
		splitOrder[indx] = itmO;
		}

		}

		fromToOrderBy = splitOrder.join(",");*/

		fromToOrderBy = orderByClause;
	} else {

		fromToOrderBy = "order by CURRENT_TIMESTAMP";
	}
	//return fromToOrderBy;
	return "with query as (    select inner_query.* , ROW_NUMBER() over (" + fromToOrderBy + ") as _hi_row_nr_      from (" + (selectClause + fromClause + whereClause + groupByClause + havingClause) +
		") inner_query  )  select * from query where _hi_row_nr_ >= " + (parseInt(offset) + 1) + " and _hi_row_nr_ <=" + (parseInt(offset) + parseInt(limit));

}
String.prototype.replaceAll = function (strReplace, strWith) {
	var esc = strReplace.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
	var reg = new RegExp(esc, 'ig');
	return this.replace(reg, strWith);
};
String.prototype.replaceBetween = function (start, end, what) {
	return this.substring(0, start) + what + this.substring(end);
};
