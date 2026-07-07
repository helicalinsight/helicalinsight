import { checkIfCrossJoin } from "../../../../../components/hi-metadata/utils/checkIfCrossJoin";

describe('checkIfCrossJoin', () => {
    test('returns false when either left or right table is missing', () => {
      const result = checkIfCrossJoin({ join: { left: {}, right: { table: 'table2' } }, tables: {} })
      expect(result).toBe(false)
    })
  
    test('returns false when both left and right tables are missing', () => {
      const result = checkIfCrossJoin({ join: {}, tables: {} })
      expect(result).toBe(false)
    })
  
  
    test('returns the connection database IDs when both left and right tables are present and connected to data sources', () => {
      const tables = {
        table1: { dataSource: { id: 1, type: 'mysql' } },
        table2: { dataSource: { id: 2, type: 'postgres' } }
      }
      const dsWithConnDbId = {
        '1__mysql': 123,
        '2__postgres': 456
      }
      const result = checkIfCrossJoin({ join: { left: { table: 'table1' }, right: { table: 'table2' } }, tables, dsWithConnDbId })
      expect(result).toEqual({ leftConnDBId: 123, rightConnDBId: 456 })
    })
  
   
  })
  