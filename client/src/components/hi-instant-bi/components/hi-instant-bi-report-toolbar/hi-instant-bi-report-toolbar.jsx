import { Menu } from "antd";
import { useDispatch } from "react-redux";
import "./hi-instant-bi-report-toolbar.scss";
import { handleInstantBIToolbarMenuClick } from "./hi-instant-bi-toolbar-options";
import { getInstantBIToolbarOptions } from "./hi-instant-bi-menu-click";
import HIInstantBIEditButtonModal from "./hi-instant-bi-edit-button-modal";
import { useState } from "react";

const HIInstantBIReportToolbar = () => {
  const dispatch = useDispatch();
  const [editModalVisible, setEditModalVisible] = useState(false);

  const editModalProps = {
    editModalVisible,
    setEditModalVisible,
  };

  const instantBIReportToolbar = (
    <>
      <Menu
        mode="vertical"
        items={getInstantBIToolbarOptions()}
        selectable={false}
        className="hi-instant-bi-report-toolbar"
        onClick={({ key }) => {
          handleInstantBIToolbarMenuClick({
            key,
            dispatch,
            setEditModalVisible,
            editModalVisible,
          });
        }}
      />
      <HIInstantBIEditButtonModal {...editModalProps} />
    </>
  );
  return instantBIReportToolbar;
};

export default HIInstantBIReportToolbar;
