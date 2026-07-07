import {
  HomeOutlined,
  DatabaseOutlined,
  FileTextOutlined,
  EditFilled,
  TableOutlined,
  LineChartOutlined,
  BarChartOutlined,
  PictureOutlined,
  SaveOutlined,
  SaveFilled,
  ShareAltOutlined,
  EyeOutlined,
  FundProjectionScreenOutlined,
  RedoOutlined,
  UserOutlined,
} from "@ant-design/icons";
import { Menu, Tooltip } from "antd";
import { routesUrl } from "../../app/constants";
import { CustomIcon } from "../common/custom-icons/CustomIcon";
import ShortCutText from "../common/hi-shortcuts/hi-shortcuts";
import HIIcon from "../common/icons/hi-icons";
import { getTooltipTitle } from "../hi-reports/hr-taskbar-items";

const {
  adminHomeUrl,
  dataSourceUrl,
  metadataUrl,
  adhocUrl,
  reportCEUrl,
  helicalReportUrl,
  cannedReportsUrl,
  dashboardDesignerUrl,
  userHomeUrl,
  cubeUrl,
  instantBIUrl,
  agentUrl,
} = routesUrl;

export function routeIcon(route) {
  switch (route) {
    case adminHomeUrl:
      return <HomeOutlined className="hi-navbar-icon" />;
    case userHomeUrl:
      return <HomeOutlined className="hi-navbar-icon" />;
    case dataSourceUrl:
      return <DatabaseOutlined className="hi-navbar-icon" />;
    case metadataUrl:
      return <TableOutlined className="hi-navbar-icon" />;
    case cubeUrl:
      return <CustomIcon name="Cube" />;
    case adhocUrl:
      return <LineChartOutlined className="hi-navbar-icon" />;
    case reportCEUrl:
      return <HIIcon name="hi-report-ce" />;
    case helicalReportUrl:
      return <BarChartOutlined className="hi-navbar-icon" />;
    case cannedReportsUrl:
      return <FileTextOutlined className="hi-navbar-icon" />;
    case dashboardDesignerUrl:
      return <PictureOutlined className="hi-navbar-icon" />;
    case instantBIUrl:
      return <HIIcon className="hi-navbar-icon" name="hi-instant-bi-svg" />;
    case agentUrl:
      return <UserOutlined className="hi-navbar-icon" />;
    case "default":
      return <EditFilled className="hi-navbar-icon" />;
  }
}

export function leftMenuDrop(menu) {
  return (
    <Menu className="user-dropdown">
      {menu.map((item, index) => (
        <Tooltip
          key={item.name + index}
          title={getTooltipTitle(item.tooltip || item.name, item.shortCut)}
          placement="left"
        >
          <Menu.Item
            onClick={item.callBack}
            icon={item.icon}
            className={"user-dropdown-item" + " " + (menu.className || "")}
          >
            <ShortCutText text={item.scText} scLocation={item.scLocation}>
              <span> {item.name} </span>
            </ShortCutText>
          </Menu.Item>
        </Tooltip>
      ))}
    </Menu>
  );
}

// export function MenuDrop(props) {
// 	let { menu } = props;
// 	return (
// 		<Menu>
// 			{menu.map((item, index) => {
// 				return (
// 					<Menu.Item key={index} onClick={(e) => props.callback(item.name)} icon={item.icon}>
// 						{item.name}
// 					</Menu.Item>
// 				);
// 			})}
// 		</Menu>
// 	);
// }

function save() {
  return {
    tooltip: "Save",
    icon: <SaveOutlined />,
    dropdown: [
      { name: "Save", icon: <SaveOutlined /> },
      { name: "Save As", icon: <SaveFilled /> },
    ],
  };
}

function navbarLeftItem(item) {
  if (item === reportCEUrl || item === metadataUrl) {
    return [save(), { tooltip: "Share", icon: <ShareAltOutlined /> }];
  }
  if (item === helicalReportUrl || item === dashboardDesignerUrl) {
    return save();
  }
}
export function taskbarItems(path) {
  if (path.match(adminHomeUrl)) {
    return [];
  }
  if (path.match(dataSourceUrl)) {
    return [];
  }
  if (path.match(metadataUrl)) {
    return navbarLeftItem(metadataUrl);
  }
  if (path.match(adhocUrl)) {
    return [];
  }
  if (path.match(reportCEUrl)) {
    return navbarLeftItem(reportCEUrl);
  }
  // if (path.match(helicalReportUrl)) {
  // 	return [
  // 		{ tooltip: 'Generate', icon: <LineChartOutlined /> },
  // 		{ tooltip: 'Preview', icon: <EyeOutlined /> },
  // 		{ tooltip: 'Presentation Mode', icon: <FundProjectionScreenOutlined /> },
  // 		{ tooltip: 'Refresh Cache', icon: <FieldTimeOutlined /> },
  // 		{
  // 			tooltip: 'Export',
  // 			icon: <ExportOutlined />,
  // 			dropdown: [
  // 				{ name: 'CSV (.csv)', icon: <EditFilled /> },
  // 				{ name: 'Excel (.xls)', icon: <FileExcelFilled /> }
  // 			]
  // 		},
  // 		{
  // 			tooltip: 'Layout',
  // 			icon: <LayoutOutlined />,
  // 			dropdownCheckList: <LayoutOptions />
  // 		},
  // 		navbarLeftItem(helicalReportUrl)
  // 	];
  // }
  // if (path.match(cannedReportsUrl)) {
  // 	return [ { tooltip: 'Home', icon: <HomeOutlined /> } ];
  // }
  if (path.match(dashboardDesignerUrl)) {
    return [
      navbarLeftItem(dashboardDesignerUrl),
      { tooltip: "Preview", icon: <EyeOutlined /> },
      { tooltip: "Refresh", icon: <RedoOutlined /> },
      { tooltip: "Presentation Mode", icon: <FundProjectionScreenOutlined /> },
      { tooltip: "Grid Lines", icon: <TableOutlined /> },
      { tooltip: "Toogle Tool Sheif", icon: <EditFilled /> },
    ];
  }
  return [];
}
