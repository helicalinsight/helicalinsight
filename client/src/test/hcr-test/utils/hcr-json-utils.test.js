import {
    HCR_REPORT_PROTECTED_KEYS,
    getHCREditableReportState,
    getReportForViewer,
    mergeJSONIntoHCRReport
} from "../../../components/hi-canned-reports/hcr-json-utils";


const buildFullReport = (overrides = {}) => ({
    key: "report-key-1",
    uuid: "uuid-1234",
    mode: "edit",
    selectedConnectionDetails: { host: "db.internal", port: 5432 },
    hcrFiltersDrawerStatus: true,
    selectedDS: "sales_ds",
    sidebarPaneActiveKey: "style",
    isPreviewing: false,
    isUpdatingCanvasPageStyles: false,
    hcrExportProperties: { format: "pdf" },
    hcrQueryRunning: false,
    canvasView: { zoom: 1 },
    canvasTabViews: [{ id: "tab-1" }],
    hcrPreviewData: [{ row: 1 }],
    hcrTableClipboardData: null,
    defaultPropertiesAdded: true,
    // non-protected / "editable" fields
    reportName: "Sales Report",
    columns: [{ id: "col-1", label: "Revenue" }],
    styles: { fontSize: 12 },
    ...overrides
});

describe("getHCREditableReportState", () => {
    it("strips every protected key from the report", () => {
        const result = getHCREditableReportState(buildFullReport());

        HCR_REPORT_PROTECTED_KEYS.forEach((protectedKey) => {
            expect(result).not.toHaveProperty(protectedKey);
        });
    });

    it("keeps all non-protected keys untouched", () => {
        const result = getHCREditableReportState(buildFullReport());

        expect(result).toEqual({
            reportName: "Sales Report",
            columns: [{ id: "col-1", label: "Revenue" }],
            styles: { fontSize: 12 }
        });
    });

    it("returns a deep clone, so mutating the result does not affect the source report", () => {
        const source = buildFullReport();
        const result = getHCREditableReportState(source);

        result.columns[0].label = "Mutated";
        result.styles.fontSize = 99;

        expect(source.columns[0].label).toBe("Revenue");
        expect(source.styles.fontSize).toBe(12);
    });

    it("returns a deep clone, so mutating the source after the call does not affect the result", () => {
        const source = buildFullReport();
        const result = getHCREditableReportState(source);

        source.columns[0].label = "Mutated After";

        expect(result.columns[0].label).toBe("Revenue");
    });

    it("returns an empty object when the report contains only protected keys", () => {
        const onlyProtected = HCR_REPORT_PROTECTED_KEYS.reduce((acc, protectedKey) => {
            acc[protectedKey] = "some-value";
            return acc;
        }, {});

        expect(getHCREditableReportState(onlyProtected)).toEqual({});
    });

    it("returns an empty object for an empty report", () => {
        expect(getHCREditableReportState({})).toEqual({});
    });

    it("returns an empty object when report is undefined", () => {
        expect(getHCREditableReportState(undefined)).toEqual({});
    });
});

describe("getReportForViewer", () => {
    it("returns a pretty-printed JSON string with 4-space indentation", () => {
        const report = buildFullReport();
        const expected = JSON.stringify(
            {
                reportName: "Sales Report",
                columns: [{ id: "col-1", label: "Revenue" }],
                styles: { fontSize: 12 }
            },
            null,
            4
        );

        expect(getReportForViewer(report)).toBe(expected);
    });

    it("produces output that excludes every protected key", () => {
        const output = getReportForViewer(buildFullReport());
        const parsed = JSON.parse(output);

        HCR_REPORT_PROTECTED_KEYS.forEach((protectedKey) => {
            expect(parsed).not.toHaveProperty(protectedKey);
        });
    });

    it("produces valid, re-parseable JSON", () => {
        const output = getReportForViewer(buildFullReport());

        expect(() => JSON.parse(output)).not.toThrow();
    });

    it("returns '{}' for an empty report", () => {
        expect(getReportForViewer({})).toBe("{}");
    });
});

describe("mergeJSONIntoHCRReport", () => {
    it("returns an empty object when called with no arguments", () => {
        expect(mergeJSONIntoHCRReport()).toEqual({});
    });

    it("overrides non-protected keys on activeReport with values from parsedReportState", () => {
        const activeReport = buildFullReport({ reportName: "Old Name" });
        const parsedReportState = { reportName: "New Name", styles: { fontSize: 20 } };

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(result.reportName).toBe("New Name");
        expect(result.styles).toEqual({ fontSize: 20 });
    });

    it("ignores protected keys present on parsedReportState, keeping activeReport's values instead", () => {
        const activeReport = buildFullReport({ mode: "view", uuid: "active-uuid" });
        const parsedReportState = buildFullReport({ mode: "edit", uuid: "parsed-uuid" });

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(result.mode).toBe("view");
        expect(result.uuid).toBe("active-uuid");
    });

    it("drops a protected key entirely if it only exists on parsedReportState", () => {
        const activeReport = { reportName: "Report A" };
        const parsedReportState = { mode: "edit", reportName: "Report A" };

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(result).not.toHaveProperty("mode");
    });

    it("keeps keys that only exist on activeReport when parsedReportState doesn't define them", () => {
        const activeReport = { reportName: "Report A", uuid: "keep-me" };
        const parsedReportState = { styles: { fontSize: 14 } };

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(result).toEqual({
            reportName: "Report A",
            uuid: "keep-me",
            styles: { fontSize: 14 }
        });
    });

    it("adds new non-protected keys that only exist on parsedReportState", () => {
        const activeReport = { reportName: "Report A" };
        const parsedReportState = { columns: [{ id: "col-1" }] };

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(result.columns).toEqual([{ id: "col-1" }]);
    });

    it("does not mutate either input argument", () => {
        const activeReport = buildFullReport({ reportName: "Active" });
        const parsedReportState = buildFullReport({ reportName: "Parsed" });
        const activeSnapshot = JSON.parse(JSON.stringify(activeReport));
        const parsedSnapshot = JSON.parse(JSON.stringify(parsedReportState));

        mergeJSONIntoHCRReport(activeReport, parsedReportState);

        expect(activeReport).toEqual(activeSnapshot);
        expect(parsedReportState).toEqual(parsedSnapshot);
    });

    it("returns a deep clone, so mutating the result does not affect either input", () => {
        const activeReport = { columns: [{ id: "col-1" }] };
        const parsedReportState = { styles: { fontSize: 12 } };

        const result = mergeJSONIntoHCRReport(activeReport, parsedReportState);
        result.columns[0].id = "mutated";
        result.styles.fontSize = 999;

        expect(activeReport.columns[0].id).toBe("col-1");
        expect(parsedReportState.styles.fontSize).toBe(12);
    });

    it("falls back to default {} for activeReport when only parsedReportState is passed", () => {
        const parsedReportState = { reportName: "Solo Parsed" };

        expect(mergeJSONIntoHCRReport(undefined, parsedReportState)).toEqual({
            reportName: "Solo Parsed"
        });
    });
});