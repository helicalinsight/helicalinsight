import React, { useEffect } from "react";
import { Row, Col, Card, Progress, Tooltip, Skeleton } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SyncOutlined } from "@ant-design/icons";
import "../index.scss";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeDiskData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import { useRef } from "react";
import CustomSkeletonCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletoncard";

const HIDiskSpaceCard = ({ apiRef, handleAbort }) => { // { handleAbort, apiRef }
  const diskSpace = useSelector((state) => state.admin?.diskData);
  const isFetched = useSelector((state) => {
    // console.log(state.admin);
    return state.admin?.isFetched?.diskData;
  });
  const { totalDiskSize, usedSpace, freeSpace } = Object(diskSpace);

  useEffect(() => {
    fetchDiskData();
  }, []);

  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const fetchDiskData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.monitorSystemDiskSpace,
        (res) => {
          dispatch(storeDiskData(res));
          dispatch(updateIsFetched({ type: "diskData", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "diskData", value: true }));

          // Notify.error({ ...e, type: "Network Call" });
        }
      );
    }
  };

  const diskTooltip = (
    <span>
      Total Disk Space : {parseInt(totalDiskSize / 1024)} GB
      <br />
      Used Disk Space : {parseInt(usedSpace / 1024)} GB <br />
      Free Disk Space : {parseInt(freeSpace / 1024)} GB
    </span>
  );

  const progressPercent = parseInt(
    (parseInt(usedSpace) / parseInt(totalDiskSize)) * 100
  );
  // console.log("isFetched", isFetched);
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
          title={<div data-testid="hi-diskspace-title">Disk Space</div>}
          className="hi-overview hi-overview-border-box"
          hoverable={isFetched}
          extra={
            <Col>
              <Tooltip
                title={<span className="tooltip-refresh">Refresh</span>}
                placement="left"
              >
                <SyncOutlined
                  onClick={() => {
                    dispatch(updateIsFetched({ type: "diskData", value: false }));
                    apiRef.current = fetchDiskData({ refresh: true });
                  }}
                />
              </Tooltip>
            </Col>
          }
        >
          <div className="hi-overview-padding">
            <h1 className="hi-overview-stats">
              {isNaN(parseInt(usedSpace / 1024))
                ? "----"
                : `${parseInt(usedSpace / 1024)} GB`}
            </h1>
            <Row className="hi-overview-antdrow" justify="space-between">
              <Col span={18}>
                <p className="hi-overview-main-heading">Total disk space</p>
              </Col>
              <Col>
                <Tooltip placement="bottom" title={diskTooltip}>
                  <p className="hi-overview-main-heading">
                    {isNaN(parseInt(totalDiskSize / 1024))
                      ? "----"
                      : `${parseInt(totalDiskSize / 1024)} GB`}
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

export { HIDiskSpaceCard };
