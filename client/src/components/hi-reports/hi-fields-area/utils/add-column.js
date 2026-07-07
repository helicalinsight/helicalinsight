
import { fromJS } from "immutable";
import getParamPath from "./get-param-path";

export default function ({ col, paramCol, path }) {
    col = fromJS(col)
    paramCol = fromJS(paramCol)
    let paramPath;

    if (!path) {
        let lastFocused = document.querySelector(".hi-dbfuncs-last-focus");

        if (!lastFocused || !lastFocused.getAttribute("data-path")) {
            return false;
        }

        paramPath = lastFocused
            .getAttribute("data-path")
            .split("|").map((n) => parseInt(n, 10));
        paramPath = getParamPath(paramPath);
    } else {
        paramPath = getParamPath(path); 
    }
    if (paramCol.has("alias")) {
        col = col.setIn(paramPath, paramCol.get("column"))
            .setIn([...paramPath.slice(0, paramPath.length - 1), "column"], true)
            .setIn([...paramPath.slice(0, paramPath.length - 1), "aliasName"], paramCol.get("alias"))
            return col.toJS()
    } else {
        col =  col.setIn(paramPath, paramCol.get("column"))
            .setIn([...paramPath.slice(0, paramPath.length - 1), "column"], true)
            return col.toJS()
    }
}
