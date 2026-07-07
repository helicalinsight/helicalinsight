function setQueryOffsetLimit(query, offset, limit) {

    if (query == "undefined" || query == "") {
        return query;
    }

    if (limit == 0) {
        return query;
    }


    if (offset == 0) {
        return insertLimit(query, limit);
    }

    return limitAndOffset(query, limit, offset);
}



function insertLimit(query, limit) {
    var index = getIndexOfSelect(query);
    if (index > 0)
        return query.substring(0, index) + " top " + limit + " " + query.substring(index, query.length);
    else
        return query;
};


function limitAndOffset(query, limit, offset) {
    var index = getIndexOfSelect(query);
    if (index > 0)
        return query.substring(0, index) + " limit " + limit + " " + offset + " " + query.substring(index, query.length);
    else
        return query;
};

function getIndexOfSelect(query) {
    return query.toLowerCase().indexOf('select') + 6;
}
