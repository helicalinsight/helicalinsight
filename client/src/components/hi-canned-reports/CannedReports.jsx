import {
  CloseOutlined,
  InfoCircleOutlined,
  PlayCircleOutlined,
  QuestionCircleOutlined,
  SaveOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import { Editor } from "@monaco-editor/react";
import {
  Button,
  Col,
  Collapse,
  Drawer,
  Popover,
  Row,
  Table,
  Tooltip,
  TreeSelect,
  Typography,
} from "antd";
import { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Panel, PanelGroup } from "react-resizable-panels";
import { VList } from "virtuallist-antd";
import { HITabs } from "..";
import "../../../node_modules/codemirror/lib/codemirror.css";
import "../../../node_modules/codemirror/mode/css/css.js";
import "../../../node_modules/codemirror/mode/javascript/javascript.js";
import "../../../node_modules/codemirror/mode/xml/xml.js";
import "../../../node_modules/codemirror/theme/material.css";
import requests from "../../base/requests/index.js";
import constants from "../../constants/index.js";
import {
  fileBrowserActions,
  hcrActions,
  hcrHandleEditingDsPaneItem,
} from "../../redux/actions";
import CustomSkeletonFilebrowser from "../common/custom-icons/CustomSkeletons/filebrowser/CustomSkeletonFilebrowser.jsx";
import ResizeHandle from "../common/hi-reSize/hi-resizeHandle.jsx";
import { CopyText } from "../hi-datasources/utils/tooltipDrawer.jsx";
import notify from "../hi-notifications/notify.js";
import { monacoReactCodeEditorOptions } from "../hi-reports/utils/constants.js";
import "./cannedReports.scss";
import { hcrDSParameter, hcrDSQuery } from "./hcr-constants.js";
import CanvasDiagram from "./hcrCanvas/hcrCanvasDiagram";
import {
  dataProcessorHcr,
  fetchGetDataSourceHcr,
  getDataSourceType,
  handleRunQuery,
  hcrSidebarPanes,
  intToStringFuncHcr,
} from "./hcrHelperMethods.js";
import HcrParameterDrawer from "./hcrParameterDrawer.jsx";
import HcrSidebar from "./hcrSidebar.jsx";
import LoadingBar from "../common/components/hi-loading-bar.jsx";
const { Paragraph, Title, Text } = Typography;

const hcrSidebarWidth = 16.4;

function CannedReports({
  tabNum,
  openFB,
  flowchartInstance,
  nodesPositions,
  queryTempuuidsMap,
  lastSelectedNodeRef,
  handleAbort,
  getApiInstance,
  resetQueryuuids = () => { }
}) {
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === tabNum,
      ),
    ) || {};
  const toggleSidebar = useSelector((state) => state.app.toggleSidebar);
  const { selectedConnectionDetails, sidebarPaneActiveKey, hcrQueryRunning, subDataSets = [] } =
    activeTab;
  const dsPaneTypes = useSelector(
    (state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === tabNum,
      )?.dsPaneTypes || [],
  );
  const selectedDS = useSelector(
    (state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === tabNum,
      )?.selectedDS || {},
  );
  const datasourceListToRender = useSelector(
    (state) => state.cannedReports.present.datasourceListToRender,
  );
  const dataSources = useSelector(
    (state) => state.cannedReports.present.allDataSourceTypes,
  );
  const dataSourceTypes = useSelector(
    (state) => state.cannedReports.present.dataSourceTypes,
  );
  const driversList = useSelector(
    (state) => state.cannedReports.present.driversList,
  );
  const reqQuery =
    dsPaneTypes
      ?.find(
        (connType) => connType.dataSourcePane === selectedDS?.dataSourcePane,
      )
      ?.menu?.find((query) => query.id === selectedDS?.id) || null;
  const addTabRef = useRef(null);
  const dispatch = useDispatch();
  const [connectionData, setConnectionData] = useState([]);
  const [treeVal, setTreeVal] = useState("Add Connection");
  const Notify = notify(dispatch);
  const isQuerySaved = reqQuery?.isSaved || reqQuery === null;
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);
  const [parametersList, setParametersList] = useState([]);
  const [subDataParametersList, setSubDataParametersList] = useState([]);

  const { saveQueryReportState, saveExecuteReportQuery } =
    requests.cannedReport(dispatch);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [open, setOpen] = useState(false);
  const isQuery = selectedDS?.dataSourcePane !== hcrDSParameter;
  // Clean when tab changesss
  useEffect(() => {
    document
      .querySelectorAll(".x6-dropdown-overlay")
      .forEach((ele) => ele.remove());
  }, [tabNum, sidebarPaneActiveKey, flowchartInstance]);

  useEffect(() => {
    if (reqQuery) {
      if (reqQuery.connectionDetails) {
        const { baseType, id } = reqQuery.connectionDetails;
        setTreeVal(`${baseType}-${id}`);
      } else {
        setTreeVal("Add Connection");
      }
    }
  }, [selectedDS, reqQuery?.connectionDetails]);

  useEffect(() => {
    if (reqQuery?.connectionDetails) {
      const { baseType, id } = reqQuery.connectionDetails;
      setTreeVal(`${baseType}-${id}`);
    } else {
      setTreeVal("Add Connection");
    }
    setDropdownOpen(false);
  }, [reqQuery?.connectionDetails]);

  useEffect(() => {
    if (selectedDS && selectedConnectionDetails) {
      dispatch(
        hcrActions.handleEditingDsPaneItem({
          dataSourcePane: selectedDS.dataSourcePane,
          itemId: reqQuery.id,
          key: "connectionDetails",
          value: selectedConnectionDetails,
        }),
      );
    }
  }, [selectedConnectionDetails]);

  useEffect(() => {
    const getTooltip = (node) => {
      const tooltipArr = ["id", "type", "dataSourceType"];
      return (
        <div className="hi-hcr-ds-info-tooltip">
          <table>
            <tbody>
              {tooltipArr.map((key) => {
                return (
                  <tr key={key} style={{ color: "white" }}>
                    <td>{key[0].toUpperCase() + key?.slice(1)} &nbsp;</td>
                    <td>{node[key]}</td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      );
    };
    if (datasourceListToRender) {
      const treeData = datasourceListToRender?.map((ds) => {
        return {
          title: ds.name,
          value: ds.name,
          isParent: true,
          name: ds.name,
          children: ds.children.map((dsChild) => {
            const { id, type } = dsChild.data;
            let { dataSourceType, name } = dsChild;
            if (!dataSourceType) {
              dataSourceType = getDataSourceType(dataSourceTypes, type);
            }
            return {
              id,
              baseType: dsChild.baseType,
              dataSourceType,
              name,
              type,
              title: (
                <div className="connection-title">
                  <Tooltip title={name}>
                    <Paragraph
                      style={{
                        maxWidth: 120,
                        marginBottom: 0,
                      }}
                      ellipsis={true}
                    >
                      {name}
                    </Paragraph>
                  </Tooltip>
                  <Popover
                    placement={"left"}
                    overlayClassName="hcr-ds-popover"
                    content={getTooltip({
                      id,
                      dataSourceType,
                      type,
                    })}
                    color="rgba(0, 0, 0, 0.7)"
                  >
                    <InfoCircleOutlined className="metadata-info" />
                  </Popover>
                </div>
              ),
              value: `${dsChild.baseType}-${id}`,
            };
          }),
        };
      });
      setConnectionData(treeData);
    }
  }, [datasourceListToRender]);

  useEffect(() => {
    addTabRef?.current?.edit("", "add");
    if (!datasourceListToRender?.length) {
      fetchGetDataSourceHcr({ dispatch });
    }
  }, []);

  function getPreviewTabCls() {
    if (reqQuery) {
      const field = [];
      reqQuery.executeQueryData?.field?.forEach((ele) => {
        field.push({
          title: () => (
            <Tooltip
              mouseEnterDelay={constants.mouseEnterDelay}
              title={ele.name}
            >
              <Title
                style={{ margin: 0, maxWidth: 120, }}
                level={5}
                // style={{ maxWidth: 60, marginBottom: 0 }}
                ellipsis={true}
              >
                {ele.name}
              </Title>
            </Tooltip>
          ),
          dataIndex: ele.name,
          key: ele.name,
          className: "table-ellipsis",
          render: (name, record) => {
            return (
              <Tooltip
                overlayClassName="security-validateTable-tooltip"
                title={name}
              >
                <Paragraph
                  style={{ maxWidth: 120, marginBottom: 0 }}
                  ellipsis={true}
                >
                  {name}
                </Paragraph>
              </Tooltip>
            );
          },
        });
      });
      return field;
    }
    return [];
  }

  const addReport = () => { };

  const handleSideTabs = ({ panes, activeKey }) => {
    if (activeKey) {
      if (activeKey !== "reports") {
        // setTabPanes({ panes, activeKey });
        dispatch(hcrActions.handleHcrSidebarPaneActiveKey(activeKey));
      } else {
        dispatch(fileBrowserActions.setShowFileBrowser(true));
        openFB({});
      }
    }
  };

  const vComponents = useMemo(() => {
    let obj = {
      vid: "hcrPreview",
      resetTopWhenDataChange: false,
      height: "100%",
    };
    // if (executeData?.data.length > 4) {
    //     obj.height = 260;
    // }
    return VList(obj);
  }, []);

  function refreshDSList() {
    fetchGetDataSourceHcr({
      dispatch, cb: () => {
        Notify.success({
          message: "DataSources List refreshed successfully",
        });
      }
    });
  }

  const checkIfGroovyConnection = (reqQuery) => {
    const connectionDetails = reqQuery?.connectionDetails;
    return ["sql.jdbc.groovy", "sql.jdbc.groovy.managed"].includes(
      connectionDetails?.baseType,
    );
  };

  const getEditorExtraOptions = (reqQuery) => {
    let options = {};
    if (checkIfGroovyConnection(reqQuery)) {
      options = {
        language: "javascript",
        // theme: 'vs-dark'
      };
    } else {
      options = {
        language: "sql",
      };
    }
    return options;
  };

  useEffect(() => {
    if (sidebarPaneActiveKey === "") {
      dispatch(hcrActions.handleHcrSidebarPaneActiveKey("canvas"));
    }
  }, [sidebarPaneActiveKey]);

  const handleUnload = (e) => {
    const dev = process.env.NODE_ENV === "development";
    if (dev) return;
    const message = "o/";
    (e || window.event).returnValue = message; //Gecko + IE
    return message;
  };

  useEffect(() => {
    window.addEventListener("beforeunload", handleUnload);
    return () => {
      window.removeEventListener("beforeunload", handleUnload);
    };
  }, []);

  return (
    <div className="cr-tab-content">
      <HcrParameterDrawer
        selectedDS={selectedDS}
        reqQuery={reqQuery}
        setIsDrawerOpen={setIsDrawerOpen}
        isDrawerOpen={isDrawerOpen}
        setParametersList={setParametersList}
        parametersList={parametersList}
        subDataParametersList={subDataParametersList}
        Notify={Notify}
        dispatch={dispatch}
        dsPaneTypes={dsPaneTypes}
        resetQueryuuids={resetQueryuuids}
      />
      <Row style={{ height: "100%", flexWrap: "noWrap" }}>
        {!toggleSidebar && (
          <Col
            className="hcr-sidebar"
            style={{
              background: "white",
              width: `${hcrSidebarWidth}%`,
            }}
          >
            <HITabs // reports, ds, canvas
              add={addReport}
              addTabRef={addTabRef}
              // remove={deleteReport}
              tabData={{
                activeKey: sidebarPaneActiveKey,
                panes: hcrSidebarPanes,
              }}
              setTabData={handleSideTabs}
              className="cr-left-tabs"
              type="line"
              tabPosition="top"
              isTooltipReq={false}
            />
            <HcrSidebar selectedTab={sidebarPaneActiveKey} />
          </Col>
        )}
        <Col
          className={"hcr-canvas-col"}
          style={{ width: !toggleSidebar ? "83.2%" : "100%" }}
        >
          {sidebarPaneActiveKey !== "datasource" &&
            sidebarPaneActiveKey !== "" ? (
            <CanvasDiagram
              sideBarPortion={hcrSidebarWidth}
              flowchartInstance={flowchartInstance}
              nodesPositions={nodesPositions}
              tabNum={tabNum}
              contentTab={sidebarPaneActiveKey}
              lastSelectedNodeRef={lastSelectedNodeRef}
            />
          ) : (
            <>
              <div className="edtr-pane-connections-container">
                <div>
                  <span className="selected-editor">
                    {checkIfGroovyConnection(reqQuery) ? (
                      <span className="groovy-question-icon">
                        <QuestionCircleOutlined
                          onClick={() => {
                            setOpen(true);
                          }}
                        />
                      </span>
                    ) : null}
                    Editor Pane: {reqQuery?.name}
                  </span>
                  {checkIfGroovyConnection(reqQuery) ? (
                    <span className="hcr-selectedEditor selected-editor">
                      Groovy
                    </span>
                  ) : (
                    <span className="hcr-selectedEditor selected-editor">
                      SQL
                    </span>
                  )}
                </div>
                <div className="edtr-pane-editor-info-conn">
                  {isQuery && (
                    <Tooltip
                      title="Query execution is mandatory to reflect field names and no.of fields in fields section."
                      placement="right"
                    >
                      <InfoCircleOutlined />
                    </Tooltip>
                  )}

                  <div
                    style={{
                      display: "flex",
                      alignItems: "center",
                      width: 220,
                      justifyContent: "space-between",
                      marginRight: 5,
                    }}
                  >
                    <TreeSelect
                      showSearch
                      className="connection-tree"
                      open={dropdownOpen}
                      onDropdownVisibleChange={(open) => setDropdownOpen(open)}
                      value={treeVal}
                      dropdownStyle={{
                        maxHeight: 400,
                        overflow: "auto",
                      }}
                      onSelect={(selectedVal, node) => {
                        if (reqQuery) {
                          if (!node.isParent) {
                            setTreeVal(selectedVal); // node.value
                            const { title, value, ...rest } = node;
                            dispatch(
                              hcrHandleEditingDsPaneItem({
                                dataSourcePane: selectedDS.dataSourcePane,
                                itemId: reqQuery.id,
                                key: "connectionDetails",
                                value: rest,
                              }),
                            );
                          }
                        } else {
                          Notify.warning({
                            message: "Please select a query",
                          });
                        }
                      }}
                      treeData={connectionData}
                      filterTreeNode={(inputValue, treeNode) => {
                        inputValue = inputValue.toLowerCase();
                        if (treeNode.isParent) {
                          return treeNode.name.includes(inputValue);
                        }
                        return (
                          treeNode.name.toLowerCase().includes(inputValue) ||
                          treeNode.id.toLowerCase().includes(inputValue) ||
                          treeNode.baseType
                            .toLowerCase()
                            .includes(inputValue) ||
                          treeNode.dataSourceType
                            .toLowerCase()
                            .includes(inputValue)
                        );
                      }}
                    />
                    <Tooltip title="Refresh" placement="left">
                      <SyncOutlined
                        data-testid="hcr-refresh-icon"
                        onClick={() => {
                          refreshDSList();
                        }}
                      />
                    </Tooltip>
                  </div>
                </div>
              </div>
              <div style={{ background: "white" }}>
                <PanelGroup direction="vertical" style={{ height: "87.5vh" }}>
                  <Panel>
                    <div className={"editor-container hcr-editor-container"}>
                      <Editor
                        className="hcr-editor"
                        value={reqQuery?.config}
                        onChange={(value) => {
                          dispatch(
                            hcrHandleEditingDsPaneItem({
                              dataSourcePane: selectedDS?.dataSourcePane,
                              itemId: reqQuery?.id,
                              key: "config",
                              value,
                            }),
                          );
                        }}
                        options={{
                          ...monacoReactCodeEditorOptions,
                          renderValidationDecorations: "off",
                          readOnly: reqQuery ? false : true,
                          lineNumbersMinChars: 1,
                        }}
                        defaultLanguage="sql"
                        {...(getEditorExtraOptions(reqQuery) || {})}
                      />
                      {!(reqQuery?.config || "").length ? (
                        <div className="hcr-editor-placeholder-container">
                          <span className="placeholder-text">
                            Please write a query and save/run
                          </span>
                        </div>
                      ) : null}
                      <div className="editor-actions">
                        <Button
                          className={`editor-save ${!isQuerySaved ? "hcr-highlight-save" : ""
                            }`}
                          icon={<SaveOutlined />}
                          disabled={isQuerySaved}
                          // type="primary"
                          onClick={() => {
                            dispatch(
                              hcrActions.handleEditingDsPaneItem({
                                dataSourcePane: selectedDS?.dataSourcePane,
                                itemId: reqQuery?.id,
                                key: "isSaved",
                                value: true,
                              }),
                            );
                          }}
                        >
                          Save
                        </Button>
                        <Button
                          className={`editor-run ${isQuerySaved &&
                            reqQuery !== null &&
                            reqQuery.config.trim()
                            ? "hcr-highlight-save"
                            : ""
                            }`}
                          // className="editor-run"
                          icon={<PlayCircleOutlined />}
                          disabled={
                            !(isQuerySaved && reqQuery !== null) ||
                            !reqQuery.config.trim()
                          }
                          onClick={() => {
                            if (!reqQuery) {
                              Notify.warning({
                                message: "Please select a query",
                              });
                            } else if (!reqQuery.isSaved) {
                              Notify.warning({
                                message: "Please save the query",
                              });
                            } else if (!reqQuery.connectionDetails) {
                              Notify.warning({
                                message: "Please add the connection",
                              });
                            } else {
                              let paraList =
                                dsPaneTypes
                                  .find(
                                    (dsPaneType) =>
                                      dsPaneType.dataSourcePane ===
                                      hcrDSParameter,
                                  )
                                  ?.menu.filter((para) => {
                                    return reqQuery.config?.includes(
                                      "${" + para.name + "}",
                                    );
                                  }) || [];
                              let subParameters = [];
                              if (isQuery) {
                                const subDS = subDataSets.find((ds) => ds.id === reqQuery.id);
                                if (subDS) {
                                  const { parameters = [] } = subDS || {}
                                  subParameters = parameters.filter((para) => reqQuery.config?.includes("${" + para.name + "}")) || [];
                                  paraList = [...paraList, ...subParameters]
                                }
                              }
                              if (paraList.length) {
                                setParametersList(paraList);
                                setSubDataParametersList(subParameters);
                                setIsDrawerOpen(true);
                              } else {
                                handleRunQuery({
                                  reqQuery: {
                                    ...reqQuery,
                                    parameterList: [],
                                  },
                                  isEditedFile: false,
                                  dispatch,
                                  selectedDS,
                                  saveQueryReportState,
                                  saveExecuteReportQuery,
                                  Notify,
                                  paraList,
                                  getApiInstance,
                                  resetQueryuuids
                                });
                              }
                            }
                          }}
                        >
                          Run
                        </Button>
                      </div>
                    </div>
                  </Panel>
                  <ResizeHandle />
                  <Panel defaultSize={45}>
                    <div className="data-preview-container">
                      <div className="data-preview-header">
                        <div className="data-preview-title">
                          <b>Data Preview</b>
                          <Tooltip
                            title="Only 10 records will be shown."
                            placement="left"
                          >
                            <InfoCircleOutlined />
                          </Tooltip>
                        </div>
                      </div>
                      {hcrQueryRunning && (
                        <div className="data-preview-loading-bar">
                          <LoadingBar handleClick={handleAbort} />
                        </div>
                      )}
                      <div className="data-preview-content">
                        {hcrQueryRunning ? (
                          <CustomSkeletonFilebrowser />
                        ) : (
                          <Table
                            className="query-preview-table"
                            dataSource={reqQuery?.executeQueryData?.data || []}
                            columns={[...getPreviewTabCls()]}
                            pagination={false}
                            size={"small"}
                            tableLayout="auto"
                            // scroll={{ y: 160 }}
                            rowClassName={(record, index) => {
                              let className = index % 2 && "table-row-color";
                              return className;
                            }}
                            components={vComponents}
                          />
                        )}
                      </div>
                    </div>
                  </Panel>
                </PanelGroup>
              </div>
            </>
          )}
        </Col>
        {/* <Col span={contentSpans[tabPanes.activeKey][2]} className="">
                    {tabPanes.activeKey !== 'datasource' ? <CanvasPropertyPane groupId={groupId} oldConfigContent={oldConfigContent} tabNum={tabNum} contentTab={tabPanes.activeKey} />
                        : <DataSourcePanes tabNum={tabNum} contentTab={tabPanes.activeKey} />}
                </Col> */}
        <Drawer
          title={"Groovy configuration info"}
          placement="right"
          width="50%"
          onClose={() => setOpen(false)}
          visible={open}
          closeIcon={<CloseOutlined />}
        >
          <div style={{ padding: "20px" }}>
            <Paragraph>
              <Text level={2}>
                In the Groovy section, we write the query/groovy code that will
                be executed to generate the report data. We can dynamically
                change the report content based on specific conditions which are
                used in the snippet. Here, we may specify a where clause in
                order to filter out the data based on the{" "}
                <b>user, org, profile, role</b> session variables. The value of
                these variables are retrived from GroovyUsersSession object.
              </Text>
            </Paragraph>

            <Col>
              <b>Note:</b>
              <p>
                The method{" "}
                <b>GroovyUsersSession.getValue('sessionVariableExpression')</b>{" "}
                always return a String value.
              </p>
            </Col>
            <Title level={4}>How to use?</Title>
            <p>
              Copy the below example and modify the code according to your
              requirement.
            </p>
            <CopyText
              databaseDialect={"groovyCopyCode"}
              name={"Groovy Copy Code"}
              type="groovy"
            />
            <Collapse>
              <pre>
                <code>
                  {`
import com.helicalinsight.efw.utility.GroovyUsersSession;
public String evalCondition() {

    String userName = GroovyUsersSession.getValue('\${user}.name'); 
    userName = userName.replaceAll("'", "");

    String responseJson;

    String selectClause = """select ("destination") as "destination","travel_cost" as "travel_cost" 
    from "travel_details" """;

    if (userName.equals("hiadmin")) {
        whereClause = """where ("destination"='Ambala')""";
    } else {
        whereClause = """where ("destination"='Paris')""";
    }

    responseJson = selectClause + " " + whereClause;

    return responseJson;  
}
`}
                </code>
              </pre>
            </Collapse>

            <Paragraph>
              We can use the user session variables along with query parameters:
            </Paragraph>
            {/* user */}
            <Row>
              <Col span={4}>
                <Paragraph>
                  <Text strong> user :</Text>
                </Paragraph>
              </Col>
              <Col span={20}>
                <Paragraph
                  style={{ display: "flex", gap: "8px", flexWrap: "wrap" }}
                >
                  <Text code>{"${user}.name"}</Text>
                  <Text code>{"${user}.email"}</Text>
                  <Text code>{"${user}.id"}</Text>
                  <Text code>{"${user}.enabled"}</Text>
                  <Text code>{"${user}.isExternalUser"}</Text>
                </Paragraph>
              </Col>
            </Row>
            {/* org */}
            <Row>
              <Col span={4}>
                <Paragraph>
                  <Text strong> org :</Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text code>{"${org}.name"}</Text>
                  <Text code>{"${org}.id"}</Text>
                </Paragraph>
              </Col>
            </Row>
            <Row>
              <Col span={4}>
                <Paragraph>
                  <Text strong> profile :</Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text code>{"${profile}.name"}</Text>
                  <Text code>{"${profile}.id"}</Text>
                  <Text code>{"${profile}.value"}</Text>
                </Paragraph>
              </Col>
            </Row>

            {/* role */}
            <Row>
              <Col span={4}>
                <Paragraph>
                  <Text strong> role :</Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text code>{"${role}.name"}</Text>
                  <Text code>{"${role}.id"}</Text>
                </Paragraph>
              </Col>
            </Row>

            <Row wrap align="middle">
              <Col>
                <Paragraph>
                  <Text strong>report parameters :</Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text code style={{ whiteSpace: "nowrap" }}>
                    {"${parameterName}"}{" "}
                  </Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text strong> eg :</Text>
                </Paragraph>
              </Col>
              <Col>
                <Paragraph>
                  <Text code>{"${booking_platform}"}</Text>
                  <Text code>{"${travel_cost}"}</Text>
                </Paragraph>
              </Col>
            </Row>
          </div>
        </Drawer>
      </Row>
    </div>
  );
}

export { CannedReports };
