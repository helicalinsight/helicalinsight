import React from "react";
import HIDataSource from "../components/hi-datasources/hi-dataSource";
import { HINavbar } from "../components";
import HILayout from "../layouts/hi-layout";

const DatasourcePage = () => {
  return <HILayout header={<HINavbar />} content={<HIDataSource />} defaultSidebar={true} />;
};

export { DatasourcePage };
