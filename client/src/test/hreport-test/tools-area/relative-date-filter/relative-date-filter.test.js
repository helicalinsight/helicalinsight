import "core-js/stable";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import "regenerator-runtime/runtime";
import RelativeList from "../../../../components/hi-reports/hi-editing-area/components/filters/relative-list";
import {
    dataWithDatepartAsDate, dataWithDatepartAsHour, dataWithDatepartAsIndividual, dataWithDatepartAsMinute, dataWithDatepartAsMonth, dataWithDatepartAsQuarter, dataWithDatepartAsSecond, dataWithDatepartAsTime, dataWithDatepartAsYear
} from './relative-date-filter-mock-data';
const crypto = require("crypto");
const flushPromises = () => new Promise(setImmediate);
const App = ({ ...props }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <RelativeList {...props} />
        </DndProvider>
    );
};

describe("hreport relative date filters test", () => {
    beforeAll(() => {
        delete window.matchMedia
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(), // deprecated
            removeListener: jest.fn(), // deprecated
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        })
        window.createObjectURL = jest.fn();
        window.HTMLElement.prototype.scrollBy = jest.fn();
        window.crypto = {};
        window.crypto.getRandomValues = arr => crypto.randomBytes(arr.length)
    });

    afterAll(() => {
        global.gc && global.gc()
      })
        
    test("relative date anchor filter with date function as individual", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsIndividual} />));
        expect(screen.queryByTestId(/hr-relative-list-value-individual/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-individual/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-individual/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-individual/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-individual/i), { target: { value: "2022-03-07 10:30:13.0" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-individual/i), { keyCode: 13, target: { value: "2022-03-07 10:30:13.0" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-individual'))
        expect(getByText("Selected Value : 2022-01-01 00:00:00")).toBeTruthy()
    });

    test("relative date anchor filter with date function as date", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsDate} />));
        expect(screen.queryByTestId(/hr-relative-list-value-date/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-date/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-date/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-date/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-date/i), { target: { value: "2021-03-07" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-date/i), { keyCode: 13, target: { value: "2021-03-07" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-date'))
        expect(getByText("Selected Value : 2021-01-01")).toBeTruthy()
    });

    test("relative date anchor filter with date function as year", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsYear} />));
        expect(screen.queryByTestId(/hr-relative-list-value-year/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-year/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-year/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-year/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-year/i), { target: { value: "2017" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-year/i), { keyCode: 13, target: { value: "2017" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-year'))
        expect(getByText("Selected Value : 2017")).toBeTruthy()
    });

    test("relative date anchor filter with date function as quarter", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsQuarter} />));
        expect(screen.queryByTestId(/hr-relative-list-value-quarter/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-quarter/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-quarter/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-quarter/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-quarter/i), { target: { value: "01" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-quarter/i), { keyCode: 13, target: { value: "01" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-quarter'))
        expect(getByText("Selected Value : 01")).toBeTruthy()
    });

    test("relative date anchor filter with date function as month", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsMonth} />));
        expect(screen.queryByTestId(/hr-relative-list-value-month/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-month/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-month/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-month/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-month/i), { target: { value: "03" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-month/i), { keyCode: 13, target: { value: "03" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-month'))
        expect(getByText("Selected Value : 01")).toBeTruthy()
    });

    test("relative date anchor filter with date function as time", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsTime} />));
        expect(screen.queryByTestId(/hr-relative-list-value-time/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-time/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-time/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-time/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-time/i), { target: { value: "12:03:51" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-time/i), { keyCode: 13, target: { value: "12:03:51" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-time'))
        expect(getByText("Selected Value : 00:00:00")).toBeTruthy()
    });

    test("relative date anchor filter with date function as hour", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsHour} />));
        expect(screen.queryByTestId(/hr-relative-list-value-hour/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-hour/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-hour/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-hour/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-hour/i), { target: { value: "12" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-hour/i), { keyCode: 13, target: { value: "12" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-hour'))
        expect(getByText("Selected Value : 00")).toBeTruthy()
    });

    test("relative date anchor filter with date function as minute", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsMinute} />));
        expect(screen.queryByTestId(/hr-relative-list-value-minute/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-minute/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-minute/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-minute/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-minute/i), { target: { value: "34" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-minute/i), { keyCode: 13, target: { value: "34" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-minute'))
        expect(getByText("Selected Value : 00")).toBeTruthy()
    });

    test("relative date anchor filter with date function as seconds", async () => {
        await flushPromises( render(<App {...dataWithDatepartAsSecond} />));
        expect(screen.queryByTestId(/hr-relative-list-value-second/i)).toBeTruthy();
        expect(screen.queryByTestId(/hr-relative-list-checkbox-second/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-checkbox-second/i), {})
        expect(screen.queryByTestId(/hr-relative-list-input-field-second/i)).toBeTruthy();
        fireEvent.change(screen.queryByTestId(/hr-relative-list-input-field-second/i), { target: { value: "59" } })
        fireEvent.keyDown(screen.queryByTestId(/hr-relative-list-input-field-second/i), { keyCode: 13, target: { value: "59" } })
        const { getByText } = within(screen.getByTestId('hr-relative-list-value-second'))
        expect(getByText("Selected Value : 00")).toBeTruthy()
    });
});