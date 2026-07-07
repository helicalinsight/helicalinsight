import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import CustomSkeletonFilebrowser from "../../../components/common/custom-icons/CustomSkeletons/filebrowser/CustomSkeletonFilebrowser";

const PreviewAreaTagWrapper = ({ isPreviewLoading }) => {
  return isPreviewLoading ? (
    <div role="presentation">
      <CustomSkeletonFilebrowser size="full" />
    </div>
  ) : (
    <div role="presentation">
      <div data-testid="preview-area">Actual preview content here</div>
    </div>
  );
};

describe("previewAreaTag logic", () => {
  test("shows skeleton when loading", () => {
    render(<PreviewAreaTagWrapper isPreviewLoading={true} />);
    const container = screen.getByRole("presentation");
    expect(container.querySelectorAll(".skeleton-bin-child").length).toBeGreaterThan(0);
  });

  test("shows preview content when not loading", () => {
    render(<PreviewAreaTagWrapper isPreviewLoading={false} />);
    expect(screen.getByTestId("preview-area")).toBeInTheDocument();
  });

    

});
