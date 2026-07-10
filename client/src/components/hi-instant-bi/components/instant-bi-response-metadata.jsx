import React, { useState } from "react";
import "./instant-bi-response-metadata.scss";
import {
  AppstoreOutlined,
  StarOutlined,
  TableOutlined,
  TagsOutlined,
} from "@ant-design/icons";

const asList = (value) => {
  if (Array?.isArray(value)) return value.filter((x) => x !== null && x !== undefined && x !== "");
  if (value === null || value === undefined || value === "") return [];
  return [value];
};

const SQL_KEYS = [
  "dialect",
  "required_table",
  "required_column",
  "required_join",
  "reason",
  "required_topic",
  "required_domain"
];

const omitEmpty = (obj = {}) =>
  Object.fromEntries(
    Object.entries(obj).filter(([, value]) => {
      if (value === undefined || value === null || value === "") return false;
      if (Array.isArray(value) && !value.length) return false;
      return true;
    })
  );

const VISIBLE_PILL_COUNT = 1;
const VISIBLE_REASON_CHARS = 120;

export const getInstantBIResponseMetadata = ({
  sqlDetails = {},
  vizDetails = {},
  tokenUsage = {},
} = {}) => {
  const metadata = omitEmpty(sqlDetails);
  delete metadata.raw_sql;
  if (vizDetails?.chart_name) metadata.chart_name = vizDetails.chart_name;
  const usage =
    tokenUsage && typeof tokenUsage === "object" && !Array.isArray(tokenUsage)
      ? tokenUsage
      : {};
  if (Object.keys(usage).length) metadata.token_usage = usage;
  return metadata;
};

const getIcon = (kind) => {
  const style = { fontSize: "9px" };
  const iconMap = {
    table: <TableOutlined style={style} />,
    column: <StarOutlined style={style} />,
    topic: <TagsOutlined style={style} />,
    domains: <AppstoreOutlined style={style} />,
  };
  return iconMap[kind] || <StarOutlined style={style} />;
};

const ShowMoreToggle = ({ expanded, onClick, collapsedLabel = "See More" }) => (
  <button type="button" className="ibm-show-more" onClick={onClick}>
    {expanded ? "show less" : collapsedLabel}
  </button>
);

const MetadataRowSection = ({
  label,
  children,
  expandable,
  expanded,
  onToggle,
  extra,
  collapsedToggleLabel,
}) => (
  <div className="ibm-section">
    <div className="ibm-section-row">
      <span className="ibm-section-label">{label}</span>
      <div className="ibm-section-content">{children}</div>
      {expandable && (
        <ShowMoreToggle
          expanded={expanded}
          onClick={onToggle}
          collapsedLabel={collapsedToggleLabel}
        />
      )}
    </div>
    {extra}
  </div>
);

const Pills = ({ items, kind }) => {
  if (!items.length) return <span className="ibm-empty">None specified</span>;
  const icon = getIcon(kind);
  return (
    <div className="ibm-pills">
      {items.map((item, ind) => (
        <span key={ind} className={`ibm-pill ${kind}`}>
          <span className="ibm-pill-ico">{icon}</span>
          {item}
        </span>
      ))}
    </div>
  );
};

const PillSection = ({ label, items, kind }) => {
  const [expanded, setExpanded] = useState(false);
  const canExpand = items.length > VISIBLE_PILL_COUNT;
  const visibleItems = expanded ? items : items.slice(0, VISIBLE_PILL_COUNT);
  const hiddenCount = Math.max(0, items.length - VISIBLE_PILL_COUNT);

  return (
    <MetadataRowSection
      label={label}
      expandable={canExpand}
      expanded={expanded}
      onToggle={() => setExpanded((value) => !value)}
      collapsedToggleLabel={hiddenCount > 0 ? `+${hiddenCount} more` : "See More"}
    >
      <Pills items={visibleItems} kind={kind} />
    </MetadataRowSection>
  );
};

const ReasonText = ({ text, expanded }) => {
  if (!text) return <span className="ibm-empty">No reasoning provided</span>;
  const fullText = String(text);
  const displayText =
    expanded || fullText.length <= VISIBLE_REASON_CHARS
      ? fullText
      : `${fullText.slice(0, VISIBLE_REASON_CHARS).trim()}…`;
  const parts = displayText.split(/('[^']+')/g);
  return (
    <span className="ibm-reason">
      {parts.map((part, i) =>
        /^'.+'$/.test(part) ? (
          <em key={i} className="query-highlight">'{part.slice(1, -1)}'</em>
        ) : (
          <React.Fragment key={i}>{part}</React.Fragment>
        )
      )}
    </span>
  );
};

const ReasonSection = ({ text }) => {
  const [expanded, setExpanded] = useState(false);
  const canExpand = String(text || "").length > VISIBLE_REASON_CHARS;

  return (
    <MetadataRowSection
      label="Reasoning"
      expandable={canExpand}
      expanded={expanded}
      onToggle={() => setExpanded((value) => !value)}
    >
      <ReasonText text={text} expanded={expanded} />
    </MetadataRowSection>
  );
};

const formatTokenValue = (value) => {
  if (value === null || value === undefined) return "—";
  return String(value);
};

const getTotalTokens = (tokenStats = {}) => {
  if (tokenStats.total_tokens != null) return tokenStats.total_tokens;
  const input = Number(tokenStats.input_tokens) || 0;
  const output = Number(tokenStats.output_tokens) || 0;
  if (input || output) return input + output;
  return null;
};

const TokenUsageSection = ({ tokenStats }) => {
  const [expanded, setExpanded] = useState(false);
  const entries = Object.entries(tokenStats || {});
  if (!entries.length) return null;

  const totalTokens = getTotalTokens(tokenStats);
  const detailEntries = entries.filter(([key]) => key !== "total_tokens");
  const canExpand = detailEntries.length > 0;

  return (
    <MetadataRowSection
      label="Token Usage"
      expandable={canExpand}
      expanded={expanded}
      onToggle={() => setExpanded((value) => !value)}
      extra={
        expanded ? (
          <div className="ibm-token-details">
            {detailEntries.map(([key, val]) => (
              <div className="ibm-token-row" key={key}>
                <span className="ibm-token-label">{key.replace(/_/g, " ")}</span>
                <span className="ibm-token-value">{formatTokenValue(val)}</span>
              </div>
            ))}
          </div>
        ) : null
      }
    >
      <div className="ibm-token-inline">
        <span className="ibm-token-label">Total tokens</span>
        <span className="ibm-token-value">{formatTokenValue(totalTokens)}</span>
      </div>
    </MetadataRowSection>
  );
};

const GenericValue = ({ value }) => {
  if (Array?.isArray(value)) {
    return (
      <div className="ibm-pills ibm-pills-compact">
        {value.map((val, index) => (
          <span key={index} className="ibm-pill table">{String(val)}</span>
        ))}
      </div>
    );
  }
  if (value && typeof value === "object") {
    return (
      <pre className="ibm-kv-value ibm-kv-value-pre">
        {JSON.stringify(value, null, 2)}
      </pre>
    );
  }
  return <span className="ibm-kv-value">{String(value)}</span>;
};

const InstantBIResponseMetadata = ({ sqlDetails = {}, vizDetails = {}, tokenUsage = {} }) => {
  const metadata = getInstantBIResponseMetadata({ sqlDetails, vizDetails, tokenUsage });
  const entries = Object.entries(metadata);
  if (!entries.length) return null;

  const isSqlInsight = SQL_KEYS.some((key) => key in metadata);
  const tables = asList(metadata.required_table);
  const columns = asList(metadata.required_column);
  const topics = asList(metadata.required_topic);
  const domains = asList(metadata.required_domain);
  const chartNames = asList(metadata.chart_name);
  const dialect = (metadata.dialect || "unknown").toString();

  const tokenStats = metadata.token_usage;
  const extraEntries = entries.filter(
    ([ent]) => !SQL_KEYS.includes(ent) && ent !== "chart_name" && ent !== "token_usage"
  );

  return (
    <div className="ibm-root">
      <div className="ibm-body">
        {isSqlInsight && (
          <>
            <div className="ibm-pair-row">
              <PillSection label="Domain" items={domains} kind="domains" />
              <PillSection label="Topic" items={topics} kind="topic" />
            </div>
            <div className="ibm-pair-row">
              <PillSection label="Tables" items={tables} kind="table" />
              <PillSection label="Columns" items={columns} kind="column" />
            </div>
            {metadata.reason && <ReasonSection text={metadata.reason} />}
            {tokenStats && <TokenUsageSection tokenStats={tokenStats} />}
          </>
        )}
        {!isSqlInsight && extraEntries.map(([key, value]) => (
          <div className="ibm-kv-row" key={key}>
            <div className="ibm-kv-label">{key.replace(/_/g, " ")}</div>
            <GenericValue value={value} />
          </div>
        ))}
        {!isSqlInsight && tokenStats && <TokenUsageSection tokenStats={tokenStats} />}
      </div>

      {(isSqlInsight || chartNames.length > 0) && (
        <div className="ibm-footer">
          {isSqlInsight && (
            <>
              <span><strong>{tables.length}</strong> {tables.length === 1 ? "table" : "tables"}</span>
              <span><strong>{columns.length}</strong> {columns.length === 1 ? "column" : "columns"}</span>
              <span>dialect: <strong>{dialect}</strong></span>
            </>
          )}
          {chartNames.length > 0 && (
            <span>chart: <strong>{chartNames.join(", ")}</strong></span>
          )}
        </div>
      )}
    </div>
  );
};

export default InstantBIResponseMetadata;
export { TokenUsageSection };
