import { useDispatch, useSelector } from "react-redux";
import { HIPropertyPane } from "../../common/index";
import { onConfiguration } from "../hcrHelperMethods";
import { hcrCanvasPaneHelperMethods } from "./hcrCanvasPaneHelperMethods";

let hcrPropertyPaneConfig;
const { pageSizeChange, pageOrientationChange, pageMarginChange, handleShapePropertyChanges } = hcrCanvasPaneHelperMethods;

export default function CanvasPropertyPane({ tabNum, contentTab, oldConfigContent, groupId }) {
    const cannedReportsData = useSelector(state => state.cannedReports.present);
    const { hCROldConfigurations, propertyPaneData } = cannedReportsData;
    const { HCR: hcrStaticData } = hCROldConfigurations;
    const { pageLayoutInfo } = propertyPaneData;
    const { size } = pageLayoutInfo;
    const dispatch = useDispatch();
    let groups = [];

    hcrPropertyPaneConfig = onConfiguration({ hcrStaticData, oldConfigContent, groups, propertyPaneData });

    const getData = ({ value, key, groupId, record }) => {
        // console.log("key, value, objectKey, path", key, value, groupId, path, record.callBack);
        let { staticPathValue, callBack, path } = record;
        eval(`${callBack}({ value, staticPathValue, dispatch, size, key, propertyPaneData, path })`);
    };

    return (
        <HIPropertyPane
            groupId={groupId}
            group={groups}
            items={hcrPropertyPaneConfig}
            getData={getData}
        />
    );
}
