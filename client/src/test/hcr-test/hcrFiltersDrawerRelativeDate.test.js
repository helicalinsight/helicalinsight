import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { HCRFiltersDrawer } from "../../components/hi-canned-reports/hcrFilters/hcrFiltersDrawer";

jest.mock("react-redux", () => {
    const pane = {
        key: "pane-1",
        dsPaneTypes: [
            { dataSourcePane: "Parameter", menu: [] },
            { dataSourcePane: "Query", menu: [] },
        ],
        canvasProperties: { previewParameters: {} },
        hcrDiagramNodesData: [],
        title: "Report",
        isPreviewing: false,
        selectedQueryId: "query-1",
        hcrExportProperties: [],
    };
    return {
        useDispatch: () => jest.fn(),
        useSelector: (selector) =>
            selector({
                cannedReports: {
                    present: {
                        hcrTabData: {
                            panes: [pane],
                            activeKey: "pane-1",
                        },
                    },
                },
            }),
    };
});

jest.mock("antd", () => {
    const React = require("react");
    return {
        Drawer: ({ children, extra }) =>
            React.createElement("div", null, extra, children),
        Button: ({ children, onClick, disabled }) =>
            React.createElement("button", { onClick, disabled }, children),
    };
});

jest.mock("../../base/requests", () => ({
    __esModule: true,
    default: {
        cannedReport: () => ({ previewRequest: jest.fn() }),
    },
}));

jest.mock("../../redux/actions", () => ({
    hcrActions: {
        handleTogglePreview: jest.fn(),
        handlePreviewTag: jest.fn(),
        handlePageDetails: jest.fn(),
        updateHcrFiltersDrawerStatus: jest.fn(),
    },
}));

jest.mock("../../utils/filter-utils", () => ({
    checkRelativeDateFilter: jest.fn((filters) => filters),
}));

jest.mock("../../utils/utilities", () => ({
    checkForAnchorRelativeParameters: jest.fn((parameters) => parameters),
    getFieldDisplayName: jest.fn((filter) => filter.label),
}));

jest.mock("../../components/hi-notifications/notify", () => () => ({
    error: jest.fn(),
}));

jest.mock(
    "../../components/hi-canned-reports/hcrCanvas/hcrCanvasPaneHelperMethods",
    () => ({ getErrorMessage: jest.fn() })
);

jest.mock("../../components/hi-canned-reports/hcrFilters/hcrFilters", () => () => null);

jest.mock("../../components/hi-canned-reports/hcrHelperMethods", () => ({
    getHcrParameterFilters: jest.fn(),
    getPreviewFormData: jest.fn(),
    handleUrlParamsFilters: jest.fn(),
    updateDateRangeFilterValues: jest.fn(),
    updateHCRRelativeDateFilters: jest.fn(),
    validateNodes: jest.fn(),
}));

const hcrHelperMethods = require("../../components/hi-canned-reports/hcrHelperMethods");

describe("HCR filters drawer relative date handling", () => {
    const filters = [
        { uid: "date-1", label: "Date", values: ["2024-01-01"], condition: "EQUALS" },
    ];
    const relativeFilters = [
        { uid: "date-1", label: "Date", values: ["today"], condition: "EQUALS" },
    ];

    beforeEach(() => {
        hcrHelperMethods.getHcrParameterFilters.mockReturnValue(filters);
        hcrHelperMethods.updateDateRangeFilterValues.mockImplementation(
            (input) => input
        );
        hcrHelperMethods.updateHCRRelativeDateFilters.mockReturnValue(relativeFilters);
        hcrHelperMethods.validateNodes.mockReturnValue({
            isValid: false,
            bandLimits: {},
        });
    });

    test("resolves relative date filters for review and applied filters", async () => {
        const setParametersReview = jest.fn();
        const setAppliedFilters = jest.fn();

        render(
            <HCRFiltersDrawer
                hcrFiltersDrawerStatus={false}
                flowchartInstance={{}}
                setIsPreviewLoading={jest.fn()}
                reportMode=""
                setAppliedFilters={setAppliedFilters}
                urlParameters={{}}
                setParametersReview={setParametersReview}
                queryTempuuidsMap={{ current: {} }}
                streamEnabled={false}
                setPreviewError={jest.fn()}
                reportKey="report-1"
            />
        );

        await waitFor(() =>
            expect(setParametersReview).toHaveBeenCalledWith({
                parameters: { Date: ["today"] },
            })
        );
        expect(hcrHelperMethods.updateHCRRelativeDateFilters).toHaveBeenCalledWith(
            filters
        );

        fireEvent.click(screen.getByText("Apply"));

        await waitFor(() =>
            expect(setAppliedFilters).toHaveBeenCalledWith(relativeFilters)
        );
        expect(hcrHelperMethods.updateDateRangeFilterValues).toHaveBeenCalledWith(
            filters
        );
        expect(hcrHelperMethods.updateHCRRelativeDateFilters).toHaveBeenCalledWith(
            filters
        );
    });
});
