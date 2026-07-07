import { Col, Collapse, Input, Row, Switch, Tooltip } from "antd";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setShowAllVizualizations, updateCardProperties, updateCustomChart, updateSubVizType } from "../../../../../redux/actions/hreport.actions";
import { toCapitalize } from "../../../../../utils/text-utils";
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
import CustomChartEditor from "../../../hi-viz-area/custom-charts/custom-chart-editor";
import { generateReport } from "../../../utils/base";
import { vizListNew } from "../../utils/constants";
import { checkIsSubVizApplicable } from "../../utils/marks-utils";
const { Panel } = Collapse;

const VizListNew = ({ getApi, vizRef }) => {
  const dispatch = useDispatch();
  const [vizSearch, setVizSearch] = useState("");
  const [localSelectedType, setLocalSelectedType] = useState("")
  const activeReport = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find((report) => report.active);
    return activeReport;
  });
  let subVizType = "";
  let allMark = {};
  if (activeReport) {
    allMark = activeReport.marksList?.find((item) => item?.value === "_all_");
    subVizType = allMark?.subVizType
  }
  const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
  const customChartOptions = { selected: false, drawer: false, applied: false }
  const { selectedType, customChart: { selected: isCustomChartSelected, code: customCode, drawer, applied }, showAllVisualizations } = activeReport || {};

  const handleChangeSubViz = (name, type) => {
    if (type === "VF") {
      dispatch(updateCustomChart({ selected: true, drawer: true }))
      return;
    }
    if (type === "Card" && name === "table") {
      dispatch(updateCardProperties({
        properties: {
          isTrend: true,
          displayTrend: ['trend', 'value'],
          trendPrefix: 'vs.',
        }
      }))
    }
    dispatch(updateSubVizType({ value: '_all_', name, id: allMark?.id }))
    setLocalSelectedType(type)
    let modifiedMarksList = activeReport?.marksList?.map((item) => {
      return {
        ...item,
        subVizType: name
      }
    })
    generateReport(
      { ...activeReport, marksList: modifiedMarksList, selectedType: type, user, customChart: { ...activeReport?.customChart, ...customChartOptions } },
      dispatch,
      getApi
    );
    dispatch(updateCustomChart(customChartOptions))
  }

  const handleClick = (type) => {
    switch (type) {
      case 'Table':
        handleChangeSubViz('Table', 'Table')
        break;
      case 'S2Chart':
        handleChangeSubViz('S2Chart', 'S2Chart')
        break;
      case 'VF':
        handleChangeSubViz('VF', 'VF')
        break;
      case 'GridChart':
        handleChangeSubViz('bar', 'GridChart')
        break;
      case 'Antcharts':
        handleChangeSubViz('bar', 'Antcharts')
        break;
      case 'MapChart':
        handleChangeSubViz('point', 'MapChart')
        break;
      case 'Card':
        handleChangeSubViz('kpi', 'Card')
        break;
      default:
        break;
    }
  }

  vizRef.current = handleClick


  const checkIsActiveSubViz = (type, selectedType, name) => {
    if (type === selectedType && ['Table', 'S2Chart', 'VF'].includes(selectedType)) {
      return true;
    }
    return (type === selectedType && subVizType === name)
  }

  useEffect(() => {
    setLocalSelectedType(selectedType)
  }, [selectedType])

  const renderChildren = (data = [], type, selectedType, parentName) => {
    return data
      .filter((item) => {
        return parentName.toLowerCase().includes(vizSearch.toLowerCase()) || item.name.toLowerCase().includes(vizSearch.toLowerCase())
      })
      .map((item, i, _arr) => {
        let disabled = checkIsSubVizApplicable(type, item.name, activeReport.fields)
        let cName = "hi-viz-item-children" + ((checkIsActiveSubViz(type, selectedType, item.name) && !!disabled) ? " hi-viz-item-children-active" : "") + (!disabled ? " hi-viz-item-children-disabled" : "")
        const { capitalize = true } = item || {}
        const title = item.title ? !capitalize ? item.title : toCapitalize(item.title) : toCapitalize(item.name)
        const isSingle = _arr.length === 1;
        return (
          <Tooltip title={item.tooltip}>
            <div
              key={i}
              data-testid={`hi-item-child-${item.key}`}
              className={cName}
              onClick={() => {
                if (!disabled) return;
                handleChangeSubViz(item.name, type)
              }}
            >
              <span>{item.icon}</span>
              <span className={"hi-viz-item-children-title" + (!isSingle ? " ellipsis" : "")}>{title}</span>
            </div>
          </Tooltip>
        )
      })
  }

  const getHeaderNode = (viz, selectedType) => {
    const { displayName, type, tooltip } = viz
    let style = {}
    if (type === selectedType) {
      style.color = "#2691e9"
    }
    return (
      <Tooltip title={tooltip}>
        <span style={style} data-testid={`hi-header-item-${viz.key}`}>
          <ShortCutText scLocation={viz?.scLocation} text={viz.scDisplay}>
            {displayName}
          </ShortCutText>
        </span>
      </Tooltip>
    )
  }

  const handleVizUIChange = (value) => {
    dispatch(setShowAllVizualizations(value))
  }


  return (
    <div className="hr-viz-list-area-new" data-testid="hi-viz-list-area-new">
      <div>
        <Input
          placeholder="Search.."
          allowClear={true}
          value={vizSearch}
          onChange={(e) => setVizSearch(e.target.value)}
        />
      </div>

      <div className="hr-editing-area-viz-switch" data-testid="hr-editing-area-viz-switch">
        <span>Show All Visualizations: </span>
        <Switch
          size={"small"}
          checked={showAllVisualizations}
          onChange={handleVizUIChange}
        />
      </div>

      <div className="hr-viz-list">
        <Row>
          {vizListNew
            .filter((viz) => {
              let isItemPresent = viz.displayName.toLowerCase().includes(vizSearch.toLowerCase())
              let checkIsPresentInChildren = viz?.children?.find((item) => {
                return item.name.toLowerCase().includes(vizSearch.toLowerCase())
              })
              return isItemPresent || checkIsPresentInChildren
            })
            .map((viz, i) => {
              return (
                <Col key={i} span={24}>
                  <Collapse
                    expandIcon={null}
                    key={viz.key}
                    defaultActiveKey={['Grid Chart', 'Chart', 'Maps', 'Table', 'Grid Table', 'VF', 'Card']}
                  >
                    <Panel key={viz.displayName} header={getHeaderNode(viz, localSelectedType)} className="hi-viz-item-panel">
                      <div className="hi-viz-item-children-container">
                        {renderChildren(viz.children, viz.type, localSelectedType, viz.displayName)}
                      </div>
                    </Panel>
                  </Collapse>
                </Col>
              );
            })}
        </Row>
      </div>
      {isCustomChartSelected && <CustomChartEditor open={drawer} code={customCode} report={activeReport} />}
    </div >
  );
};

export default VizListNew;
