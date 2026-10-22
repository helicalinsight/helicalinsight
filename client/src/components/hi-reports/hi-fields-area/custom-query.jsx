
import { Button, Drawer, Form } from "antd";
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import { useEffect, useState } from "react";
import { Controlled as CodeMirror } from 'react-codemirror2';
import { useDispatch, useSelector } from "react-redux";
import types from "../../../constants/metadata";
import { addFieldToCanvas, toggleQueryEditor, updateCustomColumn } from "../../../redux/actions/hreport.actions";
import { customFieldDataTypeList } from "../utils/constants";
import CustomColumnChip from "./custom-column-chip";
import notify from "../../hi-notifications/notify";
require('codemirror/mode/sql/sql');

const DEFAULT_CUSTOM_COLUMN_DATA_TYPE = "java.lang.String";

const resolveCustomColumnType = (backendDataType) => {
    const match = customFieldDataTypeList.find((item) => item.backendDataType === backendDataType)
        || customFieldDataTypeList.find((item) => item.backendDataType === DEFAULT_CUSTOM_COLUMN_DATA_TYPE);
    return { backendDataType: match.backendDataType, dataType: match.dataType };
}


const CustomQuery = () => {
    const dispatch = useDispatch()
    const [form] = Form.useForm();
    const [query, setQuery] = useState("")
    const [queryValidator, setQueryValidator] = useState(false);
    const [alias, setAlias] = useState("")
    const [dataType, setDataType] = useState(() => resolveCustomColumnType(DEFAULT_CUSTOM_COLUMN_DATA_TYPE))
    const Notify = notify(dispatch);

    const { customColumnData, fields = [] } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    const customFields = fields.filter(field => field.custom).filter(field => field.id !== customColumnData?.id)

    useEffect(() => {
        if (customColumnData) {
            if (customColumnData.id) {
                setQuery(customColumnData.column)
                setAlias(customColumnData.alias || "")
                setDataType(resolveCustomColumnType(customColumnData.type?.backendDataType))
            } else {
                setQuery("")
                setAlias("")
                setDataType(resolveCustomColumnType(DEFAULT_CUSTOM_COLUMN_DATA_TYPE))
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
    const onFinish = () => {
        if (query) {
            setQueryValidator(false)
            if (customFields.length) {
                if (customFields.some((field) => field.alias === alias)) {
                    return Notify.warning({
                        message: `${alias} already exists in Custom Fields.`,
                        type: "frontend"
                    })
                }
            }

            if (customColumnData.id) {
                dispatch(updateCustomColumn({ column: query, alias, id: customColumnData.id, type: dataType }))
            } else {
                dispatch(addFieldToCanvas({ column: query, alias, genre: types.CUSTOM_FORMULA, type: dataType }))
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
                    <Form.Item label="Alias">
                        <CustomColumnChip
                            alias={alias}
                            type={dataType}
                            onAliasChange={setAlias}
                            onDataTypeChange={setDataType}
                            tooltip="Double click to edit alias"
                        />
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
