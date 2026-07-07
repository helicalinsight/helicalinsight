import {
  // routesUrl,
  // roleTypes,
  // localApplicationSettings,
  defaultRoutes,
} from "../../app/constants";
import { v4 as uuidv4 } from "uuid";

import { handleHeader } from "../../components/hi-cube/helperMethods";
import { filterValues } from "../../components/hi-metadata/components/editor/security/validatedTable/validatedTable";
import { cloneDeep } from "lodash-es";
import { initialReferenceLineList, intialMarks } from "../../components/hi-reports/utils/constants";
import { designerSettingsConstants, gridSettingsConstants } from "../../components/hi-dashboard-designer/utils/config-dashboard-gridSettings";
import { hcrDSParameter, hcrDSQuery } from "../../components/hi-canned-reports/hcr-constants";
import { hcrTabInitialState } from "./hCR.reducer";
import types from "../../constants/metadata";
// import { getDevConstants } from "../../lib/hi-app-constants";

const adminIntialState = {
  isAdminTabsDataSet: false,
  users: {
    data: null,
    viewPage: 0,
    currentPage: 0,
    viewEntries: 10,
    pageSize: 1000,
    fetchMore: true,
    showMore: true,
    form: {
      name: "",
      slno: "",
    },
  },
  isFetched: {
    diskData: false,
    jvmData: false,
    tempData: false,
    cacheSize: false,
    cachedReports: false,
    cachedDataSources: false,
    productData: false,
    currentLogLevel: false,
    reportStats: false,
    dataSourceCount: false,
    metadataAdministration: false,
    metadataGeneration: false,
    metadataPreview: false,
    whatsNewContent: false,
  },
  diskData: {},
  jvmData: {},
  tempData: {},
  cacheSize: {},
  cachedReports: {},
  cachedDataSources: {},
  reportStats: {},
  dataSourceCount: {},
  currentLevel: "",
  logOptions: [],
  productData: {},
  reloadAppMessage: "",
  reloadValidMessage: "",
  reloadCacheMessage: "",
  userData: [],
  orgData: [],
  roleData: [],
  rolesList: [
    { id: 1, role: "ROLE_ADMIN" },
    { id: 2, role: "ROLE_USER" },
    { id: 3, role: "ROLE_VIEWER" },
    { id: 4, role: "ROLE_DOWNLOAD" },
    { id: 5, role: "ROLE_READ" },
  ],
  visibleDrawersUM: {
    addUser: false,
    addRole: false,
    addOrg: false,
    addProfile: false,
    editUser: false,
  },
  editUser: { type: "all", userId: 1 },
  profileId: 1,
  whatsNewContent: [],
  //system details
  osDetails: null,
  jvmThreadDetails: null,
  osTableExpand: false,
  jvmTableExpand: false,
  //Mangement Tab
  managementData: null,
  managementStaticData: null,
  managementAdvancedData: null,
  isDiceApiRunning: false,
  noSqlData: null,
  // --------------------- DICE-STORAGE --------------------
  diceStorage: {
    // enabled: false
    metadataDetails: {
      fileName: "",
      path: "",
    },
    tabName: "metadata",
    diceStorageTableData: { metadata: [], cube: [] },
    diceStorageFieldSearchText: "",
    diceStorageSearchedColumn: "",
    skeletonRowKeys: [],
    // isSkeletonLoading: false
  },
  //Plugins Tab
  pluginsData: null,
  //sheduling Tab
  schedulingList: null,
  // adfs Tab
  metadataAdministrationData: {},
  metadataGenerationData: {},
  metadataPreviewData: {},
  metadataGenerationFormValues: {},
  entityId: "",
  showMetadataPages: {
    metadataAdministration: true,
    metadataGeneration: false,
    metadataPreview: false,
  },
  // Recycle Bin
  recycleBinData: [],
};

const dashboardIntialState = {
  dashboardVariables: {},
  components: [],
  refreshReports: 0,
  refreshDashboard: 0,
};

const dashboardDesignerIntialState = {
  // tempValue: "abc",
  filterCounter: 0,
  parameterDrawerStatus: false,
  designerSettings: designerSettingsConstants,
  previewMode: false,
  designerMode: "create",
  dashboardVariables: {},
  dashboardUUID: "",
  variables: [],
  components: {},
  dashboardConfig: {
    id: "dashboard",
  },
  css: "",
  script: "",
  printOptions: {
    templateId: "Dashboard",
    title: "Dashboard",
  },
  componentSettings: {},
  toggleIframes: false,
  gridItemsData: [],
  layout: [],
  dashboardDrawerStatus: false,
  gridItemDrawerStatus: false,
  currentGroupId: "",
  groupId: "",
  gridItemId: "",
  drawerPositions: ["right", "bottom", "left", "top"],
  currentDrawerPosition: 0,
  gridSettings: gridSettingsConstants,
  filterItemsData: [],
  maximizedGridItem: {},
  maximizingStatus: false,
  gridIndex: 0,
  isLoading: false,
  reportId: "",
  applyDashboardFilters: null,
  isSaving: false,
  expandDesignerDrawers: false,
  hasUnsavedData: true,
  savedReportName: "",
  designerLayout: [],
  toggleToolsAreaShelf: true,
  openCompactFbBrower: false,
  replaceReportId: null,
};

const reportCEIntialState = {
  uuid: "",
  editing: false,
  typesDetails: null,
  dataSourceData: [],
  parametersData: [],
  reportData: [],
  dashboardLayoutData: {
    html: "",
    css: "",
  },
  activeEditorData: {
    datasourceId: "",
    dashboardId: "",
    paramterId: "",
    reportId: "",
    type: "",
    name: "",
  },
  activeTabs: {
    showDatasource: false,
    showDashboard: false,
    showParameter: false,
    showReport: false,
  },
};

const userActionsInitialState = {
  notificationDrawerStatus: false,
  notificationItemDrawer: false,
  notificationData: [],
  notificationItemId: null,
};

const appInitialState = {
  //  with  LOGIN  and  ROUTE REDUCERS.
  shouldBlockNavigation: false,
  value: true,
  sessionExpiry: "",
  isAuthenticated: /*process.env.NODE_ENV === "development" ? true :*/ false,
  isLogoutManually: false,
  hasError: false,
  // [ 'development', 'test' ].includes(process.env.NODE_ENV)
  // 	? localApplicationSettings(getDevConstants())
  // 	:
  showLicenseNotification: false,
  isLicenseRendered: false,
  applicationSettingsData: {
    userData: {
      user: {
        actualUserName: "",
        // organization: null,
        // organization: "Testingorg",
        name: "", // Only for development mode for reload issue
        roles: [],
        email: "", // Only for development mode for reload issue
      }, //  { actualUserName: "", email: "", organization: "", profile: [], roles: [], userName: "" }
      saml: {}, //  { enabled: boolean, logoutLink: "http...." }
      adminPaths: {}, //  { organisations: "http..", profiles: "http..", roles: "http..", services: "http..", users: "http.." }
    },
  },

  // role: process.env.NODE_ENV === "development" ? "ROLE_ADMIN" : "", // Only for development mode for reload issue
  // actualApplicationSettings: {},
  routes: defaultRoutes,
  activeRoute: "",
  isApplicationSettingsServiceCheck: false,
  nxtRoute: "",
  toggleSidebar: false,
  showNavbar: true,
  sessionExpiry: "",
  logType: "",
  isUrlAuthenticating: false,
  isSessionOver: false,
  viewModeInfo: null,
  editModeInfo: null,
  aboutToChangeRoute: null,
  viewerEmailModalVisible: false,
  viewerScheduleModalVisible: false,
  keysPressed: [],
  currentSCLocation: "",
};
const hcrInitialState = {
  hcrTabData: {
    activeKey: null,
    panes: []
  },
  hCROldConfigurations: {},
  isHcrLoaded: false,
  hcrMode: 'create',
  hasUnsavedData: true,
  hcrExportPropertiesData: {}
  // activeConfig: {isSaved: true},
};

const cubeInitialState = {
  hasUnsavedData: true,
  metadataDetails: {},
  cubeCurrentState: "initial",
  cubeForEdit: {},
  // headerData: handleHeader.initial,
  fieldSearchText: "",
  cubeSearchedColumn: "",
  cubeFieldsDataBackup: {},
  cubeFieldsData: {
    id: "",
    domainName: "",
    cubeDescription: "",
    children: [],
    // key: '',
    hierarchyData: { isHierarchyPresent: false, hierarchyList: [] },
  },
  cubeVisibleIndeterminate: false,
  isCubeVisibleCheck: true,
  cubeInitialList: [],
  cubeMode: "create",
  // cubeTableDeleteKeys: [],
  isCubeTableModeNormal: true,
  metadataTablesData: {},
  cubeDataAfterSave: {},
};
const agentInitialState = {
  hasUnsavedData: true,
  metadataDetails: {},
  metadataTablesData: {},
  agentMode: "create",
  agentDataAfterSave: {},
};

const getInitialIBChatState = () => {
  let id = uuidv4();
  return {
    botStatus: false,
    activeChatID: id,
    activePreviewID: null,
    loadedChatResponses: {},
    loadedChatResponseSources: {},
    chats: [
      {
        chatID: id,
        messageList: []
      }
    ],
    previews: [],
    recommendations: [],
    recommendationsVisible: false,
    loadingRecommendation: false,
    inputValue: ''
  }
}

const instantBIInitialState = {
  activeReportId: null,
  reports: [],
  searchValue: "",
  activeInstantBIReport: {},
  isSaving: false,
  loading: false,
  hasUnsavedData: true,
  layout: {
    metadataShelf: false,
    previewShelf: false,
    chatShelf: true,
  },
};

export const getIBInitialReportState = ({ active }) => {
  return {
    id: uuidv4(),
    mode: "create",
    active,
    metadata: {},
    reportInfo: {
      location: "",
      uuid: "",
      reportName: "Untitled 1",
    },
    ...getInitialIBChatState()
  }
}

const metadataInitialState = {
  conditionAndFilterValue: {
    groovy: {
      condition: {
        value: '',
      },
      filter: {
        value: ''
      }
    },
    conditionIf: {
      condition: {
        value: ""
      },
      filter: {
        value: ''
      }
    }
  },
  viewName: '',
  activeViewInfo: {},
  metadataSectionExpandedRows: [],
  datasourceExpandedRowKeys: [],
  isGetSecurityCallDone: false,
  isShowJoinModal: false,
  isViewFetching: {},
  dataFetchedFor: {
    getDatasource: false,
    joins: false,
    viewSessionVariables: false,
  },
  loadingStatus: {},
  joinsLoadingStatus: "",
  serviceErrorStatus: {}, // keys of services which failed
  fetchedDSInfo: {},
  listDataSource: [],
  supportedDataSourceTypes: [],
  allDataSourceTypes: [],
  metaDataSourceList: [],
  driversList: [],
  allDataSources: [],
  dataSourceTypes: [],
  datasourceListToRender: [],
  workFlow: {
    dataList: [],
  },
  dataSource: [],
  tables: {},
  views: [],
  activeView: false,
  categories: {},
  activeEditorTab: "info",
  dataSourcesAddedToMetadata: [],
  changeDSList: {}, // datasource list fetched when we are changing datasource connection for a connection
  changedTables: [],
  changedColumns: [],
  removedTables: [],
  removedColumns: [],
  removedDataSources: [],
  duplicateColumnList: [],
  duplicateTableList: [],
  unsavedViews: [],
  saveDetails: false,
  savedTableIds: [],
  savedColumnIds: [],
  joins: [],
  // mode: 'edit',
  mode: "create",
  allTablesKeys: [],
  selectedTableKeys: [],
  metadataName: "Metadata_1",
  activeDataSource: false, // this is to open metadata from datasource-page with specific connection
  metadataToEdit: false, // metadata info when editing metadata from Home filebrowser
  isSavingInProgress: false,
  editViewsTempData: {},
  inititalStateFromJest: false,
  timeStamp: new Date().getTime(),
  initialEditResponse: false,
  editorFullView: false,
  selectedTableOrColumnKey: {},
  expressionObj: [],
  securityConstants: {},
  edit: false,
  isAllowServiceCall: true,
  isValidatedTableShow: false,
  isAfterSaveMode: false,
  securityTableData: [],
  addOneMoreSecurity: false,
  viewSessionVariables: false,
  textEditingObj: {},
  selectedJoinNameData: {},
  filterbyData: cloneDeep(filterValues),
  isFirstRender: true,
  securityFormData: {},
  accessType: "deny",
  entityNames: [],
  executionType: "conditionIf",
  expressionName: '',
  expressionType: "global",
  isApplyDisabled: true,
  isInfoShow: true,
  securityKeysChecked: [],
  hasUnsavedData: true,
  getSecurityTableData: { tables: [], columns: [] },
  doResetFormData: true,
  tablesMergeType: false,
  activeDsInfoId: null,
  setMetadataLoading: {},
  outsideClicked: false,
};
const intialScripts = [
  {
    id: `pre-execution`,
    value: "",
    title: "Pre Execution",
  },
  {
    id: `pre-fetch`,
    value: "",
    title: "Pre Fetch",
  },
  {
    id: `post-fetch`,
    value: "",
    title: "Post Fetch",
  },
  {
    id: `post-execution`,
    value: "",
    title: "Post Execution",
  },
];
const intialOptions = {
  limitBy: 1000,
  sample: "sample",
  prependTableNameToAlias: false,
};
const intialAnalytics = [
  {
    value: false,
    key: "subTotals",
    label: "Row Sub Totals",
  },
  // {
  //   value: false,
  //   key: "columnSubTotals",
  //   label: "Column Sub Totals",
  // },
  // {
  //   value: false,
  //   key: "rowGrandTotals",
  //   label: "Row Grand Totals",
  // },
  // {
  //   value: false,
  //   key: "columnGrandTotals",
  //   label: "Column Grand Totals",
  // }, 
];

const propertyPaneInitialState = {
  showMore: false
}

export const initialCustomChart = {
  selected: false,
  drawer: false,
  code: "",
  applied: false
}

export const MEASURE_NAME = "frontend_custom_measure_name";
export const MEASURE_VALUE = "frontend_custom_measure_value";

export const initialMeasureFields = [
  {
    column: MEASURE_NAME,
    alias: "Measure Name",
    genre: types.CUSTOM_FORMULA,
    custom_frontend_field: true,
    type: { dataType: 'text' }
  },
  {
    column: MEASURE_VALUE,
    alias: "Measure Value",
    genre: types.CUSTOM_FORMULA,
    custom_frontend_field: true,
    type: { dataType: 'numeric' }
  },
]

export const measuresInitialState = {
  enable: false,
  fields: initialMeasureFields
}

export const getIntialReportState = ({ active }) => {
  return {
    id: uuidv4(),
    mode: "create",
    active,
    metadata: null,
    metadataLoading: false,
    hreportLoading: false,
    functions: {},
    databaseFunctions: {},
    fields: [],
    filters: [],
    defaultValueDisplayMap: {},
    editingField: null,
    marksList: [intialMarks],
    activeMark: intialMarks.id,
    activeTool: "1",
    scripts: intialScripts,
    selectedScript: intialScripts[0].id,
    styles: "",
    stylesId: "",
    savedStyles: "",
    sqlString: "",
    options: intialOptions,
    interactiveMode: false,
    drillDown: false,
    drillThrough: false,
    drillDownList: [],
    currentDrillDown: "",
    drillThroughList: [],
    enableDrillthroughReportLink: false,
    toolbarConfig: {
      selectable: false,
    },
    selectedType: "",
    reportData: {},
    customStyles: "",
    customScripts: [],
    analytics: intialAnalytics,
    properties: {
      title: {
        show: false,
        value: "",
        padding: 0,
        fontSize: 32,
        fontColor: { a: 1, b: 0, g: 0, r: 0 },
        alignment: "center",
        position: "top",
      },
      subTitle: {
        show: false,
        value: "",
        padding: 0,
        fontSize: 24,
        fontColor: { a: 1, b: 0, g: 0, r: 0 },
        alignment: "center",
        position: "top",
      },
      format: {
        formatFields: [],
        formatDatatype: "",
        activeFieldId: "",
        showAll: false,
      },
      axisRange: {
        fields: [],
        activeDatatype: "",
        activeId: "",
        gridLines: [],
        synchronize: false,
        showAxisName: false,
        showGridChartAxisName: true
      },
      cache: {
        isCacheEnabled: false,
        interval: "00:00:01",
      },
      card: {
        showTitle: true,
        title: "",
        prefixType: "selectIcon",
        suffixType: "selectIcon",
        prefix: "",
        suffix: "",
        prefixColor: { a: 1, b: 0, g: 0, r: 0 },
        suffixColor: { a: 1, b: 0, g: 0, r: 0 },
        isTrend: false,
        displayTrend: ['trend', 'value'],
        trendPagination: 2,
        trendPrefix: 'vs.',
        trendPrefixPosition: 'center',
        // group: ['autoAggregate'],
        cardView: 'entireView',
        cardWidth: 0,
        cardHeight: 0,
      },
      bar: {
        barType: "stacked",
        maxBarWidth: 180,
        autoFit: true
      },
      radial: {
        showRadial: false,
        showRadialLabel: false,
        showRadialValue: false,
        showDoughnutTitle: false,
        doughnutTitle: "",
        doughnutPrefixType: "selectIcon",
        doughnutSuffixType: "selectIcon",
        doughnutPrefix: "",
        doughnutSuffix: "",
        chartType: "Arc"
      },
      legend: {
        legendPosition: "right",
      },
      formatColor: {
        defaultColor: { r: 84, g: 108, b: 230, a: 1 },
        showAll: false,
        dataColors: [],
        formatColorStyle: "",
        formatColorField: "",
        minimum: { r: 183, g: 192, b: 232, a: 1 },
        maximum: { r: 84, g: 108, b: 230, a: 1 },
        backgroundColor: false,
        enableSteps: false,
        steps: null,
        enableReverse: false,
        minValue: 0,
        maxValue: 0,
        centerValue: 0,
        enableAdvanceSteps: false
      },
      labels: {
        rotateLabels: false,
        // labelsColor: { r: 255, g: 255, b: 255, a: 1 },
        labelsColor: {},
        position: 'middle',
        offsetX: 0,
        offsetY: 0,
        fontSize: 12
      },
      crosstab: {
        showGrandTotals: false,
        showRowGrandTotals: false,
        showColumnGrandTotals: false,
        showSubTotals: false,
        showRowSubTotals: false,
        showColumnSubTotals: false,
        grandTotalsPosition: "Bottom",
        subTotalsPosition: "Auto",
        crosstabCollapse: "None"
      },
      table: {
        recordsPerPage: 10,
        horizontalScroll: false,
        fetchAllRecords: false,
        showHiddenFields: false,
        disableDefaultOptions: false,
      },
      map: {
        mapStyle: 'normal',
        longitude: 0,
        latitude: 0,
        zoom: 2,
        // mapType:'mapbox'
      },
      line: {
        smooth: false
      },
      progress: {
        isStatic: true,
        target: 0,
        chartType: 'ring',
        showTargetAndValue: false,
        statisticType: 'percentage',
        '20%': { r: 84, g: 108, b: 230, a: 1 },
        '40%': { r: 84, g: 108, b: 230, a: 1 },
        '60%': { r: 84, g: 108, b: 230, a: 1 },
        '80%': { r: 84, g: 108, b: 230, a: 1 },
        '100%': { r: 84, g: 108, b: 230, a: 1 },
      },
      radar: {
        radarChartType: 'area'
      },
      shape: {
        mapDefaultShape: 'circle',
        mapShowAllShapes: false
      },
      chartTheme: {
        colorPalette: [],
        customColors: [],
        enableCustomColors: false,
        paletteName: ''
      },
      charts: {
        enablePagination: false,
        starts: 0,
        ends: 10
      },
      relationChart: {
        chartType: 'treemap',
        // sourceField: "",
        // targetField: "",
        // weightField: ""
      },
      canvas: {
        width: 0,
        height: 0,
        view: 'standard',
        align: 'center'
      },
      filter: {
        filterHeaderBGColor: { r: 51, g: 122, b: 183, a: 1 },
        filterHeaderTitleColor: { r: 255, g: 255, b: 255, a: 1 },
        filterBGColor: { r: 255, g: 255, b: 255, a: 1 },
        filterListItemColor: { r: 55, g: 55, b: 55, a: 1 },
        // filterListItemTickColor: { r: 24, g: 144, b: 255, a: 1 },
        filterListItemFontSize: 14,
        filterHeaderFontSize: 12
      },
      tooltip: {
        showTooltip: true,
        tooltipTemplate: "",
        isTemplateEdited: false,
        enableTemplate: false
      }
    },
    reportInfo: {
      location: "",
      uuid: "",
      reportName: "Untitled 1",
    },
    cellMenuData: null,
    showHiddenColumns: false,
    showHiddenRows: false,
    geoJsonData: {},
    isAborted: false,
    referenceLineList: [initialReferenceLineList],
    customChart: initialCustomChart,
    tableRecordsPerPage: 10,
    measures: measuresInitialState,
    reportModal: false,
    isDrillthroughActive: false,
    chartColorPalette: {
      "Custom Colors": {}
    },
    showAllVisualizations: true,
    tableFilters: {}
  };
};

const initialStates = {
  hcrInitialState,
  dashboardDesignerIntialState,
  userActionsInitialState,
  appInitialState,
  adminIntialState,
  dashboardIntialState,
  reportCEIntialState,
  cubeInitialState,
  instantBIInitialState,
  metadataInitialState,
  propertyPaneInitialState,
  agentInitialState
};

export default initialStates;
