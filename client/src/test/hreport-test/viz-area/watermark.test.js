import React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Watermark from "../../../components/hi-reports/hi-viz-area/watermark/watermark";

describe("Watermark", () => {
    test("renders default text when no props are passed", () => {
        render(<Watermark />);
        expect(screen.getByText("Helical Insight")).toBeInTheDocument();
    });

    test("renders custom text passed via the text prop", () => {
        render(<Watermark text="© My App" />);
        expect(screen.getByText("© My App")).toBeInTheDocument();
    });

    test("renders plain text with no link when href is not provided", () => {
        render(<Watermark text="No Link" />);
        expect(screen.getByText("No Link")).toBeInTheDocument();
        expect(screen.queryByRole("link")).not.toBeInTheDocument();
    });

    test("renders a clickable link with correct attributes when href is provided", () => {
        render(<Watermark text="My App" link="https://example.com" />);
        const link = screen.getByRole("link", { name: "My App" });

        expect(link).toBeInTheDocument();
        expect(link).toHaveAttribute("href", "https://example.com");
        expect(link).toHaveAttribute("target", "_blank");
        expect(link).toHaveAttribute("rel", "noopener noreferrer");
    });

    test("defaults to bottom-right corner with matching border radius", () => {
        render(<Watermark text="Corner Test" />);
        const badge = screen.getByText("Corner Test");
        const wrapper = badge.parentElement;
        const parent = wrapper.parentElement;

        expect(parent).toHaveStyle({ position: "absolute", bottom: "2px", right: "5%" });
        expect(wrapper).toHaveStyle({ borderTopLeftRadius: "4px" });
    });

    test("positions correctly for the bottom-left corner", () => {
        render(<Watermark text="Corner Test" placement="bottom-left" />);
        const badge = screen.getByText("Corner Test");
        const wrapper = badge.parentElement;
        const parent = wrapper.parentElement;

        expect(parent).toHaveStyle({ position: "absolute", bottom: "2px", left: "0px" });
        expect(wrapper).toHaveStyle({ borderTopRightRadius: "4px" });
    });

    test("positions correctly for the top-right placement", () => {
        render(<Watermark text="Corner Test" placement="top-right" />);
        const badge = screen.getByText("Corner Test");
        const wrapper = badge.parentElement;
        const parent = wrapper.parentElement;

        expect(parent).toHaveStyle({ position: "absolute", top: "0px", right: "5%" });
        expect(wrapper).toHaveStyle({ borderBottomLeftRadius: "4px" });
    });

    test("positions correctly for the top-left placement", () => {
        render(<Watermark text="Corner Test" placement="top-left" />);
        const badge = screen.getByText("Corner Test");
        const wrapper = badge.parentElement;
        const parent = wrapper.parentElement;
        expect(parent).toHaveStyle({ position: "absolute", top: "0px", left: "0px" });
        expect(wrapper).toHaveStyle({ borderBottomRightRadius: "4px" });
    });

    test("wrapper ignores pointer events so it never blocks the map underneath", () => {
        render(<Watermark text="Pointer Test" />);
        const badge = screen.getByText("Pointer Test");
        const wrapper = badge.parentElement;
        const topParent = wrapper.parentElement;
        expect(topParent).toHaveStyle({ pointerEvents: "none" });
        expect(wrapper).toHaveStyle({ pointerEvents: "auto" });
    });

    test("wrapper has a high z-index so it stacks above map content", () => {
        render(<Watermark text="Z-Index Test" />);
        const badge = screen.getByText("Z-Index Test");
        const wrapper = badge.parentElement;
        const parent = wrapper.parentElement;

        expect(parent).toHaveStyle({ zIndex: 10 });
    });
});