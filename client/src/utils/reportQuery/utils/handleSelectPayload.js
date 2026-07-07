console = console || { warn: () => { }, log: () => { } }
const handleSelectPayload = (payload = [], that) => {
    if (!payload || !payload.length) {
        console.warn('Please mention columns')
    }
    let { floatingType,
        hiddenIncludeInResultSet,
        isNormalTable,
        orderByColumn,
        groupByInfo = [],
        aggregateInfo = {},
        orderByInfo = {},
    } = that;

    (payload.map((e) => {
        let column = typeof e.column == 'object' ? e.column.name : e.column;
        if (!column || typeof column != 'string') {
            console.warn('required STRING for column but recived ', typeof column)
            return false
        }
        if (column && typeof column == 'string' && !column.length) {
            console.warn('required STRING for alias but recived ', typeof e.alias)
            return false
        }

        if (!(e?.alias && typeof e.alias == 'string' && e.alias.length)) {
            e.alias = column
            // e.alias = e.column
        }
        if (groupByInfo.length && (groupByInfo.indexOf(column) !== -1 || groupByInfo.indexOf(e.alias) !== -1)) { //for groupBy
            e["groupBy"] = [
                "db.generic.groupBy.group"
            ]
        }
        if (column in aggregateInfo || e.alias in aggregateInfo) {
            let list = []
            if (aggregateInfo[column]) {
                let size = Object.keys(aggregateInfo[column]).length
                let emptyArr = new Array(size).fill('hi')
                emptyArr.forEach((each, i) => {
                    list.push(aggregateInfo[column][i + 1]);
                })
            }
            if (aggregateInfo[e.alias]) {
                let size = Object.keys(aggregateInfo[e.alias]).length
                let emptyArr = new Array(size).fill('hi')
                emptyArr.forEach((each, i) => {
                    list.push(aggregateInfo[e.alias][i + 1])
                })
            }
            // let list = aggregateInfo[e.column] || []
            // if (Array.isArray(aggregateInfo[e.alias]) && aggregateInfo[e.alias].length) {
            //     list = [...list, ...aggregateInfo[e.alias]]
            // }
            if (list.length) {
                e.aggregate = list
            }
        }
        e = {
            ...e, floatingType,
            hiddenIncludeInResultSet,
            // applyBeforeAggregate,
            isNormalTable,
            orderByColumn,
            column: e?.id ? { name: column, id: e.id } : column
        }
        if (orderByInfo[e.alias]) {
            e.orderBy = [orderByInfo[e.alias]]
        }
        if (that.columns === null) that.columns = []
        // let allAliases = _.pluck(that.columns, 'alias')
        // if (allAliases.indexOf(e.alias) == -1) {
        //     that.columns.push(e)
        // }
        that.columns = that.columns.map(eachColumn => {
            if (eachColumn.alias === e.alias) return false
            return eachColumn
        }).filter(Boolean)
        that.columns.push(e)
        // else {
        //     throw new Error('Duplicate alias name found')
        // }
        return e
    }
    )).filter(Boolean)
    // that.columns = [...columns, ...result]
    return that
}

export default handleSelectPayload