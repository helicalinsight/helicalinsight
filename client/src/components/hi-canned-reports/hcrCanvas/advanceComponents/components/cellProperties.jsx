import { Collapse } from 'antd';
import { useSelector } from 'react-redux';
import { hcrCanvasPaneHelperMethods } from '../../hcrCanvasPaneHelperMethods';

const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods;

const CellProperties = (props = {}) => {
    const { onCellPropertyChange = () => { }, data = {}, EditorPanels, tableStyles = [] } = props || {}
    const { selectedCells = [], cells = {} } = data
    const HcrPropertiesConfiguration = useSelector(
        (state) =>
            state.cannedReports.present?.hCROldConfigurations
                ?.HcrPropertiesConfiguration || {},
    );
    const { tooltipInfo = {} } = HcrPropertiesConfiguration || {};

    const {
        tooltip_alignmentHeight = {},
        tooltip_alignmentWidth = {},
        tooltip_printWhenExpression = {}
    } = tooltipInfo || {}

    let cell = null
    const { InputNumberFiled, InputFiled, SelectField } = EditorPanels

    if (selectedCells.length) {
        cell = cells[selectedCells?.[0]]
    }

    const cellStyleOptions = tableStyles.map((style) => {
        return {
            label: style.styleName,
            value: style.id
        }
    })
    let inputNumberFields = [], inputTextFields = [], selectFields = [];
    if (cell) {
        inputNumberFields = [
            {
                label: getHcrPropertyTooltipInfo({ label: "Width", tooltip: tooltip_alignmentWidth, }),
                value: cell.width,
                key: "width",
                min: 50
            },
            {
                label: getHcrPropertyTooltipInfo({ label: "Height", tooltip: tooltip_alignmentHeight, }),
                value: cell.height,
                key: "height",
                min: 25
            },
        ]

        inputTextFields = [
            {
                label: getHcrPropertyTooltipInfo({ label: "Print When Expression", tooltip: tooltip_alignmentHeight, }),
                key: "printWhenExp",
                tooltip: tooltip_printWhenExpression,
                value: cell.printWhenExp
            }
        ]

        selectFields = [
            {
                label: <div className="property-label">Style</div>,
                key: "styleNameReference",
                tooltip: "",
                value: cell.styleNameReference,
                options: cellStyleOptions
            }
        ]
    }

    const checkIfValuePresent = (options, value) => {
        return options.some((option) => option.value === value)
    }


    const cellProperties = (
        <div>
            <Collapse
                defaultActiveKey={"cell"}
                size={"small"}
                className="node-property-collapse"
            >
                <Collapse.Panel
                    header={<span className="node-property-title">Cell</span>}
                    key={"cell"}
                >
                    <div className="property-group-wrapper">
                        <div className='common-group'>
                            {
                                inputNumberFields.map(({ label, key, value, min }, i) => {
                                    return (
                                        <InputNumberFiled
                                            key={key + '-' + i}
                                            label={label}
                                            value={value}
                                            width={110}
                                            onKeyDown={(e) => {
                                                if (e.key === "-" || e.key === "e") {
                                                    e.preventDefault();
                                                }
                                            }}
                                            min={min}
                                            onChange={(value) => {
                                                onCellPropertyChange({ key, value, cellIds: selectedCells, columnId: cell.columnId, bandType: cell.bandType })
                                            }}
                                        />
                                    )
                                })
                            }
                        </div>

                        {inputTextFields.map(({ label, key, value }, i) => {
                            return (
                                <InputFiled
                                    key={key + '-' + i}
                                    label={label}
                                    value={value}
                                    onChange={(value) => {
                                        onCellPropertyChange({ key, value, cellIds: selectedCells })
                                    }}
                                />
                            )
                        })}

                        {selectFields.map(({ label, key, value, options }, i) => {
                            return (
                                <SelectField
                                    key={key + '-' + i}
                                    label={label}
                                    value={value && checkIfValuePresent(options, value) ? value : ""}
                                    options={options}
                                    onChange={(value) => {
                                        onCellPropertyChange({ key, value, cellIds: selectedCells })
                                    }}
                                    width={110}
                                />
                            )
                        })}
                    </div>
                </Collapse.Panel>
            </Collapse>
        </div>
    )

    return cellProperties;
}

export default CellProperties