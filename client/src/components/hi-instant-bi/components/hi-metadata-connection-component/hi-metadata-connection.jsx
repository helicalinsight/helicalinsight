import { TableOutlined } from "@ant-design/icons";
import { Button, Tooltip } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import TutorialInfo from "../../../common/hi-tutorial";
import "./hi-metadata-connection.scss";

const HIMetadataConnection = (props) => {
  const metadata = useSelector((state) => state.instantBI.metadata);
  let { title, path, location, metadataFileName } = metadata || {};
  if (location && metadataFileName && !title && !path) {
    title = metadataFileName.split(".")[0];
    path = location + "/" + metadataFileName;
  }

  return (
    <TutorialInfo elementKey="hi-instant-bi-metadata">
      <Tooltip
        placement="left"
        title={
          title && path
            ? `Connected to ${title}. Located at ${path}`
            : "Connect to Metadata"
        }
      >
        <Button
          size="large"
          data-testid="hi-instant-bi-connect-to-metadata"
          icon={
            <TableOutlined
              className={title && path ? "hi-metadata-selected" : ""}
            />
          }
          onClick={() => {
            props.openFileBrowser();
          }}
        />
      </Tooltip>
    </TutorialInfo>
  );
};

export default HIMetadataConnection;
