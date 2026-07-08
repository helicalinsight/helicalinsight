import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import {
  ChartView,
  parseBackendErrorMessage,
} from "../../components/hi-instant-bi/utils/common-utils";

jest.mock("../../components/hi-instant-bi/components/ib-custom-chart", () => ({
  __esModule: true,
  default: () => <div data-testid="ib-custom-chart" />,
  IB_CHART_RENDER_ERROR: "Something went wrong. Please try again.",
}));

const tableData = [
  { alias: "Chennai", total_travel_cost: 1350 },
  { alias: "Jaipur", total_travel_cost: 1200 },
];

describe("ChartView", () => {
  it("should show chart render error when vf is missing for non-table charts", () => {
    render(
      <ChartView
        data={tableData}
        vf=""
        chartName="bar"
        id="msg-3"
      />,
    );
    expect(
      screen.getByText("Something went wrong. Please try again."),
    ).toBeInTheDocument();
    expect(screen.queryByText("View Details")).not.toBeInTheDocument();
    expect(screen.queryByTestId("ib-custom-chart")).not.toBeInTheDocument();
  });

  it("should show backend error details when View Details is clicked", () => {
    const backendError =
      "{'message': 'Error: SecurityException: The Table you are trying to access is out of scope or not found in the metadata', 'className': 'SecurityException'}";

    render(
      <ChartView
        data={tableData}
        vf=""
        chartName="bar"
        id="msg-4"
        backendError={backendError}
      />,
    );

    expect(screen.getByText("View Details")).toBeInTheDocument();
    fireEvent.click(screen.getByText("View Details"));
    expect(
      screen.getByText(
        "Error: SecurityException: The Table you are trying to access is out of scope or not found in the metadata",
      ),
    ).toBeInTheDocument();
  });

  it("should use compact kpi wrapper without fixed chart height", () => {
    const { container } = render(
      <ChartView
        compact
        width={400}
        data={[{ total: 41 }]}
        vf="function DrawKPI() { return null; }"
        chartName="kpi"
        id="msg-5"
      />,
    );
    const wrapper = container.querySelector(".chart-wrapper--kpi");
    expect(wrapper).toBeTruthy();
    expect(wrapper.style.height).toBe("");
    expect(screen.getByTestId("ib-custom-chart")).toBeInTheDocument();
  });
});

describe("parseBackendErrorMessage", () => {
  it("should extract message from backend error string", () => {
    expect(
      parseBackendErrorMessage(
        "{'message': 'Error: SecurityException: Table not found', 'className': 'SecurityException'}",
      ),
    ).toBe("Error: SecurityException: Table not found");
  });

  it("should return object message as-is", () => {
    expect(
      parseBackendErrorMessage({
        message: "Error: Table out of scope",
        className: "SecurityException",
      }),
    ).toBe("Error: Table out of scope");
  });
});
