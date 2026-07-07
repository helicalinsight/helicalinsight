import { SaveOutlined, SaveFilled, LoadingOutlined, EyeOutlined, TableOutlined, LayoutOutlined, CheckOutlined, UndoOutlined, RedoOutlined, SettingOutlined, UserOutlined, FolderOpenOutlined } from "@ant-design/icons";
import HIIcon from "../common/icons/hi-icons";
import { updateInstantBILayout } from "../../redux/actions/instant-bi.actions";
import "./index.scss"
const getInstantBITaskBarItems = ({
  activeReport,
  dispatch,
  layout,
  handleShare = () => { },
  onSave = () => { },
  onSaveAs = () => { },
  isSaving,
  previewShelf,
  metadataShelf,
  isMetadataPresent = false,
  openFileBrowser = () => { },
  openInstantFileBrowser = () => {}
}) => {
  let loadingItem = [];
  if (isSaving) {
    loadingItem = [
      {
        tooltip:"Please wait while we save your Instant file. This may take few moments.",
        icon: <LoadingOutlined />,
      },
    ];
  }

  const onPreview = () => {
    dispatch(updateInstantBILayout({ key: 'previewShelf', value: !previewShelf }))
  }

  const onMetadataShelf = () => {
    dispatch(updateInstantBILayout({ key: 'metadataShelf', value: !metadataShelf }))
  }

  const taskBarItems = [
    ...loadingItem,
    {
      tooltip: "Save",
      tutorialKey: "hi-instant-bi-save",
      /*mt-05*/ icon: <SaveOutlined className="taskbar-icon" />,
      dropdown: [
        {
          tooltip: "Save",
          name: "Save",
          icon: <SaveOutlined />,
          callBack: onSave,
        },
        {
          tooltip: "Save As",
          name: "Save As",
          icon: <SaveFilled />,
          callBack: onSaveAs,
        },
      ],
    },
    //as of now we are hiding this feature
    // {
    //   tooltip: "Layout",
    //   tutorialKey: "hi-instant-bi-shelf",
    //   icon: <LayoutOutlined />,
    //   dropdown: [
    //     // {
    //     //   tooltip: 'Metadata Shelf',
    //     //   name: "Metadata Shelf",
    //     //   tutorialKey: 'hi-instant-bi-metadata-shelf',
    //     //   icon: metadataShelf ? <CheckOutlined /> : null,
    //     //   callBack: onMetadataShelf
    //     // },
    //     {
    //       tooltip: "Preview Shelf",
    //       name: "Preview Shelf",
    //       tutorialKey: "hi-instant-bi-preview-shelf",
    //       icon: previewShelf ? <CheckOutlined /> : null,
    //       callBack: onPreview
    //     },
    //   ]
    // },
    {
      tooltip: 'Settings',
      tutorialKey: 'hi-instant-bi-settings',
      icon: <SettingOutlined />,
      callBack: () => null
    },
    {
      tooltip: "Open Instant (Edit)",
      tutorialKey: "hi-instant-bi-open-instant",
      icon: <FolderOpenOutlined />,
      callBack: openInstantFileBrowser,
    },
    {
      tooltip: 'Connect Agent',
      tutorialKey: 'hi-instant-bi-metadata',
      icon: <UserOutlined className={!isMetadataPresent ? "" : "instant-bi-metadata-selected"} />,
      callBack: openFileBrowser
    },
    {
      tooltip: "Share",
      tutorialKey: "hr-report-share",
      icon: <HIIcon name="hi-share" />,
      callBack: handleShare,
    },
    // {
    //   tooltip: "Undo",
    //   tutorialKey: "hi-hreport-undo",
    //   icon: <UndoOutlined />,
    //   callBack: () => { },
    // }, {
    //   tooltip: "Redo",
    //   tutorialKey: "hi-hreport-redo",
    //   icon: <RedoOutlined />,
    //   callBack: () => { },
    // },
  ];

  return taskBarItems;
};

export default getInstantBITaskBarItems;
