import { mergeJoins } from "../../../../../components/hi-metadata/utils/mergeJoins";

describe('mergeJoins', () => {
    const fetchedJoins = [
      {
        id: 1,
        left: { dbId: 'db1', tableId: 'table1', column: 'id' },
        right: { dbId: 'db1', tableId: 'table2', column: 'id' },
      },
      {
        id: 2,
        left: { dbId: 'db1', tableId: 'table1', column: 'id' },
        right: { dbId: 'db1', tableId: 'table3', column: 'id' },
      },
      {
        id: 3,
        left: { dbId: 'db1', tableId: 'table2', column: 'id' },
        right: { dbId: 'db1', tableId: 'table3', column: 'id' },
      },
    ];
  
    const existingJoins = [
      {
        id: 1,
        left: { dbId: 'db1', tableId: 'table1', column: 'id' },
        right: { dbId: 'db1', tableId: 'table2', column: 'id' },
      },
    ];
  
    const tables = {
      table1: { id: 'table1', cacheId: 'cacheTable1', connId: 'db1' },
      table2: { id: 'table2', cacheId: 'cacheTable2', connId: 'db1' },
      table3: { id: 'table3', cacheId: 'cacheTable3', connId: 'db1' },
    };
  
    test('merges fetched joins with existing joins', () => {
      const result = mergeJoins({ fetchedJoins, existingJoins, tables });
      expect(result).toContainEqual({
        id: 1,
        left: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table1',
          cacheTableid: 'cacheTable1',
        },
        right: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table2',
          cacheTableid: 'cacheTable2',
        },
      });
      expect(result).toContainEqual({
        id: 2,
        left: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table1',
          cacheTableid: 'cacheTable1',
        },
        right: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table3',
          cacheTableid: 'cacheTable3',
        },
      });
      expect(result).toContainEqual({
        id: 3,
        left: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table2',
          cacheTableid: 'cacheTable2',
        },
        right: {
          column: 'id',
          dbId: 'db1',
          tableId: 'table3',
          cacheTableid: 'cacheTable3',
        },
      });
    });
  });
  