import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DndProvider } from "react-dnd";
import OsDetailsTable from "../../../../../components/hi-admin/components/hi-systemDetails/components/os-details-table";
import reducers from "../../../../../redux";
import { admin_data_ } from "./hi-sd-mocks";
import { hiMockAxios } from "../../../../../app/mock-axios";
const crypto = require("crypto");

// const admin_initial_view_state = JSON.parse(
//   `{
//     "osTableExpand": false,
//     "jvmTableExpand": false,
//     "pluginsData": null
//    }
//   `
// );

const App = ({ admin_data_ }) => {
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
    preloadedState: { admin: admin_data_ },
  });
  let apiRef = jest.fn()
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <OsDetailsTable apiRef ={apiRef} />
      </Provider>
    </DndProvider>
  );
};

describe("OsDetailsTable Component", () => {
  test("OsDetailsTable", async () => {
    await waitFor(() => render(<App admin_data_={admin_data_} />));

    const Table = screen.queryByTestId(/os-details-table-component/i);
    expect(Table).toBeTruthy();

    const refreshIcon = screen.queryByTestId(/os-details-table-refresh/i);
      fireEvent.click(refreshIcon);
      expect(screen.queryByTestId(/os-details-table-refresh/i)).toBeTruthy();
      


  });
});
