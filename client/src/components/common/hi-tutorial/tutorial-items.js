const navbarItems = [
  {
    key: "hi-navbar-home",
    title: "Home",
    description: "Click on Home button to access home page of the user.",
  },
  {
    key: "hi-navbar-data-sources",
    title: "Data Sources",
    description: "Use this for connecting to datasource and editing it.",
  },
  {
    key: "hi-navbar-metadata",
    title: "Meta Data",
    description:
      "Create metadata from your database with required rows, columns, joins, views and row level / column level data security. This metadata will be used for various reporting purposes.",
  },
  {
    key: "hi-navbar-reports",
    title: "Reports",
    description:
      "Report designer Adhoc interface allows you to drag drop columns from various tables, apply filters, customize the look and feel and thus create entire reports on the fly.",
  },
  {
    key: "hi-navbar-dashboard-designer",
    title: "Dashboard Designer",
    description:
      "Dashboard designer you can simply drag the reports that are already created into a canvas and create your own dashboard with customisations. Also you can select multiple reports to form group.",
  },
  // {
  //   key: "hi-navbar-reports-ce",
  //   title: "Reports CE",
  //   description:
  //     "By using Report CE you can create efwce reports with your datasources,parameters and visulizations",
  // },
  {
    key: "hi-navbar-canned-reports",
    title: "Canned Reports",
    description:
      "By using Canned Reports you can create reports with your datasources, parameters.",
  },
  {
    key: "hi-navbar-instant-bi",
    title: "Instant",
    description:
      "Instant BI lets you ask natural language questions about your data and generate visualizations using AI-powered agents.",
  },
];

const adminItems = [
  {
    key: "hi-admin-sidebar",
    title: "Discover",
    description: "Direct tutorial links and usage guides.",
    moduleKey: "admin",
  },
  {
    key: "hi-file-browser",
    title: "File Browser",
    description:
      "Click on Filebrowser to perform various operations like open, share, edit, delete etc on various files like reports, dashboards, metadata, export, save result etc.",
    moduleKey: "admin",
  },
  {
    key: "hi-global-search",
    title: "Search",
    description: "Search any resource in repository.",
    moduleKey: "admin",
  },
  ...navbarItems.map((item) => ({ ...item, moduleKey: "admin" })),
  {
    key: "hi-notifications",
    title: "Notifications",
    description: "Click on notification icon to check Notifications.",
    moduleKey: "admin",
  },
  {
    key: "hi-tutorial-info",
    title: "Tutorial",
    description:
      "Click on tutorial icon to get quick overview of the current page you are present.",
    moduleKey: "admin",
  },
  {
    key: "hi-help",
    title: "Help",
    description: "Link for training documents, videos and technical guides.",
    moduleKey: "admin",
  },
  {
    key: "hi-overview",
    title: "Overview",
    description: "Overview of various administrative details.",
    moduleKey: "admin",
  },
  {
    key: "hi-recycle-bin",
    title: "Recycle Bin",
    description: "A recycle bin features allows users to restore deleted files, folder, users or any other resources.",
    moduleKey: "admin",
  },
  {
    key: "hi-user-management",
    title: "User Management",
    description:
      "Add / delete/ edit users for various kind of organizations. Admin can add / edit users for only their own organization.",
    moduleKey: "admin",
  },
  {
    key: "hi-system-details",
    title: "System Details",
    description: "Various system, java and OS related details can be seen by clicking here.",
    moduleKey: "admin",
  },
  {
    key: "hi-management",
    title: "Management",
    description:
      "The management of the application allows you to control and maintain the overall functionality and performance of the application. This includes tasks such as managing various storages, and import/export.",
    moduleKey: "admin",
  },
  {
    key: "hi-Schedule",
    title: "Schedule",
    description:
      "The Schedule section allows you to manage and view your schedule in an easy and intuitive way.",
    moduleKey: "admin",
  },
  {
    key: "hi-plugins",
    title: "Plugins",
    description:
      "The Plugin section allows you to add and manage additional functionality to your system. You can install, activate, deactivate, and delete plugins as needed.",
    moduleKey: "admin",
  },
  {
    key: "hi-disk-space",
    title: "Disk Space",
    description: "View and refresh the availaible and free disk space.",
    moduleKey: "admin",
  },
  {
    key: "hi-jvm-memory",
    title: "JVM Memory",
    description: "View and refresh the details of used, free and maximum JVM memory.",
    moduleKey: "admin",
  },
  {
    key: "hi-temp-directory",
    title: "Temp Directory",
    description: "View the details and delete temporary directory.",
    moduleKey: "admin",
  },
  {
    key: "hi-cache-card",
    title: "Cache",
    description: "Refresh and delete cache memory.",
    moduleKey: "admin",
  },
  {
    key: "hi-cached-reports",
    title: "Cached Reports",
    description: "View and delete cached reports.",
    moduleKey: "admin",
  },
  {
    key: "hi-data-sources-cached",
    title: "DataSources Cached",
    description: "View and delete the cached datasources.",
    moduleKey: "admin",
  },
  {
    key: "hi-license-details",
    title: "License Details",
    description: "Details of the Helical Insight license currently deployed.",
    moduleKey: "admin",
  },
  {
    key: "hi-logger-settings",
    title: "Logger Settings",
    description: "View / Change the logger settings from any of the 8 options.",
    moduleKey: "admin",
  },
  {
    key: "hi-reload-configurations",
    title: "Reload Configurations",
    description: "Useful for making configuration changes in respective XML files.",
    moduleKey: "admin",
  },
  
];
const adminWithOrgItems = [
  {
    key: "hi-admin-sidebar",
    title: "Discover",
    description: "Direct tutorial links and usage guides.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-file-browser",
    title: "File Browser",
    description:
      "Click on Filebrowser to perform various operations like open, share, edit, delete etc on various files like reports, dashboards, metadata, export, save result etc.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-global-search",
    title: "Search",
    description: "Search any resource in repository.",
    moduleKey: "adminWithOrg",
  },
  ...navbarItems.map((item) => ({ ...item, moduleKey: "adminWithOrg" })),
  {
    key: "hi-notifications",
    title: "Notifications",
    description: "Click on notification icon to check Notifications.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-tutorial-info",
    title: "Tutorial",
    description:
      "Click on tutorial icon to get quick overview of the current page you are present.",
    moduleKey: "admin",
  },
  {
    key: "hi-help",
    title: "Help",
    description: "Link for training documents, videos and technical guides.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-recycle-bin",
    title: "Recycle Bin",
    description: "A recycle bin features allows users to restore deleted files, folder, users or any other resources.",
    moduleKey: "admin",
  },
  {
    key: "hi-user-management",
    title: "User Management",
    description:
      "Add / delete/ edit users for various kind of organizations. Admin can add / edit users for only their own organization.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-management",
    title: "Management",
    description:
      "The management of the application allows you to control and maintain the overall functionality and performance of the application. This includes tasks such as managing various storages, and import/export.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-Schedule",
    title: "Schedule",
    description:
      "The Schedule section allows you to manage and view your schedule in an easy and intuitive way.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-plugins",
    title: "Plugins",
    description:
      "The Plugin section allows you to add and manage additional functionality to your system. You can install, activate, deactivate, and delete plugins as needed.",
    moduleKey: "adminWithOrg",
  },
  {
    key: "hi-license-details",
    title: "License Details",
    description: "Details of the Helical Insight license currently deployed.",
    moduleKey: "adminWithOrg",
  },
];
const roleUserItems = [
  {
    key: "hi-admin-sidebar",
    title: "Discover",
    description: "Direct tutorial links and usage guides.",
    moduleKey: "roleUser",
  },
  {
    key: "hi-file-browser",
    title: "File Browser",
    description:
      "Click on Filebrowser to perform various operations like open, share, edit, delete etc on various files like reports, dashboards, metadata, export, save result etc.",
    moduleKey: "roleUser",
  },
  {
    key: "hi-global-search",
    title: "Search",
    description: "Search any resource in repository",
    moduleKey: "roleUser",
  },
  ...navbarItems
    .filter((item) => item.key !== "hi-navbar-data-sources" && item.key !== "hi-navbar-metadata")
    .map((item) => ({ ...item, moduleKey: "roleUser" })),
  {
    key: "hi-notifications",
    title: "Notifications",
    description: "Click on notification icon to check Notifications.",
    moduleKey: "roleUser",
  },
  {
    key: "hi-tutorial-info",
    title: "Tutorial",
    description:
      "Click on tutorial icon to get quick overview of the current page you are present.",
    moduleKey: "admin",
  },
  {
    key: "hi-help",
    title: "Help",
    description: "Link for training documents, videos and technical guides.",
    moduleKey: "roleUser",
  },
  {
    key: "hi-user",
    title: "User",
    description: "View the role assigned, profile assigned and the name of the loggedin user.",
    moduleKey: "roleUser",
  },
  {
    key: "hi-stats",
    title: "Stats",
    description:
      "View the number of reports dashboards and data sources the current loggedin user has access to.",
    moduleKey: "roleUser",
  },
];

const datasourceItems = [
  {
    key: "hi-datasource",
    title: "Create/Edit/Delete/Share Data Sources",
    description: "Click on available database icon to Create/Edit/Delete/Share Data Sources.",
    moduleKey: "datasource",
  },
];

const userModuleItems = [
  {
    key: "hr-report-viewer",
    title: "Report Viewer Area",
    description: "Report viewing area.",
    moduleKey: "reportviewer",
  },
];

const designerItems = [
  {
    key: "hi-designer-save",
    title: "Save",
    description: "Save or save as the created dashboard in folder of your choice.",
    moduleKey: "designer",
  },
  // {
  //   key: "hi-designer-preview",
  //   title: "Preview",
  //   description: "Preview the dashboard based on the changes being made.",
  //   moduleKey: "designer",
  // },
  {
    key: "hi-designer-refresh",
    title: "Refresh",
    description:
      "Refresh the cache memory of the dashboard to see real time data from the database.",
    moduleKey: "designer",
  },
  {
    key: "hi-designer-presentation",
    title: "Presentation Mode",
    description: "View the dashboard in full screen using presentation mode.",
    moduleKey: "designer",
  },
  {
    key: "hi-designer-tool-shelf",
    title: "Toggle Tool Shelf",
    description: "Hide/Unhide Tool Shelf.",
    moduleKey: "designer",
  },
  {
    key: "hi-designer-dashboard-settings",
    title: "Dashboard Settings",
    description: "Manage your dashboard layout, add header, text, image and other settings.",
    moduleKey: "designer",
  },
  {
    key: "hi-designer-reports",
    title: "Reports",
    description: "See list of Reports within folders which can be dragged dropped into the canvas for dashboard creation purposes.",
    moduleKey: "designer",
  },
  {
    key: "hi-designer-canvas",
    title: "Canvas",
    description:
      "Drag and drop Reports and select multiple reports to group them , filters and other components into this canvas and see the dashboard here.",
    moduleKey: "designer",
  },
];

const metadataItems = [
  {
    key: "hi-metadata-create",
    title: "Create",
    description:
      "Create metadata with only selected tables, columns from saved datasource. Additionally you could also create / edit joins, create views and apply data security conditions.",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-edit",
    title: "Edit",
    description: "Edit the already created metadata.",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-ds-list",
    title: "Connections",
    description: "List of all the datasource connections available",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-metadata-section",
    title: "Metadata Section",
    description:
      "List of all the tables added to metadata. Drag and drop tables here or right click on table and 'Add to Metadata'",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-editor-info",
    title: "Info",
    description: "Information about the connections whose tables are added to metadata",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-editor-joins",
    title: "Joins",
    description: "Joins between the tables added to metadata. Can add/delete/modify joins here",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-editor-views",
    title: "Views",
    description:
      "Views can be used for creating reports with complex calculations and will appear in metadata as seperate table. Dynamic queries can be used for changing SQL on runtime based on various conditions like stored procedures.",
    moduleKey: "metadata",
  },
  {
    key: "hi-metadata-editor-security",
    title: "Security",
    description:
      "Implement row level data security based on loggedin users credentials. Implement row level, column level, table level data security.",
    moduleKey: "metadata",
  },
];

const helicalReportItems = [
  {
    key: "hr-metadata",
    title: "Connect to metadata",
    description:
      "Click on this and select the metadata from filebrowser which will be used for reporting purpose.",
    moduleKey: "hreport",
  },
  {
    key: "hi-file-browser",
    title: "File Browser",
    description:
      "Click on this and select the metadata from File Browser which will be used for reporting purpose. In addition, the File Browser also provides functionality to view a list of existing saved reports, which can be opened in edit mode.",
    moduleKey: "hreport",
  },
  {
    key: "hr-generate-report",
    title: "Generate Report",
    description: "Click on this to generate report.",
    moduleKey: "hreport",
  },
  {
    key: "hr-save",
    title: "Save",
    description: "Save or save as the report.",
    moduleKey: "hreport",
  },
  {
    key: "hr-export",
    title: "Export",
    description: "Export the report/data in excel and CSV format.",
    moduleKey: "hreport",
  },
  {
    key: "hr-cache-refresh",
    title: "Cache",
    description: "Refresh cache to see the report for the latest data from database.",
    moduleKey: "hreport",
  },
  {
    key: "hr-report-share",
    title: "Share",
    description: "Share the saved report with other users.",
    moduleKey: "hreport",
  },
  {
    key: "hr-layout",
    title: "Layout",
    description: "Enable / disable the components to be visible on the adhoc interface.",
    moduleKey: "hreport",
  },
  {
    key: "hr-preview",
    title: "Preview",
    description: "Preview the created report.",
    moduleKey: "hreport",
  },
  {
    key: "hr-presentation",
    title: "Presentation Mode",
    description: "View the report in full screen presentation mode.",
    moduleKey: "hreport",
  },
  {
    key: "hr-viz",
    title: "Visualization",
    description:
      "Created report can be seen in any of the format like tabular report, cross tabular report, axis or non axis chart, advanced chart, map etc.",
    moduleKey: "hreport",
  },
  // {
  //   key: "hr-filters",
  //   title: "Filters",
  //   description: "Add single selecet, multiselect and other kind of filters.",
  //   moduleKey: "hreport",
  // },
  // {
  //   key: "hr-editor",
  //   title: "Editor",
  //   description:
  //     "View SQLquery getting generated, add javascript HTML and CSS code for customizing the chart.",
  //   moduleKey: "hreport",
  // },
  // {
  //   key: "hr-settings",
  //   title: "Settings",
  //   description:
  //     "Select the numebr of entries to show. Also allows you to change the metadata to some other file.",
  //   moduleKey: "hreport",
  // },
  // {
  //   key: "hr-customization",
  //   title: "Customization",
  //   description:
  //     "Apply UI driven customization, drill down and other scripts to the created tabular report, crosstabular report or chart.",
  //   moduleKey: "hreport",
  // },
  {
    key: "hi-new-tab",
    title: "New Tab",
    description: "Click on plus button to add new tab .",
    moduleKey: "hreport",
  },
  {
    key: "hr-columns-shelf",
    title: "Columns Shelf",
    description: "Add fields to columns shelf to generate Report.",
    moduleKey: "hreport",
  },
  {
    key: "hr-rows-shelf",
    title: "Rows Shelf",
    description: "Add fields to Rows shelf to generate Report.",
    moduleKey: "hreport",
  },
  {
    key: "hr-editing-area",
    title: "Report Editing Area",
    description: "Report rendering area.",
    moduleKey: "hreport",
  },
];

const instantBIItems = [
  {
    key: "hi-instant-bi-save",
    title: "Save",
    description: "Save or save as your Instant BI report in a folder of your choice.",
    moduleKey: "instantBI",
  },
  {
    key: "hi-instant-bi-open-instant",
    title: "Open Instant (Edit)",
    description:
      "Open an existing Instant BI report from the file browser to continue editing.",
    moduleKey: "instantBI",
  },
  {
    key: "hi-instant-bi-metadata",
    title: "Connect Agent",
    description:
      "Select an agent from the file browser to connect your data source. The agent provides the metadata context for AI-powered analysis.",
    moduleKey: "instantBI",
  },
  {
    key: "hi-new-tab",
    title: "New Tab",
    description: "Click the plus button to add a new Instant BI report tab. Maximum 4 tabs can be created at once",
    moduleKey: "instantBI",
  },
  {
    key: "hi-instant-bi-editor-pane",
    title: "Input Pane",
    description:
      "Type your natural language questions about your data here. Ask anything and the connected agent will analyze your data and generate insights.",
    moduleKey: "instantBI",
  },
  {
    key: "hi-instant-bi-agent-icon",
    title: "Agent",
    description:
      "Click the agent icon to connect or view your connected agent. An agent must be connected before you can send queries and receive AI-powered insights.",
    moduleKey: "instantBI",
  },
  {
    key: "hi-instant-bi-send-query",
    title: "Send Query",
    description:
      "Click the send icon or press Enter to submit your query after typing. Use Shift+Enter or Ctrl+Enter to add a new line in the editor.",
    moduleKey: "instantBI",
  },
];

const hcrTutorialItems = [
  {
    key: "hcr-tabs",
    title: "Tabs",
    description:
      "Create/Edit multiple reports at once. Click on + button to create new Canned Report. Maximum 4 tabs can be created at once",
    moduleKey: "hcr",
  },
  // {
  //   key: "hcr-homePage",
  //   title: "Home Page",
  //   description:
  //     "Click to open home page which contains tutorial image.",
  //   moduleKey: "hcr",
  // },
  {
    key: "hcr-preview",
    title: "Preview",
    description:
      "User can view the designed report using preview. User can change the parameter values and export in different formats.",
    moduleKey: "hcr",
  },
  {
    key: "hcr-datasourcePane-propertyPane",
    title: "Datasource and Property panes",
    description:
      <div>
        <h5>Datasource pane</h5>
        <p>User can select available datasource queries from drop down. Listed fields and calculations/Parameters can used to drag to canvas.</p>
        <break />
        <h5>Property pane</h5>
        <p>User can change the properties of canvas or respective selected component.</p>
      </div>,
    moduleKey: "hcr",
  },
  {
    key: "hcr-canvasArea",
    title: "Canvas area",
    description:
      "User can design the canned report by drag and drop and resize the components. Context menu will be available based on selected component.",
    moduleKey: "hcr",
  },
  // {
  //   key: "hcr-propertyPane",
  //   title: "Property pane",
  //   description:
  //     "User can change the properties of canvas or respective selected component",
  //   moduleKey: "hcr",
  // },
  {
    key: "hcr-datasource",
    title: "Datasource",
    description:
      "User can manage datasource connections, datasets and parameters which can be used for report creation.",
    moduleKey: "hcr",
  },
  {
    key: "hcr-reports",
    title: "Reports",
    description:
      "List of canned reports which can be used for report editing purposes.",
    moduleKey: "hcr",
  }
]

const tutorialItems = {
  admin: adminItems,
  roleUser: roleUserItems,
  adminWithOrg: adminWithOrgItems,
  datasource: datasourceItems,
  designer: designerItems,
  metadata: metadataItems,
  hreport: helicalReportItems,
  reportviewer: userModuleItems,
  hcr: hcrTutorialItems,
  instantBI: instantBIItems,
};

export default tutorialItems;
