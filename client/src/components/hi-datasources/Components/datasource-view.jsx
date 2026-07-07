import requests from "../../../base/requests";
import { ShareFinalModal } from "../../hi-fileBrowser/components";
import notify from "../../hi-notifications/notify";
import { useEffect, useRef, useState } from "react";
import { fileBrowserActions } from "../../../redux/actions";
import { updateMetadataState, appActions } from "../../../redux/actions";
import { useDispatch, useSelector } from "react-redux";
import {
  Table,
  Button,
  Tooltip,
  Space,
  Popconfirm,
  Row,
  Col,
  Dropdown,
  Menu,
  Input,
} from "antd";
import {
  ShareAltOutlined,
  EditOutlined,
  InsertRowBelowOutlined,
  DeleteOutlined,
  SearchOutlined,
} from "@ant-design/icons";
import {
  setDsMode,
  setViewData,
  setEditData,
  setIsEditClicked,
  setClickedRecordData,
  setDataSourceConnection,
  setFlatFilesUploadName,
  setDsSericeCall,
  resetStateFields,
} from "../../../redux/actions/datasource.actions";
import { getQuickTestFD } from "../utils/getQuickTestFD";
import { getDeleteFD } from "../utils/getDeleteFD";
import { checkIfGroovyManaged } from "../utils/checkIfGroovyManaged";
import { checkIfGroovyPlain } from "../utils/checkIfGroovyPlain";
import PopconfirmBody from "../../common/components/Hi-Popconfirm";
import {calculateSerialNumber} from '../../../utils/table-utils';

const getDeleteMessage = (deleteType, clickedRecordData) => {
  if (deleteType === "cascade") {
    return <div>All the associated files with <strong>{clickedRecordData.name}</strong> will be deleted. Are you sure?</div>
  }
  return <div>Are you sure you want to delete <strong>{clickedRecordData.name}</strong> datasource?</div>
}
const DeleteConnection = (props) => {
  const { clickedRecordData, permissionLevel, record, setViewData, viewData } =
    props;
  const buttonRef = useRef();
  const dispatch = useDispatch();

  const [deleteType, setDeleteType] = useState(null);
  const handleDelete = (deleteType) => {
    if (!deleteType) return;
    const uri = "core/dataSource/delete";
    let formData = getDeleteFD({ clickedRecordData, deleteType });

    requests.datasource(dispatch).postDataSourceData(
      formData,
      uri,
      (res) => {
        const updatedData = viewData.filter((eachData) => {
          if (formData.classifier === "efwd") {
            if (
              eachData.data.dir === formData.directory &&
              eachData.data.id === formData.id
            ) {
              return false;
            }
            return true;
          } else {
            return eachData.data.id !== formData.id;
          }
        });
        dispatch(setViewData(updatedData));
        // Notify.success({
        //   type: "Network call",
        //   ...res,
        // });
      },
      (e) => {
        // Notify.error({
        //   type: "Network call",
        //   ...e,
        // });
      }
    );
  };

  // const menu = () => {
  //   return (
  //     <Menu onClick={({ key }) => setDeleteType(key)}>
  //       <Menu.Item key="simple">Simple</Menu.Item>
  //       <Menu.Item key="cascade">Cascade</Menu.Item>
  //     </Menu>
  //   );
  // };

  useEffect(() => {
    if (deleteType && buttonRef?.current) {
      buttonRef.current.click();
    }
  }, [deleteType]);

  return (
    <>
      {deleteType ? (
        <Popconfirm
        overlayStyle={{padding:"2px"}}
          title={<PopconfirmBody intent="delete" description={getDeleteMessage(deleteType, clickedRecordData)} />}
          onConfirm={() => {
            setDeleteType(null);
            handleDelete(deleteType);
          }}
          onCancel={() => setDeleteType(null)}
          onVisibleChange={(visible) => {
            if (!visible) {
              setDeleteType(null);
            }
          }}
          placement="left"
        >
          <Tooltip title="Delete" >
            <Button
              type="text"
              ref={buttonRef}
              onClick={() => {
                dispatch(setClickedRecordData(record));
              }}
              icon={<DeleteOutlined />}
            />
          </Tooltip>
        </Popconfirm>
      ) : (
        // <Dropdown
        //   // disabled={permissionLevel < 4 ? true : false}
        //   overlay={menu}
        //   trigger={["click"]}
        // >
        <Tooltip title="Delete" >
          <Button
            disabled={permissionLevel < 4 ? true : false}
            type="text"
            onClick={() => {
              if (!(permissionLevel < 4 ? true : false)) {
                setDeleteType('simple')
                dispatch(setClickedRecordData(record));
              }
            }}
            icon={<DeleteOutlined />}
          />
        </Tooltip>
        // </Dropdown>
      )}
    </>
  );
};

const DataSourceView = () => {
  const [pageSize, setPageSize] = useState(10);
  const viewData = useSelector((store) => store.datasource.viewData);
  const isShareVisible = useSelector(
    (state) => state.fileBrowser.isShareModalVisible
  );
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const clickedRecordData = useSelector(
    (store) => store.datasource.clickedRecordData
  );

  const [page, setPage] = useState(1);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [isTestServiceProcessing, setIsTestServicePrrocessing] = useState({ id: "", loading: false });
  const handleQuickTest = (record) => {
    const uri = "core/dataSource/quickTest";
    let formData = getQuickTestFD(record);
    // const formData = {
    //   id: record.data.id,
    //   type: record.data.type,
    //   classifier: record.classifier,
    //   ...(record.classifier === "efwd" && {
    //     dir: record.data.dir,
    //   }),
    // };
    setIsTestServicePrrocessing({ id: record.data.id, loading: true });

    requests.datasource(dispatch).postDataSourceData(
      formData,
      uri,
      (res) => {
        setIsTestServicePrrocessing({ id: record.data.id, loading: false });
        // Notify.success({
        //   type: "Network call",
        //   ...res,
        // });
      },
      (e) => {
        setIsTestServicePrrocessing({ id: record.data.id, loading: false });
        // Notify.error({
        //   type: "Network call",
        //   ...e,
        // });
      }
    );
  };

  const handleEdit = (record) => {
    dispatch(setClickedRecordData(record));
    getEditDataInfo(record);
  };

  const getEditDataInfo = (record) => {
    dispatch(setDsSericeCall(true));
    const uri = "core/dataSource/read";
    const formData = {
      id: record.data.id,
      type: record.data.type,
      classifier: record.classifier,
      dir: record.data.dir,
    };
    if (record.driver) {
      formData.driver = record.driver;
    }
    dispatch(setEditData({}));
    requests.datasource(dispatch).postDataSourceData(
      formData,
      uri,
      (res) => {
        dispatch(setIsEditClicked(true));
        dispatch(setEditData(res));
        dispatch(setDsSericeCall(false));
      },
      (e) => {
        dispatch(setDsSericeCall(false));
      }
    );
  };

  const handleShare = (record) => {
    dispatch(setClickedRecordData(record));
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const handleMetaDataClick = (record) => {
    // #4128 fix by gagan on 22nd March 2022 - start
    dispatch(
      updateMetadataState({
        key: "activeDataSource",
        value: record,
      })
    );
    dispatch(appActions.updateRoute("/metadata"));
    // #4128 fix by gagan on 22nd March 2022 - end
    dispatch(resetStateFields([]));
  };

  // Search functionality code
  const [searchText, setSearchText] = useState("");
  const [searchedColumn] = useState("");

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
    setSearchText(selectedKeys[0]);
    setSearchText(dataIndex);
  };

  const handleReset = (clearFilters) => {
    clearFilters();
    setSearchText("");
  };

  const getColumnSearchProps = (dataIndex) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => (
      <div style={{ padding: 8 }}>
        <Input
          placeholder={`Search ${dataIndex === "orgName" ? "organization" : dataIndex
            }`}
          value={selectedKeys[0]}
          onChange={(e) =>
            setSelectedKeys(e.target.value ? [e.target.value] : [])
          }
          onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            Search
          </Button>
          <Button
            onClick={() => {
              handleReset(clearFilters);
              handleSearch(selectedKeys, confirm, dataIndex);
            }}
            size="small"
            style={{ width: 90 }}
          >
            Reset
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
    ),
    onFilter: (value, record) => {
      if (dataIndex === "conn.id") {
        return record.data
          ? String(record.data?.id)?.includes(parseInt(value))
          : "";
      } else {
        return record[dataIndex]
          ? record[dataIndex]
            .toString()
            .toLowerCase()
            .includes(value.toLowerCase())
          : "";
      }
    },
    onFilterDropdownVisibleChange: (visible) => {
      if (visible) {
      }
    },
    render: (text) =>
      searchedColumn === dataIndex ? (
        <div
          highlightStyle={{ backgroundColor: "#ffc069", padding: 0 }}
          searchWords={[searchText]}
          autoEscape
          textToHighlight={text ? text.toString() : ""}
        />
      ) : (
        text
      ),
  });

  const columns = [
    {
      title: "S No",
      key: "sno",
      width: 60,
      render: (value, item, index) => calculateSerialNumber(page, pageSize, index),
    },
    {
      title: "Name",
      key: "Name",
      width: 150,
      dataIndex: "name",
      ellipsis: true,
      sorter: (a, b) => {
        let aName = a.name.toLowerCase();
        let bName = b.name.toLowerCase();
        if (aName > bName) {
          return 1;
        } else if (aName < bName) {
          return -1;
        } else {
          return 0;
        }
      },
      ...getColumnSearchProps("name"),
      render: (text) => (
        <Tooltip title={text} placement="topLeft">
          <span>{text}</span>
        </Tooltip>
      ),
    },
    {
      title: "Conn.Id",
      key: "Conn.Id",
      // dataIndex:"conn.id",
      width: 120,
      ellipsis: true,
      sorter: (a, b) => a.data.id - b.data.id,
      ...getColumnSearchProps("conn.id"),
      render: (record) => (
        <Tooltip title={record.data.id} placement="topLeft">
          <span>{record.data.id}</span>
        </Tooltip>
      ),
    },
    ...(clickedActiveDatabaseData.name === "Plain Jdbc DataSource" ||
      checkIfGroovyManaged(clickedActiveDatabaseData || {}) ||
      checkIfGroovyPlain(clickedActiveDatabaseData || {})
      ? [
        {
          title: "Dir",
          key: "Dir",
          width: 100,
          ellipsis: true,
          render: (record) => (
            <Tooltip title={record.data.dir} placement="topLeft">
              <span>{record.data.dir}</span>
            </Tooltip>
          ),
        },
      ]
      : []),
    ,
    {
      title: "Actions",
      key: "Actions",
      // width: "50%",
      render: (text, record) => {
        const { permissionLevel } = record;
        return permissionLevel ? (
          <>
            <Tooltip title="Test">
              <Button loading={isTestServiceProcessing.loading && isTestServiceProcessing.id === record.data.id} type="text" onClick={() => handleQuickTest(record)}>
                Test
              </Button>
            </Tooltip>
            <Tooltip title="Edit">
              <Button
                type="text"
                disabled={permissionLevel < 3 ? true : false}
                icon={<EditOutlined />}
                onClick={() => handleEdit(record)}
              />
            </Tooltip>
            <Tooltip title="Delete">
              <DeleteConnection
                permissionLevel={permissionLevel}
                clickedRecordData={clickedRecordData}
                viewData={viewData}
                setViewData={setViewData}
                record={record}
              />
            </Tooltip>
            <Tooltip title="Share">
              <Button
                type="text"
                disabled={permissionLevel < 5 ? true : false}
                onClick={() => handleShare(record)}
                icon={<ShareAltOutlined />}
              />
            </Tooltip>
            <Tooltip title="Create Metadata">
              <Button
                type="text"
                icon={<InsertRowBelowOutlined />}
                onClick={() => handleMetaDataClick(record)}
              />
            </Tooltip>
          </>
        ) : (
          <></>
        );
      },
    },
  ];

  const renderDataSourceView = () => {
    return (
      <Row data-testid="hi-datasource-view-row">
        <Col span={24}>
          <Table
            rowKey="key"
            dataSource={viewData}
            columns={columns}
            size="small"
          pagination={{
            pageSize,
            onChange(current) {
              setPage(current);
            },
            onShowSizeChange: (current, size) => setPageSize(size),
            pageSizeOptions: [10, 20, 50, 100],
          }}
          />
        </Col>
        {isShareVisible && (
          <ShareFinalModal
            from="datasource"
            shareOptions={{
              classifier: clickedRecordData.classifier,
              id: clickedRecordData.data.id,
              type: "dataSource",
              ...(clickedRecordData.classifier === "global" && {
                dataSourceProvider: clickedRecordData.dataSourceProvider,
              }),
              ...(clickedRecordData.classifier === "efwd" && {
                dir: clickedRecordData.data.dir,
              }),
            }}
          />
        )}
      </Row>
    );
  };

  return renderDataSourceView();
};

export default DataSourceView;
