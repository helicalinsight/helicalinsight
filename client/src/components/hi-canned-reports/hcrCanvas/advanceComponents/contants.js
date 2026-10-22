
const HCR_NODE_RESIZE_HANDLES = [
    { id: 'nw', cursor: "not-allowed", style: { top: -4, left: -4 }, resizeType: "both" },
    { id: 'n', cursor: "ns-resize", style: { top: -4, left: "50%", transform: "translateX(-50%)" }, resizeType: "height" },
    { id: 'ne', cursor: "nesw-resize", style: { top: -4, right: -4 }, resizeType: "both" },
    { id: 'e', cursor: "ew-resize", style: { top: "50%", right: -4, transform: "translateY(-50%)" }, resizeType: "width" },
    { id: 'se', cursor: "nwse-resize", style: { bottom: -4, right: -4 }, resizeType: "both" },
    { id: 's', cursor: "ns-resize", style: { bottom: -4, left: "50%", transform: "translateX(-50%)" }, resizeType: "height" },
    { id: 'sw', cursor: "nesw-resize", style: { bottom: -4, left: -4 }, resizeType: "both" },
    { id: 'w', cursor: "ew-resize", style: { top: "50%", left: -4, transform: "translateY(-50%)" }, resizeType: "width" },
]

const HCR_TABLE_CELL_PROPERTIES = ["printWhenExp"]

const NUMERIC_CLASSNAMES = ["Double", "Float", "Integer", "Long", "Short", "Big Decimal"],
    STRING_CLASSNAMES = ["Time", "Sql Date", "Util Date", "Timestamp", "String", "Boolean"],
    COLLECTION_CLASSNAMES = ["Collection"];

const CROSSTAB_VERSION = "c_v2.1";

export {
    HCR_NODE_RESIZE_HANDLES,
    HCR_TABLE_CELL_PROPERTIES,
    NUMERIC_CLASSNAMES,
    STRING_CLASSNAMES,
    COLLECTION_CLASSNAMES,
    CROSSTAB_VERSION
}