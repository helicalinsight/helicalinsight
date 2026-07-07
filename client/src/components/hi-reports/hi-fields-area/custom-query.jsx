
import { Button, Drawer, Form, Input } from "antd";
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import { useEffect, useState } from "react";
import { Controlled as CodeMirror } from 'react-codemirror2';
import { useDispatch, useSelector } from "react-redux";
import types from "../../../constants/metadata";
import { addFieldToCanvas, toggleQueryEditor, updateCustomColumn } from "../../../redux/actions/hreport.actions";
require('codemirror/mode/sql/sql');


const CustomQuery = () => {
    const dispatch = useDispatch()
    const [form] = Form.useForm();
    const [query, setQuery] = useState("")
    const [queryValidator, setQueryValidator] = useState(false);

    const { customColumnData } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    useEffect(() => {
        if (customColumnData) {
            if (customColumnData.id) {
                setQuery(customColumnData.column)
                form.setFieldsValue({
                    alias: customColumnData.alias
                })
            } else {
                setQuery("")
                form.setFieldsValue({
                    alias: ""
                })
            }
        }
    }, [customColumnData])

    if (!customColumnData) return null

    const onCloseEditor = () => {
        dispatch(toggleQueryEditor({}))
        setQueryValidator(false)
    }
    const handleChange = value => {
        setQuery(value)
    }
    const onFinish = (values) => {
        if (query) {
            setQueryValidator(false)

            if (customColumnData.id) {
                dispatch(updateCustomColumn({ column: query, alias: values.alias, id: customColumnData.id }))
            } else {
                dispatch(addFieldToCanvas({ column: query, alias: values.alias, genre: types.CUSTOM_FORMULA }))
            }
        } else {
            setQueryValidator(true)
        }
    }

    return (
        <Drawer
            title={
                <span className="">Custom Column</span>
            }
            placement="right"
            width={"50%"}
            onClose={onCloseEditor}
            visible={customColumnData}
        >
            <div className="hr-query-editor-container" data-testid="hr-query-editor-container" >
                <Form form={form} onFinish={onFinish} className="hr-query-alias" >
                    <Form.Item name="alias" label="Alias" rules={[{ required: true, message: 'Please provide a name' }]}>
                        <Input />
                    </Form.Item>
                    <Form.Item>
                        <div>Query</div>
                        <CodeMirror
                            className="hr-query-editor"
                            value={query}
                            options={{
                                mode: 'sql',
                                // theme: 'material',
                                lineNumbers: true
                            }}
                            onBeforeChange={(...args) => {
                                const [, , value] = args
                                handleChange(value)
                            }}
                            editorDidMount={(editor) => {
                                let tm = setTimeout(() => {
                                    clearTimeout(tm)
                                    editor.refresh();
                                }, 100)
                            }}
                        />
                    </Form.Item>
                    {queryValidator && <p className="query-validator">Please Enter Query</p>}
                    <Form.Item className="hr-query-save-btn">
                        <Button type="primary" htmlType="submit" >Save</Button>
                    </Form.Item>
                </Form>
            </div>
        </Drawer>
    )
}

export default CustomQuery;