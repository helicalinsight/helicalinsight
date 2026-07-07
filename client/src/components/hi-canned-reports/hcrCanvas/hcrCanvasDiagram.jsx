import {
  EditorPanels,
  Flowchart,
  FormWrapper
} from "@ant-design/flowchart";
import "@ant-design/flowchart/dist/index.css";
import { setLang } from "@antv/s2";
import {
  Divider,
  Input,
  Menu,
  Select,
  Space
} from "antd";
import { isEmpty } from "lodash";
import {
  useEffect,
  useState
} from "react";
import { useDrop } from "react-dnd";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { flowchartInstance } from "../../../pages";
import { hcrActions } from "../../../redux/actions";
import TutorialInfo from "../../common/hi-tutorial";
import notify from "../../hi-notifications/notify";
import { hcrCanvasViews, hcrDSQuery, hcrTableNodeInitialConfig } from "../hcr-constants";
import waitForForeignObject, {
  getFlowChartNodes,
  getMargin,
  getReqNodeData
} from "../hcrHelperMethods";
import HCRAdvancedTableComponent from "./advanceComponents/table/hcrAdvancedTableComponent";
import HCRAdvancedTableEdit from "./advanceComponents/table/hcrTableEditMode";
import AdvancedTableProperties from "./advancedTableProperties";
import HCRCanvasParameters from "./canvasProperty/hcrCanvasParameters";
import HCRCanvasProperty from "./canvasProperty/hcrCanvasProperty";
import ChartProperties from "./chartsProperties";
import CrosstabProperties from "./crosstabProperties";
import CalculationsParameters from "./hcrCalculationsParameters";
import { getAdvancedTableConfig, getTableDefaultStyles, hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
import HCRCanvasTabs from "./hcrCanvasTabs";
import HCRChartsComponent from "./hcrCharts/hcrChartsComponent";
import HCRCrossTabComponent from "./hcrCrossTab/hcrCrossTabComponent";
import HCRFields from "./hcrFields";
import ImageProperties from "./imageProperties";
import LineProperties from "./lineProperties";
import { ImageNode, LineNode, PageBreakNode, TextNode } from "./nodes";
import PageBreakProperties from "./pageBreakProperties";
import HCRShapeSearch from "./shapeSearch";
import TextProperties from "./textProperties";
setLang("en_US");

const {
  getFlowChartConfig,
  onHCRCanvasConfigChange,
  onChildNodeMove,
  getHcrChartsDefaultProperties,
  handleNodeSelection
} = hcrCanvasPaneHelperMethods || {};

const PREFIX = "flowchart-editor";
const canvasLeftMargin = 10; // % value
const canvasDefaultNodeHeight = 30;

export const CanvasService = (props) => {
  // console.log(props)
  const dispatch = useDispatch();
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
      )
    ) || {};
  const { dsPaneTypes, selectedQueryId, hcrDiagramNodesData = [], subDataSets = [] } = activeTab;
  const queriesMenu =
    dsPaneTypes
      ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
      ?.menu?.filter(
        (ele) =>
          ele.executeQueryData?.data.length ||
          ele.executeQueryData?.field.length
      ) || [];
  const selectedQuery = queriesMenu?.find((ele) => ele.id === selectedQueryId);
  const [shapeSearch, setShapeSearch] = useState("");
  const [modifiedFields, setModifiedFields] = useState([]);
  const [modifiedBuiltVariables, setModifiedBuiltVariables] = useState([]);

  const handleQueryChange = (queryId) => {
    if (queryId) {
      const advancedComponents = hcrDiagramNodesData.filter(({ category }) => ["advancedTable", "crosstab", "chart"].includes(category)) || [];
      const isPresentInSubDS = subDataSets.some(({ id }) => id === queryId)
      if (advancedComponents.length) {
        const isPresent = advancedComponents.some(({ selectedQueryID }) => selectedQueryID === queryId);
        if (isPresent) {
          notify(dispatch).warning({
            message: "This Dataset is currently used in one or more component dataset mappings. Please assign a separate dataset/subdataset for component-level data binding.",
            type: "Front End",
          });
          return
        }
      }
      if (isPresentInSubDS) {
        notify(dispatch).warning({
          message: "Sub dataset cannot be used as main dataset.",
          type: "Front End",
        });
        return
      }
    }
    dispatch(hcrActions.storeSelectedQueryId(queryId));
  };

  return (
    <TutorialInfo
      elementKey="hcr-datasourcePane-propertyPane"
      placement={"left"}
      className={"top-0"}
    >
      <div className="canvas-datasource-content">
        {/* <Card title="DataSource Pane"> */}
        <Select
          style={{ fontSize: 12, width: 110 }}
          placeholder="Select Query"
          value={selectedQueryId}
          dropdownClassName="canvas-parameter-select-12"
          allowClear={true}
          onSelect={handleQueryChange}
          onClear={() => handleQueryChange(null)}
        >
          {queriesMenu.map((query) => {
            return <Select.Option value={query.id}>{query.name}</Select.Option>;
          })}
        </Select>
        <Divider style={{ margin: "6px 0 3px 0" }} />
        <Input
          value={shapeSearch}
          onChange={(e) => {
            setShapeSearch(e.target.value);
          }}
          className="shape-input"
          placeholder={"Search fields / calculations / parameters"}
        />
        <Space direction="vertical" className="gap-1 hcr-ds-spc">
          {shapeSearch?.length > 0 && (
            <HCRShapeSearch
              shapeSearch={shapeSearch}
              modifiedFields={modifiedFields}
              modifiedBuiltVariables={modifiedBuiltVariables}
            />
          )}
          <HCRFields
            selectedQuery={selectedQuery}
            modifiedFields={modifiedFields}
            setModifiedFields={setModifiedFields}
          />
          <CalculationsParameters
            modifiedBuiltVariables={modifiedBuiltVariables}
            setModifiedBuiltVariables={setModifiedBuiltVariables}
          />
        </Space>
        {/* </Card> */}
      </div>
    </TutorialInfo>
  );
  // <p style={{ textAlign: 'center' }}>main canvas</p>;
};

const NodeComponent = (props) => {
  const { config, plugin = {} } = props;
  const { updateNode } = plugin;
  const dispatch = useDispatch();
  const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
  const { canvasProperties = {}, hcrDiagramNodesData = [] } = activeTab;
  const groupProperties = canvasProperties?.groupProperties || {};
  const { options: groupOptions = [] } = groupProperties || {};

  const [nodeConfig, setNodeConfig] = useState({
    ...config,
  });

  useEffect(() => {
    const reqNode = hcrDiagramNodesData?.find((node) => node.id === config.id);
    if (reqNode) {
      setNodeConfig((config) => {
        return {
          ...reqNode,
        };
      });
    }
  }, [hcrDiagramNodesData]);

  const onNodeConfigChange = (key, value, styles = null, otherKeyValuePairs = {}) => {
    let obj = {
      [key]: value,
      ...otherKeyValuePairs
    };

    if (key === "strikeThrough") {
      if (nodeConfig.underLine) {
        obj.underLine = false;
      }
    } else if (key === "underLine") {
      if (nodeConfig.strikeThrough) {
        obj.strikeThrough = false;
      }
    }

    if (["styleName"].includes(key) && styles) {
      obj = {
        ...obj,
        ...styles,
      };
    }

    setNodeConfig((prevConfig) => ({
      ...prevConfig,
      ...obj,
    }));

    updateNode(obj);

    let reqSelectedNodeIds =
      flowchartInstance?.current
        ?.getSelectedCells()
        ?.filter((selectedNode) => {
          if (selectedNode.store.data.data.category === config.category) {
            return selectedNode;
          }
          return null;
        })
        ?.filter(Boolean)
        ?.map((ele) => ele.id) || [];

    const allNodes =
      hcrDiagramNodesData.map((node) => {
        if (reqSelectedNodeIds.includes(node.id)) {
          return { ...node, ...obj };
        }
        return node;
      }) || [];

    dispatch(hcrActions.storeHcrDiagramNodesData(allNodes));
  };

  const propsList = {
    EditorPanels: EditorPanels,
    onNodeConfigChange: onNodeConfigChange,
    nodeConfig: nodeConfig,
    groupOptions
  }

  return (
    <div className={`${PREFIX}-panel-body`}>
      {
        {
          text: (
            <TextProperties {...propsList} />
          ),
          line: (
            <LineProperties {...propsList} />
          ),
          image: (
            <ImageProperties {...propsList} />
          ),
          pageBreak: (
            <PageBreakProperties {...propsList} />
          ),
          crosstab: (
            <CrosstabProperties {...propsList} />
          ),
          chart: (
            <ChartProperties {...propsList} />
          ),
          advancedTable: (
            <AdvancedTableProperties {...propsList} />
          ),
        }[config.category]
      }
    </div>
  );
};

const NodeService = (props) => {
  return (
    <FormWrapper {...props}>
      {(config, plugin) => (
        <NodeComponent {...props} plugin={plugin} config={config} />
      )}
    </FormWrapper>
  );
};

const formSchemaService = async (args) => {
  const { targetType } = args;
  const nodeSchema = {
    tabs: [
      {
        name: "Property Pane",
        groups: [
          {
            name: "groupName",
            controls: [
              {
                label: "Node name",
                name: "custom-node-service",
                shape: "custom-node-service",
                placeholder: "Node name",
              },
            ],
          },
        ],
      },
    ],
  };

  if (targetType === "node" || targetType === "group") {
    return nodeSchema;
  }
  return {
    tabs: [
      {
        name: "Datasource", // <TutorialInfo elementKey='hcr-datasourcePane'>
        groups: [
          {
            name: "datasourceGroup",
            controls: [
              {
                label: "Datasource",
                name: "custom-datasource-service",
                shape: "custom-datasource-service",
              },
            ],
          },
        ],
      },
      {
        name: "Property", // <TutorialInfo elementKey='hcr-propertyPane'>
        groups: [
          {
            name: "propertyGroup",
            controls: [
              {
                label: "",
                name: "custom-property-service",
                shape: "custom-property-service",
              },
            ],
          },
        ],
      },
      {
        name: "Report Parameter",
        groups: [
          {
            name: "ParameterGroup",
            controls: [
              {
                label: "",
                name: "custom-parameter-service",
                shape: "custom-parameter-service",
              },
            ],
          },
        ],
      },
    ],
  };
};

const CanvasParameter = () => {
  return <HCRCanvasParameters EditorPanels={EditorPanels} />;
};

const CanvasProperty = (props) => {
  const { config, plugin = {} } = props;
  // console.log(props)
  // const { updateNode } = plugin;
  // const [nodeConfig, setNodeConfig] = useState({
  //     ...config,
  // });
  return <HCRCanvasProperty EditorPanels={EditorPanels} />;
};

const controlMapService = (controlMap) => {
  controlMap.set("custom-node-service", NodeService);
  controlMap.set("custom-parameter-service", CanvasParameter);
  controlMap.set("custom-property-service", CanvasProperty);
  controlMap.set("custom-datasource-service", CanvasService);
  return controlMap;
};

const isToolbarPaneVisible = false;
let timeoutId = "";

function getItem(label, key, children, type) {
  const obj = {
    key,
    children,
    label,
    type,
  };
  if (!children) {
    obj.className = "hcr-context-item";
  } else {
    obj.popupClassName = "hcr-context-popup-menu";
  }
  return obj;
}

const calculations = [
  { label: "Nothing" },
  { label: "Count" },
  { label: "Sum" },
  { label: "Average" },
  { label: "Lowest" },
  { label: "Highest" },
  { label: "Standard Deviation", key: "StandardDeviation" },
  { label: "Variance" },
  { label: "System" },
  { label: "First" },
  { label: "Distinct Count", key: "DistinctCount" },
];

const HCRFlowchart = ({
  flowchartInstance,
  nodesPositions,
  sideBarPortion,
  repeatItems,
  lastSelectedNodeRef,
}) => {
  const activeTab =
    useSelector((state) =>
      state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey
      )
    ) || {};
  const {
    hcrDiagramNodesData = [],
    canvasProperties = {},
    canvasView = "canvas",
    canvasTabViews: { active = "canvas", views: tabViews = [] } = {},
    tableStyles = [],
  } = activeTab;

  const dispatch = useDispatch();
  const {
    margin: canvasMargin = {},
    layout = {},
    groupProperties,
  } = canvasProperties || {};
  const [copiedEles, setCopiedEles] = useState([]);
  const [showContext, setShowContext] = useState(true);

  useEffect(() => {
    const canvasEle = document?.getElementsByClassName(
      "xflow-canvas-container"
    )[0];
    const canvasImg = document?.getElementsByClassName(
      "xflow-x6-canvas"
    )[0];
    const reqWidth = `${layout?.size?.width}px`;
    const reqHeight = `${layout?.size?.height}px`;
    let sizeFactor = 40;
    if (!isEmpty(canvasMargin)) {
      let positions = ["top", "left", "right", "bottom"];
      sizeFactor = positions.map((position) => getMargin({ canvasMargin, key: position })).reduce((a, b) => a + b);
      sizeFactor = sizeFactor / 2
    }
    const imgReqWidth = `${layout?.size?.width - sizeFactor}px`;
    const imgReqHeight = `${layout?.size?.height - sizeFactor}px`;
    if (layout?.orientation === "Portrait") {
      canvasEle.style.width = reqWidth;
      canvasEle.style.height = reqHeight;
      if (canvasImg) {
        let tm = setTimeout(() => {
          clearTimeout(tm);
          canvasImg.style.width = imgReqWidth;
          canvasImg.style.height = imgReqHeight;
        }, 10)
      }
    } else {
      canvasEle.style.width = reqHeight;
      canvasEle.style.height = reqWidth;
      if (canvasImg) {
        let tm = setTimeout(() => {
          clearTimeout(tm);
          canvasImg.style.width = imgReqHeight;
          canvasImg.style.height = imgReqWidth;
        }, 10)
      }
    }
  }, [layout, canvasMargin]);

  const Notify = notify(dispatch);
  const [, drop] = useDrop(() => ({
    accept: "customNodes",
    drop: (item = {}, instance) => {
      const { record } = item;
      const dropState = instance.internalMonitor.store.getState();
      const { clientOffset, initialClientOffset, initialSourceClientOffset } =
        dropState.dragOffset;
      if (!clientOffset?.y) return;
      let yPosition =
        clientOffset.y -
        canvasDefaultNodeHeight / 2 /* but not record.height */ -
        42 /*navbar height + paddingTop*/ -
        getMargin({ canvasMargin: item?.canvasMargin || {}, key: "top" });
      if (yPosition < 0) {
        yPosition = 0;
      }
      const sidebarWidth = (window.screen.width * sideBarPortion) / 100;
      let xPosition =
        clientOffset.x -
        sidebarWidth -
        record.width / 2 -
        getMargin({ canvasMargin: item?.canvasMargin || {}, key: "left" }) -
        (window.screen.width - sidebarWidth) * (canvasLeftMargin / 100);
      if (xPosition < 0) {
        xPosition = 0;
      }
      let isNodePositionValid = false;
      if (layout?.orientation === "Portrait") {
        if (
          layout?.size?.width >= xPosition + record.width / 2 &&
          layout?.size?.height >= yPosition + record.height / 2
        ) {
          isNodePositionValid = true;
        }
      } else {
        if (
          layout?.size?.width >= yPosition + record.height / 2 &&
          layout?.size?.height >= xPosition + record.width / 2
        ) {
          isNodePositionValid = true;
        }
      }
      if (isNodePositionValid) {
        let width = 100, height = canvasDefaultNodeHeight;
        const nodeID = `node-${uuidv4()}`;
        const isAdvancedTable = record?.renderKey === "advancedTable";
        const isCrossTab = record?.renderKey === "crosstab";
        const isChart = record?.renderKey === "chart";
        if (isAdvancedTable || isCrossTab || isChart) {
          width = record.nodeWidth;
          height = record.nodeHeight;
        }

        let node = {
          ...record,
          id: nodeID,
          x: xPosition,
          y: yPosition,
          printRepeatedValues: true,
          height,
          fontSize: 10,
          width,
          fontFamily: "Serif",
        };
        if (isCrossTab) {
          node = {
            ...node,
            columnBreakOffset: 10,
            repeatColumnHeaders: true,
            repeatRowHeaders: true
          }
        }

        if (isAdvancedTable) {
          const tableDefaultStyles = getTableDefaultStyles(nodeID, tableStyles);
          node = {
            ...node,
            isAppliedClicked: true,
            tableConfig: hcrTableNodeInitialConfig,
            ...(getAdvancedTableConfig(1, node.width, hcrTableNodeInitialConfig, [], [], tableDefaultStyles) || {})
          }
          dispatch(hcrActions.hcrUpdateSubdataSets({ // add empty subdata set [8935]
            actionType: "addEmpty",
            id: nodeID,
          }))
          dispatch(hcrActions.hcrUpdateTableStyles({
            actionType: "addNewStyles",
            newStyles: tableDefaultStyles,
          }))
        }

        if (isChart) {
          node.chartType = "bar";
          node = { ...node, ...getHcrChartsDefaultProperties(node.chartType) };
        }
        if (node.category === "line") {
          delete node.printRepeatedValues;
        } else if (node.category === "text" && node.type === "defaultNodes") {
          node.label = '"Text"';
        }
        dispatch(hcrActions.hcrAddNode(node));
      }
    },
  }), [tableStyles]);

  const selectAllNodes = () => {
    const nodes = flowchartInstance?.current?.getNodes();
    let selectedNode;
    const reqNodes = nodes
      .filter((node) => {
        if (
          lastSelectedNodeRef.current &&
          lastSelectedNodeRef.current.id === node?.id
        ) {
          selectedNode = node;
          flowchartInstance?.current?.unselect(node);
          return null;
        } else {
          return node;
        }
      })
      ?.filter(Boolean);

    reqNodes.push(selectedNode);

    reqNodes.forEach((ele) => {
      flowchartInstance?.current?.select(ele);
    });
  };

  const updateChildren = (node, key, value, nodes) => {
    if (node.isGroup) {
      nodes.forEach((n) => {
        if (n?.parent === node.id) {
          n[key] += value;
        }
      })
    }
  }

  useEffect(() => {
    function handleCtrlKeys(e) {
      const activeElement = document.activeElement;
      const isInputField =
        activeElement.tagName === "INPUT" ||
        activeElement.tagName === "TEXTAREA" ||
        activeElement.isContentEditable;

      if (!isInputField && ([canvasView, active].includes("canvas"))) {
        if (e.code === "Delete") {

          // do not care about delete nodes, they are already deleted from flowchart's internal
          // state when press delete key, so checking selected nodes is returning with empty array,
          //  so just replace the nodes with available ones. 
          e.preventDefault();
          const selectedCells = flowchartInstance?.current
            ?.getSelectedCells()
            .map((shape) => {
              return getReqNodeData(shape.store.data.data);
            });
          if (selectedCells?.length) {
            dispatch(hcrActions.hcrDeleteNodes(selectedCells.map(cell => cell.id)));
          } else {
            let nodes = getFlowChartNodes({ flowchartInstance });
            dispatch(hcrActions.storeHcrDiagramNodesData(nodes));

            const nodeIds = nodes.map(({ id }) => id)
            const filteredViewTabIds = tabViews.filter((tab) => !nodeIds.includes(tab.id) && tab.id !== hcrCanvasViews.CANVAS).map(({ id }) => id)
            if (filteredViewTabIds.length) {
              dispatch(hcrActions.hcrUpdateCanvasView({ view: "removeMultipleTabs", tabIds: filteredViewTabIds }))
            }
            if (tableStyles.length) {
              const tableNodes = nodes.filter(node => node.category === "advancedTable").map(({ id }) => id);
              const filteredStyles = tableStyles.filter((style) => {
                if (!style.tableId) return true
                return tableNodes.includes(style.tableId);
              });
              dispatch(hcrActions.hcrUpdateTableStyles({
                actionType: "updateStyles",
                styles: filteredStyles,
              }))
            }
          }


          // let nodes = getFlowChartNodes({ flowchartInstance });
          // const selectedCells = flowchartInstance?.current
          //   ?.getSelectedCells()
          //   .map((shape) => {
          //     return getReqNodeData(shape.store.data.data);
          //   });
          // nodes = nodes.filter((node) => {
          //   return !selectedCells.find((cell) => cell.id === node.id);
          // });
          // flowchartInstance?.current?.getSelectedCells().forEach((cell) => {
          //   flowchartInstance?.current?.removeCell(cell);
          // });
          // dispatch(hcrActions.storeHcrDiagramNodesData(nodes));
        } else if (e.ctrlKey && e.key === "a") {
          e.preventDefault();
          selectAllNodes();
        } else if (e.ctrlKey && e.key === "v") {
          e.preventDefault();
          dispatch(
            hcrActions.storeHcrDiagramNodesData([
              ...getFlowChartNodes({ flowchartInstance, pastingEles: true }),
              ...copiedEles,
            ])
          );
        } else if ([37, 38, 39, 40].includes(e.keyCode)) {
          e.preventDefault();
          let nodes = getFlowChartNodes({ flowchartInstance });
          const selectedCells = flowchartInstance?.current
            ?.getSelectedCells()
            .map((shape) => {
              return getReqNodeData(shape.store.data.data);
            });
          const { width: canvasWidth, height: canvasHeight } =
            flowchartInstance?.current?.getArea();
          selectedCells.forEach((cell) => {
            nodes.find((node) => {
              if (node.id === cell.id) {
                if (e.keyCode === 39) {
                  if (node.x + node.width < canvasWidth) {
                    node.x += 1;
                    updateChildren(node, "x", 1, nodes);
                  }
                } else if (e.keyCode === 38) {
                  if (node.y > 0) {
                    node.y -= 1;
                    updateChildren(node, "y", -1, nodes);
                  }
                } else if (e.keyCode === 37) {
                  if (node.x > 0) {
                    node.x -= 1;
                    updateChildren(node, "x", -1, nodes);
                  }
                } else if (e.keyCode === 40) {
                  if (node.y + node.height < canvasHeight) {
                    node.y += 1;
                    updateChildren(node, "y", 1, nodes);
                  }
                }
                return true;
              }
              return false;
            });
          });
          dispatch(hcrActions.storeHcrDiagramNodesData(nodes));
        }
      }
    }
    window.addEventListener("keydown", handleCtrlKeys);
    return () => window.removeEventListener("keydown", handleCtrlKeys);
  }, [canvasView, active, tableStyles]);

  useEffect(() => {
    if (!hcrDiagramNodesData?.length) return;
    setTimeout(() => {
      hcrDiagramNodesData.forEach((node) => {
        const foreignObjectEle = document.querySelector(
          `g[data-cell-id="${node.id}"] foreignObject`
        );
        if (foreignObjectEle && node.__innerHTML) {
          foreignObjectEle.innerHTML = node.__innerHTML;
        }
      });
      addEleAfterNodes(hcrDiagramNodesData);
    });
  }, [hcrDiagramNodesData]);

  function addEleAfterNodes(nodes) {
    nodes?.forEach((node) => {
      if (node.isTableCell || node.isCrosstabCell) return;
      waitForForeignObject(node.id, (foreignObjectEle) => {
        const bodyEle = foreignObjectEle.querySelector("body");
        if (bodyEle) {
          const nodeAfterEle = bodyEle.querySelector(".node-after-ele");
          if (nodeAfterEle) {
            nodeAfterEle.textContent = node.repeat || "na";
          } else {
            const afterElement = document.createElement("div");
            afterElement.textContent = node.repeat || "na";
            afterElement.style.position = "absolute";
            afterElement.style.color = "mediumaquamarine";
            afterElement.style.top = "-8px";
            afterElement.style.right = "3px";
            afterElement.classList.add("node-after-ele");
            bodyEle.appendChild(afterElement);
          }
        }
      });
    });
  }

  useEffect(() => {
    if (flowchartInstance.current) {
      flowchartInstance.current.loadData({
        nodes: hcrDiagramNodesData,
        edges: [],
      });
    }
    if (hcrDiagramNodesData.length) {
      addEleAfterNodes(hcrDiagramNodesData);
    } else {
      flowchartInstance?.current?.getNodes()?.forEach((node) => {
        flowchartInstance?.current?.removeCell(node);
      });
    }
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId);
      }
    };
  }, [hcrDiagramNodesData]);

  useEffect(() => {
    return () => {
      dispatch(hcrActions.hcrUpdateCanvasView({ view: "updateActive", active: hcrCanvasViews.CANVAS }))
    }
  }, [])

  const alignItems = [
    getItem("Align", "align", [
      getItem("Fit To Width", "fitToWidth"),
      { type: "divider" },
      getItem("Stick To Top", "stickToTop"),
      getItem("Stick To Right", "stickToRight"),
      getItem("Stick To Left", "stickToLeft"),
      { type: "divider" },
      getItem("Left Align", "leftAlign"),
      getItem("Center Align", "centerAlign"),
      getItem("Right Align", "rightAlign"),
      { type: "divider" },
      getItem("Common Height", "commonHeight"),
      getItem("Common Width", "commonWidth"),
      getItem("Common Height Width", "commonHeightWidth"),
      { type: "divider" },
      getItem("Align With First Node", "alignWithFirstNode"),
    ]),
  ];

  const getDefaultMenuItems = (node) => {
    const createGroupItem = getItem("Create Group", "createGroup");
    // node = getFlowChartNodes({ flowchartInstance }).find(ele => ele.id === node.id);
    // if (node && (node.type === "defaultNodes" || node.type === "calculatedParams" || node.isGroup)) {
    //     createGroupItem.disabled = true;
    // }
    return [
      { ...createGroupItem, disabled: !["queryField"].includes(node?.type) },
      getItem("Delete", "delete"),
    ];
  };

  function getRepeatByItems(selectedRepeat) {
    const repeatItems = [
      getItem("Repeat", "repeat", [
        getItem("By Report", "rt"),
        getItem("By Page", "pg"),
        getItem("Stick To Record", "cl"),
        getItem("By Record", "rd"),
        getItem("No Data", "nd"),
        getItem("Last Page Footer", "lpf"),
      ]),
    ];
    // let groupNodes = getFlowChartNodes({ flowchartInstance })?.filter(
    //   (ele) => ele.isGrp
    // );
    let groupNodes = [];
    dispatch((_, getState) => {
      let canvasProperties =
        getState().cannedReports.present.hcrTabData.panes.find(
          (pane) => pane.key === getState().cannedReports.present.hcrTabData.activeKey
        )?.canvasProperties
      groupNodes = canvasProperties?.groupProperties?.options || [];
    });
    let isSelectedItemFound = false;
    if (groupNodes?.length) {
      // const grpItem = getItem(
      //   "By Group",
      //   "groupBy",
      //   (groupNodes || [])?.map((ele, i) => {
      //     return getItem(
      //       ele.groupName || `group_${ele.name}`,
      //       `gp${ele.grpId}`
      //     );
      //   })
      // );
      const grpItem = getItem(
        "By Group",
        "groupBy",
        (groupNodes)?.map((ele) => {
          return getItem(
            ele.name,
            `gp${ele.id}`
          );
        })
      );
      if (
        selectedRepeat &&
        !["na", "rd", "lpf", "nd"].includes(selectedRepeat)
      ) {
        grpItem.children.find((repeatTypeObj) => {
          if (selectedRepeat.includes(repeatTypeObj.key)) {
            isSelectedItemFound = true;
            repeatTypeObj.popupClassName = "hcr-context-popup-menu";
            repeatTypeObj.children = [
              getItem("Header", `header`),
              getItem("Footer", `footer`),
            ];
          }
        });
      }
      repeatItems[0].children = [...repeatItems[0].children, grpItem];
    }
    if (
      selectedRepeat &&
      !isSelectedItemFound &&
      !["na", "rd", "lpf", "nd"].includes(selectedRepeat)
    ) {
      repeatItems[0].children.find((repeatTypeObj) => {
        if (selectedRepeat.includes(repeatTypeObj.key)) {
          repeatTypeObj.children = [
            getItem(repeatTypeObj.key === "rt" ? "Title" : "Header", `header`),
            getItem(
              repeatTypeObj.key === "rt" ? "Summary" : "Footer",
              `footer`
            ),
          ];
          repeatTypeObj.popupClassName = "hcr-context-popup-menu";
        }
      });
    }
    return repeatItems;
  }

  function getCalculationsItems(dataType) {
    const calculationItems = [
      getItem(
        "Calculations",
        "calculations",
        dataType?.toLowerCase().includes("integer")
          ? calculations.map((ele) => getItem(ele.label, ele.key || ele.label))
          : [
            getItem("Count", "Count"),
            getItem("Distinct Count", "DistinctCount"),
          ]
      ),
    ];
    return calculationItems;
  }

  const flowchart = (
    <div
      ref={drop}
      className="flowchart-content"
      style={{ marginLeft: `${canvasLeftMargin}%` }}
      id="hcr-canvas-area"
      data-testid="hcr-canvas-area-container"
    >
      <Flowchart
        theme="light"
        toolbarPanelProps={{
          position: {
            top: 0,
            left: 0,
            right: 0,
          },
          show: isToolbarPaneVisible,
        }}
        scaleToolbarPanelProps={{
          layout: "horizontal",
          position: {
            right: isToolbarPaneVisible ? -40 : 0,
            top: 0,
          },
          show: false,
        }}
        onCopy={(copiedNodes) => {
          if (copiedNodes.length) {
            const reqNodes = copiedNodes.map((node) => {
              return { ...node, id: `node-${uuidv4()}` };
            });
            setCopiedEles(reqNodes);
          }
        }}
        canvasProps={{
          className: "hcr-canvas",
          position: {
            top: isToolbarPaneVisible
              ? 40
              : getMargin({ canvasMargin: canvasMargin || {}, key: "top" }),
            left: getMargin({
              canvasMargin: canvasMargin || {},
              key: "left",
            }),
            right: getMargin({
              canvasMargin: canvasMargin || {},
              key: "right",
            }),
            bottom: getMargin({
              canvasMargin: canvasMargin || {},
              key: "bottom",
            }),
          },
          config: getFlowChartConfig()
        }}
        nodePanelProps={{
          position: {
            width: 150,
            top: isToolbarPaneVisible ? 40 : 0,
            bottom: 0,
            left: 0,
          },
          defaultActiveKey: ["custom"], // ['custom', 'official']
          showOfficial: true,
          showHeader: false,
          show: false,
          registerNode:
          {
            title: "Elements",
            key: "elements",
            nodes: [
              {
                component: ImageNode,
                popover: () => <div>Image</div>,
                parentKey: "elements",
                name: "image",
                label: "Image",
                renderKey: "image",
                category: "image",
                type: "defaultNodes",
              },
              {
                component: LineNode,
                popover: () => <div>Line</div>,
                parentKey: "elements",
                name: "line",
                label: "Line",
                renderKey: "line",
                category: "line",
                type: "defaultNodes",
              },
              {
                component: PageBreakNode,
                popover: () => <div>Page Break</div>,
                parentKey: "elements",
                name: "pageBreak",
                label: "Page Break",
                renderKey: "pageBreak",
                category: "pageBreak",
                type: "defaultNodes",
              },
              {
                component: TextNode,
                popover: () => <div>Text</div>,
                name: "text",
                label: "Text",
                renderKey: "text",
                parentKey: "elements",
                category: "text",
                type: "defaultNodes",
              },
              {
                component: HCRCrossTabComponent,
                popover: () => <div>Cross Tab</div>,
                name: "crosstab",
                label: "Cross Tab",
                renderKey: "crosstab",
                parentKey: "elements",
                category: "crosstab",
                type: "defaultNodes",
              },
              {
                component: HCRChartsComponent,
                popover: () => <div>Charts</div>,
                name: "chart",
                label: "Chart",
                renderKey: "chart",
                parentKey: "elements",
                category: "chart",
                type: "defaultNodes",
              },
              {
                component: HCRAdvancedTableComponent,
                popover: () => <div>Advanced Table</div>,
                name: "advancedTable",
                label: "Advanced Table",
                renderKey: "advancedTable",
                parentKey: "elements",
                category: "advancedTable",
                type: "defaultNodes",
              }
            ],
          },
        }}
        contextMenuPanelProps={{
          showOfficial: false,
          show: showContext,
          submenu: (config) => {
            const selectedNodes = flowchartInstance?.current
              ?.getSelectedCells()
              .map((shape) => {
                return getReqNodeData(shape.store.data.data);
              });
            const { height: pageHeight, width: pageWidth } =
              flowchartInstance.current.getArea();
            if (config.menuType === "blank") {
              setShowContext(false);
              return [];
            } else {
              setShowContext(true);
            }
            return [
              {
                id: "string",
                label: "string",
                render: (data) => {
                  // console.log(data)
                  return (
                    <Menu
                      className="hcr-context"
                      popupClassName="hcr-context-popup"
                      selectedKeys={data.target.data.align}
                      onClick={(selectedItem) => {
                        // console.log('click', data.target.data);
                        // console.log(flowchartInstance.current.getSelectedCells())
                        if (data.target.data || selectedNodes.length) {
                          dispatch(
                            hcrActions.editNode({
                              nodeData: data.target.data
                                ? getReqNodeData(data.target.data)
                                : {},
                              menuKeyPath: selectedItem.keyPath,
                              pageHeight,
                              pageWidth,
                              type: "contextMenuAlign",
                              selectedNodes,
                            })
                          );
                        }
                      }}
                      style={{
                        // width: 156,
                        marginTop: 4,
                      }}
                      mode="vertical"
                      items={alignItems}
                    />
                  );
                },
              },
              {
                id: "string",
                label: "string",
                render: (data) => {
                  const disabled = data.target.data.isTableCell || data.target.data.isCrosstabCell;
                  return (
                    <Menu
                      selectedKeys={
                        data.target.data[data.target.data.repeat] ||
                        data.target.data.repeat
                      }
                      className="hcr-context"
                      onClick={(selectedItem) => {
                        if (data.target.data) {
                          dispatch(
                            hcrActions.editNode({
                              nodeData: getReqNodeData(data.target.data),
                              menuKeyPath: selectedItem.keyPath,
                              pageHeight,
                              pageWidth,
                              type: "contextMenuRepeat",
                              selectedNodes,
                            })
                          );
                        }
                      }}
                      style={
                        {
                          // width: 156,
                        }
                      }
                      mode="vertical"
                      items={getRepeatByItems(data.target.data.repeat)}
                      disabled={disabled}
                    />
                  );
                },
              },
              {
                id: "string",
                label: "string",
                render: (data) => {
                  return (
                    <Menu
                      className="hcr-context"
                      onClick={(selectedItem) => {
                        if (data.target.data) {
                          if (selectedItem.key === "delete") {
                            if (selectedNodes?.length) {
                              selectedNodes.forEach((selectedNode) => {
                                flowchartInstance.current.removeNode(selectedNode);
                              })
                              dispatch(
                                hcrActions.hcrDeleteNodes(selectedNodes.map((node) => node.id))
                              );
                            } else {
                              flowchartInstance.current.removeNode(
                                data.target.data
                              );
                              dispatch(
                                hcrActions.hcrDeleteNode({
                                  nodeId: getReqNodeData(data.target.data)?.id || ""
                                }
                                )
                              );
                            }
                          } else if (selectedItem.key === "createGroup") {
                            if (selectedNodes?.length) {
                              selectedNodes.forEach((selectedNode) => {
                                const reqNode = getReqNodeData(selectedNode);
                                dispatch(
                                  hcrActions.editNode({
                                    nodeData: reqNode,
                                    isCreateGroup: true,
                                  })
                                );
                              })
                            } else {
                              const reqNode = getReqNodeData(data.target.data);
                              dispatch(
                                hcrActions.editNode({
                                  nodeData: reqNode,
                                  isCreateGroup: true,
                                })
                              );
                            }
                          }
                        }
                      }}
                      style={
                        {
                          // width: 156,
                        }
                      }
                      mode="vertical"
                      items={getDefaultMenuItems(data.target.data)}
                    />
                  );
                },
              },
              {
                id: "string",
                label: "string",
                render: (data) => {
                  return (
                    <Menu
                      className="hcr-context"
                      onClick={(selectedItem) => {
                        if (data.target.data) {
                          if (selectedNodes?.length) {
                            selectedNodes.forEach((selectedNode) => {
                              dispatch(
                                hcrActions.addCalculation({
                                  node: selectedNode,
                                  calculation: selectedItem.key,
                                })
                              );
                            });
                          } else {
                            dispatch(
                              hcrActions.addCalculation({
                                node: data.target.data,
                                calculation: selectedItem.key,
                              })
                            );
                          }
                          // dispatch(hcrActions.editNode({ nodeData: getReqNodeData(data.target.data), menuKeyPath: selectedItem.keyPath, pageHeight, pageWidth, type: 'contextMenuRepeat', selectedNodes }));
                        }
                      }}
                      style={
                        {
                          // width: 156,
                        }
                      }
                      mode="vertical"
                      items={getCalculationsItems(
                        data.target.data?.backendDataType
                      )}
                      disabled={
                        ["queryField"].includes(data.target.data?.type)
                          ? false
                          : true
                      }
                    />
                  );
                },
              },
            ];
          },
        }}
        onAddNode={(node) => {
          dispatch(hcrActions.hcrAddNode(getReqNodeData(node)));
        }}
        detailPanelProps={{
          position: {
            width: 300,
            top: isToolbarPaneVisible ? 45 : 45,
            bottom: 0,
            right: 25,
          },
          controlMapService,
          formSchemaService,
          className:"hcr-flowchart-details-panel"
        }}
        onReady={(graph) => {
          flowchartInstance.current = graph;
          graph.loadData({
            nodes: [...(hcrDiagramNodesData || [])],
            edges: [],
          });
          graph.on("node:selected", ({ node }) => {
            lastSelectedNodeRef.current = node;
          });
          graph.on("node:change:position", ({ node }) => {
            const { x, y } = node?.store?.data?.position || {}
            nodesPositions.current[node?.id] = { x, y };
          });
          graph.on("node:moving", ({ node }) => {
            if (node?.parent) {
              onChildNodeMove(node)
            }
          });
          graph.on("node:dblclick", ({ node }) => {
            handleNodeSelection(node, dispatch);
          });

          addEleAfterNodes(hcrDiagramNodesData);
        }}
        onConfigChange={({ type, config }) => onHCRCanvasConfigChange({ type, config, dispatch, flowchartInstance, nodesPositions })}
      />
    </div>
  )

  const isTabView = canvasView === hcrCanvasViews.TAB;
  const isCanvasActive = active === hcrCanvasViews.CANVAS;

  const getActiveTabContent = (active, tabs) => {
    if (active === hcrCanvasViews.CANVAS) return null;
    const tab = tabs.find((tab) => tab.id === active);
    const node = hcrDiagramNodesData.find((node) => node.id === tab.id);
    return <HCRAdvancedTableEdit data={node} lastSelectedNodeRef={lastSelectedNodeRef} />
  }

  const flowchartWrapperStyles = { display: "block" }
  if (isTabView && !isCanvasActive) {
    flowchartWrapperStyles.display = "none"
  }

  return (
    <>
      <TutorialInfo elementKey="hcr-canvasArea" placement="bottomRight">
        <div style={{ width: "60%" }}></div>
      </TutorialInfo>
      <div className="hcr-canvas-tabs-container">
        {(isTabView) ?
          <>
            <HCRCanvasTabs
              tabs={tabViews.map((tab) => ({ ...tab, closable: tab.key !== hcrCanvasViews.CANVAS }))}
              active={active}
              onClick={(tab) => {
                dispatch(hcrActions.hcrUpdateCanvasView({ view: "updateActive", active: tab.id }))
              }}
              onClose={(tab) => {
                dispatch(hcrActions.hcrUpdateCanvasTabComponent({
                  actionType: "clearSelection",
                  id: tab.id,
                }))
                dispatch(hcrActions.hcrUpdateTableClipboard({
                  id: tab.id,
                  type: "delete",
                }))
                if (tabViews.length < 3) {
                  dispatch(hcrActions.hcrUpdateCanvasView({ view: "reset" }))
                  return;
                }
                dispatch(hcrActions.hcrUpdateCanvasView({ view: "removeTab", id: tab.id }))
              }}
            />
            <div>
              {getActiveTabContent(active, tabViews)}
            </div>
          </>
          : null}
      </div>
      <div style={flowchartWrapperStyles}>
        {flowchart}
      </div>
    </>
  );
};

export default function CanvasDiagram({
  contentTab,
  flowchartInstance,
  nodesPositions,
  sideBarPortion,
  lastSelectedNodeRef,
}) {
  return (
    <div className="flowchart-wrapper">
      <HCRFlowchart
        flowchartInstance={flowchartInstance}
        nodesPositions={nodesPositions}
        sideBarPortion={sideBarPortion}
        lastSelectedNodeRef={lastSelectedNodeRef}
      />
    </div>
  );
}

// tilte

//   page header

//     column header

//       group header

//         record

//       group footer

//     column footer

//   page footer

// summary

// lpf

// nodata
