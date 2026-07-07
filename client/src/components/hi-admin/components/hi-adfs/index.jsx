import { Col, Row } from "antd";
import React, { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { toggleMetadataContents } from "../../../../redux/actions/admin.actions";
import {
  HIMetaDataAdministration,
  HIMetaDataGeneration,
  HIMetaDataPreview,
} from "./components";
import "./index.scss";

const HIAdfs = ({ apiRef, handleAbort }) => {
  const showMetadataPages = useSelector(
    (state) => state.admin.showMetadataPages
  );
  const dispatch = useDispatch();

  useEffect(() => {
    dispatch(
      toggleMetadataContents({
        page: "metadataAdministration",
        status: true,
      })
    );
  }, []);

  return (
    <Row className="hi-adfs">
      <Col span={24}>
        {showMetadataPages.metadataAdministration ? (
          <HIMetaDataAdministration />
        ) : showMetadataPages.metadataGeneration ? (
          <HIMetaDataGeneration />
        ) : showMetadataPages.metadataPreview ? (
          <HIMetaDataPreview />
        ) : (
          <HIMetaDataAdministration />
        )}
      </Col>
    </Row>
  );
};

export { HIAdfs };
