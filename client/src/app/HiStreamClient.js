import { message } from "antd";
import notify from "../components/hi-notifications/notify";
import { handleAxiosObject, responseHandler } from "./helperMethods";

class HIStreamClient {
    constructor({ dispatch } = {}) {
        this.dispatch = dispatch;
        this.controller = null;
        this.hiAxios = null;
        this.defaultHeaders = {
            "Content-Type": "application/x-www-form-urlencoded",
        };
        this.getHiAxios();
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

    getHeaders() {
        let hiAxiosObject = this.getHiAxiosObject();
        return {
            ...this.defaultHeaders,
            ...(hiAxiosObject.headers || {})
        }
    }

    getReqId() {
        return this.hiAxios.getRequestId?.();
    }

    async request(method, url, { body, onChunk = () => { } } = {}) {
        this.controller = new AbortController();
        const response = await fetch(url,
            {
                method,
                signal: this.controller.signal,
                headers: this.getHeaders(),
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
                const errorMessage = dataStr ?? "Unknown stream error"
                notify(this.dispatch).error({ message: errorMessage, type: "Network Call" });
                return { event: "error", data: errorMessage };
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
        this.controller.abort();
    };

    post(url, body, options = {}) { return this.request("POST", url, { ...options, body }); }
}

export default HIStreamClient;