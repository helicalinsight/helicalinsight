import { handleExpandDSRow } from "../../../../../components/hi-metadata/utils/handleExpandDSRow";
import { fetchCatalogs, fetchDatasource, fetchSchema } from '../../../../../components/hi-metadata/utils';


const mockSetRequestId = jest.fn();
jest.mock('uuid', () => ({
  v4: () => '1234'
}));

jest.mock('../../../../../components/hi-metadata/utils', () => ({
  fetchCatalogs: jest.fn(),
  fetchDatasource: jest.fn(),
  fetchSchema: jest.fn()
}));

describe("handleExpandDSRow function", () => {
  let params;

  beforeEach(() => {
    params = {
      isExpanded: false,
      record: {
        category: 'schema',
        fetched: false,
        children: []
      },
      dispatch: jest.fn(),
      store: {},
      refresh: true,
      mode: 'create',
     // refreshDataSource: true,
      setRequestId: jest.fn(),
      getApi: jest.fn()
    };
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  test("fetchSchema is called when category is schema and record fetch is not set", () => {
    handleExpandDSRow(params);

    expect(fetchSchema).toHaveBeenCalled();
    expect(fetchSchema).toHaveBeenCalledWith({
      requestId: '1234',
      isExpanded: params.isExpanded,
      record: params.record,
      dispatch: params.dispatch,
      store: params.store,
      getApi: params.getApi
    });
  });

  test("fetchCatalogs is called when category is catalog and record fetch is not set", () => {
    params.record.category = 'catalog';
    handleExpandDSRow(params);

    expect(fetchCatalogs).toHaveBeenCalledTimes(1);
    expect(fetchCatalogs).toHaveBeenCalledWith({
      requestId: '1234',
      isExpanded: params.isExpanded,
      record: params.record,
      dispatch: params.dispatch,
      store: params.store,
      getApi: params.getApi
    });
  });

  test("fetchDatasource is called when category is not catalog or schema and record fetch is not set", () => {
    params.record.category = 'some-other-category';
    handleExpandDSRow(params);

    expect(fetchDatasource).toHaveBeenCalledTimes(1);
    expect(fetchDatasource).toHaveBeenCalledWith({
      requestId: '1234',
      isExpanded: params.isExpanded,
      record: params.record,
      dispatch: params.dispatch,
      store: params.store,
      refresh: params.refresh,
      getApi: params.getApi
    });
  });

  test("fetchDatasource is not called when refreshDataSource is true", () => {
    params.refreshDataSource = true;
    handleExpandDSRow(params);

    expect(fetchDatasource).toHaveBeenCalledTimes(0);
  });


  test("fetchCatalogs is called when category is table", () => {
   
    const params = {

            isExpanded : true,
            record :{
                category: 'dsGroup',
                fetched : false,
                children : []
            
            },
            dispatch : jest.fn(),
            store: {},
            refresh : false,
          
            
            setRequestId : mockSetRequestId,
            getApi : jest.fn(),
            
        };
    handleExpandDSRow(params);

    expect(fetchDatasource).toHaveBeenCalledTimes(1);
    expect(fetchDatasource).toHaveBeenCalledWith({
      requestId: '1234',
      isExpanded: true,
      record: params.record,
      dispatch: params.dispatch,
      store: params.store,
      refresh:params.refresh,
      refreshDataSource: params.refreshDataSource ,
      getApi: params.getApi
    });
  });

  test("check if setRequested is called with requestId", () => {
   
const params = {

    isExpanded : false,
    record :{
        category: 'schema',
        fetched : false,
        children : []
    
    },
    dispatch : jest.fn(),
    store: {},
    refresh : true,
    mode: 'create',
    refreshDataSource: true ,
    setRequestId : mockSetRequestId,
    getApi : jest.fn(),
    
};

    handleExpandDSRow(params);
    expect(mockSetRequestId).toBeCalledWith("1234");

});
});