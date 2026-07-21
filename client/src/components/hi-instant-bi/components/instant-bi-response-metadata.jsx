import React, { useState, useRef, useLayoutEffect, useEffect, useCallback } from "react";
import "./instant-bi-response-metadata.scss";
import {
  AppstoreOutlined,
  BarChartOutlined,
  PartitionOutlined,
  StarOutlined,
  TableOutlined,
  TagsOutlined,
} from "@ant-design/icons";

const PILL_MEASURE_SELECTOR = "[data-ibm-pill]";

const SQL_METADATA_FIELD_KEYS = [
  "dialect",
  "required_table",
  "required_column",
  "required_join",
  "reason",
  "required_topic",
  "required_domain",
  "required_cube_info",
];

const SQL_PILL_SECTION_ROWS = [
  {
    id: "domain-topic",
    sections: [
      { field: "required_domain", singular: "domain", plural: "domains", kind: "domains" },
      { field: "required_topic", singular: "topic", plural: "topics", kind: "topic" },
    ],
  },
  {
    id: "cube",
    sections: [
      {
        field: "picked_dimensions",
        singular: "dimension",
        plural: "dimensions",
        kind: "dimension",
        source: "cubeInfo",
      },
      {
        field: "picked_metrics",
        singular: "metric",
        plural: "metrics",
        kind: "metric",
        source: "cubeInfo",
      },
    ],
  },
  {
    id: "table-column",
    sections: [
      { field: "required_table", singular: "table", plural: "tables", kind: "table" },
      { field: "required_column", singular: "column", plural: "columns", kind: "column" },
    ],
  },
];

const FOOTER_STAT_CONFIG = [
  {
    field: "required_table",
    singular: "table",
    plural: "tables",
    getCount: ({ metadata }) => asList(metadata.required_table).length,
  },
  {
    field: "required_column",
    singular: "column",
    plural: "columns",
    getCount: ({ metadata }) => asList(metadata.required_column).length,
  },
  {
    field: "picked_dimensions",
    singular: "dimension",
    plural: "dimensions",
    getCount: ({ cubeInfo }) => asList(cubeInfo.picked_dimensions).length,
  },
  {
    field: "picked_metrics",
    singular: "metric",
    plural: "metrics",
    getCount: ({ cubeInfo }) => asList(cubeInfo.picked_metrics).length,
  },
  {
    field: "dialect",
    render: ({ metadata }) => (
      <span>
        dialect: <strong>{(metadata.dialect || "unknown").toString()}</strong>{" "}
      </span>
    ),
  },
];

const capitalize = (value) =>
  value ? `${value.charAt(0).toUpperCase()}${value.slice(1)}` : value;

const formatCountNoun = (count, singular, plural) =>
  Number(count) === 1 ? singular : plural;

const asList = (value) => {
  if (Array?.isArray(value)) return value.filter((x) => x !== null && x !== undefined && x !== "");
  if (value === null || value === undefined || value === "") return [];
  return [value];
};

const normalizeCubeInfo = (cubeInfo) => {
  const info = cubeInfo && typeof cubeInfo === "object" ? cubeInfo : {};
  const picked_dimensions = asList(info.picked_dimensions);
  const picked_metrics = asList(info.picked_metrics);
  if (!picked_dimensions.length && !picked_metrics.length) return null;
  return { picked_dimensions, picked_metrics };
};

const omitEmpty = (obj = {}) =>
  Object.fromEntries(
    Object.entries(obj).filter(([, value]) => {
      if (value === undefined || value === null || value === "") return false;
      if (Array.isArray(value) && !value.length) return false;
      return true;
    })
  );

const resolveSectionItems = (section, metadata, cubeInfo) => {
  const source = section.source === "cubeInfo" ? cubeInfo : metadata;
  return asList(source?.[section.field]);
};

const buildPillSectionRows = (metadata, cubeInfo) =>
  SQL_PILL_SECTION_ROWS.map((row) => ({
    ...row,
    sections: row.sections.map((section) => {
      const items = resolveSectionItems(section, metadata, cubeInfo);
      return {
        ...section,
        items,
        label: capitalize(formatCountNoun(items.length, section.singular, section.plural)),
      };
    }),
  }));

const buildFooterStats = () => FOOTER_STAT_CONFIG;
const getPillGap = (measureEl) => {
  const pillsEl = measureEl?.querySelector(".ibm-pills");
  if (!pillsEl) return 0;
  const styles = getComputedStyle(pillsEl);
  const gap = parseFloat(styles.columnGap || styles.gap);
  return Number.isFinite(gap) ? gap : 0;
};

const sumPillWidths = (widths = [], gap = 0) =>
  widths.reduce((total, width, index) => total + width + (index > 0 ? gap : 0), 0);

const getFittingPillCount = (containerWidth, pillWidths = [], gap = 0) => {
  if (!pillWidths.length) return 0;
  if (sumPillWidths(pillWidths, gap) <= containerWidth) return pillWidths.length;

  let used = 0;
  let count = 0;
  for (const width of pillWidths) {
    const nextUsed = used + (count ? gap : 0) + width;
    if (nextUsed > containerWidth && count > 0) break;
    used = nextUsed;
    count += 1;
  }

  if (count >= pillWidths.length) count = pillWidths.length - 1;
  return Math.max(1, count);
};

export const getInstantBIResponseMetadata = ({
  sqlDetails = {},
  vizDetails = {},
  tokenUsage = {},
} = {}) => {
  const metadata = omitEmpty(sqlDetails);
  delete metadata.raw_sql;
  const cubeInfo = normalizeCubeInfo(metadata.required_cube_info);
  if (cubeInfo) {
    metadata.required_cube_info = cubeInfo;
  } else {
    delete metadata.required_cube_info;
  }
  if (vizDetails?.chart_name) metadata.chart_name = vizDetails.chart_name;
  const usage =
    tokenUsage && typeof tokenUsage === "object" && !Array.isArray(tokenUsage)
      ? tokenUsage
      : {};
  if (Object.keys(usage).length) metadata.token_usage = usage;
  return metadata;
};

const ICON_MAP = {
  table: TableOutlined,
  column: StarOutlined,
  topic: TagsOutlined,
  domains: AppstoreOutlined,
  dimension: PartitionOutlined,
  metric: BarChartOutlined,
};

const getIcon = (kind) => {
  const Icon = ICON_MAP[kind] || StarOutlined;
  return <Icon style={{ fontSize: "9px" }} />;
};

const formatHiddenCountLabel = (hiddenCount) =>
  hiddenCount > 0 ? `+${hiddenCount} more` : "See More";

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

const Pills = ({ items, kind, nowrap = false }) => {
  if (!items.length) return <span className="ibm-empty">None specified</span>;
  const icon = getIcon(kind);
  return (
    <div className={`ibm-pills${nowrap ? " ibm-pills-nowrap" : ""}`}>
      {items.map((item, ind) => (
        <span
          key={`${item}-${ind}`}
          data-ibm-pill=""
          className={`ibm-pill ${kind}`}
        >
          <span className="ibm-pill-ico">{icon}</span>
          {item}
        </span>
      ))}
    </div>
  );
};

const PillSection = ({ label, items, kind }) => {
  const [expanded, setExpanded] = useState(false);
  const [visibleCount, setVisibleCount] = useState(items.length);
  const contentRef = useRef(null);
  const measureRef = useRef(null);

  const updateVisibleCount = useCallback(() => {
    if (expanded) {
      setVisibleCount(items.length);
      return;
    }

    const container = contentRef.current;
    const measure = measureRef.current;
    if (!container || !measure) return;

    const pillGap = getPillGap(measure);
    const pillWidths = Array.from(measure.querySelectorAll(PILL_MEASURE_SELECTOR)).map(
      (pill) => pill.offsetWidth
    );
    setVisibleCount(getFittingPillCount(container.clientWidth, pillWidths, pillGap));
  }, [expanded, items]);

  useLayoutEffect(() => {
    updateVisibleCount();
  }, [updateVisibleCount, items, kind, label]);

  useEffect(() => {
    setExpanded(false);
  }, [items]);

  useEffect(() => {
    const container = contentRef.current;
    if (!container || typeof ResizeObserver === "undefined") return undefined;

    const observer = new ResizeObserver(() => updateVisibleCount());
    observer.observe(container);
    return () => observer.disconnect();
  }, [updateVisibleCount]);

  const hiddenCount = Math.max(0, items.length - visibleCount);
  const visibleItems = expanded ? items : items.slice(0, visibleCount);
  const canExpand = hiddenCount > 0 || expanded;

  return (
    <MetadataRowSection
      label={label}
      expandable={canExpand}
      expanded={expanded}
      onToggle={() => setExpanded((value) => !value)}
      collapsedToggleLabel={formatHiddenCountLabel(hiddenCount)}
    >
      <div ref={contentRef} className="ibm-pill-content">
        <Pills items={visibleItems} kind={kind} nowrap={!expanded} />
        <div ref={measureRef} className="ibm-pills-measure" aria-hidden="true">
          <Pills items={items} kind={kind} nowrap />
        </div>
      </div>
    </MetadataRowSection>
  );
};

const ReasonText = ({ text, expanded, contentRef, onTruncationChange }) => {
  const [previewChars, setPreviewChars] = useState(text?.length || 0);
  const measureRef = useRef(null);

  const updatePreviewChars = useCallback(() => {
    const fullText = String(text || "");
    const container = contentRef?.current;
    const measure = measureRef.current;

    if (expanded) {
      setPreviewChars(fullText.length);
      return;
    }

    if (!container || !measure || !fullText) {
      setPreviewChars(fullText.length);
      onTruncationChange?.(false);
      return;
    }

    const containerWidth = container.clientWidth;
    const textNode = measure.querySelector("[data-ibm-reason-text]");
    if (!textNode || !containerWidth) {
      setPreviewChars(fullText.length);
      onTruncationChange?.(false);
      return;
    }

    let low = 0;
    let high = fullText.length;
    let best = 0;

    while (low <= high) {
      const mid = Math.floor((low + high) / 2);
      textNode.textContent = `${fullText.slice(0, mid).trim()}…`;
      if (textNode.offsetWidth <= containerWidth) {
        best = mid;
        low = mid + 1;
      } else {
        high = mid - 1;
      }
    }

    const isTruncated = best > 0 && best < fullText.length;
    setPreviewChars(isTruncated ? best : fullText.length);
    onTruncationChange?.(isTruncated);
  }, [contentRef, expanded, onTruncationChange, text]);

  useLayoutEffect(() => {
    updatePreviewChars();
  }, [updatePreviewChars]);

  useEffect(() => {
    const container = contentRef?.current;
    if (!container || typeof ResizeObserver === "undefined") return undefined;

    const observer = new ResizeObserver(() => updatePreviewChars());
    observer.observe(container);
    return () => observer.disconnect();
  }, [contentRef, updatePreviewChars]);

  if (!text) return <span className="ibm-empty">No reasoning provided</span>;

  const fullText = String(text);
  const displayText =
    expanded || fullText.length <= previewChars
      ? fullText
      : `${fullText.slice(0, previewChars).trim()}…`;
  const parts = displayText.split(/('[^']+')/g);

  return (
    <>
      <span className="ibm-reason">
        {parts.map((part, i) =>
          /^'.+'$/.test(part) ? (
            <em key={i} className="query-highlight">'{part.slice(1, -1)}'</em>
          ) : (
            <React.Fragment key={i}>{part}</React.Fragment>
          )
        )}
      </span>
      <span ref={measureRef} className="ibm-reason-measure" aria-hidden="true">
        <span data-ibm-reason-text="" className="ibm-reason" />
      </span>
    </>
  );
};

const ReasonSection = ({ text }) => {
  const [expanded, setExpanded] = useState(false);
  const [isTruncated, setIsTruncated] = useState(false);
  const contentRef = useRef(null);

  return (
    <MetadataRowSection
      label="Reasoning"
      expandable={isTruncated || expanded}
      expanded={expanded}
      onToggle={() => setExpanded((value) => !value)}
    >
      <div ref={contentRef} className="ibm-reason-content">
        <ReasonText
          text={text}
          expanded={expanded}
          contentRef={contentRef}
          onTruncationChange={setIsTruncated}
        />
      </div>
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
    >
      <div className="ibm-token-stack">
        <div className="ibm-token-inline">
          <span className="ibm-token-label">Total tokens</span>
          <span className="ibm-token-value">{formatTokenValue(totalTokens)}</span>
        </div>
        {expanded && (
          <div className="ibm-token-details">
            {detailEntries.map(([key, val]) => (
              <div className="ibm-token-row" key={key}>
                <span className="ibm-token-label">{key.replace(/_/g, " ")}</span>
                <span className="ibm-token-value">{formatTokenValue(val)}</span>
              </div>
            ))}
          </div>
        )}
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

const FooterCountStat = ({ count, singular, plural }) => (
  <span>
    <strong>{count}</strong> {formatCountNoun(count, singular, plural)}{" "}
  </span>
);

const InstantBIResponseMetadata = ({ sqlDetails = {}, vizDetails = {}, tokenUsage = {} }) => {
  const metadata = getInstantBIResponseMetadata({ sqlDetails, vizDetails, tokenUsage });
  const entries = Object.entries(metadata);
  if (!entries.length) return null;

  const isSqlInsight = SQL_METADATA_FIELD_KEYS.some((key) => key in metadata);
  const cubeInfo = normalizeCubeInfo(metadata.required_cube_info) || {};
  const pillSectionRows = buildPillSectionRows(metadata, cubeInfo);
  const footerStats = buildFooterStats();
  const chartNames = asList(metadata.chart_name);

  const tokenStats = metadata.token_usage;
  const reservedKeys = new Set([...SQL_METADATA_FIELD_KEYS, "chart_name", "token_usage"]);
  const extraEntries = entries.filter(([key]) => !reservedKeys.has(key));

  return (
    <div className="ibm-root">
      <div className="ibm-body">
        {isSqlInsight && (
          <>
            {pillSectionRows.map((row) => (
              <div className="ibm-pair-row" key={row.id}>
                {row.sections.map((section) => (
                  <PillSection
                    key={section.field}
                    label={section.label}
                    items={section.items}
                    kind={section.kind}
                  />
                ))}
              </div>
            ))}
            {metadata.reason && <ReasonSection text={metadata.reason} />}
            {tokenStats && (
              <div className="ibm-pair-row">
                <TokenUsageSection tokenStats={tokenStats} />
              </div>
            )}
          </>
        )}
        {!isSqlInsight &&
          extraEntries.map(([key, value]) => (
            <div className="ibm-kv-row" key={key}>
              <div className="ibm-kv-label">{key.replace(/_/g, " ")}</div>
              <GenericValue value={value} />
            </div>
          ))}
        {!isSqlInsight && tokenStats && (
          <div className="ibm-pair-row">
            <TokenUsageSection tokenStats={tokenStats} />
          </div>
        )}
      </div>

      {(isSqlInsight || chartNames.length > 0) && (
        <div className="ibm-footer">
          {isSqlInsight &&
            footerStats.map((stat) =>
              stat.render ? (
                <React.Fragment key={stat.field}>{stat.render({ metadata, cubeInfo })}</React.Fragment>
              ) : (
                <FooterCountStat
                  key={stat.field}
                  count={stat.getCount({ metadata, cubeInfo })}
                  singular={stat.singular}
                  plural={stat.plural}
                />
              )
            )}
          {chartNames.length > 0 && (
            <span>
              chart: <strong>{chartNames.join(", ")}</strong>
            </span>
          )}
        </div>
      )}
    </div>
  );
};

export default InstantBIResponseMetadata;
export { TokenUsageSection };
