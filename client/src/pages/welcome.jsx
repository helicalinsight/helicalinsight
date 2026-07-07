import { HINavbar, HIResults } from "../components";
import { Layout, Row, Col } from "antd";
import SidebarLayout from "../layouts/side-bar-layout";
import HIBodyLayout from "../layouts/hi-body-layout";
import { HIUsers } from "../components";
import { roleTypes } from "../app/constants";
import { useSelector } from "react-redux";
// import ViewMode from "./viewer";

const { Header, Content, Sider } = Layout;
const { roleUser, roleViewer, roledownload } = roleTypes;

// const ViewModeHOC = ({ viewModeInfo }) => {
//   return (
//     <Layout className="layout user-module">
//       <Header className="header">{viewModeInfo.file.title}</Header>
//       <Content>
//         <ViewMode viewModeInfo={viewModeInfo} />
//       </Content>
//     </Layout>
//   );
// };

// const BodyRenderer = ({ user }) => {
//   const viewModeInfo = useSelector((state) => state.app.viewModeInfo);
//   return (
//     <>
//       {/* <HIAdminTabs /> */}
//       {!viewModeInfo ?
//         user.roles?.some((item) =>
//           [roleAdmin, roleUser, roleViewer, roledownload].includes(item)
//         ) ? (
//           <HIUsers customRole={false} />
//         ) : (
//           <HIResults status="403" />
//         ) : <></>}
//       {viewModeInfo && <ViewModeHOC viewModeInfo={viewModeInfo} />}
//     </>
//   );
// };

const Welcome = () => {
  const { applicationSettingsData, toggleSidebar } = useSelector((state) => state.app);
  let { user } = applicationSettingsData.userData;

  return (
    <Layout>
      <Header className="hi-header hi-navbar">
        <HINavbar />
      </Header>
      <Layout>
        <Row className="hi-page-grid">
          <Col xs={24} md={{ span: toggleSidebar ? 0 : 4, order: -1 }}>
            {user.roles?.some((item) =>
              [roleUser, roleViewer, roledownload].includes(item)) ?
              <SidebarLayout /> : <Sider
                collapsed={false}
                collapsedWidth={0}
                width={"100%"}
                className={`hi-sidebar`}
              ></Sider>}
          </Col>

          <Col xs={24} md={{ span: toggleSidebar ? 24 : 20, order: 1 }}>
            <Content className="hi-body">
              <HIBodyLayout>
                {user.roles?.some((item) =>
                  [roleUser, roleViewer, roledownload].includes(item)
                ) ? (
                  <HIUsers customRole={false} />
                ) : (
                  <HIResults status="403" />
                )}
              </HIBodyLayout>
            </Content>
          </Col>
        </Row>
      </Layout>
    </Layout>
  );
};

export { Welcome };
