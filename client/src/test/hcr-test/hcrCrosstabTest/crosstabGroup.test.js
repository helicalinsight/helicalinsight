import React from "react";
import { render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import CrosstabGroup from "../../../components/hi-canned-reports/hcrCanvas/advanceComponents/components/crosstabGroup";

jest.mock("../../../components/hi-canned-reports/hcrCanvas/fieldSelector", () => () => (
    <div data-testid="field-selector" />
));

const MockInput = ({ label, value = "" }) => (
    <label>
        {typeof label === "string" ? label : "input"}
        <input value={value} readOnly />
    </label>
);

const MockSelectField = ({ label, value }) => (
    <div>
        {label}
        <span data-testid="total-position">{value}</span>
    </div>
);

const editorPanels = {
    InputFiled: MockInput,
    SelectField: MockSelectField
};

const getStore = () => ({
    getState: () => ({
        cannedReports: {
            present: {
                hcrTabData: {
                    panes: [
                        {
                            key: "pane1",
                            dsPaneTypes: [],
                            canvasProperties: {}
                        }
                    ],
                    activeKey: "pane1"
                },
                hCROldConfigurations: {
                    HCR: {
                        HCR: {
                            designerProperties: { variables: {} }
                        }
                    }
                }
            }
        }
    }),
    subscribe: jest.fn(),
    dispatch: jest.fn()
});

const renderGroup = (totalPosition) => {
    const group = {
        id: "group-1",
        label: "colA",
        expression: "$F{colA}",
        className: "java.lang.String",
        type: "columnGroup",
        totalPosition
    };
    const data = {
        id: "crosstab-1",
        config: { columnGroups: [group], rowGroups: [] }
    };

    return render(
        <Provider store={getStore()}>
            <CrosstabGroup
                data={data}
                EditorPanels={editorPanels}
                dispatch={jest.fn()}
                selectedGroup={["group-1"]}
                selectedSubDS={{ calculations: [], fields: [], groups: [], parameters: [] }}
            />
        </Provider>
    );
};

describe("CrosstabGroup total position", () => {
    it("normalizes a legacy End value to END", () => {
        renderGroup("End");

        expect(screen.getByTestId("total-position")).toHaveTextContent("END");
    });

    it("normalizes a lowercase start value to START", () => {
        renderGroup("start");

        expect(screen.getByTestId("total-position")).toHaveTextContent("START");
    });

    it("preserves NONE", () => {
        renderGroup("NONE");

        expect(screen.getByTestId("total-position")).toHaveTextContent("NONE");
    });

    it("defaults an unknown value to END", () => {
        renderGroup("middle");

        expect(screen.getByTestId("total-position")).toHaveTextContent("END");
    });
});
