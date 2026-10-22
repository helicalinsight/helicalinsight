import HIStreamClient from "../app/HiStreamClient";

const abortError = (message = "cancel") => {
  const err = new Error(message);
  err.name = "AbortError";
  return err;
};

/** Soft progress channel "error" (has channel/path) is not a fatal stream failure. */
const isFatalStreamError = (parsed) =>
  parsed?.event === "error" &&
  parsed.data?.channel !== "error" &&
  !parsed.data?.path;

/**
 * Shared SSE wait for ticket READY/ABORTED (upload, download, import console).
 */
export const waitForTicketEvents = ({
  dispatch,
  hiAxios,
  eventsUrl,
  signal,
  onEvent,
  mapReady = (data) => data || {},
}) => {
  const client = new HIStreamClient({ dispatch, hiAxios });
  const url = client.getReqURL((eventsUrl || "").replace(/^\//, ""));

  return new Promise((resolve, reject) => {
    let settled = false;
    let lastProgressError = null;
    const finish = (fn, arg) => {
      if (settled) return;
      settled = true;
      client.abortStream();
      fn(arg);
    };

    client
      .get(url, {
        signal,
        onChunk: (parsed) => {
          if (!parsed) return;
          // Soft progress errors arrive before ABORTED — reuse that message for the UI.
          const events =
            parsed.event === "batch" && Array.isArray(parsed.data)
              ? parsed.data
              : [parsed.data];
          events.forEach((d) => {
            if (d?.channel === "error" && d.message) lastProgressError = d.message;
          });
          if (parsed.event === "ready" || parsed.data?.status === "READY") {
            finish(resolve, mapReady(parsed.data));
            return;
          }
          if (parsed.event === "aborted" || parsed.data?.status === "ABORTED") {
            // Prefer progress-stream error, else aborted event message (e.g. export exception).
            const msg = lastProgressError || parsed.data?.message;
            finish(
              reject,
              !msg || msg === "cancel" ? abortError("cancel") : new Error(msg),
            );
            return;
          }
          if (isFatalStreamError(parsed)) {
            finish(
              reject,
              new Error(parsed.data?.message || parsed.data?.error || "Stream error"),
            );
            return;
          }
          onEvent?.(parsed);
        },
      })
      .then(() => {
        if (!settled) {
          if (signal?.aborted) finish(reject, abortError());
          else finish(reject, new Error("Events stream closed before READY"));
        }
      })
      .catch((e) => {
        if (settled) return;
        if (e?.name === "AbortError" || signal?.aborted) finish(reject, abortError());
        else finish(reject, e);
      });
  });
};
