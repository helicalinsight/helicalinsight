import { CloseOutlined } from '@ant-design/icons';
import { Drawer } from 'antd';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { copyToClipboard, JsonEditorPanel } from '../../../common/json-editor';
import { getDashboardForViewer, mergeJSONIntoDashboard } from './dashboard-json-utils';
import notify from '../../../hi-notifications/notify';
import { parseReportJsonText } from '../../../hi-reports/utils/hr-json-utils';
import { updateDashboardStateThroughEditor } from '../../../../redux/actions/dashboard-designer.actions';

const EMPTY_JSON = "{\n}\n";

const notifyFrontend = (dispatch, level, message) =>
    notify(dispatch)[level]({ type: "Frontend", message });

const DashboardJsonEditor = (props = {}) => {
    const {
        visible,
        onCloseDrawer = () => { },
    } = props || {}

    const [jsonText, setJsonText] = useState(EMPTY_JSON);
    const [hasUnsavedJsonChanges, setHasUnsavedJsonChanges] = useState(false);
    const skipJsonSyncRef = useRef(false);
    const lastSerializedJsonRef = useRef("");

    const designerState = useSelector((state) => state.designer.present);
    const dispatch = useDispatch();

    const handleJsonChange = (text) => {
        setJsonText(text ?? "");
        setHasUnsavedJsonChanges(true);
    };

    const handleApplyJson = () => {
        try {
            const parsedReportState = parseReportJsonText(jsonText);
            const mergedDashboard = mergeJSONIntoDashboard(
                designerState,
                parsedReportState,
            );
            const serializedJson = getDashboardForViewer(mergedDashboard);
            lastSerializedJsonRef.current = serializedJson;
            setJsonText(serializedJson);
            setHasUnsavedJsonChanges(false);
            dispatch(updateDashboardStateThroughEditor(mergedDashboard));

            notifyFrontend(
                dispatch,
                "success",
                "Canned Report Spec updated successfully",
            );
        } catch (error) {
            notifyFrontend(dispatch, "error", `Invalid SPEC: ${error.message}`);
        }
    }

    const handleCopyJson = async () => {
        try {
            await copyToClipboard(jsonText);
            notifyFrontend(dispatch, "success", "Copied JSON to clipboard");
        } catch {
            notifyFrontend(dispatch, "error", "Failed to copy JSON");
        }
    }

    useEffect(() => {
        if (hasUnsavedJsonChanges || skipJsonSyncRef.current) {
            return;
        }
        const nextJsonText = designerState
            ? getDashboardForViewer(designerState)
            : EMPTY_JSON;
        if (nextJsonText === lastSerializedJsonRef.current) {
            return;
        }
        skipJsonSyncRef.current = true;
        lastSerializedJsonRef.current = nextJsonText;
        setJsonText(nextJsonText);
        setHasUnsavedJsonChanges(false);
        requestAnimationFrame(() => {
            skipJsonSyncRef.current = false;
        });
    }, [designerState, hasUnsavedJsonChanges]);

    return (
        <Drawer
            title="Dashboard Spec"
            placement="right"
            width="45%"
            visible={visible}
            onClose={onCloseDrawer}
            className="dashboard-json-editor-drawer"
            closeIcon={<CloseOutlined data-testid="dashboard-json-editor-close-icon" />}
            destroyOnClose
        >
            <div
                className="dashboard-json-editor-drawer-body"
                data-testid="dashboard-json-editor-drawer-body"
            >
                <JsonEditorPanel
                    value={jsonText}
                    onChange={handleJsonChange}
                    onSave={handleApplyJson}
                    onCopy={handleCopyJson}
                    hasUnsavedChanges={hasUnsavedJsonChanges}
                    saveTitle="Apply Changes, Note: Please do not change name of any key, as it may break the report."
                />
            </div>
        </Drawer>
    )
}

export default DashboardJsonEditor