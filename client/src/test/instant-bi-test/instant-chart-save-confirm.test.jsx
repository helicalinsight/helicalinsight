import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import {
  InstantChartSaveConfirmBody,
  confirmInstantInlineChartSaves,
} from "../../components/hi-instant-bi/components/instant-chart-save-confirm";

describe("InstantChartSaveConfirmBody", () => {
  test("hides report names until Show names is clicked", () => {
    const namesRef = { current: [] };
    render(
      <InstantChartSaveConfirmBody
        location="Sales"
        drafts={[
          { id: "item-1", name: "Cost_by_Type" },
          { id: "item-2", name: "Bookings" },
        ]}
        namesRef={namesRef}
      />
    );

    expect(screen.getByText("2 reports are being saved in the provided location.")).toBeInTheDocument();
    expect(screen.getByText("Sales")).toBeInTheDocument();
    expect(screen.queryByDisplayValue("Cost_by_Type")).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole("button", { name: "Show names" }));
    expect(screen.getByDisplayValue("Cost_by_Type")).toBeInTheDocument();
    fireEvent.change(screen.getByDisplayValue("Bookings"), {
      target: { value: "Travel_Bookings" },
    });
    expect(namesRef.current).toEqual([
      { id: "item-1", name: "Cost_by_Type" },
      { id: "item-2", name: "Travel_Bookings" },
    ]);
  });

  test("skips the dialog when there are no inline reports", async () => {
    await expect(confirmInstantInlineChartSaves({ location: "Sales", drafts: [] })).resolves.toEqual({
      ok: true,
      namesByItemId: {},
    });
  });
});
