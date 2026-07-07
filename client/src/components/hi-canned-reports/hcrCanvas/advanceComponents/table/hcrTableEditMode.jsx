import "@ant-design/flowchart/dist/index.css";
import { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { hcrActions } from '../../../../../redux/actions';
import { getActiveBands, getAvailableBands, getSubDataSet, getTableBandStyle, isGroupBand, makeCellId } from '../../hcrCanvasPaneHelperMethods';
import EditableCell from '../components/editableCell';
import SidebarPanel from '../components/sidebarPanel';
import "./hcrAdvancedTable.scss";
import { HCR_OUTSIDE_NODE, HCR_TABLE_DATA_CELL_WIDTH, hcrDSQuery } from "../../../hcr-constants";
import { useDrop } from "react-dnd";
import { checkIfBandIsDeleted } from "../../../hcrHelperMethods";
import { v4 as uuidv4 } from "uuid";
import { isEmpty } from "lodash";

const HCRAdvancedTableEdit = (props = {}) => {
  const { data = {}, lastSelectedNodeRef } = props || {};

  const {
    bands = {},
    columnOrder = [],
    width,
    height,
    id,
    nodes = {},
    selectedNodes = [],
    selectedCells = [],
    cells = {},
    outlineDsSelectedField = null,
    outlineDSFields = [],
    selectedTable = null,
    selectedCalculation = [],
    selectedQueryID,
    selectedGroup = [],
    selectedParameter = [],
    selectedStyle = []
  } = data || {};
  const availableBands = getActiveBands(bands, cells);

  const activeTab = useSelector((state) => state.cannedReports.present.hcrTabData.panes.find(
    (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
  )
  ) || {};
  const designerProperties = useSelector(
    (state) =>
      state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR
        ?.designerProperties || {},
  );
  const { variables } = designerProperties;
  const { classNames } = variables || {};

  const { hcrTableClipboardData = {}, dsPaneTypes = [], selectedQueryId: mainQuery, subDataSets = [], tableStyles = [] } = activeTab || {}
  let selectedSubDS = getSubDataSet(subDataSets, (selectedQueryID || id));
  const { fields = [] } = selectedSubDS || {}

  let copiedNodes = [];
  const { copy = [], cut = [] } = hcrTableClipboardData?.[id] || {}
  if (copy.length) copiedNodes = copy
  if (cut.length) copiedNodes = cut

  let queriesMenu = dsPaneTypes
    ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
    ?.menu?.filter((ele) => ele.executeQueryData?.data.length || ele.executeQueryData?.field.length) || []
  queriesMenu = queriesMenu?.filter((query) => query.id !== mainQuery) || []

  const selectedNodeId = selectedNodes[0],
    selectedCellId = selectedCells[0],
    currentCalculation = selectedCalculation[0],
    currentSelectedGroup = selectedGroup[0],
    currentParameter = selectedParameter[0],
    currentSelectedStyle = selectedStyle[0]

  const [sidePanelOpen, setSidePanelOpen] = useState(false);
  const [currentShortCut, setCurrentShortCut] = useState("");
  const selectedNode = nodes[selectedNodeId] || null;
  const dispatch = useDispatch()
  const widthConstant = 40, heightConstant = 50;

  const [{ isOver, canDrop }, dropRef] = useDrop({
    accept: [HCR_OUTSIDE_NODE],
    drop: (item) => {
      if (item.type === HCR_OUTSIDE_NODE) {
        const { record } = item || {}
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
          actionType: "createNewColAndAddNodes",
          id,
          width: HCR_TABLE_DATA_CELL_WIDTH,
          record,
        }))
      }
    },
    canDrop: (dragObj, monitor) => {
      return monitor.isOver({ shallow: true })
    },
    collect: (monitor) => ({
      isOver: monitor.isOver(),
      canDrop: monitor.canDrop(),
    })
  })

  const handleNodeDelete = (nodeId) => {
    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
      actionType: "deleteNode",
      id,
      nodeId
    }))
    setSidePanelOpen(false);
  }
  const handleOpenSidePanel = () => {
    setSidePanelOpen(true);
  }

  const handleNodeClick = (e, node) => {
    if (!sidePanelOpen) {
      handleOpenSidePanel()
    }
    if (node) {
      if (e.ctrlKey) {
        const isPresent = selectedNodes.includes(node.id);
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
          actionType: isPresent ? "removeSelectedNode" : "selectNodes",
          id,
          selectedNodes: [...selectedNodes, node.id],
          nodeId: node.id
        }))
      } else {
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
          actionType: "selectNodes",
          id,
          selectedNodes: [node.id],
        }))
        lastSelectedNodeRef.current = { id: node.id, type: "image" }
      }
    }
  }

  const handleSidePanelClose = () => {
    setSidePanelOpen(false);
    dispatch(hcrActions.hcrUpdateCanvasTabComponent({ actionType: "clearSelection", id, }))
  }

  const handleChangeNodeConfig = useCallback((key, value, styles = null, otherKeyValuePairs = {}) => {
    let obj = {
      [key]: value,
      ...otherKeyValuePairs
    };

    if (key === "strikeThrough") {
      if (selectedNode.underLine) {
        obj.underLine = false;
      }
    } else if (key === "underLine") {
      if (selectedNode.strikeThrough) {
        obj.strikeThrough = false;
      }
    }

    if (["styleName"].includes(key) && styles) {
      obj = {
        ...obj,
        ...styles,
      };
    }

    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
      actionType: "updateNodeProperties",
      id,
      nodeId: selectedNodeId,
      properties: obj
    }))
  }, [selectedNodeId])

  const selectAllNodes = () => {
    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
      actionType: "selectNodes",
      id,
      selectedNodes: Object.keys(nodes)
    }))
  }

  const handleCellPropertyChange = ({ key, value, cellIds, columnId, bandType }) => {
    switch (key) {
      case "height":
      case "width":
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
          actionType: "resizeCell",
          id,
          columnId,
          cellIds,
          bandType,
          [key]: value
        }))
        break;
      default:
        dispatch(hcrActions.hcrUpdateCanvasTabComponent({
          actionType: "updateCellProperties",
          id,
          properties: { [key]: value },
          cellIds
        }))
        break;
    }
  }

  const handleOutlineDsItemChange = ({ type = "", id: fieldId, value = {}, subDSId }) => {
    switch (type) {
      case "field-item": {
        let newFields = fields.map((field) => {
          if (field.id === fieldId) {
            return {
              ...field,
              ...value
            }
          }
          return field;
        })
        dispatch(hcrActions.hcrUpdateSubdataSets({
          actionType: "updateFields",
          id: subDSId,
          fields: newFields
        }))
        break;
      }
      default:
        break;
    }
  }

  const handleOutsideClick = (e) => {
    e.preventDefault();
    handleSidePanelClose()
    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
      actionType: "clearSelection", id,
    }))

  }

  const handleTablePropertiesChange = ({ key, value }) => {
    let payload = {}
    switch (key) {
      case "selectedQueryID": {
        let subDataSet = subDataSets.find((ds) => ds.id === value);
        if (subDataSet) {
          const { id: subDSId, groups = [], selectedGroupFields = [], selectedFields = [] } = subDataSet || {}
          payload.selectedQueryID = subDSId;
          payload.selectedGroupFields = selectedGroupFields;
          payload.selectedFields = selectedFields;

          if (groups.length) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              actionType: "removeOldGroupsAndAddNew",
              id,
              groupFields: groups.map(({ name }) => name)
            }))
          } else {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              actionType: "deleteOldGroups",
              id,
            }))
          }
        } else {
          const selectedQuery = queriesMenu?.find((ele) => ele.id === value)
          const { executeQueryData, name, id: dsID } = selectedQuery || {};
          const { field = [] } = executeQueryData || {};
          const subDSPayload = {
            actionType: "add",
            id: dsID,
            name,
            groups: [],
            fields: field?.map((f) => ({ ...f, id: uuidv4() })) || [],
            selectedFields: field?.map(({ name }) => name) || [],
            selectedGroupFields: []
          }
          dispatch(hcrActions.hcrUpdateSubdataSets(subDSPayload))
          dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            actionType: "deleteOldGroups",
            id,
          }))
          payload = {
            selectedQueryID: dsID,
            selectedGroupFields: [],
            selectedFields: subDSPayload.selectedFields
          }
        }
        break;
      }
      default:
        break;
    }
    if (!isEmpty(payload)) {
      dispatch(hcrActions.hcrUpdateCanvasTabComponent({
        actionType: "tableProperties",
        id,
        properties: payload
      }))
    }
  }

  useEffect(() => {
    function handleCtrlKeys(e) {
      const activeElement = document.activeElement;
      const isInputField = activeElement.tagName === "INPUT" || activeElement.tagName === "TEXTAREA" || activeElement.isContentEditable;

      if (!isInputField) {
        if (e.code === "Delete" && selectedNodeId) {
          e.preventDefault();
          handleNodeDelete(selectedNodeId)
          setCurrentShortCut("Delete");
        }
        if (e.ctrlKey && e.key === "a") {
          e.preventDefault();
          selectAllNodes();
          setCurrentShortCut("SelectAll");
        }
        if (e.ctrlKey && e.key === "c") {
          e.preventDefault();
          setCurrentShortCut("copy");
          if (selectedNodes.length) {
            let cNodes = selectedNodes.reduce((acc, next) => {
              acc.push(nodes[next]);
              return acc;
            }, [])
            dispatch(hcrActions.hcrUpdateTableClipboard({
              id,
              type: "copy",
              nodes: cNodes
            }))
          }
        }
        if (e.ctrlKey && e.key === "x") {
          e.preventDefault();
          setCurrentShortCut("cut");
          if (selectedNodes.length) {
            let cNodes = selectedNodes.reduce((acc, next) => {
              acc.push(nodes[next]);
              return acc;
            }, [])
            dispatch(hcrActions.hcrUpdateTableClipboard({
              id,
              type: "cut",
              nodes: cNodes
            }))
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              actionType: "cutNodes",
              id,
              cutNodesData: cNodes
            }))
          }
          handleSidePanelClose()
        }
        if (e.ctrlKey && e.key === "v") {
          e.preventDefault();
          setCurrentShortCut("paste");
          const cell = cells?.[selectedCellId] || {};
          if (copiedNodes.length && !cell.deleted) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              actionType: "pasteCopiedNodes",
              id,
              copiedNodes
            }))
          }
        }
      }
    }
    window.addEventListener("keydown", handleCtrlKeys);
    if (selectedNodeId || selectedCellId || outlineDsSelectedField || selectedTable || currentCalculation || currentSelectedGroup || currentParameter || currentSelectedStyle) {
      handleOpenSidePanel()
    }
    return () => window.removeEventListener("keydown", handleCtrlKeys);
  }, [selectedNodeId, selectedCellId, currentShortCut, selectedNodes, outlineDsSelectedField, selectedTable, currentCalculation, currentSelectedGroup, currentParameter, currentSelectedStyle]);

  useEffect(() => {
    return () => {
      dispatch(hcrActions.hcrUpdateCanvasTabComponent({
        actionType: "clearSelection", id,
      }))
    }
  }, [])

  const sidebarPanelProps = {
    open: sidePanelOpen,
    onClose: handleSidePanelClose,
    nodeConfig: selectedNode,
    onNodeConfigChange: handleChangeNodeConfig,
    onCellPropertyChange: handleCellPropertyChange,
    data: data,
    classNames: classNames,
    onOutlineDsItemChange: handleOutlineDsItemChange,
    queriesMenu,
    onTablePropertiesChange: handleTablePropertiesChange,
    lastSelectedNodeRef,
    selectedSubDS,
    activeTab,
  }

  return (
    <div className='hcr-table-edit-container' style={{ width: width + widthConstant, height: height + heightConstant }}>
      <div className='table-wrapper' style={{ width, height }} onClick={handleOutsideClick} ref={dropRef}>
        <div style={{ width: 'max-content' }}>
          <table className='edit-mode'>
            <tbody>
              {availableBands.map((bandType, rowIndex, arrLength) => {
                const isFirstRow = rowIndex === 0;
                const isGroup = isGroupBand(bandType);
                if (isGroup) {
                  const groupFields = bands?.[bandType]?.groupFields || [];
                  return groupFields.map((groupField, index) => {
                    let isDeleted = checkIfBandIsDeleted(bandType, cells, groupField);
                    if (isDeleted) return null;
                    return (
                      <tr
                        key={`${bandType}-${groupField}-${index}`}
                        className='band-row'
                      >
                        {
                          columnOrder.map((colId, colIndex, _arr) => {
                            const cellId = makeCellId(colId, bandType, groupField);
                            const isFirstCol = colIndex === 0;
                            const cell = cells[cellId] || {};
                            const styles = getTableBandStyle({ bandType, tableStyles, isFirstRow, isFirstCol, cell });

                            return (
                              <EditableCell
                                key={cellId}
                                cellId={cellId}
                                onNodeClick={handleNodeClick}
                                cellStyles={styles}
                                onCloseSidePanel={handleSidePanelClose}
                                copiedNodes={copiedNodes}
                                {...data}
                              />
                            )
                          })
                        }
                      </tr>
                    )
                  })
                }
                return (
                  <tr key={bandType} className='band-row'>
                    {
                      columnOrder.map((colId, colIndex, arrLength) => {
                        const cellId = makeCellId(colId, bandType);
                        let isDeleted = checkIfBandIsDeleted(bandType, cells);
                        const isFirstCol = colIndex === 0;
                        const cell = cells[cellId] || {};
                        const styles = getTableBandStyle({ bandType, tableStyles, isFirstRow, isFirstCol, cell });

                        if (isDeleted) return null;
                        return (
                          <EditableCell
                            key={cellId}
                            cellId={cellId}
                            onNodeClick={handleNodeClick}
                            cellStyles={styles}
                            onCloseSidePanel={handleSidePanelClose}
                            copiedNodes={copiedNodes}
                            {...data}
                          />
                        )
                      })
                    }
                  </tr>
                )
              }
              )}
            </tbody>
          </table>
        </div>
      </div>
      <div className='flowchart-editor-panel-body hcr-side-bar-wrapper'>
        <SidebarPanel {...sidebarPanelProps} />
      </div>
    </div >
  )
}

export default HCRAdvancedTableEdit