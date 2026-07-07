import { updateTablesWithViews } from "../../../../../components/hi-metadata/utils/views/updateTablesWithViews";

describe('updateTablesWithViews', () => {
  const tables = {
    'table1': {
      id: 1,
      name: 'table1',
      columnsFetched: false,
      keyName: 'table1',
      alias: 'table1',
      category: 'table',
      columns: {}
    },
    'table2': {
      id: 2,
      name: 'table2',
      columnsFetched: false,
      keyName: 'table2',
      alias: 'table2',
      category: 'table',
      columns: {}
    }
  };

  const views = [
    {
      id: 1,
      uuid: 'view1',
      name: 'view1',
      alias: 'view1',
      columns: {}
    },
    {
      id: 2,
      uuid: 'view2',
      name: 'view2',
      alias: 'view2',
      columns: {
        'column1': {
          id: 1,
          name: 'column1',
          dataType: 'string',
          nullable: false
        }
      }
    }
  ];

  const activeView = 'view2';

  test('should update the existing table with view columns', () => {
    const updatedTables = updateTablesWithViews({ tables, views, activeView });
    expect(updatedTables['view2'].columns).toEqual(views[1].columns);
    expect(updatedTables['view2'].columnsFetched).toEqual(true);
    expect(updatedTables['view2'].alias).toEqual(views[1].alias);
  });

  test('should add a new view as a table if the corresponding table does not exist', () => {
    const newView = {
      id: 3,
      uuid: 'view3',
      name: 'view3',
      alias: 'view3',
      columns: {}
    };
    const updatedTables = updateTablesWithViews({ tables, views: [...views, newView], activeView: 'view3' });
    expect(updatedTables['view3'].columns).toEqual(newView.columns);
    expect(updatedTables['view3'].columnsFetched).toEqual(true);
    expect(updatedTables['view3'].alias).toEqual(newView.alias);
    expect(updatedTables['view3'].category).toEqual('view');
  });

  test('should not update tables if tables, views or activeView is falsy', () => {
    const updatedTables = updateTablesWithViews({ tables: null, views, activeView });
    expect(updatedTables).toBeFalsy();

    const updatedTables2 = updateTablesWithViews({ tables, views: null, activeView });
    expect(updatedTables2).toBeFalsy();

    const updatedTables3 = updateTablesWithViews({ tables, views, activeView: null });
    expect(updatedTables3).toBeFalsy();
  });

  test('should not update tables if the active view is not found in views', () => {
    const updatedTables = updateTablesWithViews({ tables, views, activeView: 'nonExistingView' });
    expect(updatedTables).toBeFalsy();
  });

 
});
