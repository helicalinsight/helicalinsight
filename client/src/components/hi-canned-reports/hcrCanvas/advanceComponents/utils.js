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
        }
        case "parameters-item": {
            return [
                { key: "delete_parameter_item", label: "Delete" }
            ];
        }
        case "fields": {
            return [
                { key: "create_field", label: "Create Field" }
            ];
        }
        case "fields-item": {
            return [
                { key: "delete_fields_item", label: "Delete" }
            ]
        }
        case "variables": {
            return [];
        }
        case "variables-item": {
            return [];
        }
        case "calculations": {
            return [
                { key: "create_calculation", label: "Create Calculation" }
            ];
        }
        case "calculations-item": {
            return [
                { key: "delete_calculation_item", label: "Delete" }
            ];
        }
        case "groups": {
            return [
                { key: "create_group", label: "Create Group" }
            ];
        }
        case "groups-item": {
            return [
                { key: "delete_group_item", label: "Delete" }
            ];
        }
        case "table-styles": {
            return [
                { key: "create_style", label: "Create Style" }
            ];
        }
        case "table-style-item": {
            return [
                { key: "delete_style_item", label: "Delete" },
                { key: "create_conditional_style", label: "Create Conditional Style" }
            ];
        }
        case "table-conditional-style-item": {
            return [
                { key: "delete_conditional_style_item", label: "Delete" }
            ];
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
            componentData,
            children: style?.conditionalStyles?.map((item) => {
                return {
                    title: item.expression || item.styleName,
                    key: item.id,
                    selectable: true,
                    selectKey: "table-conditional-style-item",
                    dsContextMenu: true,
                    menuType: "table-conditional-style-item",
                    conditionalStyleId: item.id,
                    styleId: style.id,
                }
            }) || []
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
        const { selectedCells, selectedNodes, outlineDsSelectedField, selectedTable, selectedCalculation, selectedGroup, selectedParameter, selectedStyle, selectedConditionalStyle } = componentData || {};
        return selectedCells?.[0] || selectedNodes?.[0] || outlineDsSelectedField || selectedTable || selectedCalculation?.[0] || selectedGroup?.[0] || selectedParameter?.[0] || selectedStyle?.[0] || selectedConditionalStyle?.[0]?.conditionalStyleId;
    }
    if (category === "crosstabv2") {
        const { selectedCells, selectedNodes, outlineDsSelectedField, selectedCalculation, selectedGroup, selectedParameter, selectedStyle, selectedCTGroup = [], selectedCTMeasure = [], selectedConditionalStyle } = componentData || {};
        return selectedCells?.[0] || selectedNodes?.[0] || outlineDsSelectedField || selectedCalculation?.[0] || selectedGroup?.[0] || selectedParameter?.[0] || selectedStyle?.[0] || selectedCTGroup?.[0] || selectedCTMeasure?.[0] || selectedConditionalStyle?.[0]?.conditionalStyleId;
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


const CT_TOTAL_POSITIONS = ["NONE", "START", "END"];

const getCTGroupTotalPosition = (group = {}) => {
    const position = (group?.totalPosition ?? "END").toString().toUpperCase();
    return CT_TOTAL_POSITIONS.includes(position) ? position : "END";
}

const getCTDataOrder = (groups = []) => {
    const items = [{ type: "detail", groupIndex: -1 }];
    for (let index = groups.length - 1; index >= 0; index -= 1) {
        const position = getCTGroupTotalPosition(groups[index]);
        if (position === "NONE") {
            continue;
        }
        const item = { type: "total", groupIndex: index };
        if (position === "START") {
            items.unshift(item);
        } else {
            items.push(item);
        }
    }
    return items;
}

const getCTHeaderItemRange = (items = [], groupIndex = 0) => {
    let start = -1;
    let end = -1;
    items.forEach((item, index) => {
        if (item.type === "detail" || item.groupIndex > groupIndex) {
            if (start === -1) {
                start = index;
            }
            end = index;
        }
    });
    return [Math.max(start, 0), end + 1];
}

const getCTTotalItemIndex = (items = [], groupIndex = 0) => {
    return items.findIndex((item) => item.type === "total" && item.groupIndex === groupIndex);
}

const getCTDataItemIndex = (items = [], groupName = "", groups = []) => {
    if (!groupName) {
        return items.findIndex((item) => item.type === "detail");
    }
    const groupIndex = groups.findIndex((group) => group.name === groupName);
    if (groupIndex === -1) {
        return -1;
    }
    return getCTTotalItemIndex(items, groupIndex);
}

const getCTColumnTrackIndex = (item = {}, columnLength = 0, rowLength = 0) => {
    return item.type === "detail" ? rowLength : rowLength + columnLength - item.groupIndex;
}

const getCTRowTrackIndex = (item = {}, rowLength = 0, columnLength = 0) => {
    return item.type === "detail" ? columnLength : columnLength + rowLength - item.groupIndex;
}

const getCrosstabTrackIndexes = (config = {}) => {
    const { columnGroups = [], rowGroups = [] } = config || {};
    const columnOrder = getCTDataOrder(columnGroups);
    const rowOrder = getCTDataOrder(rowGroups);
    return {
        columnOrder,
        rowOrder,
        colIndexes: [
            ...rowGroups.map((_, index) => index),
            ...columnOrder.map((item) => getCTColumnTrackIndex(item, columnGroups.length, rowGroups.length))
        ],
        rowIndexes: [
            ...columnGroups.map((_, index) => index),
            ...rowOrder.map((item) => getCTRowTrackIndex(item, rowGroups.length, columnGroups.length))
        ]
    };
}

const getCrosstabLayout = (config = {}) => {
    const { columnGroups = [], rowGroups = [], measureCells = [] } = config || {};
    const rowLength = rowGroups.length;
    const columnLength = columnGroups.length;
    const { columnOrder, rowOrder } = getCrosstabTrackIndexes(config);

    const columnEnd = columnLength + 1;
    const rowEnd = rowLength + 1;
    const rowHeaderWidths = rowGroups.map((_, index) => index);
    const columnHeaderHeights = columnGroups.map((_, index) => index);

    const cells = [
        {
            id: "crosstab_header_cell",
            col: [1, rowEnd],
            row: [1, columnEnd],
            widthUpdaters: rowHeaderWidths,
            heightUpdaters: columnHeaderHeights
        }
    ];

    columnGroups.forEach((grp, cIndex) => {
        const [hCell, tCell] = grp.cells || [];
        const [headerStart, headerEnd] = getCTHeaderItemRange(columnOrder, cIndex);
        const totalIndex = getCTTotalItemIndex(columnOrder, cIndex);

        cells.push({
            col: [rowEnd + headerStart, rowEnd + headerEnd],
            row: [cIndex + 1, cIndex + 2],
            widthUpdaters: columnOrder.slice(headerStart, headerEnd).map((item) => getCTColumnTrackIndex(item, columnLength, rowLength)),
            heightUpdaters: [cIndex],
            ...hCell
        });

        if (totalIndex !== -1) {
            cells.push({
                col: [rowEnd + totalIndex, rowEnd + totalIndex + 1],
                row: [cIndex + 1, columnEnd],
                widthUpdaters: [getCTColumnTrackIndex(columnOrder[totalIndex], columnLength, rowLength)],
                heightUpdaters: columnHeaderHeights.slice(cIndex),
                ...tCell
            });
        }
    });

    rowGroups.forEach((grp, rIndex) => {
        const [hCell, tCell] = grp.cells || [];
        const [headerStart, headerEnd] = getCTHeaderItemRange(rowOrder, rIndex);
        const totalIndex = getCTTotalItemIndex(rowOrder, rIndex);

        if (totalIndex !== -1) {
            cells.push({
                col: [rIndex + 1, rowEnd],
                row: [columnEnd + totalIndex, columnEnd + totalIndex + 1],
                widthUpdaters: rowHeaderWidths.slice(rIndex),
                heightUpdaters: [getCTRowTrackIndex(rowOrder[totalIndex], rowLength, columnLength)],
                ...tCell
            });
        }

        cells.push({
            col: [rIndex + 1, rIndex + 2],
            row: [columnEnd + headerStart, columnEnd + headerEnd],
            widthUpdaters: [rIndex],
            heightUpdaters: rowOrder.slice(headerStart, headerEnd).map((item) => getCTRowTrackIndex(item, rowLength, columnLength)),
            ...hCell
        });
    });

    const measureCellsByCategory = Object.values(getMeasureCellsByCellIndex(measureCells));
    const hasColumnGroups = columnLength > 0;
    const hasRowGroups = rowLength > 0;

    measureCellsByCategory.forEach((mCells, categoryIndex) => {
        mCells.forEach((cell, cellIndex) => {
            const columnItemIndex = hasColumnGroups
                ? getCTDataItemIndex(columnOrder, cell.columnTotalGroup, columnGroups)
                : cellIndex;
            const rowItemIndex = hasRowGroups
                ? getCTDataItemIndex(rowOrder, cell.rowTotalGroup, rowGroups)
                : categoryIndex;

            if (columnItemIndex === -1 || rowItemIndex === -1) {
                return;
            }

            const columnItem = columnOrder[columnItemIndex] || columnOrder[0];
            const rowItem = rowOrder[rowItemIndex] || rowOrder[0];

            cells.push({
                col: [rowEnd + columnItemIndex, rowEnd + columnItemIndex + 1],
                row: [columnEnd + rowItemIndex, columnEnd + rowItemIndex + 1],
                widthUpdaters: [getCTColumnTrackIndex(columnItem, columnLength, rowLength)],
                heightUpdaters: [getCTRowTrackIndex(rowItem, rowLength, columnLength)],
                ...cell
            });
        });
    });

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
    getCrosstabLayout,
    getCrosstabTrackIndexes
};

