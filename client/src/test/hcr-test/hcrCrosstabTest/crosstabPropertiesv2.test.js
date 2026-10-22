import React from "react";
import { render, fireEvent, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import CrosstabPropertiesV2 from "../../../components/hi-canned-reports/hcrCanvas/crosstabPropertiesv2";
import { hcrDSQuery } from "../../../components/hi-canned-reports/hcr-constants";

const MockField = ({ label, value = "", onChange = () => { } }) => (
    <label>
        {typeof label === "string" ? label : "field"}
        <input value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
);

const editorPanels = {
    InputFiled: MockField,
    InputNumberFiled: MockField,
    SelectField: MockField
};

const fields = [{ name: "colA" }, { name: "rowX" }, { name: "m1" }];

const getStore = () => ({
    getState: () => ({
        cannedReports: {
            present: {
                hcrTabData: {
                    panes: [
                        {
                            key: "pane1",
                            dsPaneTypes: [
                                {
                                    dataSourcePane: hcrDSQuery,
                                    menu: [
                                        {
                                            id: "query1",
                                            executeQueryData: { data: [], field: fields }
                                        }
                                    ]
                                }
                            ],
                            selectedQueryId: "query1",
                            subDataSets: [],
                            activeKey: "report1",
                            tableStyles: []
                        }
                    ],
                    activeKey: "pane1"
                },
                hCROldConfigurations: {
                    HCR: { HCR: { designerProperties: {} } },
                    HcrPropertiesConfiguration: {}
                }
            }
        }
    }),
    subscribe: jest.fn(),
    dispatch: jest.fn()
});

const getNodeConfig = () => ({
    id: "crosstab-1",
    selectedQueryID: "query1",
    selectedFields: ["colA", "rowX", "m1"],
    selectedColumnFields: ["colA"],
    selectedRowFields: ["rowX"],
    selectedMeasures: ["m1"],
    measuresAggregateMap: { m1: "Count" },
    borders: {},
    padding: {},
    properties: [],
    width: 200,
    height: 200,
    isCTConstructed: false
});

const getSwitch = (label) =>
    screen.getByText(label).parentElement.querySelector('button[role="switch"]');

const getPositionSelect = (label) =>
    screen.getByText(label).parentElement.querySelector(".ant-select");

const renderComponent = (nodeConfig, onNodeConfigChange = jest.fn()) =>
    render(
        <Provider store={getStore()}>
            <CrosstabPropertiesV2
                EditorPanels={editorPanels}
                nodeConfig={nodeConfig}
                onNodeConfigChange={onNodeConfigChange}
            />
        </Provider>
    );

describe("CrosstabPropertiesV2 total settings", () => {
    it("enables grand and sub totals by default and defaults both positions to END", () => {
        renderComponent(getNodeConfig());

        expect(getSwitch("Grand Totals")).toHaveClass("ant-switch-checked");
        expect(getSwitch("Sub Totals")).toHaveClass("ant-switch-checked");
        expect(getPositionSelect("Grand Total Position")).toHaveTextContent("End");
        expect(getPositionSelect("Sub Total Position")).toHaveTextContent("End");
        expect(getPositionSelect("Grand Total Position")).not.toHaveClass("ant-select-disabled");
        expect(getPositionSelect("Sub Total Position")).not.toHaveClass("ant-select-disabled");
    });

    it("sends the disabled grand total toggle through onNodeConfigChange", () => {
        const onNodeConfigChange = jest.fn();
        renderComponent(getNodeConfig(), onNodeConfigChange);

        fireEvent.click(getSwitch("Grand Totals"));

        expect(onNodeConfigChange).toHaveBeenCalledWith(
            "copiedConfig",
            expect.objectContaining({
                enableGrandTotals: false,
                enableSubTotals: true
            })
        );
    });

    it("disables only the grand total position when grand totals are turned off", () => {
        const nodeConfig = getNodeConfig();
        nodeConfig.copiedConfig = { ...nodeConfig, enableGrandTotals: false };

        renderComponent(nodeConfig);

        expect(getSwitch("Grand Totals")).not.toHaveClass("ant-switch-checked");
        expect(getPositionSelect("Grand Total Position")).toHaveClass("ant-select-disabled");
        expect(getPositionSelect("Sub Total Position")).not.toHaveClass("ant-select-disabled");
    });

    it("disables the sub total position when sub totals are turned off", () => {
        const nodeConfig = getNodeConfig();
        nodeConfig.copiedConfig = { ...nodeConfig, enableSubTotals: false };

        renderComponent(nodeConfig);

        expect(getSwitch("Sub Totals")).not.toHaveClass("ant-switch-checked");
        expect(getPositionSelect("Sub Total Position")).toHaveClass("ant-select-disabled");
        expect(getPositionSelect("Grand Total Position")).not.toHaveClass("ant-select-disabled");
    });
});
