export const findNestedObj = (obj, key, value) => {
    try {
        JSON.stringify(obj, (_, nestedValue) => {
            if (nestedValue && nestedValue[key] === value) throw nestedValue
            return nestedValue
        })
    } catch (result) {
        return result
    }
}

export const findAllNestedObj = (obj, key, value) => {
    try {
        let data = []
        JSON.stringify(obj, (_, nestedValue) => {
            if (nestedValue && nestedValue[key] === value) {
                // throw nestedValue
                if(!data.find(ele => ele.uniqueKey === nestedValue.uniqueKey))
                data.push(nestedValue)
            }
            return nestedValue
        }
        )
        return data 

    } catch (result) {
        return result
    }
}