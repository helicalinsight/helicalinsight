function fixColumns(query,catalog, schema, tables, columns,openQuote, closeQuote){
	var q = query;
	var c;
	var toSearch;
	for(i=0;i<columns.length;i++)
	{
		c = columns[i].column;
		c = c.replaceAll(catalog+".", "");				
		c = c.replaceAll(schema+".", "");
		c = c.split("."); //to get table name
		c.shift();	//remove table name
		c = c.join("."); // actual column name
		toSearch = c.replaceAll(".", "].[");		
		q = q.replaceAll(toSearch, c);		 
	}
	
	return q;
}


function setQueryOffsetLimit(query, offset, limit, context) {	
	var contextObj = JSON.parse(context);
	var query_after_fixed_columns = fixColumns(query,contextObj.formData.metadataFileJson.database.catalog, contextObj.formData.metadataFileJson.database.schema,contextObj.requestedTables, contextObj.formData.columns,contextObj.openQuote, contextObj.closeQuote);
	/*if(limit != 0){
		/*This is a temporary fix with cdata as the group by behaves differenly with limit and offset
		Eg:  group by
	[CData].[SampleTravelData].[employee_details].[employee_id]  ORDER BY 1
	 limit 30 offset 20
	 For query without group by it should be
	 limit 10, offset 20
		*/
		/*if(query_after_fixed_columns.includes("group by")){
		query_after_fixed_columns = query_after_fixed_columns +" limit "+(parseInt(limit)+parseInt(offset))+" offset "+offset
		}else{
		query_after_fixed_columns = query_after_fixed_columns +" limit "+limit+" offset "+offset
		}
	}
	return query_after_fixed_columns;
	*/
	if (limit == 0) {
        return query_after_fixed_columns;
    }

    if (offset == 0) {
        return query_after_fixed_columns + " \nlimit " + limit;
    }
	
	if(query_after_fixed_columns.includes("group by")){
		return query_after_fixed_columns +" limit "+(parseInt(limit)+parseInt(offset))+" offset "+offset;
	}

    return query_after_fixed_columns + " \nlimit " + limit + " offset " + offset;
}

String.prototype.replaceAll = function (strReplace, strWith) {
	var esc = strReplace.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
	var reg = new RegExp(esc, 'g');
	return this.replace(reg, strWith);
};

String.prototype.replaceBetween = function (start, end, what) {
	return this.substring(0, start) + what + this.substring(end);
};
