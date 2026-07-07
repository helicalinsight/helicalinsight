import React from "react";
import "../app/app.scss";
import { HINavbar } from "../components";
import HIAdminTabs from "../components/hi-admin/hi-adminTabs/hi-adminTabs";
import HILayout from "../layouts/hi-layout";

const AdminPage = () => {
  return (
    <HILayout
      header={<HINavbar />}
      content={<HIAdminTabs />}
      defaultSidebar={true}
    />
  );
};

export { AdminPage };
