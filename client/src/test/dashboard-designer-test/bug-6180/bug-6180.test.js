import "regenerator-runtime/runtime";
import "../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIDashboardDesigner } from "../../../components";
const crypto = require("crypto");

const App = ({ refresh }) => {
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
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIDashboardDesigner refresh={refresh}/>
      </Provider>
    </DndProvider>
  );
};

describe("Dashboard Designer Test", () => {
  test("rendering of designer", async () => {
    await waitFor(() => {
      const {rerender}=render(<App refresh={new Date()}/>);
      const originalComponent=<App refresh={new Date()}/>
      rerender(<App refresh={new Date()}/>)
      const updatedComponent=<App refresh={new Date()}/>
      expect(originalComponent).not.toBe(updatedComponent);
 
    });

   
  });
});
