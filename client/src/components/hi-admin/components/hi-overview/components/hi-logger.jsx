import React, { useEffect } from "react";
import { Row, Col, Menu, Dropdown, Tooltip, Card, Skeleton } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SyncOutlined, DownOutlined } from "@ant-design/icons";
import "../index.scss";
import requests from "../../../../../base/requests";
import {
  storeCurrentLevelData,
  storeLogOptionsData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import { uriConfig } from "../../../../../base/requests/admin.request";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import LoggerSkeletonCard from "../../../../common/custom-icons/CustomSkeletons/LoggerSkeletonCard";

const HILoggerCard = ({ handleAbort, apiRef }) => {
  const currentLevel = useSelector((state) => state.admin?.currentLevel);
  const isFetched = useSelector((state) => state.admin?.isFetched?.currentLogLevel);
  const logOptions = useSelector((state) => state.admin.logOptions);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  useEffect(() => {
    fetchLogData();
    fetchLogOptionsData();
  }, []);

  const fetchLogData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        { getLevel: "currentLevel" },
        uriConfig.monitorSystemLog,
        (res) => {
          dispatch(storeCurrentLevelData(res.currentLevel));
          dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
        },
        (e) => {
          // Notify.error({ ...e, type: "Network Call" });
          dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
        }
      );
    }
  };

  const setLogData = (value) => {
    requests.admin(dispatch).postAdminRequest(
      { setLevel: value },
      uriConfig.monitorSystemLog,
      (res) => {
        dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(storeCurrentLevelData(res.currentLevel));
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
      }
    );
  };

  const fetchLogOptionsData = () => {
    !logOptions?.length &&
      requests.admin(dispatch).postAdminRequest(
        { getLevel: "options" },
        uriConfig.monitorSystemLog,
        (res) => {
          dispatch(storeLogOptionsData(res?.data));
          // dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
        },
        (e) => {
          // Notify.error({ ...e, type: "Network Call" });
          // dispatch(updateIsFetched({ type: "currentLogLevel", value: true }));
        }
      );
  };

  const menu = (
    <Menu>
      {logOptions?.map((item) => (
        <Menu.Item
          onClick={() => {
            dispatch(updateIsFetched({ type: "currentLogLevel", value: false }));
            setLogData(item);
          }}
          key={item}
          value={item}
        >
          <span>{item}</span>
        </Menu.Item>
      ))}
    </Menu>
  );

  return (
    <>
      {!isFetched ? 
      ( 
      <>
      <LoadingBar handleClick={handleAbort} />
      <LoggerSkeletonCard />
      </>
  ) : ( 
      
        <Card
          title="Logger Settings"
          size="small"
          hoverable={isFetched}
          extra={
            <Tooltip placement="left" title="Refresh">
              <SyncOutlined
                onClick={() => {
                  dispatch(updateIsFetched({ type: "currentLogLevel", value: false }));
                  apiRef.current = fetchLogData({ refresh: true });
                }}
              />
            </Tooltip>
          }
          className="hi-overview hi-overview-border-box"
        >
          <Row type="flex" justify="center" align="middle" className="hi-overview-logger">
            <Col span={24}>
              <Dropdown overlay={menu}>
                <span>
                  <span className="hi-overview-stats hi-logger-stats ">
                    {currentLevel.toUpperCase()}
                  </span>
                  <DownOutlined />
                </span>
              </Dropdown>
            </Col>
          </Row>
        </Card>
  )}
</>

  );
};

export { HILoggerCard };
