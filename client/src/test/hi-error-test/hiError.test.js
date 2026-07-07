import React from "react";
import { render } from "@testing-library/react";
import { HIErrorPage } from "../../components/hi-error/hi-error";
import "@testing-library/jest-dom/extend-expect";

describe("HIErrorPage", () => {
  test("renders the error message correctly", () => {
    const { getByText } = render(<HIErrorPage />);
    const errorMessage = getByText("This url is not valid");
    expect(errorMessage).toBeInTheDocument();
  });
});
