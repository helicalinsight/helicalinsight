import { Tooltip } from "antd";
import { hcrActions } from "../../../redux/actions";
import {
  COLUMN_DATA,
  COLUMN_FOOTER,
  COLUMN_HEADER,
  HCR_CROSSTAB_CELL_HEIGHT,
  HCR_CROSSTAB_CELL_WIDTH,
  HCR_CROSSTAB_COLUMN_GROUP,
  HCR_CROSSTAB_COLUMN_HEADER,
  HCR_CROSSTAB_COLUMN_TOTAL_GROUP,
  HCR_CROSSTAB_COLUMN_TOTAL_HEADER,
  HCR_CROSSTAB_ROW_GROUP,
  HCR_CROSSTAB_ROW_HEADER,
  HCR_CROSSTAB_ROW_TOTAL_GROUP,
  HCR_CROSSTAB_ROW_TOTAL_HEADER,
  HCR_TABLE_DATA_CELL_HEIGHT,
  HCR_TABLE_DATA_CELL_WIDTH,
  hcrCanvasViews,
  hcrDSQuery,
  hcrTableBandOrder,
  hcrTableBandsTypes,
  hcrTableDefaultStyles,
  hcrTableStyleNameReferences,
  TABLE_FOOTER,
  TABLE_HEADER,
} from "../hcr-constants";
import { checkIfBandIsDeleted, getReqNodeData } from "../hcrHelperMethods";
import { v4 as uuidv4 } from "uuid";
import { cloneDeep, isEmpty } from "lodash";
import { antdChartCategoryColors } from "../../hi-reports/hi-viz-area/ant-charts/ant-utils";
import { ArrowLeftOutlined } from "@ant-design/icons";
import { current } from "immer";
import { HCR_TABLE_CELL_PROPERTIES } from "./advanceComponents/contants";

const {
  handleDiagramPageSize,
  handleDiagramMargin,
  hcrLayout,
  hcrOrientation,
  storeTextFieldContent,
  storeHCRPropertyPaneData,
} = hcrActions;

function pageSizeChange({
  staticPathValue,
  dispatch,
  value,
  propertyPaneData,
  path,
}) {
  let { pixel } = staticPathValue[value];
  let { width, height } = pixel;
  if (propertyPaneData.pageLayoutInfo.orientation === "Landscape") {
    let temp = width;
    width = height;
    height = temp;
  }
  dispatch(handleDiagramPageSize({ width, height }));
  dispatch(storeHCRPropertyPaneData({ path, value }));
}

function pageMarginChange({ key, value, dispatch, path }) {
  dispatch(storeHCRPropertyPaneData({ path, value }));
}

function pageOrientationChange({ dispatch, value, size, path }) {
  let height, width;
  height = size.fullPage.width;
  width = size.fullPage.height;
  dispatch(handleDiagramPageSize({ height, width }));
  dispatch(storeHCRPropertyPaneData({ path, value }));
}

function handleShapePropertyChanges({ value, dispatch, path }) {
  dispatch(storeHCRPropertyPaneData({ path, value }));
}

export const getHcrTableCellsPositionDetails = (data = {}) => {
  const { tableFields = [], tableConfig = {}, tablePosition = {} } = data || {};
  const { addTableHeader, addColumnHeader, addColumnFooter, addTableFooter } =
    tableConfig || {};
  const { x: tableX, y: tableY } = tablePosition;
  const cellWidth = HCR_TABLE_DATA_CELL_WIDTH,
    cellHeight = HCR_TABLE_DATA_CELL_HEIGHT;
  let rows = [];
  if (addTableHeader) rows.push(TABLE_HEADER);
  if (addColumnHeader) rows.push(COLUMN_HEADER);
  rows.push(COLUMN_DATA);
  if (addColumnFooter) rows.push(COLUMN_FOOTER);
  if (addTableFooter) rows.push(TABLE_FOOTER);

  return rows
    .map((row, rowIndex) => {
      return tableFields.map((_, columnIndex) => {
        return {
          cell: row,
          columnIndex,
          cellWidth,
          cellHeight,
          x: columnIndex * cellWidth + tableX,
          y: rowIndex * cellHeight + tableY,
        };
      });
    })
    .flat();
};

const getHCRTableClosestNode = (
  nodeConfig = {},
  dispatch,
  elementsPositions,
) => {
  if (!nodeConfig?.parent) return nodeConfig;

  let hcrDiagramNodesData = [];
  dispatch((_, getState) => {
    const activeTab = getState().cannedReports.present.hcrTabData.panes.find(
      (pane) =>
        pane.key === getState().cannedReports.present.hcrTabData.activeKey,
    );
    hcrDiagramNodesData = activeTab?.hcrDiagramNodesData || [];
  });

  const {
    parent,
    x: currentNodeX,
    y: currentNodeY,
    id: currentNode,
  } = nodeConfig || {};
  const parentNode =
    hcrDiagramNodesData?.find((ele) => ele.id === parent) || {};
  const { x, y, tableConfig } = parentNode || {};

  const tableGrid = getHcrTableCellsPositionDetails({
    tableFields: parentNode.selectedColumnFields,
    tableConfig,
    tablePosition: { x, y },
  });

  for (const cell of tableGrid) {
    const insideX = currentNodeX <= cell.x;
    const insideY = currentNodeY <= cell.y;

    if (insideX && insideY) {
      const { cell: cellType, x, y, columnIndex } = cell || {};
      elementsPositions[currentNode] = { x, y };
      return {
        ...nodeConfig,
        x,
        y,
        cell: cellType,
        columnIndex,
      };
    }
  }
};

const getFlowChartConfig = () => {
  return {
    resizing: {
      minHeight: (node) => {
        const data = node?.store?.data?.data || {};
        if (data.isCrosstabCell || data.isTableCell) {
          return data.height;
        }
        return 1;
      },
      minWidth: (node) => {
        const data = node?.store?.data?.data || {};
        if (data.isCrosstabCell || data.isTableCell) {
          return data.width;
        }
        return 1;
      },
      enabled: (node) => {
        const data = node?.store?.data?.data || {};
        return data?.resize ?? true;
      },
      maxWidth: (node) => {
        const data = node?.store?.data?.data || {};
        if (data.isCrosstabCell || data.isTableCell) {
          return data.width;
        }
      },
      maxHeight: (node) => {
        const data = node?.store?.data?.data || {};
        if (data.isCrosstabCell || data.isTableCell) {
          return data.height;
        }
      },
    },
    grid: {
      size: 10,
      visible: true,
      type: "doubleMesh",
      args: [
        { color: "#e0e0e0", thickness: 1 },
        { color: "#c0c0c0", thickness: 1, factor: 10 },
      ],
    },
    translating: {
      restrict: (cellView) => {
        const { cell = {} } = cellView || {};
        if (cell.isNode()) {
          const { isCrosstabCell = false, isTableCell = false, selectable = false } = cell.data
          if ((isCrosstabCell || isTableCell) && !selectable) {
            return cell.getBBox();
          }
          const parent = cell.getParent();
          if (parent) {
            return parent.getBBox();
          }
        }
        return true;
      },
    },
    // embedding: {
    // 	enabled: true,
    // 	findParent({ node }) {
    // 		const bbox = node.getBBox()
    // 		return this.getNodes().filter((node) => {
    // 			const data = node.getData()
    // 			if (data && data.isGroup) {
    // 				const targetBBox = node.getBBox()
    // 				return bbox.isIntersectWithRect(targetBBox)
    // 			}
    // 			return false
    // 		})
    // 	}
    // },
    panning: { enabled: false },
    transforming: false,
    mousewheel: { enabled: false },
    // selecting: {
    //   enabled: true,
    //   filter: function (cell) {
    //     const { isCrosstabCell = false, isTableCell = false, selectable = false } = cell.data || {};
    //     if ((isCrosstabCell || isTableCell) && !selectable) {
    //       return false;
    //     }
    //     return true;
    //   }
    // },
    preventDefaultDblClick: true,
    preventDefaultMouseDown: true
  };
};

// const handleNodeSelection = (node, dispatch, eType = "dblClick") => {
//   const { isGroup = false, id = "" } = node?.data || {}
//   switch (eType) {
//     case "dblClick":
//       if (isGroup) {
//         dispatch(hcrActions.setHcrNodesSelection({ parentNodeId: id, type: "select" }))
//       } else {
//         dispatch(hcrActions.setHcrNodesSelection({ type: "unselectAll" }))
//       }
//       break;
//     case "click":
//       dispatch(hcrActions.setHcrNodesSelection({ type: "unselectAll" }))
//       break;
//     default:
//       break;
//   }
// }

const handleNodeSelection = (node, dispatch) => {
  let canvasView, tabViews, paneTypes = []
  dispatch((_, getState) => {
    let state = getState()
    const activeTab = state.cannedReports.present.hcrTabData.panes.find(
      (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
    ) || {};
    const {
      canvasView: cView = "canvas",
      canvasTabViews: { views = [] } = {},
      dsPaneTypes = []
    } = activeTab || {};
    canvasView = cView;
    tabViews = views;
    paneTypes = dsPaneTypes;
  })
  const nodeData = getReqNodeData(node?.store?.data?.data || {});

  if (!isEmpty(nodeData)) {
    const id = nodeData.id;
    let viewData = {}
    if (["advancedTable"].includes(nodeData.category)) {
      switch (canvasView) {
        case hcrCanvasViews.CANVAS:
          viewData = {
            id: id, label: "Table", key: id,
          }

          const propertiesToUpdate = {}

          // update available band [8974]
          const { bands = {}, cells = {}, columnOrder = [], selectedGroupFields = [], tableConfig = {} } = nodeData || {};
          const bandConfig = getBandsConfig(tableConfig, selectedGroupFields);
          const bandsToUpdate = {}, cellsToUpdate = {}, columnWidth = HCR_TABLE_DATA_CELL_WIDTH;
          hcrTableBandOrder.forEach((bandType) => {
            if (!bands[bandType]) {
              const newBand = {
                height: HCR_TABLE_DATA_CELL_HEIGHT,
                styles: bandConfig[bandType].styles,
                ...(bandConfig[bandType]?.groupFields ? { groupFields: bandConfig[bandType].groupFields || [] } : {})
              }
              bandsToUpdate[bandType] = newBand;

              columnOrder.forEach((colId) => {
                if (isGroupBand(bandType)) {
                  const groupFields = newBand.groupFields || [];
                  groupFields.forEach((field) => {
                    const cell = createCell(colId, bandType, columnWidth, newBand.height, field);
                    cell.deleted = true;
                    cellsToUpdate[cell.id] = cell;
                  })
                } else {
                  const cell = createCell(colId, bandType, columnWidth, newBand.height);
                  cell.deleted = true;
                  cellsToUpdate[cell.id] = cell;
                }
              })
            }
          })
          if (!isEmpty(bandsToUpdate)) {
            propertiesToUpdate.bands = { ...bands, ...bandsToUpdate }
          }
          if (!isEmpty(cellsToUpdate)) {
            propertiesToUpdate.cells = { ...cells, ...cellsToUpdate }
          }
          // end [8974]

          if (!isEmpty(propertiesToUpdate)) {
            dispatch(hcrActions.editNode({ nodeId: id, multiple: true, propertiesToBeChange: propertiesToUpdate }))
          }

          dispatch(hcrActions.hcrUpdateCanvasView({ view: hcrCanvasViews.TAB, viewData, active: id }))
          break;
        case hcrCanvasViews.TAB:
          const present = tabViews.find(view => view.id === id);
          if (present) {
            dispatch(hcrActions.hcrUpdateCanvasView({ view: "updateActive", active: id }))
            break;
          }
          viewData = {
            id: id, label: "Table", key: id,
          }
          dispatch(hcrActions.hcrUpdateCanvasView({ view: hcrCanvasViews.TAB, viewData, active: id }))
          break;
        default:
          break;
      }
    }

    if (nodeData.category === "crosstab") {
      switch (canvasView) {
        case hcrCanvasViews.CANVAS:
          viewData = {
            id: id, label: "Crosstab", key: id,
          }
          dispatch(hcrActions.hcrUpdateCanvasView({ view: hcrCanvasViews.TAB, viewData, active: id }))
          break;

        case hcrCanvasViews.TAB:
          const present = tabViews.find(view => view.id === id);
          if (present) {
            dispatch(hcrActions.hcrUpdateCanvasView({ view: "updateActive", active: id }))
            break;
          }
          viewData = {
            id: id, label: "Crosstab", key: id,
          }
          dispatch(hcrActions.hcrUpdateCanvasView({ view: hcrCanvasViews.TAB, viewData, active: id }))
          break;
        default:
          break;
      }
    }
  }
}


const onHCRCanvasConfigChange = ({
  config,
  type,
  dispatch,
  flowchartInstance,
  nodesPositions,
}) => {
  if (config) {
    let reqNode = getReqNodeData(config);
    const { width, height } = reqNode || {};
    if (reqNode.y < 0) {
      reqNode.y = 0;
    }
    if (reqNode.x < 0) {
      reqNode.x = 0;
    }
    let elementsPositions = nodesPositions?.current || {};
    switch (type) {
      // case "resize:node":
      // 	dispatch(hcrActions.editNode({ nodeData: { ...reqNode, originSizeeeee: { width, height } }, nodesPositions: elementsPositions }));
      // 	break;
      case "move:node":
        reqNode = getHCRTableClosestNode(config, dispatch, elementsPositions);
        const selectedNodes =
          flowchartInstance?.current?.getSelectedCells()?.map((shape) => {
            const node = getReqNodeData(shape.store.data.data);
            if (node.y < 0) node.y = 0;
            if (node.x < 0) node.x = 0;
            return node;
          }) || [];
        const selectedNode = selectedNodes?.find(
          (selectedNode) => selectedNode?.id === reqNode?.id,
        );
        if (selectedNode) {
          dispatch(
            hcrActions.editNodes({
              positionedNodes: selectedNodes,
              type: "move:nodes",
              nodesPositions: elementsPositions,
            }),
          );
          break;
        }
        if (reqNode?.isGroup) {
          const allNodes = flowchartInstance?.current?.getNodes();
          const childNodes = allNodes?.filter((node) =>
            reqNode?.groupChildren?.includes(node.id),
          );
          dispatch(
            hcrActions.editNodes({
              positionedNodes: [...(childNodes || []), reqNode],
              type: "move:nodes",
              nodesPositions: elementsPositions,
            }),
          );
          break;
        }
        dispatch(
          hcrActions.editNode({
            nodeData: reqNode,
            nodesPositions: elementsPositions,
          }),
        );
        break;
      case "resize:node":
        dispatch(hcrActions.hcrResizeNode(reqNode));
        break;
      default:
        break;
    }
  }
};

const onChildNodeMove = (node) => {
  const parent = node?.parent;
  if (parent) {
    const { originSize = {}, originPosition = {} } = parent?.store?.data || {};
    parent?.setSize(originSize);
    parent?.setPosition(originPosition);
  }
};

const getHcrPropertyTooltipInfo = ({ label, tooltip }) => {
  const tooltipTitle = tooltip?.content?.[0]?.displayContent || "";
  let divTag = document.createElement("div");
  divTag.innerHTML = tooltipTitle;
  return (
    <Tooltip
      title={<div dangerouslySetInnerHTML={{ __html: divTag.innerHTML }} />}
    >
      <div className="property-label">{label}</div>
    </Tooltip>
  );
};

export const getNextItemsLength = (next = []) => {
  if (!next?.length) return 0;
  return next?.flat(Infinity).length;
};

const getColumnHeaders = (columns = []) => {
  if (!columns?.length) return [];
  return columns
    ?.map((column, index, arr) => {
      if (Array.isArray(column)) return null;
      const cellWidth =
        HCR_CROSSTAB_CELL_WIDTH * (getNextItemsLength(arr) || 1);
      const cellHeight = HCR_CROSSTAB_CELL_HEIGHT * getNextItemsLength(arr);
      return {
        key: column,
        emptyCellHeight: cellHeight,
        items: [
          {
            label: "$V",
            value: column,
            width: cellWidth,
            height: HCR_CROSSTAB_CELL_HEIGHT,
            identifier: HCR_CROSSTAB_COLUMN_TOTAL_GROUP,
          },
          {
            label: "Total",
            value: column,
            width: HCR_CROSSTAB_CELL_WIDTH,
            height: cellHeight,
            isTotal: true,
            identifier: HCR_CROSSTAB_COLUMN_TOTAL_GROUP,
          },
        ],
        children: getColumnHeaders(arr[index + 1]),
      };
    })
    .filter(Boolean);
};

const getRowHeaders = (rows = [], measures = []) => {
  if (!rows?.length) return [];
  return rows
    ?.map((row, index, arr) => {
      if (Array.isArray(row)) return null;
      const measuresLength = measures?.length;
      const cellWidth = HCR_CROSSTAB_CELL_WIDTH * getNextItemsLength(arr);
      const cellHeight =
        HCR_CROSSTAB_CELL_HEIGHT *
        (getNextItemsLength(arr) || 1) *
        (measuresLength || 1);
      return {
        key: row,
        emptyCellWidth: cellWidth,
        items: [
          {
            label: "$V",
            value: row,
            width: HCR_CROSSTAB_CELL_WIDTH,
            height: cellHeight,
            identifier: HCR_CROSSTAB_ROW_TOTAL_GROUP,
          },
          {
            label: "Total",
            value: row,
            width: cellWidth,
            height: HCR_CROSSTAB_CELL_HEIGHT * (measuresLength || 1),
            isTotal: true,
            identifier: HCR_CROSSTAB_ROW_TOTAL_GROUP,
          },
        ],
        children: getRowHeaders(rows[index + 1], measures),
      };
    })
    .filter(Boolean);
};

export const getNestedArr = (arr) => {
  if (arr.length === 0) return [];
  if (arr.length === 1) return arr;
  return [arr[0], getNestedArr(arr.slice(1))];
};

const calculatCTotalHeightAndWidth = (
  rows = [],
  columns = [],
  measures = [],
) => {
  const headerCWidth = HCR_CROSSTAB_CELL_WIDTH * (columns.length + 1);
  const headerCHeight = HCR_CROSSTAB_CELL_HEIGHT * columns.length;
  const rowsHWidth = HCR_CROSSTAB_CELL_WIDTH * rows.length;
  const rowsHHeight =
    HCR_CROSSTAB_CELL_HEIGHT * ((rows.length + 1) * measures.length);

  return {
    width: headerCWidth + rowsHWidth,
    height: headerCHeight + rowsHHeight,
  };
};

const getActualField = (fields = [], name) => {
  return fields.find((field) => field.name === name);
};

const getCrossTabStaticNode = ({
  height,
  width,
  value,
  addExpression,
  isTotal = false,
  nodeId,
  cell,
  identifier,
  x,
  y,
}) => {
  const id = `node-${uuidv4()}`;
  return {
    id,
    label: addExpression ? `Total \n${value}` : isTotal ? "Total" : "$V",
    value,
    borders: {},
    padding: {},
    width,
    height,
    name: "text",
    renderKey: "text",
    parentKey: "elements",
    isLeaf: true,
    repeat: "na",
    category: "text",
    zIndex: 10,
    type: "defaultNodes",
    fontSize: 10,
    group: nodeId,
    parent: nodeId,
    x,
    y,
    cell,
    identifier,
    isCrosstabCell: true,
  };
};

const getCrossTabTextNode = ({
  height,
  width,
  value,
  addExpression,
  nodeId,
  cell,
  identifier,
  x,
  y,
  otherProps = {},
  actualField = {},
}) => {
  const id = `node-${uuidv4()}`;
  return {
    id,
    name: value,
    value,
    width: width,
    height: height,
    label: addExpression ? `$V{${value}}` : "$V",
    renderKey: "text",
    isLeaf: true,
    zIndex: 10,
    fontSize: 10,
    type: "queryField",
    category: "text",
    repeat: "rd",
    borders: {},
    padding: {},
    backendDataType: actualField?.clazz || "",
    group: nodeId,
    x,
    y,
    parent: nodeId,
    cell,
    identifier,
    isCrosstabCell: true,
    ...otherProps,
  };
};

const getHeaderNodes = (
  headers = [],
  {
    crossTabX,
    crossTabY,
    emptyCellWidth,
    emptyCellHeight,
    prevHeight = 0,
    prevWidth = 0,
    nodeId,
    isColumnHeader = true,
    fields,
  },
) => {
  return headers
    .map(({ items = [], children = [] }) => {
      let headerNodes = [];
      let childrenCount = children.length;
      let prevNodeHeight = prevHeight;
      let prevNodeWidth = prevWidth;
      headerNodes = items.map(({ value, width, height }, i, arr) => {
        let identifier = HCR_CROSSTAB_COLUMN_GROUP;
        const actualField = getActualField(fields, value);
        if (!isColumnHeader) identifier = HCR_CROSSTAB_ROW_GROUP;
        if (i === 0) {
          prevNodeHeight += height;
          prevNodeWidth += width;
          let sX = crossTabX + emptyCellWidth;
          let sY = crossTabY + prevHeight;
          let cell = HCR_CROSSTAB_COLUMN_HEADER;
          if (!isColumnHeader) {
            sX = crossTabX + prevWidth;
            sY = crossTabY + emptyCellHeight;
            cell = HCR_CROSSTAB_ROW_HEADER;
          }
          return getCrossTabTextNode({
            value,
            width,
            height,
            addExpression: childrenCount,
            x: sX,
            y: sY,
            cell,
            nodeId,
            identifier,
            actualField,
          });
        }
        let tX = crossTabX + emptyCellWidth + arr[i - 1].width;
        let tY = crossTabY + prevHeight;

        if (!isColumnHeader) {
          tX = crossTabX + prevWidth;
          tY = crossTabY + emptyCellHeight + arr[i - 1].height;
        }

        return getCrossTabStaticNode({
          value,
          width,
          height,
          prevNodeWidth: arr[i - 1].width,
          addExpression: childrenCount,
          x: tX,
          y: tY,
          cell: isColumnHeader
            ? HCR_CROSSTAB_COLUMN_TOTAL_HEADER
            : HCR_CROSSTAB_ROW_TOTAL_HEADER,
          nodeId,
          identifier,
          isTotal: true,
        });
      });

      if (childrenCount) {
        headerNodes = [
          ...headerNodes,
          ...getHeaderNodes(children, {
            crossTabX,
            crossTabY,
            emptyCellWidth,
            emptyCellHeight,
            prevHeight: prevNodeHeight,
            prevWidth: prevNodeWidth,
            nodeId,
            isColumnHeader,
            fields,
          }),
        ];
      }

      return headerNodes;
    })
    .flat(Infinity);
};

export const getHCRCrosstabCols = (columnHeaders) => {
  return columnHeaders
    .map(({ items = [], children = [] }) => {
      return [
        ...(children?.length
          ? items.filter((item) => item.isTotal)
          : items.reverse()),
        ...(children?.length ? getHCRCrosstabCols(children) : []),
      ];
    })
    .flat(Infinity);
};

const getCellNodes = ({
  measures = [],
  crossTabX,
  crossTabY,
  emptyCellHeight,
  emptyCellWidth,
  nodeId,
  columnHeaders,
  rowHeaders,
  fields = [],
  measuresAggregateMap,
}) => {
  let cols = getHCRCrosstabCols(columnHeaders).reverse();
  let rows = getHCRCrosstabCols(rowHeaders).reverse();
  let rowHeight = 0;
  return rows
    .map((row) => {
      const {
        identifier: rowIdentifier,
        isTotal: isRowTotal = false,
        value: rowValue,
      } = row || {};
      return measures.map((measure) => {
        let colWidth = 0;
        const cells = cols.map((col) => {
          const {
            identifier: colIdentifier,
            isTotal: isColTotal = false,
            value: columnValue,
          } = col || {};
          const actualField = getActualField(fields, measure);
          let commonProps = {
            nodeId,
            value: measure,
            width: HCR_CROSSTAB_CELL_WIDTH,
            height: HCR_CROSSTAB_CELL_HEIGHT,
            addExpression: false,
            x: crossTabX + emptyCellWidth + colWidth,
            y: crossTabY + emptyCellHeight + rowHeight,
            otherProps: {
              measureLabel: `$V{${measure}_MEASURE}`,
              aggregateFn: measuresAggregateMap[measure],
            },
            actualField,
          };
          if (!isRowTotal && !isColTotal) {
            let cell = getCrossTabTextNode({ ...commonProps, identifier: {} });
            colWidth += HCR_CROSSTAB_CELL_WIDTH;
            return cell;
          }
          if (!isRowTotal && isColTotal) {
            let cell = getCrossTabTextNode({
              ...commonProps,
              identifier: { [colIdentifier]: columnValue },
            });
            colWidth += HCR_CROSSTAB_CELL_WIDTH;
            return cell;
          }
          if (isRowTotal && !isColTotal) {
            let cell = getCrossTabTextNode({
              ...commonProps,
              identifier: { [rowIdentifier]: rowValue },
            });
            colWidth += HCR_CROSSTAB_CELL_WIDTH;
            return cell;
          }
          if (isRowTotal && isColTotal) {
            let cell = getCrossTabTextNode({
              ...commonProps,
              identifier: {
                [rowIdentifier]: rowValue,
                [colIdentifier]: columnValue,
              },
            });
            colWidth += HCR_CROSSTAB_CELL_WIDTH;
            return cell;
          }
        });
        rowHeight += HCR_CROSSTAB_CELL_HEIGHT;
        return cells;
      });
    })
    .flat(Infinity);
};

const getCrossTabNodes = ({
  columnHeaders,
  rowHeaders,
  measures,
  crossTabX,
  crossTabY,
  emptyCellHeight,
  emptyCellWidth,
  nodeId,
  fields = [],
  measuresAggregateMap = {},
}) => {
  const clmHeaders = cloneDeep(columnHeaders);
  const rwHeaders = cloneDeep(rowHeaders);
  const columnHeaderNodes = getHeaderNodes(clmHeaders, {
    crossTabX,
    crossTabY,
    emptyCellWidth,
    emptyCellHeight,
    nodeId,
    fields,
  });
  const rowHeaderNodes = getHeaderNodes(rwHeaders, {
    crossTabX,
    crossTabY,
    emptyCellHeight,
    emptyCellWidth,
    nodeId,
    isColumnHeader: false,
    fields,
  });
  const cellNodes = getCellNodes({
    measures,
    crossTabX,
    crossTabY,
    emptyCellHeight,
    emptyCellWidth,
    nodeId,
    columnHeaders: clmHeaders,
    rowHeaders: rwHeaders,
    fields,
    measuresAggregateMap,
  });
  return [...columnHeaderNodes, ...rowHeaderNodes, ...cellNodes];
};

const getCrosstabConfig = ({
  columnFields = [],
  rowFields = [],
  measures = [],
  crossTabX,
  crossTabY,
  nodeId,
  fields = [],
  measuresAggregateMap,
  padding = {},
}) => {
  const columnHeaders = getColumnHeaders(getNestedArr(columnFields));
  const rowHeaders = getRowHeaders(getNestedArr(rowFields), measures);
  const emptyCellHeight = columnHeaders?.[0]?.emptyCellHeight;
  const emptyCellWidth = rowHeaders?.[0]?.emptyCellWidth;
  const { width, height } = calculatCTotalHeightAndWidth(
    rowFields,
    columnFields,
    measures,
  );
  let crossTabNodes = getCrossTabNodes({
    columnHeaders,
    rowHeaders,
    measures,
    crossTabX,
    crossTabY,
    emptyCellHeight,
    emptyCellWidth,
    nodeId,
    fields,
    measuresAggregateMap,
  });
  if (!isEmpty(padding) && ("Top" in padding || "Left" in padding)) {
    let pt = padding.Top || 0,
      pl = padding.Left || 0;
    crossTabNodes = crossTabNodes.map((node) => {
      return {
        ...cloneDeep(node),
        offset: {
          x: node.x - crossTabX,
          y: node.y - crossTabY,
        },
        x: node.x + (pl > 1 ? pl : 0),
        y: node.y + (pt > 1 ? pt : 0),
      };
    });
  }
  return { columnHeaders, rowHeaders, width, height, nodes: crossTabNodes };
};

const getHcrChartsDefaultProperties = () => {
  return {
    chartTitle: {
      expression: "",
      color: "#000000",
      fontSize: 10,
      bold: false,
      italic: false,
      underline: false,
      strikeThrough: false,
      position: "top",
    },
    chartSubtitle: {
      expression: "",
      color: "#000000",
      fontSize: 10,
      bold: false,
      italic: false,
      underline: false,
      strikeThrough: false,
    },
    chartLegend: {
      showLegend: true,
      position: "bottom",
      foreColor: "#000000",
      backColor: "#fefefe",
      fontSize: 10,
      bold: false,
      italic: false,
      underline: false,
      strikeThrough: false,
    },
    chartItemLabel: {
      color: "#000000",
      backgroundColor: "#fefefe",
      fontFamily: "SansSerif",
      fontSize: 10,
      bold: false,
      italic: false,
      underline: false,
      strikeThrough: false,
      showLabels: true,
    },
    chartColors: {
      backColor: "#FFFFFF",
      backgroundAlpha: 1,
      foregroundAlpha: 1,
      orientation: "Vertical",
      seriesColors: antdChartCategoryColors,
    },
    evaluationTime: "Report",
    renderType: "svg",
    theme: "default",
  };
};

const getHCRChartContainerStyles = (data = {}) => {
  const { borders = {} } = data || {};

  const defaultBorder = `0px solid #000000`;
  let styles = {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    borderTop: borders.Top
      ? `${borders.Top.stroke}px ${borders.Top.style?.toLowerCase()} ${borders.Top.color
      }`
      : defaultBorder,
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
  return styles;
};

// HCR TOOLTIPS
export const getLabel = ({ label, tooltip, isRequired, placement }) => {
  const tooltipTitle = tooltip?.content?.reduce(
    (acc, cur) => acc + cur.displayContent,
    "",
  );
  let divTag = document.createElement("div");
  divTag.innerHTML = tooltipTitle;

  return (
    <Tooltip
      title={<div dangerouslySetInnerHTML={{ __html: divTag.innerHTML }} />}
      placement={placement || "topLeft"}
    >
      <div className={`property-label ${isRequired ? "required-field" : ""}`}>
        {label}
      </div>
    </Tooltip>
  );
};
//HCR nodata in preview
export const NoDataTemplate = ({ message, handleBack }) => (
  <div className="hcr-no-data-template">
    <Tooltip title="Go back to previous view">
      <span className="hcr-abort-return-button" onClick={handleBack}>
        <ArrowLeftOutlined />
      </span>
    </Tooltip>
    <div className="no-data-content">
      <h3>{message?.title}</h3>
      <p>{message?.description}</p>
    </div>
  </div>
);

//  error message extractt
export const getErrorMessage = (
  error,
  defaultMsg = "Something went wrong while generating preview",
) => {
  if (typeof error === "string") return error;
  if (error?.message) return error.message;
  if (error?.error) return error.error;
  return defaultMsg;
};

export const handleStreamResponse = (res = {}) => {
  const { event, data = {} } = res || {};
  let type = "", returnData = {};
  switch (event) {
    case "begin":
      break;
    case "complete":
      type = "complete";
      break;
    case "error":
      type = "error";
      returnData = data;
      break;
    case "update":
      type = "update";
      returnData = data;
      break;
    default:
      if (event?.startsWith("page_")) {
        if (data.response) {
          type = "stream"
          returnData = {
            [event]: data
          }
        } else {
          type = "page_count"
          returnData = {
            totalPageCount: data?.totalPageCount || 1
          }
        }
      }
      break;
  };
  return {
    type,
    data: returnData
  }
};


// hcr advance table node

export const getTableStyleNameReference = (bandType) => {
  const { columnHeader, tableData, tableHeader } = hcrTableStyleNameReferences;
  return {
    [hcrTableBandsTypes.TABLE_HEADER]: tableHeader,
    [hcrTableBandsTypes.COLUMN_HEADER]: columnHeader,
    [hcrTableBandsTypes.GROUP_HEADER]: columnHeader,
    [hcrTableBandsTypes.COLUMN_DATA]: tableData,
    [hcrTableBandsTypes.GROUP_FOOTER]: columnHeader,
    [hcrTableBandsTypes.COLUMN_FOOTER]: columnHeader,
    [hcrTableBandsTypes.TABLE_FOOTER]: tableHeader,
  }[bandType]
}

export const makeCellId = (columnId, bandType, groupField) => `${columnId}-${bandType}${groupField ? `-${groupField}` : ""}`;

export const createCell = (columnId, bandType, width, height, groupField) => {
  const id = makeCellId(columnId, bandType, groupField);
  const obj = {
    id,
    columnId,
    bandType,
    width,
    height,
    nodeIds: [],
  }
  if (groupField) {
    obj.groupField = groupField;
  }
  return obj;
}

export const isGroupBand = (bandType) => [hcrTableBandsTypes.GROUP_HEADER, hcrTableBandsTypes.GROUP_FOOTER].includes(bandType);

export const getAvailableBands = (bands = {}) => {
  return hcrTableBandOrder.filter((bandType) => {
    if (isGroupBand(bandType)) {
      const band = bands[bandType];
      return band?.groupFields?.length > 0;
    }
    return bands[bandType];
  });
}

export const getActiveBands = (bands, cells) => {
  return hcrTableBandOrder.filter((bandType) => {
    if (isGroupBand(bandType)) {
      const band = bands[bandType];
      const grpFields = band.groupFields || [];
      const deleted = []
      grpFields.forEach((grpField) => {
        if (checkIfBandIsDeleted(bandType, cells, grpField)) deleted.push(grpField)
      })
      return deleted.length !== grpFields.length;
    }
    return !checkIfBandIsDeleted(bandType, cells);
  });
}

const getBandsConfig = (tableConfig = {}, tableGroupFields) => {
  const {
    bordersColor = "#000000",
    headerColor = "#f0f8ff",
    columnHeaderColor = "#bfe1ff",
    bodyColor = "#ffffff",
    addColumnFooter,
    addColumnHeader,
    addTableFooter,
    addTableHeader,
    addGroupHeader,
    addGroupFooter
  } = tableConfig || {}

  const headerStyle = {
    backgroundColor: headerColor,
    borderColor: bordersColor
  }
  const columnHeaderStyle = {
    backgroundColor: columnHeaderColor,
    borderColor: bordersColor
  }
  const bodyStyles = {
    backgroundColor: bodyColor,
    borderColor: bordersColor
  }

  const bandConfig = {
    [hcrTableBandsTypes.TABLE_HEADER]: {
      styles: headerStyle,
      available: addTableHeader
    },
    [hcrTableBandsTypes.COLUMN_HEADER]: {
      styles: columnHeaderStyle,
      available: addColumnHeader
    },
    [hcrTableBandsTypes.GROUP_HEADER]: {
      styles: headerStyle,
      available: addGroupHeader,
      groupFields: tableGroupFields || []
    },
    [hcrTableBandsTypes.COLUMN_DATA]: {
      styles: bodyStyles,
      available: true
    },
    [hcrTableBandsTypes.GROUP_FOOTER]: {
      styles: headerStyle,
      available: addGroupFooter,
      groupFields: tableGroupFields || []
    },
    [hcrTableBandsTypes.COLUMN_FOOTER]: {
      styles: columnHeaderStyle,
      available: addColumnFooter
    },
    [hcrTableBandsTypes.TABLE_FOOTER]: {
      styles: headerStyle,
      available: addTableFooter
    },
  }
  return bandConfig;
}

export const getTableStyles = (tableId, tableCount) => {
  return [
    {
      styleName: tableCount > 0 ? `TABLE ${tableCount}_TH` : "TABLE_TH",
      bandsApplicable: [hcrTableBandsTypes.TABLE_HEADER, hcrTableBandsTypes.TABLE_FOOTER],
      id: uuidv4(),
      tableId,
      isChanged: false,
      isConditionalStyleReq: true,
      expression: "",
      expressionBackColor: "#BFE1FF",
      borders: {
        Top: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Bottom: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Right: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Left: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        }
      },
      padding: {
        Top: 0,
        Bottom: 0,
        Right: 0,
        Left: 0
      },
      lineStyles: {
        stroke: 1,
        style: "SOLID",
        color: "#000000"
      },
      mode: "Opaque",
      fontFill: "#000000",
      fill: "#F0F8FF",
      fontSize: 10
    },
    {
      styleName: tableCount > 0 ? `TABLE ${tableCount}_CH` : "TABLE_CH",
      bandsApplicable: [
        hcrTableBandsTypes.COLUMN_HEADER,
        hcrTableBandsTypes.GROUP_HEADER,
        hcrTableBandsTypes.GROUP_FOOTER,
        hcrTableBandsTypes.COLUMN_FOOTER
      ],
      id: uuidv4(),
      tableId,
      isChanged: false,
      isConditionalStyleReq: true,
      expression: "",
      expressionBackColor: "#BFE1FF",
      borders: {
        Top: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Bottom: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Right: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Left: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        }
      },
      padding: {
        Top: 0,
        Bottom: 0,
        Right: 0,
        Left: 0
      },
      lineStyles: {
        stroke: 1,
        style: "SOLID",
        color: "#000000"
      },
      mode: "Opaque",
      fontFill: "#000000",
      fill: "#BFE1FF",
      fontSize: 10
    },
    {
      styleName: tableCount > 0 ? `TABLE ${tableCount}_TD` : "TABLE_TD",
      bandsApplicable: [hcrTableBandsTypes.COLUMN_DATA],
      id: uuidv4(),
      tableId,
      mode: "Opaque",
      isChanged: false,
      isConditionalStyleReq: true,
      expression: "",
      expressionBackColor: "#BFE1FF",
      isTD: true,
      borders: {
        Top: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Bottom: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Right: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        },
        Left: {
          stroke: 1,
          style: "SOLID",
          color: "#000103"
        }
      },
      padding: {
        Top: 0,
        Bottom: 0,
        Right: 0,
        Left: 0
      },
      lineStyles: {
        stroke: 1,
        style: "SOLID",
        color: "#000000"
      },
      mode: "Opaque",
      fontFill: "#000000",
      fill: "#FFFFFF",
      fontSize: 10
    }
  ]
}

export const getTableDefaultStyles = (tableId, previousStyles = []) => {
  if (!previousStyles?.length) return getTableStyles(tableId, 0);
  let counter = 1, styleName = `TABLE ${counter}`;
  while (previousStyles.some((style) => style.styleName.split("_")[0] === styleName)) {
    counter++
    styleName = `TABLE ${counter}`
  }
  return getTableStyles(tableId, counter);
}

export const getStyleNameReference = (styles, bandType) => {
  if (isEmpty(styles) || !bandType) return null;
  return styles.find((style) => style.bandsApplicable.includes(bandType))?.id || null;
}

export const getAdvancedTableConfig = (
  columnCount = 1,
  columnWidth = HCR_TABLE_DATA_CELL_WIDTH,
  tableConfig = {},
  tableNodes = [],
  tableGroupFields = [],
  defaultStyles = []
) => {
  const columns = {},
    cells = {},
    nodes = {},
    columnOrder = [],
    bands = {},
    selectedColumnId = "",
    selectedBandType = "",
    selectedCells = [],
    selectedNodes = [];

  const bandConfig = getBandsConfig(tableConfig, tableGroupFields);

  hcrTableBandOrder.forEach((band) => {
    bands[band] = {
      height: HCR_TABLE_DATA_CELL_HEIGHT,
      styles: bandConfig[band].styles,
      ...(bandConfig[band]?.groupFields ? { groupFields: bandConfig[band].groupFields || [] } : {})
    };
  })

  const flattenNodes = []
  for (let i = 0; i < columnCount; i++) {
    const colId = uuidv4();
    columnOrder.push(colId);
    columns[colId] = { id: colId, width: columnWidth, name: `Column ${i + 1}` };

    hcrTableBandOrder.forEach((band) => {
      const bandAvailable = bandConfig[band].available;
      if (isGroupBand(band)) { // group fields 
        const groupFields = bands[band].groupFields || [];
        groupFields.forEach((field) => {
          const cell = createCell(colId, band, columnWidth, bands[band].height, field);
          let styleNameReference = getStyleNameReference(defaultStyles, cell.bandType);
          cell.styleNameReference = styleNameReference;
          if (!bandAvailable) cell.deleted = true;
          cells[cell.id] = cell;
        })
      } else {
        const cell = createCell(colId, band, columnWidth, bands[band].height);
        let styleNameReference = getStyleNameReference(defaultStyles, cell.bandType);
        cell.styleNameReference = styleNameReference;
        if (tableNodes.length && !isGroupBand(band) && bandAvailable) {
          const currentNodes = tableNodes[i] || [];
          const node = currentNodes.find(node => node.band === band);
          if (node) {
            node.cellId = cell.id;
            flattenNodes.push(node);
            cell.nodeIds.push(node.id);
          }
        }
        if (!bandAvailable) cell.deleted = true;
        cells[cell.id] = cell;
      }
    })
  }

  if (flattenNodes?.length) {
    flattenNodes.forEach(node => {
      nodes[node.id] = node;
    })
  }

  return {
    columns,
    cells,
    nodes,
    columnOrder,
    bands,
    selectedColumnId,
    selectedBandType,
    selectedCells,
    selectedNodes
  }
}

export const getDroppedNodeData = (item = {}, cell = {}) => {
  const { bandType, id, width, nodeIds = [], height } = cell || {}
  let prevNodes = nodeIds.length || 1
  let node = {
    ...item,
    cellId: id,
    band: bandType,
    x: 0,
    y: 0,
    id: `node-${uuidv4()}`,
    printRepeatedValues: true,
    width: width / prevNodes,
    height: height,
    fontFamily: "Serif",
    fontSize: 10,
  }
  if (node.category === "line") {
    delete node.printRepeatedValues;
  }
  if (node.category === "text" && node.type === "defaultNodes") {
    node.label = '"Text"';
  }
  HCR_TABLE_CELL_PROPERTIES.forEach((property) => {
    if (cell[property]) node[property] = cell[property]
  })
  return node;
}

export const getInitialGroupData = (name) => {
  return {
    id: uuidv4(),
    expression: `$F{${name}}`,
    name: name,
    isChecked: true
  }
}

export const getSubDataSet = (subDataSets = [], id) => {
  if (!subDataSets?.length || !id) return {}

  let subDataSet = subDataSets.find((ds) => ds.id === id);
  if (!subDataSet) {
    const emptySubDS = subDataSets.filter((ds) => ds.isEmpty)
    if (emptySubDS.length) {
      let emptySubDataSet = emptySubDS.find((subDS) => {
        let subDataId = subDS.id || "";
        return subDataId.includes(id)
      })
      if (emptySubDataSet) return emptySubDataSet;
      return {}
    }
    return {}
  }
  return subDataSet;
}

export const getTableBandStyle = ({ bandType, tableStyles, tableId, isFirstRow, isFirstCol, cell = {} }) => {
  const { styleNameReference: styleId } = cell || {};
  let bandStyle = tableStyles.find((style) => style.id === styleId) || {};
  let heightOffset = 0, widthOffset = 0;
  function getBB(borders) {
    return `1px ${borders.Bottom.style?.toLowerCase()} ${borders.Bottom.color}`
  }

  function getBR(borders) {
    return `1px ${borders.Right.style?.toLowerCase()} ${borders.Right.color}`
  }

  function getBT(borders) {
    return `1px ${borders.Top.style?.toLowerCase()} ${borders.Top.color}`
  }

  function getBL(borders) {
    return `1px ${borders.Left.style?.toLowerCase()} ${borders.Left.color}`
  }

  if (isEmpty(bandStyle)) return {
    borderTop: "1px solid #000000",
    borderBottom: "1px solid #000000",
    borderLeft: "1px solid #000000",
    borderRight: "1px solid #000000",
  };

  const {
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
    borders = {},
    padding = {},
  } = bandStyle || {};
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
    display: "flex",
    backgroundColor:
      mode.toLowerCase() === "transparent" ? "transparent" : fill,
    alignItems,
    justifyContent: justifyContent,
    borderTop: isFirstRow ? getBT(borders) : defaultBorder,
    borderLeft: isFirstCol ? getBL(borders) : defaultBorder,
    borderRight: getBR(borders),
    borderBottom: getBB(borders),
    fontFamily,
  };

  let textRotate = 0;

  if (rotation === "Left") {
    textRotate = -90;
  } else if (rotation === "Right") {
    textRotate = 90;
  } else if (rotation === "UpsideDown") {
    textRotate = 180;
  }

  styleObj.transform = `rotate(${textRotate}deg)`;
  return styleObj;
}

export const hcrCanvasPaneHelperMethods = {
  pageSizeChange,
  pageOrientationChange,
  pageMarginChange,
  handleShapePropertyChanges,
  getHcrTableCellsPositionDetails,
  getHCRTableClosestNode,
  getFlowChartConfig,
  onHCRCanvasConfigChange,
  onChildNodeMove,
  getHcrPropertyTooltipInfo,
  getCrosstabConfig,
  getHcrChartsDefaultProperties,
  getHCRChartContainerStyles,
  handleNodeSelection,
};
