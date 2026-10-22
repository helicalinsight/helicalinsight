import React from "react";
import { render, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import CrosstabLayout from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/crosstab/components/crosstabLayout";
import { hcrActions } from "../../../redux/actions";

jest.mock(
    "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/crosstab/components/crosstabEditableCell",
    () => ({ cell, gridColumn, gridRow }) => (
        <div
            data-testid={`cell-${cell.id}`}
            data-grid-column={gridColumn}
            data-grid-row={gridRow}
        />
    )
);

const getDispatch = jest.fn();

const getStore = () => ({
    getState: () => ({
        cannedReports: {
            present: {
                hcrTabData: {
                    panes: [{ key: "pane1", tableStyles: [] }],
                    activeKey: "pane1"
                }
            }
        }
    }),
    subscribe: jest.fn(),
    dispatch: getDispatch
});

const getConfig = () => ({
    columnGroups: [
        {
            id: "cgA",
            name: "colA",
            totalPosition: "START",
            cells: [{ id: "chA" }, { id: "ctA" }]
        },
        {
            id: "cgB",
            name: "colB",
            totalPosition: "NONE",
            cells: [{ id: "chB" }, { id: "ctB" }]
        }
    ],
    rowGroups: [
        {
            id: "rgX",
            name: "rowX",
            totalPosition: "END",
            cells: [{ id: "rhX" }, { id: "rtX" }]
        }
    ],
    measureCells: [],
    nodes: {},
    crosstabHeader: { width: 50, height: 25 },
    colWidths: [80, 120, 160, 200],
    rowHeights: [100, 120, 140, 160]
});

const getData = () => ({ id: "crosstab-1", config: getConfig() });

const renderLayout = (mode = "view") =>
    render(
        <Provider store={getStore()}>
            <CrosstabLayout data={getData()} mode={mode} />
        </Provider>
    );

const getGrid = (container) =>
    container.querySelector(".hcr-crosstab-view-mode-container > div > div:first-child");

const getResizeHandles = (container, cursor) =>
    Array.from(container.querySelectorAll("div")).filter(
        (element) => element.style.cursor === cursor
    );

describe("CrosstabLayout track compaction", () => {
    beforeEach(() => {
        getDispatch.mockClear();
    });

    it("renders only the visible column tracks while keeping all row tracks", () => {
        const { container } = renderLayout();
        const grid = getGrid(container);

        expect(grid.style.gridTemplateColumns).toBe("80px 200px 120px");
        expect(grid.style.gridTemplateRows).toBe("100px 120px 140px 160px");
        expect(grid.style.width).toBe("400px");
        expect(grid.style.height).toBe("520px");
    });

    it("omits hidden total cells and keeps START totals in the visible layout", () => {
        const { container } = renderLayout();

        expect(container.querySelector('[data-testid="cell-ctB"]')).toBeNull();
        expect(container.querySelector('[data-testid="cell-chA"]')).not.toBeNull();
        expect(container.querySelector('[data-testid="cell-ctA"]')).not.toBeNull();
        expect(container.querySelector('[data-testid="cell-chB"]')).not.toBeNull();

        expect(
            container.querySelector('[data-testid="cell-ctA"]').dataset.gridColumn
        ).toBe("2 / span 1");
        expect(
            container.querySelector('[data-testid="cell-ctA"]').dataset.gridRow
        ).toBe("1 / span 2");
        expect(
            container.querySelector('[data-testid="cell-chB"]').dataset.gridColumn
        ).toBe("3 / span 1");
        expect(
            container.querySelector('[data-testid="cell-chB"]').dataset.gridRow
        ).toBe("2 / span 1");
    });

    it("renders one resize handle per visible track in edit mode", () => {
        const { container } = renderLayout("edit");

        expect(getResizeHandles(container, "col-resize")).toHaveLength(3);
        expect(getResizeHandles(container, "row-resize")).toHaveLength(4);
    });

    it("maps a compacted resize handle back to the original track index", () => {
        const { container } = renderLayout("edit");
        const grid = getGrid(container);
        const colHandles = getResizeHandles(container, "col-resize");

        fireEvent.mouseDown(colHandles[2], { clientX: 100, clientY: 0 });
        fireEvent.mouseMove(window, { clientX: 120, clientY: 0 });
        fireEvent.mouseUp(window);

        const updateCall = getDispatch.mock.calls.find(
            ([action]) => action?.type === hcrActions.hcrUpdateCrosstabComponent().type
        );

        expect(updateCall).toBeDefined();
        expect(updateCall[0].payload).toMatchObject({
            id: "crosstab-1",
            actionType: "updateCrosstabLayout",
            type: "col",
            position: 1,
            resizeFactor: 20,
            colWidths: [80, 140, 160, 200],
            rowHeights: [100, 120, 140, 160],
            cellsToUpdate: ["chA", "chB"]
        });
        expect(grid.style.gridTemplateColumns).toBe("80px 200px 140px");
    });
});
