
import { Typography } from 'antd';
import { useState } from 'react';
import { useDispatch } from "react-redux";
import { updateSubVizType } from "../../../../../redux/actions/hreport.actions";
import { markTypes } from '../../../utils/constants';
import Mark from './mark';
import SubVizList from './sub-viz-list';
import { getMapChartValues } from '../../../hi-viz-area/utils/utillities';

const { Text } = Typography;

const ValueComponent = (props) => {
    const [clicked, setClicked] = useState(false)
    const dispatch = useDispatch()
    let { mark, selectedType, isCardViz, reportId, properties } = props;
    let { subVizType } = mark

    const changeSubViz = name => {
        dispatch(updateSubVizType({ value: mark.value, name, id: mark?.id }))
    }
    if (isCardViz) {
        selectedType = "Ant_Card"
    }
    const isGridOrAntChart = ['GridChart', 'Antcharts'].includes(selectedType);
    let gridOrAntChartMarkTypes = markTypes.filter((item) => ['label'].includes(item));

    const markProps = {
        reportId,
        mark,
        selectedType,
        subVizType,
        properties
    }
    const handleClick = () => setClicked(!clicked)

    let { mapType } = getMapChartValues(dispatch)

    return (
        <div className="hr-mark-value" >
            {["GridChart", "SyncChart", "Antcharts", "Ant_Card", "MapChart", "Card"].includes(selectedType) &&
                <SubVizList subVizType={subVizType} selectedType={selectedType} selectItem={changeSubViz} mapType={mapType} />
            }
            {(mark.value === "_all_") && <>
                {markTypes.map((markType, i) => {
                    return <Mark {...markProps} markType={markType} key={i} {...{ clicked }} />
                })}
                <div className="hr-mark-text-container">
                    <Text className={`hr-mark-text ${clicked ? 'hr-mark-text-color' : ''}`} onClick={handleClick}>See All</Text>
                </div>
            </>}

            {(mark.value !== '_all_' && isGridOrAntChart) ?
                <>
                    {gridOrAntChartMarkTypes.map((markType, i) => {
                        return <Mark {...markProps} markType={markType} key={i} {...{ clicked }} />
                    })}
                </>
                : null
            }

        </div>
    )
}


export default ValueComponent;