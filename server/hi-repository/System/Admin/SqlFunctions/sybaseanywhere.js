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

    return insertFirstAndOffset(query, limit, offset);
}



function insertFirst(query, limit) {
    var index = getIndexOfSelect(query);
    if (index > 0)
        return query.substring(0, index) + " first " + limit + " " + query.substring(index, query.length);
    else
        return query;
};


function insertFirstAndOffset(query, limit, offset) {
    var index = getIndexOfSelect(query);
    if (index > 0)
        return query.substring(0, index) + " top " + limit + " start at " + offset + " " + query.substring(index, query.length);
    else
        return query;
};

function getIndexOfSelect(query) {
    return query.toLowerCase().indexOf('select') + 6;
}
