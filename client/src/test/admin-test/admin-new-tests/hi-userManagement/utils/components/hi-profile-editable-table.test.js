import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { admin_data ,app_data } from "./hi-um-mocks";
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import { HIProfileTable } from "../../../../../../components/hi-admin/components/hi-userManagement/components";




const App = ({ admin_data ,app_data }) => {
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
    preloadedState: { admin: admin_data ,app : app_data },
  });
 

  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <HIProfileTable  />
      </Provider>
    </DndProvider>
  );
};

describe("Rendering HIProfileTable", () => {
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

  afterAll(() => {
    global.gc && global.gc()
  })
  
  test("HIProfileTable component", async () => {
    await waitFor(() =>
      render(<App app_data ={app_data} admin_data={admin_data} />)
    );

    const Table = screen.queryByTestId(/hi-profile-editable-table/i);
    
    expect(Table).toBeTruthy();
  
    
  });
});