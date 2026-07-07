import { manipulateMDForMultiConn } from "../../../components/hi-reports/utils/manipulateMDForMultiConn"
import { md_multi_conn_res } from "./hreport.tables.mock.data"


describe('testing if all the tabels are fetched in case of using metadata with multiple connections', () => {
    test('checking tabless count', () => {
        let result = manipulateMDForMultiConn({metadata : md_multi_conn_res})
        console.log(Object.keys(result.tables))
        expect(Object.keys(result.tables).length).toBe(4)
        expect(Object.keys(result.tables).filter(key => key === 'employee_details').length).toBe(1)

    })
})