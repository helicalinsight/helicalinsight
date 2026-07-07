import {
  FileTextOutlined,
  NumberOutlined
} from '@ant-design/icons';
import { Card, Col, Dropdown, Menu, Row, Tag, Tooltip, Typography } from "antd";
import React from 'react';
import { useDrag } from 'react-dnd';
import { MEASURE_NAME, MEASURE_VALUE } from '../../../../../../redux/reducers/initialStates';
import { getFieldDisplayName } from "../../../../../../utils/utilities";
import { dropIntoMarks, getActiveMarksOptionForMeasureField } from '../../../utils/marks-utils';
import { toTitleCase } from '../../../../../../utils/text-utils';
import { addFieldToCanvas } from '../../../../../../redux/actions/hreport.actions';
import { useDispatch } from 'react-redux';

const { Text } = Typography


const MeasureFields = (props) => {
  if (!props?.filteredFields?.length) return null;
  let { measures: { fields: measureFields = [] } } = props || {}
  return (
    <Card
      size='small'
      className={"hi-mark-field"}
      data-testid="hr-measure_fields_card"
    >
      <Row>
        {measureFields?.map((field, _i, _arr) => {
          return (
            <Col className={_i <= _arr.length - 1 ? "hr-measure-field" : ""}>
              <MeasureField field={field} {...props} />
            </Col>
          )
        })}
      </Row>
    </Card>
  )
}

export default MeasureFields


export const MeasureField = (props) => {
  const { field = {}, marksList, selectedType, customChart, reportFields = [] } = props || {}
  let fieldName = getFieldDisplayName(field)
  const dispatch = useDispatch()
  const { column } = field
  let backgroundColor = column === MEASURE_NAME ? "#337ab7" : "#4CAF50"

  let dataTypeIcon = null
  if (column === MEASURE_NAME) {
    dataTypeIcon = <FileTextOutlined />
  }
  if (column === MEASURE_VALUE) {
    dataTypeIcon = <NumberOutlined />
  }

  const [{ }, drag] = useDrag(
    () => {
      return {
        type: "metadata_field",
        item: { type: "metadata_field", field },
        collect: (monitor) => ({
          opacity: monitor.isDragging() ? 0.5 : 1
        })
      }
    },
    [field]
  )

  const handleAdd = (data, addedAs) => {
    dispatch(addFieldToCanvas({ ...data, addedAs }))
  }

  const addToMarks = markType => {
    let mark = marksList[0]
    let data = { type: "metadata_field", field, markType, mark, fields: reportFields }
    dropIntoMarks(data, dispatch)
  }

  const marks = getActiveMarksOptionForMeasureField({ marksList, selectedType, customChart, fields: reportFields })
  const contextMenu = (
    <Menu className="hi-metadata-field-menu">
      <Menu.Item
        key="addToColumns"
        onClick={() => handleAdd(field, "column")}
        value={field}
      >
        Add To Columns
      </Menu.Item>
      <Menu.Item
        key="addToRows"
        onClick={() => handleAdd(field, "row")}
        value={field}
        className="add-to-rows"
      >
        Add To Rows
      </Menu.Item>
      {selectedType && (
        <Menu.SubMenu
          key="addToMarks"
          title="Add To Marks"
          popupClassName="metafield-field-sub-menu"
        >
          {marks.length > 0 ? (
            marks.map((markType) => {
              if (true) {
                return (
                  <Menu.Item
                    key={markType}
                    onClick={() => addToMarks(markType, marksList[0])}
                  >
                    {toTitleCase(markType)}
                  </Menu.Item>
                );
              }
            })
          ) : (
            <Menu.Item>{"No items found!"}</Menu.Item>
          )}
        </Menu.SubMenu>
      )}
    </Menu>
  );

  return (
    <Dropdown
      overlay={contextMenu}
      trigger={['contextMenu']}
    >
      <Tag
        closable={false}
        ref={drag}
        color={backgroundColor}
        icon={dataTypeIcon}
        key={column}
      >
        <Tooltip title={"Use this field in rows/columns/marks."} >
          <Text
            ellipsis={true}
            className="field-label" >
            {fieldName}
          </Text>
        </Tooltip>
      </Tag>
    </Dropdown>
  )
}