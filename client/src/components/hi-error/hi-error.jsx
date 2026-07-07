
import React from "react";
import { Typography } from "antd"
const  { Text } =
 Typography

const HIErrorPage = props =>{
    return(
        <div>
            <Text type="warning">This url is not valid</Text>
        </div>
    )
}

export { HIErrorPage };