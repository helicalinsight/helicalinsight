import { useState, useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Row, Col, Tooltip, Table, Skeleton } from "antd";
import {
  SyncOutlined,
  CheckOutlined,
  CloseOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
} from "@ant-design/icons";
import {
  storeJvmThreadData,
  storeSystemDetailsJvmTableExpand,
} from "../../../../../redux/actions/admin.actions";
import requests from "../../../../../base/requests";
import notify from "../../../../hi-notifications/notify";

import { VList } from "virtuallist-antd";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import JvmSkeleton from "../../../../common/custom-icons/CustomSkeletons/systemDetails/JvmSkeleton";

const JVMThreadcolumns = [
  {
    title: <Tooltip title="Name">Name</Tooltip>,
    className: "table-ellipsis",
    dataIndex: "name",
    render: (cell) => (
      <Tooltip title={cell} placement="topLeft">
        {cell}
      </Tooltip>
    ),
  },
  {
    title: <Tooltip title="Alive">Alive</Tooltip>,
    dataIndex: "alive",
    render: (text) => (text === true ? trueIcon : falseIcon),
  },
  {
    title: <Tooltip title="Daemon">Daemon</Tooltip>,
    dataIndex: "daemon",
    render: (cell) => (cell === true ? trueIcon : falseIcon),
  },
  {
    title: <Tooltip title="Interrupted">Interrupted</Tooltip>,
    dataIndex: "interrupted",
    className: "table-ellipsis",
    render: (cell) => (cell === true ? trueIcon : falseIcon),
  },
  {
    title: <Tooltip title="Priority">Priority</Tooltip>,
    dataIndex: "priority",
    render: (cell) => <Tooltip title={cell}>{cell}</Tooltip>,
  },
  {
    title: <Tooltip title="State">State</Tooltip>,
    dataIndex: "state",
    render: (cell) => (
      <Tooltip title={cell} placement="topLeft">
        {cell}
      </Tooltip>
    ),
  },
  {
    title: <Tooltip title="Id">Id</Tooltip>,
    dataIndex: "id",
    render: (cell) => <Tooltip title={cell}>{cell}</Tooltip>,
  },
  {
    title: <Tooltip title="ThreadGroupName">ThreadGroupName</Tooltip>,
    className: "table-ellipsis",
    dataIndex: "threadGroupName",
    render: (cell) => <Tooltip title={cell}>{cell}</Tooltip>,
  },
];

const trueIcon = (
  <Tooltip title="true">
    <CheckOutlined className="jvm-true-icon" />
  </Tooltip>
);
const falseIcon = (
  <Tooltip title="false">
    <CloseOutlined className="jvm-false-icon" />
  </Tooltip>
);

const JvmThreadTable = ({ apiRef, handleAbort }) => {
  let jvmThreadDataSource = useSelector((store) => store.admin.jvmThreadDetails);
  const expandJvmTable = useSelector((store) => store.admin.jvmTableExpand);

  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [loading, setLoading] = useState(!jvmThreadDataSource ? true : false);

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      fetchJvmThreadDeatils();
    }
  }, []);

  const vComponents = useMemo(() => {
    return VList({
      height: 430,
      vid: "jvm-thread-table",
    });
  }, []);

  let jvmTableVirtualProps = {};

  if (jvmThreadDataSource && jvmThreadDataSource.length > 5) {
    jvmTableVirtualProps = {
      scroll: { y: 430 },
      components: VList({
        height: 430,
        vid: "jvm",
      }),
    };
  }
  const uri = "monitor/system/systemInfo";

  const fetchJvmThreadDeatils = (refresh = false) => {
    if (!jvmThreadDataSource || refresh) {
      return requests.admin(dispatch).postJvmThreadDetails(
        { action: "threads" },
        uri,
        (res) => {
          dispatch(storeJvmThreadData(res.threadArray));
          setLoading(false);
          if (refresh) {
            Notify.success({
              type: "Network Call",
              message: "Refreshed JVM Thread Details Successfully",
            });
          }
        },
        (e) => {
          // Notify.error({ type: "Network Call", ...e });
        }
      );
    }
  };

  const renderSkeleton = () => <Skeleton title={true} paragraph={false} />;

  return (
    <Col xs={23} lg={expandJvmTable ? 24 : 12} className="system-details-table-container">
      <Row className="system-heading-container">
        <Col span={24} className="table-heading-container">
          <Row align="middle" justify="space-between">
            <Col span={12}>
              <Tooltip title="JVM Thread Details" placement="right">
                <span className="hi-system-table-title" data-testid="jvm-thread-details">
                  JVM Thread Details
                </span>
              </Tooltip>
            </Col>

            <Col span={2}>
              <Row gutter={[16, 16]} justify="end">
                <Col span={10}>
                  <Tooltip title="Refresh JVM Thread Details" placement="left">
                    <SyncOutlined data-testid = "jvm-thread-table-refresh"
                      onClick={() => {
                        apiRef.current = fetchJvmThreadDeatils({ refresh: true });
                        setLoading(true);
                      }}
                    />
                  </Tooltip>
                </Col>

                {expandJvmTable ? (
                  <Tooltip title="Collapse">
                    <Col span={10}>
                      <FullscreenExitOutlined
                        onClick={() => {
                          dispatch(storeSystemDetailsJvmTableExpand(false));
                        }}
                      />
                    </Col>
                  </Tooltip>
                ) : (
                  <Col span={10}>
                    <Tooltip title="Expand">
                      <FullscreenOutlined
                        onClick={() => {
                          dispatch(storeSystemDetailsJvmTableExpand(true));
                        }}
                      />
                    </Tooltip>
                  </Col>
                )}
              </Row>
            </Col>
          </Row>
        </Col>
      </Row>
      <Row>
        <Col span={24} className="system-table-data-container" data-testid="jvm-thread-table">
          {(loading) ? <><LoadingBar handleClick={() => handleAbort({ setLoading })} /><JvmSkeleton /></>
            : <Table
            data-testid = "jvm-thread-table-component"
              dataSource={
                jvmThreadDataSource === null && loading
                  ? [...Array(6).map(() => { })] //to generate dummy data so show skelton when data is null
                  : jvmThreadDataSource
              }
              columns={
                loading
                  ? JVMThreadcolumns.map((column) => {
                    return { ...column, render: renderSkeleton };
                  })
                  : JVMThreadcolumns
              }
              pagination={false}
              scroll={{ y: 430 }}
              components={vComponents}
              {...jvmTableVirtualProps}
              rowClassName={(record, index) => {
                let className = index % 2 && "table-row-color";
                return className;
              }}
            />}
        </Col>
      </Row>
    </Col>
  );
};

export default JvmThreadTable;
