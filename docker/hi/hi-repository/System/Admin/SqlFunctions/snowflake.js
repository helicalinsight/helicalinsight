function setQueryOffsetLimit(query, offset, limit, context) {


 obj = JSON.parse(context);

var orderByArr = obj.formData.functions.orderBy;


//return query;
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


 var fromToOrderBy = "";

 if(orderByArr){
   fromToOrderBy = "\n order by ";
   var orderAliasArry =[];
   for(var index=0;index<orderByArr.length;index++){
		var eachOrderAliasObj = orderByArr[index];
        orderAliasArry.push('"'+eachOrderAliasObj.alias+'"'+" "+eachOrderAliasObj.order);
   }
   fromToOrderBy+=orderAliasArry.join();
 }
 
 
	whereClause = whereClause.split("\= \'\"null\"\'").join(' IS NULL');
	whereClause = whereClause.split("\= \'\"NULL\"\'").join(' IS NULL');
	whereClause = whereClause.split('\= \"NULL\"').join(' IS NULL');
	whereClause = whereClause.split('\= \"null\"').join(' IS NULL');

/*


    var fromToOrderBy= "";
    if (query.indexOf("order by") > -1) {
        var splitOrder = orderByClause.split(",");
        for(var indx = 0 ; indx <splitOrder.length; indx ++){
            var itmO = splitOrder[indx].replace(" asc","").replace(" desc","").trim();
            if(indx==0){
                itmO=splitOrder[indx].substring(splitOrder[indx].indexOf("\n\t"),splitOrder[indx].length).replace(" asc","").replace(" desc","").trim();
            }
            var indexOfFound = selectClause.indexOf(itmO);
            if(indexOfFound>-1){
                indexOfFound=indexOfFound+itmO.length;
                var commaFound =selectClause.indexOf(",",indexOfFound);
                if(commaFound==-1){
                    commaFound=selectClause.length;
                }
                var ascDesc;
                if(splitOrder[indx].indexOf("asc")!=-1) {
                    ascDesc=" asc";

                }else{
                    ascDesc=" desc"
                }

                itmO = selectClause.substring(indexOfFound, commaFound).replace(" as ","" )+ascDesc;
                if(indx==0){
                    itmO= "order by\n\t"+itmO;
                }
                splitOrder[indx]=itmO;
            }

        }

        fromToOrderBy = splitOrder.join(",");

    } 
*/
    query = selectClause + fromClause + whereClause + groupByClause + havingClause + fromToOrderBy;



	if(limit == 0){
		return query;
	}
	if(offset == 0){
		return query + " FETCH FIRST "+ limit +" ROWS ONLY";
	}
	return query + " OFFSET "+offset+" ROWS" + " FETCH NEXT "+limit+" ROWS ONLY";
}
