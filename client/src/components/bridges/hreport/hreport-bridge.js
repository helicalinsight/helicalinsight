import { isEmpty } from 'lodash';
import { v4 as uuidv4 } from 'uuid';
import {
    addFieldToCanvas,
    addNewReport,
    changeFilterCondition,
    changeFilterValue,
    changeOptions,
    createFilter,
    loadIntialReport,
    loadMetadata,
    loadReportFilters,
    removeFieldFromCanvas,
    setHReportLoading,
    updateAggregations,
    updateCanvasField,
    updateCustomChart,
    updateCustomCondition,
    updateFieldAlias,
    updateFilterAlias,
    updateOrderBy,
    updateSelectedType,
    updateSubVizType
} from '../../../redux/actions/hreport.actions';
import { conditions } from '../../hi-reports/hi-editing-area/utils/constants';
import { saveDataBaseFunction } from '../../hi-reports/hi-fields-area/utils/utilities';
import {
    generateReport,
    openMetadata
} from '../../hi-reports/utils/base';
import { getTableTree } from '../../hi-reports/utils/utilities';
import { checkReportsAvailable, getReportById, getUserState } from './utils';

function getHreportInfo(reportModel = {}) {
    const {
        viz = {},
        viz_model = {},
        data_model = {}
    } = reportModel || {};

    const {
        vf_template = ""
    } = viz || {};

    const {
        data = {},
        chart = {},
        // properties = {}
    } = viz_model || {};

    const {
        rows = [],
        columns = []
    } = data || {};

    const {
        viz: vizType = "",
        mark = ""
    } = chart || {};

    const {
        columns: detailedColumns = [],
        functions = {},
        location = "",
        metadataFileName = "",
        filters = [],
        limitBy
    } = data_model || {};

    const { aggregate = [], groupBy = [] } = functions || {};

    const columnToReturn = [],
        rowsToReturn = [],
        filtersToReturn = [],
        fetchAndHideFields = [];

    function getAggregations(column) {
        return aggregate.find((clm) => clm.alias === column.alias)
    }

    function getGroupBy(column) {
        return groupBy.find((clm) => clm.column === column.alias)
    }


    function getFieldToAdd(item) {
        const column = {
            alias: item.alias,
            name: item?.column?.name,
            id: item?.column?.id
        }
        if (item.databaseFunction) {
            column.databaseFunction = item.databaseFunction
        }
        if (item.order) {
            column.order = item.order
        }
        if (item.custom) {
            column.custom = item.custom
            column.column = item.column
        }
        const aggregateFn = getAggregations(item);
        if (aggregateFn) {
            column.aggregate = aggregateFn?.function || null;
        }

        const groupByFn = getGroupBy(item);
        column.groupBy = groupByFn ? true : false;

        if (item.hidden && item.includeInResultset) {
            column.fetchAndHide = true;
        }
        return column;
    }

    function getFieldsOtherThanRowAndColumn() {
        const rowColumnFields = [...rows, ...columns];
        return detailedColumns.filter((dColumn) => !rowColumnFields.includes(dColumn.alias));
    }

    if (columns.length) {
        columns.forEach((column) => {
            const dColumn = detailedColumns.find((dColumn) => dColumn.alias === column);
            if (dColumn) {
                columnToReturn.push(getFieldToAdd(dColumn));
            }
        })
    }

    if (rows.length) {
        rows.forEach((row) => {
            const dRow = detailedColumns.find((dColumn) => dColumn.alias === row);
            if (dRow) {
                rowsToReturn.push(getFieldToAdd(dRow));
            }
        })
    }

    if (filters.length) {
        filters.forEach((filter) => {
            const { column, ...rest } = filter || {}
            filtersToReturn.push({
                ...rest,
                ...(getFieldToAdd(filter) || {}),
            })
        })
    }

    const otherFields = getFieldsOtherThanRowAndColumn();
    if (otherFields.length) {
        otherFields.forEach((field) => {
            if (field.hidden && field.includeInResultset) {
                fetchAndHideFields.push(getFieldToAdd(field))
            }
        })
    }

    if (fetchAndHideFields.length) {
        fetchAndHideFields.forEach((field) => {
            const dColumn = detailedColumns.find((dColumn) => dColumn.alias === field.alias);
            if (dColumn) {
                columnToReturn.push(getFieldToAdd(dColumn));
            }
        })
    }

    return {
        metadataInfo: {
            location,
            metadataFileName,
        },
        columns: columnToReturn,
        rows: rowsToReturn,
        filters: filtersToReturn,
        markType: mark,
        vizType: vizType ? vizType.toLowerCase() : "",
        vfTemplate: atob(vf_template),
        limitBy
    }
}

function createHReportBridge(props = {}) {
    const {
        reportModel = {},
        reportId: hReportId = null,
        reportMetadata = null,
        dispatch,
        onComplete = () => { },
        onError = () => { },
        eventUpdater = () => { },
        interactions = {}
    } = props;

    let reportId = hReportId ? hReportId : uuidv4(),
        metadataInfo = {},
        rows = [],
        columns = [],
        filters = [],
        markType = "",
        vizType = "",
        vfTemplate = "",
        limitBy = null

    function setupReportData(reportModel) {
        const reportData = getHreportInfo(reportModel);
        metadataInfo = reportData.metadataInfo;
        rows = reportData.rows;
        columns = reportData.columns;
        filters = reportData.filters;
        markType = reportData.markType;
        vizType = reportData.vizType;
        vfTemplate = reportData.vfTemplate;
        limitBy = reportData.limitBy;
    }

    function registerReport() {
        const available = checkReportsAvailable(dispatch);
        if (!available) {
            addInitialReport();
        } else {
            addReport();
        }
    }

    function addReport() {
        dispatch(addNewReport({ reportId }));
    }

    function addInitialReport() {
        dispatch(loadIntialReport({ reportId }));
    }

    function isObject(obj) {
        return obj !== null && typeof obj === 'object';
    }

    function isString(str) {
        return typeof str === 'string';
    }

    async function loadCurrentReportMetadata(info = metadataInfo) {
        const { location = '', metadataFileName = '' } = info;
        const formData = { location: location, metadataFileName };
        const metadata = await openMetadata(formData, dispatch);

        if (metadata?.error) {
            onError({ error: metadata.error })
            throw new Error(metadata.error?.message || 'Failed to load hreport metadata');
        }

        return metadata
    }

    function getMetadataTables(metadata) {
        return getTableTree(metadata)?.tables || [];
    }

    function getSimplifiedColumns(tables = []) {
        return tables.flatMap((table) => table.children || [])
    }

    function getColumnByAlias(columns, alias) {
        return columns.find((column) => column.alias === alias);
    }

    function getColumnById(columns, id) {
        return columns.find(({ column }) => column.id === id);
    }

    function getColumnFromMetadataTables(tables, field) {
        if (field.custom) {
            return {
                alias: field.alias,
                column: field.column,
                genre: "custom-formula"
            }
        }
        let columnToAdd = null;
        const flattenColumns = getSimplifiedColumns(tables);
        if (isObject(field)) {
            if (field?.id) {
                columnToAdd = getColumnById(flattenColumns, field.id)
            }
            if (!columnToAdd) {
                if (field?.name) {
                    const cAlias = field.name.split('.')?.pop();
                    columnToAdd = getColumnByAlias(flattenColumns, cAlias);
                }
            }
        }
        if (!columnToAdd) {
            columnToAdd = getColumnByAlias(flattenColumns, field) || {};
        }
        return columnToAdd;
    }

    function addFieldToReport(field, fieldId, fieldToAdd, addedAs = "column") {
        dispatch(addFieldToCanvas({
            addedAs,
            id: fieldId,
            ...fieldToAdd
        }))
        if (field.order) {
            dispatch(updateOrderBy({ id: fieldId, key: field.order }))
        }
        if (!field.custom) {
            if (field.databaseFunction && isString(field.databaseFunction)) {
                addDBFuncToField(fieldId, field.databaseFunction)
            }
            if (field.alias) {
                dispatch(updateFieldAlias({ id: fieldId, alias: field.alias }))
            }
        }
        if (field.aggregate) {
            dispatch(updateAggregations({ id: fieldId, key: [field.aggregate], group: "aggregate" }))
        }
        if (field.fetchAndHide) {
            dispatch(updateCanvasField({ id: fieldId, key: "hiddenIncludeInResultSet" }));
        }
        if (!field.groupBy) {
            const currentField = getFieldById(fieldId) || {};
            const { floatingType = "" } = currentField || {};
            if (floatingType === "discrete") {
                dispatch(updateAggregations({ id: fieldId, key: [], group: "groupBy" }))
            }
        }
    }

    function removeFieldFromReport(fieldId) {
        const field = getFieldById(fieldId);
        dispatch(removeFieldFromCanvas({ field }))
    }

    function addFieldsToReport(fields, tables, addedAs) {
        fields.forEach((field) => {
            let columnToAdd = getColumnFromMetadataTables(tables, field);
            if (columnToAdd) {
                const newFiledId = uuidv4();
                addFieldToReport(field, newFiledId, columnToAdd, addedAs)
            }
        })
    }

    function addColumns(tables = []) {
        addFieldsToReport(columns, tables, 'column');
    }

    function addRows(tables = []) {
        addFieldsToReport(rows, tables, 'row');
    }

    function getAllFilterConditions() {
        return Object.keys(conditions)
    }

    function getFieldById(id) {
        const currentReport = getCurrentReport();
        const { fields = [] } = currentReport || {};
        return fields.find((field) => field.id === id);
    }

    function addDBFuncToField(fieldId, dbFunc) {
        const field = getFieldById(fieldId);
        if (field) {
            const currentReport = getCurrentReport();
            const { databaseFunctions, fields = [] } = currentReport || {};
            let editingField = { ...field, functionsDefinition: dbFunc };
            saveDataBaseFunction({ databaseFunctions, fields, editingField }, dispatch)
        }
    }

    function updateFilterCondition(filterId, condition) {
        const allConditions = getAllFilterConditions();
        if (allConditions.includes(condition)) {
            dispatch(changeFilterCondition({ uid: filterId, condition }))
        }
    }

    function updateFilterValues(filterId, value) {
        dispatch(changeFilterValue({ value, uid: filterId, reportId }));
    }

    function addFilters(tables = []) {
        const flattenColumns = getSimplifiedColumns(tables);
        filters.forEach((filterField) => {
            const { condition } = filterField || {}
            const filterColumn = getColumnById(flattenColumns, filterField.id)
            const filterId = uuidv4();
            if (filterColumn && !filterField.custom) {
                if (filterField.databaseFunction) {
                    addFieldToReport(filterField, filterId, filterColumn, "column")
                    const field = getFieldById(filterId);
                    dispatch(createFilter({ ...field, uid: filterId, reportId }));
                    removeFieldFromReport(filterId)
                } else {
                    dispatch(createFilter({ ...filterColumn, uid: filterId, columnID: filterField.id, from: "metadata" }))
                }
                updateFilterCondition(filterId, condition)

                if (condition === "CUSTOM" && filterField.customCondition) {
                    dispatch(updateCustomCondition({ uid: filterId, customCondition: filterField.customCondition }))
                }

                if (filterField.values) {
                    updateFilterValues(filterId, filterField.values)
                }

                if (filterField.alias) {
                    dispatch(updateFilterAlias({ uid: filterId, alias: filterField.alias, reportId }))
                }

                const createdFilter = getFilterById(filterId);
                if (createdFilter) {
                    eventUpdater({ hreportId: reportId, event: "add_filter", data: createdFilter });
                }
            }
        })
    }

    function addProperties() { } // to do

    function onReportGenSuccess() {
        onComplete(true)
        lookForLoadingReports()
    }

    function lookForLoadingReports() {
        let time = 0
        const interval = setInterval(() => {
            time += 2
            const activeReport = getCurrentReport();
            if (activeReport) {
                if (activeReport.hreportLoading) {
                    dispatch(setHReportLoading({ reportId, loading: false }))
                }
                clearInterval(interval);
            }
            if (time > 10) {
                clearInterval(interval);
            }
        }, 2000);

    }

    function displayReport() {
        const activeReport = getCurrentReport();
        const user = getUserState(dispatch);
        generateReport({ ...activeReport, user }, dispatch, onReportGenSuccess);
    }

    function getCurrentReport() {
        return getReportById(dispatch, reportId);
    }

    function getFilterById(id) {
        const currentReport = getCurrentReport();
        const { filters = [] } = currentReport || {};
        return filters.find((filter) => filter.uid === id) || null;
    }

    function getReportId() {
        return reportId;
    }

    function updateVFTemplate() {
        if (vfTemplate) {
            dispatch(updateCustomChart({ code: vfTemplate }));
        }
    }

    function changeSelectedVizType(vizType, clear = false) {
        const validChartTypesMap = {
            GridChart: "GridChart",
            Antcharts: "Antcharts",
            Chart: "Antcharts",
            MapChart: "MapChart",
            Table: "Table",
            S2Chart: "S2Chart",
            GridTable: "S2Chart",
            Card: "Card",
            KPI: "Card",
            vf: "VF",
            VF: "VF",
        }
        if (vizType && validChartTypesMap[vizType]) {
            dispatch(updateSelectedType({ selectedType: validChartTypesMap[vizType] }))
        }

        if (!vizType && clear) {
            dispatch(updateSelectedType({ selectedType: "Table" }))
        }
    }

    function changeSubVizType(subVizType) {
        const currentReport = getCurrentReport();
        const allMark = currentReport?.marksList?.find((item) => item?.value === "_all_");
        if (allMark && subVizType) {
            dispatch(updateSubVizType({ value: allMark.value, name: subVizType, id: allMark?.id }))
        }
    }

    function updateVizAndMarks() {
        let selectedType = markType,
            subVizType = vizType;
        if (!isEmpty(interactions)) {
            selectedType = interactions?.selectedType || selectedType;
            subVizType = interactions?.subVizType || subVizType;
        }
        changeSubVizType(subVizType);
        changeSelectedVizType(selectedType);
        eventUpdater({ hreportId: reportId, event: "change_viz", data: { selectedType, subVizType } });
    }

    function updateMetadataForReport(reportMetadata) {
        dispatch(loadMetadata(reportMetadata));
    }

    function updateHreportFilters(filters = []) {
        if (!filters.length) return;

        dispatch(loadReportFilters({ reportId, filters }));
    }

    function updateLimitBy(limitBy) {
        if (limitBy !== null && limitBy !== undefined) {
            dispatch(changeOptions({ sample: "sample", limitBy, prependTableNameToAlias: false }));
        }
    }

    function updateReportVizDetails(metadata) {
        const metadataTables = getMetadataTables(metadata);
        if (columns.length) {
            addColumns(metadataTables);
        }
        if (rows.length) {
            addRows(metadataTables);
        }
        if (isEmpty(interactions)) {
            if (filters.length) {
                addFilters(metadataTables);
            }
        } else {
            updateHreportFilters(interactions.filters);
        }
        updateLimitBy(limitBy);
        updateVFTemplate();
        updateVizAndMarks();
        displayReport();
    }

    async function getMetadataUsingInfo(metadataInfo) {
        if (!isEmpty(metadataInfo)) {
            const loadedMetadata = await loadCurrentReportMetadata(metadataInfo);
            if (loadedMetadata) {
                updateReportVizDetails(loadedMetadata?.metadata);
            } else {
                onError({ error: true })
            }
        }
    }


    async function init() {
        if (!isEmpty(reportModel)) {
            setupReportData(reportModel);
            registerReport();
            if (!isEmpty(reportMetadata)) {
                updateMetadataForReport(reportMetadata);
                const { metadata } = reportMetadata || {};
                updateReportVizDetails(metadata);
            } else {
                await getMetadataUsingInfo(metadataInfo);
            }
        } else {
            onError({ error: true })
        }
    }

    const result = {
        init,
        getReportId
    };

    return result;
}

export default createHReportBridge;