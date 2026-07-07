import metadataRequests from "../../../base/requests/metadata.requests";
import { updateTablesWithColumns } from "./updateTablesWithColumns";
import { metadataActions } from "../../../redux/actions";
import { cloneDeep } from "lodash-es";
import { v4 as uuidv4 } from "uuid";

/*
 * to expand the table in the metadata section
 * @data This function calls the updateTablesWithColumns function and makes an api call and it updates the table column data in the metadata section.
 * return: It does not return any data it dispatch the metadata action and fetch the columns in table.
 */

export const handleTableExpand = ({
  setRequestId = () => {},
  currentTable,
  dispatch,
  dataSource,
  tables,
  duplicateTable = false,
  getApi,
  mode,
}) => {
  let metadata = {
    catalog: currentTable.catalog ? currentTable.catalog : "",
    schema: currentTable.schema ? currentTable.schema : "",
  };
  let currentDataSource = dataSource.filter(
    (ds) => ds?.connId === currentTable.connId
  )[0];
  let formData = {
    dataSource: {
      ...currentDataSource,
      datasourceName: currentTable.dataSourceName,
    },
    // dataSource: dataSource.filter(ds => ds.connId === currentTable.connId)[0] ,
    classifier: currentDataSource?.classifier,
    metadata: {
      ...metadata,
      table: currentTable.name,
    },
    refresh: true,
  };
  if (!formData?.dataSource?.dir) {
    delete formData.dataSource.dir;
  }

  !tables[currentTable.keyName]?.columnsFetched &&
    dispatch(
      metadataActions.updateLoadingStatus({
        type: currentTable.key,
        status: true,
      })
    );
  let requestId = uuidv4();
  !tables[currentTable.keyName]?.columnsFetched && setRequestId(requestId);

  
  formData.requestId = requestId;
  if (!tables[currentTable.keyName]?.columnsFetched) {
    const apiInstance = metadataRequests(dispatch).fetchColumns(
      formData,
      (res) => {
        updateTablesWithColumns({
          result: res,
          currentTable,
          dispatch,
          dataSource,
          tables,
          duplicateTable,
          mode,
        });
        dispatch(
          metadataActions.updateLoadingStatus({
            type: currentTable.key,
            status: false,
          })
        );
      },
      (err) => {
        console.log("in error -- fetch columns", err);
        tables = cloneDeep(tables);
        tables[currentTable.keyName].errorFetching = true;
        tables[currentTable.keyName].columnsFetched = false;
        tables[currentTable.keyName].columns = {};
        dispatch(
          metadataActions.updateLoadingStatus({
            type: currentTable.key,
            status: false,
          })
        );
        dispatch(
          metadataActions.updateMetadataState({
            key: "tables",
            mode: "updateTables",
            value: tables,
          })
        );
      }
    );
    getApi && getApi(apiInstance);
  }
};
