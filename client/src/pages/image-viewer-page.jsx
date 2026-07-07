import { useEffect } from 'react'
import { fetchImage } from './utils/imagePageUtils'
import { useDispatch } from 'react-redux'
import { useState } from 'react'
import { Skeleton } from 'antd'

const ImageViewer = (props = {}) => {
    const { file = {} } = props
    const [imageSrc, setImageSrc] = useState('')
    const [loading, setLoading] = useState(false)
    const dispatch = useDispatch()

    useEffect(() => {
        setLoading(true)
        if (typeof props.renderTaskbar === "function") {
            props.renderTaskbar([])
        }
        fetchImage({ file }, dispatch).then((res) => {
            if (res?.data) {
                const { imageData = "", title = "" } = res.data || {};
                setImageSrc(imageData);
                if (typeof props.setFileInfo === "function") {
                    props.setFileInfo({ fileTitle: title ?? 'Image' });
                }
            }
        })
            .catch(err => { })
            .finally(() => {
                setLoading(false)
            });
    }, [file])

    if (loading) return <Skeleton active loading={loading} className="height100percent"></Skeleton>

    return (
        <div data-testid="hi-image-viewer">
            <img src={imageSrc} style={{ width: "100%", height: "100%" }} alt="image_preview" />
        </div>
    )
}

export { ImageViewer }
