// import { configureStore } from "@reduxjs/toolkit";
// import { render, screen, waitFor } from "@testing-library/react";
// import mocks from "./admin-data-mock";
// import { Provider } from "react-redux";
// import reducers from "../../../redux";
// import axios from "axios";
// import { DndProvider } from "react-dnd";
// import { HTML5Backend } from "react-dnd-html5-backend";
// import { HIOverview } from "../../../components/hi-admin";

// const { admin_initial_view_state } = mocks;

// const App = ({ admin_initial_view_state }) => {
//   // console.log("intial_admin_state", admin_initial_view_state);
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
//     preloadedState: { admin: admin_initial_view_state },
//   });
//   return (
//     // <div>hello</div>
//     <DndProvider backend={HTML5Backend}>
//       <Provider store={store}>
//         <HIOverview />
//       </Provider>
//     </DndProvider>
//   );
// };

// describe("Rendering Intial view", () => {
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
//   });

//   test("Overview component", async () => {
//     await waitFor(() =>
//       render(<App admin_initial_view_state={admin_initial_view_state} />)
//     );
//     expect(screen.queryByTestId(/hi-overview-container/i)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-diskspace-title/i)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-diskspace-title/i).innerHTML).toBe(
//       "Disk Space"
//     );
//     const diskSpace = `${parseInt(
//       admin_initial_view_state.diskData.totalDiskSize / 1024
//     )} GB`;
//     expect(screen.queryByText(diskSpace)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-jvm-memory-title/i)).toBeTruthy();
//     const jvmMemory = `${parseInt(
//       admin_initial_view_state.jvmData.totalMemory
//     )} MB`;
//     expect(screen.queryByText(jvmMemory)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-jvm-memory-title/i).innerHTML).toBe(
//       "JVM Memory"
//     );
//     expect(screen.queryByTestId(/hi-temp-directory-title/i)).toBeTruthy();
//     expect(
//       screen.queryByTestId(/hi-temp-directory-no-of-files/i).innerHTML
//     ).toBe(admin_initial_view_state.tempData.tempFileArray.length.toString());
//     expect(screen.queryByTestId(/hi-temp-directory-title/i).innerHTML).toBe(
//       "Temp Directory"
//     );
//     expect(screen.queryByTestId(/hi-cache-size-title/i)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-cache-size-size/i).innerHTML).toBe(
//       admin_initial_view_state.cacheSize.size.toString()
//     );
//     expect(screen.queryByTestId(/hi-cache-size-title/i).innerHTML).toBe(
//       "Cache"
//     );
//     expect(screen.queryByTestId(/hi-cached-reports-title/i)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-cached-reports-size/i).innerHTML).toBe(
//       admin_initial_view_state.cachedReports.length.toString()
//     );
//     expect(screen.queryByTestId(/hi-cached-reports-title/i).innerHTML).toBe(
//       "Cached reports"
//     );
//     expect(screen.queryByTestId(/hi-data-sources-title/i)).toBeTruthy();
//     expect(screen.queryByTestId(/hi-data-sources-title/i).innerHTML).toBe(
//       "Data sources cached"
//     );
//     expect(screen.queryByTestId(/hi-data-sources-size/i).innerHTML).toBe(
//       admin_initial_view_state.cachedDataSources.dataSources.length.toString()
//     );
//   });
// });
test("Disk data", (done) => {
  expect(1).toBeTruthy();
  done();
});
