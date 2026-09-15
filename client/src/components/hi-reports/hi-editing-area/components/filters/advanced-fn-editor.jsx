import { useDispatch, useSelector } from 'react-redux';
import { updateFilterMapping } from '../../../../../redux/actions/hreport.actions';
import { getDBFunctionObject } from '../../../../../utils/utilities';
import notify from '../../../../hi-notifications/notify';
import FunctionsEditor from '../../../hi-fields-area/db-functions/editor';
import { parseDbFuncString } from '../../../hi-fields-area/utils/parse-db-func';
import { checkForDisplayDBMonthFunction } from '../../utils/filter-utils';

const AdvancedFnEditor = (props = {}) => {
    const {
        editingField = null,
        onChange = () => { },
        onClose = () => { },
        field = {},
        filter = {}
    } = props || {}
    const dispatch = useDispatch()
    const { databaseFunctions } = useSelector((state) => {
        let activeReport = state.hreport.present.reports.find((report) => report.active);
        return activeReport;
    });

    if (!editingField) return null;

    const { columnType } = editingField || {}

    const handleChange = (value) => {
        let tempValue = value
        if (typeof value === "object") {
            tempValue = value?.value;
        }
        onChange(tempValue)
    }

    const handleClose = () => {
        onClose()
    }

    const applyParsedFunction = (databaseFunction) => {
        let tempFilter = { ...filter };
        let newMapping = { ...(filter.mapping || {}) };
        const dbKey = databaseFunction && databaseFunction.key;
        checkForDisplayDBMonthFunction(filter?.condition, filter, notify, dispatch, dbKey);
        let dbFunc = getDBFunctionObject({ value: dbKey, databaseFunctions });
        let mappingObj = {};
        if (columnType === "value") {
            mappingObj = {
                ...newMapping,
                valueDBFunction: databaseFunction,
                valueDBFuntionInfo: dbFunc,
            };
            tempFilter.databaseFunction = dbFunc;
        } else {
            mappingObj = {
                ...newMapping,
                DisplayDBFunction: databaseFunction,
            };
        }
        tempFilter.mapping = mappingObj;
        tempFilter.valuesMode = "custom";
        tempFilter.values = "custom";
        dispatch(updateFilterMapping({ tempFilter }));
    };

    const handleSave = (data) => {
        const { databaseFunctions, fields, editingField } = data || {}
        let { databaseFunction } = parseDbFuncString({ databaseFunctions, fields, editingField: { ...editingField } })
        applyParsedFunction(databaseFunction)
        onClose()
    }

    return (
        <div className="advance-filter-editor-container">
            <FunctionsEditor
                editingField={editingField}
                field={field}
                onChange={handleChange}
                onClose={handleClose}
                onSave={handleSave}
            />
        </div>
    )
}

export default AdvancedFnEditor