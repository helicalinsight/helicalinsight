import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { AutoComplete, Dropdown, Input, Menu, Modal, Tag, Tooltip, Typography } from "antd";
import {
  AimOutlined,
  AppstoreOutlined,
  BranchesOutlined,
  CalculatorOutlined,
  CaretDownOutlined,
  CaretRightOutlined,
  EditOutlined,
  ExperimentOutlined,
  InfoCircleOutlined,
  MinusOutlined,
  PlusOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import {
  AgentTopicDropZone,
  AnalyticalOperationDragSource,
} from "../../../../hi-cube/agentBusinessViewDnd";
import {
  AgentAiContextTree,
  AgentRightSubmenuTextField,
} from "../../../../hi-cube/cubeConstants";
import instantBI, {
  uriConfig,
} from "../../../../../base/requests/instantbi.requests";
import "./diagnostic-capabilities-panel.scss";

const { Paragraph } = Typography;

/** Resolve a display name from a dragged cube field or metadata column. */
const getDroppedName = (item) =>
  String(
    item?.fields ||
      item?.alias ||
      item?.columnName ||
      item?.name ||
      item?.text ||
      "",
  ).trim();

const uniqueByLower = (values = []) => {
  const seen = new Set();
  return values.filter((value) => {
    const name = String(value || "").trim();
    const key = name.toLowerCase();
    if (!name || seen.has(key)) return false;
    seen.add(key);
    return true;
  });
};

/** Names the user can drop in from the metadata shelf or the model fields. */
const collectInputSuggestions = (fields = [], metadataTablesData = {}) => {
  const byName = new Map();
  const add = (name) => {
    const text = String(name || "").trim();
    const key = text.toLowerCase();
    if (!text || byName.has(key)) return;
    byName.set(key, text);
  };
  const walkFields = (nodes) => {
    (nodes || []).forEach((field) => {
      if (!field || typeof field !== "object") return;
      if (!field.isDelete) {
        add(field.fields || field.columnName);
      }
      if (Array.isArray(field.children)) walkFields(field.children);
    });
  };
  walkFields(fields);
  Object.values(metadataTablesData?.tables || {}).forEach((table) => {
    Object.values(table?.columns || {}).forEach((column) => {
      add(column?.alias || column?.name || column?.columnName);
    });
  });
  return Array.from(byName.values());
};

const measureNameFromEntry = (entry) => {
  if (typeof entry === "string") return entry.trim();
  if (entry && typeof entry === "object") {
    return String(
      entry.measure || entry.name || entry.fields || "",
    ).trim();
  }
  return "";
};

const INPUT_NAME_ALIASES = {
  dimension: "dimensions",
  additionaldimensions: "dimensions",
};

const canonicalInputName = (key) => {
  const text = String(key || "").trim();
  if (!text) return "";
  return INPUT_NAME_ALIASES[text.toLowerCase()] || text;
};

const emptyInputMap = (keys = []) =>
  Object.fromEntries(
    (keys || []).map((key) => [canonicalInputName(key), []]).filter(([key]) => key),
  );

/** Catalog slot names, including older required/optional lists. */
const catalogInputKeys = (catalog = {}) => {
  const listed = [
    ...(Array.isArray(catalog.inputs) ? catalog.inputs : []),
    ...(Array.isArray(catalog.requiredInputs) ? catalog.requiredInputs : []),
    ...(Array.isArray(catalog.optionalInputs) ? catalog.optionalInputs : []),
  ];
  const names = [];
  const seen = new Set();
  listed.forEach((key) => {
    const name = canonicalInputName(key);
    const lower = name.toLowerCase();
    if (!name || seen.has(lower)) return;
    seen.add(lower);
    names.push(name);
  });
  return names;
};

/**
 * Put saved field names on the catalog input they belong to.
 * A value stored before the catalog loads must fill "Measure", not a second slot.
 */
const foldInputs = (catalogKeys = [], saved = {}) => {
  const inputs = emptyInputMap(catalogKeys);
  const byLower = new Map(
    Object.keys(inputs).map((key) => [key.toLowerCase(), key]),
  );
  const catalogLoaded = byLower.size > 0;

  const place = (key, value) => {
    const items = coerceInputList(value);
    if (!items.length) return;
    const name = canonicalInputName(key);
    const catalogKey = byLower.get(name.toLowerCase());
    if (catalogKey) {
      inputs[catalogKey] = uniqueByLower([
        ...coerceInputList(inputs[catalogKey]),
        ...items,
      ]);
      return;
    }
    if (catalogLoaded || !name) return;
    inputs[name] = uniqueByLower([
      ...coerceInputList(inputs[name]),
      ...items,
    ]);
  };

  Object.entries(saved || {}).forEach(([key, value]) => place(key, value));
  return inputs;
};

/** Field names stored on a strategy input: a string, a list, or a dropped field. */
const coerceInputList = (value) => {
  if (Array.isArray(value)) {
    return uniqueByLower(value.flatMap((item) => coerceInputList(item)));
  }
  if (value && typeof value === "object") {
    const name = measureNameFromEntry(value) || getDroppedName(value);
    return name ? [name] : [];
  }
  const text = String(value ?? "").trim();
  return text ? [text] : [];
};

const persistInputValue = (value) => {
  const items = coerceInputList(value);
  if (!items.length) return null;
  return items.length === 1 ? items[0] : items;
};

const normalizeApplicableTo = (list = []) =>
  uniqueByLower(
    (Array.isArray(list) ? list : []).map(measureNameFromEntry),
  ).map((measure) => ({ measure }));

const buildUseEntryKey = (useEntry, idx) => {
  const op = String(
    useEntry?.analyticalOperation ||
      useEntry?.operation ||
      useEntry?.name ||
      (typeof useEntry === "string" ? useEntry : "") ||
      `use-${idx}`,
  ).trim();
  return `use-${idx}-${op}`;
};

const normalizeUseEntry = (entry, idx, operationsByName = {}) => {
  if (typeof entry === "string") {
    const opName = entry.trim();
    const catalog = operationsByName[opName.toLowerCase()] || {};
    return {
      key: buildUseEntryKey({ analyticalOperation: opName }, idx),
      analyticalOperation: opName,
      inputs: emptyInputMap(catalogInputKeys(catalog)),
    };
  }
  if (!entry || typeof entry !== "object") {
    return null;
  }
  const opName = String(
    entry.analyticalOperation || entry.operation || entry.name || "",
  ).trim();
  if (!opName) return null;
  const catalog = operationsByName[opName.toLowerCase()] || {};
  const reserved = new Set([
    "analyticalOperation",
    "analytical_operation",
    "operation",
    "name",
    "type",
    "kind",
    "key",
    "inputs",
    "requiredInputs",
    "optionalInputs",
  ]);
  const inputKeys = catalogInputKeys(
    Array.isArray(entry.inputs) && entry.inputs.length
      ? { inputs: entry.inputs }
      : catalog,
  );
  const saved = {};
  const copySaved = (value) => {
    if (!value || typeof value !== "object" || Array.isArray(value)) return;
    Object.entries(value).forEach(([key, item]) => {
      saved[key] = item;
    });
  };
  copySaved(entry.inputs);
  copySaved(entry.requiredInputs);
  copySaved(entry.optionalInputs);
  Object.entries(entry).forEach(([key, value]) => {
    if (reserved.has(key) || value == null) return;
    saved[key] = value;
  });
  return {
    key: entry.key || buildUseEntryKey({ analyticalOperation: opName }, idx),
    analyticalOperation: opName,
    inputs: foldInputs(inputKeys, saved),
  };
};

const emptyAiContext = () => ({
  instructions: "",
  synonyms: "",
  examples: "",
});

/** Same shape dimensions and measures persist: instructions, synonyms, examples. */
const readAiContext = (item = {}) => {
  const ai =
    item.aiContext && typeof item.aiContext === "object" && !Array.isArray(item.aiContext)
      ? item.aiContext
      : {};
  const synonyms = ai.synonyms ?? item.synonyms ?? "";
  return {
    instructions: String(ai.instructions ?? item.instructions ?? ""),
    synonyms: Array.isArray(synonyms)
      ? synonyms.map((part) => String(part || "").trim()).filter(Boolean).join(", ")
      : String(synonyms || ""),
    examples: String(
      ai.examples ?? ai.example ?? item.examples ?? item.example ?? "",
    ),
  };
};

const aiDraftFromContext = (aiContext = {}) => ({
  instructions: aiContext.instructions || "",
  synonyms: aiContext.synonyms || "",
  example: aiContext.examples || "",
});

const aiContextFromDraft = (draft = {}) => ({
  instructions: draft.instructions || "",
  synonyms: draft.synonyms || "",
  examples: draft.example || draft.examples || "",
});

const normalizeCapabilities = (list = [], operationsByName = {}) =>
  (Array.isArray(list) ? list : []).map((item, idx) => ({
    key: `cap-${idx}-${Date.now()}`,
    capability: String(item?.capability || `capability ${idx + 1}`),
    aiContext: readAiContext(item),
    applicableTo: normalizeApplicableTo(item?.applicableTo),
    uses: (Array.isArray(item?.uses) ? item.uses : [])
      .map((useEntry, useIdx) =>
        normalizeUseEntry(useEntry, useIdx, operationsByName),
      )
      .filter(Boolean),
  }));

/** Persist object-shaped diagnosticCapabilities for the semantic model. */
const toPayload = (caps) =>
  caps.map((cap) => ({
    capability: String(cap.capability || "").trim(),
    aiContext: readAiContext(cap),
    applicableTo: normalizeApplicableTo(cap.applicableTo),
    uses: (cap.uses || [])
      .map((useEntry) => {
        const analyticalOperation = String(
          useEntry.analyticalOperation || "",
        ).trim();
        if (!analyticalOperation) return null;
        const next = { analyticalOperation };
        Object.entries(useEntry.inputs || {}).forEach(([key, value]) => {
          const persisted = persistInputValue(value);
          if (persisted != null) next[key] = persisted;
        });
        return next;
      })
      .filter(Boolean),
  }));

const deleteOnlyMenu = (onDelete, label = "Delete") => (
  <Menu
    className="cube-bv-domain-description-menu"
    items={[
      {
        key: "delete",
        label: (
          <span className="cube-bv-delete-menu-label">
            <span className="cube-fields-menu-action-label">{label}</span>
          </span>
        ),
        onClick: ({ domEvent }) => {
          domEvent?.stopPropagation?.();
          onDelete?.();
        },
      },
    ]}
  />
);

const formatLabel = (key) =>
  String(key || "")
    .replace(/([A-Z])/g, " $1")
    .replace(/[_-]+/g, " ")
    .replace(/^\w/, (c) => c.toUpperCase())
    .trim();

const formatCategory = (key) =>
  formatLabel(key || "other").replace(/\b\w/g, (c) => c.toUpperCase());

const displayStrategyName = (name) =>
  String(name || "")
    .replace(/[_]+/g, " ")
    .replace(/\s+/g, " ")
    .trim();

const splitCommaList = (value) =>
  uniqueByLower(
    String(value || "")
      .split(",")
      .map((part) => part.trim()),
  );

const listToText = (value) =>
  (Array.isArray(value) ? value : []).map((item) => String(item || "").trim()).filter(Boolean).join(", ");

/** Turn snake_case and camelCase tokens into title case. Leave sentences unchanged. */
const formatDetailToken = (token) =>
  String(token || "")
    .replace(/([a-z0-9])([A-Z])/g, "$1 $2")
    .replace(/[_-]+/g, " ")
    .replace(/\s+/g, " ")
    .trim()
    .replace(/\b\w/g, (c) => c.toUpperCase());

const formatDetailText = (text) => {
  const value = String(text ?? "").trim();
  if (!value) return "";
  if (/^[A-Za-z0-9_-]+$/.test(value)) return formatDetailToken(value);
  return value.charAt(0).toUpperCase() + value.slice(1);
};

/** Skip client-only fields and details the semantic model screen does not show. */
const HIDDEN_STRATEGY_DETAIL_KEYS = new Set([
  "key",
  "custom",
  "operation",
  "description",
  "execution",
  "dashboardVisualizations",
  "dashboardVisualization",
  "dashboard_visualizations",
  "dashboard_visualization",
]);

const strategyDetailValue = (value) => {
  if (Array.isArray(value)) {
    return value.map((item) => formatDetailText(item)).filter(Boolean).join(", ");
  }
  if (value && typeof value === "object") {
    return Object.entries(value)
      .map(([key, item]) => {
        const text = strategyDetailValue(item);
        return text ? `${formatLabel(key)}: ${text}` : "";
      })
      .filter(Boolean)
      .join(", ");
  }
  return formatDetailText(value);
};

const strategyDetailRows = (op) =>
  Object.entries(op || {})
    .filter(([key]) => !HIDDEN_STRATEGY_DETAIL_KEYS.has(key))
    .map(([key, value]) => [formatLabel(key), strategyDetailValue(value)]);

/** Strategy details shown on hover. Dropped uses show the description only. */
const operationTooltipTitle = (op, { descriptionOnly = false } = {}) => {
  if (!op) return null;
  const description = strategyDetailValue(op.description);
  if (descriptionOnly) {
    return description ? (
      <div className="diagnostic-operation-tooltip">
        <div className="diagnostic-operation-tooltip-description">{description}</div>
      </div>
    ) : null;
  }
  const rows = strategyDetailRows(op);
  if (!description && !rows.length) return null;
  return (
    <div className="diagnostic-operation-tooltip">
      {description ? (
        <div className="diagnostic-operation-tooltip-description">{description}</div>
      ) : null}
      {rows.map(([label, value]) => (
        <div key={label} className="diagnostic-operation-tooltip-row">
          <span className="diagnostic-operation-tooltip-label">{label}</span>
          <span className="diagnostic-operation-tooltip-value">{value || "None"}</span>
        </div>
      ))}
    </div>
  );
};

const strategyTooltipProps = {
  placement: "left",
  mouseEnterDelay: 0.3,
  overlayClassName: "diagnostic-operation-tooltip-overlay",
  getPopupContainer: () => document.body,
};

const CUSTOM_STRATEGY_TYPE = "custom";

const isCustomStrategy = (entry) =>
  String(entry?.type || "").trim().toLowerCase() === CUSTOM_STRATEGY_TYPE ||
  entry?.custom === true;

const emptyCustomOperation = (index = 1) => ({
  key: `custom-op-${Date.now()}`,
  operation: `custom strategy ${index}`,
  type: CUSTOM_STRATEGY_TYPE,
  category: "custom",
  description: "",
  inputs: ["measure"],
  preconditions: [],
  limitations: [],
  outputFields: [],
  dashboardVisualizations: [],
  execution: { method: "sql", logic: "" },
  custom: true,
});

const normalizeCustomOperation = (entry, index = 0) => {
  const operation = String(entry?.operation || `custom strategy ${index + 1}`).trim();
  const execution = entry?.execution && typeof entry.execution === "object" ? entry.execution : {};
  return {
    key: entry?.key || `custom-op-${index}-${operation}`,
    operation,
    type: CUSTOM_STRATEGY_TYPE,
    category: String(entry?.category || "custom").trim() || "custom",
    description: String(entry?.description || ""),
    inputs: uniqueByLower(
      catalogInputKeys({
        inputs: entry?.inputs,
        requiredInputs: entry?.requiredInputs,
        optionalInputs: entry?.optionalInputs,
      }),
    ),
    preconditions: uniqueByLower(entry?.preconditions || []),
    limitations: uniqueByLower(entry?.limitations || []),
    outputFields: uniqueByLower(entry?.outputFields || []),
    dashboardVisualizations: uniqueByLower(entry?.dashboardVisualizations || []),
    execution: {
      method: String(execution.method || "sql"),
      logic: String(execution.logic || ""),
    },
    custom: true,
  };
};

const customOperationPayload = (operations = []) =>
  operations.map((entry) => normalizeCustomOperation(entry));

function CapabilityNameMenu({ capability, onChangeAi, onDelete }) {
  const [openKey, setOpenKey] = useState(null);
  return (
    <Menu
      className="cube-bv-domain-description-menu cube-field-semantic-menu-panel cube-field-semantic-menu-panel--agent"
      items={[
        {
          key: "ai-context",
          label: (
            <div
              onClick={(event) => event.stopPropagation()}
              onMouseDown={(event) => event.stopPropagation()}
            >
              <AgentAiContextTree
                draft={aiDraftFromContext(capability.aiContext || emptyAiContext())}
                onDraftChange={(next) => onChangeAi?.(aiContextFromDraft(next))}
                clearFieldTooltip="Clear field values"
                openKey={openKey}
                onOpenKeyChange={setOpenKey}
              />
            </div>
          ),
        },
        {
          key: "delete",
          label: (
            <span className="cube-bv-delete-menu-label">
              <span className="cube-fields-menu-action-label">Delete</span>
            </span>
          ),
          onClick: ({ domEvent }) => {
            domEvent?.stopPropagation?.();
            onDelete?.();
          },
        },
      ]}
    />
  );
}

function StrategyNameList({ label, values = [], onChange }) {
  const [draft, setDraft] = useState("");
  const commitDraft = () => {
    const text = draft.trim();
    if (!text) return;
    onChange?.(uniqueByLower([...values, text]));
    setDraft("");
  };
  return (
    <div className="diagnostic-input-slot">
      <div className="diagnostic-input-slot-row">
        <span className="diagnostic-input-slot-label">{label}</span>
        <div className="diagnostic-input-values">
          {values.map((name) => (
            <Tag
              key={name}
              closable
              className="diagnostic-input-chip"
              onClose={(event) => {
                event.preventDefault();
                onChange?.(
                  values.filter((item) => item.toLowerCase() !== name.toLowerCase()),
                );
              }}
            >
              {formatLabel(name)}
            </Tag>
          ))}
          <Input
            size="small"
            bordered={false}
            value={draft}
            placeholder={values.length ? "Add another input" : "Type an input name"}
            onChange={(event) => setDraft(event.target.value)}
            onPressEnter={(event) => {
              event.preventDefault();
              commitDraft();
            }}
            onBlur={commitDraft}
          />
        </div>
      </div>
    </div>
  );
}

function CustomStrategyMenu({ operation, onPatch, onDelete }) {
  const [openKey, setOpenKey] = useState(null);
  const textField = (fieldKey, label, value, onChange, placeholder) => (
    <AgentRightSubmenuTextField
      fieldKey={fieldKey}
      label={label}
      value={value}
      placeholder={placeholder}
      clearFieldTooltip="Clear field values"
      openKey={openKey}
      onOpenKeyChange={setOpenKey}
      onChange={onChange}
    />
  );
  return (
    <Menu
      className="cube-bv-domain-description-menu"
      items={[
        {
          key: "preconditions",
          label: textField(
            "preconditions",
            "Preconditions",
            listToText(operation.preconditions),
            (next) => onPatch({ preconditions: splitCommaList(next) }),
            "What must be true before this strategy runs",
          ),
        },
        {
          key: "limitations",
          label: textField(
            "limitations",
            "Limitations",
            listToText(operation.limitations),
            (next) => onPatch({ limitations: splitCommaList(next) }),
            "What this strategy does not cover",
          ),
        },
        {
          key: "outputFields",
          label: textField(
            "output-fields",
            "Output fields",
            listToText(operation.outputFields),
            (next) => onPatch({ outputFields: splitCommaList(next) }),
            "Fields this strategy returns",
          ),
        },
        {
          key: "execution",
          label: textField(
            "execution",
            "Execution",
            operation.execution?.logic || "",
            (next) =>
              onPatch({
                execution: {
                  method: operation.execution?.method || "sql",
                  logic: next,
                },
              }),
            "How the strategy is calculated",
          ),
        },
        {
          key: "visualizations",
          label: textField(
            "visualizations",
            "Visualizations",
            listToText(operation.dashboardVisualizations),
            (next) => onPatch({ dashboardVisualizations: splitCommaList(next) }),
            "Charts that fit this strategy",
          ),
        },
        {
          key: "delete",
          label: (
            <span className="cube-bv-delete-menu-label">
              <span className="cube-fields-menu-action-label">Delete</span>
            </span>
          ),
          onClick: ({ domEvent }) => {
            domEvent?.stopPropagation?.();
            onDelete?.();
          },
        },
      ]}
    />
  );
}

function DiagnosticInputSlot({
  label,
  tooltip = "",
  values = [],
  suggestions = [],
  domainName,
  topicName,
  onAppend,
  onRemove,
}) {
  const [draft, setDraft] = useState("");
  const draftRef = useRef("");
  const selectingRef = useRef(false);
  const setDraftValue = (value) => {
    draftRef.current = value;
    setDraft(value);
  };
  const commitDraft = () => {
    const text = draftRef.current.trim();
    if (!text) return;
    onAppend?.(text);
    setDraftValue("");
  };
  const onDrop = (item) => onAppend?.(getDroppedName(item));
  const selected = new Set(values.map((name) => String(name).toLowerCase()));
  const query = draft.trim().toLowerCase();
  const options = suggestions
    .filter((name) => !selected.has(name.toLowerCase()))
    .filter((name) => !query || name.toLowerCase().includes(query))
    .slice(0, 12)
    .map((name) => ({ value: name }));
  return (
    <AgentTopicDropZone
      className="diagnostic-input-slot"
      domainName={domainName}
      topicName={topicName}
      onDropCubeField={onDrop}
      onDropMetadataField={onDrop}
    >
      <div className="diagnostic-input-slot-row">
        <span className="diagnostic-input-slot-label">
          <span>{label}</span>
          {tooltip ? (
            <Tooltip
              title={tooltip}
              placement="top"
              mouseEnterDelay={0.2}
              getPopupContainer={() => document.body}
            >
              <InfoCircleOutlined className="diagnostic-input-slot-info" />
            </Tooltip>
          ) : null}
        </span>
        <div className="diagnostic-input-values">
          {values.map((name) => (
            <Tag
              key={name}
              closable
              className="diagnostic-input-chip"
              onClose={(event) => {
                event.preventDefault();
                onRemove?.(name);
              }}
            >
              {name}
            </Tag>
          ))}
          <AutoComplete
            className="diagnostic-input-suggest"
            size="small"
            value={draft}
            options={options}
            dropdownMatchSelectWidth={false}
            getPopupContainer={() => document.body}
            onChange={setDraftValue}
            onSelect={(value) => {
              selectingRef.current = true;
              onAppend?.(value);
              setDraftValue("");
            }}
            onKeyDown={(event) => {
              if (event.key !== "Enter" || options.length) return;
              event.preventDefault();
              commitDraft();
            }}
            onBlur={() => {
              window.setTimeout(() => {
                if (selectingRef.current) {
                  selectingRef.current = false;
                  return;
                }
                commitDraft();
              }, 200);
            }}
          >
            <Input
              size="small"
              bordered={false}
              placeholder={
                values.length
                  ? "Drop or type another"
                  : `Drop field or type ${label}`
              }
            />
          </AutoComplete>
        </div>
      </div>
    </AgentTopicDropZone>
  );
}

export function DiagnosticCapabilitiesPanel({
  cubeFieldsData = {},
  onCapabilitiesChange,
  onCustomOperationsChange,
  active = true,
}) {
  const dispatch = useDispatch();
  const metadataTablesData = useSelector(
    (state) => state.agent?.metadataTablesData || {},
  );
  const inputSuggestions = useMemo(
    () => collectInputSuggestions(cubeFieldsData?.children, metadataTablesData),
    [cubeFieldsData?.children, metadataTablesData],
  );
  const [operations, setOperations] = useState([]);
  const [inputTooltips, setInputTooltips] = useState({});
  const [operationsLoading, setOperationsLoading] = useState(false);
  const [operationsError, setOperationsError] = useState("");
  const [operationFilter, setOperationFilter] = useState("");
  const [operationFilterOpen, setOperationFilterOpen] = useState(false);
  const [customOperations, setCustomOperations] = useState(() =>
    (Array.isArray(cubeFieldsData?.customAnalyticalOperations)
      ? cubeFieldsData.customAnalyticalOperations
      : []
    )
      .filter(isCustomStrategy)
      .map(normalizeCustomOperation),
  );
  const [customStrategyDraft, setCustomStrategyDraft] = useState(null);
  const customOperationsRef = useRef(customOperations);
  customOperationsRef.current = customOperations;
  const allOperations = useMemo(() => {
    const customNames = new Set(
      customOperations
        .map((op) => String(op.operation || "").trim().toLowerCase())
        .filter(Boolean),
    );
    const catalog = operations.filter(
      (op) => !customNames.has(String(op.operation || "").trim().toLowerCase()),
    );
    return [...catalog, ...customOperations];
  }, [operations, customOperations]);
  const operationsByName = useMemo(() => {
    const map = {};
    allOperations.forEach((op) => {
      const name = String(op?.operation || "").trim().toLowerCase();
      if (name) map[name] = op;
    });
    return map;
  }, [allOperations]);

  const [capabilities, setCapabilities] = useState(() =>
    normalizeCapabilities(
      Array.isArray(cubeFieldsData?.diagnosticCapabilities)
        ? cubeFieldsData.diagnosticCapabilities
        : [],
      {},
    ),
  );
  const [collapsedCaps, setCollapsedCaps] = useState({});
  const [collapsedSections, setCollapsedSections] = useState({});
  const [collapsedUses, setCollapsedUses] = useState({});
  const [expandedCategories, setExpandedCategories] = useState({});
  const [addInputFor, setAddInputFor] = useState(null);
  const capabilitiesRef = useRef(capabilities);
  capabilitiesRef.current = capabilities;

  useEffect(() => {
    let cancelled = false;
    setOperationsLoading(true);
    setOperationsError("");
    instantBI(dispatch).instantBIUtilityRequest({
      uri: uriConfig.instantBIUtilityAnalyticalOperations,
      formData: { body: JSON.stringify({}) },
      callback: (res) => {
        if (cancelled) return;
        const payload = res?.response != null ? res.response : res;
        const list = Array.isArray(payload?.operations)
          ? payload.operations
          : Array.isArray(payload?.response?.operations)
            ? payload.response.operations
            : [];
        const tooltipSection =
          payload?.inputTooltips && typeof payload.inputTooltips === "object"
            ? payload.inputTooltips
            : payload?.response?.inputTooltips &&
                typeof payload.response.inputTooltips === "object"
              ? payload.response.inputTooltips
              : {};
        const tooltipMap = {};
        Object.entries(tooltipSection).forEach(([key, value]) => {
          const text = String(value || "").trim();
          const name = String(key || "").trim().toLowerCase();
          if (name && text) tooltipMap[name] = text;
        });
        setInputTooltips(tooltipMap);
        setOperations(list);
        setOperationsLoading(false);
        if (!list.length && payload?.status === 0) {
          setOperationsError(
            Array.isArray(payload.error)
              ? payload.error.join("; ")
              : payload.error || "Failed to load analytical operations",
          );
        }
      },
      errback: (err) => {
        if (cancelled) return;
        setOperations([]);
        setInputTooltips({});
        setOperationsLoading(false);
        setOperationsError(
          err?.message || "Failed to load analytical operations",
        );
      },
    });
    return () => {
      cancelled = true;
    };
  }, [dispatch]);

  const commitCustom = useCallback(
    (updater) => {
      const current = customOperationsRef.current;
      const next = (
        typeof updater === "function" ? updater(current) : updater
      ).map(normalizeCustomOperation);
      customOperationsRef.current = next;
      setCustomOperations(next);
      onCustomOperationsChange?.(customOperationPayload(next));
    },
    [onCustomOperationsChange],
  );

  useEffect(() => {
    if (!Array.isArray(cubeFieldsData?.customAnalyticalOperations)) {
      return;
    }
    const incoming = customOperationPayload(
      cubeFieldsData.customAnalyticalOperations.map(normalizeCustomOperation),
    );
    const current = customOperationPayload(customOperationsRef.current);
    if (JSON.stringify(incoming) === JSON.stringify(current)) {
      return;
    }
    const next = cubeFieldsData.customAnalyticalOperations
      .filter(isCustomStrategy)
      .map(normalizeCustomOperation);
    customOperationsRef.current = next;
    setCustomOperations(next);
  }, [cubeFieldsData?.customAnalyticalOperations]);

  const commit = useCallback(
    (updater) => {
      const current = capabilitiesRef.current;
      const next = typeof updater === "function" ? updater(current) : updater;
      capabilitiesRef.current = next;
      setCapabilities(next);
      onCapabilitiesChange?.(toPayload(next));
    },
    [onCapabilitiesChange],
  );

  useEffect(() => {
    if (!Array.isArray(cubeFieldsData?.diagnosticCapabilities)) {
      return;
    }
    const incoming = cubeFieldsData.diagnosticCapabilities;
    const currentPayload = toPayload(capabilitiesRef.current);
    if (JSON.stringify(incoming) === JSON.stringify(currentPayload)) {
      return;
    }
    const nextCaps = normalizeCapabilities(incoming, operationsByName);
    capabilitiesRef.current = nextCaps;
    setCapabilities(nextCaps);
  }, [cubeFieldsData?.diagnosticCapabilities, operationsByName]);

  // Fill required/optional slots from catalog without rewriting saved payload.
  useEffect(() => {
    if (!allOperations.length) return;
    setCapabilities((prev) => {
      const next = prev.map((cap) => ({
        ...cap,
        uses: (cap.uses || []).map((useEntry, idx) => {
          const catalog =
            operationsByName[
              String(useEntry.analyticalOperation || "").toLowerCase()
            ] || {};
          const folded = foldInputs(catalogInputKeys(catalog), {
            ...(useEntry.requiredInputs || {}),
            ...(useEntry.optionalInputs || {}),
            ...(useEntry.inputs || {}),
          });
          return {
            ...useEntry,
            key: useEntry.key || buildUseEntryKey(useEntry, idx),
            inputs: folded,
          };
        }),
      }));
      capabilitiesRef.current = next;
      return next;
    });
  }, [allOperations, operationsByName]);

  const updateCap = (capKey, patch) =>
    commit((prev) =>
      prev.map((c) => (c.key === capKey ? { ...c, ...patch(c) } : c)),
    );

  const addApplicableMeasure = (capKey, name) => {
    const measure = String(name || "").trim();
    if (!measure) return;
    updateCap(capKey, (c) => ({
      applicableTo: normalizeApplicableTo([
        ...(c.applicableTo || []),
        { measure },
      ]),
    }));
  };

  const removeApplicableMeasure = (capKey, measure) =>
    updateCap(capKey, (c) => ({
      applicableTo: (c.applicableTo || []).filter(
        (entry) => measureNameFromEntry(entry) !== measure,
      ),
    }));

  const renameApplicableMeasure = (capKey, previous, nextName) => {
    const clean = String(nextName || "").trim();
    if (!clean || clean === previous) return;
    updateCap(capKey, (c) => ({
      applicableTo: normalizeApplicableTo(
        (c.applicableTo || []).map((entry) =>
          measureNameFromEntry(entry) === previous
            ? { measure: clean }
            : entry,
        ),
      ),
    }));
  };

  const addUseFromOperation = (capKey, operationItem) => {
    const opName = String(
      operationItem?.operation || operationItem?.analyticalOperation || "",
    ).trim();
    if (!opName) return;
    const catalog =
      operationsByName[opName.toLowerCase()] || operationItem || {};
    updateCap(capKey, (c) => {
      const already = (c.uses || []).some(
        (useEntry) =>
          String(useEntry.analyticalOperation || "").toLowerCase() ===
          opName.toLowerCase(),
      );
      if (already) return {};
      return {
        uses: [
          ...(c.uses || []),
          {
            key: `use-${Date.now()}-${opName}`,
            analyticalOperation: opName,
            inputs: emptyInputMap(catalogInputKeys(catalog)),
          },
        ],
      };
    });
    setCollapsedCaps((p) => ({ ...p, [capKey]: false }));
    setCollapsedSections((p) => ({ ...p, [`${capKey}:uses`]: false }));
  };

  const removeUse = (capKey, useKey) =>
    updateCap(capKey, (c) => ({
      uses: (c.uses || []).filter((useEntry) => useEntry.key !== useKey),
    }));

  const setUseInputValues = (capKey, useKey, inputKey, values) => {
    const nextValues = uniqueByLower(coerceInputList(values));
    updateCap(capKey, (c) => ({
      uses: (c.uses || []).map((useEntry) => {
        if (useEntry.key !== useKey) return useEntry;
        return {
          ...useEntry,
          inputs: {
            ...(useEntry.inputs || {}),
            [inputKey]: nextValues,
          },
        };
      }),
    }));
  };

  const appendUseInputValue = (capKey, useKey, inputKey, name) => {
    const nextName = String(name || "").trim();
    if (!nextName) return;
    updateCap(capKey, (c) => ({
      uses: (c.uses || []).map((useEntry) => {
        if (useEntry.key !== useKey) return useEntry;
        return {
          ...useEntry,
          inputs: {
            ...(useEntry.inputs || {}),
            [inputKey]: uniqueByLower([
              ...coerceInputList(useEntry?.inputs?.[inputKey]),
              nextName,
            ]),
          },
        };
      }),
    }));
  };

  const handleAddCapability = () => {
    const key = `cap-${Date.now()}`;
    commit((prev) => [
      ...prev,
      {
        key,
        capability: `capability ${prev.length + 1}`,
        aiContext: emptyAiContext(),
        applicableTo: [],
        uses: [],
      },
    ]);
    setCollapsedCaps((prev) => ({ ...prev, [key]: false }));
  };

  useEffect(() => {
    if (!active) return;
    setCollapsedCaps((prev) => {
      const next = { ...prev };
      capabilitiesRef.current.forEach((cap) => {
        next[cap.key] = true;
      });
      return next;
    });
  }, [active]);

  const toggle = (setter, key) =>
    setter((prev) => ({ ...prev, [key]: prev[key] === false }));

  const openAddInput = (cap, listKey) => {
    const sectionKey = `${cap.key}:${listKey}`;
    setCollapsedCaps((p) => ({ ...p, [cap.key]: false }));
    setCollapsedSections((p) => ({ ...p, [sectionKey]: false }));
    setAddInputFor((prev) => (prev === sectionKey ? null : sectionKey));
  };

  const openCustomStrategyModal = (operation, isNew = false) => {
    setCustomStrategyDraft({
      ...normalizeCustomOperation(operation),
      isNew: Boolean(isNew),
    });
  };

  const addCustomStrategy = () => {
    openCustomStrategyModal(
      emptyCustomOperation(customOperationsRef.current.length + 1),
      true,
    );
  };

  const saveCustomStrategy = () => {
    if (!customStrategyDraft) return;
    const clean = String(customStrategyDraft.operation || "").trim();
    if (!clean) return;
    const nextOp = normalizeCustomOperation({
      ...customStrategyDraft,
      operation: clean,
    });
    const previous = customOperationsRef.current.find((op) => op.key === nextOp.key);
    if (!previous) {
      commitCustom((prev) => [...prev, nextOp]);
    } else {
      commitCustom((prev) =>
        prev.map((op) => (op.key === nextOp.key ? nextOp : op)),
      );
      if (previous.operation !== clean) {
        commit((prev) =>
          prev.map((cap) => ({
            ...cap,
            uses: (cap.uses || []).map((useEntry) =>
              String(useEntry.analyticalOperation || "").toLowerCase() ===
              previous.operation.toLowerCase()
                ? { ...useEntry, analyticalOperation: clean }
                : useEntry,
            ),
          })),
        );
      }
    }
    setExpandedCategories((prev) => ({ ...prev, custom: true }));
    setCustomStrategyDraft(null);
  };

  const patchCustomStrategy = (key, patch) =>
    commitCustom((prev) =>
      prev.map((op) => (op.key === key ? { ...op, ...patch } : op)),
    );

  const filteredOperations = useMemo(() => {
    const q = operationFilter.trim().toLowerCase();
    if (!q) return allOperations;
    return allOperations.filter((op) => {
      const blob = [
        op.operation,
        displayStrategyName(op.operation),
        op.category,
        op.description,
        ...catalogInputKeys(op),
      ]
        .join(" ")
        .toLowerCase();
      return blob.includes(q);
    });
  }, [allOperations, operationFilter]);

  const groupedOperations = useMemo(() => {
    const groups = new Map();
    filteredOperations.forEach((op) => {
      const category = String(op?.category || "other").trim() || "other";
      if (!groups.has(category)) groups.set(category, []);
      groups.get(category).push(op);
    });
    return Array.from(groups.entries());
  }, [filteredOperations]);

  const filteringOperations = Boolean(operationFilter.trim());
  const categoryIsOpen = (category) =>
    filteringOperations || Boolean(expandedCategories[category]);
  const toggleCategory = (category) =>
    setExpandedCategories((prev) => ({ ...prev, [category]: !prev[category] }));

  const renderCustomStrategy = (op) => (
    <AnalyticalOperationDragSource key={op.key} operation={op}>
      <Dropdown
        overlay={
          <CustomStrategyMenu
            operation={op}
            onPatch={(patch) => patchCustomStrategy(op.key, patch)}
            onDelete={() =>
              commitCustom((prev) => prev.filter((item) => item.key !== op.key))
            }
          />
        }
        trigger={["contextMenu"]}
        destroyPopupOnHide
      >
        <Tooltip title={operationTooltipTitle(op)} {...strategyTooltipProps}>
          <div className="business-view-context-trigger diagnostic-operation-item diagnostic-custom-strategy-row">
          <AimOutlined className="diagnostic-use-op-icon" />
          <span className="diagnostic-operation-name">
            {displayStrategyName(op.operation)}
          </span>
          <EditOutlined
            className="diagnostic-custom-strategy-edit"
            role="button"
            aria-label={`Edit ${op.operation}`}
            onMouseDown={(event) => event.stopPropagation()}
            onClick={(event) => {
              event.stopPropagation();
              openCustomStrategyModal(op, false);
            }}
          />
          </div>
        </Tooltip>
      </Dropdown>
    </AnalyticalOperationDragSource>
  );

  const renderInputSlot = (cap, useEntry, inputKey) => (
    <DiagnosticInputSlot
      key={`${useEntry.key}:${inputKey}`}
      label={formatLabel(inputKey)}
      tooltip={inputTooltips[String(inputKey || "").trim().toLowerCase()] || ""}
      values={coerceInputList(useEntry?.inputs?.[inputKey])}
      suggestions={inputSuggestions}
      domainName={cap.capability}
      topicName={`${useEntry.analyticalOperation}.${inputKey}`}
      onAppend={(name) =>
        appendUseInputValue(cap.key, useEntry.key, inputKey, name)
      }
      onRemove={(name) =>
        setUseInputValues(
          cap.key,
          useEntry.key,
          inputKey,
          coerceInputList(useEntry?.inputs?.[inputKey]).filter(
            (item) => item.toLowerCase() !== String(name).toLowerCase(),
          ),
        )
      }
    />
  );

  const renderUseRow = (cap, useEntry) => {
    const useKey = useEntry.key;
    const isCollapsed = collapsedUses[useKey] !== false;
    const inputKeys = Object.keys(useEntry.inputs || {});
    return (
      <div key={useKey} className="business-view-agent-topic-hierarchy diagnostic-use-entry">
        <div className="business-view-agent-topic-hierarchy-header">
          <span
            className="business-view-agent-topic-hierarchy-toggle"
            role="button"
            tabIndex={0}
            aria-expanded={!isCollapsed}
            onClick={(e) => {
              e.stopPropagation();
              setCollapsedUses((p) => ({ ...p, [useKey]: p[useKey] === false }));
            }}
          >
            {isCollapsed ? <PlusOutlined /> : <MinusOutlined />}
          </span>
          <Dropdown
            overlay={deleteOnlyMenu(() => removeUse(cap.key, useKey))}
            trigger={["contextMenu"]}
          >
            <Tooltip
              title={operationTooltipTitle(
                operationsByName[
                  String(useEntry.analyticalOperation || "").trim().toLowerCase()
                ],
                { descriptionOnly: true },
              )}
              {...strategyTooltipProps}
              placement="right"
            >
              <span className="business-view-context-trigger business-view-agent-topic-hierarchy-name">
                <AimOutlined className="diagnostic-use-op-icon" />
                <span>{displayStrategyName(useEntry.analyticalOperation)}</span>
              </span>
            </Tooltip>
          </Dropdown>
        </div>
        {!isCollapsed &&
          (inputKeys.length ? (
            <div className="diagnostic-use-inputs">
              <div className="diagnostic-input-group-fields diagnostic-use-input-row">
                {inputKeys.map((inputKey) =>
                  renderInputSlot(cap, useEntry, inputKey),
                )}
              </div>
            </div>
          ) : null)}
      </div>
    );
  };

  const renderApplicableSection = (cap) => {
    const sectionKey = `${cap.key}:applicableTo`;
    const isCollapsed = collapsedSections[sectionKey] !== false;
    const onDrop = (item) => addApplicableMeasure(cap.key, getDroppedName(item));
    const names = (cap.applicableTo || []).map(measureNameFromEntry);
    return (
      <AgentTopicDropZone
        key={sectionKey}
        className="business-view-agent-topic-row"
        domainName={cap.capability}
        topicName="Applicable Measures"
        onDropCubeField={onDrop}
        onDropMetadataField={onDrop}
      >
        <div
          className="business-view-agent-topic-main"
          style={{ display: "flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
        >
          <span
            className="business-view-entry-caret business-view-agent-topic-caret"
            role="button"
            tabIndex={0}
            aria-expanded={!isCollapsed}
            onClick={(e) => {
              e.stopPropagation();
              toggle(setCollapsedSections, sectionKey);
            }}
          >
            {isCollapsed ? <CaretRightOutlined /> : <CaretDownOutlined />}
          </span>
          <Tooltip
            title="Measures this capability investigates for a matching question."
            placement="top"
          >
            <CalculatorOutlined className="business-view-type-icon business-view-type-icon--topic" />
          </Tooltip>
          <div
            className="business-view-entry-label business-view-entry-label--with-add"
            style={{ display: "inline-flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
          >
            <span className="business-view-topic-title row-title">
              Applicable Measures
            </span>
            <Tooltip title="Add applicable measure" placement="top">
              <span
                className="business-view-action-btn business-view-action-btn--primary business-view-add-topic-beside-edit"
                role="button"
                tabIndex={0}
                aria-label="Add applicable measure"
                onClick={(e) => {
                  e.stopPropagation();
                  openAddInput(cap, "applicableTo");
                }}
              >
                <PlusOutlined />
              </span>
            </Tooltip>
            <Tooltip
              title="Measures this capability investigates for a matching question."
              placement="top"
            >
              <InfoCircleOutlined className="cube-info-icon" />
            </Tooltip>
          </div>
        </div>
        {!isCollapsed && (
          <div
            className="business-view-agent-topic-fields"
            style={{ display: "flex", flexDirection: "column", gap: 2, paddingLeft: 20 }}
          >
            {addInputFor === sectionKey ? (
              <div className="diagnostic-add-input">
                <Input
                  size="small"
                  autoFocus
                  placeholder="Measure name, press Enter"
                  onPressEnter={(e) => {
                    addApplicableMeasure(cap.key, e.target.value);
                    setAddInputFor(null);
                  }}
                  onBlur={() => setAddInputFor(null)}
                  onKeyDown={(e) => e.key === "Escape" && setAddInputFor(null)}
                />
              </div>
            ) : null}
            {names.length === 0 ? (
              <p className="business-view-agent-topic-fields-empty">
                Drop applicable measures here
              </p>
            ) : (
              names.map((name) => (
                <div key={name} className="business-view-agent-topic-field-row">
                  <Dropdown
                    overlay={deleteOnlyMenu(() =>
                      removeApplicableMeasure(cap.key, name),
                    )}
                    trigger={["contextMenu"]}
                  >
                    <span
                      className="business-view-context-trigger"
                      style={{ display: "inline-flex", alignItems: "center" }}
                    >
                      <Paragraph
                        className="diagnostic-value-title"
                        style={{
                          display: "inline-flex",
                          alignItems: "center",
                          margin: 0,
                        }}
                        editable={{
                          tooltip: "Edit",
                          text: name,
                          icon: (
                            <EditOutlined
                              role="button"
                              aria-label={`Edit ${name}`}
                            />
                          ),
                          onChange: (val) =>
                            renameApplicableMeasure(cap.key, name, val),
                        }}
                      >
                        <span>{name}</span>
                      </Paragraph>
                    </span>
                  </Dropdown>
                </div>
              ))
            )}
          </div>
        )}
      </AgentTopicDropZone>
    );
  };

  const renderUsesSection = (cap) => {
    const sectionKey = `${cap.key}:uses`;
    const isCollapsed = collapsedSections[sectionKey] !== false;
    return (
      <AgentTopicDropZone
        key={sectionKey}
        className="business-view-agent-topic-row"
        domainName={cap.capability}
        topicName="Uses"
        acceptAnalyticalOperations
        acceptCubeFields={false}
        onDropAnalyticalOperation={(item) => addUseFromOperation(cap.key, item)}
      >
        <div
          className="business-view-agent-topic-main"
          style={{ display: "flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
        >
          <span
            className="business-view-entry-caret business-view-agent-topic-caret"
            role="button"
            tabIndex={0}
            aria-expanded={!isCollapsed}
            onClick={(e) => {
              e.stopPropagation();
              toggle(setCollapsedSections, sectionKey);
            }}
          >
            {isCollapsed ? <CaretRightOutlined /> : <CaretDownOutlined />}
          </span>
          <Tooltip
            title="Analytical strategies applied to those measures."
            placement="top"
          >
            <BranchesOutlined className="business-view-type-icon business-view-type-icon--topic" />
          </Tooltip>
          <div
            className="business-view-entry-label business-view-entry-label--with-add"
            style={{ display: "inline-flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
          >
            <span className="business-view-topic-title row-title">Uses</span>
            <Tooltip
              title="Analytical strategies applied to those measures."
              placement="top"
            >
              <InfoCircleOutlined className="cube-info-icon" />
            </Tooltip>
          </div>
        </div>
        {!isCollapsed && (
          <div
            className="business-view-agent-topic-fields"
            style={{ display: "flex", flexDirection: "column", gap: 2, paddingLeft: 20 }}
          >
            {(cap.uses || []).length === 0 ? (
              <p className="business-view-agent-topic-fields-empty">
                Drop analytical operations here
              </p>
            ) : (
              (cap.uses || []).map((useEntry) => renderUseRow(cap, useEntry))
            )}
          </div>
        )}
      </AgentTopicDropZone>
    );
  };

  return (
    <div className="business-view-hierarchy diagnostic-business-hierarchy">
      <div className="cube-business-view-toolbar">
        <Tooltip title="Add capability" placement="left">
          <span
            className="business-view-action-btn business-view-action-btn--primary cube-bv-toolbar-action"
            role="button"
            tabIndex={0}
            aria-label="Add capability"
            onClick={handleAddCapability}
          >
            <PlusOutlined className="cube-add-metric-icon" />
          </span>
        </Tooltip>
      </div>

      <div className="business-view-hierarchy-body diagnostic-split">
        <div className="diagnostic-capabilities-pane">
        {capabilities.length === 0 ? (
          <div className="business-view-empty">
            <p className="business-view-empty-title">
              Diagnostics plans the analytical answer
            </p>
            <p className="business-view-empty-desc">
              Each capability has a name, applicable measures, and strategies
              it uses. Drag a strategy into Uses to configure its inputs.
              Click + to add a capability.
            </p>
          </div>
        ) : (
          capabilities.map((cap) => {
              const isCollapsed = collapsedCaps[cap.key] !== false;
              return (
                <div
                  key={cap.key}
                  className="business-view-entry business-view-entry--agent"
                >
                  <div
                    className="business-view-entry-header"
                    style={{
                      display: "flex",
                      alignItems: "center",
                      gap: 6,
                      flexWrap: "nowrap",
                    }}
                  >
                    <span
                      className="business-view-entry-caret"
                      role="button"
                      tabIndex={0}
                      aria-expanded={!isCollapsed}
                      onClick={() =>
                        setCollapsedCaps((prev) => ({
                          ...prev,
                          [cap.key]: prev[cap.key] === false,
                        }))
                      }
                    >
                      {isCollapsed ? (
                        <CaretRightOutlined />
                      ) : (
                        <CaretDownOutlined />
                      )}
                    </span>
                    <Tooltip
                      title="Names the investigation this capability runs when a question matches."
                      placement="top"
                    >
                      <AppstoreOutlined
                        className="business-view-type-icon business-view-type-icon--domain"
                        aria-hidden
                      />
                    </Tooltip>
                    <div
                      className="business-view-entry-label business-view-entry-label--with-add"
                      style={{
                        display: "inline-flex",
                        alignItems: "center",
                        gap: 6,
                        flexWrap: "nowrap",
                      }}
                    >
                      <Dropdown
                        overlay={
                          <CapabilityNameMenu
                            capability={cap}
                            onChangeAi={(aiContext) =>
                              updateCap(cap.key, () => ({ aiContext }))
                            }
                            onDelete={() =>
                              commit((prev) =>
                                prev.filter((c) => c.key !== cap.key),
                              )
                            }
                          />
                        }
                        trigger={["contextMenu"]}
                        destroyPopupOnHide
                      >
                        <span className="business-view-context-trigger">
                          <Paragraph
                            className="business-view-entry-title row-title"
                            style={{
                              display: "inline-flex",
                              alignItems: "center",
                              margin: 0,
                              marginBottom: 0,
                              padding: 0,
                            }}
                            editable={{
                              tooltip: "Edit capability name",
                              text: cap.capability,
                              icon: <EditOutlined role="button" />,
                              onChange: (val) => {
                                const next = (val || "").trim();
                                if (next && next !== cap.capability) {
                                  updateCap(cap.key, () => ({
                                    capability: next,
                                  }));
                                }
                              },
                            }}
                          >
                            <span className="business-view-title-text">
                              {cap.capability}
                            </span>
                          </Paragraph>
                        </span>
                      </Dropdown>
                      <Tooltip
                        title="Names the investigation this capability runs when a question matches."
                        placement="top"
                      >
                        <InfoCircleOutlined className="cube-info-icon" />
                      </Tooltip>
                    </div>
                  </div>

                  {!isCollapsed && (
                    <div
                      className="business-view-agent-topics"
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 4,
                        paddingLeft: 20,
                      }}
                    >
                      {renderApplicableSection(cap)}
                      {renderUsesSection(cap)}
                    </div>
                  )}
                </div>
              );
            })
        )}
        </div>
        <div className="diagnostic-operations-catalog">
          <div className="diagnostic-operations-catalog-header">
            <ExperimentOutlined />
            <span className="diagnostic-operations-catalog-title">
              Analytical Operations
            </span>
            <Dropdown
              trigger={["click"]}
              visible={operationFilterOpen}
              onVisibleChange={setOperationFilterOpen}
              overlay={
                <div
                  className="diagnostic-operations-filter-dropdown"
                  onMouseDown={(event) => event.stopPropagation()}
                >
                  <Input
                    size="small"
                    allowClear
                    autoFocus
                    placeholder="Search strategy"
                    value={operationFilter}
                    onChange={(event) => setOperationFilter(event.target.value)}
                  />
                </div>
              }
            >
              <Tooltip title="Search strategy" placement="left">
                <span
                  role="button"
                  tabIndex={0}
                  aria-label="Search strategy"
                  className={`ant-table-filter-trigger diagnostic-operations-filter-trigger${
                    operationFilter.trim() ? " active" : ""
                  }`}
                >
                  <SearchOutlined />
                </span>
              </Tooltip>
            </Dropdown>
            <Tooltip title="Add custom strategy" placement="left">
              <span
                className="business-view-action-btn"
                role="button"
                tabIndex={0}
                aria-label="Add custom strategy"
                onClick={(event) => {
                  event.stopPropagation();
                  addCustomStrategy();
                }}
              >
                <PlusOutlined />
              </span>
            </Tooltip>
          </div>
          <div className="diagnostic-operations-catalog-body">
            {operationsError ? (
              <p className="diagnostic-operations-error">{operationsError}</p>
            ) : null}
            <div className="diagnostic-operations-list">
              {filteredOperations.some(isCustomStrategy) ? (
                <div className="diagnostic-operation-group">
                  <div
                    className="diagnostic-operation-group-title"
                    role="button"
                    tabIndex={0}
                    onClick={() => toggleCategory("custom")}
                  >
                    <span className="business-view-entry-caret">
                      {categoryIsOpen("custom") ? (
                        <CaretDownOutlined />
                      ) : (
                        <CaretRightOutlined />
                      )}
                    </span>
                    Custom
                  </div>
                  {categoryIsOpen("custom") ? (
                    <div className="diagnostic-operation-group-items">
                      {filteredOperations
                        .filter(isCustomStrategy)
                        .map((op) => renderCustomStrategy(op))}
                    </div>
                  ) : null}
                </div>
              ) : null}
              {groupedOperations
                .filter(([, ops]) => ops.some((op) => !isCustomStrategy(op)))
                .map(([category, ops]) => {
                  const open = categoryIsOpen(category);
                  const catalogOps = ops.filter((op) => !isCustomStrategy(op));
                  return (
                    <div key={category} className="diagnostic-operation-group">
                      <div
                        className="diagnostic-operation-group-title"
                        role="button"
                        tabIndex={0}
                        onClick={() => toggleCategory(category)}
                      >
                        <span className="business-view-entry-caret">
                          {open ? <CaretDownOutlined /> : <CaretRightOutlined />}
                        </span>
                        {formatCategory(category)}
                      </div>
                      {open ? (
                        <div className="diagnostic-operation-group-items">
                          {catalogOps.map((op) => (
                            <AnalyticalOperationDragSource
                              key={op.operation}
                              operation={op}
                            >
                              <Tooltip
                                title={operationTooltipTitle(op)}
                                {...strategyTooltipProps}
                              >
                                <div className="diagnostic-operation-item">
                                  <span className="diagnostic-operation-name">
                                    {displayStrategyName(op.operation)}
                                  </span>
                                </div>
                              </Tooltip>
                            </AnalyticalOperationDragSource>
                          ))}
                        </div>
                      ) : null}
                    </div>
                  );
                })}
              {operationsLoading ? (
                <p className="business-view-agent-topic-fields-empty">Loading…</p>
              ) : null}
              {!operationsLoading && !filteredOperations.length ? (
                <p className="business-view-agent-topic-fields-empty">
                  No operations found
                </p>
              ) : null}
            </div>
          </div>
        </div>
      </div>
      <Modal
        title={
          customStrategyDraft?.isNew ? "Add custom strategy" : "Edit custom strategy"
        }
        visible={Boolean(customStrategyDraft)}
        onCancel={() => setCustomStrategyDraft(null)}
        onOk={saveCustomStrategy}
        okText="Save"
        okButtonProps={{
          disabled: !String(customStrategyDraft?.operation || "").trim(),
        }}
        destroyOnClose
        width={480}
      >
        {customStrategyDraft ? (
          <div className="diagnostic-custom-strategy-modal">
            <label className="diagnostic-custom-strategy-field">
              <span>Name</span>
              <Input
                value={customStrategyDraft.operation}
                placeholder="Strategy name"
                onChange={(event) =>
                  setCustomStrategyDraft((prev) =>
                    prev ? { ...prev, operation: event.target.value } : prev,
                  )
                }
              />
            </label>
            <label className="diagnostic-custom-strategy-field">
              <span>Description</span>
              <Input.TextArea
                autoSize={{ minRows: 3, maxRows: 6 }}
                value={customStrategyDraft.description}
                placeholder="What this strategy answers"
                onChange={(event) =>
                  setCustomStrategyDraft((prev) =>
                    prev ? { ...prev, description: event.target.value } : prev,
                  )
                }
              />
            </label>
            <StrategyNameList
              label="Inputs"
              values={customStrategyDraft.inputs}
              onChange={(inputs) =>
                setCustomStrategyDraft((prev) =>
                  prev ? { ...prev, inputs } : prev,
                )
              }
            />
          </div>
        ) : null}
      </Modal>
    </div>
  );
}

export default DiagnosticCapabilitiesPanel;
