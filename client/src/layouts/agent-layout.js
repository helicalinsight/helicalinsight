import React from "react";
import { Layout } from "antd";

const { Content } = Layout;

/** Same height shell Metadata uses via layouts/metadata-layout.js — Agent only. */
const AgentLayout = ({ children }) => (
  <div className="hi-agent-grid-container">
    <Layout className="hi-agent-grid-body">
      <Content className="hi-agent-grid-section">
        <div className="height100percent">{children}</div>
      </Content>
    </Layout>
  </div>
);

export default AgentLayout;
