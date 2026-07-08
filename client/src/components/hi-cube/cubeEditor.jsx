import {
  Typography,
  Button,
  Space,
  Divider,
  Table,
  Input,
  Checkbox,
  List,
  Dropdown,
  Switch,
  Select,
  Tooltip,
} from "antd";
import { useCubeEditorBindings } from "./cubeEditorContext";
import { memo, useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";
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
} from "@ant-design/icons";
import { useDrop } from "react-dnd";
import { VList } from "virtuallist-antd";
import {
  cubeAggregationMenu,
  cubeMeasureMenu,
  cubeSortMenu,
  handleCubeEditorHeader,
  CubeFieldsMenu,
  cubeEditorMeasureData,
} from "./cubeConstants";
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
import { SEMANTIC_TYPES } from "../hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";
import { getCubeEditorTooltipText } from "./cubeEditorTooltips";
import { useAgentName } from "../common/agent-name-context";
import { parseCommaSeparatedToArray, formatArrayAsCommaSeparated } from "./cubeSemanticFields";
import { createManualMetricChild } from "../hi-agent/utils/agent-cube-bridge";

const { Title, Paragraph } = Typography;

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

function columnTitleWithInfo(title) {
  return (
    <span className="cube-column-title">
      {title}
      <CubeFieldInfo label={title} />
    </span>
  );
}

const CubeSemanticListInput = memo(function CubeSemanticListInput({
  value,
  recordKey,
  fieldName,
  disabled,
  placeholder,
  isHierarchyChild,
  hierarchyKey,
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
    const normalized = formatArrayAsCommaSeparated(
      parseCommaSeparatedToArray(nextValue ?? ""),
    );
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

const { Option } = Select;
const { Search, TextArea } = Input;

const DESCRIPTION_FIELD_UI = {
  cube: {
    label: "Description",
    infoLabel: "Cube Description",
    placeholder: "Cube description",
  },
  agent: {
    label: "Description",
    infoLabel: "Agent Description",
    placeholder: "Description",
  },
};

export function CubeEditor() {
  const searchInput = useRef(null);
  const semanticMenuCommitsRef = useRef(new Map());
  const { cubeState, dispatch, variant } = useCubeEditorBindings();
  const { agentName, onAgentNameChange } = useAgentName();
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
  const [rightCollectedProps, drop] = useDrop(() => ({
    accept: "metadataRowChild",
    drop: (record) => {
      let key = uuidv4();
      dispatch(
        setCubeFieldsData({
          mode: "setChild",
          child: {
            key,
            columnId: record.columnId,
            fields: record.alias,
            tableId: record.tableId,
            fieldsDropdownOpen: false,
            isDimensionCheck: ["date", "dateTime", "text"].includes(
              record.dataType,
            ),
            measure: {
              isMeasureCheck: record.dataType === "numeric",
              DataType:
                record.dataType === "numeric"
                  ? cubeEditorMeasureData[0].dataType
                  : "",
              Format:
                record.dataType === "numeric"
                  ? cubeEditorMeasureData[0].format[0]
                  : "",
            },
            isVisible: true,
            sort: {
              isSortCheck: ["date", "dateTime", "text"].includes(
                record.dataType,
              ),
              value: ["date", "dateTime", "text"].includes(record.dataType)
                ? "Ascending"
                : "",
            },
            aggregation: {
              isAggregationCheck: record.dataType === "numeric",
              value: record.dataType === "numeric" ? "Sum" : "",
            },
            isPartitionCheck: false,
            table: {
              name: record.tableName,
              alias: record.tableAlias,
              ...record.dataSource,
            },
            column: {
              defaultFunction: record.defaultFunction,
            },
            columnName: record.columnName,
            dataType: record.dataType,
            type: record.type,
            agentSource: {
              kind: "column",
              table: record.tableName,
              columnName: record.columnName,
            },
            semanticType: "",
            synonyms: "",
            topic: "",
            formula: "",
            filter: "",
            example: "",
            description: "",
          },
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
    tableVirtualProps = {
      scroll: { y: 430, x: "max-content" },
    };
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
  const semanticListInputColumn = ({
    title,
    dataIndex,
    fieldName,
    placeholder,
    infoLabel,
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
        />
      ),
  });

  const inputColumns = [
    {
      title: columnTitleWithInfo("Semantic Type"),
      dataIndex: "semantictype",
      key: "semantictype",
      width: "10%",
      render: (_, record) =>
        !record.isHierarchy && (
          <Select
            style={{ width: "100%" }}
            disabled={isHierarchyChildFieldLocked(record)}
            value={record.semanticType || undefined}
            placeholder="Semantic type"
            allowClear
            onChange={(val) =>
              dispatch(
                updateFieldValues({
                  updateName: "semanticType",
                  checkVal: val || "",
                  recordKey: record.key,
                  isHierarchyChild: record.isHierarchyChild,
                  hierarchyKey: record.parentKey,
                }),
              )
            }
          >
            {SEMANTIC_TYPES.filter(Boolean).map((opt) => (
              <Option key={opt} value={opt}>
                {opt}
              </Option>
            ))}
          </Select>
        ),
    },
    ...(variant !== "agent"
      ? [
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
  const columns = [
    {
      title: columnTitleWithInfo("Fields"),
      dataIndex: "fields",
      key: "fields",
      width: isCubeTableModeNormal ? "10%" : "20%",
      ...getColumnSearchProps("fields"),
      render: (text, record, i) => (
        <Dropdown
          onVisibleChange={(flag) => handleVisibleChange({ flag, record })}
          visible={record.fieldsDropdownOpen}
          overlay={
            <CubeFieldsMenu
              dispatch={dispatch}
              record={record}
              hierarchyData={cubeFieldsData.hierarchyData}
              semanticCommitsRef={semanticMenuCommitsRef}
            />
          }
          trigger={["contextMenu"]}
        >
          <Paragraph
            className="row-title"
            editable={{
              // editing: true,
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
            {record.fields}
          </Paragraph>
        </Dropdown>
      ),
    },
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
          {
            title: columnTitleWithInfo("Dimension/Measure"),
            dataIndex: "dimensionMeasure",
            key: "dimensionMeasure",
            width: "8%",
            render: (text, record) =>
              !record.isHierarchy && (
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
                    onChange={(checked) => {
                      dispatch(
                        updateFieldValues({
                          updateName: "measure",
                          key: "isMeasureCheck",
                          value: checked,
                          recordKey: record.key,
                          isHierarchyChild: record.isHierarchyChild,
                          hierarchyKey: record.parentKey,
                        }),
                      );
                      dispatch(
                        updateFieldValues({
                          updateName: "isDimensionCheck",
                          checkVal: !checked,
                          recordKey: record.key,
                          isHierarchyChild: record.isHierarchyChild,
                          hierarchyKey: record.parentKey,
                        }),
                      );
                    }}
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
                        !record.measure?.isMeasureCheck &&
                        "table-action-icon-disabled"
                      }`}
                    />
                  </Dropdown>
                </div>
              ),
          },
          ...inputColumns,
        ]),
  ];

  return (
    <div className="cube-editor" ref={drop}>
      <div
        className={`cube-domain-description-bar${
          variant === "agent" ? " cube-domain-description-bar--agent" : ""
        }`}
      >
        {variant === "agent" && (
          <div className="cube-domain-description-field cube-agent-name-field">
            <span className="cube-domain-label">
              Agent
              <CubeFieldInfo label="Agent" />
            </span>
            <Input
              className="cube-domain-input"
              value={agentName || ""}
              placeholder="Agent_1"
              onChange={(e) => onAgentNameChange?.(e.target.value)}
            />
          </div>
        )}
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
                  checkVal: e.target.value,
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
        {variant === "agent" && (
          <div className="cube-domain-description-field cube-topic-field">
            <span className="cube-domain-label">
              Topic
              <CubeFieldInfo label="Agent Topic" />
            </span>
            <Input
              className="cube-topic-input"
              value={cubeFieldsData.cubeTopic || ""}
              placeholder="e.g. Sales, Travel"
              onChange={(e) =>
                dispatch(
                  updateFieldValues({
                    updateName: "cubeTopic",
                    checkVal: e.target.value,
                  }),
                )
              }
              onBlur={(e) => {
                const normalized = formatArrayAsCommaSeparated(
                  parseCommaSeparatedToArray(e.target.value ?? ""),
                );
                if (normalized !== (cubeFieldsData.cubeTopic || "")) {
                  dispatch(
                    updateFieldValues({
                      updateName: "cubeTopic",
                      checkVal: normalized,
                    }),
                  );
                }
              }}
            />
          </div>
        )}
      </div>
      {variant === "agent" && (
        <div className="cube-agent-fields-toolbar">
          <Button type="primary" size="small" onClick={handleAddManualMetric}>
            Add Metric
          </Button>
        </div>
      )}
      <Table
        className="schedule-table"
        columns={columns}
        dataSource={cubeTableDataSource({
          dataSource: cubeFieldsData.children,
        })}
        {...tableVirtualProps}
        pagination={false}
        size="small"
      />
      <div className="cube-editor-drop-surface" aria-hidden="true" />
    </div>
  );
}