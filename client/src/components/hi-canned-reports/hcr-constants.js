

export const hcrDSQuery = 'Query';
export const hcrDSParameter = 'Parameter';
//---------------
export const hcrParaInput = 'Input';
export const hcrParaQueryBasedDropdownList = 'Query Based Dropdown List';
export const hcrParaDate = 'Date';
export const hcrParaDateAndTime = 'Date and Time';
export const hcrParaDateRange = 'Date Range';
export const hcrParaDateAndTimeRange = 'Date and Time Range';


export const shortcutKeys = [
    { key: 'ctrl + s', action: 'Save' },
    { key: 'ctrl + shift + s', action: 'Save As' },
    { key: 'ctrl + p', action: 'Preview' },
    { key: 'ctrl + c', action: 'Copy Element' },
    { key: 'ctrl + v', action: 'Paste Element' },
    { key: 'ctrl + shift + q', action: 'Validate Nodes Position' },
    { key: 'ctrl + m', action: 'Toggle Parameters' },
    { key: 'ctrl + LeftArrow', action: 'Prev Page' },
    { key: 'ctrl + RightArrow', action: 'Next Page' },
    { key: 'ctrl + shift + LeftArrow', action: 'First Page' },
    { key: 'ctrl + shift + RightArrow', action: 'Last Page' },
    { key: 'esc', action: 'Close Preview' },
    { key: 'ctrl + z', action: 'undo' },
    { key: 'ctrl + y', action: 'redo' }
]

export const tooltipDescriptions = {
    Page_Number: "Contains the current number of pages in the report at the report time.",
    Column_Number: "Contains the current number of columns.",
    Report_Count: "Contains the number of records processed.",
    Page_Count: "Contains the current number of records processed in the current page.",
    Column_Count: "Contains the current number of records processed during the current column creation.",
    Current_Date: "Displays the current server date.",
    Current_Time: "Displays the current server time.",
    Total_Pages: "Displays the total number of pages."
};

export const hcrTableNodeInitialConfig = {
    addTableHeader: true,
    addTableFooter: true,
    addColumnHeader: true,
    addColumnFooter: true,
    addGroupHeader: true,
    addGroupFooter: true,
    bodyColor: "#ffffff",
    borderStyle: "all",
    bordersColor: "#000000",
    columnHeaderColor: "#bfe1ff",
    headerColor: "#f0f8ff",
}

export const hcrTableBandsTypes = {
    TABLE_HEADER: "tableHeader",
    COLUMN_HEADER: "columnHeaderOfTable",
    GROUP_HEADER: "tableGroupHeaders",
    COLUMN_DATA: "columnData",
    GROUP_FOOTER: "tableGroupFooters",
    COLUMN_FOOTER: "columnFooterOfTable",
    TABLE_FOOTER: "tableFooter",
}

export const hcrTableBandsLabels = {
    [hcrTableBandsTypes.TABLE_HEADER]: "Table Header",
    [hcrTableBandsTypes.COLUMN_HEADER]: "Column Header",
    [hcrTableBandsTypes.GROUP_HEADER]: "Group Header",
    [hcrTableBandsTypes.COLUMN_DATA]: "Column Data",
    [hcrTableBandsTypes.GROUP_FOOTER]: "Group Footer",
    [hcrTableBandsTypes.COLUMN_FOOTER]: "Column Footer",
    [hcrTableBandsTypes.TABLE_FOOTER]: "Table Footer",
}

export const hcrTableBandOrder = [
    hcrTableBandsTypes.TABLE_HEADER,
    hcrTableBandsTypes.COLUMN_HEADER,
    hcrTableBandsTypes.GROUP_HEADER,
    hcrTableBandsTypes.COLUMN_DATA,
    hcrTableBandsTypes.GROUP_FOOTER,
    hcrTableBandsTypes.COLUMN_FOOTER,
    hcrTableBandsTypes.TABLE_FOOTER
]

export const hcrTableStyleNameReferences = {
    tableHeader: "TABLE_TH",
    columnHeader: "TABLE_CH",
    tableData: "TABLE_TD"
}

export const hcrTableDefaultStyles = {
    [hcrTableStyleNameReferences.tableHeader]: {
        name: "TABLE_TH",
        isChanged: false,
        mode: "Opaque",
        backColor: "#F0F8FF",
        foreColor: "#000000",
        lineStyle: {
            penLineWidth: "1"
        },
        border: {
            line: {
                leftLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                rightLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                topLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                bottomLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                }
            }
        }
    },
    [hcrTableStyleNameReferences.columnHeader]: {
        name: "TABLE_CH",
        mode: "Opaque",
        isChanged: false,
        backColor: "#BFE1FF",
        foreColor: "#000000",
        lineStyle: {
            penLineWidth: "1"
        },
        border: {
            line: {
                leftLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                rightLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                topLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                bottomLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                }
            }
        }
    },
    [hcrTableStyleNameReferences.tableData]: {
        name: "TABLE_TD",
        mode: "Opaque",
        isChanged: false,
        backColor: "#FFFFFF",
        foreColor: "#000000",
        lineStyle: {
            penLineWidth: "1"
        },
        border: {
            line: {
                leftLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                rightLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                topLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                },
                bottomLine: {
                    lineWidth: 1,
                    lineColor: "#000103"
                }
            }
        }
    }
}

export const hcrContextMenuTypes = {
    BAND: "band",
    CELL: "cell",
    NODE: "node"
}

const hcrCanvasViews = {
    CANVAS: "canvas",
    TAB: "tab"
}

const HCR_TABLE_NODE = "hcrTableNode";
const HCR_OUTSIDE_NODE = "customNodes";

const TABLE_HEADER = "tableHeader",
    TABLE_FOOTER = "tableFooter",
    COLUMN_HEADER = "columnHeaderOfTable",
    COLUMN_FOOTER = "columnFooterOfTable",
    GROUP_HEADER = "tableGroupHeaders",
    GROUP_FOOTER = "tableGroupFooters",
    COLUMN_DATA = "columnData",

    HCR_TABLE_HEADER_ID = "hcrTable-table-header",
    HCR_TABLE_COLUMN_HEADER_ID = "hcrTable-column-header",
    HCR_TABLE_COLUMN_DATA_ID = "hcrTable-column-data",
    HCR_TABLE_COLUMN_FOOTER_ID = "hcrTable-column-footer",
    HCR_TABLE_FOOTER_ID = "hcrTable-table-footer",
    HCR_TABLE_DATA_CELL_WIDTH = 100,
    HCR_TABLE_DATA_CELL_HEIGHT = 25,
    HCR_CROSSTAB_CELL_WIDTH = 50,
    HCR_CROSSTAB_CELL_HEIGHT = 25,
    HCR_CROSSTAB_ROW_GROUP = "rowGroup",
    HCR_CROSSTAB_COLUMN_GROUP = "columnGroup",
    HCR_CROSSTAB_COLUMN_HEADER = "crosstabColumnHeader",
    HCR_CROSSTAB_COLUMN_TOTAL_HEADER = "crosstabTotalColumnHeader",
    HCR_CROSSTAB_ROW_HEADER = "crosstabRowHeader",
    HCR_CROSSTAB_ROW_TOTAL_HEADER = "crosstabTotalRowHeader",
    HCR_CROSSTAB_ROW_TOTAL_GROUP = "rowTotalGroup",
    HCR_CROSSTAB_COLUMN_TOTAL_GROUP = "columnTotalGroup",
    hcrCrosstabMeasuresAggregateFns = ["Count", "Sum", "Average", "Lowest", "Highest", "StandardDeviation", "Variance", "System", "First", "DistinctCount"],
    hcrChartsPositionOptions = ["Top", "Bottom", "Left", "Right"],
    hcrChartRenderTypeOptions = ["draw", "image", "svg"],
    hcrChartOrientationOptions = ["Horizontal", "Vertical"],
    hcrChartThemeOptions = ["aegean", "default", "default.spring", "eye.candy.sixties", "generic"];

export {
    TABLE_HEADER,
    TABLE_FOOTER,
    COLUMN_HEADER,
    COLUMN_FOOTER,
    GROUP_HEADER,
    GROUP_FOOTER,
    COLUMN_DATA,
    HCR_TABLE_HEADER_ID,
    HCR_TABLE_COLUMN_HEADER_ID,
    HCR_TABLE_COLUMN_DATA_ID,
    HCR_TABLE_COLUMN_FOOTER_ID,
    HCR_TABLE_FOOTER_ID,
    HCR_TABLE_DATA_CELL_WIDTH,
    HCR_TABLE_DATA_CELL_HEIGHT,
    HCR_CROSSTAB_CELL_WIDTH,
    HCR_CROSSTAB_CELL_HEIGHT,
    hcrCrosstabMeasuresAggregateFns,
    HCR_CROSSTAB_ROW_GROUP,
    HCR_CROSSTAB_COLUMN_GROUP,
    HCR_CROSSTAB_ROW_TOTAL_GROUP,
    HCR_CROSSTAB_COLUMN_TOTAL_GROUP,
    HCR_CROSSTAB_COLUMN_HEADER,
    HCR_CROSSTAB_COLUMN_TOTAL_HEADER,
    HCR_CROSSTAB_ROW_HEADER,
    HCR_CROSSTAB_ROW_TOTAL_HEADER,
    hcrChartsPositionOptions,
    hcrChartRenderTypeOptions,
    hcrChartThemeOptions,
    hcrChartOrientationOptions,
    hcrCanvasViews,
    HCR_TABLE_NODE,
    HCR_OUTSIDE_NODE
}

export const hcrImageFallback = "data:image/png;base64,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"