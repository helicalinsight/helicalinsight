
import { CloseOutlined, FileOutlined, InfoCircleOutlined, LoadingOutlined, SyncOutlined } from "@ant-design/icons";
import { Button, Card, Checkbox, Collapse, Row, Skeleton, Tooltip, Typography } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import {
    changeInteractivity,
    setEnableDrillthroughReportLink,
    removeDrillReport,
    toggleCombineCharts,
    updateAnalytics,
    updateDrillThroughList
} from "../../../../../redux/actions/hreport.actions";
import { checkIfCalendarViz, checkIfCardViz, getFieldDisplayName } from '../../../../../utils/utilities';
import LoadingBar from "../../../../common/components/hi-loading-bar";
import HITooltip from "../../../../common/components/hi-tooltip";
import DrillThroughField from "./drillthrough-field";
import MeasureNamesAndValues from "./measure-names-and-values/measure-names-and-values";
import ReferenceLineField from "./reference-line/reference-line-field";
import ValueComponent from './value';
import { isEmpty } from "lodash";
import { loadChildReport } from "../../../utils/base";

const { Text } = Typography;
const { Panel } = Collapse;
const ValuesComponent = props => {
    const dispatch = useDispatch()

    let { reportId } = props
    let {
        marksList, activeMark, interactiveMode, drillDown, drillThrough, drillThroughList, analytics, selectedType,
        fields, combine, referenceLineList, measures, properties, customChart, enableDrillthroughReportLink
    } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.id === reportId)
        return activeReport
    })
    const applicationSettings = useSelector((store) => store.app.applicationSettingsData) || {};
    const { settings = {}, showExperimentalFeatures } = applicationSettings || {};
    const { experimentalModules = [] } = settings || {};

    const childReportLoading = useSelector((state) => state.hreport.present.childReportLoading)
    const fieldsOtherThanHiddnen = fields.filter(field => !field.hidden && !field.hiddenIncludeInResultSet)
    let isCardViz = checkIfCardViz(fieldsOtherThanHiddnen, selectedType)
    selectedType = selectedType || "Table"
    const enableInteravtivity = () => {
        if (interactiveMode) {
            dispatch(changeInteractivity({
                interactiveMode: false, drillDown: false, drillThrough: false, reportId
            }))
        } else {
            dispatch(changeInteractivity({ interactiveMode: !interactiveMode, drillDown, drillThrough, reportId }))
        }
    }


    const enableDrilldown = () => {
        dispatch(changeInteractivity({ interactiveMode, drillDown: !drillDown, drillThrough, reportId }))
    }
    const enableDrillThrough = () => {
        dispatch(changeInteractivity({ interactiveMode, drillDown, drillThrough: !drillThrough, reportId }))
    }
    const combineCharts = () => {
        dispatch(toggleCombineCharts({ reportId }))
    }
    const updateColumn = ({ data, drillThroughId, fltr }) => {
        dispatch(updateDrillThroughList({ data, drillThroughId, fltr }))
    }
    const handleDelete = (drillThroughId) => {
        dispatch(removeDrillReport({ drillThroughId }))
    }
    const handleAnalyticsChange = (item) => {
        dispatch(updateAnalytics(item))
    }

    const handleRefreshDTData = () => {
        if (!drillThroughList?.length) return;
        let [_, r2 = {}] = drillThroughList ?? [];
        if (!r2 || isEmpty(r2)) return;

        const { reportInfo = {}, drillThroughId = "" } = r2 || {};
        if (isEmpty(reportInfo)) return;
        handleDelete(drillThroughId);
        let path = `${reportInfo.location}/${reportInfo?.uuid}`;
        let payload = {
            path,
            name: reportInfo?.uuid,
        }
        loadChildReport(payload, dispatch, props.getApi)
    }

    const handleEnableDTRLink = () => {
        dispatch(setEnableDrillthroughReportLink({ enableDrillthroughReportLink: !enableDrillthroughReportLink, reportId }))
    }

    function checkIfModuleExperimental(module) {
        if (experimentalModules.includes(module)) return showExperimentalFeatures;
        return true;
    }

    activeMark = null
    let discreteValues = fields?.filter((field) => field.floatingType === 'discrete').map(item => item.id)
    const nonDiscreteMarkList = marksList.filter((mark) => !discreteValues.includes(mark.id) ? mark : null).filter(Boolean)
    let filteredMarksList = nonDiscreteMarkList.length > 1 ? nonDiscreteMarkList : [nonDiscreteMarkList[0]]
    let isCombined = combine && selectedType === "GridChart"
    return (
        <div className="values-area" >
            <Text level={6}>Marks</Text>

            <Collapse defaultActiveKey={[activeMark]} className={isCombined ? "combined-measures" : ""}>
                {filteredMarksList.map(measure => {
                    let field = fields.find(field => field.id === measure.id)
                    let display = measure.value === "_all_" ? "All" : measure.value;
                    if (field) {
                        display = getFieldDisplayName(field)
                    }
                    return (
                        <Panel key={measure.id} header={display} >
                            <ValueComponent mark={measure} selectedType={selectedType} reportId={reportId} isCardViz={isCardViz} measures={measures} properties={properties} />
                        </Panel>
                    )
                })}
            </Collapse>

            {["GridChart"].includes(selectedType) && <div>
                <Checkbox onChange={combineCharts} checked={combine}>
                    <HITooltip title="Make a chart with two axes and use up to two measures.">Combine</HITooltip>
                </Checkbox>
            </div>}
            {["Table", "GridChart", "Antcharts", "MapChart", "S2Chart", "Card"].includes(selectedType) && <div className="interactive-area" >
                <Row>
                    <Checkbox
                        checked={interactiveMode}
                        onChange={enableInteravtivity}
                        data-testid="hr-report-interactivity-btn"
                    >
                        <HITooltip title="Enable interactivity to let user interact with this report">Interactivity</HITooltip>
                    </Checkbox>
                </Row>
                {interactiveMode && <Row>
                    <Checkbox
                        checked={drillDown}
                        // disabled={this.state.disabled}
                        data-testid="hr-report-drill-down-btn"
                        onChange={enableDrilldown}
                    >
                        <HITooltip title="On click of action drilldown into same report"> Drill Down</HITooltip>
                    </Checkbox>
                </Row>}
                {interactiveMode && <Row justify={"space-between"}>
                    <Checkbox
                        checked={drillThrough}
                        // disabled={this.state.disabled}
                        data-testid="hr-report-drill-through-btn"
                        onChange={enableDrillThrough}
                    >
                        <HITooltip title="On click of action open child report by passing filters">Drill Through</HITooltip>
                        <HITooltip title="Fields can only be dragged from metadata panel, in drillthrough field."><InfoCircleOutlined
                            style={{
                                fontSize: 12,
                                marginLeft: 4
                            }} />
                        </HITooltip>

                    </Checkbox>

                    {drillThrough ? <Row justify={"end"}>
                        <Tooltip title="Click to connect and drill through for detailed insights or related data." placement="topRight">
                            <span
                                className="reports-btn"
                                data-testid={`drillthrough-reports-btn`}
                                onClick={props.openFbForDrillThrough}
                            >
                                {childReportLoading ? <LoadingOutlined /> : <FileOutlined />}
                            </span>
                        </Tooltip>

                        <Tooltip title="Refresh the drill-through report to apply the latest changes. Note: Current configurations will be reset." placement="topRight">
                            <span
                                data-testid={`drillthrough-reports-refresh-btn`}
                                onClick={handleRefreshDTData}
                                className="hr-drill-through-refresh-icon"
                            >
                                <SyncOutlined />
                            </span>
                        </Tooltip>
                    </Row> : null}

                </Row>}
            </div>}
            {childReportLoading && <LoadingBar handleClick={props.abortFetchData} />}
            <Skeleton active loading={childReportLoading}>
                {drillThrough && drillThroughList.slice(0, 2).map((item) => {
                    let { reportInfo, drillThroughId, parameters, root } = item || {}
                    let { reportName } = reportInfo || {}
                    if (root) return null
                    return (
                        <Card
                            title={
                                <div>
                                    <HITooltip title={reportName}>
                                        {reportName}
                                    </HITooltip>
                                </div>
                            }
                            key={reportName}
                            className="drill-report"
                            extra={
                                <div
                                    onClick={() => handleDelete(drillThroughId)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <CloseOutlined />
                                </div>}
                        >
                            {(parameters && parameters.length) ? parameters.map((fltr) => {
                                let { label } = fltr
                                return (
                                    <DrillThroughField key={label} fltr={fltr}
                                        drillThroughId={drillThroughId}
                                        fields={fields}
                                        updateColumn={data => updateColumn({ data, drillThroughId, fltr })}
                                    />
                                )
                            }) : <div>Filters are Empty</div>}
                            <Checkbox
                                checked={enableDrillthroughReportLink}
                                data-testid="hr-report-drill-through-enable-report-link"
                                onChange={handleEnableDTRLink}
                            >
                                <Tooltip title="Upon enabling report link, context menu will be added in drillthrough action window and 'Drill Through (Default)' option will be added in context menu.">Enable Report Link</Tooltip>
                            </Checkbox>
                        </Card>
                    )
                })}
            </Skeleton>
            {selectedType === "GridChart" && checkIfModuleExperimental("dice") && <div className="analytics-area" >
                <Row style={{ borderTop: "1px solid #ddd" }}>
                    <div>Analytics</div>
                </Row>
                {
                    analytics.map(item => {
                        return (
                            <Row key={item.key} >
                                <Checkbox onChange={() => handleAnalyticsChange(item)} checked={item.value} >
                                    {item.label}
                                </Checkbox>
                            </Row>
                        )
                    })
                }
            </div>}
            {["GridChart", "Antcharts"].includes(selectedType) && <div className="reference-line-section"> <ReferenceLineField {...{ referenceLineList, fields }} /> </div>}

            <div className="hr-values-seperated-line">
                <MeasureNamesAndValues fields={fields} measures={measures} {...{ marksList, selectedType, customChart }} />
            </div>
            {/* --------------- measure names and values  --------------- */}


        </div>
    )
}

export default ValuesComponent;