import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { Dropdown, Input, Menu, Modal, Tag, Tooltip, Typography } from "antd";
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
} from "@ant-design/icons";
import {
  AgentTopicDropZone,
  AnalyticalOperationDragSource,
} from "../../../../hi-cube/agentBusinessViewDnd";
import { AgentRightSubmenuTextField } from "../../../../hi-cube/cubeConstants";
import instantBI, {
  uriConfig,
} from "../../../../../base/requests/instantbi.requests";
import "./diagnostic-capabilities-panel.scss";

const { Paragraph, Text } = Typography;

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

const measureNameFromEntry = (entry) => {
  if (typeof entry === "string") return entry.trim();
  if (entry && typeof entry === "object") {
    return String(
      entry.measure || entry.name || entry.fields || "",
    ).trim();
  }
  return "";
};

const emptyInputMap = (keys = []) =>
  Object.fromEntries(
    (keys || []).map((key) => [String(key).trim(), []]).filter(([key]) => key),
  );

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
      requiredInputs: emptyInputMap(catalog.requiredInputs),
      optionalInputs: emptyInputMap(catalog.optionalInputs),
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
    "requiredInputs",
    "optionalInputs",
  ]);
  const requiredKeys =
    Array.isArray(entry.requiredInputs) && entry.requiredInputs.length
      ? entry.requiredInputs
      : catalog.requiredInputs || [];
  const optionalKeys =
    Array.isArray(entry.optionalInputs) && entry.optionalInputs.length
      ? entry.optionalInputs
      : catalog.optionalInputs || [];
  const requiredInputs = emptyInputMap(requiredKeys);
  const optionalInputs = emptyInputMap(optionalKeys);
  Object.entries(entry).forEach(([key, value]) => {
    if (reserved.has(key) || value == null) return;
    const items = coerceInputList(value);
    if (!items.length) return;
    if (Object.prototype.hasOwnProperty.call(requiredInputs, key)) {
      requiredInputs[key] = uniqueByLower([
        ...coerceInputList(requiredInputs[key]),
        ...items,
      ]);
    } else if (Object.prototype.hasOwnProperty.call(optionalInputs, key)) {
      optionalInputs[key] = uniqueByLower([
        ...coerceInputList(optionalInputs[key]),
        ...items,
      ]);
    } else if ((catalog.requiredInputs || []).includes(key)) {
      requiredInputs[key] = items;
    } else {
      optionalInputs[key] = items;
    }
  });
  return {
    key: entry.key || buildUseEntryKey({ analyticalOperation: opName }, idx),
    analyticalOperation: opName,
    requiredInputs,
    optionalInputs,
  };
};

const normalizeCapabilities = (list = [], operationsByName = {}) =>
  (Array.isArray(list) ? list : []).map((item, idx) => ({
    key: `cap-${idx}-${Date.now()}`,
    capability: String(item?.capability || `capability ${idx + 1}`),
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
    applicableTo: normalizeApplicableTo(cap.applicableTo),
    uses: (cap.uses || [])
      .map((useEntry) => {
        const analyticalOperation = String(
          useEntry.analyticalOperation || "",
        ).trim();
        if (!analyticalOperation) return null;
        const next = { analyticalOperation };
        Object.entries(useEntry.requiredInputs || {}).forEach(([key, value]) => {
          const persisted = persistInputValue(value);
          if (persisted != null) next[key] = persisted;
        });
        Object.entries(useEntry.optionalInputs || {}).forEach(([key, value]) => {
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
  requiredInputs: ["measure"],
  optionalInputs: [],
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
    requiredInputs: uniqueByLower(entry?.requiredInputs || []),
    optionalInputs: uniqueByLower(entry?.optionalInputs || []),
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
  required = false,
  values = [],
  domainName,
  topicName,
  onAppend,
  onRemove,
}) {
  const [draft, setDraft] = useState("");
  const commitDraft = () => {
    const text = draft.trim();
    if (!text) return;
    onAppend?.(text);
    setDraft("");
  };
  const onDrop = (item) => onAppend?.(getDroppedName(item));
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
          {label}
          {required ? (
            <span className="diagnostic-input-required-mark">*</span>
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
          <Input
            size="small"
            bordered={false}
            value={draft}
            placeholder={
              values.length
                ? "Drop or type another"
                : `Drop field or type ${label}`
            }
            onChange={(event) => setDraft(event.target.value)}
            onPressEnter={(event) => {
              event.preventDefault();
              commitDraft();
            }}
            onBlur={commitDraft}
          />
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
  const [operations, setOperations] = useState([]);
  const [operationsLoading, setOperationsLoading] = useState(false);
  const [operationsError, setOperationsError] = useState("");
  const [operationFilter, setOperationFilter] = useState("");
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
          return {
            ...useEntry,
            key: useEntry.key || buildUseEntryKey(useEntry, idx),
            requiredInputs: {
              ...emptyInputMap(catalog.requiredInputs),
              ...(useEntry.requiredInputs || {}),
            },
            optionalInputs: {
              ...emptyInputMap(catalog.optionalInputs),
              ...(useEntry.optionalInputs || {}),
            },
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
            requiredInputs: emptyInputMap(catalog.requiredInputs),
            optionalInputs: emptyInputMap(catalog.optionalInputs),
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

  const setUseInputValues = (capKey, useKey, group, inputKey, values) => {
    const nextValues = uniqueByLower(coerceInputList(values));
    updateCap(capKey, (c) => ({
      uses: (c.uses || []).map((useEntry) => {
        if (useEntry.key !== useKey) return useEntry;
        return {
          ...useEntry,
          [group]: {
            ...(useEntry[group] || {}),
            [inputKey]: nextValues,
          },
        };
      }),
    }));
  };

  const appendUseInputValue = (capKey, useKey, group, inputKey, name) => {
    const nextName = String(name || "").trim();
    if (!nextName) return;
    updateCap(capKey, (c) => ({
      uses: (c.uses || []).map((useEntry) => {
        if (useEntry.key !== useKey) return useEntry;
        return {
          ...useEntry,
          [group]: {
            ...(useEntry[group] || {}),
            [inputKey]: uniqueByLower([
              ...coerceInputList(useEntry?.[group]?.[inputKey]),
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
    setter((prev) => ({ ...prev, [key]: !prev[key] }));

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
        ...(op.requiredInputs || []),
        ...(op.optionalInputs || []),
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
      </Dropdown>
    </AnalyticalOperationDragSource>
  );

  const renderInputSlot = (cap, useEntry, group, inputKey) => (
    <DiagnosticInputSlot
      key={`${useEntry.key}:${group}:${inputKey}`}
      label={formatLabel(inputKey)}
      required={group === "requiredInputs"}
      values={coerceInputList(useEntry?.[group]?.[inputKey])}
      domainName={cap.capability}
      topicName={`${useEntry.analyticalOperation}.${inputKey}`}
      onAppend={(name) =>
        appendUseInputValue(cap.key, useEntry.key, group, inputKey, name)
      }
      onRemove={(name) =>
        setUseInputValues(
          cap.key,
          useEntry.key,
          group,
          inputKey,
          coerceInputList(useEntry?.[group]?.[inputKey]).filter(
            (item) => item.toLowerCase() !== String(name).toLowerCase(),
          ),
        )
      }
    />
  );

  const renderUseRow = (cap, useEntry) => {
    const useKey = useEntry.key;
    const isCollapsed = collapsedUses[useKey] ?? false;
    const requiredKeys = Object.keys(useEntry.requiredInputs || {});
    const optionalKeys = Object.keys(useEntry.optionalInputs || {});
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
              setCollapsedUses((p) => ({ ...p, [useKey]: !isCollapsed }));
            }}
          >
            {isCollapsed ? <PlusOutlined /> : <MinusOutlined />}
          </span>
          <Dropdown
            overlay={deleteOnlyMenu(() => removeUse(cap.key, useKey))}
            trigger={["contextMenu"]}
          >
            <span className="business-view-context-trigger business-view-agent-topic-hierarchy-name">
              <AimOutlined className="diagnostic-use-op-icon" />
              <span>{displayStrategyName(useEntry.analyticalOperation)}</span>
            </span>
          </Dropdown>
        </div>
        {!isCollapsed && (
          <div className="diagnostic-use-inputs">
            {requiredKeys.length || optionalKeys.length ? (
              <div className="diagnostic-input-group-fields diagnostic-use-input-row">
                {requiredKeys.map((inputKey) =>
                  renderInputSlot(cap, useEntry, "requiredInputs", inputKey),
                )}
                {optionalKeys.map((inputKey) =>
                  renderInputSlot(cap, useEntry, "optionalInputs", inputKey),
                )}
              </div>
            ) : (
              <p className="business-view-agent-topic-fields-empty">
                No configured inputs for this operation
              </p>
            )}
          </div>
        )}
      </div>
    );
  };

  const renderApplicableSection = (cap) => {
    const sectionKey = `${cap.key}:applicableTo`;
    const isCollapsed = Boolean(collapsedSections[sectionKey]);
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
          <Tooltip title="Applicable Measures" placement="top">
            <CalculatorOutlined className="business-view-type-icon business-view-type-icon--topic" />
          </Tooltip>
          <div
            className="business-view-entry-label business-view-entry-label--with-add"
            style={{ display: "inline-flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
          >
            <span className="business-view-topic-title row-title">
              Applicable Measures
            </span>
            <Tooltip title="Applicable measures this capability explains" placement="top">
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
    const isCollapsed = Boolean(collapsedSections[sectionKey]);
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
          <Tooltip title="Uses" placement="top">
            <BranchesOutlined className="business-view-type-icon business-view-type-icon--topic" />
          </Tooltip>
          <div
            className="business-view-entry-label business-view-entry-label--with-add"
            style={{ display: "inline-flex", alignItems: "center", gap: 6, flexWrap: "nowrap" }}
          >
            <span className="business-view-topic-title row-title">Uses</span>
            <Tooltip
              title="Drag analytical operations here. Required and optional inputs expand under each operation."
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
              Each capability has a name, applicable measures, and analytical
              operations it uses. Drag an operation into Uses to configure its
              required and optional inputs. Click + to add a capability.
            </p>
          </div>
        ) : (
          <>
            <p className="diagnostic-section-note">
              Capability name, applicable measures, and analytical operations
              with strategy inputs.
            </p>
            {capabilities.map((cap) => {
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
                    <Tooltip title="Capability" placement="top">
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
                        overlay={deleteOnlyMenu(() =>
                          commit((prev) =>
                            prev.filter((c) => c.key !== cap.key),
                          ),
                        )}
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
                      <Tooltip title="Capability name" placement="top">
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
            })}
          </>
        )}
        </div>
        <div className="diagnostic-operations-catalog">
          <div className="diagnostic-operations-catalog-header">
            <ExperimentOutlined />
            <span className="diagnostic-operations-catalog-title">
              Analytical Operations
            </span>
            <Text type="secondary" className="diagnostic-operations-count">
              {operationsLoading ? "Loading…" : `${allOperations.length}`}
            </Text>
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
            <Input
              size="small"
              allowClear
              placeholder="Filter operations"
              value={operationFilter}
              onChange={(e) => setOperationFilter(e.target.value)}
              className="diagnostic-operations-filter"
            />
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
                                title={
                                  <div>
                                    <div>
                                      {op.description ||
                                        displayStrategyName(op.operation)}
                                    </div>
                                    {op.requiredInputs?.length ? (
                                      <div>
                                        Required: {op.requiredInputs.map(formatLabel).join(", ")}
                                      </div>
                                    ) : null}
                                    {op.optionalInputs?.length ? (
                                      <div>
                                        Optional: {op.optionalInputs.map(formatLabel).join(", ")}
                                      </div>
                                    ) : null}
                                  </div>
                                }
                                placement="left"
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
              label="Required inputs"
              values={customStrategyDraft.requiredInputs}
              onChange={(requiredInputs) =>
                setCustomStrategyDraft((prev) =>
                  prev ? { ...prev, requiredInputs } : prev,
                )
              }
            />
            <StrategyNameList
              label="Optional inputs"
              values={customStrategyDraft.optionalInputs}
              onChange={(optionalInputs) =>
                setCustomStrategyDraft((prev) =>
                  prev ? { ...prev, optionalInputs } : prev,
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
