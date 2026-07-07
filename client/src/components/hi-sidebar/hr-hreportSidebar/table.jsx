
import {Typography,Tooltip } from "antd"
const { Text } = Typography;

const MetadataTable = props =>{
    let { item } = props
    return(
        <Tooltip title={item.alias} >
            <Text className="hr-metadata-ellipsis" ellipsis={true} data-testid={`tablename-${item.alias}`} >{item.alias}</Text>
        </Tooltip>
    )
}

export default MetadataTable;