import { Table, Typography, Tooltip, Col, Row, Space, Divider, Dropdown, Menu, } from "antd";
import { TableOutlined, CaretRightOutlined, CaretDownOutlined, NumberOutlined, CalendarOutlined, FileTextOutlined, CheckOutlined } from "@ant-design/icons";
import { useDrag } from "react-dnd";

const { Text, Paragraph } = Typography;

const contextMenu = ({ item = {} }) => (
  <Menu className="hi-metadata-field-menu">
    <Menu.Item
      key="addToColumns"
      onClick={() => 'handleAdd(item, "column")'}
      value={item}
    >
      Add To Columns
    </Menu.Item>
    <Menu.Item
      key="addToRows"
      onClick={() => 'handleAdd(item, "row")'}
      value={item}
      className="add-to-rows"
    >
      Add To Rows
    </Menu.Item>
    <Menu.Item
      key="addToFilters"
      onClick={() => 'addFilter(item)'}
      value={item}
    >
      Add To Filters
    </Menu.Item>
  </Menu>
);

export function HrCubeDrag({ record }) {
  const [{ }, drag] = useDrag(() => {
    return {
      type: "hrCubeData",
      item: { ...record },
      collect: (monitor) => ({
        opacity: monitor.isDragging() ? 0.5 : 1
      })
    }
  }, []);


  return (
    <div ref={drag} className='record-drag' key={record.key}>
      <Dropdown
        overlay={contextMenu({ item: record })}
        trigger={['contextMenu']}
      >
        <Tooltip title={record.alias}>
          <Text style={{ width: '140px', marginBottom: 0 }} ellipsis={true}>
            {record.alias}
          </Text>
        </Tooltip>
      </Dropdown>
      {/* <Divider /> */}
    </div>
  );
}