import { useRef, useState } from "react";
import { Checkbox, Divider, Modal, Row, Col, Tooltip } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";
import DownloadProgressModal from "../../common/download-progress-modal";

const BASE_OPTIONS = ["File and Folder (default)", "Share Details", "Datasources", "Schedules", "Recycle Bin"];
const EXPORT_EVERYTHING_OPTION = "Export everything";

const getPath = (recordData) => {
  const modifiedPath = recordData.path.split("/");
  modifiedPath.pop();
  const finalPath = modifiedPath.join("/");
  return finalPath;
};

export const getFileName = (recordData) => {
  return (recordData.name && recordData.type === 'file') ? recordData.path.split("/").pop() : "";
}

const defaultVal = ["File and Folder (default)"];
const ExportModal = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const exportModalData = useSelector((store) => store.fileBrowser.exportModalData);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const enforceDatasource = Boolean(applicationSettingsData?.enforceDatasourceExportImport);
  const isSuperAdmin = !applicationSettingsData?.userData?.user?.organization;
  const showExportEverything = Boolean(props.allRepos && isSuperAdmin);
  const plainOptions = showExportEverything
    ? [...BASE_OPTIONS, EXPORT_EVERYTHING_OPTION]
    : BASE_OPTIONS;
  const initialCheckedList = enforceDatasource ? [...defaultVal, "Datasources"] : defaultVal;
  const [checkAll, setCheckAll] = useState(false);
  const [checkedList, setCheckedList] = useState(enforceDatasource ? defaultVal.concat(["Datasources"]) : defaultVal); // File and Folder is by default enabled
  const [indeterminate, setIndeterminate] = useState(true);
  const [downloadingInfo, setDownloadingInfo] = useState(null);
  const hiAxiosRef = useRef(null);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const { recordData } = exportModalData;

  const onChange = (list) => {
    const updatedList = enforceDatasource && !list.includes("Datasources") ? [...list, "Datasources"] : list;
    setCheckedList(updatedList);
    setIndeterminate(!!updatedList.length && updatedList.length < plainOptions.length);
    setCheckAll(updatedList.length === plainOptions.length);
  };

  const onCheckAllChange = (e) => {
    const nextList = e.target.checked ? plainOptions : enforceDatasource ? ["Datasources"] : [];
    setCheckedList(nextList);
    setIndeterminate(enforceDatasource && !e.target.checked);
    setCheckAll(e.target.checked);
  };

  const handleExport = () => {
    const { recordData } = exportModalData;

    if (checkedList.length === 0) {
      Notify.error({
        type: "Frontend",
        message: "Please select File and Folder",
      });
    } else {
       const formData = {
        dir: props.allRepos ? "" : (recordData.title ? getPath(recordData) : recordData.path),
        file: props.allRepos ? "" : getFileName(recordData),
        options: {
          share: checkedList.includes("Share Details"),
          dataSource: checkedList.includes("Datasources"),
          schedules: checkedList.includes("Schedules"),
          recycleBin: checkedList.includes("Recycle Bin"),
          exportEverything: showExportEverything && checkedList.includes(EXPORT_EVERYTHING_OPTION),
        },
      };

      hiAxiosRef.current = fbRequests.postExportData(formData, setDownloadingInfo);
    }
  };

  const handleOk = () => {
    dispatch(fileBrowserActions.setExportModalData({ visible: false }));
    setCheckedList(initialCheckedList);
    setIndeterminate(true);
    handleExport();
  };

  const handleCancel = () => {
    setCheckedList(initialCheckedList);
    setIndeterminate(true);
    dispatch(fileBrowserActions.setExportModalData({ visible: false, recordData: {} }));
  };

  return (
    <>
    <Modal
      title={`Export`}
      visible={exportModalData.visible}
      onOk={handleOk}
      okText="Export"
      onCancel={handleCancel}
    >
      <Row data-testid="hi-file-browser-export" gutter={[16, 16]}>
        {recordData.path && (
          <Col span={24}>
            <span style={{ color: "#000000", opacity: "45%" }}> {recordData.path}</span>
          </Col>
        )}
        <Col span={9}>
          <Tooltip title="Selects all options">
            <Checkbox indeterminate={indeterminate} onChange={onCheckAllChange} checked={checkAll}>
              Select all
            </Checkbox>
          </Tooltip>
        </Col>
        <Col span={1}>
          <Divider type="vertical" style={{ height: "100px" }} />
        </Col>
        <Col span={14}>
          <Checkbox.Group value={checkedList} onChange={onChange}>
            <div>
              <Tooltip title="All the folders and files including nested folders">
                <Checkbox value="File and Folder (default)">File and Folder (default)</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Security information of users, roles, permissions and organizations">
                <Checkbox value="Share Details">Share Details</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Datasources related metadata">
                <Checkbox value="Datasources" disabled={enforceDatasource}>Datasources</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Schedules of the reports">
                <Checkbox value="Schedules">Schedules</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Soft-deleted resources and related Recycle Bin entries">
                <Checkbox value="Recycle Bin">Recycle Bin</Checkbox>
              </Tooltip>
            </div>
            {showExportEverything && (
              <div>
                <Tooltip title="Export all associated content irrespective of current-user permissions (super admin only)">
                  <Checkbox value={EXPORT_EVERYTHING_OPTION}>Export everything</Checkbox>
                </Tooltip>
              </div>
            )}
          </Checkbox.Group>
        </Col>
      </Row>
    </Modal>
    <DownloadProgressModal
      downloadingInfo={downloadingInfo}
      onCancel={() => {
        hiAxiosRef.current?.abort?.();
        setDownloadingInfo(null);
      }}
    />
    </>
  );
};

export default ExportModal;