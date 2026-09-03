import { ArrowDownOutlined, LoadingOutlined } from '@ant-design/icons';
import { Row, Spin } from 'antd';
import { useEffect, useRef, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { useSequentialChatLoad } from '../../utils/use-sequential-chat-load';
import MessageLayout from './message-layout';
import MetadataNotConnected from './metadata-not-connected';

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
    const stickToBottomRef = useRef(true);

    const handleLoadMore = () => { }

    const scrollToBottom = (smooth = false) => {
        const el = scrollableRef.current;
        if (el) {
            el.scrollTop = el.scrollHeight;
        }
        chatEndRef.current?.scrollIntoView({
            behavior: smooth ? 'smooth' : 'auto',
            block: 'end',
        });
    };

    const handleScroll = () => {
        const el = scrollableRef.current;
        if (!el) return;
        const isNearBottom = el.scrollHeight - el.scrollTop - el.clientHeight < 120;
        stickToBottomRef.current = isNearBottom;
        setShowScrollToBottom(!isNearBottom);
    };

    const resetScrollShow = () => {
        stickToBottomRef.current = true;
        setShowScrollToBottom(false);
        scrollToBottom(true);
    }

    useEffect(() => {
        if (!messages.length) return undefined;
        stickToBottomRef.current = true;
        const t = setTimeout(() => scrollToBottom(false), 50);
        return () => clearTimeout(t);
    }, [messages.length]);

    useEffect(() => {
        const el = scrollableRef.current;
        if (!el || !messages.length || typeof ResizeObserver === 'undefined') {
            return undefined;
        }
        const content = el.firstElementChild;
        if (!content) return undefined;
        const observer = new ResizeObserver(() => {
            if (stickToBottomRef.current) {
                scrollToBottom(false);
            }
        });
        observer.observe(content);
        return () => observer.disconnect();
    }, [messages.length]);

    //  (open/edit file)
    useEffect(() => {
        if (!stickToBottomRef.current) return undefined;
        const t = setTimeout(() => scrollToBottom(false), 100);
        return () => clearTimeout(t);
    }, [loadingSequenceId]);

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
                <ArrowDownOutlined className='scroll-to-bottom' onClick={() => resetScrollShow()} style={{ fontSize: '20px' }} />
            )}
        </div>
    );
}

export default MessageList