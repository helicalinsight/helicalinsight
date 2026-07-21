import { Layout } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { AgentMetadataShelf } from "./agent-metadata-shelf";

const { Sider } = Layout;

export function AgentSidebar(props) {
  const { toggleSidebar } = useSelector((state) => state.app);

  return (
    <Sider
      collapsed={toggleSidebar}
      collapsedWidth={0}
      width={"100%"}
      className={`hi-cube-sider ${props.customClass || ""}`}
    >
      <AgentMetadataShelf {...props} />
    </Sider>
  );
}
