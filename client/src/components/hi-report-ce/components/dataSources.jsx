import { useDispatch, useSelector } from "react-redux";
import { Menu, Dropdown, Input, Space, Tooltip, Typography } from "antd";
import { onEdit, handleKeyDown, handleBlur, onDelete } from "../utils/reportActions";
import { EditOutlined, DeleteOutlined, SettingOutlined } from "@ant-design/icons";
import {
  storeReportCeDatasource,
  storeReportCeEditorData,
} from "../../../redux/actions/reprotce.actions.js";
import notify from "../../hi-notifications/notify";

const { Text } = Typography;

const DataSources = () => {
  const dispatch = useDispatch();
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const typesDetails = useSelector((store) => store.reportCe.typesDetails);
  const editing = useSelector((store) => store.reportCe.editing);
  const Notify = notify(dispatch);

  const handleDelete = (eachData, dataSourceData) => {
    const updatedData = onDelete(eachData.id, dataSourceData);
    dispatch(storeReportCeDatasource(updatedData));
  };

  const handleEdit = (eachData, dataSourceData) => {
    const updatedData = onEdit(eachData.id, dataSourceData);
    dispatch(storeReportCeDatasource(updatedData));
  };

  const handleConfigure = (eachData) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: eachData.id,
          name: eachData.name,
          type: "datasource",
        })
      );
    }
  };

  const handleDataSourceTypeChange = (id, eachType) => {
    const filteredData = dataSourceData.map((eachData) => {
      if (eachData.id === id) {
        return {
          ...eachData,
          type: {
            name: eachType.name,
            type: eachType.type,
          },
        };
      } else {
        return eachData;
      }
    });
    dispatch(storeReportCeDatasource(filteredData));
  };

  const menu = (id) => (
    <Menu>
      {typesDetails.connTypes?.map((eachType) => (
        <Menu.Item key={eachType.name} onClick={() => handleDataSourceTypeChange(id, eachType)}>
          {eachType.name}
        </Menu.Item>
      ))}
    </Menu>
  );

  return (
    <>
      {dataSourceData.length > 0 ? (
        dataSourceData.map((eachData) => (
          <Space key={eachData.id} style={{ fontWeight: "bold", marginBottom: "5px" }}>
            {eachData.isEditClicked ? (
              <span>
                <Input
                  defaultValue={`${eachData.name}`}
                  onBlur={(e) =>
                    handleBlur(
                      e,
                      eachData.id,
                      dataSourceData,
                      dispatch,
                      storeReportCeDatasource,
                      Notify
                    )
                  }
                  onKeyDown={(e) =>
                    handleKeyDown(
                      e,
                      eachData.id,
                      dataSourceData,
                      dispatch,
                      storeReportCeDatasource,
                      Notify
                    )
                  }
                />
              </span>
            ) : (
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  width: "160px",
                }}
              >
                <Space>
                  <Text
                    ellipsis={true}
                    style={{
                      color: "#fe7a87FF",
                      width: "103px",
                      marginRight: "5px",
                    }}
                  >
                    <Text
                      style={{
                        backgroundColor: "#fe7a87",
                        color: "#fff",
                        padding: "5px 5px",
                        marginRight: "5px",
                      }}
                    >
                      {eachData.number}
                    </Text>
                    <Tooltip title={`${eachData.name}`}>{eachData.name}</Tooltip>
                  </Text>
                  <Dropdown
                    overlay={() => menu(eachData.id)}
                    trigger={["click"]}
                    placement="bottomRight"
                  >
                    <Tooltip title={eachData.type.name}>
                      <Text>{eachData.type.name[0]}</Text>
                    </Tooltip>
                  </Dropdown>
                  <span>
                    <Tooltip title="Edit">
                      <SettingOutlined
                        onClick={() => {
                          handleConfigure(eachData);
                        }}
                      />
                    </Tooltip>
                  </span>
                  <span>
                    <Tooltip title="Edit">
                      <EditOutlined
                        onClick={() => {
                          handleEdit(eachData, dataSourceData);
                        }}
                      />
                    </Tooltip>
                  </span>
                  <span>
                    <Tooltip title="Delete">
                      <DeleteOutlined onClick={() => handleDelete(eachData, dataSourceData)} />
                    </Tooltip>
                  </span>
                </Space>
              </div>
            )}
          </Space>
        ))
      ) : (
        <span>Add DataSources</span>
      )}
    </>
  );
};

export default DataSources;
