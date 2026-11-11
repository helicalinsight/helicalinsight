import { Typography } from "antd";
import { useEffect, useRef, useState } from "react";
import {
  setFilterCounter,
  setPreviewMode,
  storeDashboardConfig,
  storeFiltersList,
} from "../../../../../redux/actions/dashboard-designer.actions";
import { HIDashboardDesigner } from "../../../../hi-dashboard-designer";
import { assembleInstantDashboardCreateConfig } from "../../../utils/assemble-instant-dashboard";
import { INSTANT_EDIT_MODE } from "../../../utils/save-instant-dashboard";
import "./ib-dashboard-view.scss";
import { useSelector } from "react-redux";
import { DASHBOARD_CREATE_TILE } from "../../../../bridges/dashboard/dashboard-bridge";

const { Text } = Typography;

const IBDashboardView = ({
  dashboardModel = {},
  steps = [],
  dispatch,
  activeReport = {},
  planMessageId = "",
  active = false,
  emptyLabel = "No dashboard tiles available.",
  questionsHistory = [],
  isOpenMode
} = {}) => {
  const refreshKeyRef = useRef(Date.now());
  const [startedFor, setStartedFor] = useState(null);
  const reports = useSelector((state) => state.hreport.present.reports || []);

  const items = Array.isArray(dashboardModel?.items) ? dashboardModel.items : [];
  const hasContent = items.length > 0;

  useEffect(() => {
    if (!active || typeof dispatch !== "function" || !hasContent) {
      setStartedFor(null);
      return undefined;
    }

    const config = assembleInstantDashboardCreateConfig({
      dashboardModel,
      steps,
      theme: dashboardModel.theme || {},
      questionsHistory,
      reports,
      activeReport
    });
    dispatch(storeDashboardConfig({ ...config, designerMode: INSTANT_EDIT_MODE }));
    dispatch(setPreviewMode(true));
    const { gridItemsData = [] } = config || {}
    gridItemsData.forEach((gridItem) => {
      if (gridItem.compType === DASHBOARD_CREATE_TILE) {
        const { reportInfo } = gridItem || {};
        const { hreportId = "" } = reportInfo || {};
        const report = reports.find((report) => report.id === hreportId);
        if (report && report?.filters.length) {
          dispatch(storeFiltersList({ filtersList: report.filters, designerItemId: gridItem.id, reportId: hreportId }));
        }
      }
      if (gridItem.compType === "filter-component") {
        dispatch(setFilterCounter(true))
      }
      return;
    })

    setStartedFor(planMessageId);
    return undefined;
  }, [active, planMessageId]);

  if (!hasContent) {
    return (
      <div className="ib-dashboard-view" data-testid="ib-dashboard-view">
        <Text type="secondary">{emptyLabel}</Text>
      </div>
    );
  }

  return (
    <div className="ib-dashboard-view" data-testid="ib-dashboard-view" key={`ib-dashboard-view-${planMessageId}`}>
      <div className="ib-dashboard-view__header" data-testid="ib-dashboard-view-title">
        <Text strong>{dashboardModel.title || "Dashboard"}</Text>
      </div>
      <div className="ib-dashboard-view__designer">
        {active && startedFor === planMessageId ? (
          <HIDashboardDesigner
            key={planMessageId}
            refresh={refreshKeyRef.current}
            setRefresh={() => { }}
            parameters={{}}
            fromInstant={true}
            isOpenMode={isOpenMode}
          />
        ) : (
          <div
            className="ib-dashboard-view__pending"
            data-testid="ib-dashboard-view-pending"
          >
            <Text type="secondary">Open this tab to load the dashboard…</Text>
          </div>
        )}
      </div>
    </div>
  );
};

export default IBDashboardView;
