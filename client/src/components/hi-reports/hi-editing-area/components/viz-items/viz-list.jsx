import {
  AreaChartOutlined,
  BarChartOutlined,
  CreditCardOutlined,
  LineChartOutlined,
  TableOutlined,
} from "@ant-design/icons";
import { Col, Input, Row, Switch, Tooltip, Typography } from "antd";
import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setShowAllVizualizations, updateCustomChart, updateSubVizType } from "../../../../../redux/actions/hreport.actions";
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
import ChartIcon from "../../../../common/icons/chart-icons";
import notify from "../../../../hi-notifications/notify";
import CustomChartEditor from "../../../hi-viz-area/custom-charts/custom-chart-editor";
import { generateReport } from "../../../utils/base";
import { vizList } from "../../utils/constants";
import { subVizList } from "../values/sub-viz-list";

const { Text } = Typography;
const iconMap = {
  Table: <TableOutlined />,
  SyncChart: <LineChartOutlined />,
  S2Chart: <ChartIcon name="s2-table" />,
  GridChart: <BarChartOutlined />,
  Antcharts: <AreaChartOutlined />,
  MapChart: <ChartIcon name="map-chart" />,
  VF: <ChartIcon name="vf" />,
  Card: <CreditCardOutlined />
};
const VizList = ({ vizRef, getApi }) => {
  const dispatch = useDispatch();
  const [, setHovered] = useState("");
  const [vizSearch, setVizSearch] = useState("");
  const activeReport = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find((report) => report.active);
    return activeReport;
  });
  const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
  const Notify = notify(dispatch);
  const handleClick = (type) => {
    const customChartOptions = { selected: false, drawer: false, applied: false }
    if (type === "VF") {
      dispatch(updateCustomChart({ selected: true, drawer: true }))
      return;
    }
    const activeSubVizType = activeReport?.marksList?.find((item) => item?.value === "_all_")?.subVizType
    const avalableSubVizList = subVizList[type]?.map((item) => item?.name);
    const allowedTypes = ["Table", "S2Chart"]
    if (avalableSubVizList?.includes(activeSubVizType) || allowedTypes.includes(type) || !activeSubVizType) {
      generateReport(
        { ...activeReport, selectedType: type, user, customChart: { ...activeReport?.customChart, ...customChartOptions } },
        dispatch,
        getApi
      );
    } else {
      dispatch(updateSubVizType({ value: "_all_", name: "bar" }))
      Notify.error({
        type: "Frontend",
        message: `selected subviz type is not applicable for ${type}`,
      });
      let modifiedMarksList = activeReport?.marksList?.map((item) => {
        return {
          ...item,
          subVizType: 'bar'
        }
      })
      generateReport(
        { ...activeReport, marksList: modifiedMarksList, selectedType: type, user, customChart: { ...activeReport?.customChart, ...customChartOptions } },
        dispatch,
        getApi
      );
    }
    dispatch(updateCustomChart(customChartOptions))
  };
  const handleMouseEnter = (type) => {
    setHovered(type);
  };
  const handleMouseLeave = () => {
    setHovered("");
  };
  const { selectedType, customChart: { selected: isCustomChartSelected, code: customCode, drawer, applied }, showAllVisualizations } = activeReport || {};
  vizRef.current = handleClick

  const checkCustomChartSelected = (type) => {
    return type === "VF" && isCustomChartSelected && applied
  }

  const getVizColor = (type, selectedType) => {
    if (checkCustomChartSelected(type)) {
      return "#337ab7"
    }
    if (type === selectedType && isCustomChartSelected && applied) {
      return "#000"
    }
    if (type === selectedType) {
      return "#337ab7"
    }
    return "#000"
  }

  const handleVizUIChange = (value) => {
    dispatch(setShowAllVizualizations(value))
  }


  return (
    <div className="hr-viz-list-area">
      <div>
        <Input
          placeholder="Search.."
          allowClear={true}
          value={vizSearch}
          onChange={(e) => setVizSearch(e.target.value)}
        />
      </div>

      <div className="hr-editing-area-viz-switch" data-testid={"hr-editing-area-viz-switch"}>
        <span>Show All Visualizations: </span>
        <Switch
          size={"small"}
          checked={showAllVisualizations}
          onChange={handleVizUIChange}
        />
      </div>

      <div className="hr-viz-list">
        <Row>
          {vizList
            .filter((viz) => {
              return viz.displayName
                .toLowerCase()
                .includes(vizSearch.toLowerCase());
            })
            .map((viz, i) => {
              return (
                <Col key={i} span={8}>
                  <Tooltip title={viz.tooltip} placement="topLeft">
                    <div
                      className={viz.disabled ? "hi-viz-item hi-viz-item-disabled" : "hi-viz-item"}
                      onMouseLeave={() => handleMouseLeave(viz.displayName)}
                      onMouseEnter={() => handleMouseEnter(viz.displayName)}
                      style={{
                        // color: (viz.type === selectedType || (checkCustomChartSelected(viz.type))) ? "#337ab7" : "#000",
                        color: getVizColor(viz.type, selectedType)
                      }}
                      onClick={() => {
                        !viz.disabled && handleClick(viz.type)
                      }}
                      data-testid={`viz-type-${viz.type}`}
                    >
                      <ShortCutText scLocation={viz?.scLocation} text={viz.scDisplay}>
                        {iconMap[viz.type]}
                        <div>
                          <Text ellipsis={true} > {viz.displayName} </Text>
                        </div>
                      </ShortCutText>
                    </div>
                  </Tooltip>
                </Col>
              );
            })}
        </Row>
      </div>
      {isCustomChartSelected && <CustomChartEditor open={drawer} code={customCode} report={activeReport} />}
      {/* <div className="viz-description" > #bug 4652 fix 
                <div>
                    <Title ellipsis={true} level={5}>{hovered ? hovered : selectedType} </Title>
                </div>
                <div>
                    <Text ellipsis={true} >
                        visualisation description
                    </Text>
                </div>
                <div>
                    <Title ellipsis={true} level={5}>Data Requirements</Title>
                </div>
                <div>
                    <Text ellipsis={true} >
                        Rows : 1 or more
                    </Text>
                </div>
                <div>
                    <Text ellipsis={true} >
                        Columns : 1 or more
                    </Text>
                </div>
            </div> */}
    </div>
  );
};

export default VizList;
