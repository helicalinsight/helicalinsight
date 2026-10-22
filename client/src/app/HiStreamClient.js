import { message } from "antd";
import notify from "../components/hi-notifications/notify";
import { handleAxiosObject, responseHandler } from "./helperMethods";

class HIStreamClient {
    constructor({ dispatch, hiAxios } = {}) {
        this.dispatch = dispatch;
        this.controller = null;
        this.hiAxios = hiAxios || null;
        this.defaultHeaders = {
            "Content-Type": "application/x-www-form-urlencoded",
            "Accept": "text/event-stream",
        };
        if (!this.hiAxios && dispatch) {
            this.getHiAxios();
        }
    }

    getHiAxios() {
        this.dispatch((dispatch, _, services) => {
            this.hiAxios = services(dispatch);
        });
    }

    getHiAxiosObject() {
        return handleAxiosObject({
            cancelToken: this.hiAxios.source.token,
            signal: this.hiAxios.controller.signal,
            auth: this.hiAxios.auth,
            baseURL: this.hiAxios.baseURL,
            dispatch: this.dispatch,
            successNotification: this.hiAxios.successNotification
        })
    }
    getReqURL(url, params = {}) {
        let hiAxiosObject = this.getHiAxiosObject();

        const queryString = new URLSearchParams(params).toString();
        const suffix = queryString ? "?" + queryString : "";

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return `${url}${suffix}`;
        }

        let baseURL = hiAxiosObject.baseURL || "";
        const base = baseURL.endsWith("/") ? baseURL : baseURL + "/";
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        return `${base}${url}${suffix}`;
    }

    getHeaders({ includeContentType = true } = {}) {
        let hiAxiosObject = this.getHiAxiosObject();
        return {
            ...(includeContentType
                ? this.defaultHeaders
                : { Accept: "text/event-stream" }),
            ...(hiAxiosObject.headers || {}),
            Accept: "text/event-stream",
        }
    }

    getReqId() {
        return this.hiAxios.getRequestId?.();
    }

    async request(method, url, { body, onChunk = () => { }, signal } = {}) {
        this.controller = new AbortController();
        if (signal) {
            if (signal.aborted) {
                this.controller.abort();
            } else {
                signal.addEventListener("abort", () => this.controller.abort(), { once: true });
            }
        }
        const response = await fetch(url,
            {
                method,
                signal: this.controller.signal,
                credentials: "include",
                headers: this.getHeaders({ includeContentType: method !== "GET" }),
                body
            }
        );

        if (!response.ok) {
            throw new Error(`Request failed: ${response.status} ${response.statusText}`);
        }
        responseHandler({ dispatch: this.dispatch });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let buffer = "";

        const parseEvent = (rawEvent) => {
            if (!rawEvent.trim()) return null;

            const type = rawEvent.match(/^event:\s*(.+)$/m)?.[1]?.trim() ?? null;

            const dataLines = [...rawEvent.matchAll(/^data:\s*(.+)$/gm)].map(m => m[1].trim());
            const dataStr = dataLines.length > 0 ? dataLines.join("\n") : null;

            if (type === "error") {
                let payload = dataStr;
                try {
                    payload = dataStr ? JSON.parse(dataStr) : null;
                } catch {
                    payload = dataStr;
                }
                // Soft progress channel "error" (has channel/path) is not a fatal stream failure.
                const isProgressError =
                    payload &&
                    typeof payload === "object" &&
                    (payload.channel === "error" || payload.path);
                if (!isProgressError) {
                    const errorMessage =
                        (payload && typeof payload === "object" && payload.error) ||
                        dataStr ||
                        "Unknown stream error";
                    notify(this.dispatch).error({ message: errorMessage, type: "Network Call" });
                }
                return { event: "error", data: payload };
            }

            try {
                const data = dataStr ? JSON.parse(dataStr) : null;
                return { event: type, data };
            } catch {
                console.warn("[request] Failed to parse SSE data:", dataStr);
                return null;
            }
        };

        try {
            while (true) {
                const { done, value } = await reader.read();
                if (done) {
                    if (buffer.trim()) {
                        const parsed = parseEvent(buffer);
                        if (parsed) onChunk(parsed);
                    }
                    break;
                }
                buffer += decoder.decode(value, { stream: true });
                const events = buffer.split("\n\n");
                buffer = events.pop() ?? "";

                for (const rawEvent of events) {
                    const parsed = parseEvent(rawEvent);
                    if (parsed) onChunk(parsed);
                }
            }
        } catch (err) {
            if (err.name !== "AbortError") throw err;
        } finally {
            reader.releaseLock();
        }
    }

    /** Abort only the SSE fetch (does not call /cancelRequest). */
    abortStream = () => {
        this.controller?.abort();
    };

    abort = (prop = {}) => {
        let { onSuccess = () => { } } = prop;
        this.hiAxios.instance
            .get("/cancelRequest", {
                params: { requestId: this.getReqId() },
            })
            .then((res) => {
                const data = typeof res.data === "string" ? JSON.parse(res.data) : res.data;
                onSuccess(data)
            })
            .catch((error) => {
                console.log(error);
            });
        this.abortStream();
    };

    get(url, options = {}) { return this.request("GET", url, options); }

    post(url, body, options = {}) { return this.request("POST", url, { ...options, body }); }
}

export default HIStreamClient;