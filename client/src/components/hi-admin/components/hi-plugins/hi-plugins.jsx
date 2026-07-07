import { useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { useDebounce } from "../../../../hooks";
import { VList } from "virtuallist-antd";
import { v4 as uuidv4 } from "uuid";
import { storePluginsData } from "../../../../redux/actions/admin.actions";
import { filterPluginsData, filterDeletePlugins } from "../../../../utils/utilities";
import {
  Row,
  Col,
  Input,
  Button,
  Table,
  Popconfirm,
  Drawer,
  Tooltip,
  Space,
  Skeleton,
  Card,
  Popover,
} from "antd";
import {
  WarningOutlined,
  InfoCircleOutlined,
  SyncOutlined,
  DeleteOutlined,
} from "@ant-design/icons";
import requests from "../../../../base/requests";
import notify from "../../../hi-notifications/notify";
import "./hi-plugins.scss";
import LoadingBar from "../../../common/components/hi-loading-bar";
import PopconfirmBody from "../../../common/components/Hi-Popconfirm";
import formatDate from "../../../common/components/formatDate";
import PluginsSkeleton from "../../../common/custom-icons/CustomSkeletons/plugins/PluginSkeleton"
import { addSpacesInCamelCase } from "./helperMethods";

const { Search } = Input;
let tableVirtualProps = {};

const HIPlugins = ({ apiRef, handleAbort }) => {
  const pluginsDataSource = useSelector((store) => store.admin.pluginsData);
  const [visible, setVisible] = useState(false);
  const [activeInfo, setActiveInfo] = useState({});
  const [userSearch, setUserSearch] = useState("");
  const [moreInfo, setMoreInfo] = useState([]);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [clickedRowName, setClickedRowName] = useState("");
  const [loading, setLoading] = useState(pluginsDataSource === null ? true : false);
  const dispatch = useDispatch();
  const debounceValue = useDebounce(userSearch, 500);

  const Notify = notify(dispatch);

  const uri = "monitor/system/customWatcher";
  const uninstallUri = "monitor/system/deletePlugin";

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      fetchPluginsDetails();
    }
  }, [pluginsDataSource]);

  if (pluginsDataSource !== null) {
    if (pluginsDataSource.length > 7) {
      tableVirtualProps = {
        scroll: { y: 425 },
        components: VList({
          height: 425,
          vid: "plugins",
        }),
      };
    }
  } else {
    tableVirtualProps = {};
  }

  const fetchPluginsDetails = (refresh = false) => {
    if (refresh || pluginsDataSource === null) {
      return requests.admin(dispatch).postPluginsDetails(
        { action: "scan" },
        uri,
        (res) => {
          dispatch(storePluginsData(res.data[0].plugins));
          setLoading(false);

          // Notify.success({
          //   type: "Network Call",
          //   ...res,
          // });
        },
        (e) => {
          // Notify.error({
          //   type: "Network Call",
          //   ...e,
          // });
        }
      );
    }
  };

  const onClickRefresh = () => {
    setLoading(true);
    apiRef.current = fetchPluginsDetails({ refresh: true });
  };

  const onClickDeletePlugin = (record) => {
    setClickedRowName(record.temporaryName);
    setDeleteLoading(true);
    const driverPath = `${record.details.actualPath}`;
    requests.admin(dispatch).deletePluginsDetails(
      { pluginJar: driverPath },
      uninstallUri,
      (res) => {
        dispatch(storePluginsData(filterDeletePlugins(pluginsDataSource, record.details.jarName)));
        setDeleteLoading(false);

        // Notify.success({
        //   type: "Network Call",
        //   ...res,
        // });
      },
      (e) => {
        setDeleteLoading(false);
        // Notify.error({
        //   type: "Network Call",
        //   ...e,
        // });
      }
    );
  };

  const onClickMoreInfo = (record) => {
    const convertedMoreDetails = [];
    setVisible(true);
    setActiveInfo(record);
    const moreDetails = record.details;

    convertedMoreDetails.push({ pluginKey: "Name", pluginValue: record.name });
    convertedMoreDetails.push({
      pluginKey: "Temporary Name",
      pluginValue: record.temporaryName,
    });
    convertedMoreDetails.push({
      pluginKey: "Plugin Type",
      pluginValue: record.pluginType,
    });
    Object.keys(moreDetails).map((key) => {
       let pluginKey = key.replace(/(^\w|\s\w)/g, (m) => m.toUpperCase());
       pluginKey = addSpacesInCamelCase(pluginKey)
      const pluginValue = moreDetails[key];
      convertedMoreDetails.push({ pluginKey, pluginValue });
      setMoreInfo(convertedMoreDetails);
    });
    convertedMoreDetails.push({
      pluginKey: "Status",
      pluginValue: record.status,
    });
    convertedMoreDetails.push({
      pluginKey: "Id",
      pluginValue: uuidv4().substring(0, 10),
    });
  };

  const onCloseDrawer = () => {
    setVisible(false);
  };

  const pluginDataColumns = [
    {
      title: "Plugin Name",
      dataIndex: "temporaryName",
      key: "name",
      className: "table-ellipsis",
      sorter: (a, b) => a.temporaryName.localeCompare(b.temporaryName),
      render: (title, record) => {
        return (
          <span>
            <Tooltip title="This is a temporary plugin name. This may change if the plugin is used">
              <WarningOutlined className="plugin-warning-icon" />
            </Tooltip>
            <Tooltip title={title}>{title}</Tooltip>
          </span>
        );
      },
    },
    {
      title: "Installed Date",
      dataIndex: "installedDate",
      key: "installedDate",
      className: "table-ellipsis",
      sorter: (a, b) => new Date(a.installedDate) - new Date(b.installedDate),
      render:(title, record) => { 
      return (
        <span>{formatDate(record.installedDate, false)}</span>
      )
      }
    },
    {
      title: "Status",
      dataIndex: "status",
      key: "status",
      className: "table-ellipsis",
      filters: [
        {
          text: "Enabled",
          value: "Enabled",
        },
        {
          text: "Disabled",
          value: "Disabled",
        },
      ],
      onFilter: (value, record) => record.status.indexOf(value) === 0,
    },
    {
      title: "Actions",
      key: "action",
      className: "table-ellipsis",
      render: (_, record) => {
        return pluginsDataSource.length >= 1 ? (
          <>
            <Tooltip title="More Info" placement="left">
              <Button
                type="text"
                icon={<InfoCircleOutlined />}
                onClick={() => onClickMoreInfo(record)}
                className="plugins-more-info"
              />
            </Tooltip>

            <Popconfirm
            title ={<PopconfirmBody intent="delete" description={"Uninstalling this plugin may have severe impact"} />}
            onConfirm={() => onClickDeletePlugin(record)}
            >
              <Tooltip title="uninstall" placement="right">
                <Button
                  type="text"
                  icon={<DeleteOutlined />}
                  loading={clickedRowName === record.temporaryName && deleteLoading}
                />
              </Tooltip>
            </Popconfirm>
          </>
        ) : null;
      },
    },
  ];

  const moreInfoColumns = [
    {
      title: "Key",
      id: 30,
      key: "key",
      dataIndex: "pluginKey",
    },
    {
      title: "Value",
      id: 40,
      key: "value",
      dataIndex: "pluginValue",
    },
  ];

  const getSearchResults = () => {
    if (debounceValue === "") {
      return pluginsDataSource;
    }
    const filteredDataSources = filterPluginsData(pluginsDataSource, userSearch);

    return filteredDataSources;
  };

  const serchedData = getSearchResults();

  const content = (
    <>
      <span className="plugins-alert">
        To add/remove a new plugin , go to
        <span className="plugins-code">/hi/hi-repository/System</span>
        location and add/remove plugin in 'Driver' or 'Plugins' folder
      </span>
    </>
  );

  const renderSkeleton = () => <Skeleton title={true} paragraph={false} />;
  return (
    <>
      <Row className="hi-admin-plugins-container">
        <Col span={24} className="plugins-header-container">
          <Row className="plugins-header-data-container">
            <Col span={4}>
              <Row align="middle">
                <Col className="plugins-title">
                  <Space>
                    <span>Plugins</span>
                    <span>({pluginsDataSource === null ? 0 : pluginsDataSource.length})</span>
                    <Popover content={content} placement="right" trigger="hover">
                      <InfoCircleOutlined />
                    </Popover>
                  </Space>
                </Col>
              </Row>
            </Col>
            <Col span={14}>
              <Row justify="end" align="middle">
                <Col xs={19} lg={10}>
                  <Search
                    placeholder="search plugins"
                    allowClear
                    onChange={(e) => setUserSearch(e.target.value)}
                  />
                </Col>
                <Col xs={5} lg={2} className="plugins-refresh-button">
                  <Tooltip title="Refresh">
                    <Button icon={<SyncOutlined />} onClick={onClickRefresh} />
                  </Tooltip>
                </Col>
              </Row>
            </Col>
          </Row>
        </Col>
        <Col span={24} className="plugins-table-container">
          <Card bordered={false} hoverable>
            {(loading && !["test"].includes(process.env.NODE_ENV)) ? <><LoadingBar handleClick={() => handleAbort({ setLoading })} /><PluginsSkeleton /></> :
              <Table
                data-testid="plugins-table"
                dataSource={serchedData}
                size="small"
                columns={
                  loading
                    ? pluginDataColumns.map((column) => {
                      return { ...column, render: renderSkeleton };
                    })
                    : pluginDataColumns
                }
                pagination={false}
                rowKey="id"
                bordered
                {...tableVirtualProps}
                rowClassName={(record, index) => {
                  let className = index % 2 && "table-row-color";
                  return className;
                }}
              />}
          </Card>
        </Col>
      </Row>
      <Drawer
      data-testid = "hi-plugins-drawer"
        title={`${activeInfo.temporaryName}`}
        placement="right"
        size="large"
        onClose={onCloseDrawer}
        visible={visible}
      >
        <Table
          columns={moreInfoColumns}
          dataSource={moreInfo}
          bordered
          size="small"
          pagination={false}
          rowClassName={(record, index) => {
            let className = index % 2 && "table-row-color";
            return className;
          }}
        />
      </Drawer>
    </>
  );
};

export { HIPlugins };
