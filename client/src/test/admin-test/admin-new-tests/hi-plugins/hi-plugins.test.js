import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { admin_data} from "./hi-plugins-mock";

import { hiMockAxios } from "../../../../app/mock-axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { HIPlugins } from "../../../../components/hi-admin";



const crypto = require("crypto");

const App = ({ admin_data }) => {
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
    preloadedState: { admin: admin_data },
  });
  let apiRef = jest.fn();
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIPlugins apiRef = {apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("HIPlugins Component", () => {
  test("HIPlugins", async () => {
    render(<App admin_data={admin_data} />);
    const table = screen.queryByTestId(/plugins-table/i);
    expect(table).toBeTruthy(); 

    const drawer = screen.queryByTestId(/hi-plugins-drawer/i);
    expect(drawer).toBeFalsy();
  });
});
