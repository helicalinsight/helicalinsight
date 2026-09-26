function setQueryOffsetLimit(query, offset, limit,context) {
	
	var contextObj = JSON.parse(context);
	var databaseName = contextObj.databaseName;
	
	
	
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

	whereClause = whereClause.replaceAll("false", "'false'");
	whereClause = whereClause.replaceAll("true", "'true'");
	whereClause = whereClause.replaceAll("''false''", "'false'");
	whereClause = whereClause.replaceAll("''true''", "'true'");
	whereClause = whereClause.replaceAll('"null"', "null");
	
	
	
	
	
	
	
	
	databaseName=databaseName.split(".").join("\".\"");
	databaseName="\""+databaseName+"\".";
	
	query = selectClause.split(databaseName).join("")
				+ fromClause
				+ whereClause.split(databaseName).join("")
				+ groupByClause.split(databaseName).join("")
				+ havingClause.split(databaseName).join("") 
				+ orderByClause.split(databaseName).join("");
	
	
    query=query.split('"null"').join('null');
    query=query.split("'null'").join('null');
    query=query.split('"NULL"').join('NULL');
    query=query.split("'NULL'").join('NULL');
	
	//return query;
    if (limit == 0) {
        return query;
    }

	 query=query.substr(6);
    if (offset == 0) {
		
		query="select first "+limit+" "+query;
        return query
    }

    return "select first "+limit+" skip "+ offset+" "+query;
}




String.prototype.replaceAll = function (strReplace, strWith) {
	var esc = strReplace.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
	var reg = new RegExp(esc, 'ig');
	return this.replace(reg, strWith);
};










