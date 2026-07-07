package com.helicalinsight.adhoc.services;

import com.google.gson.Gson;
import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataDBUtility;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IService;
import com.helicalinsight.efw.serviceframework.ServiceUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import net.sf.json.JSONObject;

/**
 * Executes query-related services.
 * Created by author on 02-03-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class QueryExecutorService implements IService {
	/**
     * Executes the query service.
     *
     * @param type        The type of the service.
     * @param serviceType The service type.
     * @param service     The specific service to execute.
     * @param formData    The form data for the service.
     * @return The result of the service execution.
     * @throws EfwServiceException if the logged-in user is not authorized to execute the query.
     */
    @Override
    public String doService(String type, String serviceType, String service, String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        Metadata metadata;
          String  metadataFile = formDataJson.getString("metadataFileName");
            String directory = formDataJson.getString("location");

            metadata = MetadataDBUtility.getMetadata(directory, metadataFile);
        if (!formDataJson.containsKey("metadataFileJson")) {
            Metadata mdJsonCandidate;
                mdJsonCandidate = MetadataDBUtility.getMetadata(directory, metadataFile);


            MultiConnectionMergeAdhocTable mc = new MultiConnectionMergeAdhocTable(mdJsonCandidate);
            mc.merge();
            formDataJson.accumulate("metadataFileJson", (new Gson().toJson(mdJsonCandidate)));
            formDataJson.accumulate("dbIdDbName", mc.getDbIdDbNameMap());
           Boolean isCached = mdJsonCandidate.getCached() != null ? mdJsonCandidate.getCached() : false;
            formDataJson.put("multiConnection", isCached);
            metadata = mdJsonCandidate;
            formData=formDataJson.toString();
        }

            return ServiceUtils.executeService(type, serviceType, service, formData);
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
