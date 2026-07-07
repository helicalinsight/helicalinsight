
import _ from "lodash";
import utils from "./utils";

const generateReportFormData = ({
    flux = {}, payload, raw = false
}) => {
    let { Report, metadata, selectedColumns, visualisations, Marks, filters: Filters } = flux
    let analytics = raw ? Marks.store.getState().analytics : null;
    let { options, mode } = !raw ? payload.reportState : Report.store.getState()
    let { type, subtype } = !raw ? {} : visualisations.store.getState();
    let metadataState = !raw ? payload.metadataState : { database: metadata.store.getState().database }
    let columnsState = !raw ? payload.columnsState : { columns: selectedColumns.store.getState().columns }
    let filtersState = !raw ? payload.filtersState : Filters.store.getState()
    let dataToSend = payload.dataSource.toJS ? payload.dataSource.toJS() : payload.dataSource;
    let { groupByInfo, orderByInfo } = payload?.misc || {}
    dataToSend.columns = utils.prepareColumns(
        columnsState,
        metadataState
    );
    options = options.toJS ? options.toJS() : options

    if (type && type.toUpperCase() === "VF") {
        dataToSend.vf = subtype;
    }
    // Extract functions from the columns
    dataToSend.functions = utils.extractColumnFunctions(columnsState.columns, metadataState.database);

    // See if filters are applied
    if (filtersState && filtersState.filters.size > 0) {
        _.assign(dataToSend, utils.getFilters(filtersState, metadataState, mode, dataToSend.functions));
    }

    _.assign(dataToSend, options);
    if (dataToSend.sample === "full") {
        dataToSend.limitBy = "full";
    }
    if (payload.offsetVal) {
        dataToSend.offset = payload.offsetVal
    }

    delete dataToSend.sample;
    if (dataToSend.filters) {
        _.forEach(dataToSend.filters, (filter) => {
            if (_.isArray(filter.values)) {
                let index = filter.values.indexOf("_Blank_");
                if (filter.values.indexOf("'_Blank_')") > -1) {
                    index = filter.values.indexOf("'_Blank_')");
                    filter.values[index] = filter.values[index].replace("_Blank_", "");
                } else {
                    if (index != -1) {
                        filter.values[index] = "";
                    }
                }

            } else if (typeof filter.values == "string") {
                filter.values = filter.values.replace("_Blank_", "");
            }
        });
    }
    if (dataToSend.having && _.isArray(dataToSend.having)) {
        _.forEach(dataToSend.having, (Obj) => {
            if (((Obj.dataType || '').indexOf("String") != - 1) && (Obj.function.indexOf("count") != - 1) && (Obj.function.indexOf("distinct") == - 1)) {
                Obj.dataType = "java.lang.Integer";
            }
        });
    }

    if (dataToSend.columns && _.isArray(dataToSend.columns)) {
        _.forEach(dataToSend.columns, (column) => {
            if ((column['hidden'] && !column['includeInResultset'])
                && dataToSend.functions && dataToSend.functions.hasOwnProperty('groupBy')) {
                _.remove(dataToSend.functions.groupBy, _.matchesProperty('column', column.alias));
                _.remove(dataToSend.functions.groupBy, _.matchesProperty('alias', column.alias));
            }
        });
        _.forEach(dataToSend.columns, (column) => {
            if ((column['hidden'] && !column['includeInResultset'])
                && dataToSend.functions && dataToSend.functions.hasOwnProperty('orderBy')) {
                _.remove(dataToSend.functions.orderBy, _.matchesProperty('column', column.alias));
                _.remove(dataToSend.functions.orderBy, _.matchesProperty('alias', column.alias));
            }
        });
    }
    //changes for report query -- sorting/groupBy order by based on the way applied -- start 
    if (orderByInfo) {
        let {orderBy} = dataToSend.functions
        let sortedOrder = []
        Object.keys(orderByInfo).map(info => {
            orderBy.map(order => {
                if(order.alias == info){
                    sortedOrder.push(order)
                }
            })
        })
        dataToSend.functions.orderBy = sortedOrder
    }
    if (groupByInfo) {
        let { groupBy } = dataToSend.functions
        let sortedGroupBy = []
        groupByInfo.map(info => {
            groupBy.map(group => {
                if (group.column == info) {
                    sortedGroupBy.push(group)
                }
            })
        })
        dataToSend.functions.groupBy = sortedGroupBy
    }
    //changes for report query -- end

    if (dataToSend.functions && dataToSend.functions.hasOwnProperty('groupBy') && dataToSend.functions.groupBy.length <= 0) {
        delete dataToSend.functions.groupBy;
    }

    if (dataToSend.functions && dataToSend.functions.hasOwnProperty('orderBy') && dataToSend.functions.orderBy.length <= 0) {
        delete dataToSend.functions.orderBy;
    }

    if (dataToSend.functions && dataToSend.functions.hasOwnProperty('subTotal') && dataToSend.functions.subTotal.length <= 0) {
        delete dataToSend.functions.subTotal;
    }
    let Analytics = analytics?.toJS()
    if (Analytics?.some(item => item.value)) {
        dataToSend.analytics = []
    }
    if (dataToSend.analytics) {
        Analytics.map(item => {
            dataToSend.analytics.push({ [item.key]: item.value })
        })
    }
    if (payload._requestId){
        dataToSend.requestId = payload._requestId
    }
    return dataToSend;
}
export default generateReportFormData

window.generateReportFormData = generateReportFormData