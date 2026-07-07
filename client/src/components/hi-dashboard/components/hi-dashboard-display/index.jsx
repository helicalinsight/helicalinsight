import React, { useEffect, useRef } from "react";
import { useDispatch } from "react-redux";
import { refreshDashboard, updateDashboardVariables } from "../../../../redux/actions/dashboard.actions";
import {
  storeFiltersList,
  storeLastModified
} from "../../../../redux/actions/dashboard-designer.actions";
import { HelicalReports, DashboardDesigner } from "../../../../pages";

const DashboardDisplay = (props) => {
  const {
    listeners,
    refreshReports,
    reportInfo,
    id,
    mode,
    parameter,
    dashboardVariables,
  } = props;

  const componentMap = {
    hr: HelicalReports,
    efw: DashboardDesigner,
  };
  const dispatch = useDispatch(refreshReports);
  const prevRefresh = useRef(refreshReports);
  useEffect(() => {
    prevRefresh.current = refreshReports;
  }, [refreshReports]);
  const ViewComponent = componentMap[reportInfo?.extension];

  const getReportInfo = ({ filtersList, reportId }) => {
    dispatch(storeFiltersList({ filtersList, designerItemId: id, reportId }));
  };

  const getLastModified = (lastModified) => {
    dispatch(storeLastModified({ lastModified, designerItemId: id }));
  };

  const addFilter = (data) => {
    if (typeof props.addFilter === "function") {
      props.addFilter(data);
    }
  };

  const addForwardFilterIPC = (data) => {
    if (typeof props.addForwardFilterIPC === "function") {
      props.addForwardFilterIPC(data);
    }
  };

  const deleteBackwardFilterIPC = (data) => {
    if (typeof props.deleteBackwardFilterIPC === "function") {
      props.deleteBackwardFilterIPC(data);
    }
  };

  const refreshFiltersIPC = (data) => {
    if (typeof props.refreshFiltersIPC === "function") {
      props.refreshFiltersIPC(data);
    }
  };

  const onChange = (value) => {
    dispatch(
      updateDashboardVariables({
        value,
        key: parameter?.dashboardFilter?.columnName,
      })
    );
  };

  let dashboardFilter = { ...parameter?.dashboardFilter };

  let parameters = {};

  // bug 4433 - start
  listeners?.forEach((item) => {
    if (dashboardVariables[item]) {
      parameters[item] = dashboardVariables[item];
    }
  });
  const allFilters = parameter?.dashboardFilter ? dashboardVariables : null
  // bug 4433 - end

  const handleRefreshDashboard = () => { // added for 7720
    dispatch(refreshDashboard())
  }

  // ------- 7771 -----------
  let isFilterComponent = false;
  let filterComponents = [];
  dispatch((_, getState, services) => {
    filterComponents = getState().designer.present.gridItemsData.filter(
      (item) => ["filter-component", "filter"].includes(item.compType)
    );
  });
  let filterComp = filterComponents?.find((item) => item?.id === id) ?? null;
  if (filterComp && filterComp?.filterCompId) {
    isFilterComponent = true;
  }
  // ------- 7771 -----------

  return (
    <div className="height100percent">
      {reportInfo && (
        <ViewComponent
          data-testid="hi-dashboard-display"
          file={reportInfo.file}
          extension={reportInfo.extension}
          parameters={parameters}
          mode={mode ? mode : reportInfo.mode}
          getReportInfo={getReportInfo}
          addFilter={addFilter}
          addForwardFilterIPC={addForwardFilterIPC}
          deleteBackwardFilterIPC={deleteBackwardFilterIPC}
          refreshFiltersIPC={refreshFiltersIPC}
          cacheRefresh={refreshReports !== prevRefresh.current}
          cacheRefreshCount={refreshReports}
          onChange={onChange}
          dashboardFilter={dashboardFilter}
          allFilters={allFilters}
          getLastModfified={getLastModified}
          refreshDashboard={handleRefreshDashboard}
          isFilterComponent={isFilterComponent}
        />
      )}
    </div>
  );
};

export { DashboardDisplay };
