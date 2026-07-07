import handleSelectPayload from "./utils/handleSelectPayload"
import handleSetDataBase from "./utils/handleSetDataBase"
import preparePayloadForReport from "./utils/preparePayloadForReport"
import handleGroupBy from "./utils/handleGroupBy"
import handleWhereCondition from "./utils/handleWhereCondition"
import { checkParanthesis } from "./utils/checkParanthesis"
import { handleApplyDBFunction } from "./utils/handleApplyDBFunction"
import { handleSelectBuilder } from "./utils/handleSelectBuilder"
import { buildFilterExpressions } from "./utils/buildFilterExpressions"
import extractDatabaseFunctions from "./utils/extractDatabaseFunctions"
import { prepareColumns } from "./utils/prepareColumns"
import getFilters from "./utils/getFilters"
import extractColumnFunctions from "./utils/extractColumnFunctions"
import { handleAddRawSelect } from "./utils/handleAddRawSelect"
import { handleRawFnBuilder } from "./utils/handleRawFnBuilder"
import { handleHideColumns } from "./utils/handleHideColumns"
import { updateHiddenColsInFormdata } from './utils/updateHiddenColsInFormdata'
import { handleApplyBeforeAggregate } from './utils/handleApplyBeforeAggregate'
import { updateDatabaseFunctions } from './utils/updateDatabaseFunctions'

const checkIfObject = (obj) => typeof obj == 'object' && !Array.isArray(obj) && obj !== null

const validateDataSource = (ds) => {
    if (checkIfObject(ds)) {
        return 'location' in ds && 'metadataFileName' in ds
    }
    else {
        return false
    }
}



export default {
    checkIfObject,
    validateDataSource,
    handleSelectPayload,
    handleSetDataBase,
    preparePayloadForReport,
    handleGroupBy,
    handleWhereCondition,
    checkParanthesis,
    handleApplyDBFunction,
    handleSelectBuilder,
    buildFilterExpressions,
    extractDatabaseFunctions,
    prepareColumns,
    getFilters,
    extractColumnFunctions,
    handleAddRawSelect,
    handleRawFnBuilder,
    handleHideColumns,
    updateHiddenColsInFormdata,
    handleApplyBeforeAggregate,
    updateDatabaseFunctions
}