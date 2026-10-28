import { waitForTicketEvents } from "./wait-ticket-events";
import {
  abortError as baseAbortError,
  createTransferControls,
  isRetryableNetworkError,
  sleepMs,
  waitUntilOnline,
} from "./resumable-transfer";

const DEFAULT_CHUNK_SIZE = 1024 * 1024;
const HEAD_POLL_MS = 2000;
const HEAD_POLL_MAX_ATTEMPTS = 450;
const CHECKSUM_HEADER = "x-checksum-sha256";

const abortError = (message = "Download cancelled") => baseAbortError(message);

/** Omit X-Requested-With so ResponseMetadataFilter does not strip Content-Length. */
const fileDownloadAxiosConfig = (config = {}) => ({
  ...config,
  headers: { ...(config.headers || {}), "X-Requested-With": undefined },
  transformRequest: [
    (data, headers) => {
      if (!headers) return data;
      if (typeof headers.delete === "function") headers.delete("X-Requested-With");
      else {
        delete headers["X-Requested-With"];
        if (headers.common) delete headers.common["X-Requested-With"];
      }
      return data;
    },
  ],
});

export const resolveDownloadChunkSize = (getState) => {
  const n = Number(getState?.()?.app?.applicationSettingsData?.downloadChunkSize);
  return Number.isFinite(n) && n > 0 ? Math.floor(n) : DEFAULT_CHUNK_SIZE;
};

const parseJsonMaybe = (data) => {
  if (data == null) return {};
  if (typeof data === "object") return data;
  if (typeof data === "string") {
    try {
      return JSON.parse(data);
    } catch (e) {
      return {};
    }
  }
  return {};
};

export const parseContentDispositionFileName = (header) => {
  if (!header) return "";
  const match = String(header).match(/filename\*?=(?:UTF-8''|")?([^\";]+)"?/i);
  const raw = match?.[1] || String(header).split("=")[1] || "";
  const cleaned = raw.replace(/"/g, "").trim();
  if (!cleaned) return "";
  try {
    return decodeURIComponent(cleaned);
  } catch (e) {
    return cleaned;
  }
};

const header = (headers, name) => {
  if (!headers) return undefined;
  const lower = name.toLowerCase();
  if (headers[name] != null) return headers[name];
  if (headers[lower] != null) return headers[lower];
  const key = Object.keys(headers).find((k) => k.toLowerCase() === lower);
  return key != null ? headers[key] : undefined;
};

const normalizeReady = (data = {}) => {
  const total = Number(data.contentLength ?? data.total);
  return {
    total: Number.isFinite(total) ? total : null,
    fileName: data.fileName || "",
    contentType: data.contentType,
    downloadUrl: data.downloadUrl,
    checksum: data.checksum || data.checksumSha256 || null,
  };
};

const sha256Blob = async (blob) => {
  const hash = await crypto.subtle.digest("SHA-256", await blob.arrayBuffer());
  return Array.from(new Uint8Array(hash))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
};

const ensureLeadingSlash = (url) =>
  !url || url.startsWith("http") || url.startsWith("/") ? url : `/${url}`;

const createDownloadControls = () => createTransferControls(abortError);

const waitViaHeadPoll = async (hiAxios, downloadUrl, signal) => {
  for (let attempt = 0; attempt < HEAD_POLL_MAX_ATTEMPTS; attempt++) {
    if (signal?.aborted) throw abortError();
    try {
      const head = await hiAxios.instance(
        fileDownloadAxiosConfig({
          url: downloadUrl,
          method: "HEAD",
          signal,
          validateStatus: (s) => s < 500,
        }),
      );
      if (head.status === 403) throw new Error("Download ticket forbidden (HTTP 403)");
      if (head.status === 410) throw new Error("Download ticket not available (HTTP 410)");
      if (head.status === 200) {
        const lengthHeader = header(head.headers, "content-length");
        const total = lengthHeader != null && lengthHeader !== "" ? Number(lengthHeader) : null;
        return {
          total: Number.isFinite(total) ? total : null,
          fileName: parseContentDispositionFileName(header(head.headers, "content-disposition")),
          contentType: header(head.headers, "content-type"),
          checksum: header(head.headers, CHECKSUM_HEADER) || header(head.headers, "X-Checksum-SHA256"),
        };
      }
      // 202 / other non-fatal → keep polling
    } catch (e) {
      if (e?.name === "AbortError" || e?.code === "ERR_CANCELED") throw e;
      if (
        e?.message?.includes("forbidden") ||
        e?.message?.includes("not available") ||
        attempt === HEAD_POLL_MAX_ATTEMPTS - 1
      ) {
        throw e;
      }
    }
    await new Promise((resolve, reject) => {
      const timer = setTimeout(resolve, HEAD_POLL_MS);
      const onAbort = () => {
        clearTimeout(timer);
        reject(abortError());
      };
      if (signal?.aborted) {
        onAbort();
        return;
      }
      signal?.addEventListener?.("abort", onAbort, { once: true });
    });
  }
  throw new Error("Timed out waiting for download to become READY");
};

const parseTotalFromContentRange = (contentRange) => {
  const m = contentRange && String(contentRange).match(/\/(\d+)\s*$/);
  const n = m ? Number(m[1]) : NaN;
  return Number.isFinite(n) ? n : null;
};

const progressInfo = (offset, knownTotal, phase) => ({
  progress: knownTotal > 0 ? Math.min(100, Math.round((offset / knownTotal) * 100)) : 0,
  loaded: offset,
  total: knownTotal || 0,
  phase,
});

/** Fetch file in Range chunks; on network loss, retry same offset when online. */
const fetchFileByRanges = async ({
  hiAxios,
  downloadUrl,
  total,
  chunkSize,
  onProgress,
  controls,
}) => {
  const parts = [];
  let offset = 0;
  let knownTotal = total;
  const size = chunkSize > 0 ? chunkSize : DEFAULT_CHUNK_SIZE;
  let networkAttempt = 0;
  const report = (phase) => onProgress?.(progressInfo(offset, knownTotal, phase));

  while (knownTotal == null || offset < knownTotal) {
    await controls.waitIfPaused();
    const end =
      knownTotal != null
        ? Math.min(offset + size - 1, knownTotal - 1)
        : offset + size - 1;

    try {
      const res = await hiAxios.instance(
        fileDownloadAxiosConfig({
          url: downloadUrl,
          method: "GET",
          responseType: "arraybuffer",
          headers: { Range: `bytes=${offset}-${end}` },
          signal: controls?.signal,
          validateStatus: (s) => s === 200 || s === 206 || s === 416,
        }),
      );
      networkAttempt = 0;
      if (res.status === 416) break;

      if (knownTotal == null) {
        knownTotal = parseTotalFromContentRange(header(res.headers, "content-range"));
      }

      if (controls.isCancelled()) throw abortError();

      const len = res.data?.byteLength || 0;
      if (len === 0) break;
      parts.push(res.data);
      offset += len;
      report(controls.isPaused() ? "paused" : "downloading");

      if (knownTotal == null && (len < size || res.status === 200)) break;
    } catch (e) {
      if (controls.isCancelled() || e?.name === "AbortError") throw e;
      if (!isRetryableNetworkError(e)) throw e;

      networkAttempt += 1;
      report("reconnecting");
      await waitUntilOnline(controls, () => report("reconnecting"), abortError);
      await sleepMs(
        Math.min(30000, 1000 * Math.pow(2, Math.min(networkAttempt - 1, 5))),
        controls,
        abortError,
      );
    }
  }
  return new Blob(parts);
};

/** Prepare → wait READY (SSE or HEAD) → Range download → save. */
export const runResumableDownload = async ({
  dispatch,
  hiAxios,
  prepareUrl,
  prepareBody,
  fallbackFileName,
  callback,
  Notify,
  chunkSize,
  requestId,
  useStreamResponse = false,
  handleDownloadFile,
}) => {
  const controls = createDownloadControls();
  const waitAbort = new AbortController();
  let latest = { progress: 0, loaded: 0, total: 0, phase: "preparing" };

  // Do not open the progress modal until prepare succeeds (avoids flash on early errors).
  let reportToUi = false;
  const emit = (patch = {}) => {
    latest = { ...latest, ...patch };
    if (!reportToUi) return;
    callback?.({
      ...latest,
      resumable: true,
      pause: () => {
        controls.pause();
        emit({ phase: "paused" });
      },
      resume: () => {
        controls.resume();
        emit({ phase: "downloading" });
      },
    });
  };

  if (requestId) hiAxios.requestId = requestId;

  const originalAbort = hiAxios.abort.bind(hiAxios);
  hiAxios.abort = (prop) => {
    controls.cancel();
    waitAbort.abort();
    originalAbort(prop);
  };
  hiAxios.pauseDownload = () => {
    controls.pause();
    emit({ phase: "paused" });
  };
  hiAxios.resumeDownload = () => {
    controls.resume();
    emit({ phase: "downloading" });
  };

  try {
    const prepareRes = await hiAxios.instance({
      url: prepareUrl,
      method: "POST",
      data: prepareBody,
      responseType: "text",
      headers: { Accept: "application/json" },
    });

    const payload = parseJsonMaybe(prepareRes.data);
    const ticket = payload.ticket;
    const prepareRequestId = payload.requestId || requestId;
    if (prepareRequestId) hiAxios.requestId = prepareRequestId;

    let downloadUrl = ensureLeadingSlash(
      payload.downloadUrl || (ticket ? `/fileDownload?ticket=${ticket}` : null),
    );
    const eventsUrl = ensureLeadingSlash(
      payload.eventsUrl ||
        (ticket
          ? `/downloadEvents?ticket=${ticket}${
              prepareRequestId ? `&requestId=${encodeURIComponent(prepareRequestId)}` : ""
            }`
          : null),
    );

    if (!ticket || !downloadUrl) {
      Notify?.error?.({
        type: "Backend",
        message:
          payload.message ||
          payload.response?.message ||
          "Resumable prepare did not return a ticket",
      });
      callback?.(null);
      return;
    }

    // Ticket accepted — show progress (cancel available) while artifact is prepared.
    reportToUi = true;
    emit({ progress: 0, loaded: 0, total: 0, phase: "preparing" });

    const ready =
      useStreamResponse && eventsUrl
        ? await waitForTicketEvents({
            dispatch,
            hiAxios,
            eventsUrl,
            signal: waitAbort.signal,
            mapReady: normalizeReady,
          })
        : await waitViaHeadPoll(hiAxios, downloadUrl, waitAbort.signal);

    if (controls.isCancelled() || waitAbort.signal.aborted) throw abortError();

    if (ready.downloadUrl) downloadUrl = ensureLeadingSlash(ready.downloadUrl);

    const fileName = ready.fileName || fallbackFileName || "download";
    if (ready.total === 0) {
      if (controls.isCancelled()) throw abortError();
      handleDownloadFile({ data: new Blob([]) }, fileName);
      callback?.(null);
      return;
    }

    emit({ progress: 0, loaded: 0, total: ready.total || 0, phase: "downloading" });

    const blob = await fetchFileByRanges({
      hiAxios,
      downloadUrl,
      total: ready.total,
      chunkSize,
      controls,
      onProgress: emit,
    });

    if (controls.isCancelled()) throw abortError();

    if (ready.checksum) {
      const actual = await sha256Blob(blob);
      if (actual !== String(ready.checksum).toLowerCase()) {
        throw new Error("Download checksum mismatch — file may be corrupt. Please try again.");
      }
    }

    if (controls.isCancelled()) throw abortError();
    handleDownloadFile({ data: blob }, fileName);
    callback?.(null);
  } catch (e) {
    if (controls.isCancelled()) {
      callback?.(null);
      return;
    }
    throw e;
  }
};
