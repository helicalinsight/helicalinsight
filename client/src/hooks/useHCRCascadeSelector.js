import { useSelector } from 'react-redux';
import { hcrDSParameter, hcrDSQuery } from '../components/hi-canned-reports/hcr-constants';
import { getCalcParamsNode } from '../components/hi-canned-reports/hcrHelperMethods';

const useHCRCascadeSelector = (props = {}) => {
    const HCR = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR || {});
    const { builtInVariables } = HCR.designerProperties?.variables || {};

    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};

    const { dsPaneTypes, canvasProperties = {}, selectedQueryId, hcrDiagramNodesData = [] } = activeTab || {};
    const hcrParametersArr = props?.parameters?.length ? props.parameters : dsPaneTypes?.find(ele => ele.dataSourcePane === hcrDSParameter)?.menu || []
    const newCalOptions = props?.calculations?.length ? props.calculations : canvasProperties?.calculations?.options || [];
    const newGrpOptions = canvasProperties?.groupProperties?.options || [];

    const queriesMenu = dsPaneTypes
        ?.find((ele) => ele.dataSourcePane === hcrDSQuery)
        ?.menu?.filter(
            (ele) =>
                ele.executeQueryData?.data.length ||
                ele.executeQueryData?.field.length
        ) || [];


    let fields = [];
    const getFields = (queryId) => {
        const selectedQuery = queriesMenu?.find((ele) => ele.id === queryId);
        if (selectedQuery) {
            const { executeQueryData = {} } = selectedQuery || {};
            const { field = [] } = executeQueryData || {}
            return field;
        }
        return []
    }
    fields = getFields(selectedQueryId);

    const getOtherOptions = () => {
        const calculatedList = (builtInVariables?.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: ((ele.name === "Current_Time") || (ele.name === "Current_Date")) ? ele.value : `$V{${ele.value}}` });
            if (ele.name === "Current_Date") {
                nodeObj.pattern = "MMMMM dd, yyyy";
            } else if (ele.name === "Current_Time") {
                nodeObj.pattern = "HH:mm";
            }
            return nodeObj;
        }) || []).map((item) => ({ label: item.name, value: item.label, ...(item.pattern ? { pattern: item.pattern } : {}) }))

        const newlyAddedCal = (newCalOptions.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: `$V{${ele.name}}` });
            nodeObj.id = ele.id;
            nodeObj.isNewCalculation = true;
            return nodeObj;
        }) || []).map((item) => ({ label: item.name, value: item.label }))

        const newlyAddedGrp = (newGrpOptions.map(ele => {
            const name = `${ele.name}_COUNT`;
            return getCalcParamsNode({ name, label: `$V{${name}}` });
        }) || []).map((item) => ({ label: item.name, value: item.label }))

        const paraArr = (hcrParametersArr?.map(ele => {
            return getCalcParamsNode({ name: ele.name, label: `$P{${ele.name}}` });
        }) || []).map((item) => ({ label: item.name, value: item.label }))

        const result = []

        if (paraArr.length) {
            result.push({ label: "Parameters", value: "parameters", children: paraArr })
        }
        if (calculatedList.length) {
            result.push({ label: "Variables", value: "variables", children: calculatedList })
        }
        if (newlyAddedCal.length || newlyAddedGrp.length) {
            result.push({ label: "Calculations", value: "calculations", children: [...(newlyAddedCal || []), ...(newlyAddedGrp || [])] })
        }

        return result
    }

    const getOptions = (fields) => {
        return fields.map((field) => ({ label: field.name, value: field.name })) || []
    }

    const getFormattedFields = (fields) => {
        return fields.map(({ label = "", value = "" }) => ({ label, value: `$F{${value}}` }))
    }

    const getFieldsOptions = () => {
        if (props?.fields?.length) return [{ label: "Fields", value: "fields", children: getFormattedFields(getOptions(props.fields)) }]
        if (props.node) {
            const { parent = null } = props.node || {}
            if (parent) {
                let parentNode = hcrDiagramNodesData.find((item) => item.id === parent);
                if (parentNode) {
                    const { selectedQueryID } = parentNode || {};
                    return [{ label: "Fields", value: "fields", children: getFormattedFields(getOptions(getFields(selectedQueryID))) }]
                }
            }
        }
        if (fields?.length) {
            return [{ label: "Fields", value: "fields", children: getFormattedFields(getOptions(fields)) }]
        }
        return []
    }

    const getCascaderOptions = (fields = []) => {
        return [...(getFieldsOptions(fields) || []), ...(getOtherOptions() || [])];
    }

    return {
        getCascaderOptions
    }
}

export default useHCRCascadeSelector