import { Checkbox, Collapse, Row, Tooltip } from 'antd';
import React from 'react';
import { useDispatch } from 'react-redux';
import { enableMeasureMarks } from '../../../../../../redux/actions/hreport.actions';
import HITooltip from '../../../../../common/components/hi-tooltip';
import MeasureMarks from './measure-marks';
import MeasureFields from './measure-fields';
import "./measure.scss"
import { InfoCircleOutlined } from '@ant-design/icons';

const { Panel } = Collapse;

const MeasureNamesAndValues = (props) => {
    const { measures = {}, fields = [] } = props || {}
    const { enable } = measures || {}
    let filteredFields = fields?.filter((item) => item?.addedAs === "measure_field")
    const dispatch = useDispatch()
    const enableMeasures = () => {
        dispatch(enableMeasureMarks({ enable: !enable }))
    }

    const panelHeader = (
        <div>
            <span>Measure Names and Values</span>
            <Tooltip title={"Drop measures here from metadata or fields. Custom measures can also be added. If the same field exists in the fields area and is dropped here, the fields area version won't be used for visualization. Drag Measure_Value to rows/columns to display its value."}>
                <InfoCircleOutlined style={{ marginLeft: 4, opacity: 0.8 }} />
            </Tooltip>
        </div>
    )

    return (
        <div className='hr-measure-name-and-values-container'>
            <Row>
                <Checkbox
                    checked={enable}
                    onChange={enableMeasures}
                ><HITooltip title="Enable Measure Names and Measure Values">Measures</HITooltip></Checkbox>
            </Row>
            {enable && <Collapse defaultActiveKey={['1']}>
                <Panel header={panelHeader} key={1}>
                    <MeasureMarks fields={filteredFields} />
                    <MeasureFields {...{ measures }} filteredFields={filteredFields} {...props} reportFields={fields} />
                </Panel>
            </Collapse>}
        </div>
    )
}

export default MeasureNamesAndValues