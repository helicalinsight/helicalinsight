import { Typography } from 'antd'

const { Text } = Typography

const NoMetadata = ({ title = "Please Connect Metadata", subTitle = "" }) => {
    return (
        <div className='no-metadata-container'>
            <Text className='no-metadata-title'>{title}</Text>
            <Text className='no-metadata-sub-title'>{subTitle}</Text>
        </div>
    )
}

export default NoMetadata