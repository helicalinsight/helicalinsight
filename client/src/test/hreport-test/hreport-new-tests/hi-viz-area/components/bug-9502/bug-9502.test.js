import React from "react";
import { render, screen } from "@testing-library/react";
import HiCard from "../../../../../../components/hi-reports/hi-viz-area/ant-charts/components/ant-card";

jest.mock("react-redux", () => ({
  ...jest.requireActual("react-redux"),
  useDispatch: () => jest.fn(),
  useSelector: () => false,
}));

describe("HiCard", () => {
  const renderCard = (data) =>
    render(
      <HiCard
        data={data}
        markLayers={["sum_travel_cost"]}
        marksList={[{ subVizType: "kpi" }]}
        fields={[]}
        report={{ fields: [], properties: {}, reportData: { properties: {} } }}
      />
    );

  it("renders a non-numeric Null value without appending zero", () => {
    renderCard([{ sum_travel_cost: "Null" }]);
    expect(screen.getByText("Null")).toBeTruthy();
    expect(screen.queryByText("Null0")).toBeNull();
  });

  it("continues summing numeric card values", () => {
    renderCard([{ sum_travel_cost: 10 }, { sum_travel_cost: 20 }]);
    expect(screen.getByText("30")).toBeTruthy();
  });
});
