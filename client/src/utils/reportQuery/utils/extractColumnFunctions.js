"use strict";

import { List } from "immutable";

function getAlias(value, def = false) {
    return value.has("alias") ? value.get("alias")
        : (value.has("autogen_alias") ? value.get("autogen_alias") : def);
}

/**
 * Extract "functions" object for given set of columns
 * @param  {List}   columns   List of columns
 * @param  {String} database  Name of database
 * @return {Object}           functions object in required format
 */
export default function (columns, database = "") {
    let funcs = columns.reduce((reduction, value) => {
        if (!value) return reduction;

        // let column = value.has("custom") ? value.get("column") : `${database}.${value.get("column")}`.replace(/^\./, ""),
        // let column = value.has("custom") ? value.get("column") : `${database ? `${database}.` : ''}${value.get("column")}`.replace(/^\./, ""),
        let column = value.has("custom") ? value.get("column") : typeof value.get("column") == 'object' ? value.get("column") : `${database ? `${database}.` : ''}${value.get("column")}`.replace(/^\./, ""),
            alias = getAlias(value, column);

        if (List.isList(value.get("aggregate")) && value.get("aggregate").size) {
            let agr = {
                column,
                "function": value.get("aggregate").toArray().join("_")
            };
            if (value.get('applyBeforeAggregate')) agr.applyBeforeAggregate = value.get('applyBeforeAggregate')
            if (getAlias(value)) agr.alias = getAlias(value);
            if (value.has("custom")) agr.custom = value.get("custom");

            reduction.aggregate.push(agr);
        }

        if (List.isList(value.get("groupBy")) && value.get("groupBy").size) {
            let groupBy = {
                column: getAlias(value, column)
            };

            if (getAlias(value) || value.has("custom")) {
                groupBy.custom = true;
            }

            reduction.groupBy.push(groupBy);
        }

        if (List.isList(value.get("subTotal")) && value.get("subTotal").size) {
            let subTotal = {
                column: getAlias(value, column)
            };

            if (getAlias(value) || value.has("custom")) {
                subTotal.custom = true;
            }

            reduction.subTotal.push(subTotal);
        }

        if (List.isList(value.get("orderBy")) && value.get("orderBy").size) {
            let orderByResult = {
                alias,
                order: value.get("orderBy").get(0),
                custom: true
            }
            //added for order by column
            if (value.get('showOrderByColumn') && value.get('orderByColumn')) {
                orderByResult.orderByColumn = true
            }
            reduction.orderBy.push(orderByResult);
        }

        return reduction;
    }, {
        aggregate: [],
        groupBy: [],
        orderBy: [],
        subTotal: []
    });

    if (!funcs.aggregate.length) {
        delete funcs.aggregate;
    }

    if (!funcs.groupBy.length) {
        delete funcs.groupBy;
    }

    if (!funcs.orderBy.length) {
        delete funcs.orderBy;
    }
    if (!funcs.subTotal.length) {
        delete funcs.subTotal;
    }

    return funcs;
}

/**
 * copied this from hreport
 */