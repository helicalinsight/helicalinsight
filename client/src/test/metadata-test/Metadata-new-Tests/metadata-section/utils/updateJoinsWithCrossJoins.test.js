import { updateJoinsWithCrossJoins } from "../../../../../components/hi-metadata/utils/updateJoinsWithCrossJoins";

  
describe('updateJoinsWithCrossJoins', () => {
    test('returns an empty array if no cross joins exist', () => {
      const fetchedMetadata = {
        joins: [],
        connections: [
          { 
            dataSource: { connectionDatabaseId: 'db1' } 
          },
          {
            dataSource: { connectionDatabaseId: 'db2' }
          }
        ]
      }
      const result = updateJoinsWithCrossJoins({ fetchedMetadata })
      expect(result).toEqual([])
    })
  
    test('updates cross joins with corresponding data sources', () => {
      const fetchedMetadata = {
        crossJoins: [
          {
            databaseId: 'db1',
            referenceId: 'db2',
            left: { dataSource: null },
            right: { dataSource: null }
          }
        ],
        connections: [
          { 
            dataSource: { connectionDatabaseId: 'db1' } 
          },
          {
            dataSource: { connectionDatabaseId: 'db2' }
          }
        ]
      }
      const expected = {
        crossJoins: [
          {
            databaseId: 'db1',
            referenceId: 'db2',
            left: { dataSource: { connectionDatabaseId: 'db1' } },
            right: { dataSource: { connectionDatabaseId: 'db2' } },
            action: 'noChange'
          }
        ],
        connections: [
          { 
            dataSource: { connectionDatabaseId: 'db1' } 
          },
          {
            dataSource: { connectionDatabaseId: 'db2' }
          }
        ]
      }
      const result = updateJoinsWithCrossJoins({ fetchedMetadata })
      expect(result).toEqual(expected.crossJoins)
    })
  })
  