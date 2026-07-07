import { Tabs } from "antd";
import React, { useState } from "react";
import { Responsive } from "react-grid-layout";
import { useDispatch } from "react-redux";
import { withSize } from "react-sizeme";
import {
  updateGridItemId,
  updateNestedGridLayout,
  updateTabGridItemsLayout,
} from "../../../redux/actions/dashboard-designer.actions";
import { getDashboardConfig } from "../utils/get-dashboard-config";
import { SizeAwareHIGridItem } from "./hi-grid-item";

const withSizeHOC = withSize({ monitorHeight: true });
const ResponsiveGridLayout = withSizeHOC(Responsive);
const { TabPane } = Tabs;
const getTabNamesAsArray = (tabNames, numberOfTabs) => {
  const tabNamesArr = tabNames.split(",").map((name) => name.trim());
  const result = [];

  for (let i = 0; i < numberOfTabs; i++) {
    result.push(tabNamesArr[i] || `Tab ${i + 1}`);
  }

  return result;
};

const TabItems = ({ items, nestedGridProps, size, element, tab, layouts }) => {
  const { id } = element || {};
  const { layout, tabId } = tab || {}
  const dispatch = useDispatch();
  return (
    <ResponsiveGridLayout
      onDragStop={(layout) => {
        dispatch(updateNestedGridLayout({ layout, id }));
        dispatch(updateTabGridItemsLayout({ layout, tabId }))
      }}
      onResizeStop={(layout) => {
        dispatch(updateNestedGridLayout({ layout, id }));
        dispatch(updateTabGridItemsLayout({ layout, tabId }))
      }}
      layouts={{
        lg: layout,
        md: layout,
        sm: layout,
        xs: layout,
        xxs: layout
      }}
      width={size?.width ? size?.width : null}
      className="hi-nested-grid"
      {...nestedGridProps}
    >
      {items.map((ele) => {
        const [shadowStyles, borderStyles] = getDashboardConfig(items, ele.id, [
          "shadow",
          "border",
        ]);
        const gridItemStyles = {
          ...borderStyles,
          ...shadowStyles,
        };
        return (
          <div
            className="grid-item"
            data-grid={ele.initialPosition}
            key={ele.id}
            id={`${ele.id}`}
            style={gridItemStyles}
          >
            <div
              onContextMenu={(e) => {
                dispatch(updateGridItemId(ele.id));
                e.stopPropagation();
              }}
              className="hi-grid-item"
            >
              <SizeAwareHIGridItem key={ele.id} element={ele} isTabChild={true} tabChildLayout={layouts.find(l => l.i === ele.id)} />
            </div>
          </div>
        );
      })}
    </ResponsiveGridLayout>
  );
};

function getItems(gridItem, gridItemsData, nestedGridData) {
  const items = [];
  gridItem.tabsInfo.map((tab) => {
    const tabItems = gridItem.children.filter(item => tab.item.includes(item.id))
    items.push(
      <TabPane key={tab.name} tab={tab.name}>
        <TabItems items={tabItems} {...nestedGridData} {...{ gridItemsData, tab, layouts: tab.layout }} />
      </TabPane>
    );
  });

  return items;
}

const TabComponent = ({
  gridItem,
  gridItemsData,
  nestedGridProps = {},
  size,
  element,
}) => {
  const editValues = gridItem.gridItemConfig.find(
    (item) => item.key === "edit"
  )?.values;

  const tabNamesArray = getTabNamesAsArray(
    editValues?.tabNames,
    editValues?.numberOfTabs
  );

  let activeTabToSet = tabNamesArray.includes(editValues?.activeTab)
    ? editValues?.activeTab
    : tabNamesArray[0];

  const [activeTab, setActiveTab] = useState(activeTabToSet);

  const onChange = (key) => {
    setActiveTab(key);
  };
  return (
    <Tabs
      defaultActiveKey={activeTab}
      activeKey={activeTab}
      onChange={onChange}
      centered={editValues?.centered}
      type={editValues?.tabType}
    >
      {getItems(gridItem, gridItemsData, {
        nestedGridProps,
        size,
        element,
        activeTab
      })}
    </Tabs>
  );
};
export default TabComponent;
