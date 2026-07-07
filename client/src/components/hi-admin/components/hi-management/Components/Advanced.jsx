import { useSelector } from "react-redux";
import { Row, Col, Card, Switch as Toggler } from "antd";
import { useDispatch } from "react-redux";
import { modifyAdvancedData } from "../../../../../redux/actions/admin.actions";
import requests from "../../../../../base/requests";
import notify from "../../../../hi-notifications/notify";

const advancedDiceUri = "monitor/system/management";

const Advanced = () => {
  const advancedData = useSelector((store) => store.admin.managementAdvancedData);
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const onClickMaster = () => {
    const formData = advancedData?.master
      ? { command: "STOP_MASTER" }
      : { command: "START_MASTER" };

    requests.admin(dispatch).postManagementData(
      formData,
      advancedDiceUri,
      (res) => {
        dispatch(modifyAdvancedData(["master", !advancedData?.master]));
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
  };

  const onClickWorker = () => {
    const formData = advancedData?.worker
      ? { command: "STOP_WORKER" }
      : { command: "START_WORKER" };

    requests.admin(dispatch).postManagementData(
      formData,
      advancedDiceUri,
      (res) => {
        dispatch(modifyAdvancedData(["worker", !advancedData?.worker]));
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
  };

  const onClickSpark = () => {
    const formData = advancedData?.spark ? { command: "STOP_SPARK" } : { command: "START_SPARK" };

    requests.admin(dispatch).postManagementData(
      formData,
      advancedDiceUri,
      (res) => {
        dispatch(modifyAdvancedData(["spark", !advancedData?.spark]));
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
  };

  return (
    <Card data-testid = "hi-advanced-card" hoverable>
      <Row className="advanced-container">
        <Col span={24}>
          <Row>
            <Col data-testid ="hi-advanced-text-one" span={8}>DICE Master</Col>
            <Toggler size="small" checked={advancedData?.master} onClick={onClickMaster} />
          </Row>
        </Col>
        <Col span={24}>
          <Row>
            <Col data-testid ="hi-advanced-text-two" span={8}>DICE Worker</Col>
            <Toggler size="small" checked={advancedData?.worker} onClick={onClickWorker} />
          </Row>
        </Col>
        <Col span={24}>
          <Row>
            <Col data-testid ="hi-advanced-text-three" span={8}>DICE Application</Col>
            <Toggler size="small" checked={advancedData?.spark} onClick={onClickSpark} />
          </Row>
        </Col>
      </Row>
    </Card>
  );
};

export default Advanced;
