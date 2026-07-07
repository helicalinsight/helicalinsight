import { useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Row, Col, Menu, Switch as Toggler, Skeleton } from "antd";
import { updateManagementDiceApiStatus } from "../../../../../redux/actions/admin.actions";
import requests from "../../../../../base/requests";
import Advanced from "./Advanced";
// import Configuration from "./Configuration";
import Overview from "./Overview";
import NoSqlFile from "./NoSqlFile";

const Dice = (props) => {
  const { getManagementAdvancedData } = props;
  const staticData = useSelector((store) => store.admin.managementStaticData);
  const isDiceApiRunning = useSelector((store) => store.admin.isDiceApiRunning);
  const managementAdvancedData = useSelector((store) => store.admin.managementAdvancedData);
  const [isDiceClicked, setIsDiceClicked] = useState(false);
  const [activeTab, setActiveTab] = useState("overview");
  const dispatch = useDispatch();

  const handleMenuClick = (e) => {
    setActiveTab(e.key);
  };

  const diceUri = "monitor/system/management";

  const handleDiceClick = () => {
    const formData = managementAdvancedData.computation
      ? { command: "STOP_COMPUTATION" }
      : { command: "START_COMPUTATION" };

    setIsDiceClicked(true);
    dispatch(updateManagementDiceApiStatus(true))

    requests.admin(dispatch).postManagementData(
      formData,
      diceUri,
      (res) => {
        setIsDiceClicked(false);
        getManagementAdvancedData({ refresh: true });
        dispatch(updateManagementDiceApiStatus(false))
      },
      (e) => {
        setIsDiceClicked(false);
        dispatch(updateManagementDiceApiStatus(false))
      }
    );
  };

  const renderSettings = () => {
    return (
      <Row>
        <Col data-testid = "hi-dice-Disable" xs={24} md={6} lg={4}>
          Disable
        </Col>
        <Col xs={10} md={6} lg={4}>
          <Toggler
            loading={isDiceApiRunning}
            disabled={isDiceClicked}
            checked={managementAdvancedData?.computation}
            onClick={handleDiceClick}
          />
        </Col>
        <Col data-testid = "hi-dice-Enable" xs={24} md={6} lg={4}>
          Enable
        </Col>
        <Col xs={24} md={6} lg={9}>
          {isDiceApiRunning ? "DICE Loading..." : managementAdvancedData?.computation ? "DICE Connected" : "DICE Disconnected"}
        </Col>
      </Row>
    );
  };

  return (
    <>
      <Row justify="space-between" align="middle" className="dice-menu-container">
        <Col span={8} className="switch-container">
          {managementAdvancedData ? renderSettings() : <Skeleton />}
        </Col>
        <Col sm={10} md={8}>
          <Menu 
          data-testid = "hi-dice-menu"
            selectedKeys={[activeTab]}
            mode="horizontal"
            className="management-menu"
            onClick={handleMenuClick}
          >
            <Menu.Item key="overview">
              Overview
            </Menu.Item>
            <Menu.Item key="configuration">
              Configuration
            </Menu.Item>
            <Menu.Item key="advanced">
              Advanced
            </Menu.Item>
          </Menu>
        </Col>
      </Row>
      <Col span={24}>
        <Row justify="space-between" className="dice-routes-data-container">
          {staticData && <Col span={24}>{staticData.tutorialheading}</Col>}
          {staticData && (
            <Col sm={24} md={11}>
              <ul>
                {staticData.panelItems?.map((eachItem) => (
                  <li key={eachItem}>{eachItem}</li>
                ))}
              </ul>
            </Col>
          )}
          <Col sm={24} md={12}>
            {activeTab === "overview" && <Overview />}
            {activeTab === "configuration" && <NoSqlFile />}
            {activeTab === "advanced" && <Advanced getManagementAdvancedData={getManagementAdvancedData} />}
          </Col>
        </Row>
      </Col>
    </>
  );
};

export default Dice;
