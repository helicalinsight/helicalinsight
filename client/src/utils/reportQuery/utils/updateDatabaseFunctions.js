import { handleApplyDBFunction } from './handleApplyDBFunction'
export const updateDatabaseFunctions = (payload, self) => {
    let dbf = payload
    let values = Object.values(dbf)
    values = [].concat.apply([], values)
    values.forEach(val => {
        let key = val.key.split(".").join("_") // this is to make use of the key of database fn, not the value.
        self[key] = (...args) => {
            return handleApplyDBFunction(args, self, val)
            // return self
        }
        // self[val.value.toLowerCase()] = (...args) => {
        //         return handleApplyDBFunction(args, self, val)
        //         // return self
        //     }
    })

    return self
}