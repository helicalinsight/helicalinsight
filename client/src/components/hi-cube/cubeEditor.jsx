import {
  Typography,
  Space,
  Divider,
  Table,
  Input,
  Checkbox,
  List,
  Dropdown,
  Switch,
  Menu,
  Tooltip,
} from "antd";
import { useCubeEditorBindings } from "./cubeEditorContext";
import { memo, useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import {
  cubeTableDataSource,
  handleCubeTitleEdit,
  handleHeader,
  handleHierarachyTitle,
  handleVisibleCheckStatus,
  uniqueCubeTitle,
} from "./helperMethods";
import {
  SearchOutlined,
  CloseOutlined,
  CaretUpOutlined,
  CaretDownOutlined,
  EditOutlined,
  DeleteOutlined,
  EllipsisOutlined,
  CaretRightOutlined,
  InfoCircleOutlined,
  PlusOutlined,
  MinusOutlined,
  NumberOutlined,
  FileTextOutlined,
  CalendarOutlined,
  CheckOutlined,
  AppstoreOutlined,
  TagsOutlined,
} from "@ant-design/icons";
import { useDrop } from "react-dnd";
import { VList } from "virtuallist-antd";
import {
  cubeAggregationMenu,
  cubeMeasureMenu,
  cubeSortMenu,
  handleCubeEditorHeader,
  AgentRightSubmenuTextField,
  CubeFieldsMenu,
  applyFieldMeasureRoleChange,
} from "./cubeConstants";
import { dateTypes } from "../hi-reports/utils/constants";
import {
  setCubeFieldsData,
  setCubeHeaderData,
  setCubeIndeterminate,
  setCubeSearchedColumn,
  setFieldSearchText,
  setVisibleCheckValue,
  updateFieldValues,
  setCubeInintialList,
  setCubeMode,
  setCubeState,
} from "../../redux/actions/cube.actions";
import Highlighter from "react-highlight-words";
import { fileBrowserActions } from "../../redux/actions";
import { getCubeEditorTooltipText } from "./cubeEditorTooltips";
import { parseCommaSeparatedToArray, formatArrayAsCommaSeparated } from "./cubeSemanticFields";
import { CubeSemanticTypeSelect } from "./cubeSemanticTypeField";
import {
  createManualMetricChild,
  createColumnChildFromDropRecord,
  getFieldSourceTableTooltip,
} from "../hi-agent/utils/agent-cube-bridge";
import {
  AgentCubeFieldDragSource,
  AgentTopicDropZone,
  buildTopicChildFromMetadata,
} from "./agentBusinessViewDnd";
import {
  childMatchesBusinessTopic,
  groupAssignedFieldsForTopicDisplay,
  resolveBusinessTopicDropKeys,
} from "./agentBusinessTopicMembership";
import { ToolbarIconButton } from "../common/toolbar-icon-button";

const { Title, Paragraph } = Typography;
function EditableNameParagraph({
  className,
  displayName,
  editTooltip,
  onNameChange,
  children,
  ...rest
}) {
  const titleText =
    displayName ||
    (typeof children === "string" ? children : "") ||
    "";
  const truncatedDisplay =
    titleText.length > 15 ? `${titleText.slice(0, 15)}...` : titleText;

  return (
    <Paragraph
      {...rest}
      className={className}
      editable={{
        tooltip: editTooltip,
        text: titleText,
        icon: <EditOutlined role="button" />,
        onChange: onNameChange,
        autoSize: { minRows: 2, maxRows: 3 },
      }}
    >
      <Tooltip title={titleText} placement="top" mouseEnterDelay={0.3}>
        <span className="business-view-title-text">{truncatedDisplay}</span>
      </Tooltip>
    </Paragraph>
  );
}

function AgentBusinessContextMenu({
  value,
  placeholder,
  infoLabel,
  deleteInfoLabel,
  onChange,
  onDelete,
  renderDeleteMenuItem,
  variant,
}) {
  const [openKey, setOpenKey] = useState(null);
  const clearDescriptionTooltip = getCubeEditorTooltipText(
    "Clear field values",
    variant,
  );

  return (
    <Menu
      className="cube-bv-domain-description-menu"
      items={[
        renderDeleteMenuItem({ onDelete, deleteInfoLabel }),
        {
          key: "business-description",
          label: (
            <AgentRightSubmenuTextField
              fieldKey="business-description"
              label="Description"
              tooltipLabel={infoLabel}
              value={value ?? ""}
              placeholder={placeholder}
              clearFieldTooltip={clearDescriptionTooltip}
              openKey={openKey}
              onOpenKeyChange={setOpenKey}
              onChange={onChange}
            />
          ),
        },
      ]}
    />
  );
}

function CubeFieldInfo({ label, info }) {
  const { variant } = useCubeEditorBindings();
  const tooltipTitle =
    info ??
    (typeof label === "string" && label.trim()
      ? getCubeEditorTooltipText(label, variant)
      : "More information about this field.");

  return (
    <Tooltip
      title={<div style={{ whiteSpace: "pre-wrap" }}>{tooltipTitle}</div>}
      placement="top"
    >
      <InfoCircleOutlined className="cube-info-icon" />
    </Tooltip>
  );
}

function columnTitleWithInfo(title, infoLabel) {
  return (
    <span className="cube-column-title">
      {title}
      <CubeFieldInfo label={infoLabel || title} />
    </span>
  );
}

const normalizeCommaList = (nextValue) =>
  formatArrayAsCommaSeparated(parseCommaSeparatedToArray(nextValue ?? ""));

const CubeSemanticListInput = memo(function CubeSemanticListInput({
  value,
  recordKey,
  fieldName,
  disabled,
  placeholder,
  isHierarchyChild,
  hierarchyKey,
  normalize = normalizeCommaList,
}) {
  const { dispatch } = useCubeEditorBindings();
  const [localValue, setLocalValue] = useState(value ?? "");
  const isFocusedRef = useRef(false);

  useEffect(() => {
    if (!isFocusedRef.current) {
      setLocalValue(value ?? "");
    }
  }, [value, recordKey]);

  const commit = (nextValue) => {
    const normalized = normalize(nextValue ?? "");
    if (normalized !== (value ?? "")) {
      dispatch(
        updateFieldValues({
          updateName: fieldName,
          checkVal: normalized,
          recordKey,
          isHierarchyChild,
          hierarchyKey,
        }),
      );
    }
  };

  return (
    <Input
      disabled={disabled}
      value={localValue}
      placeholder={placeholder}
      onFocus={() => {
        isFocusedRef.current = true;
      }}
      onChange={(e) => setLocalValue(e.target.value)}
      onBlur={() => {
        isFocusedRef.current = false;
        commit(localValue);
      }}
      onPressEnter={() => commit(localValue)}
    />
  );
});

const { Search, TextArea } = Input;

function getNextAgentDomainName(entries = []) {
  let max = 0;
  entries.forEach((entry) => {
    const trimmed = String(entry.domain || "").trim();
    if (/^business domain$/i.test(trimmed)) {
      max = Math.max(max, 1);
      return;
    }
    const match = trimmed.match(/^business domain (\d+)$/i);
    if (match) {
      max = Math.max(max, Number(match[1]));
      return;
    }
    const legacy = trimmed.match(/^domain_(\d+)$/i);
    if (legacy) {
      max = Math.max(max, Number(legacy[1]));
    }
  });
  return max === 0 ? "business domain" : `business domain ${max + 1}`;
}

function getNextAgentTopicName(topics = []) {
  let max = 0;
  topics.forEach((topic) => {
    const trimmed = String(topic.name || "").trim();
    if (/^topic$/i.test(trimmed)) {
      max = Math.max(max, 1);
      return;
    }
    const match = trimmed.match(/^topic (\d+)$/i);
    if (match) {
      max = Math.max(max, Number(match[1]));
      return;
    }
    const businessTopic = trimmed.match(/^business topic(?: (\d+))?$/i);
    if (businessTopic) {
      max = Math.max(max, Number(businessTopic[1] || 1));
      return;
    }
    const legacy = trimmed.match(/^topic_(\d+)$/i);
    if (legacy) {
      max = Math.max(max, Number(legacy[1]));
    }
  });
  return max === 0 ? "topic" : `topic ${max + 1}`;
}

const DESCRIPTION_FIELD_UI = {
  cube: {
    label: "Description",
    infoLabel: "Cube Description",
    placeholder: "Cube description",
  },
  agent: {
    label: "Description",
    infoLabel: "Semantic Model Description",
    placeholder: "Description",
  },
};

export function CubeEditor({ showBusinessFields = true }) {
  const searchInput = useRef(null);
  const semanticMenuCommitsRef = useRef(new Map());
  const { cubeState, dispatch, variant } = useCubeEditorBindings();
  const isAgent = variant === "agent";
  const agentSemanticTypes = useSelector((store) => store.agent?.semanticTypes);
  const showAgentBusinessFields = isAgent && showBusinessFields;
  const descriptionFieldUi =
    DESCRIPTION_FIELD_UI[variant] || DESCRIPTION_FIELD_UI.cube;
  const {
    cubeCurrentState,
    headerData,
    fieldSearchText,
    cubeSearchedColumn,
    cubeFieldsData,
    cubeVisibleIndeterminate,
    isCubeVisibleCheck,
    cubeInitialList,
    cubeMode,
    isCubeTableModeNormal,
    cubeFieldsDataBackup,
  } = cubeState;
  let tableVirtualProps = {};
  const [collapsedEntries, setCollapsedEntries] = useState({});
  const toggleEntryCollapse = (key) =>
    setCollapsedEntries((prev) => ({ ...prev, [key]: !prev[key] }));

  const [collapsedTopics, setCollapsedTopics] = useState({});
  const topicCollapseKey = (entryKey, topicKey) => `${entryKey}::${topicKey}`;
  const toggleTopicCollapse = (entryKey, topicKey) => {
    const ck = topicCollapseKey(entryKey, topicKey);
    setCollapsedTopics((prev) => ({ ...prev, [ck]: !prev[ck] }));
  };
  const [collapsedHierarchies, setCollapsedHierarchies] = useState({});
  const hierarchyCollapseKey = (entryKey, topicKey, hierarchyKey) =>
    `${entryKey}::${topicKey}::${hierarchyKey}`;
  const toggleHierarchyCollapse = (entryKey, topicKey, hierarchyKey) => {
    const ck = hierarchyCollapseKey(entryKey, topicKey, hierarchyKey);
    setCollapsedHierarchies((prev) => ({
      ...prev,
      [ck]: !(prev[ck] ?? true),
    }));
  };
  const showBusinessViewRef = useRef(showAgentBusinessFields);
  showBusinessViewRef.current = showAgentBusinessFields;

  const [rightCollectedProps, drop] = useDrop(() => ({
    accept: ["metadataRowChild"],
    canDrop: () => !showBusinessViewRef.current,
    drop: (record) => {
      if (showBusinessViewRef.current) {
        return;
      }
      dispatch(
        setCubeFieldsData({
          mode: "setChild",
          child: createColumnChildFromDropRecord(record),
        }),
      );
    },
  }));

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    dispatch(setFieldSearchText(selectedKeys[0]));
    dispatch(setCubeSearchedColumn(dataIndex));
  };

  const handleReset = ({ confirm, dataIndex, clearFilters }) => {
    clearFilters();
    confirm();
    dispatch(setFieldSearchText(""));
    dispatch(setCubeSearchedColumn(dataIndex));
  };

  const getColumnSearchProps = (dataIndex) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => {
      return (
        <div
          style={{
            padding: 8,
          }}
        >
          <Search
            ref={searchInput}
            value={selectedKeys[0]}
            onChange={(e) =>
              setSelectedKeys(e.target.value ? [e.target.value] : [])
            }
            placeholder={`Search ${dataIndex}`}
            onSearch={(value) => {
              handleSearch(selectedKeys, confirm, dataIndex);
            }}
            enterButton={
              fieldSearchText ? (
                <CloseOutlined
                  onClick={(e) => {
                    handleReset({
                      selectedKeys,
                      confirm,
                      dataIndex,
                      clearFilters,
                    });
                    e.stopPropagation();
                  }}
                />
              ) : (
                <SearchOutlined />
              )
            }
          />
        </div>
      );
    },
    filterIcon: (filtered) => (
      <SearchOutlined
        style={{
          color: filtered ? "#1890ff" : undefined,
        }}
      />
    ),
    onFilter: (value, record) =>
      record[dataIndex].toString().toLowerCase().includes(value.toLowerCase()),
    onFilterDropdownVisibleChange: (visible) => {
      if (visible) {
        setTimeout(() => {
          return searchInput.current?.select();
        }, 100);
      }
    },
    render: (text) =>
      cubeSearchedColumn === dataIndex ? (
        <Highlighter
          highlightStyle={{
            backgroundColor: "#ffc069",
            padding: 0,
          }}
          searchWords={[fieldSearchText]}
          autoEscape
          textToHighlight={text ? text.toString() : ""}
        />
      ) : (
        text
      ),
  });

  if (variant === "agent") {
    tableVirtualProps = {};
  } else {
    tableVirtualProps = {
      scroll: { y: 540 },
      components: VList({
        height: 540,
        vid: "cube-table",
      }),
    };
  }

  const onChange = (e) => {
    dispatch(
      updateFieldValues({
        updateName: "visible__Check__",
        checkVal: e.target.checked,
      }),
    );
    dispatch(setVisibleCheckValue(e.target.checked));
    dispatch(setCubeIndeterminate(false));
  };

  const handleAddManualMetric = () => {
    dispatch(
      setCubeFieldsData({
        mode: "setChild",
        child: createManualMetricChild(cubeFieldsData.children),
      }),
    );
  };

  const handleAddBusinessEntry = () => {
    dispatch(
      setCubeFieldsData({
        mode: "addBusinessEntry",
        ...(isAgent
          ? {
              domain: getNextAgentDomainName(
                cubeFieldsData.businessViewEntries,
              ),
              topics: [],
            }
          : {}),
      }),
    );
  };

  const handleVisibleChange = ({ flag, record }) => {
    if (!flag) {
      semanticMenuCommitsRef.current.get(record.key)?.();
    }
    dispatch(
      updateFieldValues({
        updateName: "fieldsDropdownOpen",
        checkVal: flag,
        recordKey: record.key,
        isHierarchyChild: record.isHierarchyChild,
        hierarchyKey: record.parentKey,
      }),
    );
    // setOpen(flag);
  };
  const isHierarchyChildFieldLocked = (record) =>
    variant !== "agent" && record.isHierarchyChild;

  const toggleFieldMeasure = (record, checked) => {
    applyFieldMeasureRoleChange(
      dispatch,
      record,
      checked,
      variant,
      isAgent && agentSemanticTypes?.length ? agentSemanticTypes : undefined,
    );
  };

  const renderAgentMeasureIcon = (record) => {
    if (record.isHierarchy) return null;

    const isMeasure = Boolean(record.measure?.isMeasureCheck);
    const isLocked = isHierarchyChildFieldLocked(record);
    const dataType = record.dataType || "";
    // NumberOutlined only for measures. Dimensions use metadata type icons
    // (boolean/date/text); numeric dimensions use FileTextOutlined.
    const RoleIcon = isMeasure
      ? NumberOutlined
      : dataType === "boolean"
        ? CheckOutlined
        : dateTypes.includes(dataType)
          ? CalendarOutlined
          : FileTextOutlined;

    return (
      <Tooltip
        title={
          isMeasure
            ? "Measure (click to use as dimension)"
            : "Dimension (click to use as measure)"
        }
      >
        <RoleIcon
          className={[
            "cube-agent-measure-toggle",
            isMeasure && "is-active",
            isLocked && "is-disabled",
          ]
            .filter(Boolean)
            .join(" ")}
          onClick={(e) => {
            e.stopPropagation();
            if (isLocked) return;
            toggleFieldMeasure(record, !isMeasure);
          }}
        />
      </Tooltip>
    );
  };

  const renderAgentFieldsColumnTitle = () => (
    <span className="cube-column-title cube-column-title--agent-fields">
      Fields
      <CubeFieldInfo label="Fields" />
      <ToolbarIconButton
        title="Add Metric"
        placement="top"
        className="cube-add-metric-action cube-add-metric-action--inline"
        stopPropagation
        onClick={handleAddManualMetric}
      >
        <PlusOutlined className="cube-add-metric-icon" />
      </ToolbarIconButton>
    </span>
  );

  const renderCubeDimensionMeasureCell = (record) => (
    <div
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
      }}
    >
      <span style={{ fontSize: 11, color: "#888", marginRight: 4 }}>
        {record.measure?.isMeasureCheck ? "Measure" : "Dimension"}
      </span>
      <Switch
        disabled={isHierarchyChildFieldLocked(record)}
        checked={record.measure?.isMeasureCheck}
        size="small"
        onChange={(checked) => toggleFieldMeasure(record, checked)}
      />
      <Dropdown
        disabled={!record.measure?.isMeasureCheck}
        overlay={cubeMeasureMenu({
          dispatch,
          record,
          hierarchyData: cubeFieldsData.hierarchyData,
        })}
        trigger={["click"]}
      >
        <EllipsisOutlined
          className={`table-action-icon ${
            !record.measure?.isMeasureCheck && "table-action-icon-disabled"
          }`}
        />
      </Dropdown>
    </div>
  );
  const semanticListInputColumn = ({
    title,
    dataIndex,
    fieldName,
    placeholder,
    infoLabel,
    normalize,
  }) => ({
    title: (
      <span className="cube-column-title">
        {title}
        <CubeFieldInfo label={infoLabel || title} />
      </span>
    ),
    dataIndex,
    key: dataIndex,
    width: "11%",
    render: (_, record) =>
      !record.isHierarchy && (
        <CubeSemanticListInput
          key={`${record.key}-${fieldName}`}
          value={record[fieldName]}
          recordKey={record.key}
          fieldName={fieldName}
          disabled={isHierarchyChildFieldLocked(record)}
          placeholder={placeholder}
          isHierarchyChild={record.isHierarchyChild}
          hierarchyKey={record.parentKey}
          normalize={normalize}
        />
      ),
  });

  const handleSemanticTypeChange = (record, value) =>
    dispatch(
      updateFieldValues({
        updateName: "semanticType",
        checkVal: value || "",
        recordKey: record.key,
        isHierarchyChild: record.isHierarchyChild,
        hierarchyKey: record.parentKey,
      }),
    );

  const inputColumns = [
    ...(variant !== "agent"
      ? [
          {
            title: columnTitleWithInfo("Semantic Type"),
            dataIndex: "semantictype",
            key: "semantictype",
            width: "10%",
            render: (_, record) =>
              !record.isHierarchy && (
                <CubeSemanticTypeSelect
                  disabled={isHierarchyChildFieldLocked(record)}
                  value={record.semanticType}
                  onChange={(val) => handleSemanticTypeChange(record, val)}
                />
              ),
          },
          semanticListInputColumn({
            title: "Synonyms",
            dataIndex: "synonyms",
            fieldName: "synonyms",
            placeholder: "e.g. users, clients",
          }),
          semanticListInputColumn({
            title: "Topic",
            dataIndex: "topic",
            fieldName: "topic",
            placeholder: "e.g. Sales, Travel",
            infoLabel: "Topic",
          }),
        ]
      : []),
  ];
  const fieldsColumn = {
      title:
        isAgent && !showAgentBusinessFields
          ? renderAgentFieldsColumnTitle()
          : columnTitleWithInfo("Fields"),
      dataIndex: "fields",
      key: "fields",
      width: isCubeTableModeNormal ? "10%" : "20%",
      ...getColumnSearchProps("fields"),
      render: (text, record, i) => {
        const sourceTableTooltip = getFieldSourceTableTooltip(record);
        const fieldDropdown = (
          <Dropdown
            onVisibleChange={(flag) => handleVisibleChange({ flag, record })}
            visible={record.fieldsDropdownOpen}
            destroyPopupOnHide
            overlay={
              <CubeFieldsMenu
                dispatch={dispatch}
                record={record}
                hierarchyData={cubeFieldsData.hierarchyData}
                semanticCommitsRef={semanticMenuCommitsRef}
                menuVisible={Boolean(record.fieldsDropdownOpen)}
                semanticTypeOptions={
                  isAgent && agentSemanticTypes?.length
                    ? agentSemanticTypes
                    : undefined
                }
              />
            }
            trigger={["contextMenu"]}
          >
            <Paragraph
              className="row-title"
              editable={{
                tooltip: "Edit",
                text: record.fields || "",
                onChange: (value) => {
                  record.fields !== value &&
                    handleHierarachyTitle({
                      updateName: "rowTitle",
                      value,
                      recordKey: record.key,
                      isHierarchyChild: record.isHierarchyChild,
                      hierarchyKey: record.parentKey,
                      dispatch,
                      cubeFieldsData,
                    });
                },
              }}
            >
              {sourceTableTooltip ? (
                <Tooltip
                  title={sourceTableTooltip}
                  placement="top"
                  mouseEnterDelay={0.3}
                >
                  <span className="cube-field-source-tooltip-target">
                    {record.fields}
                  </span>
                </Tooltip>
              ) : (
                record.fields
              )}
            </Paragraph>
          </Dropdown>
        );
        const fieldCell = (
          <div className={isAgent ? "cube-agent-field-cell" : undefined}>
            {isAgent ? renderAgentMeasureIcon(record) : null}
            {fieldDropdown}
          </div>
        );

        if (isAgent && !showAgentBusinessFields) {
          return (
            <AgentCubeFieldDragSource record={record}>
              {fieldCell}
            </AgentCubeFieldDragSource>
          );
        }

        return fieldCell;
      },
  };

  const updateBusinessEntryField = (entryKey, field, value) => {
    dispatch(
      setCubeFieldsData({
        mode: "updateBusinessEntry",
        key: entryKey,
        field,
        value,
      }),
    );
  };

  const updateBusinessTopicField = (entryKey, topicKey, field, value) => {
    dispatch(
      setCubeFieldsData({
        mode: "updateBusinessTopic",
        key: entryKey,
        topicKey,
        field,
        value,
      }),
    );
  };

  const renderAgentDeleteMenuItem = ({
    key = "delete",
    onDelete,
    deleteInfoLabel = "Remove field",
  }) => ({
    key,
    label: (
      <span className="cube-bv-delete-menu-label">
        <span className="cube-fields-menu-action-label">Delete</span>
        <span
          className="cube-bv-delete-info"
          onClick={(e) => e.stopPropagation()}
          onMouseDown={(e) => e.stopPropagation()}
        >
          <CubeFieldInfo label={deleteInfoLabel} />
        </span>
      </span>
    ),
    onClick: ({ domEvent }) => {
      domEvent?.stopPropagation?.();
      onDelete?.();
    },
  });

  const renderAgentBusinessContextMenu = ({
    value,
    placeholder,
    infoLabel,
    deleteInfoLabel,
    onChange,
    onDelete,
  }) => (
    <AgentBusinessContextMenu
      value={value}
      placeholder={placeholder}
      infoLabel={infoLabel}
      deleteInfoLabel={deleteInfoLabel}
      onChange={onChange}
      onDelete={onDelete}
      renderDeleteMenuItem={renderAgentDeleteMenuItem}
      variant={variant}
    />
  );

  const renderAgentDeleteOnlyMenu = ({ onDelete, deleteInfoLabel }) => (
    <Menu
      className="cube-bv-domain-description-menu"
      items={[renderAgentDeleteMenuItem({ onDelete, deleteInfoLabel })]}
    />
  );

  const getFieldsForTopic = (entry, topic) => {
    const domainName = (entry.domain || "").trim();
    const topicName = (topic.name || "").trim();
    if (!domainName || !topicName) {
      return [];
    }
    const collectAssignableFields = (nodes = []) =>
      nodes.flatMap((child) => {
        if (child.isDelete) {
          return [];
        }
        if (child.isHierarchy) {
          return collectAssignableFields(child.children || []);
        }
        return [child];
      });
    return collectAssignableFields(cubeFieldsData.children || []).filter(
      (child) => childMatchesBusinessTopic(child, domainName, topicName),
    );
  };

  const assignFieldToTopic = (entry, topic, fieldItem) => {
    if (!fieldItem?.key) {
      return;
    }
    const domainName = (entry.domain || "").trim();
    const topicName = (topic.name || "").trim();
    if (!domainName || !topicName) {
      return;
    }
    const childKeys = resolveBusinessTopicDropKeys(
      fieldItem,
      cubeFieldsData.children || [],
    );
    if (!childKeys.length) {
      return;
    }
    dispatch(
      setCubeFieldsData({
        mode: "assignChildToBusinessTopic",
        childKeys,
        domain: domainName,
        topic: topicName,
      }),
    );
    setCollapsedEntries((prev) => ({ ...prev, [entry.key]: false }));
  };

  const dropMetadataOntoTopic = (entry, topic, metadataItem) => {
    const domainName = (entry.domain || "").trim();
    const topicName = (topic.name || "").trim();
    if (!domainName || !topicName || !metadataItem) {
      return;
    }
    dispatch(
      setCubeFieldsData({
        mode: "setChild",
        child: buildTopicChildFromMetadata(
          metadataItem,
          domainName,
          topicName,
        ),
      }),
    );
    setCollapsedEntries((prev) => ({ ...prev, [entry.key]: false }));
  };

  const clearFieldFromTopic = (fieldKey, entry, topic) => {
    dispatch(
      setCubeFieldsData({
        mode: "clearChildBusinessTopic",
        childKey: fieldKey,
        domain: (entry?.domain || "").trim(),
        topic: (topic?.name || "").trim(),
      }),
    );
  };

  const clearHierarchyFromTopic = (hierarchyGroup, entry, topic) => {
    const childKeys = (hierarchyGroup.children || [])
      .map((field) => field.key)
      .filter(Boolean);
    if (!childKeys.length) {
      return;
    }
    dispatch(
      setCubeFieldsData({
        mode: "clearChildBusinessTopic",
        childKeys,
        domain: (entry?.domain || "").trim(),
        topic: (topic?.name || "").trim(),
      }),
    );
  };

  const renderTopicAssignedFieldRow = (field, entry, topic) => {
    const sourceTableTooltip = getFieldSourceTableTooltip(field);
    const fieldName = (
      <span className="business-view-agent-topic-field-name">
        {field.fields || field.columnName || "Untitled field"}
      </span>
    );
    const namedContent = sourceTableTooltip ? (
      <Tooltip
        title={sourceTableTooltip}
        placement="top"
        mouseEnterDelay={0.3}
      >
        {fieldName}
      </Tooltip>
    ) : (
      fieldName
    );
    return (
      <div key={field.key} className="business-view-agent-topic-field-row">
        <Dropdown
          overlay={renderAgentDeleteOnlyMenu({
            onDelete: () => clearFieldFromTopic(field.key, entry, topic),
            deleteInfoLabel: "Remove field from topic",
          })}
          trigger={["contextMenu"]}
        >
          <span className="business-view-context-trigger">{namedContent}</span>
        </Dropdown>
      </div>
    );
  };

  const renderAgentBusinessTopicRow = (entry, topic) => {
    const displayName = topic.name?.trim() || "topic";
    const isTopicCollapsed = Boolean(
      collapsedTopics[topicCollapseKey(entry.key, topic.key)],
    );
    const assignedFields = getFieldsForTopic(entry, topic);
    const displayGroups = groupAssignedFieldsForTopicDisplay(
      assignedFields,
      cubeFieldsData.children || [],
    );
    const deleteTopic = () =>
      dispatch(
        setCubeFieldsData({
          mode: "deleteBusinessTopic",
          key: entry.key,
          topicKey: topic.key,
        }),
      );
    return (
      <AgentTopicDropZone
        key={topic.key}
        className="business-view-agent-topic-row"
        domainName={entry.domain}
        topicName={topic.name}
        onDropCubeField={(fieldItem) =>
          assignFieldToTopic(entry, topic, fieldItem)
        }
        onDropMetadataField={(metadataItem) =>
          dropMetadataOntoTopic(entry, topic, metadataItem)
        }
      >
        <div className="business-view-agent-topic-main">
          <span
            className="business-view-entry-caret business-view-agent-topic-caret"
            role="button"
            tabIndex={0}
            aria-expanded={!isTopicCollapsed}
            aria-label={isTopicCollapsed ? "Expand topic" : "Collapse topic"}
            onClick={(e) => {
              e.stopPropagation();
              toggleTopicCollapse(entry.key, topic.key);
            }}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                e.preventDefault();
                e.stopPropagation();
                toggleTopicCollapse(entry.key, topic.key);
              }
            }}
          >
            {isTopicCollapsed ? (
              <CaretRightOutlined />
            ) : (
              <CaretDownOutlined />
            )}
          </span>
          <Tooltip title="Topic" placement="top">
            <TagsOutlined
              className="business-view-type-icon business-view-type-icon--topic"
              aria-hidden
            />
          </Tooltip>
          <div className="business-view-entry-label">
            <span className="business-view-name-with-info">
              <Dropdown
                overlay={renderAgentBusinessContextMenu({
                  value: topic.description,
                  placeholder:
                    "Add topic description. Ex: Orders and shipment details.",
                  infoLabel: "Topic Description",
                  deleteInfoLabel: "Delete Topic",
                  onChange: (next) =>
                    updateBusinessTopicField(
                      entry.key,
                      topic.key,
                      "description",
                      next,
                    ),
                  onDelete: deleteTopic,
                })}
                trigger={["contextMenu"]}
                destroyPopupOnHide
              >
                <span className="business-view-context-trigger">
                  <EditableNameParagraph
                    className="business-view-topic-title row-title"
                    displayName={displayName}
                    editTooltip="Edit topic name"
                    onNameChange={(value) => {
                      const next = (value || "").trim();
                      if (next !== (topic.name || "").trim()) {
                        updateBusinessTopicField(
                          entry.key,
                          topic.key,
                          "name",
                          next,
                        );
                      }
                    }}
                  >
                    {displayName}
                  </EditableNameParagraph>
                </span>
              </Dropdown>
              <CubeFieldInfo label="Topic" />
            </span>
          </div>
        </div>
        {!isTopicCollapsed ? (
          <div className="business-view-agent-topic-fields">
            {displayGroups.length === 0 ? (
              <p className="business-view-agent-topic-fields-empty">
                Drop columns here
              </p>
            ) : (
              displayGroups.map((group) => {
                if (group.type === "hierarchy") {
                  const hierarchyKey = hierarchyCollapseKey(
                    entry.key,
                    topic.key,
                    group.key,
                  );
                  const isHierarchyCollapsed =
                    collapsedHierarchies[hierarchyKey] ?? true;
                  return (
                    <div
                      key={group.key}
                      className="business-view-agent-topic-hierarchy"
                    >
                      <div className="business-view-agent-topic-hierarchy-header">
                        <span
                          className="business-view-agent-topic-hierarchy-toggle"
                          role="button"
                          tabIndex={0}
                          aria-expanded={!isHierarchyCollapsed}
                          aria-label={
                            isHierarchyCollapsed
                              ? "Expand hierarchy"
                              : "Collapse hierarchy"
                          }
                          onClick={(e) => {
                            e.stopPropagation();
                            toggleHierarchyCollapse(
                              entry.key,
                              topic.key,
                              group.key,
                            );
                          }}
                          onKeyDown={(e) => {
                            if (e.key === "Enter" || e.key === " ") {
                              e.preventDefault();
                              e.stopPropagation();
                              toggleHierarchyCollapse(
                                entry.key,
                                topic.key,
                                group.key,
                              );
                            }
                          }}
                        >
                          {isHierarchyCollapsed ? (
                            <PlusOutlined />
                          ) : (
                            <MinusOutlined />
                          )}
                        </span>
                        <Dropdown
                          overlay={renderAgentDeleteOnlyMenu({
                            onDelete: () =>
                              clearHierarchyFromTopic(group, entry, topic),
                            deleteInfoLabel: "Remove field from topic",
                          })}
                          trigger={["contextMenu"]}
                        >
                          <span className="business-view-context-trigger business-view-agent-topic-hierarchy-name">
                            {group.name}
                          </span>
                        </Dropdown>
                      </div>
                      {!isHierarchyCollapsed ? (
                        <div className="business-view-agent-topic-hierarchy-children">
                          {group.children.map((field) =>
                            renderTopicAssignedFieldRow(field, entry, topic),
                          )}
                        </div>
                      ) : null}
                    </div>
                  );
                }
                return renderTopicAssignedFieldRow(group.field, entry, topic);
              })
            )}
          </div>
        ) : null}
      </AgentTopicDropZone>
    );
  };

  const renderAgentBusinessDomainTitle = (entry) => {
    const displayName = entry.domain?.trim() || "business domain";
    return (
      <Dropdown
        overlay={renderAgentBusinessContextMenu({
          value: entry.businessDescription,
          placeholder:
            "Add domain description. Ex: Sales performance and revenue.",
          infoLabel: "Domain Description",
          deleteInfoLabel: "Delete Domain",
          onChange: (next) =>
            updateBusinessEntryField(entry.key, "businessDescription", next),
          onDelete: () =>
            dispatch(
              setCubeFieldsData({
                mode: "deleteBusinessEntry",
                key: entry.key,
              }),
            ),
        })}
        trigger={["contextMenu"]}
        destroyPopupOnHide
      >
        <span className="business-view-context-trigger">
          <EditableNameParagraph
            className="business-view-entry-title row-title"
            displayName={displayName}
            editTooltip="Edit domain name"
            onNameChange={(value) => {
              const next = (value || "").trim();
              if (next !== (entry.domain || "").trim()) {
                updateBusinessEntryField(entry.key, "domain", next);
              }
            }}
          >
            {displayName}
          </EditableNameParagraph>
        </span>
      </Dropdown>
    );
  };

  const addAgentTopic = (entry) => {
    dispatch(
      setCubeFieldsData({
        mode: "addBusinessTopic",
        key: entry.key,
        name: getNextAgentTopicName(entry.topics),
      }),
    );
    setCollapsedEntries((prev) => ({ ...prev, [entry.key]: false }));
  };

  const renderBusinessDomainFields = (entry) => (
    <>
      <div className="business-view-node" style={{ marginLeft: 0 }}>
        <span className="business-view-node-label">
          Domain
          <CubeFieldInfo label="Domain" />
        </span>
        <div className="business-view-node-input">
          <Input
            size="small"
            value={entry.domain ?? ""}
            placeholder="Domain name"
            onChange={(e) =>
              updateBusinessEntryField(entry.key, "domain", e.target.value)
            }
          />
        </div>
      </div>
      {!isAgent ? (
        <div className="business-view-node" style={{ marginLeft: 16 }}>
          <span className="business-view-node-label">
            Description
            <CubeFieldInfo label="Cube Description" />
          </span>
          <div className="business-view-node-input">
            <TextArea
              size="small"
              autoSize={{ minRows: 1, maxRows: 4 }}
              value={entry.businessDescription ?? ""}
              placeholder="Description"
              onChange={(e) =>
                updateBusinessEntryField(
                  entry.key,
                  "businessDescription",
                  e.target.value,
                )
              }
            />
          </div>
        </div>
      ) : null}
      <div className="business-view-topics" style={{ marginLeft: 16 }}>
        <div className="business-view-topics-header">
          <span className="business-view-node-label">
            Topics
            <CubeFieldInfo label="Topic" />
          </span>
          <Tooltip title="Add topic" placement="top">
            <span
              className="business-view-add-topic"
              role="button"
              tabIndex={0}
              onClick={() =>
                dispatch(
                  setCubeFieldsData({
                    mode: "addBusinessTopic",
                    key: entry.key,
                  }),
                )
              }
              onKeyDown={(e) => {
                if (e.key === "Enter" || e.key === " ") {
                  e.preventDefault();
                  dispatch(
                    setCubeFieldsData({
                      mode: "addBusinessTopic",
                      key: entry.key,
                    }),
                  );
                }
              }}
            >
              <PlusOutlined />
            </span>
          </Tooltip>
        </div>
        {(entry.topics || []).map((topic) => {
  const isTopicCollapsed = Boolean(
    collapsedTopics[topicCollapseKey(entry.key, topic.key)],
  );
  return (
    <div className="business-view-topic-block" key={topic.key}>
      <div className="business-view-topic-header" style={{ marginLeft: 16 }}>
        <span
          className="business-view-topic-toggle"
          role="button"
          tabIndex={0}
          onClick={() => toggleTopicCollapse(entry.key, topic.key)}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              e.preventDefault();
              toggleTopicCollapse(entry.key, topic.key);
            }
          }}
        >
          {isTopicCollapsed ? <CaretRightOutlined /> : <CaretDownOutlined />}
          <Tooltip
            title={topic.name?.trim() || "Untitled topic"}
            placement="top"
            mouseEnterDelay={0.3}
          >
            <span className="business-view-topic-title">
              {topic.name?.trim() || "Untitled topic"}
            </span>
          </Tooltip>
        </span>
        {(entry.topics || []).length > 1 && (
          <Tooltip title="Delete topic" placement="left">
            <DeleteOutlined
              className="cube-bv-delete-icon business-view-topic-delete"
              onClick={() =>
                dispatch(
                  setCubeFieldsData({
                    mode: "deleteBusinessTopic",
                    key: entry.key,
                    topicKey: topic.key,
                  }),
                )
              }
            />
          </Tooltip>
        )}
      </div>
      {!isTopicCollapsed && (
        <>
          <div className="business-view-node" style={{ marginLeft: 32 }}>
            <span className="business-view-node-label">Topic</span>
            <div className="business-view-node-input">
              <Input
                size="small"
                value={topic.name ?? ""}
                placeholder="e.g. Sales"
                onChange={(e) =>
                  updateBusinessTopicField(
                    entry.key,
                    topic.key,
                    "name",
                    e.target.value,
                  )
                }
              />
            </div>
          </div>
          <div className="business-view-node" style={{ marginLeft: 32 }}>
            <span className="business-view-node-label">Description</span>
            <div className="business-view-node-input">
              <TextArea
                size="small"
                autoSize={{ minRows: 1, maxRows: 4 }}
                value={topic.description ?? ""}
                placeholder="Topic description"
                onChange={(e) =>
                  updateBusinessTopicField(
                    entry.key,
                    topic.key,
                    "description",
                    e.target.value,
                  )
                }
              />
            </div>
          </div>
        </>
      )}
    </div>
  );
})}
      </div>
    </>
  );

  const columns = [
        fieldsColumn,
        ...(!isCubeTableModeNormal
      ? [
          {
            title: columnTitleWithInfo("Sort"),
            dataIndex: "sort",
            key: "sort",
            render: (text, record, index) => {
              return (
                !record.isHierarchy && (
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Checkbox
                      disabled={!record.isDimensionCheck}
                      onChange={(e) => {
                        dispatch(
                          updateFieldValues({
                            updateName: "sort",
                            key: "isSortCheck",
                            value: e.target.checked,
                            recordKey: record.key,
                            isHierarchyChild: record.isHierarchyChild,
                            hierarchyKey: record.parentKey,
                          }),
                        );
                      }}
                      checked={record.sort?.isSortCheck}
                    />
                    <Dropdown
                      disabled={!record.sort?.isSortCheck}
                      overlay={cubeSortMenu({
                        dispatch,
                        record,
                        hierarchyData: cubeFieldsData.hierarchyData,
                      })}
                      trigger={["click"]}
                    >
                      <EllipsisOutlined
                        className={`table-action-icon ${
                          !record.sort?.isSortCheck &&
                          "table-action-icon-disabled"
                        }`}
                      />
                    </Dropdown>
                  </div>
                )
              );
            },
          },
          {
            title: columnTitleWithInfo("Aggregation"),
            dataIndex: "aggregation",
            key: "aggregation",
            render: (text, record, index) => {
              return (
                !record.isHierarchy && (
                  <div
                    style={{
                      display: "flex",
                      justifyContent: "space-between",
                      alignItems: "center",
                    }}
                  >
                    <Checkbox
                      disabled={
                        isHierarchyChildFieldLocked(record) ||
                        !record.measure?.isMeasureCheck
                      }
                      onChange={(e) => {
                        dispatch(
                          updateFieldValues({
                            updateName: "aggregation",
                            key: "isAggregationCheck",
                            value: e.target.checked,
                            recordKey: record.key,
                            isHierarchyChild: record.isHierarchyChild,
                            hierarchyKey: record.parentKey,
                          }),
                        );
                      }}
                      checked={record.aggregation?.isAggregationCheck}
                    />
                    <Dropdown
                      disabled={
                        isHierarchyChildFieldLocked(record) ||
                        !record.aggregation?.isAggregationCheck
                      }
                      overlay={cubeAggregationMenu({
                        dispatch,
                        record,
                        hierarchyData: cubeFieldsData.hierarchyData,
                      })}
                      trigger={["click"]}
                    >
                      <EllipsisOutlined
                        className={`table-action-icon ${
                          (isHierarchyChildFieldLocked(record) ||
                            !record.aggregation?.isAggregationCheck) &&
                          "table-action-icon-disabled"
                        }`}
                        onClick={(e) => {}}
                      />
                    </Dropdown>
                  </div>
                )
              );
            },
          },
          {
            title: columnTitleWithInfo("Partition"),
            dataIndex: "partition",
            key: "partition",
            render: (text, record, index) => {
              return (
                !record.isHierarchy && (
                  <Checkbox
                    disabled={isHierarchyChildFieldLocked(record)}
                    onChange={(e) => {
                      dispatch(
                        updateFieldValues({
                          updateName: "isPartitionCheck",
                          checkVal: e.target.checked,
                          recordKey: record.key,
                          isHierarchyChild: record.isHierarchyChild,
                          hierarchyKey: record.parentKey,
                        }),
                      );
                    }}
                    checked={record.isPartitionCheck}
                  />
                )
              );
            },
          },
        ]
      : [
          ...(isAgent
            ? []
            : [
                {
                  title: columnTitleWithInfo("Dimension/Measure"),
                  dataIndex: "dimensionMeasure",
                  key: "dimensionMeasure",
                  width: "8%",
                  render: (text, record) =>
                    !record.isHierarchy &&
                    renderCubeDimensionMeasureCell(record),
                },
              ]),
          ...inputColumns,
        ]),
  ];

  const tableDataSource = showAgentBusinessFields
    ? cubeFieldsData.businessViewEntries || []
    : cubeTableDataSource({ dataSource: cubeFieldsData.children });

  return (
    <div className="cube-editor" ref={drop}>
      {!isAgent && (
      <div className="cube-domain-description-bar">
          <div className="cube-domain-description-field">
            <span className="cube-domain-label">
              Domain <span className="cube-required-mark">*</span>
              <CubeFieldInfo label="Domain" />
            </span>
            <Input
              className="cube-domain-input"
              value={cubeFieldsData.domainName || ""}
              placeholder="Domain name"
              onChange={(e) =>
                dispatch(
                  updateFieldValues({
                    updateName: "domainName",
                    checkVal: e.target.value.split(",")[0],
                  }),
                )
              }
            />
          </div>
          <div className="cube-domain-description-field cube-description-field">
            <span className="cube-domain-label">
              {descriptionFieldUi.label}{" "}
              <span className="cube-required-mark">*</span>
              <CubeFieldInfo label={descriptionFieldUi.infoLabel} />
            </span>
            <TextArea
              className="cube-description-input"
              value={cubeFieldsData.cubeDescription || ""}
              placeholder={descriptionFieldUi.placeholder}
              rows={1}
              onChange={(e) =>
                dispatch(
                  updateFieldValues({
                    updateName: "cubeDescription",
                    checkVal: e.target.value,
                  }),
                )
              }
            />
          </div>
      </div>
      )}
      {showAgentBusinessFields ? (
        <div className="business-view-hierarchy">
          <div className="cube-business-view-toolbar">
            <ToolbarIconButton
              title="Add domain"
              placement="left"
              ariaLabel="Add domain"
              onClick={handleAddBusinessEntry}
            >
              <PlusOutlined className="cube-add-metric-icon" />
            </ToolbarIconButton>
          </div>
          <div className="business-view-hierarchy-body">
            {tableDataSource.length === 0 ? (
              <div className="business-view-empty">
                <p className="business-view-empty-title">
                  Business View organizes your data into domains and topics
                </p>
                <p className="business-view-empty-desc">
                  Domains group related business areas. Topics inside a domain
                  hold fields that belong together. Click + to add your first
                  domain.
                </p>
              </div>
            ) : (
              tableDataSource.map((entry) => {
                const isCollapsed = Boolean(collapsedEntries[entry.key]);
                const topicCount = (entry.topics || []).length;
                return (
                  <div
                    className={`business-view-entry${
                      isAgent ? " business-view-entry--agent" : ""
                    }`}
                    key={entry.key}
                  >
                    <div className="business-view-entry-header">
                      {!isAgent ? (
                        <span
                          className="business-view-entry-toggle"
                          role="button"
                          tabIndex={0}
                          onClick={() => toggleEntryCollapse(entry.key)}
                          onKeyDown={(e) => {
                            if (e.key === "Enter" || e.key === " ") {
                              e.preventDefault();
                              toggleEntryCollapse(entry.key);
                            }
                          }}
                        >
                          {isCollapsed ? (
                            <CaretRightOutlined />
                          ) : (
                            <CaretDownOutlined />
                          )}
                          <Tooltip
                            title={entry.domain?.trim() || "Untitled domain"}
                            placement="top"
                            mouseEnterDelay={0.3}
                          >
                            <span className="business-view-entry-title">
                              {entry.domain?.trim() || "Untitled domain"}
                            </span>
                          </Tooltip>
                        </span>
                      ) : (
                        <>
                          <span
                            className="business-view-entry-caret"
                            role="button"
                            tabIndex={0}
                            aria-expanded={!isCollapsed}
                            aria-label={
                              isCollapsed ? "Expand domain" : "Collapse domain"
                            }
                            onClick={() => toggleEntryCollapse(entry.key)}
                            onKeyDown={(e) => {
                              if (e.key === "Enter" || e.key === " ") {
                                e.preventDefault();
                                toggleEntryCollapse(entry.key);
                              }
                            }}
                          >
                            {isCollapsed ? (
                              <CaretRightOutlined />
                            ) : (
                              <CaretDownOutlined />
                            )}
                          </span>
                          <Tooltip title="Business Domain" placement="top">
                            <AppstoreOutlined
                              className="business-view-type-icon business-view-type-icon--domain"
                              aria-hidden
                            />
                          </Tooltip>
                          <div className="business-view-entry-label business-view-entry-label--with-add">
                            {renderAgentBusinessDomainTitle(entry)}
                            <Tooltip title="Add topic" placement="top">
                              <span
                                className="business-view-action-btn business-view-action-btn--primary business-view-add-topic-beside-edit"
                                role="button"
                                tabIndex={0}
                                aria-label="Add topic"
                                onClick={(e) => {
                                  e.stopPropagation();
                                  addAgentTopic(entry);
                                }}
                                onKeyDown={(e) => {
                                  if (e.key === "Enter" || e.key === " ") {
                                    e.preventDefault();
                                    addAgentTopic(entry);
                                  }
                                }}
                              >
                                <PlusOutlined />
                              </span>
                            </Tooltip>
                            <CubeFieldInfo label="Business Domain" />
                          </div>
                        </>
                      )}
                      {!isAgent && (
                        <Tooltip title="Delete" placement="top">
                          <DeleteOutlined
                            className="cube-bv-delete-icon business-view-entry-delete"
                            onClick={() =>
                              dispatch(
                                setCubeFieldsData({
                                  mode: "deleteBusinessEntry",
                                  key: entry.key,
                                }),
                              )
                            }
                          />
                        </Tooltip>
                      )}
                    </div>
                    {isAgent && !isCollapsed && (
                      <div className="business-view-agent-topics">
                        {topicCount === 0 ? (
                          <p className="business-view-agent-topics-empty">
                            No topics yet — use + to add one
                          </p>
                        ) : (
                          (entry.topics || []).map((topic) =>
                            renderAgentBusinessTopicRow(entry, topic),
                          )
                        )}
                      </div>
                    )}
                    {!isAgent && !isCollapsed && (
                      <div className="business-view-entry-fields">
                        {renderBusinessDomainFields(entry)}
                      </div>
                    )}
                  </div>
                );
              })
            )}
          </div>
        </div>
      ) : (
        <Table
          className="schedule-table"
          columns={columns}
          dataSource={tableDataSource}
          {...tableVirtualProps}
          pagination={false}
          size="small"
        />
      )}
      {!showAgentBusinessFields && (
        <div className="cube-editor-drop-surface" aria-hidden="true" />
      )}
    </div>
  );
}