import qs from "qs";
import { v4 as uuidv4 } from "uuid";
import { waitForTicketEvents } from "./wait-ticket-events";
import {
  abortError,
  createTransferControls,
  isRetryableNetworkError,
  sleepMs,
  waitUntilOnline,
} from "./resumable-transfer";

const DEFAULT_CHUNK_SIZE = 1024 * 1024;
/** Same cadence as resumable-download HEAD poll when streamResponse is off. */
const PREPARE_POLL_MS = 2000;
const PREPARE_POLL_MAX_ATTEMPTS = 450;

const uploadRetryOpts = { includeCanceled: true, retryHttp400: true };

export const isResumableUploadEnabled = (getState) => {
  const v = getState?.()?.app?.applicationSettingsData?.resumableUpload;
  return v !== false && v !== "false";
};

export const resolveUploadChunkSize = (getState) => {
  const n = Number(getState?.()?.app?.applicationSettingsData?.uploadChunkSize);
  return Number.isFinite(n) && n > 0 ? Math.floor(n) : DEFAULT_CHUNK_SIZE;
};

export const formDataHasFile = (formData) => {
  if (!formData || typeof formData.entries !== "function") return false;
  for (const [, value] of formData.entries()) {
    if (typeof File !== "undefined" && value instanceof File) return true;
    if (
      typeof Blob !== "undefined" &&
      value instanceof Blob &&
      !(typeof File !== "undefined" && value instanceof File)
    ) {
      return true;
    }
  }
  return false;
};

const extractUploadParts = (formData) => {
  let file = null;
  const fields = {};
  for (const [key, value] of formData.entries()) {
    if (
      (typeof File !== "undefined" && value instanceof File) ||
      (typeof Blob !== "undefined" && value instanceof Blob)
    ) {
      if (!file || key === "file") {
        file = value;
      }
    } else {
      fields[key] = value;
    }
  }
  return { file, fields };
};

/** Read + hash in slices so large files can report prepare progress (0–1% of total). */
const sha256Blob = async (blob, onBytesRead) => {
  const total = blob.size || 0;
  const sliceSize = 4 * 1024 * 1024;
  const buffer = new Uint8Array(total);
  let offset = 0;
  while (offset < total) {
    const end = Math.min(offset + sliceSize, total);
    const chunk = new Uint8Array(await blob.slice(offset, end).arrayBuffer());
    buffer.set(chunk, offset);
    offset = end;
    if (typeof onBytesRead === "function") onBytesRead(offset, total);
  }
  const hash = await crypto.subtle.digest(
    "SHA-256",
    total > 0 ? buffer : await blob.arrayBuffer(),
  );
  return Array.from(new Uint8Array(hash))
    .map((b) => b.toString(16).padStart(2, "0"))
    .join("");
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

const normalizeTargetUrl = (url) => {
  if (!url) return "";
  let t = String(url).trim();
  const q = t.indexOf("?");
  if (q >= 0) t = t.substring(0, q);
  if (!t.startsWith("/") && !t.startsWith("http")) t = `/${t}`;
  try {
    if (t.startsWith("http")) {
      t = new URL(t).pathname;
    }
  } catch (e) {
    /* keep */
  }
  return t;
};

const emitProgress = (onUploadProgress, loaded, total, phase = "uploading") => {
  if (typeof onUploadProgress !== "function") return;
  onUploadProgress({
    loaded,
    total,
    lengthComputable: total > 0,
    progress: total > 0 ? Math.min(100, Math.round((loaded * 100) / total)) : 0,
    phase,
  });
};

const bridgeCancelToken = (cancelToken, onCancel) => {
  if (!cancelToken) return () => {};
  let settled = false;
  const run = () => {
    if (settled) return;
    settled = true;
    onCancel();
  };
  if (cancelToken.promise && typeof cancelToken.promise.then === "function") {
    cancelToken.promise.then(run, run);
  }
  return () => {
    settled = true;
  };
};

const ensureLeadingSlash = (url) =>
  !url || url.startsWith("http") || url.startsWith("/") ? url : `/${url}`;

/**
 * Non-SSE wait (mirrors download waitViaHeadPoll): re-POST prepare until READY + importResult.
 */
const waitViaPreparePoll = async ({ hiAxios, prepareUrl, prepareBody, signal }) => {
  for (let attempt = 0; attempt < PREPARE_POLL_MAX_ATTEMPTS; attempt++) {
    if (signal?.aborted) throw abortError();
    try {
      const res = await hiAxios.instance.post(prepareUrl, qs.stringify(prepareBody), {
        signal,
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        validateStatus: (s) => s < 500,
      });
      if (res.status === 403) throw new Error("Upload ticket forbidden (HTTP 403)");
      if (res.status === 410) throw new Error("Upload ticket not available (HTTP 410)");
      const payload = parseJsonMaybe(res.data);
      const status = payload.status || payload.response?.status;
      if (status === "ABORTED" || status === "EXPIRED") {
        throw abortError(payload.message || "Upload aborted");
      }
      if (status === "READY" || payload.importResult != null) {
        return {
          status: "READY",
          importResult: payload.importResult,
          ticket: payload.ticket,
          requestId: payload.requestId,
        };
      }
      // PREPARING / other → keep polling
    } catch (e) {
      if (signal?.aborted) throw abortError();
      if (
        e?.message?.includes("forbidden") ||
        e?.message?.includes("not available") ||
        attempt === PREPARE_POLL_MAX_ATTEMPTS - 1
      ) {
        throw e;
      }
      // Network blips (incl. AbortError / ERR_CANCELED without user cancel) → keep polling
    }
    await new Promise((resolve, reject) => {
      const timer = setTimeout(resolve, PREPARE_POLL_MS);
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
  throw new Error("Timed out waiting for upload to become READY");
};

const invokeSuccess = (callback, payload) => {
  if (typeof callback !== "function") return;
  if (payload && typeof payload === "object" && payload.status !== undefined) {
    const { status, response, lastModified } = payload;
    if (lastModified && response && typeof response === "object") {
      response.lastModified = lastModified;
    }
    if (status) callback(response);
    else callback(response != null ? response : payload);
    return;
  }
  callback(payload);
};

/**
 * Prepare via existing import URL (resumable=true) → chunks → auto-finalize on last chunk.
 * When streamResponse: wait via SSE (/downloadEvents). Otherwise: poll prepare (like download HEAD).
 * Resume: re-POST same import URL. Abort via hiAxios.abort → /cancelRequest.
 */
export const runResumableUpload = async ({
  dispatch,
  hiAxios,
  targetUrl,
  formData,
  config = {},
  callback,
  errback,
  chunkSize,
  requestId,
  onEvent,
  useStreamResponse = false,
}) => {
  const controls = createTransferControls(abortError);
  const waitAbort = new AbortController();
  const { file, fields } = extractUploadParts(formData);

  if (!file) {
    throw new Error("No file found in FormData for resumable upload");
  }

  const rid = requestId || hiAxios.requestId || uuidv4();
  hiAxios.requestId = rid;
  const prepareUrl = normalizeTargetUrl(targetUrl);

  const originalAbort = hiAxios.abort.bind(hiAxios);
  hiAxios.abort = (prop) => {
    controls.cancel();
    waitAbort.abort();
    originalAbort(prop);
  };

  const unbridge = bridgeCancelToken(config.cancelToken, () => {
    hiAxios.abort();
  });

  const size = chunkSize > 0 ? chunkSize : DEFAULT_CHUNK_SIZE;
  const total = file.size || 0;
  let networkAttempt = 0;
  let lastLoaded = 0;
  const onProgress = config.onUploadProgress;

  /** Monotonic loaded so prepare (~1%) is not wiped when the first chunk reports 0. */
  const report = (loaded, phase) => {
    if (typeof loaded === "number" && loaded > lastLoaded) {
      lastLoaded = loaded;
    }
    const p = phase || (controls.isPaused() ? "paused" : "uploading");
    emitProgress(onProgress, lastLoaded, total, p);
  };

  hiAxios.pauseUpload = () => {
    controls.pause();
    report(lastLoaded, "paused");
  };
  hiAxios.resumeUpload = () => {
    controls.resume();
    report(lastLoaded, "uploading");
  };

  /** Map prepare (hash + init) into ~0–1% so the bar appears before the first chunk. */
  const emitPrepareProgress = (bytesRead) => {
    if (total <= 0) {
      report(0, "preparing");
      return;
    }
    const fraction = Math.min(1, Math.max(0, bytesRead / total));
    report(Math.floor(fraction * total * 0.01), "preparing");
  };

  const buildPrepareBody = async () => {
    const checksumSha256 = await sha256Blob(file, (read) => {
      if (controls.isCancelled()) throw abortError();
      emitPrepareProgress(read);
    });
    return {
      ...fields,
      resumable: true,
      fileName: file.name || "upload.bin",
      fileSize: total,
      checksumSha256,
      requestId: rid,
      contentType: file.type || "application/octet-stream",
    };
  };

  // Declared outside try so cancel during chunks can settle SSE without an unhandled rejection.
  let readyPromise = null;

  try {
    report(0, "preparing");
    if (controls.isCancelled()) throw abortError();
    const prepareBody = await buildPrepareBody();
    if (controls.isCancelled()) throw abortError();
    emitPrepareProgress(total);

    const initRes = await hiAxios.instance.post(prepareUrl, qs.stringify(prepareBody), {
      signal: waitAbort.signal,
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
    });
    let initPayload = parseJsonMaybe(initRes.data);
    const ticket = initPayload.ticket;
    const eventsUrl = ensureLeadingSlash(
      initPayload.eventsUrl ||
        (ticket
          ? `/downloadEvents?ticket=${ticket}&requestId=${encodeURIComponent(rid)}`
          : null),
    );
    const serverChunkSize =
      Number(initPayload.chunkSize) > 0 ? Number(initPayload.chunkSize) : size;
    const totalChunks =
      Number(initPayload.totalChunks) > 0
        ? Number(initPayload.totalChunks)
        : total === 0
          ? 1
          : Math.ceil(total / serverChunkSize);

    if (!ticket) {
      throw new Error(initPayload.message || "Resumable prepare did not return a ticket");
    }

    const received = new Set(
      Array.isArray(initPayload.receivedChunks)
        ? initPayload.receivedChunks.map(Number)
        : [],
    );

    const refreshViaPrepare = async () => {
      // No waitAbort.signal — same as download Ranges; cancel checked via controls.
      const res = await hiAxios.instance.post(prepareUrl, qs.stringify(prepareBody), {
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
      });
      initPayload = parseJsonMaybe(res.data);
      received.clear();
      (initPayload.receivedChunks || []).forEach((i) => received.add(Number(i)));
    };

    const recomputeAckedBytes = () => {
      let bytes = 0;
      for (let i = 0; i < totalChunks; i++) {
        if (received.has(i)) {
          const s = i * serverChunkSize;
          bytes += Math.min(serverChunkSize, Math.max(0, total - s));
        }
      }
      return bytes;
    };

    const reportReconnecting = () => report(lastLoaded, "reconnecting");

    /** SSE must stay subscribed before finalize; reconnect after network drop (like download Ranges). */
    const waitReadyWithReconnect = async () => {
      let attempt = 0;
      while (true) {
        if (controls.isCancelled()) throw abortError();
        try {
          return await waitForTicketEvents({
            dispatch,
            hiAxios,
            eventsUrl,
            signal: waitAbort.signal,
            onEvent,
          });
        } catch (e) {
          if (controls.isCancelled() || waitAbort.signal.aborted) throw abortError();
          // User cancel or terminal ticket abort/stream error — do not reconnect-loop.
          if (e?.name === "AbortError" || !isRetryableNetworkError(e)) throw e;
          attempt += 1;
          await waitUntilOnline(controls, reportReconnecting);
          await sleepMs(
            Math.min(30000, 1000 * Math.pow(2, Math.min(attempt - 1, 5))),
            controls,
          );
        }
      }
    };

    readyPromise =
      useStreamResponse && eventsUrl ? waitReadyWithReconnect() : null;

    let ackedBytes = recomputeAckedBytes();
    report(
      Math.max(ackedBytes, total > 0 ? Math.floor(total * 0.01) : 0),
      "uploading",
    );

    for (let index = 0; index < totalChunks; index++) {
      await controls.waitIfPaused();
      if (controls.isCancelled()) throw abortError();
      if (received.has(index)) continue;

      const start = index * serverChunkSize;
      const end = Math.min(start + serverChunkSize, total);
      const blob = file.slice(start, end);

      while (true) {
        await controls.waitIfPaused();
        if (controls.isCancelled()) throw abortError();
        try {
          // No waitAbort.signal on chunks — mirrors download Range GETs so disconnect ≠ cancel.
          await hiAxios.instance.post(
            `/uploadChunk?ticket=${encodeURIComponent(ticket)}&index=${index}`,
            blob,
            {
              headers: { "Content-Type": "application/octet-stream" },
              onUploadProgress: (evt) => {
                const chunkLoaded = evt.loaded || 0;
                report(
                  ackedBytes + chunkLoaded,
                  controls.isPaused() ? "paused" : "uploading",
                );
              },
              transformRequest: [(data) => data],
            },
          );
          networkAttempt = 0;
          received.add(index);
          ackedBytes += end - start;
          report(ackedBytes, controls.isPaused() ? "paused" : "uploading");
          break;
        } catch (e) {
          if (controls.isCancelled()) throw abortError();
          if (!isRetryableNetworkError(e, uploadRetryOpts)) throw e;
          networkAttempt += 1;
          await waitUntilOnline(controls, reportReconnecting);
          await sleepMs(
            Math.min(30000, 1000 * Math.pow(2, Math.min(networkAttempt - 1, 5))),
            controls,
          );
          try {
            await refreshViaPrepare();
          } catch (refreshErr) {
            if (controls.isCancelled()) throw abortError();
            if (!isRetryableNetworkError(refreshErr, uploadRetryOpts)) throw refreshErr;
            continue;
          }
          if (received.has(index)) {
            ackedBytes = recomputeAckedBytes();
            report(ackedBytes, "uploading");
            break;
          }
        }
      }
    }

    if (controls.isCancelled()) throw abortError();
    if (total > 0) {
      report(Math.max(ackedBytes, Math.floor(total * 0.99)), "uploading");
    }
    const ready =
      readyPromise != null
        ? await readyPromise
        : await waitViaPreparePoll({
            hiAxios,
            prepareUrl,
            prepareBody,
            signal: waitAbort.signal,
          });

    report(total, "uploading");
    const importResult = parseJsonMaybe(ready.importResult ?? ready);

    if (importResult && importResult.status !== undefined && !importResult.status) {
      if (typeof errback === "function") errback(importResult.response || importResult);
      return;
    }
    invokeSuccess(callback, importResult);
  } catch (e) {
    if (controls.isCancelled() || e?.message === "cancel") {
      if (typeof errback === "function") errback(abortError("cancel"));
      return;
    }
    if (typeof errback === "function") errback(e);
  } finally {
    unbridge();
    // Cancel/pause path often exits via chunk loop before await readyPromise.
    if (readyPromise) {
      readyPromise.catch(() => {});
    }
  }
};
