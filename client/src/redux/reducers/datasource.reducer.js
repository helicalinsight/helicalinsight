export const menuData = [
  { id: "all", name: "All", path: "/all" },
  {
    id: "supported",
    name: "Supported",
    path: "/supported",
    categoryName: "Supported",
  },
  {
    id: "bigdata",
    name: "Bigdata",
    path: "/bigdata",
  },
  {
    id: "flatfiles",
    name: "Flatfiles",
    path: "/flatfiles",
  },
  { id: "rdbms", name: "RDBMS", path: "/rdbms" },
  {
    id: "nosql",
    name: "No SQL & Big Data",
    path: "/nosql",
    categoryName: "No SQL & Big Data",
  },
  { id: "advanced", name: "Advanced", path: "/advanced", categoryName: "" },
];

const intialState = {
  isSericeCall: false, // when backend serice is happening
  dsMode: {
    mode: "",
    driver: "",
    data: {},
  },
  menuData: menuData,
  dataSources: [],
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
  clickedDataSource: {}
};

const datasourceReducer = (state = intialState, action) => {
  switch (action.type) {
    case "RESET_FIELDS":
      return {
        ...intialState,
        dataSources: action.payload,
        dataSourceTypes: state.dataSourceTypes,
        dataSourceDriversList: state.dataSourceDriversList,
      };
    case "SET_DS_SERICE_CALL":
      return { ...state, isSericeCall: action.payload };
    case "SET_DS_MODE":
      return { ...state, dsMode: action.payload };
    case "SET_DATA_SOURCE_TYPES":
      return { ...state, dataSourceTypes: action.payload };
    case "SET_DATA_SOURCES":
      return { ...state, dataSources: action.payload };
    case "SET_DATA_SOURCE_DRIVERS_LIST":
      return { ...state, dataSourceDriversList: action.payload };
    case "SET_SELECTED_DRIVER_INFO":
      return { ...state, selectedDriverInfo: action.payload };
    case "SET_DRIVER_CATEGORY":
      return { ...state, driverCategory: action.payload };
    case "SET_FILE_BROWSER_FOLDER":
      return { ...state, fileBrowserFolder: action.payload };
    case "SET_REPORTS_DATA":
      return { ...state, reportsData: action.payload };
    case "SET_FLAT_FILES_UPLOAD_NAME":
      return { ...state, flatFileUploadName: action.payload };
    case "SET_VIEW_DATA":
      return { ...state, viewData: action.payload };
    case "SET_EDIT_DATA":
      return { ...state, editData: action.payload };
    case "SET_CLICKED_ACTIVE_FILE_DATA":
      return { ...state, clickedActiveDatabaseData: action.payload };
    case "SET_CLICKED_RECORD_DATA":
      return { ...state, clickedRecordData: action.payload };
    case "SET_IS_EDIT_CLICKED":
      return { ...state, isEditClicked: action.payload };
    case "SET_DATA_SOURCE_CONNECTION":
      return { ...state, isDatasourceConnectionSuccess: action.payload };
    case "SET_DATASOURCE_TEST_CONNECTION":
      return { ...state, isTestConnectionSuccess: action.payload };
    case "SET_CONNECTED_DATASOURCE_DATA":
      return { ...state, connectedDatasourceData: action.payload };
    case "SET_BUTTON_TYPE":
      return { ...state, buttonTypes: action.payload };
    case "SET_CLICKED_DATA_SOURCE":
      return { ...state, clickedDataSource: action.payload };
    default:
      return { ...state };
  }
};

export default datasourceReducer;
