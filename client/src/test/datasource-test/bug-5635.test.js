import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import {
  render,
  screen,
  fireEvent,
  act,
  waitFor,
} from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { menuData } from "../../redux/reducers/datasource.reducer";
import { DatasourcePage } from "../../pages";
import { hiMockAxios } from "../../app/mock-axios";

const crypto = require("crypto");

const intialState = {
  dsMode: {
    mode: "",
    driver: "",
    data: {},
  },
  dataSources: [],
  menuData: menuData,
  dataSourceTypes: [],
  dataSourceDriversList: [],
  reportsData: {},
  driverCategory: "",
  clickedActiveDatabaseData: {}, //data when user clicks on database card
  flatFileUploadName: {},
  selectedDriverInfo: {}, //data based on selected driver
  clickedRecordData: {}, //record data when user clciks on any actions in view table
  fileBrowserFolder: {},
  isEditClicked: false,
  isDatasourceConnectionSuccess: false,
  isTestConnectionSuccess: false,
  selectedDriverCatergory: "",
  viewData: null,
  editData: {},
  connectedDatasourceData: {},
  buttonTypes: { type: "", datasourceType: "" },
};

const App = ({ intialState }) => {
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
    preloadedState: { datasource: intialState },
  });
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DatasourcePage />
      </Provider>
    </DndProvider>
  );
};

describe("Datasource", () => {
  test("Datasource Module", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App intialState={intialState} />
        </BrowserRouter>
      )
    );
    expect(screen.queryByText(/Choose Database/i)).toBeTruthy();
    expect(screen.queryByTestId(/choose-database/i)).toBeTruthy();
    expect(screen.queryByTestId(/supported/i)).toBeTruthy();
    expect(screen.queryByTestId(/bigdata/i)).toBeTruthy();
    expect(screen.queryByTestId(/nosql/i)).toBeTruthy();
    expect(screen.queryByTestId(/advanced/i)).toBeTruthy();
    // expect(screen.queryByText(/Hive/i)).toBeTruthy();
    // await waitFor(() => {
    //   expect(screen.getByText('Apache Drill')).toBeInTheDocument()
    // })
    // await waitFor(() => screen.getByText("Apache Drill"));
    // screen.debug(undefined, 400000);
    // expect(screen.queryByTestId(/Apache Drill/i)).toBeTruthy();
  });
});
