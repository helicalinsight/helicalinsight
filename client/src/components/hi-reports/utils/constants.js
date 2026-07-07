import { v4 as uuidv4 } from "uuid";

export const dateTypes = ["dateTime", "date", "time"]
export const dateFormats = {"dateTime":"YYYY-MM-DD HH:MM:SS","date":"YYYY-MM-DD"}

export const dateFunctions = {
    "dateTime": [
        { label: "Date", part: "date", key: "sql.typeConversion.todate", "returns": "date", "parameters": [{ name: "column" }] },
        { label: "Time", part: "time", key: "sql.typeConversion.totime", "returns": "time", "parameters": [{ name: "column" }] },
        { label: "Years", part: "year", key: "sql.dateTime.year", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Quarters", part: "quarter", key: "sql.dateTime.quarter", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Months", part: "month", key: "sql.dateTime.month", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Days", part: "day", key: "sql.dateTime.day", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Hours", part: "hour", key: "sql.dateTime.hour", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Minutes", part: "minute", key: "sql.dateTime.minute", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Seconds", part: "second", key: "sql.dateTime.second", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
    ],
    "date": [
        { label: "Years", part: "year", key: "sql.dateTime.year", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Quarters", part: "quarter", key: "sql.dateTime.quarter", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Months", part: "month", key: "sql.dateTime.month", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Days", part: "day", key: "sql.dateTime.day", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
    ],
    "time": [
        { label: "Hours", part: "hour", key: "sql.dateTime.hour", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Minutes", part: "minute", key: "sql.dateTime.minute", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Seconds", part: "second", key: "sql.dateTime.second", "returns": "numeric", "parameters": [{ name: "datetime" }] },
        { label: "Individual", part: "individual", key: "individual", "parameters": [{ name: "datetime" }] }
    ]
}

export const markTypes = ["color","label","shape","size","tooltip","detail"]
export const referenceLineMarkTypes = ["value"] // ["value", "label", "tooltip"]
export const initialReferenceLineList = {
    display: "All",
    id: uuidv4(),
    referenceType: "Line",
    value: "",
    enabled:false,
    isStatic:true,
}
export const referenceLineAll = "All"
export const intialMarks = {
    value: "_all_",
    id: uuidv4(),
    subVizType: "",
    color: {
        fields: []
    },
    size: {
        fields: []
    },
    label: {
        fields: []
    },
    tooltip: {
        fields: []
    },
    shape: {
        fields: []
    },
    detail: {
        fields: []
    }
}

export const preExecutionVars = [
    {
        group:"Metadata",
        vars:[
            {key:"metadata",desc:"returns object,it contains metadata details",mutable:false,showInInfo:true},
            {key:"metadata_file",desc:"returns object,it stores metadata location",mutable:true,showInInfo:true,
                example:`metadataFile.location = "test"; \n
                        metadataFile.metadataFileName = "test";`
            },
            {key:"database",desc:"returns string,it returns the database name",mutable:true,showInInfo:true},
        ]
    },
    {
        group:"Fields",
        vars:[
            {key:"fields",desc:"returns array,it lists fields with all the details from column/row shelf ",mutable:false,showInInfo:true},
            {key:"rows",desc:"returns array,it lists fields from row shelf",mutable:false,showInInfo:true},
            {key:"columns",desc:"returns array,it lists fields from column shelf",mutable:false,showInInfo:true},
        ]
    },
    {
        group:"Tools",
        vars:[
            {key:"filters",desc:"returns array,it list downs filters",mutable:false,showInInfo:true},
            {key:"mark_fields",desc:"returns array,it lists fields which are present in marks",mutable:false,showInInfo:true},
            {key:"marks",desc:"returns array,it lists fields with all the details which are present in marks",mutable:false,showInInfo:true},
            {
                key:"visualisation",desc:"returns string,it returns current visualisation of the report",
                mutable:true,showInInfo:true,
                example:`visualisationType = "GridChart" //Table,SyncChart,GridChart,Antcharts`
            },
            {
                key:"properties",mutable:true,showInInfo:true,
                desc:"returns object,it lists all the properties which are applied on report",
                example:`properties.title.show = true
                properties.title.value = "test"`
            },
            {key:"settings",desc:"returns object,it lists settings of the report",mutable:false,showInInfo:true},
            {key:"defaultValueDisplayMap",desc:"",mutable:false,showInInfo:false},
            {key:"databaseFunctions",desc:"",mutable:false,showInInfo:false},
            {key:"dateFunctions",desc:"",mutable:false,showInInfo:false},
        ]
    },
    {
        group:"Apis",
        vars:[
            {
                key:"add_row",desc:"add fields to rows shelf",mutable:false,showInInfo:true,
                example:`add_row({table:"travel_details",column:"travel_medium"})`
            },
            {
                key:"add_column",desc:"add fields to column shelf",mutable:false,showInInfo:true,
                example:`add_column({table:"travel_details",column:"travel_medium"})`
            },
            {
                key:"add_mark",desc:"add fields to marks",mutable:false,showInInfo:true,
                example:`add_mark({table:"travel_details",column:"booking_platform"}) \n
                add_mark({table:"travel_details",column:"booking_platform",markType:"color"}) \n
                add_mark({table:"travel_details",column:"booking_platform",markType:"size"})`
            },
            {
                key:"add_filter",desc:"creates a new filter",mutable:false,showInInfo:true,
                example:`add_filter({table:"travel_details",column:"booking_platform"}) \n
                add_filter({table:"travel_details",column:"booking_platform",values:"Agent"}) \n
                add_filter({table:"travel_details",column:"travel_date"}) \n
                add_filter({table:"travel_details",column:"travel_date",values:[2015]})`
            },
            {
                key:"remove_column",desc:"deletes field from column shelf",mutable:false,showInInfo:true,
                example:`remove_column({table:"travel_details",column:"travel_medium"})`
            },
            {
                key:"remove_row",desc:"deletes field from rows shelf",mutable:false,showInInfo:true,
                example:`remove_row({table:"travel_details",column:"travel_medium"})`
            },
            {
                key:"remove_mark",desc:"deletes field from marks",mutable:false,showInInfo:true,
                example:`remove_mark({table:"travel_details",column:"travel_medium"})`
            },
            {
                key:"remove_filter",desc:"deletes filter",mutable:false,showInInfo:true,
                example:`remove_filter({table:"travel_details",column:"travel_medium"})`
            },
        ]
    },
    {
        group:"User",
        vars:[
            {
                key:"user",desc:"returns object, it holds current logged in user data",mutable:false,showInInfo:true,
                example:`console.log(user, user.name, user.email, user.actualUserName, user.roles, user.organization)`
            },
        ]
    },
]
export const preFetchVars = [
    {
        group:"Metadata",
        vars:[
            {key:"metadata",desc:"returns object,it contains metadata details",mutable:false,showInInfo:true},
            {key:"metadata_file",desc:"returns object,it stores metadata location",mutable:false,showInInfo:true},
            {key:"database",desc:"returns string,it returns the database name",mutable:false,showInInfo:true},
        ]
    },
    {
        group:"Fields",
        vars:[
            {key:"fields",desc:"returns array,it lists fields with all the details from column/row shelf",mutable:false,showInInfo:true},
            {key:"rows",desc:"returns array,it lists fields from row shelf",mutable:false,showInInfo:true},
            {key:"columns",desc:"returns array,it lists fields from columns shelf",mutable:false,showInInfo:true},
        ]
    },
    {
        group:"Tools",
        vars:[
            {key:"visualisation",desc:"returns string,it returns current visualisation of the report",mutable:false,showInInfo:true},
            {key:"filters",desc:"returns array,it list downs filters",mutable:false,showInInfo:true},
            {key:"filterExpression",desc:`returns array,the list contains two arrays one is for where (fields) and 
            antother one is for having (fields)`,mutable:false,showInInfo:true},
            {key:"settings",desc:"returns object,it lists settings of the report",mutable:false,showInInfo:true},
            {key:"mark_fields",desc:"returns array,it lists fields which are present in marks",mutable:false,showInInfo:true},
            {key:"marks",desc:"returns array,it lists fields with all the details which are present in marks",mutable:false,showInInfo:true},
            {key:"properties",desc:"returns object,it lists all the properties which are applied on report",mutable:false,showInInfo:true},
            {key:"defaultValueDisplayMap",desc:"",mutable:false,showInInfo:false},
            {key:"databaseFunctions",desc:"",mutable:false,showInInfo:false},
            {key:"dateFunctions",desc:"",mutable:false,showInInfo:false},
        ]
    },
    {
        group:"Apis",
        vars:[
            {key:"query",desc:"returns object,it is used for generating form data for generating sql",mutable:true,showInInfo:true},
            {
                key:"setFilterExpression",desc:`It will be executed during Pre Fetch.Use this if you have complex
                 filter expression`,mutable:true,showInInfo:true,
                example:`
                console.log(filterExpression)  \n
                setFilterExpression("destination OR source") \n
                setFilterExpression("destination OR source","sum_travel_cost OR sum_travelled_by") \n
                setFilterExpression("","sum_travel_cost OR sum_travelled_by")`
            },
            {key:"manipulateFormData",desc:`It will be executed during Pre Fetch.It accecpts callback function 
                as argument.Use this to dynamically change form data`,mutable:false,showInInfo:true,
                example:`manipulateFormData((formData)=>{ \n
                    console.log(formData)
                }) \n`
            },
        ]
    },
    {
        group:"User",
        vars:[
            {
                key:"user",desc:"returns object, it holds current logged in user data",mutable:false,showInInfo:true,
                example:`console.log(user, user.name, user.email, user.actualUserName, user.roles, user.organization)`
            },
        ]
    },    
]
export const postFetchVars = [
    {
        group:"Metadata",
        vars:[
            {key:"metadata",desc:"returns object,it contains metadata details",mutable:false,showInInfo:true},
            {key:"metadata_file",desc:"returns object,it stores metadata location",mutable:false,showInInfo:true},
            {key:"database",desc:"returns string,it returns the database name",mutable:false,showInInfo:true},
        ]
    },
    {
        group: "Fields",
        vars:[
            {key:"fields",desc:"returns array,it lists fields with all the details from column/row shelf",mutable:false,showInInfo:true},
            {key:"rows",desc:"returns array,it lists fields from row shelf",mutable:false,showInInfo:true},
            {key:"columns",desc:"returns array,it lists fields from column shelf",mutable:false,showInInfo:true},
        ]
    },
    // {
    //     group: "Fields",
    //     vars:[
    //         {key:"fields",desc:"returns array,it lists fields with all the details from column/row shelf",mutable:false,showInInfo:true},
    //         {key:"rows",desc:"returns array,it lists fields from row shelf",mutable:false,showInInfo:true},
    //         {key:"columns",desc:"returns array,it lists fields from column shelf",mutable:false,showInInfo:true},
    //     ]
    // },
    {
        group: "Tools",
        vars:[
            {key:"visualisation",desc:"returns string,it returns current visualisation of the report",mutable:false,showInInfo:true},
            {key:"filters",desc:"returns array,it list downs filters",mutable:false,showInInfo:true},
            {key:"settings",desc:"returns object,it lists settings of the report",mutable:false,showInInfo:true},
            {key:"mark_fields",desc:"returns array,it lists fields which are present in marks",mutable:false,showInInfo:true},
            {key:"marks",desc:"returns array,it lists fields with all the details which are present in marks",mutable:false,showInInfo:true},
            {key:"properties",desc:"returns object,it lists all the properties which are applied on report",mutable:false,showInInfo:true},
            {key:"defaultValueDisplayMap",desc:"",mutable:false,showInInfo:false},
            {key:"databaseFunctions",desc:"",mutable:false,showInInfo:false},
            {key:"dateFunctions",desc:"",mutable:false,showInInfo:false},
        ]
    },
    {
        group:"Data",
        vars:[
            {
                key:"data",desc:"returns object, it holds reports data",mutable:true,showInInfo:true,
                example:`data[0].source = "Delhi"\n
                data.push({source:"Agra"}) \n
                data = data.filter(record=> record.travel_cost > 10000 )`,
            },
            {
                key:"combine_measures_data",desc:"returns combined data object of provided measures, that holds modified measures data",mutable:true,showInInfo:true,
                example: `let data = combine_measures_data(['sum_destination_id','sum_source_id']) \n
                console.log(data)`,
            }
        ]
    },
    {
        group:"User",
        vars:[
            {
                key:"user",desc:"returns object, it holds current logged in user data",mutable:false,showInInfo:true,
                example:`console.log(user, user.name, user.email, user.actualUserName, user.roles, user.organization)`
            },
        ]
    },

]

export const postExecutionVars = [
    {
        group:"Metadata",
        vars:[
            {key:"metadata",desc:"returns object,it contains metadata details",mutable:false,showInInfo:true},
            {key:"metadata_file",desc:"returns object,it stores metadata location",mutable:false,showInInfo:true},
            {key:"database",desc:"returns string,it returns the database name",mutable:false,showInInfo:true},
        ]
    },
    {
        group: "Fields",
        vars:[
            {key:"fields",desc:"returns array,it lists fields with all the details from column/row shelf",mutable:false,showInInfo:true},
            {key:"rows",desc:"returns array,it lists fields from row shelf",mutable:false,showInInfo:true},
            {key:"columns",desc:"returns array,it lists fields from column shelf",mutable:false,showInInfo:true},
        ]
    },
    {
        group: "Tools",
        vars: [
            { key: "visualisation", desc: "returns string,it returns current visualisation of the report", mutable: true, showInInfo: true },
            { key: "filters", desc: "returns array,it list downs filters", mutable: false, showInInfo: true },
            { key: "settings", desc: "returns object,it lists settings of the report", mutable: false, showInInfo: true },
            { key: "mark_fields", desc: "returns array,it lists fields which are present in marks", mutable: false, showInInfo: true },
            { key: "marks", desc: "returns array,it lists fields with all the details which are present in marks", mutable: false, showInInfo: true },
            {
                key: "properties", desc: "returns object,it lists all the properties which are applied on report", mutable: false, showInInfo: true
            },
            { key: "defaultValueDisplayMap", desc: "", mutable: false, showInInfo: false },
            { key: "databaseFunctions", desc: "", mutable: false, showInInfo: false },
            { key: "dateFunctions", desc: "", mutable: false, showInInfo: false },
        ]
    },
    {
        group:"Data",
        vars:[
            {
                key:"data",desc:"returns object, it holds reports data",mutable:true,showInInfo:true,
            },
        ]
    },
    {
        group:"User",
        vars:[
            {
                key:"user",desc:"returns object, it holds current logged in user data",mutable:false,showInInfo:true,
                example:`console.log(user, user.name, user.email, user.actualUserName, user.roles, user.organization)`
            },
        ]
    },

]


export const monacoReactCodeEditorOptions = {
    acceptSuggestionOnCommitCharacter: true,
    acceptSuggestionOnEnter: "on",
    accessibilitySupport: "auto",
    autoIndent: false,
    automaticLayout: true,
    codeLens: true,
    colorDecorators: true,
    contextmenu: true,
    cursorBlinking: "blink",
    cursorSmoothCaretAnimation: false,
    cursorStyle: "line",
    disableLayerHinting: false,
    disableMonospaceOptimizations: false,
    dragAndDrop: false,
    fixedOverflowWidgets: false,
    folding: true,
    foldingStrategy: "auto",
    fontLigatures: false,
    formatOnPaste: false,
    formatOnType: false,
    hideCursorInOverviewRuler: false,
    highlightActiveIndentGuide: true,
    links: true,
    mouseWheelZoom: false,
    multiCursorMergeOverlapping: true,
    multiCursorModifier: "alt",
    overviewRulerBorder: true,
    overviewRulerLanes: 2,
    quickSuggestions: true,
    quickSuggestionsDelay: 100,
    readOnly: false,
    renderControlCharacters: false,
    renderFinalNewline: true,
    renderIndentGuides: true,
    renderLineHighlight: "all",
    renderWhitespace: "none",
    revealHorizontalRightPadding: 30,
    roundedSelection: true,
    rulers: [],
    scrollBeyondLastColumn: 5,
    scrollBeyondLastLine: true,
    selectOnLineNumbers: true,
    selectionClipboard: true,
    selectionHighlight: true,
    showFoldingControls: "mouseover",
    smoothScrolling: false,
    suggestOnTriggerCharacters: true,
    wordBasedSuggestions: true,
    wordSeparators: "~!@#$%^&*()-=+[{]}|;:'\",.<>/?",
    wordWrap: "off",
    wordWrapBreakAfterCharacters: "\t})]?|&,;",
    wordWrapBreakBeforeCharacters: "{([+",
    wordWrapBreakObtrusiveCharacters: ".",
    wordWrapColumn: 80,
    wordWrapMinified: true,
    wrappingIndent: "none"
};

export const geographicalSubTypes = [
    { key: "lat", value: "lat", label: "Latitude" },
    { key: "long", value: "long", label: "Longitude" },
    { key: "world", value: "world", label: "World" },
    { key: "country", value: "country", label: "Country" },
    { key: "state & province", value: "state", label: "State" },
    { key: "city", value: "city", label: "City" },
]

export const orderBySubMenuOptions = [
    { key: "asc", value: "asc", label: "Ascending" },
    { key: "desc", value: "desc", label: "Descending" },
]

export const mapColors = [
    '#9d0208',
    '#d00000',
    '#dc2f02',
    '#e85d04',
    '#f48c06',
    '#faa307',
    '#ffba08',
  ]