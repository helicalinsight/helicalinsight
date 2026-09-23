import { isEmpty } from 'lodash';
import { v4 as uuidv4 } from 'uuid';
import {
    addFieldToCanvas,
    addNewReport,
    applyReportScripts,
    changeEditorContent,
    changeFilterCondition,
    changeFilterValue,
    changeOptions,
    createFilter,
    loadIntialReport,
    loadMetadata,
    loadReportFilters,
    removeFieldFromCanvas,
    setHReportLoading,
    toggleFloating,
    updateAggregations,
    updateCanvasField,
    updateCustomChart,
    updateCustomCondition,
    updateFieldAlias,
    updateFilter,
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
import { resolveHreportSelectedType } from './viz-type-map';

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

    const props = viz_model?.properties || {};
    const geographicRoles = props.geographicRoles || props.geographic_roles || {};
    const isMapMark = String(mark || "").toLowerCase() === "maps";

    const {
        columns: detailedColumns = [],
        functions = {},
        location = "",
        metadataFileName = "",
        filters = [],
        limitBy,
        filterExpression = []
    } = data_model || {};

    const { groupBy = [] } = functions || {};

    const columnToReturn = [],
        rowsToReturn = [],
        filtersToReturn = [],
        fetchAndHideFields = [];

    function getGroupBy(column) {
        return groupBy.find((clm) => clm.column === column.alias)
    }

    function inferGeographicType(name = "") {
        const text = String(name || "").trim().toLowerCase();
        if (!text) return "";
        const fromRoles = geographicRoles[name] || geographicRoles[text];
        if (fromRoles) return String(fromRoles).trim();
        if (/\b(lat|latitude)\b/.test(text) || /(^|_)lat($|_)/.test(text)) return "lat";
        if (/\b(lon|lng|long|longitude)\b/.test(text) || /(^|_)lon(g)?($|_)/.test(text)) return "long";
        if (/\b(city|cities)\b/.test(text)) return "city";
        if (/\b(state|province|states|provinces)\b/.test(text)) return "state";
        if (/\b(country|countries)\b/.test(text)) return "country";
        if (/\bworld\b/.test(text)) return "world";
        return "";
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
        if (item.aggregate && Array.isArray(item?.aggregateList) && item?.aggregateList?.length) {
            column.aggregate = item?.aggregateList
        }

        const groupByFn = getGroupBy(item);
        column.groupBy = groupByFn ? true : false;

        if (item.hidden && item.includeInResultset) {
            column.fetchAndHide = true;
        }
        const geoType = String(item.geographicType || item.geographic_type || "").trim();
        if (geoType) {
            column.geographicType = geoType;
        } else if (isMapMark) {
            const inferred = inferGeographicType(item.alias || item?.column?.name || "");
            if (inferred) {
                column.geographicType = inferred;
            }
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
        limitBy,
        filterExpression
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
        limitBy = null,
        filterExpression = [];

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
        filterExpression = reportData.filterExpression;
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
            ...fieldToAdd,
            ...(field.geographicType ? { geographicType: field.geographicType } : {}),
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
            dispatch(updateAggregations({ id: fieldId, key: field.aggregate, group: "aggregate" }))
            // InstantBI applies aggregate after addFieldToCanvas; clear the default
            // discrete floatingType so COUNT/SUM measures behave as continuous.
            dispatch(toggleFloating({ id: fieldId, reportId, floatingType: "" }))
        }
        if (!field.aggregate) {
            dispatch(updateAggregations({ id: fieldId, key: [], group: "aggregate" }))
        }
        if (field.fetchAndHide) {
            dispatch(updateCanvasField({ id: fieldId, key: "hiddenIncludeInResultSet" }));
        }
        if (field.groupBy) {
            dispatch(updateAggregations({ id: fieldId, key: ['db.generic.groupBy.group'], group: "groupBy" }))
        }
        if (!field.groupBy) {
            dispatch(updateAggregations({ id: fieldId, key: [], group: "groupBy" }))
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

                if (filterField.customCondition) {
                    dispatch(updateCustomCondition({ uid: filterId, customCondition: filterField.customCondition }))
                }

                if (filterField.values) {
                    updateFilterValues(filterId, filterField.values)
                }

                if (filterField.alias) {
                    dispatch(updateFilterAlias({ uid: filterId, alias: filterField.alias, reportId }))
                }

                // Keep InstantBI Adhoc flags so SQL expression bounds are not quoted.
                const createdFilter = getFilterById(filterId);
                if (createdFilter) {
                    if (filterField.isCustomValue != null || filterField.encloseInQuotes != null) {
                        dispatch(updateFilter({
                            ...createdFilter,
                            ...(filterField.isCustomValue != null
                                ? { isCustomValue: filterField.isCustomValue }
                                : {}),
                            ...(filterField.encloseInQuotes != null
                                ? { encloseInQuotes: filterField.encloseInQuotes }
                                : {}),
                            uid: filterId,
                            reportId,
                        }));
                    }
                    eventUpdater({
                        hreportId: reportId,
                        event: "add_filter",
                        data: getFilterById(filterId) || createdFilter,
                    });
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
        const selectedType = resolveHreportSelectedType(vizType);
        if (selectedType) {
            dispatch(updateSelectedType({ selectedType }))
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
        let selectedType = resolveHreportSelectedType(markType) || markType,
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

    function updateFilterExpression(filterExpression = []) {
        if (Array.isArray(filterExpression) && filterExpression.length) {
            dispatch(changeEditorContent({ id: "pre-fetch", value: `setFilterExpression(${filterExpression.map(item => `"${item}"`).join(', ')})` }))
            dispatch(applyReportScripts())
        }
        return;
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
        updateFilterExpression(filterExpression);
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