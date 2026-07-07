import { useState, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Controlled as ControlledEditor } from "react-codemirror2";
import { Row, Col, Button } from "antd";
import {
  storeReportCeDatasource,
  storeReportCeParameters,
  storeReportCeDashboardData,
  storeReportCeReport,
  storeReportEdit,
} from "../../../redux/actions/reprotce.actions.js";
import notify from "../../hi-notifications/notify";

import "codemirror/lib/codemirror.css";
import "codemirror/theme/dracula.css";
import "codemirror/theme/material.css";
import "../../../../node_modules/codemirror/mode/xml/xml.js";
import "../../../../node_modules/codemirror/mode/css/css.js";
import "codemirror/addon/edit/closebrackets";
import "codemirror/addon/edit/closetag";
import "../index.scss";

const Editor = ({ language, value, setEditorState }) => {
  const dispatch = useDispatch();
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const reportData = useSelector((store) => store.reportCe.reportData);
  const activeEditorData = useSelector(
    (store) => store.reportCe.activeEditorData
  );
  const dashboardLayoutData = useSelector(
    (store) => store.reportCe.dashboardLayoutData
  );

  // const [editing, setEdting] = useState(false);
  const [editorData, setEditorData] = useState("");
  // const Notify = notify(dispatch);

  useEffect(() => {
    setEditorData(value);
  }, [value]);

  const handleChange = (editor, data, value) => {
    dispatch(storeReportEdit(true));
    setEditorData(value);
  };

  const handleSaveEditorData = () => {
    dispatch(storeReportEdit(false));
    if (activeEditorData.type === "datasource") {
      const updatedData = dataSourceData.map((eachData) => {
        if (eachData.id === activeEditorData.datasourceId) {
          if (language === "xml") {
            return { ...eachData, configure: editorData };
          }
        }
        return eachData;
      });
      dispatch(storeReportCeDatasource(updatedData));
    } else if (activeEditorData.type === "dashboardLayout") {
      if (language === "xml") {
        dispatch(
          storeReportCeDashboardData({
            ...dashboardLayoutData,
            html: editorData,
          })
        );
      }
      if (language === "css") {
        console.log(editorData);
        dispatch(
          storeReportCeDashboardData({
            ...dashboardLayoutData,
            css: editorData,
          })
        );
      }
    } else if (activeEditorData.type === "parameter") {
      const updatedData = parametersData.map((eachParam) => {
        if (eachParam.id === activeEditorData.paramterId) {
          if (language === "javascript") {
            return { ...eachParam, configure: editorData };
          } else if (language === "sql") {
            return { ...eachParam, sql: { ...eachParam, text: editorData } };
          }
        }
        return eachParam;
      });
      dispatch(storeReportCeParameters(updatedData));
    } else if (activeEditorData.type === "report") {
      const updatedData = reportData.map((eachReport) => {
        if (eachReport.id === activeEditorData.reportId) {
          if (language === "javascript") {
            return { ...eachReport, configure: editorData };
          } else if (language === "sql") {
            return {
              ...eachReport,
              sql: { ...eachReport, text: editorData },
            };
          } else if (language === "xml") {
            return { ...eachReport, visualisation: editorData };
          }
        }
        return eachReport;
      });
      dispatch(storeReportCeReport(updatedData));
    }
  };

  return (
    <Row>
      <Col span={24} className="editor-container">
        <ControlledEditor
          onBeforeChange={handleChange}
          value={editorData}
          className="hi-code-editor"
          options={{
            lineWrapping: true,
            lint: true,
            mode: language,
            lineNumbers: true,
            theme: "dracula",
            autoCloseTags: true,
            autoCloseBrackets: true,
            autofocus: true,
          }}
        />
      </Col>
      <Col span={24}>
        <Button
          type="primary"
          style={{
            bottom: 50,
            left: "90%",
            position: "absolute",
            zIndex: 100,
          }}
          onClick={handleSaveEditorData}
        >
          Save
        </Button>
      </Col>
    </Row>
  );
};

export default Editor;
