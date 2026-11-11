import {
    getPreviewFormData,
    handleHcrFilterChange,
    updateHCRRelativeDateFilters,
} from "../../../components/hi-canned-reports/hcrHelperMethods";
import { anchorConstant } from "../../../utils/filter-utils";

const ANCHOR_DATE = "2024-05-01 10:00:00";

function makeCanvasProperties() {
    return {
        margin: { left: 10, right: 10, top: 10, bottom: 10 },
        layout: { orientation: "portrait", size: { width: 595, height: 842 } },
        pageProperties: {
            columnCount: 1,
            columnSpacing: 0,
            columnWidth: 595,
            printOrder: "Vertical",
            whenNoData: "NoPages",
            summaryWithHeaderAndFooter: false,
            floatColumnFooter: false,
            titleNewPage: false,
            summaryInNewPage: false,
            ignorePagination: false,
        },
        calculations: { options: [] },
        groupProperties: { options: [] },
        pageStyles: { options: [] },
    };
}

function makeDispatch() {
    const state = {
        cannedReports: {
            present: {
                hcrTabData: {
                    panes: [{ key: "pane-1" }],
                    activeKey: "pane-1",
                },
            },
        },
    };
    return jest.fn((thunk) => {
        if (typeof thunk === "function") {
            thunk(undefined, () => state);
        }
    });
}

function buildPreviewFormData(filters) {
    return getPreviewFormData({
        flowchartInstance: {},
        query: { id: "query-1", executeQueryData: { field: [] } },
        canvasProperties: makeCanvasProperties(),
        filters,
        hcrDiagramNodesData: [
            { category: "staticText", repeat: "staticText", y: 0, height: 0 },
        ],
        reportName: "relative-date-report",
        isHCR: true,
        isExport: false,
        saveDetails: { location: "/reports" },
        dispatch: makeDispatch(),
    });
}

function applyFilterChange({ value, filter, prevFilters }) {
    let updatedFilters;
    handleHcrFilterChange({
        value,
        filter,
        setFilters: (updater) => {
            updatedFilters = updater(prevFilters);
        },
    });
    return updatedFilters;
}

describe("HCR relative date filters", () => {
    describe("updateHCRRelativeDateFilters", () => {
        test("replaces date filter values with values derived from its anchor", () => {
            const filter = {
                uid: "date-1",
                condition: "EQUALS",
                dataType: "date",
                values: ["2024-01-01"],
                anchor: {
                    anchorDate: ANCHOR_DATE,
                    isAnchor: false,
                    relativePart: "day",
                    part: "days",
                    value: 0,
                    direction: "",
                },
            };

            const result = updateHCRRelativeDateFilters([filter]);

            expect(result[0].values).toEqual(["today"]);
            expect(result[0].anchor).toBe(filter.anchor);
            expect(filter.values).toEqual(["2024-01-01"]);
        });

        test("supports date-part filters without a date dataType", () => {
            const filter = {
                uid: "date-part-1",
                condition: "EQUALS",
                datePart: "individual",
                values: ["2024-01-01"],
                anchor: {
                    anchorDate: ANCHOR_DATE,
                    relativePart: "month",
                    part: "months",
                    value: 1,
                    direction: "last",
                },
            };

            const result = updateHCRRelativeDateFilters([filter]);

            expect(result[0].values).toEqual(["month-1"]);
        });

        test("returns filters without anchors or non-date filters unchanged", () => {
            const dateFilter = {
                uid: "date-1",
                condition: "EQUALS",
                dataType: "date",
                values: ["2024-01-01"],
            };
            const textFilter = {
                uid: "text-1",
                condition: "EQUALS",
                dataType: "text",
                values: ["EMEA"],
                anchor: { relativePart: "month", value: 1, direction: "last" },
            };

            const result = updateHCRRelativeDateFilters([dateFilter, textFilter]);

            expect(result[0]).toBe(dateFilter);
            expect(result[1]).toBe(textFilter);
        });

        test("returns an empty array for empty filters", () => {
            expect(updateHCRRelativeDateFilters([])).toEqual([]);
        });
    });

    describe("handleHcrFilterChange", () => {
        test("preserves the anchor when a non-range date filter value changes", () => {
            const anchor = {
                anchorDate: ANCHOR_DATE,
                isAnchor: true,
                relativePart: "month",
            };
            const filter = {
                uid: "date-1",
                condition: "EQUALS",
                dataType: "date",
                anchor,
            };
            const prevFilters = [
                { uid: "date-1", values: ["2024-01-01"], anchor: { old: true } },
            ];

            const result = applyFilterChange({
                value: "2024-02-01",
                filter,
                prevFilters,
            });

            expect(result[0].values).toEqual(["2024-02-01"]);
            expect(result[0].anchor).toBe(anchor);
        });

        test("preserves the anchor for date-part filters", () => {
            const anchor = {
                anchorDate: ANCHOR_DATE,
                relativePart: "year",
            };
            const filter = {
                uid: "date-part-1",
                condition: "IS_ONE_OF",
                datePart: "individual",
                dateValuesType: "select",
                anchor,
            };
            const prevFilters = [{ uid: "date-part-1", values: [] }];

            const result = applyFilterChange({
                value: "2024",
                filter,
                prevFilters,
            });

            expect(result[0].values).toEqual(["2024"]);
            expect(result[0].anchor).toBe(anchor);
            expect(result[0].dateValuesType).toBe("select");
        });

        test("does not add an anchor to non-date filters", () => {
            const filter = {
                uid: "text-1",
                condition: "EQUALS",
                dataType: "text",
            };

            const result = applyFilterChange({
                value: "EMEA",
                filter,
                prevFilters: [{ uid: "text-1", values: [] }],
            });

            expect(result[0].values).toEqual(["EMEA"]);
            expect(result[0]).not.toHaveProperty("anchor");
        });

        test("preserves the anchor and updates related filters for an IN_RANGE date filter", () => {
            const anchor = {
                anchorDate: ANCHOR_DATE,
                isAnchor: true,
                relativePart: "quarter",
            };
            const filter = {
                uid: "range-1",
                condition: "IN_RANGE",
                dataType: "date",
                dateValuesType: "datePicker",
                range: ["start", "end"],
                anchor,
            };
            const prevFilters = [
                { uid: "range-1", values: ["2024-01-01", "2024-03-31"] },
                { uid: "start", label: "start", values: ["2023-01-01"] },
                { uid: "end", label: "end", values: ["2023-03-31"] },
            ];

            const result = applyFilterChange({
                value: ["2024-04-01", "2024-06-30"],
                filter,
                prevFilters,
            });

            expect(result[0].values).toEqual(["2024-04-01", "2024-06-30"]);
            expect(result[0].dateValuesType).toBe("datePicker");
            expect(result[0].anchor).toBe(anchor);
            expect(result[1].values).toEqual(["2024-04-01"]);
            expect(result[2].values).toEqual(["2024-06-30"]);
        });
    });

    describe("getPreviewFormData", () => {
        test.each([
            ["java.lang.Integer", "2024-01-15"],
            ["java.util.Collection", ["2024-01-15"]],
            ["java.lang.String", "2024-01-15"],
        ])(
            "stores date anchors separately for %s filter parameters",
            (className, expectedValue) => {
                const label = "Start Date";
                const filter = {
                    label,
                    className,
                    condition: "EQUALS",
                    dataType: "date",
                    values: ["2024-01-15"],
                    displayFormat: "YYYY-MM-DD",
                    valueFormat: "YYYY-MM-DD",
                    anchor: {
                        anchorDate: ANCHOR_DATE,
                        isAnchor: true,
                        relativePart: "month",
                    },
                };

                const formData = buildPreviewFormData([filter]);
                const anchorLabel = `${anchorConstant}${label}`;

                expect(formData.parameterAnchors).toEqual({
                    [anchorLabel]: ANCHOR_DATE,
                });
                expect(formData.designerProperties.parameters[0]).toEqual(
                    expect.objectContaining({ name: label, value: expectedValue })
                );
                expect(formData.designerProperties.parameters[0]).not.toHaveProperty(anchorLabel);
                expect(formData.connectionDetails).not.toHaveProperty(anchorLabel);
            }
        );

        test.each([
            ["start", "2024-01-01"],
            ["end", "2024-01-31"],
        ])(
            "stores anchor parameters for the %s of an IN_RANGE date filter",
            (label, expectedValue) => {
                const filter = {
                    label,
                    className: "java.lang.String",
                    condition: "IN_RANGE",
                    dataType: "date",
                    range: ["start", "end"],
                    values: ["2024-01-01", "2024-01-31"],
                    displayFormat: "YYYY-MM-DD",
                    valueFormat: "YYYY-MM-DD",
                    anchor: {
                        anchorDate: ANCHOR_DATE,
                        isAnchor: true,
                        relativePart: "month",
                    },
                };

                const formData = buildPreviewFormData([filter]);
                const anchorLabel = `${anchorConstant}${label}`;

                expect(formData.parameterAnchors[anchorLabel]).toBe(ANCHOR_DATE);
                expect(formData.designerProperties.parameters[0].value).toBe(expectedValue);
                expect(formData.designerProperties.parameters[0]).not.toHaveProperty(anchorLabel);
                expect(formData.connectionDetails).not.toHaveProperty(anchorLabel);
            }
        );

        test("uses the raw relative date value instead of formatting it again", () => {
            const filter = {
                label: "Start Date",
                className: "java.lang.String",
                condition: "EQUALS",
                dataType: "date",
                values: ["today-1"],
                displayFormat: "YYYY-MM-DD",
                valueFormat: "YYYY-MM-DD",
                anchor: {
                    anchorDate: ANCHOR_DATE,
                    isAnchor: true,
                    relativePart: "day",
                    part: "days",
                    value: 1,
                    direction: "last",
                },
            };

            const formData = buildPreviewFormData([filter]);

            expect(formData.designerProperties.parameters[0].value).toBe("today-1");
            expect(formData.connectionDetails["Start Date"]).toBe("today-1");
        });

        test("uses an empty string when an anchor date is missing", () => {
            const label = "Start Date";
            const formData = buildPreviewFormData([
                {
                    label,
                    className: "java.lang.String",
                    condition: "EQUALS",
                    dataType: "date",
                    values: ["2024-01-15"],
                    displayFormat: "YYYY-MM-DD",
                    valueFormat: "YYYY-MM-DD",
                    anchor: { isAnchor: true, relativePart: "month" },
                },
            ]);

            expect(formData.parameterAnchors[`${anchorConstant}${label}`]).toBe("");
        });

        test("omits parameterAnchors when no filter is anchored", () => {
            const formData = buildPreviewFormData([
                {
                    label: "Start Date",
                    className: "java.lang.String",
                    condition: "EQUALS",
                    dataType: "date",
                    values: ["2024-01-15"],
                    displayFormat: "YYYY-MM-DD",
                    valueFormat: "YYYY-MM-DD",
                    anchor: { isAnchor: false, relativePart: "month" },
                },
            ]);

            expect(formData).not.toHaveProperty("parameterAnchors");
        });
    });
});
