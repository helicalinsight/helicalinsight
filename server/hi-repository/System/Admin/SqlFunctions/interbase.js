function setQueryOffsetLimit(query, offset, limit) {

    if (limit == 0) {
        return query;
    }

    if (offset == 0) {
        return query + " rows " + limit;
    }

    return query + "rows " + offset + " to" + (offset + limit);
}
