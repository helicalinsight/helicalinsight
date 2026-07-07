import { fetchFileBrowserData } from "../../../../components/hi-fileBrowser/helperMethods";

describe("fetchFileBrowserData", () => {
  test("fetches file browser data", () => {
    const dispatch = jest.fn();
    const refresh = false;
    const settings = {};

    const result = fetchFileBrowserData(dispatch, refresh, settings);
    expect(result).toBe(undefined);
  });
});
