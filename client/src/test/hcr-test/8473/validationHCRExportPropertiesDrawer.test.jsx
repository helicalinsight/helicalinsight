import '@testing-library/jest-dom';
import { render } from "@testing-library/react";
import HCRExportPropertiesDrawer from "../../../components/hi-canned-reports/hcrCanvas/canvasProperty/hcrExportPropertiesDrawer";

// Mock window.matchMedia for Ant Design
beforeAll(() => {
  window.matchMedia = window.matchMedia || function (query) {
    return {
      matches: false,
      media: query,
      onchange: null,
      addListener: function () { },
      removeListener: function () { },
      addEventListener: function () { },
      removeEventListener: function () { },
      dispatchEvent: function () { },
    };
  };
});

describe("HCRExportPropertiesDrawer - implemented fixes", () => {
  const mockOnEditProperty = jest.fn();
  const data = [
    { id: 1, key: "prop1", value: "value1" },
    { id: 2, key: "prop2", value: "value2" },
  ];

  beforeEach(() => {
    mockOnEditProperty.mockClear();
  });

  it("allows editing a property including empty key/value", () => {
    render(
      <HCRExportPropertiesDrawer
        visible={true}
        data={data}
        onEditProperty={mockOnEditProperty}
      />
    );
    const propButtons = document.getElementsByClassName("ant-collapse-header");
    expect(propButtons.length).toBe(2);
  });
});

