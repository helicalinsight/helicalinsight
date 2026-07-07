

import { Button, Collapse, Tooltip, Typography } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { ReloadOutlined } from "@ant-design/icons"
import { Controlled as CodeMirror } from 'react-codemirror2';
import { useSelector, useDispatch } from "react-redux"
import { generateReport } from '../../../utils/base';
import LoadingBar from '../../../../common/components/hi-loading-bar';
import notify from '../../../../../components/hi-notifications/notify'
import ShortCutText from '../../../../common/hi-shortcuts/hi-shortcuts';
import { isMatchingShortcut, resetShortcuts } from '../../../utils/utilities';
require('codemirror/mode/sql/sql');

const { Panel } = Collapse;
const { Text } = Typography;



const SqlEditor = (props) => {
    let { reportId } = props
    const dispatch = useDispatch()
    const refreshRef = useRef()
    const copyRef = useRef()
    const keysPressed = useSelector(store => store.app.keysPressed)
    const currentSCLocation = useSelector(store => store.app.currentSCLocation)
    const [isFetching, setFetching] = useState(false)
    const activeReport = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.id === reportId)
        return activeReport
    })
    const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
    const { sqlString } = activeReport
    useEffect(() => {
        handleGenerateQuery()
    }, [])
    const handleGenerateQuery = () => {
        setFetching(true)
        generateReport({ ...activeReport, generateQuery: true, user }, dispatch, props.getApi).then(() => {
            setFetching(false)
        })
        // generateReport({ ...activeReport, generateQuery: true }, dispatch, props.getApi).then(() => {
        //     setFetching(false)
        // })
    }
    useEffect(() => {
        if (currentSCLocation === 'HR SQ') {
          if (isMatchingShortcut(keysPressed, ['q', 'r'])) {
            if (refreshRef.current) {
                refreshRef.current.click()
                resetShortcuts(dispatch)
            }
          } 
          if (isMatchingShortcut(keysPressed, ['q', 'c'])) {
            if (sqlString.length && navigator.clipboard) {
                navigator.clipboard.writeText(sqlString);
                Notify.success({ message: "SQL copied to clipboard successfully!" })
            } else {
                // Notify.warning({ message: "There is no data to Copy!" })
            }
            resetShortcuts(dispatch)
          } 
        }
    },[keysPressed])
    const Notify = notify(dispatch);
    return (
        <div data-testid = "hi-report-sql-editor" className="height100percent">
            <Collapse activeKey={["sql"]}
                className="hi-scripts-collapse height100percent" >
                <Panel showArrow={false} header={
                    <div>
                        SQL Viewer
                        {isFetching && <LoadingBar handleClick={props.abortFetchData} />}
                    </div>} key={"sql"}
                    className="sql-container height100percent"
                    extra={<div >
                                <Button ref={refreshRef} onClick={handleGenerateQuery} loading={isFetching}
                                    className="reload-btn" >
                                    {!isFetching && 
                                    <ShortCutText scLocation="HR SQ" text="R" menuItem={true}>
                                        <Tooltip title={isFetching ? "" : "Reload"} >
                                        <ReloadOutlined />
                                    </Tooltip>
                                    </ShortCutText>
                                    }
                                </Button>
                        {!isFetching && 
                            <ShortCutText scLocation="HR SQ" text="C" menuItem={true}>
                                <Text
                                    ref={copyRef}
                                    className="copy-icon"
                                    copyable={{
                                        onCopy: () => {
                                            return !sqlString.length ?
                                                Notify.warning({ message: "There is no data to Copy!" })
                                                : null
                                        },
                                        text: sqlString,
                                    }}
                                />
                            </ShortCutText>
                        }
                    </div>}  >
                    <CodeMirror
                        className="sql hi-code-editor"
                        value={sqlString}
                        options={{
                            mode: 'sql',
                            // theme: 'material',
                            lineNumbers: true
                        }}
                        onBeforeChange={() => {
                            // handleChange(editor, data, value, script.id)
                        }}
                        onChange={() => {
                            // handleChange(editor, data, value,script.id)
                        }}
                    />
                </Panel>
            </Collapse>


        </div >
    )
}

export default SqlEditor