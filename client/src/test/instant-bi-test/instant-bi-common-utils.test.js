import "core-js/stable";
import "regenerator-runtime/runtime";
import {
  isIbKpiChart,
  isIbTableChart,
} from "../../components/hi-instant-bi/utils/common-utils";

describe("isIbKpiChart", () => {
  it("should detect kpi from chart name", () => {
    expect(isIbKpiChart("kpi", "")).toBe(true);
    expect(isIbKpiChart("KPI", "")).toBe(true);
  });

  it("should detect kpi from vf code", () => {
    expect(isIbKpiChart("", "function DrawKPI() { return null; }")).toBe(true);
  });

  it("should return false for non-kpi charts", () => {
    expect(isIbKpiChart("bar", "function DrawBar() {}")).toBe(false);
  });
});

describe("isIbTableChart", () => {
  it("should detect table from chart name", () => {
    expect(isIbTableChart("table", "")).toBe(true);
    expect(isIbTableChart("Table", "")).toBe(true);
  });

  it("should detect table from DrawTable vf", () => {
    expect(isIbTableChart("", "function DrawTable() { return <Table />; }")).toBe(
      true,
    );
  });

  it("should detect table from ant Table jsx in vf", () => {
    expect(
      isIbTableChart("", "function Draw() { return <Table dataSource={data} />; }"),
    ).toBe(true);
  });

  it("should detect table from GridTable usage in vf", () => {
    expect(isIbTableChart("", "function Draw() { return <GridTable {...report} />; }")).toBe(
      true,
    );
  });

  it("should return false for bar charts", () => {
    expect(isIbTableChart("bar", "function DrawBar() { return <Column />; }")).toBe(
      false,
    );
  });
});
