


import React from 'react';
import { useSelector } from 'react-redux';



const FunctionsText = props => {
    let { databaseFunctions, fields } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    let funcList = []
    Object.keys(databaseFunctions).map(type => {
        databaseFunctions[type].map(func => {
            funcList.push(func.value)
        })
    })
    fields = fields.map(clmn => clmn.column)
    let { text } = props
    let arr = []
    text.split("(").map(i1 => {
        i1.split(",").map(i2 => {
            i2.split(")").map(i3 => {
                arr.push(i3)
            })
        })
    })
    return (
        <div className="function-string" >
            {text.split("(").map((w1, i1) => {
                return (
                    <span key={i1}>{i1 !== 0 && <span>(</span>}{
                        w1.split(",").map((w2, i2) => {
                            return (
                                <span key={i2} >{i2 !== 0 && <span>,</span>}{w2.split(")").map((w3, i3) => {
                                    let color = ""
                                    if (funcList.indexOf(w3.replace(/ /g, '')) > -1) {
                                        color = "red"
                                    } else if (fields.indexOf(w3.replace(/ /g, '')) > -1) {
                                        color = "green"
                                    } else {
                                        color = "black"
                                    }
                                    return (
                                        <span key={i3} >
                                            {i3 !== 0 && <span>)</span>}
                                            <span style={{ color, whiteSpace: "pre" }} >{w3}</span>
                                        </span>
                                    )
                                })}</span>
                            )
                        })
                    }</span>
                )
            })}
            {/* {arr.map(word =>{
                    if( funcList.indexOf(word.replace(/ /g,'')) > -1  ){
                        return <div style={{color:"red"}} >{word}</div>
                    }else{
                        return <div >{word}</div>
                    }
                })}                 */}
        </div>
    )
}

export default FunctionsText