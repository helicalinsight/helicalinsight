import {
  DownOutlined,
  BankOutlined,
  BellOutlined,
  LogoutOutlined,
  QuestionCircleOutlined,
  BulbOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, List, Tooltip, Badge, Typography } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { toggleNotifications } from "../../../redux/actions/useractions.actions";
import { HINotifications } from "../../hi-notifications";
import { leftMenuDrop } from "../helperMethods";
import { logoutHandler, executeTutorial } from "./userAction.helperMethods";
import { appActions } from "../../../redux/actions/app.actions";
import TutorialInfo from "../../common/hi-tutorial";

const { Text } = Typography;
const { toggleMainNavbar } = appActions;

const UserActions = ({ width, screenSizeBreakPoint }) => {
  const {
    applicationSettingsData,
    actualApplicationSettings,
    isSessionOver,
    activeRoute,
    isTutorialOn
  } = useSelector((state) => state.app);
  let { user } = applicationSettingsData.userData;
  const notificationData = useSelector(
    (state) => state.userActions.notificationData
  );
  const dispatch = useDispatch();
  let normalListDropdown = [
    {
      name: `${user.organization || "Not Set"}`,
      icon: <BankOutlined className="action-icon" />,
    },
    {
      name: `Logout ${user.actualUserName !== user.name ? `(${user.name})` : ""
        } `,
      icon: <LogoutOutlined className="action-icon" />,
      callBack: () => {
        logoutHandler({
          user,
          dispatch,
          isSessionOver,
          activeRoute,
        });
      },
    },
  ];
  let normalList = [
    {
      name: "Notifications",
      icon: (
        <Badge color="#1890ff" size="small" count={notificationData.length}>
          <BellOutlined className="action-icon" />
        </Badge>
      ),
      callBack: () => {
        dispatch(toggleNotifications());
      },
      tutorialElementKey: "hi-notifications",
    },
    {
      name:
        user.actualUserName !== user.name
          ? `${user.actualUserName} as ${user.name}`
          : user.name,
      icon: (
        <Button className="username-icon" type="primary" shape="circle">
          {user.name?.split("").slice(0, 2).join("").toUpperCase()}
        </Button>
      ),
      text: true,
      dropdown: normalListDropdown,
    },
  ];
  const variedList = [
    {
      name: "Tutorial",
      icon: <BulbOutlined className="action-icon" />,
      callBack: () => {
        // dispatch(updateTutorialOn(true));
        executeTutorial({ activeRoute, dispatch, toggleMainNavbar, user });
      },
      tutorialElementKey: "hi-tutorial-info",
    },
    {
      name: "Help",
      icon: <QuestionCircleOutlined className="action-icon" />,
      tutorialElementKey: "hi-help",
    },
  ];

  if (width > screenSizeBreakPoint) {
    normalList = [...variedList, ...normalList];
  } else {
    normalListDropdown = [...variedList, ...normalListDropdown];
  }

  normalListDropdown.unshift({ name: "@ " + user.email });

  return (
    <List
      className="user-actions"
      dataSource={normalList}
      renderItem={(ele) => (
        <List.Item className="user-action">

          <div
          // onClick={ele.callBack}
          >
            {ele.text ? (
              ele.dropdown ? (
                <Tooltip
                  // className={isTutorialOn ? 'hide-tooltip' : 'useraction-tooltip'}
                  placement={ele.text && ele.dropdown && "left"}
                  title={ele.name}
                >
                  <Dropdown
                    placement="bottomRight"
                    arrow="true"
                    overlay={leftMenuDrop(normalListDropdown)}
                  >
                    <span onClick={ele.callBack} className="action-icons">
                      <Text ellipsis style={{ width: "97px" }}>
                        {ele.icon} {ele.name}
                        <DownOutlined style={{ fontSize: "10px" }} />
                      </Text>
                    </span>
                  </Dropdown>
                </Tooltip>
              ) : (
                <Tooltip
                  // className={isTutorialOn ? 'hide-tooltip' : 'useraction-tooltip'}
                  placement={ele.text && ele.dropdown && "left"}
                  title={ele.name}
                >
                  <span onClick={ele.callBack} className="action-icon">
                    <Text style={{ display: "flex", alignItems: "center" }}>
                      {ele.icon}
                      &nbsp;{ele.name}
                    </Text>
                  </span>
                </Tooltip>
              )
            ) : ele.name === "Help" ? (

              <a
                href="https://www.helicalinsight.com/learn/"
                target="_blank"
                rel="noreferrer"
              >
                <TutorialInfo elementKey="hi-help" className="pad-top-10">
                  <Tooltip
                    // className={isTutorialOn ? 'hide-tooltip' : 'useraction-tooltip'}
                    placement={ele.text && ele.dropdown && "left"}
                    title={ele.name}
                  >
                    <span onClick={ele.callBack}>
                      {ele.icon}
                    </span>
                  </Tooltip>
                </TutorialInfo>
              </a>
            ) : (
              // <span >
              <TutorialInfo elementKey={ele.tutorialElementKey} className="pad-top-10">
                <Tooltip
                  // className={isTutorialOn ? 'hide-tooltip' : 'useraction-tooltip'}
                  placement={ele.text && ele.dropdown && "left"}
                  title={ele.name}
                >
                  <span onClick={ele.callBack}>
                    {ele.icon}
                  </span>
                </Tooltip>
              </TutorialInfo>
              // </span>
            )}
          </div>
        </List.Item>
      )}
    >
      <HINotifications />
    </List>
  );
};

export default UserActions;
