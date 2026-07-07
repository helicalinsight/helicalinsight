
import { cloneDeep, isEmpty } from "lodash-es";
import { v4 as uuidv4 } from "uuid";
import types from "../../../../constants/metadata";

import { addFieldToCanvas, addFieldToMarks } from "../../../../redux/actions/hreport.actions";
import { MEASURE_NAME, MEASURE_VALUE } from "../../../../redux/reducers/initialStates";
import notify from "../../../hi-notifications/notify";
import { markTypes } from "../../utils/constants";
import { checkIfCardViz } from "../../../../utils/utilities";
import { applicableVizForColumnsAndRowsMap } from "./constants";
import { getCanvasFieldDataType, getFloatingType } from "../../../../utils/filter-utils";

const handleAdd = (data, dispatch) => {
    let { mark, markType, field } = data
    let { table, column } = field
    if ([MEASURE_NAME, MEASURE_VALUE].includes(field?.column)) {
        let payload = { table, column, addedAs: markType, mark, markType, ...field }
        dispatch(addFieldToCanvas(cloneDeep(payload)))
    } else {
        let payload = { table, column, addedAs: markType, mark, markType }
        dispatch(addFieldToCanvas(cloneDeep(payload)))
    }
    // selectedColumns.actions.addColumn(data);
}
export const dropIntoMarks = (data, dispatch) => {
    let { field, type, markType, mark, fields } = data
    field = { ...field }
    let isExist = false;
    if (type === "canvas_field") {
        let newItem = { markType, addedAs: markType, fieldId: field.id, mark }
        dispatch(addFieldToMarks(newItem));
    } else if (type === "metadata_field") {
        field.markType = markType
        field.addedAs = markType
        field.mark = mark
        handleAdd(data, dispatch)
        // dispatch(addFieldToMarks(item));
    } else {
        field.id = uuidv4();
        field.genre = types.COLUMN;
        field.mark = mark
        field.markType = markType
        field.addedAs = markType
        let alias = ""
        isExist = fields.find(clmn => {
            let { table, column } = field
            let columnName = `${table.name}.${column.name}`
            if (columnName === clmn.column) {
                alias = clmn.autogen_alias ? clmn.autogen_alias : clmn.alias
                return true
            }
        })
        if (!isExist) {
            // config.addColumn(item);
            // setTimeout(() => {
            //     let addedField = columns.toJS().find(field => {
            //         return field.id === item.id
            //     })
            //     if (addedField) {
            //         item.alias = addedField.autogen_alias ? addedField.autogen_alias : addedField.alias
            //     } else {
            //         item.alias = item.column.toJS().alias
            //     }
            //     config.addFieldToMarks(item);
            // }, 100)
            field.addToCanvas = true;
            addFieldToMarks(field);
        } else {
            field.alias = alias
            addFieldToMarks(field);
        }
    }
}
export const dropIntoParams = (data, dispatch) => {
    const Notify = notify(dispatch);
    let { field, filterId, drillThroughId } = data
    let fields = [], filters = [], prependTableNameToAlias = false
    dispatch((dispatch, getState) => {
        let activeReport = getState().hreport.present.reports.find(item => item.active);
        if (activeReport) {
            fields = activeReport.fields
            filters = activeReport.filters
            prependTableNameToAlias = activeReport.prependTableNameToAlias
        }
    });
    field = { ...field }
    field.addedAs = "drillthrough_field"
    let { table, column, columnName } = data.field
    let mainFields = fields.filter(field => field.addedAs !== "drillthrough_field")
    let fullColumnName = `${table.name}.${columnName}`
    // let placesToCheck = ['row', 'column', 'color', 'label', 'shape', 'size', 'tooltip', 'detail']
    let isPresentInFields = mainFields.map(field => field.column).includes(fullColumnName)
    let isPresentInFilters = filters.map(field => field.column).includes(fullColumnName)
    let hidden = true;
    if (isPresentInFields) {
        hidden = false
    }
    // let hiddenIncludeInResultSet = true;
    // if (isPresentInFields) {
    //     hiddenIncludeInResultSet = false
    // }
    if (!isPresentInFields && !isPresentInFilters) {
        return Notify.warning({ message: "Dragged field is not present in fields or filters" });
    }
    let addedAs = field.addedAs
    let payload = { table, column, addedAs, filterId, drillThroughId, hidden }

    dispatch(addFieldToCanvas(cloneDeep(payload)))
}

export const checkMarkFieldDisable = ({ selectedType, subVizType, markType, vfMarks = false, progressChartType = '' }) => {
    if (vfMarks) {
        return false
    }
    if (selectedType === "SyncChart") {
        return true
    }
    if (selectedType === "Table" && ["label", "shape", "tooltip", "detail"].includes(markType)) {
        return true
    }
    if (selectedType === "GridChart") {
        if (markType === "size" && subVizType !== "point") {
            return true
        }
        if (markType === "shape" && subVizType !== "point") {
            return true
        }
        if (markType === "detail") {
            return true
        }
    }
    if (selectedType === "Antcharts" && ["detail"].includes(markType)) {
        return true
    }
    if (selectedType === "Antcharts") {
        if (markType === "size" && (["bar", "line", "area"].includes(subVizType) || subVizType === '')) {
            return true;
        }
        if (["shape"].includes(markType) && !["point", "radar"].includes(subVizType)) {
            return true;
        }
        if (!["label", "tooltip", "color"].includes(markType) && subVizType === "arc") {
            return true;
        }
        if (!["label", "tooltip", "color"].includes(markType) && subVizType === "doughnut") {
            return true;
        }
        if (!["color", "size", "tooltip"].includes(markType) && subVizType === "text") {
            return true;
        }
        if (!["tooltip", "label", "color"].includes(markType) && ['size', 'shape'].includes(markType) && subVizType === "radar") {
            return true;
        }
        if (!["color", "tooltip", "label"].includes(markType) && ['size'].includes(markType) && ["treemap", "waterfall"].includes(subVizType)) {
            return true;
        }
        if (["progress"].includes(subVizType)) {
            switch (progressChartType) {
                case "ring":
                    return true
                case "gauge":
                    return !["color"].includes(markType)
                case "bullet":
                    return !["color", "label"].includes(markType)
                default:
                    return false
            }
        }
        if (!["color", "label", "tooltip"].includes(markType) && ["relation", "rose", "radial bar", 'calendar'].includes(subVizType)) {
            return true;
        }
    }
    if (selectedType === "Ant_Card") {
        if (["progress"].includes(subVizType)) {
            switch (progressChartType) {
                case "ring":
                    return true
                case "gauge":
                    return !["color"].includes(markType)
                case "bullet":
                    return !["color", "label"].includes(markType)
                default:
                    return false
            }
        }
        if (["arc", "doughnut"].includes(subVizType) && ["shape", "size"].includes(markType)) return true
        if (!["arc", "doughnut"].includes(subVizType) && ["label", "shape", "tooltip", "size", "color"].includes(markType)) return true
        if (["arc", "doughnut"].includes(subVizType) && ["detail"].includes(markType)) return true
    }
    if (selectedType === "MapChart") {
        if (["heatmap"].includes(subVizType) && !["size", "label"].includes(markType)) return true;
        if (["point", ""].includes(subVizType) && !["size", "color", "label", "tooltip", "shape"].includes(markType)) return true;
        if (["line"].includes(subVizType) && !["size", "color", "tooltip"].includes(markType)) return true;
    }
    if (selectedType === "S2Chart" && ["label", "shape", "tooltip", "detail"].includes(markType)) {
        return true
    }
    if (selectedType === "Card") {
        if (["label", "shape", "tooltip", "size", "color"].includes(markType)) return true
    }
}

const isFrontendCustomColumn = (field) => {
    return field?.custom_frontend_field || [MEASURE_NAME, MEASURE_VALUE].includes(field?.column);
}

export const dropIntoMeasureNameAndValue = (data, fields, dispatch) => {
    let { table, column, columnName, ...rest } = data.field
    if (isFrontendCustomColumn(data.field)) return null;
    let fullColumnName = `${table.name}.${columnName}`
    let isPresentInFields = fields.map(field => field.column).includes(fullColumnName)
    if (isPresentInFields) return null;
    let addedAs = "measure_field";
    let payload = { table, column, addedAs, ...rest }
    dispatch(addFieldToCanvas(cloneDeep(payload)))
}

export const getActiveMarksOptionForMeasureField = ({ marksList = [], selectedType = '', customChart = {}, fields = [] }) => {
    let { subVizType } = marksList[0];
    let marks = markTypes
    if (["Table", "S2Chart"].includes(selectedType)) {
        marks = ["color", "size"]
    }
    if (selectedType === "GridChart") {
        marks = ["color", "label", "tooltip"]
        if (subVizType === "point") {
            marks = markTypes
        }
        if (subVizType === "arc") {
            marks = ["color", "tooltip", "label"]
        }
    }
    if (selectedType === "Antcharts") {
        if (["arc", "area", "line", "bar", "doughnut"].includes(subVizType) || subVizType === '') {
            marks = ["color", "label", "tooltip"]
        } else if (["point"].includes(subVizType)) {
            marks = ["color", "label", "shape", "size", "tooltip"]
        } else if (["text"].includes(subVizType)) {
            marks = ["color", "size", "tooltip"]
        } else {
            marks = []
        }
    }
    let isCardViz = module !== 'cube' && ["area", "line", "bar", ""].includes(subVizType) && checkIfCardViz(fields, selectedType)
    if (isCardViz) {
        marks = ["detail"]
    }

    if (selectedType === "MapChart") {
        marks = ["color", "label", "size", "tooltip"]
        if (subVizType === "heatmap") {
            marks = ["label", "size"]
        }
        if (subVizType === "line") {
            marks = ["color", "size", "tooltip"]
        }
    }
    if (customChart?.selected && customChart?.applied) {
        marks = ["color", "label", "shape", "size", "tooltip", "detail"]
    }

    return marks;
}

const conditionPassCheck = (conditions = [], fieldsMap = {}) => {
    if (!conditions.length) return true;
    return conditions.some(({ exact = false, ...rest }) => {
        return Object.keys(rest).every((key) => exact ? fieldsMap[key] === rest[key] : fieldsMap[key] >= rest[key]);
    })
}

export const checkIsSubVizApplicable = (type, subVizType, fields) => {
    if (["Table", "S2Chart", "VF"].includes(type)) return true;

    const isHidden = (field) => field.hidden || field.hiddenIncludeInResultSet

    const rows = fields.filter(field => field.addedAs === "row" && !isHidden(field));
    const columns = fields.filter(field => field.addedAs === "column" && !isHidden(field));
    const rowsAndColumns = [...rows, ...columns];

    if (!rowsAndColumns.length) return true;

    const meauresInRows = rows.filter(field => getFloatingType(field).isMeasure).length;
    const dimensionsInRows = rows.filter(field => (!getFloatingType(field).isMeasure || getFloatingType(field).floatingType === "discrete")).length;
    const meauresInColumn = columns.filter(field => getFloatingType(field).isMeasure).length;
    const dimensionsInColumns = columns.filter(field => (!getFloatingType(field).isMeasure || getFloatingType(field).floatingType === "discrete")).length;
    const geoGraphicFields = (rowsAndColumns.filter((field) => field?.geographicType?.length) ?? []).length;
    const dateFields = (rowsAndColumns.filter((field) => ['date', 'dateTime'].includes(getCanvasFieldDataType(field))) ?? []).length;
    const nonDateFields = (rowsAndColumns.filter((field) => !['date', 'dateTime'].includes(getCanvasFieldDataType(field))) ?? []).length;

    const measuresDimensionsMapInRowsColumns = {
        meauresInRows: meauresInRows,
        meauresInColumn: meauresInColumn,
        dimensionsInRows: dimensionsInRows,
        dimensionsInColumns: dimensionsInColumns,
    }

    const totalDimensions = dimensionsInRows + dimensionsInColumns;
    const totalMeasures = meauresInRows + meauresInColumn;

    let applicableSubViz = applicableVizForColumnsAndRowsMap?.[type]?.[subVizType] ?? {};

    if (isEmpty(applicableSubViz)) return false;

    const { measures, dimensions, isCalendar = false, isMap = false, onlyDimension = false, isGridHeatmap = false, requires = [] } = applicableSubViz || {}

    if (isMap) return geoGraphicFields ? true : false;
    if (isCalendar) return (dateFields && nonDateFields === 0) ? true : false;
    if (onlyDimension) return (totalDimensions === dimensions && totalMeasures === 0);

    return (totalDimensions >= dimensions) && (totalMeasures >= measures) && conditionPassCheck(requires, measuresDimensionsMapInRowsColumns);

}