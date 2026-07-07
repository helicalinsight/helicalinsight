import React from "react";
import { Layout } from "antd";
const { Header, Content } = Layout;

const MetadataLayout = props => {
    const { children } = props
    return (
        <div className="hi-metadata-container">
            <Layout className="hi-metadata-body">
                <Content className="hi-metadata-metadata-section">
                    <div className="height100percent">
                        {children}
                    </div>
                </Content>
            </Layout>
        </div>
    )
}

export default MetadataLayout