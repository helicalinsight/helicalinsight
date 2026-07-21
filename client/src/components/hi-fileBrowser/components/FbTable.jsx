import React, { useState, useEffect, useMemo, useRef } from "react";
import { useDrag, useDrop } from "react-dnd";
import { useHistory, useLocation } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import moment from "moment";
import { Table, Typography, Dropdown, Menu, Tooltip, Row, Col, Progress } from "antd";
import { FolderOpenOutlined, FileTextOutlined, FolderOutlined } from "@ant-design/icons";
import { VList } from "virtuallist-antd";
import { filterByType, fbGroupBy, filterByFileType, addFileToFolder } from "../helperMethods.js";
import { ContextDrawer, PropertiesDrawer, FbContextMenu, DeleteModal, ShareFinalModal } from ".";
import { fileBrowserActions, appActions } from "../../../redux/actions";
import { getPermissionLevelsText } from "../../../utils/utilities.js";
import { routesUrl } from "../../../app/constants";
import { getExtensionIcon } from "../constants.js";
import { allContextMenuOptions, filterByOptions, AGENT_INTERACT_ACTION } from "../constants.js";
import ExportModal from "./ExportModal.jsx";
import { removeAllReports } from "../../../redux/actions/hreport.actions.js";
import { openCompactFbBrower, replaceReportId } from "../../../redux/actions/dashboard-designer.actions.js";
import notify from "../../hi-notifications/notify.js";
import requests from "../../../base/requests/index.js";
import axios from "axios";
const { Text } = Typography;
const { updateRoute } = appActions;
let source;

const placeEditOnTop = (options) => {
  const editOption = options.find(op => op?.id === 'edt');
  if (!editOption) {
    return options;
  }
  const restOptions = options.filter(op => op?.id !== 'edt');
  return [editOption, ...restOptions];
}

const ContextMenuWrapper = (props) => {
  const { contextMenuOptions, record, visible, posX, posY, setVisible, mode, closeFbOnCallback, files, hiddenFileInput, selectedRecordRef } =
    props;
  const onVisibleChange = (visible) => setVisible({ visible });
  const currentUserRoles = useSelector(
    (state) => state.app.applicationSettingsData.userData.user.roles
  );
  const fbCoords = useSelector((state) => state.fileBrowser.compactModeCoordinates);
  const copyOrCutItemDetails = useSelector((state) => state.fileBrowser.copyOrCutItemDetails);

  const mergeContextOptions = ({ contextMenuOptions, allContextMenuOptions }) => {
    let dataToMerge = contextMenuOptions.filter((option) => option.merge);
    contextMenuOptions = contextMenuOptions.filter((option) => !option.merge);
    let mergeOpions = {};
    dataToMerge.forEach((opt) => {
      mergeOpions[opt.id] = opt;
    });
    let result = allContextMenuOptions
      .map((all) => {
        if (Object.keys(mergeOpions).includes(all.id)) {
          all = { ...all, ...mergeOpions[all.id] };
        }
        return all;
      })
      .filter((item) => !item.hide);
    return [...contextMenuOptions, ...result];
  };

  const [menuOptions, setMenuOptions] = useState(
    contextMenuOptions
      ? contextMenuOptions.append
        ? mergeContextOptions({
          contextMenuOptions: contextMenuOptions.options,
          allContextMenuOptions,
        })
        : contextMenuOptions.options
      : allContextMenuOptions
  );
  const [currentMenuOptions, setCurrentMenuOptions] = useState(menuOptions);
  const filterOptions = (record) => {
    const options = menuOptions.filter((option) => {
      let filterCheck = option.types.includes(record.type);
      if (record.type === "file" && option.extensions) {
        let recordExtension = record.extension;
        if (['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp'].includes(recordExtension)) recordExtension = 'image';
        filterCheck = filterCheck && option.extensions.includes(record.extension);
      }
      if (option.permission) {
        filterCheck = filterCheck && option.permission.includes(Number(record.permissionLevel));
      }
      if (
        option.id === "exp" &&
        currentUserRoles.length > 0 &&
        option.userRoles &&
        Array.isArray(option.userRoles)
      ) {
        filterCheck = filterCheck && currentUserRoles.some((e) => option.userRoles.includes(e));
      }
      if (
        option.id === "shr" &&
        currentUserRoles.length > 0 &&
        option.userRoles &&
        Array.isArray(option.userRoles)
      ) {
        filterCheck = filterCheck && currentUserRoles.some((e) => option.userRoles.includes(e));
      }
      if (
        option.id !== "shr" &&
        currentUserRoles.length > 0 &&
        option.userRoles &&
        !Array.isArray(option.userRoles) &&
        typeof option.userRoles === "object" &&
        option.userRoles !== null
      ) {
        filterCheck =
          filterCheck &&
          currentUserRoles.some((e) => option.userRoles[e]?.includes(record.extension));
      }
      return filterCheck;
    });
    setCurrentMenuOptions(options);
  };

  useEffect(() => {
    if (record) {
      filterOptions(record);
    }
  }, [record]);

  const cordX = mode && mode === "compact" ? posX - 40 - (fbCoords ? fbCoords.x : 0) : posX;
  const cordY = mode && mode === "compact" ? posY - 60 - (fbCoords ? fbCoords.y : 0) : posY;
  return (
    <Dropdown
      destroyPopupOnHide={true}
      overlay={
        <FbContextMenu
          setMenuVisible={setVisible}
          record={record}
          menuOptions={props.showEditOnTop ? placeEditOnTop([...currentMenuOptions]) : currentMenuOptions}
          closeFbOnCallback={closeFbOnCallback}
          copyOrCutItemDetails={copyOrCutItemDetails}
          files={files}
          hiddenFileInput={hiddenFileInput}
          selectedRecordRef={selectedRecordRef}
        />
      }
      trigger={["contextMenu"]}
      visible={visible}
      onVisibleChange={onVisibleChange}
    >
      <span
        style={{ position: "absolute", left: `${posX}px`, top: `${posY}px` }}
        onClick={(e) => e.preventDefault()}
      >
        {" "}
      </span>
    </Dropdown>
  );
};
const ContextMenuActions = ({ extensionOptions }) => {
  const recordPath = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord?.path
  );
  return (
    <>
      <ContextDrawer />
      <PropertiesDrawer />
      <DeleteModal extensionOptions={extensionOptions} />
      {recordPath && <ShareFinalModal />}
      <ExportModal />
    </>
  );
};
const FbTable = (props) => {
  const { contextMenuOptions, footerForm, onDoubleClick, mode, closeFbOnCallback } = props;
  const copyOrCutItemDetails = useSelector((state) => state.fileBrowser.copyOrCutItemDetails);
  const [isUploading, setIsUploading] = useState(false);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [uploadedPercentage, setUploadedPercentage] = useState(0);
  const hiddenFileInput = useRef();
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const selectedRecordRef = useRef(null);

  // const location = useLocation();  location is not used.
  // const { pathname } = location;

  const getToolTipTitle = (name, record) => {
    if (props.size === "mini") {
      return record.path;
    }
    return record.type === "file" ? record.title : name;
  }

  const fbColumns = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
      width: 150,
      className: "table-ellipsis",
      defaultSortOrder: "ascend",
      sorter: (a, b) => {
        let sorterField = "name";
        if (a.type === "file") sorterField = "title";
        return a[sorterField].localeCompare(b[sorterField]);
      },
      render: (name, record) => {
        return (
          <Tooltip
            placement="top"
            // title={record.type === "file" ? record.title : name}
            title={getToolTipTitle(name, record)}
            //color="#000"
            overlayInnerStyle={{ height: "100%", fontSize: 14 }}
          >
            <Text>{record.type === "file" ? record.title : name}</Text>
          </Tooltip>
        );
      },
    },
    {
      title: "Path",
      dataIndex: "path",
      key: "path",
      width: 150,
      className: "table-ellipsis",
      render: (name, record) => (
        <Tooltip
          placement="top"
          title={record.path}
          //color="#000"
          overlayInnerStyle={{ height: "100%", fontSize: 14 }}
        >
          <Text>{record.path}</Text>
        </Tooltip>
      ),
    },
    {
      title: "Last Modified",
      dataIndex: "lastModified",
      key: "lastModified",
      width: 150,
      className: "table-ellipsis",
      render: (date) =>
        // date ? new Date(JSON.parse(date)).toDateString() : "-",
        date ? (
          <Tooltip
            placement="top"
            title={moment(new Date(JSON.parse(date))).format("dddd, MMMM Do, YYYY, h:mm:ss a")}
            //color="#000"
            overlayInnerStyle={{ height: "100%", fontSize: 14 }}
          >
            {moment(new Date(JSON.parse(date))).format("dddd, MMMM Do, YYYY, h:mm:ss a")}
          </Tooltip>
        ) : (
          "-"
        ),
      sorter: (a, b) =>
        a.lastModified &&
        new Date(JSON.parse(a.lastModified)) - new Date(JSON.parse(b.lastModified)),
    },
    {
      title: "Extension",
      dataIndex: "extension",
      key: "extension",
      width: 150,
      render: (extension) => (extension ? extension : "-"),
    },
    {
      title: "Permission Level",
      dataIndex: "permissionLevel",
      key: "permissionLevel",
      className: "table-ellipsis",
      width: 150,
      render: (permission, record) =>
        permission ? (
          <Tooltip
            placement="top"
            title={
              getPermissionLevelsText().find((e) => String(e.level) === permission)?.permission
            }
            overlayInnerStyle={{ height: "100%", fontSize: 14 }}
          >
            {getPermissionLevelsText().find((e) => String(e.level) === permission)?.permission}
            {record.inherit && "*"}
          </Tooltip>
        ) : (
          "-"
        ),
    },
  ];
  const files = useSelector((state) => {
    if (props.extensionOptions?.includes('image')) {
      return state.cannedReports.present.imagesList?.data;
    }
    return state.fileBrowser.files.data;
  });
  const filteredFiles = useSelector((state) => state.fileBrowser.filteredFiles);
  const searchResults = useSelector((state) => state.fileBrowser.searchResults);

  const expandedItem = useSelector((state) => state.fileBrowser.expandedRow);
  const clickedContextRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );

  const selectedColumns = useSelector((state) => state.fileBrowser.tableColumns);
  const [showContext, setShowContext] = useState({
    visible: false,
    x: 0,
    y: 0,
  });
  const [columns, setColumns] = useState(fbColumns);
  useEffect(() => {
    let temp = [];
    if (mode === "compact") {
      temp = fbColumns.filter((e) => e.key === "name");
    } else {
      if (selectedColumns)
        temp = fbColumns.filter((e) => e.key === "name" || selectedColumns.includes(e.key));
    }
    if (temp.length > 0) setColumns(temp);
  }, [selectedColumns, mode]);

  const dataSource = searchResults ? searchResults : filteredFiles;

  const filterValues = useSelector((state) => state.fileBrowser.globalFilters);
  const applyFilters = () => {
    const filters = Object.entries(filterValues);
    const doesFilterExist = filters.find((filter) => filter[1] != undefined);
    if (doesFilterExist) {
      filterFiles(filters);
    } else {
      dispatch(fileBrowserActions.setFilteredFiles(files));
    }
  };

  useEffect(() => {
    applyFilters();
  }, [filterValues, files]);

  const filterFiles = (filters) => {
    let result = [];
    filters.forEach((filter) => {
      const filesToFilter = result.length > 0 ? result : files;
      if (filter[1]) {
        if (filter[0] === "filterByType") {
          const filteredItems = filterByType(JSON.parse(JSON.stringify(filesToFilter)), filter[1]);
          result = filteredItems;
        }
        if (filter[0] === "groupBy") {
          const filteredItems = fbGroupBy(filter[1], JSON.parse(JSON.stringify(filesToFilter)));
          result = filteredItems;
        }
      }
    });
    const updatedResult =
      footerForm && footerForm.type === "Save"
        ? filterByFileType(result, "type", "folder")
        : result;
    dispatch(fileBrowserActions.setFilteredFiles(updatedResult));
  };

  const onRowClassName = (record) => {
    let classes = "";
    if (
      (expandedItem && expandedItem.path === record.path) ||
      (clickedContextRecord &&
        clickedContextRecord.path &&
        clickedContextRecord.path === record.path)
    ) {
      classes += "expand-parent ";
    }

    if (
      record.path === copyOrCutItemDetails?.sourceUrl
      && copyOrCutItemDetails?.action === "cut"
    ) {
      classes += "faded ";
    }

    return classes;
  };

  const onExpandIcon = ({ expanded, onExpand, record }) => {
    return (
      <span style={{ marginRight: 5 }}>
        {record.type === "file" ? (
          getExtensionIcon(record.extension)
        ) : expanded ? (
          <FolderOpenOutlined />
        ) : (
          <FolderOutlined />
        )}
      </span>
    );
  };

  const onExpand = (_, row) => {
    const { children, ...rest } = row;
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({
        clickedRecord: rest,
      })
    );
  };

  const moveRow = (dragIndex, hoverIndex) => {
    const dragRow = dataSource[dragIndex];
  };

  const onTableRow = (record, index) => {
    return {
      record,
      index,
      moveRow,
      onClick: () => { },
      onDoubleClick: (event) => {
        if (record.type === "file") {
          if (onDoubleClick && typeof onDoubleClick == "function") {
            onDoubleClick(record);
          }
          if (!onDoubleClick) {
            if (record.extension === "metadata") {
              dispatch(fileBrowserActions.setShowFileBrowser(false));
              dispatch(
                appActions.setEditModeInfo({
                  dir: record.path,
                  file: record.name,
                  extension: record.extension,
                  route: 'hreport'
                })
              );
              dispatch(updateRoute(routesUrl.helicalReportUrl));



              // 4533
              // dispatch(
              //   appActions.setEditModeInfo({
              //     dir: record.path,
              //     file: record.name,
              //     title: record.title,
              //   })
              // );
              // dispatch(updateRoute(routesUrl.metadataUrl));
            }
            if (["instant", "hr", "image", "efwdd", "hcr"].includes(record.extension)) {
              dispatch(removeAllReports());
              dispatch(
                appActions.setViewModeInfo({
                  file: {
                    path: record.path,
                    name: record.name,
                    title: record.title,
                  },
                  mode: "open",
                  filters: [],
                  extension: record.extension,
                })
              );
              dispatch(updateRoute(routesUrl.reportViewUrl));
            }
            if (record.extension === "cube") {
              // 4533
              dispatch(
                appActions.setEditModeInfo({
                  dir: record.path,
                  file: record.name,
                  title: record.title,
                  extension: record.extension,
                })
              );
              dispatch(updateRoute(routesUrl.cubeUrl));
            }
            if (record.extension === "model") {
              dispatch(
                appActions.setEditModeInfo({
                  dir: record.path,
                  file: record.name,
                  title: record.title,
                  extension: record.extension,
                  action: AGENT_INTERACT_ACTION,
                })
              );
              dispatch(updateRoute(routesUrl.instantBIUrl));
            }
            // if (record.extension === "instant") {
            //   dispatch(
            //     appActions.setEditModeInfo({
            //       dir: record.path,
            //       file: record.name,
            //       title: record.title,
            //     })
            //   );
            //   dispatch(updateRoute(routesUrl.instantBIUrl));
            // }
          }
          if (closeFbOnCallback === undefined || closeFbOnCallback)
            dispatch(fileBrowserActions.setShowFileBrowser(false));
          if (mode !== "compact") {
            dispatch(openCompactFbBrower(false));
            dispatch(replaceReportId(null));
          }
        }
      },
      onContextMenu: (event) => {
        event.preventDefault();
        setShowContext({ visible: false });
        setShowContext({
          record,
          visible: true,
          x: event.clientX,
          y: event.clientY,
        });
        const { children, ...rec } = record;
        dispatch(
          fileBrowserActions.setContextMenuItemDetails({
            clickedRecord: rec,
          })
        );
      },
    };
  };
  const type = "DragableBodyRow";

  const vc = useMemo(() => {
    return VList({
      height: mode === "compact" ? "100%" : footerForm ? "51vh" : "80vh",
      resetTopWhenDataChange: false,
    }); // last data: 59, 80
  }, []);

  const DraggableBodyRow = (props) => {
    const { record, index, moveRow, className, style, ...restProps } = props;
    const ref = useRef(null);
    const [, drag] = useDrag({
      type,
      item: {
        record,
      },
      collect: (monitor) => ({
        isDragging: monitor.isDragging(),
      }),
      canDrag: () => {
        if (record?.type === "folder" || record?.extension !== "hr") return false;
        return true;
      },
    });

    useEffect(() => {
      drag(ref);
    }, [drag]);

    const components = useMemo(() => vc.body.row, []);
    const tempProps = useMemo(() => {
      return {
        ref: ref,
        style: { cursor: "move", ...style },
        ...restProps,
      };
    }, [className, restProps, style]);

    // if(record?.type === "folder" || record?.extension !== "hr") return <>{components(tempProps)}</>
    return <>{components(tempProps, ref)}</>;
  };

  const components = useMemo(() => {
    return {
      ...vc,
      body: {
        ...vc.body,
        row: DraggableBodyRow,
      },
    };
  }, []);

  const handleImport = ({ e, record }) => {
    if (!e || !e.target.files.length) {
      return Notify.error({
        type: "Frontend",
        message: "Please choose an image file (.png, .jpg, etc.)",
      });
    }

    const file = e.target.files[0];

    const formData = new FormData();
    formData.append("file", file);
    formData.append("type", "image");
    formData.append("destination", record.path);

    source = axios.CancelToken.source();
    setIsUploading(true);

    fbRequests.postImageUpload(
      formData,
      {
        cancelToken: source.token,
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          setUploadedPercentage(percentCompleted);
        },
      },
      (res) => {
        setIsUploading(false);
        if (props?.refreshWhenUpload) {
          let element = document.querySelector("#hi-file-browser-refresh")
          element?.click()
        }
        const updatedResult = addFileToFolder(JSON.parse(JSON.stringify(files)), res.data, clickedContextRecord.path)
        dispatch(
          fileBrowserActions.setFbContent({
            loading: false,
            data: updatedResult,
            error: null,
          })
        );
      },
      (e) => {
        if (e.message === "cancel") {
          Notify.success({
            type: "Network Call",
            message: "Request cancelled successfully",
          });
        } else {
          Notify.error({
            type: "Upload Failed",
            message: e.message || "An error occurred during upload.",
          });
        }
        setIsUploading(false);
      }
    );

    e.target.value = "";
  };

  return (
    <>
      <Table
        data-testid="hi-file-browser-fbTable"
        className="fb-table"
        columns={columns}
        rowKey={(record) => {
          if (record.path) return record.path;
          return record.id;
        }}
        size="small"
        pagination={false}
        scroll={{
          y: mode === "compact" ? "100%" : footerForm ? "51vh" : "80vh",
          x: "100%",
        }}
        components={mode === "compact" ? components : vc}
        onRow={onTableRow}
        dataSource={dataSource}
        footer={footerForm ? () => footerForm.form : undefined}
        rowClassName={onRowClassName}
        expandIcon={onExpandIcon}
        onExpand={onExpand}
        expandRowByClick
      />
      <input
        style={{ display: "none" }}
        type="file"
        onChange={(e) => {
          handleImport({ e, record: selectedRecordRef.current })
        }}
        accept="image/*"
        ref={hiddenFileInput}
      />
      {isUploading && (
        <Row>
          <Col span={8}>
            <Progress percent={uploadedPercentage} />
          </Col>
          <Col span={1}></Col>
          <Col span={4}>
            <span
              className="clear-button"
              onClick={() => source.cancel("cancel")}
            >
              Cancel
            </span>
          </Col>
        </Row>
      )}
      {mode !== "compact" && (
        <>
          <ContextMenuActions extensionOptions={props.extensionOptions} />
          {showContext && (
            <ContextMenuWrapper
              visible={showContext.visible}
              setVisible={setShowContext}
              posX={showContext.x}
              posY={showContext.y}
              contextMenuOptions={contextMenuOptions}
              record={showContext.record}
              mode={mode}
              closeFbOnCallback={closeFbOnCallback}
              files={files}
              showEditOnTop={props.showEditOnTop}
              hiddenFileInput={hiddenFileInput}
              selectedRecordRef={selectedRecordRef}
            />
          )}
        </>
      )}
    </>
  );
};

export { FbTable };
