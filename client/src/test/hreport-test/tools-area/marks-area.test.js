
import { render, screen, fireEvent,waitFor } from '@testing-library/react';
import SubVizList from "../../../components/hi-reports/hi-editing-area/components/values/sub-viz-list.jsx"


describe("Rendering marks pane", () => {
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
        window.HTMLElement.prototype.scrollBy = jest.fn();
    });
    
  afterAll(() => {
    global.gc && global.gc()
  })
    
    test("sub visualisation list for grid chart",async () => {
        await waitFor(() => render(<SubVizList   selectedType="GridChart" subVizType="bar" />))
        expect(screen.queryByTestId(/selected-sub-viz-type/i)).toBeTruthy();
        await waitFor(() => fireEvent.click(screen.queryByTestId(/selected-sub-viz-type/i)) )
        expect(screen.queryByTestId(/sub-viz-type-bar/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-line/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-area/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-arc/i)).toBeTruthy();
    });
    test("sub visualisation list for sync chart",async () => {
        await waitFor(() => render(<SubVizList   selectedType="SyncChart" subVizType="bar" />))
        expect(screen.queryByTestId(/selected-sub-viz-type/i)).toBeTruthy();
        await waitFor(() => fireEvent.click(screen.queryByTestId(/selected-sub-viz-type/i)) )
        expect(screen.queryByTestId(/sub-viz-type-Column/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-SplineArea/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-StepArea/i)).toBeTruthy();
        expect(screen.queryByTestId(/sub-viz-type-Bubble/i)).toBeTruthy();
    });
});