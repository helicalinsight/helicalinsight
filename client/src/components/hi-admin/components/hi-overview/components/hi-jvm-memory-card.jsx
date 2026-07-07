import React, { useEffect } from "react";
import { Row, Col, Card, Progress, Tooltip, Skeleton } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SyncOutlined } from "@ant-design/icons";
import "../index.scss";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeJVMData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import CustomSkeletonCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletoncard";

const HIJVMMemoryCard = ({ handleAbort, apiRef }) => {
  const jvmMemory = useSelector((state) => state.admin?.jvmData);
  const isFetched = useSelector((state) => state.admin?.isFetched?.jvmData);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const { totalMemory, usedMemory, maxMemory, freeMemory, unit } = jvmMemory
    ? jvmMemory
    : {};

  useEffect(() => {
    fetchJVMData();
  }, []);

  const fetchJVMData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        { action: "memory" },
        uriConfig.monitorSystemSystemInfo,
        (res) => {
          dispatch(storeJVMData(res));
          dispatch(updateIsFetched({ type: "jvmData", value: true }));
        },
        (e) => {
          // Notify.error({ ...e, type: "Network Call" });
          dispatch(updateIsFetched({ type: "jvmData", value: true }));
        }
      );
    }

  };

  const jvmTooltip = (
    <span>
      Total JVM Memory : {totalMemory} {unit} <br />
      Free JVM Memory : {freeMemory} {unit} <br />
      Used JVM Memory : {usedMemory} {unit} <br />
      Maximum JVM Memory : {maxMemory} {unit}
    </span>
  );

  const progressPercent = parseInt(
    (parseInt(usedMemory) / parseInt(totalMemory)) * 100
  );

  return (
    <>
      {!isFetched ? (
        <>
        <LoadingBar handleClick={handleAbort} />
        <CustomSkeletonCard />
  </>  
        ) : ( 
        <Card
          size="small"
          title={<div data-testid="hi-jvm-memory-title">JVM Memory</div>}
          className="hi-overview hi-overview-border-box"
          hoverable={isFetched}
          extra={
            <Tooltip title={<span>Refresh</span>} placement="left">
              <SyncOutlined
                onClick={() => {
                  dispatch(updateIsFetched({ type: "jvmData", value: false }));

                  apiRef.current = fetchJVMData({ refresh: true });
                }}
              />
            </Tooltip>
          }
        >
          <div className="hi-overview-padding">
            <h1 className="hi-overview-stats">
              {usedMemory ? `${usedMemory} ${unit}` : "----"}
            </h1>
            <Row className="hi-overview-antdrow" justify="space-between">
              <Col span={16}>
                <p className="hi-overview-main-heading">Total JVM memory</p>
              </Col>
              <Col>
                <Tooltip placement="bottom" title={jvmTooltip}>
                  <p className="hi-overview-main-heading">
                    {totalMemory && isFetched ? `${totalMemory} ${unit}` : "----"}
                  </p>
                </Tooltip>
              </Col>
            </Row>
          </div>
          <Progress
            trailColor="#ccc"
            strokeLinecap="square"
            percent={progressPercent}
            showInfo={false}
            strokeColor="#1ec469"
            strokeWidth={6}
          />
        </Card>
        )}
    </>
  );
};

export { HIJVMMemoryCard };
