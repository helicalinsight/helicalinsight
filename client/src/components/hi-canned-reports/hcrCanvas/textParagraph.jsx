import { InputNumber, Tooltip } from 'antd'

const TextParagraph = (props = {}) => {

    const {
        nodeValues = {},
        onPropertyChange = () => { },
        SelectField,
        InputNumberFiled
    } = props || {}

    const { paragraph = {} } = nodeValues

    const lineSpacingOptions = [
        { label: "Single", value: "Single" },
        { label: "1.5 Lines", value: "1_1_2" },
        { label: "Double", value: "Double" },
        { label: "At Least", value: "AtLeast" },
        { label: "Fixed", value: "Fixed" },
        { label: "Proportional", value: "Proportional" },
    ]

    const selectTypes = [
        {
            title: "Line Spacing",
            options: lineSpacingOptions,
            key: "lineSpacing",
            tooltip: "Type of line spacing for the text object.",
            value: paragraph.lineSpacing || "Single"
        }
    ]

    const inputTypes = [
        {
            title: "Line Spacing Size",
            key: "lineSpacingSize",
            tooltip: "Line spacing size.",
            value: paragraph.lineSpacingSize || 1
        },
        {
            title: "First Line Indent",
            key: "firstLineIndent",
            tooltip: "First line indentation size.",
            value: paragraph.firstLineIndent || 0
        },
        {
            title: "Left Indent",
            key: "leftIndent",
            tooltip: "Left indentation size.",
            value: paragraph.leftIndent || 0
        },
        {
            title: "Right Indent",
            key: "rightIndent",
            tooltip: "Right indentation size.",
            value: paragraph.rightIndent || 0
        },
        {
            title: "Spacing After",
            key: "spacingAfter",
            tooltip: "Spacing after paragraph.",
            value: paragraph.spacingAfter || 0
        },
        {
            title: "Spacing Before",
            key: "spacingBefore",
            tooltip: "Spacing before paragraph.",
            value: paragraph.spacingBefore || 0
        },
        {
            title: "Tab Stop Width",
            key: "tabStopWidth",
            tooltip: "Tab stop width.",
            value: paragraph.tabStopWidth || 0
        },
    ]

    const getLabel = (label, tooltip) => {
        return (
            <Tooltip title={tooltip}>
                <div className="property-label">{label}</div>
            </Tooltip>
        )
    }

    return (
        <div>
            {selectTypes.map((ele) => {
                return (
                    <SelectField
                        label={getLabel(ele.title, ele.tooltip)}
                        value={ele.value}
                        options={ele.options}
                        width={110}
                        onChange={(value) => {
                            onPropertyChange({ key: "paragraph", value: { ...paragraph, [ele.key]: value } })
                        }}
                    />
                )
            })}
            <div className="alignment-properties">
                {inputTypes.map((ele) => {
                    return (
                        <div>
                            <div className="property-label">{ele.title}</div>
                            <InputNumber
                                style={{
                                    width: 110
                                }}
                                min={0}
                                step={ele.key === "lineSpacingSize" ? 0.1 : 1}
                                value={ele.value}
                                onChange={(value) => {
                                    onPropertyChange({ key: "paragraph", value: { ...paragraph, [ele.key]: value } })
                                }}
                            />
                        </div>
                    )
                })}
            </div>
        </div>
    )
}

export default TextParagraph