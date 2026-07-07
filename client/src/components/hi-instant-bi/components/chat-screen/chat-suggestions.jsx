import { MessageOutlined,ThunderboltOutlined } from '@ant-design/icons';
import { Skeleton } from 'antd';
import LoadingBar from '../../../common/components/hi-loading-bar';
import './chat-suggestions.scss';

const ChatSuggestions = ({
    loadingRecommendation = false,
    recommendations = [],
    onClickRecommendation = () => { },
    handleSendMessage = () => {},
    botStatus = false,
    onAbortRecommendations = () => { },
}) => {
    let suggestions = recommendations?.slice(0, 3) ?? [];
    return (
        <div className="chat-suggestions">
            <div className="chat-suggestions__header">
                <span className="chat-suggestions__icon"><ThunderboltOutlined style={{color:"#6b7280"}}/></span>
                <span className="chat-suggestions__label">Suggested</span>
            </div>

            {loadingRecommendation ? (
                <>
                   <div className="chat-suggestions__loading-bar">
                        <LoadingBar handleClick={onAbortRecommendations} />
                    </div>
                    {[1, 2, 3].map((_, index) => (
                        <Skeleton.Input
                            size='small'
                            key={index}
                            active
                            className='skeleton'
                            style={{
                                width: index === 0 ? '25%' : index === 1 ? '35%' : '45%'
                            }}
                        />
                    ))}
                </>
            ) : (
                suggestions?.map((item, index) => {
                    const title = typeof item === 'string' ? item : item?.title;
                    return (
                      <div
                        key={index}
                        className="suggestion-item"
                        style={{ animationDelay: `${index * 0.25}s` }}
                        onClick={() => {
                          if (botStatus) return;
                          onClickRecommendation(item);
                          const title =
                            typeof item === "string" ? item : item?.title;
                          handleSendMessage(title);
                        }}
                      >
                        <span className="suggestion-item__title">
                          <MessageOutlined className="suggestion-item__icon" />
                          {title}
                        </span>
                      </div>
                    );
                })
            )}
        </div>
    );
};

export default ChatSuggestions;
