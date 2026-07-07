import requests from "../../../base/requests";
import { metadataActions, updateJoinsData, updateLoadingStatusForJoins } from "../../../redux/actions";
import { uuid as uniquedId } from "../../../utils/uuid";
import { mergeJoins } from "./mergeJoins";
import { updateJoinsWithCrossJoins } from "./updateJoinsWithCrossJoins";
import { areJoinsFetched } from "./utils";

async function actionDispatching({joins, dispatch, index, dataSource, connId}) {
    // if(index === (dataSource.length-1)){
    //     dispatch(metadataActions.updateJoinsData({ data: joins, override: true }))
    // } 
    dispatch(updateLoadingStatusForJoins({ id: connId, status: false }))
    dispatch(metadataActions.metadataUndoRedoPurpose([{key: 'joins', value: joins}]))

    // dispatch(updateJoinsData({ data: joins, override: true }))
}

export const fetchJoins = ({ dataSource, tables, dispatch, forEditMeta = false, joinsToUpdate = false, existingJoins = [], multiConnection = false, mergeType = 'reload', checkForCrossJoins = false, fetchedMetadata = null }) => {
    if (forEditMeta && joinsToUpdate) {
        handleJoinsInEdit({ joins: joinsToUpdate, dispatch, multiConnection, dataSource, checkForCrossJoins, fetchedMetadata })
        return
    }
    if (!dataSource.length) {
        return
    }
    let joins = [...existingJoins]
    let dsLength = dataSource.length
    let serviceFlags = [];
   
    dataSource.forEach((ds, index) => {
        let { id, type, dir, catalog, schema, connId, joinsFetched = false } = ds;
        let formData = {};
        //check if for this connId joins are already fetched
        // if(areJoinsFetched(joins, connId) || joinsFetched) return;
        formData = {
            dataSource: {
                id,
                type,
                dir,
                catalog,
                schema
            },
            metadata: {
                metdataDir: "",
                filename: "",
                table: Object.values(tables).map(table => {
                    if (table.connId === connId) {
                        return table.name
                    }
                    return false
                }).filter(Boolean)
            }
        }
        
        dsLength = 1
        dispatch(updateLoadingStatusForJoins({ id: connId, status: true }))
        requests.metadata(dispatch).fetchJoins(formData, res => {
            let fetchedJoins = JSON.parse(JSON.stringify(res.joins))
            serviceFlags.push(true)
            // joins = cloneDeep(joins)
            fetchedJoins = fetchedJoins.map(join => {
                // join.connId = connId
                join.uuid = uniquedId(); // dbId: connId
                join.left = { ...join.left, dbId: connId};
                join.right = { ...join.right, dbId: connId};
                return join;
            });
            // if (serviceFlags.length === dsLength || true) {
            if (mergeType === 'merge') {
                joins = mergeJoins({ fetchedJoins, existingJoins: joins, tables });
                // if(joins.length) {
                //     joins = mergeJoins({ fetchedJoins, existingJoins: joins })
                // } else {
                //     joins = fetchedJoins;
                // }
            } else {
                //mergeType is reload
                joins = [...joins, ...fetchedJoins];
            }
            joins= JSON.parse(JSON.stringify(joins))
            joins = joins.map((join, index) => {
                //TODO: check this logic for deleted joins. it can fail for deleted joins
                // it is failing for deleted join inbetween
                join.index = index + 1
                if (!join.action) {
                    join.action = 'noChange'
                }
                return join
            })
            actionDispatching({joins, dispatch, index, dataSource, connId})
            // }
        }, () => {
            serviceFlags.push(true)
            dispatch(updateLoadingStatusForJoins({ id: connId, status: false }))
            if (serviceFlags.length === dsLength) {
                dispatch(updateJoinsData({ data: joins, override: true }))
            }
        })

    });
}

const handleJoinsInEdit = ({ joins, dispatch, multiConnection, dataSource, checkForCrossJoins = false, fetchedMetadata = null }) => {
    joins = joins.map((join, index) => {
        join.index = index + 1
        join.action = 'noChange'
        join.uuid = uniquedId()
        join.left = { ...join.left, }; // dataSource
        join.right = { ...join.right, };
        return join
    });
    (fetchedMetadata && checkForCrossJoins) && (joins = joins.concat(updateJoinsWithCrossJoins({ fetchedMetadata })))
    dispatch(updateJoinsData({ data: joins, override: true, multiConnection }))
}