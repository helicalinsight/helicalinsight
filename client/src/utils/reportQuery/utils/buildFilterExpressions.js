
export const buildFilterExpressions = ({ data }) => {
    let having = []
        , filter = []
    data.map(e => {
        if (('aggregate' in e) && e.aggregate) {
            having.push(e)
            return
        }

        filter.push(e)
    }
    )
    having.sort(function (a, b) {
        return a.id > b.id;
    });
    filter.sort(function (a, b) {
        return a.id > b.id;
    });
    let customFilterExpression = ''
        , customHavingExpression = '',
        // currentFilterGroupId = null,
        // currentHavingGroupId = null,
        bracesInfo = {
            filter: { open: false, close: false },
            having: { open: false, close: false }
        },
        openBracket = ' ( ',
        closeBracket = ' ) '
    filter.map((f, i) => {
        if (!customFilterExpression.length) {
            if (f.filterGroupId && !bracesInfo.filter.open) {
                customFilterExpression += openBracket
                bracesInfo.filter.open = true
            }
            if (!f.filterGroupId && bracesInfo.filter.open) {
                customFilterExpression += closeBracket
                bracesInfo.filter.open = false
            }

            customFilterExpression += ' ${' + i + '} '
        } else {
            if (!f.filterGroupId && bracesInfo.filter.open) {
                customFilterExpression += closeBracket
                bracesInfo.filter.open = false
            }
            customFilterExpression += f.operator
            if (f.filterGroupId && !bracesInfo.filter.open) {
                customFilterExpression += openBracket
                bracesInfo.filter.open = true
            }
            customFilterExpression += ' ${' + i + '} '
        }
    }
    )
    // if (!utils.checkParanthesis(customFilterExpression)) {
    //     customFilterExpression += closeBracket
    // }

    having.map((f, i) => {
        // if (f.filterGroupId && !bracesInfo.having.open) {
        //     customHavingExpression += openBracket
        //     bracesInfo.having.open = true

        // }
        // if (!f.filterGroupId && bracesInfo.having.open) {
        //     customHavingExpression += closeBracket
        //     bracesInfo.having.open = false
        // }
        // if (!customHavingExpression.length) {
        //     customHavingExpression += ' ${' + i + '} '
        // } else {
        //     customHavingExpression += f.operator
        //     customHavingExpression += ' ${' + i + '} '

        // }
        if (!customHavingExpression.length) {
            if (f.filterGroupId && !bracesInfo.having.open) {
                customHavingExpression += openBracket
                bracesInfo.having.open = true
            }
            if (!f.filterGroupId && bracesInfo.having.open) {
                customHavingExpression += closeBracket
                bracesInfo.having.open = false
            }

            customHavingExpression += ' ${' + i + '} '
        } else {
            if (!f.filterGroupId && bracesInfo.having.open) {
                customHavingExpression += closeBracket
                bracesInfo.having.open = false
            }
            customHavingExpression += f.operator
            if (f.filterGroupId && !bracesInfo.having.open) {
                customHavingExpression += openBracket
                bracesInfo.having.open = true
            }
            customHavingExpression += ' ${' + i + '} '
        }

    }
    )
    return { customFilterExpression, customHavingExpression }
}