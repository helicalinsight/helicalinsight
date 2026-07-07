import { FieldTimeOutlined, ReloadOutlined, SyncOutlined } from "@ant-design/icons";
import { fetchInstantBIReportAPI } from "./utils/instant-bi-requests";

const getInstantBIOpenTaskbarItems = ({ dispatch, file, reportId, setFileInfo }) => [
  {
    title: "cacheTime",
    key: "cacheTime",
    icon: <FieldTimeOutlined />,
    callback: () => {},
  },
  {
    title: "Refresh",
    icon: <SyncOutlined />,
    dropdown: true,
    menu: [
      {
        title: "Current Report",
        key: "currentReport",
        icon: <ReloadOutlined />,
        callback: () =>
          fetchInstantBIReportAPI({
            dispatch,
            file: { path: file?.path, name: file?.name },
            mode: "open",
            setFileInfo,
            reportId,
          }),
      },
    ],
  },
];

export default getInstantBIOpenTaskbarItems;
