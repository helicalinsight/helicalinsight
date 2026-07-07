import { Tooltip } from 'antd'
import React from 'react'
import { uuid } from '../../../../../../utils/uuid'


let commonLineStyles = {
    zIndex: 99,
    cursor: 'pointer',
    position: 'absolute',
}

const CustomReferenceLine = (props) => {
    const { width, top, label = "", tooltip = "", height, referenceLineAtY = true, positionFactor = 0 } = props || {}

    const referenceLineStylesX = {
        height: `${height}px`,
        width: '1px',
        borderRight: `1px solid rgb(255,0,0)`,
        left: `${top}px`,
        top: positionFactor,
        ...commonLineStyles
    }

    const referenceLineStylesY = {
        width: `${width}px`,
        height: '1px',
        borderBottom: `1px solid rgb(255,0,0)`,
        top: `${top}px`,
        right: positionFactor,
        ...commonLineStyles
    }

    return (
        <div style={referenceLineAtY ? referenceLineStylesY : referenceLineStylesX} data-testid="hr-reference-line" key={uuid()} />
    )
}

export default CustomReferenceLine