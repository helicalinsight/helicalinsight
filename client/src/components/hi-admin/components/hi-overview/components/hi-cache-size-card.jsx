import React, { useEffect } from "react";
import { Row, Col, Card, Tooltip, Skeleton, Popconfirm } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SyncOutlined, DeleteOutlined,ExclamationCircleFilled } from "@ant-design/icons";
import "../index.scss";

import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeCacheSize,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm";
import CacheCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletonCacheCard";

const HICacheSizeCard = ({ handleAbort, apiRef }) => {
  const cacheSize = useSelector((state) => state.admin?.cacheSize);
  const isFetched = useSelector((state) => state.admin?.isFetched?.cacheSize);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  useEffect(() => {
    fetchCacheSizeData();
  }, []);

  const fetchCacheSizeData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        { action: "memory" },
        uriConfig.monitorCacheSize,
        (res) => {
          dispatch(storeCacheSize(res));
          dispatch(updateIsFetched({ type: "cacheSize", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "cacheSize", value: true }));
          // Notify.error({ ...e, type: "Network Call" });
        }
      );
    }
  };

  const deleteCacheSizeData = () => {
    requests.admin(dispatch).postAdminRequest(
      {},
      uriConfig.monitorCacheClean,
      (s) => {
        // Notify.success({ ...s, type: "Network Call" });
        fetchCacheSizeData({ refresh: true });
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "cacheSize", value: true }));
      }
    );
  };

  return (
    <>
     
        {!isFetched ? ( 
          <>
          <LoadingBar handleClick={handleAbort} />
          <CacheCard />
       
        </> ) : ( 
           <Card
        hoverable={isFetched}
        className="hi-overview hi-overview-border-box hi-overview-border-right hi-overview-padding-cache hi-cache-size"
      >
          <Row justify="space-between">
            <Col className="hi-overview-main-heading hi-overview-cache" span={14}>
              <span data-testid="hi-cache-size-title">Cache</span>
            </Col>
            <Col>
              <Row gutter={[16, 16]}>
                <Col>
                  <Tooltip title="Delete all in-memory cache" placement="left">
                  <Popconfirm
                  title ={<PopconfirmBody intent="delete"/>}
                  onConfirm={() => {
                  dispatch(
                    updateIsFetched({ type: "cacheSize", value: false })
                  );
                  deleteCacheSizeData();
                }
                }
                placement="top"
              >
                    <DeleteOutlined
                    />
                    </Popconfirm>
                  </Tooltip>
                </Col>
                <Col>
                  <Tooltip title="Refresh" placement="left">
                    <SyncOutlined
                      onClick={() => {
                        dispatch(
                          updateIsFetched({ type: "cacheSize", value: false })
                        );
                        apiRef.current = fetchCacheSizeData({ refresh: true });
                      }}
                    />
                  </Tooltip>
                </Col>
              </Row>
            </Col>
          </Row>
          <p
            data-testid="hi-cache-size-size"
            className="hi-overview-stats hi-overview-no-bold "
          >
            {isFetched ? cacheSize?.size : "---"}
          </p>
          <p className="hi-overview-main-heading">Resources in memory</p>
       
      </Card>
  )}
    </>
  );
};

export { HICacheSizeCard };
