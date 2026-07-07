import { LoadingOutlined } from '@ant-design/icons';
import { Empty, Spin } from 'antd';
import Parser from 'html-react-parser';
import { isEqual } from 'lodash-es';
import { useSelector } from 'react-redux';
import { getCanvasDimensions } from '../common/custom-icons/CustomSkeletons/cannedReports/customCannedSkeleton';
import Watermark from '../hi-reports/hi-viz-area/watermark/watermark';

const PreviewArea = ({ previewTag, flowchartInstance, setIsPreviewLoading, isPreviewLoading, reportMode, setAppliedFilters, urlParameters }) => {
    // const [filterDragObj, setFilterDragObj] = useState({ x: 0, y: (window.screen.availHeight - 45) / 2 });
    const activeReport = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey), isEqual) || {};
    const { hcrFiltersDrawerStatus } = activeReport || {};
    const hcrTabData = useSelector(state => state.cannedReports.present.hcrTabData);
    const canvasProperties = useSelector(state => state.cannedReports.present.hcrTabData?.panes?.find(pane => pane.key === hcrTabData.activeKey)?.canvasProperties || {});
    const dimensions = getCanvasDimensions(canvasProperties);
    const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || null));

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
                    {metaInfo ?
                        <Watermark
                            text={"Helical Insight"}
                            link={"https://www.helicalinsight.com/"}
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
        </div>
    );
}

export { PreviewArea };
