import { useEffect, useMemo, useRef, useState } from "react";
import { Button, Drawer, Tag, Tooltip, message } from "antd";
import { CheckOutlined, CopyOutlined } from "@ant-design/icons";
import "./stream-console.scss";

const { CheckableTag } = Tag;
const COPY_FEEDBACK_MS = 3000;

const LEVELS = [
  { key: "log", label: "Log", tag: "LOG", color: "default" },
  { key: "info", label: "Info", tag: "INFO", color: "blue" },
  { key: "warning", label: "Warning", tag: "WARNING", color: "orange" },
  { key: "error", label: "Error", tag: "ERROR", color: "red" },
  { key: "debug", label: "Debug", tag: "DEBUG", color: "purple" },
];

const LEVEL_META = LEVELS.reduce((acc, level) => {
  acc[level.key] = level;
  return acc;
}, {});

const formatTs = (ts) => {
  if (ts == null || ts === "") return "";
  const d = new Date(typeof ts === "number" ? ts : Number(ts));
  if (Number.isNaN(d.getTime())) return "";
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  const ss = String(d.getSeconds()).padStart(2, "0");
  const ms = String(d.getMilliseconds()).padStart(3, "0");
  return `${hh}:${mm}:${ss}.${ms}`;
};

const entryBody = (entry) => {
  if (entry.path && entry.message) return `${entry.path}: ${entry.message}`;
  return entry.message || entry.path || entry.text || "";
};

const StreamConsole = ({
  open,
  onClose,
  title = "Console",
  consoleState = { entries: [] },
  counts: countsProp,
}) => {
  const entries = consoleState.entries || [];
  const [activeLevels, setActiveLevels] = useState([]);
  const [copied, setCopied] = useState(false);
  const copyTimerRef = useRef(null);
  const bodyRef = useRef(null);
  const stickToBottomRef = useRef(true);

  const counts = useMemo(() => {
    if (countsProp) return countsProp;
    const next = { log: 0, debug: 0, info: 0, warning: 0, error: 0, total: 0 };
    entries.forEach((e) => {
      if (next[e.level] != null) next[e.level] += 1;
      next.total += 1;
    });
    return next;
  }, [countsProp, entries]);

  const visibleEntries = useMemo(() => {
    if (!activeLevels.length) return entries;
    const allowed = new Set(activeLevels);
    return entries.filter((e) => allowed.has(e.level));
  }, [entries, activeLevels]);

  useEffect(() => {
    const el = bodyRef.current;
    if (!el || !stickToBottomRef.current) return;
    el.scrollTop = el.scrollHeight;
  }, [visibleEntries]);

  const onBodyScroll = () => {
    const el = bodyRef.current;
    if (!el) return;
    stickToBottomRef.current = el.scrollHeight - el.scrollTop - el.clientHeight < 24;
  };

  const toggleLevel = (key, checked) => {
    setActiveLevels((prev) => {
      if (checked) return prev.includes(key) ? prev : [...prev, key];
      return prev.filter((k) => k !== key);
    });
  };

  const selectAll = () => setActiveLevels([]);

  const copyVisible = async () => {
    const text = visibleEntries
      .map((e) => {
        const meta = LEVEL_META[e.level];
        const tag = meta?.tag || e.level.toUpperCase();
        const ts = formatTs(e.ts);
        const body = entryBody(e);
        return ts ? `${tag}\t${ts}\t${body}` : `${tag}\t${body}`;
      })
      .join("\n");
    if (!text) {
      message.info("No entries to copy");
      return;
    }
    try {
      await navigator.clipboard.writeText(text);
      clearTimeout(copyTimerRef.current);
      setCopied(true);
      copyTimerRef.current = setTimeout(() => setCopied(false), COPY_FEEDBACK_MS);
    } catch {
      message.error("Failed to copy");
    }
  };

  return (
    <Drawer title={title} open={open} onClose={onClose} width={720} destroyOnClose={false}>
      <div className="hi-stream-console">
        <div className="hi-stream-console-toolbar">
          <div className="hi-stream-console-filters">
            <CheckableTag
              checked={!activeLevels.length}
              onChange={(checked) => {
                if (checked) selectAll();
              }}
            >
              All {counts.total || 0}
            </CheckableTag>
            {LEVELS.map(({ key, label }) => (
              <CheckableTag
                key={key}
                checked={activeLevels.includes(key)}
                onChange={(checked) => toggleLevel(key, checked)}
              >
                {label} {counts[key] || 0}
              </CheckableTag>
            ))}
          </div>
          <Tooltip title={copied ? "Copied to clipboard" : "Copy"}>
            <Button
              size="small"
              type="text"
              icon={copied ? <CheckOutlined /> : <CopyOutlined />}
              onClick={copyVisible}
              aria-label={copied ? "Copied" : "Copy"}
              className={
                copied
                  ? "hi-stream-console-copy hi-stream-console-copy-done"
                  : "hi-stream-console-copy"
              }
            />
          </Tooltip>
        </div>
        <div
          ref={bodyRef}
          className="hi-stream-console-body"
          onScroll={onBodyScroll}
        >
          {visibleEntries.length ? (
            visibleEntries.map((entry) => {
              const meta = LEVEL_META[entry.level] || LEVELS[0];
              return (
                <div
                  key={entry.id}
                  className={`hi-stream-console-row hi-stream-console-row-${entry.level}`}
                >
                  <Tag color={meta.color} className="hi-stream-console-tag">
                    {meta.tag}
                  </Tag>
                  <span className="hi-stream-console-ts">{formatTs(entry.ts)}</span>
                  <span className="hi-stream-console-msg">{entryBody(entry)}</span>
                </div>
              );
            })
          ) : (
            <div className="hi-stream-console-empty">No entries</div>
          )}
        </div>
      </div>
    </Drawer>
  );
};

export default StreamConsole;
