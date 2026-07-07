import { useEffect, useState } from "react";
import { Menu, Card, Row, Col } from "antd";
import { NavLink, useRouteMatch, Route, Switch } from "react-router-dom";
import { useLocation } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { appActions } from "../../../../redux/actions";
import {
  updateManagementData,
  updateManagementStaticData,
  updateManagementAdvancedData,
} from "../../../../redux/actions/admin.actions";
import requests from "../../../../base/requests";
import Middleware from "./Components/Middleware";
import ImportFiles from "./Components/ImportFiles";
import Dice from "./Components/Dice";
// import { Cube } from "./Components/cube/Cube";
import "./hi-management.scss";
import { DiceStorage } from "./Components/dice-storage/DiceStorage";
import ExportRepository from "./Components/ExportRepository";

const menuItems = [
  { label: "Middleware", key: "middleware", module: "middleware" },
  { label: "DICE", key: "dice", module: "dice" },
  { label: "DICE Storage", key: "diceStorage", module: "dice" },
  { label: "Import", key: "import", module: "import" },
  { label: "Export", key: "export", module: "export" }
  // { label: "Cube", key: "cube" },
];

const HIManagement = ({ apiRef, handleAbort }) => {
  const managementData = useSelector((store) => store.admin.managementData);
  const managementStaticData = useSelector((store) => store.admin.managementStaticData);
  const managementAdvancedData = useSelector((store) => store.admin.managementAdvancedData);
  const applicationSettings = useSelector((store) => store.app.applicationSettingsData) || {};
  const { settings = {}, showExperimentalFeatures } = applicationSettings || {};
  const { experimentalModules = [] } = settings || {};
  const [activeKey, setActiveKey] = useState("middleware");

  const dispatch = useDispatch();

  useEffect(() => {
    if (process.env.NODE_ENV === "test") {
      return null;
    } else {
      getManagementTabDetails();
      getManagementStaticContent();
      if (checkIfModuleExperimental("dice")) {
        getManagementAdvancedData();
      }
    }
  }, []);

  const uri = "core/dataSource/drillConfig";
  const staticDataUri = "content/static/getContents";
  const advancedDataUri = "monitor/system/management";

  const getManagementTabDetails = () => {
    !managementData &&
      requests.admin(dispatch).postManagementData(
        {},
        uri,
        (res) => {
          const data = res.drill || res;
          dispatch(updateManagementData(data));
        },
        (e) => {
          console.log(e);
        }
      );
  };

  const getManagementStaticContent = () => {
    !managementStaticData &&
      requests.admin(dispatch).postManagementData(
        { contentId: "Static/managementContent" },
        staticDataUri,
        (res) => {
          dispatch(updateManagementStaticData(res));
        },
        () => {
          // console.log(e);
        }
      );
  };

  const getManagementAdvancedData = (refresh = false) => {
    (!managementAdvancedData || refresh) &&
      requests.admin(dispatch).postManagementData(
        { command: "GET_INFO" },
        advancedDataUri,
        (res) => {
          dispatch(updateManagementAdvancedData(res));
        },
        () => {
          // console.log(e);
        }
      );
  };

  function handleMenuClick(e) {
    setActiveKey(e.key);
  }

  function checkIfModuleExperimental(module) {
    if (experimentalModules.includes(module)) return showExperimentalFeatures;
    return true;
  }

  return (
    <Card hoverable className="hi-management-card">
      <Row className="hi-management-container">
        <Col span={24} className="hi-management-routes">
          <Menu
            data-testid="management-menu"
            selectedKeys={[activeKey]}
            onClick={handleMenuClick}
            mode="horizontal"
            className="management-menu"
            // items={menuItems}
            items={menuItems.filter((item) => checkIfModuleExperimental(item.module))}
          />
        </Col>
        <Col span={24}>
          {activeKey === "middleware" && <Middleware />}
          {activeKey === "dice" && <Dice getManagementAdvancedData={getManagementAdvancedData} />}
          {activeKey === "diceStorage" && <DiceStorage handleAbort={handleAbort} apiRef={apiRef} />}
          {activeKey === "import" && <ImportFiles />}
          {activeKey === "export" && <ExportRepository />}
          {/* {activeKey === "cube" && <Cube />} */}
        </Col>
      </Row>
    </Card>
  );
};

export { HIManagement };
