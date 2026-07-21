import React from "react";
import { Card, Col, Row } from "antd";
import { useSelector } from "react-redux";
import { Column } from "@ant-design/plots";
import "../index.scss";
import { getColumnConfig } from "../utils/overview-chart-utils";

const HICacheDashboardChart = () => {
  const cacheSize = useSelector((state) => state.admin?.cacheSize);
  const cachedReports = useSelector((state) => state.admin?.cachedReports);
  const cachedDataSources = useSelector((state) => state.admin?.cachedDataSources);
  const isCacheFetched = useSelector((state) => state.admin?.isFetched?.cacheSize);
  const isReportsFetched = useSelector((state) => state.admin?.isFetched?.cachedReports);
  const isDataSourcesFetched = useSelector(
    (state) => state.admin?.isFetched?.cachedDataSources
  );

  if (!isCacheFetched || !isReportsFetched || !isDataSourcesFetched) {
    return null;
  }

  const chartData = [
    {
      category: "Cache",
      value: parseInt(cacheSize?.size) || 0,
      displayValue: `${cacheSize?.size || 0}`,
    },
    {
      category: "Reports",
      value: cachedReports?.length || 0,
      displayValue: `${cachedReports?.length || 0}`,
    },
    {
      category: "Data Sources",
      value: cachedDataSources?.dataSources?.length || 0,
      displayValue: `${cachedDataSources?.dataSources?.length || 0}`,
    },
  ];

  return (
    <Card
      size="small"
      className="hi-overview hi-overview-cache-dashboard"
      title={<div data-testid="hi-cache-dashboard-title">Cache Dashboard</div>}
    >
      <Row>
        <Col span={24}>
          <Column {...getColumnConfig({ data: chartData, height: 180, color: "#1ec469" })} />
        </Col>
      </Row>
    </Card>
  );
};

export { HICacheDashboardChart };
