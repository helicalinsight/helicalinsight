/** Shared helpers for resumable upload / download (pause, online wait, retry). */

export const abortError = (message = "cancel") => {
  const err = new Error(message);
  err.name = "AbortError";
  return err;
};

export const createTransferControls = (makeAbortError = abortError) => {
  let paused = false;
  let cancelled = false;
  let resumeWaiters = [];
  const abortController = new AbortController();
  const wake = () => {
    const w = resumeWaiters;
    resumeWaiters = [];
    w.forEach((r) => r());
  };
  return {
    pause: () => {
      paused = true;
    },
    resume: () => {
      paused = false;
      wake();
    },
    cancel: () => {
      cancelled = true;
      paused = false;
      if (!abortController.signal.aborted) {
        abortController.abort();
      }
      wake();
    },
    signal: abortController.signal,
    isPaused: () => paused,
    isCancelled: () => cancelled,
    waitIfPaused: async () => {
      while (paused && !cancelled) {
        await new Promise((r) => resumeWaiters.push(r));
      }
      if (cancelled) throw makeAbortError();
    },
  };
};

/**
 * @param {object} [opts]
 * @param {boolean} [opts.includeCanceled] treat AbortError/ERR_CANCELED as retryable (caller checks isCancelled)
 * @param {boolean} [opts.retryHttp400] treat HTTP 400 as retryable (incomplete upload chunk)
 */
export const isRetryableNetworkError = (
  e,
  { includeCanceled = false, retryHttp400 = false } = {},
) => {
  if (!e) return false;
  if (e.message === "cancel") return false;
  if (e.name === "AbortError" && e.message === "cancel") return false;
  if (e.code === "ERR_NETWORK" || e.message === "Network Error") return true;
  if (e.name === "AbortError" || e.code === "ERR_CANCELED") return includeCanceled;
  if (e.isAxiosError && !e.response) return true;
  const status = e.response?.status;
  if (retryHttp400 && status === 400) return true;
  return status === 502 || status === 503 || status === 504;
};

export const sleepMs = (ms, controls, makeAbortError = abortError) =>
  new Promise((resolve, reject) => {
    const timer = setTimeout(() => {
      clearInterval(poll);
      if (controls?.isCancelled()) reject(makeAbortError());
      else resolve();
    }, ms);
    const poll = setInterval(() => {
      if (controls?.isCancelled()) {
        clearTimeout(timer);
        clearInterval(poll);
        reject(makeAbortError());
      }
    }, 250);
  });

export const waitUntilOnline = (controls, onWaiting, makeAbortError = abortError) => {
  if (typeof navigator === "undefined" || navigator.onLine) return Promise.resolve();
  onWaiting?.();
  return new Promise((resolve, reject) => {
    const done = (fn, arg) => {
      window.removeEventListener("online", onOnline);
      clearInterval(poll);
      fn(arg);
    };
    const onOnline = () => {
      if (controls?.isCancelled()) done(reject, makeAbortError());
      else done(resolve);
    };
    window.addEventListener("online", onOnline);
    const poll = setInterval(() => {
      if (controls?.isCancelled()) done(reject, makeAbortError());
      else if (navigator.onLine) onOnline();
    }, 500);
  });
};

/** Status line for transfer progress UI (upload / download). */
export const transferPhaseMessage = (phase, kind = "download") => {
  const noun = kind === "upload" ? "Upload" : "Download";
  const gerund = kind === "upload" ? "uploading" : "downloading";
  if (phase === "paused") return `${noun} paused`;
  if (phase === "reconnecting") return "Network lost — reconnecting and will resume…";
  if (phase === "preparing") return `Preparing ${gerund}…`;
  return `Please wait while ${gerund}`;
};
