import React, { useState, useEffect } from 'react'
import Ribbon from './components/ribbon'
import { Row, Col } from 'antd';
import { useHistory } from "react-router-dom";
import { getResources, getResources__test } from '../../base/service';
import { useDispatch } from 'react-redux'
import './index.scss'
const HiReportViewer = ({ dir = null, file = null, mode = 'open', action = 'newWindow' }) => {
    // {
    //     dir: '1463377807724/1463983915686/1463838054907',
    //         file: 'd1560c88-be0d-4380-8225-8a8df4eb53bf.report',
    //             mode: 'open'
    // }
    const location = useHistory();
    console.log('in location', location)
    let useIframe = action === 'newWindow'
    const [reportData, setReportData] = useState(`<!DOCTYPE html>
        <html>
        <style>
        body{
            background-color : red
        }
        </style>
        <body >

        <p>This is a paragraph.</p>
        <p>This is another paragraph.</p>

        </body>
        </html>

        `)
    const dispatch = useDispatch()
    const fetchReport = () => dir && file && getResources(dispatch, {
        dir, file, mode : 'dashboard'
    }, (res) => {
        console.log('in get request ', res)
        setReportData(res)
    }, (err) => console.log('in error', err)
    )

    useEffect(() => {
        fetchReport()
    })

    return (
        <div className='hi-report-viewer-container'>
            <Row>
                <Col span={24}>
                    {/* <Ribbon>
                    </Ribbon> */}
                </Col>
                <Col span={24}>
                    {reportData && <iframe className='hi-report-viewer-iframe' srcDoc={reportData} />}
                </Col>
            </Row>
        </div>
    )
}

export default HiReportViewer
