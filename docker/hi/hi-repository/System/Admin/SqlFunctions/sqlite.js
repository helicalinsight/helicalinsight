function setQueryOffsetLimit(query, offset, limit) {

    if (limit == 0) {
        return query;
    }

    if (offset == 0) {
        return query + " \nlimit " + limit;
    }

    return query + " \nlimit " + limit + " offset " + offset;
}
