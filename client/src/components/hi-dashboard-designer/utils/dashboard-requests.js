import {
  savedDashboardConfig,
  storeDashboardConfig,
  setIsLoading,
  isSavingDesigner,
  changeTheReport,
} from "../../../redux/actions/dashboard-designer.actions";
import {
  convertGridItemsData,
  convertOldConfigToNewConfig,
  convertOldVariablesConfigToNewVariablesConfig,
} from "./old-hi-app-data-converstions";
import { addItem } from "./common-functions";
import requests from "../../../base/requests";
import { uriConfig } from "../../../base/requests/dashboard.requests";
import { appActions, fileBrowserActions } from "../../../redux/actions";
import { appendDesignerConstants, appendDesignerObjectConstants, checkForDefaultValuesInDashboard, checkForDefaultValuesInDashboardForSave, designerSettingsConstants, designerStateComponenetsInitialPositionConstants, gridItemConfigConstants, gridSettingsConstants, removeDesignerConstants, removeDesignerObjectConstants, removeLayoutConstants } from "./config-dashboard-gridSettings";
import { replacePathsInJson } from "./recursive-functions";
import { cloneDeep } from "lodash";


const { updateRoute, setAccessDeniedInfo } = appActions;


export const editTheDashboard = ({
  dir,
  fileName,
  dispatch,
  Notify,
  parameters,
  designerMode,
  setFileInfo,
  setParametersReview,
}) => {
  dispatch(setIsLoading(true));
  let dashboardVariables = {};
  requests.dashboard(dispatch).postDashboardRequest(
    {
      dir,
      file: fileName,
    },
    uriConfig.editDashboard,
    (res) => {
      let report_paths = res?.report_paths || [];
      res.state = replacePathsInJson(res.state, report_paths)
      let components = res?.state?.components;
      document.title = `${res.reportName} | HI:Dashboard Designer`;
      if (typeof setFileInfo === "function") {
        setFileInfo({ fileTitle: res.reportName });
      }
      if (Array.isArray(components)) {
        components = components.map(ele => ({ ...ele, initialPosition: appendDesignerObjectConstants({ apiObj: ele.initialPosition, constantObj: designerStateComponenetsInitialPositionConstants }), gridItemConfig: appendDesignerConstants({ constantData: gridItemConfigConstants, apiData: ele.gridItemConfig }) }));
        const layout = removeLayoutConstants(res.state.componentSettings?.layout);
        const gridSettingsData = appendDesignerConstants({ constantData: gridSettingsConstants, apiData: res.state.componentSettings?.gridSettingsData })
        dashboardVariables = res.state.variables;
        const dashboardData = res.state.componentSettings?.dashboardData;
        const gridIndex = res.state.componentSettings?.gridIndex;
        let designerSettings = appendDesignerConstants({ constantData: designerSettingsConstants, apiData: res.state.componentSettings?.designerSettings })
        designerSettings = checkForDefaultValuesInDashboard(designerSettings, res.state.componentSettings?.designerSettings) // added for 7970 bug
        const parameterDrawerStatus =
          res.state.componentSettings?.parameterDrawerStatus;
        if (typeof setParametersReview === "function") {
          setParametersReview({
            parameters: { ...dashboardVariables, ...parameters },
          });
        }
        dispatch(
          storeDashboardConfig({
            gridSettings: gridSettingsData,
            gridItemsData: components,
            layout,
            dashboardUUID: fileName,
            dashboardVariables: { ...dashboardVariables, ...parameters },
            components: dashboardData,
            designerMode,
            parameterDrawerStatus,
            gridIndex,
            designerSettings,
            savedReportName: res.reportName,
          })
        );
      }
      // const { gridItemsData, layout } = convertGridItemsData(components);
      else if (typeof components === "object") {
        const newConfig = convertOldConfigToNewConfig(res.state.designer.presentConfig);
        const newData = convertGridItemsData(components);
        dashboardVariables = convertOldVariablesConfigToNewVariablesConfig(
          res.state.variables
        );
        const resultObj = {
          gridSettings: [
            {
              key: "breakpoints",
              values: [
                { name: "lg", value: 1200, tooltip: "Large Screens" },
                { name: "md", value: 996, tooltip: "Medium Screens" },
                { name: "sm", value: 768, tooltip: "Small Screens" },
                { name: "xs", value: 480, tooltip: "Extra Small Screens" },
                {
                  name: "xxs",
                  value: 480,
                  tooltip: "Extra Extra Small Screens",
                },
              ],
            },
            {
              key: "columns",
              values: [
                { name: "lg", value: 100, tooltip: "Large Screens" },
                { name: "md", value: 100, tooltip: "Medium Screens" },
                { name: "sm", value: 100, tooltip: "Small Screens" },
                { name: "xs", value: 100, tooltip: "Extra Small Screens" },
                {
                  name: "xxs",
                  value: 100,
                  tooltip: "Extra Extra Small Screens",
                },
              ],
            },
            ...newConfig,
          ],
          gridItemsData: newData.gridItemsData,
          layout: newData.layout,
          dashboardUUID: fileName,
          gridWidthOption: res.state.gridWidthOption,
          dashboardVariables: { ...dashboardVariables, ...parameters },
          designerMode,
        };
        dispatch(storeDashboardConfig(resultObj));
      }
    },
    (e) => {
      // Notify.error({ ...e, type: "Network Call" });
      // dispatch(setAccessDeniedInfo({ subTitle: e?.message }));
      // dispatch(updateRoute("/access-denied"));
    }
  );
};

export const saveTheDashboard = ({
  designerSettings,
  dir,
  fileName,
  gridItemsData,
  layout,
  dashboardConfig,
  css,
  script,
  printOptions,
  componentSettings,
  dispatch,
  Notify,
  gridSettingsData,
  dashboardVariables,
  uuid,
  parameterDrawerStatus,
}) => {
  dispatch(isSavingDesigner(true));
  gridSettingsData = removeDesignerConstants({ constantData: gridSettingsConstants, apiData: gridSettingsData });
  // designerSettings = removeDesignerConstants({ constantData: designerSettingsConstants, apiData: designerSettings });  // commented for 7970 so that default state of designer settings can also be saved

  let version = ''
  dispatch((_, getState) => {
    let productInfo = getState().admin?.productData ?? {}
    const { Version = '' } = productInfo;
    version = Version
  });

  const dashboardState = {
    variables: dashboardVariables,
    components: gridItemsData.map(ele => ({ ...ele, initialPosition: removeDesignerObjectConstants({ apiObj: ele.initialPosition, constantObj: designerStateComponenetsInitialPositionConstants }), gridItemConfig: removeDesignerConstants({ constantData: gridItemConfigConstants, apiData: ele.gridItemConfig }) })),
    dashboardConfig,
    css,
    script,
    printOptions,
    componentSettings: {
      ...componentSettings,
      gridSettingsData,
      layout,
      designerSettings,
      parameterDrawerStatus,
    },
    // need to add version here for saving a new dashboard/ an existing dashboard , for every new/old dashbaord version will be saved if you save the dashboard.  
    version
  };

  requests.dashboard(dispatch).postDashboardRequest(
    {
      htmlString: JSON.stringify(<div>hi</div>),
      state: dashboardState,
      dir,
      fileName,
      uuid: uuid ? uuid : undefined,
    },
    uriConfig.saveDashboard,
    (res) => {
      dispatch(
        savedDashboardConfig({ uuid: res.uuid, savedReportName: fileName })
      );
      document.title = `${fileName} | HI:Dashboard Designer`;
      // Notify.success({ ...res, type: "Network Call" });
      if (res?.data) {
        dispatch(fileBrowserActions.saveFileinFb(res.data));
      }
    },
    (e) => {
      // Notify.error({ ...e, type: "Network Call" });
    }
  );
};
export const openInDashboardCallback = ({
  path,
  name,
  title,
  resourceId,
  dispatch,
  gridItemsData,
  openThroughApi = false,
  gridItemId,
  userDefinedLayout,
  replaceReportIdValue,
}) => {
  const reportInfo = {
    file: {
      path,
      name,
      title,
    },
    mode: "dashboard",
    filters: [],
    component: "hreport",
    extension: "hr",
    resourceId,
  };
  if (replaceReportIdValue) {
    dispatch(changeTheReport({ reportInfo }));
    return;
  }
  return addItem({
    dispatch,
    reportInfo,
    compType: "dashboard-designer-component",
    gridItemsData,
    openThroughApi,
    gridItemId,
    userDefinedLayout,
    replaceReportIdValue,
  });
};
