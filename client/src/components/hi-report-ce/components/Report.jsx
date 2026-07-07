import { useDispatch, useSelector } from "react-redux";
import {
  onEdit,
  handleKeyDown,
  handleBlur,
  onDelete,
  handleSqlTypeChange,
  onChangeDataSource,
} from "../utils/reportActions";
import { Menu, Card, Dropdown, Input, Space, Tooltip, Typography, Checkbox } from "antd";
import { EditOutlined, DeleteOutlined, LinkOutlined, SettingOutlined } from "@ant-design/icons";
import {
  storeReportCeReport,
  storeReportCeEditorData,
} from "../../../redux/actions/reprotce.actions.js";
import { handleAddParameters, handleAddDataSource } from "../../hi-sidebar/hi-reportCeSidebar";
import notify from "../../hi-notifications/notify";
import "../index.scss";

const { Text } = Typography;

const Report = () => {
  const dispatch = useDispatch();
  const reportData = useSelector((store) => store.reportCe.reportData);
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const typesDetails = useSelector((store) => store.reportCe.typesDetails);
  const editing = useSelector((store) => store.reportCe.editing);
  const Notify = notify(dispatch);

  const handleDelete = (report, reportData) => {
    const updatedData = onDelete(report.id, reportData);
    dispatch(storeReportCeReport(updatedData));
  };

  const handleEdit = (report, reportData) => {
    const updatedData = onEdit(report.id, reportData);
    dispatch(storeReportCeReport(updatedData));
  };

  const handleReportTypeChange = (id, eachType) => {
    const filteredData = reportData.map((report) => {
      if (report.id === id) {
        return {
          ...report,
          vizName: eachType.name,
          type: eachType.type,
          subtype: eachType.subtype,
          icon: eachType.icon,
        };
      } else {
        return report;
      }
    });
    dispatch(storeReportCeReport(filteredData));
  };

  const chartMenu = (id) => (
    <Menu>
      {typesDetails?.vizTypes?.map((eachType) => (
        <Menu.Item key={eachType.name} onClick={() => handleReportTypeChange(id, eachType)}>
          <Space>
            <img
              className="viz-icon"
              alt={eachType.icon}
              src={`../images/hi-report-charts/${eachType.icon}.png`}
            />
            {eachType.name}
          </Space>
        </Menu.Item>
      ))}
    </Menu>
  );

  const sqlMenu = (id) => (
    <Menu>
      {typesDetails?.sqlTypes?.map((eachType) => (
        <Menu.Item
          key={eachType.name}
          onClick={() =>
            handleSqlTypeChange(id, eachType.name, reportData, dispatch, storeReportCeReport)
          }
        >
          {eachType.name}
        </Menu.Item>
      ))}
    </Menu>
  );

  const onChange = (checkedValues, id) => {
    const selectedParams = parametersData.filter((eachParam) => {
      if (checkedValues.includes(eachParam.id)) {
        return true;
      }
      return false;
    });

    const updatedData = reportData.map((eachReport) => {
      if (eachReport.id === id) {
        return {
          ...eachReport,
          sql: { ...eachReport.sql, parameters: selectedParams },
        };
      }
      return eachReport;
    });

    dispatch(storeReportCeReport(updatedData));
  };

  const paramsMenu = (id) => (
    <Card style={{ padding: "8px" }}>
      {parametersData.length === 0 ? (
        <span onClick={() => handleAddParameters("", dispatch, parametersData)}>
          Add Parameters
        </span>
      ) : (
        <>
          <Checkbox.Group
            options={parametersData}
            onChange={(checkedValues) => onChange(checkedValues, id)}
          />
        </>
      )}
    </Card>
  );

  const dataSourceMenu = (id) => (
    <Card style={{ padding: "5px" }}>
      {dataSourceData.length === 0 ? (
        <span onClick={() => handleAddDataSource("", dispatch, dataSourceData)}>
          Add Datasource
        </span>
      ) : (
        <Checkbox.Group
          options={dataSourceData}
          onChange={(checkedValues) =>
            onChangeDataSource(
              id,
              checkedValues,
              dataSourceData,
              reportData,
              dispatch,
              storeReportCeReport
            )
          }
        />
      )}
    </Card>
  );

  const handleConfigure = (report) => {
    if (editing) {
      Notify.error({
        message: "Please click on Apply button to save changes.",
        type: "warning",
      });
    } else {
      dispatch(
        storeReportCeEditorData({
          id: report.id,
          name: report.name,
          type: "report",
        })
      );
    }
  };

  const VizDropDown = ({ chartMenu, report }) => {
    return (
      <span>
        <Dropdown overlay={() => chartMenu(report.id)} trigger={["click"]}>
          <Tooltip className="cusor-pointer" title={report.vizName}>
            <img
              className="viz-icon"
              alt={report.icon}
              src={`../images/hi-report-charts/${report.icon}.png`}
            />
          </Tooltip>
        </Dropdown>
      </span>
    );
  };

  const renderReports = () => {
    return reportData.map((report) => (
      <Space key={report.id} style={{ fontWeight: "bold", marginBottom: "5px" }}>
        {report.isEditClicked ? (
          <span>
            <Input
              defaultValue={`${report.name}`}
              onBlur={(e) =>
                handleBlur(e, report.id, reportData, dispatch, storeReportCeReport, Notify)
              }
              onKeyDown={(e) =>
                handleKeyDown(e, report.id, reportData, dispatch, storeReportCeReport, Notify)
              }
            />
          </span>
        ) : (
          <div className="display">
            <div>
              <Text ellipsis={true} style={{ color: "#fe7a87FF", width: "103px" }}>
                <span className="text-number">{reportData.indexOf(report) + 1}</span>
                <Tooltip title={`${report.name}`}>{report.name}</Tooltip>
              </Text>
              <Space>
                <VizDropDown chartMenu={chartMenu} report={report} />
                <span>
                  <Tooltip title="Configuration">
                    <SettingOutlined onClick={() => handleConfigure(report)} />
                  </Tooltip>
                </span>
                <span>
                  <Tooltip title="Edit">
                    <EditOutlined
                      onClick={() => {
                        handleEdit(report, reportData);
                      }}
                    />
                  </Tooltip>
                </span>
                <span>
                  <Tooltip title="Delete">
                    <DeleteOutlined onClick={() => handleDelete(report, reportData)} />
                  </Tooltip>
                </span>
              </Space>
            </div>
            <div style={{ textAlign: "center" }}>
              <Text ellipsis={true} style={{ color: "#5C5CFF", width: "103px" }}>
                <span className="sql-number number">{report.number}</span>
                <span onClick={() => handleConfigure(report)} className="cursor-pointer">
                  SQL
                </span>
              </Text>
              <Space>
                <Dropdown
                  overlay={() => paramsMenu(report.id)}
                  trigger={["click"]}
                  placement="bottomRight"
                >
                  <span className="cursor-pointer">P</span>
                </Dropdown>
                <Dropdown
                  overlay={() => dataSourceMenu(report.id)}
                  trigger={["click"]}
                  placement="bottomRight"
                >
                  <LinkOutlined />
                </Dropdown>

                <Dropdown
                  overlay={() => sqlMenu(report.id)}
                  trigger={["click"]}
                  placement="bottomRight"
                >
                  <Tooltip className="cursor-pointer" title={report.sql.type}>
                    {report.sql.type[0].toUpperCase()}
                  </Tooltip>
                </Dropdown>
              </Space>
            </div>
            <div style={{ textAlign: "center", marginTop: "10px" }}>
              <Text ellipsis={true}>
                <Text className="sql-number number">{report.number}</Text>
                <span onClick={() => handleConfigure(report)} className="cursor-pointer">
                  Visualization
                </span>
              </Text>
            </div>
          </div>
        )}
      </Space>
    ));
  };

  return <>{reportData.length > 0 ? renderReports() : <span>Add Reports</span>}</>;
};

export default Report;
