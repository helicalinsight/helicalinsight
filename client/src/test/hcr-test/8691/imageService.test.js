import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";

jest.mock("@ant-design/icons", () => ({
  FileImageOutlined: () => (
    <div data-testid="file-image-icon">FileImageIcon</div>
  ),
  FileUnknownOutlined: () => (
    <div data-testid="file-unknown-icon">FileUnknownIcon</div>
  ),
}));

jest.mock("antd", () => ({
  Image: ({ src, fallback, style, preview }) =>
    src ? (
      <img data-testid="ant-image" src={src} style={style} alt="image" />
    ) : null,
}));

jest.mock("react-redux", () => ({
  useDispatch: () => jest.fn(),
}));

jest.mock("../../../base/requests", () => {
  const mockHcrImageServiceRequest = jest.fn();
  const mockCannedReport = jest.fn(() => ({
    hcrImageServiceRequest: mockHcrImageServiceRequest,
  }));

  global.mockHcrImageServiceRequest = mockHcrImageServiceRequest;
  global.mockCannedReport = mockCannedReport;
  return {
    __esModule: true,
    default: {
      cannedReport: mockCannedReport,
    },
  };
});

import ImageNode from "../../../components/hi-canned-reports/hcrCanvas/nodes/imageNode";

beforeAll(() => {
  window.baseURL = "http://localhost:3000";
});

beforeEach(() => {
  jest.clearAllMocks();
  if (global.mockHcrImageServiceRequest) {
    global.mockHcrImageServiceRequest.mockClear();
  }
  if (global.mockCannedReport) {
    global.mockCannedReport.mockClear();
  }
  if (global.mockCannedReport) {
    global.mockCannedReport.mockReturnValue({
      hcrImageServiceRequest: global.mockHcrImageServiceRequest,
    });
  }
});

describe("ImageNode", () => {
  it("should handle Base64 image without API call", () => {
    const props = {
      data: {
        imagePath: "data:image/png;base64,test123",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    expect(global.mockCannedReport).toHaveBeenCalled();
    expect(global.mockHcrImageServiceRequest).not.toHaveBeenCalled();
  });

  it("should not call API for file without directory", () => {
    const props = {
      data: {
        imagePath: "logo.png",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    expect(global.mockCannedReport).toHaveBeenCalled();
    expect(global.mockHcrImageServiceRequest).not.toHaveBeenCalled();
  });

  it("should display FileImageOutlined when isElementRender is true", () => {
    const props = {
      data: {
        label: "Test Image",
        imagePath: "path/to/image.png",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: true,
    };
    render(<ImageNode {...props} />);
    expect(screen.getByTestId("file-image-icon")).toBeInTheDocument();
    expect(screen.getByText("Test Image")).toBeInTheDocument();
  });

  it("it should display FileImageOutlined placeholder when imagePath is empty", () => {
    const props = {
      data: {
        imagePath: "",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    expect(screen.getByTestId("file-image-icon")).toBeInTheDocument();
  });

  it("it shouldd handle external image URL correctly", async () => {
    global.mockHcrImageServiceRequest.mockImplementation(
      (params, endpoint, successCallback) => {
        successCallback({
          status: 1,
          response: {
            data: {
              content: "data:image/png;base64,mockedBase64String",
            },
          },
        });
      },
    );
    const props = {
      data: {
        imagePath: "assets/images/logo.png",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    await waitFor(() => {
      expect(global.mockCannedReport).toHaveBeenCalled();
      expect(global.mockHcrImageServiceRequest).toHaveBeenCalledWith(
        { dir: "assets/images", file: "logo.png" },
        "util/io/imageService",
        expect.any(Function),
        expect.any(Function),
      );
    });
  });

  it("should showw error icon when API call failss", async () => {
    global.mockHcrImageServiceRequest.mockImplementation(
      (params, endpoint, successCallback, errorCallback) => {
        if (errorCallback) {
          errorCallback();
        }
      },
    );
    const props = {
      data: {
        imagePath: "assets/images/logo.png",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    await waitFor(() => {
      expect(screen.getByTestId("file-unknown-icon")).toBeInTheDocument();
    });
    expect(global.mockCannedReport).toHaveBeenCalled();
    expect(global.mockHcrImageServiceRequest).toHaveBeenCalled();
  });

  it("it should parse image path with forward slashess correctly", async () => {
    global.mockHcrImageServiceRequest.mockImplementation(
      (params, endpoint, successCallback) => {
        successCallback({
          status: 1,
          response: {
            data: {
              content: "data:image/png;base64,test",
            },
          },
        });
      },
    );
    const props = {
      data: {
        imagePath: "folder/subfolder/image.jpg",
        width: 100,
        height: 100,
        padding: {},
        borders: {},
      },
      isElementRender: false,
    };
    render(<ImageNode {...props} />);
    await waitFor(() => {
      expect(global.mockHcrImageServiceRequest).toHaveBeenCalledWith(
        { dir: "folder/subfolder", file: "image.jpg" },
        "util/io/imageService",
        expect.any(Function),
        expect.any(Function),
      );
    });
  });
});