import agentRequests from "../../base/requests/agent.requests";
import {
  setAgentMetadataTablesData,
  setAgentSemanticTypes,
} from "../../redux/actions/agent.actions";

jest.mock("../../base/service", () => ({
  postRequest: jest.fn(),
}));

jest.mock("../../redux/actions/agent.actions", () => ({
  setAgentMetadataTablesData: jest.fn((payload) => ({
    type: "AGENT_METADATA_TABLES_DATA",
    payload,
  })),
  setAgentSemanticTypes: jest.fn((payload) => ({
    type: "AGENT_SEMANTIC_TYPES",
    payload,
  })),
}));

const { postRequest } = require("../../base/service");
describe("agent.requests", () => {
  const dispatch = jest.fn();
  const mockAbort = jest.fn();
  const apiInstance = { abort: mockAbort };
  beforeEach(() => {
    jest.clearAllMocks();
    postRequest.mockImplementation((dispatchArg, uri, formData, callback, errback) => {
      callback({ tables: [] });
      return apiInstance;
    });
  });

  it("shoudl getAgentMetadataTablesData returns an abortablee request instance", () => {
    const { getAgentMetadataTablesData } = agentRequests(dispatch);
    const result = getAgentMetadataTablesData({
      path: "meta-dir",
      fileName: "Sample.metadata",
    });
    expect(postRequest).toHaveBeenCalledWith(
      dispatch,
      "adhoc/metadata/get",
      { location: "meta-dir", metadataFileName: "Sample.metadata" },
      expect.any(Function),
      expect.any(Function),
    );
    expect(result).toBe(apiInstance);
    expect(dispatch).toHaveBeenCalledWith(
      setAgentMetadataTablesData({ tables: [] }),
    );
  });

  it("it shoudl getAgentMetadataTablesData invokess optional callback & errback hook", () => {
    const callback = jest.fn();
    const errback = jest.fn();
    postRequest.mockImplementation((dispatchArg, uri, formData, success, error) => {
      success({ tables: ["t1"] });
      error(new Error("failed"));
      return apiInstance;
    });
    const { getAgentMetadataTablesData } = agentRequests(dispatch);
    getAgentMetadataTablesData({
      path: "meta-dir",
      fileName: "Sample.metadata",
      callback,
      errback,
    });
    expect(callback).toHaveBeenCalledWith({ tables: ["t1"] });
    expect(errback).toHaveBeenCalled();
  });

  it("getSemanticTypes fetches and stores grouped semantic type options", () => {
    const semanticTypes = [
      {
        label: "Temporal",
        value: "TEMPORAL",
        options: [{ label: "Date", value: "DATE" }],
      },
    ];
    const response = { semanticTypes };
    postRequest.mockImplementation((dispatchArg, uri, formData, callback) => {
      callback(response);
      return apiInstance;
    });
    const { getSemanticTypes } = agentRequests(dispatch);
    const callback = jest.fn();
    const result = getSemanticTypes(callback);
    expect(postRequest).toHaveBeenCalledWith(
      dispatch,
      "content/static/getContents",
      { contentId: "Static/semantictypes" },
      expect.any(Function),
      expect.any(Function),
    );
    expect(result).toBe(apiInstance);
    expect(dispatch).toHaveBeenCalledWith(setAgentSemanticTypes(semanticTypes));
    expect(callback).toHaveBeenCalledWith(response);
  });
});
