import React, { useState, useEffect } from "react";
import { Row, Col, Card, Table, Tooltip, Skeleton, Button, Drawer, Popconfirm } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SyncOutlined, SmallDashOutlined, ExclamationCircleFilled } from "@ant-design/icons";
import "../index.scss";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeCachedDataSourcesData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm";
import CacheDataCard from "../../../../common/custom-icons/CustomSkeletons/CachedDataCard";

const HIDataSourcesCard = ({ handleAbort, apiRef }) => {
  const cachedDataSources = useSelector(
    (state) => state.admin?.cachedDataSources
  );
  const disabledButtonCheck = !cachedDataSources?.dataSources?.length;
  const isFetched = useSelector(
    (state) => state.admin?.isFetched?.cachedDataSources
  );
  const [selectedIds, setSelectedIds] = useState([]);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [visible, setVisible] = useState(false);

  const onClose = () => {
    setVisible(false);
    setSelectedIds([]);
  };

  useEffect(() => {
    fetchDataSourcesData();
  }, []);

  const fetchDataSourcesData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        {},
        uriConfig.coreDataSourceCachedDS,
        (res) => {
          dispatch(storeCachedDataSourcesData(res));
          dispatch(updateIsFetched({ type: "cachedDataSources", value: true }));
        },
        (e) => {
          dispatch(updateIsFetched({ type: "cachedDataSources", value: true }));
          // Notify.error({ ...e, type: "Network Call" });
        }
      );
    }
  };

  const deleteSelectedDataSources = () => {
    const allIds = cachedDataSources.dataSources.map((item) => ({ id: item.id, baseType: item.baseType }));
    requests.admin(dispatch).postAdminRequest(
      { ids: !selectedIds.length ? allIds : selectedIds },
      uriConfig.coreDataSourceShutdown,
      (s) => {
        // Notify.success({ ...s, type: "Network Call" });
        setSelectedIds([]);
        fetchDataSourcesData({ refresh: true });
      },
      (e) => {
        // Notify.error({ ...e, type: "Network Call" });
        dispatch(updateIsFetched({ type: "cachedDataSources", value: true }));
      }
    );
  };

  const columns = [
    {
      title: () => <Tooltip title="ID">ID</Tooltip>,
      dataIndex: "id",
      className: "table-ellipsis",
      showSorterTooltip: false,
      width: "10%",
      render: (id) => (
        <Tooltip placement="topLeft" title={id}>
          {id}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Name">Name</Tooltip>,
      dataIndex: "name",
      className: "table-ellipsis",

      showSorterTooltip: false,
      render: (name) => (
        <Tooltip placement="topLeft" title={name}>
          {name}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Type">Type</Tooltip>,
      dataIndex: "type",
      className: "table-ellipsis",

      showSorterTooltip: false,
      render: (type) => (
        <Tooltip placement="topLeft" title={type}>
          {type}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Base Type">Base Type</Tooltip>,
      dataIndex: "baseType",
      className: "table-ellipsis",

      showSorterTooltip: false,
      render: (baseType) => (
        <Tooltip placement="topLeft" title={baseType}>
          {baseType}
        </Tooltip>
      ),
    },
    {
      title: () => (
        <Tooltip title="Data Source Provider">Data Source Provider</Tooltip>
      ),
      dataIndex: "dataSourceProvider",
      className: "table-ellipsis",

      showSorterTooltip: false,
      render: (cell) => (
        <Tooltip placement="topLeft" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Cache Status">Cache Status</Tooltip>,
      dataIndex: "isDatabaseMetadataCached",
      className: "table-ellipsis",

      showSorterTooltip: false,
      render: (cell) => (
        <Tooltip placement="topLeft" title={cell}>
          {cell}
        </Tooltip>
      ),
    },
  ];

  const rowSelection = {
    selectedRowKeys: selectedIds.map((item) => item.id),
    onChange: (selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => ({ id: item.id, baseType: item.baseType }));
      setSelectedIds(Ids);
    },
    onSelect: (record, selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => ({ id: item.id, baseType: item.baseType }));
      setSelectedIds(Ids);
    },
    onSelectAll: (record, selectedRowKeys, selectedRows) => {
      const Ids = selectedRows.map((item) => ({ id: item.id, baseType: item.baseType }));
      setSelectedIds(Ids);
    },
  };

  return (
    <>
      {!isFetched ? (
        <>
          <LoadingBar handleClick={handleAbort} />
          <CacheDataCard />
        </>
      ) : (
        <Card
          hoverable={isFetched}
          className="hi-overview hi-overview-border-box hi-overview-no-border hi-overview-padding-cache"
        >
          <Row justify="space-between">
            <Col
              className="hi-overview-main-heading hi-overview-cache"
              span={14}
            ></Col>
            <Col>
              <Row gutter={[16, 16]}>
                <Col>
                  <Tooltip placement="left" title="View cached datasources">
                    <SmallDashOutlined
                      data-testid="hi-data-sources-card-button"
                      onClick={() => {
                        setVisible(true);
                      }}
                    />
                  </Tooltip>
                  <Drawer
                    data-testid="hi-data-sources-card-drawer"
                    title={
                      <span
                        className="hi-drawer-title">Cached Data Sources</span>
                    }
                    placement="right"
                    className="drawer"
                    width={"60%"}
                    onClose={onClose}
                    visible={visible}
                    bodyStyle={{
                      overflow: "auto",
                    }}
                    extra={
                      <>
                        {cachedDataSources?.dataSources?.length ?
                          <Popconfirm
                            title={<PopconfirmBody intent="delete" />}
                            onConfirm={() => {
                              dispatch(
                                updateIsFetched({
                                  type: "cachedDataSources",
                                  value: false,
                                })
                              );
                              deleteSelectedDataSources();
                              onClose();
                            }
                            }
                            placement="bottomLeft"
                          >
                            <Button
                              disabled={disabledButtonCheck}

                            >
                              {!selectedIds.length
                                ? cachedDataSources?.dataSources?.length
                                  ? "Delete All"
                                  : `Delete Selected (${selectedIds.length})`
                                : `Delete Selected (${selectedIds.length})`}
                            </Button>
                          </Popconfirm>
                          : <Button
                            disabled={disabledButtonCheck}

                          >
                            {`Delete Selected (${selectedIds.length})`}
                          </Button>}</>
                    }
                  >
                    <Row gutter={[16, 16]}>
                      <Col span={24}>
                        <Table
                          className="table-in-drawer"
                          position={["bottomLeft"]}
                          rowSelection={{
                            type: "checkbox",
                            selectedRowKeys: [...selectedIds],
                            ...rowSelection,
                          }}
                          showSorterTooltip={false}
                          rowClassName={(record, index) => {
                            let className = index % 2 && "table-row-color";
                            return className;
                          }}
                          rowKey="id"
                          columns={columns}
                          dataSource={cachedDataSources?.dataSources}
                        />
                      </Col>
                      <Col></Col>
                    </Row>
                  </Drawer>
                </Col>
                <Col>
                  <Tooltip placement="left" title="Refresh">
                    <SyncOutlined
                      data-testid="hi-data-sources-card-refresh-icon"
                      onClick={() => {
                        dispatch(
                          updateIsFetched({
                            type: "cachedDataSources",
                            value: false,
                          })
                        );
                        apiRef.current = fetchDataSourcesData({ refresh: true });
                      }}
                    />
                  </Tooltip>
                </Col>
              </Row>
            </Col>
          </Row>
          <p
            data-testid="hi-data-sources-size"
            className="hi-overview-stats hi-overview-no-bold "
          >
            {isFetched ? cachedDataSources?.dataSources?.length : "---"}
          </p>
          <p
            data-testid="hi-data-sources-title"
            className="hi-overview-main-heading"
          >
            Data sources cached
          </p>
        </Card>
      )
      }
    </>
  );
};

export { HIDataSourcesCard };
