import React, { useState } from "react";
import { Drawer, Button, Select } from "antd";
import {
  CaretDownOutlined,
  CaretRightOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import {
  allTables,
  allTopics,
  highlightJSON,
  uid,
  SEMANTIC_TYPES,
  COLUMN_TYPES,
  AddItemDrawer,
} from "./semantic-metadata-utils";
import {
  TextField,
  TextareaField,
  SelectField,
  StatTile,
  ChipsEditor,
  InfoTooltip,
} from "./semantic-metadata-fields";
import { SEMANTIC_TOOLTIPS } from "./semantic-metadata-tooltips";
import { useDrawerWithDraft } from "./useDrawer";

const { Option } = Select;

function SectionActionHeader({ action }) {
  return action ? (
    <div className="me-section-head">
      <div />
      {action}
    </div>
  ) : null;
}

function LabelWithTooltip({ label }) {
  return (
    <div className="me-label-row">
      <span className="me-label-text">{label}</span>
      <InfoTooltip label={label} />
    </div>
  );
}

function FormLabel({ label }) {
  return (
    <label>
      <LabelWithTooltip label={label} />
    </label>
  );
}

//collapsible
function CollapsibleCard({ title, actions, children, defaultExpanded = true }) {
  const [isExpanded, setIsExpanded] = useState(defaultExpanded);
  return (
    <div
      className={`me-card me-card-collapsible${
        isExpanded ? "" : " me-card-collapsed"
      }`}
    >
      <div
        className="me-card-head me-card-head-toggle"
        role="button"
        tabIndex={0}
        aria-expanded={isExpanded}
        onClick={() => setIsExpanded((prev) => !prev)}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            setIsExpanded((prev) => !prev);
          }
        }}
      >
        <div className="me-card-title">
          <span className="me-card-toggle-icon" aria-hidden="true">
            {isExpanded ? <CaretDownOutlined /> : <CaretRightOutlined />}
          </span>
          {title}
        </div>
        {actions && (
          <div className="me-card-actions" onClick={(e) => e.stopPropagation()}>
            {actions}
          </div>
        )}
      </div>
      {isExpanded && <div className="me-card-body">{children}</div>}
    </div>
  );
}

function RemovableCard({
  title,
  confirmLabel,
  onRemove,
  children,
  defaultExpanded = true,
}) {
  return (
    <CollapsibleCard
      title={title}
      defaultExpanded={defaultExpanded}
      actions={
        <CloseOutlined
          onClick={() => {
            if (window.confirm(`Delete ${confirmLabel}?`)) onRemove();
          }}
        />
      }
    >
      {children}
    </CollapsibleCard>
  );
}

function ItemListSection({ items, emptyText, addAction, renderItem }) {
  return (
    <section className="me-section active">
      <SectionActionHeader action={addAction} />
      {!items.length && <div className="me-empty">{emptyText}</div>}
      {items.map((item, index) => renderItem(item, index))}
    </section>
  );
}
//column
function ColumnsEditorTable({ table, onUpdate }) {
  const columns = table.columns || [];
  const updateColumn = (colIndex, patch) => {
    const next = [...columns];
    next[colIndex] = { ...next[colIndex], ...patch };
    table.columns = next;
    onUpdate();
  };

  const removeColumn = (colIndex) => {
    table.columns = columns.filter((_, i) => i !== colIndex);
    onUpdate();
  };

  return (
    <div
      style={{
        overflow: "auto",
        border: "1px solid var(--me-border)",
        borderRadius: 10,
      }}
    >
      <table className="me-col-table">
        <thead>
          <tr>
            <th>Column</th>
            <th>Semantic type</th>
            <th>Type</th>
            <th>Description</th>
            <th style={{ width: 46 }} />
          </tr>
        </thead>
        <tbody>
          {!columns.length && (
            <tr>
              <td
                colSpan={5}
                style={{
                  padding: 18,
                  textAlign: "center",
                  color: "var(--me-text-muted)",
                }}
              >
                No columns yet.
              </td>
            </tr>
          )}
          {columns?.map((col, colIndex) => (
            <tr key={colIndex}>
              <td>
                <input
                  className="mono"
                  value={col.column_name ?? ""}
                  placeholder="column_name"
                  onChange={(e) =>
                    updateColumn(colIndex, { column_name: e.target.value })
                  }
                />
              </td>
              <td>
                <Select
                  style={{ width: "100%" }}
                  value={col.semantic_type || undefined}
                  placeholder="-"
                  allowClear
                  onChange={(val) =>
                    updateColumn(colIndex, { semantic_type: val || undefined })
                  }
                >
                  {SEMANTIC_TYPES.filter(Boolean).map((opt) => (
                    <Option key={opt} value={opt}>
                      {opt}
                    </Option>
                  ))}
                </Select>
              </td>
              <td>
                <Select
                  style={{ width: "100%" }}
                  value={col.type || undefined}
                  placeholder="-"
                  allowClear
                  onChange={(val) =>
                    updateColumn(colIndex, { type: val || undefined })
                  }
                >
                  {COLUMN_TYPES.filter(Boolean).map((opt) => (
                    <Option key={opt} value={opt}>
                      {opt}
                    </Option>
                  ))}
                </Select>
              </td>
              <td>
                <input
                  value={col.description ?? ""}
                  placeholder="description"
                  onChange={(e) =>
                    updateColumn(colIndex, { description: e.target.value })
                  }
                />
              </td>
              <td>
                <CloseOutlined onClick={() => removeColumn(colIndex)} />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function ColumnsEditorSubSection({ tableObj, onAddColumn, onUpdate }) {
  return (
    <>
      <div className="me-divider" />
      <div className="me-row">
        <div>Columns</div>
        <span className="me-tag table">
          {(tableObj.columns || []).length} column(s)
        </span>
        <div className="me-spacer" />
        <Button type="primary" onClick={onAddColumn}>
          Add Column
        </Button>
      </div>
      <div style={{ height: 10 }} />
      <ColumnsEditorTable table={tableObj} onUpdate={onUpdate} />
    </>
  );
}

// table & metric

function TableFormFields({ table, onChange }) {
  return (
    <>
      <div className="me-grid cols-3">
        <TextField
          label="Database table"
          mono
          value={table.database_table}
          onChange={(v) => onChange({ ...table, database_table: v })}
        />
        <TextField
          label="Unique identifier (PK)"
          mono
          value={table.unique_identifier_of_table || ""}
          onChange={(v) =>
            onChange({ ...table, unique_identifier_of_table: v })
          }
        />
        <TextField
          label="Refresh frequency"
          value={table.refresh_frequency || ""}
          onChange={(v) => onChange({ ...table, refresh_frequency: v })}
        />
      </div>
      <div style={{ height: 10 }} />
      <div className="me-grid cols-2">
        <div className="me-field">
          <FormLabel label="Topic name(s)" />
          <ChipsEditor
            items={table.dimension_name || []}
            kind="topic"
            placeholder="Add a topic "
            scrollable
            onChange={(next) => onChange({ ...table, dimension_name: next })}
          />
        </div>
        <TextareaField
          label="Description"
          info={SEMANTIC_TOOLTIPS["Table Description"]}
          value={table.description || ""}
          onChange={(v) => onChange({ ...table, description: v })}
        />
      </div>
    </>
  );
}

function MetricFormFields({ metric, tables, onChange }) {
  return (
    <>
      <div className="me-grid cols-2">
        <TextField
          label="Metric name"
          mono
          value={metric.metric}
          onChange={(v) => onChange({ ...metric, metric: v })}
        />
        <TextField
          label="Description"
          info={SEMANTIC_TOOLTIPS["Metric Description"]}
          value={metric.description}
          onChange={(v) => onChange({ ...metric, description: v })}
        />
      </div>
      <div style={{ height: 10 }} />
      <div className="me-grid">
        <TextareaField
          rows={1}
          label="Formula"
          value={metric.formula}
          onChange={(v) => onChange({ ...metric, formula: v })}
        />
        <TextareaField
          rows={1}
          label="Filter (optional)"
          value={metric.filter || ""}
          onChange={(v) => onChange({ ...metric, filter: v })}
        />
      </div>
      <div style={{ height: 10 }} />
      <div className="me-field">
        <FormLabel label="Source Tables" />
        <ChipsEditor
          items={metric.tables || []}
          kind="table"
          suggestions={tables}
          placeholder="Add a table "
          scrollable
          onChange={(next) => onChange({ ...metric, tables: next })}
        />
      </div>
    </>
  );
}

function MetricItemCard({ metric, idx, tables, onChange, onRemove }) {
  return (
    <RemovableCard
      title={
        <>
          <span className="me-tag metric">{idx + 1}.Metric</span>
          <span>{metric.metric || "(unnamed)"}</span>
        </>
      }
      confirmLabel={`metric "${metric.metric || "(unnamed)"}"`}
      onRemove={onRemove}
    >
      <MetricFormFields metric={metric} tables={tables} onChange={onChange} />
    </RemovableCard>
  );
}

function TableItemCard({ table, idx, onChange, onRemove, onUpdate }) {
  const topicLabel = (table.dimension_name || []).join(", ") || "-";

  return (
    <RemovableCard
      title={
        <>
          <span className="me-tag table">
            {idx + 1}.{table.database_table || "table"}
          </span>
          <span>{topicLabel}</span>
        </>
      }
      confirmLabel={`table "${table.database_table || "(unnamed)"}"`}
      onRemove={onRemove}
    >
      <TableFormFields table={table} onChange={onChange} />
      <ColumnsEditorSubSection
        tableObj={table}
        onAddColumn={() =>
          onChange({
            ...table,
            columns: [
              ...(table.columns || []),
              { column_name: "new_column", semantic_type: "", description: "" },
            ],
          })
        }
        onUpdate={onUpdate}
      />
    </RemovableCard>
  );
}

export function OverviewSection({ data, setData }) {
  const metadataInfo = data.metadata_info.metadata;

  const patchMetadata = (patchFn) => {
    const next = {
      ...data,
      metadata_info: { ...data.metadata_info, metadata: { ...metadataInfo } },
    };
    patchFn(next.metadata_info.metadata);
    setData(next);
  };

  return (
    <section className="me-section active">
      <div className="me-card">
        <div className="me-grid">
          <div className="me-field">
            <FormLabel label="Business Domain(s)" />
            <ChipsEditor
              items={metadataInfo.domain}
              kind="topic"
              placeholder="Add a domain and press Enter"
              onChange={(next) => patchMetadata((m) => (m.domain = next))}
            />
          </div>
          <TextareaField
            label="Description"
            rows={3}
            value={metadataInfo.description}
            onChange={(v) => patchMetadata((m) => (m.description = v))}
          />
        </div>
      </div>
      <div className="me-card">
        <FormLabel label="Overview" />
        <div className="me-grid cols-3">
          <StatTile label="Tables" value={data.cube_metadata.length} />
          <StatTile
            label="Business Metrics"
            value={data.business_metrics.length}
          />
          <StatTile label="Synonym Sets" value={data.synonyms.length} />
          <StatTile
            label="Total Columns"
            value={data.cube_metadata.reduce(
              (acc, t) => acc + (t.columns || []).length,
              0,
            )}
          />
          <StatTile label="Topics" value={allTopics(data).length} />
          <StatTile
            label="Examples"
            value={data.examples.reduce(
              (acc, t) => acc + (t.eg || []).length,
              0,
            )}
          />
        </div>
      </div>
    </section>
  );
}

export function MetricsSection({ data, setData }) {
  const availableTables = allTables(data);
  const { isOpen, draft, setDraft, openDrawer, closeDrawer } =
    useDrawerWithDraft(() => ({
      metric: `new_metric_${uid()}`,
      description: "",
      formula: "",
      filter: "",
      tables: [],
    }));

  const updateMetricAtIndex = (idx, updatedMetric) => {
    const next = { ...data, business_metrics: [...data.business_metrics] };
    next.business_metrics[idx] = updatedMetric;
    setData(next);
  };

  const removeMetricAtIndex = (idx) =>
    setData({
      ...data,
      business_metrics: data.business_metrics.filter((_, j) => j !== idx),
    });

  const saveNewMetric = () => {
    if (draft) {
      setData({ ...data, business_metrics: [...data.business_metrics, draft] });
      closeDrawer();
    }
  };

  return (
    <>
      <ItemListSection
        items={data.business_metrics}
        emptyText='No business metrics yet. Click "Add Metric" above.'
        addAction={
          <Button type="primary" onClick={openDrawer}>
            Add Metric
          </Button>
        }
        renderItem={(metric, idx) => (
          <MetricItemCard
            key={idx}
            metric={metric}
            idx={idx}
            tables={availableTables}
            onChange={(updated) => updateMetricAtIndex(idx, updated)}
            onRemove={() => removeMetricAtIndex(idx)}
          />
        )}
      />
      <AddItemDrawer
        title="Add New Metric"
        open={isOpen}
        onClose={closeDrawer}
        onSave={saveNewMetric}
      >
        {draft && (
          <MetricFormFields
            metric={draft}
            tables={availableTables}
            onChange={setDraft}
          />
        )}
      </AddItemDrawer>
    </>
  );
}

export function TablesSection({ data, setData }) {
  const { isOpen, draft, setDraft, openDrawer, closeDrawer } =
    useDrawerWithDraft(() => ({
      dimension_name: ["New Topic"],
      unique_identifier_of_table: "",
      description: "",
      database_table: "new_table",
      refresh_frequency: "daily",
      columns: [],
    }));

  const updateTableAtIndex = (idx, updatedTable) => {
    const next = { ...data, cube_metadata: [...data.cube_metadata] };
    next.cube_metadata[idx] = updatedTable;
    setData(next);
  };

  const removeTableAtIndex = (idx) =>
    setData({
      ...data,
      cube_metadata: data.cube_metadata.filter((_, j) => j !== idx),
    });

  const saveNewTable = () => {
    if (draft) {
      setData({ ...data, cube_metadata: [...data.cube_metadata, draft] });
      closeDrawer();
    }
  };

  return (
    <>
      <ItemListSection
        items={data.cube_metadata}
        emptyText="No tables defined."
        addAction={
          <Button type="primary" onClick={openDrawer}>
            Add Table
          </Button>
        }
        renderItem={(table, idx) => (
          <TableItemCard
            key={idx}
            table={table}
            idx={idx}
            onChange={(updated) => updateTableAtIndex(idx, updated)}
            onUpdate={() =>
              setData((prev) => ({
                ...prev,
                cube_metadata: [...prev.cube_metadata],
              }))
            }
            onRemove={() => removeTableAtIndex(idx)}
          />
        )}
      />
      <AddItemDrawer
        title="Add New Table"
        open={isOpen}
        onClose={closeDrawer}
        onSave={saveNewTable}
        width={700}
      >
        {draft && (
          <>
            <TableFormFields table={draft} onChange={setDraft} />
            <ColumnsEditorSubSection
              tableObj={draft}
              onAddColumn={() =>
                setDraft((prev) => ({
                  ...prev,
                  columns: [
                    ...(prev.columns || []),
                    {
                      column_name: "new_column",
                      semantic_type: "",
                      description: "",
                    },
                  ],
                }))
              }
              onUpdate={() =>
                setDraft((prev) => ({
                  ...prev,
                  columns: [...(prev.columns || [])],
                }))
              }
            />
          </>
        )}
      </AddItemDrawer>
    </>
  );
}

export function SynonymsSection({ data, setData }) {
  const availableTables = allTables(data);
  const { isOpen, draft, setDraft, openDrawer, closeDrawer } =
    useDrawerWithDraft(() => ({
      database_table: "",
      synonyms: [],
    }));

  const updateSynonymAtIndex = (idx, patch) => {
    const next = { ...data, synonyms: [...data.synonyms] };
    next.synonyms[idx] = { ...next.synonyms[idx], ...patch };
    setData(next);
  };

  const saveNewSynonym = () => {
    if (draft) {
      setData({ ...data, synonyms: [...data.synonyms, draft] });
      closeDrawer();
    }
  };

  function SynonymFormFields({ value, onChange }) {
    return (
      <div className="me-grid cols-2">
        <SelectField
          label="Database table"
          value={value.database_table || ""}
          options={availableTables}
          allowFree
          onChange={(v) => onChange({ ...value, database_table: v })}
        />
        <div className="me-field">
          <FormLabel label="Synonyms" />
          <ChipsEditor
            items={value.synonyms || []}
            kind="topic"
            scrollable
            placeholder="Add a synonym "
            onChange={(next) => onChange({ ...value, synonyms: next })}
          />
        </div>
      </div>
    );
  }

  return (
    <>
      <ItemListSection
        items={data.synonyms}
        emptyText="No synonym sets yet."
        addAction={
          <Button type="primary" onClick={openDrawer}>
            Add Table
          </Button>
        }
        renderItem={(synonymEntry, idx) => (
          <CollapsibleCard
            key={idx}
            title={
              <>
                <span className="me-tag table">
                  {idx + 1}.{synonymEntry.database_table || "table"}
                </span>
                <span>{(synonymEntry.synonyms || []).length} synonym(s)</span>
              </>
            }
            actions={
              <CloseOutlined
                onClick={() =>
                  setData({
                    ...data,
                    synonyms: data.synonyms.filter((_, j) => j !== idx),
                  })
                }
              />
            }
          >
            <SynonymFormFields
              value={synonymEntry}
              onChange={(v) => updateSynonymAtIndex(idx, v)}
            />
          </CollapsibleCard>
        )}
      />
      <AddItemDrawer
        title="Add New Synonym Set"
        open={isOpen}
        onClose={closeDrawer}
        onSave={saveNewSynonym}
      >
        {draft && <SynonymFormFields value={draft} onChange={setDraft} />}
      </AddItemDrawer>
    </>
  );
}

// Defined at module level to prevent remount on every render (fixes focus-loss on keystroke)
function ExampleLinesList({ eg, onChangeEg }) {
  return (
    <div className="me-field">
      <FormLabel label="Examples" />
      {(eg || []).map((line, lineIdx) => (
        <div key={lineIdx} className="me-row" style={{ marginBottom: 6 }}>
          <input
            className="me-input mono"
            value={line}
            onChange={(ev) => {
              const next = [...eg];
              next[lineIdx] = ev.target.value;
              onChangeEg(next);
            }}
          />
          <CloseOutlined
            onClick={() =>
              onChangeEg((eg || []).filter((_, j) => j !== lineIdx))
            }
          />
        </div>
      ))}
      <Button
        type="primary"
        onClick={() => onChangeEg([...(eg || []), "term -> column or value"])}
      >
        Add Example
      </Button>
    </div>
  );
}

export function ExamplesSection({ data, setData }) {
  const availableTables = allTables(data);
  const { isOpen, draft, setDraft, openDrawer, closeDrawer } =
    useDrawerWithDraft(() => ({
      database_table: "",
      eg: [],
    }));

  const updateExampleAtIndex = (idx, patch) => {
    const next = { ...data, examples: [...data.examples] };
    next.examples[idx] = { ...next.examples[idx], ...patch };
    setData(next);
  };

  const saveNewExample = () => {
    if (draft) {
      setData({ ...data, examples: [...data.examples, draft] });
      closeDrawer();
    }
  };

  return (
    <>
      <ItemListSection
        items={data.examples}
        emptyText="No example groups yet."
        addAction={
          <Button type="primary" onClick={openDrawer}>
            Add Group
          </Button>
        }
        renderItem={(exampleGroup, idx) => (
          <CollapsibleCard
            key={idx}
            title={
              <>
                <span className="me-tag table">
                  {idx + 1}.{exampleGroup.database_table || "table"}
                </span>
                <span>{(exampleGroup.eg || []).length} example(s)</span>
              </>
            }
            actions={
              <CloseOutlined
                onClick={() =>
                  setData({
                    ...data,
                    examples: data.examples.filter((_, j) => j !== idx),
                  })
                }
              />
            }
          >
            <SelectField
              label="Database table"
              value={exampleGroup.database_table || ""}
              options={availableTables}
              allowFree
              onChange={(v) => updateExampleAtIndex(idx, { database_table: v })}
            />
            <div className="me-divider" />
            <ExampleLinesList
              eg={exampleGroup.eg}
              onChangeEg={(next) => updateExampleAtIndex(idx, { eg: next })}
            />
          </CollapsibleCard>
        )}
      />
      <AddItemDrawer
        title="Add New Example Group"
        open={isOpen}
        onClose={closeDrawer}
        onSave={saveNewExample}
      >
        {draft && (
          <>
            <SelectField
              label="Database table"
              value={draft.database_table || ""}
              options={availableTables}
              allowFree
              onChange={(v) => setDraft((p) => ({ ...p, database_table: v }))}
            />
            <div className="me-divider" />
            <ExampleLinesList
              eg={draft.eg}
              onChangeEg={(next) => setDraft((p) => ({ ...p, eg: next }))}
            />
          </>
        )}
      </AddItemDrawer>
    </>
  );
}

function DomainFormFields({ value, onChange }) {
  return (
    <div className="me-grid cols-2">
      <TextField
        label="Domain name"
        value={value.domain_name || ""}
        onChange={(v) => onChange({ ...value, domain_name: v })}
      />
      <div className="me-field">
        <FormLabel label="Topics" />
        <ChipsEditor
          items={value.topics || []}
          kind="topic"
          scrollable
          placeholder="Add a topic "
          onChange={(next) => onChange({ ...value, topics: next })}
        />
      </div>
    </div>
  );
}

function MappingFormFields({ value, availableTopics, onChange }) {
  return (
    <div className="me-grid cols-2">
      <SelectField
        label="Topic"
        value={value.topic_name || ""}
        options={availableTopics}
        allowFree
        onChange={(v) => onChange({ ...value, topic_name: v })}
      />
      <div className="me-field">
        <FormLabel label="Components" />
        <ChipsEditor
          items={value.component || []}
          kind="topic"
          scrollable
          placeholder="Add a component "
          onChange={(next) => onChange({ ...value, component: next })}
        />
      </div>
    </div>
  );
}

function TopicsSubsectionHeader({ label }) {
  return (
    <div className="me-subsection-head">
      <FormLabel label={label} />
    </div>
  );
}

export function TopicsSection({ data, setData }) {
  const availableTopics = allTopics(data);

  const domainDrawer = useDrawerWithDraft(() => ({
    domain_name: "",
    topics: [],
  }));
  const mappingDrawer = useDrawerWithDraft(() => ({
    topic_name: "",
    component: [],
  }));

  const saveNewDomain = () => {
    if (domainDrawer.draft) {
      setData({ ...data, domain: [...data.domain, domainDrawer.draft] });
      domainDrawer.closeDrawer();
    }
  };

  const saveNewMapping = () => {
    if (mappingDrawer.draft) {
      setData({
        ...data,
        topic_mappings: [...data.topic_mappings, mappingDrawer.draft],
      });
      mappingDrawer.closeDrawer();
    }
  };

  const updateDomainAtIndex = (idx, patch) => {
    const next = { ...data, domain: [...data.domain] };
    next.domain[idx] = { ...next.domain[idx], ...patch };
    setData(next);
  };

  const updateMappingAtIndex = (idx, patch) => {
    const next = { ...data, topic_mappings: [...data.topic_mappings] };
    next.topic_mappings[idx] = { ...next.topic_mappings[idx], ...patch };
    setData(next);
  };

  return (
    <section className="me-section active">
      <TopicsSubsectionHeader label="Domains - Topics" />
      <ItemListSection
        items={data.domain}
        emptyText="No domains yet."
        addAction={
          <Button type="primary" onClick={domainDrawer.openDrawer}>
            Add Domain
          </Button>
        }
        renderItem={(domainEntry, idx) => (
          <CollapsibleCard
            key={idx}
            title={
              <>
                <span className="me-tag topic">
                  {idx + 1}.{domainEntry.domain_name || "domain"}
                </span>
                <span>{(domainEntry.topics || []).length} topic(s)</span>
              </>
            }
            actions={
              <CloseOutlined
                onClick={() =>
                  setData({
                    ...data,
                    domain: data.domain.filter((_, j) => j !== idx),
                  })
                }
              />
            }
          >
            <DomainFormFields
              value={domainEntry}
              onChange={(v) => updateDomainAtIndex(idx, v)}
            />
          </CollapsibleCard>
        )}
      />

      <TopicsSubsectionHeader label="Topic - Components" />
      <ItemListSection
        items={data.topic_mappings}
        emptyText="No topic mappings yet."
        addAction={
          <Button type="primary" onClick={mappingDrawer.openDrawer}>
            Add Mapping
          </Button>
        }
        renderItem={(mappingEntry, idx) => (
          <CollapsibleCard
            key={idx}
            title={
              <>
                <span className="me-tag topic">
                  {idx + 1}.{mappingEntry.topic_name || "topic"}
                </span>
                <span>
                  {(mappingEntry.component || []).length} component(s)
                </span>
              </>
            }
            actions={
              <CloseOutlined
                onClick={() =>
                  setData({
                    ...data,
                    topic_mappings: data.topic_mappings.filter(
                      (_, j) => j !== idx,
                    ),
                  })
                }
              />
            }
          >
            <MappingFormFields
              value={mappingEntry}
              availableTopics={availableTopics}
              onChange={(v) => updateMappingAtIndex(idx, v)}
            />
          </CollapsibleCard>
        )}
      />

      <AddItemDrawer
        title="Add New Domain"
        open={domainDrawer.isOpen}
        onClose={domainDrawer.closeDrawer}
        onSave={saveNewDomain}
      >
        {domainDrawer.draft && (
          <DomainFormFields
            value={domainDrawer.draft}
            onChange={domainDrawer.setDraft}
          />
        )}
      </AddItemDrawer>

      <AddItemDrawer
        title="Add New Topic Mapping"
        open={mappingDrawer.isOpen}
        onClose={mappingDrawer.closeDrawer}
        onSave={saveNewMapping}
      >
        {mappingDrawer.draft && (
          <MappingFormFields
            value={mappingDrawer.draft}
            availableTopics={availableTopics}
            onChange={mappingDrawer.setDraft}
          />
        )}
      </AddItemDrawer>
    </section>
  );
}

export function JsonSection({ data }) {
  const jsonOutput = JSON.stringify(data, null, 2);
  return (
    <section className="me-section active">
      <SectionActionHeader />
      <div className="me-json-wrap">
        <pre dangerouslySetInnerHTML={{ __html: highlightJSON(jsonOutput) }} />
      </div>
    </section>
  );
}
