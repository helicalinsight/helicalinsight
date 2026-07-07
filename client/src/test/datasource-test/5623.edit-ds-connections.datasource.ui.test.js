// import React from "react";
// import { screen, render } from "@testing-library/react";
// import { createStore } from "redux";
// import User from "./User";
// import reducers from "../../redux";
// // import store from "./store";
// import HIDataSource from "../../components/hi-datasources/hi-dataSource.jsx";

// jest.mock("./api", () => ({
//   getUserData: () => ({ name: "mock name" })
// }));

// const initialState = {
// 	user: { name: "mock name" },
// };

// const store = createStore(reducers, initialState);

// const Wrapper = ({ children }) => (
// 	<Provider store={store}>{children}</Provider>
// );

// describe("User", () => {
//   it("should display user name", async () => {
//     render(<HIDataSource />, { wrapper: Wrapper });

//     const userName = await screen.findByText("mock name");

//     expect(userName).toBeTruthy();
//   });
// });




import { configureStore } from "@reduxjs/toolkit";
import { render, screen, fireEvent, act, waitFor } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../redux";
import axios from "axios";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import HIDataSource from "../../components/hi-datasources/hi-dataSource.jsx";
import { BrowserRouter } from "react-router-dom";
import { menuData } from "../../redux/reducers/datasource.reducer";
import { hiMockAxios } from "../../app/mock-axios";
const crypto = require("crypto");


const App = () => {
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
        preloadedState: {
            app: {
                "shouldBlockNavigation": false,
                "value": true,
                "isAuthenticated": true,
                "isLogoutManually": false,
                "hasError": false,
                "showLicenseNotification": false,
                "isLicenseRendered": false,
                "applicationSettingsData": {
                    "userData": {
                        "serverTime": 1666270278993,
                        "expiryTime": 1666272078993,
                        "sessionUserName": "hiadmin",
                        "sessionUserEmail": "admin@helicalinsight.com",
                        "user": {
                            "name": "hiadmin",
                            "email": "admin@helicalinsight.com",
                            "actualUserName": "hiadmin",
                            "roles": [
                                "ROLE_ADMIN",
                                "ROLE_USER",
                                "ROLE_VIEWER"
                            ],
                            "profile": []
                        },
                        "rootDirectoryPermission": "4",
                        "provideHTMLExport": false,
                        "enableReportSave": "false",
                        "defaultEmailResourceType": "url",
                        "saml": {
                            "enabled": false,
                            "logoutLink": "/saml/logout/?"
                        },
                        "baseUrl": "http://localhost:5050/hi-ee/",
                        "clientTime": 1666270279696
                    },
                    "contentId": "Static/DashboardGlobals",
                    "settings": {
                        "jwtToken": null,
                        "adminPaths": {
                            "users": "admin/users",
                            "profiles": "admin/profiles",
                            "roles": "admin/roles",
                            "organisations": "admin/organisations",
                            "services": "services.html"
                        },
                        "services": "services",
                        "optionalReportParams": {},
                        "recursiveDirectoryLoad": false,
                        "HDI": {
                            "adhoc": {
                                "urls": {
                                    "services": "/services",
                                    "createDataSource": "/createDataSource",
                                    "listDataSources": "/listDataSources",
                                    "listLocations": "/listLocations",
                                    "getResources": "/getResources",
                                    "adhocReport": "/adhocReport"
                                }
                            }
                        },
                        "DashboardGlobals": {
                            "solutionLoader": "getSolutionResources",
                            "resourceLoader": "/getEFWSolution.html",
                            "updateService": "/executeDatasource.html",
                            "chartingService": "/visualizeData.html",
                            "exportData": "/exportData.html",
                            "reportDownload": "/downloadReport.html",
                            "productInfo": "getProductInformation.html",
                            "sendMail": "/sendMail.html",
                            "updateEFWTemplate": "/updateEFWTemplate.html",
                            "openHcr ": "/hcr-report.html",
                            "editHcr ": "/hcr-report-edit.html",
                            "controllers": {
                                "efw": "/getEFWSolution.html",
                                "efwsr": "/executeSavedReport.html",
                                "efwfav": "/executeFavourite.html",
                                "report": "/hi.html"
                            },
                            "saveReport": "/saveReport.html",
                            "fsop": "/fileSystemOperations",
                            "importFile": "/importFile.html",
                            "downloadEnableSavedReport": "/downloadEnableSavedReport.html",
                            "scheduling": {
                                "get": "/getScheduleData.html",
                                "update": "/updateScheduleData.html"
                            },
                            "services": "/services",
                            "designerCreate": "/designer.html",
                            "designerEdit": "/designer-edit.html",
                            "ceReportCreate": "/ce-report-create.html",
                            "ceReportEdit": "/ce-report-edit.html",
                            "adhocEdit": "/adhoc/report-edit.html",
                            "datasourceCreate": "/adhoc/datasources.html",
                            "metadataEdit": "/adhoc/metadata-edit.html",
                            "metadataCreate": "/adhoc/metadata-create.html",
                            "adhocReportCreate": "/adhoc/report-create.html",
                            "helicalReportCreate": "/adhoc/helical-report.html",
                            "helicalReportEdit": "/adhoc/helical-report-edit.html",
                            "openAdhoc": "/hi.html",
                            "openEfw": "/hi.html",
                            "visualizeAdhoc": "/visualizeAdhoc.html"
                        }
                    }
                },
                "routes": [
                    {
                        "title": "Home",
                        "addInNavbar": true,
                        "tutorialElementKey": "hi-navbar-home",
                        "url": "/admin",
                        "roles": [
                            "ROLE_ADMIN"
                        ]
                    },
                    {
                        "title": "HI: Login",
                        "url": "/",
                        "addInNavbar": false,
                        "roles": []
                    },
                    {
                        "title": "Data Sources",
                        "url": "/datasource",
                        "addInNavbar": true,
                        "roles": [
                            "ROLE_ADMIN"
                        ],
                        "tutorialElementKey": "hi-navbar-data-sources"
                    },
                    {
                        "title": "Meta Data",
                        "url": "/metadata",
                        "addInNavbar": true,
                        "roles": [
                            "ROLE_ADMIN"
                        ],
                        "tutorialElementKey": "hi-navbar-metadata"
                    },
                    {
                        "title": "Cube",
                        "url": "/cube",
                        "addInNavbar": true,
                        "roles": [
                            "ROLE_ADMIN"
                        ],
                        "tutorialElementKey": "hi-navbar-cube"
                    },
                    {
                        "title": "Reports",
                        "url": "/helical-report",
                        "addInNavbar": true,
                        "roles": [
                            "ROLE_ADMIN",
                            "ROLE_USER",
                            "ROLE_DOWNLOAD"
                        ],
                        "tutorialElementKey": "hi-navbar-reports"
                    },
                    {
                        "title": "Dashboard Designer",
                        "url": "/dashboard-designer",
                        "addInNavbar": true,
                        "roles": [
                            "ROLE_ADMIN",
                            "ROLE_USER",
                            "ROLE_DOWNLOAD"
                        ],
                        "tutorialElementKey": "hi-navbar-dashboard-designer"
                    },
                    {
                        "title": "Report View",
                        "url": "/report-viewer",
                        "addInNavbar": false,
                        "roles": [
                            "ROLE_ADMIN",
                            "ROLE_USER",
                            "ROLE_DOWNLOAD",
                            "ROLE_VIEWER"
                        ]
                    },
                    {
                        "title": "HI",
                        "url": "/hi",
                        "addInNavbar": false,
                        "roles": [],
                        "accessbleForAll": true
                    }
                ],
                "activeRoute": "/datasource/all",
                "isApplicationSettingsServiceCheck": true,
                "nxtRoute": "",
                "toggleSidebar": false,
                "showNavbar": true,
                "logType": "",
                "isUrlAuthenticating": false,
                "isSessionOver": false,
                "viewModeInfo": null,
                "editModeInfo": null,
                "aboutToChangeRoute": null,
                "viewerEmailModalVisible": false,
                "viewerScheduleModalVisible": false
            }
        },
    });
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
        window.console.error = () => { }
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
        render(
            <BrowserRouter>
                <App />
            </BrowserRouter>
        );
        expect(screen.queryByText(/Choose Database/i)).toBeTruthy();
        expect(screen.queryByTestId(/choose-database/i)).toBeTruthy();
        expect(screen.queryByTestId(/supported/i)).toBeTruthy();
        expect(screen.queryByTestId(/bigdata/i)).toBeTruthy();
        expect(screen.queryByTestId(/nosql/i)).toBeTruthy();
        expect(screen.queryByTestId(/advanced/i)).toBeTruthy();
    });
});
