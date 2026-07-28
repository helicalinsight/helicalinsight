import { useEffect, useMemo, useState } from "react";
import { useDispatch } from "react-redux";
import {
  Row,
  Col,
  List,
  Input,
  Empty,
  Spin,
  Typography,
  Tag,
  Button,
  Collapse,
  Tooltip,
  Space,
} from "antd";
import {
  SyncOutlined,
  FileOutlined,
  InfoCircleOutlined,
  FormOutlined,
  CodeOutlined,
} from "@ant-design/icons";
import requests from "../../../../base/requests";
import { uriConfig } from "../../../../base/requests/admin.request";
import { fetchUiLayout, hasLayoutSections } from "../../../common/ui-generator";
import notify from "../../../hi-notifications/notify";
import PropertiesEditor from "./components/PropertiesEditor";
import CodeEditor from "./components/CodeEditor";
import LayoutFormEditor from "./components/LayoutFormEditor";
import { CONFIG_TYPES, getFileTypeLabel } from "./utils/config-tree-utils";
import {
  CONFIGURATION_LAYOUT_CONTENT_ID,
  buildCategorizedFiles,
  canToggleRawEditor,
  contentToEditorText,
  editorTextToSaveContent,
  filterCategorizedFiles,
  resolveEditorLanguage,
  resolveRawEditorLanguage,
  toEditorUiContentId,
  toFileLayoutContentId,
} from "./utils/configuration-layout";
import "./hi-configurations.scss";

const { Search } = Input;
const { Title, Text } = Typography;
const { Panel } = Collapse;

const typeColor = {
  [CONFIG_TYPES.PROPERTIES]: "blue",
  [CONFIG_TYPES.XML]: "green",
  [CONFIG_TYPES.JSON]: "orange",
  [CONFIG_TYPES.OTHER]: "default",
};

const HIConfigurations = ({ apiRef }) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [layout, setLayout] = useState(null);
  const [files, setFiles] = useState([]);
  const [fileFilter, setFileFilter] = useState("");
  const [selectedFile, setSelectedFile] = useState(null);
  const [editorUi, setEditorUi] = useState(null);
  const [fileLayout, setFileLayout] = useState(null);
  const [filePayload, setFilePayload] = useState(null);
  const [listLoading, setListLoading] = useState(false);
  const [readLoading, setReadLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [rawContent, setRawContent] = useState("");
  const [forceRawEditor, setForceRawEditor] = useState(false);

  const uri = uriConfig.monitorSystemReadWrite;

  const fetchLayout = () =>
    fetchUiLayout({
      dispatch,
      contentId: CONFIGURATION_LAYOUT_CONTENT_ID,
      onSuccess: (res) => setLayout(res || null),
      onError: () => setLayout(null),
    });

  const fetchFiles = () => {
    setListLoading(true);
    const request = requests.admin(dispatch).postAdminRequest(
      { action: "list" },
      uri,
      (res) => {
        setListLoading(false);
        setFiles(res?.files || []);
      },
      (error) => {
        setListLoading(false);
        Notify.error({
          type: "Configurations",
          message: error?.message || "Failed to load configuration files",
        });
      }
    );
    if (apiRef) {
      apiRef.current = request;
    }
  };

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return undefined;
    }
    fetchLayout();
    fetchFiles();
    return undefined;
  }, []);

  const categorizedFiles = useMemo(
    () => buildCategorizedFiles(layout, files),
    [layout, files]
  );

  const filteredCategories = useMemo(
    () => filterCategorizedFiles(categorizedFiles, fileFilter),
    [categorizedFiles, fileFilter]
  );

  const activeCategoryKeys = useMemo(
    () => filteredCategories.map((category) => category.key),
    [filteredCategories]
  );

  const useFormLayout = hasLayoutSections(fileLayout) && !forceRawEditor;

  const showEditorToggle = canToggleRawEditor({
    fileType: selectedFile?.type || filePayload?.type,
    editorUi,
    fileLayout,
    fileName: selectedFile?.name,
  });

  const rawEditorLanguage = resolveRawEditorLanguage(
    filePayload?.type || selectedFile?.type,
    selectedFile?.name
  );

  const loadStaticJson = (contentId, onDone) => {
    if (!contentId) {
      onDone?.(null);
      return;
    }
    fetchUiLayout({
      dispatch,
      contentId,
      onSuccess: (res) => onDone?.(res || null),
      onError: () => onDone?.(null),
    });
  };

  const loadFile = (fileMeta) => {
    setSelectedFile(fileMeta);
    setFilePayload(null);
    setEditorUi(null);
    setFileLayout(null);
    setRawContent("");
    setForceRawEditor(false);
    setReadLoading(true);

    // Prefer filename.ui.layout; empty / missing sections fall back to extension editor.
    loadStaticJson(toFileLayoutContentId(fileMeta.name), (formLayout) => {
      // Keep metadata even when sections are empty; form rendering still gated by hasLayoutSections.
      setFileLayout(formLayout && typeof formLayout === "object" ? formLayout : null);
      loadStaticJson(toEditorUiContentId(fileMeta.name), (ui) => {
        setEditorUi(ui);
        const request = requests.admin(dispatch).postAdminRequest(
          {
            action: "read",
            file: fileMeta.name,
            path: fileMeta.path || "Admin",
          },
          uri,
          (res) => {
            setReadLoading(false);
            setFilePayload(res);
            setRawContent(contentToEditorText(res?.type, res?.content));
          },
          (error) => {
            setReadLoading(false);
            Notify.error({
              type: "Configurations",
              message: error?.message || `Failed to read ${fileMeta.name}`,
            });
          }
        );
        if (apiRef) {
          apiRef.current = request;
        }
      });
    });
  };

  const saveFile = (content) => {
    if (!selectedFile?.name) return;
    const type = filePayload?.type || selectedFile?.type;
    let payload = content;
    if (typeof content === "string") {
      try {
        payload = editorTextToSaveContent(type, content);
      } catch (error) {
        Notify.error({
          type: "Configurations",
          message: error?.message || "Invalid editor content",
        });
        return;
      }
    }
    setSaving(true);
    requests.admin(dispatch).postAdminRequest(
      {
        action: "write",
        file: selectedFile.name,
        path: selectedFile.path || "Admin",
        content: payload,
      },
      uri,
      (res) => {
        setSaving(false);
        Notify.success({
          type: "Configurations",
          message: res?.message || "File Saved Successfully",
        });
        if (typeof content === "string") {
          setRawContent(content);
        }
        if (payload && typeof payload === "object") {
          setFilePayload((prev) => (prev ? { ...prev, content: payload } : prev));
        } else if (typeof payload === "string") {
          setFilePayload((prev) =>
            prev ? { ...prev, content: payload } : { type, content: payload }
          );
        }
      },
      (error) => {
        setSaving(false);
        Notify.error({
          type: "Configurations",
          message: error?.message || "Failed to save file",
        });
      }
    );
  };

  const resolveStructuredEditor = () => {
    if (editorUi?.editor && editorUi.editor !== "layout") {
      return editorUi.editor;
    }
    return (
      editorUi?.fallbackEditor ||
      fileLayout?.fallbackEditor ||
      filePayload?.type ||
      CONFIG_TYPES.OTHER
    );
  };

  const renderRawCodeEditor = () => (
    <CodeEditor
      key={`raw-${selectedFile?.name}-${rawEditorLanguage}`}
      value={rawContent}
      language={rawEditorLanguage}
      saving={saving}
      onSave={saveFile}
    />
  );

  const renderStructuredEditor = () => {
    const mode = resolveStructuredEditor();

    if (mode === "properties" || mode === CONFIG_TYPES.PROPERTIES) {
      return (
        <PropertiesEditor
          content={filePayload.content || {}}
          saving={saving}
          onSave={saveFile}
        />
      );
    }

    if (mode === "xml" || mode === CONFIG_TYPES.XML) {
      return (
        <CodeEditor
          value={rawContent}
          language="xml"
          saving={saving}
          onSave={saveFile}
        />
      );
    }

    if (mode === "json" || mode === CONFIG_TYPES.JSON) {
      return (
        <CodeEditor
          value={rawContent}
          language="json"
          saving={saving}
          onSave={saveFile}
        />
      );
    }

    return (
      <CodeEditor
        value={rawContent}
        language={resolveEditorLanguage(editorUi, filePayload.type)}
        saving={saving}
        onSave={saveFile}
      />
    );
  };

  const renderEditor = () => {
    if (!selectedFile) {
      return <Empty description="Select a configuration file to edit" />;
    }
    if (readLoading) {
      return (
        <div className="hi-config-editor-loading">
          <Spin />
        </div>
      );
    }
    if (!filePayload) {
      return <Empty description="Unable to load file content" />;
    }

    if (forceRawEditor) {
      return renderRawCodeEditor();
    }

    if (useFormLayout) {
      const format = fileLayout.format || filePayload.type || "xml";
      const formContent =
        format === "xml" || format === CONFIG_TYPES.XML
          ? rawContent || filePayload.content || ""
          : filePayload.content;

      return (
        <LayoutFormEditor
          layout={fileLayout}
          content={formContent}
          format={format}
          saving={saving}
          onSave={saveFile}
        />
      );
    }

    return renderStructuredEditor();
  };

  const renderCategoryHeader = (category) => (
    <span className="hi-config-category-header">
      <span>{category.title}</span>
      <Tag className="hi-config-category-count">{category.files.length}</Tag>
      {category.description ? (
        <Tooltip title={category.description}>
          <InfoCircleOutlined className="hi-config-category-info" />
        </Tooltip>
      ) : null}
    </span>
  );

  const editorTitle =
    (useFormLayout && fileLayout?.title) ||
    fileLayout?.title ||
    editorUi?.title ||
    selectedFile?.title ||
    selectedFile?.name ||
    "Editor";

  const editorDescription =
    (useFormLayout && fileLayout?.description) ||
    fileLayout?.description ||
    editorUi?.description ||
    selectedFile?.description ||
    null;

  return (
    <Row className="hi-configurations" gutter={12}>
      <Col span={7} className="hi-config-file-panel">
        <div className="hi-config-file-panel-header">
          <Title level={5}>{layout?.title || "Configuration Files"}</Title>
          <Button
            icon={<SyncOutlined spin={listLoading} />}
            onClick={() => {
              fetchLayout();
              fetchFiles();
            }}
            size="small"
          />
        </div>
        {layout?.description ? (
          <Text type="secondary" className="hi-config-layout-description">
            {layout.description}
          </Text>
        ) : null}
        <Search
          allowClear
          placeholder="Filter by name or description"
          value={fileFilter}
          onChange={(event) => setFileFilter(event.target.value)}
          className="hi-config-file-search"
        />
        <Spin spinning={listLoading}>
          {filteredCategories.length ? (
            <Collapse
              className="hi-config-category-collapse"
              defaultActiveKey={activeCategoryKeys}
              key={fileFilter || "all"}
              ghost
            >
              {filteredCategories.map((category) => (
                <Panel
                  header={renderCategoryHeader(category)}
                  key={category.key}
                >
                  <List
                    className="hi-config-file-list"
                    size="small"
                    dataSource={category.files}
                    renderItem={(item) => (
                      <List.Item
                        className={
                          selectedFile?.name === item.name
                            ? "hi-config-file-item active"
                            : "hi-config-file-item"
                        }
                        onClick={() => loadFile(item)}
                      >
                        <List.Item.Meta
                          avatar={<FileOutlined />}
                          title={
                            <Tooltip title={item.name}>
                              <span>{item.title || item.name}</span>
                            </Tooltip>
                          }
                          description={
                            item.description ? (
                              <Text
                                type="secondary"
                                className="hi-config-file-description"
                                ellipsis={{ tooltip: item.description }}
                              >
                                {item.description}
                              </Text>
                            ) : null
                          }
                        />
                      </List.Item>
                    )}
                  />
                </Panel>
              ))}
            </Collapse>
          ) : (
            <Empty description="No configuration files found" />
          )}
        </Spin>
      </Col>
      <Col span={17} className="hi-config-editor-panel">
        <div className="hi-config-editor-header">
          <div>
            <Title level={5} className="hi-config-editor-title">
              {editorTitle}
            </Title>
            {selectedFile && editorDescription ? (
              <Text type="secondary">{editorDescription}</Text>
            ) : null}
          </div>
          <Space>
            {showEditorToggle && (
              <Button
                size="small"
                icon={forceRawEditor ? <FormOutlined /> : <CodeOutlined />}
                onClick={() => {
                  // Keep raw text in sync when leaving structured properties/json editors.
                  if (!forceRawEditor && filePayload) {
                    setRawContent(
                      contentToEditorText(filePayload.type, filePayload.content)
                    );
                  }
                  setForceRawEditor((prev) => !prev);
                }}
              >
                {forceRawEditor
                  ? hasLayoutSections(fileLayout)
                    ? "Form"
                    : "Editor"
                  : "Raw"}
              </Button>
            )}
            {selectedFile && (
              <Tag color={typeColor[selectedFile.type] || "default"}>
                {(
                  (forceRawEditor && "RAW") ||
                  (useFormLayout && "LAYOUT") ||
                  editorUi?.editor ||
                  selectedFile.type ||
                  ""
                ).toUpperCase()}
              </Tag>
            )}
          </Space>
        </div>
        <div className="hi-config-editor-body">{renderEditor()}</div>
      </Col>
    </Row>
  );
};

export default HIConfigurations;
export { HIConfigurations };
