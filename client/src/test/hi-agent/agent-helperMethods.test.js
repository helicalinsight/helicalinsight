const warning = jest.fn();

jest.mock("../../components/hi-notifications/notify", () => () => ({
  warning,
}));

import {
  validateAgentDomainAndDescription,
  validateAgentSaveInput,
} from "../../components/hi-agent/utils/agent-helperMethods";

describe("validateAgentDomainAndDescription", () => {
  beforeEach(() => {
    warning.mockClear();
  });

  it("shows a warning when domain and agent description are empty", () => {
    const dispatch = jest.fn();

    const isValid = validateAgentDomainAndDescription({
      cubeFieldsData: { domainName: "", cubeDescription: "" },
      dispatch,
    });

    expect(isValid).toBe(false);
    expect(warning).toHaveBeenCalledWith({
      message: "Domain and Agent Description are required before saving.",
      type: "Front End",
    });
  });

  it("returns true when domain and agent description are present", () => {
    const dispatch = jest.fn();

    const isValid = validateAgentDomainAndDescription({
      cubeFieldsData: {
        domainName: "Sales",
        cubeDescription: "Sales agent",
      },
      dispatch,
    });

    expect(isValid).toBe(true);
    expect(warning).not.toHaveBeenCalled();
  });
});

describe("validateAgentSaveInput", () => {
  beforeEach(() => {
    warning.mockClear();
  });

  it("validates domain and description from raw JSON content", () => {
    const dispatch = jest.fn();

    const isValid = validateAgentSaveInput({
      editorContent: JSON.stringify({
        metadata_info: { metadata: { domain: [], description: "" } },
      }),
      isRawJsonView: true,
      dispatch,
    });

    expect(isValid).toBe(false);
    expect(warning).toHaveBeenCalled();
  });
});
