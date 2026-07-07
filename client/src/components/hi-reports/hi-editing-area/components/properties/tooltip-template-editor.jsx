import { CloseOutlined, InfoCircleOutlined } from '@ant-design/icons'
import { Button, Col, Drawer, Row, Tooltip } from 'antd'
import React, { useEffect, useMemo, useRef } from 'react'
import ReactQuill, { Quill } from 'react-quill'
import { formats, toolbarOptions } from '../../../../common/hi-property-pane/utils/react-quill-configurations'
import "react-quill/dist/quill.snow.css";


const TooltipTemplateEditor = (props = {}) => {
    const { open, template, onClose = () => { }, onChange = () => { }, onReset = () => { } } = props || {}
    const [tooltipTemplate, setTooltipTemplate] = React.useState("")

    const quillRef = useRef(null);

    const reactQuillModules = useMemo(() => {
        const modules = {
            toolbar: {
                container: toolbarOptions({ customColor: { r: 255, g: 255, b: 255, a: 100 } }),
                handlers: {
                    'color': function (value) {
                        this.quill.format('color', value);
                    },
                    'background': function (value) {
                        this.quill.format('background', value);
                    }
                }
            },
            imageResize: {
                parchment: Quill.import('parchment'),
                modules: ['Resize', 'DisplaySize']
            }
        }
        return modules
    }, []);

    const drawerInfo = (
        <div data-testid="hi-tooltip-template-editor-drawer-title">
            <span>Tooltip Template</span>
            <Tooltip title={"Fields can be accessed in template using {{field_name}}. For example, if field name is sum_travel_cost, then expression will be, Travel Cost : {{sum_travel_cost}}"}>
                <InfoCircleOutlined style={{ fontSize: 12, marginLeft: 8 }} />
            </Tooltip>
        </div>
    )

    const handleChange = (value) => {
        setTooltipTemplate(value)
    }

    const handleSave = () => {
        onChange(tooltipTemplate)
    }

    useEffect(() => {
        setTooltipTemplate(template)
    }, [template, open])

    return (
        <Drawer
            title={drawerInfo}
            placement="right"
            width="35%"
            onClose={onClose}
            visible={open}
            closeIcon={<CloseOutlined />}
        >
            <div className="hi-tooltip-template-editor" data-testid="hi-tooltip-template-editor">
                <ReactQuill
                    ref={quillRef}
                    modules={reactQuillModules}
                    value={tooltipTemplate}
                    formats={formats}
                    placeholder={"Tooltip template"}
                    preserveWhitespace
                    onChange={handleChange}
                />
                <Row justify={"end"} style={{ marginTop: 8 }} gutter={[16, 16]}>
                    <Col>
                        <Button
                            onClick={handleSave}
                            type="primary"
                            size="small"
                            data-testid="hi-tooltip-template-editor-save-button"
                        >
                            Save
                        </Button>
                    </Col>
                    <Col>
                        <Button
                            onClick={onReset}
                            size="small"
                            data-testid="hi-tooltip-template-editor-reset-button"
                        >
                            <Tooltip title={"Reset the template to default."}>
                                <span> Reset</span>
                            </Tooltip>
                        </Button>
                    </Col>
                </Row>
            </div>
        </Drawer>
    )
}

export default TooltipTemplateEditor