import { LoadingOutlined, SendOutlined, BulbOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { Select, Tooltip } from 'antd';
import { useEffect, useState } from 'react';
import { changeIBInputValue, updateInstantBILayout, updateRecommendationsVisibility } from '../../../../redux/actions/instant-bi.actions';
import BeatLoader from './beat-loader';
import ChatSuggestions from './chat-suggestions';
import "./message-input-box-new.scss";
import InstantBITooltip from '../../instant-bi-tooltip-title';
import notify from '../../../hi-notifications/notify';
import AiDisclaimer from './ai-disclaimer';
import TutorialInfo from '../../../common/hi-tutorial';
import { CustomIcon } from '../../../common/custom-icons/CustomIcon';

const BrainIcon = (props = {}) => (
    <svg
        viewBox="0 0 24 24"
        width="1em"
        height="1em"
        fill="none"
        stroke="currentColor"
        strokeWidth="1.8"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
        {...props}
    >
        <path d="M9.5 4.5a3.5 3.5 0 0 0-3.4 4.2A3.5 3.5 0 0 0 4 12a3.5 3.5 0 0 0 2.2 3.2A3.5 3.5 0 0 0 9.5 19.5h1" />
        <path d="M14.5 4.5a3.5 3.5 0 0 1 3.4 4.2A3.5 3.5 0 0 1 20 12a3.5 3.5 0 0 1-2.2 3.2A3.5 3.5 0 0 1 14.5 19.5h-1" />
        <path d="M12 4.5v15" />
        <path d="M9.5 9h5" />
        <path d="M9.5 13h5" />
    </svg>
);

const CHAT_MODE_OPTIONS = [
    {
        value: "auto",
        title: "Auto",
        icon: <ThunderboltOutlined />,
    },
    {
        value: "fast",
        title: "Fast",
        icon: <BulbOutlined />,
    },
    {
        value: "think",
        title: "Think",
        icon: <BrainIcon />,
    },
];

const chatModeSelectOptions = CHAT_MODE_OPTIONS.map((option) => ({
    value: option.value,
    label: (
        <span className="ib-chat-mode-option">
            {option.icon}
            <span className="ib-chat-mode-option__label">{option.title}</span>
        </span>
    ),
}));

const CHAT_MODES = CHAT_MODE_OPTIONS.map((option) => option.value);

export const resolveChatMode = (mode) => {
    const value = String(mode || "").toLowerCase();
    return CHAT_MODES.includes(value) ? value : "auto";
};

const MessageInputBoxNew = (props = {}) => {
    const {
        onSend = () => { },
        botStatus,
        botMessage = '',
        recommendation = '',
        activeReport: {
            recommendationsVisible = false,
            loadingRecommendation = false,
            recommendations = [],
            inputValue,
            subject = {},
        },
        handleChangeRecommendation = () => { },
        dispatch,
        isMetadataPresent,
        connectMetadata = () => { },
        metadataLoading,
        onClickRecommendation = () => { },
        isOpenMode,
        isEditMode,
        metadata,
        onAbortRequest = () => { },
        onAbortRecommendations = () => { },
        isFullWidth,
        activeReportId,
        messages = [],
        chatMode = "auto",
        onChatModeChange = () => { },
    } = props || {}
    const [rows, setRows] = useState(1)
    const Notify = notify(dispatch);
    const setInputValue = (value) => {
        dispatch(changeIBInputValue({ value }))
    }
    const handleOpenMetadataShelf = () => {
        dispatch(updateInstantBILayout({ key: "metadataShelf", value: true }))
    }

    const handleChange = (e) => {
        const { value } = e?.target || {}
        setInputValue(value)
    }

    const handleSendMessage = (message) => {
        if (!message.length || botStatus) return;
         if (!isMetadataPresent) {
           Notify.warning({
             type: "Frontend",
             message: "Please select/connect a semantic model before sending message.",
           });
           return;
         }
        onSend(message, resolveChatMode(chatMode))
        setInputValue('')
        if (recommendationsVisible) {
            dispatch(updateRecommendationsVisibility({ visible: false, reportId: activeReportId }))
            handleChangeRecommendation("")
        }
    }
    const connectedAgentName = subject?.model?.file ?? subject?.agent?.file ?? metadata?.data?.modelName ?? "";
    const metadataContainer = (
      <div className="left-icons-container">
        {!isOpenMode && (
          <>
            <TutorialInfo elementKey="hi-instant-bi-agent-icon" placement="topLeft">
              <InstantBITooltip
                title={
                  !isMetadataPresent
                    ? "Connect your Semantic Model to start using Instant BI"
                    : `Chatting with: ${connectedAgentName}`
                }
              >
                <button
                  className="ib-metadata-button"
                  data-testid="hi-instant-bi-agent-icon"
                  onClick={() => {
                    if (!isMetadataPresent) {
                      connectMetadata();
                      return;
                    }
                    // handleOpenMetadataShelf()
                    return;
                  }}
                >
                  {!isMetadataPresent && (
                    <div className="ib-metadata-not-present-dot" />
                  )}
                  {!metadataLoading ? (
                    <span
                      className={
                        !isMetadataPresent ? "" : "instant-bi-metadata-selected"
                      }
                    >
                      <CustomIcon name="Cube" />
                    </span>
                  ) : (
                    <LoadingOutlined />
                  )}
                </button>
              </InstantBITooltip>
            </TutorialInfo>
          </>
        )}
      </div>
    );

    const sendButtonContainer = (
        <div className="ib-action-buttons">
            {botStatus ? (
                <InstantBITooltip title="Abort this request">
                    <button
                        className="icon-button ib-metadata-button stop-button"
                        onClick={() => onAbortRequest()}
                        data-testid="ib-chat-abort-button"
                        aria-label="Abort this request"
                    >
                        <span className="ib-stop-icon" aria-hidden="true" />
                    </button>
                </InstantBITooltip>
            ) : (
                <TutorialInfo elementKey="hi-instant-bi-send-query" placement="topRight">
                    <InstantBITooltip title={`Press Ctrl+Enter or Shift+Enter to add a new line or Press Enter to send.`} >
                        <button
                            className="icon-button ib-metadata-button send-button"
                            onClick={() => handleSendMessage(inputValue)}
                            data-testid="ib-chat-send-button"
                        >
                            <SendOutlined size={20} /> {'  '}
                        </button>
                    </InstantBITooltip>
                </TutorialInfo>
            )}
        </div>
    )

    useEffect(() => {
        if (recommendation?.length) {
            setInputValue(recommendation)
        }
    }, [recommendation])

    useEffect(() => {
        if (inputValue) {
            const lineBreakCount = (inputValue.match(/\n/g) || []).length;
            if (lineBreakCount > 0 && lineBreakCount <= 5) {
                setRows(lineBreakCount)
            }
            if (lineBreakCount === 0) setRows(1)
        } else {
            setRows(1)
        }
    }, [inputValue])

    return (
        <div className="ib-chat-container">
              {botStatus ?
                <Tooltip title={"click to abort this request"}>
                    <div className='instant-bi-beat-loader' onClick={() => onAbortRequest()}>
                        <span>{botMessage || "Let me find out what I can do…"}</span>
                        <BeatLoader />
                    </div>
                </Tooltip> : null}

            <div className="search-section" >
                <TutorialInfo elementKey="hi-instant-bi-editor-pane" placement="top">
                <div
                    // className={`search-container ${isFullWidth ? "no-border-shadow" : ""}`}
                    className={`search-container`}
                    data-testid="hi-instant-bi-editor-pane"
                >
                    <div
                        //  className={`search-input-wrapper ${isFullWidth ? "no-bottom-margin" : ""}`}
                        className={`search-input-wrapper`}
                    >
                        {/* {isFullWidth ? metadataContainer : null} */}
                        <textarea
                            type="text"
                            id="ib-search-input"
                            placeholder="Ask anything about your data here..."
                            value={inputValue}
                            onChange={handleChange}
                            rows={rows}
                            className="search-input"
                            // className={"search-input" + (!isMetadataPresent ? " input-box-disabled" : "")}
                            onKeyDown={(e) => {
                                if (e.key === "Enter" && !botStatus) {
                                    if (e.shiftKey) {
                                        return;
                                    }
                                    if (e.ctrlKey) {
                                        setInputValue(inputValue + "\n")
                                        return;
                                    }
                                    e.preventDefault();
                                    handleSendMessage(inputValue)
                                    return;
                                }
                            }}
                            disabled={botStatus}
                        />
                        {/* {isFullWidth ? sendButtonContainer : null} */}
                    </div>

                    <div className="action-buttons-row">
                        <div className="ib-chat-mode-and-meta">
                            {metadataContainer}
                            <InstantBITooltip
                                title={
                                    CHAT_MODE_OPTIONS.find(
                                        (option) => option.value === resolveChatMode(chatMode)
                                    )?.title || "Chat mode"
                                }
                            >
                                <span className="ib-chat-mode-select-wrap">
                                    <Select
                                        className="ib-chat-mode-select"
                                        value={resolveChatMode(chatMode)}
                                        onChange={(value) => onChatModeChange(value)}
                                        disabled={botStatus}
                                        options={chatModeSelectOptions}
                                        dropdownClassName="ib-chat-mode-select-dropdown"
                                        data-testid="ib-chat-mode-select"
                                        aria-label="Chat mode"
                                        dropdownMatchSelectWidth={false}
                                        showArrow={false}
                                        suffixIcon={null}
                                    />
                                </span>
                            </InstantBITooltip>
                        </div>
                        {sendButtonContainer}
                    </div>

                    {/* {!isFullWidth ? <div className="action-buttons-row">
                        {metadataContainer}
                        {sendButtonContainer}
                    </div> : null} */}
                </div>
                </TutorialInfo>
            </div>

            {messages?.length > 0 && (
                <AiDisclaimer className="ib-ai-disclaimer--editor" />
            )}

            {/* chat suggestions */}
            {((recommendationsVisible || loadingRecommendation) && !isEditMode) && (
                <ChatSuggestions
                    {...{
                        loadingRecommendation,
                        recommendations,
                        onClickRecommendation,
                        handleSendMessage,
                        botStatus,
                        onAbortRecommendations,
                    }}
                />
            )}
        </div >
    )
}

export default MessageInputBoxNew