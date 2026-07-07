import { Row } from 'antd';
import { useEffect, useRef, useState } from 'react';
import Markdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import IBCustomChart from '../ib-custom-chart';
import InstantBITooltip from '../../instant-bi-tooltip-title';
import { tabItems, isIbKpiChart, isIbTableChart } from '../../utils/common-utils';
import CommonMarkdownTable from '../../utils/common-markdown-table';
import InstantBIResponseMetadata from '../instant-bi-response-metadata';

const InstantBIPreviewContent = (props) => {
    const {
        messageList = [],
        activePreview: {
            id: previewId = '',
            vf: code = '',
            data = [],
            dataId,
            sql = '',
            sqlDetails = {},
            vizDetails = {},
            tokenUsage = {},
        } = {}
    } = props || {}
    const [chartAreaWidth, setChartAreaWidth] = useState(10);
    const [chartAreaHeight, setChartAreaHeight] = useState(10);
    const [activeTab, setActiveTab] = useState("preview");
    const userMessageCount = messageList.filter((message) => message.isUser).length;

    const chartAreaRef = useRef();

    useEffect(() => {
        setActiveTab("preview");
    }, [previewId, userMessageCount]);

    useEffect(() => {
        function outputsize() {
            if (chartAreaRef?.current?.parentNode) {
                let parentNode = chartAreaRef.current.parentNode;
                if (parentNode.offsetHeight - 10 !== chartAreaHeight) {
                    setChartAreaHeight(parentNode.offsetHeight - 10);
                }
                if (parentNode.offsetWidth - 5 !== chartAreaWidth) {
                    setChartAreaWidth(parentNode.offsetWidth - 5);
                }
            }
        }

        if (
            chartAreaRef?.current?.parentNode &&
            process.env.NODE_ENV !== "test"
        ) {
            new ResizeObserver(outputsize).observe(
                chartAreaRef.current.parentNode
            );
        }
    }, [chartAreaRef?.current]);

    const visible = !(!code || !data);
    const isTableChart = isIbTableChart(vizDetails?.chart_name, code);

    return (
        <div className='ib-preview-container'>
            {visible && (
                <Row justify={"end"}>
                    <div className="icon-tabs-container-preview">
                        {tabItems.map((item) => (
                            <InstantBITooltip
                                key={item.key}
                                title={item.title}
                            >
                                <button
                                    className={`icon-tab-btn ${
                                        activeTab === item.key ? "active" : ""
                                    }`}
                                    onClick={() => {
                                        setActiveTab(item.key);
                                    }}
                                >
                                    {item.icon}
                                </button>
                            </InstantBITooltip>
                        ))}
                    </div>
                </Row>
            )}

            <div ref={chartAreaRef} className='ib-preview-area'>
                {visible ? (
                    <>
                        {activeTab === "preview" && (
                            isTableChart ? (
                                <div className="json-data-viewer">
                                    <CommonMarkdownTable data={data || []} />
                                </div>
                            ) : (
                                <IBCustomChart
                                    data={data}
                                    showToolbar={false}
                                    customChart={{
                                        code: code
                                    }}
                                    {...{
                                        chartAreaHeight,
                                        chartAreaWidth,
                                        dataId
                                    }}
                                    autoFit={true}
                                    isKpiChart={isIbKpiChart(vizDetails?.chart_name, code)}
                                />
                            )
                        )}

                        {activeTab === "sql" && (
                            <Markdown remarkPlugins={[remarkGfm]}>
                                {sql}
                            </Markdown>
                        )}

                        {activeTab === "data" && (
                            <div className='json-data-viewer'>
                              <CommonMarkdownTable data={data || []} />
                            </div>
                        )}

                        {activeTab === "metadata" && (
                            <div className='json-data-viewer'>
                                <InstantBIResponseMetadata
                                    sqlDetails={sqlDetails}
                                    vizDetails={vizDetails}
                                    tokenUsage={tokenUsage}
                                />
                            </div>
                        )}
                    </>
                ) : null}
            </div>
        </div>
    );
};

export default InstantBIPreviewContent;