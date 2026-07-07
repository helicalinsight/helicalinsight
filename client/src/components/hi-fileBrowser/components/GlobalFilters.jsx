import React, { useState, useEffect, useRef, useMemo } from "react";
import {
  Form,
  Select,
  Input,
  Row,
  Col,
  Space,
  Button,
  Switch,
  Tag,
  Tooltip,
  Menu,
  Dropdown,
  Tabs,
  Radio,
  Checkbox,
  Popover,
} from "antd";
import { useSelector, useDispatch } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import { useDebounce } from "../../../hooks";
import { searchData } from "../helperMethods.js";
import {
  getFilterOptionsForExtensions,
  groupByOptions,
  tableColumnsConfig,
} from "../constants";
import {
  FilterOutlined,
  FolderAddOutlined,
  MenuOutlined,
  SearchOutlined,
  SettingFilled,
  SyncOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import cannedReport from "../../../base/requests/cannedReports.request.js";
import { fetchImagesData } from "../../hi-canned-reports/hcrHelperMethods.js";
const { TabPane } = Tabs;
const { Search } = Input;

const FilterByType = (props) => {
  const { filterOptions } = props;
  const dispatch = useDispatch();

  const onFilterChange = ({ key }) => {
    const val = key;
    let valueToSet = [val];
    if (val === "all") valueToSet = filterOptions.map((e) => e.value);
    dispatch(fileBrowserActions.setFilterByType(valueToSet));
  };

  return (
    filterOptions.length > 1 && (
      <Menu className="fb-filters" selectable defaultSelectedKeys={["all"]} onClick={onFilterChange}>
        <Menu.Item className="fb-filter-item" key={"all"} value={"all"} icon={<SettingFilled />}>
          All
        </Menu.Item>
        {filterOptions.map((option) => (
          <Menu.Item className="fb-filter-item" icon={option.icon} key={option.value} value={option.value}>
            <Tooltip title={option.value} placement="right">
              <span>{option.label}</span>
            </Tooltip>
          </Menu.Item>
        ))}
      </Menu>
    )
  );
};

const GroupBy = () => {
  const dispatch = useDispatch();
  const onChange = (e) => {
    const val = e.target.value;
    if (val === "none") dispatch(fileBrowserActions.setFilteredFiles(null));
    dispatch(fileBrowserActions.setGroupBy(val === "none" ? null : val));
  };

  useEffect(() => {
    return (() => {
      dispatch(fileBrowserActions.setGroupBy(null));
      dispatch(fileBrowserActions.setFilteredFiles(null));
    })
  }, []);

  return (
    <Radio.Group defaultValue={"none"} onChange={onChange}>
      <Space direction="vertical">
        {groupByOptions.map((option) => (
          <Radio key={option.value} value={option.value}>
            {option.label}
          </Radio>
        ))}
      </Space>
    </Radio.Group>
  );
};

const DynamicColumns = () => {
  const dispatch = useDispatch();
  const defaultCheckedValues = tableColumnsConfig.map((item) => item.value);
  const [selectedItems, setSelectedItems] = useState(null);
  useEffect(() => {
    if (selectedItems)
      dispatch(fileBrowserActions.setTableColumns(selectedItems));
  }, [selectedItems]);

  const onColumnsChange = (val) => setSelectedItems(val);

  return (
    <Checkbox.Group
      defaultValue={defaultCheckedValues}
      onChange={onColumnsChange}
    >
      {tableColumnsConfig.length > 0 &&
        tableColumnsConfig.map((item) => (
          <Checkbox key={item.value} value={item.value}>
            {item.label}
          </Checkbox>
        ))}
    </Checkbox.Group>
  );
};

// const DropdownWrapper = (WrappedComponent) => {
//   return
//   const [visible, setVisible] = useState(false);
//   const onVisibleChange = (visible) => setVisible(visible);
//   return (
//     <>
//       <Dropdown
//         overlay={menu}
//         visible={visible}
//         trigger={["click"]}
//         overlayClassName="global-filters-dropdown"
//         onVisibleChange={onVisibleChange}
//       >
//         <WrappedComponent onIconClick={() => setVisible((prev) => !prev)} />
//       </Dropdown>
//     </>
//   );
// };
const RearrangeSettings = ({ footerForm = {} }) => {
  const [visible, setVisible] = useState(false);
  let isActive = false;
  const groupByValue = useSelector(
    (state) => state.fileBrowser.globalFilters.groupBy
  );
  const tableConfigValues = useSelector(
    (state) => state.fileBrowser.tableColumns
  );
  if (!groupByValue && tableConfigValues) {
    isActive =
      tableConfigValues.length != tableColumnsConfig.length ? true : false;
  }
  if (groupByValue) isActive = true;

  const menu = (
    <Menu>
      <Menu.Item key="rearrange-menu">
        <Tabs defaultActiveKey="1" tabPosition="left" size="small">
          <TabPane tab="Group by" key="1">
            <GroupBy />
          </TabPane>
          <TabPane tab="Table Columns" key="2">
            <DynamicColumns />
          </TabPane>
        </Tabs>
      </Menu.Item>
    </Menu>
  );
  const onVisibleChange = (visible) => setVisible(visible);
  return (
    <>
      <Dropdown
        overlay={menu}
        visible={visible && (footerForm.type !== 'Save')}
        trigger={["click"]}
        overlayClassName="global-filters-dropdown"
        onVisibleChange={onVisibleChange}
      >
        <Tooltip title="Rearrange Data">
          <Button type="text" style={{ height: 18 }} disabled={!(footerForm.type !== 'Save')} block icon={<MenuOutlined
            onClick={() => setVisible((prev) => !prev)}
            style={{ fontSize: 16, color: isActive ? "#1890ff" : "" }}
          />} />
          {/* <MenuOutlined
            onClick={() => setVisible((prev) => !prev)}
            style={{ fontSize: 16, color: isActive ? "#1890ff" : "" }}
          /> */}
        </Tooltip>
      </Dropdown>
    </>
  );
};

const Filters = (props) => {
  const dispatch = useDispatch();
  const { extensionOptions, isHideFilters = false } = props;
  const [visible, setVisible] = useState(false);
  const [filterVisible, setFilterVisible] = useState(true);
  const extensionOptionsKey = extensionOptions?.join(",") ?? "";
  const fileExtensions = useSelector(
    (state) => state.app.applicationSettingsData?.settings?.fileExtensions
  );
  const fileExtensionsKey = fileExtensions?.join(",") ?? "";
  const filterOptions = useMemo(
    () => getFilterOptionsForExtensions(fileExtensions, extensionOptions),
    [fileExtensions, extensionOptionsKey]
  );
  const menu = <FilterByType filterOptions={filterOptions} />;
  const filterValue = useSelector(
    (state) => state.fileBrowser.globalFilters.filterByType
  );
  const onVisibleChange = (visible) => setVisible(visible);
  const initializedFilterKeyRef = useRef(null);

  useEffect(() => {
    const filterKey = `${fileExtensionsKey}|${extensionOptionsKey}|${isHideFilters}`;
    if (initializedFilterKeyRef.current === filterKey) return;
    initializedFilterKeyRef.current = filterKey;
    if (isHideFilters || (extensionOptions && extensionOptions.length === 1))
      setFilterVisible(false);

    const filterValues = getFilterOptionsForExtensions(
      fileExtensions,
      extensionOptions
    ).map((e) => e.value);
    dispatch(fileBrowserActions.setFilterByType(filterValues));
  }, [fileExtensionsKey, extensionOptionsKey, isHideFilters]);

  // hide filter
  // if there are extension options and there is only one extension
  //
  return (
    <>
      <Dropdown
        overlay={menu}
        visible={visible}
        trigger={["click"]}
        overlayClassName="global-filters-dropdown"
        onVisibleChange={onVisibleChange}
      >
        {filterVisible ? (
          <Tooltip title="Filters">
            <FilterOutlined
              onClick={() => setVisible((prev) => !prev)}
              style={{
                fontSize: 16,
                color: filterValue?.length === 1 ? "#1890ff" : "",
              }}
            />
          </Tooltip>
        ) : (
          <></>
        )}
      </Dropdown>
    </>
  );
};

const FbSearch = (props) => {
  const { mode } = props;
  const globalSearch = useSelector((state) => state.fileBrowser.globalSearch);
  const dispatch = useDispatch();
  const [searchTerm, setSearchTerm] = useState(null);
  const SEARCH_DELAY = 500;
  const debouncedSearchTerm = useDebounce(searchTerm, SEARCH_DELAY);
  const files = useSelector((state) => state.fileBrowser.files.data);
  const filteredFiles = useSelector((state) => state.fileBrowser.filteredFiles);
  const isFileBrowserOpen = useSelector(
    (state) => state.fileBrowser.showFileBrowser
  );
  const [inputVisible, setInputVisible] = useState(false);
  const searchClick = useRef(null);
  const searchInput = useRef(null);

  // useEffect(() => {
  //   if (globalSearch) setSearchTerm(globalSearch);
  // }, [globalSearch]);

  useEffect(() => {
    // used debouncing for efficiency
    if (debouncedSearchTerm != null && isFileBrowserOpen) {
      const filesToSearch = filteredFiles ? filteredFiles : files;
      const searchResults = searchData(filesToSearch, debouncedSearchTerm);
      dispatch(
        fileBrowserActions.setSearchResults(
          debouncedSearchTerm === "" ? null : searchResults
        )
      );
    }

    /**
     * On unmount, clear the search term and reset the search results to null
     * Refer bug 7431 for details
     */
    return () => {
      dispatch(fileBrowserActions.setSearchResults(null));
    }
  }, [debouncedSearchTerm, filteredFiles, isFileBrowserOpen]);

  useEffect(() => {
    if (globalSearch && searchClick.current) {
      searchClick.current.click()
      setInputVisible(true)
    }
  }, [globalSearch])

  useEffect(() => {
    if (globalSearch && inputVisible && searchInput.current) {
      searchInput.current.focus()
    }
  }, [inputVisible, globalSearch])

  if (mode === "compact") {
    return (
      <Input
        style={{ width: "100%" }}
        value={searchTerm}
        placeholder="Search..."
        allowClear
        onChange={(e) => setSearchTerm(e.target.value)}
      />
    );
  }

  return (
    <Popover
      align={{ offset: [0, 11] }}
      id="hi-popover-padding-0"
      content={
        <Input
          ref={searchInput}
          value={searchTerm}
          placeholder="Search..."
          allowClear
          onChange={(e) => setSearchTerm(e.target.value)}
          onBlur={() => searchInput.current.focus()}
        />
      }
      placement="leftBottom"
      trigger="click"
    >
      <Tooltip title="Search">
        <SearchOutlined
          ref={searchClick}
          style={{ fontSize: 16, color: searchTerm ? "#1890ff" : "" }}
        />
      </Tooltip>
    </Popover>
  );
};

const GlobalFilters = ({
  footerForm,
  getFileBrowserData,
  extensionOptions,
  mode,
  closeFb,
  isHideFilters,
}) => {
  const dispatch = useDispatch();
  const onAddFolderToRoot = () => {
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({
        contextItem: "New Folder Root",
      })
    );
  };
  return (
    <Row data-testid="hi-file-browser-GlobalFilters" align="middle" justify={"end"}>
      <Space
        size="middle"
        align={"center"}
        className={`global-filters-space ${mode}`}
      >
        <FbSearch mode={mode} />
        <Filters extensionOptions={extensionOptions} isHideFilters={isHideFilters} />
        {mode != "compact" && (
          <>
            <RearrangeSettings footerForm={footerForm} />
            <Tooltip title="Add folder to root">
              <FolderAddOutlined
                onClick={onAddFolderToRoot}
                style={{ fontSize: 16 }}
              />
            </Tooltip>
          </>
        )}
        <Tooltip title="Refresh">
          <SyncOutlined
            onClick={() => {
              if (extensionOptions?.includes('image')) {
                fetchImagesData(dispatch, true, cannedReport(dispatch)?.getResources);
              } else {
                getFileBrowserData({ refresh: true })
              }
            }}
            style={{ fontSize: 16 }}
            id={"hi-file-browser-refresh"}
          />
        </Tooltip>
        {mode != "compact" && (
          <Tooltip title="Close">
            <CloseOutlined style={{ fontSize: 16 }} onClick={closeFb} />
          </Tooltip>
        )}
      </Space>
    </Row>
  );
};

export { GlobalFilters };