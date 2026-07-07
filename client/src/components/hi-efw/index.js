import React, {useEffect, useState} from 'react'
import { Row, Col } from 'antd';
import { getResources, getResources__test } from '../../base/service';
import './index.scss'
import { useDispatch } from 'react-redux'

const  HIEfwLoader = ({ dir = null, file = null, mode = 'dashboard' }) => {
    const dispatch = useDispatch()
    const [error, setError] = useState(false)
    const [loaded, setLoaded] = useState(false)
    const [reportData, setReportData] = useState(null)
    useEffect(() => {
        if(!dir && !file){
            setError(true)
            return
        }
        fetchReport()
    })

    const fetchReport = () => dir && file && getResources(dispatch, {
        dir, file, mode: 'dashboard'
    }, (res) => {
        setReportData(res)
        setLoaded(true)
    }, (err) =>{
        setError(true)
        setLoaded(true)

    }
    )
    return (
        <div className='hi-efw-viewer-container'>
            <Row>
                <Col span={24}>
                    {loaded && reportData && !error && <iframe className='hi-efw-viewer-iframe' srcDoc={reportData} />}
                    {loaded && error && <div>Failed loading report - valid error to be shown here</div>}
                </Col>
            </Row>
        </div>
    )
}

export default HIEfwLoader
