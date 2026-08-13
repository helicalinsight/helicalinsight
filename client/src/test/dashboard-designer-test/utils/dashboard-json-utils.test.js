import {
    DASHBOARD_STATE_PROTECTED_KEYS,
    getDashboardEditableReportState,
    getDashboardForViewer,
    mergeJSONIntoDashboard

} from "../../../components/hi-dashboard-designer/components/json-editor/dashboard-json-utils";

const buildFullDashboard = (overrides = {}) => ({
    filterCounter: 3,
    previewMode: true,
    designerMode: false,
    dashboardVariables: [{ name: "region", value: "APAC" }],
    dashboardUUID: "dash-uuid-1",
    variables: { user: "manish" },
    dashboardConfig: { theme: "dark" },
    script: "console.log('hi')",
    printOptions: { orientation: "landscape" },
    toggleIframes: true,
    dashboardDrawerStatus: "open",
    gridItemDrawerStatus: "closed",
    currentGroupId: "group-1",
    groupId: "group-2",
    gridItemId: "item-9",
    drawerPositions: { x: 10, y: 20 },
    currentDrawerPosition: "left",
    gridIndex: 4,
    isLoading: false,
    reportId: "report-77",
    applyDashboardFilters: true,
    isSaving: false,
    hasUnsavedData: true,
    savedReportName: "Q3 Overview",
    replaceReportId: "report-88",
    filterItemsData: [{ id: "f1" }],
    maximizedGridItem: "item-3",
    css: ".grid { color: red; }",
    itemAddedStatus: true,
    maximizingStatus: false,
    dashboardName: "Sales Dashboard",
    gridItems: [{ id: "item-1", type: "chart" }],
    layout: { columns: 12, rowHeight: 30 },
    ...overrides
});

describe("DASHBOARD_STATE_PROTECTED_KEYS", () => {
    it("is exported and contains all expected protected keys with no duplicates", () => {
        expect(DASHBOARD_STATE_PROTECTED_KEYS).toHaveLength(30);
        expect(new Set(DASHBOARD_STATE_PROTECTED_KEYS).size).toBe(
            DASHBOARD_STATE_PROTECTED_KEYS.length
        );
    });
});

describe("getDashboardEditableReportState", () => {
    it("strips every protected key from the dashboard", () => {
        const result = getDashboardEditableReportState(buildFullDashboard());

        DASHBOARD_STATE_PROTECTED_KEYS.forEach((protectedKey) => {
            expect(result).not.toHaveProperty(protectedKey);
        });
    });

    it("keeps all non-protected keys untouched", () => {
        const result = getDashboardEditableReportState(buildFullDashboard());

        expect(result).toEqual({
            dashboardName: "Sales Dashboard",
            gridItems: [{ id: "item-1", type: "chart" }],
            layout: { columns: 12, rowHeight: 30 }
        });
    });

    it("returns a deep clone, so mutating the result does not affect the source dashboard", () => {
        const source = buildFullDashboard();
        const result = getDashboardEditableReportState(source);

        result.gridItems[0].type = "table";
        result.layout.columns = 24;

        expect(source.gridItems[0].type).toBe("chart");
        expect(source.layout.columns).toBe(12);
    });

    it("returns a deep clone, so mutating the source after the call does not affect the result", () => {
        const source = buildFullDashboard();
        const result = getDashboardEditableReportState(source);

        source.gridItems[0].type = "table";

        expect(result.gridItems[0].type).toBe("chart");
    });

    it("returns an empty object when the dashboard contains only protected keys", () => {
        const onlyProtected = DASHBOARD_STATE_PROTECTED_KEYS.reduce((acc, protectedKey) => {
            acc[protectedKey] = "some-value";
            return acc;
        }, {});

        expect(getDashboardEditableReportState(onlyProtected)).toEqual({});
    });

    it("returns an empty object for an empty dashboard", () => {
        expect(getDashboardEditableReportState({})).toEqual({});
    });

    it("returns an empty object when dashboard is undefined", () => {
        expect(getDashboardEditableReportState(undefined)).toEqual({});
    });
});

describe("getDashboardForViewer", () => {
    it("returns a pretty-printed JSON string with 4-space indentation", () => {
        const dashboard = buildFullDashboard();
        const expected = JSON.stringify(
            {
                dashboardName: "Sales Dashboard",
                gridItems: [{ id: "item-1", type: "chart" }],
                layout: { columns: 12, rowHeight: 30 }
            },
            null,
            4
        );

        expect(getDashboardForViewer(dashboard)).toBe(expected);
    });

    it("produces output that excludes every protected key", () => {
        const output = getDashboardForViewer(buildFullDashboard());
        const parsed = JSON.parse(output);

        DASHBOARD_STATE_PROTECTED_KEYS.forEach((protectedKey) => {
            expect(parsed).not.toHaveProperty(protectedKey);
        });
    });

    it("produces valid, re-parseable JSON", () => {
        const output = getDashboardForViewer(buildFullDashboard());

        expect(() => JSON.parse(output)).not.toThrow();
    });

    it("returns '{}' for an empty dashboard", () => {
        expect(getDashboardForViewer({})).toBe("{}");
    });
});

describe("mergeJSONIntoDashboard", () => {
    it("returns an empty object when called with no arguments", () => {
        expect(mergeJSONIntoDashboard()).toEqual({});
    });

    it("overrides non-protected keys on activeReport with values from parsedDashboardState", () => {
        const activeReport = buildFullDashboard({ dashboardName: "Old Name" });
        const parsedDashboardState = { dashboardName: "New Name", layout: { columns: 6, rowHeight: 40 } };

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(result.dashboardName).toBe("New Name");
        expect(result.layout).toEqual({ columns: 6, rowHeight: 40 });
    });

    it("ignores protected keys present on parsedDashboardState, keeping activeReport's values instead", () => {
        const activeReport = buildFullDashboard({ isSaving: false, reportId: "active-report-id" });
        const parsedDashboardState = buildFullDashboard({ isSaving: true, reportId: "parsed-report-id" });

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(result.isSaving).toBe(false);
        expect(result.reportId).toBe("active-report-id");
    });

    it("drops a protected key entirely if it only exists on parsedDashboardState", () => {
        const activeReport = { dashboardName: "Dashboard A" };
        const parsedDashboardState = { css: ".x { color: blue; }", dashboardName: "Dashboard A" };

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(result).not.toHaveProperty("css");
    });

    it("keeps keys that only exist on activeReport when parsedDashboardState doesn't define them", () => {
        const activeReport = { dashboardName: "Dashboard A", reportId: "keep-me" };
        const parsedDashboardState = { layout: { columns: 4, rowHeight: 20 } };

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(result).toEqual({
            dashboardName: "Dashboard A",
            reportId: "keep-me",
            layout: { columns: 4, rowHeight: 20 }
        });
    });

    it("adds new non-protected keys that only exist on parsedDashboardState", () => {
        const activeReport = { dashboardName: "Dashboard A" };
        const parsedDashboardState = { gridItems: [{ id: "item-5" }] };

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(result.gridItems).toEqual([{ id: "item-5" }]);
    });

    it("does not mutate either input argument", () => {
        const activeReport = buildFullDashboard({ dashboardName: "Active" });
        const parsedDashboardState = buildFullDashboard({ dashboardName: "Parsed" });
        const activeSnapshot = JSON.parse(JSON.stringify(activeReport));
        const parsedSnapshot = JSON.parse(JSON.stringify(parsedDashboardState));

        mergeJSONIntoDashboard(activeReport, parsedDashboardState);

        expect(activeReport).toEqual(activeSnapshot);
        expect(parsedDashboardState).toEqual(parsedSnapshot);
    });

    it("returns a deep clone, so mutating the result does not affect either input", () => {
        const activeReport = { gridItems: [{ id: "item-1" }] };
        const parsedDashboardState = { layout: { columns: 12, rowHeight: 30 } };

        const result = mergeJSONIntoDashboard(activeReport, parsedDashboardState);
        result.gridItems[0].id = "mutated";
        result.layout.columns = 999;

        expect(activeReport.gridItems[0].id).toBe("item-1");
        expect(parsedDashboardState.layout.columns).toBe(12);
    });

    it("falls back to default {} for activeReport when only parsedDashboardState is passed", () => {
        const parsedDashboardState = { dashboardName: "Solo Parsed" };

        expect(mergeJSONIntoDashboard(undefined, parsedDashboardState)).toEqual({
            dashboardName: "Solo Parsed"
        });
    });
});