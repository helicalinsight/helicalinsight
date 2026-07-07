import { DashboardOutlined } from "@ant-design/icons";
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

export function ensureShape(d) {
  const next = d && typeof d === "object" ? { ...d } : {};
  next.business_metrics = Array.isArray(next.business_metrics)
    ? next.business_metrics
    : [];
  next.synonyms = Array.isArray(next.synonyms) ? next.synonyms : [];
  next.examples = Array.isArray(next.examples) ? next.examples : [];
  next.metadata_info =
    next.metadata_info && typeof next.metadata_info === "object"
      ? next.metadata_info
      : { metadata: {} };
  next.metadata_info.metadata = next.metadata_info.metadata || {};
  next.metadata_info.metadata.domain = Array.isArray(
    next.metadata_info.metadata.domain,
  )
    ? next.metadata_info.metadata.domain
    : [];
  next.metadata_info.metadata.description =
    next.metadata_info.metadata.description || "";
  next.domain = Array.isArray(next.domain) ? next.domain : [];
  next.topic_mappings = Array.isArray(next.topic_mappings)
    ? next.topic_mappings
    : [];
  next.cube_metadata = Array.isArray(next.cube_metadata)
    ? next.cube_metadata
    : [];
  return syncTopicMappings(next);
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

export function buildTopicMappingsFromCubeMetadata(data) {
  const domainTopics = collectDomainTopics(data);
  if (!domainTopics.size) {
    return [];
  }

  const byTopicName = new Map();

  (data.cube_metadata || []).forEach((table) => {
    const topicName = Array.isArray(table.dimension_name)
      ? String(table.dimension_name[0] ?? "").trim()
      : String(table.dimension_name ?? "").trim();
    if (!topicName || !domainTopics.has(topicName)) {
      return;
    }

    const previous = byTopicName.get(topicName);
    const component = [
      ...new Set([...(previous?.component || []), topicName]),
    ];
    byTopicName.set(topicName, { topic_name: topicName, component });
  });

  return Array.from(byTopicName.values());
}

function collectDimensionNames(data) {
  const names = new Set();
  (data.cube_metadata || []).forEach((table) => {
    const topicName = Array.isArray(table.dimension_name)
      ? String(table.dimension_name[0] ?? "").trim()
      : String(table.dimension_name ?? "").trim();
    if (topicName) {
      names.add(topicName);
    }
  });
  return names;
}

function isAutoManagedTopicMapping(mapping, domainTopics, dimensionNames) {
  if (!mapping?.topic_name) {
    return false;
  }
  if (domainTopics.has(mapping.topic_name)) {
    return true;
  }
  return (mapping.component || []).some(
    (component) =>
      domainTopics.has(component) || dimensionNames.has(component),
  );
}

export function syncTopicMappings(data) {
  const autoMappings = buildTopicMappingsFromCubeMetadata(data);

  if (autoMappings.length) {
    return { ...data, topic_mappings: autoMappings };
  }

  const domainTopics = collectDomainTopics(data);
  const dimensionNames = collectDimensionNames(data);
  const topic_mappings = (data.topic_mappings || []).filter(
    (mapping) =>
      !isAutoManagedTopicMapping(mapping, domainTopics, dimensionNames),
  );

  return { ...data, topic_mappings };
}

export function allTables(data) {
  const s = new Set();
  data.cube_metadata.forEach((t) => t.database_table && s.add(t.database_table));
  data.synonyms.forEach((t) => t.database_table && s.add(t.database_table));
  data.examples.forEach((t) => t.database_table && s.add(t.database_table));
  data.business_metrics.forEach((m) =>
    (m.tables || []).forEach((t) => s.add(t)),
  );
  return Array.from(s).sort();
}

export function allTopics(data) {
  const s = new Set();
  data.domain.forEach((d) => (d.topics || []).forEach((t) => s.add(t)));
  data.topic_mappings.forEach((m) => m.topic_name && s.add(m.topic_name));
  return Array.from(s).sort();
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
    // icon: <DashboardOutlined/>,
    description:
      "Top-level information clients see first - domain, description, and high-level topics.",
  },
  {
    key: "metrics",
    label: "Business Metrics",
    countKey: "metrics",
    description:
      "Business KPIs, formulas, filters, and source tables.",
  },
  {
    key: "tables",
    label: "Tables & Columns",
    countKey: "tables",
    description:
      "Each table maps a database object to a friendly business dimension with column-level descriptions.",
  },
  {
    key: "synonyms",
    label: "Synonyms",
    countKey: "synonyms",
    description:
      "Words a user might type that the agent should resolve to the underlying database table.",
  },
  {
    key: "examples",
    label: "Examples",
    countKey: "examples",
    description:
      "Mapping hints (left to right) the agent uses for ambiguous business terms.",
  },
  {
    key: "topics",
    label: "Topics & Mappings",
    countKey: "topics",
    description:
      "Logical groupings the agent uses to choose which tables to look at first.",
  },
  {
    key: "json",
    label: "Raw JSON",
    // icon: "{ }",
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
    return ensureShape(typeof agentData === "string" ? JSON.parse(agentData) : agentData);
  } catch {
    return ensureShape({});
  }
};

export const unwrapAgentStatePayload = unwrapStatePayload;

export const parsePastedAgentPayload = (rawText) => {
  const { state, metadataRef } = parseClipboardPayload(rawText, {
    normalizeState: ensureShape,
  });

  return { agentState: state, metadataRef };
};

export const buildAgentClipboardPayload = (agentState, metadataDetails) =>
  buildClipboardPayload(agentState, metadataDetails, {
    normalizeState: (state) => ensureShape(parseAgentData(state)),
  });

export const parseAgentPayloadForSave = (rawText) =>
  parsePayloadForSave(rawText, { normalizeState: ensureShape });

//drawer
export const  AddItemDrawer =({ title, open, onClose, onSave, width = 600, children }) =>{
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
}