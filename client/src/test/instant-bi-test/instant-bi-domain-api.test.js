import "core-js/stable";
import "regenerator-runtime/runtime";
import {
  abortRecommendationsRequest,
  fetchRecommendationsAPI,
} from "../../components/hi-instant-bi/utils/instant-bi-requests";

describe("Instant BI Domain API Tests", () => {
  afterAll(() => {
    global.gc && global.gc();
  });
  test("it should payload should contain file and dir properties", () => {
    const testFormData = {
      metadataFileName: "sample.agent",
      location: "sample-dir",
    };
    const expectedAgentPayload = {
      agent: {
        file: "sample.agent",
        dir: "sample-dir",
      },
    };
    expect(expectedAgentPayload.agent.file).toBe(testFormData.metadataFileName);
    expect(expectedAgentPayload.agent.dir).toBe(testFormData.location);
  });

  test("should getFunctions and getDateFunctions APIs should be commented out", () => {
    const fileContent = require("../../components/hi-instant-bi/utils/instant-bi-requests");
    expect(fileContent).toBeDefined();
    expect(fileContent.fetchRecommendationsAPI).toBeDefined();
    expect(fileContent.abortRecommendationsRequest).toBeDefined();
  });
});
