import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import "./hi-fileBrowser.scss";
import { Skeleton, Drawer, Typography, Row, Col, Tooltip, Modal } from "antd";
import { GlobalFilters, FbTable } from "./components";
import { fetchFileBrowserData } from "./helperMethods";
import { fileBrowserActions } from "../../redux/actions";
import { InfoCircleOutlined } from "@ant-design/icons";
import CustomSkeletonFilebrowser from "../common/custom-icons/CustomSkeletons/filebrowser/CustomSkeletonFilebrowser";
const { Title } = Typography;

const HIFileBrowser = (props) => {
  const { getFileBrowserData, onFbClose, ...rest } = props;
  const files = useSelector((state) => {
    if (props.extensionOptions?.includes('image')) {
      return state.cannedReports.present.imagesList;
    }
    return state.fileBrowser.files
  });

  useEffect(() => {
    if (props.extensionOptions?.includes('image')) {
      // fetchImagesData(dispatch, false, cannedReport(dispatch)?.getResources);
    } else {
      getFileBrowserData();
    }
  }, []);

  return files.loading ? <CustomSkeletonFilebrowser size={props.size} /> : <FbTable {...rest} onFbClose={onFbClose} />;
};

const FileBrowserWrapper = (props) => {
  const dispatch = useDispatch();
  const showFileBrowser = useSelector((state) => state.fileBrowser.showFileBrowser);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const copyOrCutItemDetails = useSelector((state) => state.fileBrowser.copyOrCutItemDetails);
  const getFileBrowserData = (refreshObj = {}) => {
    const { refresh } = refreshObj
    if (refresh) {
      dispatch(fileBrowserActions.setCutOrCopyItemDetails(null));
    }
    fetchFileBrowserData(dispatch, refresh || false, applicationSettingsData.settings);
  }

  const toggleFB = (bool) => dispatch(fileBrowserActions.setShowFileBrowser(bool))

  useEffect(() => {
    if (!showFileBrowser) resetFields()
  }, [showFileBrowser])

  useEffect(() => {
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({
        contextItem: null,
        clickedRecord: null,
      })
    );
  }, []);

  const resetFields = () => {
    dispatch(fileBrowserActions.setFilteredFiles(null));
    dispatch(fileBrowserActions.setFilterByType(null));
    dispatch(fileBrowserActions.setGroupBy(null));
    dispatch(fileBrowserActions.setSearchResults(null));
    dispatch(fileBrowserActions.setTableColumns(null));
    dispatch(fileBrowserActions.setGlobalSearch(null));
    dispatch(fileBrowserActions.setGlobalFbVisibility(false));
  }
  const onFbClose = () => {
    if (props.onFbClose) props.onFbClose()
    toggleFB(false);
    dispatch(fileBrowserActions.setCutOrCopyItemDetails(null));
    fileBrowserActions.saveFileinFb([copyOrCutItemDetails])
    if (extensionOptions?.includes('image')) {
      getFileBrowserData({ refresh: true })
    }
  }

  const { extensionOptions, containerId, mode, isHideFilters, footerForm } = props;
  return (
    <React.Fragment>
      <Modal
        // zIndex={1001} // drawer has zindex of 1000
        className={`fileBrowser-drawer ${mode ? mode : ""}`}
        centered={true}
        visible={showFileBrowser}
        onCancel={mode === "compact" ? () => { } : onFbClose}
        destroyOnClose={true}
        footer={null}
        width={"100%"}
        getContainer={containerId ? containerId : "#root"}
      >
        <>
          <Row
            align="middle"
            justify={`${mode !== "compact" ? "space-between" : "end"}`}
          >
            {mode !== "compact" && (
              <Col>
                <Title className="filebrowser-header" level={4}>
                  File Browser
                  <Tooltip
                    placement="bottom"
                    color="#000"
                    title={
                      "Select folder/file and then right click for context menu operations"
                    }
                    overlayInnerStyle={{
                      height: "100%",
                      width: "100%",
                      fontSize: 14,
                    }}
                  >
                    <InfoCircleOutlined
                      style={{
                        fontSize: "0.8rem",
                        marginLeft: 4,
                        cursor: "pointer",
                      }}
                    />
                  </Tooltip>
                </Title>
              </Col>
            )}
            <Col flex="auto">
              <GlobalFilters
                getFileBrowserData={getFileBrowserData}
                extensionOptions={extensionOptions}
                isHideFilters={isHideFilters}
                mode={mode}
                closeFb={onFbClose}
                footerForm={footerForm}
              />
              {/* <Tooltip title="Close">
                  <CloseOutlined
                    className="filebrowser-close"
                    onClick={onFbClose}
                  />
                </Tooltip> */}
            </Col>
          </Row>
          <HIFileBrowser
            {...props}
            getFileBrowserData={getFileBrowserData}
            onFbClose={onFbClose}
          />
        </>
      </Modal>
    </React.Fragment>
  );
};

export { FileBrowserWrapper as HIFileBrowser }