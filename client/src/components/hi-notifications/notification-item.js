import { Drawer, Col, Row, Typography } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { updateNotificationItem } from "../../redux/actions/useractions.actions";
import {
  CheckOutlined,
  CloseOutlined,
  InfoOutlined,
  ExclamationOutlined,
} from "@ant-design/icons";
import "./index.scss";

const successIcon = <CheckOutlined className="hi-success-icon" />;
const errorIcon = <CloseOutlined className="hi-error-icon" />;
const infoIcon = <InfoOutlined className="hi-info-icon" />;
const warnIcon = <ExclamationOutlined className="hi-warn-icon" />;

const { Text } = Typography;

const HINotificationItem = () => {
  const visible = useSelector(
    (state) => state.userActions.notificationItemDrawer
  );
  const dispatch = useDispatch();
  const id = useSelector((state) => state.userActions.notificationItemId);
  const data = useSelector((state) => state.userActions.notificationData);
  const item = data.find((item) => item.id === id);
  return (
    <Drawer
      title={<span className="hi-drawer-title">Notification Details</span>}
      onClose={() => {
        dispatch(
          updateNotificationItem({
            notificationItemId: null,
            status: false,
          })
        );
      }}
      className="hi-notifications"
      visible={visible}
    >
      <Row
        onClick={() => {
          dispatch(
            updateNotificationItem({
              notificationItemId: item.id,
              status: true,
            })
          );
        }}
        className="hi-notification-item"
        justify="center"
        align="middle"
      >
        <Col span={3}>
          {item?.status === "success"
            ? successIcon
            : item?.status === "error"
            ? errorIcon
            : item?.status === "info"
            ? infoIcon
            : item?.status === "warn"
            ? warnIcon
            : null}
        </Col>
        <Col span={16}>
          <Row>
            {/* <Col span={8}>
              <Text
                type="primary"
                className={
                  item?.status === "success"
                    ? "hi-tag hi-success-background"
                    : item?.status === "error"
                    ? "hi-tag hi-error-background"
                    : item?.status === "info"
                    ? "hi-tag hi-info-background"
                    : "hi-tag hi-warn-background"
                }
              >
                {item?.status.toUpperCase()}
              </Text>
            </Col> */}
            <Col span={16}>
              <Text type="primary" className="hi-tag hi-info-background">
                {item?.type ? item.type.toUpperCase() : "Frontend"}
              </Text>
            </Col>
            <Col span={24}>
              <Text
                // type={item.status === "success" ? "success" : "danger"}
                className="hi-notify-text"
              >
                {item?.message}
              </Text>
            </Col>
          </Row>
        </Col>
        <Col span={2}>
          <Text copyable={{ text: item?.message }} />
        </Col>
      </Row>
    </Drawer>
  );
};
export default HINotificationItem;
