import {
	FolderAddOutlined,
	RetweetOutlined,
	DeleteOutlined,
	UploadOutlined,
	SnippetsOutlined,
	DownloadOutlined,
	SelectOutlined,
	ArrowRightOutlined,
	FileTextOutlined,
	EditFilled,
	TableOutlined,
	BarChartOutlined,
	PictureOutlined,
	EditOutlined,
	SaveOutlined,
	ShareAltOutlined,
	FileOutlined,
	CloudDownloadOutlined,
	CopyOutlined,
	ScissorOutlined,
	ImportOutlined,
	FileImageOutlined,
} from '@ant-design/icons';
import HIIcon from '../../components/common/icons/hi-icons';
import { routesUrl, roleTypes } from '../../app/constants';
import { CustomIcon } from '../common/custom-icons/CustomIcon';
const { metadataUrl, helicalReportUrl, dashboardDesignerUrl, cubeUrl, instantBIUrl, cannedReportsUrl, agentUrl } = routesUrl;
const { roleAdmin, roleUser, roleViewer, roledownload } = roleTypes;

export const AGENT_INTERACT_ACTION = 'interact';

export const modeTypes = {
	NEW_WINDOW_MODE: 'newWindow',
	MAIN_WINDOW_MODE: 'open'
};

const filterByOptions = [
	{
		value: 'efw',
		label: 'Report',
		icon: <FileOutlined />
	},
	{
		value: 'efwsr',
		label: 'Saved Report',
		icon: <SaveOutlined />
	},
	// {
	//   value: "report",
	//   label: "Adhoc Report",
	//   icon: "line-chart",
	// },
	{
		value: 'hr',
		label: 'Helical Report',
		icon: <BarChartOutlined />
	},
	{
		value: 'efwce',
		label: 'Community Report',
		icon: <EditFilled />
	},
	{
		value: 'hcr',
		label: 'Canned Report',
		icon: <FileTextOutlined />
	},
	{
		value: 'efwdd',
		label: 'Dashboard Designer',
		icon: <PictureOutlined />
	},
	{
		value: 'metadata',
		label: 'Metadata',
		icon: <TableOutlined />
	},
	{
		value: 'efwresult',
		label: 'Result',
		icon: <CloudDownloadOutlined />
	},
	{
		value: 'cube',
		label: 'Cube',
		icon: <CustomIcon name="Cube" />
	},
	{
		value: 'instant',
		label: 'Instant',
		icon: <HIIcon name="hi-instant-bi-svg" />
	},
	{
		value: 'image',
		label: 'Image',
		icon: <FileImageOutlined />
	},
	{
		value: 'model',
		label: 'Semantic Model',
		icon: <CustomIcon name="Cube" />
	}
];

const defaultUnknownExtensionIcon = <FileOutlined />;
const getFilterOptionsForExtensions = (fileExtensions, extensionOptions = null) => {
	let extensions;
	if (fileExtensions?.length) {
		extensions = [ ...fileExtensions ];
	} else if (extensionOptions?.length) {
		extensions = [ ...extensionOptions ];
	} else {
		extensions = filterByOptions.map((op) => op.value);
	}
	if (extensionOptions?.length) {
		extensions = extensions.filter((ext) => extensionOptions.includes(ext));
	}
	return extensions.map((ext) => {
		const knownOption = filterByOptions.find((op) => op.value === ext);
		if (knownOption) return knownOption;
		return {
			value: ext,
			label: 'Unknown',
			icon: defaultUnknownExtensionIcon,
			isUnknown: true
		};
	});
};

const getExtensionIcon = (extension) => {
	const knownOption = filterByOptions.find((op) => op.value === extension);
	return knownOption?.icon || defaultUnknownExtensionIcon;
};

const groupByOptions = [
	{
		value: 'none',
		label: 'Default'
	},
	{
		value: 'extension',
		label: 'File Type'
	}
];

const allContextMenuOptions = [
	{
		id: 'chr',
		name: 'Create Report',
		types: [ 'file' ],
		extensions: [ 'metadata', 'cube' ],
		icon: <EditOutlined />,
		disabled: false,
		permission: [ 2, 3, 4, 5 ],
		userRoles: {
			[roleAdmin]: [ 'metadata', 'cube' ],
			[roleUser]: [ 'metadata', 'cube' ],
			[roledownload]: [ 'metadata', 'cube' ]
		}
	},
	{
		id: 'opn',
		name: 'Open',
		types: [ 'file' ], // mandatory
		icon: <ArrowRightOutlined />,
		extensions: [ 'hr', 'efwdd', 'instant', 'hcr', 'image' ], // optional
		disabled: false,
		permission: [ 2, 3, 4, 5 ],
		userRoles: {
			[roleUser]: [ 'hr', 'efwdd', 'hcr', 'image', 'instant' ],
			[roleAdmin]: [ 'hr', 'efwdd', 'instant', 'hcr', 'image' ]
		}
	},
	{
		id: 'onw',
		name: 'Open in new window',
		types: [ 'file' ],
		icon: <SelectOutlined rotate={90} />,
		extensions: [ 'hr', 'efwdd', 'instant', 'hcr', 'image' ],
		disabled: false,
		permission: [ 2, 3, 4, 5 ],
		userRoles: {
			[roleUser]: [ 'hr', 'efwdd', 'hcr', 'image', 'instant' ],
			[roleAdmin]: [ 'hr', 'efwdd', 'instant', 'hcr', 'image' ]
		}
	},
	{
		id: 'nwf',
		name: 'New Folder',
		types: [ 'folder' ],
		//addFor: ["General"],
		//page: 'adhoc'
		icon: <FolderAddOutlined />,
		disabled: false,
		permission: [ 3, 4, 5 ]
	},
	{
		id: 'edt',
		name: 'Edit',
		types: [ 'file' ],
		icon: <HIIcon name="hi-edit-box" />,
		extensions: [ 'metadata', 'hr', 'efwdd', 'cube', 'instant', 'hcr', 'image', 'model' ],
		disabled: false,
		permission: [ 3, 4, 5 ],
		userRoles: {
			[roleUser]: [ 'hr', 'efwdd', 'hcr', 'image', 'instant', ], // added efwdd dashboard designer edit for user as well  bug id : 6652
			[roleAdmin]: [ 'metadata', 'hr', 'efwdd', 'cube', 'instant', 'hcr', 'image', 'model' ]
		}
	},
	{
		id: 'rnm',
		name: 'Rename',
		types: [ 'folder', 'file' ],
		icon: <RetweetOutlined />,
		disabled: false,
		permission: [ 3, 4, 5 ]
	},
	// {
	// 	id: 'umd',
	// 	name: 'Use this metadata',
	// 	types: [ 'file' ],
	// 	extensions: [ 'metadata' ],
	// 	icon: <EditOutlined />,
	// 	disabled: true,
	// 	permission: [ 1, 3, 4, 5 ]
	// },
	{
		id: 'dlt',
		name: 'Delete',
		types: [ 'folder', 'file' ],
		icon: <DeleteOutlined />,
		disabled: false,
		permission: [ 4, 5 ]
	},
	{
		id: 'imp',
		name: 'Import',
		types: [ 'folder' ],
		// extensions: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		icon: <ImportOutlined />,
		disabled: false,
		permission: [ 3, 4, 5 ],
		userRoles: [ roleAdmin, roleUser, roledownload, roleViewer ]
		// userRoles: {
		// 	[roleAdmin]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roleUser]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roleViewer]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roledownload]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ]
		// }
	},
	{
		id: 'exp',
		name: 'Export',
		types: [ 'folder', 'file' ],
		icon: <DownloadOutlined />,
		disabled: false,
		permission: [ 3, 4, 5 ],
		userRoles: [ roleAdmin ]
	},
	{
		id: 'cpy',
		name: 'Copy',
		types: [ 'folder', 'file' ],
		icon: <CopyOutlined />,
		disabled: false,
		permission: [ 2, 3, 4, 5 ],
	},
	{
		id: 'cut',
		name: 'Cut',
		types: [ 'folder', 'file' ],
		icon: <ScissorOutlined />,
		disabled: false,
		permission: [ 4, 5 ],
	},
	{
		id: 'pst',
		name: 'Paste',
		types: [ 'folder' ],
		// extensions: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		icon: <SnippetsOutlined />,
		disabled: true,
		permission: [ 3, 4, 5 ],
		// userRoles: {
		// 	[roleAdmin]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roleUser]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roleViewer]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ],
		// 	[roledownload]: [ 'efw', 'efwsr', 'report', 'hr', 'efwce', 'hcr', 'efwdd', 'efwresult' ]
		// }
	},
	{
		id: 'shr',
		name: 'Share',
		types: [ 'folder', 'file' ],
		icon: <ShareAltOutlined />,
		disabled: false,
		permission: [ 5 ],
		userRoles: [ roleAdmin, roleUser ]
	},
	{
		id: 'shp',
		name: 'Show Properties',
		types: [ 'folder', 'file' ],
		icon: <SnippetsOutlined />,
		disabled: false,
		permission: [ 1, 2, 3, 4, 5 ]
	}
];

const tableColumnsConfig = [
	{ label: 'Last Modified', value: 'lastModified' },
	{ label: 'Path', value: 'path' },
	{ label: 'Extension', value: 'extension' },
	{ label: 'Permission Level', value: 'permissionLevel' }
];

const filtersForRoutes = [
	// {
	//   path: "/adhoc",
	//   filters: ["metadata", "hr"],
	// },
	{
		path: '/datasource',
		filters: [ 'report', 'hr', 'metadata' ]
	},
	{
		path: '/helical-reports',
		filters: [ 'metadata' ]
	},
	{
		path: '/report-ce',
		filters: [ 'efwce' ]
	},
	{
		path: '/helical-reports',
		filters: [ 'metadata' ]
	}
];

const contextMenuEditOptions = [
	{
		extension: 'hr',
		routeTo: helicalReportUrl
	},
	{
		extension: 'metadata',
		routeTo: metadataUrl
	},
	{
		extension: 'efwdd',
		routeTo: dashboardDesignerUrl
	},
	{
		extension: 'cube',
		routeTo: cubeUrl
	},
	{
		extension: 'instant',
		routeTo: instantBIUrl
	},
	{
		extension: 'hcr',
		routeTo: cannedReportsUrl
	},
	{
		extension: 'model',
		routeTo: agentUrl
	}
];

const agentInteractContextMenuOption = {
	id: 'intr',
	name: 'Interact',
	types: [ 'file' ],
	extensions: [ 'model' ],
	icon: <HIIcon name="hi-instant-bi-svg" />,
	disabled: false,
	permission: [ 2, 3, 4, 5 ],
	userRoles: {
		[roleUser]: [ 'model' ],
		[roleAdmin]: [ 'model' ]
	}
};

export {
	filterByOptions,
	getFilterOptionsForExtensions,
	getExtensionIcon,
	defaultUnknownExtensionIcon,
	groupByOptions,
	allContextMenuOptions,
	agentInteractContextMenuOption,
	tableColumnsConfig,
	filtersForRoutes,
	contextMenuEditOptions
};
