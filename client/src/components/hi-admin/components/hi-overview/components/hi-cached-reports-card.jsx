import React, { useState, useEffect } from "react";
import { Row, Col, Card, Tooltip, Skeleton, Drawer, Table, Button, Popconfirm } from "antd";
import { useSelector, useDispatch } from "react-redux";
import {
  SyncOutlined,
  DeleteOutlined,
  SmallDashOutlined,
  ExclamationCircleFilled
} from "@ant-design/icons";
import "../index.scss";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeCachedReports,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm";
import CacheDataCard from "../../../../common/custom-icons/CustomSkeletons/CachedDataCard";

const HICachedReportsCard = ({ handleAbort, apiRef }) => {
  const cachedReports = useSelector((state) => state.admin?.cachedReports);
  const isFetched = useSelector(
    (state) => state.admin?.isFetched?.cachedReports
  );
  const disabledButtonCheck = !cachedReports?.length;
  const [visible, setVisible] = useState(false);
  const [selectedPaths, setSelectedPaths] = useState([]);
  const [deleteAll, setDeleteAll] = useState(false);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  useEffect(() => {
    fetchCachedReports();
  }, []);

  const fetchCachedReports = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        { dir: "/" },
        uriConfig.monitorCacheDump,
        (res) => {
          dispatch(storeCachedReports(res.reportList));
          dispatch(updateIsFetched({ type: "cachedReports", value: true }));
        },
        (e) => {
          // Notify.error({ ...e, type: "Network Call" });
          dispatch(updateIsFetched({ type: "cachedReports", value: true }));
        }
      );
    }
  };

  const deleteCachedReports = () => {
    requests.admin(dispatch).postAdminRequest(
      { dir: ["/"] },
      uriConfig.monitorCacheClean,
      (s) => {
        // Notify.success({ ...s, type: "Network Call" });
        fetchCachedReports({ refresh: true });
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "cachedReports", value: true }));
      }
    );
  };

  const deleteSelectedCachedReports = () => {
    const allPaths = cachedReports.map((item) => item.path);
    requests.admin(dispatch).postAdminRequest(
      {
        dir: !selectedPaths.length || deleteAll ? allPaths : selectedPaths,
      },
      uriConfig.monitorCacheClean,
      (s) => {
        // Notify.success({ ...s, type: "Network Call" });
        setSelectedPaths([]);
        fetchCachedReports({ refresh: true });
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "cachedReports", value: true }));
      }
    );
  };

  const onClose = () => {
    setVisible(false);
    setSelectedPaths([]);
  };

  const columns = [
    {
      title: () => <Tooltip title="File">File</Tooltip>,
      dataIndex: "path",
      className: "table-ellipsis",
      render: (path) => (
        <Tooltip placement="topLeft" title={path}>
          {path}
        </Tooltip>
      ),
    },
  ];

  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      const paths = selectedRows?.map((item) => item.path);
      setDeleteAll(false);
      setSelectedPaths(paths);
    },
    onSelect: (record, selectedRowKeys, selectedRows) => {
      const paths = selectedRows?.map((item) => item.path);
      setDeleteAll(false);
      setSelectedPaths(paths);
    },
    onSelectAll: (record, selectedRowKeys, selectedRows) => {
      const paths = selectedRows?.map((item) => item.path);
      setDeleteAll(true);
      setSelectedPaths(paths);
    },
  };

  return (
    <>
      {!isFetched
        ? (<>
          <LoadingBar handleClick={handleAbort} />
          <CacheDataCard />
        </>
        ) : (
          <Card
            hoverable={isFetched}
            className="hi-overview hi-overview-border-box hi-overview-border-right hi-overview-padding-cache"
          >

            <Row justify="space-between">
              <Col
                className="hi-overview-main-heading hi-overview-cache"
                span={14}
              ></Col>
              <Col>
                <Row gutter={[16, 16]}>
                  <Col>
                    <Tooltip placement="left" title="View cached reports">
                      <SmallDashOutlined
                        data-testid="hi-cached-reports-card-button"
                        onClick={() => {
                          setVisible(true);
                        }}
                      />
                    </Tooltip>
                    <Drawer
                      title={
                        <span className="hi-drawer-title">Cached Reports</span>
                      }
                      placement="right"
                      width={"60%"}
                      onClose={onClose}
                      visible={visible}
                      extra={
                        <>
                          {cachedReports?.length ?
                            <Popconfirm
                              title={<PopconfirmBody intent="delete" />}
                              onConfirm={() => {
                                dispatch(
                                  updateIsFetched({
                                    type: "cachedReports",
                                    value: false,
                                  })
                                );
                                deleteSelectedCachedReports();
                                onClose();
                              }
                              }
                              placement="bottomLeft"
                            >
                              <Button
                                disabled={disabledButtonCheck}

                              >
                                {!selectedPaths.length
                                  ? cachedReports?.length
                                    ? "Delete All"
                                    : `Delete Selected (${selectedPaths.length})`
                                  : `Delete Selected (${selectedPaths.length})`}
                              </Button>
                            </Popconfirm>
                            : <Button
                              disabled={disabledButtonCheck}

                            >
                              {`Delete Selected (${selectedPaths.length})`}
                            </Button>}</>
                      }

                    >
                      <Table
                        position={["bottomLeft"]}
                        rowSelection={{
                          type: "checkbox",
                          selectedRowKeys: [...selectedPaths],
                          ...rowSelection,
                        }}
                        rowClassName={(record, index) => {
                          let className = index % 2 && "table-row-color";
                          return className;
                        }}
                        rowKey="path"
                        columns={columns}
                        dataSource={cachedReports}
                      />
                    </Drawer>
                  </Col>
                  <Col>
                    <Tooltip placement="left" title="Delete all cached reports">
                      <Popconfirm
                        title={<PopconfirmBody intent="delete" />}
                        onConfirm={() => {
                          dispatch(
                            updateIsFetched({ type: "cachedReports", value: false })
                          );
                          deleteCachedReports();
                        }
                        }
                        placement="top"
                      >
                        <DeleteOutlined
                          data-testid="hi-data-cached-reports-card-del-icon"
                        />
                      </Popconfirm>
                    </Tooltip>
                  </Col>
                  <Col>
                    <Tooltip placement="left" title="Refresh">
                      <SyncOutlined
                        data-testid="hi-data-cached-reports-card-refresh-icon"
                        onClick={() => {
                          dispatch(
                            updateIsFetched({ type: "cachedReports", value: false })
                          );
                          apiRef.current = fetchCachedReports({ refresh: true });
                        }}
                      />
                    </Tooltip>
                  </Col>
                </Row>
              </Col>
            </Row>
            <p
              data-testid="hi-cached-reports-size"
              className="hi-overview-stats hi-overview-no-bold "
            >
              {isFetched ? cachedReports?.length : "---"}
            </p>
            <p
              data-testid="hi-cached-reports-title"
              className="hi-overview-main-heading"
            >
              Cached reports
            </p>
          </Card>
        )
      }
    </>
  );
};

export { HICachedReportsCard };
