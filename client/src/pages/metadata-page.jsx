import { LoadingOutlined, SaveFilled, SaveOutlined, UndoOutlined, RedoOutlined } from "@ant-design/icons";
import { isEqual } from 'lodash-es';
import React from "react";
import { useDispatch, useSelector, useStore } from "react-redux";
import { HIMetadata, HINavbar } from "../components";
import HIIcon from '../components/common/icons/hi-icons';
import { ShareFinalModal } from "../components/hi-fileBrowser/components";
import { CheckTablesToHaveAtleastOneTableOrView, checkTablesAvailability, handleJoinActions, handleSave, validateMetadataName } from "../components/hi-metadata/utils";
import notify from "../components/hi-notifications/notify";
import HILayout from "../layouts/hi-layout";
import MetadataLayout from "../layouts/metadata-layout";
import { appActions, fileBrowserActions, metadataActions } from "../redux/actions";
import { Modal } from "antd";
import { mapTablesColumnsWithUniqueKey } from "../components/hi-metadata/components/editor/security/securityHelperMethods";
import { handleExpressionTabColDelete } from "../components/hi-metadata/components/editor/security/validatedTable/constants";

function hasClass(el, className) {
    if (el.classList)
        return el.classList.contains(className);
    return !!el.className.match(new RegExp('(\\s|^)' + className + '(\\s|$)'));
}

function addClass(el, className) {
    if (el.classList)
        el.classList.add(className)
    else if (!hasClass(el, className))
        el.className += " " + className;
}

function removeClass(el, className) {
    if (el.classList)
        el.classList.remove(className)
    else if (hasClass(el, className)) {
        var reg = new RegExp('(\\s|^)' + className + '(\\s|$)');
        el.className = el.className.replace(reg, ' ');
    }
}

const HIMetadataPageComponent = ({ urlObj }) => {
    const store = useStore()
    const dispatch = useDispatch()
    const saveDetails = useSelector(state => state.metadata.present.saveDetails, isEqual)
    const isSavingInProgress = useSelector(state => state.metadata.present.isSavingInProgress)
    const editMdFromHomeInfo = useSelector(state => state.app.editModeInfo)
    const isMultiConnection = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata).length > 1;
    const views = useSelector(state => state.metadata.present.views);
    const joins = useSelector(state => state.metadata.present.joins);
    const tables = useSelector(state => state.metadata.present.tables);
    const metadataName = useSelector(state => state.metadata.present.metadataName);
    const mode = useSelector(state => state.metadata.present.mode);
    const securityTableData = useSelector(state => state.metadata.present.securityTableData);
    const canUndo = useSelector(state => state.metadata.past);
    const canRedo = useSelector(state => state.metadata.future);
    const [filebrowserFor, setFilebrowserFor] = React.useState(false)
    const [saveType, setSaveType] = React.useState('save')
    const Notify = notify(dispatch);
    let onEditMetadata = null;
    const shareRef = React.useRef(null);
    const isShareModalVisible = useSelector(
        (state) => state.fileBrowser.isShareModalVisible
    );
    const isAlreadyFetched = React.useRef(null)

    React.useEffect(() => {
        if (!isShareModalVisible) {
            shareRef.current = false;
        }
    }, [isShareModalVisible]);

    React.useEffect(() => { //4533
        if (editMdFromHomeInfo?.extension === 'metadata' && editMdFromHomeInfo?.route !== 'hreport') {
            if (editMdFromHomeInfo && ('dir' in editMdFromHomeInfo) && ('file' in editMdFromHomeInfo) && !(editMdFromHomeInfo.source === 'metadata-page') && editMdFromHomeInfo.file?.includes('.metadata')) {
                dispatch(metadataActions.updateMetadataState({
                    key: 'mode', value: 'edit', others:
                        [{ key: 'editMdFromHomeInfo', value: editMdFromHomeInfo }] //#4931
                }))
                isAlreadyFetched.current = false;
                dispatch(appActions.setEditModeInfo(false))//#4931
            }
        }
    }, [editMdFromHomeInfo])

    React.useEffect(() => { //4533
        if (urlObj?.dir && urlObj?.file && typeof (urlObj.dir) === 'string' && typeof (urlObj.file) === 'string') {
            if (urlObj.file?.includes('.metadata') && urlObj.dir.length) {
                dispatch(metadataActions.updateMetadataState({
                    key: 'mode', value: 'edit', others:
                        [{ key: 'editMdFromHomeInfo', value: { file: urlObj.file, dir: `${urlObj.dir}/${urlObj.file}` } }]
                }))
            }
        }
    }, [])

    const handleKeyPress = React.useCallback((event) => {
        if (event.ctrlKey === true && !event.shiftKey) {
            if (event.key?.toLowerCase() === 's') {
                validateViewsForMultiConnections({ isSaveAction: true })
                event.preventDefault();
            }
        }
        else if (event.ctrlKey && event.shiftKey) {
            if (event.key?.toLowerCase() === 's') {
                validateViewsForMultiConnections({ isSaveAction: false })
                // onSaveAs()
                event.preventDefault();
            }
            else if (event.key?.toLowerCase() === 'q') {
                dispatch(metadataActions.toggleMDEditor({}))
            }
            else if (event.key.toLowerCase() === 'x') {
                console.log(handleSave({ store, dispatch, type: 'save', location: 'saveDetails.location', fileName: 'metadataName', returnDataForJest: true }))
                // event.preventDefault();
            }
            else if (event.key.toLowerCase() === 'z') {
                console.log(handleSave({ store, dispatch, type: 'saveAs', location: 'saveDetails.location', fileName: 'metadataName', returnDataForJest: true }))
                // event.preventDefault();
            }
        }
    }, []);

    React.useEffect(() => {
        // attach the event listener
        document.addEventListener('keydown', handleKeyPress);

        // remove the event listener
        return () => {
            document.removeEventListener('keydown', handleKeyPress);
        };
    }, [handleKeyPress]);

    const getListOfTableColumnNames = (tables) => {
        let tabCols = []
        Object.values(tables).map(eachTable => {
            if (!eachTable.columns) {
                tabCols.push(eachTable.alias)
            }
            Object.keys(eachTable.columns || []).map(col => {
                tabCols.push(`${eachTable.alias}.${eachTable.columns[col].alias}`)
            })
        })
        return tabCols
    }

    function validateViewsForMultiConnections({ isSaveAction = true }) {
        if (isMultiConnection && views.length) {
            return Modal.confirm({
                title: "Save Metadata",
                content: <div>
                    Views are not supported with multiple connections means that views cannot be used when there are multiple connections to the database.
                    <br />
                    If you are editing the metadata consider removing the view.
                    <br /> <br />
                    Newly created views will be deleted.
                    <br />
                </div>,
                onOk: () => { isSaveAction ? onSave() : onSaveAs() },
                onCancel: () => { return }
            })
        }
        if (isSaveAction) {
            return onSave()
        } else {
            return onSaveAs()
        }
    }

    const onSave = () => {
        //validating joins before save -- start
        let { emptyJoins, invalidJoins } = handleJoinActions({
            joins,
            listOfTableNames: Object.values(tables).map(eachTable => eachTable.name),
            listOfTableColumnNames: getListOfTableColumnNames(tables),
            tables,
            action: 'validateJoins',
        })

        if (emptyJoins) {
            // One or more joins are empty.
            notify(dispatch).error({
                type: "Validation",
                message: 'One or more joins are empty.',
            })
            return
        }
        if (invalidJoins) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Joins contains some invalid tables or invalid columns.',
            })
            return
        }

        const mappedTablesColumns = mapTablesColumnsWithUniqueKey({ tables, mode });
        let invalidSecurities = securityTableData.some(record => {
            return handleExpressionTabColDelete({ mappedTablesColumns, record });
        })
        if (invalidSecurities) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Security contains some invalid expressions.',
            })
            return
        }

        if (!checkTablesAvailability({ tables })) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Please add atleast one table to metadata panel',
            })
            return
        }

        if (!CheckTablesToHaveAtleastOneTableOrView({ tables })) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Please have atleast one table or view along with duplicate table(s) in the metadata',
            })
            return
        }

        let invalidName = validateMetadataName(metadataName)
        if (invalidName) {
            notify(dispatch).error({
                type: "Validation",
                message: invalidName,
            })
            addClass(document.getElementById('metadata-name-blinker'), 'blink')
            setTimeout(() => {
                removeClass(document.getElementById('metadata-name-blinker'), 'blink')
            }, 1500)
            return
        }
        //validating joins before save -- end
        setFilebrowserFor('save')
        // saveType = 'save'
        setSaveType('save')
        if (store.getState()?.metadata?.present.saveDetails?.uuid) {
            handleSave({ store, dispatch, type: 'save', location: saveDetails.location, fileName: metadataName })
            return
        }
        dispatch(fileBrowserActions.setSearchResults(null));
        dispatch(fileBrowserActions.setShowFileBrowser(true))
    }
    const onSaveAs = () => {
        //validating joins before save -- start
        let { emptyJoins, invalidJoins } = handleJoinActions({
            joins,
            listOfTableNames: Object.values(tables).map(eachTable => eachTable.name),
            listOfTableColumnNames: getListOfTableColumnNames(tables),
            tables,
            action: 'validateJoins',
        })
        if (emptyJoins) {
            // One or more joins are empty.
            notify(dispatch).error({
                type: "Validation",
                message: 'One or more joins are empty.',
            })
            return
        }
        if (invalidJoins) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Joins contains some invalid tables or invalid columns.',
            })
            return
        }
        const mappedTablesColumns = mapTablesColumnsWithUniqueKey({ tables, mode });
        let invalidSecurities = securityTableData.some(record => {
            return handleExpressionTabColDelete({ mappedTablesColumns, record });
        })
        if (invalidSecurities) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Security contains some invalid expressions.',
            })
            return
        }
        if (!checkTablesAvailability({ tables })) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Please add atleast one table to metadata panel',
            })
            return
        }

        if (!CheckTablesToHaveAtleastOneTableOrView({ tables })) {
            notify(dispatch).error({
                type: "Validation",
                message: 'Please have atleast one table or view along with duplicate table(s) in the metadata',
            })
            return
        }

        let invalidName = validateMetadataName(metadataName)
        if (invalidName) {
            notify(dispatch).error({
                type: "Validation",
                message: invalidName,
            })
            return
        }
        setFilebrowserFor('save')
        // saveType = 'saveAs'
        setSaveType('saveAs')
        // handleSave({ store, dispatch, type: 'saveAs' })
        dispatch(fileBrowserActions.setShowFileBrowser(true))
        dispatch(fileBrowserActions.setSearchResults(null));
    }
    const openFB = ({ fbFor = 'edit', onEdit }) => {
        if (onEdit) onEditMetadata = onEdit
        setFilebrowserFor(fbFor)
        dispatch(fileBrowserActions.setSearchResults(null));
    }
    const handleShare = () => {
        if (!saveDetails.location || !saveDetails.uuid) {
            Notify.warning({ message: "Please save the metadata to a valid location!!!" });
            return;
        }
        shareRef.current = true;
        dispatch(fileBrowserActions.setShareModalVisibility());
    }
    let taskBarItems = []
    if (isSavingInProgress) {
        taskBarItems = [
            {
                icon: <LoadingOutlined />,
                tooltip: "Please wait while we save your metadata. This may take a few moments.",
            }
        ]
    }
    taskBarItems = [
        ...taskBarItems,
        {
            tooltip: 'Save', icon: <SaveOutlined />, dropdown: [
                { tooltip: 'Save', name: 'Save', icon: <SaveOutlined />, callBack: () => validateViewsForMultiConnections({ isSaveAction: true }) },
                { tooltip: 'Save As', name: 'Save As', icon: <SaveFilled />, callBack: () => validateViewsForMultiConnections({ isSaveAction: false }) }
            ]
        },
        {
            tooltip: "Share",
            icon: <HIIcon name="hi-share" testId='shareIconForShare' />,
            callBack: handleShare,
        },
        {
            tooltip: "Undo",
            icon: <UndoOutlined style={canUndo.length ? {} : { cursor: "not-allowed", opacity: 0.5 }} />,
            callBack: () => {
                if (canUndo) dispatch(metadataActions.metadataUndo());
            },
        },
        {
            tooltip: "Redo",
            icon: <RedoOutlined style={canRedo.length ? {} : { cursor: "not-allowed", opacity: 0.5 }} />,
            callBack: () => {
                if (canRedo) dispatch(metadataActions.metadataRedo())
            },
        }
    ]



    return (
        <React.Fragment>
            <HILayout
                header={<HINavbar taskbar={taskBarItems} />}
                content={
                    <MetadataLayout>
                        <HIMetadata handleShare={handleShare} filebrowserFor={filebrowserFor} setFilebrowserFor={setFilebrowserFor} saveType={saveType} openFB={openFB} onEditMetadata={onEditMetadata} isAlreadyFetched={isAlreadyFetched} />
                    </MetadataLayout>
                }
                defaultSidebar={false}
                customClass={" hi-p0"}
            />
            {shareRef?.current && (
                <ShareFinalModal
                    shareOptions={{
                        type: "file",
                        dir: saveDetails?.location,
                        file: saveDetails?.uuid,
                    }}
                />
            )}
        </React.Fragment>
    );

}

const HIMetadataPage = props => {
    const dispatch = useDispatch()
    const timeStamp = useSelector(state => state.metadata.present.timeStamp)
    const source = useSelector(state => state.app.editModeInfo?.source);
    React.useEffect(() => {
        return () => {
            dispatch(metadataActions.resetMetadataState({}))
            !(source) && dispatch(appActions.setEditModeInfo(false))
        }
    }, [])
    React.useEffect(() => {
        window.addEventListener("beforeunload", handleUnload);
        return () => {
            window.removeEventListener("beforeunload", handleUnload);
            dispatch(metadataActions.clearUndoRedoHistory())
        };
    }, []);

    const handleUnload = (e) => {
        const message = "o/";
        (e || window.event).returnValue = message; //Gecko + IE
        return message;
    };
    return < HIMetadataPageComponent {...props} key={timeStamp} />
}

export { HIMetadataPage };

