function checkKeywords(inputString) {
    // Define the keywords to search for
    var keywords = ['min', 'max', 'sum', 'avg', 'distinct', 'ussa'];

    // Convert the input string to lowercase to make the search case-insensitive
    var lowerCaseInput = inputString.toLowerCase();

    // Check if any of the keywords are present in the string
    for (var i = 0; i < keywords.length; i++) {
        if (lowerCaseInput.indexOf(keywords[i]) !== -1) {
            return true;
        }
    }

    return false;
}


function parseSQL(sql) {
  
  const selectRegex = /^select\s+/i;
var result = {};

  // Remove "select" keyword
  const selectPart = sql.replace(selectRegex, '').trim();


var columnsStr = selectPart;
    var currentIndex = 0;

    while (currentIndex < columnsStr.length) {
        var asIndex = columnsStr.toLowerCase().indexOf(' as ', currentIndex);
        var commaIndex = columnsStr.indexOf(',', currentIndex);

        if (commaIndex === -1) commaIndex = columnsStr.length;

        if (asIndex !== -1 && asIndex < commaIndex) {
            // Extract column name and alias
            var colName = columnsStr.substring(currentIndex, asIndex).trim();
            var alias = columnsStr.substring(asIndex + 4, commaIndex).trim();
            result[colName]= alias;
        } else {
            // If there's no 'as', consider the whole part as column name with no alias
            var colName = columnsStr.substring(currentIndex, commaIndex).trim();
           result[colName]= null;
        }

        currentIndex = commaIndex + 1;
    }

  
  
  
    return result;
}
function escapeRegExp(string) {
    return string.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'); // $& means the whole matched string
}

function replacePlaceholders(json, str) {
    for (var key in json) {
        if (json.hasOwnProperty(key)) {
            var value = json[key];
            var regex = new RegExp(escapeRegExp(key), 'g');
            str = str.replace(regex,value);
        }
    }
    return str;
}


function insertStringAt(str, index, insertStr) {
    return str.substring(0, index) + insertStr + str.substring(index);
}




function setQueryOffsetLimit(query, offset, limit,context) {
	query=query.split("`").join("\"");
	
	const datePattern = /'(\d{4}-\d{2}-\d{2})'/g;

// Replace the date pattern with "date yyyy-dd-mm"
//query = query.replace(datePattern, "{d '$1'}");
	
	
	
	var fromIndex = query.indexOf("\nfrom\n");
    var whereIndex = query.indexOf("\nwhere\n");
    var groupByIndex = query.indexOf("\ngroup by\n");
    ;
    var havingIndex = query.indexOf("\nhaving\n");
    var orderByIndex = query.indexOf("\norder by\n");

    var fromEndIndex;
    if (whereIndex > -1) {
        fromEndIndex = whereIndex;
    } else if (groupByIndex > -1) {
        fromEndIndex = groupByIndex;
    } else if (havingIndex > -1) {
        fromEndIndex = havingIndex;
    } else if (orderByIndex > -1) {
        fromEndIndex = orderByIndex;
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
  
	
	
	
	
	
	var fromIndex = query.indexOf("\nfrom\n");
	 var selectClause = query.substring(0, fromIndex);
	 
	if(query.includes('as "display"') &&  query.includes("distinct")){
			const distinctRegex = /\bDISTINCT\b/gi;
			query = query.replace(distinctRegex, '');
			selectClause = selectClause.replace(distinctRegex, '');
			var asc=orderByClause.toLowerCase().indexOf(" asc")>-1?" asc ":"";
			 asc=orderByClause.toLowerCase().indexOf(" desc")>-1?" desc ":asc;
			
		var isKeywordPresent = checkKeywords(selectClause);
		
		var groupByClause = isKeywordPresent?"":' group by "display", "value"'  

		var newQuery ="";
		var finalWhere="";
		if(query.includes("like")){
					
				 	whereClause = whereClause.substring(whereClause.indexOf("(")+1,whereClause.lastIndexOf(")"));

					var inWherClause = whereClause.split("like");
					if(inWherClause[0].indexOf(")")==-1){
						 newQuery = ', concat('+inWherClause[0]  +') as "dummy"';
					}else{
						 newQuery = ', '+inWherClause[0]  +' as "dummy"';
					}
				   
					
					 groupByClause = isKeywordPresent?'group by "dummy" ' :groupByClause +', "dummy" ';
					 
					query = selectClause + newQuery + query.substring(query.indexOf("from"),query.indexOf("where\n"));	
					finalWhere = ' where "dummy" like '+inWherClause[1]+ '\norder by\n\t"display" '+asc ;
					
				}
					query = query + finalWhere;
					if(query.indexOf("group by")==-1) query = insertStringAt(query,query.indexOf("order by"),groupByClause);
					
	}else{
		var parsedResult = parseSQL(selectClause);	
		whereClauseO=replacePlaceholders(parsedResult,whereClause);		
		havingClauseO=replacePlaceholders(parsedResult,havingClause);		
		
		query = selectClause + fromClause + whereClauseO + groupByClause + havingClauseO + orderByClause
	}


    if (limit == 0) {
        return query;
    }

    if (offset == 0) {
        return query + " \nlimit " + limit;
    }

    return query + " \nlimit " + limit + " offset " + offset;
}


