import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../redux";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { BrowserRouter } from "react-router-dom";
import { reduxStateWhenDataSourceAdvancedCardGroovyManagedIsClicked } from "./bug-5645-mock-data";
import axios from "axios";
import DataSourceAdvancedFiles from "../../../components/hi-datasources/Components/datasource-advanced-files";

const crypto = require("crypto");

const App = () => {
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
      datasource: reduxStateWhenDataSourceAdvancedCardGroovyManagedIsClicked,
    },
  });

  const props = {
    editable: false,
    driverCategory: "advanced",
    activeKey: "1",
    testConnClick: false,
    saveConnClick: false,
    editorInput:
      'import groovy.sql.Sql;\n      import net.sf.json.JSONObject;\n      import com.helicalinsight.adhoc.metadata.GroovyUsersSession;\n      public JSONObject evalCondition() {\n        JSONObject responseJson = new JSONObject();\n        String userName = GroovyUsersSession.getValue(\'${user}.name\');\n        userName = userName.replaceAll("\'", "");\n        if (userName.equals("hiadmin")) {\n          responseJson.put("globalId", 1);\n        }\n      \n        if (userName.equals("hiuser")) {\n          responseJson.put("globalId", 3);\n        }\n      \n        if (userName.equals("test")) {\n          responseJson.put("globalId", 4);\n        }\n      \n        responseJson.put("type", "global.jdbc");\n      \n        //throw new RuntimeException("This is a test exception" +responseJson);\n        return responseJson;\n      }',
  };
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <DataSourceAdvancedFiles {...props} />
      </Provider>
    </DndProvider>
  );
};

describe("Datasource Advanced Tab Groovy Managed", () => {
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
  
  test("Elements present in Advanced Files of Data Source", async () => {
    await waitFor(() =>
      render(
        <BrowserRouter>
          <App />
        </BrowserRouter>
      )
    );
    expect(
      screen.queryByTestId(/hi-advanced-tab-datasource-name/i)
    ).toBeTruthy();
  });
});
