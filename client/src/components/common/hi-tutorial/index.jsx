import { useSelector, useDispatch } from "react-redux";
import React from "react";
import { Popconfirm } from "antd";
import { CloseOutlined } from "@ant-design/icons";
import { appActions } from "../../../redux/actions/app.actions";
import tutorialItems from "./tutorial-items";
import { Typography, Row, Col } from "antd";
import "./index.scss";

const { Title, Paragraph } = Typography;

const TutorialInfo = (props) => {
  const { elementKey, placement = "bottom", showFbCompact, className = '' } = props;
  const tutorialData = useSelector((state) => state.app.tutorialData || {});
  const showFileBrowser = useSelector((state) => state.fileBrowser.showFileBrowser);
  const activeRoute = useSelector((state) => state.app.activeRoute);
  const { key, description, title, moduleKey } = tutorialData;
  const tutorialIndex = tutorialItems[moduleKey]?.findIndex(
    (item) => item.key === key
  );
  const text = (
    <Row className="hi-tutorial-container">
      <Col span={24}>
        <Row justify="space-between">
          <Col span={20}>
            <Title
              data-testid="hi-tutorial-title"
              className="hi-title"
              level={5}
            >
              {title}
            </Title>
          </Col>
          <Col>
            <CloseOutlined
              onClick={() => {
                dispatch(
                  appActions.changeTutorialData({
                    key,
                    moduleKey,
                    status: "exit",
                  })
                );
              }}
            />
          </Col>
        </Row>
      </Col>
      <Col>
        <Paragraph className="hi-description">{description}</Paragraph>
      </Col>
    </Row>
  );
  const dispatch = useDispatch();
  const confirm = (e) => {
    dispatch(appActions.changeTutorialData({ key, moduleKey, status: "next" }));
    if (activeRoute.includes("metadata")) {
      e.stopPropagation();
    }
  };

  const cancel = (e) => {
    if (
      !tutorialIndex ||
      tutorialIndex === tutorialItems[moduleKey]?.length - 1
    ) {
      dispatch(
        appActions.changeTutorialData({ key, moduleKey, status: "exit" })
      );
    }
    dispatch(appActions.changeTutorialData({ key, moduleKey, status: "back" }));
    if (activeRoute.includes("metadata")) {
      e.stopPropagation();
    }
  };
  if (elementKey !== key) {
    return (
      <div data-testid="hi-tutorial" className="hi-tutorial-info">
        {props.children}
      </div>
    );
  }
  return (
    <div
      onClick={(e) => {
        if (
          !activeRoute.includes("metadata")
          // &&
          // !activeRoute.includes("designer")
        ) {
          e.stopPropagation();
        }
      }}
      data-testid="hi-tutorial"
      className="hi-tutorial-info"
    >
      <Popconfirm
        icon={false}
        title={text}
        overlayClassName={`hi-tutorial ${className}`}
        placement={placement}
        visible={(key === elementKey) && (elementKey === "hi-designer-reports") ? showFbCompact ? true : !showFileBrowser : !showFileBrowser}
        onConfirm={confirm}
        onCancel={cancel}
        okText={
          tutorialItems[moduleKey]?.length === 1
            ? "Exit"
            : tutorialIndex === tutorialItems[moduleKey]?.length - 1
              ? "Exit"
              : "Next"
        }
        showCancel={tutorialItems[moduleKey]?.length > 1}
        cancelText={
          tutorialItems[moduleKey]?.length === 1
            ? null
            : !tutorialIndex
              ? "Exit"
              : "Back"
        }
      >
        {props.children}
      </Popconfirm>
    </div>
  );
};

export default TutorialInfo;
