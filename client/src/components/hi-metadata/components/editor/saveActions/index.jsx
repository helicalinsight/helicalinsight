import { BarChartOutlined, CheckCircleTwoTone, ShareAltOutlined } from '@ant-design/icons';
import { Col, Row, Typography } from 'antd';
import React from 'react';
import { useDispatch, useSelector } from "react-redux";
import { routesUrl } from "../../../../../app/constants";
// import metadataRequests from '../../../../../base/requests/metadata.requests';
import { appActions } from "../../../../../redux/actions";
// import notify from '../../../../hi-notifications/notify';

const { Title } = Typography;
function SaveActions({ handleShare }) {

    const saveDetails = useSelector(state => state.metadata.present.saveDetails)
    const dispatch = useDispatch()

    // const handleDumpClick = ({ type = 'sample' }) => {
    //     // sample FD { "location": "1629715571483", "metadataFileName": "7c436b9a-9e51-4ce0-b7d6-d943be7684bf.metadata", "dumpType": "sample" }
    //     if (!(saveDetails?.location && saveDetails?.uuid)) {
    //         notify(dispatch).success({
    //             type: 'Validation',
    //             message: 'Please save metadata before dumping'
    //         })
    //         return
    //     }
    //     if (typeof type !== 'string' || !['sample', 'deep'].includes(type))
    //         return
    //     let formData = {
    //         location: saveDetails.location,
    //         metadataFileName: saveDetails.uuid,
    //         dumpType: type
    //     }
    //     metadataRequests(dispatch).dumpMetadata(formData, res => {
    //         console.log('in dump data success', res)
    //     }, err => {
    //         notify(dispatch).error({
    //             type: 'Network Call',
    //             message: err.message
    //         })
    //     })
    // }

    return (
        <Row>
            <Col span={24} className='hi-metadata-save-actions-check-icon'>
                <CheckCircleTwoTone twoToneColor="#52c41a" />
            </Col>
            <Col span={24} className='tac'>
                <Title level={3}>Metadata Saved Successfully</Title>
            </Col>
            <Col span={24} className='tac'>
                <Title level={4}>Please choose possible option to go to next step</Title>
            </Col>
            <Col span={24}>
                <Row gutter={8}>
                    <Col xs={24} sm={24} md={12} lg={12}>
                        <Row className='save-action-box metadata-use-in-report-parent'
                            onClick={() => {
                                dispatch(appActions.setEditModeInfo({ dir: `${saveDetails.location}/${saveDetails.uuid}`, file: `${saveDetails.uuid}`, extension: 'metadata', source: 'metadata-page' }));
                                dispatch(appActions.updateRoute(routesUrl.helicalReportUrl));
                            }}
                        >
                            <Col span={24} className='save-action-box-chartIcon'>
                                <span><BarChartOutlined /></span>
                                <span className='metadata-use-in-report-default' >
                                    Create Report
                                </span>
                            </Col>
                            {/* <Col span={16}>
                                <div className='metadata-use-in-report-default' >
                                    Create Report
                                </div>
                            </Col> */}
                        </Row>
                    </Col>
                    <Col xs={24} sm={24} md={12} lg={12}>
                        <Row className='save-action-box metadata-use-in-report-parent' onClick={handleShare}>
                            <Col span={24} className='save-action-box-chartIcon'>
                                <span><ShareAltOutlined /></span>
                                <span className='metadata-use-in-report-default' >
                                    Share
                                </span>
                            </Col>
                            {/* <Col span={16}>
                                <div className='metadata-use-in-report-default' >
                                    Share
                                </div>
                            </Col> */}
                        </Row>
                    </Col>
                    {/* as per discussion with Nitin commenting below code as the cube is not developed at backend */}
                    {/* <Col xs={24} sm={12} md={8} lg={8}>
                        <Row className='save-action-box metadata-use-in-report-parent metadata-dump-action-container'>
                            <Col span={8} className='save-action-box-chartIcon'>
                                <DatabaseOutlined />
                            </Col>
                            <Col span={16}>
                                <div className='metadata-use-in-report-default metadata-dump-action-default' >
                                    Load data by using the created metadata
                                </div>
                                <div className='metadata-use-in-report-default metadata-dump-action-hover' >
                                    <Row gutter={32}>
                                        <Col span={24} >
                                            <span className='hover-underline-animation'
                                                onClick={() => handleDumpClick({ type: 'sample' })}
                                            >Sample Dump</span>
                                        </Col>
                                        <Col span={24} >
                                            <span className='hover-underline-animation'
                                                onClick={() => handleDumpClick({ type: 'deep' })}
                                            >Deep Dump</span>
                                        </Col>
                                    </Row>
                                </div>
                            </Col>
                        </Row>
                    </Col> */}
                </Row>
            </Col>
        </Row>
    )
}

export default SaveActions