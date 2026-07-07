import React, { useState, useRef, useMemo } from "react";
import {
  Table,
  Typography,
  Input,
  InputNumber,
  Select,
  Menu,
  Radio,
  Checkbox,
  DatePicker,
  Upload,
  Button,
  Popover,
  Slider,
  Switch,
  Row,
  Col,
  Tooltip,
  TimePicker,
  Cascader,
} from "antd";
import {
  UploadOutlined,
  SortAscendingOutlined,
  SearchOutlined,
  SortDescendingOutlined,
  SwapOutlined,
  QuestionCircleOutlined,
  PlusCircleOutlined,
  PlusOutlined,
  WarningOutlined,
} from "@ant-design/icons";
import { SketchPicker, ChromePicker, PhotoshopPicker } from "react-color";
import { useDebounce } from "../../../hooks";
import ReactQuill, { Quill } from "react-quill";
import { Controlled as CodeMirror } from "react-codemirror2";
import "../../../../node_modules/codemirror/lib/codemirror.css";
import "../../../../node_modules/codemirror/theme/material.css";
import "../../../../node_modules/codemirror/mode/xml/xml.js";
import "../../../../node_modules/codemirror/mode/javascript/javascript.js";
import "../../../../node_modules/codemirror/mode/css/css.js";
import "react-quill/dist/quill.snow.css";
import PropTypes from "prop-types";
import moment from "moment";
import { useEffect } from "react";
import "./index.scss";
import dayjs from "dayjs";
import ShortCutText from "../hi-shortcuts/hi-shortcuts";
import ImageResize from 'quill-image-resize-module-react';
import customParseFormat from 'dayjs/plugin/customParseFormat'
import { formats, rgbaToHex, toolbarOptions } from "./utils/react-quill-configurations";
import { useDispatch, useSelector } from "react-redux";
import { setShowMoreIcons } from "../../../redux/actions/property-pane.actions";
import JsDrawer from "../../hi-dashboard-designer/components/js-drawer.jsx";

dayjs.extend(customParseFormat)

Quill.register('modules/imageResize', ImageResize);
const { Text } = Typography;
const { Option } = Select;
const { TextArea } = Input;

const HIPropertyPane = (props) => {
  let {
    items,
    group,
    groupId,
    infoItems,
    infoPanelItems,
    newKey,
    defaultSortingOrder,
    keysPressed,
    altTriggered,
    shortCutConfig,
    menuWidth,
    menuToolbarWidth,
  } = props;
  const { apply, reset, search } = shortCutConfig || "";
  newKey = newKey ? newKey : 1;
  const dispatch = useDispatch()
  const [itemsData, setItemsData] = useState([]);
  const [currentGroupId, setCurrentGroupId] = useState(groupId);
  const sortingOrder = ["ascending", "descending", false];
  const [toSort, toggleSort] = useState(
    defaultSortingOrder
      ? sortingOrder[sortingOrder.indexOf(defaultSortingOrder)]
      : sortingOrder[2]
  );
  const [searchTerm, setSearchTerm] = useState("");
  const [selecteDropdownOpen, setSelecteDropdownOpen] = useState(false);
  const showMoreCardIcons = useSelector((state) => state.propertyPane.showMore);
  const SEARCH_DELAY = 500;
  const debouncedSearchTerm = useDebounce(searchTerm, SEARCH_DELAY);
  const currentInfoItem = infoItems?.find(
    (item) => item.key === currentGroupId
  );
  const currentInfoPanelItem = infoPanelItems?.find(
    (item) => item.key === currentGroupId
  );

  const itemsRef = useRef(null);
  const quillRef = useRef(null);
  const customColor = itemsData?.find(item => item.groupId === "header" && item.key === "customColor")?.value
  const reactQuillModules = useMemo(() => {
    const modules = {
      toolbar: {
        container: toolbarOptions({ customColor }), handlers: {
          'color': function (value) {
            if (value === 'custom-color') value = rgbaToHex(customColor);
            this.quill.format('color', value);
          },
          'background': function (value) {
            if (value === 'custom-color') value = rgbaToHex(customColor);
            this.quill.format('background', value);
          }
        }
      },
      imageResize: {
        parchment: Quill.import('parchment'),
        modules: ['Resize', 'DisplaySize']
      }
    }
    return modules
  }, [customColor]);

  const gridItem = useSelector(state => state.designer.present.gridItemsData.find(item => item.id === state.designer.present.gridItemId));

  useEffect(() => {
    setItemsData(items);
    itemsRef.current = items;
  }, [newKey]);

  useEffect(() => {
    altTriggered &&
      group.forEach((item) => {
        if (
          item.shortcuts?.some((item) =>
            keysPressed
              .join("")
              .toLowerCase()
              .endsWith(item.join("").toLowerCase())
          )
        ) {
          setCurrentGroupId(item.key);
        }
      });
  }, [keysPressed]);

  useEffect(() => {
    setCurrentGroupId(groupId);
  }, [groupId]);

  const sortingFunc = () => {
    switch (toSort) {
      case sortingOrder[2]:
        toggleSort(sortingOrder[0]);
        break;
      case sortingOrder[0]:
        toggleSort(sortingOrder[1]);
        break;
      case sortingOrder[1]:
        toggleSort(sortingOrder[2]);
        break;
      default:
        toggleSort(sortingOrder[2]);
        break;
    }
  };

  const onShowMoreClick = () => {
    dispatch(setShowMoreIcons(!showMoreCardIcons))
  }

  const dropdownRender = (menu, record) => {
    if (record.showMore) {
      return (<div>
        {menu}
        <div style={{ padding: '3px', color: showMoreCardIcons ? "blue" : "gray", textAlign: 'center', cursor: "pointer", }} onClick={onShowMoreClick}>
          Show More
        </div>
      </div>)
    }
    return (<div className="hr-report-dropdown-renderer">
      {menu}
    </div>
    )
  };


  const renderElement = (text, record) => {
    switch (text) {
      case "Input":
        return (
          <Input
            value={record?.value?.toString()}
            data-testid={record?.dataTestId ? record.dataTestId : null}
            placeholder={record?.placeholder}
            onChange={(e) => {
              collectData({
                value: e.target.value === null ? "" : e.target.value,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            className="hi-width-100"
            size={record.size ? record.size : "small"}
          />
        );
      case "InputNumber":
        return (
          <InputNumber
            defaultValue={record?.value}
            max={record?.maxInput}
            min={record?.minInput}
            value={record?.value}
            disabled={record?.disabled || false}
            onChange={(value) => {
              collectData({
                value: value === null ? 0 : record?.step ? value : parseInt(value),
                key: record.key,
                groupId: record.groupId,
                record, // required muliple keys in hcr, So sending record(Modified by narendra)
              });
            }}
            className="hi-width-100"
            size={record.size ? record.size : "small"}
            step={record?.step}
            stringMode={record?.stringMode}
          />
        );
      case "TimePicker":
        return (
          <TimePicker
            // 6351 - fix start
            value={record?.value ? dayjs(`${record?.value}`, 'HH:mm:ss') : dayjs(`00:00:00`, 'HH:mm:ss')}
            defaultValue={record?.defaultValue ? dayjs(`${record.defaultValue}`, "HH:mm:ss") : dayjs(`00:00:00`, "HH:mm:ss")}
            onSelect={(time) => {
              collectData({
                value: time.format('HH:mm:ss'), // 6351 - fix end
                // timeString === null || timeString === ""
                //   ? record?.defaultValue
                //     ? record.defaultValue
                //     : "00:00:00"
                //   : timeString,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            showNow={record?.showNow}
            className="hi-width-100"
            size={record.size ? record.size : "small"}
          />
        );
      case "Select":
        let selectProps = {
          showSearch: true,
          disabled: record?.disabled ? true : false,
          mode: record?.multiSelect && "multiple",
          allowClear: (record?.multiSelect && true) || record?.allowClear,
          defaultValue: record?.selectedKey ? record?.selectedKey : record?.multiSelect ? record?.value : null,
          onChange: (value) => {
            collectData({
              value,
              key: record.key,
              groupId: record.groupId,
              record,
            });
          },
          value: record?.value,
          className: "hi-width-100 hi-input-number",
          size: record?.size ? record.size : "small",
          dropdownRender: (menu) => dropdownRender(menu, record),
          onClear: record?.onClear,
          onDropdownVisibleChange: (open) => {
            setSelecteDropdownOpen(open)
          }
        }

        if (record?.isGroupType) {
          return <Select {...selectProps} options={record?.options} />
        }
        const spaces = <>&nbsp;&nbsp;&nbsp;&nbsp;</>

        return (
          <Select {...selectProps}>
            {record.values?.map((item) => {
              return <Option key={item?.key} value={item?.key}>
                <Tooltip
                  title={record?.dropdownTooltipEnabled ? item?.label : item?.tooltip}
                  placement="left"
                >
                  {(item?.space && selecteDropdownOpen) ? spaces : null}
                  {item?.label}
                </Tooltip>
              </Option>
            })}
          </Select>
        );
      case "Radio":
        return (
          <Radio.Group
            // defaultActiveFirstOption={true}
            value={record?.value}
            onChange={(e) => {
              collectData({
                value: e.target.value,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            className="hi-width-100 hi-input-number"
            size={record.size ? record.size : "small"}
          >
            {record?.values?.map((item) => (
              <Radio key={item.key} value={item.key}>
                {item.label}
              </Radio>
            ))}
          </Radio.Group>
        );
      case "Checkbox":
        return (
          <Checkbox
            // defaultActiveFirstOption={true}
            onChange={(e) => {
              collectData({
                value: e.target.checked,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            className="hi-width-100 hi-input-number"
            size={record.size ? record.size : "small"}
          >
            {record.value}
          </Checkbox>
        );
      case "DatePicker":
        return (
          <DatePicker
            onChange={(value, dateString) => {
              collectData({
                value: dateString,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            showTime={record?.showTime && true}
            showNow={false}
            className="hi-width-100 hi-input-number"
            size={record.size ? record.size : "small"}
            defaultValue={record?.value && moment(record?.value, record?.showTime ? 'YYYY-MM-DD HH:mm:ss' : 'YYYY-MM-DD')}
          />
        );
      case "ColorPicker": {
        let presetColors = ['#FFFFFF', '#D0021B', '#F5A623', '#F8E71C', '#8B572A', '#7ED321', '#417505', '#BD10E0', '#9013FE', '#4A90E2', '#50E3C2', '#B8E986', '#000000', '#4A4A4A', '#9B9B9B'];
        if (record?.remove) {
          presetColors.push({ color: '#fafafa', title: "None" });
        }
        const isValidColor = record?.value && ('r' in record.value && 'g' in record.value && 'b' in record.value);
        return (
          <Popover
            trigger="click"
            id="hi-popover-padding-0"
            placement="bottom"
            content={
              <SketchPicker
                width="200px"
                color={record?.value || {}}
                onChangeComplete={(value) => {
                  let rgb = value.rgb;
                  if (value?.hex === '#fafafa' && record?.remove) {
                    rgb = {}
                  }
                  collectData({
                    value: rgb,
                    key: record.key,
                    groupId: record.groupId,
                    record,
                  });
                }}
                className="hi-color-picker"
                presetColors={presetColors}
              />
            }
          >
            <Row align="middle">
              <Col span={4}>
                <div
                  className="hi-color-dot"
                  style={{
                    backgroundColor: isValidColor ? `rgba(${record.value?.r},${record.value?.g},${record.value?.b},${record.value?.a})` : '#fafafa',
                  }}
                ></div>
              </Col>
              <Col span={20}>
                <Button className="hi-width-100">
                  {isValidColor ? <Tooltip
                    title={`R:${record.value?.r}
                  G:${record.value?.g}
                  B:${record.value?.b}
                  A:${record.value?.a}`}
                  >
                    <span className="hi-color-picker-text">
                      {`R:${record.value?.r}
                  G:${record.value?.g}
                  B:${record.value?.b}
                  A:${record.value?.a} `}
                    </span>
                  </Tooltip> : <span className="hi-color-picker-text">None</span>}
                </Button>
              </Col>
            </Row>
          </Popover>
        );
      }
      case "Upload":
        return (
          <Upload {...record?.value}>
            <Button
              size={record.size ? record.size : "small"}
              icon={<UploadOutlined />}
            >
              Click to Upload
            </Button>
          </Upload>
        );
      case "Slider":
        const { min, max, step, defaultValue, value } = record;
        return (
          <Slider
            size={record.size ? record.size : "small"}
            onChange={(value) => {
              collectData({
                value,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
            min={min}
            max={max}
            step={step}
            defaultValue={defaultValue}
            value={value}
          />
        );
      case "Switch":
        return (
          <>
            <Switch
              size={record.size ? record.size : "small"}
              checked={record?.value}
              onChange={(value) => {
                collectData({
                  value,
                  key: record.key,
                  groupId: record.groupId,
                  record,
                });
              }}
              disabled={record.disabled ?? false}
            />
            {record.showWarning ? <Tooltip title={record?.warningMessage} placement="left"><WarningOutlined style={{ color: "orange", marginLeft: 8 }} /></Tooltip> : null}
          </>
        );
      case "TextEditor":
        return (
          <div className="hi-editor">
            {
              <ReactQuill
                ref={quillRef}
                modules={reactQuillModules}
                value={record?.value}
                formats={formats}
                placeholder={record?.placeholder}
                preserveWhitespace
                onChange={(value) => {
                  collectData({
                    value,
                    key: record.key,
                    groupId: record.groupId,
                    record,
                  });
                }}
              />
            }
          </div>
        );
      case "NormalTextArea":
        return (
          <TextArea
            rows={record?.row || 4}
            value={record?.value || ""}
            placeholder={record?.placeholder || ""}
            onChange={(ele) => {
              collectData({
                value: ele.target.value,
                key: record.key,
                groupId: record.groupId,
                record,
              });
            }}
          />
        );
      case "CodeEditor":
        return (
          <div className="hi-editor">
            {
              <CodeMirror
                value={record?.value}
                options={{
                  mode: record?.mode,
                  // theme: "material",
                  lineNumbers: true,
                  autofocus: true,
                  autoRefresh: true,
                }}
                editorDidMount={(editor) => {
                  setTimeout(() => {
                    editor.refresh();
                    editor.focus();
                  });
                }}
                // editorWillUnmount={(editor) => {}}
                onBeforeChange={(editor, data, value) => {
                  collectData({
                    value,
                    key: record.key,
                    groupId: record.groupId,
                    record,
                  });
                }}
              // onChange={(editor, data, value) => {
              //   props.getData(value, record.key, record.groupId);
              // }}
              />
            }
          </div>
        );
      case "MultipleColorPicker": {
        let presetColors = ['#FFFFFF', '#D0021B', '#F5A623', '#F8E71C', '#8B572A', '#7ED321', '#417505', '#BD10E0', '#9013FE', '#4A90E2', '#50E3C2', '#B8E986', '#000000', '#4A4A4A', { color: 'transparent', title: 'OK' }];
        let node = document.querySelector('[title="OK"]');
        if (node) {
          node.innerText = "OK";
          node.style.width = 'max-content';
          node.style.height = '125%';
          node.style.padding = '0 12px';
        }
        return (
          <Popover
            trigger="click"
            id="hi-popover-padding-0"
            placement="bottom"
            content={
              <SketchPicker
                width="200px"
                presetColors={presetColors}
                color={record?.selectedColor}
                onChangeComplete={(value) => {
                  let hex = value?.hex;
                  if (hex !== 'transparent') {
                    record?.handleColorChange(hex);
                  } else {
                    let colorValues = [...record?.value, record?.selectedColor];
                    if (record?.value?.length < 10) {
                      collectData({
                        value: colorValues,
                        key: record.key,
                        groupId: record.groupId,
                        record,
                      });
                    }
                  }
                }}
                className="hi-color-picker"
              />
            }
          >
            <Row align="middle">
              <Col span={4}>
                <div
                  className="hi-color-dot"
                  style={{
                    backgroundColor: '#fff',
                  }}
                />
              </Col>
              <Col span={20}>
                <Select
                  mode={"multiple"}
                  defaultValue={record?.value}
                  value={record?.value}
                  className="hi-width-100 hi-input-number"
                  size={record.size ? record.size : "small"}
                  dropdownRender={() => { return null }}
                  dropdownStyle={{ padding: 0 }}
                  onChange={(value) => {
                    collectData({
                      value,
                      key: record.key,
                      groupId: record.groupId,
                      record,
                    });
                  }}
                />
              </Col>
            </Row>
          </Popover>
        )
      }
      case 'Button': {
        return (
          <Button
            type={record?.type || "primary"}
            size={record?.size || "small"}
            onClick={record?.onClick}
            icon={record?.icon || null}
            disabled={record?.disabled || false}
            >
            <Tooltip title={record.tooltip} placement="left">{record?.title || ''}</Tooltip>

          </Button>
        )
      }
      default:
        return null;
    }
  };

  const columns = [
    {
      title: "Name",
      dataIndex: "label",
      key: "name",
      ellipsis: true,
      width: currentGroupId === "crosstab" ? "60%" : "40%",
      render: (text, record) => {
        return (
          <>
            <Tooltip
              title={record?.tooltip ? record.tooltip : text}
              placement="topLeft"
            >
              <Text strong>
                {text}
                {record?.putAsterisk && <span style={{ color: "#1890ff" }}>*</span>}
              </Text>
            </Tooltip>
            {record?.showJSDrawerIcon && <JsDrawer compType={gridItem?.compType} isDashboard={record?.isDashboard} />}
          </>
        )
      },
    },
    {
      title: "Value",
      // ellipsis: true,
      dataIndex: "elementType",
      key: "value",
      render: (text, record) => renderElement(text, record),
    },
  ];

  const getSortedItems = (items) => {
    const itemsCopy = [
      ...items?.filter((item) => item?.groupId === currentGroupId),
    ];
    const itemsCopyWithoutEnable = [
      ...itemsCopy.filter((item) => item.key !== "enable"),
    ];
    switch (toSort) {
      case sortingOrder[0]:
        // const obj = new Query()
        //   .from(activeGroupItemsCopy)
        //   .sort("key", sortingOrder[0])
        //   .execute();
        const sortedItemsCopy = itemsCopyWithoutEnable.sort((a, b) =>
          a.label.localeCompare(b.label)
        );
        // console.log("sortedItemsCopy", sortedItemsCopy);
        sortedItemsCopy.unshift(
          itemsCopy.find((item) => item.key === "enable")
        );
        return sortedItemsCopy;

      case sortingOrder[1]:
        const sortedItemsReverse = itemsCopyWithoutEnable
          .sort((a, b) => a.label.localeCompare(b.label))
          .reverse();
        sortedItemsReverse.unshift(
          itemsCopy.find((item) => item.key === "enable")
        );
        return sortedItemsReverse;
      default:
        return itemsCopy;
    }
  };

  const getSearchedItems = (items) => {
    const itemsCopy = [
      ...items?.filter((item) => item?.groupId === currentGroupId),
    ];
    const itemsCopyWithoutEnable = [
      ...itemsCopy.filter((item) => item.key !== "enable"),
    ];
    if (debouncedSearchTerm === "") {
      return items;
    } else {
      // let filteredArray = new Query()
      //   .from(filteredCurrentItems)
      //   .where("label", "regex", debouncedSearchTerm)
      //   .execute();
      // temporary fix need to fix this in query utility
      // if (!Array.isArray(filteredArray)) {
      //   filteredArray = [filteredArray];
      // }
      let filteredArray = itemsCopyWithoutEnable.filter((item) =>
        item.label.toLowerCase().includes(debouncedSearchTerm.toLowerCase())
      );
      filteredArray.unshift(itemsCopy.find((item) => item.key === "enable"));
      return filteredArray;
    }
  };

  const sortedItems = getSortedItems([...itemsData]);
  // console.log("sortedItems", sortedItems);
  const searchedItems = getSearchedItems([...sortedItems]);

  const onResetForm = () => {
    if (typeof props.onReset === "function") {
      props.onReset({ groupId: currentGroupId, itemsRef, itemsData });
    }
    setItemsData([...itemsRef.current]);
  };

  const collectData = ({ value, key, groupId, record }) => {
    setItemsData((prevItems) => {
      return [...prevItems].map((item) => {
        if (item?.groupId === groupId && item?.key === key) {
          // item.value = e.target.value === null ? "" : e.target.value;
          return {
            ...item,
            value,
          };
        }
        return item;
      });
    });
    if (typeof props.getData === "function") {
      props.getData({
        value,
        key,
        groupId,
        record,
      });
    }
  };

  return (
    <div className="hi-property-pane">
      <Row
        className="hi-property-pane-header"
        align="middle"
        justify="space-between"
        gutter={[8, 8]}
      >
        <Col span={menuWidth ? menuWidth : 18}>
          <Menu
            onClick={({ key }) => {
              setCurrentGroupId(key);
            }}
            mode="horizontal"
            selectedKeys={[currentGroupId]}
            items={group}
          />
        </Col>
        <Col span={menuToolbarWidth ? menuToolbarWidth : 6}>
          <Row>
            {currentInfoItem && (
              <Col span={8}>
                <Tooltip
                  placement="leftBottom"
                  title={
                    <Row>
                      {currentInfoItem?.infos.map((info, index) => (
                        <Col key={index} span={24}>{info}</Col>
                      ))}
                    </Row>
                  }
                >
                  <Button
                    type="text"
                    // onClick={() => {
                    //   setShowSearch(!showSearch);
                    // }}
                    size="big"
                    icon={<QuestionCircleOutlined />}
                  ></Button>
                </Tooltip>
              </Col>
            )}
            <Col
              className={"hi-hide-background"}
              span={currentInfoItem ? 8 : 12}
            >
              <Tooltip
                placement="leftBottom"
                title="Sort properties by name">
                <Button
                  onClick={() => {
                    sortingFunc();
                  }}
                  type="text"
                  size="big"
                  icon={
                    toSort === sortingOrder[0] ? (
                      <SortAscendingOutlined
                      // className={toSort ? "hi-icon hi-selected" : "hi-icon"}
                      />
                    ) : toSort === sortingOrder[1] ? (
                      <SortDescendingOutlined
                      // className={toSort ? "hi-icon hi-selected" : "hi-icon"}
                      />
                    ) : (
                      <SwapOutlined
                        rotate={90}
                      // className={toSort ? "hi-icon hi-selected" : "hi-icon"}
                      />
                    )
                  }
                ></Button>
              </Tooltip>
            </Col>
            <Col span={8}>
              <Popover
                id="hi-popover-padding-0"
                content={
                  <Input
                    placeholder="Search..."
                    allowClear
                    onChange={(e) => setSearchTerm(e.target.value)}
                  />
                }
                placement="leftBottom"
                trigger="click"
              >
                <Button
                  type="text"
                  // onClick={() => {
                  //   setShowSearch(!showSearch);
                  // }}
                  ref={search?.ref && search.ref}
                  size="big"
                  icon={
                    <ShortCutText text={search?.text} scLocation={search?.scLocation}>
                      <Tooltip
                        placement="leftBottom"
                        title="Search">
                        <SearchOutlined
                          className={
                            searchTerm ? "hi-icon hi-selected" : "hi-icon"
                          }
                        />
                      </Tooltip>
                    </ShortCutText>
                  }
                ></Button>
              </Popover>
            </Col>
          </Row>
        </Col>
        {currentInfoPanelItem && (
          <Col span={24}>
            <Text
              code
              strong={currentInfoPanelItem?.strong}
              copyable={currentInfoPanelItem?.copyable}
            >
              {currentInfoPanelItem?.info}
            </Text>
          </Col>
        )}
      </Row>
      <Row className="hi-property-pane-body">
        <Col span={24}>
          {sortedItems.length ? (
            <Table
              showHeader={false}
              className="hi-property-pane-table"
              size="small"
              pagination={false}
              rowKey={(record) => {
                return `${record?.groupId}_${record?.key}`;
              }}
              dataSource={[...searchedItems]}
              columns={columns}
              rowClassName={(record, index) => {
                let className = index % 2 && "table-row-color";
                return className;
              }}
            />
          ) : null}
        </Col>
      </Row>
      <Row justify="end" gutter={[16, 16]} className="hi-property-pane-footer">
        <Col>
          {props.showApply ? (
            <ShortCutText text={apply?.text} scLocation={apply?.scLocation} isButton={true}>
              <Tooltip title="The apply button saves the current values of the properties and updates the display accordingly." >

                <Button
                  onClick={() => {
                    if (typeof props.getDataOnApply === "function") {
                      props.getDataOnApply([...itemsData]);
                    }
                  }}
                  ref={apply?.ref && apply.ref}
                  type="primary"
                >
                  Apply
                </Button>
              </Tooltip>

            </ShortCutText>
          ) : null}
        </Col>
        <Col>
          <ShortCutText text={reset?.text} scLocation={reset?.scLocation} isButton={true}>
            <Tooltip
              placement="topLeft"
              title="The reset button discards any changes made to the properties and restores them to their applied state. Be cautious while doing this as you may lose some important data."
            >

              <Button
                data-testid="hi-property-pane-reset-button"
                onClick={() => {
                  onResetForm();
                }}
                ref={reset?.ref && reset.ref}
              >
                Reset
              </Button>
            </Tooltip>

          </ShortCutText>
        </Col>
      </Row>
    </div>
  );
};

const object = {
  key: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
};
object.children = PropTypes.arrayOf(PropTypes.shape(object));

const newGroup = PropTypes.arrayOf(PropTypes.shape(object));

HIPropertyPane.propTypes = {
  group: newGroup,
  getData: PropTypes.func,
  items: PropTypes.arrayOf(
    PropTypes.shape({
      key: PropTypes.string.isRequired,
      label: PropTypes.string.isRequired,
      value: PropTypes.oneOfType([
        PropTypes.string,
        PropTypes.number,
        PropTypes.object,
        PropTypes.array,
        PropTypes.bool,
      ]),
      elementType: PropTypes.string.isRequired,
    }).isRequired
  ).isRequired,
};

export { HIPropertyPane };
