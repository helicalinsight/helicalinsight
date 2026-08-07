import { Tooltip } from 'antd'
import React, { useState } from 'react'
import { FieldItem } from '../../../hcrFields'
import { OutlineDSContextMenu, OutlineTableContextMenu } from './outlineContextMenu'

const OutlineTitle = (props = {}) => {
    const { title = null, data = {}, draggable = false, contextMenu, dsContextMenu = false, ...rest } = props || {}
    const [visible, setVisible] = useState(null)

    function getTooltipTitle(title, tooltip) {
        return <Tooltip title={tooltip} placement="topRight"><div>{title}</div></Tooltip>
    }

    if (draggable) {
        let titleToRender = (
            <div>
                <FieldItem field={data} />
            </div>
        )
        if (!dsContextMenu) return titleToRender;
        return (
            <OutlineDSContextMenu
                title={titleToRender}
                visible={visible}
                onVisibleChange={(value) => setVisible(value)}
                {...rest}
            />

        )
    }
    if (contextMenu) {
        const { deleted } = rest || {}
        return (
            <OutlineTableContextMenu
                onVisibleChange={(value) => setVisible(value)}
                visible={visible}
                title={<div style={{ opacity: deleted ? 0.5 : 1 }}>{title}</div>}
                {...rest}
            />
        )
    }
    if (dsContextMenu) {
        return (
            <OutlineDSContextMenu
                title={props.tooltip ? getTooltipTitle(title, props.tooltip) : <div>{title}</div>}
                visible={visible}
                onVisibleChange={(value) => setVisible(value)}
                {...rest}
            />

        )
    }
    if (props.tooltip) {
        return getTooltipTitle(title, props.tooltip)
    }
    return title;
}

export default OutlineTitle