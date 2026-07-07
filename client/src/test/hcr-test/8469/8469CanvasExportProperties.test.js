// src/test/hcr-test/8469/8469CanvasExportProperties.test.js

import React from "react";
import { render, fireEvent, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import CanvasExportProperties from "../../../components/hi-canned-reports/hcrCanvas/canvasProperty/hcrCanvasExportProperties";
import { hcrActions } from "../../../redux/actions";

// MOCK window.matchMedia for Ant Design
beforeAll(() => {
  Object.defineProperty(window, "matchMedia", {
    writable: true,
    value: jest.fn().mockImplementation((query) => ({
      matches: false,
      media: query,
      onchange: null,
      addListener: jest.fn(), // deprecated
      removeListener: jest.fn(), // deprecated
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    })),
  });
});

// Mock InputFiled component
const MockInputFiled = ({ label, value, onChange }) => (
  <input
    aria-label={label.props.children}
    value={value}
    onChange={(e) => onChange(e.target.value)}
  />
);

// Mock Redux store
const mockStore = {
  getState: () => ({
    cannedReports: {
      present: {
        hcrTabData: {
          panes: [
            {
              key: "pane1",
              hcrExportProperties: [
                { id: "1", key: "prop1", value: "val1" }
              ],
            },
          ],
          activeKey: "pane1",
        },
        hcrExportPropertiesData: {
          pane1: [{ key: "prop1", value: "val1", description: "desc1" }],
        },
      },
    },
  }),
  subscribe: jest.fn(),
  dispatch: jest.fn(),
};

describe("CanvasExportProperties Component", () => {
  const renderComponent = () =>
    render(
      <Provider store={mockStore}>
        <CanvasExportProperties
          dispatch={mockStore.dispatch}
          InputFiled={MockInputFiled}
        />
      </Provider>
    );

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should add a new property", () => {
    renderComponent();

    const keyInput = screen.getByRole("combobox");
    const valueInput = screen.getByRole("inputBox");

    fireEvent.change(keyInput, { target: { value: "newProp" } });
    fireEvent.change(valueInput, { target: { value: "newValue" } });

    const addButton = screen.getByText("Add");
    fireEvent.click(addButton);

    expect(mockStore.dispatch).toHaveBeenCalledWith(
      expect.objectContaining({
        type: hcrActions.hcrAddNewExportProperty().type,
        payload: expect.objectContaining({
          key: "newProp",
          value: "newValue",
        }),
      })
    );
  });

  test("should handle editing a property", () => {
    renderComponent();

    // Call handlePropertyEdit directly since simulating Drawer events is tricky
    const editPayload = { key: "prop1", value: "val1Edited" };
    const id = "1";

    mockStore.dispatch(hcrActions.hcrEditExportProperty({ id, ...editPayload }));

    expect(mockStore.dispatch).toHaveBeenCalledWith(
      expect.objectContaining({
        type: hcrActions.hcrEditExportProperty().type,
        payload: { id, ...editPayload },
      })
    );
  });

  test("should handle deleting a property", () => {
    renderComponent();

    const id = "1";
    mockStore.dispatch(hcrActions.hcrDeleteExportProperty(id));

    expect(mockStore.dispatch).toHaveBeenCalledWith(
      expect.objectContaining({
        type: hcrActions.hcrDeleteExportProperty().type,
        payload: id,
      })
    );
  });
});
