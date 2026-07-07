/* eslint-disable no-param-reassign */
"use strict";

import { Map } from "immutable";

/**
 * Extract "databaseFunction" object for given set of columns
 * @param  {List}   funcs   List of columns
 * @param  {List}   column  Current column
 * @return {Object}         functions object in required format
 */
export default function (funcs, column) {
    let reducer = function (reduction, value, key) {
        if (key === "key") {
            reduction.functionName = value;
        } else if (key === "parameters") {
            reduction.parameters = value.reduce((reductionParam, param) => {
                if (!param.has("value")) {
                    reductionParam[param.get("name")] = param.has("column")
                        ? column
                        : param.get("defaultValue");
                } else if (Map.isMap(param.get("value"))) {
                    reductionParam[param.get("name")] = param.get("value").reduce(reducer, {});
                } else {
                    reductionParam[param.get("name")] = param.get("value");
                }

                return reductionParam;
            }, {});
        } else if (key === "returns") {
            reduction.dataType = value;
        }

        return reduction;
    };

    return funcs.reduce(reducer, {});
}


/**
 * copied this from hreport
 */