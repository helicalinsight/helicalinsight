import { fromJS } from "immutable";
import utils from "../utils";

const preparePayloadForReport = (self) => {
    let columnsState = {}, metadataState = {}
    // , options = {}
    let { columns,
        database,
        limitBy,
        prependTableNameToAlias,
        sample,
        mode,
        dataSource,
        offsetVal,
        // groupBy,
        filtersInfo
    } = self
    if (!columns) return false
    columnsState.columns = fromJS(columns)

    if (!(typeof database == 'string' && database.length > 0)) {
        // throw 'Database is not yet specified'
        // return false
    }
    metadataState.database = database
    if ((typeof limitBy === 'number' && limitBy >= -1)) {
        limitBy = limitBy === -1 ? 'full' : limitBy
    }
    if (!limitBy) {
        limitBy = 1000
    }

    let updatedExpressions = utils.buildFilterExpressions({ data: filtersInfo.filters })
    if (updatedExpressions.customFilterExpression) {
        filtersInfo.customFilterExpression = updatedExpressions.customFilterExpression
    }
    if (updatedExpressions.customHavingExpression) {
        filtersInfo.customHavingExpression = updatedExpressions.customHavingExpression
        // debugger
    }
    if (filtersInfo) {
        filtersInfo.filters = fromJS(filtersInfo.filters)
        if (!utils.checkParanthesis(filtersInfo.customFilterExpression)) {
            filtersInfo.customFilterExpression += ')'
        }
        if (!utils.checkParanthesis(filtersInfo.customHavingExpression)) {
            filtersInfo.customHavingExpression += ')'
        }
    }

    return {
        columnsState,
        metadataState,
        reportState: {
            options: fromJS({
                limitBy,
                sample,
                prependTableNameToAlias
            }),
            mode
        },
        filtersState: filtersInfo,
        dataSource: fromJS(dataSource),
        offsetVal
    }

}
export default preparePayloadForReport