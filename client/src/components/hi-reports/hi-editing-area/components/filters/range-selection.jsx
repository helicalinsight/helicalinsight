

import React from 'react';
import { Slider,InputNumber } from "antd"
import { useSelector } from 'react-redux';
import { useRef } from 'react';
import { useEffect } from 'react';
import { isEqual } from 'lodash-es';

const isTestMode = process.env.NODE_ENV === "test"

const RangeSelection = props => {
  const dataIdsRef = useRef()
    const { filter,values,reportId } = props
    const activeReport = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.id === reportId)
        return activeReport || {}
    })
    const { filters } = activeReport
    let { configId,cascade,valuesRange } = filter
    let cascadeFilterDataIds = []
    let { isEnabled } = cascade
    if(isEnabled){
        let cascadeFilterIds = cascade.filters.map(filter =>filter.uid)
        cascadeFilterDataIds = filters.filter(filter=> cascadeFilterIds.includes(filter.uid)).map(filter =>{
            return filter.dataId
        }).filter(dataId => !!dataId)
    }
    useEffect(() => {
        if(isEnabled && cascadeFilterDataIds.length && !isEqual(dataIdsRef.current,cascadeFilterDataIds)){
          !isTestMode && props.getMinMaxValues()
          dataIdsRef.current = cascadeFilterDataIds
        }
    })
    useEffect(() => {
      !isTestMode && props.getMinMaxValues()
    }, [configId])
  
  if(!Object.keys(valuesRange).length){
    valuesRange = {min:0,max:10000000}
  }
  let minInput = typeof values[0] !== 'number' ? valuesRange.min : values[0]
  let maxInput = typeof values[1] !== 'number' ? valuesRange.max : values[1]

  return (
    <div>
          <InputNumber
          data-testid = "hi-report-range-selection"
            disabled={values.includes("_all_")}
            min={valuesRange.min}
            max={valuesRange.max}
            value={minInput}
            className="range-value-min"
            onChange={e=>props.onChange([e,maxInput])}
            />
          <InputNumber
            disabled={values.includes("_all_")}
            min={valuesRange.min}
            max={valuesRange.max}
            className="range-value-max"
            value={maxInput}
            onChange={e=>props.onChange([minInput, e])}
          />
      <Slider range={true} 
        disabled={values.includes("_all_")}
        value={[minInput, maxInput]} 
        min={valuesRange.min} max={valuesRange.max}
        onChange={props.onChange}  />
    </div>
  )
}

export default RangeSelection;


