


import React from 'react';
import { Row } from 'antd';



export const DescriptionComponent = props => {
    let { activeFunc, databaseFunctions } = props
    let datatypes = Object.keys(databaseFunctions)
    let funcList = []
    datatypes.map(type => {
        databaseFunctions[type].map(func => {
            funcList.push(func)
        })
    })
    let activeFuncVal;
    funcList.map(func => {
        if (func.value === activeFunc) {
            activeFuncVal = func
        }
    })
    if (activeFuncVal) {
        return (
            <Row data-testid = "hi-reports-description-row" className="description" >
                <div  >
                    <div><span className="side-heading" >Value:</span> {activeFuncVal.value} </div>
                    <div><span className="side-heading" >Signature:</span> {activeFuncVal.signature} </div>
                    <div><span className="side-heading" >Description:</span> {activeFuncVal.description} </div>
                </div>
            </Row>
        )
    } else {
        return null;
    }
}

export default DescriptionComponent;