import { Row, Col } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { storeReportCeEditorData } from "../../../redux/actions/reprotce.actions.js";
import Editor from "./Editor";
import classNames from "classnames";

const DashboardEditor = (props) => {
  const dispatch = useDispatch();
  const { html, css, setHtml, setCss } = props;
  const activeEditorData = useSelector(
    (store) => store.reportCe.activeEditorData
  );

  return (
    <Row>
      <Col
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditorData.dashboardId === "html",
        })}
        span={12}
        onClick={() =>
          dispatch(
            storeReportCeEditorData({ id: "html", type: "dashboardLayout" })
          )
        }
      >
        <span>HTML</span>
      </Col>
      <Col
        className={classNames({
          "edit-language-title": true,
          "active-tab": activeEditorData.dashboardId === "css",
        })}
        span={12}
        onClick={() =>
          dispatch(
            storeReportCeEditorData({ id: "css", type: "dashboardLayout" })
          )
        }
      >
        CSS
      </Col>
      <Col span={24}>
        {activeEditorData.dashboardId === "html" && (
          <Editor
            language="xml"
            displayName="HTML"
            value={html}
            setEditorState={setHtml}
          />
        )}
        {activeEditorData.dashboardId === "css" && (
          <Editor
            language="css"
            displayName="CSS"
            value={css}
            setEditorState={setCss}
          />
        )}
      </Col>
    </Row>
  );
};

export default DashboardEditor;
