import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from "uuid";
import { updateIBActivePreview, updateIBChatMessageList, updateIBPreviewData, updateBIBotStatus } from '../../../../redux/actions/instant-bi.actions';
import { getInstantBIAgentSubject, prepareIBChatNewMessage } from '../../utils/common-utils';
import { abortRecommendationsRequest, instantBiChatAPI } from '../../utils/instant-bi-requests';
import "./chat-screen.scss";
import MessageInputBoxNew from './message-input-box-new';
import MessageList from './message-list';
import Watermark from '../../../hi-reports/hi-viz-area/watermark/watermark';

const InstantBIChatScreen = (props = {}) => {
    const {
        metadata,
        isMetadataPresent,
        activeReportId,
        activeChatID,
        messageList = [],
        botStatus,
        reportId,
        activeReport,
        connectMetadata,
        isOpenMode,
        isEditMode
    } = props || {}

    let apiRef = useRef(null);
    const abortedRef = useRef(false);

    const { loading = false } = metadata || {};
    const dispatch = useDispatch();
    const { layout } = useSelector((state) => state.instantBI);
    const { metadataShelf, previewShelf } = layout || {};
    const [recommendation, setRecommendation] = useState('')
    const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || null));

    const isFullWidth = (metadataShelf && previewShelf) || previewShelf;

    const handlePreparePreviewData = (info = {}) => {
        const { reportId, metadata, data, vf, vf_title, activeChatID, id, ...rest } = info;
        let previewData = {
            dataId: uuidv4(),
            id,
            display: `Preview-${id.slice(0, 6)}`,
            vf_title,
            metadata,
            data,
            vf,
            // summary,
            chatID: activeChatID,
            ...rest
        }
        dispatch(updateIBPreviewData({ reportId, previewData }))
        dispatch(updateIBActivePreview({ reportId, previewID: id }))
    }

    const handleAIMessage = (res) => {
        const {
            data = [],
            vf = '',
            vf_title = '',
            metadata = [],
            sqlDetails = {},
            //  summary = '',
            // error = false,
            botMessage: aiResponse = '',
            sql = '',
            createPreview,
            // flow = [],
            error = false,
            abortedRequest = false,
            userInput = '',
            chatSequenceId = 1,
            fullChatResponse = {}
        } = res || {};
        if (abortedRequest) return;
        let message = prepareIBChatNewMessage(aiResponse)
        let newId = uuidv4();
        let newMessage = {
            ...message,
            data,
            vf,
            vf_title,
            metadata,
            id: newId,
            error,
            sql,
            userInput,
            chatSequenceId,
            fullChatResponse,
            needsLoadChat: false
        }
        dispatch(updateIBChatMessageList({ newMessage, activeChatID, reportId }))
        if (!error && createPreview) {
            handlePreparePreviewData({
                reportId,
                metadata,
                data,
                vf,
                vf_title,
                activeChatID,
                id: newId,
                sql,
                sqlDetails,
                vizDetails: fullChatResponse?.viz || {},
                tokenUsage: fullChatResponse?.token_usage || {},
            })
        }
    }

    const handleSendMessage = (message) => {
        const userMessages = messageList.filter(m => m.isUser);
        const chatSequenceId = userMessages.length + 1;
        const newMessage = prepareIBChatNewMessage(message, true)
        dispatch(updateIBChatMessageList({ newMessage, activeChatID, reportId }))
        let formData = {
            input: message,
        }
        if (isMetadataPresent) {
            const agent = getInstantBIAgentSubject(activeReport);
            if (agent) {
                formData.subject = { agent };
            }
        }
        abortedRef.current = false;
        apiRef.current = instantBiChatAPI({
            formData,
            dispatch,
            onAIMessage: handleAIMessage,
            activeReportId,
            chatId: activeChatID,
            chatSequenceId,
            abortedRef,
        })
    }

    const handleAbortRequest = () => {
        abortedRef.current = true;
        apiRef.current?.abort({
            setLoading: () => dispatch(updateBIBotStatus({ status: false, reportId: activeReportId })),
        });
        dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }));
        apiRef.current = null;
    }

    const handleAbortRecommendations = () => {
        abortRecommendationsRequest(activeReportId);
    }

    useEffect(() => {
        return () => {
            abortRecommendationsRequest(activeReportId);
        };
    }, [activeReportId]);

    const handleChangeRecommendation = (recommendation) => {
        setRecommendation('')
        setRecommendation(recommendation)
    }

    const chatContainerClassName = [
        'instant-bi-chat-container',
        isFullWidth ? 'instant-bi-chat-container--full-width' : '',
        !messageList?.length ? 'instant-bi-chat-container--centered' : '',
    ]
        .filter(Boolean)
        .join(' ');

    return (
        <div className='ib-chat-root-container'>
            <div className={chatContainerClassName}>
                <div className='instant-bi-message-list'>
                    <MessageList
                        messages={messageList}
                        {...{
                            activeReport,
                            dispatch,
                            reportId,
                            activeReportId,
                            activeChatID,
                            isMetadataPresent,
                            isFullWidth,
                            isOpenMode,
                            isEditMode
                        }}
                        onClick={handleChangeRecommendation}
                    />
                </div>
                {!isOpenMode && (
                    <div className='instant-bi-message-input'>
                        <MessageInputBoxNew
                            messages={messageList}
                            onSend={handleSendMessage}
                            botStatus={botStatus}
                            activeReport={activeReport}
                            metadataLoading={loading}
                            {...{
                                recommendation,
                                handleChangeRecommendation,
                                dispatch,
                                isMetadataPresent,
                                connectMetadata,
                                isOpenMode,
                                isEditMode,
                                metadata,
                                isFullWidth,
                                activeReportId
                            }}
                            onAbortRequest={handleAbortRequest}
                            onAbortRecommendations={handleAbortRecommendations}
                            onClickRecommendation={handleChangeRecommendation}
                        />
                    </div>
                )}
            </div>
            {metaInfo ?
                <Watermark
                    text={`Powered by ${metaInfo.productName}©${metaInfo.version}`}
                    link={metaInfo.link || "https://www.helicalinsight.com/"}
                    placement="bottom-right"
                    tooltip="Please upgrade your license to remove this watermark."
                    right={10}
                />
                : null}
        </div>
    )
}

export default InstantBIChatScreen