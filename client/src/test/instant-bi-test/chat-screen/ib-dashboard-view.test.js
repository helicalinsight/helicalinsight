import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { createStore } from "redux";

jest.mock("../../../components/hi-dashboard-designer", () => ({
  __esModule: true,
  HIDashboardDesigner: () =>
    require("react").createElement("div", { "data-testid": "hi-dashboard-designer" }),
}));

jest.mock("../../../components/hi-instant-bi/instant-bi-tooltip-title", () => ({
  __esModule: true,
  default: ({ children }) => children,
}));

jest.mock("../../../components/hi-instant-bi/utils/assemble-instant-dashboard", () => ({
  __esModule: true,
  assembleInstantDashboardCreateConfig: jest.fn(({ dashboardModel = {} }) => ({
    gridItemsData: [{ id: dashboardModel.title }],
    title: dashboardModel.title,
  })),
  getGridItemsFiltersSettings: jest.fn(() => ({})),
}));

jest.mock("../../../components/hi-instant-bi/utils/common-utils", () => ({
  __esModule: true,
  getInstantBIMetadataForReport: jest.fn(() => ({})),
}));

jest.mock("../../../components/hi-instant-bi/utils/convert-sql-to-hr-save", () => ({
  __esModule: true,
  persistConvertDashboardSeed: jest.fn(),
}));

jest.mock("../../../components/hi-instant-bi/utils/save-instant-dashboard", () => ({
  __esModule: true,
  INSTANT_EDIT_MODE: "instant-edit",
}));

jest.mock("../../../components/bridges/dashboard/dashboard-bridge", () => ({
  __esModule: true,
  DASHBOARD_CREATE_TILE: "dashboard-create-component",
}));

const IBDashboardView = require("../../../components/hi-instant-bi/components/chat-screen/ib-dashboard-view/ib-dashboard-view").default;
const { assembleInstantDashboardCreateConfig } = require("../../../components/hi-instant-bi/utils/assemble-instant-dashboard");
const actionTypes = require("../../../redux/actions/actionTypes").default;

const createDashboardStore = () => {
  const initialState = {
    hreport: { present: { reports: [] } },
    designer: {
      present: {
        gridItemsData: [],
        previewMode: false,
      },
    },
  };

  return createStore((state = initialState, action) => {
    switch (action.type) {
      case actionTypes.STORE_DASHBOARD_CONFIG:
        return {
          ...state,
          designer: {
            ...state.designer,
            present: { ...state.designer.present, ...action.payload },
          },
        };
      case actionTypes.SET_PREVIEW_MODE:
        return {
          ...state,
          designer: {
            ...state.designer,
            present: { ...state.designer.present, previewMode: action.payload },
          },
        };
      default:
        return state;
    }
  });
};

const dashboardModel = {
  title: "Dashboard A",
  items: [{ id: "tile-1" }],
};

const renderView = (store, active) => (
  <Provider store={store}>
    <IBDashboardView
      dashboardModel={dashboardModel}
      dispatch={store.dispatch}
      planMessageId="message-1"
      active={active}
    />
  </Provider>
);

describe("IBDashboardView", () => {
  beforeEach(() => {
    assembleInstantDashboardCreateConfig.mockClear();
  });

  test("unmounts the designer when inactive and reloads its config when reopened", async () => {
    const store = createDashboardStore();
    const { rerender } = render(renderView(store, true));

    await waitFor(() => expect(screen.getByTestId("hi-dashboard-designer")).toBeTruthy());
    expect(assembleInstantDashboardCreateConfig).toHaveBeenCalledTimes(1);
    expect(store.getState().designer.present.gridItemsData).toEqual([{ id: "Dashboard A" }]);

    rerender(renderView(store, false));
    expect(screen.queryByTestId("hi-dashboard-designer")).toBeNull();
    expect(screen.getByTestId("ib-dashboard-view-pending")).toBeTruthy();

    rerender(renderView(store, true));
    await waitFor(() => expect(screen.getByTestId("hi-dashboard-designer")).toBeTruthy());
    expect(assembleInstantDashboardCreateConfig).toHaveBeenCalledTimes(2);
  });
});
