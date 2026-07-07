import { Tooltip } from "antd";
import { EditOutlined } from "@ant-design/icons";

export const getInstantBIToolbarOptions = () => {
  return [
    {
      key: "edit",
      tooltip: "Edit the report",
      icon: (
        <Tooltip title="Go to drag and drop mode" placement="right">
          <EditOutlined
          //   className={
          //     gridItemlayoutObj?.static ? "hi-icon hi-selected" : "hi-icon"
          //   }
          />
        </Tooltip>
      ),
    },
  ];
};
