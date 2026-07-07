



import React from "react";
import { Layout, Menu } from 'antd';
const { Header, Content } = Layout;


const LoginLayout = props => {
    const { children } = props
    return (
        <div>
            {children}
        </div>
    )
}

export default LoginLayout

