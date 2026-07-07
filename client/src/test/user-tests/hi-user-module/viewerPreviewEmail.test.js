import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen } from "@testing-library/react";
import "core-js/stable";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { hiMockAxios } from "../../../app/mock-axios";
import EmailPreview from "../../../components/hi-user-module/components/EmailPreview";
import reducers from "../../../redux";
const flushPromises = () => new Promise(setImmediate);

const App = (props) => {
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: hiMockAxios,
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
    });
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <EmailPreview {...props} />
            </Provider>
        </DndProvider>
    );
};

describe("Rendering PreviewDrawerEmail", () => {
    beforeAll(() => {
        delete window.matchMedia;
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(), // deprecated
            removeListener: jest.fn(), // deprecated
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        });
        window.HTMLElement.prototype.scrollBy = jest.fn();
    });

    test("PreviewDrawerEmail Component", async () => {
        let loading = false, data = {
            "subject": "'somen@helicaltech.com'",
            "recipients": [
                "manish.kumar@helicaltech.com,"
            ],
            "body": "<p>'somen@helicaltech.com'</p><p>'somen@helicaltech.com'</p><p>'true'</p><p>'hiadmin'</p>"
        }
        await flushPromises(
            render(<App onPreviewClick={jest.fn()} loading={loading} data={data} />)
        );



        const Button = screen.queryByTestId(/hi-viewer-preview-email-button/i);
        expect(Button).toBeTruthy();
        fireEvent.click(Button);
        const drawer = screen.queryByTestId(/hi-viewer-email-preview/i);
        expect(drawer).toBeTruthy();
    });
});
