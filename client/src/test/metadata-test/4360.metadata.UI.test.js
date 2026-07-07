// import { updateJoinsAfterAliasingTableColumn } from '../../components/../utils/updateJoinsAfterAliasingTableColumn'
import { updateJoinsAfterAliasingTableColumn } from '../../components/hi-metadata/utils/updateJoinsAfterAliasingTableColumn'
import { data4630 } from './4630.mock.data'


describe('testing joins bugs 4630 -- updateJoinsAfterAliasingTableColumn', () => {

    test('testing with default joins - testing table alias name', () => {
        let result = updateJoinsAfterAliasingTableColumn({
            tables: data4630.tables,
            joins: data4630.joins
        })
        expect((() => {
            return result[4].right.tableAlias
        })()).toBe("travel_details_alias")
    })

    test('testing with default joins - testing table name', () => {
        let result = updateJoinsAfterAliasingTableColumn({
            tables: data4630.tables,
            joins: data4630.joins
        })
        expect((() => {
            return result[4].right.table
        })()).toBe("travel_details")
    })

    test('testing with default joins - testing column name', () => {
        let result = updateJoinsAfterAliasingTableColumn({
            tables: data4630.tables,
            joins: data4630.joins
        })
        expect((() => {
            return result[3].right.column
        })()).toBe("destination_id")
    })

    test('testing with default joins - testing column alias name', () => {
        let result = updateJoinsAfterAliasingTableColumn({
            tables: data4630.tables,
            joins: data4630.joins
        })
        expect((() => {
            return result[3].right.columnAlias
        })()).toBe("destination_id_alias")
    })

    

})