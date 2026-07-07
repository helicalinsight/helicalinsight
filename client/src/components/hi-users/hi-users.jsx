import React, { useState, useEffect } from "react";
import { Button, List, Card, Row, Col, Typography, Tooltip } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { LoadData } from "../../lib/pagenation";
import { FundOutlined, DatabaseOutlined } from "@ant-design/icons";
import "./hi-users.scss";
import {
  storeReportStats,
  updateIsFetched,
  storeDataSourceCount,
} from "../../redux/actions/admin.actions";
import requests from "../../base/requests";
import { uriConfig } from "../../base/requests/admin.request";
import notify from "../hi-notifications/notify";
import { HIRecentReportsCard } from "./components";
import TutorialInfo from "../../components/common/hi-tutorial";
import HITooltip from "../common/components/hi-tooltip";
import { fetchWhatsNewContent } from "./utils/what's-new-api";
import { uuid } from "../../utils/uuid";

const { Title, Text } = Typography;

const HIUsers = (props) => {
  // const [count, setCount] = useState(1);
  const { customRole } = props;
  const { applicationSettingsData } = useSelector((state) => state.app) || {};
  const isFetchedReportsStats = useSelector(
    (state) => state.admin?.isFetched?.reportStats
  );
  const isFetchedDataSourceCount = useSelector(
    (state) => state.admin?.isFetched?.dataSourceCount
  );
  const isFetchedWhatsNewContent=useSelector(
    (state) => state.admin?.isFetched?.whatsNewContent
  );

  const reportsCount = useSelector(
    (state) => state.admin.reportStats?.reportsCount
  );
  const latestReports = useSelector(
    (state) => state.admin.reportStats?.latestReports
  );
  const dataSourceCount = useSelector(
    (state) => state.admin.dataSourceCount?.dataSourceCount
  );
  const whatsNewContent = useSelector(
    (state) => state.admin.whatsNewContent
  );
  let { user } = applicationSettingsData?.userData || {};
  // const { data, loaded, onNext } = LoadData({ key: "users" });
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  // if (!loaded) {
  //   return <div>Loading....aaa..</div>;
  // }
  useEffect(() => {
    if (!customRole) {
      fetchReportStats();
      fetchDataSourceCount();
    }
    fetchWhatsNewContent({dispatch,isFetchedWhatsNewContent})
  }, []);
  const fetchReportStats = (refresh = false) => {
    (!isFetchedReportsStats || refresh) &&
      requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemReportStats,
        (res) => {
          dispatch(storeReportStats(res));
          dispatch(updateIsFetched({ type: "reportStats", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "reportStats", value: true }));
          // Notify.error({ ...e, type: "Network Call" });
        }
      );
  };
  const fetchDataSourceCount = (refresh = false) => {
    (!isFetchedDataSourceCount || refresh) &&
      requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemdatasourceCount,
        (res) => {
          dispatch(storeDataSourceCount(res));
          dispatch(updateIsFetched({ type: "dataSourceCount", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "dataSourceCount", value: true }));
          // Notify.error({ ...e, type: "Network Call" });
        }
      );
  };

  return (
    <Row gutter={[8, 8]} justify="center" className="hi-users">
      <Col xs={24} md={12} lg={6}>
        <TutorialInfo elementKey="hi-user">
          <Card data-testid = "hi-users-card" hoverable className="hi-users-card text-align-center">
            <Row gutter={[8, 8]}>
              <Col span={12} offset={6}>
                <Button
                  className="hi-users-username-icon"
                  type="primary"
                  shape="circle"
                >
                  {user?.name?.split("").slice(0, 2).join("").toUpperCase()}
                </Button>
              </Col>
              <Col span={12} offset={6}>
                <Title level={3}> {user?.name?.toUpperCase()}</Title>
              </Col>
              <Col className="text-align-none" span={24}>
                <HITooltip
                  title={user.profile
                    ?.map((item) => item.profileName)
                    ?.join(",")}
                >
                  <Text>
                    {" "}
                    Profiles:{" "}
                    {user.profile
                      ?.map((item) => item.profileName)
                      ?.join(",")}{" "}
                  </Text>
                </HITooltip>
              </Col>
              <Col className="text-align-none" span={24}>
                <Tooltip title={user?.roles?.join(",")}>
                  <Text>Roles: {user?.roles?.join(",")}</Text>
                </Tooltip>
              </Col>
            </Row>
          </Card>
        </TutorialInfo>
      </Col>

      <Col xs={24} md={12} lg={6}>
        <TutorialInfo elementKey="hi-stats">
          <Card hoverable className="hi-users-card text-align-center">
            <Row gutter={[8, 8]}>
              <Col span={12}>
                <FundOutlined className="hi-users-icon" />
              </Col>
              <Col span={12}>
                <Row align="middle">
                  <Col span={24}>
                    <Text strong className="stats">
                      {reportsCount}
                    </Text>
                  </Col>
                  <Col span={24}>
                    <Text className="hi-users-text">Reports</Text>
                  </Col>
                </Row>
              </Col>
              <Col span={12}>
                <DatabaseOutlined className="hi-users-icon" />
              </Col>
              <Col span={12}>
                <Row align="middle">
                  <Col span={24}>
                    <Text strong className="stats">
                      {dataSourceCount}
                    </Text>
                  </Col>
                  <Col span={24}>
                    <Text className="hi-users-text">Data Sources</Text>
                  </Col>
                </Row>
              </Col>
            </Row>
          </Card>
        </TutorialInfo>
      </Col>
      <Col xs={24} lg={12}>
        <Card hoverable className="hi-users-card">
          <Title data-testid="hi-whats-new" level={4}>What's New?</Title>
          <List
            loading={!isFetchedWhatsNewContent}
            size="small"
            rowKey={uuid()}
            dataSource={whatsNewContent}
            renderItem={(item) => (
              <List.Item>&bull;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;{<span dangerouslySetInnerHTML={{ __html: `<span>${item}</span>` }}></span>}</List.Item>
            )}
          />
        </Card>
      </Col>
      <Col xs={24}>
        <Card hoverable className="hi-users-card">
          <Row gutter={[16, 16]}>
            <Col span={24}>
              <Title level={4}>Recent Reports</Title>
            </Col>
            {/* {[{ title: "hello" }, { title: "hel" }]} */}
            {latestReports?.map((item) => (
              <HIRecentReportsCard key={uuid()} report={item} />
            ))}
          </Row>
        </Card>
      </Col>
    </Row>
  );
};

export { HIUsers };
