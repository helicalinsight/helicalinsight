import React, { useEffect, useState } from "react";
import { Row } from "antd";
import { PushpinFilled, PushpinOutlined } from "@ant-design/icons";
import { Responsive, WidthProvider } from "react-grid-layout";
import { useDispatch, useSelector } from "react-redux";
import { Cube } from "../../../../hi-cube/cube";
import { useWindowSize } from "../../../../../customHooks/useWindowSize";
import { updateAgentGridItemsLayout } from "../../../../../redux/actions/agent.actions";
import { AgentMetadataShelf } from "../../agent-metadata-shelf";
import { CubeShelf } from "./cube-shelf";
import {
  ToolShelf,
  TOOL_BUSINESS_VIEW,
  TOOL_AGENT_JSON,
} from "./tool-shelf";
import { AgentJsonPanel } from "./agent-json-panel";
import "react-grid-layout/css/styles.css";
import "react-resizable/css/styles.css";
import "./agent-shelves.scss";

const ResponsiveGridLayout = WidthProvider(Responsive);
const BPS = ["lg", "md", "sm", "xs", "xxs"];

const DEFAULT_SHELF_LAYOUT = {
  metadataShelf: true,
  fieldsShelf: true,
  toolsShelf: true,
};

const mapLayouts = (layouts, fn) =>
  Object.fromEntries(BPS.map((bp) => [bp, layouts[bp].map(fn)]));

function buildShelfLayouts(calculatedH, { showMetadata, showFields }) {
  const bp = (sidebar, fields, editing) => [
    { i: "sidebar-area", isDraggable: false, isResizable: false, ...sidebar },
    { i: "metadata-area", isDraggable: false, isResizable: false, ...fields },
    { i: "editing-area", isDraggable: false, isResizable: false, ...editing },
  ];
  const hidden = { w: 0, h: 0, x: 0, y: 0 };
  const fullH = (w, x = 0, y = 0) => ({ w, h: calculatedH, x, y });

  if (showMetadata && showFields) {
    return {
      xxs: bp(fullH(100, 0, 0), fullH(100, 0, calculatedH), fullH(100, 0, 2 * calculatedH)),
      xs: bp(fullH(50, 0, 0), fullH(50, 50, 0), fullH(100, 0, calculatedH)),
      sm: bp(fullH(50, 0, 0), fullH(50, 50, 0), fullH(100, 0, calculatedH)),
      md: bp(fullH(17, 0, 0), fullH(20, 17, 0), fullH(63, 37, 0)),
      lg: bp(fullH(17, 0, 0), fullH(20, 17, 0), fullH(63, 37, 0)),
    };
  }
  if (showMetadata && !showFields) {
    return {
      xxs: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      xs: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      sm: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      md: bp(fullH(30, 0, 0), hidden, fullH(70, 30, 0)),
      lg: bp(fullH(30, 0, 0), hidden, fullH(70, 30, 0)),
    };
  }
  if (!showMetadata && showFields) {
    return {
      xxs: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      xs: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      sm: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      md: bp(hidden, fullH(30, 0, 0), fullH(70, 30, 0)),
      lg: bp(hidden, fullH(30, 0, 0), fullH(70, 30, 0)),
    };
  }
  return {
    xxs: bp(hidden, hidden, fullH(100, 0, 0)),
    xs: bp(hidden, hidden, fullH(100, 0, 0)),
    sm: bp(hidden, hidden, fullH(100, 0, 0)),
    md: bp(hidden, hidden, fullH(100, 0, 0)),
    lg: bp(hidden, hidden, fullH(100, 0, 0)),
  };
}

const PinButton = ({ className, pinned, onClick }) => (
  <div className={className} onClick={onClick}>
    {pinned ? <PushpinFilled /> : <PushpinOutlined />}
  </div>
);

export function AgentWorkspace({
  shelfLayout = DEFAULT_SHELF_LAYOUT,
  metadataShelfProps = {},
  jsonText = "",
  onJsonChange,
  onSaveJson,
  onCopyJson,
  hasUnsavedJsonChanges = false,
}) {
  const dispatch = useDispatch();
  const layouts = useSelector((state) => state.agent.gridItemsLayout);
  const metadataDetails = useSelector(
    (state) => state.agent.metadataDetails || {},
  );
  const isMetadataLoaded = Boolean(
    metadataDetails.path && metadataDetails.fileName,
  );
  const [activeTool, setActiveTool] = useState(TOOL_BUSINESS_VIEW);
  const [, offsetHeight] = useWindowSize();
  const [savedLayout, setSavedLayout] = useState(null);
  const [resizeByArea, setResizeByArea] = useState({});

  const showMetadata = Boolean(shelfLayout.metadataShelf ?? true);
  const showFields = Boolean(shelfLayout.fieldsShelf ?? true);
  const toolsShelf = Boolean(shelfLayout.toolsShelf ?? true);
  const layoutKey = `${showMetadata ? 1 : 0}-${showFields ? 1 : 0}`;
  const calculatedH = offsetHeight / 12 || 52;

  useEffect(() => {
    if (!isMetadataLoaded && activeTool === TOOL_AGENT_JSON) {
      setActiveTool(TOOL_BUSINESS_VIEW);
    }
  }, [isMetadataLoaded, activeTool]);

  // Fallback until Redux is initialized (same default as before)
  const activeLayouts =
    layouts ||
    buildShelfLayouts(52, { showMetadata: true, showFields: true });

  useEffect(() => {
    dispatch(
      updateAgentGridItemsLayout(
        buildShelfLayouts(calculatedH, { showMetadata, showFields }),
      ),
    );
    setSavedLayout(null);
    setResizeByArea({});
  }, [layoutKey]);

  useEffect(() => {
    if (!savedLayout?.prev) return;
    savedLayout.current.forEach((ele) => {
      const prevRow = savedLayout.prev.find((j) => j.i === ele.i);
      if (JSON.stringify(ele) !== JSON.stringify(prevRow) && !resizeByArea[ele.i]) {
        setResizeByArea((prev) => ({ ...prev, [ele.i]: true }));
      }
    });
  }, [savedLayout]);

  const handlePin = (key) => {
    if (!layouts) return;
    dispatch(
      updateAgentGridItemsLayout(
        mapLayouts(layouts, (item) =>
          item.i === key ? { ...item, isResizable: !item.isResizable } : item,
        ),
      ),
    );
  };

  const isPinned = (key) =>
    !activeLayouts.lg.find((item) => item.i === key)?.isResizable;

  return (
    <Row className="height100percent agent-workspace-row">
      <ResponsiveGridLayout
        key={layoutKey}
        cols={{ lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 }}
        className="metadata-layout agent-layout layout"
        rowHeight={1}
        isDraggable={false}
        isResizable
        measureBeforeMount
        breakpoints={{ lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 }}
        compactType="vertical"
        margin={[0, 10]}
        layouts={activeLayouts}
        onLayoutChange={(layout) => {
          if (!savedLayout) setSavedLayout({ prev: undefined, current: layout });
        }}
        onResizeStop={(layout) => {
          if (!layouts) return;
          dispatch(
            updateAgentGridItemsLayout(
              mapLayouts(layouts, (item) => {
                const cur = layout.find((u) => u.i === item.i);
                return cur
                  ? { ...item, x: cur.x, y: cur.y, h: cur.h, w: cur.w }
                  : item;
              }),
            ),
          );
          if (savedLayout?.current) {
            setSavedLayout({ prev: savedLayout.current, current: layout });
          }
        }}
      >
        <div
          key="sidebar-area"
          className={`${
            showMetadata
              ? "b1pxddd metadata-sidebar agent-workspace-metadata-shelf"
              : "display-none"
          } ${resizeByArea["sidebar-area"] ? "" : "grid-height-99"}`}
        >
          {showMetadata && (
            <>
              <AgentMetadataShelf {...metadataShelfProps} />
              <PinButton
                className="hr-resize-pin"
                pinned={isPinned("sidebar-area")}
                onClick={() => handlePin("sidebar-area")}
              />
            </>
          )}
        </div>

        <div
          key="metadata-area"
          className={`b1pxsr metadata-section agent-workspace-cube-shelf ${
            showFields ? "" : "display-none"
          } ${resizeByArea["metadata-area"] ? "" : "grid-height-99"}`}
        >
          {showFields && (
            <>
              <CubeShelf showBusinessFields={false} />
              <PinButton
                className="hr-resize-pin"
                pinned={isPinned("metadata-area")}
                onClick={() => handlePin("metadata-area")}
              />
            </>
          )}
        </div>

        <div
          key="editing-area"
          className={`b1pxsr metadata-editor-section metadata-editor agent-workspace-right-area ${
            resizeByArea["editing-area"] ? "" : "grid-height-99"
          }`}
        >
          <div className="agent-workspace-right-inner">
            <div
              className={`agent-workspace-tool-shelf b1pxsr${
                toolsShelf ? "" : " display-none"
              }`}
            >
              {toolsShelf && (
                <ToolShelf
                  activeTool={activeTool}
                  onSelect={setActiveTool}
                  disabledTools={{
                    [TOOL_BUSINESS_VIEW]: !isMetadataLoaded,
                    [TOOL_AGENT_JSON]: !isMetadataLoaded,
                  }}
                  disabledToolTips={{
                    [TOOL_BUSINESS_VIEW]:
                      "Connect a metadata file to enable Business View",
                    [TOOL_AGENT_JSON]:
                      "Connect a metadata file before opening JSON",
                  }}
                />
              )}
            </div>
            <div className="agent-workspace-main">
              {activeTool === TOOL_AGENT_JSON ? (
                <AgentJsonPanel
                  value={jsonText}
                  onChange={onJsonChange}
                  onSave={onSaveJson}
                  onCopy={onCopyJson}
                  hasUnsavedChanges={hasUnsavedJsonChanges}
                />
              ) : (
                <div className="agent-workspace-business-view">
                  <Cube showBusinessFields />
                </div>
              )}
            </div>
          </div>
          <PinButton
            className="hr-resize-pin-editing"
            pinned={isPinned("editing-area")}
            onClick={() => handlePin("editing-area")}
          />
        </div>
      </ResponsiveGridLayout>
    </Row>
  );
}

export default AgentWorkspace;
