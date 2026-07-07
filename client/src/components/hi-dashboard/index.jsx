import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { updateDashboardVariables } from "../../redux/actions/dashboard.actions";
import {
  createFilter,
  deleteDilldownFilter,
  addDilldownFilter,
} from "../../redux/actions/hreport.actions";
import { getFieldDisplayName } from "../../utils/utilities";
import { HIDashboardWrapper } from "./components/hi-dashboard-wrapper";

// THIS IS A HOC FOR GRID ITEM IN DASHBOARD DESIGNER

const HIDashboard = (props) => {
  const { type, parameter, reportInfo, id, listeners, reportId } = props;
  const variables = useSelector((state) => state.dashboard.present.dashboardVariables);
  const refreshReports = useSelector((state) => state.dashboard.present.refreshReports);
  const applyDashboardFilters = useSelector(
    (state) => state.designer.present.applyDashboardFilters
  );
  const dispatch = useDispatch();

  const addForwardFilterIPC = (newFilter) => {
    let gridItemsData = [];
    dispatch((dispatch, getState, services) => {
      gridItemsData = getState().designer.present.gridItemsData.filter(
        (item) => item.compType === "dashboard-designer-component"
      );
    });
    const reportIds = [];
    gridItemsData.forEach((item) => {
      if (item.reportId) {
        reportIds.push(item.reportId);
      }
    });

    reportIds.forEach((reportId) => {
      dispatch(addDilldownFilter({ ...newFilter, reportId }));
    });
    // dispatch(
    //   updateDashboardVariables({
    //     key: getFieldDisplayName(data),
    //     value: data.values,
    //   })
    // );
  };

  const deleteBackwardFilterIPC = (data) => {
    let gridItemsData = [];
    dispatch((dispatch, getState, services) => {
      gridItemsData = getState().designer.present.gridItemsData.filter(
        (item) => item.compType === "dashboard-designer-component"
      );
    });
    const reportIds = [];
    gridItemsData.forEach((item) => {
      if (item.reportId) {
        reportIds.push(item.reportId);
      }
    });

    reportIds.forEach((reportId) => {
      dispatch(deleteDilldownFilter({ ...data, reportId }));
    });
    // dispatch(
    //   updateDashboardVariables({
    //     key: getFieldDisplayName(data),
    //     value: data.values,
    //   })
    // );
  };

  const getGridItems = (data) => {
    return data.map((item) => {
      if (item.children) {
        return getGridItems(item.children)
      }
      return item.compType === "dashboard-designer-component" ? item : null;
    }).flat(Infinity).filter(Boolean);
  }

  const refreshFiltersIPC = (data) => {
    let gridItemsData = [];
    dispatch((dispatch, getState, services) => {
      gridItemsData = getGridItems(getState().designer.present.gridItemsData);
      // gridItemsData = getState().designer.present.gridItemsData.filter(
      //   (item) => item.compType === "dashboard-designer-component"
      // );
    });
    const reportIds = [];
    gridItemsData.forEach((item) => {
      if (item.reportId) {
        reportIds.push(item.reportId);
      }
    });

    reportIds.forEach((reportId) => {
      dispatch(deleteDilldownFilter({ ...data, reportId }));
    });
    dispatch(
      updateDashboardVariables({
        key: getFieldDisplayName(data.filter),
        value: [],
      })
    );
  };

  const addFilter = (data) => {
    let gridItemsData = [];
    dispatch((dispatch, getState, services) => {
      gridItemsData = getGridItems(getState().designer.present.gridItemsData);
      // gridItemsData = getState().designer.present.gridItemsData.filter(
      //   (item) => item.compType === "dashboard-designer-component"
      // );
    });
    const reportIds = [];
    gridItemsData.forEach((item) => {
      if (item.reportId) {
        reportIds.push(item.reportId);
      }
    });

    reportIds.forEach((reportId) => {
      dispatch(createFilter({ ...data, reportId }));
    });
    dispatch(
      updateDashboardVariables({
        key: getFieldDisplayName(data),
        value: data.values,
      })
    );
  };
  const enableApplyButton = useSelector(
    (state) =>
      state.designer.present.designerSettings?.find((item) => item.key === "parameters")
        .values.enableApplyButton
  );
  return (
    <HIDashboardWrapper
     data-testid = "hi-dashboard"
      type={type}
      parameter={parameter}
      id={id}
      listeners={listeners}
      reportInfo={reportInfo}
      dashboardVariables={variables}
      refreshReports={refreshReports}
      applyDashboardFilters={applyDashboardFilters}
      enableApplyButton={enableApplyButton}
      reportId={reportId}
      addFilter={addFilter}
      addForwardFilterIPC={addForwardFilterIPC}
      deleteBackwardFilterIPC={deleteBackwardFilterIPC}
      refreshFiltersIPC={refreshFiltersIPC}
    />
  );
};
export { HIDashboard };
