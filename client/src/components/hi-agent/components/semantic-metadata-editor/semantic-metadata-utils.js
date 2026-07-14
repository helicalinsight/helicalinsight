import { Button, Drawer } from "antd";
import {
  buildClipboardPayload,
  buildMetadataClipboardBlock,
  copyToClipboard,
  extractMetadataReference,
  parseClipboardPayload,
  parsePayloadForSave,
  resolveMetadataRefForPaste,
  unwrapStatePayload,
} from "../../../common/json-editor";

export {
  copyToClipboard,
  extractMetadataReference,
  resolveMetadataRefForPaste,
  buildMetadataClipboardBlock,
};

export function uid() {
  return Math.random().toString(36).slice(2, 10);
}

const normalizeAgentLevel = (level = {}) => ({
  levelName: level.levelName || "",
  semanticType: level.semanticType || "",
  tableId: level.tableId == null ? "" : String(level.tableId),
  columnName: level.columnName || "",
  columnId: level.columnId == null ? "" : String(level.columnId),
  defaultFunction: level.defaultFunction || "",
  description: level.description || "",
  metric: {
    formula: level.metric?.formula || "",
  },
  ...(Number.isFinite(level.sortOrder) ? { sortOrder: level.sortOrder } : {}),
});

const normalizeAgentDimension = (dimension = {}) => {
  const normalized = {
    dimensionName: dimension.dimensionName || "",
    semanticType: dimension.semanticType || "",
    tableId: dimension.tableId == null ? "" : String(dimension.tableId),
    columnName: dimension.columnName || "",
    columnId: dimension.columnId == null ? "" : String(dimension.columnId),
    defaultFunction: dimension.defaultFunction || "",
    description: dimension.description || "",
    metric: {
      formula: dimension.metric?.formula || "",
    },
    ...(Number.isFinite(dimension.sortOrder)
      ? { sortOrder: dimension.sortOrder }
      : {}),
  };

  if (Array.isArray(dimension.hierarchies) && dimension.hierarchies.length) {
    normalized.hierarchies = dimension.hierarchies.map((hierarchy) => ({
      hierarchyName: hierarchy.hierarchyName || "",
      primaryColumnId:
        hierarchy.primaryColumnId == null
          ? ""
          : String(hierarchy.primaryColumnId),
      tableId: hierarchy.tableId == null ? "" : String(hierarchy.tableId),
      columnName: hierarchy.columnName || "",
      levels: Array.isArray(hierarchy.levels)
        ? hierarchy.levels.map(normalizeAgentLevel)
        : [],
    }));
  }

  return normalized;
};

export function ensureShape(d) {
  const next = d && typeof d === "object" ? d : {};
  const domain = Array.isArray(next.domain)
    ? next.domain.map((entry) => ({
        domain_name: entry?.domain_name || "",
        description: entry?.description || "",
        topics: Array.isArray(entry?.topics) ? entry.topics : [],
      }))
    : [];
  const cube_info = Array.isArray(next.cube_info)
    ? next.cube_info.map((cube) => ({
        cubeName: "",
        dimensions: Array.isArray(cube?.dimensions)
          ? cube.dimensions.map(normalizeAgentDimension)
          : [],
        measures: Array.isArray(cube?.measures) ? cube.measures : [],
      }))
    : [];

  return {
    domain,
    cube_info: cube_info.length
      ? cube_info
      : [{ cubeName: "", dimensions: [], measures: [] }],
  };
}

export function collectDomainTopics(data) {
  const topics = new Set();
  (data.domain || []).forEach((entry) => {
    (entry.topics || []).forEach((topic) => {
      if (topic) {
        topics.add(topic);
      }
    });
  });
  return topics;
}

export function allTopics(data) {
  const topics = new Set();
  (data.domain || []).forEach((entry) => {
    (entry.topics || []).forEach((topic) => {
      if (topic) {
        topics.add(topic);
      }
    });
  });
  return Array.from(topics).sort();
}

export function highlightJSON(text) {
  return text
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(
      /("(\\u[\da-fA-F]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
      (m) => {
        let cls = "tok-num";
        if (/^"/.test(m)) cls = /:$/.test(m) ? "tok-key" : "tok-str";
        else if (/true|false/.test(m)) cls = "tok-bool";
        else if (/null/.test(m)) cls = "tok-null";
        return `<span class="${cls}">${m}</span>`;
      },
    );
}

export const SECTIONS = [
  {
    key: "overview",
    label: "Overview",
    description:
      "Top-level information clients see first - domain, description, and high-level topics.",
  },
  {
    key: "json",
    label: "Raw JSON",
    description:
      "Live preview of the metadata document. Use the top bar to copy it.",
  },
];

export const SEMANTIC_TYPES = [
  "",
  "entity",
  "person",
  "organization",
  "category",
  "string",
  "text",
  "numeric",
  "datetime",
  "date",
  "location",
  "geography",
  "geography_latitude",
  "geography_longitude",
];

export const COLUMN_TYPES = ["", "temporal", "categorical", "numeric"];

export const parseAgentData = (agentData) => {
  if (!agentData) return ensureShape({});
  try {
    return ensureShape(
      typeof agentData === "string" ? JSON.parse(agentData) : agentData,
    );
  } catch {
    return ensureShape({});
  }
};

export const unwrapAgentStatePayload = unwrapStatePayload;

export const parsePastedAgentPayload = (rawText) => {
  const { state, metadataRef } = parseClipboardPayload(rawText, {
    normalizeState: (value) =>
      value && typeof value === "object" ? value : {},
  });

  return { agentState: state, metadataRef };
};

export const buildAgentClipboardPayload = (agentState, metadataDetails) =>
  buildClipboardPayload(agentState, metadataDetails, {
    normalizeState: (state) => ensureShape(parseAgentData(state)),
  });

export const parseAgentPayloadForSave = (rawText) =>
  parsePayloadForSave(rawText, { normalizeState: ensureShape });

export const AddItemDrawer = ({
  title,
  open,
  onClose,
  onSave,
  width = 600,
  children,
}) => {
  return (
    <Drawer
      title={<span className="hi-drawer-title">{title}</span>}
      placement="right"
      onClose={onClose}
      visible={open}
      width={width}
      footer={
        <div style={{ display: "flex", justifyContent: "flex-end" }}>
          <Button onClick={onClose} style={{ marginRight: 8 }}>
            Cancel
          </Button>
          <Button type="primary" onClick={onSave}>
            Save
          </Button>
        </div>
      }
      className="semantic-metadata-drawer"
    >
      <div className="semantic-metadata-editor">
        <div style={{ padding: "20px 0" }}>{children}</div>
      </div>
    </Drawer>
  );
};
