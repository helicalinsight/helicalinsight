
import { useState } from "react";
import { Input,  Drawer, Row, Col, Menu,List } from "antd"
import { editFieldFunctions, updateFunctionDefination } from "../../../../redux/actions/hreport.actions"
import { useSelector, useDispatch } from "react-redux";
import AutoSuggestComponent from "./auto-suggest";
import { 
    addSelectedValue, checkIsFound, getTooltip, getCurrentValue, getSuggestions, getActiveFunc,setCursorPos 
} from "../utils/suggetions";
import { useEffect } from "react";
import "./editor.scss"
import Param from "./db-function-item";
import SaveBlock from "./save-block";
import DescriptionComponent from "./descrition";
import { toTitleCase } from "../../../../utils/text-utils"

const FunctionsEditor = props => {
    let { field } = props
    const [lastCursorPos,setLastCursorPos] = useState(0)
    const [activeTab,setActiveTab] = useState("functions")
    const [activeFunc,setActiveFunc] = useState(null)
    const [selectedDatatype,setSelectedDatatype] = useState("text")
    const [searchedText,setSearchedText] = useState("")
    const [suggestions,setSuggestions] = useState([])
    const [showSuggestions,setShowSuggestions] = useState(true)
    const [currentTooltip,setCurrentTooltip] = useState(null)
    const [cursorPosOnBlur,setCursorPosOnBlur] = useState(0)
    const [autoSuggestValue,setAutoSuggestValue] = useState("")
    const allFuncsList = []
    const { editingField, databaseFunctions, fields } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    Object.keys(databaseFunctions).map(type => {
        databaseFunctions[type].map(func => {
            allFuncsList.push(func)
        })
    })
    useEffect(() => {
        if(field){
            const { type = {} } = field || {}
            const {dataType = ''} = type
            setSelectedDatatype(() => (dataType && dataType === 'dateTime') ? 'date' : 'text')
        }
    },[field])
    useEffect(()=>{
        if(lastCursorPos){
            setCursorPos(lastCursorPos)
        }
    },[lastCursorPos])
    useEffect(() => {
        setAutoSuggestValue(editingField?.functionsDefinition)
    }, [editingField?.functionsDefinition])
    const dispatch = useDispatch()
    
    if (!editingField) return null

    const onCloseEditor = () => {
        dispatch(editFieldFunctions({ id: field.id }))
    }
    const handleSelectDataType = dataType => {
        setSelectedDatatype(dataType)
        setActiveTab("functions")
    }
    const handleDoubleClick = (data,e) => {
        // e.preventDefault()
        e.stopPropagation();
        let selectedValue = addSelectedValue({ value: data.value, type: data.type }, lastCursorPos)
        dispatch(updateFunctionDefination({ functionsDefinition: selectedValue.value }))
        setLastCursorPos(selectedValue.cursorPos)
    }
    const handleFunctionClick = (data,e) => {
        e.stopPropagation();
        let isFound = checkIsFound({newValue:data,databaseFunctions})
        setActiveFunc(isFound)
    }
    const onChange = (event, { newValue, method }) => {
        if (method === "type") {
            let isFound = checkIsFound({databaseFunctions})
            if(!newValue) dispatch(updateFunctionDefination({ functionsDefinition: newValue }))
            setAutoSuggestValue(newValue)
            setCurrentTooltip(getTooltip(databaseFunctions))
            if(isFound || !newValue) setActiveFunc(isFound)
        } else if (method === "enter" || method === "click") {
            let isFound = checkIsFound({newValue,databaseFunctions})
            let selectedValue = addSelectedValue(newValue)
            dispatch(updateFunctionDefination({ functionsDefinition: selectedValue.value }))
            setAutoSuggestValue(selectedValue.value)
            if(isFound) setActiveFunc(isFound)
            setLastCursorPos(selectedValue.cursorPos)
        } else if (method === "down" || method === "up") {
            let isFound = checkIsFound({newValue,databaseFunctions})
            if(isFound) setActiveFunc(isFound)
        }
    }
    const handleBlur = () => {
        let elem = document.getElementById("ip")
        let val = elem.value;
        let currentTooltip = getTooltip(databaseFunctions, 1)
        let pos = val.slice(0, elem.selectionStart).length;
        setCursorPosOnBlur(pos)
        setCurrentTooltip(currentTooltip)
        setLastCursorPos(0)
    }
    const handleKeyDown = (e) => {
        if (e.keyCode === 37 || e.keyCode === 39) {
            let keyCode = e.keyCode === 37 ? -1 : 1
            let elem = document.getElementById("ip")
            let val = elem.value;
            let pos = val.slice(0, elem.selectionStart).length;
            pos = pos + keyCode
            setCurrentTooltip(getTooltip(databaseFunctions, pos))
            setShowSuggestions(false)
        }
    }
    const handleInputClick = () => {
        let elem = document.getElementById("ip")
        let val = elem.value;
        let pos = val.slice(0, elem.selectionStart).length;
        let activeFunc = getActiveFunc(databaseFunctions, pos)
        setCurrentTooltip(getTooltip(databaseFunctions, pos))
        if(activeFunc) setActiveFunc(activeFunc)
    }
    const handleDrop = item => {
        let selectedValue = addSelectedValue({ value: item.value, type: item.draggingFrom }, cursorPosOnBlur)
        dispatch(updateFunctionDefination({ functionsDefinition: selectedValue.value }))
        setLastCursorPos(selectedValue.cursorPos)
    }
    const onSuggestionsFetchRequested = () => {
        let list = allFuncsList.concat(fields)
        let currentValue;
        if (showSuggestions) {
            currentValue = getCurrentValue()
        } else {
            currentValue = ""
            setShowSuggestions(true)
        }
        setSuggestions(getSuggestions(currentValue, list))
    }
    const onSuggestionsClearRequested = () => {
        setSuggestions([])
    }
    const handleShowDataTypes = () => {
        setActiveTab("dataType")
    }
    const handleShowdbfunctions = () => {
        setActiveTab("functions")
    }
    const handleShowColumnList = () => {
        setActiveTab("fields")
    }
    const closeDialog = () => {
        onCloseEditor()
    }
    const handleSearch = e => {
        setSearchedText(e.target.value)
    }
    let filteredDatatypes = Object.keys(databaseFunctions).sort()
    let filteredFunctions = databaseFunctions[selectedDatatype] || []
    let filteredColumns = fields
    if (searchedText.length) {
        filteredDatatypes = filteredDatatypes.filter(dataType => dataType.toLowerCase().search(searchedText.toLowerCase()) > -1)
        filteredFunctions = databaseFunctions[selectedDatatype].filter(func => func.value.toLowerCase().search(searchedText.toLowerCase()) > -1)
        filteredColumns = fields.filter(clmn => clmn.column.split(".")[1].toLowerCase().search(searchedText.toLowerCase()) > -1)
    }
    let sortedFunctions = [...filteredFunctions].sort((a,b)=> a.value > b.value ? 1 : -1 )
    let tempEditingField = {...editingField, functionsDefinition: autoSuggestValue }


    return (
        <Drawer
            title={
                <span data-testid="functions-editor" className="" >Functions</span>
            }
            placement="right"
            width={"50%"}
            className={"db-functions-editor"}
            onClose={onCloseEditor}
            visible={editingField && editingField.id === field?.id}
        >
            <Row >
                <Col span={24} >
                    <div>
                        <AutoSuggestComponent
                            // editing={editingField}
                            editing={tempEditingField}
                            suggestions={suggestions}
                            currentTooltip={currentTooltip}
                            onChange={onChange}
                            handleBlur={handleBlur}
                            handleKeyDown={handleKeyDown}
                            handleInputClick={handleInputClick}
                            handleDrop={handleDrop}
                            onSuggestionsFetchRequested={onSuggestionsFetchRequested}
                            onSuggestionsClearRequested={onSuggestionsClearRequested} />
                    </div>
                </Col>
            </Row>
            <Row>
            {activeFunc && <DescriptionComponent 
                    activeFunc={activeFunc}
                    databaseFunctions={databaseFunctions}
                     />}
            </Row>
            <Row>
                <Col span={24} >
                    <Row>
                        <Col span={24} >
                            <Menu 
                            data-testid = "hi-report-editor-menu"
                                // onClick={this.handleClick} 
                                selectedKeys={[activeTab]} mode="horizontal">
                                <Menu.Item key="dataType" onClick={handleShowDataTypes} >
                                    Data Type
                                </Menu.Item>
                                <Menu.Item key="functions" onClick={handleShowdbfunctions} >
                                    Functions
                                </Menu.Item>
                                <Menu.Item key="fields" onClick={handleShowColumnList}>
                                    Fields
                                </Menu.Item>
                            </Menu>
                        </Col>
                    </Row>
                        <Row>
                            <Col span={24} >
                                <Input placeholder={`Search ${activeTab}`} allowClear onChange={handleSearch} />
                            </Col>
                        </Row>
                        <Row>
                            <Col span={24}>
                                {activeTab === "dataType" && (
                                    <List
                                        size="small"
                                        className="functions-scrollbar"
                                        bordered
                                        dataSource={filteredDatatypes}
                                        renderItem={item => {
                                            let className = "db-func-list-item"
                                            if (selectedDatatype === item) {
                                                className = "db-func-list-item db-func-list-item-active"
                                            }
                                            return (
                                                <div onClick={()=>handleSelectDataType(item)} className={className}  >
                                                    {toTitleCase(item)}
                                                </div>
                                            )
                                        }
                                             
                                        }
                                    />
                                )}
                                 {activeTab === "functions" && (
                                     <List
                                        size="small"
                                        className="functions-scrollbar"
                                        bordered
                                        dataSource={sortedFunctions}
                                        renderItem={item =><Param 
                                            handleFunctionClick={handleFunctionClick}
                                            handleDoubleClick={handleDoubleClick}
                                            item={item} type={"function"} />}
                                    />
                                )}
                                {activeTab === "fields" && (
                                     <List
                                        size="small"
                                        className="functions-scrollbar"
                                        bordered
                                        dataSource={filteredColumns}
                                        
                                        renderItem={item => <Param 
                                            handleDoubleClick={handleDoubleClick}
                                            item={item} type={"column"} /> }
                                    />
                                )}
                            </Col>
                        </Row>
                </Col>
            </Row>
            <div className="editor-footer" >
                
                    <SaveBlock closeDialog={closeDialog}
                        databaseFunctions={databaseFunctions} fields={fields} 
                        // editingField={editingField}
                        editingField={tempEditingField}
                         /> 
            </div>
        </Drawer>
    )
}

export default FunctionsEditor;