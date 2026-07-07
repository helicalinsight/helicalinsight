import {
    AreaChartOutlined,
    BarChartOutlined,
    CalendarOutlined,
    CreditCardOutlined,
    DotChartOutlined,
    FileTextOutlined,
    HeatMapOutlined,
    LineChartOutlined,
    Loading3QuartersOutlined,
    MehOutlined,
    PieChartOutlined,
    RadarChartOutlined,
    RiseOutlined,
    SlidersOutlined,
    TableOutlined,
} from "@ant-design/icons";
import ChartIcon from "../../../common/icons/chart-icons";


export const vizList = [
    {
        "type": "Table",
        "displayName": "Table",
        "title": "Table",
        "scDisplay": "T",
        "scLocation": "HR VZ",
        "tooltip": "Displays the data in a row wise table. Rows and columns configuration will be ignored while visualizing.",
    },
    {
        "type": "S2Chart",
        "title": "S2",
        "displayName": "Grid Table",
        "subVizType": "Column",
        "scDisplay": "O",
        "scLocation": "HR VZ",
        "tooltip": "Grid Table reports summarize data in a tabular format across multiple dimensions.",
    },
    {
        "type": "GridChart",
        "title": "Grid Chart",
        "displayName": "Grid Chart",
        "subVizType": "bar",
        "scDisplay": "G",
        "scLocation": "HR VZ",
        "tooltip": "Grid charts allow to visualize data from rows and columns in a graphical format after rearranging and summarizing it.",
    },
    {
        "type": "Antcharts",
        "title": "More Charts",
        "displayName": "Chart",
        "scDisplay": "M",
        "scLocation": "HR VZ",
        "subVizType": "bar",
        "tooltip": "Chart allows to visualize data from rows and columns in a basic graphical formats like pie, bar, line etc.",
    },
    {
        "type": "MapChart",
        "title": "MapChart",
        "displayName": "Maps",
        "scDisplay": "X",
        "scLocation": "HR VZ",
        "subVizType": "point",
        "tooltip": "Maps allows you to visualize data in the form of map.",
    },
    {
        "type": "VF",
        "title": "VF",
        "displayName": "VF",
        "scDisplay": "V",
        "scLocation": "HR VZ",
        "subVizType": "point",
        "tooltip": "Create your custom chart.",
    },
    {
        "type": "Card",
        "title": "Card",
        "displayName": "Card",
        "scDisplay": "K",
        "scLocation": "HR VZ",
        "subVizType": "bar",
        "tooltip": "Card help highlight key performance indicators by showing a single summarized value with optional trends or comparisons.",
    },
];

export const vizListNew = [
    {
        type: "GridChart",
        title: "Grid Chart",
        key: "Grid_Chart",
        displayName: "Grid Chart",
        subVizType: "bar",
        scDisplay: "G",
        scLocation: "HR VZ",
        tooltip: "Grid charts allow to visualize data from rows and columns in a graphical format after rearranging and summarizing it.",
        icon: <BarChartOutlined />,
        children: [
            {
                name: "bar",
                title: "bar",
                key: "grid-bar",
                icon: <BarChartOutlined />,
                tooltip: "at least 1 dimension and 1 measure is required"
            },
            {
                name: "line",
                title: "line",
                key: "grid-line",
                icon: <LineChartOutlined />,
                tooltip: "at least 1 dimension and 1 measure is required"
            },
            {
                name: "area",
                title: "area",
                key: "grid-area",
                icon: <AreaChartOutlined />,
                tooltip: "at least 1 dimension and 1 measure is required"
            },
            {
                name: "point",
                title: "point",
                key: "grid-point",
                icon: <DotChartOutlined />,
                tooltip: "at least 1 dimension and 1 measure is required"
            },
            {
                name: "tick",
                title: "tick",
                key: "grid-tick",
                icon: <ChartIcon name="tick-chart" />,
                tooltip: "at least 1 dimension and 1 measure is required"
            },
            {
                name: "arc",
                title: "arc",
                key: "grid-arc",
                icon: <PieChartOutlined />,
                tooltip: "at least 1 measure is required"
            },
            {
                name: "doughnut",
                title: "doughnut",
                key: "grid-doughnut",
                icon: <ChartIcon name="doughnut-chart" />,
                tooltip: "at least 1 measure is required"
            },
            {
                name: "heatmap",
                title: "heatmap",
                key: "grid-heatmap",
                icon: <HeatMapOutlined />,
                tooltip: "at least 1 dimension is required columns and 1 in rows"
            },
            {
                name: "text",
                title: "text",
                key: "grid-text",
                icon: <FileTextOutlined />,
                tooltip: "at least 1 dimension in columns and 1 dimension in rows is required"
            },
        ]
    },
    {
        type: "Antcharts",
        title: "More Charts",
        key: "More_Chart",
        displayName: "Chart",
        scDisplay: "M",
        scLocation: "HR VZ",
        subVizType: "bar",
        tooltip: "Chart allows to visualize data from rows and columns in a basic graphical formats like pie, bar, line etc.",
        icon: <AreaChartOutlined />,
        children: [
            { key: "ant-bar", title: "bar", name: "bar", icon: <BarChartOutlined />, tooltip: "1 dimension and at least 1 measure is required" },
            { key: "ant-line", title: "line", name: "line", icon: <LineChartOutlined />, tooltip: "1 dimension and at least 1 measure is required" },
            { key: "ant-text", title: "word Cloud", name: "text", icon: <ChartIcon name="word-cloud" />, tooltip: 'only 1 dimension is required' },
            { key: "ant-arc", title: "arc", name: "arc", icon: <PieChartOutlined />, tooltip: "at least 1 measure is required" },
            { key: "ant-area", title: "area", name: "area", icon: <AreaChartOutlined />, tooltip: "1 dimension and at least 1 measure is required" },
            { key: "ant-doughnut", title: "doughnut", name: "doughnut", icon: <ChartIcon name="doughnut-chart" />, tooltip: "1 measure is required" },
            { key: "ant-point", title: "point", name: "point", icon: <DotChartOutlined />, tooltip: "1 dimension and at least 1 measure is required" },
            { key: "ant-waterfall", title: "waterfall", name: "waterfall", icon: <SlidersOutlined />, tooltip: "at least 1 dimension and 1 measure is required" },
            { key: "ant-radar", title: "radar", name: "radar", icon: <RadarChartOutlined />, tooltip: "at least 1 dimension and 1 measure is required" },
            { key: "ant-progress", title: "progress", name: "progress", icon: <Loading3QuartersOutlined />, tooltip: "at least 1 measure is required" },
            { key: "ant-relation", title: "relation", name: "relation", icon: <ChartIcon name="relation-chart" />, tooltip: "at least 1 dimension and 1 measure is required" },
            { key: "ant-calendar", title: "calendar", name: "calendar", icon: <CalendarOutlined />, tooltip: "at least 1 date column is required in columns " }
        ]
    },
    {
        type: "MapChart",
        title: "MapChart",
        key: "MapChart",
        displayName: "Maps",
        scDisplay: "X",
        scLocation: "HR VZ",
        subVizType: "point",
        tooltip: "Maps allows you to visualize data in the form of map.",
        icon: <ChartIcon name="map-chart" />,
        children: [
            { key: "map-line", title: "line", name: "line", icon: <LineChartOutlined />, tooltip: 'at least 1 geographic dimension is required' },
            { key: "map-point", title: "point", name: "point", icon: <DotChartOutlined />, tooltip: 'at least 1 geographic dimension is required' },
            { key: "map-heatmap", title: "heatmap", name: "heatmap", icon: <HeatMapOutlined />, tooltip: 'at least 1 geographic dimension is required' },
        ]
    },
    {
        type: "Table",
        title: "Table",
        key: "Table",
        displayName: "Table",
        scDisplay: "T",
        scLocation: "HR VZ",
        tooltip: "Displays the data in a row wise table. Rows and columns configuration will be ignored while visualizing.",
        icon: <TableOutlined />,
        children: [
            {
                name: "table",
                title: "table",
                key: "table",
                icon: <TableOutlined />,
                tooltip: "Displays the data in a row wise table. Rows and columns configuration will be ignored while visualizing."
            },
        ]
    },
    {
        type: "S2Chart",
        title: "S2",
        key: "S2",
        displayName: "Grid Table",
        subVizType: "Column",
        scDisplay: "O",
        scLocation: "HR VZ",
        tooltip: "Grid Table reports summarize data in a tabular format across multiple dimensions.",
        icon: <ChartIcon name="s2-table-new" />,
        children: [
            {
                name: "Grid Table",
                title: "Grid Table",
                key: "s2",
                icon: <ChartIcon name="s2-table-new" />,
                tooltip: "Grid Table reports summarize data in a tabular format across multiple dimensions."
            }
        ]
    },
    {
        type: "Card",
        title: "Card",
        key: "Card",
        displayName: "Card",
        scDisplay: "K",
        scLocation: "HR VZ",
        subVizType: "KPI",
        tooltip: "Card help highlight key performance indicators by showing a single summarized value with optional trends or comparisons.",
        icon: <CreditCardOutlined />,
        children: [
            { key: "KPI", title: "KPI", capitalize: false, name: "bar", activeNames: ["bar", "line", "area", "table"], icon: <CreditCardOutlined />, tooltip: "only 1 measure is required" },
            { key: "KPI-trend", title: "Trend", name: "table", activeNames: ["table", "trend"], icon: <RiseOutlined />, tooltip: "only 1 measure is required" },
        ]
    },
    {
        type: "VF",
        title: "VF",
        key: "VF",
        displayName: "VF",
        scDisplay: "V",
        scLocation: "HR VZ",
        subVizType: "point",
        tooltip: "Create your custom chart.",
        icon: <ChartIcon name="vf-new" />,
        children: [
            {
                name: "vf",
                title: "vf",
                key: "vf",
                icon: <ChartIcon name="vf-new" />,
                tooltip: "Create your custom chart."
            }
        ]
    },

];


export const getPivotCharts = () => {
    return [
        "Column",
        "Bar",
        "Line",
        "Spline",
        "Area",
        "SplineArea",
        "StepLine",
        "StepArea",
        "StackingColumn",
        "StackingBar",
        "StackingArea",
        "StackingColumn100",
        "StackingBar100",
        "StackingArea100",
        "Scatter",
        "Bubble",
        "Polar",
        "Radar",
        "Pareto"
    ]
}
export const getChartsList = () => {
    return [
        "Hilo"
    ]
}

const DataTypes = {
    NUMERIC: "numeric",
    TEXT: "text",
    DATE: "date",
    TIME: "time",
    DATETIME: "dateTime",
    BOOLEAN: "boolean",
    OTHER: "other"
};


let conditions = {
    EQUALS: "Equals",
    NOT_EQUALS: "Not Equals",
    IS_ONE_OF: "Is One of",
    IS_NOT_ONE_OF: "Is not One of",
    CONTAINS: "Contains",
    DOES_NOT_CONTAINS: "Does not contains",
    STARTS_WITH: "Starts with",
    DOES_NOT_STARTS_WITH: "Does not starts with",
    ENDS_WITH: "Ends with",
    DOES_NOT_ENDS_WITH: "Does not ends with",
    IS_LESS_THAN: "Is less than",
    IS_GREATER_THAN: "Is greater than",
    IS_LESS_THAN_OR_EQUAL_TO: "Is less than or equal to",
    IS_GREATER_THAN_OR_EQUAL_TO: "Is greater than or equal to",
    IS_BETWEEN: "In between",
    IS_NOT_BETWEEN: "Not in between",
    AFTER: "After",
    BEFORE: "Before",
    IS_ON_OR_BEFORE: "Is on or before",
    IS_ON_OR_AFTER: "Is on or after",
    CUSTOM: "Custom",
    IS_NULL: "Is Null",
    IS_NOT_NULL: "Is Not Null",
    IN_RANGE: "In Range",
    NOT_IN_RANGE: "Not in Range",

};


export const allConditions = {
    [DataTypes.TEXT]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        STARTS_WITH: conditions.STARTS_WITH,
        DOES_NOT_STARTS_WITH: conditions.DOES_NOT_STARTS_WITH,
        ENDS_WITH: conditions.ENDS_WITH,
        DOES_NOT_ENDS_WITH: conditions.DOES_NOT_ENDS_WITH,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CONTAINS: conditions.CONTAINS,
        DOES_NOT_CONTAINS: conditions.DOES_NOT_CONTAINS,
        CUSTOM: conditions.CUSTOM
    },
    [DataTypes.NUMERIC]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        IS_GREATER_THAN: conditions.IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL_TO: conditions.IS_GREATER_THAN_OR_EQUAL_TO,
        IS_LESS_THAN: conditions.IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL_TO: conditions.IS_LESS_THAN_OR_EQUAL_TO,
        IS_BETWEEN: conditions.IS_BETWEEN,
        IS_NOT_BETWEEN: conditions.IS_NOT_BETWEEN,
        IN_RANGE: conditions.IN_RANGE,
        NOT_IN_RANGE: conditions.NOT_IN_RANGE,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CUSTOM: conditions.CUSTOM
    },
    [DataTypes.DATE]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        IS_BETWEEN: conditions.IS_BETWEEN,
        IS_NOT_BETWEEN: conditions.IS_NOT_BETWEEN,
        IS_GREATER_THAN: conditions.IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL_TO: conditions.IS_GREATER_THAN_OR_EQUAL_TO,
        IS_LESS_THAN: conditions.IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL_TO: conditions.IS_LESS_THAN_OR_EQUAL_TO,
        IN_RANGE: conditions.IN_RANGE,
        NOT_IN_RANGE: conditions.NOT_IN_RANGE,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CUSTOM: conditions.CUSTOM
    },
    [DataTypes.DATETIME]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        IS_BETWEEN: conditions.IS_BETWEEN,
        IS_NOT_BETWEEN: conditions.IS_NOT_BETWEEN,
        IS_GREATER_THAN: conditions.IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL_TO: conditions.IS_GREATER_THAN_OR_EQUAL_TO,
        IS_LESS_THAN: conditions.IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL_TO: conditions.IS_LESS_THAN_OR_EQUAL_TO,
        IN_RANGE: conditions.IN_RANGE,
        NOT_IN_RANGE: conditions.NOT_IN_RANGE,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CUSTOM: conditions.CUSTOM
    },
    [DataTypes.TIME]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        IS_GREATER_THAN: conditions.IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL_TO: conditions.IS_GREATER_THAN_OR_EQUAL_TO,
        IS_LESS_THAN: conditions.IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL_TO: conditions.IS_LESS_THAN_OR_EQUAL_TO,
        IS_BETWEEN: conditions.IS_BETWEEN,
        IS_NOT_BETWEEN: conditions.IS_NOT_BETWEEN,
        IN_RANGE: conditions.IN_RANGE,
        NOT_IN_RANGE: conditions.NOT_IN_RANGE,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CUSTOM: conditions.CUSTOM
    },
    [DataTypes.BOOLEAN]: {
        EQUALS: conditions.EQUALS
    },
    [DataTypes.OTHER]: {
        EQUALS: conditions.EQUALS,
        NOT_EQUALS: conditions.NOT_EQUALS,
        IS_ONE_OF: conditions.IS_ONE_OF,
        IS_NOT_ONE_OF: conditions.IS_NOT_ONE_OF,
        STARTS_WITH: conditions.STARTS_WITH,
        DOES_NOT_STARTS_WITH: conditions.DOES_NOT_STARTS_WITH,
        ENDS_WITH: conditions.ENDS_WITH,
        DOES_NOT_ENDS_WITH: conditions.DOES_NOT_ENDS_WITH,
        IS_NULL: conditions.IS_NULL,
        IS_NOT_NULL: conditions.IS_NOT_NULL,
        CONTAINS: conditions.CONTAINS,
        DOES_NOT_CONTAINS: conditions.DOES_NOT_CONTAINS,
        CUSTOM: conditions.CUSTOM
    }
};

export const countriesInitialCodes = {
    "AFG": "Afghanistan",
    "AGO": "Angola",
    "ALB": "Albania",
    "ARE": "United Arab Emirates",
    "ARG": "Argentina",
    "ARM": "Armenia",
    "ATA": "Antarctica",
    "ATF": "French Southern and Antarctic Lands",
    "AUS": "Australia",
    "AUT": "Austria",
    "AZE": "Azerbaijan",
    "BDI": "Burundi",
    "BEL": "Belgium",
    "BEN": "Benin",
    "BFA": "Burkina Faso",
    "BGD": "Bangladesh",
    "BGR": "Bulgaria",
    "BHS": "The Bahamas",
    "BIH": "Bosnia and Herzegovina",
    "BLR": "Belarus",
    "BLZ": "Belize",
    "BMU": "Bermuda",
    "BOL": "Bolivia",
    "BRA": "Brazil",
    "BRN": "Brunei",
    "BTN": "Bhutan",
    "BWA": "Botswana",
    "CAF": "Central African Republic",
    "CAN": "Canada",
    "CHE": "Switzerland",
    "CHL": "Chile",
    "CHN": "China",
    "CIV": "Ivory Coast",
    "CMR": "Cameroon",
    "COD": "Democratic Republic of the Congo",
    "COG": "Republic of the Congo",
    "COL": "Colombia",
    "CRI": "Costa Rica",
    "CUB": "Cuba",
    "-99": "Northern Cyprus",
    "CYP": "Cyprus",
    "CZE": "Czech Republic",
    "DEU": "Germany",
    "DJI": "Djibouti",
    "DNK": "Denmark",
    "DOM": "Dominican Republic",
    "DZA": "Algeria",
    "ECU": "Ecuador",
    "EGY": "Egypt",
    "ERI": "Eritrea",
    "ESP": "Spain",
    "EST": "Estonia",
    "ETH": "Ethiopia",
    "FIN": "Finland",
    "FJI": "Fiji",
    "FLK": "Falkland Islands",
    "FRA": "France",
    "GAB": "Gabon",
    "GBR": "United Kingdom",
    "GEO": "Georgia",
    "GHA": "Ghana",
    "GIN": "Guinea",
    "GMB": "Gambia",
    "GNB": "Guinea Bissau",
    "GNQ": "Equatorial Guinea",
    "GRC": "Greece",
    "GRL": "Greenland",
    "GTM": "Guatemala",
    "GUF": "French Guiana",
    "GUY": "Guyana",
    "HND": "Honduras",
    "HRV": "Croatia",
    "HTI": "Haiti",
    "HUN": "Hungary",
    "IDN": "Indonesia",
    "IND": "India",
    "IRL": "Ireland",
    "IRN": "Iran",
    "IRQ": "Iraq",
    "ISL": "Iceland",
    "ISR": "Israel",
    "ITA": "Italy",
    "JAM": "Jamaica",
    "JOR": "Jordan",
    "JPN": "Japan",
    "KAZ": "Kazakhstan",
    "KEN": "Kenya",
    "KGZ": "Kyrgyzstan",
    "KHM": "Cambodia",
    "KOR": "South Korea",
    "CS-KM": "Kosovo",
    "KWT": "Kuwait",
    "LAO": "Laos",
    "LBN": "Lebanon",
    "LBR": "Liberia",
    "LBY": "Libya",
    "LKA": "Sri Lanka",
    "LSO": "Lesotho",
    "LTU": "Lithuania",
    "LUX": "Luxembourg",
    "LVA": "Latvia",
    "MAR": "Morocco",
    "MDA": "Moldova",
    "MDG": "Madagascar",
    "MEX": "Mexico",
    "MKD": "Macedonia",
    "MLI": "Mali",
    "MLT": "Malta",
    "MMR": "Myanmar",
    "MNE": "Montenegro",
    "MNG": "Mongolia",
    "MOZ": "Mozambique",
    "MRT": "Mauritania",
    "MWI": "Malawi",
    "MYS": "Malaysia",
    "NAM": "Namibia",
    "NCL": "New Caledonia",
    "NER": "Niger",
    "NGA": "Nigeria",
    "NIC": "Nicaragua",
    "NLD": "Netherlands",
    "NOR": "Norway",
    "NPL": "Nepal",
    "NZL": "New Zealand",
    "OMN": "Oman",
    "PAK": "Pakistan",
    "PAN": "Panama",
    "PER": "Peru",
    "PHL": "Philippines",
    "PNG": "Papua New Guinea",
    "POL": "Poland",
    "PRI": "Puerto Rico",
    "PRK": "North Korea",
    "PRT": "Portugal",
    "PRY": "Paraguay",
    "QAT": "Qatar",
    "ROU": "Romania",
    "RUS": "Russia",
    "RWA": "Rwanda",
    "ESH": "Western Sahara",
    "SAU": "Saudi Arabia",
    "SDN": "Sudan",
    "SSD": "South Sudan",
    "SEN": "Senegal",
    "SLB": "Solomon Islands",
    "SLE": "Sierra Leone",
    "SLV": "El Salvador",
    "-99": "Somaliland",
    "SOM": "Somalia",
    "SRB": "Republic of Serbia",
    "SUR": "Suriname",
    "SVK": "Slovakia",
    "SVN": "Slovenia",
    "SWE": "Sweden",
    "SWZ": "Swaziland",
    "SYR": "Syria",
    "TCD": "Chad",
    "TGO": "Togo",
    "THA": "Thailand",
    "TJK": "Tajikistan",
    "TKM": "Turkmenistan",
    "TLS": "East Timor",
    "TTO": "Trinidad and Tobago",
    "TUN": "Tunisia",
    "TUR": "Turkey",
    "TWN": "Taiwan",
    "TZA": "United Republic of Tanzania",
    "UGA": "Uganda",
    "UKR": "Ukraine",
    "URY": "Uruguay",
    "USA": "United States of America",
    "UZB": "Uzbekistan",
    "VEN": "Venezuela",
    "VNM": "Vietnam",
    "VUT": "Vanuatu",
    "PSE": "West Bank",
    "YEM": "Yemen",
    "ZAF": "South Africa",
    "ZMB": "Zambia",
    "ZWE": "Zimbabwe"
}

export const applicableVizForColumnsAndRowsMap = {
    // measures = at least measures required to generate a chart
    // dimensions = at least dimensions required to generate a chart
    GridChart: {
        bar: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        line: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        area: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
            ]
        },
        point: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        tick: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        arc: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 },
            ]
        },
        doughnut: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 },
            ]
        },
        heatmap: {
            measures: 0,
            dimensions: 2,
            isGridHeatmap: true,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 0, dimensionsInRows: 1, meauresInColumn: 0 },
            ]
        },
        text: {
            measures: 0,
            dimensions: 2,
            requires: [
                { dimensionsInColumns: 1, dimensionsInRows: 1 }
                // { dimensionsInColumns: 1, meauresInRows: 1 },
                // { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        }
    },
    Antcharts: {
        bar: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        line: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        area: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        // bar: {
        //     measures: 1,
        //     dimensions: 0,
        //     requires: [
        //         { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
        //         { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
        //         { dimensionsInColumns: 1, meauresInRows: 1 },
        //         { dimensionsInRows: 1, meauresInColumn: 1 }
        //     ]
        // },
        // line: {
        //     measures: 1,
        //     dimensions: 0,
        //     requires: [
        //         { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
        //         { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
        //         { dimensionsInColumns: 1, meauresInRows: 1 },
        //         { dimensionsInRows: 1, meauresInColumn: 1 }
        //     ]
        // },
        // area: {
        //     measures: 1,
        //     dimensions: 0,
        //     requires: [
        //         { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
        //         { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
        //         { dimensionsInColumns: 1, meauresInRows: 1 },
        //         { dimensionsInRows: 1, meauresInColumn: 1 }
        //     ]
        // },
        point: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        arc: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
                { dimensionsInColumns: 1, meauresInRows: 1 },
            ]
        },
        doughnut: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
                { dimensionsInColumns: 1, meauresInRows: 1 },
            ]
        },
        text: {
            measures: null,
            dimensions: 1,
            onlyDimension: true,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: null },
                { dimensionsInRows: 1, meauresInColumn: null }
            ]
        },
        waterfall: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 }
            ]
        },
        radar: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        progress: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1 },
                { dimensionsInRows: 0, meauresInColumn: 1 }
            ]
        },
        relation: {
            measures: 1,
            dimensions: 1,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 1 },
                { dimensionsInRows: 1, meauresInColumn: 1 }
            ]
        },
        calendar: {
            measures: 0,
            dimensions: 1,
            isCalendar: true,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 0 },
            ]
        }
    },
    MapChart: {
        line: {
            isMap: true,
            dimensions: 1,
            measures: 0,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 0 },
                { dimensionsInRows: 1, meauresInColumn: 0 }
            ]
        },
        point: {
            isMap: true,
            dimensions: 1,
            measures: 0,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 0 },
                { dimensionsInRows: 1, meauresInColumn: 0 }
            ]
        },
        heatmap: {
            isMap: true,
            dimensions: 1,
            measures: 0,
            requires: [
                { dimensionsInColumns: 1, meauresInRows: 0 },
                { dimensionsInRows: 1, meauresInColumn: 0 }
            ]
        }
    },
    Card: {
        kpi: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
        table: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
        bar: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
        line: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
        area: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
        trend: {
            measures: 1,
            dimensions: 0,
            requires: [
                { dimensionsInColumns: 0, meauresInRows: 1, dimensionsInRows: 0, meauresInColumn: 0, exact: true },
                { dimensionsInColumns: 0, meauresInRows: 0, dimensionsInRows: 0, meauresInColumn: 1, exact: true },
            ]
        },
    }
}


export const formatGroupOptions = [
    {
        label: 'Number',
        options: [
            {
                value: '#,##0',
                label: '1,234',
            },
            {
                value: '#,##0.00',
                label: '1,234.00',
            },
            {
                value: '#,##0.00;-#,##0.00',
                label: '-1,234.00',
            },
        ],
    },
    {
        label: 'Percentage',
        options: [
            {
                value: '0%',
                label: '100%',
            },
            {
                value: '0.00%',
                label: '100.00%',
            },
        ],
    },
    {
        label: 'Date and Time',
        options: [
            {
                value: 'dd-mm-yyyy hh:mm:ss',
                label: '01-01-2020 00:00:00',
            },
            {
                value: 'dd-mm-yyyy',
                label: '01-01-2020',
            },
            {
                value: 'dd-mmm-yy',
                label: '01-Jan-20',
            },
            {
                value: 'dd-mmm',
                label: '01-Jan',
            },
            {
                value: 'mmm-yy',
                label: 'Jan-15',
            },
            {
                value: 'h:mm AM/PM',
                label: '12:00 AM',
            },
            {
                value: 'h:mm:ss AM/PM',
                label: '12:00:00 AM',
            },
            {
                value: 'hh:mm',
                label: '00:00',
            },
            {
                value: 'hh:mm:ss',
                label: '00:00:00',
            },
            {
                value: 'mm:ss',
                label: '00:00',
            },
            {
                value: 'mm:ss.0',
                label: '00:00.0',
            },
        ],
    },
    {
        label: 'Fraction',
        options: [
            {
                value: '# ?/?',
                label: 'Up to onc Digit (1/4)',
            },
            {
                value: '# ??/??',
                label: 'Up to tow Digits (21/25)',
            },
            {
                value: '# ???/???',
                label: 'Up to three Digits (312/943)',
            },
            {
                value: '# ?/2',
                label: 'As halves (1/2)',
            },
            {
                value: '# ?/4',
                label: 'As quarters (2/4)',
            },
            {
                value: '# ?/8',
                label: 'As eights (4/8)',
            },
            {
                value: '# ?/16',
                label: 'As sixteenths (8/16)',
            },
            {
                value: '# ?/10',
                label: 'As tenths (3/10)',
            },
            {
                value: '# ?/100',
                label: 'As hundredths (30/100)',
            },
        ],
    },
]