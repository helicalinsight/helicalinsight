import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { hiMockAxios } from "../../../../../../app/mock-axios";
import SortableTable from "../../../../../../components/hi-dashboard-designer/components/hi-parameter-drawer/hi-sortable-table";
import { designer_data } from "./sortableTable.mocks";



const App = ({ designer_data }) => {
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
    preloadedState: { designer: designer_data },
  });
  const props = {
    dataSource:[] ,
    columns:[] ,
      onSortEnd:jest.fn()
  }
  
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <SortableTable {...props}/>
      </Provider>
    </DndProvider>
  );
};

describe("Rendering SortableTable", () => {
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
  
  test("SortableTable component", async () => {
    await waitFor(() =>
      render(<App designer_data={designer_data} />)
    );
    const table = screen.queryByTestId(/hi-parameter-sortable-table/i);
    
    expect(table).toBeTruthy();
  });
});
