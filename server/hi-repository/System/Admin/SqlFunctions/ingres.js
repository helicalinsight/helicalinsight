function setQueryOffsetLimit(query, offset, limit) {

    if (query == "undefined" || query == "") {
        return query;
    }

    if (limit == 0) {
        return query;
    }


    if (offset == 0) {
        return insertFirst(query, limit);
    }

    return query + " offset " + offset + " fetch first " + limit + " rows only";
}



function insertFirst(query, limit) {
    var index = getIndexOfSelect(query);
    if (index > 0)
        return query.substring(0, index) + " first " + limit + " " + query.substring(index, query.length);
    else
        return query;
};



function getIndexOfSelect(query) {
    return query.toLowerCase().indexOf('select') + 6;
}
