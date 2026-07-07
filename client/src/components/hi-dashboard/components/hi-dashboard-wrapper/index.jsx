import React from "react";
import { useDispatch } from "react-redux";
import { Input } from "antd";
import { setDashboardVariables } from "../../../../redux/actions/dashboard.actions";
import { isEqual } from "lodash-es";
import { DashboardDisplay } from "../hi-dashboard-display";

const areEqual = (prevProps, nextProps) => {
  if(nextProps.reportInfo?.mode === "filter"){
    if(!isEqual(prevProps.dashboardVariables,nextProps.dashboardVariables)){
      return false
    }else{
      return true
    }
  }
  if (nextProps.enableApplyButton) {
    if (
      prevProps.applyDashboardFilters === nextProps.applyDashboardFilters &&
      prevProps.refreshReports === nextProps.refreshReports
    ) {
      return true;
    }
  }
  if (isEqual(prevProps.listeners, nextProps.listeners)) {
    let prevParameters = {};
    nextProps?.listeners?.forEach(
      (item) => (prevParameters[item] = prevProps.dashboardVariables[item])
    );
    let nextParameters = {};
    nextProps?.listeners?.forEach(
      (item) => (nextParameters[item] = nextProps.dashboardVariables[item])
    );
    return (
      isEqual(prevParameters, nextParameters) &&
      prevProps.refreshReports === nextProps.refreshReports
    );
  }
};

const DashboardInput = (props) => {
  const { parameter } = props;
  const dispatch = useDispatch();
  return (
    <Input
      onChange={(e) => {
        dispatch(setDashboardVariables({ parameter, value: e.target.value }));
      }}
    />
  );
};

const HIDashboardWrapper = React.memo((props) => {
  const {
    type,
    parameter,
    listeners,
    reportInfo,
    refreshReports,
    id,
    dashboardVariables,
    reportId,
  } = props;
  const addFilter = (data) => {
    props.addFilter(data);
  };

  const addForwardFilterIPC = (data) => {
    props.addForwardFilterIPC(data);
  };

  const deleteBackwardFilterIPC = (data) => {
    props.deleteBackwardFilterIPC(data);
  };

  const refreshFiltersIPC = (data) => {
    props.refreshFiltersIPC(data);
  };

  const renderComponent = () => {
    switch (type) {
      case "hr":
        return (
          <DashboardDisplay
          data-testid = "hi-dashboard-wrapper"
            parameter={parameter}
            listeners={listeners}
            reportInfo={reportInfo}
            id={id}
            reportId={reportId}
            addForwardFilterIPC={addForwardFilterIPC}
            deleteBackwardFilterIPC={deleteBackwardFilterIPC}
            refreshFiltersIPC={refreshFiltersIPC}
            dashboardVariables={dashboardVariables}
            refreshReports={refreshReports}
            addFilter={addFilter}
          />
        );
      case "input":
        return <DashboardInput parameter={parameter} />;
      default:
        return <div />;
    }
  };
  return <>{renderComponent()}</>;
}, areEqual);

export { HIDashboardWrapper };
