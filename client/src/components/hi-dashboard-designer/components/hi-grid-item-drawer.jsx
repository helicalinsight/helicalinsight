import { Drawer, Space, Button, Tooltip } from "antd";
import { HIPropertyPane } from "../../common";
import { useSelector, useDispatch } from "react-redux";
import {
  LayoutOutlined,
  ShrinkOutlined,
  ArrowsAltOutlined,
} from "@ant-design/icons";
import {
  changeDrawerPosition,
  toggleGridItemDrawer,
  updateGridItemStyles,
  designerDrawerExpansion,
  updateTabGridItemsLayoutFromProperties,
} from "../../../redux/actions/dashboard-designer.actions";
import { getPropertyPaneItemsConfig } from "../utils/get-property-pane-items-config";
import {
  checkIfTabChild,
  getGridItem,
  getGridItemLayoutObject,
} from "../utils/recursive-functions";
import { getGroup, getInfoItems, getInfoPanelItems } from "../utils/constants";
import "./hi-dashboard-drawer/index.scss";
import { useEffect, useRef } from "react";
import { getComponentHeight, getComponentWidth, handleDashboardDesignerKeyPress,  } from "../utils/common-functions";
import notify from "../../hi-notifications/notify";

const HIGridItemDrawer = (desginerProps = {}) => {
  const { setRefresh } = desginerProps  
  // variables
  const applyRef = useRef(null)
  const resetRef = useRef(null)
  const searchRef = useRef(null)
  const gridItemDrawerStatus = useSelector(
    (state) => state.designer.present.gridItemDrawerStatus
  );
  const drawerPositions = useSelector(
    (state) => state.designer.present.drawerPositions
  );
  const currentDrawerPosition = useSelector(
    (state) => state.designer.present.currentDrawerPosition
  );
  const expandDrawer = useSelector(
    (state) => state.designer.present.expandDesignerDrawers
  );
  const visible = useSelector(
    (state) => state.designer.present.gridItemDrawerStatus
  );
  const dispatch = useDispatch();
  const gridItemsData = useSelector(
    (state) => state.designer.present.gridItemsData
  );
  const groupId = useSelector((state) => state.designer.present.groupId);
  const gridItemId = useSelector((state) => state.designer.present.gridItemId);
  const layout = useSelector((state) => state.designer.present.layout);
  const gridItemData = getGridItem(gridItemsData, gridItemId);
  const gridItemlayoutObj = getGridItemLayoutObject({
    data: gridItemsData,
    id: gridItemId,
    layout: layout,
  });
  const [isTabChild, tabChildLayout] = checkIfTabChild(gridItemData, gridItemsData);
  const { compType } = gridItemData || {};
  let group = getGroup({ type: "gridItem", compType });
  const infoItems = getInfoItems({ type: ["tab", "grouped-component"].includes(compType) ? "grid" : "gridItem", compType });

  const columnsInfos = infoItems.find(i => i.key === "columns");
  const breakpointsInfos = infoItems.find(i => i.key === "breakpoints");

  if(columnsInfos && breakpointsInfos){
    const width = getComponentWidth(gridItemId, 217);
    const height = getComponentHeight(gridItemId, 210);
    const heightWidth = `The grid currently has a width of ${width} pixels and a height of ${height} pixels. The asterisk (*) indicates the present screen size.`
    columnsInfos.infos.push(heightWidth);
    breakpointsInfos.infos.push(heightWidth);
  }

  const infoPanelItems = getInfoPanelItems({ type: "gridItem", gridItemId });
  const [
    headerItems,
    shadowItems,
    backgroundItems,
    borderItems,
    htmlItems,
    cssItems,
    jsItems,
    settingsItems,
    alignmentItems,
    textItems,
    imageItems,
    tabItems,
    gridItems,
    columnValues,
    breakpointValues,
    dropdownItems,
  ] = getPropertyPaneItemsConfig({
    array: gridItemsData,
    id: gridItemId,
    compType,
    type: [
      "header",
      "shadow",
      "background",
      "border",
      "html",
      "css",
      "javascript",
      "griditemsettings",
      "alignment",
      "text",
      "image",
      "tab",
      "grid",
      "columns",
      "breakpoints",
      "select-dropdown"
    ],
    getGridItemLayoutObj: isTabChild && tabChildLayout ? tabChildLayout : gridItemlayoutObj,
  });
  let items = [
    ...headerItems,
    ...shadowItems,
    ...backgroundItems,
    ...borderItems,
    ...htmlItems,
    ...cssItems,
    ...jsItems,
    ...settingsItems,
    ...alignmentItems,
    ...(compType === "text"
      ? textItems
      : compType === "image"
      ? imageItems
      : compType === "tab"
      ? [...tabItems, ...gridItems, ...columnValues, ...breakpointValues]
      : compType === "grouped-component"
      ? [...gridItems, ...columnValues, ...breakpointValues]
      : compType === "select-dropdown"
      ? [...dropdownItems,...gridItems, ...columnValues, ...breakpointValues]
      : []),
  ];

  // functions

  const onClose = () => {
    dispatch(toggleGridItemDrawer(false));
  };

  // const getData = ({ value, key, groupId }) => {
  //   dispatch(updateGridItemStyles({ key, value, groupId, gridItemId }));
  // };

  const getDataOnApply = (itemsData) => {
    dispatch(updateGridItemStyles({ itemsData, gridItemId }));
    dispatch(updateTabGridItemsLayoutFromProperties({ itemsData, gridItemId }))
  };

  const handleCodeEditorsReset=({groupId,itemsRef,itemsData})=>{
    // code for throwing residue warning
    
        const condition=!itemsRef?.current.find(item=>item.groupId===groupId && item.key==="enable").value
    
    const codeEditorsCondition=(groupId==="html" || groupId==="css" ||groupId==="javascript")  && condition
    if(codeEditorsCondition){
      notify(dispatch).warning({message:"You have clicked reset but there might be some residue left. Please consider refreshing the page to ensure that all the changes have been reset properly."})
    }
      }

  const getHeader = () => {
    switch (gridItemData?.compType) {
      case "dashboard-designer-component":
        return gridItemData?.reportInfo?.file?.title;
      case "text":
        return "Text";
      case "image":
        return "Image";
      case "tab":
        return "Tab";
      case "grouped-component":
        return "Group";
      case "select-dropdown":
        return "Dropdown";
      case "html-component":
        return "HTML";
      default:
        return groupId[0]?.toUpperCase() + groupId?.slice(1);
    }
  };


  const keysPressed = useSelector((state) => state.app.keysPressed);
  const altTriggered = useSelector((state) => state.app.altTriggered);
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);



  useEffect(() => {
    handleDashboardDesignerKeyPress({
      dispatch,
      keysPressed,
      altTriggered,
      currentSCLocation,
      setRefresh,
      propertyPaneRefs:{applyRef,resetRef,searchRef}
    });
  }, [keysPressed]);
  

  // rendering

  return (
    <Drawer
      title={<span className="hi-drawer-title">{`${getHeader()}`}</span>}
      placement={drawerPositions[currentDrawerPosition]}
      mask
      width={expandDrawer ? "90%" : "35%"}
      className="hi-dashboard-drawer"
      onClose={onClose}
      visible={visible}
      extra={
        <Space>
          <Tooltip title="Change layout">
            <Button
              icon={<LayoutOutlined />}
              className="hi-icon"
              type="text"
              onClick={() => {
                dispatch(changeDrawerPosition());
              }}
            />
          </Tooltip>
          {expandDrawer ? (
            <Tooltip title="Shrink drawer">
              <Button
                icon={<ShrinkOutlined />}
                className="hi-icon"
                type="text"
                onClick={() => {
                  dispatch(designerDrawerExpansion());
                }}
              />
            </Tooltip>
          ) : (
            <Tooltip title="Expand drawer">
              <Button
                icon={<ArrowsAltOutlined />}
                className="hi-icon"
                type="text"
                onClick={() => {
                  dispatch(designerDrawerExpansion());
                }}
              />
            </Tooltip>
          )}
        </Space>
      }
    >
      {visible && (
        <HIPropertyPane
          infoPanelItems={infoPanelItems}
          maskClosable={true}
          infoItems={infoItems}
          groupId={groupId}
          group={group}
          items={items}
          getData={() => {}}
          onReset={handleCodeEditorsReset}
          showApply={true}
          newKey={gridItemId}
          getDataOnApply={getDataOnApply}
          config={{
            gridItemDrawerStatus,
            // containerWidth: expandDrawer ? "90%" : "30%",
          }}
          shortCutConfig={{
            apply: {
              ref: applyRef,
              scLocation: "DD",
              text:"A"
            },
            reset: {
              ref: resetRef,
              scLocation: "DD",
              text:"R"
            },
            search: {
              ref: searchRef,
              scLocation: "DD",
              text:"Q"
            }
          }}
        />
      )}
    </Drawer>
  );
};
export { HIGridItemDrawer };
