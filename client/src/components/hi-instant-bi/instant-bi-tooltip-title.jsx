import { Tooltip } from 'antd'

const InstantBITooltip = ({ title, children, ...rest }) => {
    return (
        <Tooltip
            title={() => (
                <span className='instant-bi-tooltip-title'>{title}</span>
            )}
            {...rest}
        >
            {children}
        </Tooltip>
    )
}

export default InstantBITooltip