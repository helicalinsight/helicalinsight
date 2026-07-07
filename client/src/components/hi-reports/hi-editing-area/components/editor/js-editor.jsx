import { useState } from "react"
import { Collapse, Button, Tooltip,Drawer } from 'antd';
import { DownOutlined, InfoCircleOutlined } from "@ant-design/icons"
import { useSelector, useDispatch } from "react-redux"
import { Controlled as CodeMirror } from 'react-codemirror2'
import 'codemirror/lib/codemirror.css';
import 'codemirror/theme/material.css';
import { changeActiveScript, addNewScript, deleteScript, changeEditorContent, applyReportScripts } from "../../../../../redux/actions/hreport.actions";
import { generateReport } from '../../../utils/base';
import { postExecutionVars, postFetchVars, preExecutionVars, preFetchVars } from '../../../utils/constants';
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
require('codemirror/mode/javascript/javascript');


const { Panel } = Collapse;

const JsEditor = ({ getApi ,applyRef}) => {
    const dispatch = useDispatch()
    const [infoId,setInfoId] = useState(null)
    const [disabled, setDisabled] = useState(false)
    const activeReport = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    const { scripts, selectedScript } = activeReport
    const onScriptChange = (id) => {
        dispatch(changeActiveScript({ id }))
    }
    const addNew = () => {
        dispatch(addNewScript())
    }
    const clearScript = id => {
        dispatch(changeEditorContent({ id, value: "" }))
        setDisabled(false)
    }
    const handleChange = (editor, data, value, id) => {
        dispatch(changeEditorContent({ id, value }))
    }
    const handleApply = e => {
        // generateReport(activeReport,dispatch,getApi)
        setDisabled(true)
        dispatch(applyReportScripts())
    }
    const onCloseEditor = e =>{
        setInfoId(null);
    }
    const expandIcon = props => {
        return (
            <div onClick={() => onScriptChange(props.panelKey)} >
                <DownOutlined />
            </div>
        )
    }
    const getTooltipContent = id => {
        let list = []
        if (id === "pre-execution") {
            list = preExecutionVars
        }
        if (id === "pre-fetch") {
            list = preFetchVars
        }
        if (id === "post-fetch") {
            list = postFetchVars
        }
        if(id === "post-execution"){
            list = postExecutionVars
        }
        return list.map(groupItem => {
            let { vars,group } = groupItem
            return(
                <div key={groupItem} >
                    <h2> {group}</h2>
                    {vars.map(item => {
                        let { key,showInInfo,desc,example } = item
                        if(!showInInfo)return null
                        return(
                            <div key={key} >
                                <div><code>{key} </code> </div>
                                <div style={{paddingLeft:"10px",color:"#777"}}  >{desc}</div>
                                {example && <div style={{paddingLeft:"10px",color:"#555"}}>
                                    {example.split("\n").map((line,i)=>{
                                        return(
                                            <div key={i}>
                                                <code    >
                                                    {line}
                                                </code>
                                            </div>
                                        )
                                    })}
                                </div>}
                            </div>
                        )
                    })}
                </div>
            )
        })
    }
    let toolTipContent = getTooltipContent(infoId)
    return (
        <div data-testid = "hi-report-js-editor" style={{ overflow: 'auto' }} >
            <div className="inject-btn" >
                <ShortCutText text="A" scLocation="HR OP" menuItem={true}>
                    <Button ref={applyRef} type="primary" disabled={disabled} onClick={handleApply}>Apply</Button>
                </ShortCutText>
            </div>
            <Drawer
                title={
                    <span className="">Script Info</span>
                }
                placement="right"
                width={"50%"}
                onClose={onCloseEditor}
                visible={infoId}
            >
                <div className="hr-script-info" >
                    {toolTipContent}
                </div>
            </Drawer>
            <Collapse activeKey={[selectedScript]}
                expandIcon={expandIcon}
                className="hi-scripts-collapse" >
                {scripts.map((script, i) => {
                    let { title } = script
                    title = <div>
                        {title ? title : `script${i + 1}`}{' '} &nbsp;
                        <Tooltip placement={"left"} title={"click to see scipt info"} >
                            <span onClick={e=> setInfoId(script.id) } className="script-info-icon" ><InfoCircleOutlined /></span>
                        </Tooltip>
                    </div>
                    let value = script.editingValue === undefined ? script.value : script.editingValue
                    return (
                        <Panel header={title} key={script.id}
                            extra={<Tooltip title={"Remove Script"} >
                                <Button type={"text"} onClick={() => clearScript(script.id)} >Clear</Button>
                            </Tooltip>
                            }
                        >
                            <CodeMirror
                                className="hi-code-editor"
                                value={value}
                                options={{
                                    mode: 'javascript',
                                    // theme: 'material',
                                    lineNumbers: true
                                }}
                                onBeforeChange={(editor, data, value) => {
                                    disabled && setDisabled(false)
                                    handleChange(editor, data, value, script.id)
                                }}
                                onChange={() => {
                                    // handleChange(editor, data, value,script.id)
                                }}
                            />
                        </Panel>
                    )
                })}
            </Collapse>
        </div>
    )
}

export default JsEditor;