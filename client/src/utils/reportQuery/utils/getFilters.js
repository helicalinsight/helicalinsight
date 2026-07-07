"use strict";

import extractDatabaseFunctions from "./extractDatabaseFunctions";
import { List as iList } from "immutable";
import _ from 'lodash';

const dateTypes = ["dateTime", "date", "time"]

/**
 * Get "filters" and "having" object for columns
 * @param  {Object} filtersState   Filters State
 * @param  {Object} metadataState  Metadata State
 * @return {Object}                Object that can be merged into request data
 */

/**
 * NOTE: IS_ONE_OF and EQUALS will be handled by the backend. This is the reason why you find any block for the same.
 */

const backendDatatypeMapping = new Map([  // added this mapping of backend datatype for bug [6049]
    ["text", "java.lang.String"],
    ["boolean", "java.lang.Boolean"],
    ["numeric", "java.lang.Integer"],
    ["date","java.sql.Date"],
    ["date","java.sql.Date"],
    ["time","java.sql.Time"],
    ["dateTime","java.sql.Timestamp"],
    ["other","java.lang.Object"]
  ]);

export default function (filtersState, metadataState, mode) {
    let { filters, customFilterExpression, customHavingExpression, modeSt } = filtersState,
        { database } = metadataState,
        customFilterExpressionObj = {}, customHavingExpressionObj = {},
        dataToSend = filters.reduce((r, f, i) => {
            let filter = f.withMutations((map) => (
                // map.set("column", `${database}.${f.get("column")}`.replace(/^\./, ""))
                // map.set("column", `${database ? `${database}.` : ''}${f.get("column")}`.replace(/^\./, ""))
                map.set("column", typeof f.get('column')  == 'object' ? f.get("column") : `${database ? `${database}.` : ''}${f.get("column")}`.replace(/^\./, ""))
                    .set("dataType", backendDatatypeMapping.get(f.get("dataType")))
                    // .set("dataType", f.get("backendDataType"))
                    .delete("data")
                    // .delete("mode")
                    .delete("valuesMode")
                    .delete("databaseFunction")
                    .delete("backendDataType")
                    .delete("type")
                    .delete("currentDate")
                    .delete("anchor")
                    .delete("showTimePicker")
                    .delete("minInput")
                    .delete("maxInput")
                    .delete("showRelativeList")
                    .delete("dateValuesType")
                    // .delete("cascade")
                    .delete("rangeSelectionToggole")
                    .delete("valuesRange")
                    .delete("dateTimeToggle")
                    .delete("rangeValuesType")
                // .delete("mapping")
            ));
            switch (f.get("valuesMode")) {
                case "none":
                    // If values are disabled
                    filter = filter.delete("values");
                    break;
                case "all":
                    // Skip Filter if "all" values are selected
                    return r;
                case "custom":
                    // Add flag if "custom" values are selected
                    let condition = f.get("condition");
                    let conditionList = [
                        "IS_BETWEEN",
                        "IS_NOT_BETWEEN",
                        "IN_RANGE",
                        "NOT_IN_RANGE"
                    ];
                    if (!condition.match(new RegExp(conditionList.join("|"), "i"))) {
                        let temp = [];
                        filter.get("values").toJS().forEach((val) => {
                            if (typeof val == "string") {
                                let temp1 = val.split(",");
                                temp = temp.concat(temp1);
                            }
                        });
                        if (temp.length > 0) {
                            filter = filter.set("values", iList(temp));
                        }
                        if (f.get("encloseInQuotes")) {
                            let values = filter.get("values").map((val) => `'${val.trim()}'`);
                            filter = filter.set("values", values);
                        }
                    } else {
                        let temp = [];
                        filter.get("values").toJS().forEach((val) => {
                            if (typeof val == "string") {
                                let temp1 = val.split(",");
                                temp = temp.concat(temp1);
                            }
                        });
                        if (temp.length > 0) {
                            filter = filter.set("values", iList(temp));
                        }
                    }

                    filter = filter.set("isCustomValue", true);
                    break;
            }
            let dataType = f.get('dataType');

            let dbFuncDataType = "";
            if (f.getIn(['config', 'mapping'])) {
                // if(f.get('mapping')?.get('valueDBFunction')?.get('returns')){
                if (f.getIn(['config', 'mapping', 'valueDBFunction', 'returns'])) {
                    dbFuncDataType = f.getIn(['config', 'mapping', 'valueDBFunction', 'returns'])
                    filter = filter.set(
                        "databaseFunction",
                        extractDatabaseFunctions(
                            f.getIn(['config', 'mapping', 'valueDBFunction']),
                            f.get("column")
                        )
                    );
                }
                else {
                    dbFuncDataType = dataType;
                }
            }
            else {
                if (f.has("databaseFunction") && f.get("databaseFunction").size > 0) {
                    let databaseFunction = f.get("databaseFunction").toJS();
                    dbFuncDataType = databaseFunction.returns ? databaseFunction.returns : "";
                } else {
                    dbFuncDataType = dataType;
                }

                if (f.has("databaseFunction") && f.get("databaseFunction").size > 0) {
                    filter = filter.set(
                        "databaseFunction",
                        extractDatabaseFunctions(
                            f.get("databaseFunction"),
                            f.get("column")
                        )
                    );
                }
            }
            // console.log('filter - getfilters', filter.toJS());

            let allExp = `('_all_' = '_all_')`;

            if (filter.get("condition") === "CUSTOM") {
                filter = filter.set("customCondition", filter.get("customCondition") || "");
                if (filter.get("encloseInQuotes")) {
                    filter = filter.set("values", ["'" + filter.get("values").join("','") + "'"]);
                }
            } else {
                filter = filter.delete("customCondition");
            }

            if (filter.get("condition") === "NOT_EQUALS") {
                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("customCondition", "<>");
                allExp = `('_all_' <> '_all_')`;
            }

            if ((filter.get("condition") === "IN_RANGE") || (filter.get("condition") === "NOT_IN_RANGE")) {
                filter = filter.set("encloseInQuotes", false);

                if (filter.get("values")) {
                    let values = filter.get("values").toJS();
                    if (values.length > 0) {
                        if (dbFuncDataType && (dateTypes.indexOf(dbFuncDataType) > -1)) {
                            filter = filter.set("encloseInQuotes", true);
                        }
                    }
                }
            }

            if (filter.get("condition") === "IS_BETWEEN" || filter.get("condition") === "IS_NOT_BETWEEN") {

                if (filter.get("condition") === "IS_NOT_BETWEEN") {
                    filter = filter.set("customCondition", "NOT BETWEEN");
                }
                if (filter.get("condition") === "IS_BETWEEN") {
                    filter = filter.set("customCondition", "BETWEEN");
                }

                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("encloseInQuotes", false);
                let temp = "";

                if (filter.get("values")) {
                    let values = filter.get("values").toJS();

                    if (values.length > 0) {
                        filter = filter.set("isCustomValue", true);

                        if (dbFuncDataType && (dateTypes.indexOf(dbFuncDataType) > -1)) {
                            temp = "'" + values[0] + "' AND '" + values[1] + "'";
                        } else {
                            temp = "" + values[0] + " AND " + values[1];
                        }
                    } else {
                        temp = "'' AND ''";
                    }
                    filter = filter.set("values", [temp]);
                }
            }

            if (filter.get("condition") === "CONTAINS" || filter.get("condition") === "DOES_NOT_CONTAINS") {
                let joinedValues;

                if (filter.get("condition") === "CONTAINS") {
                    filter = filter.set("customCondition", "like");
                }

                if (filter.get("values")) {
                    let values = filter.get("values"),
                        resultString = escapeMetaCharacters(values.join("','"));
                    joinedValues = "'%" + resultString + "%'";
                    filter = filter.set("values", [joinedValues]);

                }

                if (filter.get("condition") === "DOES_NOT_CONTAINS") {
                    filter = filter.set("customCondition", "not like");
                    if (joinedValues.match(new RegExp(['_all_'].join("|"), "i"))) {
                        allExp = `('_all_' <> '_all_')`;
                    }
                }

                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("encloseInQuotes", false);
            }


            if (filter.get("condition") === "STARTS_WITH") {
                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("customCondition", "like");
                filter = filter.set("encloseInQuotes", false);
                if (filter.get("values")) {
                    let values = filter.get("values"),
                        resultString = escapeMetaCharacters(values.join("','")),
                        joinedValues = "'" + resultString + "%'";
                    filter = filter.set("values", [joinedValues]);
                }
            }

            if (filter.get("condition") === "DOES_NOT_STARTS_WITH") {
                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("customCondition", "not like");
                filter = filter.set("encloseInQuotes", false);
                if (filter.get("values")) {
                    let values = filter.get("values"),
                        resultString = escapeMetaCharacters(values.join("','")),
                        joinedValues = "'" + resultString + "%'";
                    filter = filter.set("values", [joinedValues]);

                    if (joinedValues.match(new RegExp(['_all_'].join("|"), "i"))) {
                        allExp = `('_all_' <> '_all_')`;
                    }
                }
            }

            if (filter.get("condition") === "ENDS_WITH") {
                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("customCondition", "like");
                filter = filter.set("encloseInQuotes", false);
                if (filter.get("values")) {
                    let values = filter.get("values"),
                        resultString = escapeMetaCharacters(values.join("','")),
                        joinedValues = "'%" + resultString + "'";
                    filter = filter.set("values", [joinedValues]);
                }
            }

            if (filter.get("condition") === "DOES_NOT_ENDS_WITH") {
                filter = filter.set("condition", "CUSTOM");
                filter = filter.set("customCondition", "not like");
                filter = filter.set("encloseInQuotes", false);
                if (filter.get("values")) {
                    let values = filter.get("values"),
                        resultString = escapeMetaCharacters(values.join("','")),
                        joinedValues = "'%" + resultString + "'";
                    filter = filter.set("values", [joinedValues]);

                    if (joinedValues.match(new RegExp(['_all_'].join("|"), "i"))) {
                        allExp = `('_all_' <> '_all_')`;
                    }
                }
            }

            if (filter.get("condition") === "IS_NULL" || filter.get("condition") === "IS_NOT_NULL") {

                if (filter.get("condition") === "IS_NULL") {
                    filter = filter.set("customCondition", "IS NULL");
                }
                if (filter.get("condition") === "IS_NOT_NULL") {
                    filter = filter.set("customCondition", "IS NOT NULL");
                }
                filter = filter.set("condition", "CUSTOM");
                filter = filter.delete("values");
                filter = filter.set("encloseInQuotes", false);
            }


            if (
                filter.get("condition") === "IS_LESS_THAN" ||
                filter.get("condition") === "IS_GREATER_THAN" ||
                filter.get("condition") === "IS_LESS_THAN_OR_EQUAL_TO" ||
                filter.get("condition") === "IS_GREATER_THAN_OR_EQUAL_TO"
            ) {
                if (filter.get("condition") === "IS_LESS_THAN") {
                    filter = filter.set("customCondition", "<");
                }
                if (filter.get("condition") === "IS_GREATER_THAN") {
                    filter = filter.set("customCondition", ">");
                }
                if (filter.get("condition") === "IS_LESS_THAN_OR_EQUAL_TO") {
                    filter = filter.set("customCondition", "<=");
                }
                if (filter.get("condition") === "IS_GREATER_THAN_OR_EQUAL_TO") {
                    filter = filter.set("customCondition", ">=");
                }
                filter = filter.set("condition", "CUSTOM");
                if (dbFuncDataType && (dateTypes.indexOf(dbFuncDataType) > -1)) {
                    filter = filter.set("encloseInQuotes", true);
                }
            }


            if (filter.get("condition") === "IS_NOT_ONE_OF") {
                let datePart = filter.get("datePart")
                filter = filter.set("condition", "CUSTOM");
                let values = filter.get("values"), joinedValues = [],
                    // encloseInQuotes = filter.get("encloseInQuotes");
                    encloseInQuotes = values.size ? true : false; //5556

                if (dbFuncDataType && (dbFuncDataType === "text" || dbFuncDataType === "other")) {
                    if (!encloseInQuotes) {
                        joinedValues = [values.join(",") + ")"];
                    }
                    else {
                        joinedValues = ["'" + values.join("','") + "')"];
                    }
                } else if (dbFuncDataType && (dateTypes.indexOf(dbFuncDataType) > -1) || datePart === "date" || datePart === "time") {
                    let JValues = filter.get("values").toJS();
                    joinedValues = ["'" + JValues.join("','") + "')"];
                } else if (datePart === "date" || datePart === "time") {
                    let JValues = filter.get("values").toJS();
                    joinedValues = ["'" + JValues.join("','") + "')"];
                } else {
                    joinedValues = [values.join(",") + ")"];
                }
                filter = filter.set("customCondition", " NOT IN (");
                filter = filter.set("values", joinedValues);
                filter = filter.set("encloseInQuotes", false);
                filter = filter.set("isCustomValue", true);
                allExp = `('_all_' <> '_all_')`;
            }

            if (filter.get("condition") === "IS_ONE_OF") {
                filter = filter.set("condition", "CUSTOM");
                let values = filter.get("values"), joinedValues = [],
                    // encloseInQuotes = filter.get("encloseInQuotes");
                    encloseInQuotes = values.size ? true : false; //5556
                let datePart = filter.get("datePart")
                if (dbFuncDataType && (dbFuncDataType === "text" || dbFuncDataType === "other")) {
                    if (!encloseInQuotes) {
                        joinedValues = [values.join(",") + ")"];
                    }
                    else {
                        joinedValues = ["'" + values.join("','") + "')"];
                    }
                } else if (dbFuncDataType && (dbFuncDataType === "dateTime" || dbFuncDataType === "time" || dbFuncDataType === "date"
                    || datePart === "date" || datePart === "time")) {
                    let JValues = filter.get("values").toJS();
                    joinedValues = ["'" + JValues.join("','") + "')"];
                } else if (datePart === "date" || datePart === "time") {
                    let JValues = filter.get("values").toJS();
                    joinedValues = ["'" + JValues.join("','") + "')"];
                } else {
                    joinedValues = [values.join(",") + ")"];
                }
                filter = filter.set("customCondition", " IN (");
                filter = filter.set("values", joinedValues);
                filter = filter.set("encloseInQuotes", false);
                filter = filter.set("isCustomValue", true);
            }

            let allExist = _isAllExist(filter.toJS().values, i);

            // If filter has aggregate functions, it is a "having" filter
            // else it is a "where" clause
            if (f.has("aggregate") && f.get("aggregate") && f.get("aggregate").size) {
                // filter = filter.delete("label")
                filter = filter.set("function", f.get("aggregate").toArray().join("_"))
                    .delete("aggregate");
                if (f.getIn(['config', 'aggregate'])) {
                    filter = filter.set("function", f.getIn(['config', 'aggregate']).toArray().join("_"))
                    // .delete("aggregate");
                }
                r.having.push(filter.toJS());
                if ((mode != undefined && mode == "open") || (modeSt != undefined && modeSt == "print")) {
                    let exp = `\${${r.having.length - 1}}`;
                    //checks if all exists in the value
                    if (allExist) {
                        customHavingExpressionObj[r.having.length - 1] = allExp;
                        customHavingExpression = customHavingExpression.replace(
                            `\${${i}}`,
                            allExp
                        );
                    } else {
                        customHavingExpressionObj[r.having.length - 1] = exp;

                        //${i} present
                        if (customHavingExpression.indexOf(`\${${i}}`) != -1) {
                            customHavingExpression = customHavingExpression.replace(
                                `\${${i}}`,
                                exp
                            );
                        } else {
                            let occObj = occurrences(customHavingExpression, allExp), count = 0, id = r.having.length - 1;
                            for (var key = 0; key < id; key++) {
                                let exists = r.having[key] ? _isAllExist(r.having[key].values) : false;
                                if (exists) {
                                    count++;
                                }
                            }
                            //let str1  = customHavingExpression.slice(0,occObj[count+1]-1);
                            // To fix Bug #381. 
                            // To fix Bug #381. 
                            // To fix Bug #381. 
                            // To fix Bug #381. 
                            // To fix Bug #381. 
                            var firstSlice = ((occObj[count + 1] - 1) == -1) ? 0 : occObj[count + 1] - 1;
                            let str1 = customHavingExpression.slice(0, firstSlice);
                            let str2 = customHavingExpression.slice((occObj[count + 1] + allExp.length), customHavingExpression.length);
                            customHavingExpression = str1 + " " + exp + " " + str2;
                        }
                    }
                } else {
                    customHavingExpression = customHavingExpression.replace(
                        `\${${i}}`,
                        `\${${r.having.length - 1}}`
                    );
                }
            } else {
                r.filters.push(filter.toJS());
                if ((mode != undefined && mode == "open") || (modeSt != undefined && modeSt == "print")) {
                    let exp = `\${${r.filters.length - 1}}`;

                    if (allExist) {
                        customFilterExpressionObj[r.filters.length - 1] = allExp;
                        customFilterExpression = customFilterExpression.replace(
                            `\${${i}}`,
                            allExp
                        );
                    } else {
                        customFilterExpressionObj[r.filters.length - 1] = exp;
                        if (customFilterExpression.indexOf(`\${${i}}`) != -1) {
                            customFilterExpression = customFilterExpression.replace(
                                `\${${i}}`,
                                exp
                            );
                        } else {
                            let occObj = occurrences(customFilterExpression, allExp), count = 0, id = r.filters.length - 1;
                            for (var key = 0; key < id; key++) {
                                let exists = r.filters[key] ? _isAllExist(r.filters[key].values) : false;
                                if (exists) {
                                    count++;
                                }
                            }
                            var firstSlice = ((occObj[count + 1] - 1) == -1) ? 0 : occObj[count + 1] - 1;
                            let str1 = customFilterExpression.slice(0, firstSlice);
                            let str2 = customFilterExpression.slice((occObj[count + 1] + allExp.length), customFilterExpression.length);
                            customFilterExpression = str1 + " " + exp + " " + str2;
                        }
                    }
                } else {
                    customFilterExpression = customFilterExpression.replace(
                        `\${${i}}`,
                        `\${${r.filters.length - 1}}`
                    );
                }
            }
            return r;
        }, {
            filters: [],
            having: []
        });




    if (dataToSend.filters.length < 1) {
        delete dataToSend.filters;
        delete dataToSend.customFilterExpression;
    }

    if (customFilterExpression != undefined && customFilterExpression != "") {
        dataToSend.customFilterExpression = customFilterExpression;
    }

    if (customHavingExpression != undefined && customHavingExpression != "") {
        dataToSend.customHavingExpression = customHavingExpression;
    }

    if (dataToSend.having.length < 1) {
        delete dataToSend.having;
        delete dataToSend.customHavingExpression;
    }
    else {
        //update aggregate functctions for filters
        dataToSend.having = dataToSend.having.map((f) => {
            if (f.aliasProvided) {
                // console.log('in alias provided', f)
            }
            delete f.config
            delete f.mapping
            return f;
        })
    }
    //removing unwanted keys 'config', 'mapping'
    if (dataToSend.filters?.length) {
        dataToSend.filters = dataToSend.filters.map(f => {
            delete f.config
            delete f.mapping
            return f
        })
    }

    return dataToSend;
}

function _isAllExist(values) {
    let isExistInArray, isExistInString = "";
    if (values && _.isArray(values)) {
        isExistInArray = _.find(values, (item) => {
            if (item && typeof item == "string") {
                return (item.toLowerCase().match(new RegExp(['_all_'].join("|"), "i")));
            } else if (item) {
                return (item.toString().toLowerCase().match(new RegExp(['_all_'].join("|"), "i")));
            }
        });
    } else if (values) {
        isExistInString = values.toLowerCase().match(new RegExp(['_all_'].join("|"), "i"));
    }

    return (isExistInArray != undefined || (isExistInString != "" && isExistInString != -1)) ? true : false;
}

function occurrences(string, subString) {
    string += "";
    subString += "";
    if (subString.length <= 0) return (string.length + 1);

    var n = 0,
        pos = 0,
        occObj = {},
        step = subString.length;

    while (true) {
        pos = string.indexOf(subString, pos);
        if (pos >= 0) {
            ++n;
            occObj[n] = pos;
            pos += step;
        } else break;
    }
    return occObj;
}

function escapeMetaCharacters(inputString) {
    var metaCharacters = ["\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<", ">", "-", "&"];
    var outputString = "";
    if (inputString != undefined) {
        for (var i = 0; i < metaCharacters.length; i++) {
            if (_.includes(metaCharacters[i], inputString)) {
                outputString = inputString.replace(metaCharacters[i], "\\" + metaCharacters[i]);
                inputString = outputString;
            }
        }
    }
    return inputString;
}

/**
 * copied this from hreport
 */