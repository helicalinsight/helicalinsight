import createIBDashboardBridge, {
  DASHBOARD_CREATE_TILE,
  hasDashboardTileModel,
  matchStepReportModel,
} from "../../../components/bridges/dashboard/dashboard-bridge";
import createHReportBridge from "../../../components/bridges/hreport/hreport-bridge";

jest.mock("../../../components/bridges/hreport/hreport-bridge", () => ({
  __esModule: true,
  default: jest.fn(),
}));

const TILE = DASHBOARD_CREATE_TILE;
const MODEL = { viz_model: { data: {} } };

function makeTile(overrides = {}) {
  return {
    id: "tile-1",
    compType: TILE,
    report_model: { ...MODEL },
    ...overrides,
  };
}

function mockBridgeSuccess(reportPayload = { id: "hydrated" }) {
  createHReportBridge.mockImplementation((props = {}) => ({
    init: jest.fn(() => {
      props.onComplete?.(true, reportPayload);
      return Promise.resolve();
    }),
  }));
}

beforeEach(() => {
  jest.clearAllMocks();
});

describe("DASHBOARD_CREATE_TILE", () => {
  it("exports the dashboard tile component type", () => {
    expect(DASHBOARD_CREATE_TILE).toBe("dashboard-create-component");
  });
});

describe("hasDashboardTileModel", () => {
  it("returns true for step.report_model", () => {
    expect(hasDashboardTileModel({ report_model: MODEL })).toBe(true);
  });

  it("returns true for step.reportModel (camelCase)", () => {
    expect(hasDashboardTileModel({ reportModel: MODEL })).toBe(true);
  });

  it("returns true for step.fullChatResponse.report_model", () => {
    expect(
      hasDashboardTileModel({ fullChatResponse: { report_model: MODEL } })
    ).toBe(true);
  });

  it("returns true for step.chat_response.report_model", () => {
    expect(
      hasDashboardTileModel({ chat_response: { report_model: MODEL } })
    ).toBe(true);
  });

  it("prefers report_model over the other shapes", () => {
    const other = { viz_model: { other: true } };
    expect(
      hasDashboardTileModel({
        report_model: MODEL,
        reportModel: other,
        fullChatResponse: { report_model: other },
        chat_response: { report_model: other },
      })
    ).toBe(true);
  });

  it("returns false for an empty step", () => {
    expect(hasDashboardTileModel({})).toBe(false);
  });

  it("returns false when called with no arguments", () => {
    expect(hasDashboardTileModel()).toBe(false);
  });

  it("returns false for an empty report_model object", () => {
    expect(hasDashboardTileModel({ report_model: {} })).toBe(false);
  });

  it("returns false for non-object report models", () => {
    expect(hasDashboardTileModel({ report_model: "model" })).toBe(false);
    expect(hasDashboardTileModel({ report_model: 42 })).toBe(false);
    expect(hasDashboardTileModel({ report_model: null })).toBe(false);
  });

  it("returns false when nested report_model containers are empty", () => {
    expect(hasDashboardTileModel({ fullChatResponse: {} })).toBe(false);
    expect(hasDashboardTileModel({ chat_response: {} })).toBe(false);
    expect(
      hasDashboardTileModel({ fullChatResponse: { report_model: {} } })
    ).toBe(false);
  });
});

describe("matchStepReportModel", () => {
  const modelA = { id: "A" };
  const modelB = { id: "B" };
  const steps = [
    { index: 1, report_model: modelA },
    { index: 2, report_model: modelB },
  ];

  it("returns empty model and -1 when there are no steps", () => {
    expect(matchStepReportModel({ item: {}, steps: [] })).toEqual({
      reportModel: {},
      stepIndex: -1,
    });
  });

  it("falls back to item.report_model when cursor is out of bounds", () => {
    const item = { report_model: { id: "fallback" } };
    expect(
      matchStepReportModel({ item, steps, cursor: 99 })
    ).toEqual({
      reportModel: { id: "fallback" },
      stepIndex: -1,
    });
  });

  it("falls back to {} when neither steps nor item model exist", () => {
    expect(matchStepReportModel({ item: {}, steps: [] })).toEqual({
      reportModel: {},
      stepIndex: -1,
    });
  });

  it("uses cursor position when item id has no numeric suffix", () => {
    expect(
      matchStepReportModel({ item: { id: "tile" }, steps, cursor: 1 })
    ).toEqual({ reportModel: modelB, stepIndex: 1 });
  });

  it("uses cursor 0 by default", () => {
    expect(matchStepReportModel({ item: {}, steps })).toEqual({
      reportModel: modelA,
      stepIndex: 0,
    });
  });

  it("matches step.index from the numeric suffix in item.id", () => {
    expect(
      matchStepReportModel({ item: { id: "tile-2" }, steps, cursor: 0 })
    ).toEqual({ reportModel: modelB, stepIndex: 1 });
  });

  it("suffix match wins over cursor", () => {
    // cursor points at modelA but suffix asks for modelB
    expect(
      matchStepReportModel({ item: { id: "anything-2" }, steps, cursor: 0 })
    ).toEqual({ reportModel: modelB, stepIndex: 1 });
  });

  it("falls back to positional (arrayIndex + 1) match when step.index is missing", () => {
    const positional = [
      { report_model: modelA },
      { report_model: modelB },
    ];
    expect(
      matchStepReportModel({ item: { id: "tile-2" }, steps: positional })
    ).toEqual({ reportModel: modelB, stepIndex: 1 });
  });

  it("handles step.index = 0 via positional fallback (0 is falsy)", () => {
    const zeroIndexed = [
      { index: 0, report_model: modelA },
      { index: 0, report_model: modelB },
    ];
    // Number(0) || arrayIndex+1 -> 1 for first, 2 for second
    expect(
      matchStepReportModel({ item: { id: "tile-1" }, steps: zeroIndexed })
    ).toEqual({ reportModel: modelA, stepIndex: 0 });
    expect(
      matchStepReportModel({ item: { id: "tile-2" }, steps: zeroIndexed })
    ).toEqual({ reportModel: modelB, stepIndex: 1 });
  });

  it("falls back to cursor when suffix does not match any step", () => {
    expect(
      matchStepReportModel({ item: { id: "tile-99" }, steps, cursor: 0 })
    ).toEqual({ reportModel: modelA, stepIndex: 0 });
  });

  it("skips steps without a report model when counting cursor positions", () => {
    const mixed = [
      { index: 1, report_model: modelA },
      { index: 2 }, // no model -> filtered out
      { index: 3, reportModel: modelB },
    ];
    // modeled = [modelA (arrayIndex 0), modelB (arrayIndex 2)]
    expect(matchStepReportModel({ item: {}, steps: mixed, cursor: 1 })).toEqual({
      reportModel: modelB,
      stepIndex: 2,
    });
  });

  it("reads camelCase reportModel and nested chat shapes through suffix match", () => {
    const camel = [{ index: 5, reportModel: modelA }];
    const full = [{ index: 5, fullChatResponse: { report_model: modelA } }];
    const chat = [{ index: 5, chat_response: { report_model: modelA } }];
    expect(
      matchStepReportModel({ item: { id: "tile-5" }, steps: camel })
    ).toEqual({ reportModel: modelA, stepIndex: 0 });
    expect(
      matchStepReportModel({ item: { id: "tile-5" }, steps: full })
    ).toEqual({ reportModel: modelA, stepIndex: 0 });
    expect(
      matchStepReportModel({ item: { id: "tile-5" }, steps: chat })
    ).toEqual({ reportModel: modelA, stepIndex: 0 });
  });

  it("ignores non-array steps", () => {
    expect(
      matchStepReportModel({ item: { report_model: modelA }, steps: null })
    ).toEqual({ reportModel: modelA, stepIndex: -1 });
  });

  it("works with default (no) arguments", () => {
    expect(matchStepReportModel()).toEqual({ reportModel: {}, stepIndex: -1 });
  });
});

describe("createIBDashboardBridge - pending items filtering", () => {
  it("returns no pending items for empty gridItemsData", () => {
    const bridge = createIBDashboardBridge({ gridItemsData: [] });
    expect(bridge.items).toEqual([]);
  });

  it("defaults gridItemsData to [] when omitted", () => {
    const bridge = createIBDashboardBridge({});
    expect(bridge.items).toEqual([]);
  });

  it("defaults everything when called with no arguments", () => {
    const bridge = createIBDashboardBridge();
    expect(bridge.items).toEqual([]);
  });

  it("tolerates non-array gridItemsData", () => {
    const bridge = createIBDashboardBridge({ gridItemsData: null });
    expect(bridge.items).toEqual([]);
  });

  it("keeps tiles with a top-level report_model", () => {
    const item = makeTile();
    const bridge = createIBDashboardBridge({ gridItemsData: [item] });
    expect(bridge.items).toEqual([item]);
  });

  it("keeps tiles with reportInfo.file.report_model", () => {
    const item = makeTile({
      report_model: undefined,
      reportInfo: { file: { report_model: MODEL } },
    });
    // remove top-level key so only the nested path counts
    delete item.report_model;
    const bridge = createIBDashboardBridge({ gridItemsData: [item] });
    expect(bridge.items).toEqual([item]);
  });

  it("keeps tiles with instant_item chat_response report_model", () => {
    const item = makeTile({
      reportInfo: {
        file: { instant_item: { chat_response: { report_model: MODEL } } },
      },
    });
    delete item.report_model;
    const bridge = createIBDashboardBridge({ gridItemsData: [item] });
    expect(bridge.items).toEqual([item]);
  });

  it("drops tiles with a wrong compType even if they carry a model", () => {
    const item = makeTile({ compType: "other-component" });
    const bridge = createIBDashboardBridge({ gridItemsData: [item] });
    expect(bridge.items).toEqual([]);
  });

  it("drops tiles with an empty report model", () => {
    const emptyTop = makeTile({ report_model: {} });
    const emptyNested = makeTile({
      reportInfo: { file: { report_model: {} } },
    });
    delete emptyNested.report_model;
    const bridge = createIBDashboardBridge({
      gridItemsData: [emptyTop, emptyNested],
    });
    expect(bridge.items).toEqual([]);
  });

  it("drops tiles with no model at all", () => {
    const item = { id: "tile-x", compType: TILE };
    const bridge = createIBDashboardBridge({ gridItemsData: [item] });
    expect(bridge.items).toEqual([]);
  });

  it("keeps only hydratable tiles from a mixed list", () => {
    const good = makeTile({ id: "good" });
    const badType = makeTile({ id: "bad-type", compType: "text" });
    const badModel = makeTile({ id: "bad-model", report_model: {} });
    const bridge = createIBDashboardBridge({
      gridItemsData: [good, badType, badModel],
    });
    expect(bridge.items).toEqual([good]);
  });
});

describe("createIBDashboardBridge - init flow", () => {
  it("calls onComplete with [] and never touches hreport bridge when nothing is pending", async () => {
    createHReportBridge.mockImplementation(() => ({
      init: jest.fn(() => Promise.resolve()),
    }));
    const onComplete = jest.fn();
    const onItemUpdate = jest.fn();
    const bridge = createIBDashboardBridge({
      gridItemsData: [{ id: "plain", compType: "text" }],
      onComplete,
      onItemUpdate,
    });

    await bridge.init();

    expect(createHReportBridge).not.toHaveBeenCalled();
    expect(onItemUpdate).not.toHaveBeenCalled();
    expect(onComplete).toHaveBeenCalledWith([]);
  });

  it("hydrates a single tile and forwards reportMetadata, dispatch and model", async () => {
    mockBridgeSuccess({ id: "hydrated-1" });
    const dispatch = jest.fn();
    const reportMetadata = { metadata: { name: "meta" } };
    const onItemUpdate = jest.fn();
    const onComplete = jest.fn();
    const item = makeTile({
      id: "tile-1",
      report_model: MODEL,
      reportInfo: { hreportId: "hr-1", dashboardItemId: "d-1" },
    });

    const bridge = createIBDashboardBridge({
      dispatch,
      gridItemsData: [item],
      reportMetadata,
      onItemUpdate,
      onComplete,
    });
    await bridge.init();

    expect(createHReportBridge).toHaveBeenCalledTimes(1);
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({
        dispatch,
        reportModel: MODEL,
        reportId: "hr-1",
        reportMetadata,
        interactions: {},
      })
    );
    expect(onItemUpdate).toHaveBeenCalledWith({ id: "hydrated-1" });
    expect(onComplete).toHaveBeenCalledWith([item]);
  });

  it("processes multiple tiles sequentially in order", async () => {
    const seen = [];
    createHReportBridge.mockImplementation((props = {}) => ({
      init: jest.fn(() => {
        seen.push(props.reportModel.id);
        props.onComplete?.(true, { id: props.reportModel.id });
        return Promise.resolve();
      }),
    }));
    const onItemUpdate = jest.fn();
    const onComplete = jest.fn();
    const items = [
      makeTile({ id: "t-1", report_model: { id: "m1" } }),
      makeTile({ id: "t-2", report_model: { id: "m2" } }),
      makeTile({ id: "t-3", report_model: { id: "m3" } }),
    ];

    await createIBDashboardBridge({
      gridItemsData: items,
      onItemUpdate,
      onComplete,
    }).init();

    expect(seen).toEqual(["m1", "m2", "m3"]);
    expect(onItemUpdate).toHaveBeenCalledTimes(3);
    expect(onItemUpdate.mock.calls.map(([r]) => r.id)).toEqual([
      "m1",
      "m2",
      "m3",
    ]);
    expect(onComplete).toHaveBeenCalledWith(items);
  });

  it("resolves reportId from reportInfo.hreportId first", async () => {
    mockBridgeSuccess();
    const item = makeTile({
      reportInfo: {
        hreportId: "hr-direct",
        file: { instant_item: { hreportId: "hr-nested" } },
      },
    });
    await createIBDashboardBridge({ gridItemsData: [item] }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({ reportId: "hr-direct" })
    );
  });

  it("resolves reportId from instant_item when direct id is missing", async () => {
    mockBridgeSuccess();
    const item = makeTile({
      reportInfo: { file: { instant_item: { hreportId: "hr-nested" } } },
    });
    delete item.report_model;
    item.reportInfo.file.instant_item.chat_response = {
      report_model: MODEL,
    };
    await createIBDashboardBridge({ gridItemsData: [item] }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({ reportId: "hr-nested" })
    );
  });

  it("defaults reportId to empty string when no hreport id exists", async () => {
    mockBridgeSuccess();
    const item = makeTile({ reportInfo: {} });
    await createIBDashboardBridge({ gridItemsData: [item] }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({ reportId: "" })
    );
  });

  it("prefers file.report_model over top-level report_model", async () => {
    mockBridgeSuccess();
    const fileModel = { id: "from-file" };
    const item = makeTile({
      report_model: { id: "from-top" },
      reportInfo: { file: { report_model: fileModel } },
    });
    await createIBDashboardBridge({ gridItemsData: [item] }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({ reportModel: fileModel })
    );
  });

  it("looks up interactions by dashboardItemId first", async () => {
    mockBridgeSuccess();
    const interactionsById = {
      "dash-1": { selectedType: "by-dashboard" },
      "comp-1": { selectedType: "by-component" },
      "hr-1": { selectedType: "by-hreport" },
    };
    const item = makeTile({
      reportInfo: {
        hreportId: "hr-1",
        dashboardItemId: "dash-1",
        component_id: "comp-1",
      },
    });
    await createIBDashboardBridge({
      gridItemsData: [item],
      interactionsById,
    }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({
        interactions: { selectedType: "by-dashboard" },
      })
    );
  });

  it("falls back to component_id when dashboardItemId is missing", async () => {
    mockBridgeSuccess();
    const item = makeTile({
      reportInfo: { hreportId: "hr-1", component_id: "comp-1" },
    });
    await createIBDashboardBridge({
      gridItemsData: [item],
      interactionsById: {
        "comp-1": { selectedType: "by-component" },
        "hr-1": { selectedType: "by-hreport" },
      },
    }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({
        interactions: { selectedType: "by-component" },
      })
    );
  });

  it("falls back to hreportId interactions and then to {}", async () => {
    mockBridgeSuccess();
    const byHreport = makeTile({
      id: "by-hreport",
      reportInfo: { hreportId: "hr-1" },
    });
    await createIBDashboardBridge({
      gridItemsData: [byHreport],
      interactionsById: { "hr-1": { selectedType: "by-hreport" } },
    }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({
        interactions: { selectedType: "by-hreport" },
      })
    );

    jest.clearAllMocks();
    mockBridgeSuccess();
    const noMatch = makeTile({
      id: "no-match",
      reportInfo: { hreportId: "hr-unknown" },
    });
    await createIBDashboardBridge({
      gridItemsData: [noMatch],
      interactionsById: { other: {} },
    }).init();
    expect(createHReportBridge).toHaveBeenCalledWith(
      expect.objectContaining({ interactions: {} })
    );
  });

  it("tags forwarded events with dashboardItemId", async () => {
    let capturedEventUpdater;
    createHReportBridge.mockImplementation((props = {}) => {
      capturedEventUpdater = props.eventUpdater;
      return { init: jest.fn(() => {
        props.onComplete?.(true, {});
        return Promise.resolve();
      }) };
    });
    const eventUpdater = jest.fn();
    const item = makeTile({ id: "tile-ev" });
    await createIBDashboardBridge({
      gridItemsData: [item],
      eventUpdater,
    }).init();

    capturedEventUpdater({ event: "change_viz", data: {} });
    expect(eventUpdater).toHaveBeenCalledWith({
      event: "change_viz",
      data: {},
      dashboardItemId: "tile-ev",
    });
  });

  it("continues to the next tile when inner onError fires", async () => {
    createHReportBridge
      .mockImplementationOnce((props = {}) => ({
        init: jest.fn(() => {
          props.onError?.({ error: true });
          return Promise.resolve();
        }),
      }))
      .mockImplementationOnce((props = {}) => ({
        init: jest.fn(() => {
          props.onComplete?.(true, { id: "second" });
          return Promise.resolve();
        }),
      }));
    const onItemUpdate = jest.fn();
    const onComplete = jest.fn();
    const onError = jest.fn();
    const items = [
      makeTile({ id: "first", report_model: { id: "m1" } }),
      makeTile({ id: "second", report_model: { id: "m2" } }),
    ];

    await createIBDashboardBridge({
      gridItemsData: items,
      onItemUpdate,
      onComplete,
      onError,
    }).init();

    expect(createHReportBridge).toHaveBeenCalledTimes(2);
    expect(onItemUpdate).toHaveBeenCalledTimes(1);
    expect(onItemUpdate).toHaveBeenCalledWith({ id: "second" });
    expect(onComplete).toHaveBeenCalledWith(items);
    expect(onError).not.toHaveBeenCalled();
  });

  it("continues to the next tile when bridge.init() rejects", async () => {
    createHReportBridge
      .mockImplementationOnce(() => ({
        init: jest.fn(() => Promise.reject(new Error("init failed"))),
      }))
      .mockImplementationOnce((props = {}) => ({
        init: jest.fn(() => {
          props.onComplete?.(true, { id: "recovered" });
          return Promise.resolve();
        }),
      }));
    const onItemUpdate = jest.fn();
    const onComplete = jest.fn();
    const items = [
      makeTile({ id: "bad", report_model: { id: "m1" } }),
      makeTile({ id: "good", report_model: { id: "m2" } }),
    ];

    await createIBDashboardBridge({
      gridItemsData: items,
      onItemUpdate,
      onComplete,
    }).init();

    expect(createHReportBridge).toHaveBeenCalledTimes(2);
    expect(onItemUpdate).toHaveBeenCalledWith({ id: "recovered" });
    expect(onComplete).toHaveBeenCalledWith(items);
  });

  it("calls onError when createHReportBridge throws synchronously", async () => {
    createHReportBridge.mockImplementation(() => {
      throw new Error("factory boom");
    });
    const onError = jest.fn();
    const onComplete = jest.fn();
    const bridge = createIBDashboardBridge({
      gridItemsData: [makeTile()],
      onError,
      onComplete,
    });

    await bridge.init();

    expect(onError).toHaveBeenCalledWith(expect.any(Error));
    expect(onComplete).not.toHaveBeenCalled();
  });

  it("works with all default callbacks (no throw when omitted)", async () => {
    mockBridgeSuccess();
    const bridge = createIBDashboardBridge({
      gridItemsData: [makeTile()],
    });
    await expect(bridge.init()).resolves.toBeUndefined();
  });
});
