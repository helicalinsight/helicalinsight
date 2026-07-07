import { CloseOutlined, InfoCircleOutlined } from '@ant-design/icons';
import { Drawer, Tooltip } from 'antd';
import React from 'react';
import { reportInfo } from './utilities';

const initialInfo = {
    title: "",
    open: false,
    active: null,
}

const CustomChartInfo = () => {
    // const { report: { reportData: { data = [] } } } = props
    const [info, setInfo] = React.useState(initialInfo)
    return (
        <div style={{ width: "100%" }} data-testid="custom-chart-info-container">
            <h2>VF (Visualization Framework) Info</h2>
            <CustomDiv>
                <h3>Overview</h3>
                <p>VF is a tool in hreport that allows you to create custom charts, tables, and other visualizations. It provides access to all the visualization libraries available in hreport, as well as components that are not available in hreport.</p>
                <p>VF can use the libraries and data present in the hreport. You can create a function to extract the desired visualization.</p>
            </CustomDiv>
            <CustomDiv>
                <h3>Access Variables</h3>
                <p>VF has access to the following variables:</p>
                <ul>
                    <li><b>data:</b> The current report's data.</li>
                    <li><b>report:</b> The current active report which contains all the data about the current report. <InfoIcon onClick={() => { setInfo({ title: "Report Info", open: true, active: "report" }) }} /></li>
                    <li><b>components:</b> This contains all the components that you can use. <InfoIcon onClick={() => { setInfo({ title: "Components Info", open: true, active: "components" }) }} /></li>
                    <li><b>helperFunctions:</b> This contains helper functions to enhance chart creation. In below examples it is shown how you can utilise these helper functions. <InfoIcon onClick={() => { setInfo({ title: "Helper Functions Info", open: true, active: "helperFunctions" }) }} /></li>
                </ul>
            </CustomDiv>
            <CustomDiv>
                <h3>Examples</h3>
                <p>Below are some examples demonstrating how to use VF:</p>
                <CustomDiv>
                    <h4>Chart Example</h4>
                    <p>This example creates a Line chart using the data present in the report.</p>
                    <pre className="custom-chart-code">
                        {moreChartExample}
                    </pre>
                </CustomDiv>
                <CustomDiv>
                    <h4>Grid Chart Example</h4>
                    <p>This example demonstrates how to use the Muze library for visualization.</p>
                    <pre className="custom-chart-code">
                        {gridChartExample}
                    </pre>
                </CustomDiv>
                <CustomDiv>
                    <h4>Table Example</h4>
                    <p>This example shows how to create a custom table.</p>
                    <pre className="custom-chart-code">
                        {tableExample}
                    </pre>
                </CustomDiv>
                <CustomDiv>
                    <h4>GridTable Example</h4>
                    <p>This example demonstrates a Grid Table. This is a simple example of grid table.</p>
                    <pre className="custom-chart-code">
                        {gridTableExample}
                    </pre>
                    <p>If you want to customize grid table by adding data or fields, then you can use this example : </p>
                    <pre className="custom-chart-code">
                        {gridTableCustomizationExample}
                    </pre>
                </CustomDiv>
            </CustomDiv>
            <CustomDiv>
                <p><b>Note:</b> The above examples require correct fields to be added to the fields section of the report.</p>
            </CustomDiv>
            <Drawer
                title={info.title}
                placement="right"
                width="45%"
                onClose={() => setInfo(initialInfo)}
                visible={info.open}
                className='hi-custom-chart-editor-drawer'
                closeIcon={<CloseOutlined />}
            >
                {info.open ? (
                    <div>
                        {info.active === "report" ? <ReportInfo /> : null}
                        {info.active === "components" ? <ComponentsInfo /> : null}
                        {info.active === "helperFunctions" ? <HelperFunctionsInfo /> : null}
                        {/* {info.active === "data" ? <DataInfo data={data} /> : null} */}
                    </div>
                ) : null}
            </Drawer>
        </div>
    )
}

export default CustomChartInfo

const ReportInfo = () => {
    return (
        <div>
            <h3>Report</h3>
            <p>Report is having all the data about the current active report , i.e id, marksList, metadata etc. </p>
            <p>Below are the fields present in the report</p>
            <ul>
                {Object.keys(reportInfo).map((item, index) => {
                    return (
                        <li key={index}><b>{item}</b> : {reportInfo[item]}</li>
                    )
                })}
            </ul>
        </div>
    )
}

const ComponentsInfo = () => (
    <div>
        <h2>Components</h2>
        <p>Components is an instance containing all usable components within VF, including those from 'morechart', 'gridchart', 'pivot-table', 'antd components', and more.</p>
        <p>These components from the HI application can be utilized to create custom charts. For example:</p>
        <p>You can use the following components of</p>
        <h3>Antd Components.</h3>
        <ul>
            <li>AutoComplete</li>
            <li>Cascader</li>
            <li>Checkbox</li>
            <li>ColorPicker</li>
            <li>DatePicker</li>
            <li>Form</li>
            <li>Input</li>
            <li>InputNumber etc.</li>
        </ul>
        <h3>More Chart Components.</h3>
        <ul>
            <li>Bar</li>
            <li>Line</li>
            <li>Area</li>
            <li>Pie</li>
            <li>Heatmap</li>
            <li>Radar etc.</li>
        </ul>
        <p>Similarly you have all other components access. You can get these components from vf's <b>components</b> instance. For eg.</p>
        <code>const {'{'} Bar, Line, Area, AutoComplete, ...rest {'}'} = components</code>
    </div>
);

const HelperFunctionsInfo = () => {
    return (
        <div>
            <h3>Helper Functions</h3>
            <p>VF provides several helper functions to enhance chart creation:</p>
            <ul>
                <li><b>getTooltip:</b> This function applies default tooltips to the chart.</li>
                This function takes 2 parameters for Chart and Grid Chart, <b>type</b> of the chart and <b>report</b> variable in the vf. You have to use this function in configuration of the component < br />
                For Chart : <code>getTooltip("antd",report)</code><br />
                For Grid Chart : <code>getTooltip("muze",report)</code><br />< br />

                <li><b>getPropertiesConfig:</b> This function applies properties to the charts.</li>
                This function also takes 2 parameters, <b>type</b> of the chart and <b>report</b> variable in the vf. For Grid chart it requires  2 extra parameter. you have to use this function in configuration of the component < br />
                For Chart : <code>getPropertiesConfig("antd", report)</code><br />
                For Grid Chart : <code>getPropertiesConfig("muze",report,"bar",{'{canvasRef}'})</code><br /><br />

                <li><b>enableInteractivity:</b> This function enables interactivity in the VF.</li>
                This function also takes 2 parameters, <b>type</b> of the chart and <b>report</b> variable in the vf. For Grid chart it requires  1 extra parameter with some extra variables. For Chart, you have to use this function in configuration of the component and for Grid Chart you have to use this in the Function Body< br />
                For Chart : <code>enableInteractivity("antd", report)</code><br />
                For Grid Chart : <code>
                    const otherProps = {'{dataModel: carsDm, canvasRef}'} <br />
                    enableInteractivity("muze", report, otherProps)
                </code><br /><br />

                <li><b>getGridChartLabels:</b> This function enables mark's label field for grid chart in the VF.</li>
                This function is use to get the labels for Grid chart, it require 1 parameter , i.e report, and will plot labels for Grid Chart VF if label field is present in marks. You need to use this function in The Component under layers.
                <code>getGridChartLabels(report)</code><br /><br />

                <li><b>getTableColumns:</b> This function gets you the columns and rows of the report for table visualization in VF.</li>
                This function is used to retrive the columns and rows of the report and can only be used to Table VF reoprts.It requires only one parameter. Then you have to use this variable in the Component's configuration< br />
                <code>const columns = getTableColumns(report)</code>
                <br /><br />


                <li><b>getGridChartSortConfig:</b> This function is for Grid chart VF only, it gets you the configuration required for generating Grid Chart.</li>
                This function retruns a promise containing configuration for grid chart and you can use this in function body.It accepts  3 parameters, <b>report</b>, <b>data</b> and <b>schema</b>(this is optional, and should be used when you are providng static data to the vf).< br />
                <code>getGridChartSortConfig(report,data).then((dm) {'=>' + 'console.log(dm)'}).catch(e {'=>'} console.log(e))</code>
                <br /><br />

                <li><b>changePageSize:</b> This function is used in the vf table report , it controls the page size. The usage example is given in the examples info for table.</li>
                <br /><br />

                <b>Note:</b> All The functions usage examples are give in the examples section of the VF info.
            </ul>
        </div>
    )
}

// const DataInfo = ({ data }) => {
//     return (
//         <div>
//             <h3>Data:</h3>
//             <p>Below is the data present in the report.</p>
//             <code>
//                 {JSON.stringify(data, null, 40)}
//             </code>
//         </div>
//     )
// }


const InfoIcon = ({ onClick = () => { }, title = "Click to see in detail" }) => {
    return (
        <Tooltip title={title}>
            <span className="custom-chart-info-icon" onClick={onClick}>
                <InfoCircleOutlined />
            </span>
        </Tooltip>
    )
}

const CustomDiv = ({ children }) => {
    return (
        <div style={{ marginTop: "20px" }}>{children}</div>
    )
}

const moreChartExample = `function DrawLine() { 
    const {Line} = components 
    const {
        getTooltip,
        enableInteractivity,
        getPropertiesConfig 
    } = helperFunctions
    const config = {
      data,
      xField: 'booking_platform',
      yField: 'sum_travel_cost',
      point: {
        size: 5,
        shape: 'diamond',
      },
      tooltip: getTooltip("antd", report),
      ...enableInteractivity("antd", report),
      ...getPropertiesConfig("antd", report)
    };
  
    return <Line {...config} />;
  }`

const gridChartExample = `function DemoMuzeChart() {
    const { Muze, Canvas, Layer, SideEffects, Behaviours } = components
    const [carsDm, setCarsDm] = React.useState(null)
    const {
        getTooltip,
        enableInteractivity,
        getGridChartConfig,
        getPropertiesConfig
    } = helperFunctions;
    const canvasRef = React.useRef(null)
    if (carsDm === null) {
        getGridChartConfig(report, data).then((dm) => {
            console.log(dm, dm.getSchema(), dm.getData())
            setCarsDm(dm)
        });
    }
    if (!carsDm) return null;
    let height = report.chartAreaHeight;
    let width = report.chartAreaWidth;

    const sideEffectMap = SideEffects.config()
        .for(["lasso-selection"])
        .on(["brush"])
        .create();

    const behaviourConfig = Behaviours.config()
        .for(["brush"])
        .on(["dataSelection"])
        .dissociateFrom(["select", "click"])
        .create();

    const otherProps = { dataModel: carsDm, canvasRef }
    enableInteractivity("muze", report, otherProps)

    return (
        <Muze
            data={carsDm}
            colorScheme={["#a9d3f2", "#f4a4c7"]}
            width={width}
            height={height}
            behaviours={behaviourConfig}
            sideEffects={sideEffectMap}
            crossInteractive
        >
            <Canvas
                ref={canvasRef}
                columns={["booking_platform"]}
                rows={["sum_travel_cost"]}
                width={width}
                height={height}
                tooltips={[getTooltip("muze", report)]}
                {...getPropertiesConfig("muze", report, "bar", { canvasRef })}
            >
                <Layer mark="bar" name="bar_layer" />
            </Canvas>
        </Muze>
    )
}
`

const tableExample = `function CustomTable() {
    const { Table } = components
    const { getTableColumns, changePageSize } = helperFunctions
    const [current, setCurrent] = React.useState(1);
    const columns = getTableColumns(report)
    let scrollHeight = report.chartAreaHeight - 100
    scrollHeight = scrollHeight < 10 ? 50 : scrollHeight;
    let pageSize  = report.report.tableRecordsPerPage
    const onChange = (page) => {
        setCurrent(page);
    };

    return (
        <div className="hreport-table">
            <Table
                dataSource={data}
                columns={columns}
                size="small"
                scroll={{ y: scrollHeight }}
                pagination={{
                    pageSize,
                    total: data.length,
                    current,
                    onChange,
                    onShowSizeChange: (_, size) => {
                        changePageSize(report, size);
                    },
                }}
            />
        </div>
    );
}`

const gridTableExample = `function CreateGridTable() {
    const { GridTable } = components
    return(
        <GridTable {...report}/>
    );
}`

const gridTableCustomizationExample = `function CreateGridTable() {
    const { GridTable } = components
    let newData = [
        {
            "booking_platform": "Agent",
            "sum_travel_cost": 3641245,
            "sum_test": 12345
        },
        {
            "booking_platform": "Makemytrip",
            "sum_travel_cost": 6719588,
            "sum_test": 12345
        },
        {
            "booking_platform": "Website",
            "sum_travel_cost": 8173137,
            "sum_test": 12345
        }
    ]
    let newFields = [
        {
            "label": "sum_test",
            "id": "1",
            "addedAs": "row",
            "floatingType": "continous",
        }
    ]
    let tempMetadata = report.metadata[0]
    tempMetadata = {
        ...tempMetadata, 3: { name: "sum_test", type: "numeric" }
    }
    let newMetadata = [...report.metadata]
    newMetadata[0] = tempMetadata
    let gridTableProps = { ...report, data: newData, fields: [...report.fields, ...newFields], metadata: newMetadata }
    return (
        <GridTable {...gridTableProps} />
    )
}`