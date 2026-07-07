import React from "react";

import { BulbOutlined, DownOutlined } from "@ant-design/icons";
import { Collapse, List, Typography } from "antd";
import "./index.scss";
const { Panel } = Collapse;

const gettingStartedData = [
  {
    name: "Helical Insight",
    url: "https://www.helicalinsight.com/getting-started-hi/",
  },
  {
    name: "User & Role Managment",
    url: "https://www.helicalinsight.com/user-role-management/",
  },
  {
    name: "Adhoc Designer",
    url: "https://www.helicalinsight.com/getting-start-with-adhoc-designer/",
  },
  {
    name: "Dashboard Designer",
    url: "https://www.helicalinsight.com/dashboard-designer-overview/",
  },
  {
    name: "User Repository",
    url: "https://www.helicalinsight.com/repository-ui-introduction/",
  },
];
const connectingToData = [
  {
    name: "Create,Edit and Share Data Source",
    url: "https://www.helicalinsight.com/adhoc-datasource/",
  },
];
const metaData = [
  {
    name: "Create & Edit Meta Data",
    url: "https://helicalinsight.com/create-and-edit-metadata/",
  },
  { name: "Views", url: "https://www.helicalinsight.com/usage-of-views/" },
  {
    name: "Data Security",
    url: "https://www.helicalinsight.com/data-security/",
  },
];
const creatingReports = [
  {
    name: "Report",
    url: "https://www.helicalinsight.com/adhoc-interface-overview/",
  },
  {
    name: "Create Reports",
    url: "https://www.helicalinsight.com/adhoc-reports/",
  },
  {
    name: "Applying Filters",
    url: "https://www.helicalinsight.com/filters-usage/",
  },
  {
    name: "Custom Column",
    url: "https://www.helicalinsight.com/custom-column/",
  },
  {
    name: "Exporting Reports",
    url: "https://www.helicalinsight.com/exporting-report/",
  },
];

const HIAdminSidebarContent = () => {
  const handleDashboardClick = () => {
    window.open("https://www.helicalinsight.com/dashboard-designer-overview/", "_blank");
  };

  const handleLearnMoreClick = () => {
    window.open("https://www.helicalinsight.com/learn/", "_blank");
  };

  return (
    <div className="hi-sidebar-content">
      <Collapse
        expandIcon={({ isActive }) => (
          <DownOutlined rotate={isActive ? 180 : 0} />
        )}
        expandIconPosition="right"
        className="hi-custom-collapse"       
        ghost
        defaultActiveKey={['1']}
      >
        <Panel data-testid = "hi-admin-sidebar" className="hi-custom-panel" header="Discover" key="1">
          <Collapse
            className="hi-custom-collapse"
            expandIcon={() => <BulbOutlined />}            
            ghost
          >
            <p className="hi-tiny-text">Training</p>
            <Panel className="hi-medium-text" header="Getting Started" key="1">
              <List
                size="small"
                className="hi-custom-list"
                dataSource={gettingStartedData}
                renderItem={(item) => (
                  <List.Item>
                    <a href={item.url} target="_blank" rel="noreferrer">
                      <Typography.Text>&bull; {item.name}</Typography.Text>
                    </a>
                  </List.Item>
                )}
              />
            </Panel>
            <Panel
              className="hi-medium-text"
              header="Connecting to Data"
              key="2"
            >
              <List
                className="hi-custom-list"
                dataSource={connectingToData}
                renderItem={(item) => (
                  <List.Item>
                    <a href={item.url} target="_blank" rel="noreferrer">
                      <Typography.Text>&bull; {item.name}</Typography.Text>
                    </a>
                  </List.Item>
                )}
              />
            </Panel>
            <Panel
              className="hi-medium-text"
              header="Creating Metadata"
              key="3"
            >
              <List
                className="hi-custom-list"
                dataSource={metaData}
                renderItem={(item) => (
                  <List.Item>
                    <a href={item.url} target="_blank" rel="noreferrer">
                      <Typography.Text>&bull; {item.name}</Typography.Text>
                    </a>
                  </List.Item>
                )}
              />
            </Panel>
            <Panel className="hi-medium-text" header="Creating Reports" key="4">
              <List
                className="hi-custom-list"
                dataSource={creatingReports}
                renderItem={(item) => (
                  <a href={item.url} target="_blank" rel="noreferrer">
                    <List.Item>
                      <Typography.Text>&bull; {item.name}</Typography.Text>
                    </List.Item>
                  </a>
                )}
              />
            </Panel>
            <div style ={{marginTop:"4px" ,display:"flex",flexDirection:"column",gap:"8px"}}>
            <div className="hi-medium-text hi-custom-list">
              <a href={"https://www.helicalinsight.com/dashboard-designer-overview/"} target="_blank" rel="noreferrer">
                <BulbOutlined style={{ color: "#222425", fontSize: "12px" ,marginLeft:"5px", marginRight: "12px"}} />
                <Typography.Text>Creating Dashboard</Typography.Text>
              </a>
            </div>
            <div className="hi-medium-text hi-custom-list">
              <a href={"https://www.helicalinsight.com/learn/"} target="_blank" rel="noreferrer">
                <BulbOutlined style={{ color: "#222425", fontSize: "12px",marginLeft:"5px", marginRight: "13px"}} />
                <Typography.Text>Learn More</Typography.Text>
              </a>
            </div>
            </div>
          </Collapse>
        </Panel>
        {/* <Panel className="hi-custom-panel" header="Statistics" key="2" />
        <Panel className="hi-custom-panel" header="What's New" key="3" /> */}
      </Collapse>
    </div>
  );
};

export { HIAdminSidebarContent };
