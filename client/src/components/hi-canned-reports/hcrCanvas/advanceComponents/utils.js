import { isEmpty } from "lodash";
import { hcrContextMenuTypes, hcrDSQuery, hcrTableBandsLabels, hcrTableBandsTypes } from "../../hcr-constants";
import { checkIfBandIsDeleted } from "../../hcrHelperMethods";
import { getAvailableBands, isGroupBand, makeCellId } from "../hcrCanvasPaneHelperMethods";
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

const getHCRCrosstabContextMenu = (data = {}) => {
    const { menuType, copiedNodes = [] } = data || {}

    const undoRedoItems = [
        { key: "undo", label: "Undo" },
        { key: "redo", label: "Redo", className: !copiedNodes?.length ? "" : "group-end" },
    ]

    switch (menuType) {
        case hcrContextMenuTypes.CELL:
            return [
                ...undoRedoItems,
                ...(copiedNodes?.length ? [{ key: "paste_node", label: "Paste" }] : [])
            ]
            break;
        case hcrContextMenuTypes.NODE:
            return [
                { key: "cut_node", label: "Cut" },
                { key: "copy_node", label: "Copy" },
                { key: "delete_node", label: "Delete", className: "group-end" },
                ...undoRedoItems,
            ]
            break;
        default:
            break;
    }

}

const getOutlineDSContextMenu = (data = {}) => {
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

const getDatasetOutlineData = (subDataSetOptions = [], componentData = {}, selectedSubDataSet = {}) => {
    let parameters = subDataSetOptions.find((item) => item.value === "parameters"),
        variables = subDataSetOptions.find((item) => item.value === "variables"),
        calculations = subDataSetOptions.find((item) => item.value === "calculations"),
        fields = subDataSetOptions.find((item) => item.value === "fields");

    let groups = selectedSubDataSet?.groups || []

    const { selectedFields = [] } = componentData;

    function getItem(item) {
        return {
            ...item,
            title: item.label,
            key: item.key ? item.key : item.value + item.label,
            className: "ant-tree-title-node-title",
            selectable: item.selectable || false,
            draggable: true,
            componentData
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
                componentData
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
            componentData
        },
        {
            title: "Fields",
            key: "fields",
            children: fields,
            selectable: false,
            dsContextMenu: true,
            menuType: "fields",
            componentData,
        },
        {
            title: "Variables",
            key: "variables",
            children: variables,
            selectable: false,
            dsContextMenu: false,
            menuType: "variables",
            componentData
        },
        {
            title: "Calculations",
            key: "calculations",
            children: calculations,
            selectable: false,
            dsContextMenu: true,
            menuType: "calculations",
            componentData
        },
        {
            title: "Groups",
            key: "groups",
            children: groups,
            selectable: false,
            dsContextMenu: true,
            menuType: "groups",
            componentData
        },
    ]
}

export const getStylesOutline = (styles, componentData) => {
    return styles.map((style) => {
        return {
            title: style.styleName,
            key: style.id,
            children: [],
            selectable: true,
            selectKey: "table-style-item",
            dsContextMenu: true,
            menuType: "table-style-item",
            styleId: style.id,
            componentData
        }
    })
}

export const getCrosstabOutlineData = (crosstab = {}) => {
    const { config = {} } = crosstab || {}
    const {
        columnGroups = [],
        rowGroups = [],
        measures = [],
        measureCells = [],
        nodes = {}
    } = config || {}

    function getItem(item, selectable = false, selectKey = "") {
        return {
            title: item.label,
            key: item.id,
            id: item.id,
            selectable: selectable ? true : false,
            selectKey,
            currentData: item,
        }
    }

    function getGroupsChildren(groups) {
        return groups.map((rg) => {
            return {
                ...getItem(rg, true, "crosstab-group-item"),
                children: rg.cells?.map((cell) => {
                    return {
                        ...getItem(cell, true, "cell"),
                        children: cell.nodeIds?.map((nodeId) => {
                            const node = nodes[nodeId]
                            return {
                                ...getItem(node, true, "node"),
                                children: []
                            }
                        })
                    }
                })
            }
        })
    }

    function getMeasureCells() {
        return measureCells.map((item) => {
            return {
                ...getItem(item, true, "cell"),
                children: item.nodeIds?.map((nodeId) => {
                    const node = nodes[nodeId]
                    return {
                        ...getItem(node, true, "node"),
                        children: []
                    }
                })
            }
        })
    }

    return [
        {
            title: "Row Groups",
            key: "row_groups",
            children: getGroupsChildren(rowGroups),
            selectable: false
        },
        {
            title: "Column Groups",
            key: "column_groups",
            children: getGroupsChildren(columnGroups),
            selectable: false
        },
        {
            title: "Measures",
            key: "measures",
            children: measures.map((m) => getItem(m, true, "crosstab-measure-item")),
            selectable: false
        },
        ...getMeasureCells(),
    ]
}

const getHcrTableOutlineData = (selectedNode = {}, subDataSetOptions, selectedSubDataSet = {}, name, tableStyles = []) => {
    const { id: selectedNodeId, category } = selectedNode;
    const isTable = category === "advancedTable",
        isCrosstab = category === "crosstabv2";

    const title = isTable ? "Table" : isCrosstab ? "Crosstab" : "";
    const selectedKey = isTable ? "table" : isCrosstab ? "crosstab" : "";
    let selectedNodeData = []
    if (isTable) {
        selectedNodeData = getTableOutlinedata(selectedNode);
    }
    if (isCrosstab) {
        selectedNodeData = []
    }

    return [
        {
            title: "Styles",
            key: "styles",
            children: getStylesOutline(tableStyles, selectedNode),
            selectable: false,
            dsContextMenu: true,
            menuType: "table-styles",
            tooltip: "Apply styles to control the formatting and appearance of table elements such as headers, detail cells, footers, fonts, colors, borders, and alignment."
        },
        {
            title: `Dataset ${name ? "(" + name + ")" : ""}`,
            key: "dataset",
            children: getDatasetOutlineData(subDataSetOptions, selectedNode, selectedSubDataSet),
            selectable: false,
            selectKey: "sub-dataset"
        },
        {
            title: title,
            key: selectedNodeId,
            children: selectedNodeData,
            selectable: true,
            selectKey: selectedKey
        }
    ]
}

const getDsAndStylesOutlineData = ({ data, name, tableStyles, subDataSetOptions, selectedSubDataSet }) => {
    return [
        {
            title: "Styles",
            key: "styles",
            children: getStylesOutline(tableStyles, data),
            selectable: false,
            dsContextMenu: true,
            menuType: "table-styles",
            tooltip: "Apply styles to control the formatting and appearance of table elements such as headers, detail cells, footers, fonts, colors, borders, and alignment."
        },
        {
            title: `Dataset ${name ? "(" + name + ")" : ""}`,
            key: "dataset",
            children: getDatasetOutlineData(subDataSetOptions, data, selectedSubDataSet),
            selectable: false,
            selectKey: "sub-dataset"
        },
    ]
}

const getOutlineTreeData = ({ currentComponentData = {}, subDataSetOptions = [], selectedSubDataSet = {}, name, tableStyles = [] }) => {
    const { id: compId, category } = currentComponentData;
    const isTable = category === "advancedTable",
        isCrosstab = category === "crosstabv2";

    const title = isTable ? "Table" : isCrosstab ? "Crosstab" : "Outline";
    const selectedKey = isTable ? "table" : isCrosstab ? "crosstabv2" : "";
    let selectedNodeData = []
    if (isTable) {
        selectedNodeData = getTableOutlinedata(currentComponentData);
    }
    if (isCrosstab) {
        selectedNodeData = getCrosstabOutlineData(currentComponentData);
    }
    return [
        ...(getDsAndStylesOutlineData({ data: currentComponentData, name, tableStyles, subDataSetOptions, selectedSubDataSet })),
        {
            title: title,
            key: compId,
            children: selectedNodeData,
            selectable: true,
            selectKey: selectedKey
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

const getOutlinePanelTitle = (category) => {
    return {
        advancedTable: "Table Outline",
        crosstabv2: "Crosstab Outline",
    }[category]
}


const getQueryItems = (dsPaneTypes) => {
    return dsPaneTypes
        ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
        ?.menu?.filter(
            (ele) =>
                ele.executeQueryData?.data.length ||
                ele.executeQueryData?.field.length,
        ) || [];
}

const getSelectedKeys = (componentData = {}) => {
    const { category } = componentData || {}
    if (category === "advancedTable") {
        const { selectedCells, selectedNodes, outlineDsSelectedField, selectedTable, selectedCalculation, selectedGroup, selectedParameter, selectedStyle } = componentData || {};
        return selectedCells?.[0] || selectedNodes?.[0] || outlineDsSelectedField || selectedTable || selectedCalculation?.[0] || selectedGroup?.[0] || selectedParameter?.[0] || selectedStyle?.[0];
    }
    if (category === "crosstabv2") {
        const { selectedCells, selectedNodes, outlineDsSelectedField, selectedCalculation, selectedGroup, selectedParameter, selectedStyle, selectedCTGroup = [], selectedCTMeasure = [] } = componentData || {};
        return selectedCells?.[0] || selectedNodes?.[0] || outlineDsSelectedField || selectedCalculation?.[0] || selectedGroup?.[0] || selectedParameter?.[0] || selectedStyle?.[0] || selectedCTGroup?.[0] || selectedCTMeasure?.[0];
    }
    return null;
}

const getCellsFromCrosstab = (crosstab = {}) => {
    const { columnGroups = [], rowGroups = [], measureCells = [] } = crosstab.config || {};
    const cells = [
        ...measureCells,
        ...(columnGroups.flatMap(({ cells = [] }) => cells) || []),
        ...(rowGroups.flatMap(({ cells = [] }) => cells) || []),
    ]
    return cells.reduce((acc, next) => {
        acc[next.id] = next;
        return acc
    }, {});
}

const getCTSelectedGroup = (crosstab = {}, groupId) => {
    const { columnGroups = [], rowGroups = [] } = crosstab.config || {}
    return [...columnGroups, ...rowGroups].find((group) => group.id === groupId) || {}
}

const getMeasureCellsByCellIndex = (measureCells) => {
    return measureCells.reduce((acc, curr) => {
        if (acc[curr.cellIndex]) {
            acc[curr.cellIndex].push(curr);
        } else {
            acc[curr.cellIndex] = [curr];
        }
        return acc;
    }, {})
}


const getCrosstabLayout = (config = {}) => {
    const { columnGroups = [], rowGroups = [], measureCells = [], colWidths = [], rowHeights = [] } = config || {};
    const rowLength = rowGroups.length,
        columnLength = columnGroups.length;

    const totalRowsCols = rowLength + columnLength + 1;
    const columnEnd = columnLength + 1,
        rowEnd = rowLength + 1;

    const cells = [
        { id: "crosstab_header_cell", col: [1, rowEnd], row: [1, columnEnd], widthUpdaters: rowGroups.map((_, i) => i), heightUpdaters: columnGroups.map((_, i) => i) }
    ]

    function getWIndexesWithoutRow() {
        return colWidths.map((_, i) => i > rowLength - 1 ? i : null).filter(Boolean);
    }

    function getWIndexesWithoutColumn() {
        return colWidths.map((_, i) => i < rowLength ? i : null).filter((item) => item !== null);
    }

    function getHIndexesWithoutRow() {
        return rowHeights.map((_, i) => i < columnLength ? i : null).filter((item) => item !== null);
    }

    function getHIndexesWithRowOnly() {
        return rowHeights.map((_, i) => i > columnLength - 1 ? i : null).filter(Boolean);
    }


    columnGroups.forEach((grp, cIndex, arr) => {
        const nextArr = arr.slice(cIndex + 1);

        const [hCell, tCell] = grp.cells || [];
        const widths = getWIndexesWithoutRow();
        const heights = getHIndexesWithoutRow();
        cells.push({
            col: [rowEnd, rowEnd + nextArr.length + 1],
            row: [cIndex + 1, cIndex + 2],
            widthUpdaters: widths.slice(0).filter((_, i, arr) => i < arr.length - 1 - cIndex),
            heightUpdaters: [heights[cIndex]],
            ...hCell
        })
        cells.push({
            col: [rowEnd + nextArr.length + 1, totalRowsCols - cIndex],
            row: [cIndex + 1, columnEnd],
            widthUpdaters: [widths.reverse()[cIndex]],
            heightUpdaters: heights.slice(cIndex),
            ...tCell
        })
    })

    rowGroups.forEach((grp, rIndex, arr) => {
        const nextArr = arr.slice(rIndex + 1);
        const [hCell, tCell] = grp.cells || [];
        const widths = getWIndexesWithoutColumn()
        const heights = getHIndexesWithRowOnly()

        cells.push({
            col: [rIndex + 1, nextArr.length ? rIndex + 2 : rowEnd],
            row: [columnEnd, totalRowsCols - rIndex],
            widthUpdaters: [widths[rIndex]],
            heightUpdaters: heights.slice(0).filter((_, i, arr) => i < arr.length - 1 - rIndex),
            ...hCell
        })
        cells.push({
            col: [rIndex + 1, rowEnd],
            row: [totalRowsCols - rIndex, totalRowsCols - rIndex + 1],
            widthUpdaters: widths.slice(rIndex),
            heightUpdaters: [heights.reverse()[rIndex]],
            ...tCell
        })
    })

    const measureCellsByCategory = Object.values(getMeasureCellsByCellIndex(measureCells));


    measureCellsByCategory.forEach((mCells, rIndex) => {
        const widths = getWIndexesWithoutRow();
        const heights = getHIndexesWithRowOnly()

        mCells.forEach((cell, cIndex) => {
            cells.push({
                col: [rowEnd + cIndex, rowEnd + cIndex + 1],
                row: [columnEnd + rIndex, columnEnd + rIndex + 1],
                widthUpdaters: [widths[cIndex]],
                heightUpdaters: [heights[rIndex]],
                ...cell
            })
        })
    })

    return cells;
}

export {
    getActiveSubDSParameterType,
    getCategoryClassNames,
    getCellsFromCrosstab,
    getDatasetOutlineData,
    getHCRTableContextMenu,
    getHcrTableOutlineData,
    getMappedParameters,
    getOutlineDSContextMenu,
    getOutlinePanelTitle,
    getOutlineTreeData,
    getParentKeys,
    getQueryItems,
    getSelectedKeys,
    getTableCellTextStyles,
    getTableOutlinedata,
    getCTSelectedGroup,
    getHCRCrosstabContextMenu,
    getMeasureCellsByCellIndex,
    getCrosstabLayout
};

