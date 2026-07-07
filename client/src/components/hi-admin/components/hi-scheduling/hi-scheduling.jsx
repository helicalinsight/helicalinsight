import { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { VList } from "virtuallist-antd";
import { Row, Col, Button, Table, Popconfirm, Tooltip, Skeleton, Card, Popover } from "antd";
import requests from "../../../../base/requests";
import { updateScheduledList } from "../../../../redux/actions/admin.actions";
import notify from "../../../hi-notifications/notify";
import moment from "moment";
import "./hi-scheduling.scss";

import {
  InfoCircleOutlined,
  PauseOutlined,
  DeleteOutlined,
  CaretRightOutlined,
  ForwardOutlined,
  DownOutlined,
  ReloadOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import MoreSchedulingInfo from "./MoreSchedulingInfo";
import LoadingBar from "../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../common/components/Hi-Popconfirm";
import ScheduleSkeleton from "../../../common/custom-icons/CustomSkeletons/schedule/ScheduleSkeleton"

let tableVirtualProps = {};

const content = (
  <p className="scheduling-alert">
    Deleting a scheduled job is temporary.The job may be rescheduled on application
    restart/reload/redeploy etc, if EndDate is greater than current date. To permanently delete the job, please use the "Delete" button next to "Schedule Expired".
  </p>
);

const icons = {
  pause: <PauseOutlined />,
  resume: <ForwardOutlined />,
  start: <CaretRightOutlined />,
  shutdown: <DownOutlined />,
  refresh: <SyncOutlined />,
};

const typeFilters = [
  {
    text: "Helical Report",
    value: "hr",
  },
  {
    text: "Dashboard",
    value: "efwdd",
  },
  {
    text: "Other",
    value: "hwf",
  },
  {
    text: "Canned Report",
    value: "hcr",
  },
  {
    text: "Report",
    value: "efw",
  },
];

const scheduleTypes = {
  hr: "Helical Report",
  efwdd: "Dashboard",
  efw: "Report",
  hwf: "Other",
  hcr: "Canned Report",
};

const dateFormat = "DD/MM/YYYY hh:mm A";

const HIScheduling = ({ apiRef, handleAbort }) => {
  const schedulingData = useSelector((store) => store.admin.schedulingList);
  const [visible, setVisible] = useState(false);
  const [moreInfo, setMoreInfo] = useState([]);
  const [loading, setLoading] = useState(schedulingData === null ? true : false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [clickedRowId, setclickedRowId] = useState("");
  const [schedulingList, setSchedulingList] = useState(schedulingData);

  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const uri = "monitor/scheduling/schedule";

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      fetchSchedulingDetails();
    }
  }, []);

  useEffect(() => {
    if (schedulingData && schedulingData.length > 0) setSchedulingList(schedulingData);
  }, [schedulingData]);

  if (schedulingList !== null) {
    if (schedulingList.length >= 8) {
      tableVirtualProps = {
        scroll: { y: 415 },
        components: VList({
          height: 425,
          vid: "scheduling",
        }),
      };
    }
  } else {
    tableVirtualProps = {};
  }

  const onCloseDrawer = () => {
    setVisible(false);
  };

  const fetchSchedulingDetails = (refresh = false) => {
    if (schedulingList === null || refresh) {
      dispatch(updateScheduledList(null));
      setLoading(true);
      apiRef.current = requests.admin(dispatch).postSchedulingData(
        { action: "list" },
        uri,
        (res) => {
          dispatch(updateScheduledList(res.scheduledList));
          setLoading(false);
        },
        (e) => {
          setLoading(false);
        }
      );
    }
  };

  const getData = (record, type) => {
    const data = schedulingList.map((eachData) => {
      if (!type && eachData.jobId === record.jobId) {
        return {
          ...eachData,
          triggerState: eachData.triggerState === "PAUSED" ? "NORMAL" : "PAUSED",
        };
      }
      if (type) {
        return {
          ...eachData,
          triggerState: type,
        };
      }
      return eachData;
    });
    setSchedulingList(data);
  };

  const schedulingActions = (formData) => {
    requests.admin(dispatch).postSchedulingData(
      formData,
      uri,
      (res) => {
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onPauseAll = () => {
    requests.admin(dispatch).postSchedulingData(
      { action: "pauseAll" },
      uri,
      (res) => {
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
        getData("", "NORMAL");
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onResumeAll = () => {
    requests.admin(dispatch).postSchedulingData(
      { action: "resumeAll" },
      uri,
      (res) => {
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
        getData("", "NORMAL");
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onClickMoreInfo = (record) => {
    let convertedMoreDetails = [];
    let schedulingValue;
    setVisible(true);

    Object.keys(record).map((key) => {
      const schedulingKey =
        key.charAt(0).toUpperCase() +
        key
          .slice(1)
          .replace(/([A-Z])/g, " $1")
          .trim();
      if (schedulingKey === "Next Fire Time" || schedulingKey === "Start Date") {
        schedulingValue = moment(new Date(record[key])).format("ddd MMM Do YYYY, hh:mm:ss a");
      } else if (schedulingKey === "Last Executed On") {
        if (record[key] === "") {
          schedulingValue = moment(new Date(), dateFormat);
        }
      } else if (schedulingKey === "Report Parameters") {
        schedulingValue = JSON.stringify(record[key]);
      } else {
        schedulingValue = record[key].toString();
      }

      convertedMoreDetails = [...convertedMoreDetails, { schedulingKey, schedulingValue }];

      setMoreInfo(convertedMoreDetails);
    });
  };

  const onDeleteSingleTask = (record) => {
    setclickedRowId(record.jobId);
    setDeleteLoading(true);

    requests.admin(dispatch).postSchedulingData(
      { action: "delete", jobId: record.jobId },
      uri,
      (res) => {
        setDeleteLoading(false);
        fetchSchedulingDetails({ refresh: true });
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onExecuteSingleTask = (record) => {
    setclickedRowId(record.jobId);
    requests.admin(dispatch).postSchedulingData(
      { action: "execute", jobId: record.jobId },
      uri,
      (res) => {
        setDeleteLoading(false);
        fetchSchedulingDetails({ refresh: true });
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onPauseSingleTask = (record) => {
    requests.admin(dispatch).postSchedulingData(
      { action: "pause", jobId: record.jobId },
      uri,
      (res) => {
        setDeleteLoading(false);
        getData(record);
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onResumeSingleTask = (record) => {
    requests.admin(dispatch).postSchedulingData(
      { action: "resume", jobId: record.jobId },
      uri,
      (res) => {
        setDeleteLoading(false);
        getData(record);
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const renderActions = (record) => {
    if (!record.inMemoryStatus) {
      return (
        <Popconfirm
          title={
            <PopconfirmBody
              intent="delete"
              description={"Are you sure you want to delete this Schedule job Permanently?"}
            />
          }
          placement="left"
          onConfirm={() => onDeleteSingleTask(record)}
        >
          <Tooltip title="Delete Permanently" placement="right">
            <Button
              type="text"
              icon={<DeleteOutlined />}
              loading={clickedRowId === record.jobId && deleteLoading}
            />
          </Tooltip>
        </Popconfirm>
      );
    }
    return schedulingList.length >= 1 ? (
      <div style={{ display: "flex", flexWrap: "wrap" }}>
        {record.triggerState === "PAUSED" ? (
          <Tooltip title="Resume this job" placement="left">
            <Button
              type="text"
              icon={<CaretRightOutlined />}
              onClick={() => {
                setclickedRowId(record.jobId);
                onResumeSingleTask(record);
              }}
            />
          </Tooltip>
        ) : (
          <Tooltip title="Pause this job" placement="left">
            <Button
              type="text"
              icon={<PauseOutlined />}
              onClick={() => {
                setclickedRowId(record.jobId);
                onPauseSingleTask(record);
              }}
              className="scheduling-more-info"
            />
          </Tooltip>
        )}

        <Tooltip title="Execute this job">
          <Button
            type="text"
            icon={<ReloadOutlined />}
            onClick={() => onExecuteSingleTask(record)}
          />
        </Tooltip>

        <Popconfirm
          title={<PopconfirmBody width="300px" intent="delete" description={"Deleting a scheduled job is temporary"} />}
          placement="left"
          onConfirm={() => onDeleteSingleTask(record)}
        >
          <Tooltip title="Delete this job" placement="right">
            <Button
              type="text"
              icon={<DeleteOutlined />}
              loading={clickedRowId === record.jobId && deleteLoading}
            />
          </Tooltip>
        </Popconfirm>
      </div>
    ) : null;
  };

  const schedulingDataColumns = [
    {
      title: "Sl No",
      dataIndex: "slno",
      width: 70,
      className: "table-ellipsis",
      sorter: (a, z) => a.slno - z.slno,
    },
    {
      title: "Type",
      dataIndex: "type",
      className: "table-ellipsis",
      width: 150,
      render: (_, record) => {
        return record.type ? <span>{scheduleTypes[record.type]}</span> : "";
      },
      filters: typeFilters,
      onFilter: (value, record) => record.type.indexOf(value) === 0,
    },
    {
      title: "Job Id",
      dataIndex: "jobId",
      className: "table-ellipsis",
      width: 100,
      sorter: (a, z) => a.jobId - z.jobId,
    },
    {
      title: "Actions",
      className: "table-ellipsis",
      render: (_, record) => {
        if (record.inMemoryStatus === true) {
          return renderActions(record);
        }
        return (
          <>
            <span className="expired"> Schedule Expired </span>
            {renderActions(record)}
          </>
        );
      },
    },
    {
      title: "Days of Week",
      dataIndex: "daysofWeek",
      className: "table-ellipsis",
      render: (text, record) => {
        const is_days_present = record.daysofWeek.length > 0;
        return (
          <Tooltip
            title={is_days_present ? record.daysofWeek : record.frequency}
            placement="topLeft"
          >
            {is_days_present ? (
              record.daysofWeek.map((eachDay) => <span> {eachDay} </span>)
            ) : (
              <span>{record.frequency}</span>
            )}
          </Tooltip>
        );
      },
    },

    {
      title: "Email Recipients",
      dataIndex: "emailRecipients",
      className: "table-ellipsis scheduling-email-recipients",
      render: (text) => (
        <Tooltip placement="topLeft" title={text?.join("") || ""}>
          <span>{text?.join("") || null}</span>
        </Tooltip>
      ),
    },
    {
      title: "Schedule Details",
      className: "table-ellipsis",
      render: (_, record) => (
        <Tooltip data-testid="hi-scheduling-more-info" title="More Info">
          <Button type="link" onClick={() => onClickMoreInfo(record)}>
            More Info
          </Button>
        </Tooltip>
      ),
    },
  ];

  const ActionsCard = () => {
    return (
      <Card hoverable className="actions-card">
        <Row justify="center" align="middle">
          <Col span={5}>
            <Button
              data-testid="pause-all"
              type="text"
              icon={icons.pause}
              onClick={() => onPauseAll()}
            >
              Pause All
            </Button>
          </Col>
          <Col span={5}>
            <Button
              data-testid="resume-all"
              type="text"
              icon={icons.resume}
              onClick={() => onResumeAll()}
            >
              Resume All
            </Button>
          </Col>
          <Col span={4}>
            <Button
              data-testid="start"
              type="text"
              icon={icons.start}
              onClick={() => schedulingActions({ action: "start" })}
            >
              Start
            </Button>
          </Col>
          <Col span={5}>
            <Button
              type="text"
              data-testid="shutdown"
              icon={icons.shutdown}
              onClick={() => schedulingActions({ action: "shutdown" })}
            >
              Shutdown
            </Button>
          </Col>
          <Col span={4}>
            <Tooltip title="Click on refresh button to see the updated data">
              <Button
                data-testid="refresh"
                type="text"
                icon={icons.refresh}
                onClick={() => fetchSchedulingDetails({ refresh: true })}
              >
                Refresh
              </Button>
            </Tooltip>
          </Col>
        </Row>
      </Card>
    );
  };

  const renderSkeleton = () => <Skeleton title={true} paragraph={false} />;

  return (
    <>
      <Row className="hi-admin-scheduling-container">
        <Col span={24} className="scheduling-header-container">
          <Row className="scheduling-header-data-container">
            <Col span={4}>
              <Row align="middle">
                <Col className="scheduling-title">
                  <span data-testid="hi-scheduling-text">Schedule</span>
                </Col>
                <Col>
                  <Popover
                    content={content}
                    placement="right"
                    trigger="hover"
                    overlayStyle={{
                      width: "70vw",
                    }}
                  >
                    <span>
                      <InfoCircleOutlined className="scheduling-alert-icon" />
                    </span>
                  </Popover>
                </Col>
              </Row>
            </Col>
            <Col lg={14} sm={20}>
              <ActionsCard />
            </Col>
          </Row>
        </Col>
        <Col span={24} className="scheduling-table-container">
          <Card bordered={false} hoverable>
            {(loading && !["test"].includes(process.env.NODE_ENV)) ? <><LoadingBar handleClick={handleAbort} />
              <ScheduleSkeleton /></> :
              <Table
                data-testid="scheduling-table"
                dataSource={schedulingList}
                size="small"
                columns={
                  loading
                    ? schedulingDataColumns.map((column) => {
                      return { ...column, render: renderSkeleton };
                    })
                    : schedulingDataColumns
                }
                pagination={false}
                rowKey="id"
                bordered
                {...tableVirtualProps}
                rowClassName={(record, index) => {
                  let className = index % 2 && "table-row-color";
                  return className;
                }}
              />}
          </Card>
        </Col>
      </Row>
      <MoreSchedulingInfo visible={visible} moreInfo={moreInfo} onCloseDrawer={onCloseDrawer} />
    </>
  );
};

export { HIScheduling };
