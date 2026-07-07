import { prepareRelativeOptionFromAnchor, rangeConditions } from "../../../../utils/filter-utils";


export const getFilterDataType = filter => {
    return filter.dataType
}

const configIdsMap = new Map();
export function previousStateInstance() {
    return {
        save: function (configId) {
            configIdsMap.set(configId, configId)
        },
        restore: function (configId) {
            return configIdsMap.get(configId)
        },
        clear: function () {
            configIdsMap.clear()
        },
        getMap: function () {
            return configIdsMap
        }
    }
}

export const checkForDisplayDBMonthFunction = (condition, filter, notify, dispatch, dbValue) => {
    const conditionToCheck = ["IN_RANGE", "IS_BETWEEN", "NOT_IN_RANGE", "IS_NOT_BETWEEN"]
    if (conditionToCheck.includes(condition)) {
        if (!dbValue) {
            if (filter?.mapping?.DisplayDBFunction?.key?.includes("monthname")) {
                notify(dispatch).warning({ message: `${condition} is not supported for monthname db function`, type: "frontend" });
            }
        }
        if (dbValue && dbValue.includes('monthname')) {
            notify(dispatch).warning({ message: `${condition} is not supported for monthname db function`, type: "frontend" });
        }
    }
}

export const getRelativeDateValues = (data, values, filter) => {
    const { anchorDateData = {} } = data || {};
    let {
        anchorDate,
        isAnchor,
        active,
        relativePart,
        value,
        direction,
        lastInput,
        nextInput,
        part
    } = anchorDateData;
    const isRange = rangeConditions.includes(filter?.condition)
    if (relativePart) {
        let newAnchor = {
            anchorDate,
            isAnchor,
            active,
            relativePart,
            value,
            direction,
            lastInput,
            nextInput,
            part
        }
        return values.map((value, _i) => prepareRelativeOptionFromAnchor({ anchor: newAnchor, isRange: isRange, actualValue: value, index: _i + 1 }))
    }
    return values;
}
