import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Menu, Card, Dropdown, Input, Space, Tooltip, Typography, Checkbox } from "antd";
import { handleAddParameters, handleAddDataSource } from "../../hi-sidebar/hi-reportCeSidebar";
import notify from "../../hi-notifications/notify";
import {
  onEdit,
  handleBlur,
  handleKeyDown,
  onDelete,
  handleSqlTypeChange,
  onChangeDataSource,
  // onChangeParameters,
} from "../utils/reportActions";
import {
  EditOutlined,
  SettingOutlined,
  DeleteOutlined,
  LinkOutlined,
  EllipsisOutlined,
} from "@ant-design/icons";
import {
  storeReportCeParameters,
  storeReportCeEditorData,
} from "../../../redux/actions/reprotce.actions.js";
import "../index.scss";

const { Text } = Typography;

const ParamsDropdown = ({ paramsMenu, id }) => {
  const [visible, setVisible] = useState(false);
  const handleVisibleChange = (value) => setVisible(value);
  return (
    <Dropdown
      overlay={() => paramsMenu(id)}
      trigger={["click"]}
      onVisibleChange={handleVisibleChange}
      visible={visible}
    >
      <span className="cursor-pointer">P</span>
    </Dropdown>
  );
};

const DataSourceDropdown = ({ dataSourceMenu, id }) => {
  const [visible, setVisible] = useState(false);
  const handleVisibleChange = (value) => setVisible(value);
  return (
    <Dropdown
      overlay={() => dataSourceMenu(id)}
      trigger={["click"]}
      visible={visible}
      onVisibleChange={handleVisibleChange}
    >
      <LinkOutlined />
    </Dropdown>
  );
};

const Parameters = () => {
  const dispatch = useDispatch();
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const typesDetails = useSelector((store) => store.reportCe.typesDetails);
  const editing = useSelector((store) => store.reportCe.editing);
  // const [checkedParams, setCheckedParams] = useState([]);
  const Notify = notify(dispatch);

  const handleParameterTypeChange = (id, name) => {
    const filteredData = parametersData.map((param) =>
      param.id === id ? { ...param, type: name } : param
    );
    dispatch(storeReportCeParameters(filteredData));
  };

  const menu = (id) => (
    <Menu>
      {typesDetails?.parameterTypes?.map((type) => (
        <Menu.Item key={type.name} onClick={() => handleParameterTypeChange(id, type.name)}>
          {type.name}
        </Menu.Item>
      ))}
    </Menu>
  );

  const SqlDropdown = ({ sqlMenu, param }) => {
    return (
      <Dropdown overlay={() => sqlMenu(param.id)} trigger={["click"]} placement="bottomRight">
        <Tooltip title={param.sql.type}>
          <span className="cursor-pointer">{param.sql.type[0].toUpperCase()}</span>
        </Tooltip>
      </Dropdown>
    );
  };

  const sqlMenu = (id) => (
    <Menu>
      {typesDetails?.sqlTypes?.map((eachType) => (
        <Menu.Item
          key={eachType.name}
          onClick={() =>
            handleSqlTypeChange(
              id,
              eachType.name,
              parametersData,
              dispatch,
              storeReportCeParameters
            )
          }
        >
          {eachType.name}
        </Menu.Item>
      ))}
    </Menu>
  );

  const quotesMenu = () => {
    return (
      <Card style={{ padding: "5px" }}>
        <Space>
          Quotes
          <Input size="small" style={{ width: "30px" }} />
          <Input size="small" style={{ width: "30px" }} />
        </Space>
      </Card>
    );
  };

  const QuotesDropdown = ({ quotesMenu }) => {
    const [visible, setVisible] = useState(false);
    const handleVisibleChange = (value) => setVisible(value);
    return (
      <Dropdown overlay={quotesMenu} onVisibleChange={handleVisibleChange} visible={visible}>
        <EllipsisOutlined />
      </Dropdown>
    );
  };

  const paramsMenu = (id) => {
    const filteredParms = parametersData.filter((param) => param.id !== id);
    return (
      <Card style={{ padding: "8px" }}>
        {parametersData.length <= 1 ? (
          <span
            onClick={() => {
              handleAddParameters("", dispatch, parametersData);
            }}
          >
            Add Parameters
          </span>
        ) : (
          <Checkbox.Group
          // options={filteredParms}
          // onChange={(checkedValues) => {
          //   console.log(checkedValues);
          //   onChangeParameters(
          //     id,
          //     checkedValues,
          //     parametersData,
          //     dispatch,
          //     storeReportCeParameters
          //   );
          // }}
          >
            {filteredParms.map((eachParam) => (
              <span>
                <Checkbox value={eachParam.id}> {eachParam.name}</Checkbox>
                <QuotesDropdown quotesMenu={quotesMenu} />
              </span>
            ))}
          </Checkbox.Group>
        )}
      </Card>
    );
  };

  const dataSourceMenu = (id) => {
    if (dataSourceData.length === 0) {
      return (
        <Menu>
          <Menu.Item onClick={() => handleAddDataSource("", dispatch, dataSourceData)}>
            Add Datasource
          </Menu.Item>
        </Menu>
      );
    }
    return (
      <Card style={{ padding: "5px" }}>
        <Checkbox.Group
          options={dataSourceData}
          onChange={(checkedValues) =>
            onChangeDataSource(
              id,
              checkedValues,
              dataSourceData,
              parametersData,
              dispatch,
              storeReportCeParameters
            )
          }
        />
      </Card>
    );
  };

  const handleDelete = (param, parametersData) => {
    const updatedData = onDelete(param.id, parametersData);
    dispatch(storeReportCeParameters(updatedData));
  };

  const handleEdit = (param, parametersData) => {
    const updatedData = onEdit(param.id, parametersData);
    dispatch(storeReportCeParameters(updatedData));
  };

  const handleConfigure = (param) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: param.id,
          name: param.name,
          type: "parameter",
        })
      );
    }
  };
  const renderParametrs = () => {
    return parametersData.map((param) => (
      <Space key={param.id} style={{ fontWeight: "bold", marginBottom: "5px" }}>
        {param.isEditClicked ? (
          <span>
            <Input
              defaultValue={`${param.name}`}
              onBlur={(e) =>
                handleBlur(e, param.id, parametersData, dispatch, storeReportCeParameters, Notify)
              }
              onKeyDown={(e) =>
                handleKeyDown(
                  e,
                  param.id,
                  parametersData,
                  dispatch,
                  storeReportCeParameters,
                  Notify
                )
              }
            />
          </span>
        ) : (
          <div style={{ display: "flex", flexDirection: "column" }}>
            <div>
              <Text ellipsis={true} style={{ color: "#fe7a87FF", width: "103px" }}>
                <Text className="text-number number">{parametersData.indexOf(param) + 1}</Text>
                <Tooltip title={`${param.name}`}>{param.name}</Tooltip>
              </Text>
              <Space>
                <span>
                  <Dropdown
                    overlay={() => menu(param.id)}
                    trigger={["click"]}
                    placement="bottomRight"
                  >
                    <Tooltip className="cursor-pointer" title={param.type}>
                      {param.type[0]}
                    </Tooltip>
                  </Dropdown>
                </span>
                <span>
                  <Tooltip title="Configuration">
                    <SettingOutlined onClick={() => handleConfigure(param)} />
                  </Tooltip>
                </span>
                <span>
                  <Tooltip title="Edit">
                    <EditOutlined
                      onClick={() => {
                        handleEdit(param, parametersData);
                      }}
                    />
                  </Tooltip>
                </span>
                <span>
                  <Tooltip title="Delete">
                    <DeleteOutlined onClick={() => handleDelete(param, parametersData)} />
                  </Tooltip>
                </span>
              </Space>
            </div>
            <div style={{ textAlign: "center" }}>
              <Text ellipsis={true} style={{ color: "#5C5CFF", width: "103px" }}>
                <Text className="sql-number number">{param.number}</Text>
                <span onClick={() => handleConfigure(param)}>SQL</span>
              </Text>
              <Space>
                <ParamsDropdown paramsMenu={paramsMenu} id={param.id} />
                <DataSourceDropdown dataSourceMenu={dataSourceMenu} id={param.id} />
                <SqlDropdown sqlMenu={sqlMenu} param={param} />
              </Space>
            </div>
          </div>
        )}
      </Space>
    ));
  };

  return (
    <>
      {parametersData.length > 0 ? (
        renderParametrs()
      ) : (
        <span className="cursor-pointer">Add Parameters</span>
      )}
    </>
  );
};

export default Parameters;
