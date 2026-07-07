import React, { useState, useEffect } from "react";
import { Typography, Modal } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { deleteItemById } from "../helperMethods.js";
import { fileBrowserActions, hcrActions } from "../../../redux/actions";
import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify.js";
import { capitalize } from "lodash-es";
const { Text } = Typography;

const DeleteModal = ({ extensionOptions }) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);
  const [confirmLoading, setConfirmLoading] = useState(false);
  // const fbRequests = requests.filebrowser(dispatch, path);
  const clickedContextRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const clickedContextMenuItem = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails
  );
  useEffect(() => {
    if (clickedContextMenuItem.contextItem && clickedContextMenuItem.contextItem === "Delete") {
      setIsDeleteModalVisible(true)
    }
  }, [clickedContextMenuItem]);

  useEffect(() => {
    if (!isDeleteModalVisible) setConfirmLoading(false)
  }, [isDeleteModalVisible])

  const handleCancel = () => {
    setIsDeleteModalVisible(false);
    dispatch(fileBrowserActions.setContextMenuItemDetails({ "contextItem": null }));
  };

  const onDeleteItem = () => {
    setConfirmLoading(true);
    // if (extensionOptions?.includes('image')) {
    //   const path = clickedContextRecord.path?.split('/');
    //   const file = path.pop();
    //   const dir = path.join('/')
    //   requests.cannedReport(dispatch)?.handleDeleteImage({
    //     action: "delete", dir, file
    //   }, (res) => {
    //     dispatch(hcrActions.handleHcrImageDel({ delKey: "path", delPath: clickedContextRecord.path }));
    //     handleCancel()
    //   }, (err) => {
    //     handleCancel()
    //   })
    // } else {
    const postObj = {
      sourceArray: JSON.stringify([clickedContextRecord.path]),
      action: "delete",
    };
    fbRequests.postContextMenuOperations(
      postObj,
      (res) => {
        // Notify.success({ ...res, type: "Network Call" });
        dispatch(fileBrowserActions.setDeletedItems({ delKey: "path", delPath: clickedContextRecord.path }));
        if (extensionOptions?.includes('image')) {
          dispatch(hcrActions.handleHcrImageDel({ delKey: "path", delPath: clickedContextRecord.path }));
        }
        handleCancel();
      },
      (error) => {
        // Notify.error({ ...error, type: "Network Call" });
        handleCancel()
      }
    );
    // }
  };

  return (
    <Modal
      data-testid="hi-file-browser-del-modal"
      title={`Deleting ${clickedContextRecord && capitalize(clickedContextRecord.type)}`}
      visible={isDeleteModalVisible}
      onOk={onDeleteItem}
      onCancel={handleCancel}
      confirmLoading={confirmLoading}
    >
      <Text>
        Are you sure you want to delete{" "}
        <b>
          {clickedContextRecord &&
            (clickedContextRecord.type === "folder"
              ? clickedContextRecord.name
              : clickedContextRecord.title)}
        </b>
        ?
      </Text>
    </Modal>
  );
};

export { DeleteModal };
