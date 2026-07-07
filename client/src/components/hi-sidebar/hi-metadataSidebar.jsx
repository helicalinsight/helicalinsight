import { ApartmentOutlined, CloseOutlined, EditOutlined, ExclamationCircleOutlined, InfoCircleFilled, InfoCircleOutlined, LinkOutlined, MoreOutlined, SearchOutlined, SyncOutlined, TableOutlined } from "@ant-design/icons";
import { Button, Checkbox, Col, Dropdown, Input, Menu, Modal, Popconfirm, Popover, Row, Table, Tooltip, Typography } from "antd";
import { cloneDeep, isEqual } from "lodash-es";
import React, { useEffect, useRef, useState } from "react";
import { useDrag } from "react-dnd";
import { useDispatch, useSelector, useStore } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { VList } from "virtuallist-antd";
import CustomMetadataIcon from '../../components/hi-metadata/customMetadataIcon';
import {
  handleDSTableOptionsClick,
  handleExpandDSRow,
  handleSave,
  validateMetadataName
} from "../../components/hi-metadata/utils/";
import { filterDS } from "../../components/hi-metadata/utils/filterDS";
import { handleDSTableCheck } from "../../components/hi-metadata/utils/handleDSTableCheck";
import { useWindowSize } from "../../customHooks/useWindowSize";
import { appActions, fileBrowserActions, handleDSExpanedRowKeys, metadataActions } from "../../redux/actions";
import { setDsMode, setDataSources, setIsEditClicked } from "../../redux/actions/datasource.actions.js";
import { classnames } from "../../utils/classNames";
import { findAllNestedObj } from "../../utils/find-nested-obj";
import TutorialInfo from '../common/hi-tutorial';
import HIIcon from '../common/icons/hi-icons';
import { SidebarFooter } from "../hi-sidebar-footer";
import constants from '../../constants'
import LoadingBar from "../common/components/hi-loading-bar";
import { HIFileBrowser } from "../hi-fileBrowser/hi-fileBrowser";
import SaveItems from "../hi-fileBrowser/SaveItems";

const { Text } = Typography;
const { Column } = Table;
const { Search } = Input;
const tooltipDelay = constants.mouseEnterDelay

const tableMenu = ({ record, dispatch, dsListToRender, store, isAfterSaveMode, mode }) => (
  <Menu
    onClick={({ key }) => {
      if (key !== "addToMetadata") {
        handleDSTableOptionsClick({
          record,
          option: key,
          dsListToRender,
          dispatch,
          store,
        });
        return;
      }
      if (key === "addToMetadata") {
        if (!Object.keys(store.getState()?.metadata?.present.tables)?.length) {
          handleDSTableOptionsClick({
            record,
            option: key,
            dsListToRender,
            dispatch,
            store,
            type: "reload",
          });
          return;
        }
      }
      Modal.confirm({
        title: "Add To Metadata",
        icon: <ExclamationCircleOutlined />,
        content: (
          <Row>
            <Col>
              <strong>Changes will be reflected based on below actions:</strong>
            </Col>
            <Col>
              <strong>Merge </strong>:The changes made in metadata panel will be
              kept as is and changes in the datasource panel will be synched
            </Col>
            <Col>
              <strong>Reload</strong>:The metadata will be overriden by the
              selection made in the datasource panel
            </Col>
          </Row>
        ),
        okButtonProps: { disabled: (mode === "edit" || isAfterSaveMode) ? true : false },
        okText: "Reload",
        cancelText: "Merge",
        okType: 'danger',
        cancelType: 'primary',
        onOk: () => {
          handleDSTableOptionsClick({ record, option: key, dsListToRender, dispatch, store, type: 'reload' });
        },
        onCancel: (e) => {
          if (e?.triggerCancel) {
            return
          }
          if (e.name) {
            handleDSTableOptionsClick({
              record,
              option: key,
              dsListToRender,
              dispatch,
              store,
              type: "merge",
            });
          }
          if (typeof e === 'function') {
            e()
          }
        },
      });
    }}
  >
    <Menu.Item key="addToMetadata" value={record}>
      Add to Metadata
    </Menu.Item>
    <Menu.Item key="reset" value={record}>
      Reset
    </Menu.Item>
    <Menu.Item key="selectAll" value={record}>
      Select All
    </Menu.Item>
  </Menu>
);

const handleEditDs = (dispatch, record) => {
  dispatch(appActions.updateRoute("/datasource/all"));
  dispatch(setDataSources([]));
  dispatch(setIsEditClicked(true));
  let data = {
    classifier: record.classifier,
    id: record.data.id,
    type: record.data.type,
    vendorName:record.vendorName,
  }
  if (record.dataSourceType) {
    data._dataSourceType = record.dataSourceType
  }

  if (record?.data?.dir) {
    data.dir = record.data.dir
  }
  dispatch(
    setDsMode({
      mode: "metadataEdit",
      driver: record.driver,
      data
    })
  );
};

const dsMenu = ({ record, dispatch, handleDsAction }) => {
  return (
    <Menu
      onClick={({ key }) => {
        handleDsAction({ key, record });
      }}
    >
      <Menu.Item key="refreshDataSource">
        <Tooltip title="Refresh">
          <SyncOutlined />
        </Tooltip>
      </Menu.Item>
      <Menu.Item
        key="editDataSource"
        onClick={() => handleEditDs(dispatch, record)}
        disabled={record?.permissionLevel < 3 ? true : false}
      >
        <Tooltip title="Edit">
          <EditOutlined />
        </Tooltip>
      </Menu.Item>
    </Menu>
  );
};

const HIMetadatSidebar = ({ handleAbort, filebrowserFor, setFilebrowserFor, saveType, openFB, onEditMetadata }) => {
  const dsListToRenderFromRedux = useSelector(
    (state) => state.metadata.present.datasourceListToRender,
    isEqual
  );
  const datasourceExpandedRowKeys = useSelector((state) => state.metadata.present.datasourceExpandedRowKeys);
  const aboutToChangeRoute = useSelector(state => state.app.aboutToChangeRoute)
  const mode = useSelector((state) => state.metadata.present.mode); //isAllowServiceCall
  const isAllowServiceCall = useSelector((state) => state.metadata.present.isAllowServiceCall);
  const toggleSidebar = useSelector(state => state.app.toggleSidebar)
  const [, offsetHeight] = useWindowSize();
  const [height, setHeight] = React.useState("0px");
  const store = useStore();
  const dispatch = useDispatch();
  const [searchIds, setSearchIds] = React.useState({});
  const [dsListToRender, setDsListToRender] = React.useState([]);
  const [requestId, setRequestId] = useState('');
  const apiRef = useRef(null);
  const setMetadataLoading = useSelector(state => state.metadata.present.setMetadataLoading);
  const tables = useSelector(state => state.metadata.present.tables);
  const [tableCount, setTableCount] = useState({ selected: 0, total: 0 });
  const isGlobalFbEnabled = useSelector(state => state.fileBrowser.globalFbEnabled);
  const metadataName = useSelector(state => state.metadata.present.metadataName);
  const [metadataModeChangeModalVisible, setMetadataModeChangeModalVisible] = useState({ currentMode: 'create', visible: false });
  const editCBRef = useRef(null);
  const [serachedText, setSearchedText] = useState('');

  function getApi(apiInstance) {
    apiRef.current = apiInstance;
  }

  function abortRequest() {
    apiRef.current?.abort();
  }

  useEffect(() => {
    if (aboutToChangeRoute === false && store.getState().app.activeRoute?.includes('metadata')) {
      dispatch(appActions.aboutToChangeRoute(null))
      dispatch(metadataActions.resetMetadataState({ mode: "create" }))
    }
  }, [aboutToChangeRoute])

  useEffect(() => {
    if (Object.values(searchIds)?.length) {
      let [value, item] = Object.values(searchIds)[0]
      // handleDSSearch({ value, item })
      let dsList = cloneDeep(dsListToRenderFromRedux);
      if (serachedText) {
        handleSearchDSNames(serachedText, filterDS({ text: value, record: item, dsList }));
      } else {
        setDsListToRender(filterDS({ text: value, record: item, dsList }));
      }
    }
    else {
      if (serachedText) {
        handleSearchDSNames(serachedText, dsListToRenderFromRedux);
      } else {
        setDsListToRender(dsListToRenderFromRedux);
      }
    }
  }, [dsListToRenderFromRedux, searchIds]);

  useEffect(() => {
    getSelectedTableCount(dsListToRenderFromRedux);
  }, [dsListToRenderFromRedux])

  useEffect(() => {
    document.querySelector(".hi-metadata-metadata-section") &&
      calculateHeight();
  }, [offsetHeight]);

  const calculateHeight = () => {
    let topPart = 75;
    let bottomPart = 140;
    let pageHeightExceptNav =
      document.querySelector(".hi-metadata-metadata-section")?.offsetHeight ||
      500;
    let finalHeight = pageHeightExceptNav - topPart - bottomPart;
    setHeight(`${finalHeight}px`);
    return finalHeight;
  };
  const vc = React.useMemo(() => {
    return VList({
      height: height,
      resetTopWhenDataChange: false,
      onScroll: () => { },
      vid: "metadata-page-datasource-section",
    });
  }, []);

  const handleSearchDSNames = (text = '', dsList) => {
    setSearchedText(text.toLowerCase());
    // let children;
    let filtered = cloneDeep(dsList || dsListToRenderFromRedux).map(ds => {
      try {
        let children = ds.children?.map(child => {
          if (child.name.toLowerCase().includes(text.toLowerCase()) || (child.data?.id + '').includes(text)) {
            return child
          }
          return false
        }).filter(Boolean)
        if (children?.length) {
          ds.children = children;
          return ds
        }
        // else {
        if (ds.name.toLowerCase().includes(text.toLowerCase())) return ds
        // }
      }
      catch (err) {
        return false
      }
      return false
    }).filter(Boolean)
    setDsListToRender(filtered);
    // return filtered;
  };

  let timer;
  const debounce = (func, timeout = 300) => {
    clearTimeout(timer);
    timer = setTimeout(func, timeout);
  };
  const handleDSSearch = ({ text, item = false }) => {
    item && setSearchIds(ids => {
      ids[item.uuid] = [text, item]
      return ids
      // return { [e]: [text, item] };
    })
    debounce(() => {
      let dsList = cloneDeep(dsListToRenderFromRedux);
      if (serachedText) {
        handleSearchDSNames(serachedText, filterDS({ text, record: item, dsList }));
      } else {
        setDsListToRender(filterDS({ text, record: item, dsList }));
      }
    });
  };

  // const getSelectedTableCount = useCallback(() => {
  //   let tables = findAllNestedObj((dsListToRenderFromRedux || {}), 'category', 'table');
  //   //disscuss with testing team
  //   const selectedTables = tables.filter(table => table.selected);
  //   setTableCount({selected: selectedTables.length, total: tables.length})
  //   // return <span>{selectedTables}/{tables.length}</span>
  //   // return <span>{`${[...new Set((tables || []).map(t => t.selected && t.id).filter(Boolean))].length}/${(new Set((tables || []).map(t => t.id) || []).size)}`}</span>
  // }, [dsListToRenderFromRedux]);

  function getSelectedTableCount(dsListToRenderFromRedux = {}) {
    let tables = findAllNestedObj((dsListToRenderFromRedux), 'category', 'table');
    const selectedTables = tables.filter(table => table.selected);
    setTableCount({ selected: selectedTables.length, total: tables.length })
  }

  const areWeTestingWithJest = () => {
    return process.env.JEST_WORKER_ID !== undefined;
  }

  const confirm = (passedMode) => {
    if ((passedMode || metadataModeChangeModalVisible.currentMode) === "create") {
      dispatch(appActions.setEditModeInfo(null));
      dispatch(
        metadataActions.resetMetadataState({ mode: "create" })
      );
      setMetadataModeChangeModalVisible(prevState => ({ ...prevState, visible: false }));
      handleAbort();
    } else if ((passedMode || metadataModeChangeModalVisible.currentMode) === "edit") {
      dispatch(metadataActions.resetMetadataState({
        others:
        {
          editMdFromHomeInfo: {
            dir: editCBRef.current.path,
            file: editCBRef.current.name
          },
          mode: 'edit',
          timeStamp: new Date().getTime()
        }
      }))
      editCBRef.current = undefined;
    }
    dispatch(metadataActions.clearUndoRedoHistory());
  };

  const onFormSumbit = (arg, name) => {
    handleSave({ store, dispatch, type: saveType, location: arg.path, fileName: name })
    dispatch(fileBrowserActions.setShowFileBrowser(false))
    dispatch(fileBrowserActions.setSearchResults(null));
    setFilebrowserFor(false)
  }

  // bug 6402 fix - start
  const onFbDoubleClick = (record) => {
    if (typeof onEditMetadata === 'function') {
      onEditMetadata(record)
      return
    }
    editCBRef.current = record;
    if (Object.keys(tables).length || mode === 'edit') {
      setMetadataModeChangeModalVisible({ currentMode: 'edit', visible: true })
    } else if (metadataModeChangeModalVisible.currentMode === 'create') {
      setMetadataModeChangeModalVisible({ currentMode: 'edit', visible: false })
      confirm('edit')
    }
  }
  // bug 6402 fix - end

  let fbProperties = {}
  if (filebrowserFor === 'save') {
    fbProperties.isHideFilters = true
    fbProperties.footerForm = {
      type: "Save",
      form: (
        <SaveItems
          formHeading="Metadata file name"
          onFormSumbit={onFormSumbit}
          saveButtonText="Save"
          cancelButtonText="Cancel"
          inputValue={metadataName}
          validateName={validateMetadataName}
        />
      ),
    }
  }
  else if (filebrowserFor === 'edit') {
    fbProperties.extensionOptions = ["metadata"]
    fbProperties.contextMenuOptions = {
      append: true,
      options: [
        {
          name: "Edit",
          key: 'edit',
          id: 'edt',
          merge: true,
          disabled: false,
          types: ["file"],
          // icon: <EditOutlined />,
          extensions: ["metadata"],
          callback: (record) => {
            if (typeof onEditMetadata === 'function') {
              onEditMetadata(record)
              return
            }
            editCBRef.current = record;
            if (Object.keys(tables).length || mode === 'edit') {
              setMetadataModeChangeModalVisible({ currentMode: 'edit', visible: true })
            } else if (metadataModeChangeModalVisible.currentMode === 'create') {
              setMetadataModeChangeModalVisible({ currentMode: 'edit', visible: false })
              confirm('edit')
            }
          },
        }
      ],
    }
  }

  return (
    <React.Fragment>
      <Modal title={((metadataModeChangeModalVisible.currentMode === 'edit' && mode === 'create') || (metadataModeChangeModalVisible.currentMode === 'create' && (mode === 'edit' || mode === ''))) ? 'Confirm Mode Switch?' : 'Open Another Metadata File?'}
        visible={metadataModeChangeModalVisible.visible}
        onOk={() => { confirm() }}
        onCancel={() => {
          setMetadataModeChangeModalVisible((prevState) => {
            return { ...prevState, visible: false }
          })
        }}>
        <Text>
          {`${((metadataModeChangeModalVisible.currentMode === 'edit' && mode === 'create') || (metadataModeChangeModalVisible.currentMode === 'create' && mode === 'edit')) ? 'Are you sure you want to switch the mode? This will discard any unsaved changes.' : 'Are you sure you want to open another metadata file? This will discard any unsaved changes.'}`}
        </Text>
      </Modal>
      {!isGlobalFbEnabled && Object.keys(fbProperties).length > 0 && (
        <HIFileBrowser {...fbProperties}
        onDoubleClick={onFbDoubleClick}  
        showEditOnTop={true}
        />
      )}
      <div
        className={`${!toggleSidebar
          ? "hi-sidebar-content hi-metadata-sidebar-content"
          : "display-none"
          }`}
        data-testid={
          toggleSidebar
            ? `hi-metadata-sidebar-visible`
            : "hi-metadata-sidebar-hidden"
        }
      >
        <Row data-testid="hi-metadata-sidebar-row">
          <Col span={12}>
            <TutorialInfo elementKey="hi-metadata-create" placement="rightTop">
              <Button
                block
                className={classnames(
                  {
                    "metadata-mode-button-active": mode === "create",
                    "metadata-mode-button-not-active": mode === "edit",
                  },
                  "border-right-none",
                  "font-bold"
                )}
                onClick={(e) => {
                  if (mode === "create") return;
                  if (mode === 'edit' || Object.keys(tables).length) {
                    setMetadataModeChangeModalVisible({ currentMode: 'create', visible: true })
                  } else {
                    confirm('create');
                  }
                }}
              >
                CREATE
              </Button>
            </TutorialInfo>
          </Col>
          <Col span={12}>
            <TutorialInfo elementKey="hi-metadata-edit">
              <Button
                block
                className={classnames(
                  {
                    "metadata-mode-button-active": mode === "edit",
                    "metadata-mode-button-not-active": mode === "create",
                  },
                  "font-bold"
                )}
                onClick={() => {
                  if (mode === "edit") return;
                  dispatch(fileBrowserActions.setShowFileBrowser(true))
                  openFB({})
                }}
              >
                EDIT
              </Button>
            </TutorialInfo>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Search
              data-testid="metadata-sidebar-search"
              placeholder="Search Datasources"
              allowClear
              onSearch={(e) => handleSearchDSNames(e)}
            />
          </Col>
          {/* {!setMetadataLoading?.isDataSourceFetched && <Col span={24}><LoadingBar /></Col>} */}
          {!setMetadataLoading?.isDataSourceFetched &&
            <Col span={24}
              onClick={() => {
                handleAbort && handleAbort();
              }}>
              <LoadingBar />
            </Col>}
          <Row>
            <TutorialInfo elementKey="hi-metadata-ds-list" placement="rightTop">
              <Col span={24} className='hi-metadata-connections-tutorial'></Col>
            </TutorialInfo>
            <Table
              data-testid="hi-metadata-sidebar-table"
              size="small"
              className="custom-table-scroll-bar hi-tree-table"
              dataSource={dsListToRender}
              showHeader={false}
              expandedRowKeys={datasourceExpandedRowKeys}
              // rowKey={}
              onExpand={(isExpanded, record) => {
                dispatch(handleDSExpanedRowKeys(record.key))
                handleExpandDSRow({
                  setRequestId,
                  isExpanded,
                  record,
                  dispatch,
                  store,
                  mode,
                  getApi
                });
              }}
              scroll={{ y: height, x: "100%" }}
              components={() => VList({
                height: height,
                resetTopWhenDataChange: false,
                onScroll: () => { },
                vid: "metadata-page-datasource-section",
              })
              }
              pagination={false}
              rowKey={(record) => record.key}
            >
              <Column
                ellipsis={{
                  showTitle: true,
                }}
                key="action"
                render={(text, record) => (
                  <DragAssist
                    isAllowServiceCall={isAllowServiceCall}
                    setRequestId={setRequestId}
                    requestId={requestId}
                    item={record}
                    dsListToRender={dsListToRender}
                    dsListToRenderFromRedux={dsListToRenderFromRedux}
                    updateSearchIds={(e, value = '', item) => { // e is uuid
                      setSearchIds((ids) => {
                        ids = cloneDeep(ids);
                        if (item === false) return {}
                        return { [e]: [value, item] };
                      });
                    }}
                    handleDSSearch={handleDSSearch}
                    searchIds={searchIds}
                    abortRequest={abortRequest}
                    getApi={getApi}
                  ></DragAssist>
                )}
              />
            </Table>
          </Row>
        </Row>
        <Row>
          {!toggleSidebar && (
            <Col span={24} className="metadata-sidebar-footer-section">
              <Row>
                <Col span={24} className="pl-10">
                  <Text strong>
                    Selected tables {tableCount.selected}/{tableCount.total}
                  </Text>
                </Col>
                {!areWeTestingWithJest() && (
                  <SidebarFooter
                    fileBrowserOnClick={() => {
                      dispatch(fileBrowserActions.setGlobalFbVisibility(false));
                      dispatch(fileBrowserActions.setShowFileBrowser(true))
                      openFB({});
                    }}
                  />
                )}
              </Row>
            </Col>
          )}
        </Row>
      </div>
    </React.Fragment >
  );
};

export { HIMetadatSidebar };

const DragAssist = ({
  isAllowServiceCall,
  setRequestId,
  requestId: requestIdToCancel,
  item,
  dsListToRender,
  updateSearchIds,
  handleDSSearch,
  searchIds,
  dsListToRenderFromRedux = false,
  abortRequest,
  getApi
}) => {
  const requestId = uuidv4();
  const store = useStore();
  const dispatch = useDispatch();
  const inputRef = React.useRef(null);
  const [inputVal, setInputVal] = useState('');
  const { dataSourceTypes, serviceErrorStatus = {} } = useSelector((state) => state.metadata.present);
  let isAfterSaveMode = store.getState().metadata.present.isAfterSaveMode;
  let mode = store.getState().metadata.present.mode;
  const [{ }, drag] = useDrag(() => {
    return {
      type: "tableFromDS",
      item: item,
      collect: (monitor) => ({
        opacity: monitor.isDragging() ? 0.5 : 1,
      }),
    };
  }, []);

  React.useEffect(() => {
    inputRef?.current?.focus && inputRef.current.focus()
  }, [searchIds])

  const loadingStatus = useSelector((state) => state.metadata.present.loadingStatus);
  const getTooltipTitle = ({ item, category = false }) => {
    let result = {};
    if (category === "dataSource" && item?.data) {
      result["Id"] = item.data.id;
      result["Type"] = item.data.type;
      if (item.data?.dir) result["Dir"] = item.data.dir;
      if (!item.dataSourceType) {
        const source = dataSourceTypes.find(source => source.type === item.data?.type);
        result["DataSourceType"] = source?.name;
      } else {
        result["DataSourceType"] = item.dataSourceType;
      }
    }
    return (
      <div className="hi-metadata-ds-info-tooltip">
        <table>
          <tbody>
            {Object.keys(result).map((key) => {
              return (
                <tr key={key} style={{ color: 'white' }}>
                  <td >{key} &nbsp;</td>
                  <td >{result[key]}</td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    );
  };

  const handleDsAction = ({ key, record }) => {
    key === "refreshDataSource" &&
      handleExpandDSRow({
        setRequestId,
        isExpanded: false,
        record,
        dispatch,
        store,
        refreshDataSource: true,
        getApi
      });
  };

  return (
    <div
      ref={(item.category === "table" || item.category === "view") ? drag : null}
      style={{ width: "100%" }}
      id={item.uuid}
      className={
        (item.category === "table" || item.category === "view") ? "hide-checkbox-for-type-table" : ""
      }
    >
      <React.Fragment>
        {(item.category === "table" || item.category === "view") && (
          <Dropdown
            overlay={tableMenu({
              mode,
              isAfterSaveMode,
              record: item,
              dsListToRender,
              dispatch,
              store,
            })}
            trigger={["contextMenu"]}
          >
            <Row>
              <Col span={20}>
                <Tooltip mouseEnterDelay={tooltipDelay} title={`Table : ${item.name}`}>
                  <Text ellipsis={true}>
                    <TableOutlined />
                    {` ${item.name}`}
                  </Text>
                </Tooltip>
              </Col>
              <Col span={4}>
                <Checkbox
                  onClick={(e) => {
                    handleDSTableCheck({
                      checked: e.target.checked,
                      record: item,
                      dispatch,
                      dsListToRender: dsListToRenderFromRedux
                    });
                    // dispatch(
                    //   metadataActions.updateMetadataState({
                    //     mode: "selectedTables",
                    //     key: item.uniqueKey,
                    //     checked: e.target.checked,
                    //   })
                    // );
                  }}
                  checked={!!item.selected}
                />
              </Col>
              {item.category !== 'table' && loadingStatus[item.key] && <Col span={24} onClick={() => {
                abortRequest && abortRequest();
              }}><LoadingBar /></Col>}
            </Row>
          </Dropdown>
        )}
        {item.category !== "table" && (
          <Row>
            {{
              dsGroup: (
                <Col span={24}>
                  <Row>
                    <Col span={20}>
                      <Row wrap={false}>
                        <Col >
                          <div className="data-source-image-block">
                             {['athena', 'dynamicswitch', "snowflake", "trino", "elasticsearch","duckdb", "yugabyte" , "celerdata","cockroach","sapdb","databriks","api","flatfile","flatfilecsv","flatfileexcel","flatfiletsv","flatfilejson","flatfileaws","flatfileazureblobstorage","flatfilecloudfarer2","flatfilegcs","flatfileparquet","flatfilegooglespreadsheet"].includes(item.name?.toLowerCase()?.replace(/ /g, "")) ?
                              <CustomMetadataIcon type={item.name} />
                              :
                              <span
                                className={
                                  "databases " +
                                  item.name?.toLowerCase()?.replace(/ /g, "") +
                                  "-icon"
                                }
                              ></span>}
                          </div>
                        </Col>
                        <Col >
                          <Tooltip mouseEnterDelay={tooltipDelay}
                            title={`List of ${item.name} datasources to create metadata`}
                          >
                            <Text strong ellipsis={true}>
                              &nbsp;
                              {item.name}
                            </Text>
                          </Tooltip>
                        </Col>
                      </Row>
                    </Col>

                    <Col span={4}>
                      <Row justify="end">
                        <Tooltip mouseEnterDelay={tooltipDelay}
                          title={`${item.children?.length} connection(s) available`}
                        >
                          <Text strong>
                            <div className="number-circle">
                              {item.children?.length}
                            </div>
                          </Text>
                        </Tooltip>
                      </Row>
                    </Col>
                  </Row>
                </Col>
              ),
              dataSource: (
                <Col span={24}>
                  <Row align="center">
                    <Col xs={17} sm={17} md={17} lg={17} xl={17}
                    >
                      <Row>
                        <Col span={22}>
                          &nbsp;
                          <Tooltip mouseEnterDelay={tooltipDelay} title={!serviceErrorStatus[item.key] ? item.name : ''}>
                            <Text
                              ellipsis={{ tooltip: false }}
                              type={item.key in serviceErrorStatus ? 'danger' : 'default'}
                            >
                              <LinkOutlined />
                              <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key] ? item.name : ''}>
                                {item.name}
                              </Tooltip>
                              {
                                item.key in serviceErrorStatus && <span className='ds-fetch-error'>
                                  <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key]}>
                                    <InfoCircleFilled />
                                  </Tooltip>
                                </span>
                              }
                            </Text>
                          </Tooltip>
                        </Col>
                      </Row>
                    </Col>
                    <Col xs={7} sm={7} md={7} lg={7} xl={7} className="metadata-data">
                      <Row className="metadata-table-icons">
                        <Popover content={getTooltipTitle({
                          item,
                          category: "dataSource",
                        })} mouseEnterDelay={tooltipDelay} color="rgba(0, 0, 0, 0.7)">
                          <InfoCircleOutlined className="metadata-info" />
                        </Popover>
                        <Input
                          size="small"
                          key={item.uuid}
                          placeholder="search"
                          className={classnames(
                            Object.keys(searchIds || {}).indexOf(item.uuid) === -1
                              ? "search-ds-hide"
                              : "search-ds-active"
                          )}
                          ref={Object.keys(searchIds || {}).indexOf(item.uuid) !== -1 ? inputRef : null}
                          value={inputVal}
                          defaultValue={searchIds[item.uuid] || ''}
                          onChange={(e) => {
                            setInputVal(e.target.value);
                            handleDSSearch({ text: e.target.value, item: item })
                          }}
                        />
                        {Object.keys(searchIds || {}).indexOf(item.uuid) === -1 ? <Tooltip title="Search">
                          <SearchOutlined className="metadata-search"
                            data-testid='search-icon-datasource-search-metadata'
                            onClick={() => {
                              updateSearchIds(item.uuid, inputVal, item)
                            }}
                          />
                        </Tooltip>
                          :
                          <CloseOutlined className="metadata-close" onClick={() => {
                            setInputVal('');
                            updateSearchIds(item.uuid, '', false)
                          }} />}
                        <Dropdown
                          overlay={dsMenu({
                            record: item,
                            dsListToRender,
                            dispatch,
                            handleDsAction,
                          })}
                          trigger={["contextMenu", "click"]}
                        >
                          <MoreOutlined className="metadata-more" />
                        </Dropdown>
                      </Row>
                    </Col>
                    {loadingStatus[item.key] && (
                      <Col onClick={() => {
                        abortRequest && abortRequest();
                      }} span={24}><LoadingBar /></Col>
                    )}
                  </Row>
                </Col>
              ),
              schema: (
                <Row>
                  <Col span={24}>
                    <Tooltip mouseEnterDelay={tooltipDelay} title={!serviceErrorStatus[item.key] ? `Schema : ${item.name}` : ''}>
                      <Text
                        ellipsis={{ tooltip: false }}
                        type={item.key in serviceErrorStatus ? 'danger' : 'default'}
                      >
                        <ApartmentOutlined />
                        <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key] ? `Schema : ${item.name}` : ''}>
                          {item.name}
                        </Tooltip>
                        {
                          item.key in serviceErrorStatus && <span className='ds-fetch-error'>
                            <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key]}>
                              <InfoCircleFilled />
                            </Tooltip>
                          </span>
                        }
                      </Text>
                    </Tooltip>
                  </Col>
                  {loadingStatus[item.key] && (
                    <Col onClick={() => {
                      abortRequest && abortRequest();
                    }} span={24}><LoadingBar /></Col>
                  )}
                </Row>
              ),
              catalog: (
                <Row>
                  <Col span={24}>
                    <Tooltip mouseEnterDelay={tooltipDelay} title={!serviceErrorStatus[item.key] ? `Catalog : ${item.name}` : ''}>
                      <Text
                        ellipsis={{ tooltip: false }}
                        type={item.key in serviceErrorStatus ? 'danger' : 'default'}
                      >
                        <HIIcon type='hi-news-paper' />
                        <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key] ? `Catalog : ${item.name}` : ''}>
                          {item.name}
                        </Tooltip>
                        {
                          item.key in serviceErrorStatus && <span className='ds-fetch-error'>
                            <Tooltip mouseEnterDelay={tooltipDelay} title={serviceErrorStatus[item.key]}>
                              <InfoCircleFilled />
                            </Tooltip>
                          </span>
                        }
                      </Text>
                    </Tooltip>
                  </Col>
                  {loadingStatus[item.key] && (
                    <Col onClick={() => {
                      abortRequest && abortRequest();
                    }} span={24}><LoadingBar /></Col>
                  )}
                </Row>
              ),
            }[item.category] || (
                <Row>
                  <Col span={24}>
                    <Text ellipsis={{ tooltip: item.name }}>
                      {` ${item.name}`}
                    </Text>
                  </Col>
                </Row>
              )}
          </Row>
        )}
      </React.Fragment>
    </div>
  );
};
