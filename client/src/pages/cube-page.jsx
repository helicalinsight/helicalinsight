import { HINavbar } from "../components";
import { Cube } from "../components/hi-cube/cube";
import { CubeSidebar } from "../components/hi-cube/cubeSidebar";
import HILayout from "../layouts/hi-layout";
import { SaveOutlined, SaveFilled, SettingOutlined, EditOutlined, TableOutlined } from "@ant-design/icons";
import HIIcon from "../components/common/icons/hi-icons";
import { useDispatch, useSelector } from "react-redux";
import { cubeFileDataAfterSave, cubeLocalResetter, saveCubeMetadataFileDetails, savedCubeDetailsForEdit, setCubeFieldsData, setCubeMode, setCubeTableMode } from "../redux/actions/cube.actions";
import cubeRequests from "../base/requests/cube.requests";
import { convertResponseToCubeFieldsData, handleCubeFormdata, saveCubeSelectedMetadataFileDetails, validateCubeDomainAndDescription } from "../components/hi-cube/helperMethods";
import { appActions, fileBrowserActions } from "../redux/actions";
import SaveItems from "../components/hi-fileBrowser/SaveItems";
import React, { useEffect, useState } from "react";

export function HICubePage({ urlObj = {} }) {
    const cubeState = useSelector((store) => store.cube);
    const { cubeFieldsData, cubeMode, isCubeTableModeNormal, metadataTablesData, metadataDetails, cubeForEdit, cubeDataAfterSave } = cubeState;
    const dispatch = useDispatch();
    const editModeInfo = useSelector((store) => store.app.editModeInfo);
    const [filebrowserFor, setFilebrowserFor] = useState('');
    let fbProperties = {};
    const { getCubeForEdit } = cubeRequests(dispatch);
    const shareRef = React.useRef(null);
    const { dir: urlDir, file: urlFile } = urlObj;

    // console.log(editModeInfo, 'editMode');

    useEffect(() => {
        return () => {
            dispatch(cubeLocalResetter());
        }
    }, [])

    const onFormSumbit = (onSaveData, name) => {
        // dir: saveDetails?.location,
        // file: saveDetails?.uuid,
        handleCubeFormdata({ dispatch, type: filebrowserFor, location: onSaveData.path, fileName: name, cubeFieldsData, metadataTablesData, metadataDetails, cubeMode })
        dispatch(fileBrowserActions.setShowFileBrowser(false))
        setFilebrowserFor('')
    }

    function handleCubeEdit({ dir, file }) {
        getCubeForEdit({ dir, file }, '', (res) => {
            dispatch(setCubeMode('edit'));
            dispatch(savedCubeDetailsForEdit(res));
            dispatch(cubeFileDataAfterSave({ dir: res.location, file: res.fileName }));
            dispatch(saveCubeMetadataFileDetails({
                path: res.metadata.location,
                fileName: res.metadata.metadataFileName,
            }))
            // saveCubeSelectedMetadataFileDetails({ dispatch, record: { path: `${res.metadata.location}/${res.metadata.metadataFileName}` }, cubeMode });
            convertResponseToCubeFieldsData({ dispatch, cubes: res.cubes });
            document.title = `${editModeInfo.title} | HI:Cube`;
            dispatch(setCubeTableMode());
        }, (err) => {
            console.log(err);
        })
    }

    useEffect(() => {
        if (editModeInfo && (editModeInfo.extension === 'cube')) {
            const fileDtls = editModeInfo.dir.split('/');
            let file = fileDtls.pop();
            let dir = fileDtls.join('/');
            // const file = editModeInfo.file;

            dir && file && handleCubeEdit({ dir, file });
            dispatch(appActions.setEditModeInfo(null));
        }
    }, [editModeInfo])

    useEffect(() => {
        if (Object.keys(urlObj).length && urlDir && urlFile) {
            const fileArr = urlFile.split('.');
            const extension = fileArr[fileArr.length - 1];
            (extension === 'cube') && handleCubeEdit({ dir: urlDir, file: urlFile })
        }
    }, [urlObj])

    const onSaveAs = () => {
        if (!validateCubeDomainAndDescription({ cubeFieldsData, dispatch })) {
            return;
        }
        setFilebrowserFor('saveAs');
        dispatch(fileBrowserActions.setShowFileBrowser(true));
        dispatch(fileBrowserActions.setGlobalFbVisibility(true));
        // const { location, fileName } = cubeForEdit;
        // onFormSumbit({ path: location || 'cube' }, fileName || 'cube-src');
    }

    if (filebrowserFor === 'save' || filebrowserFor === 'saveAs') {
        fbProperties.footerForm = {
            type: "Save",
            form: (
                <SaveItems
                    formHeading="Cube file name"
                    onFormSumbit={onFormSumbit}
                    saveButtonText={filebrowserFor === 'saveAs' ? "Save As" : "Save"}
                    cancelButtonText="Cancel"
                    inputValue={filebrowserFor === 'saveAs' ? cubeForEdit.fileName : 'cube-1'}
                />
            ),
        }
    } else if (filebrowserFor === 'edit') {
        fbProperties.extensionOptions = ["cube"]
        fbProperties.contextMenuOptions = {
            append: true,
            options: [
                {
                    name: "Edit",
                    key: 'edit',
                    id: 'edt',
                    merge: true,
                    disabled: false,
                    types: ["file"],
                    // icon: <EditOutlined />,
                    extensions: ["cube"],
                    callback: (record) => {
                        // console.log(record)
                        dispatch(
                            appActions.setEditModeInfo({
                                dir: record.path,
                                file: record.name, // + '.cube',
                                extension: record.extension,
                                title: record.title
                            })
                        );
                        // dispatch(setCubeFieldsData({ mode: 'edit', value: record })); //pending
                    },
                },
                // {
                //     // merge: true,
                //     icon: <TableOutlined />,
                //     id: 'use',
                //     name: "Use metadata",
                //     types: ["file"],
                //     extensions: ["metadata"],
                //     disabled: false,
                //     callback: (rec) => {
                //         saveCubeSelectedMetadataFileDetails({ dispatch, rec, cubeMode });
                //     },
                // }
            ],
        }
    }

    const handleShare = () => {
        shareRef.current = true;
        dispatch(fileBrowserActions.setShareModalVisibility());
    }

    const onSave = () => {
        if (!validateCubeDomainAndDescription({ cubeFieldsData, dispatch })) {
            return;
        }
        const savedLocation = cubeForEdit?.location || cubeDataAfterSave?.dir;
        const savedFileName = cubeForEdit?.fileName || cubeDataAfterSave?.file;
        if (cubeMode === 'edit' && savedLocation && savedFileName) {
            setFilebrowserFor('save');
            onFormSumbit({ path: savedLocation }, savedFileName);
            return;
        }
        setFilebrowserFor('save');
        dispatch(fileBrowserActions.setShowFileBrowser(true));
        dispatch(fileBrowserActions.setGlobalFbVisibility(true));
    }

    const onSwitchToAdvance = () => {
        dispatch(setCubeTableMode());
    }

    const taskBarItems = [
        {
            tooltip: 'Save', icon: <SaveOutlined />, dropdown: [
                { tooltip: 'Save', name: 'Save', icon: <SaveOutlined />, callBack: onSave },
                { tooltip: 'Save As', name: 'Save As', icon: <SaveFilled />, callBack: onSaveAs }
            ]
        },
        {
            tooltip: "Share",
            icon: <HIIcon name="hi-share" />,
            callBack: handleShare,
        },
        {
            tooltip: isCubeTableModeNormal ? 'Switch To Advance' : 'Switch To Normal',
            icon: <SettingOutlined />,
            callBack: onSwitchToAdvance,
        },
    ]
    return <HILayout
        header={<HINavbar taskbar={taskBarItems} />}
        content={<Cube />}
        defaultSidebar={false}
        // isCustomSidebar={true}
        customSidebar={<CubeSidebar urlObj={urlObj} shareRef={shareRef} fbProperties={fbProperties} setFilebrowserFor={setFilebrowserFor} filebrowserFor={filebrowserFor} />}
    />
}

