import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import mocks from "../../../../overview-module/admin-data-mock";
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import { HIOrgEditableTable } from "../../../../../../components/hi-admin/components/hi-userManagement/components";

const { admin_initial_view_state } = mocks;

const App = ({ useractions_initial_view_state }) => {
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
    preloadedState: { admin: admin_initial_view_state },
  });

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIOrgEditableTable />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIOrgEditableTable", () => {
  test("HIOrgEditableTable component", async () => {
    await waitFor(() =>
      render(<App admin_initial_view_state={admin_initial_view_state} />)
    );
    expect(screen.queryByTestId(/hi-org-editable-table-des/i)).toBeFalsy();
   
    
  });
});