import { isEmpty } from "lodash";
import { hcrContextMenuTypes, hcrTableBandOrder, hcrTableBandsLabels, hcrTableBandsTypes } from "../../hcr-constants";
import { getAvailableBands, isGroupBand, makeCellId } from "../hcrCanvasPaneHelperMethods";
import { checkIfBandIsDeleted, getCalcParamsNode } from "../../hcrHelperMethods";
import { COLLECTION_CLASSNAMES, NUMERIC_CLASSNAMES, STRING_CLASSNAMES } from "./contants";

const getTableCellTextStyles = (node) => {
    const canvasDefaultNodeHeight = 25;
    const {
        id,
        label,
        fill = "#ffffff",
        fontFill = "#000000",
        fontSize = 14,
        verticalAlign = "middle",
        horizontalAlign = "center",
        mode = "Transparent",
        strikeThrough,
        underLine,
        italic,
        bold,
        fontFamily = "Serif",
        rotation = "None",
        width = 100,
        height = canvasDefaultNodeHeight,
        borders = {},
        padding = {},
    } = node || {};

    const defaultBorder = `0px solid #000000`;
    const defaultPadding = 0;

    let alignItems,
        justifyContent,
        textAlign = horizontalAlign;

    if ((textAlign = "justified")) {
        textAlign = "justify";
    }

    if (verticalAlign === "top") {
        alignItems = "flex-start";
    } else if (verticalAlign === "middle") {
        alignItems = "center";
    } else if (verticalAlign === "bottom") {
        alignItems = "flex-end";
    } else if (verticalAlign === "justified") {
        alignItems = "stretch";
    }

    if (horizontalAlign === "left") {
        justifyContent = "flex-start";
    } else if (horizontalAlign === "center") {
        justifyContent = "center";
    } else if (horizontalAlign === "right") {
        justifyContent = "flex-end";
    }

    const styleObj = {
        width,
        height,
        display: "flex",
        backgroundColor:
            mode.toLowerCase() === "transparent" ? "transparent" : fill,
        alignItems,
        justifyContent: justifyContent,
        color: fontFill,
        borderTop: borders.Top
            ? `${borders.Top.stroke}px ${borders.Top.style?.toLowerCase()} ${borders.Top.color
            }`
            : defaultBorder,
        fontSize,
        borderBottom: borders.Bottom
            ? `${borders.Bottom.stroke}px ${borders.Bottom.style?.toLowerCase()} ${borders.Bottom.color
            }`
            : defaultBorder,
        borderLeft: borders.Left
            ? `${borders.Left.stroke}px ${borders.Left.style?.toLowerCase()} ${borders.Left.color
            }`
            : defaultBorder,
        borderRight: borders.Right
            ? `${borders.Right.stroke}px ${borders.Right.style?.toLowerCase()} ${borders.Right.color
            }`
            : defaultBorder,
    };

    if (italic) {
        styleObj.fontStyle = "italic";
    }
    if (bold) {
        styleObj.fontWeight = "bold";
    }
    if (underLine) {
        styleObj.textDecoration = "underline";
    } else if (strikeThrough) {
        styleObj.textDecoration = "line-through";
    }

    let textRotate = 0;
    if (rotation === "Left") {
        textRotate = -90;
    } else if (rotation === "Right") {
        textRotate = 90;
    } else if (rotation === "UpsideDown") {
        textRotate = 180;
    }

    return {
        containerStyles: styleObj,
        textStyles: {
            fontFamily,
            marginTop: padding.Top || defaultPadding,
            marginBottom: padding.Bottom || defaultPadding,
            marginLeft: padding.Left || defaultPadding,
            marginRight: padding.Right || defaultPadding,
            transform: `rotate(${textRotate}deg)`,
            textAlign,
        }
    }
}

const getTableOutlinedata = (tableNode = {}) => {
    if (!tableNode || isEmpty(tableNode)) return []

    const { nodes, cells, columns, columnOrder, bands, id: tableId } = tableNode || {}
    const availableBands = getAvailableBands(bands)

    if (!availableBands.length) return []
    return availableBands.map((bandType) => {
        const isGroup = isGroupBand(bandType);
        const groupFields = bands?.[bandType]?.groupFields || [];

        if (isGroup && groupFields.length) {
            return groupFields.map((groupField) => {
                const isDeleted = checkIfBandIsDeleted(bandType, cells, groupField);
                return {
                    title: hcrTableBandsLabels[bandType] + " - " + groupField,
                    key: `${bandType}-${groupField}`,
                    selectable: false,
                    contextMenu: true,
                    menuType: hcrContextMenuTypes.BAND,
                    bandType: `${bandType}-${groupField}`,
                    tableId,
                    tableData: tableNode,
                    deleted: isDeleted,
                    children: columnOrder.map((colId, index) => {
                        const col = columns[colId]
                        const cellId = makeCellId(colId, bandType, groupField);
                        const cell = cells[cellId];
                        if (!cell) return null;
                        return {
                            title: col.name || `Column ${index + 1}`,
                            key: cellId,
                            bandType,
                            columnId: colId,
                            isNode: false,
                            contextMenu: true,
                            menuType: hcrContextMenuTypes.CELL,
                            tableId,
                            tableData: tableNode,
                            cellId,
                            deleted: cell.deleted || false,
                            selectable: true,
                            selectKey: "cell",
                            children: cell.nodeIds.map((nodeId) => {
                                const node = nodes[nodeId]
                                return {
                                    title: node.label,
                                    key: node.id,
                                    isNode: true,
                                    bandType,
                                    columnId: colId,
                                    cellId,
                                    className: "ant-tree-title-node-title",
                                    contextMenu: true,
                                    menuType: hcrContextMenuTypes.NODE,
                                    tableId,
                                    tableData: tableNode,
                                    nodeId,
                                    selectable: true,
                                    selectKey: "node",
                                }
                            })
                        }
                    }).filter(Boolean)
                }
            })
        }
        const isDeleted = checkIfBandIsDeleted(bandType, cells);
        return {
            title: hcrTableBandsLabels[bandType],
            key: bandType,
            selectable: false,
            contextMenu: true,
            menuType: hcrContextMenuTypes.BAND,
            bandType,
            tableId,
            tableData: tableNode,
            deleted: isDeleted,
            children: columnOrder.map((colId, index) => {
                const col = columns[colId]
                const cellId = makeCellId(colId, bandType)
                const cell = cells[cellId];
                if (!cell) return null;
                return {
                    title: col.name || `Column ${index + 1}`,
                    key: cellId,
                    bandType,
                    columnId: colId,
                    isNode: false,
                    contextMenu: true,
                    menuType: hcrContextMenuTypes.CELL,
                    tableId,
                    tableData: tableNode,
                    cellId,
                    deleted: cell.deleted || false,
                    selectable: true,
                    selectKey: "cell",
                    children: cell.nodeIds.map((nodeId) => {
                        const node = nodes[nodeId]
                        return {
                            title: node.label,
                            key: node.id,
                            isNode: true,
                            bandType,
                            columnId: colId,
                            cellId,
                            className: "ant-tree-title-node-title",
                            contextMenu: true,
                            menuType: hcrContextMenuTypes.NODE,
                            tableId,
                            tableData: tableNode,
                            nodeId,
                            selectable: true,
                            selectKey: "node",
                        }
                    })
                }
            }).filter(Boolean)
        }
    }).flat(1)
}

const getHCRTableContextMenu = (data = {}) => {
    const { menuType, bandType, row_deleted = false, cell_deleted = false, copiedNodes = [] } = data || {}
    const columnItems = [
        { key: "create_col_at_beginning", label: "Create Column At The Beginning" },
        { key: "create_col_at_end", label: "Create Column At The End", className: "group-end" },
    ]
    const cellColumnItems = [
        { key: "create_col_after", label: "Create Column After" },
        { key: "create_col_before", label: "Create Column Before", },
    ]
    const selectItems = [
        { key: "select_all_cells", label: "Select All Cells" },
        { key: "select_all_nodes", label: "Select All Elements", className: "group-end" },
    ]
    const undoRedoItems = [
        { key: "undo", label: "Undo" },
        { key: "redo", label: "Redo", className: !copiedNodes?.length ? "" : "group-end" },
    ]

    switch (menuType) {
        case hcrContextMenuTypes.BAND:
            return [
                ...columnItems,
                (bandType !== hcrTableBandsTypes.COLUMN_DATA && { key: !row_deleted ? "delete_row" : "create_row", label: !row_deleted ? "Delete Row" : "Create Row" }),
                ...selectItems,
                ...undoRedoItems
            ].filter(Boolean)
            break;
        case hcrContextMenuTypes.CELL:
            return [
                ...cellColumnItems,
                ...columnItems,
                { key: "delete_column", label: "Delete Column" },
                { key: !cell_deleted ? "delete_cell" : "create_cell", label: !cell_deleted ? "Delete Cell" : "Create Cell" },
                ...selectItems,
                ...undoRedoItems,
                ...(copiedNodes?.length && !cell_deleted ? [{ key: "paste_node", label: "Paste" }] : [])
            ]
            break;
        case hcrContextMenuTypes.NODE:
            return [
                { key: "cut_node", label: "Cut" },
                { key: "copy_node", label: "Copy" },
                { key: "delete_node", label: "Delete", className: "group-end" },
                ...undoRedoItems
            ]
            break;
        default:
            break;
    }
}

const getHCRTableOutlineDSContextMenu = (data = {}) => {
    const { menuType } = data || {}

    switch (menuType) {
        case "parameters": {
            return [
                { key: "create_parameter", label: "Create Parameter" }
            ];
            break;
        }
        case "parameters-item": {
            return [
                { key: "delete_parameter_item", label: "Delete" }
            ];
            break;
        }
        case "fields": {
            return [
                { key: "create_field", label: "Create Field" }
            ];
            break;
        }
        case "fields-item": {
            return [
                { key: "delete_fields_item", label: "Delete" }
            ]
            break;
        }
        case "variables": {
            return [];
            break;
        }
        case "variables-item": {
            return [];
            break;
        }
        case "calculations": {
            return [
                { key: "create_calculation", label: "Create Calculation" }
            ];
            break;
        }
        case "calculations-item": {
            return [
                { key: "delete_calculation_item", label: "Delete" }
            ];
            break;
        }
        case "groups": {
            return [
                { key: "create_group", label: "Create Group" }
            ];
            break;
        }
        case "groups-item": {
            return [
                { key: "delete_group_item", label: "Delete" }
            ];
            break;
        }
        case "table-styles": {
            return [
                { key: "create_style", label: "Create Style" }
            ];
            break;
        }
        case "table-style-item": {
            return [
                { key: "delete_style_item", label: "Delete" }
            ];
            break;
        }
        default:
            break
    }

}

const getDatasetOutlineData = (subDataSetOptions = [], tableData = {}, selectedSubDataSet = {}) => {
    let parameters = subDataSetOptions.find((item) => item.value === "parameters"),
        variables = subDataSetOptions.find((item) => item.value === "variables"),
        calculations = subDataSetOptions.find((item) => item.value === "calculations"),
        fields = subDataSetOptions.find((item) => item.value === "fields");

    let groups = selectedSubDataSet?.groups || []

    const { selectedFields = [] } = tableData;

    function getItem(item) {
        return {
            ...item,
            title: item.label,
            key: item.key ? item.key : item.value + item.label,
            className: "ant-tree-title-node-title",
            selectable: item.selectable || false,
            draggable: true,
            tableData
        }
    }

    if (fields) {
        const { children = [] } = fields;
        fields = children.map(({ id, label, value, data = {} }) => {
            return getItem({
                label,
                value,
                data,
                dsContextMenu: true,
                menuType: "fields-item",
                selectable: true,
                selectKey: "fields-item",
                isField: true,
                key: id,
                fieldId: id
            })
        })
    }

    if (parameters) {
        const { children = [] } = parameters;
        parameters = children.map((item) => getItem({
            ...item,
            key: item.id,
            parameterId: item.id,
            dsContextMenu: true,
            selectable: true,
            selectKey: "parameters-item",
            menuType: "parameters-item",
        }))
    } else {
        parameters = []
    }

    if (variables) {
        const { children = [] } = variables;
        variables = children.map((item) => getItem({
            ...item,
            dsContextMenu: false,
            menuType: "variables-item"
        }))
    } else {
        variables = []
    }

    if (calculations) {
        const { children = [] } = calculations;
        calculations = children.map((item) => getItem({
            ...item,
            dsContextMenu: true,
            menuType: "calculations-item",
            key: item.id,
            selectable: true,
            selectKey: "calculations-item",
            calculationId: item.id
        }))
    } else {
        calculations = []
    }

    if (groups.length) {
        groups = groups.map((grp = {}) => {
            const { id, name } = grp || {}
            return {
                id,
                title: name,
                key: id,
                className: "ant-tree-title-node-title",
                selectable: true,
                selectKey: "groups-item",
                dsContextMenu: true,
                menuType: "groups-item",
                groupId: id,
                tableData
            }
        })
    }

    return [
        {
            title: "Parameters",
            key: "parameters",
            children: parameters,
            selectable: false,
            dsContextMenu: true,
            menuType: "parameters",
            tableData
        },
        {
            title: "Fields",
            key: "fields",
            children: fields,
            selectable: false,
            dsContextMenu: true,
            menuType: "fields",
            tableData,
        },
        {
            title: "Variables",
            key: "variables",
            children: variables,
            selectable: false,
            dsContextMenu: false,
            menuType: "variables",
            tableData
        },
        {
            title: "Calculations",
            key: "calculations",
            children: calculations,
            selectable: false,
            dsContextMenu: true,
            menuType: "calculations",
            tableData
        },
        {
            title: "Groups",
            key: "groups",
            children: groups,
            selectable: false,
            dsContextMenu: true,
            menuType: "groups",
            tableData
        },
    ]
}

export const getStylesOutline = (tableStyles, tableData) => {
    return tableStyles.map((style) => {
        return {
            title: style.styleName,
            key: style.id,
            children: [],
            selectable: true,
            selectKey: "table-style-item",
            dsContextMenu: true,
            menuType: "table-style-item",
            styleId: style.id,
            tableData
        }
    })
}

const getHcrTableOutlineData = (tableNode = {}, subDataSetOptions, selectedSubDataSet = {}, name, tableStyles = []) => {
    const { id: tableId } = tableNode;
    const tableData = getTableOutlinedata(tableNode);
    return [
        {
            title: "Styles",
            key: "styles",
            children: getStylesOutline(tableStyles, tableNode),
            selectable: false,
            dsContextMenu: true,
            menuType: "table-styles",
            tooltip: "Apply styles to control the formatting and appearance of table elements such as headers, detail cells, footers, fonts, colors, borders, and alignment."
        },
        {
            title: `Dataset ${name ? "(" + name + ")" : ""}`,
            key: "dataset",
            children: getDatasetOutlineData(subDataSetOptions, tableNode, selectedSubDataSet),
            selectable: false,
            selectKey: "sub-dataset"
        },
        {
            title: "Table",
            key: tableId,
            children: tableData,
            selectable: true,
            selectKey: "table"
        }
    ]
}

const getCategoryClassNames = (classNames = {}) => {
    if (!classNames || isEmpty(classNames)) return [];

    const getOptions = (fields) => {
        return fields.map((field) => ({ label: field.name, value: field.value })) || []
    }

    return [
        { label: "Numeric", value: "numeric", children: getOptions(NUMERIC_CLASSNAMES.map((item) => ({ name: item, value: classNames?.[item] }))) },
        { label: "String", value: "string", children: getOptions(STRING_CLASSNAMES.map((item) => ({ name: item, value: classNames?.[item] }))) },
        { label: "Collection", value: "collection", children: getOptions(COLLECTION_CLASSNAMES.map((item) => ({ name: item, value: "java.util.Collection" }))) }
    ]
}



const getActiveSubDSParameterType = (type) => {
    if (!type) return type;

    const classNamesMap = {
        Double: "java.lang.Double",
        Float: "java.lang.Float",
        Integer: "java.lang.Integer",
        Long: "java.lang.Long",
        Short: "java.lang.Short",
        "Big Decimal": "java.math.BigDecimal",
        Time: "java.sql.Time",
        Boolean: "java.lang.Boolean",
        "Sql Date": "java.sql.Date",
        "Util Date": "java.util.Date",
        Timestamp: "java.sql.Timestamp",
        String: "java.lang.String",
        Collection: "java.util.Collection"
    }

    const categories = [
        { label: "Numeric", value: "numeric", children: NUMERIC_CLASSNAMES.map((item) => classNamesMap[item]), default: "java.lang.Integer" },
        { label: "String", value: "string", children: STRING_CLASSNAMES.map((item) => classNamesMap[item]), default: "java.lang.String" },
        { label: "Collection", value: "collection", children: COLLECTION_CLASSNAMES.map((item) => "java.util.Collection"), default: "java.util.Collection" }
    ]

    return categories.find(({ children }) => children.includes(type))?.default || ""

}

const getMappedParameters = (parameters = []) => {
    if (!parameters.length) return []
    return parameters.map((param) => {
        const { id, mappingExpression, name } = param;
        return {
            parameter: name,
            id,
            expression: mappingExpression
        }
    })
}

const getParentKeys = (data, targetKey) => {
    let result = null;

    function dfs(nodes, ancestors) {
        for (const node of nodes) {
            if (node.key === targetKey) {
                result = ancestors.slice();
                return true;
            }
            if (node.children && node.children.length) {
                if (dfs(node.children, [...ancestors, node.key])) {
                    return true;
                }
            }
        }
        return false;
    }

    dfs(data, []);
    return result;
}


export {
    getHcrTableOutlineData,
    getTableCellTextStyles,
    getTableOutlinedata,
    getDatasetOutlineData,
    getHCRTableContextMenu,
    getHCRTableOutlineDSContextMenu,
    getCategoryClassNames,
    getActiveSubDSParameterType,
    getMappedParameters,
    getParentKeys
};
