import { Select, TreeSelect, Dropdown, Tooltip } from "antd";
import { useEffect, useMemo, useState } from "react";
import { useSelector } from "react-redux";
import { CloseOutlined, RightOutlined, DownOutlined } from "@ant-design/icons";
import { SEMANTIC_TYPES } from "../hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";
import { useCubeEditorBindings } from "./cubeEditorContext";

const { Option } = Select;

const SEMANTIC_NODE_SEPARATOR = "::";

const normalizeSemanticKey = (value) =>
  String(value ?? "").trim().toLowerCase();

export const findSemanticTypeOption = (groups, storedValue) => {
  if (!storedValue) {
    return null;
  }
  const raw = String(storedValue).trim();
  const separatorIndex = raw.indexOf(SEMANTIC_NODE_SEPARATOR);
  const lookupValue =
    separatorIndex >= 0
      ? raw.slice(separatorIndex + SEMANTIC_NODE_SEPARATOR.length)
      : raw;
  const normalizedLookup = normalizeSemanticKey(lookupValue);
  for (const group of groups || []) {
    const groupKey = group.value || group.label;
    const match = (group.options || []).find(
      (opt) =>
        normalizeSemanticKey(opt.value) === normalizedLookup ||
        normalizeSemanticKey(opt.label) === normalizedLookup,
    );
    if (match) {
      return { groupKey, option: match };
    }
  }
  return null;
};

export const resolveSemanticTypeValue = (preferred, semanticTypeOptions) => {
  if (!preferred) {
    return preferred;
  }
  if (!isGroupedSemanticTypes(semanticTypeOptions)) {
    return preferred;
  }
  const found = findSemanticTypeOption(semanticTypeOptions, preferred);
  return found?.option.label ?? preferred;
};

export const ROLE_SEMANTIC_TYPE_DEFAULTS = {
  agent: { measure: "Number", dimension: "Text" },
  cube: { measure: "numeric", dimension: "text" },
};

export const getDefaultSemanticTypeForRole = (
  isMeasure,
  variant = "cube",
  semanticTypeOptions,
) => {
  const defaults =
    ROLE_SEMANTIC_TYPE_DEFAULTS[variant] || ROLE_SEMANTIC_TYPE_DEFAULTS.cube;
  const preferred = isMeasure ? defaults.measure : defaults.dimension;
  return resolveSemanticTypeValue(preferred, semanticTypeOptions);
};

export const isGroupedSemanticTypes = (types) =>
  Array.isArray(types) &&
  types.some((item) => item && typeof item === "object" && item.options);

const highlightMatch = (text, searchText) => {
  const value = String(text ?? "");
  const term = String(searchText ?? "").trim();
  const index = term ? value.toLowerCase().indexOf(term.toLowerCase()) : -1;
  if (index < 0) {
    return value;
  }
  return (
    <>
      {value.slice(0, index)}
      <span className="cube-semantic-type-highlight">
        {value.slice(index, index + term.length)}
      </span>
      {value.slice(index + term.length)}
    </>
  );
};

export const buildSemanticTreeData = (groups, searchText = "") =>
  (groups || []).map((group) => {
    const groupKey = group.value || group.label;
    return {
      title: highlightMatch(group.label, searchText),
      titleText: group.label,
      value: `grp${SEMANTIC_NODE_SEPARATOR}${groupKey}`,
      selectable: false,
      children: (group.options || []).map((opt) => ({
        title: highlightMatch(opt.label, searchText),
        titleText: opt.label,
        value: `${groupKey}${SEMANTIC_NODE_SEPARATOR}${opt.value}`,
      })),
    };
  });

const getMatchingSemanticGroupKeys = (groups, searchText) => {
  const term = normalizeSemanticKey(searchText);
  if (!term) {
    return [];
  }
  return (groups || [])
    .filter(
      (group) =>
        normalizeSemanticKey(group.label).includes(term) ||
        (group.options || []).some((opt) =>
          normalizeSemanticKey(opt.label).includes(term),
        ),
    )
    .map((group) => `grp${SEMANTIC_NODE_SEPARATOR}${group.value || group.label}`);
};

export const toSemanticNodeValue = (groups, storedValue) => {
  const found = findSemanticTypeOption(groups, storedValue);
  if (found) {
    return `${found.groupKey}${SEMANTIC_NODE_SEPARATOR}${found.option.value}`;
  }
  return storedValue || undefined;
};

export const fromSemanticNodeValue = (nodeValue) => {
  if (!nodeValue) return "";
  const separatorIndex = nodeValue.indexOf(SEMANTIC_NODE_SEPARATOR);
  return separatorIndex >= 0
    ? nodeValue.slice(separatorIndex + SEMANTIC_NODE_SEPARATOR.length)
    : nodeValue;
};
// Prefer label for storage so raw JSON matches defaults like Text/Number (not person.first_name)
export const toStoredSemanticType = (nodeValue, groups) => {
  if (!nodeValue) return "";
  const found = findSemanticTypeOption(groups, nodeValue);
  if (found?.option?.label) {
    return found.option.label;
  }
  return fromSemanticNodeValue(nodeValue);
};

export const getSemanticTypeDisplayLabel = (storedValue, semanticTypeOptions) => {
  if (!storedValue) {
    return "";
  }
  if (isGroupedSemanticTypes(semanticTypeOptions)) {
    const found = findSemanticTypeOption(semanticTypeOptions, storedValue);
    if (found) {
      return found.option.label;
    }
  }
  return storedValue;
};

const getAgentFieldPreview = (value, emptyLabel = "Add") => {
  const trimmed = (value ?? "").trim();
  if (!trimmed) {
    return { text: emptyLabel, isEmpty: true };
  }
  return {
    text: trimmed.length > 22 ? `${trimmed.slice(0, 22)}…` : trimmed,
    isEmpty: false,
  };
};

/**
 * Shared Semantic Type control (same JSON field: semanticType).
 * Grouped types use TreeSelect (category → leaf).
 */
export function CubeSemanticTypeSelect({
  value,
  onChange,
  disabled = false,
  style,
  getPopupContainer,
  size = "small",
}) {
  const { variant } = useCubeEditorBindings();
  const agentSemanticTypes = useSelector((store) => store.agent?.semanticTypes);
  const semanticTypeOptions =
    variant === "agent" && agentSemanticTypes?.length
      ? agentSemanticTypes
      : SEMANTIC_TYPES;
  const grouped = isGroupedSemanticTypes(semanticTypeOptions);
  const popupContainer =
    getPopupContainer || ((trigger) => trigger.parentElement);

  const [expandedKeys, setExpandedKeys] = useState([]);
  const [searchText, setSearchText] = useState("");

  const semanticTreeData = useMemo(
    () =>
      grouped ? buildSemanticTreeData(semanticTypeOptions, searchText) : [],
    [grouped, semanticTypeOptions, searchText],
  );

  const treeValue = useMemo(
    () =>
      grouped
        ? toSemanticNodeValue(semanticTypeOptions, value)
        : undefined,
    [grouped, semanticTypeOptions, value],
  );

  const treeExpandedKeys = useMemo(() => {
    if (!grouped || !treeValue) {
      return [];
    }
    const separatorIndex = treeValue.indexOf(SEMANTIC_NODE_SEPARATOR);
    if (separatorIndex < 0) {
      return [];
    }
    const groupKey = treeValue.slice(0, separatorIndex);
    return [`grp${SEMANTIC_NODE_SEPARATOR}${groupKey}`];
  }, [grouped, treeValue]);

  useEffect(() => {
    if (searchText) {
      setExpandedKeys(getMatchingSemanticGroupKeys(semanticTypeOptions, searchText));
      return;
    }
    setExpandedKeys(treeExpandedKeys);
  }, [searchText, semanticTypeOptions, treeExpandedKeys]);

  if (grouped) {
    return (
      <TreeSelect
        className="cube-semantic-type-select"
        size={size}
        style={style || { width: "100%" }}
        disabled={disabled}
        value={treeValue}
        placeholder="Semantic type"
        allowClear
        showSearch
        searchValue={searchText}
        filterTreeNode={(input, node) =>
          normalizeSemanticKey(node?.titleText).includes(
            normalizeSemanticKey(input),
          )
        }
        treeData={semanticTreeData}
        treeExpandedKeys={expandedKeys}
        onTreeExpand={setExpandedKeys}
        onSearch={(nextSearchText) => setSearchText(nextSearchText)}
        treeDefaultExpandAll={false}
        dropdownClassName="cube-semantic-type-tree-dropdown"
        dropdownStyle={{
          maxHeight: 280,
          overflow: "auto",
          zIndex: 1100,
        }}
        dropdownMatchSelectWidth={false}
        listHeight={240}
        getPopupContainer={popupContainer}
        onChange={(val) =>
          onChange(toStoredSemanticType(val, semanticTypeOptions))
        }
      />
    );
  }

  return (
    <Select
      className="cube-semantic-type-select"
      size={size}
      style={style || { width: "100%" }}
      disabled={disabled}
      value={value || undefined}
      placeholder="Semantic type"
      allowClear
      showSearch
      optionFilterProp="label"
      dropdownClassName="cube-semantic-type-dropdown"
      dropdownMatchSelectWidth={false}
      dropdownStyle={{ zIndex: 1100 }}
      listHeight={220}
      getPopupContainer={popupContainer}
      onChange={(val) => onChange(val || "")}
    >
      {semanticTypeOptions.filter(Boolean).map((opt) => (
        <Option key={opt} value={opt} label={opt}>
          {opt}
        </Option>
      ))}
    </Select>
  );
}

export function AgentSemanticTypeSubmenuRow({
  label,
  value,
  disabled = false,
  clearFieldTooltip,
  onChange,
  openKey = null,
  onOpenKeyChange,
}) {
  const fieldKey = "semanticType";
  const open = openKey === fieldKey;
  const { variant } = useCubeEditorBindings();
  const agentSemanticTypes = useSelector((store) => store.agent?.semanticTypes);
  const semanticTypeOptions =
    variant === "agent" && agentSemanticTypes?.length
      ? agentSemanticTypes
      : SEMANTIC_TYPES;
  const displayLabel = getSemanticTypeDisplayLabel(value, semanticTypeOptions);
  const preview = getAgentFieldPreview(displayLabel);
  const stop = (e) => e.stopPropagation();

  const setOpen = (nextOpen) => {
    if (!onOpenKeyChange) {
      return;
    }
    onOpenKeyChange(nextOpen ? fieldKey : null);
  };

  const overlay = (
    <div
      className="cube-agent-semantic-type-submenu-panel"
      onClick={stop}
      onMouseDown={stop}
    >
      {!disabled && value ? (
        <div className="cube-agent-accordion-editor-toolbar">
          <span />
          <Tooltip title={clearFieldTooltip} placement="top">
            <CloseOutlined
              className="cube-agent-accordion-clear"
              onClick={(e) => {
                stop(e);
                onChange("");
              }}
            />
          </Tooltip>
        </div>
      ) : null}
      <CubeSemanticTypeSelect
        disabled={disabled}
        value={value ?? ""}
        onChange={onChange}
        getPopupContainer={() => document.body}
        style={{ width: 220 }}
      />
    </div>
  );

  return (
    <div className="cube-agent-semantic-type-submenu-field">
      <Dropdown
        overlay={open ? overlay : <div />}
        trigger={["click"]}
        placement="rightTop"
        visible={open}
        onVisibleChange={(next) => {
          if (disabled) {
            return;
          }
          setOpen(next);
        }}
        overlayClassName="cube-agent-semantic-type-submenu-overlay"
        getPopupContainer={() => document.body}
      >
        <div
          className={`cube-agent-format-menu-row cube-agent-text-submenu-row${
            open ? " is-open" : ""
          }`}
          onClick={stop}
          onMouseDown={stop}
          role="button"
          tabIndex={0}
          aria-expanded={open}
        >
          <span className="cube-agent-accordion-label">
            {label ?? (
              <span className="cube-fields-menu-action-label">Semantic Type</span>
            )}
          </span>
          <span className="cube-agent-format-trigger">
            <span
              className={`cube-agent-format-value${
                preview.isEmpty ? " cube-agent-format-value--empty" : ""
              }`}
            >
              {preview.text}
            </span>
            {open ? (
              <DownOutlined className="cube-agent-format-arrow" />
            ) : (
              <RightOutlined className="cube-agent-format-arrow" />
            )}
          </span>
        </div>
      </Dropdown>
    </div>
  );
}
