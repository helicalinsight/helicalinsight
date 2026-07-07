// import { configureStore } from "@reduxjs/toolkit";
// import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
// import { Provider } from "react-redux";
// import reducers from "../../redux";
// import axios from "axios";
// import { DndProvider } from "react-dnd";
// import { HTML5Backend } from "react-dnd-html5-backend";
// import HIReportCE from "../../components/hi-report-ce/hi-report-ce.jsx";
// import { BrowserRouter } from "react-router-dom";
// const crypto = require("crypto");

// const reportCEIntialState = {
//   uuid: "",
//   editing: false,
//   typesDetails: null,
//   dataSourceData: [],
//   parametersData: [],
//   reportData: [],
//   dashboardLayoutData: {
//     html: "",
//     css: "",
//   },
//   activeEditorData: {
//     datasourceId: "",
//     dashboardId: "",
//     paramterId: "",
//     reportId: "",
//     type: "",
//     name: "",
//   },
//   activeTabs: {
//     showDatasource: false,
//     showDashboard: false,
//     showParameter: false,
//     showReport: false,
//   },
// };

// const App = ({ reportCEIntialState }) => {
//   const store = configureStore({
//     reducer: reducers,
//     middleware: (getDefaultMiddleware) =>
//       getDefaultMiddleware({
//         thunk: {
//           extraArgument: axios,
//         },
//         immutableCheck: false,
//         serializableCheck: false,
//       }),
//     preloadedState: { reportCe: reportCEIntialState },
//   });
//   return (
//     <DndProvider backend={HTML5Backend}>
//       <Provider store={store}>
//         <HIReportCE />
//       </Provider>
//     </DndProvider>
//   );
// };

// describe("Report CE", () => {
//   beforeAll(() => {
//     delete window.matchMedia;
//     window.matchMedia = (query) => ({
//       matches: false,
//       media: query,
//       onchange: null,
//       addListener: jest.fn(), // deprecated
//       removeListener: jest.fn(), // deprecated
//       addEventListener: jest.fn(),
//       removeEventListener: jest.fn(),
//       dispatchEvent: jest.fn(),
//     });
//     window.HTMLElement.prototype.scrollBy = jest.fn();
//     window.crypto = {};
//     window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
//   });

//   test("Report CE", async () => {
//     render(
//       <BrowserRouter>
//         <App reportCEIntialState={reportCEIntialState} />
//       </BrowserRouter>
//     );
//     expect(screen.queryByTestId(/report-ce-image/i)).toBeTruthy();
//   });
// });

function sum(a, b) {
  return a + b;
}

test("adds 1 + 2 to equal 3", () => {
  expect(sum(1, 2)).toBe(3);
});
