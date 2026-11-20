import "regenerator-runtime/runtime";
import actionTypes from "../../redux/actions/actionTypes";
import { hcrReducer } from "../../redux/reducers/hCR.reducer";

const createState = (nodes) => ({
  hcrTabData: {
    activeKey: "tab-1",
    panes: [
      {
        key: "tab-1",
        hcrDiagramNodesData: nodes,
        canvasProperties: {
          groupProperties: {
            options: [],
          },
        },
      },
    ],
  },
});

const createRepeatAction = ({ nodeData, selectedNodes, menuKeyPath }) => ({
  type: actionTypes.HCR_EDIT_NODE,
  payload: {
    nodeData,
    selectedNodes,
    menuKeyPath: menuKeyPath || ["rt", "groupBy"],
    type: "contextMenuRepeat",
  },
});

const getDiagramNodes = (state) => state.hcrTabData.panes[0].hcrDiagramNodesData;

const getNode = (state, id) =>
  getDiagramNodes(state).find((node) => node.id === id);

describe("hcrReducer contextMenuRepeat with selected nodes", () => {
  it("should set the repeat band on every selected node", () => {
    const state = createState([
      { id: "node-1", repeat: "pg" },
      { id: "node-2" },
      { id: "node-3", repeat: "pg" },
    ]);

    const nextState = hcrReducer(
      state,
      createRepeatAction({
        nodeData: { id: "node-1", repeat: "pg" },
        selectedNodes: [{ id: "node-1" }, { id: "node-2" }],
      }),
    );

    expect(getNode(nextState, "node-1").repeat).toBe("rt");
    expect(getNode(nextState, "node-2").repeat).toBe("rt");
  });

  it("should leave unselected nodes unchanged", () => {
    const state = createState([
      { id: "node-1" },
      { id: "node-2" },
      { id: "node-3", repeat: "pg" },
    ]);

    const nextState = hcrReducer(
      state,
      createRepeatAction({
        nodeData: { id: "node-1" },
        selectedNodes: [{ id: "node-1" }, { id: "node-2" }],
      }),
    );

    expect(getNode(nextState, "node-3").repeat).toBe("pg");
    expect(getNode(nextState, "node-3")).not.toHaveProperty("rt");
  });

  it("should not add the header band to nodes in a multi-node selection", () => {
    const state = createState([{ id: "node-1" }, { id: "node-2" }]);

    const nextState = hcrReducer(
      state,
      createRepeatAction({
        nodeData: { id: "node-1" },
        selectedNodes: [{ id: "node-1" }, { id: "node-2" }],
      }),
    );

    expect(getNode(nextState, "node-1").repeat).toBe("rt");
    expect(getNode(nextState, "node-2").repeat).toBe("rt");
    expect(getNode(nextState, "node-1")).not.toHaveProperty("rt");
    expect(getNode(nextState, "node-2")).not.toHaveProperty("rt");
  });

  it.each([
    ["an empty selection", []],
    ["an undefined selection", undefined],
  ])(
    "should keep single-node repeat and header behavior with %s",
    (_label, selectedNodes) => {
      const state = createState([{ id: "node-1" }]);

      const nextState = hcrReducer(
        state,
        createRepeatAction({
          nodeData: { id: "node-1" },
          selectedNodes,
        }),
      );

      expect(getNode(nextState, "node-1").repeat).toBe("rt");
      expect(getNode(nextState, "node-1").rt).toBe("header");
    },
  );
});
