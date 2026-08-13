import { useEffect, useMemo, useState } from "react";
import { useDispatch } from "react-redux";
import {
  List,
  Input,
  Empty,
  Spin,
  Typography,
  Button,
  Collapse,
  Tooltip,
  Space,
  Tabs,
} from "antd";
import {
  SyncOutlined,
  SearchOutlined,
  CloseOutlined,
  FileOutlined,
  FileTextOutlined,
  CodeOutlined,
  DatabaseOutlined,
  SafetyCertificateOutlined,
  CloudServerOutlined,
  ApiOutlined,
  CheckCircleOutlined,
  MailOutlined,
  SettingOutlined,
  AppstoreOutlined,
  FormOutlined,
  EditOutlined,
} from "@ant-design/icons";
import { Panel as ResizePanel, PanelGroup, PanelResizeHandle } from "react-resizable-panels";
import requests from "../../../../base/requests";
import { uriConfig } from "../../../../base/requests/admin.request";
import { fetchUiLayout, hasLayoutSections } from "../../../common/ui-generator";
import notify from "../../../hi-notifications/notify";
import PropertiesEditor from "./components/PropertiesEditor";
import CodeEditor from "./components/CodeEditor";
import LayoutFormEditor from "./components/LayoutFormEditor";
import InstantBISettingsEditor from "./components/InstantBISettingsEditor";
import { CONFIG_TYPES } from "./utils/config-tree-utils";
import {
  CONFIGURATION_LAYOUT_CONTENT_ID,
  buildCategorizedFiles,
  contentToEditorText,
  editorTextToSaveContent,
  filterCategorizedFiles,
  resolveEditorLanguage,
  resolveRawEditorLanguage,
  toEditorUiContentId,
  toFileLayoutContentId,
  toSentenceCaseLabel,
} from "./utils/configuration-layout";
import "./hi-configurations.scss";

const { Title } = Typography;
const { Panel } = Collapse;

const CATEGORY_ICONS = {
  sql: DatabaseOutlined,
  "sql security": SafetyCertificateOutlined,
  security: SafetyCertificateOutlined,
  caching: CloudServerOutlined,
  datasource: ApiOutlined,
  validation: CheckCircleOutlined,
  mail: MailOutlined,
  system: SettingOutlined,
  other: AppstoreOutlined,
};

const getCategoryIcon = (category) => {
  const key = String(category?.icon || category?.key || "")
    .trim()
    .toLowerCase();
  return CATEGORY_ICONS[key] || AppstoreOutlined;
};

const getFileExtension = (fileName = "") => {
  const parts = String(fileName).split(".");
  if (parts.length < 2) return "";
  return parts.pop().toLowerCase();
};

const getExtensionIcon = (fileName, fileType) => {
  const extension = getFileExtension(fileName);
  if (extension === "xml" || fileType === CONFIG_TYPES.XML) {
    return <CodeOutlined className="hi-config-file-type-icon hi-config-file-type-icon--xml" />;
  }
  if (extension === "json" || fileType === CONFIG_TYPES.JSON) {
    return (
      <FileTextOutlined className="hi-config-file-type-icon hi-config-file-type-icon--json" />
    );
  }
  if (extension === "properties" || fileType === CONFIG_TYPES.PROPERTIES) {
    return (
      <FileTextOutlined className="hi-config-file-type-icon hi-config-file-type-icon--properties" />
    );
  }
  if (extension === "groovy") {
    return <CodeOutlined className="hi-config-file-type-icon hi-config-file-type-icon--groovy" />;
  }
  return <FileOutlined className="hi-config-file-type-icon" />;
};

const FileTypeIcon = ({ fileName, fileType }) => (
  <Tooltip title={fileName}>
    <span className="hi-config-file-type-icon-wrap">
      {getExtensionIcon(fileName, fileType)}
    </span>
  </Tooltip>
);

const HIConfigurations = ({ apiRef }) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [activeConfigTab, setActiveConfigTab] = useState("system");
  const [layout, setLayout] = useState(null);
  const [files, setFiles] = useState([]);
  const [fileFilter, setFileFilter] = useState("");
  const [searchOpen, setSearchOpen] = useState(false);
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

  const [openCategoryKeys, setOpenCategoryKeys] = useState([]);

  useEffect(() => {
    if (fileFilter) return undefined;
    setOpenCategoryKeys((prev) =>
      prev.filter((key) =>
        filteredCategories.some((category) => category.key === key)
      )
    );
    return undefined;
  }, [filteredCategories, fileFilter]);

  const resolvedOpenCategoryKeys = fileFilter
    ? filteredCategories.map((category) => category.key)
    : openCategoryKeys;

  const useFormLayout = hasLayoutSections(fileLayout) && !forceRawEditor;

  const showEditorToggle = hasLayoutSections(fileLayout);

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
    if (fileMeta?.category) {
      setOpenCategoryKeys((prev) =>
        prev.includes(fileMeta.category)
          ? prev
          : [...prev, fileMeta.category]
      );
    }
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

  const renderCategoryHeader = (category) => {
    const Icon = getCategoryIcon(category);
    return (
      <span className="hi-config-category-header">
        <Tooltip title={category.description || undefined} placement="right">
          <Icon className="hi-config-category-icon" aria-hidden />
        </Tooltip>
        <span className="hi-config-category-title">{category.title}</span>
        <span className="hi-config-category-count">{category.files.length}</span>
      </span>
    );
  };

  const editorTitle = toSentenceCaseLabel(
    (useFormLayout && fileLayout?.title) ||
      fileLayout?.title ||
      editorUi?.title ||
      selectedFile?.title ||
      selectedFile?.name ||
      "Editor"
  );

  const editorDescription =
    (useFormLayout && fileLayout?.description) ||
    fileLayout?.description ||
    editorUi?.description ||
    selectedFile?.description ||
    null;

  const renderSystemConfig = () => (
    <PanelGroup
      direction="horizontal"
      autoSaveId="hi-configurations-system-split"
      className="hi-config-system-row"
    >
      <ResizePanel
        defaultSize={18}
        minSize={12}
        maxSize={32}
        className="hi-config-file-panel"
      >
        <div className="hi-config-file-panel-header">
          {searchOpen ? (
            <Input
              allowClear
              autoFocus
              size="small"
              placeholder="Search file"
              value={fileFilter}
              onChange={(event) => setFileFilter(event.target.value)}
              className="hi-config-file-search-inline"
              prefix={<SearchOutlined />}
            />
          ) : (
            <Title level={5}>Search file</Title>
          )}
          <Space size={4} className="hi-config-file-panel-actions">
            <Tooltip title={searchOpen ? "Hide search" : "Search files"}>
              <Button
                icon={searchOpen ? <CloseOutlined /> : <SearchOutlined />}
                size="small"
                type={searchOpen || fileFilter ? "primary" : "default"}
                ghost={!!(searchOpen || fileFilter)}
                onClick={() => {
                  setSearchOpen((open) => {
                    if (open) {
                      setFileFilter("");
                    }
                    return !open;
                  });
                }}
              />
            </Tooltip>
            <Tooltip title="Refresh">
              <Button
                icon={<SyncOutlined spin={listLoading} />}
                onClick={() => {
                  fetchLayout();
                  fetchFiles();
                }}
                size="small"
              />
            </Tooltip>
          </Space>
        </div>
        <Spin spinning={listLoading}>
          {filteredCategories.length ? (
            <Collapse
              className="hi-config-category-collapse"
              activeKey={resolvedOpenCategoryKeys}
              onChange={(keys) =>
                setOpenCategoryKeys(Array.isArray(keys) ? keys : [keys])
              }
              expandIcon={() => null}
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
                    split={false}
                    dataSource={category.files}
                    renderItem={(item) => {
                      const displayName = item.title || item.name;
                      return (
                        <List.Item
                          className={
                            selectedFile?.name === item.name
                              ? "hi-config-file-item active"
                              : "hi-config-file-item"
                          }
                          onClick={() => loadFile(item)}
                        >
                          <List.Item.Meta
                            avatar={
                              <FileTypeIcon
                                fileName={item.name}
                                fileType={item.type}
                              />
                            }
                            title={
                              <Tooltip title={item.description || displayName}>
                                <span className="hi-config-file-name">
                                  {displayName}
                                </span>
                              </Tooltip>
                            }
                          />
                        </List.Item>
                      );
                    }}
                  />
                </Panel>
              ))}
            </Collapse>
          ) : (
            <Empty description="No configuration files found" image={Empty.PRESENTED_IMAGE_SIMPLE} />
          )}
        </Spin>
      </ResizePanel>
      <PanelResizeHandle className="hi-config-resize-handle" />
      <ResizePanel
        defaultSize={82}
        minSize={50}
        className="hi-config-editor-panel"
      >
        <div className="hi-config-editor-header">
          <div className="hi-config-editor-header-text">
            {selectedFile && editorDescription ? (
              <Tooltip title={editorDescription}>
                <Title level={5} className="hi-config-editor-title">
                  {editorTitle}
                </Title>
              </Tooltip>
            ) : (
              <Title level={5} className="hi-config-editor-title">
                {editorTitle}
              </Title>
            )}
          </div>
          {showEditorToggle ? (
            <Button
              type="link"
              size="small"
              className="hi-config-editor-mode-link"
              icon={forceRawEditor ? <FormOutlined /> : <EditOutlined />}
              onClick={() => {
                if (!forceRawEditor && filePayload) {
                  setRawContent(
                    contentToEditorText(filePayload.type, filePayload.content)
                  );
                }
                setForceRawEditor((prev) => !prev);
              }}
            >
              {forceRawEditor ? "See form" : "Edit"}
            </Button>
          ) : null}
        </div>
        <div className="hi-config-editor-body">{renderEditor()}</div>
      </ResizePanel>
    </PanelGroup>
  );

  return (
    <div className="hi-configurations">
      <Tabs
        className="hi-config-main-tabs"
        activeKey={activeConfigTab}
        onChange={setActiveConfigTab}
      >
        <Tabs.TabPane tab="File Config" key="system">
          {renderSystemConfig()}
        </Tabs.TabPane>
        <Tabs.TabPane tab="Instant BI" key="instantbi">
          <div className="hi-config-instantbi-panel">
            <InstantBISettingsEditor />
          </div>
        </Tabs.TabPane>
      </Tabs>
    </div>
  );
};

export default HIConfigurations;
export { HIConfigurations };
