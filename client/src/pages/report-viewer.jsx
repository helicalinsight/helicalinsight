import {
  FullscreenOutlined,
  FullscreenExitOutlined,
  SelectOutlined,
  MailOutlined,
  SaveOutlined,
  FolderOutlined,
  SyncOutlined,
  MailFilled,
  ClockCircleOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  MenuOutlined,
  BellOutlined,
} from "@ant-design/icons";
import {
  Layout,
  Tooltip,
  Row,
  Col,
  Button,
  Dropdown,
  Menu,
  Skeleton,
  Empty,
  Typography,
  Badge,
} from "antd";
import React, { useState, useEffect, useLayoutEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { HINavbar } from "../components";
import { ViewerDrawerEmail, ViewerDrawerSchedule } from "../components";
import { modeTypes } from "../components/hi-fileBrowser/constants";
import HILayout from "../layouts/hi-layout";
import { appActions, fileBrowserActions } from "../redux/actions";
import { getFullCacheTime } from "../utils/utilities";
import { HINotifications } from "../components/hi-notifications";
import { toggleNotifications } from "../redux/actions/useractions.actions";
import ViewMode from "./viewer";
import TutorialInfo from "../components/common/hi-tutorial";
import { removeAllReports } from "../redux/actions/hreport.actions";
const { Text, Paragraph } = Typography;

const { showNavbar, toggleSidebar } = appActions;
const { Header, Content } = Layout;
const { NEW_WINDOW_MODE, MAIN_WINDOW_MODE } = modeTypes;
var qs = require("qs");

const ViewModeHOC = ({
  viewModeInfo,
  expanded,
  setExpanded,
  params,
  reportViewerMode,
  jwtView,
  setParametersReview,
}) => {
  const dispatch = useDispatch();
  const items = [
    {
      key: "1",
      label: "Email",
      icon: <MailFilled />,
      onClick: () => dispatch(appActions.setViewerEmailModalVisibility(true)),
    },
    {
      key: "2",
      label: "Schedule",
      icon: <ClockCircleOutlined />,
      onClick: () => dispatch(appActions.setViewerScheduleVisibility(true)),
    },
  ];
  // const emailMenu = (
  //   <Menu>
  //     {items.map((e) => (
  //       <Menu.Item icon={e.icon} onClick={e.onClick} disabled={e.disabled}>
  //         {e.label}
  //       </Menu.Item>
  //     ))}
  //   </Menu>
  // );

  const newPath = viewModeInfo.file.path.split("/");
  // document.title = `${capitalize(viewModeInfo.file.title.split(".")[0])} | HI:Report-Viewer`; // Changing document title based on report
  const [taskbarExtra, setTaskbarExtra] = useState([]);
  const [docTitle, setDocTitle] = useState("");
  const { lastModified } = useSelector((state) => state.app);
  newPath.splice(-1);

  useEffect(() => {
    if (docTitle.length > 0) {
      if (!jwtView) {
        document.title = `${docTitle} | HI:Report-Viewer`; // Changing document title based on report
      }
    }
  }, [docTitle]);

  const ExpandIcon = (props) => {
    return expanded ? <FullscreenExitOutlined {...props} /> : <FullscreenOutlined {...props} />;
  };

  const onOpenClick = (mode) => {
    window.open(
      window.baseURL +
      `#/report-viewer?dir=${newPath.join("/")}&file=${viewModeInfo.file.name}&mode=${mode}`
    );
  };

  const renderTaskbar = (items) => {
    setTaskbarExtra(items);
  };

  const setFileInfo = ({ fileTitle }) => {
    setDocTitle(fileTitle);
  };

  const parameters =
    Object.keys(params).length > 0
      ? Object.entries(params).reduce((accumulator, entry) => {
        let values = /\[|{/.test(entry[1]) ? Function(`return ${entry[1]}`)() : entry[1];
        if (accumulator[entry[0]]) {
          let prevValues = Array.isArray(values) ? values : [values];
          let nextValues = Array.isArray(accumulator[entry[0]])
            ? accumulator[entry[0]]
            : [accumulator[entry[0]]];
          accumulator[entry[0]] = [...nextValues, ...prevValues];
        } else {
          accumulator[entry[0]] = values;
        }
        return accumulator;
      }, {})
      : {};

  const notificationData = useSelector((state) => state.userActions.notificationData);

  let taskEles = [];

  if (reportViewerMode !== MAIN_WINDOW_MODE) {
    taskEles.push({
      key: 'Notifications',
      label: 'Notifications',
      className: 'report-viewer-menu-item',
      icon: <Tooltip title={"Notifications"} overlayStyle={{ padding: "0px" }}>  <Badge offset={[0, 14]} color="#1890ff" size="small" count={notificationData.length}>
        <BellOutlined style={{ fontSize: 18 }} onClick={() => dispatch(toggleNotifications())} />
      </Badge></Tooltip>,
    })
  }

  taskbarExtra.forEach((item, i) => {
    if (item.dropdown) {
      taskEles.push(
        {
          key: item.title,
          label: item.title,
          className: 'report-viewer-menu-item',
          icon: <Tooltip title={item.title}>{item.icon}</Tooltip>,
          children: item.menu.map((menuItem, index) => {
            return reportViewerMode === NEW_WINDOW_MODE &&
              menuItem.key === "currentReport" ? null : {
              className: 'report-viewer-sub-menu-item' + " " + (menuItem.className || ''),
              label: <Tooltip title={menuItem.tooltip || null}>{menuItem.title}</Tooltip>,
              key: menuItem.title,
              onClick: menuItem.callback,
              icon: menuItem.icon
            }
          })
        })
    } else {
      if (item.key === "cacheTime") {
        item.title = getFullCacheTime(lastModified);
        taskEles.push(
          {
            key: item.title,
            label: item.title,
            className: 'report-viewer-menu-item',
            icon: <Tooltip overlayInnerStyle={{ width: "230px" }} title={item.title}>{item.icon}</Tooltip>,
            onClick: item.callback
          }
        )
      } else {
        taskEles.push(
          {
            key: item.title,
            label: item.title,
            className: 'report-viewer-menu-item',
            icon: <Tooltip title={item.title}>{item.icon}</Tooltip>,
            onClick: item.callback
          }
        )
      }
    }
  })

  if (reportViewerMode === MAIN_WINDOW_MODE) {
    taskEles = [...taskEles,
    {
      className: 'report-viewer-menu-item',
      label: "Open in new window",
      key: "Open in new window",
      icon: <Tooltip title={"Open in new window"}>
        <SelectOutlined rotate={90} />
      </Tooltip>,
      onClick: () => {
        onOpenClick("open");
      }
    }, {
      key: "Preview report",
      label: "Preview report",
      className: 'report-viewer-menu-item',
      icon: <Tooltip title={"Preview report"}>
        <ExpandIcon />
      </Tooltip>,
      onClick: () => setExpanded((prev) => !prev)
    }];

    if (viewModeInfo.extension !== "instant") {
      taskEles.push({
        key: "Email/Schedule",
        label: "Email/Schedule",
        icon: <Tooltip title={"Email/Schedule"}>
          <MailOutlined />
        </Tooltip>,
        className: 'report-viewer-menu-item',
        children: items.map((e) => {
          return {
            className: 'report-viewer-sub-menu-item',
            label: e.label,
            key: e.label,
            onClick: e.onClick,
            icon: e.icon,
            disabled: e.disabled
          }
        })
      });
    }

    taskEles.push({
      key: "Save Report",
      label: "Save Report",
      className: 'report-viewer-menu-item pad-0-0-0-12',
      disabled: true,
      icon:
        <Tooltip title={"Save Report"}>
          <SaveOutlined />
        </Tooltip>
    });
  }

  return (
    <Layout className={`layout user-module ${expanded ? "fullScreen" : ""}`}>
      {params.mode !== "dashboard" && (
        <TutorialInfo elementKey="hr-report-viewer">
          <Header className="header">
            <Row align="middle" justify={"space-between"} style={{ width: "100%" }}>
              <Col span={12}>
                <Row align="middle" style={{ lineHeight: 'normal' }}>
                  <FolderOutlined style={{ fontSize: 20 }} />
                  {docTitle.length > 0 ? (
                    <Tooltip title={docTitle}>
                      <Paragraph className="report-title" style={{ maxWidth: '60%', marginBottom: 0 }} ellipsis={true}>
                        {docTitle}
                      </Paragraph>
                    </Tooltip>

                    // <div className="report-title">{"nne mddmdwkkjdkkwq"}</div>
                  ) : (
                    <Skeleton.Button
                      style={{
                        display: "flex",
                        alignItems: "center",
                        marginLeft: 5,
                      }}
                      active={"true"}
                      size={"small"}
                    />
                  )}
                </Row>
              </Col>
              {/* <Col span={8}></Col> */}
              {
                <Col span={12}>
                  <Menu
                    // overflowedIndicator={<SaveOutlined />}
                    // openKeys={['Export']}
                    // defaultOpenKeys={['Export']}
                    defaultSelectedKeys={[]}
                    selectedKeys={[]}
                    selectable={false}
                    className="hi-report-viewer-menu"
                    mode={"horizontal"}
                    items={taskEles} />
                </Col>
              }
            </Row>
          </Header>
        </TutorialInfo>
      )}
      <Content className="report-viwer-content">
        <ViewMode
          viewModeInfo={viewModeInfo}
          parameters={parameters}
          renderTaskbar={renderTaskbar}
          setFileInfo={setFileInfo}
          setParametersReview={setParametersReview}
        />
      </Content>
      {reportViewerMode !== MAIN_WINDOW_MODE && <HINotifications />}
    </Layout>
  );
};

const ReportViewer = ({ report, urlObj }) => {
  const { viewModeInfo } = useSelector((state) => state.app);
  const dispatch = useDispatch();
  urlObj = urlObj || {};
  let parameters = Object.assign(urlObj, {});
  let reportViewerMode = MAIN_WINDOW_MODE;
  let { dir, file, ...params } = report || parameters;
  const [expanded, setExpanded] = useState(null);
  const [parametersReview, setParameters] = useState({});
  // dir = dir ? dir + `/${file}` : null;

  (report || (dir && file)) && (reportViewerMode = NEW_WINDOW_MODE);

  useEffect(() => {
    if (expanded !== null) {
      dispatch(toggleSidebar());
      dispatch(showNavbar());
    }
  }, [expanded]);

  useEffect(() => {
    return () => {
      dispatch(toggleSidebar(false));
      dispatch(showNavbar(true));
    };
  }, []);

  useEffect(() => {
    if (dir && file) {
      setExpanded(true);
      dispatch(
        appActions.setViewModeInfo({
          file: {
            path: dir,
            name: file,
            title: file.split(".")[0].replaceAll("_", " "),
          },
          mode: "open",
          filters: [],
        })
      );
      // dispatch(fileBrowserActions.setWindowMode(modeTypes.NEW_WINDOW_MODE));
    }
  }, [dir, file]);

  const setParametersReview = (data) => {
    setParameters(data);
  };

  const content = viewModeInfo ? (
    <ViewModeHOC
      viewModeInfo={viewModeInfo}
      expanded={expanded}
      setExpanded={setExpanded}
      dir={dir}
      file={file}
      params={params}
      jwtView={report}
      reportViewerMode={reportViewerMode}
      setParametersReview={setParametersReview}
    />
  ) : (
    <TutorialInfo elementKey="hr-report-viewer">
      <Empty
        className="user-module-empty"
        description={<Text type="secondary">Please select a report</Text>}
      />
    </TutorialInfo>
  );
  return (
    <>
      <HILayout header={<HINavbar urlObj={urlObj} />} content={content} defaultSidebar={true} urlObj={urlObj} />
      {reportViewerMode === MAIN_WINDOW_MODE && viewModeInfo && (
        <>
          <ViewerDrawerEmail parametersReview={parametersReview} />
          <ViewerDrawerSchedule parametersReview={parametersReview} />
        </>
      )}
    </>
  );
};

export { ReportViewer };
