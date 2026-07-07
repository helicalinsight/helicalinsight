import { Col, Layout, Row, Button, Modal, Typography } from "antd";
import React, { useEffect,useRef,useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { roleTypes } from "../../app/constants";
import { EditOutlined } from '@ant-design/icons';
import { appActions, fileBrowserActions } from "../../redux/actions";
import { classnames } from "../../utils/classNames";
import { HIFileBrowser } from "../hi-fileBrowser/hi-fileBrowser";
import { SidebarFooter } from "../hi-sidebar-footer";
import { CubeMetadata } from "./cubeMetadata";
import { cubeFileDataAfterSave, saveCubeMetadataFileDetails, savedCubeDetailsForEdit, setCubeFieldsData, setCubeMode } from "../../redux/actions/cube.actions";
import { fbOnClickHandler, saveCubeSelectedMetadataFileDetails } from "./helperMethods";
import { ShareFinalModal } from "../hi-fileBrowser/components";
import { CustomIcon } from "../common/custom-icons/CustomIcon";
import HiMetadataArea from "../hi-sidebar/hr-hreportSidebar/hi-metadata-area";
import cubeRequests from "../../base/requests/cube.requests";

const { Sider } = Layout;
const { Text } = Typography;
const { roleUser, roledownload, roleAdmin } = roleTypes;
const parseMetadataRecord = (record) => {
    const fileDtls = record.path.split('/');
    const fileName = fileDtls.pop();
    const path = fileDtls.join('/');
    return { path, fileName };
};

export function CubeSidebar({ urlObj = {}, ...props }) {
    const { applicationSettingsData, toggleSidebar } = useSelector(
        (state) => state.app
    );
    const cubeState = useSelector((store) => store.cube);
    const { cubeMode, cubeDataAfterSave, metadataTablesData, metadataDetails } = cubeState;
    const { fileName, path } = metadataDetails;
    const { roles } = applicationSettingsData.userData.user;
    const dispatch = useDispatch();
    const { getCubeMetadataTablesData } = cubeRequests(dispatch);
    const isGlobalFbEnabled = useSelector(state => state.fileBrowser.globalFbEnabled)
    const showFileBrowser = useSelector((state) => state.fileBrowser.showFileBrowser);
    const isShareModalVisible = useSelector(
        (state) => state.fileBrowser.isShareModalVisible
    );
    const isFooterVisible = [roleAdmin, roleUser, roledownload].some((item) =>
        roles?.includes(item)
    );
    const [metadataChangeModalVisible, setMetadataChangeModalVisible] = useState(false);
    const [metadataToBeChanged, setMetadataToBeChanged] = useState(null);
    const [modalSource, setModalSource] = useState(null);
    const skipMetadataChangeConfirm = useRef(false);
    const checkIsMetadataPresent = () => Boolean(path && fileName);
    const applyMetadataSelection = (record) => {
        skipMetadataChangeConfirm.current = false;
        const { path: metadataPath, fileName: metadataFileName } = parseMetadataRecord(record);
        dispatch(setCubeFieldsData({ mode: 'reset' }));
        dispatch(setCubeMode('create'));
        dispatch(savedCubeDetailsForEdit({}));
        dispatch(cubeFileDataAfterSave({}));
        dispatch(saveCubeMetadataFileDetails({
            path: metadataPath,
            fileName: metadataFileName,
        }));
        dispatch(fileBrowserActions.setShowFileBrowser(false));
        document.title = `HI: Cube`;
    };
    
    const promptMetadataChange = (record, source) => {
        if (skipMetadataChangeConfirm.current) {
            applyMetadataSelection(record);
            return;
        }
        if (checkIsMetadataPresent()) {
            setMetadataToBeChanged(record);
            setModalSource(source);
            setMetadataChangeModalVisible(true);
            return;
        }
        applyMetadataSelection(record);
    };
     const changeMetadata = (record) => {
        promptMetadataChange(record, 'callback');
    };

    const handleMetadataModalOkClick = () => {
        if (modalSource === 'openFileBrowser') {
            skipMetadataChangeConfirm.current = true;
            dispatch(setCubeMode('edit'));
            fbOnClickHandler(dispatch);
            props.setFilebrowserFor('edit');
        } else if (modalSource === 'connectToMetadata') {
            skipMetadataChangeConfirm.current = true;
            props.setFilebrowserFor('');
            fbOnClickHandler(dispatch);
        } else if (metadataToBeChanged) {
            applyMetadataSelection(metadataToBeChanged);
        }
        setMetadataToBeChanged(null);
        setModalSource(null);
        setMetadataChangeModalVisible(false);
    };

    const onFbDoubleClick = (record) => {
        if (props.filebrowserFor === 'edit') {
            dispatch(
                appActions.setEditModeInfo({
                    dir: record.path,
                    file: record.name,
                    extension: record.extension,
                    title: record.title,
                })
            );
            return;
        }
       promptMetadataChange(record, 'doubleClick');
    }
    // console.log(cubeDataAfterSave);
    useEffect(() => {
        if (!isShareModalVisible) {
            props.shareRef.current = false;
        }
    }, [isShareModalVisible]);
      useEffect(() => {
        if (!showFileBrowser) {
          skipMetadataChangeConfirm.current = false;
        }
      }, [showFileBrowser]);

    useEffect(() => {
        // const formData = { location: 'sai_ganesh', metadataFileName: 'Metadata_1.metadata' };
        // if (["development"].includes(process.env.NODE_ENV)) {
        //     getCubeMetadataTablesData({ path: 'narendra', fileName: 'Metadata_1.metadata' });
        // } else {
        (path && fileName) && getCubeMetadataTablesData({ path, fileName });
        // }
    }, [path, fileName])

    useEffect(() => {
        if (Object.keys(urlObj).length && urlObj.dir && urlObj.file) {
            const fileArr = urlObj.file.split('.');
            const extension = fileArr[fileArr.length - 1];
            (extension === 'metadata') && dispatch(saveCubeMetadataFileDetails({
                path: urlObj.dir,
                fileName: urlObj.file,
            }));
            // (extension === 'metadata') && getCubeMetadataTablesData({ path: urlObj.dir, fileName: urlObj.file });
        }
    }, [urlObj])

    let fbProperties = {
        extensionOptions: ["metadata"],
        contextMenuOptions: {
            append: true,
            options: [
                {
                    // merge: true,
                    icon: <CustomIcon name="Cube" />,
                    id: 'use',
                    name: "Use This Metadata",
                    types: ["file"],
                    extensions: ["metadata"],
                    disabled: false,
                    callback: (record) => {
                        // console.log(rec, 'edt');
                        // dispatch(setCubeFieldsData({ mode: 'reset' }));
                        // dispatch(setCubeMode('create'));
                        // let fileDtls = record.path.split('/');
                        // let fileName = fileDtls.pop();
                        // let path = fileDtls.join('/');
                        // dispatch(saveCubeMetadataFileDetails({
                        //     path,
                        //     fileName,
                        // }))
                        // document.title = `HI: Cube`;
                        changeMetadata(record);
                    },
                }
            ],
        }, onDoubleClick: { onFbDoubleClick }
    }
    if (Object.keys(props.fbProperties).length) {
        fbProperties = { ...props.fbProperties };
    }
    // const openFileBrowser = (ext) => {
    //     if (ext === "hr") {
    //       openForEdit();
    //       return null;
    //     }
    //     setShowFb({
    //       onDoubleClick: changeMetadata,
    //       contextMenuOptions: {
    //         append: true,
    //         options: [
    //           {
    //             name: "Use This Metadata",
    //             types: ["file"],
    //             icon: <EditOutlined />,
    //             extensions: ["metadata"],
    //             callback: changeMetadata,
    //             merge: true,
    //             id: "chr",
    //           },
    //         ],
    //       },
    //       extensionOptions: ["metadata"],
    //     });
    //     dispatch(fileBrowserActions.setShowFileBrowser(true));
    //   };

    const openFileBrowser = () => {
         if (checkIsMetadataPresent()) {
            setMetadataToBeChanged(null);
            setModalSource('openFileBrowser');
            setMetadataChangeModalVisible(true);
            return;
        }
        dispatch(setCubeMode('edit'));
        fbOnClickHandler(dispatch);
        props.setFilebrowserFor('edit');
    }

    const onConnectToMetadata = () => {
        // dispatch(setCubeMode('create'));
        if (checkIsMetadataPresent()) {
            setMetadataToBeChanged(null);
            setModalSource('connectToMetadata');
            setMetadataChangeModalVisible(true);
            return;
        }
        props.setFilebrowserFor('');
        fbOnClickHandler(dispatch)
    }
    // console.log({ metadataTablesData });
    return <Sider
        collapsed={toggleSidebar}
        collapsedWidth={0}
        width={"100%"}
        className={`hi-cube-sider ${props.customClass || ""}`}
    >
        {/*-----------     content  (just replace this)      ----------*/}
        <HiMetadataArea
            metadata={metadataTablesData}
            size={{ height: "100%" }}
            openFileBrowser={openFileBrowser} //onEdit
            module='cube'
            onConnectToMetadata={onConnectToMetadata}
        // filebrowserConfig={filebrowserConfig}
        // onGlobalSearch={() => setShowFb(false)}
        />
        {props.shareRef?.current && (
            <ShareFinalModal
                shareOptions={{
                    type: "file",
                    dir: cubeDataAfterSave.dir,
                    file: cubeDataAfterSave.file,
                }}
            />
        )}
        {isGlobalFbEnabled && <HIFileBrowser
            {...fbProperties}
            onDoubleClick={onFbDoubleClick}
        />}
         <Modal
            title="Open another metadata file?"
            open={metadataChangeModalVisible}
            onOk={handleMetadataModalOkClick}
            onCancel={() => {
                skipMetadataChangeConfirm.current = false;
                setMetadataToBeChanged(null);
                setModalSource(null);
                setMetadataChangeModalVisible(false);
            }}
        >
            <Text>
                Are you sure you want to open another metadata file? This will discard any unsaved changes on the cube page.
            </Text>
        </Modal>
    </Sider>
}