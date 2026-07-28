import { InfoCircleOutlined, CloseOutlined } from "@ant-design/icons";
import Editor from "@monaco-editor/react";
import { Button, Carousel, Row, Space, Tooltip, Typography } from "antd";
import { monacoReactCodeEditorOptions } from "../../hi-reports/utils/constants";
import "../../hi-reports/hi-viz-area/hi-viz-area.scss";

const { Text } = Typography;

/** Instant BI VF editor UI (HReport file untouched). */
export const IbVfEditorTitle = ({ slide = 1, onToggleInfo }) => (
  <Space className="custom-chart-title">
    <Text>{slide === 1 ? "VF Editor" : "VF Editor Info"}</Text>
    <Tooltip
      title={
        slide === 1 ? "Click to see the info" : "Click to go back to editor"
      }
    >
      <span
        className="custom-chart-info-icon"
        data-testid="ib-vf-editor-info-icon"
        onClick={onToggleInfo}
      >
        {slide === 1 ? <InfoCircleOutlined /> : <CloseOutlined />}
      </span>
    </Tooltip>
  </Space>
);

export const IbVfEditorFooter = ({ onApply }) => (
  <Row justify="end" data-testid="ib-vf-editor-footer">
    <Button
      onClick={onApply}
      type="primary"
      data-testid="ib-vf-editor-apply-button"
    >
      Apply
    </Button>
  </Row>
);

export const IbVfEditorBody = ({
  code = "",
  onChange,
  slide = 1,
  carouselRef,
  editorHeight = "70vh",
}) => (
  <Carousel ref={carouselRef} dots={false}>
    <div
      className="hi-custom-chart-editor-container"
      data-testid="ib-vf-editor-container"
    >
      <Editor
        value={code}
        onChange={onChange}
        options={monacoReactCodeEditorOptions}
        height={editorHeight}
        defaultLanguage="javascript"
      />
      {!code?.length && (
        <div className="hi-custom-chart-placeholder-container">
          <span className="placeholder-text">Please write your script here</span>
        </div>
      )}
    </div>
    {slide === 2 && (
      <div className="ib-vf-editor-info">
        <h2>VF (Visualization Framework) Info</h2>
        <p>
          Edit the VF template to customize this chart. Click Apply to update
          the preview.
        </p>
      </div>
    )}
  </Carousel>
);
