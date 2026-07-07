function setQueryOffsetLimit(query, offset, limit, ctx) {
    var context = JSON.parse(ctx);
	
    var database = context.metadata.database;
    var catalog = database.catalog;
    var catalogReplaceString = '`' + catalog + '`.';
    var schemaString = database.schema;
    var schemaReplaceString = '`' + schemaString + '`.';

    var re = requiredRegex(catalogReplaceString);
    query = query.replace(re, "");
    re = requiredRegex(schemaReplaceString);
    query = query.replace(re, "");
	//Handles the view immedieatly after from
	re = requiredRegex("\nfrom\n\t\\(");
    query = query.replace(re, "\nfrom\n \(");
	
    re = requiredRegex("\nfrom\n\t");
    query = query.replace(re, "\nfrom\n\t" + schemaReplaceString);

	re = requiredRegex("inner join \\(");
    query = query.replace(re, "inner join\t \(");
	
	
	re = requiredRegex("inner join ");
    query = query.replace(re, "inner join " + schemaReplaceString);
	
    if (limit == 0) {
        return query;
    }

    if (offset == 0) {
        return query + " \nlimit " + limit;
    }

    return query + " \nlimit " + limit + " offset " + offset;
}

function requiredRegex(str) {
    return new RegExp(str, "g");
}