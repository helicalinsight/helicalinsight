import { ArrowLeftOutlined, InfoCircleOutlined } from '@ant-design/icons'
import { Tooltip } from 'antd'
import './info-container.scss'

const InfoContainer = (props = {}) => {
    const { pane = "", onClose = () => { }, tooltip = "Minimize this pane", icon = false, iconTooltip = null } = props || {}
    return (
        <div className='hi-instant-bi-info-container'>
            <div>
                <span>{pane}
                    {icon && <Tooltip placement='topLeft' title={iconTooltip}><InfoCircleOutlined size={10} style={{ marginLeft: 4, cursor: 'pointer', }} /></Tooltip>}
                </span>
            </div>
            <div>
                <Tooltip title={tooltip}>
                    <span onClick={onClose} className='hi-ib-close'>
                        <ArrowLeftOutlined />
                    </span>
                </Tooltip>
            </div>
        </div>
    )
}

InfoContainer.defaultProps = {
    pane: "",
    value: null,
    onClose: () => { }
}

export default InfoContainer