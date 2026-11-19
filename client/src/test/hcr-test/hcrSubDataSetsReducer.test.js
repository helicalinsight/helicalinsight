import "regenerator-runtime/runtime";
import actionTypes from "../../redux/actions/actionTypes";
import {
  hcrReducer,
  hcrTabInitialState,
} from "../../redux/reducers/hCR.reducer";

const makeSubDataSet = (id, overrides = {}) => ({
  id,
  name: id,
  isEmpty: false,
  ...overrides,
});

const makeNode = (id, category, selectedQueryID, overrides = {}) => ({
  id,
  category,
  ...(selectedQueryID && { selectedQueryID }),
  ...overrides,
});

const createState = ({
  nodes = [],
  subDataSets = [],
  tableStyles = [],
} = {}) => ({
  hcrTabData: {
    activeKey: "tab-1",
    panes: [
      {
        ...hcrTabInitialState,
        key: "tab-1",
        hcrDiagramNodesData: nodes,
        subDataSets,
        tableStyles,
      },
    ],
  },
});

const getPane = (state) => state.hcrTabData.panes[0];

const getNodeIds = (state) =>
  getPane(state).hcrDiagramNodesData.map((node) => node.id);

const getSubDataSetIds = (state) =>
  getPane(state).subDataSets.map((subDataSet) => subDataSet.id);

const setNodes = (payload) => ({
  type: actionTypes.HCR_DIAGRAM_NODES_DATA,
  payload,
});

describe("hcrReducer HCR_DIAGRAM_NODES_DATA sub dataset cleanup", () => {
  it("keeps the existing array payload behaviour without touching sub data sets", () => {
    const state = createState({
      nodes: [makeNode("table-1", "advancedTable", "query-1")],
      subDataSets: [makeSubDataSet("query-1")],
    });

    const nextState = hcrReducer(
      state,
      setNodes([makeNode("table-2", "advancedTable", "query-2")]),
    );

    expect(getNodeIds(nextState)).toEqual(["table-2"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-1"]);
  });

  it("accepts an object payload with nodes and leaves sub data sets unchanged", () => {
    const state = createState({
      nodes: [makeNode("table-1", "advancedTable", "query-1")],
      subDataSets: [makeSubDataSet("query-1")],
    });

    const nextState = hcrReducer(
      state,
      setNodes({
        nodes: [makeNode("table-2", "advancedTable", "query-2")],
      }),
    );

    expect(getNodeIds(nextState)).toEqual(["table-2"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-1"]);
  });

  it("clears all sub data sets when deleteAction is set and no nodes remain", () => {
    const state = createState({
      nodes: [makeNode("table-1", "advancedTable", "query-1")],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("EMPTY_table-1", { isEmpty: true }),
      ],
    });

    const nextState = hcrReducer(
      state,
      setNodes({ nodes: [], deleteAction: true }),
    );

    expect(getNodeIds(nextState)).toEqual([]);
    expect(getSubDataSetIds(nextState)).toEqual([]);
  });

  it("keeps only sub data sets referenced by remaining advanced components", () => {
    const state = createState({
      nodes: [
        makeNode("table-1", "advancedTable", "query-1"),
        makeNode("ct-1", "crosstabv2", "query-2"),
      ],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("query-2"),
        makeSubDataSet("query-3"),
        makeSubDataSet("EMPTY-table-1", { isEmpty: true }),
      ],
    });

    const nextState = hcrReducer(
      state,
      setNodes({
        nodes: [
          makeNode("table-1", "advancedTable", "query-1"),
          makeNode("ct-1", "crosstabv2", "query-2"),
          makeNode("text-1", "text"),
        ],
        deleteAction: true,
      }),
    );

    expect(getSubDataSetIds(nextState)).toEqual(["query-1", "query-2"]);
  });

  it("clears all sub data sets when deleteAction is set and no advanced components remain", () => {
    const state = createState({
      nodes: [makeNode("table-1", "advancedTable", "query-1")],
      subDataSets: [makeSubDataSet("query-1")],
    });

    const nextState = hcrReducer(
      state,
      setNodes({
        nodes: [makeNode("text-1", "text")],
        deleteAction: true,
      }),
    );

    expect(getNodeIds(nextState)).toEqual(["text-1"]);
    expect(getSubDataSetIds(nextState)).toEqual([]);
  });
});

describe("hcrReducer HCR_DELETE_NODE sub dataset cleanup", () => {
  it("removes the sub data set owned by a deleted advanced table", () => {
    const state = createState({
      nodes: [
        makeNode("table-1", "advancedTable", "query-1"),
        makeNode("table-2", "advancedTable", "query-2"),
      ],
      subDataSets: [makeSubDataSet("query-1"), makeSubDataSet("query-2")],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODE,
      payload: { nodeId: "table-1" },
    });

    expect(getNodeIds(nextState)).toEqual(["table-2"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-2"]);
  });

  it("removes the sub data set owned by a deleted crosstab", () => {
    const state = createState({
      nodes: [makeNode("ct-1", "crosstabv2", "query-1")],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("query-2"),
      ],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODE,
      payload: { nodeId: "ct-1" },
    });

    expect(getNodeIds(nextState)).toEqual([]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-2"]);
  });

  it("does not remove sub data sets when the deleted node is not an advanced component", () => {
    const state = createState({
      nodes: [
        makeNode("text-1", "text", "query-1"),
        makeNode("table-1", "advancedTable", "query-2"),
      ],
      subDataSets: [makeSubDataSet("query-1"), makeSubDataSet("query-2")],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODE,
      payload: { nodeId: "text-1" },
    });

    expect(getNodeIds(nextState)).toEqual(["table-1"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-1", "query-2"]);
  });

  it("does not remove sub data sets when the deleted advanced component has no selected query", () => {
    const state = createState({
      nodes: [makeNode("table-1", "advancedTable")],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("EMPTY_table-1", { isEmpty: true }),
      ],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODE,
      payload: { nodeId: "table-1" },
    });

    expect(getNodeIds(nextState)).toEqual([]);
    expect(getSubDataSetIds(nextState)).toEqual([
      "query-1",
      "EMPTY_table-1",
    ]);
  });
});

describe("hcrReducer HCR_DELETE_NODES sub dataset cleanup", () => {
  it("removes sub data sets owned by every deleted advanced component", () => {
    const state = createState({
      nodes: [
        makeNode("table-1", "advancedTable", "query-1"),
        makeNode("ct-1", "crosstabv2", "query-2"),
        makeNode("table-2", "advancedTable", "query-3"),
      ],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("query-2"),
        makeSubDataSet("query-3"),
      ],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODES,
      payload: ["table-1", "ct-1"],
    });

    expect(getNodeIds(nextState)).toEqual(["table-2"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-3"]);
  });

  it("does not remove sub data sets for deleted non-advanced nodes", () => {
    const state = createState({
      nodes: [
        makeNode("text-1", "text", "query-1"),
        makeNode("table-1", "advancedTable", "query-2"),
      ],
      subDataSets: [makeSubDataSet("query-1"), makeSubDataSet("query-2")],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODES,
      payload: ["text-1"],
    });

    expect(getNodeIds(nextState)).toEqual(["table-1"]);
    expect(getSubDataSetIds(nextState)).toEqual(["query-1", "query-2"]);
  });

  it("skips advanced components without a selected query", () => {
    const state = createState({
      nodes: [
        makeNode("table-1", "advancedTable"),
        makeNode("table-2", "advancedTable", "query-2"),
      ],
      subDataSets: [
        makeSubDataSet("query-1"),
        makeSubDataSet("query-2"),
        makeSubDataSet("EMPTY_table-1", { isEmpty: true }),
      ],
    });

    const nextState = hcrReducer(state, {
      type: actionTypes.HCR_DELETE_NODES,
      payload: ["table-1"],
    });

    expect(getNodeIds(nextState)).toEqual(["table-2"]);
    expect(getSubDataSetIds(nextState)).toEqual([
      "query-1",
      "query-2",
      "EMPTY_table-1",
    ]);
  });
});
