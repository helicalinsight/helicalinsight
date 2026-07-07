import { useState, useEffect, useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Table, Row, Col, Tooltip, Skeleton } from "antd";
import { SyncOutlined, FullscreenExitOutlined, FullscreenOutlined } from "@ant-design/icons";
import { VList } from "virtuallist-antd";
import {
  storeOsData,
  storeSystemDetailsOsTableExpand,
} from "../../../../../redux/actions/admin.actions";
import notify from "../../../../hi-notifications/notify";
import requests from "../../../../../base/requests";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import OsSkeleton from "../../../../common/custom-icons/CustomSkeletons/systemDetails/OsSkeleton"

const osDataColumns = [
  {
    title: <Tooltip title="Key">Key</Tooltip>,
    dataIndex: "osname",
    render: (cell) => (
      <Tooltip title={cell} placement="topLeft">
        {cell}
      </Tooltip>
    ),
  },
  {
    title: <Tooltip title="Value">Value</Tooltip>,
    dataIndex: "ossource",
    render: (cell) => (
      <Tooltip title={cell} placement="topLeft">
        {cell}
      </Tooltip>
    ),
  },
];

let osTableVirtualProps = {};

const OsDetailsTable = ({ apiRef, handleAbort }) => {
  let osDataSource = useSelector((store) => store.admin.osDetails);
  const expandOsTable = useSelector((store) => store.admin.osTableExpand);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [loading, setLoading] = useState(!osDataSource ? true : false);

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      fetchOsDetails();
    }
  }, []);

  const vComponents = useMemo(() => {
    return VList({
      height: 430,
      vid: "os-details-table",
    });
  }, []);

  if (osDataSource && osDataSource.length > 5) {
    osTableVirtualProps = {
      scroll: { y: 430 },
      components: VList({
        height: 430,
        vid: "osTable",
      }),
    };
  }

  const uri = "monitor/system/systemInfo";

  const fetchOsDetails = (refresh = false) => {
    if (!osDataSource || refresh) {
      return requests.admin(dispatch).postOsDetails(
        { action: "system" },
        uri,
        (res) => {
          const osDetails = res.sysInfo;
          const convertedOsDetails = [];
          Object.keys(osDetails).map((key) => {
            let os = key.replace(/\./g, " ").replace(/_/g, " "); // Replace dots and underscores with spaces
            os = os.replace(/([a-z])([A-Z])/g, "$1 $2"); // Add space before capital letters, except the first letter
            os = os.replace(/-/g, " "); // Replace hyphens with spaces
            let osname = os.replace(/(^\w|\s\w)/g, (m) => m.toUpperCase()); // to make first letter capital after space

            // Handle special cases
            if (osname === "Jdk Tls Ephemeral DHKey Size") {
              osname = "Jdk Tls Ephemeral DH Key Size";
            } else if (osname === "Jboss I18n Generate Proxies") {
              osname = "Jboss I 18 N Generate Proxies";
            }
            const ossource = osDetails[key];
            convertedOsDetails.push({ osname, ossource });
          });
          dispatch(storeOsData(convertedOsDetails));
          setLoading(false);
          if (refresh) {
            Notify.success({
              type: "Network Call",
              message: "Refreshed OS Details Successfully",
            });
          }
        },
        (e) => {
          // Notify.error({ type: "Network Call", ...e });
        }
      );
    }
  };

  const renderSkeleton = () => <>
    <Skeleton title={true} paragraph={false} /></>;
  return (
    <Col xs={23} lg={expandOsTable ? 24 : 11} className="system-details-table-container">
      <Row className="system-heading-container">
        <Col span={24} className="table-heading-container">
          <Row align="middle" justify="space-between">
            <Col span={12}>
              <Tooltip title="OS Details" placement="right">
                <span className="hi-system-table-title" data-testid="os-details">
                  OS Details
                </span>
              </Tooltip>
            </Col>

            <Col span={2}>
              <Row gutter={[16, 16]} justify="end">
                <Col span={10}>
                  <Tooltip title="Refresh OS Details" placement="left">
                    <SyncOutlined
                    data-testid = "os-details-table-refresh"
                      onClick={() => {
                        apiRef.current = fetchOsDetails({ refresh: true });
                        setLoading(true);
                      }}
                    />
                  </Tooltip>
                </Col>

                {expandOsTable ? (
                  <Tooltip title="Collapse">
                    <Col data-testid ="os-details-table-collapse" span={10}>
                      <FullscreenExitOutlined
                        onClick={() => {
                          dispatch(storeSystemDetailsOsTableExpand(false));
                        }}
                      />
                    </Col>
                  </Tooltip>
                ) : (
                  <Col data-testid ="os-details-table-expand" span={10}>
                    <Tooltip title="Expand">
                      <FullscreenOutlined
                        onClick={() => {
                          dispatch(storeSystemDetailsOsTableExpand(true));
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
        <Col span={24} className="system-table-data-container" data-testid="os-table">
          {(loading) ? <><LoadingBar handleClick={() => handleAbort({ setLoading })} /><OsSkeleton /></>
            : <Table
            data-testid = "os-details-table-component"
              dataSource={
                osDataSource === null && loading
                  ? [...Array(6).map(() => { })] //to generate dummy data so show skelton when data is null
                  : osDataSource
              }
              columns={
                loading
                  ? osDataColumns.map((column) => {
                    return {
                      ...column, render: renderSkeleton,
                      // onCell: (_, index) => ({
                      //   colSpan: index === 0 ? 2 : 1,
                      // }),
                    };
                  })
                  : osDataColumns
              }
              pagination={false}
              scroll={{ y: 430 }}
              components={vComponents}
              {...osTableVirtualProps}
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

export default OsDetailsTable;
