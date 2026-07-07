import { isArray } from "lodash"

export const getJdbcUrlSubString = (jdbcUrl, index) => {
    let subString = jdbcUrl.split('?')[index] || ""
    return subString
}

export const getSelectedDriverURL = (selectedDriver) => {
    if (selectedDriver.url) {
        if (isArray(selectedDriver.url)) {
            return selectedDriver.url[0]
        }
        return selectedDriver.url
    }
} 