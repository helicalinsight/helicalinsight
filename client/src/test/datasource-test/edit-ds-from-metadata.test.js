import { configureStore } from "@reduxjs/toolkit";
import { render, screen } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import { hiMockAxios } from "../../app/mock-axios";
import HIDataSource from "../../components/hi-datasources/hi-dataSource.jsx";
import reducers from "../../redux";
import { menuData } from "../../redux/reducers/datasource.reducer";
import { appStore, dsStore } from "./mock-data/edit-ds-from-metadata.mock";
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
    preloadedState: { datasource: dsStore, app: appStore },
});
const dispatch = store.dispatch
const App = ({ intialState }) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={store}>
                <HIDataSource />
            </Provider>
        </DndProvider>
    );
};

describe("Datasource", () => {
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
      
    test("Datasource Module", async () => {
        // render(
        //     <BrowserRouter>
        //         <App intialState={intialState} />
        //     </BrowserRouter>
        // );
        dispatch({
            type: 'SET_FILE_BROWSER_FOLDER',
            payload: {}
        })
        dispatch({
            type: 'SET_IS_EDIT_CLICKED',
            payload: true
        })
        dispatch({
            type: 'SET_EDIT_DATA',
            payload: {
                permissionLevel: 5,
                driver: null,
                data: {
                    dir: 'Gagan',
                    driverName: null,
                    type: 'sql.jdbc.groovy.managed',
                    id: 201,
                    userName: null,
                    password: null,
                    condition: 'import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue(\'${user}.name\');\n        userName = userName.replaceAll("\'", "");\n        if (userName.equals("hiadmin")) {\n          responseJson.put("globalId", 1);\n        }\n      \n        if (userName.equals("hiuser")) {\n          responseJson.put("globalId", 3);\n        }\n      \n        if (userName.equals("test")) {\n          responseJson.put("globalId", 4);\n        }\n      \n        responseJson.put("type", "global.jdbc");\n      \n        //throw new RuntimeException("This is a test exception" +responseJson);\n        return responseJson;\n      }',
                    jdbcUrl: null
                },
                name: 'groovy man',
                classifier: 'efwd',
                type: 'sql.jdbc.groovy.managed',
                dataSourceType: 'Plain Groovy Managed DataSource'
            }
        })
        dispatch({
            type: 'SET_DATA_SOURCE_CONNECTION',
            payload: false
        })
        dispatch({
            type: 'SET_FILE_BROWSER_FOLDER',
            payload: {
                path: 'Gagan'
            }
        })
        render(
            <BrowserRouter>
                <App intialState={intialState} />
            </BrowserRouter>
        );
        // waitFor(2000)
        // expect(screen.queryByText(/Gagan/i)).toBeTruthy();
        // expect(screen.queryByText(/Groovy Managed Jdbc DataSource/i)).toBeTruthy();
        expect(screen.queryByText(/Choose Database/i)).toBeTruthy();
        expect(screen.queryByTestId(/choose-database/i)).toBeTruthy();
        expect(screen.queryByTestId(/supported/i)).toBeTruthy();
        expect(screen.queryByTestId(/bigdata/i)).toBeTruthy();
        expect(screen.queryByTestId(/nosql/i)).toBeTruthy();
        expect(screen.queryByTestId(/advanced/i)).toBeTruthy();
    });
});
