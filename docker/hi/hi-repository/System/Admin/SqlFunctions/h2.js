function setQueryOffsetLimit(query, offset, limit) {

    if (query == "undefined" || query == "") {
        return query;
    }

    if (limit == 0) {
        return query;
    }


    if (offset == 0) {
        return query + " limit " + limit;
    }

    return query + " limit " + limit + " offset " + offset;
}
