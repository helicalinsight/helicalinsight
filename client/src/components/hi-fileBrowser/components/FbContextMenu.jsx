import React, { useState, useEffect, useRef } from "react";
import { Col, Menu, Modal, Popconfirm, Progress, Row, Typography } from "antd";
import { useDispatch, useSelector } from "react-redux";
import {
  fileBrowserActions,
  appActions,
  setSecurityTableData,
  setFlterbyData,
  setFirstRender,
  metadataActions,
} from "../../../redux/actions";
import { AGENT_INTERACT_ACTION, contextMenuEditOptions } from "../constants.js";
import { routesUrl } from "../../../app/constants";
import { filterValues } from "../../hi-metadata/components/editor/security/validatedTable/validatedTable";
import { cloneDeep } from "lodash-es";
import { removeAllReports } from "../../../redux/actions/hreport.actions";
import { addFolder, containsSameNamedResource, deleteItemById, getPasteFormData, handleCheckFileInNewResource } from "../helperMethods.js";
import requests from "../../../base/requests/index.js";
import HIPopConfirm from "../../common/components/pop-confirm.jsx";
import { ExclamationCircleOutlined } from "@ant-design/icons";
const { updateRoute } = appActions;

const FbContextMenu = (props) => {
  const dispatch = useDispatch();
  const { setMenuVisible, record, menuOptions, closeFbOnCallback, copyOrCutItemDetails, files, hiddenFileInput, selectedRecordRef } = props;
  const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
  const metadataMode = useSelector((state) => state.metadata.present.mode);
  const fbRequests = requests.filebrowser(dispatch, applicationSettingsData.settings);

  const onContextMenuItemClick = (item) => {
    // closing context menu
    setMenuVisible({ visible: false });

    // storing clicked context item in redux
    const { children, ...rec } = record;
    dispatch(
      fileBrowserActions.setContextMenuItemDetails({
        contextItem: item.name,
        clickedRecord: rec,
      })
    );

    // invoking callbacks for local context items
    if (item.callback && typeof item.callback === "function") {
      item.callback(record);
      // closing file browser
      if (
        (closeFbOnCallback === undefined || closeFbOnCallback) &&
        !item.executeCallbackOnly
      )
        dispatch(fileBrowserActions.setShowFileBrowser(false));
      return;
    }

    //if share is clicked, toggling share visibility
    if (item.id === "shr") {
      dispatch(fileBrowserActions.setShareModalVisibility());
    }

    if ((item.id === "opn" || item.id === "onw") && record.type === "file") {
      if (item.id === "opn") {
        dispatch(removeAllReports());
        dispatch(fileBrowserActions.setShowFileBrowser(false));
        dispatch(fileBrowserActions.setSearchResults(null));
        dispatch(
          appActions.setViewModeInfo({
            file: { path: record.path, name: record.name, title: record.title },
            mode: "open",
            filters: [],
            extension: record.extension,
          })
        );
        dispatch(updateRoute(routesUrl.reportViewUrl));
      }
      if (item.id === "onw") {
        const path = record.path.split("/");
        path.splice(-1);
        const newUrl =
          window.baseURL +
          `#/report-viewer?dir=${path.join("/")}&file=${record.name}&mode=open`;
        window.open(newUrl);
      }
    }

    if (item.id === "edt" && item.extensions.includes(record.extension)) {
      if (metadataMode === "edit" && record.extension.includes('metadata')) {
        dispatch(fileBrowserActions.setShowFileBrowser(false));
        Modal.confirm({
          title: <Typography.Text style={{ paddingInline: "11px" }}>Open Another Metadata File?</Typography.Text>,
          icon: <></>,
          closable: true,
          content: <div style={{ padding: "24px 16px", "borderBlock": "1px solid #f0f0f0", margin: "14px -10px -14px" }}><Typography.Text>{"Are you sure you want to open another metadata file? This will discard any unsaved changes."}</Typography.Text></div>,
          bodyStyle: { padding: "14px 10px" },
          width: "520px",
          onOk: () => {
            if (!item.executeCallbackOnly) {
              const routeToUrl = contextMenuEditOptions.find(
                (e) => e.extension === record.extension
              );
              if (record.extension.includes('metadata')) {
                dispatch(metadataActions.resetMetadataState({ mode: "" }))
              }
              if (routeToUrl) dispatch(updateRoute(routeToUrl.routeTo));
              dispatch(
                appActions.setEditModeInfo({
                  dir: record.path,
                  file: record.name,
                  extension: record.extension,
                  route: 'metadata'
                })
              );
            }
          },
        })
      } else {
        if (!item.executeCallbackOnly) {
          dispatch(fileBrowserActions.setShowFileBrowser(false));
          const routeToUrl = contextMenuEditOptions.find(
            (e) => e.extension === record.extension
          );
          if (record.extension.includes('metadata')) {
            dispatch(metadataActions.resetMetadataState({ mode: "" }))
          }
          if (routeToUrl) dispatch(updateRoute(routeToUrl.routeTo));
          dispatch(
            appActions.setEditModeInfo({
              dir: record.path,
              file: record.name,
              extension: record.extension,
              route: 'metadata'
            })
          );
        }
      }
    }

    if (item.id === "chr") {
      dispatch(fileBrowserActions.setShowFileBrowser(false));
      dispatch(
        appActions.setEditModeInfo({
          dir: record.path,
          file: record.name,
          extension: record.extension,
        })
      );
      dispatch(updateRoute(routesUrl.helicalReportUrl));
    }

    if (item.id === "intr") {
      dispatch(fileBrowserActions.setShowFileBrowser(false));
      dispatch(
        appActions.setEditModeInfo({
          dir: record.path,
          file: record.name,
          title: record.title,
          extension: record.extension,
          action: AGENT_INTERACT_ACTION,
        })
      );
      dispatch(updateRoute(routesUrl.instantBIUrl));
    }

    if (item.id === "exp") {
      dispatch(
        fileBrowserActions.setExportModalData({
          visible: true,
          recordData: record,
        })
      );
    }

    if (item.id === "cut") {
      dispatch(
        fileBrowserActions.setCutOrCopyItemDetails({ action: "cut", sourceUrl: record.path, sourcePermission: record.permissionLevel, sourceName: record.description || record.name, sourceType: record.type, sourceChildren: record.children || [] })
      );
      // dispatch(fileBrowserActions.setDeletedItems({ delKey: "path", delPath: record.path }));
    }

    if (item.id === "cpy") {
      dispatch(
        fileBrowserActions.setCutOrCopyItemDetails({ action: "copy", sourceUrl: record.path, sourcePermission: record.permissionLevel, sourceName: record.description || record.name, sourceType: record.type })
      );
    }

    if (item.id === "pst") {
      fbRequests.postContextMenuOperations(
        getPasteFormData({ copyOrCutItemDetails, record, item }),
        (res) => {
          // res.data.name = res.data.path.split("/").pop();
          // if (copyOrCutItemDetails.action === "cut" ) {
          //   dispatch(fileBrowserActions.setDeletedItems({ delKey: "path", delPath: copyOrCutItemDetails.sourceUrl }));
          // }
          if (res.data.type === 'file') {
            dispatch(fileBrowserActions.saveFileinFb([res.data]));
            if (copyOrCutItemDetails.action === "cut") {
              if (containsSameNamedResource(res.data, record.children)) {
                if (!item.isSkipped && res.data.path !== copyOrCutItemDetails.sourceUrl)
                  dispatch(fileBrowserActions.setDeletedItems({ delKey: "path", delPath: copyOrCutItemDetails.sourceUrl }));
              } else {
                dispatch(fileBrowserActions.setDeletedItems({ delKey: "path", delPath: copyOrCutItemDetails.sourceUrl }));
              }
            }
          } else { // folder
            // res.data["public"] = res.public ? true : false;
            let updatedFiles = JSON.parse(JSON.stringify(files)) || [];
            const sameNameResource = containsSameNamedResource(res.data, record.children);
            if (copyOrCutItemDetails.action === "cut") {
              if (sameNameResource) {
                updatedFiles = deleteItemById(updatedFiles, "path", res.data.path);
                if (item.isSkipped) {
                  function findReqChildInDest(destParentChildren, resChildToCheck) {
                    return destParentChildren.find(item => (item.name === resChildToCheck.name) && (item.type === resChildToCheck.type));
                  }

                  function deleteRecord(resChildren, destChild) {
                    for (let i = 0; i < resChildren.length; i++) {
                      if (containsSameNamedResource(resChildren[i], destChild.children)) {
                        if (resChildren[i].type === 'folder') {
                          deleteRecord(resChildren[i].children, findReqChildInDest(destChild.children, resChildren[i]));
                        } else {
                          if (!findReqChildInDest(destChild.children, resChildren[i])) {
                            updatedFiles = deleteItemById(updatedFiles, "path", resChildren[i].path);
                          }
                        }
                      } else {
                        updatedFiles = deleteItemById(updatedFiles, "path", resChildren[i].path);
                      }
                    }
                    return;
                  }
                  deleteRecord(copyOrCutItemDetails.sourceChildren, findReqChildInDest(record.children, { name: copyOrCutItemDetails.sourceName, type: copyOrCutItemDetails.sourceType }));
                } else { //not skipped
                  updatedFiles = deleteItemById(updatedFiles, "path", copyOrCutItemDetails.sourceUrl);
                }

              } else {
                //copy
                updatedFiles = deleteItemById(updatedFiles, "path", copyOrCutItemDetails.sourceUrl);
              }
            }
            else {
              if (sameNameResource) {
                updatedFiles = deleteItemById(updatedFiles, "path", res.data.path);

              }
            }
            const result = addFolder(
              updatedFiles,
              res.data
            );
            dispatch(
              fileBrowserActions.setFbContent({
                loading: false,
                data: result,
                error: null,
              })
            );
          }
          dispatch(fileBrowserActions.setCutOrCopyItemDetails(null));
        },
        (error) => {
          // Notify.error({ ...error, type: "Network Call" });
          // handleCancel()
        }
      );
    }

    if (item.id === "imp") {

    }

    return;
  };

  const onCancelPopover = () => {
    // setSelectedUser([]);
  }

  return menuOptions?.length > 0 ? (
    <Menu data-testid="hi-file-browser-context-menu" className="fb-context-menu">
      {menuOptions.map((item, i) => {
        if (item.id === 'pst') {
          let isResourceExist = false;
          let sourceParentPath = '';
          if (copyOrCutItemDetails) {
            sourceParentPath = copyOrCutItemDetails.sourceUrl?.split('/');
            if (sourceParentPath.length > 1) {
              sourceParentPath.pop();
            }
            sourceParentPath = sourceParentPath.join('/');
          }
          if (record?.path !== sourceParentPath) {
            // (record?.path !== sourceParentPath) && sourceParentPath && copyOrCutItemDetails && record?.children
            isResourceExist = true
            // handleCheckFileInNewResource({ files: record.children || [], sourceType: copyOrCutItemDetails.sourceType, sourceName: copyOrCutItemDetails.sourceName });
          }
          return isResourceExist ? <Popconfirm
            title={<HIPopConfirm
              title={`${'Skip Or Override'}`}
              description={<p>Choose to skip or override the file(s) or resource(s) you are copying if it already exists. Skipping preserves the original, overriding replaces it.
                {/* Are you sure you want to overwrite the resource <b>{copyOrCutItemDetails?.sourceName}</b> in the current path with new one? */}
              </p>} />}
            onConfirm={() => { onContextMenuItemClick({ ...item, isSkipped: true }) }}
            okText="Skip"
            onCancel={() => { onContextMenuItemClick({ ...item, isSkipped: false }) }}
            cancelText="Override"
            placement='left'
          >
            <Menu.Item
              disabled={!Boolean(copyOrCutItemDetails)}
              style={{ height: 30, lineHeight: 2.2 }}
              key={`menu${i}`}
              icon={item.icon}
            >
              {item.name}
            </Menu.Item>
          </Popconfirm> : <Menu.Item
            disabled={!Boolean(copyOrCutItemDetails)}
            style={{ height: 30, lineHeight: 2.2 }}
            key={`menu${i}`}
            icon={item.icon}
            onClick={({ domEvent }) => {
              domEvent.stopPropagation();
              onContextMenuItemClick(item);
            }}
          >
            {item.name}
          </Menu.Item>

        } else if (item.id === 'imp') {
          return <>
            <Menu.Item
              disabled={item.disabled}
              style={{ height: 30, lineHeight: 2.2 }}
              key={`menu${i}`}
              icon={item.icon}
              onClick={() => {
                selectedRecordRef.current = record;
                hiddenFileInput.current.click();
              }}
            >
              {item.name}
            </Menu.Item>
          </>
        } else {
          return <Menu.Item
            disabled={item.disabled}
            style={{ height: 30, lineHeight: 2.2 }}
            key={`menu${i}`}
            icon={item.icon}
            onClick={({ domEvent }) => {
              if (item.name === "Edit") {
                dispatch(setFirstRender(true));
                dispatch(setFlterbyData(cloneDeep(filterValues)));
              }
              domEvent.stopPropagation();
              onContextMenuItemClick(item);
            }}
          >
            {item.name}
          </Menu.Item>
        }
      })}
    </Menu>
  ) : (
    <></>
  );
};

export { FbContextMenu };
