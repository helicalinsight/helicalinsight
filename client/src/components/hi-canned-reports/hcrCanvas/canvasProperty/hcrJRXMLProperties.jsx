import { Space, Switch, Tooltip } from 'antd';
import { useDispatch, useSelector } from 'react-redux';
import { hcrActions } from '../../../../redux/actions';

const {
    hcrUpdateJRXMLProperty
} = hcrActions

const JRXMLProperties = (props = {}) => {
    const { getLabel = () => { } } = props;
    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};
    const { enableJRXML = false } = activeTab || {}

    const dispatch = useDispatch();

    const handlePropertyChange = (value) => {
        dispatch(hcrUpdateJRXMLProperty({ value }))
    }

    return (
        <div>
            <Space style={{ width: 150, alignItems: 'flex-end' }}>
                <div className="parameter-label">
                    <Tooltip title={"if enabled, it will generate JRXML file when preview report."}>
                        Enable JRXML
                    </Tooltip>
                </div>
                <Switch
                    checked={enableJRXML}
                    onChange={(value) => {
                        handlePropertyChange(value)
                    }}
                />
            </Space>
        </div>
    )
}

export default JRXMLProperties