import { Tabs, Modal, Button, Typography, Tooltip } from "antd";
import { useState } from "react";
import { useDispatch } from "react-redux";
import TutorialInfo from "../common/hi-tutorial";
import notify from "../hi-notifications/notify";
import {
  PlusOutlined,
  CloseOutlined,
} from "@ant-design/icons";
import ShortCutText from "../common/hi-shortcuts/hi-shortcuts";

const { TabPane } = Tabs;
const { Text } = Typography;

const HITabs = ({ isHcr, tabData, setTabData, add, remove, className, tabPosition, type, addTabRef, isTooltipReq = true }) => {
  const dispatch = useDispatch();
  const [warningVisible, setWarningVisible] = useState({ visible: false, deleteKey: "" });

  const Notify = notify(dispatch);
  const onChange = (activeKey) => {
    setTabData({ ...tabData, activeKey });
  };

  const onEdit = (targetKey, action) => {
    if (action === "add") {
      if (tabData.panes.length < 4) {
        add();
      } else {
        Notify.warning({
          message: "You have reached the maximum number of tabs.",
          type: "Frontend",
        });
      }
    } else if (action === "remove") {
      setWarningVisible({ visible: true, deleteKey: targetKey });
    }
  };
  const addIcon = (
    <TutorialInfo elementKey={"hi-new-tab"}>
      <span
        role="img"
        aria-label="plus"
        className="anticon anticon-plus"
        onClick={(e) => onEdit({}, "add")}
      >
        <svg
          viewBox="64 64 896 896"
          focusable="false"
          data-icon="plus"
          width="1em"
          height="1em"
          fill="currentColor"
          aria-hidden="true"
        >
          <path d="M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z"></path>
          <path d="M176 474h672q8 0 8 8v60q0 8-8 8H176q-8 0-8-8v-60q0-8 8-8z"></path>
        </svg>
      </span>
    </TutorialInfo>
  );

  if (addTabRef) {
    addTabRef.current = {
      edit: onEdit,
      tabData: tabData,
      onChange: onChange,
    }
  }

  return (
    <TutorialInfo elementKey={"hi-new-tab"}>
      <Tabs
        className={className || ""}
        hideAdd={false}
        tabPosition={tabPosition || "top"}
        onChange={onChange}
        activeKey={tabData.activeKey}
        type={type}
        onEdit={onEdit}
        addIcon={<ShortCutText scLocation="HR" text="A">
          <PlusOutlined />
        </ShortCutText>}
      >
        {tabData.panes.map((pane) => (
          <TabPane
            key={pane.key}
            closeIcon={<ShortCutText scLocation="HR" text="D">
              <CloseOutlined />
            </ShortCutText>}
            tab={isTooltipReq ? (<Tooltip title={pane.title}>
              <span> {pane.title}</span>
            </Tooltip>
            ) : <span> {pane.title}</span>}
          />
        ))}
      </Tabs>
      <Modal
        visible={warningVisible.visible}
        okText="Leave"
        cancelText="Cancel"
        onOk={() => {
          remove(warningVisible.deleteKey);
          setWarningVisible({ visible: false, deleteKey: "" });
        }}
        onCancel={() => setWarningVisible({ visible: false, deleteKey: "" })}
      >
        <Text>Are you sure you want to leave?</Text>
      </Modal>
    </TutorialInfo>
  );
};

export { HITabs };

// closeIcon={
//     <TutorialInfo elementKey={"hi-new-tab"} >
//         <span role="img" aria-label="close" className="anticon anticon-close"
//             onClick={e=> onEdit(pane.key,"remove") } >
//             <svg viewBox="64 64 896 896" focusable="false" data-icon="close" width="1em"
//                 height="1em" fill="currentColor" aria-hidden="true">
//                     <path d="M563.8 512l262.5-312.9c4.4-5.2.7-13.1-6.1-13.1h-79.8c-4.7 0-9.2 2.1-12.3 5.7L511.6 449.8 295.1 191.7c-3-3.6-7.5-5.7-12.3-5.7H203c-6.8 0-10.5 7.9-6.1 13.1L459.4 512 196.9 824.9A7.95 7.95 0 00203 838h79.8c4.7 0 9.2-2.1 12.3-5.7l216.5-258.1 216.5 258.1c3 3.6 7.5 5.7 12.3 5.7h79.8c6.8 0 10.5-7.9 6.1-13.1L563.8 512z">
//             </path></svg></span>
//     </TutorialInfo>
// }
