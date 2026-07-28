import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { queryByText, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../app/mock-axios";
import { HIUsers } from "../../../components";
import { bug6384reduxState } from "./bug-6384-mock-data";
const crypto = require("crypto");

const App = ({  store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
      <HIUsers
        customRole={false}
      />
      </Provider>
    </DndProvider>
  );
};

  


describe("Dashboard Designer Test", () => {
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
      preloadedState:bug6384reduxState
  });
  
  afterAll(() => {
    global.gc && global.gc()
  })

  test("renders the whatsNewContent correctly",async () => {
    await waitFor(() => render(<App store={store} />))
    expect(screen.queryByTestId(/hi-whats-new/i)).toBeTruthy()
    // expect(screen.queryByText(/HI is now a Single Page Application (SPA) for a better web experience./i)).toBeTruthy()

    
     
  });

});


