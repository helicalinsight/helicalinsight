import React, { useEffect } from "react";
import { Card, Col, Row, Tooltip } from "antd";
import { SyncOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { Column } from "@ant-design/plots";
import "../index.scss";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeDataSourceCount,
  storeReportStats,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import CustomSkeletonCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletoncard";
import { getColumnConfig } from "../utils/overview-chart-utils";

const HISystemOverviewChart = ({ handleAbort, apiRef }) => {
  const dispatch = useDispatch();
  const diskData = useSelector((state) => state.admin?.diskData);
  const jvmData = useSelector((state) => state.admin?.jvmData);
  const cacheSize = useSelector((state) => state.admin?.cacheSize);
  const cachedReports = useSelector((state) => state.admin?.cachedReports);
  const cachedDataSources = useSelector((state) => state.admin?.cachedDataSources);
  const reportStats = useSelector((state) => state.admin?.reportStats);
  const dataSourceCount = useSelector((state) => state.admin?.dataSourceCount);
  const isFetchedReportStats = useSelector(
    (state) => state.admin?.isFetched?.reportStats
  );
  const isFetchedDataSourceCount = useSelector(
    (state) => state.admin?.isFetched?.dataSourceCount
  );

  const isFetchedDisk = useSelector((state) => state.admin?.isFetched?.diskData);
  const isFetchedJvm = useSelector((state) => state.admin?.isFetched?.jvmData);
  const isFetchedCache = useSelector((state) => state.admin?.isFetched?.cacheSize);
  const isFetchedReports = useSelector((state) => state.admin?.isFetched?.cachedReports);
  const isFetchedCachedDs = useSelector(
    (state) => state.admin?.isFetched?.cachedDataSources
  );

  const isSystemDataReady =
    isFetchedDisk &&
    isFetchedJvm &&
    isFetchedCache &&
    isFetchedReports &&
    isFetchedCachedDs;

  useEffect(() => {
    if (!isFetchedReportStats) fetchReportStats();
    if (!isFetchedDataSourceCount) fetchDataSourceCount();
  }, []);

  const fetchReportStats = (refresh = false) => {
    if (!isFetchedReportStats || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemReportStats,
        (res) => {
          dispatch(storeReportStats(res));
          dispatch(updateIsFetched({ type: "reportStats", value: true }));
        },
        () => {
          dispatch(updateIsFetched({ type: "reportStats", value: true }));
        }
      );
    }
  };

  const fetchDataSourceCount = (refresh = false) => {
    if (!isFetchedDataSourceCount || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemdatasourceCount,
        (res) => {
          dispatch(storeDataSourceCount(res));
          dispatch(updateIsFetched({ type: "dataSourceCount", value: true }));
        },
        () => {
          dispatch(updateIsFetched({ type: "dataSourceCount", value: true }));
        }
      );
    }
  };

  const refreshDashboard = () => {
    dispatch(updateIsFetched({ type: "reportStats", value: false }));
    dispatch(updateIsFetched({ type: "dataSourceCount", value: false }));
    apiRef.current = Promise.all([
      fetchReportStats({ refresh: true }),
      fetchDataSourceCount({ refresh: true }),
    ]);
  };

  if (!isSystemDataReady) {
    return (
      <>
        <LoadingBar handleClick={handleAbort} />
        <CustomSkeletonCard />
      </>
    );
  }

  const chartData = [
    {
      category: "Disk Used (GB)",
      value: parseInt(diskData.usedSpace / 1024) || 0,
      displayValue: `${parseInt(diskData.usedSpace / 1024) || 0} GB`,
    },
    {
      category: "JVM Used (MB)",
      value: parseInt(jvmData.usedMemory) || 0,
      displayValue: `${jvmData.usedMemory || 0} ${jvmData.unit || "MB"}`,
    },
    {
      category: "Cache Items",
      value: parseInt(cacheSize?.size) || 0,
      displayValue: `${cacheSize?.size || 0}`,
    },
    {
      category: "Cached Reports",
      value: cachedReports?.length || 0,
      displayValue: `${cachedReports?.length || 0}`,
    },
    {
      category: "Cached DS",
      value: cachedDataSources?.dataSources?.length || 0,
      displayValue: `${cachedDataSources?.dataSources?.length || 0}`,
    },
    {
      category: "Total Reports",
      value: parseInt(reportStats?.reportsCount) || 0,
      displayValue: `${reportStats?.reportsCount || 0}`,
    },
    {
      category: "Data Sources",
      value: parseInt(dataSourceCount?.dataSourceCount) || 0,
      displayValue: `${dataSourceCount?.dataSourceCount || 0}`,
    },
  ];

  return (
    <Card
      size="small"
      className="hi-overview hi-overview-dashboard-chart"
      title={<div data-testid="hi-system-overview-chart-title">System Overview</div>}
      extra={
        <Tooltip title="Refresh dashboard metrics" placement="left">
          <SyncOutlined
            data-testid="hi-system-overview-chart-refresh"
            onClick={refreshDashboard}
          />
        </Tooltip>
      }
    >
      <Row>
        <Col span={24}>
          <Column {...getColumnConfig({ data: chartData, height: 240 })} />
        </Col>
      </Row>
    </Card>
  );
};

export { HISystemOverviewChart };
