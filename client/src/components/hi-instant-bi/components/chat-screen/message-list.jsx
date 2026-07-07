import { ArrowDownOutlined, LoadingOutlined } from '@ant-design/icons';
import { Row, Spin } from 'antd';
import { useEffect, useRef, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import MessageLayout from './message-layout';
import MetadataNotConnected from './metadata-not-connected';
import { useSequentialChatLoad } from '../../utils/use-sequential-chat-load';

const MessageList = (props = {}) => {
    const {
        messages = [],
        isMetadataPresent,
        isFullWidth,
        isOpenMode,
        isEditMode,
        activeReport,
        dispatch,
        reportId,
    } = props

    const {
        requestLoad,
        retryLoad,
        loadingSequenceId,
        skippedSequenceIds,
        abortedSequenceIds,
        abortLoadChat,
    } = useSequentialChatLoad({
        messages,
        activeReport,
        reportId,
        dispatch,
        isOpenMode,
        isEditMode,
    });

    const [showScrollToBottom, setShowScrollToBottom] = useState(false);
    const chatEndRef = useRef(null);
    const scrollableRef = useRef(null);

    const handleLoadMore = () => { }

    const scrollToBottom = () => {
        chatEndRef.current?.scrollIntoView({ behavior: 'smooth', block: 'end' });
    };

    const handleScroll = () => {
        const el = scrollableRef.current;
        if (!el) return;
        const isNearBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 100;
        setShowScrollToBottom(!isNearBottom);
    };

    const resetScrollShow = () => {
        setShowScrollToBottom(false);
    }

    useEffect(() => {
        let tm = setTimeout(() => {
            clearTimeout(tm);
            scrollToBottom();
        },100)
    }, [messages.length]);

    if (!isMetadataPresent) return <MetadataNotConnected />

    if (!messages.length) return null;

    return (
        <div
            id="scrollableDiv"
            className="instant-bi-chat-message-box"
            ref={scrollableRef}
            onScroll={isOpenMode ? undefined : handleScroll}
        >
            <InfiniteScroll
                dataLength={messages.length}
                next={handleLoadMore}
                hasMore={false}
                style={{
                    display: "flex",
                    flexDirection: "column-reverse",
                    overflow: isOpenMode ? "visible" : "hidden",
                }}
                loader={
                    <Row
                        align="middle"
                        justify="center"
                        style={{ padding: "5px" }}
                    >
                        <Spin
                            tip="Loading"
                            size="large"
                            indicator={<LoadingOutlined />}
                        />
                    </Row>
                }
                inverse={false}
                scrollableTarget="scrollableDiv"
            >
                {messages &&
                    messages.map((item, index) => (
                        <MessageLayout
                            chatItem={item}
                            // key={item.message_id + "-" + index}
                            key={item.id}
                            index={index}
                            {...item}
                            {...{
                                isFullWidth,
                                isOpenMode,
                                isEditMode,
                                activeReport,
                                dispatch,
                                scrollableRootRef: isOpenMode ? null : scrollableRef,
                                onRequestChatLoad: requestLoad,
                                onRetryLoad: retryLoad,
                                loadingChatSequenceId: loadingSequenceId,
                                onAbortChatLoad: abortLoadChat,
                                skippedSequenceIds,
                                abortedSequenceIds,
                            }}
                            handleScroll={resetScrollShow}
                        />
                    ))}
            </InfiniteScroll>
            <div ref={chatEndRef} />
            {showScrollToBottom && (
                <ArrowDownOutlined className='scroll-to-bottom' onClick={scrollToBottom} style={{ fontSize: '20px' }} />
            )}
        </div>
    );
}

export default MessageList 