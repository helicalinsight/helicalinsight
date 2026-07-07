
import { useState, useEffect, useRef } from "react"
import { Row, Menu, Col } from "antd"

import JsEditor from "./js-editor";
import CssEditor from "./css-editor";
import { useDispatch, useSelector } from "react-redux";
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
import { isMatchingShortcut, resetShortcuts } from "../../../utils/utilities";

const CodeEditor = (props) => {
    const dispatch = useDispatch()
    const applyRef = useRef()
    const [activeTab, setActiveTab] = useState("js")
    const keysPressed = useSelector(store => store.app.keysPressed)
    const currentSCLocation = useSelector(store => store.app.currentSCLocation)

    useEffect(() => {
        if (currentSCLocation === 'HR OP') {
            if (isMatchingShortcut(keysPressed, ['o', 'j'])) {
                setActiveTab("js")
                resetShortcuts(dispatch)
            } else if (isMatchingShortcut(keysPressed, ['o', 'c'])) {
                setActiveTab("css")
                resetShortcuts(dispatch)
            }
            else if (isMatchingShortcut(keysPressed, ['o', 'a'])) {
                if (applyRef.current) {
                    applyRef.current.click()
                }
            }
        }
    }, [keysPressed])

    return (
        <>
            <Row>
                <Menu data-testid = "hi-report-code-editor" className="code-editor-tabs" selectedKeys={[activeTab]} mode="horizontal">
                    <Menu.Item key="js" onClick={() => setActiveTab("js")} >
                        <ShortCutText scLocation="HR OP" menuItem={true} text="J">
                            <span> JS </span>
                        </ShortCutText>
                    </Menu.Item>
                    <Menu.Item key="css" onClick={() => setActiveTab("css")}>
                        <ShortCutText scLocation="HR OP" menuItem={true} text="C">
                            <span> CSS </span>
                        </ShortCutText>
                    </Menu.Item>
                </Menu>
            </Row>
            <Row className="hr-operations-container" >
                <Col span={24} >
                    {activeTab === "js" && <JsEditor {...props} applyRef={applyRef} />}
                    {activeTab === "css" && <CssEditor />}
                </Col>
            </Row>
        </>
    )

}

export default CodeEditor;