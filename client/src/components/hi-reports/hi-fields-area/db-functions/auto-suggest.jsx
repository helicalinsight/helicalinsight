
import React,{useEffect,useRef} from 'react';
import Autosuggest from 'react-autosuggest';
import { useDrop } from 'react-dnd';
import { NumberOutlined, CalendarOutlined, FileTextOutlined,CheckOutlined} from "@ant-design/icons";

// import selectedColumnsActions from '../../../../../flux/actions/selected-column-actions';
import FunctionsText from './functions-text'
import { getSuggestionValue, validate } from '../utils/suggetions';
import { getFieldDisplayName } from '../../../../utils/utilities';
import { getCanvasFieldDataType } from '../../../../utils/filter-utils';
import { dateTypes } from '../../utils/constants';


const styles = {
  value: `
  .editing-area{
    overflow-x: auto;
    overflow-y: visible;
    height: 150px;
    border: 1px solid #ddd;
  }
  .react-autosuggest__container {
    position: relative;
    display: inline;
  }
  
  .react-autosuggest__input {
    height: 24px;
    padding: 0px;
    font-weight: 300;
    font-size: 12px;
    border:none;
    caret-color: black;
    background-color: transparent;
    color: transparent;
  }
  
  .react-autosuggest__input--focused {
    outline: none;
  }
  
  .react-autosuggest__input--open {
    border-bottom-left-radius: 0;
    border-bottom-right-radius: 0;
  }
  
  .react-autosuggest__suggestions-container {
    display: none;
  }
  
  .react-autosuggest__suggestions-container--open {
    display: block;
    position: absolute;
    top: 31px;
    width: 380px;
    border: 1px solid #aaa;
    background-color: #fff;
    font-size: 12px !important;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px; 
    z-index: 2;
    overflow: auto;
    height:180px;
  }
  
  .react-autosuggest__suggestions-list {
    margin: 0;
    padding: 0;
    list-style-type: none;
  }
  
  .react-autosuggest__suggestion {
    cursor: pointer;
    padding: 5px;
  }
  
  .react-autosuggest__suggestion--highlighted {
    background-color: #337ab7;
    color:#fff;
  }
  .func-tooltip{
    border: 1px solid #33312b;
    background-color: #33312b;
    color: #e0ded7;
    position:absolute;
    top:18px;
  }
  .input-field {
    position: relative;
    display: inline-block;
    width:100%;
}
.input-field > label {
    position: absolute;
    left: 0px;
    font-size: 12px;
    top: 50%;
    margin-top: -7px;
}
.input-field > input[type=text]:focus + label {
    display: none !important;
}
.input-field > label > span {
    letter-spacing: -2px;
}
.function-string{
    display:inline;
}
.function-string div{
    display:inline;
}
  .arg-bold{
    font-weight: 600;
  }
  `
}

const renderSuggestion = (suggestion) => {
  let key = String(Math.floor(Math.random() * 1000))
  let dataTypeIcon 
  if(suggestion.column){
    let dataType = getCanvasFieldDataType(suggestion)
    dataTypeIcon = <span className="hr-metadata-field-icon" >
        { dataType === "numeric" && <NumberOutlined />}
        { dataType === "text" && <FileTextOutlined />}
        { dataType === "boolean" && <CheckOutlined />}
        { dateTypes.includes(dataType) && <CalendarOutlined />}
    </span>
  }
  return (
      <span key={key} >
          {suggestion.column && <span>
            {dataTypeIcon}
            {getFieldDisplayName(suggestion)} 
          </span>}
          {!suggestion.column && <span>
              <span className="suggestion-column"> f  </span>
              {suggestion.value}
              ({suggestion.parameters && suggestion.parameters.map((param, i) =>{
                return <span key={"suggestion"+i} >{i !== 0 && <span>,</span>} {param.name} </span>
              })} )
          </span>}
      </span>
  );
}


const AutoSuggestComponent = props => {
  let focused = useRef(false)
  useEffect(() => { 
    if(!focused.current){
      let inputElement = document.querySelector("#ip")
      if(inputElement && inputElement.getAttribute("class").includes("react-autosuggest__input--focused") ){
        inputElement?.focus()
        focused.current = true;
      }else{
        inputElement?.focus()
      }
    }
  })
  let { editing } = props
  let functionsDefinition = editing.functionsDefinition
  let { suggestions, currentTooltip, onChange, handleBlur, handleKeyDown, handleInputClick,onSuggestionsFetchRequested
    ,onSuggestionsClearRequested,handleDrop } = props
  let funcToolip = currentTooltip
  let inputWidth = functionsDefinition.length ? functionsDefinition.length * 8 + "px" : "100%"

  const [, drop] = useDrop(() => ({
    accept: "functionText" ,
    drop: item => handleDrop(item),
  }))

  let elem = document.getElementById("ip")
  let pos
  if(elem){
    let val = elem.value;
    pos = val.slice(0, elem.selectionStart).length
  }else{
    pos = 0
  }
  let inValid = validate(functionsDefinition)
  const inputProps = {
    placeholder: "search for functions",
    value: functionsDefinition,
    style: { width: inputWidth },
    onChange: onChange,
    onBlur: handleBlur,
    onKeyDown: handleKeyDown,
    onClick: handleInputClick,
    id: "ip",
  }
  return (
    <div 
        ref={drop} 
        className="editing-area auto-suggest-area"
         >
      <style>
        {styles.value}
        {`.react-autosuggest__suggestions-container--open{
          left: ${pos * 5}px ;
        }
        .func-tooltip{
          left: ${pos * 5}px ;
        }
        `}
      </style>
      <div className="input-field"  >
        <label htmlFor="ip">
          <FunctionsText text={functionsDefinition} />
        </label> 
        <Autosuggest
          suggestions={suggestions}
          highlightFirstSuggestion={true}
          onSuggestionsFetchRequested={onSuggestionsFetchRequested}
          onSuggestionsClearRequested={onSuggestionsClearRequested}
          getSuggestionValue={getSuggestionValue}
          renderSuggestion={renderSuggestion}
          // focusInputOnSuggestionClick={true}
          inputProps={inputProps} />

      </div>
      {(funcToolip && !suggestions.length) && <span className="func-tooltip" >
        {funcToolip.value}({
          funcToolip.parameters.map((param, i) => <span key={"func-tooltip"+i}
            className={funcToolip.argIndex === i ? "arg-bold" : ""} > {i !== 0 && <span>,</span>} {param.name} </span>)
        } )
      </span>}
      {inValid  && <div className="err-msg" > this defination is invalid </div> }
    </div>
  )
}


export default AutoSuggestComponent