import React from "react";
import {
  fireEvent,
  render,
  screen,
  waitFor
} from "@testing-library/react";
import { Provider } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { hcrDSQuery } from "../../../components/hi-canned-reports/hcr-constants";

let AdvancedTableProperties;

jest.mock("uuid", () => ({
  v4: jest.fn()
}));

const originalFetch = global.fetch;

global.fetch = jest.fn((url, ...rest) => {
  const urlStr = String(url);
  if (urlStr.includes(".wasm") || urlStr.includes("muze")) {
    return new Promise(() => {});
  }
  return originalFetch
    ? originalFetch(url, ...rest)
    : Promise.reject(new Error("fetch not available"));
});

afterAll(() => {
  global.fetch = originalFetch;
});

let uuidCounter = 0;

beforeAll(() => {
  AdvancedTableProperties =
    require("../../../components/hi-canned-reports/hcrCanvas/advancedTableProperties").default;
});

beforeEach(() => {
  uuidCounter = 0;
  uuidv4.mockImplementation(() => `uuid-${++uuidCounter}`);
});

const MockField = ({ label, value = "", onChange = () => { } }) => (
  <label>
    <span>{label}</span>
    <input
      value={value ?? ""}
      onChange={(event) => onChange(event.target.value)}
    />
  </label>
);

const editorPanels = {
  InputFiled: MockField,
  InputNumberFiled: MockField,
  SelectField: MockField
};

const fields = [
  { name: "alpha", clazz: "java.lang.String" },
  { name: "beta", clazz: "java.lang.Integer" },
  { name: "gamma" }
];

const makeQuery = (id, name, overrides = {}) => ({
  id,
  name,
  executeQueryData: {
    data: [{ value: 1 }],
    field: fields
  },
  ...overrides
});

const mainQuery = makeQuery("q-main", "Main Query");
const otherQuery = makeQuery("q-other", "Other Query");
const emptyQuery = {
  id: "q-empty",
  name: "Empty Query",
  executeQueryData: { data: [], field: [] }
};

const makePane = (overrides = {}) => ({
  key: "pane1",
  dsPaneTypes: [
    {
      dataSourcePane: hcrDSQuery,
      menu: [mainQuery, otherQuery, emptyQuery]
    }
  ],
  selectedQueryId: "q-main",
  subDataSets: [],
  activeKey: "report-1",
  tableStyles: [],
  ...overrides
});

const buildStore = ({
  panes = [makePane()],
  activeKey = "pane1",
  designerProperties = {},
  hcrPropertiesConfiguration = {},
  hcrExportPropertiesData = {}
} = {}) => ({
  getState: () => ({
    cannedReports: {
      present: {
        hcrTabData: {
          panes,
          activeKey
        },
        hCROldConfigurations: {
          HCR: { HCR: { designerProperties } },
          HcrPropertiesConfiguration: hcrPropertiesConfiguration
        },
        hcrExportPropertiesData
      }
    }
  }),
  subscribe: jest.fn(),
  dispatch: jest.fn()
});

const makeNodeConfig = (overrides = {}) => ({
  id: "table-1",
  selectedQueryID: "q-main",
  selectedFields: ["alpha", "beta"],
  selectedColumnFields: ["alpha"],
  selectedGroupFields: [],
  borders: {},
  padding: {},
  properties: [],
  x: 10,
  y: 20,
  width: 200,
  height: 200,
  tableConfig: {},
  ...overrides
});

const renderComponent = ({
  nodeConfig = makeNodeConfig(),
  pane = makePane(),
  panes = [pane],
  activeKey = "pane1",
  designerProperties,
  hcrPropertiesConfiguration,
  hcrExportPropertiesData,
  onNodeConfigChange = jest.fn()
} = {}) => {
  const store = buildStore({
    panes,
    activeKey,
    designerProperties,
    hcrPropertiesConfiguration,
    hcrExportPropertiesData
  });
  const view = render(
    <Provider store={store}>
      <AdvancedTableProperties
        EditorPanels={editorPanels}
        nodeConfig={nodeConfig}
        onNodeConfigChange={onNodeConfigChange}
      />
    </Provider>
  );
  return { ...view, store, onNodeConfigChange };
};

const getSelectByLabel = (label) =>
  screen
    .getByText(label)
    .closest(".property-label")
    .parentElement.parentElement.querySelector(".ant-select");

const getSwitchByLabel = (label) => {
  let element = screen.getByText(label);
  while (element && element !== document.body) {
    if (
      element.nextElementSibling?.matches?.('button[role="switch"]')
    ) {
      return element.nextElementSibling;
    }
    element = element.parentElement;
  }
  return null;
};

const getVisibleDropdown = () =>
  document.querySelector(".ant-select-dropdown:not(.ant-select-dropdown-hidden)");

const getVisibleOptions = () =>
  Array.from(
    getVisibleDropdown()?.querySelectorAll(".ant-select-item-option") || []
  );

const getVisibleOptionTexts = () =>
  getVisibleOptions().map((option) => option.textContent.trim());

const getVisibleOptionByText = (optionText) =>
  getVisibleOptions().find(
    (element) => element.textContent.trim() === optionText
  );

const openSelect = async (select) => {
  fireEvent.mouseDown(select.querySelector(".ant-select-selector"));
  await waitFor(() => expect(getVisibleDropdown()).toBeInTheDocument());
};

const clickVisibleOption = async (optionText) => {
  await waitFor(() => expect(getVisibleOptionTexts()).toContain(optionText));
  const option = getVisibleOptions().find(
    (element) => element.textContent.trim() === optionText
  );
  fireEvent.click(option);
};

const clickSelectAll = async (buttonText = "Select All") => {
  const button = await screen.findByRole("button", { name: buttonText });
  fireEvent.click(button);
};

const expandPanel = (title) => {
  fireEvent.click(screen.getByText(title));
};

const getFieldInput = (label) =>
  screen.getByText(label).closest("label").querySelector("input");

const getActions = (store, type) =>
  store.dispatch.mock.calls
    .map(([action]) => action)
    .filter((action) => action.type === type);

const getEditNodePayload = (store) =>
  getActions(store, "HCR_EDIT_NODE")[0]?.payload;

const lastCopiedConfig = (onNodeConfigChange) =>
  onNodeConfigChange.mock.calls[onNodeConfigChange.mock.calls.length - 1][1];

describe("AdvancedTableProperties rendering", () => {
  it("renders the core table sections and the apply button", () => {
    renderComponent();

    expect(screen.getByText("Table")).toBeInTheDocument();
    expect(screen.getByText("Query")).toBeInTheDocument();
    expect(screen.getByText("Select Fields")).toBeInTheDocument();
    expect(screen.getByText("Select Table Group Fields")).toBeInTheDocument();
    expect(
      screen.getByText("Select Fields For Table Columns")
    ).toBeInTheDocument();
    expect(screen.getByText("Alignment")).toBeInTheDocument();
    expect(screen.getByText("Table Configuration")).toBeInTheDocument();
    expect(screen.getByText("Properties")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Apply" })).toBeEnabled();
  });

  it("displays the selected query and marks the selected fields", async () => {
    renderComponent();

    expect(getSelectByLabel("Query")).toHaveTextContent("q-main");

    await openSelect(getSelectByLabel("Select Fields"));
    expect(getVisibleOptionByText("alpha")).toHaveClass(
      "ant-select-item-option-selected"
    );
    expect(getVisibleOptionByText("beta")).toHaveClass(
      "ant-select-item-option-selected"
    );

    await openSelect(getSelectByLabel("Select Fields For Table Columns"));
    expect(getVisibleOptionByText("alpha")).toHaveClass(
      "ant-select-item-option-selected"
    );
  });

  it("lists only queries that have data or fields and excludes the main dataset query", async () => {
    renderComponent();

    await openSelect(getSelectByLabel("Query"));

    expect(getVisibleOptionTexts()).toEqual(["Other Query"]);
  });

  it("shows the main dataset message when no other query is selectable", async () => {
    renderComponent({
      pane: makePane({
        dsPaneTypes: [
          {
            dataSourcePane: hcrDSQuery,
            menu: [mainQuery]
          }
        ]
      })
    });

    await openSelect(getSelectByLabel("Query"));

    expect(
      screen.getByText("The query is already selected in the Main Dataset")
    ).toBeInTheDocument();
  });

  it("lists all fields of the selected query in the field selector", async () => {
    renderComponent();

    await openSelect(getSelectByLabel("Select Fields"));

    expect(getVisibleOptionTexts()).toEqual(["alpha", "beta", "gamma"]);
  });

  it("lists only selected fields in the group and column selectors", async () => {
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "gamma"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["gamma"]
      })
    });

    await openSelect(getSelectByLabel("Select Fields For Table Columns"));
    expect(getVisibleOptionTexts()).toEqual(["alpha", "gamma"]);

    await openSelect(getSelectByLabel("Select Table Group Fields"));
    expect(getVisibleOptionTexts()).toEqual(["alpha", "gamma"]);
  });

  it("hides invalid selected field values from the field selector", async () => {
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "ghost"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));

    expect(getVisibleOptionTexts()).toEqual(["alpha", "beta", "gamma"]);
    expect(getVisibleOptionByText("alpha")).toHaveClass(
      "ant-select-item-option-selected"
    );
    expect(getVisibleOptionByText("ghost")).toBeUndefined();
  });

  it("renders without an active pane without crashing", () => {
    renderComponent({
      panes: [],
      activeKey: "missing-pane",
      nodeConfig: makeNodeConfig({
        selectedQueryID: undefined,
        selectedFields: [],
        selectedColumnFields: [],
        selectedGroupFields: []
      })
    });

    expect(screen.getByText("Query")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Apply" })).toBeEnabled();
  });
});

describe("AdvancedTableProperties field cleanup effects", () => {
  it("removes invalid selected, column, and group fields", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "ghost"],
        selectedColumnFields: ["beta", "ghost"],
        selectedGroupFields: ["ghost", "alpha"]
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).toHaveBeenCalledTimes(1);
    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha"],
        selectedColumnFields: ["beta"],
        selectedGroupFields: ["alpha"]
      })
    );
  });

  it("does not clean up fields when every selected field is valid", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["beta"]
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).not.toHaveBeenCalled();
  });

  it("skips field cleanup after the table has been constructed", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "ghost"],
        selectedColumnFields: ["beta", "ghost"],
        selectedGroupFields: ["ghost"],
        isTableConstructed: true
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).not.toHaveBeenCalled();
  });

  it("cleans up invalid group fields while keeping valid selected fields", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["ghost"]
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    );
  });

  it("clears column fields when no fields are selected", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: [],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).toHaveBeenCalledTimes(1);
    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: [],
        selectedColumnFields: [],
        selectedGroupFields: ["alpha"]
      })
    );
  });

  it("does not clear column fields when there are none", () => {
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: [],
        selectedColumnFields: [],
        selectedGroupFields: []
      }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).not.toHaveBeenCalled();
  });

  it("cleans up fields from the copied config when one exists", () => {
    const copiedConfig = makeNodeConfig({
      selectedFields: ["alpha", "ghost"],
      selectedColumnFields: ["alpha"],
      selectedGroupFields: []
    });
    const onNodeConfigChange = jest.fn();
    renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig }),
      onNodeConfigChange
    });

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    );
  });
});

describe("AdvancedTableProperties query selection", () => {
  it("updates the selected query", async () => {
    const { onNodeConfigChange } = renderComponent();

    await openSelect(getSelectByLabel("Query"));
    await clickVisibleOption("Other Query");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedQueryID: "q-other",
        selectedFields: ["alpha", "beta"]
      })
    );
  });

  it("copies fields and group fields from an existing sub dataset", async () => {
    const { onNodeConfigChange } = renderComponent({
      pane: makePane({
        subDataSets: [
          {
            id: "q-other",
            selectedFields: ["gamma"],
            selectedGroupFields: ["gamma"]
          }
        ]
      })
    });

    await openSelect(getSelectByLabel("Query"));
    await clickVisibleOption("Other Query");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedQueryID: "q-other",
        selectedFields: ["gamma"],
        selectedGroupFields: ["gamma"]
      })
    );
  });

  it("clears the selected query", () => {
    const { onNodeConfigChange } = renderComponent();
    const querySelect = getSelectByLabel("Query");

    fireEvent.mouseDown(querySelect.querySelector(".ant-select-clear"));

    expect(lastCopiedConfig(onNodeConfigChange).selectedQueryID).toBeUndefined();
  });

  it("disables the query selector after the table is constructed", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({ isTableConstructed: true })
    });

    expect(getSelectByLabel("Query")).toHaveClass("ant-select-disabled");
  });

  it("disables field and group selectors when a sub dataset already exists", () => {
    renderComponent({
      pane: makePane({
        subDataSets: [{ id: "q-main" }]
      })
    });

    expect(getSelectByLabel("Select Fields")).toHaveClass("ant-select-disabled");
    expect(getSelectByLabel("Select Table Group Fields")).toHaveClass(
      "ant-select-disabled"
    );
    expect(getSelectByLabel("Query")).not.toHaveClass("ant-select-disabled");
    expect(getSelectByLabel("Select Fields For Table Columns")).not.toHaveClass(
      "ant-select-disabled"
    );
  });
});

describe("AdvancedTableProperties field selection", () => {
  it("adds a field and keeps the still-valid column and group fields", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));
    await clickVisibleOption("beta");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      })
    );
  });

  it("removes a field and prunes dependent column and group fields", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["beta"]
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));
    await clickVisibleOption("alpha");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["beta"],
        selectedColumnFields: [],
        selectedGroupFields: ["beta"]
      })
    );
  });

  it("selects all query fields at once", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));
    await clickSelectAll();

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha", "beta", "gamma"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      })
    );
  });

  it("deselects all fields when everything is already selected", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta", "gamma"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["beta"]
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));
    await clickSelectAll("Deselect All");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: [],
        selectedColumnFields: [],
        selectedGroupFields: ["beta"]
      })
    );
  });

  it("selects all fields after filtering the field search", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    await openSelect(getSelectByLabel("Select Fields"));
    fireEvent.change(
      getSelectByLabel("Select Fields").querySelector(
        ".ant-select-selection-search-input"
      ),
      { target: { value: "be" } }
    );
    await clickSelectAll();

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    );
  });

  it("adds a table column field", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    await openSelect(getSelectByLabel("Select Fields For Table Columns"));
    await clickVisibleOption("beta");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedColumnFields: ["alpha", "beta"]
      })
    );
  });

  it("adds a table group field", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    await openSelect(getSelectByLabel("Select Table Group Fields"));
    await clickVisibleOption("beta");

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedGroupFields: ["beta"]
      })
    );
  });

  it("selects all filtered column fields", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    await openSelect(getSelectByLabel("Select Fields For Table Columns"));
    fireEvent.change(
      getSelectByLabel("Select Fields For Table Columns").querySelector(
        ".ant-select-selection-search-input"
      ),
      { target: { value: "be" } }
    );
    await clickSelectAll();

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedColumnFields: ["alpha", "beta"]
      })
    );
  });

  it("selects all filtered group fields", async () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha"]
      })
    });

    await openSelect(getSelectByLabel("Select Table Group Fields"));
    fireEvent.change(
      getSelectByLabel("Select Table Group Fields").querySelector(
        ".ant-select-selection-search-input"
      ),
      { target: { value: "be" } }
    );
    await clickSelectAll();

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({
        selectedGroupFields: ["alpha", "beta"]
      })
    );
  });

  it("disables all table selectors after construction", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({ isTableConstructed: true })
    });

    expect(getSelectByLabel("Query")).toHaveClass("ant-select-disabled");
    expect(getSelectByLabel("Select Fields")).toHaveClass("ant-select-disabled");
    expect(getSelectByLabel("Select Table Group Fields")).toHaveClass(
      "ant-select-disabled"
    );
    expect(getSelectByLabel("Select Fields For Table Columns")).toHaveClass(
      "ant-select-disabled"
    );
  });
});

describe("AdvancedTableProperties table configuration", () => {
  const booleanOptions = [
    "Add Table Header",
    "Add Column Header",
    "Add Group Header",
    "Add Group Footer",
    "Add Column Footer",
    "Add Table Footer"
  ];

  it("renders all table switches off by default", () => {
    renderComponent();

    expandPanel("Table Configuration");

    booleanOptions.forEach((label) => {
      expect(getSwitchByLabel(label)).not.toHaveClass("ant-switch-checked");
    });
    expect(
      getSwitchByLabel("Use alternated detail rows background")
    ).not.toHaveClass("ant-switch-checked");
  });

  it.each(booleanOptions)(
    "updates the table config when %s is toggled",
    (label) => {
      const { onNodeConfigChange } = renderComponent();

      expandPanel("Table Configuration");
      fireEvent.click(getSwitchByLabel(label));

      const tableConfig =
        lastCopiedConfig(onNodeConfigChange).tableConfig;
      expect(tableConfig).toEqual({ [labelKey(label)]: true });
      expect(lastCopiedConfig(onNodeConfigChange).isAppliedClicked).toBe(false);
    }
  );

  it("reflects table config values in the switches", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({
        tableConfig: {
          addTableHeader: true,
          addGroupFooter: true
        }
      })
    });

    expandPanel("Table Configuration");

    expect(getSwitchByLabel("Add Table Header")).toHaveClass(
      "ant-switch-checked"
    );
    expect(getSwitchByLabel("Add Group Footer")).toHaveClass(
      "ant-switch-checked"
    );
    expect(getSwitchByLabel("Add Table Footer")).not.toHaveClass(
      "ant-switch-checked"
    );
  });

  it("preserves other table config values when toggling a switch", () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        tableConfig: {
          bodyColor: "#123456",
          addTableHeader: true
        }
      })
    });

    expandPanel("Table Configuration");
    fireEvent.click(getSwitchByLabel("Add Table Footer"));

    expect(lastCopiedConfig(onNodeConfigChange).tableConfig).toEqual({
      bodyColor: "#123456",
      addTableHeader: true,
      addTableFooter: true
    });
  });

  it("enables alternating row background", () => {
    const { onNodeConfigChange } = renderComponent();

    expandPanel("Table Configuration");
    fireEvent.click(
      getSwitchByLabel("Use alternated detail rows background")
    );

    expect(lastCopiedConfig(onNodeConfigChange).expression).toBe(true);
  });

  it("shows the alternating row color picker with its default color", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({ expression: true })
    });

    expandPanel("Table Configuration");

    expect(screen.getByText("Alt. Row Detail")).toBeInTheDocument();
    expect(screen.getByText("#BFE1FF")).toBeInTheDocument();
  });

  it("shows the configured alternating row color", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({
        expression: true,
        expressionBackColor: "#ABCDEF"
      })
    });

    expandPanel("Table Configuration");

    expect(screen.getByText("#ABCDEF")).toBeInTheDocument();
  });

  it("hides the alternating row color picker when expression is off", () => {
    renderComponent();

    expandPanel("Table Configuration");

    expect(screen.queryByText("Alt. Row Detail")).not.toBeInTheDocument();
  });

  it("disables table configuration switches after construction", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({ isTableConstructed: true })
    });

    expandPanel("Table Configuration");

    booleanOptions.forEach((label) => {
      expect(getSwitchByLabel(label)).toHaveClass("ant-switch-disabled");
    });
    expect(
      getSwitchByLabel("Use alternated detail rows background")
    ).toHaveClass("ant-switch-disabled");
  });

  it("disables the apply button once the table is applied", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({ isAppliedClicked: true })
    });

    expect(screen.getByRole("button", { name: "Apply" })).toBeDisabled();
  });
});

const labelKey = (label) => {
  const keys = {
    "Add Table Header": "addTableHeader",
    "Add Column Header": "addColumnHeader",
    "Add Group Header": "addGroupHeader",
    "Add Group Footer": "addGroupFooter",
    "Add Column Footer": "addColumnFooter",
    "Add Table Footer": "addTableFooter"
  };
  return keys[label];
};

describe("AdvancedTableProperties node defaults", () => {
  it("fills default node values before sending changes", () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        borders: {},
        padding: {},
        properties: []
      })
    });

    expandPanel("Table Configuration");
    fireEvent.click(getSwitchByLabel("Add Table Header"));

    expect(lastCopiedConfig(onNodeConfigChange)).toEqual(
      expect.objectContaining({
        fontFamily: "Serif",
        fontFill: "#000000",
        fill: "#fefefe",
        borders: {
          Top: { stroke: 0, style: "SOLID", color: "#000000" },
          Bottom: { stroke: 0, style: "SOLID", color: "#000000" },
          Right: { stroke: 0, style: "SOLID", color: "#000000" },
          Left: { stroke: 0, style: "SOLID", color: "#000000" }
        },
        padding: {
          Top: 1,
          Bottom: 1,
          Right: 1,
          Left: 1
        },
        properties: []
      })
    );
  });

  it("keeps explicitly configured node values", () => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        fontFamily: "Arial",
        fontFill: "#111111",
        fill: "#222222",
        borders: {
          Top: { stroke: 2, style: "DASH", color: "#333333" }
        },
        padding: {
          Top: 0,
          Bottom: 2,
          Right: 3,
          Left: 4
        }
      })
    });

    expandPanel("Table Configuration");
    fireEvent.click(getSwitchByLabel("Add Table Header"));

    const copiedConfig = lastCopiedConfig(onNodeConfigChange);
    expect(copiedConfig.fontFamily).toBe("Arial");
    expect(copiedConfig.fontFill).toBe("#111111");
    expect(copiedConfig.fill).toBe("#222222");
    expect(copiedConfig.borders.Top).toEqual({
      stroke: 2,
      style: "DASH",
      color: "#333333"
    });
    expect(copiedConfig.padding).toEqual({
      Top: 0,
      Bottom: 2,
      Right: 3,
      Left: 4
    });
  });

  it("updates the copied config instead of the original node config when one exists", () => {
    const copiedConfig = makeNodeConfig({
      customKey: "custom-value",
      tableConfig: { bodyColor: "#111111" }
    });
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig })
    });

    expandPanel("Table Configuration");
    fireEvent.click(getSwitchByLabel("Add Table Footer"));

    const updatedConfig = lastCopiedConfig(onNodeConfigChange);
    expect(updatedConfig.customKey).toBe("custom-value");
    expect(updatedConfig.tableConfig).toEqual({
      bodyColor: "#111111",
      addTableFooter: true
    });
  });

  it("renders values from the copied config over the node config", async () => {
    const copiedConfig = makeNodeConfig({
      selectedQueryID: "q-other",
      selectedFields: ["gamma"],
      selectedColumnFields: ["gamma"],
      selectedGroupFields: [],
      tableConfig: { addTableFooter: true }
    });
    renderComponent({
      nodeConfig: makeNodeConfig({
        selectedQueryID: "q-main",
        tableConfig: { addTableHeader: true },
        copiedConfig
      })
    });

    expect(getSelectByLabel("Query")).toHaveTextContent("Other Query");

    await openSelect(getSelectByLabel("Select Fields"));
    expect(getVisibleOptionByText("gamma")).toHaveClass(
      "ant-select-item-option-selected"
    );

    expandPanel("Table Configuration");
    expect(getSwitchByLabel("Add Table Footer")).toHaveClass(
      "ant-switch-checked"
    );
    expect(getSwitchByLabel("Add Table Header")).not.toHaveClass(
      "ant-switch-checked"
    );
  });
});

describe("AdvancedTableProperties alignment", () => {
  it.each([
    ["X", "x"],
    ["Y", "y"],
    ["Width", "width"],
    ["Height", "height"]
  ])("updates %s through onNodeConfigChange", (label, key) => {
    const { onNodeConfigChange } = renderComponent({
      nodeConfig: makeNodeConfig({
        x: 10,
        y: 20,
        width: 200,
        height: 200
      })
    });

    expandPanel("Alignment");
    const input = getFieldInput(label);
    expect(input).toHaveValue(String(key === "x" ? 10 : key === "y" ? 20 : 200));

    fireEvent.change(input, { target: { value: "42" } });

    expect(onNodeConfigChange).toHaveBeenCalledWith(
      "copiedConfig",
      expect.objectContaining({ [key]: "42" })
    );
  });
});

describe("AdvancedTableProperties properties panel", () => {
  it("adds a custom property to the node config", () => {
    const { onNodeConfigChange } = renderComponent();

    expandPanel("Properties");

    const keyContainer = screen.getByTestId(
      "hcr-components-properties-key-container"
    );
    const valueInput = screen.getByRole("inputBox");
    const addButton = screen.getByTestId(
      "hcr-components-properties-add-button"
    );

    expect(addButton).toBeDisabled();
    fireEvent.change(keyContainer.querySelector("input"), {
      target: { value: "customKey" }
    });
    fireEvent.change(valueInput, { target: { value: "customValue" } });
    expect(addButton).toBeEnabled();
    fireEvent.click(addButton);

    expect(lastCopiedConfig(onNodeConfigChange).properties).toEqual([
      expect.objectContaining({
        id: expect.any(String),
        key: "customKey",
        value: "customValue",
        alias: "",
        description: ""
      })
    ]);
  });

  it("requires both a property key and value before adding", () => {
    renderComponent();

    expandPanel("Properties");

    const keyContainer = screen.getByTestId(
      "hcr-components-properties-key-container"
    );
    const addButton = screen.getByTestId(
      "hcr-components-properties-add-button"
    );

    fireEvent.change(keyContainer.querySelector("input"), {
      target: { value: "customKey" }
    });
    expect(addButton).toBeDisabled();

    fireEvent.change(
      screen.getByTestId("hcr-components-properties-value-container").querySelector(
        "input"
      ),
      { target: { value: "customValue" } }
    );
    expect(addButton).toBeEnabled();
  });

  it("shows the number of configured properties", () => {
    renderComponent({
      nodeConfig: makeNodeConfig({
        properties: [
          { id: "p1", key: "key1", value: "value1" },
          { id: "p2", key: "key2", value: "value2" }
        ]
      })
    });

    expandPanel("Properties");

    expect(
      screen.getByText("2 properties configured (click to edit)")
    ).toBeInTheDocument();
  });
});

describe("AdvancedTableProperties common table properties", () => {
  const hcrPropertiesConfiguration = {
    table: {
      content: [
        {
          name: "Table",
          content: [
            {
              type: "textBox",
              name: "Font Family",
              label: "Font Family",
              value: "fontFamily"
            },
            {
              type: "checkBox",
              name: "Bold",
              label: "Bold",
              value: "bold"
            },
            {
              type: "DropDown",
              name: "Mode",
              value: "mode"
            }
          ]
        }
      ]
    }
  };

  it("updates a text table property", () => {
    const { onNodeConfigChange } = renderComponent({
      hcrPropertiesConfiguration: {
        ...hcrPropertiesConfiguration,
        designerProperties: {
          mode: ["Opaque", "Transparent"]
        }
      }
    });

    const input = getFieldInput("Font Family");
    expect(input).toHaveValue("Serif");

    fireEvent.change(input, { target: { value: "Arial" } });

    expect(lastCopiedConfig(onNodeConfigChange).fontFamily).toBe("Arial");
  });

  it("updates a checkbox table property", () => {
    const { onNodeConfigChange } = renderComponent({
      hcrPropertiesConfiguration
    });

    fireEvent.click(screen.getByRole("checkbox", { name: "Bold" }));

    expect(lastCopiedConfig(onNodeConfigChange).bold).toBe(true);
  });

  it("updates a dropdown table property", async () => {
    const { onNodeConfigChange } = renderComponent({
      hcrPropertiesConfiguration,
      designerProperties: {
        mode: ["Opaque", "Transparent"]
      }
    });

    await openSelect(getSelectByLabel("Mode"));
    await clickVisibleOption("Transparent");

    expect(lastCopiedConfig(onNodeConfigChange).mode).toBe("Transparent");
  });
});

describe("AdvancedTableProperties apply", () => {
  it("builds a table and marks it as constructed", () => {
    const { store } = renderComponent();

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const editAction = getActions(store, "HCR_EDIT_NODE")[0];
    expect(editAction.payload).toEqual(
      expect.objectContaining({
        nodeId: "table-1",
        multiple: true,
        isTable: true
      })
    );

    const propertiesToBeChange = editAction.payload.propertiesToBeChange;
    expect(propertiesToBeChange).toEqual(
      expect.objectContaining({
        isAppliedClicked: true,
        isTableConstructed: true,
        copiedConfig: {},
        width: 200,
        height: 125
      })
    );
    expect(Object.keys(propertiesToBeChange.columns)).toHaveLength(1);
    expect(Object.keys(propertiesToBeChange.nodes)).toHaveLength(1);

    const tableNode = Object.values(propertiesToBeChange.nodes)[0];
    expect(tableNode).toEqual(
      expect.objectContaining({
        name: "alpha",
        label: "$F{alpha}",
        band: "columnData",
        isTableCell: true,
        backendDataType: "java.lang.String",
        cellId: expect.any(String)
      })
    );
    expect(Object.keys(propertiesToBeChange.cells)).toHaveLength(5);
    expect(propertiesToBeChange.bands.columnData.height).toBe(25);
  });

  it("expands the table width when the columns exceed the node width", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedColumnFields: ["alpha", "beta", "gamma"],
        width: 200
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const propertiesToBeChange = getEditNodePayload(store).propertiesToBeChange;
    expect(propertiesToBeChange.width).toBe(303);
    expect(Object.keys(propertiesToBeChange.columns)).toHaveLength(3);
  });

  it("keeps the node width when the columns fit", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedColumnFields: ["alpha", "beta"],
        width: 500
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const propertiesToBeChange = getEditNodePayload(store).propertiesToBeChange;
    expect(propertiesToBeChange.width).toBe(500);
  });

  it("adds the group header bands to the table height", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha", "beta"],
        tableConfig: { addGroupHeader: true }
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(getEditNodePayload(store).propertiesToBeChange.height).toBe(175);
  });

  it("adds the group header and footer bands to the table height", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha", "beta"],
        tableConfig: {
          addGroupHeader: true,
          addGroupFooter: true
        }
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(getEditNodePayload(store).propertiesToBeChange.height).toBe(225);
  });

  it("builds one column when no column fields are selected", () => {
    const copiedConfig = makeNodeConfig({
      selectedFields: [],
      selectedColumnFields: [],
      selectedGroupFields: ["alpha"],
      height: 200,
      width: 200
    });
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: [],
        selectedColumnFields: [],
        selectedGroupFields: ["alpha"],
        copiedConfig
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const propertiesToBeChange = getEditNodePayload(store).propertiesToBeChange;
    expect(propertiesToBeChange.height).toBe(200);
    expect(Object.keys(propertiesToBeChange.columns)).toHaveLength(1);
    expect(Object.keys(propertiesToBeChange.nodes)).toHaveLength(0);
  });

  it("creates column header and data nodes when column headers are enabled", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedColumnFields: ["alpha", "beta"],
        tableConfig: { addColumnHeader: true },
        width: 200
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const propertiesToBeChange = getEditNodePayload(store).propertiesToBeChange;
    const tableNodes = Object.values(propertiesToBeChange.nodes);
    expect(tableNodes).toHaveLength(4);
    expect(tableNodes.filter((node) => node.band === "columnHeaderOfTable")).toHaveLength(2);
    expect(tableNodes.filter((node) => node.band === "columnData")).toHaveLength(2);
    expect(
      tableNodes.find((node) => node.band === "columnHeaderOfTable")
    ).toEqual(
      expect.objectContaining({
        name: "text",
        label: "alpha",
        isTableCell: true
      })
    );
  });

  it("updates alternate row banding styles when expression is configured", () => {
    const copiedConfig = makeNodeConfig({
      expression: true,
      expressionBackColor: "#ABCDEF"
    });
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig }),
      pane: makePane({
        tableStyles: [
          {
            id: "td-style",
            tableId: "table-1",
            isTD: true,
            bandsApplicable: []
          },
          {
            id: "th-style",
            tableId: "table-1",
            isTH: true,
            bandsApplicable: []
          }
        ]
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const styleAction = getActions(store, "HCR_UPDATE_TABLE_STYLES")[0];
    expect(styleAction.payload).toEqual({
      actionType: "updateAltRowBanding",
      styleId: "td-style",
      updatedStyles: {
        expression: "new Boolean($V{REPORT_COUNT}.intValue()%2==0)",
        expressionBackColor: "#ABCDEF"
      }
    });
  });

  it("uses the default alternate row color when none is configured", () => {
    const copiedConfig = makeNodeConfig({ expression: true });
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig }),
      pane: makePane({
        tableStyles: [
          {
            id: "td-style",
            tableId: "table-1",
            isTD: true,
            bandsApplicable: []
          }
        ]
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(
      getActions(store, "HCR_UPDATE_TABLE_STYLES")[0].payload.updatedStyles
        .expressionBackColor
    ).toBe("#BFE1FF");
  });

  it("dispatches alternate row banding without a style id when no detail style exists", () => {
    const copiedConfig = makeNodeConfig({ expression: true });
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig }),
      pane: makePane({ tableStyles: [] })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const styleAction = getActions(store, "HCR_UPDATE_TABLE_STYLES")[0];
    expect(styleAction.payload).toEqual(
      expect.objectContaining({
        actionType: "updateAltRowBanding",
        styleId: undefined
      })
    );
  });

  it("adds a sub dataset for the selected query", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedFields: ["alpha", "beta", "gamma"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: ["alpha", "beta"]
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const subDataSetAction = getActions(store, "HCR_UPDATE_SUB_DATASETS")[0];
    expect(subDataSetAction.payload).toEqual(
      expect.objectContaining({
        actionType: "add",
        reportKey: "report-1",
        id: "q-main",
        name: "Main Query",
        fields,
        selectedFields: ["alpha", "beta", "gamma"],
        selectedGroupFields: ["alpha", "beta"]
      })
    );
    expect(subDataSetAction.payload.groups).toEqual([
      {
        id: expect.any(String),
        expression: "$F{alpha}",
        name: "alpha",
        isChecked: true
      },
      {
        id: expect.any(String),
        expression: "$F{beta}",
        name: "beta",
        isChecked: true
      }
    ]);
  });

  it("does not add a sub dataset when one already exists for the query", () => {
    const { store } = renderComponent({
      pane: makePane({
        subDataSets: [{ id: "q-main" }]
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(getActions(store, "HCR_UPDATE_SUB_DATASETS")).toHaveLength(0);
  });

  it("does not add a sub dataset when no query is selected", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({
        selectedQueryID: undefined,
        selectedFields: ["alpha"],
        selectedColumnFields: ["alpha"],
        selectedGroupFields: []
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(getActions(store, "HCR_UPDATE_SUB_DATASETS")).toHaveLength(0);
    expect(getActions(store, "HCR_EDIT_NODE")).toHaveLength(1);
  });

  it("does not rebuild or re-add data when the table is already constructed", () => {
    const copiedConfig = makeNodeConfig({
      isTableConstructed: true,
      width: 300,
      height: 250,
      expression: true
    });
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({ copiedConfig }),
      pane: makePane({
        subDataSets: [],
        tableStyles: [{ id: "td-style", tableId: "table-1", isTD: true }]
      })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    const propertiesToBeChange = getEditNodePayload(store).propertiesToBeChange;
    expect(propertiesToBeChange.width).toBe(300);
    expect(propertiesToBeChange.height).toBe(250);
    expect(getActions(store, "HCR_UPDATE_TABLE_STYLES")).toHaveLength(0);
    expect(getActions(store, "HCR_UPDATE_SUB_DATASETS")).toHaveLength(0);
  });

  it("does not dispatch anything when apply is disabled", () => {
    const { store } = renderComponent({
      nodeConfig: makeNodeConfig({ isAppliedClicked: true })
    });

    fireEvent.click(screen.getByRole("button", { name: "Apply" }));

    expect(store.dispatch).not.toHaveBeenCalled();
  });
});
