import { Tooltip } from 'antd'

const InstantBITooltip = ({ title, children }) => {
    return (
        <Tooltip title={() => {
            return (
                <span className='instant-bi-tooltip-title'>{title}</span>
            )
        }}>
            {children}
        </Tooltip>

    )
}

export default InstantBITooltip