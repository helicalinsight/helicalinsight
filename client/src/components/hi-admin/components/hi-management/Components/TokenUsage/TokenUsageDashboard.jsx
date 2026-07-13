import { memo, useCallback, useEffect, useMemo, useState } from "react";
import {
  Card,
  Col,
  Row,
  Table,
  Tooltip,
  Button,
  Space,
  Input,
  DatePicker,
} from "antd";
import { SyncOutlined, CalendarOutlined, FullscreenOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { Column, Line } from "@ant-design/plots";
import moment from "moment";
import { useDispatch } from "react-redux";
import requests from "../../../../../../base/requests";
import { uriConfig } from "../../../../../../base/requests/admin.request";
import {
  getColumnConfig,
  getLineConfig,
} from "../../../hi-overview/utils/overview-chart-utils";
import "../../../hi-overview/index.scss";
import "./token-usage.scss";

const LLM_USAGE_URI = uriConfig.monitorSystemLlmUsageStats;
const DEFAULT_PAGE_SIZE = 10;
const DEFAULT_DAYS = 30;
const TOP_STATS_LIMIT = 5;
const BREAKDOWN_TABLE_SCROLL_Y = 220;

const TREND_PRESETS = {
  "24H": 1,
  "7D": 7,
  "1M": 30,
  "3M": 90,
};

const SORTABLE_COLUMNS = new Set([
  "createdAt",
  "username",
  "endpoint",
  "modelName",
  "totalTokens",
  "totalCost",
  "requestStatus",
]);

const SERVER_SORTER = { compare: () => 0 };
const TABLE_SORT_DIRECTIONS = ["ascend", "descend"];

const formatNumber = (value) => {
  const num = Number(value ?? 0);
  if (!Number.isFinite(num) || num === 0) return "NA";
  return num.toLocaleString();
};

const formatCost = (value) => formatNumber(value);

const toBackendSortOrder = (antdOrder) => {
  if (antdOrder === "ascend") return "asc";
  if (antdOrder === "descend") return "desc";
  return "desc";
};

const buildTrendDateRange = (preset, customRange) => {
  if (preset === "custom" && customRange?.[0] && customRange?.[1]) {
    return {
      fromDate: customRange[0].clone().startOf("day").valueOf(),
      toDate: customRange[1].clone().endOf("day").valueOf(),
    };
  }
  const days = TREND_PRESETS[preset] || DEFAULT_DAYS;
  return {
    fromDate: moment().subtract(days, "days").startOf("day").valueOf(),
    toDate: moment().endOf("day").valueOf(),
  };
};

const SummaryStatCard = ({ title, value, loading, valueClassName }) => (
  <Card
    size="small"
    title={<div>{title}</div>}
    className="hi-overview-border-box token-usage-stat-card"
    loading={loading}
    hoverable
  >
    <div className="hi-overview-padding">
      <h1
        className={["hi-overview-stats", valueClassName].filter(Boolean).join(" ")}
      >
        {value}
      </h1>
    </div>
  </Card>
);

const TokenUsagePanelCard = ({
  title,
  loading,
  extra,
  children,
  className = "",
}) => (
  <Card
    size="small"
    title={<div>{title}</div>}
    className={`hi-overview-border-box token-usage-panel-card ${className}`.trim()}
    loading={loading}
    hoverable
    extra={extra}
  >
    {children}
  </Card>
);

const TokenUsageFilters = memo(({ onApply, onRefresh, isRefreshing }) => {
  const [username, setUsername] = useState("");
  const [endpoint, setEndpoint] = useState("");
  const [requestStatus, setRequestStatus] = useState("");

  const handleApply = () => {
    onApply({
      username: username.trim(),
      endpoint: endpoint.trim(),
      requestStatus: requestStatus.trim(),
    });
  };

  return (
    <Space size="middle">
      <Input
        allowClear
        size="small"
        placeholder="Filter by user"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        onPressEnter={handleApply}
      />
      <Input
        allowClear
        size="small"
        placeholder="Filter by endpoint"
        value={endpoint}
        onChange={(e) => setEndpoint(e.target.value)}
        onPressEnter={handleApply}
      />
      <Input
        allowClear
        size="small"
        placeholder="Filter by status"
        style={{ width: 140 }}
        value={requestStatus}
        onChange={(e) => setRequestStatus(e.target.value)}
        onPressEnter={handleApply}
      />
      <Button size="small" onClick={handleApply}>
        Apply
      </Button>
      <Tooltip title="Refresh">
        <SyncOutlined
          spin={isRefreshing}
          onClick={onRefresh}
          className="token-usage-refresh-icon"
        />
      </Tooltip>
    </Space>
  );
});

TokenUsageFilters.displayName = "TokenUsageFilters";

const TokenUsageDashboard = ({ apiRef, handleAbort }) => {
  const dispatch = useDispatch();
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [detailsLoading, setDetailsLoading] = useState(true);
  const [trendLoading, setTrendLoading] = useState(true);
  const [userStatsLoading, setUserStatsLoading] = useState(true);
  const [modelStatsLoading, setModelStatsLoading] = useState(true);
  const [summary, setSummary] = useState(null);
  const [endpointStats, setEndpointStats] = useState([]);
  const [userStats, setUserStats] = useState([]);
  const [modelStats, setModelStats] = useState([]);
  const [dailyTrend, setDailyTrend] = useState([]);
  const [details, setDetails] = useState({ records: [], total: 0 });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [sortField, setSortField] = useState(null);
  const [sortOrder, setSortOrder] = useState(null);
  const [usernameFilter, setUsernameFilter] = useState("");
  const [endpointFilter, setEndpointFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [periodPreset, setPeriodPreset] = useState("1M");
  const [customRange, setCustomRange] = useState(null);
  const [showCustomPicker, setShowCustomPicker] = useState(false);
  const [showAllUsers, setShowAllUsers] = useState(false);
  const [showAllModels, setShowAllModels] = useState(false);

  const baseFilter = useMemo(
    () => ({
      days: DEFAULT_DAYS,
      limit: TOP_STATS_LIMIT,
    }),
    []
  );

  const postUsageRequest = useCallback(
    (formData) =>
      new Promise((resolve, reject) => {
        requests.admin(dispatch).postManagementData(
          formData,
          LLM_USAGE_URI,
          (res) => resolve(res),
          (err) => reject(err)
        );
      }),
    [dispatch]
  );

  const resolveDateRange = useCallback(
    (options = {}) => {
      const nextPreset = options.periodPreset ?? periodPreset;
      const nextCustomRange = options.customRange ?? customRange;
      return buildTrendDateRange(nextPreset, nextCustomRange);
    },
    [customRange, periodPreset]
  );

  const buildStatsFilter = useCallback(
    (options = {}) => {
      const filter = { ...baseFilter, ...resolveDateRange(options) };
      const nextUsername = options.username ?? usernameFilter;
      const nextEndpoint = options.endpoint ?? endpointFilter;
      const nextStatus = options.requestStatus ?? statusFilter;
      if (nextUsername) filter.username = nextUsername;
      if (nextEndpoint) filter.endpoint = nextEndpoint;
      if (nextStatus) filter.requestStatus = nextStatus;
      return filter;
    },
    [baseFilter, endpointFilter, resolveDateRange, statusFilter, usernameFilter]
  );

  const buildDetailFilter = useCallback(
    (options = {}) => {
      const activeSortField =
        options.sortField !== undefined ? options.sortField : sortField;
      const activeSortOrder =
        options.antdSortOrder !== undefined ? options.antdSortOrder : sortOrder;

      const filter = {
        action: "details",
        page: options.page ?? page,
        pageSize: options.pageSize ?? pageSize,
        ...resolveDateRange(options),
      };

      if (activeSortField && activeSortOrder) {
        filter.sortField = activeSortField;
        filter.sortOrder =
          options.sortOrder ?? toBackendSortOrder(activeSortOrder);
      }

      const nextUsername = options.username ?? usernameFilter;
      const nextEndpoint = options.endpoint ?? endpointFilter;
      const nextStatus = options.requestStatus ?? statusFilter;
      if (nextUsername) filter.username = nextUsername;
      if (nextEndpoint) filter.endpoint = nextEndpoint;
      if (nextStatus) filter.requestStatus = nextStatus;
      return filter;
    },
    [
      endpointFilter,
      page,
      pageSize,
      resolveDateRange,
      sortField,
      sortOrder,
      statusFilter,
      usernameFilter,
    ]
  );

  const loadDetails = useCallback(
    async (options = {}) => {
      setDetailsLoading(true);
      try {
        const detailsRequest = postUsageRequest(buildDetailFilter(options));
        apiRef.current = detailsRequest;
        const detailsRes = await detailsRequest;
        setDetails(detailsRes || { records: [], total: 0 });
      } catch (error) {
        console.error("Failed to load LLM usage details", error);
      } finally {
        setDetailsLoading(false);
      }
    },
    [apiRef, buildDetailFilter, postUsageRequest]
  );

  const loadDailyTrend = useCallback(
    async (options = {}) => {
      const statsFilter = buildStatsFilter(options);

      setTrendLoading(true);
      try {
        const trendRequest = postUsageRequest({
          action: "dailyTrend",
          ...statsFilter,
        });
        apiRef.current = trendRequest;
        const trendRes = await trendRequest;
        setDailyTrend(
          Array.isArray(trendRes?.items) ? [...trendRes.items].reverse() : []
        );
      } catch (error) {
        console.error("Failed to load daily trend", error);
      } finally {
        setTrendLoading(false);
      }
    },
    [apiRef, buildStatsFilter, postUsageRequest]
  );

  const loadUserStats = useCallback(
    async (options = {}) => {
      const showAll = options.showAll ?? showAllUsers;
      const statsFilter = buildStatsFilter(options);

      setUserStatsLoading(true);
      try {
        const userRequest = postUsageRequest({
          ...statsFilter,
          action: "byUser",
          limit: showAll ? 0 : TOP_STATS_LIMIT,
        });
        const userRes = await userRequest;
        setUserStats(Array.isArray(userRes?.items) ? userRes.items : []);
      } catch (error) {
        console.error("Failed to load user stats", error);
      } finally {
        setUserStatsLoading(false);
      }
    },
    [buildStatsFilter, postUsageRequest, showAllUsers]
  );

  const loadModelStats = useCallback(
    async (options = {}) => {
      const showAll = options.showAll ?? showAllModels;
      const statsFilter = buildStatsFilter(options);

      setModelStatsLoading(true);
      try {
        const modelRequest = postUsageRequest({
          ...statsFilter,
          action: "byModel",
          limit: showAll ? 0 : TOP_STATS_LIMIT,
        });
        const modelRes = await modelRequest;
        setModelStats(Array.isArray(modelRes?.items) ? modelRes.items : []);
      } catch (error) {
        console.error("Failed to load model stats", error);
      } finally {
        setModelStatsLoading(false);
      }
    },
    [buildStatsFilter, postUsageRequest, showAllModels]
  );

  const loadDashboard = useCallback(
    async (options = {}) => {
      const statsFilter = buildStatsFilter(options);

      setDashboardLoading(true);
      try {
        const requestsList = [
          postUsageRequest({ action: "summary", ...statsFilter }),
          postUsageRequest({ ...statsFilter, action: "byEndpoint" }),
        ];

        apiRef.current = Promise.all(requestsList);
        const [summaryRes, endpointRes] = await apiRef.current;

        setSummary(summaryRes || {});
        setEndpointStats(Array.isArray(endpointRes?.items) ? endpointRes.items : []);
      } catch (error) {
        console.error("Failed to load LLM usage dashboard", error);
      } finally {
        setDashboardLoading(false);
      }
    },
    [apiRef, buildStatsFilter, postUsageRequest]
  );

  const loadAll = useCallback(
    async (options = {}) => {
      const nextShowAll =
        options.showAll !== undefined ? options.showAll : undefined;
      await Promise.all([
        loadDashboard(options),
        loadDailyTrend(options),
        loadUserStats(
          nextShowAll !== undefined ? { ...options, showAll: nextShowAll } : options
        ),
        loadModelStats(
          nextShowAll !== undefined ? { ...options, showAll: nextShowAll } : options
        ),
        loadDetails(options),
      ]);
    },
    [loadDashboard, loadDailyTrend, loadDetails, loadModelStats, loadUserStats]
  );

  useEffect(() => {
    loadAll();
  }, []);

  const endpointChartData = endpointStats.map((item) => ({
    category: item.groupKey || "unknown",
    value: Number(item.totalTokens || 0),
    displayValue: formatNumber(item.totalTokens),
  }));

  const dailyTrendData = dailyTrend.map((item) => ({
    date: item.groupKey,
    tokens: Number(item.totalTokens || 0),
    requests: Number(item.requestCount || 0),
  }));

  const getColumnSortOrder = (field) =>
    sortField === field && sortOrder ? sortOrder : null;

  const columns = useMemo(
    () => [
      {
        title: "Time",
        dataIndex: "createdAt",
        key: "createdAt",
        width: 160,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("createdAt"),
        render: (value) =>
          value ? moment(value).format("YYYY-MM-DD HH:mm:ss") : "-",
      },
      {
        title: "User",
        dataIndex: "username",
        key: "username",
        width: 120,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("username"),
      },
      {
        title: "Endpoint",
        dataIndex: "endpoint",
        key: "endpoint",
        width: 180,
        ellipsis: true,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("endpoint"),
      },
      {
        title: "Model",
        dataIndex: "modelName",
        key: "modelName",
        width: 140,
        ellipsis: true,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("modelName"),
        render: (value) => value || "-",
      },
      {
        title: (
          <span className="token-usage-column-title">
            Tokens
            <Tooltip title="Total (Input / Output tokens)" placement="bottom">
              <span
                className="token-usage-column-info"
                onClick={(event) => event.stopPropagation()}
                onMouseEnter={(event) => event.stopPropagation()}
                onMouseLeave={(event) => event.stopPropagation()}
              >
                <InfoCircleOutlined />
              </span>
            </Tooltip>
          </span>
        ),
        dataIndex: "totalTokens",
        key: "totalTokens",
        width: 140,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("totalTokens"),
        render: (_, record) => (
          <span>
            {formatNumber(record.totalTokens)} ({formatNumber(record.inputTokens)}/
            {formatNumber(record.outputTokens)})
          </span>
        ),
      },
      {
        title: "Cost",
        dataIndex: "totalCost",
        key: "totalCost",
        width: 100,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("totalCost"),
        render: (value) => formatCost(value),
      },
      {
        title: "Status",
        dataIndex: "requestStatus",
        key: "requestStatus",
        width: 100,
        sorter: SERVER_SORTER,
        sortDirections: TABLE_SORT_DIRECTIONS,
        sortOrder: getColumnSortOrder("requestStatus"),
        render: (value) => value || "-",
      },
      {
        title: "Query",
        dataIndex: "userQuery",
        key: "userQuery",
        ellipsis: true,
        render: (value) => (
          <Tooltip title={value}>
            <span>{value || "-"}</span>
          </Tooltip>
        ),
      },
    ],
    [sortField, sortOrder]
  );

  const breakdownColumns = [
    {
      title: "Requests",
      dataIndex: "requestCount",
      key: "requestCount",
      render: (value) => formatNumber(value),
    },
    {
      title: "Tokens",
      dataIndex: "totalTokens",
      key: "totalTokens",
      render: (value) => formatNumber(value),
    },
    {
      title: "Cost",
      dataIndex: "totalCost",
      key: "totalCost",
      render: (value) => formatCost(value),
    },
  ];

  const handleRefresh = () => {
    loadAll({ page, pageSize, sortField, antdSortOrder: sortOrder });
  };

  const handleTableChange = (pagination, _filters, sorter) => {
    const activeSorter = Array.isArray(sorter) ? sorter[0] : sorter;
    let nextPage = pagination.current || 1;
    const nextPageSize = pagination.pageSize || DEFAULT_PAGE_SIZE;
    let nextSortField = sortField;
    let nextSortOrder = sortOrder;

    const field = activeSorter?.field || activeSorter?.columnKey;
    const incomingOrder = activeSorter?.order || null;

    if (field && SORTABLE_COLUMNS.has(field)) {
      if (incomingOrder) {
        const sortChanged = field !== sortField || incomingOrder !== sortOrder;
        if (sortChanged) {
          nextSortField = field;
          nextSortOrder = incomingOrder;
          nextPage = 1;
        }
      } else if (sortField && field === sortField && sortOrder) {
        // Third click on the active column clears sort.
        nextSortField = null;
        nextSortOrder = null;
        nextPage = 1;
      }
    }

    setPage(nextPage);
    setPageSize(nextPageSize);
    setSortField(nextSortField);
    setSortOrder(nextSortOrder);
    loadDetails({
      page: nextPage,
      pageSize: nextPageSize,
      sortField: nextSortField,
      antdSortOrder: nextSortOrder,
    });
  };

  const handleApplyFilters = useCallback(
    ({ username, endpoint, requestStatus }) => {
      setUsernameFilter(username);
      setEndpointFilter(endpoint);
      setStatusFilter(requestStatus);
      setPage(1);
      setShowAllUsers(false);
      setShowAllModels(false);
      loadAll({ page: 1, showAll: false, username, endpoint, requestStatus });
    },
    [loadAll]
  );

  const handlePeriodPresetChange = (preset) => {
    if (preset === "custom") {
      setShowCustomPicker(true);
      setPeriodPreset("custom");
      return;
    }
    setShowCustomPicker(false);
    setPeriodPreset(preset);
    setPage(1);
    loadAll({ periodPreset: preset, page: 1 });
  };

  const handleCustomRangeChange = (range) => {
    setCustomRange(range);
    if (range?.[0] && range?.[1]) {
      setPeriodPreset("custom");
      setPage(1);
      loadAll({ periodPreset: "custom", customRange: range, page: 1 });
    }
  };

  const handleToggleShowAllUsers = () => {
    const nextShowAll = !showAllUsers;
    setShowAllUsers(nextShowAll);
    loadUserStats({ showAll: nextShowAll });
  };

  const handleToggleShowAllModels = () => {
    const nextShowAll = !showAllModels;
    setShowAllModels(nextShowAll);
    loadModelStats({ showAll: nextShowAll });
  };

  const renderPeriodFilter = () => (
    <div className="token-usage-trend-filter">
      <button
        type="button"
        className={`token-usage-trend-filter__btn${
          periodPreset === "custom" ? " token-usage-trend-filter__btn--active" : ""
        }`}
        onClick={() => handlePeriodPresetChange("custom")}
      >
        <CalendarOutlined />
        <span>Custom</span>
      </button>
      {Object.keys(TREND_PRESETS).map((preset) => (
        <button
          key={preset}
          type="button"
          className={`token-usage-trend-filter__btn${
            periodPreset === preset ? " token-usage-trend-filter__btn--active" : ""
          }`}
          onClick={() => handlePeriodPresetChange(preset)}
        >
          {preset}
        </button>
      ))}
      {showCustomPicker && (
        <DatePicker.RangePicker
          className="token-usage-trend-filter__picker"
          value={customRange}
          onChange={handleCustomRangeChange}
          allowClear
        />
      )}
    </div>
  );

  const renderShowAllButton = (expanded, onClick, loading) => (
    <button
      type="button"
      className="token-usage-show-all hi-reload-configs-button"
      onClick={onClick}
      disabled={loading}
    >
      {!expanded && <FullscreenOutlined style={{ height: 14 }} />}
      <span>{expanded ? "Show Less" : "Show All"}</span>
    </button>
  );

  const shouldShowBreakdownToggle = (items, showAll) =>
    showAll || (Array.isArray(items) && items.length >= TOP_STATS_LIMIT);

  const getBreakdownTableScroll = (showAll) =>
    showAll ? { y: BREAKDOWN_TABLE_SCROLL_Y } : undefined;

  const isRefreshing =
    dashboardLoading ||
    detailsLoading ||
    trendLoading ||
    userStatsLoading ||
    modelStatsLoading;

  const lineChartConfig = getLineConfig({
    data: dailyTrendData,
    height: 240,
    tooltipFormatter: (datum) => ({
      name: "Tokens",
      value: formatNumber(datum.tokens),
    }),
  });

  const requestCount = Number(summary?.requestCount || 0);
  const successCount = Number(summary?.successCount || 0);
  const failureCount = Number(summary?.failureCount || 0);

  return (
    <div className="hi-overview token-usage-dashboard">
      <Row gutter={[12, 12]} align="top" className="hi-overview-box">
        <Col span={24}>
          <Row
            justify="space-between"
            align="middle"
            gutter={[12, 12]}
            className="token-usage-toolbar"
          >
            <Col>{renderPeriodFilter()}</Col>
            <Col>
              <TokenUsageFilters
                onApply={handleApplyFilters}
                onRefresh={handleRefresh}
                isRefreshing={isRefreshing}
              />
            </Col>
          </Row>
        </Col>

        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Total Requests"
            value={formatNumber(requestCount)}
            loading={dashboardLoading}
          />
        </Col>
        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Success"
            value={formatNumber(successCount)}
            loading={dashboardLoading}
            valueClassName="token-usage-stat--success"
          />
        </Col>
        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Failed"
            value={formatNumber(failureCount)}
            loading={dashboardLoading}
            valueClassName="token-usage-stat--failed"
          />
        </Col>
        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Total Tokens"
            value={formatNumber(summary?.totalTokens)}
            loading={dashboardLoading}
          />
        </Col>
        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Input / Output"
            value={`${formatNumber(summary?.inputTokens)} / ${formatNumber(summary?.outputTokens)}`}
            loading={dashboardLoading}
            valueClassName="token-usage-stat--io"
          />
        </Col>
        <Col xs={24} md={8}>
          <SummaryStatCard
            title="Total Cost"
            value={formatCost(summary?.totalCost)}
            loading={dashboardLoading}
          />
        </Col>

        <Col xs={24} lg={12}>
          <TokenUsagePanelCard
            title="Tokens by Endpoint"
            loading={dashboardLoading}
            extra={
              <Tooltip title="Refresh" placement="left">
                <SyncOutlined
                  spin={dashboardLoading}
                  onClick={handleRefresh}
                />
              </Tooltip>
            }
          >
            <div className="hi-overview-padding token-usage-chart-body">
              {endpointChartData.length > 0 ? (
                <Column {...getColumnConfig({ data: endpointChartData, height: 240 })} />
              ) : (
                <div className="token-usage-empty">No endpoint usage recorded yet.</div>
              )}
            </div>
          </TokenUsagePanelCard>
        </Col>
        <Col xs={24} lg={12}>
          <TokenUsagePanelCard title="Token Trend" loading={trendLoading}>
            <div className="hi-overview-padding token-usage-chart-body">
              {dailyTrendData.length > 0 ? (
                <Line {...lineChartConfig} />
              ) : (
                <div className="token-usage-empty">No daily trend data available.</div>
              )}
            </div>
          </TokenUsagePanelCard>
        </Col>

        <Col xs={24} lg={12}>
          <TokenUsagePanelCard title="Top Users by Tokens" loading={userStatsLoading}>
            <div
              className={`token-usage-table-body${
                showAllUsers ? " token-usage-table-body--scrollable" : ""
              }`}
            >
              <Table
                size="small"
                pagination={false}
                rowKey={(record) => record.groupKey}
                dataSource={userStats}
                scroll={getBreakdownTableScroll(showAllUsers)}
                columns={[
                  { title: "User", dataIndex: "groupKey", key: "groupKey" },
                  ...breakdownColumns,
                ]}
              />
            </div>
            {shouldShowBreakdownToggle(userStats, showAllUsers) &&
              renderShowAllButton(showAllUsers, handleToggleShowAllUsers, userStatsLoading)}
          </TokenUsagePanelCard>
        </Col>
        <Col xs={24} lg={12}>
          <TokenUsagePanelCard title="Usage by Model" loading={modelStatsLoading}>
            <div
              className={`token-usage-table-body${
                showAllModels ? " token-usage-table-body--scrollable" : ""
              }`}
            >
              <Table
                size="small"
                pagination={false}
                rowKey={(record) => record.groupKey}
                dataSource={modelStats}
                scroll={getBreakdownTableScroll(showAllModels)}
                columns={[
                  { title: "Model", dataIndex: "groupKey", key: "groupKey" },
                  ...breakdownColumns,
                ]}
              />
            </div>
            {shouldShowBreakdownToggle(modelStats, showAllModels) &&
              renderShowAllButton(showAllModels, handleToggleShowAllModels, modelStatsLoading)}
          </TokenUsagePanelCard>
        </Col>

        <Col span={24}>
          <TokenUsagePanelCard title="Usage Details" className="token-usage-details">
            <div className="token-usage-table-body">
              <Table
                size="small"
                rowKey="id"
                columns={columns}
                dataSource={details.records || []}
                onChange={handleTableChange}
                loading={detailsLoading}
                pagination={{
                  current: page,
                  pageSize,
                  total: details.total || 0,
                  showSizeChanger: true,
                }}
                scroll={{ x: 1100 }}
              />
            </div>
          </TokenUsagePanelCard>
        </Col>
      </Row>
    </div>
  );
};

export default TokenUsageDashboard;
