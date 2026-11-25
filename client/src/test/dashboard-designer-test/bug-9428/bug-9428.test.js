import React from "react";
import { fireEvent, render, screen } from "@testing-library/react";
import DashboardJsonEditor from "../../../components/hi-dashboard-designer/components/json-editor/dashboard-json-editor";

jest.mock("antd", () => ({
    Drawer: ({ children }) => children,
}));

jest.mock("react-redux", () => ({
    useSelector: (selector) => selector({ designer: { present: {} } }),
    useDispatch: () => jest.fn(),
}));

jest.mock("../../../components/common/json-editor", () => {
    const React = require("react");
    return {
        copyToClipboard: jest.fn(),
        unwrapStatePayload: (data) =>
            data?.state == null
                ? data
                : typeof data.state === "string"
                  ? JSON.parse(data.state)
                  : data.state,
        JsonEditorPanel: ({ value, onChange, onSave }) =>
            React.createElement(
                React.Fragment,
                null,
                React.createElement("textarea", {
                    "data-testid": "dashboard-json",
                    value,
                    onChange: (event) => onChange(event.target.value),
                }),
                React.createElement("button", { onClick: onSave }, "Apply"),
            ),
    };
});

jest.mock("../../../components/hi-notifications/notify", () =>
    jest.fn(() => ({ success: jest.fn(), error: jest.fn() })),
);

describe("DashboardJsonEditor", () => {
    const originalDocumentTitle = document.title;

    afterEach(() => {
        document.title = originalDocumentTitle;
    });

    it("updates the document title and file info when applying a dashboard title", () => {
        const setFileInfo = jest.fn();
        document.title = "Dashboard | HelicalInsight";

        render(
            React.createElement(DashboardJsonEditor, {
                visible: true,
                setFileInfo,
            }),
        );
        fireEvent.change(screen.getByTestId("dashboard-json"), {
            target: { value: JSON.stringify({ dashboardTitle: "Copied Dashboard" }) },
        });
        expect(screen.getByTestId("dashboard-json")).toHaveValue(
            JSON.stringify({ dashboardTitle: "Copied Dashboard" }),
        );
        fireEvent.click(screen.getByRole("button", { name: "Apply" }));

        expect(setFileInfo).toHaveBeenCalledWith({ fileTitle: "Copied Dashboard" });
        expect(document.title).toBe("Copied Dashboard | HelicalInsight");
    });
});
