import { useState } from "react";
import { Drawer, Card, Row, Col, Typography, Tooltip, Button } from "antd";
import {
  CheckOutlined,
  CloseOutlined,
  InfoOutlined,
  ExclamationOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import {
  toggleNotifications,
  deleteNotificationItem,
  resetNotificationData,
  updateNotificationItem,
} from "../../redux/actions/useractions.actions";
import "./index.scss";
import HINoticationItem from "./notification-item";
import { List, AutoSizer } from "react-virtualized";

const successIcon = <CheckOutlined className="hi-success-icon" />;
const errorIcon = <CloseOutlined className="hi-error-icon" />;
const infoIcon = <InfoOutlined className="hi-info-icon" />;
const warnIcon = <ExclamationOutlined className="hi-warn-icon" />;
const { Text, Title } = Typography;

const HINotifications = () => {
  const visible = useSelector(
    (state) => state.userActions.notificationDrawerStatus
  );
  // const [loading, setLoading] = useState(false);
  const data = useSelector((state) => state.userActions.notificationData);
  let dataWithoutDuplicates = data.filter(
    (thing, index, self) =>
      index === self.findIndex((t) => t.message === thing.message)
  );
  const dataWithNumberOfOccurances = [];
  dataWithoutDuplicates.forEach((item) => {
    const count = data.reduce(function (n, val) {
      return n + (val.message === item.message);
    }, 0);
    dataWithNumberOfOccurances.push({
      ...item,
      type: item.type ? item.type : "Frontend",
      number: count,
    });
  });

  // const [data, setData] = useState(notificationData);
  const [ellipsis] = useState(true);
  const dispatch = useDispatch();
  function rowRenderer({
    key, // Unique key within array of rows
    index, // Index of row within collection
  }) {
    return (
      <div className="hi-notification-card">
        <HINoticationItem />
        <Card
          className={
            dataWithNumberOfOccurances[index]?.status === "success"
              ? "green-border-left"
              : dataWithNumberOfOccurances[index]?.status === "error"
              ? "red-border-left"
              : dataWithNumberOfOccurances[index]?.status === "info"
              ? "blue-border-left"
              : dataWithNumberOfOccurances[index]?.status === "warn"
              ? "yellow-border-left"
              : null
          }
          hoverable
          key={key}
        >
          <Row align="middle">
            <Col offset={1} span={3}>
              {dataWithNumberOfOccurances[index]?.status === "success"
                ? successIcon
                : dataWithNumberOfOccurances[index]?.status === "error"
                ? errorIcon
                : dataWithNumberOfOccurances[index]?.status === "info"
                ? infoIcon
                : dataWithNumberOfOccurances[index]?.status === "warn"
                ? warnIcon
                : null}
            </Col>
            <Col span={20}>
              <Row>
                <Col span={24}>
                  <Row>
                    <Col span={20}>
                      <Text
                        type="primary"
                        className="hi-tag hi-info-background"
                      >
                        {dataWithNumberOfOccurances[index]?.type.toUpperCase()}
                      </Text>
                    </Col>
                    <Col className="hi-on-hover-icon" span={2}>
                      <Text
                        copyable={{
                          text: dataWithNumberOfOccurances[index].message,
                        }}
                      />
                    </Col>
                    <Col className="hi-on-hover-icon" span={2}>
                      <CloseOutlined
                        onClick={() => {
                          dispatch(
                            deleteNotificationItem(
                              dataWithNumberOfOccurances[index].id
                            )
                          );
                        }}
                      />
                    </Col>
                  </Row>
                </Col>
                {/* <Col span={8}>
                  <Text
                    type="primary"
                    className={
                      dataWithNumberOfOccurances[index]?.status === "success"
                        ? "hi-tag hi-success-background"
                        : dataWithNumberOfOccurances[index]?.status === "error"
                        ? "hi-tag hi-error-background"
                        : dataWithNumberOfOccurances[index]?.status === "info"
                        ? "hi-tag hi-info-background"
                        : "hi-tag hi-warn-background"
                    }
                  >
                    {dataWithNumberOfOccurances[index]?.status.toUpperCase()}
                  </Text>
                </Col> */}
                {dataWithNumberOfOccurances[index]?.number > 1 && (
                  <Col span={2}>
                    <span className="hi-notify-number">
                      {dataWithNumberOfOccurances[index]?.number}
                    </span>
                  </Col>
                )}
                <Col
                  onClick={() => {
                    dispatch(
                      updateNotificationItem({
                        notificationItemId:
                          dataWithNumberOfOccurances[index]?.id,
                        status: true,
                      })
                    );
                  }}
                  span={22}
                >
                  <Tooltip placement="top" title="Click to show more">
                    <Text
                      // type={item.status === "success" ? "success" : "danger"}
                      className="hi-notify-text"
                      style={ellipsis ? { width: 400 } : undefined}
                      ellipsis={ellipsis}
                    >
                      {dataWithNumberOfOccurances[index]?.message}
                    </Text>
                  </Tooltip>
                </Col>
              </Row>
            </Col>
          </Row>
        </Card>
      </div>
    );
  }

  return (
    <Drawer
      onClose={() => {
        dispatch(toggleNotifications());
      }}
      title={<span className="hi-drawer-title">Notifications</span>}
      visible={visible}
      className="hi-notifications"
      extra={
        <Tooltip placement="left" title="Clear All">
          <Button
            disabled={dataWithNumberOfOccurances.length > 0 ? false : true}
            style={{ backgroundColor: "white", borderColor: "white" }}
          >
            <DeleteOutlined
              className="hi-icon"
              onClick={() => {
                dispatch(resetNotificationData());
              }}
            />
          </Button>
        </Tooltip>
      }
    >
      {dataWithNumberOfOccurances.length ? (
        <AutoSizer>
          {({ height, width }) => (
            <List
              width={width}
              height={height}
              rowCount={dataWithNumberOfOccurances.length}
              rowHeight={65}
              rowRenderer={rowRenderer}
            />
          )}
        </AutoSizer>
      ) : (
        <Title
          data-testid="hi-notifications-title"
          className="hi-title"
          level={5}
        >
          No Notifications
        </Title>
      )}
    </Drawer>
  );
};

export { HINotifications };
