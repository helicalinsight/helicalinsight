import { Dropdown, Menu } from 'antd';
import { isEmpty } from 'lodash';
import { useDispatch } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import { hcrActions, hcrRedo, hcrUndo } from '../../../../../../redux/actions';
import { HCR_TABLE_DATA_CELL_WIDTH } from '../../../../hcr-constants';
import { getInitialGroupData } from '../../../hcrCanvasPaneHelperMethods';
import { getHCRCrosstabContextMenu, getHCRTableContextMenu, getOutlineDSContextMenu } from '../../utils';


const ContextMenu = (props = {}) => {
  const {
    title = "",
    visible,
    onVisibleChange = () => { },
    handleMenuClick = () => { },
    menu = []
  } = props || {}

  const contextMenu = (
    <Menu onClick={handleMenuClick} className='hcr-table-context-menu' style={{ width: 200 }}>
      {menu?.map((ele) => {
        return <Menu.Item style={{ fontSize: 12 }} className={ele.className || ""} key={ele.key}>{ele.label}</Menu.Item>
      })}
    </Menu>
  )

  return (
    <Dropdown
      visible={visible}
      onVisibleChange={(visible) => onVisibleChange(visible)}
      overlay={contextMenu}
      trigger={["contextMenu"]}
      placement="bottomLeft"
    >
      {title}
    </Dropdown>
  )
}
const OutlineDSContextMenu = (props = {}) => {
  const {
    visible,
    title = null,
    onVisibleChange = () => { },
    menuType,
    componentData = {},
    fieldId = null,
    selectedSubDataSet = {},
    groupId = null,
    calculationId = null,
    parameterId = null,
    styleId = null,
  } = props || {}

  const { fields = [], id: subDSId, groups = [], calculations = [], parameters = [] } = selectedSubDataSet || {}
  const { id: compId, category } = componentData || {}
  const isTable = category === "advancedTable";
  const dispatch = useDispatch()

  const menu = getOutlineDSContextMenu({ menuType })
  const handleMenuClick = ({ key, domEvent: e }) => {
    e.stopPropagation();
    let payload = {};
    switch (key) {
      case "create_field": {
        if (fields.length) {
          let name = "Field 1"

          let counter = 1
          while (fields.some((field) => field.name === name)) {
            name = `Field ${counter}`
            counter++
          }

          payload = {
            actionType: "updateFields",
            id: subDSId,
            fields: [
              ...fields,
              {
                id: uuidv4(),
                name,
                clazz: "java.lang.String",
              }
            ]
          }
        } else {
          payload = {
            actionType: "updateFields",
            id: subDSId,
            fields: [
              {
                id: uuidv4(),
                name: "Field 1",
                clazz: "java.lang.String",
              }
            ]
          }
        }
        break;
      }
      case "delete_fields_item": {
        payload = {
          actionType: "updateFields",
          id: subDSId,
          fields: fields.filter((field) => field.id !== fieldId)
        }
        if (isTable) {
          dispatch(hcrActions.hcrUpdateCanvasTabComponent({ actionType: "clearSelection", id: compId }))
        } else {
          dispatch(hcrActions.hcrUpdateCrosstabComponent({ actionType: "clearSelection", id: compId }))
        }
        break;
      }
      case "create_calculation": {
        payload = {
          id: compId,
          actionType: "selectCalculation",
          selectedCalculation: ["create_calculation"]
        }
        dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
        break;
      }
      case "create_group": {
        if (groups.length) {
          let name = "Group 1"

          let counter = 1
          while (groups.some((field) => field.name === name)) {
            name = `Group ${counter}`
            counter++
          }
          payload = {
            actionType: "updateGroups",
            id: subDSId,
            groups: [
              ...groups,
              getInitialGroupData(name.replace("group_", ""))
            ]
          }
          if (isTable) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              id: compId,
              actionType: "addNewGroup",
              groupField: name,
              category
            }))
          }
        } else {
          payload = {
            actionType: "updateGroups",
            id: subDSId,
            groups: [
              ...groups,
              getInitialGroupData("Group 1")
            ]
          }
          if (isTable) {
            dispatch(hcrActions.hcrUpdateCanvasTabComponent({
              id: compId,
              actionType: "addNewGroup",
              groupField: "Group 1",
              category
            }))
          }
        }
        break;
      }
      case "delete_group_item": {
        payload = {
          actionType: "updateGroups",
          id: subDSId,
          groups: groups.filter((group) => group.id !== groupId)
        }
        const group = groups.find((group) => group.id === groupId)
        if (isTable) {
          dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            id: compId,
            actionType: "deleteGroup",
            groupField: group?.name,
            category
          }))
        }
        break;
      }
      case "delete_calculation_item": {
        payload = {
          actionType: "updateCalculations",
          id: subDSId,
          calculations: calculations.filter((cal) => cal.id !== calculationId)
        }
        dispatch(hcrActions.setHcrCanvasCalculations({ clearKeyValuePairs: true }));
        break;
      }
      case "create_parameter": {
        payload = {
          actionType: "addNewParameter",
          id: subDSId,
        }
        break;
      }
      case "delete_parameter_item": {
        payload = {
          actionType: "updateParameters",
          id: subDSId,
          parameters: parameters.filter((param) => param.id !== parameterId)
        }
        break;
      }
      case "create_style": {
        dispatch(hcrActions.hcrUpdateTableStyles({
          actionType: "createStyle",
          compId,
        }))
        return;
        break;
      }
      case "delete_style_item": {
        dispatch(hcrActions.hcrUpdateTableStyles({
          actionType: "deleteStyleById",
          compId,
          styleId
        }))
        if (isTable) {
          dispatch(hcrActions.hcrUpdateCanvasTabComponent({
            id: compId,
            actionType: "clearSelection",
          }))
        } else {
          dispatch(hcrActions.hcrUpdateCrosstabComponent({
            id: compId,
            actionType: "clearSelection",
          }))
        }
        return;
        break;
      }
      default:
        break;
    }
    if (!isEmpty(payload)) {
      if (payload.actionType === "selectCalculation") {
        if (isTable) {
          dispatch(hcrActions.hcrUpdateCanvasTabComponent(payload))
        } else {
          dispatch(hcrActions.hcrUpdateCrosstabComponent(payload))
        }

      } else {
        dispatch(hcrActions.hcrUpdateSubdataSets(payload))
      }
    }
  }
  return (
    <ContextMenu
      title={title}
      visible={visible}
      onVisibleChange={onVisibleChange}
      handleMenuClick={handleMenuClick}
      menu={menu}
    />
  )
}

const OutlineTableContextMenu = (props = {}) => {
  const {
    onVisibleChange = () => { },
    visible,
    title = null,
    menuType,
    bandType,
    columnId,
    tableData = {},
    cellId,
    nodeId,
    deleted,
    children = [],
    copiedNodes = [],
    cb = () => { },
    ...rest
  } = props || {}
  const dispatch = useDispatch();
  const { id: tableId, columnOrder = [], cells = {}, nodes = {}, selectedNodes = [] } = tableData || {}
  if (!tableId) return null;
  const deleteRowOption = children.length ? children.every((child) => child.deleted) : false;
  const menu = getHCRTableContextMenu({
    menuType,
    bandType,
    cell_deleted: deleted,
    row_deleted: deleteRowOption,
    copiedNodes
  });
  const updateTable = (actionType, payload) => {
    dispatch(hcrActions.hcrUpdateCanvasTabComponent({
      actionType,
      id: tableId,
      ...payload,
    }))
  }

  const selectCell = () => {
    if (cellId) {
      dispatch(hcrActions.hcrUpdateCanvasTabComponent({
        actionType: "selectCells",
        id: tableId,
        selectedCells: [cellId]
      }))
    }
    return;
  }

  const handleMenuClick = ({ key, domEvent: e }) => {
    e.stopPropagation();

    switch (key) {
      case "create_col_at_beginning": {
        updateTable("addColumn", { columnIndex: 0, width: HCR_TABLE_DATA_CELL_WIDTH })
        break;
      }
      case "create_col_at_end": {
        updateTable("addColumn", { columnIndex: columnOrder.length, width: HCR_TABLE_DATA_CELL_WIDTH })
        break;
      }
      case "create_col_after": {
        const colIndex = columnOrder.findIndex((col) => col === columnId);
        updateTable("addColumn", { columnIndex: colIndex > -1 ? colIndex + 1 : 0, width: HCR_TABLE_DATA_CELL_WIDTH })
        break;
      }
      case "create_col_before": {
        const colIndex = columnOrder.findIndex((col) => col === columnId);
        updateTable("addColumn", { columnIndex: colIndex > 0 ? colIndex : 0, width: HCR_TABLE_DATA_CELL_WIDTH })
        break;
      }
      case "delete_column": {
        const colIndex = columnOrder.findIndex((col) => col === columnId);
        updateTable("removeColumn", { columnIndex: colIndex, columnId });
        break;
      }
      case "delete_cell": {
        updateTable("removeCell", { cellId, bandType })
        break;
      }
      case "create_cell": {
        updateTable("createCell", { cellId, bandType })
        break;
      }
      case "delete_row": {
        let cellsToDelete = []
        for (let cell in cells) {
          if (cell.includes(bandType)) cellsToDelete.push(cell)
        }
        updateTable("removeRow", { cellIds: cellsToDelete, bandType })
        break;
      }
      case "create_row": {
        let cellsToAdd = []
        for (let cell in cells) {
          if (cell.includes(bandType)) cellsToAdd.push(cell)
        }
        updateTable("createRow", { cellIds: cellsToAdd, bandType })
        break;
      }
      case "select_all_cells": {
        const cellIds = Object.keys(cells) || []
        if (cellIds.length) {
          updateTable("selectCells", { selectedCells: cellIds })
        }
        break;
      }
      case "select_all_nodes": {
        let nodeIds = Object.keys(nodes) || []
        if (nodeIds.length) {
          updateTable("selectNodes", { selectedNodes: nodeIds })
        }
        break;
      }
      case "undo": {
        dispatch(hcrUndo())
        break;
      }
      case "redo": {
        dispatch(hcrRedo())
        break;
      }
      case "cut_node": {
        if (selectedNodes.length) {
          let cNodes = selectedNodes.reduce((acc, next) => {
            acc.push(nodes[next]);
            return acc;
          }, [])
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id: tableId,
            type: "cut",
            nodes: cNodes
          }))
          updateTable("cutNodes", { cutNodesData: cNodes })
        } else {
          let node = nodes[nodeId]
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id: tableId,
            type: "cut",
            nodes: [node]
          }))
          updateTable("cutNodes", { cutNodesData: [node] })
        }
        break;
      }
      case "copy_node": {
        if (selectedNodes.length) {
          let cNodes = selectedNodes.reduce((acc, next) => {
            acc.push(nodes[next]);
            return acc;
          }, [])
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id: tableId,
            type: "copy",
            nodes: cNodes
          }))
        } else {
          let node = nodes[nodeId]
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id: tableId,
            type: "copy",
            nodes: [node]
          }))
        }
        break;
      }
      case "delete_node": {
        updateTable("deleteNode", { nodeId })
        selectCell()
        break;
      }
      case "paste_node": {
        selectCell()
        updateTable("pasteCopiedNodes", { copiedNodes })
        break;
      }
      default:
        break;
    }
    cb()
  }

  return (
    <ContextMenu
      title={title}
      visible={visible}
      onVisibleChange={onVisibleChange}
      handleMenuClick={handleMenuClick}
      menu={menu}
    />
  )

}

const OutlineCrosstabContextMenu = (props = {}) => {
  const {
    title = null,
    visible,
    menuType,
    onVisibleChange = () => { },
    cellId,
    nodeId,
    cb = () => { },
    crosstabData = {},
    copiedNodes = []
  } = props || {}
  const dispatch = useDispatch();
  const { id, selectedNodes = [], config: { nodes = {} } = {} } = crosstabData || {}
  const menu = getHCRCrosstabContextMenu({ menuType, copiedNodes })
  const updateCrosstab = (actionType, payload) => {
    dispatch(hcrActions.hcrUpdateCrosstabComponent({
      id,
      actionType,
      ...payload
    }))
  }

  const selectCell = () => {
    if (cellId) {
      dispatch(hcrActions.hcrUpdateCrosstabComponent({
        actionType: "selectCells",
        id,
        selectedCells: [cellId]
      }))
    }
    return;
  }

  const handleMenuClick = ({ key, domEvent: e }) => {
    e.stopPropagation()

    switch (key) {
      case "undo": {
        dispatch(hcrUndo())
        break;
      }
      case "redo": {
        dispatch(hcrRedo())
        break;
      }
      case "cut_node": {
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
          updateCrosstab("cutNodes", { cutNodesData: cNodes })
        } else {
          let node = nodes[nodeId]
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id,
            type: "cut",
            nodes: [node]
          }))
          updateCrosstab("cutNodes", { cutNodesData: [node] })
        }
        break;
      }
      case "copy_node": {
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
        } else {
          let node = nodes[nodeId]
          dispatch(hcrActions.hcrUpdateTableClipboard({
            id,
            type: "copy",
            nodes: [node]
          }))
        }
        break;
      }
      case "delete_node": {
        updateCrosstab("deleteNode", { nodeId })
        selectCell()
        break;
      }
      case "paste_node": {
        selectCell()
        updateCrosstab("pasteCopiedNodes", { copiedNodes })
        break;
      }
      default:
        break;
    }
  }

  return (
    <ContextMenu
      title={title}
      visible={visible}
      onVisibleChange={onVisibleChange}
      handleMenuClick={handleMenuClick}
      menu={menu}
    />
  )
}

export {
  OutlineDSContextMenu,
  OutlineTableContextMenu,
  OutlineCrosstabContextMenu
};
