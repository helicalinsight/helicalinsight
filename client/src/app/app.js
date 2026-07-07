import React, { useEffect, useRef, useState } from "react";
import { Provider } from "react-redux";
import reducers from "../redux/index";
import { message } from "antd";
import { configureStore } from "@reduxjs/toolkit";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { handleAuthDetails, handleAxiosObject } from "./helperMethods";
import { HIRouter } from "../pages";
import "./app.scss";
import "./app-print.scss";
import qs from "qs";
import ErrorBoundry from "../errorBoundry/ErrorBoundry";
import HIAxios from "./HiAxios";

var r = document.querySelector(":root");
function LightenDarkenColor(col, amt) {
  var num = parseInt(col, 16);
  var r = (num >> 16) + amt;
  var b = ((num >> 8) & 0x00ff) + amt;
  var g = (num & 0x0000ff) + amt;
  var newColor = g | (b << 8) | (r << 16);
  return "#" + newColor.toString(16);
}
let mainColor = "337ab7";
r.style.setProperty("--ant-primary-color", LightenDarkenColor(mainColor, 0));
r.style.setProperty(
  "--ant-primary-color-hover",
  LightenDarkenColor(mainColor, 40)
);

const App = ({ auth, report, baseURL, intial_hreport_state }) => {
  const [isAuthDetailsCheck, setIsAuthDetailsCheck] = useState(false);
  const queryString = window.location.hash?.split("?")[1] || "";
  let parameters = qs.parse(queryString);
  let { authToken, ...rest } = parameters;
  // if (authToken && authToken.startsWith("Bearer")) {
  //   auth = { type: "jwt", authToken: authToken, embedMode: false, rest };
  //   report = { dir: rest.dir, file: rest.file };
  // }

  const store = configureStore({
    reducer: reducers,
    middleware: (getDefaultMiddleware) =>
      getDefaultMiddleware({
        thunk: {
          extraArgument: (dispatch, successNotification) => {
            return new HIAxios(auth, baseURL, dispatch, successNotification);
          },
        },
        immutableCheck: false, // if a payload of action is too long/heavy then it was causing slows down issue in development mode so set it to false , in production mode it is by default false
        serializableCheck: false, // if a payload of action is too long/heavy then it was causing slows down issue in development mode so set it to false , in production mode it is by default false
      }),
    preloadedState: {
      hreport: intial_hreport_state,
    },
    devTools: {
      serialize: {
        options: {
          undefined: true,
          function: function (fn) { return fn.toString() }
        }
      }
    }
  });

  if (process.env.NODE_ENV === "development") {
    window.store = store;
  }

  message.config({
    duration: 2,
    // top: 500,
  });

  useEffect(() => {
    if (auth && auth.embedMode === false) {
      setIsAuthDetailsCheck(true);
    } else {
      setIsAuthDetailsCheck(handleAuthDetails({ auth, report }));
    }
  }, []);

  return isAuthDetailsCheck ? (
    <DndProvider backend={HTML5Backend}>
      <Provider store={store}>
        <ErrorBoundry>
          <HIRouter report={report} auth={auth} />
        </ErrorBoundry>
      </Provider>
    </DndProvider>
  ) : (
    <></>
  );
};

export default App;
