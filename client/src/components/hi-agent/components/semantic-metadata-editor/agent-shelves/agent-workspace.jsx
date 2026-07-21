import React, { useEffect, useState } from "react";
import { Row } from "antd";
import { Responsive, WidthProvider } from "react-grid-layout";
import { Cube } from "../../../../hi-cube/cube";
import { useWindowSize } from "../../../../../customHooks/useWindowSize";
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

const DEFAULT_SHELF_LAYOUT = {
  metadataShelf: true,
  fieldsShelf: true,
  toolsShelf: true,
};

function buildShelfLayouts(calculatedH, { showMetadata, showFields }) {
  const bp = (sidebar, fields, editing) => [
    { i: "sidebar-area", ...sidebar },
    { i: "metadata-area", ...fields },
    { i: "editing-area", ...editing },
  ];

  const hidden = { w: 0, h: 0, x: 0, y: 0 };
  const fullH = (w, x = 0, y = 0) => ({ w, h: calculatedH, x, y });

  // All shelves visible — same proportions as Metadata (17 / 20 / 63).
  if (showMetadata && showFields) {
    return {
      xxs: bp(fullH(100, 0, 0), fullH(100, 0, calculatedH), fullH(100, 0, 2 * calculatedH)),
      xs: bp(fullH(50, 0, 0), fullH(50, 50, 0), fullH(100, 0, calculatedH)),
      sm: bp(fullH(50, 0, 0), fullH(50, 50, 0), fullH(100, 0, calculatedH)),
      md: bp(fullH(17, 0, 0), fullH(20, 17, 0), fullH(63, 37, 0)),
      lg: bp(fullH(17, 0, 0), fullH(20, 17, 0), fullH(63, 37, 0)),
    };
  }

  // Metadata only — main expands to remaining width (like HReport).
  if (showMetadata && !showFields) {
    return {
      xxs: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      xs: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      sm: bp(fullH(100, 0, 0), hidden, fullH(100, 0, calculatedH)),
      md: bp(fullH(30, 0, 0), hidden, fullH(70, 30, 0)),
      lg: bp(fullH(30, 0, 0), hidden, fullH(70, 30, 0)),
    };
  }

  // Fields only — main expands to remaining width.
  if (!showMetadata && showFields) {
    return {
      xxs: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      xs: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      sm: bp(hidden, fullH(100, 0, 0), fullH(100, 0, calculatedH)),
      md: bp(hidden, fullH(30, 0, 0), fullH(70, 30, 0)),
      lg: bp(hidden, fullH(30, 0, 0), fullH(70, 30, 0)),
    };
  }

  // All left shelves off — main pane occupies full width (HReport-style).
  return {
    xxs: bp(hidden, hidden, fullH(100, 0, 0)),
    xs: bp(hidden, hidden, fullH(100, 0, 0)),
    sm: bp(hidden, hidden, fullH(100, 0, 0)),
    md: bp(hidden, hidden, fullH(100, 0, 0)),
    lg: bp(hidden, hidden, fullH(100, 0, 0)),
  };
}
export function AgentWorkspace({
  shelfLayout = DEFAULT_SHELF_LAYOUT,
  metadataShelfProps = {},
  onCopyJson,
  onPasteJson,
}) {
  const [activeTool, setActiveTool] = useState(TOOL_BUSINESS_VIEW);
  const [, offsetHeight] = useWindowSize();
  const [savedLayout, setSavedLayout] = useState(null);
  const [resizeByArea, setResizeByArea] = useState({
    "sidebar-area": false,
    "metadata-area": false,
    "editing-area": false,
  });

  const {
    metadataShelf = true,
    fieldsShelf = true,
    toolsShelf = true,
  } = shelfLayout;

  const showMetadata = Boolean(metadataShelf);
  const showFields = Boolean(fieldsShelf);
  const layoutKey = `${showMetadata ? 1 : 0}-${showFields ? 1 : 0}`;

  useEffect(() => {
    setSavedLayout(null);
    setResizeByArea({
      "sidebar-area": false,
      "metadata-area": false,
      "editing-area": false,
    });
  }, [layoutKey]);

  useEffect(() => {
    savedLayout &&
      savedLayout.prev &&
      savedLayout.current.forEach((ele) => {
        const prevRow = savedLayout.prev.find((j) => j.i === ele.i);
        if (JSON.stringify(ele) !== JSON.stringify(prevRow)) {
          const obj = { ...resizeByArea };
          if (!obj[ele.i]) {
            obj[ele.i] = true;
            setResizeByArea(obj);
          }
        }
      });
  }, [savedLayout]);

  let calculatedH = 0;
  try {
    calculatedH = offsetHeight / 12 || 52;
  } catch (e) {
    calculatedH = 52;
  }

  const storedLayouts = buildShelfLayouts(calculatedH, {
    showMetadata,
    showFields,
  });

  const layoutProps = {
    cols: { lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 },
    className: "layout",
    rowHeight: 1,
    colWidth: 1,
    isDraggable: false,
    isResizable: true,
    preventCollision: false,
    measureBeforeMount: true,
    breakpoints: { lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 },
    compactType: "vertical",
    margin: [0, 10],
  };

  return (
    <Row className="height100percent agent-workspace-row">
      <ResponsiveGridLayout
        key={layoutKey}
        {...layoutProps}
        layouts={storedLayouts}
        onLayoutChange={(layout) => {
          if (!savedLayout) {
            setSavedLayout({ prev: undefined, current: layout });
          }
        }}
        onResizeStop={(layout) => {
          if (savedLayout && savedLayout.current) {
            setSavedLayout({ prev: savedLayout.current, current: layout });
          }
        }}
        onBreakpointChange={() => {}}
        className="metadata-layout agent-layout"
      >
        <div
          key="sidebar-area"
          className={`${
            showMetadata
              ? "b1pxddd metadata-sidebar agent-workspace-metadata-shelf"
              : "display-none"
          } ${resizeByArea["sidebar-area"] ? "" : "grid-height-99"}`}
        >
          {showMetadata ? (
            <AgentMetadataShelf {...metadataShelfProps} />
          ) : null}
        </div>

        <div
          key="metadata-area"
          className={`b1pxsr metadata-section agent-workspace-cube-shelf ${
            showFields ? "" : "display-none"
          } ${resizeByArea["metadata-area"] ? "" : "grid-height-99"}`}
        >
          {showFields ? <CubeShelf showBusinessFields={false} /> : null}
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
              {toolsShelf ? (
                <ToolShelf activeTool={activeTool} onSelect={setActiveTool} />
              ) : null}
            </div>
            <div className="agent-workspace-main">
              {activeTool === TOOL_AGENT_JSON ? (
                <AgentJsonPanel onCopy={onCopyJson} onPaste={onPasteJson} />
              ) : (
                <div className="agent-workspace-business-view">
                  <Cube showBusinessFields />
                </div>
              )}
            </div>
          </div>
        </div>
      </ResponsiveGridLayout>
    </Row>
  );
}

export default AgentWorkspace;
