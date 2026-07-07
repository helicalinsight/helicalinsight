

import { fromJS } from "immutable";
import { getFieldDisplayName } from "../../../../utils/utilities";


export const escapeRegexCharacters = (str) => {
    return str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

export const getSuggestions = (value, list) => {
    const escapedValue = escapeRegexCharacters(value.trim());
    if (escapedValue === '') {
        return [];
    }
    const regex = new RegExp('^' + escapedValue, 'i');
    return list.filter(func => regex.test(func.column ? getFieldDisplayName(func) : func.value));
}

export const getSuggestionValue = (suggestion) => {
    return suggestion.column ? {value: getFieldDisplayName(suggestion),type:"column"} : {value: suggestion.value,type:"function"};
}

export const checkIsFound = ({newValue,databaseFunctions}) =>{
    let isFound = ""
    let currentValue = newValue ? newValue.value : getCurrentValue()
    fromJS(databaseFunctions).mapKeys((key, funcList) => {
        funcList.map(func=>{
            let value = func.get("value") || ""
            if(value.toLowerCase() === currentValue.toLowerCase() ){
                isFound = value
            }
        })
    } )
    return isFound
}

export const getCurrentValue = () => {
    let arr = []
    let elem = document.getElementById("ip")
    let val = elem.value;
    let pos = val.slice(0, elem.selectionStart).length;
    let beforeCursor = val.slice(0, pos)
    let afterCursor = val.slice(pos, val.length)
    val = beforeCursor + "HICURSOR" + afterCursor
    val.split("(").map(i1 => {
        i1.split(",").map(i2 => {
            i2.split(")").map(i3 => {
                arr.push(i3)
            })
        })
    })
    let currentValue = "";
    arr.map(word => {
        let cursorIndex = word.indexOf("HICURSOR")
        if (word.indexOf("HICURSOR") > -1) {
            currentValue = word.substring(0, cursorIndex) + word.substring(cursorIndex + 8)
        }
    })
    return currentValue || ""
}

export const addSelectedValue = (currentValue,lastCursorPos) => {

    let elem = document.getElementById("ip")
    let val = elem.value;
    let pos = lastCursorPos ? lastCursorPos : val.slice(0, elem.selectionStart).length;
    let beforeCursor = val.slice(0, pos)
    let afterCursor = val.slice(pos, val.length)
    val = beforeCursor + "HICURSOR" + afterCursor
    val = val.split("(").map(i1 => {
        i1 = i1.split(",").map(i2 => {
            i2 = i2.split(")").map((i3) => {
                let cursorIndex = i3.indexOf("HICURSOR")
                if (cursorIndex > -1) {
                    if(currentValue.type === "function"){
                        return currentValue.value + "HIFUNCTIONENDPOINT"
                    }else if(currentValue.type === "column") {
                        return currentValue.value + "HICOLUMNENDPOINT"
                    }else{
                        return currentValue.value
                    }
                } else {
                    return i3
                }
            })
            i2 = i2.join(")")
            return i2
        })
        i1 = i1.join(",")
        return i1
    })
    val = val.join("(")
    let hiFunctionEndPoint = val.indexOf("HIFUNCTIONENDPOINT")
    let hiColumnEndPoint = val.indexOf("HICOLUMNENDPOINT")

    if(hiFunctionEndPoint > -1){
        if(val[hiFunctionEndPoint + 1] !== "("  ){
            val = val.replace('HIFUNCTIONENDPOINT','()');
            return {value:val,cursorPos:hiFunctionEndPoint + 1 }
        }else{
            val = val.replace('HIFUNCTIONENDPOINT','');
            return {value:val,cursorPos:hiFunctionEndPoint }
        } 
    }else if(hiColumnEndPoint > -1) {
        val = val.replace('HICOLUMNENDPOINT','');
        return {value:val,cursorPos:hiColumnEndPoint }
    }else{
        return {value:val,cursorPos:0 }
    }
}

export const setCursorPos = pos =>{
    var ctrl = document.getElementById("ip")

    if (ctrl && ctrl.setSelectionRange) {
        ctrl.focus();
        ctrl.setSelectionRange(pos, pos);
    }
}

export const getTooltip1 = (databaseFunctions,lastCursorPos) =>{
    let arr = []
    let elem = document.getElementById("ip")
    if(elem){
        let val = elem.value;
        let pos = lastCursorPos ? lastCursorPos : val.slice(0, elem.selectionStart).length;
        let beforeCursor = val.slice(0, pos)
        let afterCursor = val.slice(pos, val.length)
        val = beforeCursor + "HICURSOR" + afterCursor
        val = val.replace(/ /g,'')
        val.split("(").map(i1 => {
            i1.split(",").map(i2 => {
                i2.split(")").map((i3) => {
                    arr.push(i3)
                })
            })
        })
        let tooltip = ""
        arr.map((word,i)=>{
            if(word.indexOf("HICURSOR") > -1){
                word = word.replace("HICURSOR","")
                if(!word){
                    tooltip = arr[i -1]
                }
            }
        })
        if(tooltip){
            let funcTooltip
            databaseFunctions = databaseFunctions
            let dataTypes = Object.keys(databaseFunctions)
            dataTypes.map(type=>{
                databaseFunctions[type].map(func=>{
                    if(func.value === tooltip ){
                        funcTooltip = func
                    }
                })
            })
            return funcTooltip
        }else{
            return ""
        }
    }
}

export const getActiveFunc = (databaseFunctions,lastCursorPos) =>{
    let arr = []
    let elem = document.getElementById("ip")
    if(elem){
        let val = elem.value;
        let pos = lastCursorPos ? lastCursorPos : val.slice(0, elem.selectionStart).length;
        let beforeCursor = val.slice(0, pos)
        let afterCursor = val.slice(pos, val.length)
        val = beforeCursor + "HICURSOR" + afterCursor
        val = val.replace(/ /g,'')
        val.split("(").map(i1 => {
            i1.split(",").map(i2 => {
                i2.split(")").map((i3) => {
                    arr.push(i3)
                })
            })
        })
        let activeFunc = ""
        arr.map((word)=>{
            if(word.indexOf("HICURSOR") > -1){
                word = word.replace("HICURSOR","")
                activeFunc = word
            }
        })
        if(activeFunc){
            let activeFuncValue
            databaseFunctions = databaseFunctions
            let dataTypes = Object.keys(databaseFunctions)
            dataTypes.map(type=>{
                databaseFunctions[type].map(func=>{
                    if(func.value === activeFunc ){
                        activeFuncValue = func.value
                    }
                })
            })
            return activeFuncValue
        }else{
            return ""
        }
    }
}


export const getTooltip = (databaseFunctions,lastCursorPos) =>{
    // "java(c(),())"
    let elem = document.getElementById("ip")
    if(elem){
        let val = elem.value;
        let pos = lastCursorPos ? lastCursorPos : val.slice(0, elem.selectionStart).length
        val = val.split("")
        val = val.slice(0, pos).concat(["HICURSOR"]).concat(val.slice(pos)) 
        let tempArr = []
        let currentBraces = {}
        val.some((letter,i)=>{
            if(letter === "("){
                tempArr.push(i)
            }
            if(letter === ")" ){
                let poped = tempArr.pop()
                if(val.indexOf("HICURSOR") > poped && val.indexOf("HICURSOR") < i ){
                    currentBraces = {i:poped,j:i}
                    return true
                }
            }
        })
        let currentText = ""
        if(currentBraces.i && currentBraces.j){
            for(let i=currentBraces.i + 1;i < currentBraces.j;i++){
                currentText = currentText + val[i]
            }
        }
        let currentFunc = ""
        for(let i=currentBraces.i - 1 ;i >= 0;i--){
            if(val[i] === "("){
                break
            }else{
                currentFunc = val[i] + currentFunc
            }
        }
        currentText = currentText.split(",")
        let showToolTip = false;
        let argIndex;
        currentText.map((word,i)=>{
            if(word.indexOf("HICURSOR") > -1){
                word = word.replace("HICURSOR",'')
                word = word.replace(/ /g,'')
                if(!word){
                    showToolTip = true
                    argIndex = i
                }
            }
        })
        if(showToolTip && currentFunc ){
            let currentFuncValue;
            databaseFunctions = databaseFunctions
            let dataTypes = Object.keys(databaseFunctions)
            dataTypes.map(type=>{
                databaseFunctions[type].map(func=>{
                    if(func.value === currentFunc ){
                        currentFuncValue = {...func}
                        currentFuncValue.argIndex = argIndex
                    }
                })
            })
            return currentFuncValue
        }
        return ""
    }else{
        return ""
    }
}


export const validate = (text) =>{
    text = text.split("")
    let openedBraces = [],inValid = false
    text.map((letter,i) =>{
        if(letter === "("){
            openedBraces.push(i)
        }
        if(letter === ")" ){
            if(openedBraces.length){
                openedBraces.pop()
            }else{
                inValid = true
            } 
        }
    })
    if(openedBraces.length){
        inValid = true
    }
    return inValid
}