function setQueryOffsetLimit(query, offset, limit, context) {

    if (limit == 0) {
        return query;
    }

   /* if (offset == 0) {
        return query ;
    }*/

    return formQueryWithPagination(query,offset,limit, context);
}




function formQueryWithPagination(query,offset,limit, context){

 obj = JSON.parse(context);
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
	if(orderByClause==""){
		orderByClause="order by CURRENT_DATE asc";
	}

var asValuesArray = [];
    var aliases = selectClause.split(",");

    for (index = 0; index < aliases.length; index++) {

        var asValue = aliases[index].substring(aliases[index].lastIndexOf(" as "), aliases[index].length)

        asValue = asValue.replace(" as ", "").trim()
        asValuesArray.push(asValue);
    }

    var aliasesText = [];
    for (aliasIndex = 0; aliasIndex < obj.formData.columns.length; aliasIndex++) {

        var aliasText = obj.formData.columns[aliasIndex].alias.replace(".", "_");
        if (isInArray('"' + aliasText + '"', asValuesArray)) {
            aliasesText.push('"' + aliasText + '"');
        }
    }

 var aliasesJoined = aliasesText.join(",");
 var startOfSelect = query.toLowerCase().indexOf("select");
 
 var selectKeyword = query.indexOf("distinct")> -1 ? "select distinct " :"select ";
 var rowNum = query.indexOf("distinct")> -1 ? " dense_rank() " :" row_number() ";
 
   var pagingSelect =  selectKeyword + aliasesJoined + ' from ( select ';

outerQuery =")outerQuery  where  rownum BETWEEN "+(parseInt(offset)+1)+" and "+(parseInt(offset)+parseInt(limit));
pagingSelect =pagingSelect +selectClause+ ","+ rowNum +" over("+orderByClause +" ) as rownum  "+fromClause +  whereClause+ groupByClause + havingClause +outerQuery;

return pagingSelect;

}

function isInArray(value, array) {
    return array.indexOf(value) > -1;
}