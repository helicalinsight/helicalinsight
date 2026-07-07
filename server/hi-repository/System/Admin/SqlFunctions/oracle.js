function setQueryOffsetLimit(query, offset, limit, context) {

    var obj = JSON.parse(context);
	
    var filtersArray = obj.formData.filters;
    var actualFilterValuesArray = [];
    var actualFilterValuesArrayWithOutQuotes = [];
    var replaceFilterValuesArray = [];
   // var checkFunction = ["sql.dateTime.extractSecond","sql.dateTime.extractMinute", "sql.dateTime.extractHour", "sql.dateTime.extractDay", "sql.dateTime.extractYear", "sql.text.dateTimeToString", "sql.date.monthsBetween", "sql.dateTime.toChar", "sql.dateTime.extract", "sql.text.dateToString", "sql.text.numericToString", "sql.dateTime.systimestamp", "sql.text.timeToString", "sql.dateTime.extractMonth"];
    var functionName;
	var requiredToCast;

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
				requiredToCast =(outerFunctionReturnType ==='dateTime'||outerFunctionReturnType ==='date');
            }else{
				
				
			if(filterDataType==='java.lang.String'){
					var isValueDate=false;
					if(valuesArray){
						if(valuesArray.length>0){
						var dateComponent=valuesArray[0].split(" ");
							isValueDate=isDate(dateComponent[0])
						}
					}
					
					if(isValueDate){
						outerFunctionReturnType='date' 
						if(dateComponent.length>1)outerFunctionReturnType='dateTime';
						requiredToCast =true;
					}
				
				}
				
				
				
				if(filterDataType==='java.sql.Timestamp'){
					outerFunctionReturnType='dateTime';
				requiredToCast=true;
				}else if(filterDataType==='java.sql.Date'){
					outerFunctionReturnType='date';
					requiredToCast =true;
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

				if(type!=undefined && requiredToCast){
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
	
    var formatedQuery;
	
    if (limit == 0) {
        formatedQuery = query;
        if (replaceFilterValuesArray && replaceFilterValuesArray.length > 0) {
            if (formatedQuery.includes(actualFilterValuesArray[0])) {
                formatedQuery = replaceOriginal(actualFilterValuesArray, replaceFilterValuesArray, formatedQuery)
            } else {
                formatedQuery = replaceOriginal(actualFilterValuesArrayWithOutQuotes, replaceFilterValuesArray, formatedQuery)
            }
        }
		
		formatedQuery = formatedQuery.replace(/TIMESTAMP '_all_'/g, "'_all_'");
        return formatedQuery;
    }

    if (offset == 0) {
        formatedQuery = "select * from ( " + query + " ) table_ where rownum <= " + limit + " order by rownum";
        if (replaceFilterValuesArray && replaceFilterValuesArray.length > 0) {
            if (formatedQuery.includes(actualFilterValuesArray[0])) {
                formatedQuery = replaceOriginal(actualFilterValuesArray, replaceFilterValuesArray, formatedQuery)
            } else {
                formatedQuery = replaceOriginal(actualFilterValuesArrayWithOutQuotes, replaceFilterValuesArray, formatedQuery)
            }
        }
		formatedQuery = formatedQuery.replace(/TIMESTAMP '_all_'/g, "'_all_'");
        return formatedQuery ;
    }

    formatedQuery = "select outer.*  from (select rownum rn, inner.* from ( " + query + " ) inner) outer where outer.rn > " + offset + " and outer.rn <= " + (parseInt(offset) + parseInt(limit));


    if (replaceFilterValuesArray && replaceFilterValuesArray.length > 0) {
        if (formatedQuery.includes(actualFilterValuesArray[0])) {
            formatedQuery = replaceOriginal(actualFilterValuesArray, replaceFilterValuesArray, formatedQuery)
        } else {
            formatedQuery = replaceOriginal(actualFilterValuesArrayWithOutQuotes, replaceFilterValuesArray, formatedQuery)
        }
    }

	formatedQuery = formatedQuery.replace(/TIMESTAMP '_all_'/g, "'_all_'");
    return formatedQuery ;
}



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
var contains = function(needle) {

    var findNaN = needle !== needle;
    var indexOf;

    if (!findNaN && typeof Array.prototype.indexOf === 'function') {
        indexOf = Array.prototype.indexOf;
    } else {
        indexOf = function(needle) {
            var i = -1,
                index = -1;

            for (i = 0; i < this.length; i++) {
                var item = this[i];

                if ((findNaN && item !== item) || item === needle) {
                    index = i;
                    break;
                }
            }

            return index;
        };
    }

    return indexOf.call(this, needle) > -1;
};
String.prototype.replaceAll = function(search, replacement) {
    var target = this;
    return target.split(search).join(replacement);
};



var isDate = function(date) {
    return (new Date(date) !== "Invalid Date") && !isNaN(new Date(date));
}






