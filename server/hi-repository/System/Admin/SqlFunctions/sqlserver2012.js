function setQueryOffsetLimit(query, offset, limit,context) {
var contextObj=JSON.parse(context);
	var databaseName=contextObj.databaseName;
	databaseName=databaseName.split(".").join("].[");
	
    databaseName="["+databaseName+"].";
	
	query=query.split(databaseName).join("");   

    if (limit == 0) {
        return query;
    }

   /* if (offset == 0) {
        return "select top "+limit+" * from (" + query + " ) table_";
    }
*/


 if (offset == 0) {
        return "select top "+ limit+ query.substring(query.indexOf("select")+6);
    }



	//    return   "select top "+limit+" * from (" + query + " ) table_ where row_number >= " + offset;

 return   query + " offset "+offset+ " rows fetch next "+limit+" rows only";

}

