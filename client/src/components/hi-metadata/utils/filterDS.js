import { isObject } from '../../../utils/is-object';

let filteredValues = []
const filterDS = ({ dsList, record = null, text }) => {
    if (!Array.isArray(dsList)// dslist is an array
        || (Array.isArray(dsList) && dsList.length === 0)// if it is an array and checking length
    ) {
        return []
    }
    if (!isObject(record)// record to be an object
        || (isObject(record) && Object.keys(record).length === 0)// record should not be an empty object
        || typeof (text) !== 'string'// text to be string
    ) {
        return dsList
    }
    filteredValues = []
    let dsKey = record.keyPath
    let parentKey = dsKey.split('/')[0]
    dsList.forEach(ds => {
        if (ds.key === parentKey) {
            ds.children = ds.children.map(child => {
                if (child.keyPath === record.keyPath) {
                    ds.children = traverseChildren({
                        children: ds.children,
                        text
                    })
                }
                return child
            }
            )
            // ds.children = traverseChildren({
            //     children: ds.children,
            //     text
            // })
        }
        return ds
    }
    )
    let dsResult = manipulateData({
        dsList,
        filteredValues,
        text,
        record
    })
    return dsResult
}

const traverseChildren = ({ children, text }) => {
    children.map(child => {
        if (child.name.toLowerCase().includes(text.toLowerCase())) {
            filteredValues.push(child.keyPath)
            // return child
        }
        if (child.children?.length) {
            child = traverseChildren({
                children: child.children,
                text
            })
        }

        return child
    }
    )
    return children
}

const manipulateData = ({ filteredValues, dsList, text, record }) => {
    dsList = dsList.map((eachDs) => { //#4993
        // dsList[1].children = dsList[1].children.map(ds => {
        eachDs.children = eachDs.children.map(ds => {
            if (ds.uuid === record.uuid) {
                if (checkIncludes({
                    filteredValues,
                    key: ds.keyPath
                })) {
                    ds.children = filterChildrenTree({
                        filteredValues,
                        children: ds.children,
                        text
                    })
                } else {
                    ds.children = []
                }
            }
            return ds
        }
        )
        return eachDs
    })
    return dsList
}
const checkIncludes = ({ filteredValues, key }) => {
    return filteredValues.some(substring => substring.includes(key))
}
const filterChildrenTree = ({ filteredValues, children, text }) => {
    children = children.map(child => {
        if (child.children?.length) {
            // if (child.name.includes(text)) {
            if (child.name.toLowerCase().includes(text.toLowerCase())) {
                return child
            }
            child.children = filterChildrenTree({
                filteredValues,
                children: child.children,
                text
            })
            // }
        }
        if (checkIncludes({
            filteredValues,
            key: child.keyPath
        })) {
            // if (child.name.includes(text)) {
            return child
            // }
        }
        return false
    }
    ).filter(Boolean)
    return children
}


export { filterDS };
