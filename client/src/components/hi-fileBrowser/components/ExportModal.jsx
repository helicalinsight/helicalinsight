import { useState } from "react";
import { Checkbox, Divider, Modal, Row, Col, Tooltip, message } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";

const plainOptions = ["File and Folder (default)", "Share Details", "Datasources", "Schedules"];

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
  const [checkAll, setCheckAll] = useState(false);
  const [checkedList, setCheckedList] = useState(defaultVal); // File and Folder is by default enabled
  const [indeterminate, setIndeterminate] = useState(true);
  const [percent, setPercent] = useState(0);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const { recordData } = exportModalData;

  const onChange = (list) => {
    setCheckedList(list);
    setIndeterminate(!!list.length && list.length < plainOptions.length);
    setCheckAll(list.length === plainOptions.length);
  };

  const onCheckAllChange = (e) => {
    setCheckedList(e.target.checked ? plainOptions : []);
    setIndeterminate(false);
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
        },
      };

      fbRequests.postExportData(
        formData,
        (progressEvent) => {
          downLoadProgress(progressEvent);
        },
        (res) => {
          handleDownLoad(res, recordData);
        }
      );
    }
  };

  const downLoadProgress = (progressEvent) => {
    const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
    setPercent(percentCompleted);
  };

  const handleDownLoad = (res, record) => {
    let fileInfo = res.headers["content-disposition"] || "";
    let fileInfoName = fileInfo.split("=") || [];
    let fileName = fileInfoName[1] || "";
    fileName = fileName.substr(1, fileName.length - 2) || "Untitled1";
    try {
      let encoding = new TextDecoder("utf-8");
      let arr = new Uint8Array(res.data);
      let resString = encoding.decode(arr);
      let data = JSON.parse(resString);
      Notify.error({ type: "Backend", message: data.response.message });
    } catch (e) {
      if (e.name === "SyntaxError") {
        message.loading({ content: ` ${fileName} is downloading`, key: "loading" });

        const reader = new FileReader();
        reader.onload = function () {
          const a = document.createElement("a");
          a.href = reader.result;
          a.setAttribute("download", fileName);
          a.style.display = "none";
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(reader.result);
          a.remove();
        };
        reader.readAsDataURL(new File([res.data], { type: ".zip" }));

        // const blob = new Blob([res.data], { type: ".zip" });
        // const href = URL.createObjectURL(blob);
        // const a = Object.assign(document.createElement("a"), {
        //   href,
        //   style: "display:none",
        //   download: `${fileName}`,
        // });

        // document.body.appendChild(a);
        // a.click();
        // URL.revokeObjectURL(href);
        // a.remove();
        Notify.success({
          message: `${fileName} downloaded successfully`,
          // key: "hagsa",
          // duration: 3,
          type: "Network Call"
        });
        Notify.success({
          message: `Exported successfully`,
          type: "Network Call"
        });
      }
    }
  };

  const handleOk = () => {
    dispatch(fileBrowserActions.setExportModalData({ visible: false }));
    setCheckedList(defaultVal);
    setIndeterminate(true);
    handleExport();
  };

  const handleCancel = () => {
    setCheckedList(defaultVal);
    setIndeterminate(true);
    dispatch(fileBrowserActions.setExportModalData({ visible: false, recordData: {} }));
  };

  return (
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
                <Checkbox value="Datasources">Datasources</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Schedules of the reports">
                <Checkbox value="Schedules">Schedules</Checkbox>
              </Tooltip>
            </div>
          </Checkbox.Group>
        </Col>
      </Row>
    </Modal>
  );
};

export default ExportModal;
