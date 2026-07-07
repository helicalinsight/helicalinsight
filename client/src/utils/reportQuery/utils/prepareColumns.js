
import { List } from "immutable";
import extracDatabaseFunctions from "./extractDatabaseFunctions";

export const getFieldType = (col, databaseFunction) => {
    let dataType = databaseFunction ? databaseFunction.dataType : col.getIn(["type", "dataType"])
    let dimensions = ["text", "dateTime", "date"]
    let measures = ["numeric"]
    if (dimensions.indexOf(dataType) > -1) {
        return "dimension"
    } else if (measures.indexOf(dataType) > -1) {
        return "measure"
    }
}



export const prepareFields = (columnsState, metadataState) => {
    let { columns } = columnsState,
        { database } = metadataState;

    // For each column set appropriate flags and options
    return columns.map((col) => {
        let custom = col.get("custom");
        let column = {
            // column: custom ? col.get("column") : `${database}.${col.get("column")}`.replace(/^\./, "")
            // column: custom ? col.get("column") : `${database ? `${database}.` : ''}${col.get("column")}`.replace(/^\./, "")
            column: custom ? col.get("column") : typeof col.get("column") == 'object' ? col.get("column") : `${database ? `${database}.` : ''}${col.get("column")}`.replace(/^\./, "")
        };

        // Set alias option if required
        if (col.has("autogen_alias")) column.alias = col.get("autogen_alias");

        if (col.has("alias")) column.alias = col.get("alias");

        // Set custom flag if required
        if (custom) column.custom = col.get("custom");

        // Set hidden flag if required
        if (col.get("hidden")) column.hidden = true;
        if (col.get("hiddenIncludeInResultSet")) {
            column.hidden = true;
            column.includeInResultset = true;
        }
        // Set aggregate flags if required
        if (List.isList(col.get("aggregate")) && col.get("aggregate").size) {
            column.aggregate = true;
            column.aggregateList = col.get("aggregate").toJS()
        }
        if (col.has("databaseFunction") && col.get("databaseFunction").size > 0) {
            if (col.getIn(['databaseFunction', 'raw'])){ // made changes for reportQuery builder.raw
                column.databaseFunction = col.get("databaseFunction")
                column.databaseFunction = column.databaseFunction.delete('raw')
            }
            else{
                column.databaseFunction = extracDatabaseFunctions(col.get("databaseFunction"), column.column);
            }
        }
        column.fieldType = getFieldType(col, column.column.databaseFunction)
        column.addedAs = col.get("addedAs")
        column.fieldDataType = col.get("type")
        column.floatingType = col.get("floatingType")
        return column;
    }).toJS();
}

export const prepareColumns = (columnsState, metadataState) => {
    return prepareFields(columnsState, metadataState)
        // .filter(col=> col["addedAs"] === "column" )
        .map(col => {
            return col
        })
}

export const prepareRows = (columnsState, metadataState) => {
    return prepareFields(columnsState, metadataState).filter(col => col["addedAs"] === "row").map(col => {
        return col
    })
}

