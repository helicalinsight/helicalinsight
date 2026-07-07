function setQueryOffsetLimit(query, offset, limit) {

    if (query == "undefined" || query == "") {
        return query;
    }

    if (limit == 0) {
        return query;
    }


    if (offset == 0) {
        return query + " fetch " + limit + "first rows only";
    }

    return query + " offset " + offset + " rows fetch " + limit + " first rows only";
}
