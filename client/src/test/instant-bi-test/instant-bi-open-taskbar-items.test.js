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

  it("should return cacheTime and Refresh dropdown taskbar items", () => {
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
    expect(items[1].menu).toHaveLength(2);
    expect(items[1].menu[0]).toMatchObject({
      title: "Cache",
      key: "cache",
    });
    expect(items[1].menu[1]).toMatchObject({
      title: "Current Report",
      key: "currentReport",
    });
  });

  it("should invoke onCacheRefresh when cache callback is invoked", () => {
    const onCacheRefresh = jest.fn();
    const items = getInstantBIOpenTaskbarItems({
      dispatch: mockDispatch,
      file,
      reportId,
      setFileInfo: mockSetFileInfo,
      onCacheRefresh,
    });
    items[1].menu[0].callback();
    expect(onCacheRefresh).toHaveBeenCalledTimes(1);
    expect(fetchInstantBIReportAPI).not.toHaveBeenCalled();
  });

  it("should not throw when cache callback is invoked without onCacheRefresh", () => {
    const items = getInstantBIOpenTaskbarItems({
      dispatch: mockDispatch,
      file,
      reportId,
      setFileInfo: mockSetFileInfo,
    });
    expect(() => items[1].menu[0].callback()).not.toThrow();
  });

  it("should reload current report when current report callback is invoked", () => {
    const items = getInstantBIOpenTaskbarItems({
      dispatch: mockDispatch,
      file,
      reportId,
      setFileInfo: mockSetFileInfo,
    });
    items[1].menu[1].callback();
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
