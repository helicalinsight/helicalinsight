export const getViewPayload = ({ activeView = {}, dataSource = [], saveDetails = {} }) => {
    let payload = {
        hasStoredProcedure: false
    }
    if (saveDetails && ('uuid' in saveDetails) && ('location' in saveDetails)) {
        payload['location'] = saveDetails.location
        payload['metadataFileName'] = saveDetails.uuid
    }
    else {
        return false
    }
    if ('classifier' in dataSource[0]) {
        payload['classifier'] = dataSource[0].classifier
    }
    else {
        return false
    }
    payload.viewId = activeView.id
    return payload
}


/**
 * sample formdata
 * {
    "metadataFileName": "aa3d9571-5568-444e-9446-79ec1f53c78f.metadata",
    "location": "1463377807724/1463377836985/1591703058466",
    "classifier": "db.generic",
    "viewId": "219f780b-5f47-40d2-b565-14f0d5a6a72c",
    "hasStoredProcedure": false
   }
 */