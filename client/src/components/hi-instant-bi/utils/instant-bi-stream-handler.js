/**
 * Small SSE handler for InstantBI activity streams.
 * Java wraps complete as { status: 1, response } — same as buffered JSON.
 */
export class InstantBIStreamHandler {
  constructor({ onProgress, onComplete, onError } = {}) {
    this.onProgress = onProgress;
    this.onComplete = onComplete;
    this.onError = onError;
  }

  handleChunk(chunk = {}) {
    const { event, data } = chunk;
    if (event === "progress") {
      this.onProgress?.(data || {});
      return;
    }
    if (event === "complete") {
      this.onComplete?.(this.unwrapComplete(data));
      return;
    }
    if (event === "error") {
      this.onError?.(this.parseError(data));
    }
  }

  unwrapComplete(data) {
    if (data && typeof data === "object" && data.response != null && data.status !== undefined) {
      return data.response;
    }
    return data;
  }

  parseError(data) {
    if (data && typeof data === "object") {
      return data;
    }
    if (typeof data === "string") {
      try {
        return JSON.parse(data);
      } catch {
        return { error: data };
      }
    }
    return { error: "Unknown stream error" };
  }
}

export default InstantBIStreamHandler;
