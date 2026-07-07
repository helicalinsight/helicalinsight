
import { Layout } from 'antd';
import React from "react";

const BaseLayout = props => {
    return(
        <Layout>
            {props.children}
        </Layout>
    )
}

export default BaseLayout;

