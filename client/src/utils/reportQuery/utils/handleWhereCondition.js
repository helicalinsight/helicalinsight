import utils from "../utils";
// import { object } from "underscore";
const handleWhereCondition = (payload, self, operator = 'AND', config = {}) => {
    let {
        columns,
        filtersInfo: { filters, customFilterExpression, filterGroupId, bracketOpen },
        aggregateInfo
    } = self
    /**
     * handling for below case now
     * .where(column, condition, value)
     */
    if (payload.length === 2 && ['null', 'notnull'].indexOf(payload[1]) !== -1) {
        payload.push([])
    }
    if (payload?.length === 3 || payload?.length === 4) {
        // let col = payload[0],
        let col = typeof payload[0] == "object" ? payload[0]?.name : payload[0],
            condition = payload[1],
            values = payload[2],
            type = payload[3] || 'text';
        condition = condition.toLowerCase();
        switch (condition) {
            case '=':
                condition = 'EQUALS'
                break;
            case '!=':
            case '<>':
                condition = 'NOT_EQUALS'
                break;
            case '>':
                condition = 'IS_GREATER_THAN'
                break;
            case '>=':
                condition = "IS_GREATER_THAN_OR_EQUAL_TO"
                break;
            case '<':
                condition = "IS_LESS_THAN"
                break;
            case '<=':
                condition = "IS_LESS_THAN_OR_EQUAL_TO"
                break;
            case 'inbetween':
                condition = "IS_BETWEEN"
                break;
            case 'notinbetween':
                condition = "IS_NOT_BETWEEN"
                break;
            case 'inrange':
                condition = "IN_RANGE"
                break;
            case 'notinrange':
                condition = "NOT_IN_RANGE"
                break;
            case 'null':
                condition = "IS_NULL"
                break;
            case 'notnull':
                condition = "IS_NOT_NULL"
                break;
            case 'in':
                condition = 'IS_ONE_OF'
                break;
            case 'notin':
                condition = 'IS_NOT_ONE_OF'
                break;
            case 'like':
            case 'contains':
            case 'startswith':
            case 'endswith':
                condition = 'CONTAINS'
                break;
            case 'notlike':
            case 'doesnotcontains':
            case 'doesnotstartswith':
            case 'doesnotendswith':
                condition = 'DOES_NOT_CONTAINS'
                break
            default:
                condition = condition.toUpperCase()
                break;
        }
        let allAliasNames = columns.map(col => col.alias)
        let allColNames = columns.map(col => {
            return col.column
        })

        let newFilterCol = allAliasNames.indexOf(col) !== -1 ? allColNames[allAliasNames.indexOf(col)] : false
        let filterValues = !Array.isArray(values) ? [values] : values
        let newFilter = {
            // column: newFilterCol || col,
            column: (typeof payload[0] == "object" && payload[0]?.id && process.env.NODE_ENV !== "test" ) ? payload[0] :  newFilterCol || col,
            condition,
            values: filterValues,
            "backendDataType": config.dataType === 'boolean' ? 'java.lang.Boolean' : typeof filterValues[0] === 'string' ? "java.lang.String" : 'java.lang.Integer',
            dataType: type || 'string',
            id: filters.length,
            filterGroupId: filterGroupId,
            operator,
            config
        }

        if (!payload[3]) {
            newFilter.dataType = config.dataType ? config.dataType : typeof filterValues[0] === 'string' ? 'text' : 'number'
        }

        let selectedCol = [...self.columns].map(col => {
            if (col.column === newFilterCol) {
                return col
            }
            return false
        }).filter(Boolean)[0]

        if (selectedCol && ('databaseFunction' in selectedCol)) {
            newFilter.databaseFunction = selectedCol.databaseFunction
        }
        if (Array.isArray(config.aggregate) && config.aggregate.length) {
            newFilter.aggregate = config.aggregate
        }
        else if ((col in aggregateInfo) || (newFilterCol in aggregateInfo)) {
            if (aggregateInfo[col]) {
                newFilter.aggregate = Object.values(aggregateInfo[col])
            }
            else {
                newFilter.aggregate = Object.values(aggregateInfo[newFilterCol])
            }
            newFilter.dataType = 'number'
        }

        if (utils.checkIfObject(config)) {
            newFilter = { ...newFilter, ...config }
        }

        filters.push(newFilter)
        let openBracket = ' ( ', closeBracket = ' ) '

        if (customFilterExpression.length <= 1) {
            if (newFilter.filterGroupId && !bracketOpen) {
                customFilterExpression += openBracket
                self.filtersInfo.bracketOpen = true
            }
            customFilterExpression += ' ${' + (newFilter.id) + '} '
        }
        else {
            if (!newFilter.filterGroupId && bracketOpen) {
                customFilterExpression += closeBracket
                self.filtersInfo.bracketOpen = false
            }
            customFilterExpression += operator
            if (newFilter.filterGroupId && !bracketOpen) {
                customFilterExpression += openBracket
                self.filtersInfo.bracketOpen = true
            }

            customFilterExpression += ' ${' + (newFilter.id) + '} '
        }

        self.filtersInfo.filters = filters
    }
    else if (payload?.length === 1 && typeof payload[0] === 'function') {
        self.filtersInfo.filterGroupId = (Math.random() + 1).toString(36).substring(7)
        payload[0](self)
        self.filtersInfo.filterGroupId = null
    }
    return self
}
export default handleWhereCondition
