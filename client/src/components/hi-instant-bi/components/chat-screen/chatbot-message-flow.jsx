import { InfoCircleOutlined } from '@ant-design/icons';
import { Popover } from 'antd';
import { useState } from 'react';
import "./chatbot-message-flow.scss"

const ChatBotMessageFlow = (props) => {
    const { flow = [] } = props || {}
    const [open, setOpen] = useState(false);

    // const hide = () => {
    //     setOpen(false);
    // };

    const handleOpenChange = (newOpen) => {
        setOpen(newOpen);
    };

    const content = (
        <ul>
            {flow.map((item, index) => <li key={index}>{item}</li>)}
        </ul>
    );


    return (
        <Popover
            content={content}
            title={"Message Flow"}
            trigger="click"
            open={open}
            onOpenChange={handleOpenChange}
            placement='topRight'
            overlayClassName="chatbot-message-flow-popover"
        >
            <InfoCircleOutlined style={{ cursor: 'pointer', fontSize: '12px' }} />
        </Popover>
    )
}

export default ChatBotMessageFlow