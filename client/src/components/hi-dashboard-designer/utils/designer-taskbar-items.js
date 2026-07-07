import {
    SaveOutlined,
    SaveFilled,
    EyeOutlined,
    SyncOutlined,
    FundProjectionScreenOutlined,
    RedoOutlined,
    UndoOutlined,
    SettingOutlined,
    LoadingOutlined
  } from "@ant-design/icons";
import { designerRedo, designerUndo, resetDesignerState, setToggleToolsAreaShelf, toggleDashboardDrawer, toggleGridItemDrawer, updateGroupId } from "../../../redux/actions/dashboard-designer.actions";
import { TaskbarIcon } from "../../common/custom-icons/CustomIcon";
import { handleFullScreenClick } from "../../hi-reports/hr-taskbar-items";


export const designerTaskbarItems=({isSaving,setRefresh,onSave,onSaveAs,dispatch,refs,designerPast,designerFuture,handleUndo,handleRedo})=>{
    let loadingItem = [];
    if (isSaving) {
      loadingItem = [
        {
          tooltip: "Please wait while we save your dashboard. This may take a few moments.",
          icon: <LoadingOutlined />,
        },
      ];
    }
    refs.save.current=onSave
  refs.saveAs.current=onSaveAs
  refs.undo.current=handleUndo
  refs.redo.current=handleRedo
   return [
      ...loadingItem,
      {
        tooltip: "Save",
        scLocation: "DD",
        scText: "S",
        id: "dd-save",
        ref:refs.saveDropdown,
        tutorialKey: "hi-designer-save",
        /*mt-05*/ icon: <SaveOutlined className="taskbar-icon" />,
        dropdown: [
          {
            tooltip: "Save (Ctrl + S)",
            name: "Save",
            icon: <SaveOutlined />,
            scText: "1",
            scLocation: "DD SAVE",
            ref:refs.save,    
            callBack: onSave,
          },
          {
            tooltip: "Save As",
            name: "Save As",
            scText: "2",
            scLocation: "DD SAVE",
            ref:refs.saveAs,    
            icon: <SaveFilled />,
            callBack: onSaveAs,
          },
        ],
      },
      // {
      //   tooltip: "Preview",
      //   tutorialKey: "hi-designer-preview",
      //   icon: (
      //     <EyeOutlined
      //       className="taskbar-icon"
      //       style={{ cursor: "not-allowed", fontSize: "20px" }}
      //     />
      //   ),
      //   // callBack: handlePreviewClick,
      // },
      {
        tooltip: "Refresh",
        tutorialKey: "hi-designer-refresh",
         scLocation: "DD",
        scText: "R",
        icon: (
          <SyncOutlined
            className="taskbar-icon"
          />
        ),
        callBack: ()=>{
          setRefresh(new Date())},
      },
      {
        tooltip: "Presentation Mode",
        tutorialKey: "hi-designer-presentation",
         scLocation: "DD",
        scText: "P",
        icon: (
          <FundProjectionScreenOutlined
            className="taskbar-icon"
          />
        ),
        callBack: ()=>{handleFullScreenClick()},
      },
      // {
      //   tooltip: "Grid Lines",
      //   icon: (
      //     <TutorialInfo elementKey="hi-designer-grid-lines">
      //       <TableOutlined />
      //     </TutorialInfo>
      //   ),
      // },
      {
        tooltip: "Toggle Tool Shelf", // 5444 - fix
        tutorialKey: "hi-designer-tool-shelf",
        shortcutText: "L",
        scText: "L",
        scLocation: "DD",
        icon: <TaskbarIcon className="taskbar-icon" />,
        callBack: () => {
          dispatch(setToggleToolsAreaShelf());
        },
      },
      {
        tooltip: "Undo (Ctrl + Z)",
        tutorialKey: "hi-designer-undo",
        dataTestId: "hi-undo-designer-onclick",
        shortcutText: "Z",
        scText: "Z",
        scLocation: "DD",
  
        icon: (
          <UndoOutlined
            data-testid="hi-designer-undo"
            style={
              designerPast?.length ? {} : { cursor: "not-allowed", opacity: 0.5 }
            }
            className="taskbar-icon"
          />
        ),
        callBack: () => {
          dispatch(designerUndo());
          dispatch(toggleGridItemDrawer(false));
          dispatch(toggleDashboardDrawer(false));
        },
      },
      {
        tooltip: "Redo (Ctrl + Y)",
        tutorialKey: "hi-designer-redo",
        dataTestId: "hi-redo-designer-onclick",
        shortcutText: "Y",
        scText: "Y",
        scLocation: "DD",
  
        icon: (
          <RedoOutlined
            data-testid="hi-designer-redo"
            style={
              designerFuture?.length
                ? {}
                : { cursor: "not-allowed", opacity: 0.5 }
            }
            className="taskbar-icon"
          />
        ),
        callBack: () => {
          dispatch(designerRedo());
          dispatch(toggleGridItemDrawer(false));
          dispatch(toggleDashboardDrawer(false));
        },
      },
      {
        tooltip: "Settings",
        tutorialKey: "hi-designer-dashboard-settings",
        shortcutText: "I",
        scText: "I",
        scLocation: "DD",
        icon: <SettingOutlined className="taskbar-icon" />,
        callBack: () => {
          dispatch(updateGroupId("header"));
          dispatch(toggleDashboardDrawer(true));
        },
      },
    ];
}