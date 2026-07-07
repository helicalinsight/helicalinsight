

import { fromJS } from "immutable";
import getParamPath from "./get-param-path";

export default function ({ col, func, path,intial }) {
    col = fromJS(col)

    if (!col.has("databaseFunction") || intial) {
        col = col.set("databaseFunction", func)
        return col.toJS()
    }

    let paramPath;

    if (!path) {
        let lastFocused = document.querySelector(".hi-dbfuncs-last-focus");

        if (!lastFocused || !lastFocused.getAttribute("data-path")) {
            return false;
        }

        paramPath = lastFocused
            .getAttribute("data-path")
            .split("|").map((n) => parseInt(n, 10));
    } else {
        paramPath = path;
    }
    col = col.setIn(getParamPath(paramPath), func)
    return col.toJS()
}
