
import { DownOutlined } from "@ant-design/icons";
import { Button, Col, Collapse, Tooltip, Typography } from 'antd';
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import { Liquid } from 'liquidjs';
import { useMemo, useState } from 'react';
import { Controlled as CodeMirror } from 'react-codemirror2';
import { useDispatch, useSelector } from "react-redux";
import { changeEditorContent, saveHreportStyles } from "../../../../../redux/actions/hreport.actions";
import notify from '../../../../hi-notifications/notify';
require('codemirror/mode/css/css');
const { Text } = Typography;


const { Panel } = Collapse;

export const errorHandlingLiquidJs = ({ value = "<p></p>", Notify }) => {
    const engine = new Liquid();
    let result;
    try {
        result = engine.parseAndRenderSync(value);
    } catch (e) {
        Notify.error({ message: e.message, type: "Frontend" });
    }
    return result;
};

const CssEditor = () => {
    const [disabled, setDisabled] = useState(false)
    const [activeKey, setActiveKey] = useState("css")
    const dispatch = useDispatch()
    const Notify = notify(dispatch);
    const { styles = {}, id = '', stylesId = '' } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    }) || {}
    let elementID = useMemo(
        () => !stylesId ? `hi-report-${id?.split("-")[0]}` : stylesId,
        [id, stylesId]
    );
    const onScriptChange = () => {
        // dispatch(changeActiveScript({ id }))
    }
    const handleChange = (editor, data, value, id) => {
        dispatch(changeEditorContent({ id, value }))
    }
    const expandIcon = props => {
        return (
            <div onClick={() => onScriptChange(props.panelKey)} >
                <DownOutlined />
            </div>
        )
    }

    const clearScript = () => {
        dispatch(changeEditorContent({ id: null, value: "" }))
        setDisabled(false)
    }


    // const cssData = useMemo(
    //     () =>
    //         errorHandlingLiquidJs({
    //             value: styles,
    //             Notify
    //         }),
    //     [styles]
    // );

    const handleApplyCss = () => {
        setDisabled(true)
        dispatch(saveHreportStyles({ styles: styles }))
        // let styleElement = document.createElement("style");
        // styleElement.setAttribute("id", "hreport-dynamic-css");
        // styleElement.innerHTML = cssData;
        // let parent = document.getElementById("hi-editing-area-container");
        // if (cssData) {
        //     parent.appendChild(styleElement);
        // } else {
        //     document.getElementById(`hreport-dynamic-css`)?.remove();
        // }
    }

    return (
        <div>
            <Col span={24} data-testid="hi-report-css-editor" className="hi-component-id-text-wrapper">
                <Text
                    code
                    strong
                    copyable={{ text: `#${elementID}` }}
                >
                    Component id : {elementID}
                </Text>
            </Col>
            <div className="hi-css-editor-btn" data-testid="hi-report-css-inject-btn">
                <Button type="primary" disabled={disabled} onClick={handleApplyCss}>Inject</Button>
            </div>
            <Collapse activeKey={activeKey}
                expandIcon={expandIcon}
                className="hi-scripts-collapse"
                onChange={(aKey) => setActiveKey(aKey)}
            >
                <Panel header={`css`} key={"css"} extra={<Tooltip title={"Remove Script"} >
                    <Button type={"text"} onClick={clearScript} >Clear</Button>
                </Tooltip>}>
                    <CodeMirror
                        className="hi-code-editor"
                        value={styles}
                        options={{
                            mode: 'css',
                            // theme: 'material',
                            lineNumbers: true
                        }}
                        onBeforeChange={(editor, data, value) => {
                            disabled && setDisabled(false)
                            handleChange(editor, data, value)
                        }}
                        onChange={() => {
                            // handleChange(editor, data, value,script.id)
                        }}
                    />
                </Panel>
            </Collapse>
        </div>
    )
}

export default CssEditor;