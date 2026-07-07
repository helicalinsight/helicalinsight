function setQueryOffsetLimit(query, offset, limit,context) {
	var contextObj = JSON.parse(context);
	var databaseName=contextObj.databaseName
	databaseName=databaseName.split(".").join("\".\"");
databaseName="\""+databaseName+"\".";
	query=query.split(databaseName).join("");
    query=query.split('"null"').join('null');
    query=query.split("'null'").join('null');
    query=query.split('"NULL"').join('NULL');
    query=query.split("'NULL'").join('NULL');
	//return query;
    if (limit == 0) {
        return query;
    }

    if (offset == 0) {
        return query + " \nlimit " + limit;
    }

    return query + " \nlimit " + limit + " offset " + offset;
}
