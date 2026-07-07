jest.mock('@ant-design/flowchart', () => ({
    Flowchart: () => <div>Flowchart</div>,
    FormWrapper: () => <div>FormWrapper</div>,
    EdgeService: () => <div>EdgeService</div>,
    GroupService: () => <div>GroupService</div>,
    EditorPanels: () => <div>EditorPanels</div>,
    NsGraph: () => <div>NsGraph</div>,
  }));
  
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import { BrowserRouter } from "react-router-dom";
import { hcrMockState } from "./7608.mock";
import reducers from "../../../redux";
import { CannedReports } from "../../../components";
import { hiMockAxios } from "../../../app/mock-axios";
const crypto = require("crypto");

const App = ({ hcrMockState }) => {
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
    preloadedState: { cannedReports: hcrMockState },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <CannedReports tabNum={'1'} openFB={() => {}} flowchartInstance={{}}/>
      </Provider>
    </DndProvider>
  );
};

describe("Testing refresh UI in hcr", () => {
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
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc();
  });

  test("Canned Report component", async () => {
    render(
      <BrowserRouter>
        <App hcrMockState={hcrMockState} />
      </BrowserRouter>
    );

    const Button = screen.queryByTestId(/hcr-refresh-icon/i);
    fireEvent.click(Button);
    expect(screen.queryByTestId(/hcr-refresh-icon/i)).toBeTruthy();
  });
});
