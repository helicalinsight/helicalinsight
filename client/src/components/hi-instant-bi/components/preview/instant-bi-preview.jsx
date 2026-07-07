import IBSpace from '../ib-space/ib-space'
import InstantBIPreviewContent from './instant-bi-preview-content'
import InstantBIPreviewFooter from './instant-bi-preview-footer'
import './instant-bi-preview.scss'
import NoMetadata from './no-metadata'

const InstantBIPreview = (props = {}) => {
    const { isMetadataPresent, previews, activePreviewID, ...rest } = props
    if (!isMetadataPresent || !previews?.length) return <NoMetadata title='Instant BI preview' subTitle='All your generated previews will appear here' />;

    const activePreview = previews?.find((preview) => preview?.id === activePreviewID)

    return (
        <div className={'instant-bi-preview-container'}>
            <IBSpace
                stack="vertical"
                justifyContent="space-between"
            >
                <div className='instant-bi-preview-content'><InstantBIPreviewContent {...{ previews, activePreview, ...rest }} /></div>
                <div className='instant-bi-preview-footer'><InstantBIPreviewFooter {...{ previews, activePreview, ...rest }} /></div>
            </IBSpace>
        </div >
    )
}

export default InstantBIPreview


