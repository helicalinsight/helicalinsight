package com.helicalinsight.efw.serviceframework;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.DataSourceMapping;
import com.helicalinsight.admin.service.DatabaseCacheService;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.controller.EfwServicesController;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.TaskInterruptedException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.parallelprocessor.WorkerManager;
import com.helicalinsight.parallelprocessor.api.GenericWorker;
import com.helicalinsight.parallelprocessor.api.ISplitter;
import com.helicalinsight.parallelprocessor.cache.ApplicationCacheManager;
import com.helicalinsight.stream.StreamSession;

import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Somen
 *         Created by helical021 on 1/23/2019.
 */
@Component
public class ServiceManager {

    private static final Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private static final String DELIMITER = "_W_";
    private static final String WORKER_BEAN = "@workerBean";
    private static final String SERVICE_IMPL = "@serviceImplBean";
    private Map<String, List<String>> requestServiceIdMap = new HashMap<>();
    private List<String> blockedId = new ArrayList<>();


    public void clearAll() {
        requestServiceIdMap.clear();
        blockedId.clear();
    }

    @Autowired
    private DatabaseCacheService databaseCacheService;

    @Autowired
    private WorkerManager workerManager;

    @Autowired
    private ServicesXmlReader servicesXmlReader;


    @Autowired
    private ApplicationCacheManager applicationCacheManager;

    public String pickTheWorkerClass(String type, String serviceType, String service) {

        JSONObject servicesXmlJson = JsonUtils.getJsonOfImportableXml("services");

        JSONObject serviceClass = servicesXmlReader.getServiceClass(servicesXmlJson, type, serviceType, service);
        if (serviceClass == null) {
            throw new EfwServiceException("The expected service %s is not " + "available. Check for ");
        }
        if (serviceClass.has("@access")) {
            String accessString = serviceClass.getString("@access");
            if (StringUtils.isNotBlank(accessString)) {
                List<String> convertedRankList = Arrays.asList(accessString.split(","));
                List<String> userRoles = AuthenticationUtils.getUserRoles();
                Boolean hasAccess = false;
                for (String role : convertedRankList) {

                    hasAccess = userRoles.contains(role);
                    if(hasAccess){
                        break;
                    }
                }
                if (!hasAccess) {
                    throw new AccessDeniedException("You may not have sufficient privilege to access the url");
                }
            }

        }

        String toReturn = serviceClass.getString("@class");
        if (serviceClass.has(WORKER_BEAN)) {
            toReturn += DELIMITER + serviceClass.getString(WORKER_BEAN);
        }
        if (serviceClass.has(SERVICE_IMPL)) {
            toReturn += DELIMITER + serviceClass.getString(SERVICE_IMPL);
        }
        return toReturn;

    }
    /**
     * abortResult
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link ServiceManager#abortResult(JsonObject formJson) } instead </p>
     * @param formJson
     * @return
     */
    @Deprecated
    public String abortResult(JSONObject formJson) {
        String requestIdToAbort = formJson.getString("requestIdToAbort");
        blockedId.add(requestIdToAbort);
        List<String> serviceIds = requestServiceIdMap.get(requestIdToAbort);
        if (serviceIds == null) {
            throw new EfwServiceException("The serviceId is not found. Probably the service is completed");
        }
        boolean isAborted;
        int count = 0;
        for (String ids : serviceIds) {
            isAborted = workerManager.stopWorkerByServiceId(ids);
            if (isAborted) {
                requestServiceIdMap.remove(requestIdToAbort);
                applicationCacheManager.deleteFromCache(ids);
                count++;
            }
        }

        JSONObject response = new JSONObject();
        if (count > 0) {
            response.put("response", "Successfully aborted the workers ");
        } else {
            throw new TaskInterruptedException("Could not abort the workers");
        }
        JSONObject result = new JSONObject();
        result.put("result", response);

        return result.toString();
    }
    /**
     * using gson
     * abortResult(JsonObject formJson)
     * @param formJson
     * @return
     */
    @Deprecated(forRemoval = true)
    public String abortResult(JsonObject formJson) {
        String requestIdToAbort = formJson.get("requestIdToAbort").getAsString();
        blockedId.add(requestIdToAbort);
        List<String> serviceIds = requestServiceIdMap.get(requestIdToAbort);
        if (serviceIds == null) {
            throw new EfwServiceException("The serviceId is not found. Probably the service is completed");
        }
        boolean isAborted;
        int count = 0;
        for (String ids : serviceIds) {
            isAborted = workerManager.stopWorkerByServiceId(ids);
            if (isAborted) {
                requestServiceIdMap.remove(requestIdToAbort);
                applicationCacheManager.deleteFromCache(ids);
                count++;
            }
        }

        JsonObject response = new JsonObject();
        if (count > 0) {
            response.addProperty("response", "Successfully aborted the workers ");
        } else {
            throw new TaskInterruptedException("Could not abort the workers");
        }
        JsonObject result = new JsonObject();
        result.add("result", response);

        return result.toString();
    }


    
    public void streamResult(String type, String serviceType, String service, JsonObject formData, String workerClass, StreamSession session) {
        streamResultFromComponent(type, serviceType, service, formData, workerClass, session);
    }


    public Object getResult(String type, String serviceType, String service, JsonObject formDataId, String workerClass) {
        JsonObject formJson=formDataId;
        if(formDataId.has("formDataId")){
            formJson= EfwServicesController.gsonStore.get(formDataId.get("formDataId").getAsString());;
        }


        if (formJson.has("serviceId")) {
            String serviceId = formJson.get("serviceId").getAsString();
            if (formJson.has("pageNumber")) {
                Integer pageNumber = formJson.get("pageNumber").getAsInt() - 1;

                final List<JsonObject> resultsArray = applicationCacheManager.newReadFromCache(serviceId);

                if (resultsArray != null) {
                    JsonObject singleJson = resultsArray.get(pageNumber);
                    if (pageNumber < resultsArray.size() && pageNumber >= 0) {
                        singleJson.addProperty("resultPage", pageNumber + 1);
                        singleJson.addProperty("totalPage", resultsArray.size());
                        return singleJson;
                    } else {
                        throw new EfwServiceException("The page number should be between 1 and  " + resultsArray.size());
                    }
                }
            }
            if (formJson.has("allPages")) {
                JsonObject response = new JsonObject();
                serveFromCache(null, serviceId, response);
                return response;
            }

        }

        if (workerClass.contains(DELIMITER)) {
            Integer pageNumber = null;
            if (formJson.has("parameters")) {
                JsonObject parameters = formJson.getAsJsonObject("parameters");
                if (parameters.has("fetchTables") && parameters.has("page")) {
                    pageNumber = parameters.get("page").getAsInt();
                }
                parameters.remove("page");
            }
            JsonObject paginatedResult = getPaginatedResult(type, serviceType, service, formJson, workerClass);
            if (pageNumber != null) {
                JsonArray tableArray = paginatedResult.getAsJsonObject("response").getAsJsonObject("metadata").getAsJsonArray("catalogs").get(0).getAsJsonObject().getAsJsonArray("schemas").get(0).getAsJsonObject().getAsJsonArray("tables");
                Integer count = tableArray.size();
                int toIndex = pageNumber * 100;
                if (toIndex > tableArray.size())
                    toIndex = tableArray.size();
                //List list = JSONArray.fromObject(tableArray.subList((pageNumber - 1) * 100, toIndex));
                JsonArray list = new JsonArray();
                for (int i = (pageNumber - 1) * 100; i <= toIndex; i++) {
                    JsonElement element = tableArray.get(i);
                    list.add(element.getAsString());
                }
                
                tableArray= new JsonArray();
                tableArray.addAll(list);
                paginatedResult.addProperty("resultPage", pageNumber);
                paginatedResult.addProperty("totalPage", Math.ceil(count / 100.0));
                paginatedResult.addProperty("count", count);
            }

            return paginatedResult;
        }
        Object resultFromComponent = getResultFromComponent(type, serviceType, service, formJson, workerClass);
        return resultFromComponent instanceof String? new Gson().fromJson(resultFromComponent.toString(),JsonObject.class):resultFromComponent;
        

    }

    /**
     * using gson
     * getPaginatedResult(String type, String serviceType, String service, JsonObject formJson, String workerClass)
     * @param type
     * @param serviceType
     * @param service
     * @param formJson
     * @param workerClass
     * @return
     */
   
    /**
     * Datasource cache has 3 modes:
     *
     * 1. No Cache
     *    - Data is always fetched directly from the datasource.
     *    - Nothing is stored in the cache table.
     *
     * 2. Partial Cache
     *    - Only few records cached in the database.
     *    - Remaining data is fetched from the datasource when needed and cached again if needed.
     *
     * 3. Full Cache
     *    - All datasource data is cached in the database.
     *    - Workers read data from the cached tables instead of querying the datasource.
     *
     * Since workers run in parallel, each worker processes a small unit of work.
     * If an exception occurs, only that worker’s transaction/resources are rolled back
     * without affecting other workers.
     */
    private JsonObject getPaginatedResult(String type, String serviceType, String service, JsonObject formJson, String workerClass) {

        String classBeanArray[] = workerClass.split(DELIMITER);
        String serviceClass = classBeanArray[0];
        String workerBean = classBeanArray[1];
        String serviceImpl = null;

        if (classBeanArray.length > 2) {
            serviceImpl = classBeanArray[2];
        }


        ISplitter splitter = (ISplitter) ApplicationContextAccessor.getBean(workerBean + "Splitter");
        String requestId = GsonUtility.optString(formJson, "requestId");
        if (requestId != null && RequestRegistryFilter.cancelledRequests.contains(requestId)) {
            return new Gson().fromJson("{'status':0}",JsonObject.class);

        }
        // formJson.discard("requestId");
        if (formJson.has("parameters")) {
            formJson.getAsJsonObject("parameters").remove("skipped");
        }
        splitter.setFormData(formJson);
        List<JsonObject> formDataList = splitter.newPrepareFormDataList();

        if (formDataList.size() == 0) {
            formJson.addProperty("maxSize", 0);
            formJson.addProperty("position", 0);
            Object resultFromComponent = getResultFromComponent(type, serviceType, service, formJson, serviceClass);
            return new Gson().fromJson(new Gson().toJson(resultFromComponent),JsonObject.class);
        }

        String serviceId = splitter.newPrepareUniqueId(type, serviceType, service);
        List<GenericWorker> genericWorkerList = handleMultipleWorkers(type, serviceType, service, serviceClass, workerBean, formDataList);

        boolean notFoundInCache = !databaseCacheService.findMappingKey(serviceId);
        if ( notFoundInCache) {
            Object cacheKey = splitter.getCacheObject();
            if(cacheKey instanceof DataSourceMapping) {
                DataSourceMapping dsKey = (DataSourceMapping) cacheKey;
                dsKey.setKey(serviceId);
                dsKey.setCreateDateTime(new Date());
                dsKey.setMaxPages(formDataList.size());
                databaseCacheService.addDatabaseConnection(dsKey);
            }
        }
        if (requestId != null && !requestId.isEmpty()) {
            List<String> serviceIdList = requestServiceIdMap.get(requestId);
            if (serviceIdList == null) {
                serviceIdList = new ArrayList<>();
            }
            serviceIdList.add(serviceId);
            requestServiceIdMap.put(requestId, serviceIdList);
        }


        if (classBeanArray.length > 2 && !formJson.has("skipNext")) {
            PreExecution bean = (PreExecution) ApplicationContextAccessor.getBean(serviceImpl);
            bean.preProcess(formJson);
        }


        Boolean noAsync = formJson.has("noAsync")?formJson.get("noAsync").getAsBoolean():Boolean.FALSE;

        if (noAsync) {
            JsonObject s = asyncFalse(formDataList, genericWorkerList, serviceId);

            return s;
        } else {

            final List<JsonObject> jsonObjects = applicationCacheManager.newReadFromCache(serviceId);

            if (jsonObjects != null) {
                return jsonObjects.get(0);
            }
            return asyncTrue(formDataList, genericWorkerList, serviceId);

        }

    }

    public void stopAllWorkers() {
        workerManager.stopAll();
    }
    
    /**
     * using gson
     * handleMultipleWorkers(String type, String serviceType, String service, String serviceClass, String workerBean, List<JsonObject> formDataList)
     * @param type
     * @param serviceType
     * @param service
     * @param serviceClass
     * @param workerBean
     * @param formDataList
     * @return
     */
    public List<GenericWorker> handleMultipleWorkers(String type, String serviceType, String service, String serviceClass, String workerBean, List<JsonObject> formDataList) {
        List<GenericWorker> genericWorkerList = new ArrayList<>();
        int count = 0;
        for (JsonObject aFormDataList : formDataList) {
            GenericWorker genericWorker = (GenericWorker) ApplicationContextAccessor.getBean(workerBean + "Worker");
            JsonObject serviceDetails = new JsonObject();
            serviceDetails.addProperty("service", service);
            serviceDetails.addProperty("type", type);
            serviceDetails.addProperty("serviceType", serviceType);
            serviceDetails.addProperty("serviceClass", serviceClass);
            aFormDataList.add("serviceDetails", serviceDetails);
            aFormDataList.addProperty("maxSize", formDataList.size());
            aFormDataList.addProperty("position", count++);
            
            genericWorker.setFormData(aFormDataList);
            genericWorkerList.add(genericWorker);
        }
        return genericWorkerList;
    }
    private JsonObject asyncTrue(List<JsonObject> formDataList, List<GenericWorker> genericWorkerList, String serviceId) {
        List<GenericWorker> singleWorker = new ArrayList<>();
        singleWorker.add(genericWorkerList.get(0));
        final boolean isSubmitted = workerManager.submitWorkers(singleWorker, serviceId);
        //todo handle for submitted or not submitted similar to asyncFalse
        genericWorkerList.remove(0);


        List<JsonObject> jsonObjects = null;
        int count = 1;
        while (jsonObjects == null) {
            jsonObjects = applicationCacheManager.newReadFromCache(serviceId);
            try {
                Thread.sleep(300);
                if (count >= 10) {
                    break;
                }
                count++;

            } catch (InterruptedException e) {
                throw new EfwServiceException("Request has been interrupted.");
            }
        }


        if (jsonObjects == null) {
            applicationCacheManager.deleteFromCache(serviceId);
            throw new EfwServiceException("Could not obtain cache. ");
        }
        final JsonObject jsonObject = jsonObjects.get(0);

        if (jsonObject.get("status").getAsString().equals("0")) {
            applicationCacheManager.deleteFromCache(serviceId);
            return jsonObject;
        }
        jsonObject.addProperty("totalPage", formDataList.size());
        jsonObject.addProperty("resultPage", 1);
        jsonObject.addProperty("serviceId", serviceId);
        if (genericWorkerList.size() > 0) {
            new HIManagedThread(() -> workerManager.submitWorkers(genericWorkerList, serviceId)).start();
        }
        return jsonObject;
    }

    private JsonObject asyncFalse(List<JsonObject> formDataList, List<GenericWorker> genericWorkerList, String serviceId) {
        boolean isSubmitted = workerManager.submitWorkers(genericWorkerList, serviceId);
        final JsonObject jsonObject = new JsonObject();
        if (isSubmitted) {
            Integer pageSize = formDataList.size();
            serveFromCache(pageSize, serviceId, jsonObject);

        } else {
            jsonObject.addProperty("result", "Processed Queued. Please wait until worker is free");

        }
        return jsonObject;
    }
    
    /**
     * using gson
     * serveFromCache(Integer pageSize, String serviceId, JsonObject jsonObject)
     * @param pageSize
     * @param serviceId
     * @param jsonObject
     */
    private void serveFromCache(Integer pageSize, String serviceId, JsonObject jsonObject) {
        List<JsonObject> jsonObjects = applicationCacheManager.newReadFromCache(serviceId);
        jsonObject.addProperty("totalPage", pageSize == null ? jsonObject.size() : pageSize);
        jsonObject.add("allResult", new Gson().fromJson(jsonObjects.toString(),JsonArray.class));
        jsonObject.addProperty("serviceId", serviceId);
        //todo merging logic for all json is to be required
    }
    
    /**
     * using gson
     * getResultFromComponent(String type, String serviceType, String service, JsonObject formJson, String workerClass)
     * @param type
     * @param serviceType
     * @param service
     * @param formJson
     * @param workerClass
     * @return
     */
    private Object getResultFromComponent(String type, String serviceType, String service, JsonObject formDataId, String workerClass) {
        IService iService = FactoryMethodWrapper.getTypedInstance(workerClass, IService.class);
        String result;
        if (iService != null) {
            Object o = iService.executeService(type, serviceType, service, formDataId.toString());
            if(o!=null) return  o;
            JsonObject formJson = formDataId;
            if(formDataId.has("formDataId")){
                formJson= EfwServicesController.gsonStore.get(formDataId.get("formDataId").getAsString());;
            }
            result = iService.doService(type, serviceType, service, formJson.toString());
        } else {
            throw new EfwServiceException(String.format("The instance of the class %s could not be obtained." + "" +
                    " Check for typos.", workerClass));
        }
        return result;
    }
    
    
    
    private void streamResultFromComponent(String type, String serviceType, String service, JsonObject formJson, String workerClass, StreamSession session) {
        IService iService = FactoryMethodWrapper.getTypedInstance(workerClass, IService.class);
        if (iService != null) {
        	logger.debug("Found service classs : {}", iService);
            iService.streamResponse(type, serviceType, service, formJson.toString(), session);
        } else {
            throw new EfwServiceException(String.format("The instance of the class %s could not be obtained." + "" +
                    " Check for typos.", workerClass));
        }
    }

}
