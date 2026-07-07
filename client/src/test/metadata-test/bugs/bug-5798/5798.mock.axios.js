import qs from "qs";
import { mockData5798 } from "./5798.mock.data";


export const editMetadataMockAxios = () => {
  let data = {
    post: function (url, data, config) {
      let payload = qs.parse(data);
      let { type, serviceType, service, formData } = payload;
      try {
        formData = window.atob(formData);
      } catch (e) {}
      formData = JSON.parse(formData);
      return new Promise((resolve, reject) => {
        let { contentId, location, metadataFileName, id } = formData;

        if (type === 'content' && serviceType === "static" && service === "getContents") {
          if (contentId === "Static/DataSourcesList") {
            return resolve({
              data: JSON.stringify(mockData5798.getStaticDSList),
            });
          }
        }
        if (
          type === "adhoc" &&
          serviceType === "metadata" &&
          service === "get" 
        ) {
          if (
            location === "sibtain" &&
            metadataFileName === "Metadata_duplicate.metadata"
          ) {
            return resolve({
              data: JSON.stringify(mockData5798.getMetaDataTables),
            });
          }
        }

        if (
          type === "adhoc" &&
          serviceType === "metadata" &&
          service === "metadataWorkflow"
        ) {
          let { fetchCatalogs, fetchSchemas, fetchTables, fetchData } = formData.parameters;

          if (
            id === "1" &&
            formData.type === "dynamicDataSource" &&
            fetchCatalogs &&
            fetchSchemas
          ) {
            return resolve({
              data: JSON.stringify(
                mockData5798.mockdataFetchSchemaFetchCatalog
              ),
            });
          }

          if (
            id === "1" &&
            formData.type === "dynamicDataSource" &&
            fetchTables &&
            fetchData
          ) {
            return resolve({
              data: JSON.stringify(
                mockData5798.mockdataFetchTables
              ),
            });
          }

          if (
            id === "1001" 
            // formData.type === "dynamicDataSource" &&
            // fetchCatalogs &&
            // fetchSchemas
          ) {
            console.log('helllloooo worl')
            return resolve({
              data: JSON.stringify(
                mockData5798.fetchDataSourceChildren
              ),
            });
          }
        }
        
        console.log("POST REQ", type, serviceType, service, formData);

        return resolve({});
      });
    },
    get: (url, data, config) => {
      let { params } = data;
      return new Promise((resolve, reject) => {
        if (url === "listDataSources" && params.classifier === "all") {
          return resolve({
            data: JSON.stringify(mockData5798.getGetListDS),
          });
        }
        console.log("GET REQ", url, data);
        resolve({
          data: {},
        });
      });
    },
  };
  return { instance: data };
};
