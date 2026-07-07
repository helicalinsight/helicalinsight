import React from "react";
import { render } from "@testing-library/react";
import ErrorArea from "../../../../../components/hi-reports/hi-viz-area/ant-charts/components/ant-error-area";
describe("ErrorArea", () => {
  it("renders the error message", () => {
    const errorMessage = "An error occurred";
    const { getByText } = render(<ErrorArea message={errorMessage} />);
    expect(getByText(errorMessage)).toBeTruthy();
  });
});
