import { useSelector } from "react-redux";
import { hcrActions } from "../../../../redux/actions";
import { Checkbox } from "antd";

export default function CanvasPageProperties({ SelectField, InputNumberFiled, dispatch, whenNoDataType, printOrder, getLabel }) {
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { canvasProperties } = activeTab;
    const { pageProperties: canvasPageProps } = canvasProperties;
    const HcrPropertiesConfiguration = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HcrPropertiesConfiguration || {});
    const { tooltipInfo } = HcrPropertiesConfiguration;
    const { tooltip_columnCount, tooltip_columnSpacing, tooltip_columnWidth, tooltip_printOrder, tooltip_whenNoData, tooltip_summaryWithHeaderAndFooter, tooltip_floatingColumnFooter, tooltip_titleInNewPage, tooltip_summaryInNewPage, tooltip_ignorePagination } = tooltipInfo;

    const pagePropsDefault = {
        columnCount: canvasPageProps.columnCount,
        columnSpacing: canvasPageProps.columnSpacing || 0,
        columnWidth: canvasPageProps.columnWidth || 0,
        printOrder: canvasPageProps.printOrder || 'Vertical',
        whenNoData: canvasPageProps.whenNoData || '',
        summaryWithHeaderAndFooter: canvasPageProps.summaryWithHeaderAndFooter || false,
        floatColumnFooter: canvasPageProps.floatColumnFooter || false,
        titleNewPage: canvasPageProps.titleNewPage || false,
        summaryInNewPage: canvasPageProps.summaryInNewPage || false,
        ignorePagination: canvasPageProps.ignorePagination || false
    };

    const inputTypes = [{
        label: "Column Count",
        key: 'columnCount',
        tooltip: tooltip_columnCount
    }, {
        label: "Column Spacing",
        key: 'columnSpacing',
        tooltip: tooltip_columnSpacing
    }, {
        label: "Column Width",
        key: 'columnWidth',
        tooltip: tooltip_columnWidth

    }];

    const checkOptions = [
        {
            label: 'Summary with header and footer',
            key: 'summaryWithHeaderAndFooter',
            tooltip: tooltip_summaryWithHeaderAndFooter
        },
        {
            label: 'Float column footer',
            key: 'floatColumnFooter',
            tooltip: tooltip_floatingColumnFooter
        },
        {
            label: 'Title in new page',
            key: 'titleNewPage',
            tooltip: tooltip_titleInNewPage
        },
        {
            label: 'Summary in new page',
            key: 'summaryInNewPage',
            tooltip: tooltip_summaryInNewPage
        },
        {
            label: 'Ignore pagination',
            key: 'ignorePagination',
            tooltip: tooltip_ignorePagination
        },
    ];

    const onChange = (isChecked, key) => {
        dispatch(hcrActions.setHcrCanvasPageProps({ key, value: isChecked }));
    };

    return <>
        <div className="common-group">
            {inputTypes.map(ele => {
                return <InputNumberFiled
                    label={getLabel({ label: ele.label, tooltip: ele.tooltip })}
                    value={pagePropsDefault[ele.key]}
                    width={110}
                    onChange={(value) => {
                        dispatch(hcrActions.setHcrCanvasPageProps({ key: ele.key, value }));
                    }}
                />
            })}
            <SelectField
                label={getLabel({ label: 'Print Order', tooltip: tooltip_printOrder, placement: 'left' })}
                value={pagePropsDefault.printOrder}
                options={printOrder.map(order => {
                    return {
                        label: order,
                        value: order
                    }
                })}
                width={110}
                onChange={(value) => {
                    dispatch(hcrActions.setHcrCanvasPageProps({ key: 'printOrder', value }));
                }}
            />
            <SelectField
                label={getLabel({ label: 'When No Data', tooltip: tooltip_whenNoData })}
                value={pagePropsDefault.whenNoData}
                options={Object.entries(whenNoDataType).map(ele => {
                    return {
                        label: ele[0],
                        value: ele[1]
                    }
                })}
                width={110}
                onChange={(value) => {
                    dispatch(hcrActions.setHcrCanvasPageProps({ key: 'whenNoData', value }));
                }}
            />
        </div>
        {checkOptions.map(ele => {
            return <div><Checkbox style={{ marginLeft: 0 }} onChange={(e) => { onChange(e.target.checked, ele.key) }} checked={pagePropsDefault[ele.key]}>
                {getLabel({ label: ele.label, tooltip: ele.tooltip })}
            </Checkbox>
            </div>
        })}
    </>
}