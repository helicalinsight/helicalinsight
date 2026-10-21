import { ConsoleSqlOutlined, DatabaseOutlined, EyeOutlined, FullscreenOutlined } from "@ant-design/icons";
import { Modal, Tabs } from "antd";
import { useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { refreshDashboard } from "../../../../redux/actions";
import notify from "../../../hi-notifications/notify";
import InstantBITooltip from "../../instant-bi-tooltip-title";
import { cleanSQL, getHReportSelectedChartType, getInstantBIAgentSubject, resolveInstantBiSql } from "../../utils/common-utils";
import { loadInstantBIDataInsight } from "../../utils/instant-bi-requests";
import InstantChartView from "./chart-view";
import { PreviewTab, SemanticTab, SQLTab } from "./chat-tabs";
import "./dashboard-tile-tabs.scss";

const resolveTileReportModel = (reportInfo = {}) =>
  reportInfo?.file?.report_model
  || reportInfo?.file?.instant_item?.chat_response?.report_model
  || reportInfo?.file?.instant_item?.report_model
  || {};

const getTitle = (icon, title = "") => {
  return (<InstantBITooltip title={title}>{icon}</InstantBITooltip>)
}

const DashboardTileTabs = ({ hreportId, gridId = "", reportInfo = {}, parameter, listeners } = {}) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [activeTab, setActiveTab] = useState("preview");
  const [isMaximized, setIsMaximized] = useState(false);
  const [isLoadingDataInsight, setIsLoadingDataInsight] = useState(false);
  const [dataInsightTrail, setDataInsightTrail] = useState([]);
  const [dataInsightContent, setDataInsightContent] = useState(
    reportInfo?.file?.instant_item?.chat_response?.data_insight?.insight || ""
  );
  const dataInsightApiRef = useRef(null);
  const dataInsightAbortedRef = useRef(false);

  const reports = useSelector((state) => state.hreport?.present?.reports || []);
  const dashboardVariables = useSelector((state) => state.dashboard.present.dashboardVariables);
  const refreshReports = useSelector((state) => state.dashboard.present.refreshReports);
  const applyDashboardFilters = useSelector((state) => state.designer.present.applyDashboardFilters);
  const enableApplyButton = useSelector((state) => {
    const settings = state.designer?.present?.designerSettings || [];
    return Boolean(settings.find((item) => item.key === "parameters")?.values?.enableApplyButton);
  });
  const [appliedVariables, setAppliedVariables] = useState(dashboardVariables);
  const applyStampRef = useRef(applyDashboardFilters);

  const prevRefresh = useRef(refreshReports);
  useEffect(() => {
    prevRefresh.current = refreshReports;
  }, [refreshReports]);

  useEffect(() => {
    if (!enableApplyButton) {
      setAppliedVariables(dashboardVariables);
      return;
    }
    if (applyStampRef.current !== applyDashboardFilters) {
      applyStampRef.current = applyDashboardFilters;
      setAppliedVariables(dashboardVariables);
    }
  }, [enableApplyButton, applyDashboardFilters, dashboardVariables]);

  useEffect(() => () => {
    if (dataInsightApiRef.current) {
      dataInsightAbortedRef.current = true;
      dataInsightApiRef.current.abort();
      dataInsightApiRef.current = null;
    }
  }, []);

  if (!hreportId) return null;

  const reportModel = resolveTileReportModel(reportInfo);
  const vizModel = reportModel?.viz_model || {};
  const dataModel = reportModel?.data_model || {};
  const chartName = String(vizModel?.chart?.viz || "Chart").toLowerCase();
  const sql = dataModel?.sql || "";

  const canMaximize = Boolean(hreportId)

  let dashboardFilter = { ...parameter?.dashboardFilter };

  let parameters = {};
  const variablesForCharts = enableApplyButton ? appliedVariables : dashboardVariables;

  listeners?.forEach((item) => {
    if (variablesForCharts?.[item]) {
      parameters[item] = variablesForCharts[item];
    }
  });
  const allFilters = parameter?.dashboardFilter ? variablesForCharts : null

  const handleRefreshDashboard = () => {
    dispatch(refreshDashboard())
  }

  const handleCopySQL = async () => {
    try {
      await navigator.clipboard.writeText(cleanSQL(sql));
      Notify.success({ type: "Frontend", message: "SQL copied !" });
    } catch (err) { }
  };

  const modalVizType = getHReportSelectedChartType(hreportId, reports, "");

  const { file = {} } = reportInfo || {};
  const { instant_item = {} } = file || {}
  const { chat_response = {} } = instant_item || {};

  const cleanedSql = resolveInstantBiSql({
    fullChatResponse: chat_response
  });
  const resolvedSql = cleanedSql ? `\`\`\`sql\n${cleanedSql}\n\`\`\`` : "";

  const handleAbortDataInsight = () => {
    dataInsightAbortedRef.current = true;
    dataInsightApiRef.current?.abort();
    dataInsightApiRef.current = null;
    setIsLoadingDataInsight(false);
  };

  const handleDataInsight = () => {
    if (isLoadingDataInsight || !dispatch) return;
    let ibReport = null;
    dispatch((_, getState) => {
      ibReport = (getState().instantBI?.reports || []).find((report) => report.active) || null;
    });
    const reportId = ibReport?.id;
    const chatId = ibReport?.activeChatID;
    const chatSequenceId = instant_item?.chat_seq_id || instant_item?.chatSeqId || "";
    const userInput = instant_item?.question || file?.title || "";
    const agentSubject = getInstantBIAgentSubject(ibReport || {}) || {};
    const info = ibReport?.reportInfo || {};
    if (!reportId || !chatId || !chatSequenceId || !userInput || !agentSubject.file) {
      Notify?.error?.({
        type: "Frontend",
        message: "Unable to explain this chart: chat context is missing.",
      });
      return;
    }
    if (!cleanedSql && !chat_response?.sql?.raw_sql) {
      Notify?.error?.({
        type: "Frontend",
        message: "Unable to explain this chart: SQL is not available yet.",
      });
      return;
    }
    dataInsightAbortedRef.current = false;
    setIsLoadingDataInsight(true);
    setDataInsightTrail([]);
    const liveData = reports.find((report) => report.id === hreportId)?.reportData?.data || [];
    dataInsightApiRef.current = loadInstantBIDataInsight({
      dispatch,
      reportId,
      chatSequenceId,
      userInput,
      location: info.location,
      fileName: info.uuid || (info.reportName && `${info.reportName}.instant`),
      chatId,
      agent: agentSubject,
      useLoadChatPayload: false,
      existingChatResponse: {
        ...chat_response,
        sql: chat_response?.sql || (cleanedSql ? { raw_sql: cleanedSql } : {}),
        data: liveData,
        hreportId,
      },
      skipDispatch: true,
      Notify,
      abortedRef: dataInsightAbortedRef,
      onProgress: (progress) => {
        if (progress?.message) {
          setDataInsightTrail((prev) => {
            if (prev[prev.length - 1] === progress.message) return prev;
            return [...prev, progress.message];
          });
        }
      },
      onComplete: (result) => {
        setIsLoadingDataInsight(false);
        dataInsightApiRef.current = null;
        const insight = result?.insight
          || result?.response?.insight
          || result?.data_insight?.insight
          || "";
        if (insight) setDataInsightContent(insight);
      },
    });
  };

  const currentHReport = reports.find((report) => report.id === hreportId);

  const commonProps = {
    vizDetails: chat_response?.viz || {},
    sqlDetails: chat_response?.sql || {},
    tokenUsage: chat_response?.token_usage || {},
    isOpenMode: false,
    isEditMode: false,
    showDataInsightButton: Boolean(hreportId) && !isLoadingDataInsight,
    handleDataInsight,
    resolvedFullChatResponse: chat_response,
    fullChatResponse: {
      ...chat_response,
      hreportId
    },
    isLoadingDataInsight,
    dataInsightTrail,
    dataInsightContent,
    handleAbortDataInsight,
    hasDataInsightTokens: {},
    dispatch,
    activeReport: {},
    handleCopySQL,
    resolvedSql,
    dashboardMode: true,
    extraProps: {
      parameters,
      dashboardFilter,
      allFilters,
      // refreshDashboard: handleRefreshDashboard,
      cacheRefresh: refreshReports !== prevRefresh.current,
      cacheRefreshCount: refreshReports,
      // onChange
    },
    filters: currentHReport?.filters
  }

  const handleTabChange = (active) => {
    if (active === "maximize") {
      setIsMaximized?.(true);
      return;
    }
    setActiveTab(active);
  };


  const semanticPane = (
    <Tabs.TabPane tab={getTitle(<DatabaseOutlined />, "Semantic")} key="semantic">
      <SemanticTab {...commonProps} />
    </Tabs.TabPane>
  );
  const previewPane = (
    <Tabs.TabPane tab={getTitle(<EyeOutlined />, "Preview")} key="preview">
      <PreviewTab {...commonProps} setActiveTab={setActiveTab} />
    </Tabs.TabPane>
  );
  const sqlPane = (
    <Tabs.TabPane tab={getTitle(<ConsoleSqlOutlined />, "SQL")} key="sql">
      <SQLTab {...commonProps} />
    </Tabs.TabPane>
  );

  return (
    <div className="ib-dashboard-tile-tabs">
      <Tabs
        className="dashboard-icon-tabs"
        activeKey={activeTab}
        onChange={handleTabChange}
      >
        {
          <Tabs.TabPane tab={getTitle(<FullscreenOutlined />, "Maximize")} key="maximize">
            {null}
          </Tabs.TabPane>
        }
        {semanticPane}
        {previewPane}
        {sqlPane}
      </Tabs>

      <Modal
        title="Preview"
        visible={isMaximized && canMaximize}
        onCancel={() => setIsMaximized(false)}
        width="95%"
        style={{ top: 20 }}
        footer={null}
        wrapClassName="ib-chart-preview-modal"
        destroyOnClose
      >
        <InstantChartView
          data={[]}
          vf=""
          id={gridId}
          chartName={chartName}
          chartSettings={{}}
          plotConfig={{}}
          className="chart-wrapper--modal"
          fullChatResponse={commonProps.fullChatResponse}
          vizType={modalVizType}
          extraProps={{}}
        />
      </Modal>
    </div>
  );
};

export default DashboardTileTabs;
