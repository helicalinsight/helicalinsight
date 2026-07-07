import { Select } from "antd";
import React, { useMemo } from "react";
import { errorHandlingLiquidJs } from "../../hi-reports/hi-editing-area/components/editor/css-editor";
import { getGlobalVariables } from "../../../utils/utilities";
import { getDashboardVariableScope } from "../utils/common-functions";
import { useDispatch, useSelector } from "react-redux";
import { executeScript } from "../../hi-reports/utils/utilities";
import { addToTab, removeItemsOpenThroughApi, updateDropdown } from "../../../redux/actions/dashboard-designer.actions";
import { openInDashboardCallback } from "../utils/dashboard-requests";
import { updateDashboardVariables } from "../../../redux/actions";

export const getDropDownApis = (gridItemsData, id, dispatch) => {
  const apis = {
    open_report: (path, opts = {}) => {

      // Extract name and title from path
      const name = path.split("/").pop()
      const title = name.split(".")[0]

      const {
        compId,   // id of the component
        tabIdx = 0,   // only relevant is compType is 'tab'
        layout: userDefinedLayout
      } = opts;

      // Get the component where to add the report
      const comp = gridItemsData?.find(item => item.id === compId);

      // Remove previously added reports
      if (id) dispatch(removeItemsOpenThroughApi({ id }));

        // open report in the dashboard
        const reportId = openInDashboardCallback({
          path,
          name,
          title,
          dispatch,
          gridItemsData,
          openThroughApi: true,
          gridItemId: id,
          userDefinedLayout
        });

        if (comp && comp.compType === 'tab') {
          dispatch(addToTab({
            tabItemId: comp.id,
            tabId: `${comp.id}-${tabIdx}`,
            item: reportId,
            openThroughApi: true,
            openThrough: id,
            userDefinedLayout
          }))
        }
    }
  };

  return apis;
}

const getDropdownOptions = (dropdownValues) => {
  const valuesArr = dropdownValues.split(",").map((name) => name.trim());
  const result = [];

  for (let value of valuesArr) {
    result.push({ value: value, label: value });
  }

  return result;
};

const DropdownComponent = ({ editValues, gridItem }) => {
  const gridItemsData = useSelector(
    (state) => state.designer.present.gridItemsData
  );
  const jsData = useMemo(
    () => errorHandlingLiquidJs({ value: editValues?.jsValue || "" }),
    [editValues]
  );
  const dispatch = useDispatch();
  let dashboardVariables = {};
  dispatch((dispatch, getState) => {
    dashboardVariables = getState().dashboard.present.dashboardVariables;
  });

  const dropdownApis = getDropDownApis(gridItemsData, gridItem.id, dispatch);

  const globalVariables = getGlobalVariables({ dispatch });

  const handleChange = (value) => {
    dispatch(updateDropdown({ id: gridItem.id, value: value }));
    dispatch((dispatch, getState) => {
      let selectedDropdownValues = getState().dashboard.present.dashboardVariables?.selected_dropdown_values || {};
      const updated = { key: "selected_dropdown_values", value: { ...selectedDropdownValues, [gridItem.id]: value } };
      dispatch(updateDashboardVariables(updated))
    });
    let scope = getDashboardVariableScope(globalVariables, dispatch);
    scope = { value, ...scope, ...scope.getAllVariables(), ...dropdownApis };
    if (!jsData) return;
    executeScript({ ...scope }, jsData, dispatch);
  };

  let selectedValue = dashboardVariables[gridItem?.id] || gridItem.selectedValue;

  return (
    <Select
      style={{ marginLeft: "20px", width: "93%" }}
      showSearch
      placeholder="Select"
      optionFilterProp="children"
      onChange={handleChange}
      filterOption={(input, option) =>
        (option?.label ?? "").toLowerCase().includes(input.toLowerCase())
      }
      options={getDropdownOptions(editValues.dropdownValues)}
      bordered={editValues.bordered}
      size={editValues.dropdownSize}
      placement={editValues.placement}
      allowClear={editValues.allowClear}
      defaultValue={selectedValue}
    />
  );
};
export default DropdownComponent;
