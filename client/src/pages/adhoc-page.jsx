import React, {useState} from "react";
import { Layout, Row, Col, Tooltip } from "antd";
import {  HINavbar } from "../components";
import SidebarLayout from "../layouts/side-bar-layout";
import { useSelector, useDispatch } from "react-redux";
import { appActions } from "../redux/actions";
// import SaveItems from "../components/hi-fileBrowser/SaveItems";
// import { capitalize } from "lodash";
import HIBodyLayout from "../layouts/hi-body-layout";
import ViewMode from "./viewer";
import {
  FullscreenOutlined,
} from "@ant-design/icons";
const { showNavbar, toggleSidebar } = appActions;
const { Header, Content } = Layout;

const ViewModeHOC = ({ viewModeInfo }) => {
  const dispatch = useDispatch();
  return (
    <Layout className="layout user-module">
      <Header className="header">
        {viewModeInfo.file.title}
        <Tooltip title="Expand">
          <FullscreenOutlined
            style={{marginLeft: 'auto'}}
            onClick={() => {
              dispatch(toggleSidebar());
              dispatch(showNavbar());
            }}
          />
        </Tooltip>
      </Header>
      <Content>
        <ViewMode viewModeInfo={viewModeInfo} />
      </Content>
    </Layout>
  );
};

const AdhocPage = () => {
  // const dispatch = useDispatch();
  // const [path,setPath] = useState(null)
  // const [,setDir] = useState(null)
  const { showNavbar: showNav } = useSelector(
		(state) => state.app
	  );
  const viewModeInfo = useSelector((state) => state.app.viewModeInfo);
  // const saveCallback = (dir, fileName, saveToredux) => {
  //   setDir({dir, fileName})
  //   //saveToredux(file)
  // }
  // const onDbC = rec => console.log(rec, 'dbc')

  return (
    <Layout>
      {!showNav && <HINavbar />}
      <Layout>
        <Row className="hi-page-grid">
          <Col xs={24} md={4}>
            <SidebarLayout />
          </Col>
          <Col xs={24} md={!showNav ? 20 : 24}>
            <Content className="hi-body">
              <HIBodyLayout>
                {viewModeInfo && <ViewModeHOC viewModeInfo={viewModeInfo} />}
              </HIBodyLayout>
            </Content>
          </Col>
        </Row>
      </Layout>
      {/* <Layout>
        <Button onClick={() => dispatch(showNavbar())}>Hide</Button>
        <SidebarLayout />
        <Content className="hi-body">
          ADHOC
          <Space direction="vertical">
            <div>
              {path && <p>Path: {path}</p>}
              <Button
                type="primary"
                onClick={() =>
                  dispatch(fileBrowserActions.setShowFileBrowser(true))
                }
              >
                Open FB
              </Button>
            </div>
            {dir && (
              <Space direction="vertical">
                <div>Directory - {dir.dir.path}</div>
                <div>FileName - {dir.fileName}</div>
              </Space>
            )}
          </Space>
          <HIFileBrowser
            onDoubleClick={onDbC}
            // footerForm={{
            //   type: "Save",
            //   form: (
            //     <SaveItems
            //       formHeading="Report file name"
            //       onFormSumbit={saveCallback}
            //       saveButtonText="Save"
            //       cancelButtonText="Cancel"
            //       inputValue=""
            //     />
            //   ),
            // }}
            contextMenuOptions={{
              append: true,
              options: [
                {
                  merge: true,
                  id: 'edt',
                  name: "Edit",
                  types: ["file"],
                  extensions: ["metadata"],
                  disabled: false,
                  callback: (rec) => {
                    console.log(rec, 'edt')
                  },
                },
                {
                  name: "Random",
                  types: ["file"],
                  icon: "",
                  extensions: ["report"],
                  callback: (rec) => {
                    console.log(rec, "opt2");
                  },
                },
                {
                  name: "open",
                  merge:true,
                  id:"opn",
                  types: ["file"],
                  extensions: ["report"],
                  hide:true
                },
              ],
            }}
            extensionOptions={["metadata"]}
          />
        </Content>
      </Layout>  */}
    </Layout>
  );
};

export { AdhocPage };
