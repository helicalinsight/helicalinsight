import "regenerator-runtime/runtime";
import "../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { render, screen, waitFor } from "@testing-library/react";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { Provider } from "react-redux";
import { hiMockAxios } from "../../app/mock-axios";
import { HIInstantBI } from "../../pages";
import reducers from "../../redux";
import { editButtonReduxData } from "./instant-bi-redux-mocks/edit-button-redux-data";
import { handleEditModalOnOK } from "../../components/hi-instant-bi/components/hi-instant-bi-report-toolbar/hi-handle-edit-mode-on-ok";
import { derivedFormDataConvertorToReportProps } from "../../components/hi-instant-bi/utils/instant-bi-utilities";
import App from "../../app/app";

const crypto = require("crypto");
const flushPromises = () => new Promise(setImmediate);
const AppComponent = ({ store }) => {
  return (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <App />
      </Provider>
    </DndProvider>
  );
};

describe("Going to hreport testing", () => {
  test("Instant BI On edit testing", async () => {
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
      preloadedState: { instantBI: editButtonReduxData },
    });
    const dispatch = store.dispatch;
    const getState = store.getState;
    await flushPromises( render(<AppComponent store={store} />));
    const reportProps = derivedFormDataConvertorToReportProps({
      derivedFormData: getState().instantBI.derivedFormdata,
      metadata: getState().instantBI.metadata,
    });
    await flushPromises( handleEditModalOnOK({ dispatch, reportProps }));
    expect(getState().app.editModeInfo).toStrictEqual({
      reportProps,
      extension: "hr",
    });
  });
});
