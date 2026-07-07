import {
    DownOutlined,
    LineChartOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, Menu, Tooltip } from 'antd';
import { toTitleCase } from '../../../../../../utils/text-utils';

const referenceOptions = {
    Line: [
        { name: 'Line', icon: <LineChartOutlined /> },
    ]
}


const ReferenceLineList = (props) => {
    let { reference = "Line" } = props
    const hanldeClick = item => {
        props.selectItem(item.name)
    }
    let sortedList = referenceOptions[reference].sort((a, b) => a.name > b.name ? 1 : -1)
    const menu = (
        <Menu className="sub-viz-list" selectedKeys={[reference]} >
            {sortedList.map(item => {
                return (
                    <Menu.Item
                        key={item.name}
                        icon={item.icon}
                        onClick={() => hanldeClick(item)}
                    >
                        <span data-testid={`sub-viz-type-${item.name}`} >{toTitleCase(item.name)}</span>
                    </Menu.Item>

                )
            })}
        </Menu>
    )
    return (
        <Dropdown
            className='sub-viz-selected'
            icon={<DownOutlined />}
            overlay={menu}
            trigger={["click"]}
        >
            <Button data-testid="selected-sub-viz-type" >
                {toTitleCase(reference)}
            </Button>
        </Dropdown>

    )
}

export default ReferenceLineList