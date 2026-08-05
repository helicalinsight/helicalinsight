import { v4 as uuidV4 } from "uuid";


export const createHCRCrosstabGrid = (rows = [], columns = [], measures = []) => {
    if (!rows?.length || !columns?.length || !measures?.length) return [];
    let r = Array.from({ length: (rows.length + 1) * measures.length }).map((_, i) => i)
    let c = Array.from({ length: (columns.length + 1) }).map((clmn, i) => i)
    return r.map((row) => {
        return c.map((column) => {
            return `${row}-${column}`
        })
    })
}