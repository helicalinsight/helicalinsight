import handleGroupBy from "../utils/handleGroupBy";

let bp = {
    "column": "travel_details.booking_platform",
    "alias": "booking_platform",
}
let dest = {
    "column": "travel_details.destination",
    "alias": "destination",
}
// let columnsPayLoad = [
//     {
//         "column": "travel_details.booking_platform",
//         "alias": "booking_platform",
//     },
//     {
//         "column": "travel_details.destination",
//         "alias": "destination",
//     }
// ]
let query = new ReportQuery({
    "location": "1463377807724/1463377836985/1591703058466",
    "metadataFileName": "aa3d9571-5568-444e-9446-79ec1f53c78f.metadata"
})
query.select([bp])
query.from('HIUSER')
query.limit(-1)
// query.rfd({ flux: hrFlux })
// describe("test handleGroupBy function ", () => {
//     test('check if geoupby exists by default', () => {
//         let result = query.reportFormData()
//         expect((() => {
//             let {columns } = result
//             return columns
//         })()).toBe(true)
//     })
// })
//unable to write test cases for this because the function which generates formdata for report is not yet fully developed and is taking info from flux, whihc is not available with jest