import React, { useEffect } from 'react'
import { useDispatch } from 'react-redux'
import notify from '../../../../hi-notifications/notify'
import HrProperties from './properties'

const HrPropertiesWrapper = (props) => {
    const { dataId, loading } = props
    const dispatch = useDispatch()
    let Notify = notify(dispatch)
    let env = process.env.NODE_ENV;
    if (env === "test") {
        Notify = props.notify
    }
    useEffect(() => {
        if (!dataId && !loading) {
            Notify.warning({ type: "Frontend", message: "Please generate any report to view properties." })
        }
    }, [dataId, loading])

    return <HrProperties />

}

export default HrPropertiesWrapper