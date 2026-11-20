import { FieldTimeOutlined, ReloadOutlined, SyncOutlined } from "@ant-design/icons";
import { fetchInstantBIReportAPI } from "./utils/instant-bi-requests";

const getInstantBIOpenTaskbarItems = ({ dispatch, file, reportId, setFileInfo, onCacheRefresh = () => { } }) => [
  {
    title: "cacheTime",
    key: "cacheTime",
    icon: <FieldTimeOutlined />,
    callback: () => { },
  },
  {
    title: "Refresh",
    icon: <SyncOutlined />,
    dropdown: true,
    menu: [
      {
        title: "Cache",
        key: "cache",
        icon: <SyncOutlined />,
        callback: () => {
          onCacheRefresh()
        },
      },
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
