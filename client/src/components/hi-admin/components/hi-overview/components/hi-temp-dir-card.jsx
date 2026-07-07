import React, { useState, useEffect } from "react";
import {
  Row,
  Col,
  Card,
  Progress,
  Drawer,
  Table,
  Button,
  Tooltip,
  Skeleton,
  Popconfirm
} from "antd";
import { useSelector, useDispatch } from "react-redux";
import {
  SyncOutlined,
  DeleteOutlined,
  SmallDashOutlined,
  ExclamationCircleFilled
} from "@ant-design/icons";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/admin.request";
import {
  storeTempData,
  updateIsFetched,
} from "../../../../../redux/actions/admin.actions";
import "../index.scss";
import notify from "../../../../hi-notifications/notify";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../../common/components/Hi-Popconfirm";
import formatDate from "../../../../common/components/formatDate";
import CustomSkeletonCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletoncard";
const HITempDirCard = ({ handleAbort, apiRef }) => {
  const tempData = useSelector((state) => state.admin?.tempData);
  const isFetched = useSelector((state) => state.admin?.isFetched?.tempData);
  const [visible, setVisible] = useState(false);
  const [selectedTempFiles, setSelectedTempFiles] = useState([]);
  const disabledButtonCheck = !tempData?.tempFileArray?.length;

  const dispatch = useDispatch();
  const total = tempData?.tempFileArray
    ? tempData?.tempFileArray.reduce((total, obj) => obj.fileSize + total, 0)
    : undefined;

  const tempTooltip = <span>No. of temporary files</span>;
  const Notify = notify(dispatch);

  useEffect(() => {
    fetchTempData();
  }, []);

  const fetchTempData = (refresh = false) => {
    if (!isFetched || refresh) {
      return requests.admin(dispatch).postAdminRequest(
        { action: "list" },
        uriConfig.monitorSystemTempFile,
        (res) => {
          dispatch(storeTempData(res));
          dispatch(updateIsFetched({ type: "tempData", value: true }));
        },
        () => {
          dispatch(updateIsFetched({ type: "tempData", value: true }));
        }
      );
    }
  };

  const deleteTempData = () => {
    requests.admin(dispatch).postAdminRequest(
      { action: "deleteAll" },
      uriConfig.monitorSystemTempFile,
      (s) => {
        // Notify.success({ ...s, type: "Network Call" });
        fetchTempData({ refresh: true });
      },
      () => {
        dispatch(updateIsFetched({ type: "tempData", value: true }));
      }
    );
  };

  const deleteSelectedTempFiles = () => {
    const allTempFileNames = tempData?.tempFileArray?.map((item) => item.fileName);
    requests.admin(dispatch).postAdminRequest(
      { action: "delete", files: !selectedTempFiles.length ? allTempFileNames : selectedTempFiles },
      uriConfig.monitorSystemTempFile,
      (res) => {
        setSelectedTempFiles([]);
        console.log(res);
        // Notify.success({ ...res, type: "Network Call" });
        fetchTempData({ refresh: true });
      },
      () => {
        dispatch(updateIsFetched({ type: "tempData", value: true }));
      }
    );
  };

  const columns = [
    {
      title: () => <Tooltip title="File Name">File Name</Tooltip>,
      dataIndex: "fileName",
      className: "sorter-no-tooltip table-ellipsis",
      render: (path) => (
        <Tooltip placement="topLeft" title={path}>
          {path}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Size">Size</Tooltip>,
      dataIndex: "fileSize",
      className: "sorter-no-tooltip table-ellipsis",
      render: (path) => (
        <Tooltip placement="topLeft" title={`${parseInt(path / 1024)}kB`}>
          {`${parseInt(path / 1024)} KB`}
        </Tooltip>
      ),
    },
    {
      title: () => <Tooltip title="Created At">Created At</Tooltip>,
      dataIndex: "lastModified",
      className: "sorter-no-tooltip table-ellipsis",
      render: (path) => (
        <Tooltip placement="top" title={formatDate(path)}>
          {formatDate(path)}
        </Tooltip>
      ),
    },
  ];

  const onClose = () => {
    setVisible(false);
    setSelectedTempFiles([]);
  };
  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      const tempFiles = selectedRows.map((item) => item.fileName);
      setSelectedTempFiles(tempFiles);
    },
    onSelect: (record, selectedRowKeys, selectedRows) => {
      const tempFiles = selectedRows.map((item) => item.fileName);
      setSelectedTempFiles(tempFiles);
    },
    onSelectAll: (record, selectedRowKeys, selectedRows) => {
      const tempFiles = selectedRows.map((item) => item.fileName);
      setSelectedTempFiles(tempFiles);
    },
  };
  let tempProgressPercent = tempData?.tempFileArray?.length || 0;
  tempProgressPercent = tempProgressPercent > 100 ? 100 : tempProgressPercent;

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
          title={<div data-testid="hi-temp-directory-title">Temp Directory</div>}
          className="hi-overview hi-overview-border-box"
          hoverable={isFetched}
          extra={
            <Row gutter={[16, 16]}>
              <Col>
                <Tooltip

                  title={
                    <span className="tooltip-refresh">
                      View temp resources/files
                    </span>
                  }
                  placement="left"
                >
                  <SmallDashOutlined
                    data-testid="hi-temp-directory-button"
                    onClick={() => {
                      setVisible(true);
                    }}
                  />
                </Tooltip>
                <Drawer
                  data-testid="hi-temp-dir-card-drawer-title"
                  title={
                    <span className="hi-drawer-title">
                      Application Temp Files
                    </span>
                  }
                  placement="right"
                  width={"60%"}
                  className="my-drawer"
                  onClose={onClose}
                  visible={visible}
                  extra={
                    <>
                      {tempData?.tempFileArray?.length ?
                        <Popconfirm
                          title={<PopconfirmBody intent="delete" />}
                          onConfirm={() => {
                            dispatch(
                              updateIsFetched({ type: "tempData", value: false })
                            );
                            deleteSelectedTempFiles();
                            onClose();
                          }
                          }
                          placement="bottomLeft"
                        >
                          <Button
                            disabled={disabledButtonCheck}

                          >
                            {!selectedTempFiles.length
                              ? tempData?.tempFileArray?.length
                                ? "Delete All"
                                : `Delete Selected (${selectedTempFiles.length})`
                              : `Delete Selected (${selectedTempFiles.length})`}
                          </Button>
                        </Popconfirm>
                        : <Button
                          disabled={disabledButtonCheck}

                        >
                          {`Delete Selected (${selectedTempFiles.length})`}
                        </Button>}</>
                  }
                >
                  <Row gutter={[16, 16]}>
                    <Col span={24}>
                      <Table
                        rowSelection={{
                          type: "checkbox",
                          selectedRowKeys: [...selectedTempFiles],
                          ...rowSelection,
                        }}
                        rowClassName={(record, index) => {
                          let className = index % 2 && "table-row-color";
                          return className;
                        }}
                        showSorterTooltip={false}
                        rowKey="fileName"
                        columns={columns}
                        dataSource={tempData?.tempFileArray}
                      />
                    </Col>
                    <Col></Col>
                  </Row>
                </Drawer>
              </Col>
              <Col>
                <Tooltip
                  title={
                    <span className="tooltip-refresh">
                      Delete all temp resources older than 24hrs
                    </span>
                  }
                  placement="left"
                >
                  <Popconfirm
                    title={<PopconfirmBody intent="delete" />}
                    onConfirm={() => {
                      dispatch(
                        updateIsFetched({ type: "tempData", value: false })
                      );
                      deleteTempData();
                    }
                    }
                    placement="left"
                  >
                    <DeleteOutlined />
                  </Popconfirm>
                </Tooltip>
              </Col>
              <Col>
                <Tooltip
                  title={<span className="tooltip-refresh">Refresh</span>}
                  placement="left"
                >
                  <SyncOutlined
                    data-testid="hi-temp-dir-card-refresh-icon"
                    onClick={() => {
                      dispatch(
                        updateIsFetched({ type: "tempData", value: false })
                      );

                      apiRef.current = fetchTempData({ refresh: true });
                    }}
                  />
                </Tooltip>
              </Col>
            </Row>
          }
        >
          <div className="hi-overview-padding">
            <h1 className="hi-overview-stats">
              {isNaN((total / 1024).toFixed(2))
                ? "----"
                : `${(total / 1024).toFixed(2)} KB`}
            </h1>
            <Row className="hi-overview-antdrow" justify="space-between">
              <Col span={20}>
                <p data-testid="hi-temp-dir-card-total" className="hi-overview-main-heading">
                  Total temporary file(s)
                </p>
              </Col>
              <Col>
                <Tooltip placement="left" title={tempTooltip}>
                  <p
                    data-testid="hi-temp-directory-no-of-files"
                    className="hi-overview-main-heading"
                  >
                    {tempData?.tempFileArray
                      ? tempData?.tempFileArray?.length
                      : "----"}
                  </p>
                </Tooltip>
              </Col>
            </Row>
          </div>
          <Progress
            trailColor="#ccc"
            strokeLinecap="square"
            percent={tempProgressPercent}
            showInfo={false}
            strokeColor="#1ec469"
            strokeWidth={6}
          />
        </Card>
      )}
    </>

  );
};

export { HITempDirCard };
