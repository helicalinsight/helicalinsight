import { ExportOutlined } from "@ant-design/icons";
import { Typography } from "antd";
import { cloneDeep } from "lodash";
import { useEffect, useRef, useState } from "react";
import {
  setFilterCounter,
  setPreviewMode,
  storeDashboardConfig,
  storeFiltersList,
} from "../../../../../redux/actions/dashboard-designer.actions";
import { loadReportFilters } from "../../../../../redux/actions/hreport.actions";
import { HIDashboardDesigner } from "../../../../hi-dashboard-designer";
import InstantBITooltip from "../../../instant-bi-tooltip-title";
import { assembleInstantDashboardCreateConfig, getGridItemsFiltersSettings } from "../../../utils/assemble-instant-dashboard";
import { attachMissingDashboardFilters } from "../../../utils/attach-dashboard-filters";
import { getInstantBIMetadataForReport, getMetadataForHreport } from "../../../utils/common-utils";
import { persistConvertDashboardSeed } from "../../../utils/convert-sql-to-hr-save";
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
  renderThinkStepCard = () => null,
  questionsHistory = []
} = {}) => {
  const snapshotRef = useRef(null);
  const hydratedIdsRef = useRef(new Map());
  const refreshKeyRef = useRef(Date.now());
  const activatedForRef = useRef(null);
  const cleanupRef = useRef(null);
  const [started, setStarted] = useState(false);
  const reports = useSelector((state) => state.hreport.present.reports || []);

  const items = Array.isArray(dashboardModel?.items) ? dashboardModel.items : [];
  const hasContent = items.length > 0;

  const reportsWithDashboardFilters = () => {
    const attached = attachMissingDashboardFilters({
      reports,
      dashboardModel,
      questionsHistory,
    });
    attached.changedIds.forEach((reportId) => {
      const report = attached.reports.find((item) => item.id === reportId);
      if (report) {
        dispatch(loadReportFilters({ reportId, filters: report.filters }));
      }
    });
    return attached.reports;
  };

  const handleExploreDashboard = () => {
    if (typeof dispatch !== "function" || !hasContent) return;
    const boundReports = reportsWithDashboardFilters();
    const dashboardConfig = assembleInstantDashboardCreateConfig({
      dashboardModel,
      steps,
      theme: dashboardModel.theme || {},
      questionsHistory,
      isStatic: false,
      reports: boundReports
    });
    const gridItemsFiltersInfo = getGridItemsFiltersSettings(dashboardConfig.gridItemsData, boundReports);
    const subject = getInstantBIMetadataForReport(activeReport) || {};
    persistConvertDashboardSeed(
      { ...dashboardConfig, designerMode: INSTANT_EDIT_MODE },
      { subject },
      boundReports,
      gridItemsFiltersInfo
    );
    window.open(`${window.baseURL}#/dashboard-designer`);
  };

  useEffect(() => {
    if (!active || typeof dispatch !== "function" || !hasContent) return undefined;
    if (activatedForRef.current === planMessageId) return undefined;
    if (cleanupRef.current) {
      cleanupRef.current();
      cleanupRef.current = null;
    }
    activatedForRef.current = planMessageId;
    setStarted(true);
    hydratedIdsRef.current = new Map();
    let snapshot = null;
    dispatch((_, getState) => {
      snapshot = cloneDeep(getState()?.designer?.present);
    });
    snapshotRef.current = snapshot;

    const boundReports = reportsWithDashboardFilters();
    const config = assembleInstantDashboardCreateConfig({
      dashboardModel,
      steps,
      theme: dashboardModel.theme || {},
      questionsHistory,
      reports: boundReports
    });
    dispatch(storeDashboardConfig({ ...config, designerMode: INSTANT_EDIT_MODE }));
    dispatch(setPreviewMode(true));
    const { gridItemsData = [] } = config || {}
    gridItemsData.forEach((gridItem) => {
      if (gridItem.compType === DASHBOARD_CREATE_TILE) {
        const { reportInfo } = gridItem || {};
        const { hreportId = "" } = reportInfo || {};
        const report = boundReports.find((report) => report.id === hreportId);
        if (report && report?.filters.length) {
          dispatch(storeFiltersList({ filtersList: report.filters, designerItemId: gridItem.id, reportId: hreportId }));
        }
      }
      if (gridItem.compType === "filter-component") {
        dispatch(setFilterCounter(true))
      }
      return;
    })

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
    <div className="ib-dashboard-view" data-testid="ib-dashboard-view">
      <div className="ib-dashboard-view__header" data-testid="ib-dashboard-view-title">
        <Text strong>{dashboardModel.title || "Dashboard"}</Text>
        <InstantBITooltip title="Explore this dashboard">
          <button
            type="button"
            className="ib-dashboard-view__explore-btn"
            onClick={handleExploreDashboard}
            data-testid="ib-dashboard-view-explore"
            aria-label="Explore this dashboard"
          >
            <ExportOutlined />
          </button>
        </InstantBITooltip>
      </div>
      <div className="ib-dashboard-view__designer">
        {started ? (
          <HIDashboardDesigner
            refresh={refreshKeyRef.current}
            setRefresh={() => { }}
            parameters={{}}
            fromInstant={true}
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
