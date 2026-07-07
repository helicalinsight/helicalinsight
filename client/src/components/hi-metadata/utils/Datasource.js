import produce from 'immer';
import { cloneDeep } from 'lodash-es';
import { metadataActions } from '../../../redux/actions';


//This function is not used in the application.
export const Datasource = ({store}) => {
    let storeDatasource = store.getState().metadata.present.dataSource;
    
    function create({}) { // not implemented
      return ;
    }

    function update(id) {
        return ;
    }

    function remove(id) {
    return ;
    }

    function fetch({dbId, connId}) {
        // let dataSourceItem = store.getState().metadata.allDataSources.find(ds => {
        //     return ds.data.id === data?.id;// && ds.data.type === data?.type
        // })
        let relatedDatasource = storeDatasource.find(ele => {
            return ele.dbId === dbId || ele.connId === connId;
        })
        return relatedDatasource;
        // driver: dataSourceItem,
            // datasourceName: dataSourceItem.name,
    }

return {
    fetch
}

};
