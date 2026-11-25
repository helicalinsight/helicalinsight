import { updateHashFilterParameter } from "../../../utils/utilities";
import { getRoutePathname } from "../../../customHooks/helperMethods";

describe("Hreport filter URL synchronization", () => {
  test("updates an existing filter value without removing route parameters", () => {
    const hash = "#/report-viewer?dir=Bhargav_Smoke&file=Trip_Wise_Travel_Distribution.hr&mode=open&travel_type=International";

    expect(updateHashFilterParameter(hash, "travel_type", "Domestic")).toBe(
      "#/report-viewer?dir=Bhargav_Smoke&file=Trip_Wise_Travel_Distribution.hr&mode=open&travel_type=Domestic"
    );
  });

  test("serializes repeated filter values", () => {
    const hash = "#/report-viewer?dir=Reports&file=Travel.hr&mode=open";

    expect(updateHashFilterParameter(hash, "travel_type", ["Domestic", "International"])).toBe(
      "#/report-viewer?dir=Reports&file=Travel.hr&mode=open&travel_type=Domestic&travel_type=International"
    );
  });

  test("matches route paths without treating query params as a different route", () => {
    expect(getRoutePathname("/report-viewer?travel_type=International")).toBe(
      "/report-viewer"
    );
  });
});
