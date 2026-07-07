import { Col, Row, Skeleton } from 'antd';
import React from 'react';
import { v4 as uuidv4 } from "uuid";


const ChatScreenRecommendationSkeleton = () => {

    const getSkeletonSpan = ({ span = 7 }) => {
        return (
            <Col span={span} key={uuidv4()}>
                <Row>
                    <Skeleton.Button active={true} size={'small'} shape='round' block={true} />
                </Row>
            </Col>
        )
    }

    return (
        <div className='ib-recommendation-skeleton-container'>
            {Array.from({ length: 4 }).map((_i) => getSkeletonSpan({ span: 5, item: _i }))}
        </div>
    )
}

export default ChatScreenRecommendationSkeleton