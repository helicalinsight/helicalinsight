import "core-js/stable";
import "regenerator-runtime/runtime";
import getInstantBIOpenTaskbarItems from "../../components/hi-instant-bi/instant-bi-open-taskbar-items";
import { fetchInstantBIReportAPI } from "../../components/hi-instant-bi/utils/instant-bi-requests";

jest.mock("../../components/hi-instant-bi/utils/instant-bi-requests", () => ({
  fetchInstantBIReportAPI: jest.fn(),
}));

describe("getInstantBIOpenTaskbarItems", () => {
  const mockDispatch = jest.fn();
  const mockSetFileInfo = jest.fn();
  const file = { path: "reports", name: "sample.instant" };
  const reportId = "report-123";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("it should return cacheTime and Refresh dropdown taskbar items", () => {
    const items = getInstantBIOpenTaskbarItems({
      dispatch: mockDispatch,
      file,
      reportId,
      setFileInfo: mockSetFileInfo,
    });
    expect(items).toHaveLength(2);
    expect(items[0]).toMatchObject({ title: "cacheTime", key: "cacheTime" });
    expect(items[1]).toMatchObject({
      title: "Refresh",
      dropdown: true,
    });
    expect(items[1].menu).toHaveLength(1);
    expect(items[1].menu[0]).toMatchObject({
      title: "Current Report",
      key: "currentReport",
    });
  });

  it ("it should reload current report when current report callback is invoked", () => {
    const items = getInstantBIOpenTaskbarItems({
      dispatch: mockDispatch,
      file,
      reportId,
      setFileInfo: mockSetFileInfo,
    });
    items[1].menu[0].callback();
    expect(fetchInstantBIReportAPI).toHaveBeenCalledTimes(1);
    expect(fetchInstantBIReportAPI).toHaveBeenCalledWith({
      dispatch: mockDispatch,
      file: { path: file.path, name: file.name },
      mode: "open",
      setFileInfo: mockSetFileInfo,
      reportId,
    });
  });
});
