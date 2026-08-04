import { Row } from 'antd';
import { useEffect, useState } from 'react';
import Markdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import InstantBITooltip from '../../instant-bi-tooltip-title';
import { tabItems, ChartView, isIbTableChart } from '../../utils/common-utils';
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
    const [activeTab, setActiveTab] = useState("preview");
    const userMessageCount = messageList.filter((message) => message.isUser).length;

    useEffect(() => {
        setActiveTab("preview");
    }, [previewId, userMessageCount]);

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

            <div className='ib-preview-area'>
                {visible ? (
                    <>
                        {activeTab === "preview" && (
                            isTableChart ? (
                                <div className="json-data-viewer">
                                    <CommonMarkdownTable data={data || []} />
                                </div>
                            ) : (
                                <ChartView
                                    data={data}
                                    vf={code}
                                    id={dataId || previewId}
                                    chartName={vizDetails?.chart_name || ""}
                                    className="chart-wrapper--preview-panel"
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