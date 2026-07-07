import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";
import { useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Row, Col, Card, Progress, Tooltip } from "antd";
import {
  ArrowDownOutlined,
  FileSearchOutlined,
  UploadOutlined,
} from "@ant-design/icons";

let driverLink;

const UploadFile = (props) => {
  const { getData, onCloseDrawer } = props;
  const dispatch = useDispatch();
  const [progress, setProgress] = useState(0);
  const [isUploading, setIsUploading] = useState(false);
  const hiddenFileInput = useRef(null);
  const Notify = notify(dispatch);
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );

  const onDetectDriver = () => {
    const uri = "core/dataSource/loadDriver";
    const formData = {
      action: "load",
      driverName: clickedActiveDatabaseData.name,
    };

    requests.datasource(dispatch).postDataSourceData(
      formData,
      uri,
      (res) => {
        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };
  const handleClick = () => {
    hiddenFileInput.current.click();
  };

  const handleChange = (event) => {
    const fileUploaded = event.target.files[0];
    const formData = new FormData();

    formData.append("destination", "");
    formData.append("type", "datasource");
    formData.append("file", fileUploaded);

    // axios.post("http://localhost:3000/importFile.html", formData, {
    //   onUploadProgress: (progressEvent) => {
    //     const percentCompleted = Math.round(
    //       (progressEvent.loaded * 100) / progressEvent.total
    //     );
    //     setProgress(percentCompleted);
    //   },
    // });

    requests.datasource(dispatch).postUploadFile(
      formData,
      {
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          setIsUploading(true);
          setProgress(percentCompleted);
        },
      },
      (res) => {
        onCloseDrawer();
        setIsUploading(false);
        Notify.success({
          type: "Network Call",
          message: `The file ${fileUploaded.name} has been imported successfully`,
        });
        getData();
      },
      (e) => {
        setIsUploading(false);
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  if (clickedActiveDatabaseData.name === "Ξ Add Driver Ξ") {
    driverLink = "https://www.helicalinsight.com/adding-new-db-driver/";
  } else {
    driverLink = `https://www.helicalinsight.com/datasource/?q=#${clickedActiveDatabaseData.name}`;
  }

  return (
    <Row data-testid = "hi-datasource-upload" gutter={[16, 16]}>
      <Col span={24}>
        <p className="upload-heading">
          {clickedActiveDatabaseData.name === "Ξ Add Driver Ξ" ? (
            <p>You may upload other jdbc drivers</p>
          ) : (
            <p>Datasource Support Available</p>
          )}
        </p>
      </Col>
      <Col xs={24} md={8}>
        <a href={driverLink} target="_blank">
          <Card hoverable className="upload-card">
            <ArrowDownOutlined />
            <p>Download relevant database driver</p>
          </Card>
        </a>
      </Col>

      {clickedActiveDatabaseData.name !== "Ξ Add Driver Ξ" && (
        <Col xs={24} md={8} onClick={onDetectDriver}>
          <Card hoverable className="upload-card">
            <FileSearchOutlined />
            <p>Detect the driver in lib/classpath</p>
          </Card>
        </Col>
      )}

      <Col xs={24} md={8} onClick={handleClick}>
        <Card hoverable className="upload-card">
          <input
            ref={hiddenFileInput}
            type="file"
            accept=" .jar, .zip, .rar, .license"
            onChange={handleChange}
            style={{ display: "none" }}
          />
          <Tooltip
            title="Note that if the driver jar is a single file then upload the jar file. If there are multiple dependency then put all the files into a folder. Zip the folder to file and upload"
            placement="topLeft"
          >
            <UploadOutlined />
            <p>Upload driver(.jar/.zip)</p>
          </Tooltip>
          {isUploading && <Progress percent={progress} />}
        </Card>
      </Col>
    </Row>
  );
};

export default UploadFile;
