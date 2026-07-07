
import { Menu, Dropdown, Typography, Tooltip } from "antd"
// import { v4 as uuidv4 } from "uuid";
import { useDispatch, useSelector } from "react-redux";
import { NumberOutlined, CalendarOutlined, FileTextOutlined, CheckOutlined } from "@ant-design/icons";
import { useDrag } from 'react-dnd'
import { dateTypes, markTypes } from "../../hi-reports/utils/constants";
import { dropIntoMarks } from "../../hi-reports/hi-editing-area/utils/marks-utils";
import { toTitleCase } from "../../../utils/text-utils"
import { checkIfCardViz } from "../../../utils/utilities";
import { addFieldToCanvas, createFilter } from "../../../redux/actions/hreport.actions";

const { Text } = Typography;


const MetadataField = props => {
  let { item, module, isHrSidebarCube } = props
  const isCubeLikeModule = module === 'cube' || module === 'agent';
  // console.log({ item })
  // let { searchText } = metadata

  const dispatch = useDispatch()
  let { fields, marksList, selectedType, customChart, properties: { progress: { chartType: progressChartType = '' } = {} } = {} } = useSelector(state => {
    let activeReport = state.hreport.present.reports.find(report => report.active)
    return activeReport || {};
  })
  const hrSidebar = useSelector((state) => state.hreport.present.hrSidebar);
  selectedType = selectedType || "Table"
  const [{ }, drag] = useDrag(
    () => {
      // console.log(item, 'drag console')
      return {
        type: isCubeLikeModule ? "metadataRowChild" : "metadata_field",
        item: isCubeLikeModule ? { ...item } : { type: "metadata_field", field: item },
        collect: (monitor) => ({
          opacity: monitor.isDragging() ? 0.5 : 1
        })
      }
    },
    []
  )
  const handleAdd = (data, addedAs) => {
    dispatch(addFieldToCanvas({ ...data, addedAs }))
  }
  const addFilter = data => {
    dispatch(createFilter({ ...data, columnID: data?.column?.id, from: "metadata" })) // refactor
  }
  const addToMarks = markType => {
    let mark = marksList[0]
    let data = { type: "metadata_field", field: item, markType, mark, fields }
    dropIntoMarks(data, dispatch)
  }
  let { subVizType } = isCubeLikeModule || marksList[0];
  let marks = markTypes
  if (["Table", "CrossTab", "S2Chart"].includes(selectedType)) {
    marks = ["color", "size"]
  }
  if (selectedType === "GridChart") {
    marks = ["color", "label", "tooltip"]
    if (subVizType === "point") {
      marks = markTypes
    }
    if (subVizType === "arc") {
      marks = ["color", "tooltip", "label"]
    }
  }
  if (selectedType === "Antcharts") {
    if (["arc", "area", "line", "bar", "doughnut"].includes(subVizType) || subVizType === '') {
      marks = ["color", "label", "tooltip"]
    } else if (["point"].includes(subVizType)) {
      marks = ["color", "label", "shape", "size", "tooltip"]
    } else if (["text"].includes(subVizType)) {
      marks = ["color", "size", "tooltip"]
    } else if (["treemap", "waterfall", "relation"].includes(subVizType)) {
      marks = ["color", "label", "tooltip"]
    } else if (["radar"].includes(subVizType)) {
      marks = ["label", "tooltip", "color"]
    } else if (["progress"].includes(subVizType)) {
      switch (progressChartType) {
        case "ring":
          marks = []
          break;
        case "gauge":
          marks = ['color']
          break;
        case "bullet":
          marks = ["color", "label"]
          break;
        default:
          marks = []
          break;
      }
    } else if (["calendar"].includes(subVizType)) {
      marks = ['color', 'label', 'size', 'tooltip']
    } else {
      marks = []
    }
  }
  let isCardViz = !isCubeLikeModule && ["area", "line", "bar", "table", ""].includes(subVizType) && checkIfCardViz(fields, selectedType)
  if (isCardViz) {
    marks = ["detail"]
  }

  if (selectedType === "Card") {
    marks = ["detail"]
  }

  if (selectedType === "MapChart") {
    marks = ["color", "label", "size", "tooltip", "shape"]
    if (subVizType === "heatmap") {
      marks = ["label", "size"]
    }
    if (subVizType === "line") {
      marks = ["color", "size", "tooltip"]
    }
  }
  if (customChart?.selected && customChart?.applied) {
    marks = ["color", "label", "shape", "size", "tooltip", "detail"]
  }
  const contextMenu = (
    <Menu className="hi-metadata-field-menu">
      <Menu.Item
        key="addToColumns"
        onClick={() => handleAdd(item, "column")}
        value={item}
      >
        Add To Columns
      </Menu.Item>
      <Menu.Item
        key="addToRows"
        onClick={() => handleAdd(item, "row")}
        value={item}
        className="add-to-rows"
      >
        Add To Rows
      </Menu.Item>
      <Menu.Item
        key="addToFilters"
        onClick={() => addFilter(item)}
        value={item}
      >
        Add To Filters
      </Menu.Item>
      {selectedType && selectedType !== "SyncChart" && (
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
  let dataType = !isHrSidebarCube && Object.values(item.type)[0]

  return (
    <div ref={drag} data-testid="hi-metadata-field-dropdown" className='metadata-dragable'>
      <Dropdown
        overlay={isCubeLikeModule ? <Menu items={[]} /> : contextMenu}
        trigger={['contextMenu']}
      >
        <div className="hr-metadata-label" onDoubleClick={() => handleAdd(item, "column", dispatch)} >
          <Tooltip title={item.alias} >
            <span >
              {!isHrSidebarCube && <span className="hr-metadata-field-icon" >
                {dataType === "numeric" && <NumberOutlined />}
                {['text', 'other'].includes(dataType) && <FileTextOutlined />}
                {dataType === "boolean" && <CheckOutlined />}
                {dateTypes.includes(dataType) && <CalendarOutlined />}
              </span>}
              <Text className="hr-metadata-ellipsis" ellipsis={true} data-testid={`columnname-${item.alias}`} >{item.alias}</Text>
            </span>
          </Tooltip>
        </div>
      </Dropdown>
    </div>
  )
}

export default MetadataField