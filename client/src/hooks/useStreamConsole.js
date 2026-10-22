import { useCallback, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { waitForTicketEvents } from "../base/wait-ticket-events";

const LEVELS = ["log", "debug", "info", "warning", "error"];

const emptyState = () => ({ entries: [] });

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

const isNoiseEvent = (data) =>
  data?.status === "PREPARING" && !data?.message && !data?.path;

let entrySeq = 0;

const toEntry = (data, levelOverride) => {
  if (!data || isNoiseEvent(data)) return null;
  if (typeof data === "string") {
    return toEntry({ message: data, ts: Date.now() }, levelOverride);
  }
  const level = levelOverride || data.channel || data.level;
  if (!LEVELS.includes(level)) return null;
  const path = data.path || "";
  const message = data.message || "";
  if (!path && !message) return null;
  const ts = data.ts ?? Date.now();
  const tsLabel = formatTs(ts);
  const body = path ? `${path}: ${message}` : message;
  const text = tsLabel ? `[${tsLabel}] ${body}` : body;
  return {
    id: `${ts}-${entrySeq++}`,
    level,
    path,
    message,
    ts,
    text,
  };
};

const emptyCounts = () => ({
  log: 0,
  debug: 0,
  info: 0,
  warning: 0,
  error: 0,
  total: 0,
});

const deriveCounts = (entries) => {
  const counts = emptyCounts();
  entries.forEach((e) => {
    if (counts[e.level] != null) counts[e.level] += 1;
    counts.total += 1;
  });
  return counts;
};

const appendEntries = (prev, nextEntries) => {
  if (!nextEntries?.length) return prev;
  const seen = new Set(prev.entries.map((e) => e.text + "|" + e.level));
  const merged = [...prev.entries];
  nextEntries.forEach((entry) => {
    const key = entry.text + "|" + entry.level;
    if (!seen.has(key)) {
      seen.add(key);
      merged.push(entry);
    }
  });
  return { entries: merged };
};

export const useStreamConsole = () => {
  const dispatch = useDispatch();
  const [consoleState, setConsoleState] = useState(emptyState);
  const abortRef = useRef(null);
  const ingestRef = useRef(null);

  const applyEntries = useCallback((entries) => {
    if (!entries?.length) return;
    setConsoleState((prev) => appendEntries(prev, entries));
  }, []);

  const reset = useCallback(() => setConsoleState(emptyState()), []);

  const ingestSseChunk = useCallback(
    (parsed) => {
      if (!parsed) return;
      if (parsed.event === "batch" && Array.isArray(parsed.data)) {
        applyEntries(parsed.data.map((e) => toEntry(e)).filter(Boolean));
        return;
      }
      const entry = toEntry(parsed.data, parsed.event || parsed.data?.channel);
      if (entry) applyEntries([entry]);
    },
    [applyEntries],
  );
  ingestRef.current = ingestSseChunk;

  const appendLog = useCallback(
    (message, path = "file") => {
      const entry = toEntry({ path, message, ts: Date.now() }, "log");
      if (entry) applyEntries([entry]);
    },
    [applyEntries],
  );

  const hydrateFromNotification = useCallback(
    (notification) => {
      if (!notification) return;
      const items = [];
      (notification.logs || []).forEach((entry) => {
        const e = toEntry(entry, "log");
        if (e) items.push(e);
      });
      [
        ["info", notification.info],
        ["warning", notification.warnings],
        ["error", notification.errors],
      ].forEach(([level, bucket]) => {
        Object.entries(bucket || {}).forEach(([path, messages]) => {
          (messages || []).forEach((message) => {
            const e = toEntry({ path, message }, level);
            if (e) items.push(e);
          });
        });
      });
      applyEntries(items);
    },
    [applyEntries],
  );

  const disconnect = useCallback(() => {
    abortRef.current?.abort();
    abortRef.current = null;
  }, []);

  const connect = useCallback(
    (eventsUrl, { clear = false } = {}) => {
      disconnect();
      if (clear) reset();
      const waitAbort = new AbortController();
      abortRef.current = waitAbort;
      let hiAxios;
      dispatch((d, getState, services) => {
        hiAxios = services(d);
      });
      return waitForTicketEvents({
        dispatch,
        hiAxios,
        eventsUrl,
        signal: waitAbort.signal,
        onEvent: (parsed) => ingestRef.current?.(parsed),
      });
    },
    [disconnect, dispatch, reset],
  );

  const counts = deriveCounts(consoleState.entries);

  return {
    consoleState,
    counts,
    reset,
    appendLog,
    ingestSseChunk,
    hydrateFromNotification,
    connect,
    disconnect,
  };
};
