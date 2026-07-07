import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import { Provider } from "react-redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import axios from "axios";
import reducers from "../../../../redux";
import { propsFor5650, reduxStateFor5650 } from "./bug-5650-mock-data";
import { HIPropertyPane } from "../../../../components/common";
import { useState } from "react";
import { updateReportProperty } from "../../../../redux/actions/hreport.actions";

const crypto = require("crypto");

const App = ({ store }) => {
  const [key, setKey] = useState(new Date());
  const [items, setItems] = useState([
    {
      key: "prefix",
      label: "Prefix",
      placeHolder: "Enter prefix",
      value: "asdfsa",
      elementType: "Input",
      groupId: "format",
      dataTestId: "hi-hr-properties-prefix",
    },
  ]);
  const props = propsFor5650;
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIPropertyPane
          {...props}
          items={items}
          newKey={key}
          onReset={() => {
            setKey(new Date());
            setItems([
              {
                key: "prefix",
                label: "Prefix",
                placeHolder: "Enter prefix",
                value: "",
                elementType: "Input",
                groupId: "format",
                dataTestId: "hi-hr-properties-prefix",
              },
            ]);
          }}
        />
      </Provider>
    </DndProvider>
  );
};

describe("Property Pane unit testing", () => {
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
    global.gc && global.gc()
  })
  
  test("Elements present in Property Pane", async () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: {
            extraArgument: axios,
          },
          immutableCheck: false,
          serializableCheck: false,
        }),
      preloadedState: {
        hreport: reduxStateFor5650,
      },
    });
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App store={store} />
        </BrowserRouter>
      )
    );
    // const dispatch = store.dispatch;
    expect(screen.queryByTestId(/hi-property-pane-reset-button/i)).toBeTruthy();
    expect(screen.queryByTestId(/hi-hr-properties-prefix/i).value).toBe(
      "asdfsa"
    );
    await waitFor(() =>
      fireEvent.click(screen.queryByTestId(/hi-property-pane-reset-button/i))
    );

    expect(screen.queryByTestId(/hi-hr-properties-prefix/i).value).toBe("");
  });
});
