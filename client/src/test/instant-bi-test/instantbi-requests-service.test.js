import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { hiMockAxios } from "../../app/mock-axios";
import instantBI, { uriConfig } from "../../base/requests/instantbi.requests";
import reducers from "../../redux";
import Base64 from "../../base/utils/Base64";

describe("Instant BI Requests Service Tests", () => {
  let store;
  let dispatch;
  let instantBIInstance;

  beforeEach(() => {
    store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: {
            extraArgument: hiMockAxios,
          },
          immutableCheck: false,
          serializableCheck: false,
        }),
    });
    dispatch = store.dispatch;
    instantBIInstance = instantBI(dispatch);
  });
  afterAll(() => {
    global.gc && global.gc();
  });
  test("should uri config should have correct endpoints", () => {
    expect(uriConfig.instantBIDomain).toBe("ai/recommendation/domain");
    expect(uriConfig.instantBIRecommendation).toBe("ai/recommendation/analyst");
    expect(uriConfig.adhocMetadataGet).toBe("instantbi/instant/getAgent");
    expect(uriConfig.adhocMetadataGetFunctions).toBe("instantbi/aiagent/getFunctions");
    expect(uriConfig.adhocReportGetDerivedFormdata).toBe("instantbi/report/getDerivedFormdata");
    expect(uriConfig.adhocInstantSaveReport).toBe("instantbi/instant/saveReport");
    expect(uriConfig.adhocInstantGetReportForEdit).toBe("instantbi/instant/getReportForEdit");
    expect(uriConfig.adhocInstantGetReport).toBe("instantbi/instant/getReport");
  });
  test("should base64 encoding should work correctly for agent payload", () => {
    const agentPayload = {
      file: "pg_sample_travel_data.agent",
      dir: "test",
    };
    const encoded = Base64.encode(JSON.stringify(agentPayload));
    expect(encoded).toBeDefined();
    expect(typeof encoded).toBe("string");
    const decoded = JSON.parse(Base64.decode(encoded));
    expect(decoded.file).toBe(agentPayload.file);
    expect(decoded.dir).toBe(agentPayload.dir);
  });

  test("it should instantBI should return all required methods", () => {
    expect(instantBIInstance.postInstantBIRequest).toBeDefined();
    expect(instantBIInstance.getMetadata).toBeDefined();
    expect(instantBIInstance.getFunctions).toBeDefined();
    expect(instantBIInstance.getDateFunctions).toBeDefined();
    expect(instantBIInstance.saveInstantBIReport).toBeDefined();
    expect(instantBIInstance.getInstantBIReportForEdit).toBeDefined();
    expect(instantBIInstance.instantBIChatRequest).toBeDefined();
    expect(instantBIInstance.instantBIFetchDomain).toBeDefined();
    expect(instantBIInstance.instantBIFetchRecommendation).toBeDefined();
  });
});
