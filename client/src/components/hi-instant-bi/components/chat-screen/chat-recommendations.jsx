import React from 'react'
import { transformRecommendations } from '../../utils/common-utils'
import { Col, Row, Tag, Tooltip } from 'antd';
import { v4 as uuidv4 } from "uuid";
import { updateRecommendationsVisibility } from '../../../../redux/actions/instant-bi.actions';


const Chip = ({ title = '', onClick = () => { }, smallTag = false }) => {
    const isLongTitle = title.length > 18
    return (
        <Tooltip title={title}>
            <Tag
                key={title}
                onClick={() => onClick(title)}
                className='ib-recommendation-tag'
            >
                <span>
                    {(isLongTitle || smallTag) ? `${smallTag ? title.slice(0, 10) : title.slice(0, 18)}...` : title}
                </span>
            </Tag >
        </Tooltip>
    )
}

// const ChipSpan = ({ children, span = 8 }) => {
//     return (
//         <Col span={span} key={uuidv4()}>
//             <Row>
//                 {children}
//             </Row>
//         </Col>
//     )
// }

const IBChatRecommnedations = (props) => {
    const { recommendations = [], onClick = () => { }, dispatch } = props || {}
    // let transformedRecommendations = transformRecommendations(recommendations?.filter(item => item)?.slice(0, 11))?.filter(Boolean)
    let transformedRecommendations = recommendations?.filter(Boolean)?.slice(0, 11)
    const handleClick = (title) => {
        onClick(title)
        // dispatch(updateRecommendationsVisibility(false))
    }

    return (
        <div className='ib-recommendation-container'>
            {
                transformedRecommendations?.map((item, _i) => {
                    return (
                        <div className='ib-recommendation-item'>
                            <Chip title={item} smallTag={false} onClick={handleClick} />
                        </div>
                    )
                })
            }

            {/* {
                transformedRecommendations?.map((item, _i) => {
                    return (
                        <Row justify={[1, 2].includes(_i) ? "space-between" : "space-around"} className={[1, 2].includes(_i) ? 'ib-re-gutter-row' : ''}>
                            {item?.length > 0 && item?.map((rec) => {
                                return (
                                    <ChipSpan span={_i === 2 ? 5 : 8}>
                                        <Chip title={rec} smallTag={_i === 2} onClick={handleClick} />
                                    </ChipSpan>
                                )
                            })}
                        </Row>
                    )
                })
            } */}
        </div>
    )
}

export default IBChatRecommnedations