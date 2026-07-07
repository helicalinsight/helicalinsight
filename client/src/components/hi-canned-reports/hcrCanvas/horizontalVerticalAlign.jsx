import { AlignCenterOutlined, AlignLeftOutlined, AlignRightOutlined, VerticalAlignBottomOutlined, VerticalAlignMiddleOutlined, VerticalAlignTopOutlined } from "@ant-design/icons";
import { Button, Space, Tooltip } from "antd";
import HIIcon from "../../common/icons/hi-icons";

export default function HorizontalVerticalAlign({ nodeValues, onPropertyChange, showJustify = true }) {
    return <div className="property-group-wrapper">
        <div className="property-group">Alignment</div>
        <Space className="gap-15">
            <div style={{ display: 'flex' }}>
                <Button type={nodeValues?.horizontalAlign === 'left' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'horizontalAlign', value: 'left' }); }} icon={<Tooltip title="Left Align">
                    <AlignLeftOutlined />
                </Tooltip>} size={'small'} />
                <Button type={nodeValues?.horizontalAlign === 'center' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'horizontalAlign', value: 'center' }); }} icon={<Tooltip title="Center Align">
                    <AlignCenterOutlined />
                </Tooltip>} size={'small'} />
                <Button type={nodeValues?.horizontalAlign === 'right' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'horizontalAlign', value: 'right' }); }} icon={<Tooltip title="Right Align">
                    <AlignRightOutlined />
                </Tooltip>} size={'small'} />
                {showJustify && <Tooltip title="Horizontal Justify">
                    <Button type={nodeValues?.horizontalAlign === 'justified' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'horizontalAlign', value: 'justified' }); }} icon={<HIIcon name="hi-hcr-horizontal-justify" />}
                        size={'small'} />
                </Tooltip>}
            </div>
            <div style={{ display: 'flex' }}>
                <Button type={nodeValues?.verticalAlign === 'top' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'verticalAlign', value: 'top' }); }} icon={<Tooltip title="Top Align">
                    <VerticalAlignTopOutlined />
                </Tooltip>} size={'small'} />
                <Button type={nodeValues?.verticalAlign === 'middle' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'verticalAlign', value: 'middle' }); }} icon={<Tooltip title="Middle Align">
                    <VerticalAlignMiddleOutlined />
                </Tooltip>} size={'small'} />
                <Button type={nodeValues?.verticalAlign === 'bottom' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'verticalAlign', value: 'bottom' }); }} icon={<Tooltip title="Bottom Align">
                    <VerticalAlignBottomOutlined />
                </Tooltip>} size={'small'} />
                {showJustify && <Tooltip title={<>
                    Vertical Justify
                    <div>Note: By selection of this property, there won't be any difference visually. It's effect can be seen in preview</div>
                </>} placement="left">
                    <Button type={nodeValues?.verticalAlign === 'justified' ? 'primary' : 'default'} onClick={() => { onPropertyChange({ key: 'verticalAlign', value: 'justified' }); }} icon={<HIIcon name="hi-hcr-vertical-justify" />} size={'small'} />
                </Tooltip>}
            </div>
        </Space>
    </div>
}