import { Button, Col, Popover, Row, Tooltip } from "antd";
import { SketchPicker } from "react-color";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";
const { getHcrPropertyTooltipInfo } = hcrCanvasPaneHelperMethods

export default function NodeColorPicker({ popoverOverlayClass = "", keyWord, label, direction, onPropertyChange, clrVal, borders, placement, tooltip = {}, size = "small", disabled = false }) {
    return (
        <div>
            <div className="property-label">{getHcrPropertyTooltipInfo({ label, tooltip })}</div>
            <div style={{ opacity: disabled ? 0.5 : 1, pointerEvents: disabled ? "none" : "auto" }}>
                <Popover
                    trigger="click"
                    overlayClassName={`color-popover ${popoverOverlayClass}`}
                    id="hi-popover-padding-0"
                    getPopupContainer={(triggerNode) => triggerNode.closest('.xflow-json-schema-form-body')}
                    placement={placement || ((direction === "Top") ? "bottomLeft" : (['Color', 'Back Color'].includes(label) ? 'topLeft' : "topRight"))}
                    content={
                        <SketchPicker
                            color={clrVal}
                            disableAlpha={true}
                            onChangeComplete={(value) => {
                                // const hexa = getHexa(value.hex, value.rgb.a);
                                if (direction) {
                                    onPropertyChange({
                                        key: 'borders', value: {
                                            ...borders,
                                            [direction]: {
                                                ...borders[direction],
                                                [keyWord]: value.hex
                                            },
                                        }
                                    })
                                } else {
                                    onPropertyChange({ key: keyWord, value: value.hex })
                                }
                            }}
                        />
                    }
                >
                    <Row align="middle">
                        <Col span={24}>
                            <Button size={size} style={{ width: 110, textAlign: 'start' }}>
                                <Tooltip
                                    title={clrVal}
                                >
                                    <span>
                                        <span style={{ border: '1px solid lightgrey', width: 8, height: 8, background: clrVal, marginRight: 5, display: 'inline-block' }} />
                                        {clrVal}
                                    </span>
                                </Tooltip>
                            </Button>
                        </Col>
                    </Row>
                </Popover>
            </div>
        </div>
    )
}