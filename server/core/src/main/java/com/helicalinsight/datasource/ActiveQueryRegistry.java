package com.helicalinsight.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.resourcesecurity.InvalidDataException;

import net.sf.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ActiveQueryRegistry
 * This {@code enum} class is responsible for managing active queries and cancellations of queries.
 */
@SuppressWarnings("unused")
public enum ActiveQueryRegistry {
    QUERY_REGISTRY_INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(ActiveQueryRegistry.class);
    private final Map<String, RunningQueryDetails> activeQueryDetailsMap = new ConcurrentHashMap<>();
    private final Map<String, String> queryIdThreadNameMap = new ConcurrentHashMap<>();
    private final Set<String> requestedForCancelSet = new HashSet<>();
    private final Map<String,Integer> fileProcessThreadMap=new ConcurrentHashMap<>();

    /**
     * getRegistry()
     * @return ActiveQueryRegistry instance
     */
    public static ActiveQueryRegistry getRegistry() {
        return QUERY_REGISTRY_INSTANCE;
    }
    /**
     * registerStatements(String threadId, RunningQueryDetails runningQueryDetails)
     * This method used to register sql query statement, if it is already registered then it will cancel that Statement.
     * 
     * @param threadId						thread id
     * @param runningQueryDetails			it deals with {@code java.sql.PreparedStatement} and {@code java.sql.Statement}
     * @throws DuplicateEntryException()  if duplicate entry is registered with the same identifier.
     */
    public void registerStatements(String threadId, RunningQueryDetails runningQueryDetails) {
        String[] split = threadId.split("##");
        if (split.length < 3) {
            return;
        }
        String queryId = split[2];
        if (requestedForCancelSet.contains(queryId)) {
            logger.info("Statement not registered, request to cancel query with queryId " + queryId + " was found");
            runningQueryDetails.cancelQuery();
            requestedForCancelSet.remove(queryId);
            return;
        }
        logger.info("The statement is now registered for threadName" + threadId);
        if (activeQueryDetailsMap.containsKey(threadId)) {
            throw new DuplicateEntryException("The uuid is already registered");
        }
        this.activeQueryDetailsMap.put(threadId, runningQueryDetails);
    }
    /**
     * registerQueryIdThreadName(String queryId, String threadName)
     * it register the id and tread name in HashMap called {@code queryIdThreadNameMap}
     * @param queryId          	Id in String format
     * @param threadName        threadName to register 
     */
    public void registerQueryIdThreadName(String queryId, String threadName) {
        this.queryIdThreadNameMap.put(queryId, threadName);
    }
    /**
     * findThreadNameForQueryId(String queryId)
     * @param queryId         id to fetch associate thread name 
     * @return thread name from HashMap
     */
    public String findThreadNameForQueryId(String queryId) {
        return this.queryIdThreadNameMap.get(queryId);
    }
    /**
     * deRegisterStatement(String threadId)
     * Method used to remove {@code java.sql.PreparedStatement} or {@code java.sql.Statement}
     * from the map {@code activeQueryDetailsMap}.
     * @param threadId      id to remove sql statements
     */
    public void deRegisterStatement(String threadId) {
        logger.info("The statement is deRegistered at " + System.currentTimeMillis());
        this.activeQueryDetailsMap.remove(threadId);
    }

    /**
     * cancelQuery(String queryId)
     * Cancels a query if query details are associated with the provided query ID; otherwise, adds it to a cancellation queue.
     * @param queryId       id required to get thread Name from HashMap
     * @return jsonObject with message indicating the cancellation status.
     */
    public JsonObject cancelQuery(String queryId) {
        String threadName = this.queryIdThreadNameMap.get(queryId);
        logger.info("ThreadName to cancel is " + threadName);
        RunningQueryDetails runningQueryDetails = null;
        if (threadName != null) {
            runningQueryDetails = this.activeQueryDetailsMap.get(threadName);
        }
        JsonObject response = new JsonObject();
        if (runningQueryDetails == null) {
            requestedForCancelSet.add(queryId);
            logger.info("Could not cancel the thread, Queued in the list ");
            response.addProperty("message", "Could not cancel query, queued in for cancellation");
            return response;
        }
        response.addProperty("message", "Query cancelled successfully");
        runningQueryDetails.cancelQuery();
        return response;
    }
    /**
     * cancelQueryByThreadName(String threadName)
     * Cancels a query associated with the specified thread name.
     * @param threadName       name of the associate thread
     * @return jsonObject with message query cancelled successfully or not 
     */
    public JsonObject cancelQueryByThreadName(String threadName) {
        RunningQueryDetails runningQueryDetails = this.activeQueryDetailsMap.get(threadName);
        JsonObject response = new JsonObject();
        if (runningQueryDetails == null) {
            response.addProperty("message", "Could not be cancel query, the threadName " + threadName + "does not exists");
            return response;
        }
        runningQueryDetails.cancelQuery();
        response.addProperty("message", "Query cancelled successfully");
        runningQueryDetails.cancelQuery();
        return response;

    }

    /**
     * listDetails()
     * Returns jsonObject with active query and queued cancellation query.
     * @return jsonObject .
     */
    public JsonObject listDetails() {
        JsonObject data = new JsonObject();
        JsonArray activeQuery = new JsonArray();
        JsonArray requestedForCancelArray = new JsonArray();
        for (String key : activeQueryDetailsMap.keySet()) {
            RunningQueryDetails runningQueryDetails = activeQueryDetailsMap.get(key);
            activeQuery.add(runningQueryDetails.getDatabaseDetails());
        }
        data.add("activeQuery", activeQuery);
        if (!requestedForCancelSet.isEmpty()) {
            JsonObject queuedSet = new JsonObject();
            queuedSet.add("queue", (JsonElement) requestedForCancelSet);
            requestedForCancelArray.add(queuedSet);
        }
        data.add("requestedForCancelArray", requestedForCancelArray);
        return data;
    }
    /**
     * registerOrPutThreadFileProcessData(String requestId,int value)  
     * This method stores the provided request ID and integer value in the {@code fileProcessThreadMap} If the request ID
     * already exists in the map, the provided value is added to the existing value.
     *
     * @param requestId       unique id
     * @param value			  integer value associated with id
     */
    public void registerOrPutThreadFileProcessData(String requestId,int value) {
    	this.fileProcessThreadMap.put(requestId, this.fileProcessThreadMap.getOrDefault(requestId, 0)+value);
    }
    /**
     * getFileProcessedPercentage(String requestId)
     * Returns the associated value of requested id.
     * @param requestId       unique id
     * @return jsonObject
     */
    public JsonObject getFileProcessedPercentage(String requestId) {
		JsonObject result=new JsonObject();
		if(!fileProcessThreadMap.isEmpty()) {
			result.addProperty("percentage", fileProcessThreadMap.get(requestId));
			result.addProperty("message","File Processing Status Fetched");
		}
		return result;
    }
	/**
	 * deregisterFileProcessThread(String requestId)
	 * It removes the respective data using id from the {@code fileProcessThreadMap}
	 * @param requestId      unique id
	 */
	public void deregisterFileProcessThread(String requestId) {
		this.fileProcessThreadMap.remove(requestId);
	}
}
