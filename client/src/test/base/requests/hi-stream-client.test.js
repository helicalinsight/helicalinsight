
const { TextEncoder, TextDecoder } = require("util");
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

import { handleAxiosObject } from "../../../app/helperMethods";
import HIStreamClient from "../../../app/HiStreamClient";

jest.mock("../../../app/helperMethods", () => ({
    handleAxiosObject: jest.fn(),
    responseHandler: jest.fn(),
}));

const makeHiAxios = (overrides = {}) => ({
    source: { token: "mock-cancel-token" },
    controller: { signal: "mock-signal" },
    auth: "mock-auth",
    baseURL: "https://api.example.com",
    successNotification: jest.fn(),
    getRequestId: jest.fn(() => "req-123"),
    instance: {
        get: jest.fn().mockResolvedValue({ data: { status: 1 } }),
    },
    ...overrides,
});

const makeDispatch = (hiAxios) =>
    jest.fn((thunkOrAction) => {
        if (typeof thunkOrAction === "function") {
            thunkOrAction(
                jest.fn(),          // inner dispatch (unused)
                undefined,          // getState (unused)
                () => hiAxios       // services factory
            );
        }
    });

const buildClient = (hiAxiosOverrides = {}) => {
    const hiAxios = makeHiAxios(hiAxiosOverrides);
    const dispatch = makeDispatch(hiAxios);
    handleAxiosObject.mockReturnValue({
        baseURL: hiAxios.baseURL,
        headers: { Authorization: "Bearer token" },
    });
    const client = new HIStreamClient({ dispatch });
    return { client, hiAxios, dispatch };
};


describe("HIStreamClient – constructor", () => {
    it("stores dispatch and calls getHiAxios on construction", () => {
        const { client, dispatch } = buildClient();
        expect(client.dispatch).toBe(dispatch);
        expect(dispatch).toHaveBeenCalledWith(expect.any(Function));
    });

    it("sets hiAxios from the services thunk", () => {
        const { client, hiAxios } = buildClient();
        expect(client.hiAxios).toBe(hiAxios);
    });

    it("initialises controller to null before any request", () => {
        const { client } = buildClient();
        expect(client.controller).toBeNull();
    });
});


describe("getHiAxiosObject", () => {
    it("delegates to handleAxiosObject with correct properties", () => {
        const { client, hiAxios } = buildClient();
        client.getHiAxiosObject();

        expect(handleAxiosObject).toHaveBeenCalledWith(
            expect.objectContaining({
                cancelToken: hiAxios.source.token,
                signal: hiAxios.controller.signal,
                auth: hiAxios.auth,
                baseURL: hiAxios.baseURL,
                successNotification: hiAxios.successNotification,
            })
        );
    });
});


describe("getReqURL", () => {
    it("appends a relative path to the base URL", () => {
        const { client } = buildClient();
        expect(client.getReqURL("users")).toBe("https://api.example.com/users");
    });

    it("strips a leading slash from the relative path", () => {
        const { client } = buildClient();
        expect(client.getReqURL("/users")).toBe("https://api.example.com/users");
    });

    it("appends query params when provided", () => {
        const { client } = buildClient();
        const url = client.getReqURL("search", { q: "hello", page: 2 });
        expect(url).toContain("q=hello");
        expect(url).toContain("page=2");
    });

    it("returns an absolute URL unchanged (no double-slash)", () => {
        const { client } = buildClient();
        const abs = "https://other.com/api/data";
        expect(client.getReqURL(abs)).toBe(abs);
    });

    it("handles http:// absolute URLs", () => {
        const { client } = buildClient();
        const abs = "http://other.com/api";
        expect(client.getReqURL(abs)).toBe(abs);
    });

    it("appends query params to absolute URLs", () => {
        const { client } = buildClient();
        const url = client.getReqURL("https://other.com/api", { key: "val" });
        expect(url).toBe("https://other.com/api?key=val");
    });

    it("ensures baseURL with trailing slash does not create double slash", () => {
        handleAxiosObject.mockReturnValue({ baseURL: "https://api.example.com/" });
        const { client } = buildClient();
        expect(client.getReqURL("users")).toBe("https://api.example.com/users");
    });
});


describe("getHeaders", () => {
    it("merges default and axios headers", () => {
        const { client } = buildClient();
        const headers = client.getHeaders();
        expect(headers["Content-Type"]).toBe("application/x-www-form-urlencoded");
        expect(headers["Authorization"]).toBe("Bearer token");
    });

    it("axios headers override defaults when keys clash", () => {
        handleAxiosObject.mockReturnValue({
            baseURL: "https://api.example.com",
            headers: { "Content-Type": "x-www-form-urlencoded" },
        });
        const { client } = buildClient();
        expect(client.getHeaders()["Content-Type"]).toBe("application/x-www-form-urlencoded");
    });

    it("works when axios returns no headers", () => {
        handleAxiosObject.mockReturnValue({ baseURL: "https://api.example.com" });
        const { client } = buildClient();
        expect(client.getHeaders()).toEqual({
            "Content-Type": "application/x-www-form-urlencoded",
            "Authorization": "Bearer token",
        });
    });
});


describe("getReqId", () => {
    it("returns the request ID from hiAxios.getRequestId", () => {
        const { client } = buildClient();
        expect(client.getReqId()).toBe("req-123");
    });

    it("returns undefined gracefully when getRequestId is absent", () => {
        const { client } = buildClient({ getRequestId: undefined });
        expect(client.getReqId()).toBeUndefined();
    });
});
