import { Collapse, Typography } from 'antd';
import React from 'react';
import { useDispatch } from 'react-redux';
import { getFieldDisplayName } from '../../../../../../utils/utilities';
import ReferenceLineList from './reference-line-list';
import ReferenceLineMark from './reference-line-mark';
const { Panel } = Collapse;
const { Text } = Typography;



const ReferenceLineField = (props) => {
    const { referenceLineList, fields } = props || {}
    const dispatch = useDispatch()
    const handleSelect = () => { }
    return (
        <div className='hr-reference-line-container'>
            <Text level={6}>Reference</Text>
            <Collapse>
                {referenceLineList?.map(rField => {
                    let field = fields.find(field => field.id === rField.id)
                    let display = rField.display;
                    if (field) {
                        display = getFieldDisplayName(field)
                    }
                    let rProps = { ...rField }
                    return (
                        <Panel key={rField.id} header={display} >
                            {rField?.label === "All" && <ReferenceLineList selectItem={handleSelect} />}
                            <ReferenceLineMark  {...rProps} />
                        </Panel>
                    )
                })}
            </Collapse>
        </div>
    )
}

export default ReferenceLineField