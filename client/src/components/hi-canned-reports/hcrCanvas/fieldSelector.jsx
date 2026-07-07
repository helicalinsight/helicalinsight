import { Input, Popover } from 'antd';
import { useState } from 'react';
import CustomCascader from './customCascader';
import "./custom-cascader.scss";
import { cloneDeep } from 'lodash';

const { TextArea } = Input

const FieldSelector = (props = {}) => {
    const { onChange = () => { }, options = [], value = "", placeholder = "", disabled = false, styles = {}, appendValue = true } = props || {}
    const [open, setOpen] = useState(false)

    const handleInputChange = (e) => {
        const value = e.target.value || "";
        onChange({ value });
    }

    const handleCascadeValueChange = (val = {}) => {
        const valueObj = cloneDeep(val)
        if (appendValue) {
            valueObj.value = value + (val?.value || "")
        }
        onChange(valueObj);
        setOpen(false)
    }

    const content = (
        <CustomCascader
            options={options}
            onChange={handleCascadeValueChange}
            open={open}
        />
    )

    return (
        <Popover
            content={content}
            trigger="click"
            open={open}
            onOpenChange={(nOpen) => setOpen(nOpen)}
            showArrow={false}
            placement="bottomLeft"
            overlayClassName='ant-popover-bottom-left'
        >
            <TextArea rows={3} placeholder={placeholder} value={value} onChange={handleInputChange} disabled={disabled} style={styles} data-testid="hcr-properties-fieldSelector" />
        </Popover>
    )
}

export default FieldSelector