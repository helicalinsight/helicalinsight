import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Card,
  Col,
  Row,
  Table,
  Tag,
  Tooltip,
  Button,
  Space,
  Statistic,
  Select,
  Input,
} from "antd";
import { SyncOutlined } from "@ant-design/icons";
import { Column, Line } from "@ant-design/plots";
import moment from "moment";
import { useDispatch } from "react-redux";
import requests from "../../../../../../base/requests";
import { uriConfig } from "../../../../../../base/requests/admin.request";
import { getColumnConfig } from "../../../hi-overview/utils/overview-chart-utils";
import "./token-usage.scss";

const LLM_USAGE_URI = uriConfig.monitorSystemLlmUsageStats;
const DEFAULT_PAGE_SIZE = 15;
const DEFAULT_DAYS = 30;

const formatNumber = (value) => {
  const num = Number(value || 0);
  return Number.isFinite(num) ? num.toLocaleString() : "0";
};

const formatCost = (value) => {
  const num = Number(value || 0);
  return Number.isFinite(num) ? `$${num.toFixed(4)}` : "$0.0000";
};

const TokenUsageDashboard = ({ apiRef, handleAbort }) => {
  const dispatch = useDispatch();
  const [dashboardLoading, setDashboardLoading] = useState(true);
  const [detailsLoading, setDetailsLoading] = useState(true);
  const [summary, setSummary] = useState(null);
  const [endpointStats, setEndpointStats] = useState([]);
  const [userStats, setUserStats] = useState([]);
  const [modelStats, setModelStats] = useState([]);
  const [dailyTrend, setDailyTrend] = useState([]);
  const [details, setDetails] = useState({ records: [], total: 0 });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(DEFAULT_PAGE_SIZE);
  const [usernameFilter, setUsernameFilter] = useState("");
  const [endpointFilter, setEndpointFilter] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const baseFilter = useMemo(
    () => ({
      days: DEFAULT_DAYS,
      limit: 10,
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

  const buildDetailFilter = useCallback(
    (options = {}) => {
      const filter = {
        action: "details",
        page: options.page ?? page,
        pageSize: options.pageSize ?? pageSize,
        days: DEFAULT_DAYS,
      };
      const nextUsername = options.username ?? usernameFilter;
      const nextEndpoint = options.endpoint ?? endpointFilter;
      const nextStatus = options.requestStatus ?? statusFilter;
      if (nextUsername) filter.username = nextUsername;
      if (nextEndpoint) filter.endpoint = nextEndpoint;
      if (nextStatus) filter.requestStatus = nextStatus;
      return filter;
    },
    [endpointFilter, page, pageSize, statusFilter, usernameFilter]
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

  const loadDashboard = useCallback(
    async (options = {}) => {
      const nextUsername = options.username ?? usernameFilter;
      const nextEndpoint = options.endpoint ?? endpointFilter;
      const nextStatus = options.requestStatus ?? statusFilter;

      const statsFilter = { ...baseFilter, days: DEFAULT_DAYS };
      if (nextUsername) statsFilter.username = nextUsername;
      if (nextEndpoint) statsFilter.endpoint = nextEndpoint;
      if (nextStatus) statsFilter.requestStatus = nextStatus;

      setDashboardLoading(true);
      try {
        const requestsList = [
          postUsageRequest({ action: "summary", ...statsFilter }),
          postUsageRequest({ ...statsFilter, action: "byEndpoint" }),
          postUsageRequest({ ...statsFilter, action: "byUser" }),
          postUsageRequest({ ...statsFilter, action: "byModel" }),
          postUsageRequest({ action: "dailyTrend", ...statsFilter }),
        ];

        apiRef.current = Promise.all(requestsList);
        const [summaryRes, endpointRes, userRes, modelRes, trendRes] =
          await apiRef.current;

        setSummary(summaryRes || {});
        setEndpointStats(Array.isArray(endpointRes?.items) ? endpointRes.items : []);
        setUserStats(Array.isArray(userRes?.items) ? userRes.items : []);
        setModelStats(Array.isArray(modelRes?.items) ? modelRes.items : []);
        setDailyTrend(
          Array.isArray(trendRes?.items)
            ? [...trendRes.items].reverse()
            : []
        );
      } catch (error) {
        console.error("Failed to load LLM usage dashboard", error);
      } finally {
        setDashboardLoading(false);
      }
    },
    [
      apiRef,
      baseFilter,
      endpointFilter,
      postUsageRequest,
      statusFilter,
      usernameFilter,
    ]
  );

  const loadAll = useCallback(
    async (options = {}) => {
      await Promise.all([loadDashboard(options), loadDetails(options)]);
    },
    [loadDashboard, loadDetails]
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

  const columns = [
    {
      title: "Time",
      dataIndex: "createdAt",
      key: "createdAt",
      width: 160,
      render: (value) =>
        value ? moment(value).format("YYYY-MM-DD HH:mm:ss") : "-",
    },
    {
      title: "User",
      dataIndex: "username",
      key: "username",
      width: 120,
    },
    {
      title: "Endpoint",
      dataIndex: "endpoint",
      key: "endpoint",
      width: 180,
      ellipsis: true,
    },
    {
      title: "Model",
      dataIndex: "modelName",
      key: "modelName",
      width: 140,
      ellipsis: true,
      render: (value) => value || "-",
    },
    {
      title: "Tokens",
      key: "tokens",
      width: 140,
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
      render: (value) => formatCost(value),
    },
    {
      title: "Status",
      dataIndex: "requestStatus",
      key: "requestStatus",
      width: 100,
      render: (value) => (
        <Tag color={value === "success" ? "green" : "red"}>
          {value || "unknown"}
        </Tag>
      ),
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
  ];

  const handleRefresh = () => {
    loadAll({ page, pageSize });
  };

  const handleTableChange = (pagination) => {
    const nextPage = pagination.current || 1;
    const nextPageSize = pagination.pageSize || DEFAULT_PAGE_SIZE;
    setPage(nextPage);
    setPageSize(nextPageSize);
    loadDetails({ page: nextPage, pageSize: nextPageSize });
  };

  const applyFilters = () => {
    setPage(1);
    loadAll({ page: 1 });
  };

  return (
    <div className="token-usage-dashboard">
      <Row justify="end" className="token-usage-toolbar">
        <Col>
          <Space>
            <Input
              allowClear
              placeholder="Filter by user"
              value={usernameFilter}
              onChange={(e) => setUsernameFilter(e.target.value)}
              onPressEnter={applyFilters}
            />
            <Input
              allowClear
              placeholder="Filter by endpoint"
              value={endpointFilter}
              onChange={(e) => setEndpointFilter(e.target.value)}
              onPressEnter={applyFilters}
            />
            <Select
              allowClear
              placeholder="Status"
              style={{ width: 140 }}
              value={statusFilter || undefined}
              onChange={(value) => setStatusFilter(value || "")}
              options={[
                { label: "Success", value: "success" },
                { label: "Failed", value: "failed" },
              ]}
            />
            <Button onClick={applyFilters}>Apply</Button>
            <Tooltip title="Refresh">
              <Button
                icon={<SyncOutlined />}
                onClick={handleRefresh}
                loading={dashboardLoading || detailsLoading}
              />
            </Tooltip>
          </Space>
        </Col>
      </Row>

      <Row gutter={[16, 16]} className="token-usage-summary">
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic title="Total Requests" value={summary?.requestCount || 0} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic
              title="Success"
              value={summary?.successCount || 0}
              valueStyle={{ color: "#3f8600" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic
              title="Failed"
              value={summary?.failureCount || 0}
              valueStyle={{ color: "#cf1322" }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic
              title="Total Tokens"
              value={summary?.totalTokens || 0}
              formatter={(value) => formatNumber(value)}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic
              title="Input / Output"
              value={`${formatNumber(summary?.inputTokens)} / ${formatNumber(summary?.outputTokens)}`}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8} lg={4}>
          <Card loading={dashboardLoading}>
            <Statistic
              title="Total Cost"
              value={formatCost(summary?.totalCost)}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} className="token-usage-charts">
        <Col xs={24} lg={12}>
          <Card title="Tokens by Endpoint" loading={dashboardLoading}>
            {endpointChartData.length > 0 ? (
              <Column {...getColumnConfig({ data: endpointChartData, height: 260 })} />
            ) : (
              <div className="token-usage-empty">No endpoint usage recorded yet.</div>
            )}
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Daily Token Trend" loading={dashboardLoading}>
            {dailyTrendData.length > 0 ? (
              <Line
                data={dailyTrendData}
                height={260}
                autoFit
                xField="date"
                yField="tokens"
                smooth
                point={{ size: 3 }}
                tooltip={{
                  formatter: (datum) => ({
                    name: "Tokens",
                    value: formatNumber(datum.tokens),
                  }),
                }}
              />
            ) : (
              <div className="token-usage-empty">No daily trend data available.</div>
            )}
          </Card>
        </Col>
      </Row>

      <Row gutter={[16, 16]} className="token-usage-breakdown">
        <Col xs={24} lg={12}>
          <Card title="Top Users by Tokens" loading={dashboardLoading}>
            <Table
              size="small"
              pagination={false}
              rowKey={(record) => record.groupKey}
              dataSource={userStats}
              columns={[
                { title: "User", dataIndex: "groupKey", key: "groupKey" },
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
              ]}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Usage by Model" loading={dashboardLoading}>
            <Table
              size="small"
              pagination={false}
              rowKey={(record) => record.groupKey}
              dataSource={modelStats}
              columns={[
                { title: "Model", dataIndex: "groupKey", key: "groupKey" },
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
              ]}
            />
          </Card>
        </Col>
      </Row>

      <Card title="Usage Details" className="token-usage-details">
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
      </Card>
    </div>
  );
};

export default TokenUsageDashboard;
