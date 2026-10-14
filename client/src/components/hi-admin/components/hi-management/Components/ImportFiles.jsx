import { useRef, useState, useEffect } from "react";
import { Checkbox, Button, Form, Radio, Progress, Row, Col, Tooltip, Space, Input } from "antd";
import { UploadOutlined, InfoCircleOutlined, FolderOpenOutlined, EditOutlined } from "@ant-design/icons";
import { v4 as uuidv4 } from "uuid";
import requests from "../../../../../base/requests";
import { useDispatch, useSelector } from "react-redux";
import notify from "../../../../hi-notifications/notify";
import axios from "axios";
import { fileBrowserActions } from "../../../../../redux/actions";
import { HIFileBrowser } from "../../../../hi-fileBrowser/hi-fileBrowser";
import { setFileBrowserFolder } from "../../../../../redux/actions/datasource.actions";
import Base64 from "../../../../../base/utils/Base64";
import { useStreamConsole } from "../../../../../hooks/useStreamConsole";
import StreamConsole from "../../../../common/stream-console/StreamConsole";
import { transferPhaseMessage } from "../../../../../base/resumable-transfer";
let source;

const formatNotificationMessages = (bucket) => {
  if (!bucket || typeof bucket !== "object") {
    return "";
  }
  const entries = Object.entries(bucket);
  if (!entries.length) {
    return "";
  }
  const maxPaths = 5;
  const lines = entries.slice(0, maxPaths).map(([path, messages]) => {
    const text = Array.isArray(messages) ? messages.join(" ") : String(messages || "");
    return `${path}: ${text}`;
  });
  if (entries.length > maxPaths) {
    lines.push(`and ${entries.length - maxPaths} more`);
  }
  return lines.join("\n");
};

const parseImportResult = (importResult) => {
  if (!importResult) return null;
  let payload = importResult;
  if (typeof payload === "string") {
    try {
      payload = JSON.parse(payload);
    } catch {
      return null;
    }
  }
  if (payload?.status !== undefined) {
    return payload.response || payload;
  }
  return payload;
};

const ImportFiles = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const streamResponse = useSelector((state) =>
    Boolean(state.app.applicationSettingsData?.streamResponse),
  );
  const {
    consoleState,
    counts,
    reset: resetConsole,
    appendLog,
    hydrateFromNotification,
    ingestSseChunk,
    connect,
    disconnect,
  } = useStreamConsole();
  const [consoleOpen, setConsoleOpen] = useState(false);
  const lastUploadLogPct = useRef(-1);

  const showImportNotifications = (notification) => {
    if (!notification || consoleOpen) {
      return;
    }
    const warningMsg = formatNotificationMessages(notification.warnings);
    if (warningMsg) {
      Notify.warning({ type: "Network Call", message: warningMsg });
    }
    const errorMsg = formatNotificationMessages(notification.errors);
    if (errorMsg) {
      Notify.error({ type: "Network Call", message: errorMsg });
    }
    const infoMsg = formatNotificationMessages(notification.info);
    if (infoMsg) {
      Notify.info({ type: "Network Call", message: infoMsg });
    }
  };

  const finishImportSuccess = (res, fileName) => {
    Notify.success({
      type: "Network Call",
      message: `Imported ${fileName || "file"} Successfully with ${res.insertCount} Inserts, ${res.skipCount} Skips and ${res.updateCount} Updates`,
    });
    // Non-stream: fill console from response. Stream: already ingested via SSE.
    if (!streamResponse) {
      hydrateFromNotification(res?.notification);
    }
    showImportNotifications(res?.notification);
    setFileUploaded("");
    setCheckedList([]);
    setValue("");
    setIsUploading(false);
    setShowUploadProgress(false);
    setFolderPath("");
  };
  const hiddenFileInput = useRef();
  const importRequestIdRef = useRef(null);
  /** Stage-2 needs explicit /cancelRequest; stage-1 resumable bridges CancelToken → hiAxios.abort. */
  const needsExplicitCancelRequestRef = useRef(false);
  const [browseButton,setBrowseButton] = useState(false);
  const [fileUploaded, setFileUploaded] = useState("");
  const [checkedList, setCheckedList] = useState([]);
  const [isUploading, setIsUploading] = useState(false);
  const [showUploadProgress, setShowUploadProgress] = useState(false);
  const [loading, setLoading] = useState(false);
  const [uploadedPercentage, setUploadedPercentage] = useState(0);
  const [uploadPhase, setUploadPhase] = useState("uploading");
  const [canPauseResume, setCanPauseResume] = useState(false);
  const uploadAxiosRef = useRef(null);
  const [value, setValue] = useState("");
  const [folderPath, setFolderPath] = useState("");
  const [key, setKey] = useState(null);

  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const enforceDatasource = Boolean(applicationSettingsData?.enforceDatasourceExportImport);
  useEffect(() => {
    setLoading(false);
  }, [fileUploaded]);

  const handleChange = (e) => {
    setValue(e.target.value);
  };

  const onUploadFile = (e) => {
    setFileUploaded(e.target.files[0]);
    handleImport({ upload: false, e });
  };

  const handleCancelImport = () => {
    const requestId = importRequestIdRef.current;
    const hadAxios = !!uploadAxiosRef.current?.abort;
    disconnect();
    appendLog("Cancel requested…", "package");
    // Abort in-flight transfer (covers non-resumable signal + resumable controls).
    uploadAxiosRef.current?.abort?.();
    source?.cancel("cancel");
    // Clear progress UI immediately so late onUploadProgress cannot keep the bar moving.
    setShowUploadProgress(false);
    setIsUploading(false);
    setLoading(false);
    setCanPauseResume(false);
    uploadAxiosRef.current = null;
    // Stage-2 when no hiAxios.abort ran: still need /cancelRequest for server worker.
    if (needsExplicitCancelRequestRef.current && requestId && !hadAxios) {
      needsExplicitCancelRequestRef.current = false;
      dispatch((_, __, services) => {
        const hiAxios = services(dispatch);
        hiAxios.instance.get("/cancelRequest", { params: { requestId } });
      });
    } else {
      needsExplicitCancelRequestRef.current = false;
    }
  };

  const handleImport = ({ upload, e }) => {
    source = axios.CancelToken.source();
    if (upload && !fileUploaded) {
      return Notify.error({
        type: "Frontend",
        message: `Please choose .zip file`,
      });
    }

    const requestId = uuidv4();
    importRequestIdRef.current = requestId;
    // Stage 2 needs /cancelRequest; stage 1 resumable bridges CancelToken → abort.
    needsExplicitCancelRequestRef.current = !!upload;
    dispatch((_, __, services) => {
      const hiAxios = services(dispatch);
      hiAxios.requestId = requestId;
    });

    const uploadingFile = !!(e && e.target.files?.length);
    const selectedFile = uploadingFile ? e.target.files[0] : fileUploaded;
    const uploadFileName = selectedFile?.name || fileUploaded?.name;

    if (uploadingFile) {
      // Phase 1: new file — clear previous console (live stream or end-of-request hydrate).
      disconnect();
      resetConsole();
      lastUploadLogPct.current = -1;
      if (streamResponse) {
        const sizeMb =
          selectedFile?.size != null
            ? `${(selectedFile.size / (1024 * 1024)).toFixed(2)} MB`
            : "";
        appendLog(
          sizeMb
            ? `Selected ${uploadFileName} (${sizeMb}). Uploading package…`
            : `Selected ${uploadFileName}. Uploading package…`,
        );
      }
      setIsUploading(true);
      setShowUploadProgress(true);
      setUploadedPercentage(0);
      setUploadPhase("preparing");
      setCanPauseResume(false);
      uploadAxiosRef.current = null;
    }

    if (upload) {
      // Phase 2: keep phase-1 console lines; append live stream or final notification.
      disconnect();
      if (streamResponse) {
        appendLog("Starting resource import…", "import");
      }
      setIsUploading(true);
      setShowUploadProgress(false);
    }

    const formData = new FormData();

    const data = {
      onConflict: upload ? value : "skip",
      upload,
      options: {
        share: checkedList.includes("share"),
        dataSource: checkedList.includes("dataSource"),
        schedules: checkedList.includes("schedules"),
        recycleBin: checkedList.includes("recycleBin"),
      },
      destination: folderPath,
    };
    if(upload) {
      data.key = key;
    }
    formData.append("formData", Base64.encode(JSON.stringify(data)));
    if (e) {
      formData.append("file", e.target.files[0]);
    }

    if (!upload) {
      setLoading(true);
    }

    const hiAxios = requests.admin(dispatch).postUploadFile(
      formData,
      {
        stream: !!(upload && streamResponse),
        cancelToken: source.token,
        params: { requestId },
        // Progress SSE → console only when streamResponse is enabled.
        onEvent: streamResponse ? ingestSseChunk : undefined,
        onUploadProgress: (progressEvent) => {
          const total = progressEvent.total || 0;
          const percentCompleted =
            total > 0 ? Math.round((progressEvent.loaded * 100) / total) : 0;
          setUploadedPercentage(percentCompleted);
          if (progressEvent.phase) {
            setUploadPhase(progressEvent.phase);
          }
          if (streamResponse && !upload && total > 0) {
            const bucket = Math.floor(percentCompleted / 25) * 25;
            if (bucket !== lastUploadLogPct.current && bucket > 0) {
              lastUploadLogPct.current = bucket;
              appendLog(`Upload progress ${bucket}%`, "file");
            }
          }
        },
      },
      (res) => {
        setCanPauseResume(false);
        uploadAxiosRef.current = null;
        if (!upload) {
          setLoading(false);
          setIsUploading(false);
          setShowUploadProgress(false);
          const options = res?.options || {};
          Object.entries(options).forEach(([key, value]) => {
            value && setCheckedList((prevList) => [...prevList, key]);
          });
          setKey(res?.key);
          // Phase-1 (analyze) logs/warnings live on notification — hydrate for stream and non-stream.
          hydrateFromNotification(res?.notification);
          appendLog(
            `Package prepared${res?.key ? ` (key ${res.key})` : ""}. Choose options and click Import.`,
            "file",
          );
          return;
        }
        if (upload && streamResponse && res?.eventsUrl) {
          // Streaming import uses a server-generated requestId for the worker Future.
          if (res.requestId) {
            importRequestIdRef.current = res.requestId;
            needsExplicitCancelRequestRef.current = true;
          }
          // Prefer server eventsUrl (includes requestId when backend wired).
          const streamUrl =
            res.eventsUrl.includes("requestId=") || !res.requestId
              ? res.eventsUrl
              : `${res.eventsUrl}${res.eventsUrl.includes("?") ? "&" : "?"}requestId=${encodeURIComponent(res.requestId)}`;
          // Do not clear — continue after file-phase logs. Drawer stays closed until Show logs.
          connect(streamUrl, { clear: false })
            .then((ready) => {
              const parsed = parseImportResult(ready?.importResult);
              if (parsed) {
                finishImportSuccess(parsed, uploadFileName);
              } else {
                setIsUploading(false);
                Notify.error({
                  type: "Network Call",
                  message: "Import finished but result could not be read",
                });
              }
            })
            .catch((err) => {
              setIsUploading(false);
              needsExplicitCancelRequestRef.current = false;
              if (err?.name === "AbortError" || err?.message === "cancel") {
                appendLog("Import cancelled.", "package");
                Notify.success({
                  type: "Network Call",
                  message: "Request cancelled successfully",
                });
              } else {
                Notify.error({
                  type: "Network Call",
                  message: err?.message || "Import failed",
                });
              }
            });
          return;
        }
        if (upload) {
          finishImportSuccess(res, uploadFileName);
        }
      },
      (e) => {
        setCanPauseResume(false);
        uploadAxiosRef.current = null;
        if (e?.message === "cancel" || e?.name === "AbortError") {
          appendLog("Import cancelled.", "package");
          Notify.success({
            type: "Network Call",
            message: "Request cancelled successfully",
          });
        }
        disconnect();
        setLoading(false);
        setIsUploading(false);
        setShowUploadProgress(false);
        needsExplicitCancelRequestRef.current = false;
      }
    );
    uploadAxiosRef.current = hiAxios;
    // Pause/Resume only when resumable upload is enabled and handlers were attached.
    const resumableOn =
      applicationSettingsData?.resumableUpload !== false &&
      applicationSettingsData?.resumableUpload !== "false";
    setCanPauseResume(
      resumableOn && typeof hiAxios?.pauseUpload === "function",
    );

    if (e) {
      e.target.value = "";
    }
  };

  const handleCheckedList = (list) => {

    const updatedList =
    enforceDatasource && key && !list.includes("dataSource")
      ? [...list, "dataSource"]
      : list;

    setCheckedList(updatedList);
  };

  return (
    <div className="import-container">
      <Form
      data-testid = "hi-admin-importfiles-form"
        labelCol={{
          span: 12,
        }}
        wrapperCol={{
          span: 14,
        }}
        layout="horizontal"
      >
        <Form.Item
          label={[
            <Tooltip title="Please export the files/folders using Filebrowser before Importing">
              <InfoCircleOutlined style={{ marginRight: "5px", fontSize: "14px" }} />
            </Tooltip>,
            "Choose .zip file",
          ]}
        >
          <input
            style={{ display: "none" }}
            type="file"
            onChange={onUploadFile}
            accept=".zip"
            ref={hiddenFileInput}
          // className="import-file"
          />
          <Space>
            <Button
            data-testid = "hi-admin-importfiles-btn"
              type="primary"
              loading={loading}
              onClick={() => {
                hiddenFileInput.current.click();
              }}
            >
              Select File
            </Button>
            {fileUploaded && <span>{fileUploaded.name}</span>}
          </Space>
        </Form.Item>
        <Form.Item label={["Options"]}>
          <Checkbox.Group data-testid = "hi-admin-importfiles-checkbox" value={checkedList} onChange={handleCheckedList}>
            <div>
              <Tooltip  title="Security information of users, roles, permissions and organizations">
                <Checkbox  value="share">Share Details</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Datasources related metadata">
                <Checkbox value="dataSource" disabled={Boolean(key) && enforceDatasource} >Datasources</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Schedules of the reports">
                <Checkbox value="schedules">Schedules</Checkbox>
              </Tooltip>
            </div>
            <div>
              <Tooltip title="Soft-deleted resources and related Recycle Bin entries">
                <Checkbox value="recycleBin">Recycle Bin</Checkbox>
              </Tooltip>
            </div>
          </Checkbox.Group>
        </Form.Item>
        <Form.Item label={["On Conflict"]}>
          <Radio.Group data-testid = "hi-admin-importfiles-radiobtn" onChange={handleChange} value={value}>
            <Tooltip title="New files will not be created">
              <Radio value={"skip"}>Skip</Radio>
            </Tooltip>
            <Tooltip title="Updates the conflicted files">
              <Radio value={"update"}>Update</Radio>
            </Tooltip>
          </Radio.Group>
        </Form.Item>
        <Form.Item label={[
            <Tooltip title="Please choose the location where you want to import the zip file. If you don't choose any location, it will use the existing one from zip file.">
              <InfoCircleOutlined style={{ marginRight: "5px", fontSize: "14px" }} />
            </Tooltip>,
            "Location",
          ]}>
        <Row>
                <Col>
                  <Form.Item data-testid = "hi-admin-importfiles-folderPath">
                    <Input style={{width:"100%"}} disabled  value={folderPath} />
                  </Form.Item>
                </Col>
                  <Col>
                    <Form.Item>
                      <Button
                      style={{ marginLeft:"none"}}
                        type="primary"
                        onClick={() =>{ 
                          dispatch(fileBrowserActions.setShowFileBrowser(true))
                          setBrowseButton(true)
                        }
                        }
                        icon={<FolderOpenOutlined />}
                      >
                        Browse
                      </Button>
                    </Form.Item>
                  </Col>
              </Row>
        </Form.Item>
        <Form.Item label={["Upload File"]}>
          <Space>
          <Button
            type="primary"
            icon={<UploadOutlined />}
            onClick={() => handleImport({ upload: true, e: null })}
            loading={isUploading ? true : false}
          >
            Import
          </Button>
          {(isUploading || counts.total > 0) ? (
            <Button type="link" onClick={() => setConsoleOpen(true)}>
              Show logs
            </Button>
          ) : null}
          </Space>
          {isUploading && (
            <Row align="middle" gutter={8}>
              <Col span={8}>
                {showUploadProgress ? (
                  <>
                    <Progress percent={uploadedPercentage} />
                    {canPauseResume ? (
                      <div style={{ fontSize: 12, color: "rgba(0,0,0,0.45)", marginTop: 4 }}>
                        {transferPhaseMessage(uploadPhase, "upload")}
                      </div>
                    ) : null}
                  </>
                ) : (
                  <Progress percent={100} status="active" showInfo={false} />
                )}
              </Col>
              <Col>
                <Space>
                  {canPauseResume &&
                    showUploadProgress &&
                    (uploadPhase === "paused" ? (
                      <span
                        className="clear-button"
                        onClick={() => uploadAxiosRef.current?.resumeUpload?.()}
                      >
                        Resume
                      </span>
                    ) : (
                      <span
                        className="clear-button"
                        onClick={() => uploadAxiosRef.current?.pauseUpload?.()}
                      >
                        Pause
                      </span>
                    ))}
                  <span className="clear-button" onClick={handleCancelImport}>
                    Cancel
                  </span>
                </Space>
              </Col>
            </Row>
          )}
        </Form.Item>
      </Form>
        { browseButton ? 
          <HIFileBrowser
          contextMenuOptions={{
            append: false,
            options: browseButton ? [
              {
                name: "Use This Folder",
                types: ["folder"],
                icon: <EditOutlined />,
                callback: (rec) => {
                  dispatch(setFileBrowserFolder({ path: rec.path }));
                  setFolderPath(rec.path);
                  setBrowseButton(false);
                },
              },
            ] : [],
          }}
          onFbClose={() => { setBrowseButton(false)}}
        />   : null  }
      <StreamConsole
        open={consoleOpen}
        onClose={() => setConsoleOpen(false)}
        title="Import console"
        consoleState={consoleState}
        counts={counts}
      />
    </div>
  );
};

export default ImportFiles;
