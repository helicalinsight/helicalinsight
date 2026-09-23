import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from "uuid";
import { patchIBChatMessage, removeIBChatMessage, updateBIBotStatus, updateIBActivePreview, updateIBChatMessageList, updateIBPreviewData } from '../../../../redux/actions/instant-bi.actions';
import { isOpenSource } from '../../../../utils/utilities';
import Watermark from '../../../hi-reports/hi-viz-area/watermark/watermark';
import { getInstantBIAgentSubject, prepareIBChatNewMessage } from '../../utils/common-utils';
import { appendActivityLine, buildChatActivityLines } from '../../utils/chat-activity-script';
import { abortRecommendationsRequest, instantBiChatAPI } from '../../utils/instant-bi-requests';
import "./chat-screen.scss";
import MessageInputBoxNew, { resolveChatMode } from './message-input-box-new';
import MessageList from './message-list';

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
    const messageListRef = useRef(messageList);

    useEffect(() => {
        messageListRef.current = messageList;
    }, [messageList]);

    const { loading = false } = metadata || {};
    const dispatch = useDispatch();
    const { layout } = useSelector((state) => state.instantBI);
    const streamResponse = useSelector((state) => Boolean(state.app.applicationSettingsData?.streamResponse));
    const { metadataShelf, previewShelf } = layout || {};
    const [recommendation, setRecommendation] = useState('')
    const [chatMode, setChatMode] = useState('auto')
    const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || {}));
    const openSource = isOpenSource(metaInfo)

    const isFullWidth = (metadataShelf && previewShelf) || previewShelf;

    const patchThinkQuestionHistory = (planMessageId, questionIndex, updater) => {
        const current = (messageListRef.current || []).find((item) => item.id === planMessageId);
        if (!current) return;
        const baseHistory = (current.questionHistory || []).length
            ? current.questionHistory
            : (current.askedQuestions || []).map((question, index) => ({
                index: index + 1,
                question,
                title: "",
                analysis: "",
            }));
        const questionHistory = baseHistory.map((item) => {
            const matches = Number(item.index) === Number(questionIndex)
                || Number(item.index || 0) === Number(questionIndex);
            if (!matches) return item;
            const next = typeof updater === "function" ? updater(item) : { ...item, ...updater };
            // Never drop narrative fields while hydrating Preview.
            return {
                ...item,
                ...next,
                analysis: next.analysis || item.analysis || "",
                answer: next.answer || item.answer || "",
                purpose: next.purpose || item.purpose || "",
                question: next.question || item.question || "",
                title: next.title || item.title || "",
                chat_seq_id: next.chat_seq_id || item.chat_seq_id || "",
                report_model: next.report_model || item.report_model || {},
            };
        });
        dispatch(patchIBChatMessage({
            messageId: planMessageId,
            patch: { questionHistory },
            activeChatID,
            reportId,
        }));
        messageListRef.current = (messageListRef.current || []).map((item) =>
            item.id === planMessageId ? { ...item, questionHistory } : item
        );
    };

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
            botMessage: aiResponse = '',
            sql = '',
            createPreview,
            error = false,
            abortedRequest = false,
            userInput = '',
            chatSequenceId = 1,
            fullChatResponse = {},
            replaceMessageId,
            isThinkPlan = false,
            askedQuestions = [],
            questionHistory = [],
            citedQuestionIndexes = [],
            openingInsight = '',
            finalAnswer = '',
            phase = '',
            activityTrail = [],
            plan = {},
            llmActivityDetails = null,
            dashboardModel = null,
        } = res || {};
        if (abortedRequest) return;
        let message = prepareIBChatNewMessage(aiResponse)
        let newId = replaceMessageId || uuidv4();
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
            needsLoadChat: false,
            isStreaming: false,
            activityTrail: Array.isArray(activityTrail) ? activityTrail : [],
            isThinkPlan: Boolean(isThinkPlan),
            askedQuestions,
            questionHistory,
            citedQuestionIndexes,
            openingInsight: openingInsight || '',
            finalAnswer: finalAnswer || aiResponse || '',
            phase,
            plan: plan || {},
            llmActivityDetails: llmActivityDetails || null,
            dashboardModel: dashboardModel || null,
            text: isThinkPlan ? (finalAnswer || aiResponse || '') : message.text,
        }
        if (replaceMessageId) {
            dispatch(patchIBChatMessage({ messageId: replaceMessageId, patch: newMessage, activeChatID, reportId }))
            messageListRef.current = (messageListRef.current || []).map((item) =>
                item.id === replaceMessageId ? { ...item, ...newMessage } : item
            );
        } else {
            dispatch(updateIBChatMessageList({ newMessage, activeChatID, reportId }))
            messageListRef.current = [newMessage, ...(messageListRef.current || [])];
        }
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

    const prefersDetailTable = (stepItem = {}) => {
        const blob = [
            stepItem.viz_hint,
            stepItem.vizHint,
            stepItem.question,
            stepItem.title,
            stepItem.purpose,
        ]
            .filter(Boolean)
            .join(" ")
            .toLowerCase();
        const hint = String(stepItem.viz_hint || stepItem.vizHint || "").toLowerCase();
        if (["line", "area", "timeseries", "time_series", "trend"].includes(hint)) {
            return true;
        }
        return [
            "trend",
            "over time",
            "over date",
            "time series",
            "timeseries",
            "by date",
            "by month",
            "by week",
            "by day",
            "moved over",
            "detailed",
        ].some((token) => blob.includes(token));
    };

    const handleShowThinkQuestion = async (question, questionIndex, planMessage = {}, stepItem = {}) => {
        if (!question || !planMessage?.id) return;
        const planMessageId = planMessage.id;
        const showSeqId = stepItem.chat_seq_id
            || stepItem.chatSeqId
            || `${planMessage.chatSequenceId || planMessage.chat_seq_id || '1'}_${questionIndex}`;
        const reportModel = stepItem.report_model
            || stepItem.reportModel
            || stepItem.fullChatResponse?.report_model
            || stepItem.chat_response?.report_model
            || {};
        if (!reportModel || !Object.keys(reportModel).length) {
            patchThinkQuestionHistory(planMessageId, questionIndex, (item) => ({
                ...item,
                vizLoading: false,
                vizError: 'No report model available for this step.',
            }));
            return;
        }

        patchThinkQuestionHistory(planMessageId, questionIndex, (item) => ({
            ...item,
            vizLoading: true,
            vizError: '',
            vizActivityTrail: ['Preparing visualization from report model…'],
        }));

        try {
            const { hydrateThinkStepFromReportModel } = await import('../../utils/instant-bi-requests');
            const res = await hydrateThinkStepFromReportModel({
                stepItem,
                dispatch,
                activeReportId,
                chatSequenceId: showSeqId,
                userInput: question,
            });
            if (res?.error || !res?.fullChatResponse?.hreportId) {
                patchThinkQuestionHistory(planMessageId, questionIndex, (item) => ({
                    ...item,
                    vizLoading: false,
                    vizError: res?.botMessage || 'Unable to load visualization.',
                    vizActivityTrail: ['Visualization failed.'],
                }));
                return;
            }
            patchThinkQuestionHistory(planMessageId, questionIndex, (item) => ({
                ...item,
                vizLoading: false,
                vizError: '',
                fullChatResponse: res.fullChatResponse,
                chat_response: res.fullChatResponse,
                report_model: res.fullChatResponse?.report_model || reportModel,
                data: res.data,
                vf: res.vf,
                vizDetails: res.fullChatResponse?.viz || {},
                vizActivityTrail: ['Visualization ready.'],
            }));
        } catch (error) {
            patchThinkQuestionHistory(planMessageId, questionIndex, (item) => ({
                ...item,
                vizLoading: false,
                vizError: error?.message || 'Unable to load visualization.',
                vizActivityTrail: ['Visualization failed.'],
            }));
        }
    }

    const handleSendMessage = (message, mode = chatMode) => {
        const userMessages = messageList.filter(m => m.isUser);
        const chatSequenceId = userMessages.length + 1;
        const newMessage = prepareIBChatNewMessage(message, true)
        dispatch(updateIBChatMessageList({ newMessage, activeChatID, reportId }))
        const resolvedMode = resolveChatMode(mode);
        let formData = {
            input: message,
            mode: resolvedMode,
        }
        if (isMetadataPresent) {
            const model = getInstantBIAgentSubject(activeReport);
            if (model) {
                formData.subject = { model };
            }
        }
        abortedRef.current = false;
        const opening = buildChatActivityLines(message)[0];
        const isThink = resolvedMode === "think";
        const trailRef = { current: streamResponse ? [opening] : [] };
        const questionsRef = { current: [] };
        const placeholder = streamResponse
            ? {
                ...prepareIBChatNewMessage(""),
                isStreaming: true,
                activityTrail: trailRef.current,
                text: opening,
                userInput: message,
                chatSequenceId,
                isThinkPlan: isThink,
                askedQuestions: [],
                questionHistory: [],
                citedQuestionIndexes: [],
                openingInsight: "",
                finalAnswer: "",
            }
            : null;
        if (placeholder) {
            dispatch(updateIBChatMessageList({ newMessage: placeholder, activeChatID, reportId }))
            dispatch(updateBIBotStatus({ status: true, reportId: activeReportId, botMessage: opening }));
        }
        apiRef.current = instantBiChatAPI({
            formData,
            dispatch,
            onAIMessage: (res) => handleAIMessage({
                ...res,
                replaceMessageId: placeholder?.id,
                activityTrail: trailRef.current,
            }),
            onProgress: (progress) => {
                if (!placeholder) {
                    return;
                }
                const nextMessage = progress?.message;
                const stage = progress?.stage;
                if (stage === "auto" && /think/i.test(String(nextMessage || ""))) {
                    dispatch(patchIBChatMessage({
                        messageId: placeholder.id,
                        activeChatID,
                        reportId,
                        patch: { isThinkPlan: true, isStreaming: true },
                    }));
                }
                if (stage === "think_question" && nextMessage) {
                    const match = String(nextMessage).match(/^Question\s+(\d+)\s*:\s*(.*)$/i);
                    const index = match ? Number(match[1]) : questionsRef.current.length + 1;
                    const question = match ? match[2] : nextMessage;
                    const exists = questionsRef.current.some((item) => item.index === index);
                    if (!exists) {
                        questionsRef.current = [
                            ...questionsRef.current,
                            { index, question, title: "" },
                        ];
                    }
                    dispatch(patchIBChatMessage({
                        messageId: placeholder.id,
                        activeChatID,
                        reportId,
                        patch: {
                            isThinkPlan: true,
                            isStreaming: true,
                            askedQuestions: questionsRef.current.map((item) => item.question),
                            questionHistory: questionsRef.current,
                            text: trailRef.current.join("\n"),
                            activityTrail: trailRef.current,
                        },
                    }));
                }
                if (!nextMessage) {
                    return;
                }
                trailRef.current = appendActivityLine(trailRef.current, nextMessage);
                const latest = trailRef.current[trailRef.current.length - 1];
                dispatch(updateBIBotStatus({ status: true, reportId: activeReportId, botMessage: latest }));
                dispatch(patchIBChatMessage({
                    messageId: placeholder.id,
                    activeChatID,
                    reportId,
                    patch: {
                        text: trailRef.current.join("\n"),
                        activityTrail: trailRef.current,
                        isStreaming: true,
                        isThinkPlan: isThink || Boolean(questionsRef.current.length),
                        askedQuestions: questionsRef.current.map((item) => item.question),
                        questionHistory: questionsRef.current,
                    },
                }));
            },
            activeReportId,
            chatId: activeChatID,
            chatSequenceId,
            abortedRef,
        })
        if (apiRef.current && placeholder) {
            apiRef.current.placeholderId = placeholder.id;
        }
    }

    const handleAbortRequest = () => {
        abortedRef.current = true;
        const placeholderId = apiRef.current?.placeholderId;
        apiRef.current?.abort({
            setLoading: () => dispatch(updateBIBotStatus({ status: false, reportId: activeReportId })),
        });
        dispatch(updateBIBotStatus({ status: false, reportId: activeReportId }));
        if (placeholderId) {
            dispatch(removeIBChatMessage({ messageId: placeholderId, activeChatID, reportId }));
        }
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
                            isEditMode,
                            onShowThinkQuestion: handleShowThinkQuestion,
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
                            botMessage={activeReport?.botMessage || (chatMode === "think" ? "Thinking through your question" : chatMode === "auto" ? "Analyzing your question" : "Bot is Typing")}
                            activeReport={activeReport}
                            metadataLoading={loading}
                            chatMode={chatMode}
                            onChatModeChange={setChatMode}
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
            {openSource ?
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