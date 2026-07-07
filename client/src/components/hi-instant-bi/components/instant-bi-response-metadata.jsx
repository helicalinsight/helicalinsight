import React from "react";
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

const ReasonText = ({ text }) => {
  if (!text) return <span className="ibm-empty">No reasoning provided</span>;
  const parts = String(text).split(/('[^']+')/g);
  return (
    <p className="ibm-reason">
      {parts.map((part, i) =>
        /^'.+'$/.test(part) ? (
          <em key={i} className="query-highlight">'{part.slice(1, -1)}'</em>
        ) : (
          <React.Fragment key={i}>{part}</React.Fragment>
        )
      )}
    </p>
  );
};

const formatTokenValue = (value) => {
  if (value === null || value === undefined) return "—";
  return String(value);
};

const TokenUsageSection = ({ tokenStats }) => {
  const entries = Object.entries(tokenStats || {});
  if (!entries.length) return null;

  return (
    <div className="ibm-section full">
      <div className="ibm-section-label">Token Usage</div>
      <div className="ibm-token-usage">
        {entries?.map(([key, val]) => (
          <div className="ibm-token-row" key={key}>
            <span className="ibm-token-label">{key.replace(/_/g, " ")}</span>
            <span className="ibm-token-value">{formatTokenValue(val)}</span>
          </div>
        ))}
      </div>
    </div>
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

  /*  SQL data */
  const isSqlInsight = SQL_KEYS.some((key) => key in metadata);
  const tables  = asList(metadata.required_table);
  const columns = asList(metadata.required_column);
  const topics = asList(metadata.required_topic);
  const domains = asList(metadata.required_domain);
  const chartNames = asList(metadata.chart_name);
  const dialect = (metadata.dialect || "unknown").toString();
  const joinVal = metadata.required_join;
  const joinsCount = Array.isArray(joinVal)
    ? joinVal.length
    : joinVal && (typeof joinVal !== "string" || joinVal.trim()) ? 1 : 0;

  const tokenStats = metadata.token_usage;
  const extraEntries = entries.filter(
    ([ent]) => !SQL_KEYS.includes(ent) && ent !== "chart_name" && ent !== "token_usage"
  );

  return (
    <div className="ibm-root">
      <div className="ibm-card">
          <div className="ibm-body">
            {isSqlInsight && (
              <>
              <div className="ibm-section">
                  <div className="ibm-section-label">Domain</div>
                  <Pills items={domains} kind="domains" />
                </div>
                   <div className="ibm-section">
                  <div className="ibm-section-label">Topic</div>
                  <Pills items={topics} kind="topic" />
                </div>
                {/* Tables */}
                <div className="ibm-section">
                  <div className="ibm-section-label">Tables</div>
                  <Pills items={tables} kind="table" />
                </div>
                {/* Columns */}
                <div className="ibm-section">
                  <div className="ibm-section-label">Columns</div>
                  <Pills items={columns} kind="column" />
                </div>

                {metadata.reason && (
                  <div className="ibm-section full">
                    <div className="ibm-section-label">Reasoning</div>
                    <ReasonText text={metadata.reason} />
                  </div>
                )}
                {tokenStats && <TokenUsageSection tokenStats={tokenStats} />}
              </>
            )}
            {!isSqlInsight && extraEntries.map(([key, value]) => (
              <div className="ibm-kv-row ibm-kv-row-full" key={key}>
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
    </div>
  );
};

export default InstantBIResponseMetadata;
export { TokenUsageSection };
