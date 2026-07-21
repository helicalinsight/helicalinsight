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

const normalizeDomainTopic = (topicEntry) => {
  if (typeof topicEntry === "string") {
    const topic = topicEntry.trim();
    return topic ? { topic, description: "" } : null;
  }
  if (!topicEntry || typeof topicEntry !== "object") {
    return null;
  }
  if (Array.isArray(topicEntry.topics)) {
    return topicEntry.topics
      .filter(Boolean)
      .map((topic) => ({
        topic: String(topic).trim(),
        description: topicEntry.description || "",
      }));
  }
  const topic = String(topicEntry.topic || topicEntry.name || "").trim();
  const description = topicEntry.description || "";
  const components = Array.isArray(topicEntry.components)
    ? topicEntry.components
        .map((component) => ({
          id:
            component?.id == null || component?.id === ""
              ? ""
              : String(component.id),
          name: String(component?.name || "").trim(),
        }))
        .filter((component) => component.id || component.name)
    : [];
  if (!topic && !description.trim() && !components.length) {
    return null;
  }
  return {
    topic,
    description,
    ...(components.length ? { components } : {}),
  };
};

const normalizeDomainTopics = (topics = []) =>
  (Array.isArray(topics) ? topics : [])
    .flatMap((entry) => {
      const normalized = normalizeDomainTopic(entry);
      if (!normalized) {
        return [];
      }
      return Array.isArray(normalized) ? normalized : [normalized];
    })
    .filter((entry) => entry.topic || entry.description || entry.components?.length);

export function ensureShape(d) {
  const next = d && typeof d === "object" ? d : {};
  const legacyTopics = Array.isArray(next.topics) ? next.topics : [];
  const domain = Array.isArray(next.domain)
    ? next.domain.map((entry, index) => {
        let topics = normalizeDomainTopics(entry?.topics);
        // Migrate legacy parallel top-level topics array by index.
        if (!topics.length && legacyTopics[index]) {
          topics = normalizeDomainTopics([legacyTopics[index]]);
        }
        return {
          domain_name: entry?.domain_name || "",
          description: entry?.description || "",
          topics,
        };
      })
    : [];
  const cubeSource = Array.isArray(next.cube)
    ? next.cube
    : Array.isArray(next.cube_info)
      ? next.cube_info
      : [];
  const cube = cubeSource.map((entry) => ({
        cubeName: "",
        dimensions: Array.isArray(entry?.dimensions) ? entry.dimensions : [],
        measures: Array.isArray(entry?.measures) ? entry.measures : [],
      }));

  return {
    domain,
    cube: cube.length
      ? cube
      : [{ cubeName: "", dimensions: [], measures: [] }],
  };
}

export function collectDomainTopics(data) {
  const topics = new Set();
  (data.domain || []).forEach((entry) => {
    (entry.topics || []).forEach((topicEntry) => {
      const topic =
        typeof topicEntry === "string"
          ? topicEntry
          : topicEntry?.topic || topicEntry?.name || "";
      if (topic) {
        topics.add(topic);
      }
    });
  });
  return topics;
}

export function allTopics(data) {
  return Array.from(collectDomainTopics(data)).sort();
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
