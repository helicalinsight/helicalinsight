import { useSelector } from 'react-redux';
import { hcrDSParameter, hcrDSQuery } from '../components/hi-canned-reports/hcr-constants';
import { getCalcParamsNode } from '../components/hi-canned-reports/hcrHelperMethods';

const useSubDataSet = (props = {}) => {
    const { fields = [], calculations = [], parameters = [] } = props || {};
    const HCR = useSelector(state => state.cannedReports.present?.hCROldConfigurations?.HCR?.HCR || {});
    const { builtInVariables } = HCR.designerProperties?.variables || {};

    const activeTab = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey)) || {};

    const { dsPaneTypes, canvasProperties = {}, hcrDiagramNodesData = [] } = activeTab || {};
    // const hcrParametersArr = dsPaneTypes?.find(ele => ele.dataSourcePane === hcrDSParameter)?.menu || []

    const getFieldsOptions = (fields) => {
        return fields.map((field) => ({
            ...field,
            label: field.name,
            value: `$F{${field.name}}`,
            data: {
                name: field.name,
                label: `$F{${field.name}}`,
                renderKey: 'text',
                isLeaf: true,
                zIndex: 10,
                type: 'queryField',
                category: 'text',
                repeat: 'rd',
                borders: {},
                padding: {},
                backendDataType: field.clazz
            }
        })) || []
    }

    const getFormattedFields = (fields) => {
        return fields.map(({ label = "", value = "", ...rest }) => ({ label, value: `$F{${value}}`, ...rest })) || []
    }

    const getSubDataSetOptions = () => {
        const calculatedList = (builtInVariables?.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: ((ele.name === "Current_Time") || (ele.name === "Current_Date")) ? ele.value : `$V{${ele.value}}` });
            if (ele.name === "Current_Date") {
                nodeObj.pattern = "MMMMM dd, yyyy";
            } else if (ele.name === "Current_Time") {
                nodeObj.pattern = "HH:mm";
            }
            return nodeObj;
        }) || []).map((item) => ({ label: item.name, value: item.label, data: item, ...(item.pattern ? { pattern: item.pattern } : {}) }))

        const newlyAddedCal = (calculations.map(ele => {
            const nodeObj = getCalcParamsNode({ name: ele.name, label: `$V{${ele.name}}` });
            nodeObj.id = ele.id;
            nodeObj.isNewCalculation = true;
            return nodeObj;
        }) || []).map((item) => ({ label: item.name, value: item.label, id: item.id, data: item }))

        const paraArr = (parameters?.map(ele => {
            const parObj = getCalcParamsNode({ name: ele.name, label: `$P{${ele.name}}` });
            parObj.id = ele.id;
            return parObj;
        }) || []).map((item) => ({ label: item.name, value: item.label, id: item.id, data: item }))

        const result = []

        if (paraArr.length) {
            result.push({ label: "Parameters", value: "parameters", children: paraArr })
        }
        if (calculatedList?.length) {
            result.push({ label: "Variables", value: "variables", children: calculatedList })
        }
        if (newlyAddedCal?.length) {
            result.push({ label: "Calculations", value: "calculations", children: newlyAddedCal })
        }
        if (fields?.length) {
            result.push({
                label: "Fields", value: "fields", children: getFieldsOptions(fields)
            })
        }
        return result
    }

    return {
        getSubDataSetOptions
    }
}

export default useSubDataSet;