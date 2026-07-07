
import { CloseOutlined, VerticalAlignBottomOutlined } from "@ant-design/icons";
import { Button, Card, Menu, Radio, Tooltip } from "antd";
import { useEffect, useState } from "react";
import ReactDOM from 'react-dom';
import OutsideClickHandler from 'react-outside-click-handler';
import { useDispatch, useSelector } from "react-redux";
import { setDrillthroughActive, setMenuData } from "../../../redux/actions/hreport.actions";
import { rangeConditions } from '../../../utils/filter-utils';
import { getFieldAliasName, getFieldDisplayName, modifyFilters } from '../../../utils/utilities';
import notify from "../../hi-notifications/notify";
import { fetchReport, generateReport, setActiveReport } from "../utils/base";
import "./cell-card.scss";
import { addFilter, getPropertyFieldInfo, getPropertyText } from "./utils/utillities";
import { ShowMore } from './viz-tooltip';


const isTestMode = process.env.NODE_ENV === "test";
const CellCardContent = props => {
    let { reportId, activeReport, dispatch, reports, report, format, user } = props;
    const Notify = notify(dispatch);

    const { drillDown, drillThrough, fields, cellMenuData, drillThroughList, filters, reportModal, isDrillthroughActive, enableDrillthroughReportLink } = activeReport
    const [condition, setCondition] = useState("IS_ONE_OF")
    const [show, setShow] = useState(false)
    if (!cellMenuData || reportModal) return null
    if (!drillThrough && !drillDown) {
        return null
    }
    let lastReportDTReport = drillThroughList[drillThroughList.length - 1]
    let { payload, position, drillDownFilterValues } = cellMenuData
    drillDownFilterValues = drillDownFilterValues || []
    const handleClick = ({ field, value }) => {
        if (!drillDown) return null
        let newClmn = addFilter([
            { field, value, condition, drillownFilter: "report", drillDownFilterValues }
        ], fields)[0]
        newClmn.reportId = reportId
        props.addFilter(newClmn)
        handleClose()
    }

    const handleClose = () => {
        dispatch(setMenuData({ reportId, menu: null }))
    }

    const handleDrillThrough = (payload = [], _all_ = false) => {
        handleClose()
        openDrillThrough(payload?.length ? payload.map(item => {
            return { ...item, condition }
        }) : [], _all_)
        if (!isDrillthroughActive) {
            dispatch(setDrillthroughActive({ id: reportId }))
        }
    }
    const addFilterValue = (filter, value) => {
        let values = Array.isArray(value) ? value : [value]
        if (rangeConditions.includes(filter.condition) && values.length !== 2) {
            values = [values[0], values[0]]
        }
        return { ...filter, values }
    }
    const openDrillThrough = async (drillData, _all_ = false) => {
        !_all_ && filters.map(fltr => {
            let isPresent = drillData.find(item => item.field === getFieldDisplayName(fltr))
            if (!isPresent) {
                drillData.push({
                    condition: drillData[0].condition,
                    field: getFieldDisplayName(fltr),
                    value: fltr.values
                })
            }
        })
        let lastReport = drillThroughList[drillThroughList.length - 1]
        if (drillThroughList.length === 0 || lastReport.active) {
            let message = `Please select drill through report`
            Notify.warning({ message });
            return
        }
        let reportFields = activeReport.fields
        let tempDrillThroughList = setActiveReport({ list: drillThroughList, index: 1, drillData, updatedData: activeReport })
        let currentReport = tempDrillThroughList.find(item => item.active)
        let { parameters, reportInfo, drillThroughId } = currentReport
        let reportState = reports.find(report => report.id === drillThroughId)
        if (_all_ || !reportState) {
            let file = {
                path: reportInfo.location + "/" + reportInfo.uuid,
                name: reportInfo.uuid
            }
            let res = await fetchReport({ file, mode: "open", fromDrillThrough: true }, dispatch)
            // let res = await fetchReport({ file, mode: "edit" }, dispatch)
            reportState = { ...res };
        }
        // if(!saveData.filters || !saveData.filters){
        //     let message = `Filters are not preset`
        //     return Notify.warning({ message });
        // }
        if (reportState.drillThrough && reportState.drillThroughList[1]) {
            let childDrillThroughItem = reportState.drillThroughList[1]
            if (!tempDrillThroughList.map(item => item?.drillThroughId).includes(childDrillThroughItem.drillThroughId)) {
                tempDrillThroughList = [...tempDrillThroughList, childDrillThroughItem]
            } else {
                // third child report was not rendering so added this else condition, now tempDrillThroughList will have all the previous drillThrough list items.
                tempDrillThroughList = [...reportState.drillThroughList]
            }
        }
        !_all_ && drillData.map(item => {
            parameters.map(fltr => {
                let fieldInfo = reportFields.find(field => field.id === fltr.mappedColumnId)
                if (!fieldInfo) return null
                if (getFieldDisplayName(fieldInfo) === item.field) {
                    let filters = reportState.filters.map(filter => {
                        // if (filter.column === fieldInfo.column) { // it should be mapped with column alias/label/autogen_alias [7856]
                        //     return addFilterValue(filter, item.value)
                        // }
                        if (getFieldDisplayName(filter) === getFieldDisplayName(fltr)) {
                            // if (getFieldDisplayName(filter) === getFieldDisplayName(fieldInfo)) {
                            return addFilterValue(filter, item.value)
                        }
                        return filter
                    })
                    reportState = { ...reportState, filters }
                }
            })
        })
        let inValid = false
        parameters.map(fltr => {
            if (fltr.isStatic) {
                if (!fltr.staticValue) {
                    inValid = true
                    return null
                }
                let filters = reportState.filters.map(filter => {
                    if (getFieldDisplayName(filter) === getFieldDisplayName(fltr)) {
                        let value = (fltr.staticValue || "").split(",")
                        return addFilterValue(filter, value)
                    }
                    return filter
                })
                reportState = { ...reportState, filters }
                return null
            }
        })
        if (inValid) {
            let message = `One or many static filter(s) are empty.Please provide value`
            return Notify.warning({ message });
        }
        let params = {}
        reportState.filters.map(item => {
            params[getFieldAliasName(item)] = item.values
        })
        reportState = modifyFilters(reportState, params)
        let reportId = tempDrillThroughList[0].drillThroughId
        generateReport({
            ...reportState, fromDrillThrough: true, id: reportId, loadState: true, drillThroughId, drillThrough: true, user,
            drillThroughList: tempDrillThroughList
        }, dispatch, props.getApi)
    }
    let payloadlength = payload.length > 7 ? 6 : payload.length
    let height = (payloadlength * 24) + 65
    let { left, top } = position
    if (document.body.offsetWidth < (left + 330)) {
        left = document.body.offsetWidth - 330
    }
    if (document.body.offsetHeight < (top + height)) {
        left = document.body.offsetHeight - height
    }
    let isRange = false
    let currentIndex = drillThroughList.findIndex(item => item.active)
    let nextReport = drillThroughList[currentIndex + 1]
    if (nextReport) {
        (nextReport.parameters || []).map(fltr => {
            if (rangeConditions.includes(fltr.condition)) {
                isRange = true
            }
        })
    }

    const getMenuLabelDefault = (label = null, tooltip = null) => {
        return (
            <Tooltip title={tooltip ?? label}>
                <span>{label}</span>
            </Tooltip>
        )
    }

    const checkIsStaticFilter = (name = '') => {
        let result = false
        const { parameters = [] } = lastReportDTReport || {}
        if (!parameters.length) return false
        let field = parameters.find(item => getFieldDisplayName(item) === name)
        if (field) {
            result = field?.isStatic ?? false
        }
        return result
    }

    const getStringyfyData = (data = {}) => {
        return JSON.stringify(data, null, 2).replace(/[{}"]/g, "").replace(/:/g, " : ")
    }

    const getMenuLabel = (label = null, tooltip = null, data = {}) => {
        let formattedData = getStringyfyData(data)
        return (
            <Tooltip title={
                <div>
                    <span>
                        Drill through the data to see more details for the points listed below.
                    </span>
                    {/* <span>{tooltip}</span> */}
                    <pre style={{ marginLeft: -6, marginTop: -14 }}><span>{formattedData}</span></pre>
                </div>
            }>
                <span>{label}</span>
            </Tooltip>
        )
    }

    const getPrettyData = (data = []) => {
        if (!data.length) return {}
        return data.reduce((acc, next) => {
            acc[next.field] = checkIsStaticFilter(next.field) ? 'static' : Array.isArray(next.value) ? next.value.join(",") : next.value;
            return acc
        }, {})
    }

    // const getDefaultDrillReportParameters = () => {
    //     if (!drillThrough) return;
    //     let tempDrillThroughList = setActiveReport({ list: drillThroughList, index: 1, updatedData: activeReport })
    //     let { parameters = [] } = tempDrillThroughList.find(item => item.active)
    //     if (parameters?.length) {
    //         let tempPayload = parameters.map((item) => {
    //             if (item.values?.length) {
    //                 return {
    //                     field: [getFieldDisplayName(item)],
    //                     value: item.values?.join(",")
    //                 }
    //             }
    //             return null;
    //         }).filter(Boolean)
    //         return getPrettyData(tempPayload);
    //     }
    // }

    const menuItems = [
        {
            key: "drillthrough",
            label: "",
            icon: <span data-testid="drillthrough-btn"><VerticalAlignBottomOutlined /></span>,
            children: [
                {
                    // label: getMenuLabel('Drill through', `Drill through with parameters: ${getStringyfyData(getPrettyData(payload))}, seamlessly extract and analyze targeted data.`),
                    label: getMenuLabel('Drill through (Selected)', `Drill through with parameters: `, getPrettyData(payload)),
                    key: "drillthrough_parameters",
                    icon: <VerticalAlignBottomOutlined />,
                    onClick: () => drillThroughList?.length < 2 ? null : handleDrillThrough(payload),
                    className: 'hr-card-ant-menu-item'
                },
                {
                    // label: getMenuLabel('Drill through (default)', `Drill through with default set parameters: ${getStringyfyData(getDefaultDrillReportParameters())}, to effortlessly access and analyze predefined data`),
                    label: getMenuLabelDefault('Drill through (Default)', 'Generate a drill-through report using the default value of the child report.'),
                    key: "drillthrough_default",
                    icon: <span data-testid="drillthrough-default-button"><VerticalAlignBottomOutlined /></span>,
                    onClick: () => drillThroughList?.length < 2 ? null : handleDrillThrough([], true),
                    className: 'hr-card-ant-menu-item'
                },
            ],
        }
    ]

    const extra = (
        <div className='cell-card-extra-container'>
            {drillDown && <>
                <span>
                    <Radio
                        onChange={() => setCondition("IS_ONE_OF")} checked={condition === "IS_ONE_OF"} >
                        Keep only
                    </Radio>
                </span>
                <span>
                    <Radio
                        onChange={() => setCondition("IS_NOT_ONE_OF")} checked={condition === "IS_NOT_ONE_OF"}>
                        Exclude
                    </Radio>
                </span>
            </>}
            {
                drillThrough ?
                    enableDrillthroughReportLink ?
                        <Menu
                            defaultSelectedKeys={[]}
                            selectedKeys={[]}
                            selectable={false}
                            mode="horizontal"
                            items={menuItems}
                            className='hr-card-ant-menu'
                            getPopupContainer={(triggerNode) => triggerNode.parentNode}
                            data-testid="drillthrough-menu"
                        />
                        :
                        <Tooltip title="Drill through the data to see more details for the points listed below.">
                            <Button type="text" onClick={() => handleDrillThrough(payload)} disabled={drillThroughList?.length < 2} data-testid="drillthrough-btn" >
                                <VerticalAlignBottomOutlined />
                            </Button>
                        </Tooltip>
                    : null
            }
            {drillThrough && isTestMode ? <div onClick={() => handleDrillThrough(payload)} data-testid="drillthrough-testing-btn" /> : null}   {/* Menu was not rendering in test mode */}
            {/* {drillThrough &&
                <Tooltip
                    title={() => <div>
                        <div>Drill Through</div>
                        {isRange && <div> One or many filter(s) requires more than one value. Same value will be sent twice.</div>}
                    </div>}
                >
                    <Button type="text" onClick={handleDrillThrough} disabled={drillThroughList?.length < 2}
                        data-testid="drillthrough-btn" >
                        <VerticalAlignBottomOutlined />
                    </Button>
                </Tooltip>} */}
            <span onClick={handleClose} className="cursor-pointer" data-testid="close-cell-menu" >
                <CloseOutlined />
            </span>
        </div >
    )
    return (
        <OutsideClickHandler onOutsideClick={() => handleClose()}>
            <div>
                <div className="cell-menu" style={{ top: `${top}px`, left: `${left}px`, height: `${height}px` }}>
                    <Card size="small" title="Actions" extra={extra}>
                        {payload.filter((item) => item.field !== "value").map((item, index) => {
                            if (index > 5 && !show) return null
                            let { field, value } = item;
                            let display = value
                            if (Array.isArray(value)) {
                                display = [...new Set(value)].join(",")
                            }
                            const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, field)
                            return (
                                <div className="row drill-down-row cursor-pointer"
                                    key={field}
                                    style={{ opacity: drillDown ? 1 : 0.5 }}
                                    data-testid={`drilldown - ${display}`}
                                    onClick={() => handleClick({ field, value })} >
                                    <div className="field-name"> {field} </div>
                                    <div className="field-value">: {getPropertyText({ text: display, name: field, applyOn: "actions", isApplyClicked, fieldType, formatField })} </div>
                                </div>
                            )
                        })}
                        {payload?.filter((item) => item.field !== "value")?.length > 6 && <ShowMore {...{ show }} onClick={() => setShow(!show)} />}
                    </Card>
                </div>
            </div>
        </OutsideClickHandler>
    )
}


const CellCard = props => {
    const dispatch = useDispatch()
    const reports = useSelector(state => state.hreport.present.reports)
    const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
    const activeReport = reports.find(report => report.id === props.reportId) || {}
    const { cellMenuData } = activeReport
    useEffect(() => {
        if (document.querySelector("#report-drilldown-menu")) {
            if (cellMenuData) {
                ReactDOM.unmountComponentAtNode(document.querySelector("#report-drilldown-menu"))
                ReactDOM.render(<CellCardContent data-testid="hi-report-cell-card" {...props}
                    dispatch={dispatch}
                    reports={reports}
                    user={user}
                    activeReport={activeReport} />, document.querySelector("#report-drilldown-menu"))
            } else {
                ReactDOM.unmountComponentAtNode(document.querySelector("#report-drilldown-menu"))
            }
        }
        return () => {
            if (document.querySelector("#report-drilldown-menu")) {
                ReactDOM.unmountComponentAtNode(document.querySelector("#report-drilldown-menu"))
            }
        }
    }, [cellMenuData])
    return null
}
export default CellCard