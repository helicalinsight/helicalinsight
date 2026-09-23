import { CopyOutlined, DownloadOutlined, EyeOutlined, FileTextOutlined, LoadingOutlined, SyncOutlined } from '@ant-design/icons';
import Editor from '@monaco-editor/react';
import { Button, Drawer, Empty, Radio, Row, Spin, Tabs, Tooltip } from 'antd';
import Parser from 'html-react-parser';
import { isEqual } from 'lodash-es';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import requests from '../../base/requests';
import { isOpenSource } from '../../utils/utilities';
import { getCanvasDimensions } from '../common/custom-icons/CustomSkeletons/cannedReports/customCannedSkeleton';
import notify from '../hi-notifications/notify';
import Watermark from '../hi-reports/hi-viz-area/watermark/watermark';
import ToolbarIconButton from '../common/toolbar-icon-button';
import "../common/json-editor/json-editor.scss";

const JRXMLViewer = (props) => {
    const { activeReport, showJRXML, onClose = () => { } } = props || {};
    const { jrxmlPath } = activeReport || {};

    const [jrxml, setJrxml] = useState("")
    const [loading, setLoading] = useState(false);
    const dispatch = useDispatch();
    const { getJRXMLData } = requests.cannedReport(dispatch);
    const Notify = notify(dispatch);

    useEffect(() => {
        if (jrxmlPath) {
            setLoading(true);
            getJRXMLData(jrxmlPath, "", (jrxmlRes) => {
                let jrxml = jrxmlRes;
                setJrxml(jrxml);
                setLoading(false);
            }, (err) => {
                setLoading(false);
                Notify.error(err?.message || 'Failed to load JRXML');
            });
        }
    }, [jrxmlPath])

    const onCopy = () => {
        if (jrxml) {
            navigator.clipboard.writeText(jrxml);
        }
    }

    const onDownload = () => {
        if (jrxml) {
            const element = document.createElement("a");
            element.setAttribute("href", "data:text/plain;charset=utf-8," + encodeURIComponent(jrxml));
            element.setAttribute("download", (activeReport?.title || "Untitled 1") + ".jrxml");
            element.style.display = "none";
            document.body.appendChild(element);
            element.click();
            document.body.removeChild(element);
        }
    }

    const renderJRXML = () => {
        return (
            <div className="hcr-preview-jrxml-viewer json-editor-panel">
                <div className="json-editor-container">
                    {jrxml &&
                        <>
                            <div className="json-editor-panel-toolbar">
                                <ToolbarIconButton key={"Copy"} title={"Copy"} onClick={onCopy}>
                                    <CopyOutlined className="json-editor-panel-icon" />
                                </ToolbarIconButton>
                                <ToolbarIconButton key={"Download JRXML"} title={"Download JRXML"} onClick={onDownload}>
                                    <DownloadOutlined className="json-editor-panel-icon" />
                                </ToolbarIconButton>
                            </div>
                            <Editor
                                height="90vh"
                                defaultLanguage="xml"
                                value={jrxml}
                                options={{ readOnly: true, minimap: { enabled: false } }}
                            />
                        </>
                    }
                </div>
            </div>
        )
    }

    return (
        <Drawer
            title="JRXML"
            width={"45%"}
            onClose={onClose}
            visible={showJRXML}
            bodyStyle={{ paddingBottom: 80 }}
        >
            {loading ? <Spin indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />} /> : renderJRXML()}
        </Drawer>
    )


}


const PreviewArea = (props = {}) => {
    const { previewTag, flowchartInstance, setIsPreviewLoading, isPreviewLoading, reportMode, setAppliedFilters, urlParameters } = props || {};
    // const [filterDragObj, setFilterDragObj] = useState({ x: 0, y: (window.screen.availHeight - 45) / 2 });
    const activeReport = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey), isEqual) || {};
    const { enableJRXML } = activeReport || {};
    const hcrTabData = useSelector(state => state.cannedReports.present.hcrTabData);
    const canvasProperties = useSelector(state => state.cannedReports.present.hcrTabData?.panes?.find(pane => pane.key === hcrTabData.activeKey)?.canvasProperties || {});
    const dimensions = getCanvasDimensions(canvasProperties);
    const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || {}));
    const openSource = isOpenSource(metaInfo)

    return (
        <div>
            {!isPreviewLoading ? (
                <div className='preview-hcr-nodes'>
                    {previewTag ? (
                        Parser(previewTag)
                    ) : (
                        <div
                            className="a4"
                            style={{ width: `${dimensions.width}px`, height: `${dimensions.height}px` }}
                        />
                    )}
                    {openSource ?
                        <Watermark
                            text={`Powered by ${metaInfo.productName}©${metaInfo.version}`}
                            link={metaInfo.link || "https://www.helicalinsight.com/"}
                            placement="bottom-right"
                            tooltip="Please upgrade your license to remove this watermark."
                        />
                        : null}
                </div>
            ) : (
                <Empty className='edit-section-lazy-loading-spinner' image={null} description={null}>
                    <Spin indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />} />
                </Empty>
            )}

            {enableJRXML && <JRXMLViewer activeReport={activeReport} showJRXML={props.showJRXML} onClose={props.onClose} />}
        </div>
    );
}

export { PreviewArea };
