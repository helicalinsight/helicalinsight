import { useEffect, useRef, useState } from "react";
import { Drawer, Space, Button, Tooltip } from "antd";
import { HIPropertyPane } from "../../../common";
import { useSelector, useDispatch } from "react-redux";
import {
  LayoutOutlined,
  ShrinkOutlined,
  ArrowsAltOutlined,
} from "@ant-design/icons";
import {
  changeDrawerPosition,
  updateGridSettings,
  designerDrawerExpansion,
} from "../../../../redux/actions/dashboard-designer.actions";
import { toggleDashboardDrawer } from "../../../../redux/actions/dashboard-designer.actions";
import { getPropertyPaneItemsConfig } from "../../utils/get-property-pane-items-config";
import {
  getGroup,
  getInfoItems,
  getInfoPanelItems,
} from "../../utils/constants";
import "./index.scss";
import { appActions } from "../../../../redux/actions";
import { fallsInBreakpoint, getComponentHeight, getComponentWidth, handleDashboardDesignerKeyPress } from "../../utils/common-functions";
import notify from "../../../hi-notifications/notify";

const HIDashboardDrawer = (designerProps = {}) => {
  const { setRefresh } = designerProps
  // variables
  const applyRef = useRef(null)
  const resetRef = useRef(null)
  const searchRef = useRef(null)
  const dispatch = useDispatch();
  const dashboardDrawerStatus = useSelector(
    (state) => state.designer.present.dashboardDrawerStatus
  );
  const gridSettingsData = useSelector(
    (state) => state.designer.present.gridSettings
  );
  const designerSettingsData = useSelector(
    (state) => state.designer.present.designerSettings
  );
  const groupId = useSelector((state) => state.designer.present.groupId);
  const colsValues = gridSettingsData.find(
    (item) => item.key === "columns"
  ).values;
  const breakpointsValues = gridSettingsData.find(
    (item) => item.key === "breakpoints"
  ).values;
  const expandDrawer = useSelector(
    (state) => state.designer.present.expandDesignerDrawers
  );
  const keysPressed = useSelector((state) => state.app.keysPressed);
  const altTriggered = useSelector((state) => state.app.altTriggered);
  let group = getGroup({ type: "grid", dispatch });
  const infoItems = getInfoItems({ type: "grid" });
  const columnsInfos = infoItems.find(i => i.key === "columns");
  const breakpointsInfos = infoItems.find(i => i.key === "breakpoints");

  if(columnsInfos && breakpointsInfos){
    const width = getComponentWidth("hi-dashboard", 217);
    const height = getComponentHeight("hi-dashboard", 210);
    const heightWidth = `The grid currently has a width of ${width} pixels and a height of ${height} pixels. The asterisk (*) indicates the present screen size.`
    columnsInfos.infos.push(heightWidth);
    breakpointsInfos.infos.push(heightWidth);
  }
  const infoPanelItems = getInfoPanelItems({ type: "grid" });
  const [
    headerItems,
    shadowItems,
    backgroundItems,
    borderItems,
    htmlItems,
    cssItems,
    jsItems,
    gridItems,
  ] = getPropertyPaneItemsConfig({
    array: gridSettingsData,
    type: [
      "header",
      "shadow",
      "background",
      "border",
      "html",
      "css",
      "javascript",
      "grid",
      "columns",
      "breakpoints"
    ],
  });
  const [parameterItems] = getPropertyPaneItemsConfig({
    array: designerSettingsData,
    type: ["parameters"],
  });
  let items = [
    ...breakpointsValues.map((item) => ({
      key: item.key,
      label: item.name,
      tooltip: item.tooltip,
      value: item?.value ? item.value : 2,
      putAsterisk: fallsInBreakpoint(breakpointsValues, getComponentWidth("hi-dashboard", 217)) === item.key,
      elementType: "InputNumber",
      groupId: "breakpoints",
    })),
    ...colsValues.map((item) => ({
      key: item.key,
      label: item.name,
      tooltip: item.tooltip,
      value: item?.value ? item.value : 2,
      putAsterisk: fallsInBreakpoint(breakpointsValues, getComponentWidth("hi-dashboard", 217)) === item.key,
      elementType: "InputNumber",
      groupId: "columns",
    })),
    ...headerItems,
    ...shadowItems,
    ...backgroundItems,
    ...borderItems,
    ...htmlItems,
    ...cssItems,
    ...jsItems,
    ...parameterItems,
    ...gridItems,
  ];
  const drawerPositions = useSelector(
    (state) => state.designer.present.drawerPositions
  );
  const currentDrawerPosition = useSelector(
    (state) => state.designer.present.currentDrawerPosition
  );
  const visible = useSelector(
    (state) => state.designer.present.dashboardDrawerStatus
  );

  // functions

  const onClose = () => {
    dispatch(toggleDashboardDrawer(false));
  };

  // const getData = ({ value, key, groupId }) => {
  //   console.log({ value, key, groupId });
  //   setItemsData((prevItems) => {
  //     return prevItems.map((item) => {
  //       if (item.groupId === groupId && item.key === key) {
  //         item.value = value;
  //       }
  //       return item;
  //     });
  //   });
  //   // if (groupId === "parameters") {
  //   //   dispatch(updateDesignerSettings({ key, value, groupId }));
  //   // } else {
  //   //   dispatch(updateGridSettings({ key, value, groupId }));
  //   // }
  // };
  // shortcut logic
  const currentSCLocation = useSelector((state) => state.app.currentSCLocation);



  useEffect(() => {
    handleDashboardDesignerKeyPress({
      dispatch,
      keysPressed,
      altTriggered,
      currentSCLocation,
      setRefresh,
      propertyPaneRefs: { applyRef, resetRef, searchRef }
    });
  }, [keysPressed]);

  const getDataOnApply = (itemsData) => {
    dispatch(updateGridSettings(itemsData));
  };

  const handleCodeEditorsReset = ({ groupId, itemsRef, itemsData }) => {
    // code for throwing residue warning
    // reset issue
    const condition = !itemsRef?.current.find(item => item.groupId === groupId && item.key === "enable")?.value

    const codeEditorsCondition = (groupId === "html" || groupId === "css" || groupId === "javascript") && condition
    if (codeEditorsCondition) {
      notify(dispatch).warning({ message: "You have clicked reset but there might be some residue left. Please consider refreshing the page to ensure that all the changes have been reset properly." })
    }
  }



  //rendering

  return (
    <Drawer
      title={<span className="hi-drawer-title">Dashboard Settings</span>}
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
          infoItems={infoItems}
          groupId={groupId}
          group={group}
          items={items}
          onReset={handleCodeEditorsReset}
          getData={() => { }}
          showApply={true}
          getDataOnApply={getDataOnApply}
          config={{ dashboardDrawerStatus }}
          keysPressed={keysPressed}
          altTriggered={altTriggered}
          shortCutConfig={{
            apply: {
              ref: applyRef,
              scLocation: "DD",
              text: "A"
            },
            reset: {
              ref: resetRef,
              scLocation: "DD",
              text: "R"
            },
            search: {
              ref: searchRef,
              scLocation: "DD",
              text: "Q"
            }
          }}
        />
      )}
    </Drawer>
  );
};
export { HIDashboardDrawer };
