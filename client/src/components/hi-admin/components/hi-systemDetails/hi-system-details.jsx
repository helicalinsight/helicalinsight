import { useSelector } from "react-redux";
import { Row } from "antd";

import JvmThreadTable from "./components/jvm-thread-table";
import OsDetailsTable from "./components/os-details-table";

import "./hi-system-details.scss";
import { useRef } from "react";

const HISystemDetails = ({ apiRef, handleAbort }) => {
  const expandJvmTable = useSelector((store) => store.admin.jvmTableExpand);
  const expandOsTable = useSelector((store) => store.admin.osTableExpand);
  return (
    <Row className="hi-system-details">
      {expandJvmTable === false && <OsDetailsTable apiRef={apiRef} handleAbort={handleAbort} />}
      {expandOsTable === false && <JvmThreadTable apiRef={apiRef} handleAbort={handleAbort} />}
    </Row>
  );
};

export { HISystemDetails };
