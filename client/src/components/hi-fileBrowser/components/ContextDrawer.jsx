import React, { useMemo, useEffect, useState } from "react";
import { useSelector, useDispatch } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import requests from "../../../base/requests";
import { EditFolderForm, SaveFileForm } from ".";
import { renameItemById, addFolder } from "../helperMethods.js";
import notify from "../../hi-notifications/notify";
import { Drawer, Space, Button } from "antd";
const ContextDrawer = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const files = useSelector((state) => state.fileBrowser.files.data);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const [drawerTitle, setDrawerTitle] = useState(null);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [onDrawerSubmit, setOnDrawerSubmit] = useState(false);
  const [loading, setLoading] = useState(false);
  const onCancelClick = () => {
    setLoading(false)
    setDrawerVisible(false)
  };
  const onAdd = (name, record, data, isRoot, isPublic, isHidden) => {
    const postObj = {
      sourceArray: JSON.stringify(!isRoot ? [record.path] : [""]),
      action: "newFolder",
      folderName: name,
    };
    // if(isRoot && !isHidden){
    //   postObj["isVisible"] = true
    // }
    if (isPublic) {
      postObj["isPublic"] = isPublic
    }
    // postObj["isVisible"] = !isHidden
    fbRequests.postContextMenuOperations(
      postObj,
      (res) => {
        onCancelClick();
        // Notify.success({ ...res, type: "Network Call" });
        res.data["public"] = res.public ? true : false;
        const result = addFolder(
          JSON.parse(JSON.stringify(data)),
          res.data
        );
        dispatch(
          fileBrowserActions.setFbContent({
            loading: false,
            data: result,
            error: null,
          })
        );
      },
      (error) => {
        onCancelClick();
        // Notify.error({ ...error, type: "Network Call" });
      }
    );
  };
  const onRename = (name, record, data, isPublic, isHidden) => {
    const postObj = {
      sourceArray: JSON.stringify([[record.path, name]]),
      action: "rename",
    };
    if ((isPublic !== record.public) && (isPublic || record.public)) {
      postObj["isPublic"] = isPublic
    }
    // postObj["isVisible"] = !isHidden
    fbRequests.postContextMenuOperations(
      postObj,
      (res) => {
        let renameParams = [
          JSON.parse(JSON.stringify(data)),
          record.path,
          name,
        ];
        onCancelClick();
        // Notify.success({ ...res, type: "Network Call" });
        if ((isPublic !== record.public) && (isPublic || record.public)) {
          renameParams = [...renameParams, res.public, res.permissionLevel];
        }
        const result = renameItemById(...renameParams);
        dispatch(
          fileBrowserActions.setFbContent({
            loading: false,
            data: result,
            error: null,
          })
        );
      },
      (error) => {
        onCancelClick();
        // Notify.error({ ...error, type: "Network Call" });
      }
    );
  };
  const footerForms = useMemo(() => {
    return {
      "New Folder": (
        <EditFolderForm
          onFormConfirm={(name, record, isPublic, isHidden) => onAdd(name, record, files, false, isPublic, isHidden)}
          title="Add new Folder"
          onCancelClick={onCancelClick}
          buttonText="add"
          setDrawerTitle={setDrawerTitle}
          onDrawerSubmit={onDrawerSubmit}
          setOnDrawerSubmit={setOnDrawerSubmit}
          setLoading={setLoading}
          action={"add"}
        />
      ),
      "New Folder Root": (
        <EditFolderForm
          onFormConfirm={(name, record, isPublic, isHidden) => onAdd(name, "", files, true, isPublic, isHidden)}
          title="Add new Folder"
          addToRoot={true}
          onCancelClick={onCancelClick}
          buttonText="add"
          setDrawerTitle={setDrawerTitle}
          onDrawerSubmit={onDrawerSubmit}
          setOnDrawerSubmit={setOnDrawerSubmit}
          setLoading={setLoading}
          action={"add"}
        />
      ),
      "Rename": (
        <EditFolderForm
          onFormConfirm={(name, record, isPublic, isHidden) =>
            onRename(name, record, files, isPublic, isHidden)
          }
          title="Rename"
          onCancelClick={onCancelClick}
          buttonText="save"
          setDrawerTitle={setDrawerTitle}
          onDrawerSubmit={onDrawerSubmit}
          setOnDrawerSubmit={setOnDrawerSubmit}
          setLoading={setLoading}
          action={"rename"}
        />
      ),
      "Save File": <SaveFileForm onCancelClick={onCancelClick} />,
    };
  }, [files, setDrawerTitle, onDrawerSubmit]);

  useEffect(() => {
    if (!drawerVisible) {
      dispatch(
        fileBrowserActions.setContextMenuItemDetails({ contextItem: null })
      );
    }
  }, [drawerVisible]);

  const clickedContextMenuItem = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.contextItem
  );
  useEffect(() => {
    if (
      clickedContextMenuItem &&
      footerForms[clickedContextMenuItem] &&
      !drawerVisible
    )
      setDrawerVisible(true);
  }, [clickedContextMenuItem]);

  return (
    <React.Fragment>
      {clickedContextMenuItem && drawerVisible && (
        <Drawer
        data-testid = "hi-file-browser"
          title={drawerTitle}
          placement="right"
          onClose={() => {
            setOnDrawerSubmit(false)
            setDrawerVisible(false)
          }}
          zIndex={1005}
          visible={drawerVisible}
          destroyOnClose={true}
          width={400}
          extra={
            <Space>
              <Button onClick={() => setDrawerVisible(false)}>Cancel</Button>
              <Button loading={loading} disabled={loading} onClick={() => {
                setOnDrawerSubmit(true)
              }} type="primary">
                Submit
              </Button>
            </Space>
          }
        >
          {footerForms[clickedContextMenuItem]}
        </Drawer>
      )}
    </React.Fragment>
  );
};

export { ContextDrawer };
