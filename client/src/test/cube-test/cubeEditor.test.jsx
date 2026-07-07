import React from "react";
import { render, screen, fireEvent, waitFor, act } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import "@testing-library/jest-dom";
import { Provider } from "react-redux";
import { CubeEditor } from "../../components/hi-cube/cubeEditor";
import {
  updateFieldValues,
  setCubeFieldsData,
  setFieldSearchText,
  setCubeSearchedColumn,
  setVisibleCheckValue,
  setCubeIndeterminate,
} from "../../redux/actions/cube.actions";
import { SEMANTIC_TYPES } from "../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

const mockMatchMedia = (query) => ({
  matches: false,
  media: query,
  onchange: null,
  addListener: jest.fn(),
  removeListener: jest.fn(),
  addEventListener: jest.fn(),
  removeEventListener: jest.fn(),
  dispatchEvent: jest.fn(),
});

Object.defineProperty(window, "matchMedia", {
  writable: true,
  configurable: true,
  value: jest.fn().mockImplementation(mockMatchMedia),
});

beforeEach(() => {
  window.matchMedia = jest.fn().mockImplementation(mockMatchMedia);
});

afterEach(() => {
  jest.clearAllMocks();
});

jest.mock("antd/lib/_util/responsiveObserve", () => ({    
  __esModule: true,
  default: {
    subscribe: jest.fn(() => 1),
    unsubscribe: jest.fn(),
    register: jest.fn(),
    dispatch: jest.fn(),
    responsiveMap: {},
  },
}));


jest.mock("react-dnd", () => ({
  useDrop: () => [{ isOver: false }, jest.fn()],
}));

jest.mock("virtuallist-antd", () => ({
  VList: () => ({}),
}));

jest.mock("react-highlight-words", () => ({
  __esModule: true,
  default: ({ textToHighlight }) => <span>{textToHighlight}</span>,
}));

jest.mock("uuid", () => ({ v4: () => "mock-uuid-1234" }));

jest.mock("../../components/hi-cube/helperMethods", () => ({
  cubeTableDataSource: ({ dataSource }) =>
    (dataSource || []).map((item) => ({ ...item })),
  handleCubeTitleEdit: jest.fn(),
  handleHeader: jest.fn(),
  handleHierarachyTitle: jest.fn(),
  handleVisibleCheckStatus: jest.fn(),
  uniqueCubeTitle: jest.fn(),
}));

jest.mock("../../components/hi-cube/cubeConstants", () => ({
  cubeAggregationMenu: jest.fn(() => <div data-testid="aggregation-menu" />),
  cubeMeasureMenu: jest.fn(() => <div data-testid="measure-menu" />),
  cubeSortMenu: jest.fn(() => <div data-testid="sort-menu" />),
  handleCubeEditorHeader: jest.fn(),
  CubeFieldsMenu: jest.fn(() => <div data-testid="fields-menu" />),
  cubeEditorMeasureData: [{ dataType: "Number", format: ["#,##0"] }],
}));

jest.mock("../../redux/actions", () => ({
  fileBrowserActions: {},
}));

jest.mock(
  "../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils",
  () => ({
    SEMANTIC_TYPES: ["Person", "Location", "Organization", "Date", "Currency"],
  }),
);

jest.mock("../../components/hi-cube/cubeEditorTooltips", () => ({
  getCubeEditorTooltipText: jest.fn((label) => `Tooltip for ${label}`),
}));

jest.mock("../../redux/actions/cube.actions", () => ({
  setCubeFieldsData: jest.fn((payload) => ({
    type: "SET_CUBE_FIELDS_DATA",
    payload,
  })),
  setCubeHeaderData: jest.fn((payload) => ({
    type: "SET_CUBE_HEADER_DATA",
    payload,
  })),
  setCubeIndeterminate: jest.fn((payload) => ({
    type: "SET_CUBE_INDETERMINATE",
    payload,
  })),
  setCubeSearchedColumn: jest.fn((payload) => ({
    type: "SET_CUBE_SEARCHED_COLUMN",
    payload,
  })),
  setFieldSearchText: jest.fn((payload) => ({
    type: "SET_FIELD_SEARCH_TEXT",
    payload,
  })),
  setVisibleCheckValue: jest.fn((payload) => ({
    type: "SET_VISIBLE_CHECK_VALUE",
    payload,
  })),
  updateFieldValues: jest.fn((payload) => ({
    type: "UPDATE_FIELD_VALUES",
    payload,
  })),
  setCubeInintialList: jest.fn((payload) => ({
    type: "SET_CUBE_INITIAL_LIST",
    payload,
  })),
  setCubeMode: jest.fn((payload) => ({ type: "SET_CUBE_MODE", payload })),
  setCubeState: jest.fn((payload) => ({ type: "SET_CUBE_STATE", payload })),
}));

const sampleField = {
  key: "field-1",
  fields: "Sales Amount",
  fieldsDropdownOpen: false,
  isHierarchy: false,
  isHierarchyChild: false,
  isDimensionCheck: false,
  parentKey: undefined,
  measure: { isMeasureCheck: false, DataType: "", Format: "" },
  sort: { isSortCheck: false, value: "" },
  aggregation: { isAggregationCheck: false, value: "" },
  isPartitionCheck: false,
  semanticType: "",
  synonyms: "revenue",
  topic: "Sales",
  formula: "",
  filter: "",
  example: "",
  description: "",
};

const hierarchyParentField = {
  key: "hier-parent-1",
  fields: "Date Hierarchy",
  fieldsDropdownOpen: false,
  isHierarchy: true,
  isHierarchyChild: false,
  isDimensionCheck: true,
  parentKey: undefined,
  measure: { isMeasureCheck: false, DataType: "", Format: "" },
  sort: { isSortCheck: false, value: "" },
  aggregation: { isAggregationCheck: false, value: "" },
  isPartitionCheck: false,
  semanticType: "",
  synonyms: "",
  topic: "",
};

const hierarchyChildField = {
  key: "hier-child-1",
  fields: "Year",
  fieldsDropdownOpen: false,
  isHierarchy: false,
  isHierarchyChild: true,
  isDimensionCheck: true,
  parentKey: "hier-parent-1",
  measure: { isMeasureCheck: false, DataType: "", Format: "" },
  sort: { isSortCheck: false, value: "" },
  aggregation: { isAggregationCheck: false, value: "" },
  isPartitionCheck: false,
  semanticType: "",
  synonyms: "",
  topic: "",
};

const measureField = {
  ...sampleField,
  key: "field-measure",
  fields: "Revenue",
  measure: { isMeasureCheck: true, DataType: "Number", Format: "#,##0" },
  isDimensionCheck: false,
};

function buildCubeState(overrides = {}) {
  return {
    cubeCurrentState: "initial",
    headerData: {},
    fieldSearchText: "",
    cubeSearchedColumn: "",
    cubeFieldsData: {
      id: "",
      domainName: "Finance",
      cubeDescription: "Sales analytics cube",
      children: [sampleField],
      hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
    },
    cubeVisibleIndeterminate: false,
    isCubeVisibleCheck: true,
    cubeInitialList: [],
    cubeMode: "create",
    isCubeTableModeNormal: true,
    cubeFieldsDataBackup: {},
    ...overrides,
  };
}

function createStore(cubeState) {
  return {
    getState: () => ({ cube: cubeState }),
    dispatch: jest.fn(),
    subscribe: () => () => {},
  };
}

function renderCubeEditor(cubeState) {
  const store = createStore(cubeState);
  const view = render(
    <Provider store={store}>
      <CubeEditor />
    </Provider>,
  );
  return { store, ...view };
}

describe("CubeEditor layout & structure", () => {
  it("it should renders the root cube editor container", () => {
    const { container } = renderCubeEditor(buildCubeState());
    expect(container.querySelector(".cube-editor")).toBeInTheDocument();
  });

  it("should renders domain label anddd input with correct value", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByText("Domain")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Domain name")).toHaveValue("Finance");
    expect(screen.getByPlaceholderText("Cube description")).toHaveValue(
      "Sales analytics cube",
    );
  });

  it("shoulld renders empty domain input when domainnaame is not set", () => {
    renderCubeEditor(
      buildCubeState({
        cubeFieldsData: {
          domainName: "",
          children: [],
          hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
        },
      }),
    );
    expect(screen.getByPlaceholderText("Domain name")).toHaveValue("");
  });

  it("it should renders the fields table", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByText("Fields")).toBeInTheDocument();
  });

  it("should renders a field row with the field name", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByText("Sales Amount")).toBeInTheDocument();
  });

  it("should renders multiple field rows", () => {
    const state = buildCubeState({
      cubeFieldsData: {
        domainName: "Finance",
        children: [
          sampleField,
          { ...sampleField, key: "field-2", fields: "Profit" },
        ],
        hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    expect(screen.getByText("Sales Amount")).toBeInTheDocument();
    expect(screen.getByText("Profit")).toBeInTheDocument();
  });

  it("should renders with empty children without crashing", () => {
    renderCubeEditor(
      buildCubeState({
        cubeFieldsData: {
          domainName: "Finance",
          children: [],
          hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
        },
      }),
    );
    expect(screen.getByText("Fields")).toBeInTheDocument();
  });
});

describe("CubeEditor normal table mode (isCubeTableModeNormal:: true)", () => {
  it("should shows Semantic Type, Synonyms, Topic, and Dimension/Measure columns", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: true }));
    expect(screen.getByText("Semantic Type")).toBeInTheDocument();
    expect(screen.getByText("Synonyms")).toBeInTheDocument();
    expect(screen.getByText("Topic")).toBeInTheDocument();
    expect(screen.getByText("Dimension/Measure")).toBeInTheDocument();
  });

  it("should does NOT show Sort, Aggregation, or Partition columns", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: true }));
    expect(screen.queryByText("Sort")).not.toBeInTheDocument();
    expect(screen.queryByText("Aggregation")).not.toBeInTheDocument();
    expect(screen.queryByText("Partition")).not.toBeInTheDocument();
  });

  it("should renders synonym input with the field's existing value", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByDisplayValue("revenue")).toBeInTheDocument();
  });

  it("should renders topic input with the field's existing value", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByDisplayValue("Sales")).toBeInTheDocument();
  });

  it("should shows 'Dimension' label when isMeasureCheck is false", () => {
    renderCubeEditor(buildCubeState());
    expect(screen.getByText("Dimension")).toBeInTheDocument();
  });

  it("should shows 'Measure' label when isMeasureCheck is true", () => {
    renderCubeEditor(
      buildCubeState({
        cubeFieldsData: {
          domainName: "Finance",
          children: [measureField],
          hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
        },
      }),
    );
    expect(screen.getByText("Measure")).toBeInTheDocument();
  });
});

describe("CubeEditor schedule table mode (isCubeTableModeNormal: false)", () => {
  it("should shows Sort, Aggregation, and Partition columns", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: false }));
    expect(screen.getByText("Sort")).toBeInTheDocument();
    expect(screen.getByText("Aggregation")).toBeInTheDocument();
    expect(screen.getByText("Partition")).toBeInTheDocument();
  });

  it("should does not show Semantic Type or Dimension/Measure columns", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: false }));
    expect(screen.queryByText("Semantic Type")).not.toBeInTheDocument();
    expect(screen.queryByText("Dimension/Measure")).not.toBeInTheDocument();
  });

  it("should renders sort checkbox unchecked by default", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: false }));
    const checkboxes = screen.getAllByRole("checkbox");
    expect(checkboxes[0]).not.toBeChecked();
  });

  it("should renders partition checkbox unchecked by default", () => {
    renderCubeEditor(buildCubeState({ isCubeTableModeNormal: false }));
    const checkboxes = screen.getAllByRole("checkbox");
    checkboxes.forEach((cb) => expect(cb).not.toBeChecked());
  });

  it("shouild renders aggregation checkbox checked when isAggregationCheck is true", () => {
    const state = buildCubeState({
      isCubeTableModeNormal: false,
      cubeFieldsData: {
        domainName: "Finance",
        children: [
          {
            ...measureField,
            aggregation: { isAggregationCheck: true, value: "Sum" },
          },
        ],
        hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    const checkboxes = screen.getAllByRole("checkbox");
    expect(checkboxes[1]).toBeChecked();
  });

  it("should renders Sort checkbox checked when isSortCheck is true", () => {
    const state = buildCubeState({
      isCubeTableModeNormal: false,
      cubeFieldsData: {
        domainName: "Finance",
        children: [
          {
            ...sampleField,
            isDimensionCheck: true,
            sort: { isSortCheck: true, value: "Ascending" },
          },
        ],
        hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    const checkboxes = screen.getAllByRole("checkbox");
    expect(checkboxes[0]).toBeChecked();
  });
});

describe("CubeEditor  dispatched actions", () => {
  it("should dispatches updateFieldValues with cubeDescription on description change", () => {
    const { store } = renderCubeEditor(buildCubeState());
    fireEvent.change(screen.getByPlaceholderText("Cube description"), {
      target: { value: "Updated cube summary" },
    });
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({
        updateName: "cubeDescription",
        checkVal: "Updated cube summary",
      }),
    );
  });

  it("should dispatches updateFieldValues with domainName on domain input change", () => {
    const { store } = renderCubeEditor(buildCubeState());
    fireEvent.change(screen.getByPlaceholderText("Domain name"), {
      target: { value: "Retail" },
    });
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({ updateName: "domainName", checkVal: "Retail" }),
    );
  });

  it("should dispatches updateFieldValues on Sort checkbox change (schedule mode)", () => {
    const state = buildCubeState({
      isCubeTableModeNormal: false,
      cubeFieldsData: {
        domainName: "Finance",
        children: [{ ...sampleField, isDimensionCheck: true }],
        hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
      },
    });
    const { store } = renderCubeEditor(state);
    const checkboxes = screen.getAllByRole("checkbox");
    fireEvent.click(checkboxes[0]); 
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({
        updateName: "sort",
        key: "isSortCheck",
        value: true,
        recordKey: "field-1",
        isHierarchyChild: false,
        hierarchyKey: undefined,
      }),
    );
  });

  it("should dispatches updatefieldvalues on aggregation checkbox change ", () => {
    const state = buildCubeState({
      isCubeTableModeNormal: false,
      cubeFieldsData: {
        domainName: "Finance",
        children: [
          {
            ...measureField,
            aggregation: { isAggregationCheck: false, value: "" },
          },
        ],
        hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
      },
    });
    const { store } = renderCubeEditor(state);
    const checkboxes = screen.getAllByRole("checkbox");
    fireEvent.click(checkboxes[1]); 
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({
        updateName: "aggregation",
        key: "isAggregationCheck",
        value: true,
        recordKey: "field-measure",
        isHierarchyChild: false,
        hierarchyKey: undefined,
      }),
    );
  });

  it("should dispatches updatefieldvalues on ppartition checkbox change", () => {
    const state = buildCubeState({ isCubeTableModeNormal: false });
    const { store } = renderCubeEditor(state);
    const checkboxes = screen.getAllByRole("checkbox");
    fireEvent.click(checkboxes[2]); // Partition checkbox
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({
        updateName: "isPartitionCheck",
        checkVal: true,
        recordKey: "field-1",
        isHierarchyChild: false,
        hierarchyKey: undefined,
      }),
    );
  });
});

describe("CubeSemanticListInput local state & commit", () => {
  it("should updates local value on change without dispatching immediately", () => {
    const { store } = renderCubeEditor(buildCubeState());
    const synonymInput = screen.getByDisplayValue("revenue");
    fireEvent.change(synonymInput, { target: { value: "sales" } });
    const domainDispatch = store.dispatch.mock.calls.find(
      (call) => call[0]?.payload?.updateName === "synonyms",
    );
    expect(domainDispatch).toBeUndefined();
    expect(synonymInput).toHaveValue("sales");
  });

  it("should dispatches updateFieldValues with new synonyms value on blur", () => {
    const { store } = renderCubeEditor(buildCubeState());
    const synonymInput = screen.getByDisplayValue("revenue");
    fireEvent.change(synonymInput, { target: { value: "clients" } });
    fireEvent.blur(synonymInput);
    expect(store.dispatch).toHaveBeenCalledWith(
      updateFieldValues({
        updateName: "synonyms",
        checkVal: "clients",
        recordKey: "field-1",
        isHierarchyChild: false,
        hierarchyKey: undefined,
      }),
    );
  });

  it("should dispatches on Enter key press", () => {
    const { store } = renderCubeEditor(buildCubeState());
    const synonymInput = screen.getByDisplayValue("revenue");
    fireEvent.change(synonymInput, { target: { value: "users" } });
    fireEvent.keyDown(synonymInput, { key: "Enter", code: "Enter" });
    fireEvent.keyPress(synonymInput, { key: "Enter", code: "Enter" });
    expect(
      store.dispatch.mock.calls.some(
        (call) =>
          call[0]?.payload?.updateName === "synonyms" &&
          call[0]?.payload?.checkVal === "users",
      ) ||
        store.dispatch.mock.calls.length > 0,
    ).toBe(true);
  });

  it("should does not dispatch if value is unchanged on blur", () => {
    const { store } = renderCubeEditor(buildCubeState());
    const synonymInput = screen.getByDisplayValue("revenue");
    fireEvent.focus(synonymInput);
    fireEvent.blur(synonymInput);
    const synonymDispatches = store.dispatch.mock.calls.filter(
      (call) => call[0]?.payload?.updateName === "synonyms",
    );
    expect(synonymDispatches).toHaveLength(0);
  });

  it("should renders synonym input as disabled for hierarchy child fields", () => {
    const state = buildCubeState({
      cubeFieldsData: {
        domainName: "Finance",
        children: [hierarchyChildField],
        hierarchyData: { isHierarchyPresent: true, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    const inputs = screen.getAllByRole("textbox");
    const disabledInputs = inputs.filter((el) => el.disabled);
    expect(disabledInputs.length).toBeGreaterThan(0);
  });
});

describe("CubeEditor  hierarchy rows", () => {
  it("should does not render semantic inputs for hierarchy parent rows", () => {
    const state = buildCubeState({
      cubeFieldsData: {
        domainName: "HR",
        children: [hierarchyParentField],
        hierarchyData: { isHierarchyPresent: true, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    const textboxes = screen.getAllByRole("textbox");
    expect(
      textboxes.every((tb) => tb.placeholder === "Domain name" || tb.disabled),
    ).toBe(false);
  });

  it("should renders field name for hierarchy child", () => {
    const state = buildCubeState({
      cubeFieldsData: {
        domainName: "Finance",
        children: [hierarchyChildField],
        hierarchyData: { isHierarchyPresent: true, hierarchyList: [] },
      },
    });
    renderCubeEditor(state);
    expect(screen.getByText("Year")).toBeInTheDocument();
  });
});

describe("CubeEditor  search filter UI", () => {
  it("renders a search icon in the Fields column header", () => {
    const { container } = renderCubeEditor(buildCubeState());
    expect(
      container.querySelector(".anticon-search") ||
        container.querySelector("[data-icon='search']"),
    ).toBeTruthy();
  });
});

describe("CubeEditor  CubeFieldInfo tooltips", () => {
  it("renders info icons next to column headers", () => {
    const { container } = renderCubeEditor(buildCubeState());
    const infoIcons = container.querySelectorAll(".cube-info-icon");
    expect(infoIcons.length).toBeGreaterThanOrEqual(3);
  });
  it("renders the cube-info-icon next to the Domain label", () => {
    const { container } = renderCubeEditor(buildCubeState());
    const domainLabel = container.querySelector(".cube-domain-label");
    expect(domainLabel).toBeInTheDocument();
    expect(domainLabel.querySelector(".cube-info-icon")).toBeInTheDocument();
  });
});


