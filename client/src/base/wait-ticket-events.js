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
          if (parsed.event === "ready" || parsed.data?.status === "READY") {
            finish(resolve, mapReady(parsed.data));
            return;
          }
          if (parsed.event === "aborted" || parsed.data?.status === "ABORTED") {
            finish(reject, abortError(parsed.data?.message || "cancel"));
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
