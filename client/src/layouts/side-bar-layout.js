import { Col, Layout, Row } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { HIAdminSidebarContent, SidebarFooter } from "../components";
import { fileBrowserActions } from "../redux/actions";
import TutorialInfo from "../components/common/hi-tutorial";
import { roleTypes } from "../app/constants";
// import { Collapse, List, Typography } from "antd";
// import { useRouteMatch, useLocation } from "react-router-dom";
const { Sider } = Layout;
const { roleUser, roledownload, roleAdmin } = roleTypes;

const SidebarLayout = (props) => {
  const { applicationSettingsData, toggleSidebar } = useSelector(
    (state) => state.app
  );
  const { roles } = applicationSettingsData.userData.user;
  const dispatch = useDispatch();
  const isFooterVisible = [roleAdmin, roleUser, roledownload].some((item) =>
    roles?.includes(item)
  );
  let componentToBeMounted = props.children ? (
    props.children
  ) : (
    <TutorialInfo elementKey="hi-admin-sidebar">
      <HIAdminSidebarContent />
    </TutorialInfo>
  );
  return (
    <Sider
      collapsed={toggleSidebar}
      collapsedWidth={0}
      width={"100%"}
      // width={props.customWidth || "15%"}
      className={`hi-sidebar ${props.customClass || ""}`}
    >
      <Row className="hi-sidebar-grid">
        <Col className="flex-grow-1" span={props.children ? 24 : 0} md={24}>
          {componentToBeMounted}
        </Col>
        <Col span={props.children ? 24 : 0} md={24}>
          {props.children && props.children[1] ? props.children[1] : <></>}
        </Col>
        {isFooterVisible && (
          <Col className="height75percent" span={24}>
            <SidebarFooter
              urlObj={props.urlObj}
              fileBrowserOnClick={() => {
                dispatch(fileBrowserActions.setGlobalFbVisibility(true));
                dispatch(fileBrowserActions.setShowFileBrowser(true));
              }}
            />
          </Col>
        )}
      </Row>
    </Sider>
  );
};

export default SidebarLayout;
