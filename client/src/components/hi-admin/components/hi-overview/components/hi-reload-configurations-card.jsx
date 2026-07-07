import React, { useState } from "react";
import { Row, Col, Button, Tooltip, Typography, Card } from "antd";
import { useDispatch } from "react-redux";
import requests from "../../../../../base/requests";
import {
  storeReloadAppData,
  storeReloadValidData,
  storeReloadCacheData,
} from "../../../../../redux/actions/admin.actions";
import { uriConfig } from "../../../../../base/requests/admin.request";
import "../index.scss";
import notify from "../../../../hi-notifications/notify";
import { SyncOutlined } from "@ant-design/icons";

const { Text } = Typography;

const HIReloadConfigurationCard = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [isLoading, setIsLoading] = useState({
    reloadApp: false,
    reloadValid: false,
    reloadCache: false,
  });

  const fetchReloadApplicationData = () => {
    requests.admin(dispatch).postAdminRequest(
      { refresh: true },
      uriConfig.monitorCacheUpdateConfiguration,
      (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(storeReloadAppData(res.message));
        setIsLoading((state) => ({ ...state, reloadApp: false }));
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        setIsLoading(false);
      }
    );
  };

  const fetchValidationData = () => {
    requests.admin(dispatch).postAdminRequest(
      { refresh: "validation" },
      uriConfig.monitorCacheRefresh,
      (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(storeReloadValidData(res.message));
        setIsLoading((state) => ({ ...state, reloadValid: false }));
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        setIsLoading(false);
      }
    );
  };

  const fetchReloadCacheData = () => {
    requests.admin(dispatch).postAdminRequest(
      { refresh: "cache" },
      uriConfig.monitorCacheRefresh,
      (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(storeReloadCacheData(res.message));
        setIsLoading((state) => ({ ...state, reloadCache: false }));
      },
      () => {
        setIsLoading(false);
      }
    );
  };
  return (
    <Card
      data-testid="hi-reload-config-card-title"
      size="small"
      hoverable={true}
      title="Reload Configurations"
      className="hi-overview-border-box"
    >
      <Row className="hi-overview-padding-3" justify="center" gutter={[16, 8]}>
        <Col md={8} lg={8} xl={8}>
          <Tooltip placement="left" title="Reload settings.xml changes">
            <Button
              data-testid="hi-reload-config-card-buttonOne"
              onClick={() => {
                setIsLoading((state) => ({ ...state, reloadApp: true }));
                fetchReloadApplicationData();
              }}
              className="hi-reload-configs-button"
              // loading={isLoading.reloadApp}
              icon={<SyncOutlined style={{ height: 14 }} spin={isLoading.reloadApp} />}
            >
              <Text data-testid="hi-reload-config-card-textOne" className="hi-ellipses" ellipsis="true">
                Application
              </Text>
            </Button>
          </Tooltip>
        </Col>
        <Col md={8} lg={8} xl={8}>
          <Tooltip placement="top" title="Reload validation.xml changes">
            <Button
              data-testid="hi-reload-config-card-buttonTwo"
              ellipses="true"
              className="hi-reload-configs-button"
              onClick={() => {
                setIsLoading((state) => ({ ...state, reloadValid: true }));
                fetchValidationData();
              }}
              // loading={isLoading.reloadValid}
              icon={<SyncOutlined style={{ height: 14 }} spin={isLoading.reloadValid} />}
            >
              <Text data-testid="hi-reload-config-card-textTwo" className="hi-ellipses" ellipsis="true">
                Validation
              </Text>
            </Button>
          </Tooltip>
        </Col>
        <Col md={8} lg={8} xl={8}>
          <Tooltip placement="topLeft" title="Reload cache.xml changes"> {/* bug 6122 */}
            <Button
              data-testid="hi-reload-config-card-buttonThree"
              className="hi-reload-configs-button"
              onClick={() => {
                setIsLoading((state) => ({ ...state, reloadCache: true }));
                fetchReloadCacheData();
              }}
              // loading={isLoading.reloadCache}
              icon={<SyncOutlined style={{ height: 14 }} spin={isLoading.reloadCache} />}
            >
              <Text data-testid="hi-reload-config-card-textThree" className="hi-ellipses" ellipsis="true">
                Cache
              </Text>
            </Button>
          </Tooltip>
        </Col>
      </Row>
    </Card>
  );
};
export { HIReloadConfigurationCard };
